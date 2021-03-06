package common;

import common.comparator.ResultPCFComparator;
import common.datastore.Operation;
import common.datastore.Result;
import common.datastore.SimpleOperation;
import common.datastore.action.Action;
import common.datastore.order.Order;
import core.mino.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Stream;

public class ResultHelper {
    private static final ResultPCFComparator COMPARATOR = new ResultPCFComparator();

    public static List<Result> uniquify(List<Result> results) {
        TreeSet<Result> set = new TreeSet<>(COMPARATOR);
        set.addAll(results);
        return new ArrayList<>(set);
    }

    public static Stream<Operation> createOperationStream(Result result) {
        Order order = result.getOrder();
        OperationHistory history = order.getHistory();
        Stream<Operation> operationStream = history.getOperationStream();

        Piece lastPiece = result.getLastPiece();
        Action lastAction = result.getLastAction();
        SimpleOperation lastOperation = new SimpleOperation(lastPiece, lastAction.getRotate(), lastAction.getX(), lastAction.getY());
        return Stream.concat(operationStream, Stream.of(lastOperation));
    }
}
