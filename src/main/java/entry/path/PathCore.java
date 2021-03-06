package entry.path;

import common.ValidPiecesPool;
import common.buildup.BuildUp;
import common.buildup.BuildUpStream;
import common.datastore.BlockField;
import common.datastore.MinoOperationWithKey;
import common.datastore.Operation;
import common.datastore.OperationWithKey;
import common.datastore.blocks.LongPieces;
import common.order.ReverseOrderLookUp;
import core.action.reachable.Reachable;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
import core.mino.Piece;
import entry.path.output.FumenParser;
import searcher.pack.SizedBit;
import searcher.pack.separable_mino.SeparableMino;
import searcher.pack.task.PerfectPackSearcher;
import searcher.pack.task.Result;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class PathCore {
    private final PerfectPackSearcher searcher;
    private final FumenParser fumenParser;
    private final ThreadLocal<BuildUpStream> buildUpStreamThreadLocal;
    private final ThreadLocal<? extends Reachable> reachableThreadLocal;
    private final boolean isUsingHold;
    private final int maxDepth;
    private final ValidPiecesPool piecesPool;

    private final ReverseOrderLookUp reverseOrderLookUpReduceDepth;
    private final ReverseOrderLookUp reverseOrderLookUpSameDepth;

    PathCore(PerfectPackSearcher searcher, int maxDepth, boolean isUsingHold, FumenParser fumenParser, ThreadLocal<BuildUpStream> buildUpStreamThreadLocal, ThreadLocal<? extends Reachable> reachableThreadLocal, ValidPiecesPool piecesPool) {
        this.searcher = searcher;
        this.fumenParser = fumenParser;
        this.buildUpStreamThreadLocal = buildUpStreamThreadLocal;
        this.reachableThreadLocal = reachableThreadLocal;
        this.isUsingHold = isUsingHold;
        this.maxDepth = maxDepth;
        this.piecesPool = piecesPool;

        this.reverseOrderLookUpReduceDepth = new ReverseOrderLookUp(maxDepth, maxDepth + 1);
        this.reverseOrderLookUpSameDepth = new ReverseOrderLookUp(maxDepth, maxDepth);
    }

    List<PathPair> run(Field field, SizedBit sizedBit) throws ExecutionException, InterruptedException {
        List<Result> candidates = searcher.toList();
        int maxClearLine = sizedBit.getHeight();
        return candidates.parallelStream()
                .map(result -> {
                    LinkedList<MinoOperationWithKey> operations = result.getMemento()
                            .getSeparableMinoStream(sizedBit.getWidth())
                            .map(SeparableMino::toMinoOperationWithKey)
                            .collect(Collectors.toCollection(LinkedList::new));

                    // 地形の中で組むことができるoperationsをすべてリスト化する
                    BuildUpStream buildUpStream2 = buildUpStreamThreadLocal.get();
                    List<List<MinoOperationWithKey>> validOperaions = buildUpStream2.existsValidBuildPatternDirectly(field, operations)
                            .collect(Collectors.toList());

                    // 地形の中で組むことができるものがないときはスキップ
                    if (validOperaions.isEmpty())
                        return PathPair.EMPTY_PAIR;

                    // 地形の中で組むことができるSetを作成
                    HashSet<LongPieces> piecesSolution = validOperaions.stream()
                            .map(
                                    operationWithKeys -> operationWithKeys.stream().map(OperationWithKey::getPiece)
                            )
                            .map(LongPieces::new)
                            .collect(Collectors.toCollection(HashSet::new));

                    // 探索シーケンスの中で組むことができるSetを作成
                    HashSet<LongPieces> piecesPattern = getPiecesPattern(piecesSolution);

                    // 探索シーケンスの中で組むことができるものがないときはスキップ
                    if (piecesPattern.isEmpty())
                        return PathPair.EMPTY_PAIR;

                    // 探索シーケンスの中でテト譜にするoperationsを選択する
                    HashSet<LongPieces> validPieces = piecesPool.getValidPieces();
                    List<MinoOperationWithKey> operationsToUrl = validOperaions.stream()
                            .filter(o -> {
                                return validPieces.contains(new LongPieces(o.stream().map(Operation::getPiece)));
                            })
                            .findFirst()
                            .orElse(Collections.emptyList());

                    // 譜面の作成
                    String fumen = fumenParser.parse(operationsToUrl, field, maxClearLine);

                    HashSet<LongPieces> validSpecifiedPatterns = getValidSpecifiedPatterns(field, operations, maxClearLine);

                    return new PathPair(result, piecesSolution, piecesPattern, fumen, new ArrayList<>(operationsToUrl), validPieces, validSpecifiedPatterns);
                })
                .filter(pathPair -> pathPair != PathPair.EMPTY_PAIR)
                .collect(Collectors.toList());
    }

    List<PathPair> run(Field field, SizedBit sizedBit, BlockField blockField) throws ExecutionException, InterruptedException {
        int maxClearLine = sizedBit.getHeight();

        List<Result> candidates = searcher.stream(resultStream -> {
            return resultStream
                    .filter(result -> {
                        LinkedList<MinoOperationWithKey> operations = result.getMemento()
                                .getSeparableMinoStream(sizedBit.getWidth())
                                .map(SeparableMino::toMinoOperationWithKey)
                                .collect(Collectors.toCollection(LinkedList::new));

                        BlockField mergedField = new BlockField(maxClearLine);
                        operations.forEach(operation -> {
                            Field operationField = createField(operation, maxClearLine);
                            mergedField.merge(operationField, operation.getPiece());
                        });

                        return mergedField.containsAll(blockField);
                    })
                    .collect(Collectors.toList());
        });

        return candidates.stream()
                .map(result -> {
                    LinkedList<MinoOperationWithKey> operations = result.getMemento()
                            .getSeparableMinoStream(sizedBit.getWidth())
                            .map(SeparableMino::toMinoOperationWithKey)
                            .collect(Collectors.toCollection(LinkedList::new));

                    // 地形の中で組むことができるoperationsを一つ作成
                    BuildUpStream buildUpStream = buildUpStreamThreadLocal.get();
                    List<MinoOperationWithKey> sampleOperations = buildUpStream.existsValidBuildPatternDirectly(field, operations)
                            .findFirst()
                            .orElse(Collections.emptyList());

                    // 地形の中で組むことができるものがないときはスキップ
                    if (sampleOperations.isEmpty())
                        return PathPair.EMPTY_PAIR;

                    // 地形の中で組むことができるSetを作成
                    HashSet<LongPieces> piecesSolution = buildUpStream.existsValidBuildPatternDirectly(field, operations)
                            .map(operationWithKeys -> operationWithKeys.stream()
                                    .map(OperationWithKey::getPiece)
                                    .collect(Collectors.toList())
                            )
                            .map(LongPieces::new)
                            .collect(Collectors.toCollection(HashSet::new));

                    // 探索シーケンスの中で組むことができるSetを作成
                    HashSet<LongPieces> piecesPattern = getPiecesPattern(piecesSolution);

                    // 探索シーケンスの中で組むことができるものがないときはスキップ
                    if (piecesPattern.isEmpty())
                        return PathPair.EMPTY_PAIR;

                    // 譜面の作成
                    String fumen = fumenParser.parse(sampleOperations, field, maxClearLine);

                    HashSet<LongPieces> validPieces = piecesPool.getValidPieces();

                    HashSet<LongPieces> validSpecifiedPatterns = getValidSpecifiedPatterns(field, operations, maxClearLine);

                    return new PathPair(result, piecesSolution, piecesPattern, fumen, new ArrayList<>(sampleOperations), validPieces, validSpecifiedPatterns);
                })
                .filter(pathPair -> pathPair != PathPair.EMPTY_PAIR)
                .collect(Collectors.toList());
    }

    private Field createField(MinoOperationWithKey key, int maxClearLine) {
        Mino mino = key.getMino();
        Field test = FieldFactory.createField(maxClearLine);
        test.put(mino, key.getX(), key.getY());
        test.insertWhiteLineWithKey(key.getNeedDeletedKey());
        return test;
    }

    private HashSet<LongPieces> getPiecesPattern(HashSet<LongPieces> piecesSolution) {
        HashSet<LongPieces> validPieces = piecesPool.getValidPieces();
        HashSet<LongPieces> allPieces = piecesPool.getAllPieces();

        if (piecesPool.isHoldReduced()) {
            // allとvalidが異なる
            return piecesSolution.stream()
                    .filter(validPieces::contains)
                    .flatMap(blocks -> {
                        return reverseOrderLookUpReduceDepth.parse(blocks.getPieces())
                                .map(stream -> stream.collect(Collectors.toCollection(ArrayList::new)))
                                .flatMap(blocksWithHold -> {
                                    int nullIndex = blocksWithHold.indexOf(null);
                                    if (nullIndex < 0)
                                        return Stream.of(new LongPieces(blocksWithHold));

                                    Stream.Builder<LongPieces> builder = Stream.builder();
                                    for (Piece piece : Piece.values()) {
                                        blocksWithHold.set(nullIndex, piece);
                                        builder.accept(new LongPieces(blocksWithHold));
                                    }
                                    return builder.build();
                                });
                    })
                    .filter(allPieces::contains)
                    .collect(Collectors.toCollection(HashSet::new));
        } else if (isUsingHold) {
            // allとvalidが同じだが、ホールドが使える
            return piecesSolution.stream()
                    .filter(validPieces::contains)
                    .flatMap(blocks -> {
                        return reverseOrderLookUpSameDepth.parse(blocks.getPieces())
                                .map(stream -> stream.collect(Collectors.toCollection(ArrayList::new)))
                                .flatMap(blocksWithHold -> {
                                    int nullIndex = blocksWithHold.indexOf(null);
                                    if (nullIndex < 0)
                                        return Stream.of(new LongPieces(blocksWithHold));

                                    Stream.Builder<LongPieces> builder = Stream.builder();
                                    for (Piece piece : Piece.values()) {
                                        blocksWithHold.set(nullIndex, piece);
                                        builder.accept(new LongPieces(blocksWithHold));
                                    }
                                    return builder.build();
                                });
                    })
                    .filter(allPieces::contains)
                    .collect(Collectors.toCollection(HashSet::new));

        } else {
            // allとvalidが同じで、ホールドも使えない
            // そのまま絞り込みだけ実施
            return piecesSolution.stream()
                    .filter(validPieces::contains)
                    .collect(Collectors.toCollection(HashSet::new));
        }
    }

    private HashSet<LongPieces> getValidSpecifiedPatterns(Field field, LinkedList<MinoOperationWithKey> operations, int maxClearLine) {
        HashSet<LongPieces> allSpecifiedPieces = piecesPool.getAllSpecifiedPieces();

        Reachable reachable = reachableThreadLocal.get();

        if (isUsingHold) {
            // ミノ順の並び替えも確認する
            return allSpecifiedPieces.stream()
                    .filter(pieces -> {
                        return BuildUp.existsValidByOrderWithHold(field, operations.stream(), pieces.getPieces(), maxClearLine, reachable, maxDepth);
                    })
                    .collect(Collectors.toCollection(HashSet::new));
        } else {
            // そのまま絞り込む
            return allSpecifiedPieces.stream()
                    .filter(pieces -> {
                        return BuildUp.existsValidByOrder(field, operations.stream(), pieces.getPieces(), maxClearLine, reachable, maxDepth);
                    })
                    .collect(Collectors.toCollection(HashSet::new));
        }
    }
}