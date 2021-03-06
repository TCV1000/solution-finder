package searcher.spins.scaffold;

import common.datastore.PieceCounter;
import core.neighbor.SimpleOriginalPiece;
import searcher.spins.Solutions;
import searcher.spins.candidates.CandidateWithMask;
import searcher.spins.pieces.Scaffolds;
import searcher.spins.results.Result;
import searcher.spins.scaffold.results.*;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class ScaffoldRunner {
    private static final Comparator<ScaffoldResult> COMPARATOR = Comparator.comparingInt(ScaffoldResult::getNumOfUsingPiece);

    private final Scaffolds scaffolds;

    public ScaffoldRunner(Scaffolds scaffolds) {
        this.scaffolds = scaffolds;
    }

    public Stream<ScaffoldResultWithoutT> build(Result result, List<SimpleOriginalPiece> targetOperations) {
        // 最後のフィールドで消去されているラインを取得
        long initFilledLine = result.getAllMergedFilledLine();

        EmptyScaffoldResultWithoutT scaffoldResult = new EmptyScaffoldResultWithoutT(result, targetOperations);
        return localSearch(scaffoldResult, initFilledLine, initFilledLine, AddLastScaffoldResultWithoutT::new);
    }

    public Stream<ScaffoldResultWithT> build(Result result, SimpleOriginalPiece tOperation, List<SimpleOriginalPiece> targetOperations) {
        // 最後のフィールドで消去されているラインを取得
        long initFilledLine = result.getAllMergedFilledLine();

        // 最後のミノを置く前のフィールドで消去されているラインを取得
        long filledLineWithoutT = initFilledLine & ~tOperation.getUsingKey();

        EmptyScaffoldResultWithT scaffoldResult = new EmptyScaffoldResultWithT(result, tOperation, targetOperations);
        return localSearch(scaffoldResult, initFilledLine, filledLineWithoutT, AddLastScaffoldResultWithT::new);
    }

    public Stream<ScaffoldResultWithT> build(CandidateWithMask candidateWithMask, SimpleOriginalPiece operation) {

        // 最後のフィールドで消去されているラインを取得
        Result result = candidateWithMask.getResult();
        long initFilledLine = result.getAllMergedFilledLine();

        // 最後のミノを置く前のフィールドで消去されているラインを取得
        long filledLineWithoutT = candidateWithMask.getAllMergedFilledLineWithoutT();

        EmptyScaffoldResultWithTFromCandidate scaffoldResult = new EmptyScaffoldResultWithTFromCandidate(candidateWithMask, Collections.singletonList(operation));
        return localSearch(scaffoldResult, initFilledLine, filledLineWithoutT, AddLastScaffoldResultWithT::new);
    }

    private <T extends ScaffoldResult> Stream<T> localSearch(
            T scaffoldResult, long initFilledLine, long filledLineWithoutT, BiFunction<T, SimpleOriginalPiece, T> factory
    ) {
        // すでに完成している
        if (scaffoldResult.existsAllOnGround()) {
            return Stream.of(scaffoldResult);
        }

        // 使用していないミノがある
        PieceCounter reminderPieceCounter = scaffoldResult.getReminderPieceCounter();
        if (reminderPieceCounter.isEmpty()) {
            return Stream.empty();
        }

        // 使用されているミノが少ない順
        PriorityQueue<T> candidates = new PriorityQueue<>(COMPARATOR);
        candidates.add(scaffoldResult);

        // 解の組み合わせを記録できるようにする
        Solutions<Long> solutions = new Solutions<>();

        // 探索する
        Stream.Builder<T> results = Stream.builder();

        while (!candidates.isEmpty()) {
            T next = candidates.poll();
            List<T> nextCandidates = this.localSearch(next, initFilledLine, filledLineWithoutT, solutions, results, factory);
            candidates.addAll(nextCandidates);
        }

        return results.build();
    }

    // 空中に浮いているミノの下にミノを置いて探索
    private <T extends ScaffoldResult> List<T> localSearch(
            T scaffoldResult, long initFilledLine, long filledLineWithoutT,
            Solutions<Long> solutions, Stream.Builder<T> builder, BiFunction<T, SimpleOriginalPiece, T> factory
    ) {
        // 浮いているミノを取得する
        List<SimpleOriginalPiece> airOperations = scaffoldResult.getAirOperations();

        // ローカル内で探索済みのミノにマークする
        // 複数のミノから同じ足場用ミノが選択される可能性があるため
        Set<Long> visitedKeys = new HashSet<>();

        // 残りのミノを取得
        PieceCounter reminderPieceCounter = scaffoldResult.getReminderPieceCounter();

        List<T> candidates = new ArrayList<>();

        for (SimpleOriginalPiece air : airOperations) {
            // 足場となるミノを取得
            Stream<SimpleOriginalPiece> scaffoldPieces = scaffolds.get(air)
                    .filter(it -> {
                        long needDeletedKey = it.getNeedDeletedKey();
                        return (filledLineWithoutT & needDeletedKey) == needDeletedKey;
                    });

            scaffoldPieces
                    .filter(operation -> {
                        // そのミノがまだ使われていない
                        PieceCounter currentPieceCounter = PieceCounter.getSinglePieceCounter(operation.getPiece());
                        return reminderPieceCounter.containsAll(currentPieceCounter);
                    })
                    .forEach(operation -> {
                        long currentKey = operation.toUniqueKey();

                        // 既に探索されたか
                        if (visitedKeys.contains(currentKey)) {
                            return;
                        }
                        visitedKeys.add(currentKey);

                        // フィールドに置くスペースがある
                        if (!scaffoldResult.canPut(operation)) {
                            return;
                        }

                        // 部分的な組み合わせでまだ解が見つかっていない
                        Set<Long> keys = scaffoldResult.toKeys();
                        if (solutions.partialContains(keys, currentKey)) {
                            return;
                        }

                        T nextScaffoldResult = factory.apply(scaffoldResult, operation);

                        long filledLine = nextScaffoldResult.getLastResult().getAllMergedFilledLine();

                        // 消去されるラインが変わらない
                        if (filledLine != initFilledLine) {
                            return;
                        }

                        // まだ未使用のミノが残っているので、次の探索へ進む
                        // 解の場合でも、さらに足場が高いケースがあるかもしれないため、次の探索へ進む
                        PieceCounter nextPieceCounter = nextScaffoldResult.getReminderPieceCounter();
                        if (!nextPieceCounter.isEmpty()) {
                            candidates.add(nextScaffoldResult);
                        }

                        // 解であるか
                        if (nextScaffoldResult.existsAllOnGround()) {
                            solutions.add(nextScaffoldResult.toKeys());
                            builder.accept(nextScaffoldResult);
                        }
                    });
        }

        return candidates;
    }
}
