package concurrent.checkmate.invoker.no_hold;

import common.datastore.blocks.ReadOnlyListPieces;
import core.action.candidate.Candidate;
import common.datastore.Pair;
import core.mino.Piece;
import searcher.checkmate.Checkmate;
import common.datastore.Result;
import common.datastore.action.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

class Task implements Callable<List<Pair<List<Piece>, List<Result>>>> {
    private final Obj obj;
    private final List<ReadOnlyListPieces> targets;

    Task(Obj obj, List<ReadOnlyListPieces> targets) {
        this.obj = obj;
        this.targets = targets;
    }

    @Override
    public List<Pair<List<Piece>, List<Result>>> call() throws Exception {
        Checkmate<Action> checkmate = obj.checkmateThreadLocal.get();
        Candidate<Action> candidate = obj.candidateThreadLocal.get();

        // 探索
        List<Pair<List<Piece>, List<Result>>> allResults = new ArrayList<>();
        for (ReadOnlyListPieces piece : targets) {
            List<Piece> pieces = piece.getPieces();
            List<Result> results = checkmate.search(obj.field, pieces, candidate, obj.maxClearLine, obj.maxDepth);
            allResults.add(new Pair<>(pieces, results));
        }

        return allResults;
    }
}
