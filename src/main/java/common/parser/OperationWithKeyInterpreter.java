package common.parser;

import common.datastore.MinoOperationWithKey;
import common.datastore.OperationWithKey;
import common.datastore.FullOperationWithKey;
import core.mino.Piece;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.srs.Rotate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OperationWithKeyInterpreter {
    public static String parseToString(List<? extends OperationWithKey> operations) {
        return parseToString(operations.stream());
    }

    public static String parseToString(Stream<? extends OperationWithKey> operations) {
        return operations
                .map(OperationWithKeyInterpreter::parseToString)
                .collect(Collectors.joining(";"));
    }

    private static String parseToString(OperationWithKey operation) {
        return String.format("%s,%s,%d,%d,%d,%d",
                operation.getPiece().getName(),
                StringEnumTransform.toString(operation.getRotate()),
                operation.getX(),
                operation.getY(),
                operation.getNeedDeletedKey(),
                operation.getUsingKey()
        );
    }

    public static List<MinoOperationWithKey> parseToList(String operations, MinoFactory minoFactory) {
        return parseToStream(operations, minoFactory).collect(Collectors.toList());
    }

    public static Stream<MinoOperationWithKey> parseToStream(String operations, MinoFactory minoFactory) {
        return Arrays.stream(operations.split(";"))
                .map(s -> s.split(","))
                .map(strings -> {
                    Piece piece = StringEnumTransform.toPiece(strings[0]);
                    Rotate rotate = StringEnumTransform.toRotate(strings[1]);
                    Mino mino = minoFactory.create(piece, rotate);
                    int x = Integer.valueOf(strings[2]);
                    int y = Integer.valueOf(strings[3]);
                    long deleteKey = Long.valueOf(strings[4]);
                    long usingKey = Long.valueOf(strings[5]);
                    return new FullOperationWithKey(mino, x, y, deleteKey, usingKey);
                });
    }
}
