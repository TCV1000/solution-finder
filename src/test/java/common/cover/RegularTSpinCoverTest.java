package common.cover;

import common.cover.reachable.ReachableForCoverWrapper;
import common.datastore.*;
import common.parser.BlockInterpreter;
import common.parser.OperationTransform;
import core.action.reachable.SoftdropTOnlyReachable;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.mino.Piece;
import core.srs.MinoRotation;
import core.srs.Rotate;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class RegularTSpinCoverTest {
    private final MinoFactory minoFactory = new MinoFactory();
    private final MinoShifter minoShifter = new MinoShifter();
    private final MinoRotation minoRotation = MinoRotation.create();

    @Test
    void cansBuildDouble1() {
        int height = 4;
        Field field = FieldFactory.createField("" +
                        "__________" +
                        "__________" +
                        "______XXXX" +
                        "___XXXXXXX"
                , height);
        List<Operation> operationList = Arrays.asList(
                new SimpleOperation(Piece.L, Rotate.Right, 0, 1),
                new SimpleOperation(Piece.Z, Rotate.Spawn, 4, 1),
                new SimpleOperation(Piece.T, Rotate.Reverse, 2, 1)
        );
        List<MinoOperationWithKey> operationsWithKey = toMinoOperationWithKey(operationList, field, height);
        ReachableForCoverWrapper reachable = new ReachableForCoverWrapper(new SoftdropTOnlyReachable(minoFactory, minoShifter, minoRotation, height));

        RegularTSpinCover cover = new RegularTSpinCover(2);

        {
            List<Piece> pieces = toPieceList("LT");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
        }

        {
            List<Piece> pieces = toPieceList("LZT");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }

        {
            List<Piece> pieces = toPieceList("TLZ");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }

        {
            List<Piece> pieces = toPieceList("OLTZ");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
        }

        {
            List<Piece> pieces = toPieceList("LZTOIJS");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }
    }

    @Test
    void cansBuildDouble2() {
        int height = 4;
        Field field = FieldFactory.createField("" +
                        "__________" +
                        "__________" +
                        "______XXXX" +
                        "XXX_XXXXXX"
                , height);
        List<Operation> operationList = Arrays.asList(
                new SimpleOperation(Piece.L, Rotate.Reverse, 6, 2),
                new SimpleOperation(Piece.S, Rotate.Spawn, 1, 1),
                new SimpleOperation(Piece.T, Rotate.Reverse, 3, 1)
        );
        List<MinoOperationWithKey> operationsWithKey = toMinoOperationWithKey(operationList, field, height);
        ReachableForCoverWrapper reachable = new ReachableForCoverWrapper(new SoftdropTOnlyReachable(minoFactory, minoShifter, minoRotation, height));

        RegularTSpinCover cover = new RegularTSpinCover(2);

        {
            List<Piece> pieces = toPieceList("LST");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }

        {
            List<Piece> pieces = toPieceList("STL");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }

        {
            List<Piece> pieces = toPieceList("OSTL");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
        }
    }

    @Test
    void cansBuildSingle1() {
        int height = 4;
        Field field = FieldFactory.createField("" +
                        "__________" +
                        "__________" +
                        "______XXXX" +
                        "XXX_XXXXXX"
                , height);
        List<Operation> operationList = Arrays.asList(
                new SimpleOperation(Piece.L, Rotate.Reverse, 6, 2),
                new SimpleOperation(Piece.S, Rotate.Spawn, 1, 1),
                new SimpleOperation(Piece.T, Rotate.Reverse, 3, 1)
        );
        List<MinoOperationWithKey> operationsWithKey = toMinoOperationWithKey(operationList, field, height);
        ReachableForCoverWrapper reachable = new ReachableForCoverWrapper(new SoftdropTOnlyReachable(minoFactory, minoShifter, minoRotation, height));

        RegularTSpinCover cover = new RegularTSpinCover(1);

        {
            List<Piece> pieces = toPieceList("LST");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }

        {
            List<Piece> pieces = toPieceList("STL");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }

        {
            List<Piece> pieces = toPieceList("OSTL");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isTrue();
        }
    }

    @Test
    void cansBuildMini() {
        int height = 4;
        Field field = FieldFactory.createField("" +
                        "__________" +
                        "__________" +
                        "__________" +
                        "_XXXXXXXXX"
                , height);
        List<Operation> operationList = Arrays.asList(
                new SimpleOperation(Piece.O, Rotate.Spawn, 2, 1),
                new SimpleOperation(Piece.T, Rotate.Right, 0, 1)
        );
        List<MinoOperationWithKey> operationsWithKey = toMinoOperationWithKey(operationList, field, height);
        ReachableForCoverWrapper reachable = new ReachableForCoverWrapper(new SoftdropTOnlyReachable(minoFactory, minoShifter, minoRotation, height));

        RegularTSpinCover cover = new RegularTSpinCover(1);

        {
            List<Piece> pieces = toPieceList("TO");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
        }

        {
            List<Piece> pieces = toPieceList("OT");

            assertThat(
                    cover.canBuild(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
            assertThat(
                    cover.canBuildWithHold(field, operationsWithKey.stream(), pieces, height, reachable, operationsWithKey.size())
            ).isFalse();
        }
    }

    private List<Piece> toPieceList(String str) {
        return BlockInterpreter.parse(str).collect(Collectors.toList());
    }

    private List<MinoOperationWithKey> toMinoOperationWithKey(List<Operation> operationList, Field field, int height) {
        return OperationTransform.parseToOperationWithKeys(
                field, new Operations(operationList), minoFactory, height
        ).stream().map(m -> {
            Mino mino = minoFactory.create(m.getPiece(), m.getRotate());
            return new MinimalOperationWithKey(mino, m.getX(), m.getY(), m.getNeedDeletedKey());
        }).collect(Collectors.toList());
    }
}