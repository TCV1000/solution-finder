package common;

import common.datastore.Operation;
import core.action.reachable.LockedReachable;
import core.field.Field;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.Piece;
import core.srs.MinoRotationDetail;
import core.srs.Rotate;
import core.srs.RotateDirection;
import core.srs.SpinResult;
import searcher.spins.SpinCommons;
import searcher.spins.spin.Spin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpinChecker {
    private final MinoFactory minoFactory;
    private final MinoRotationDetail minoRotationDetail;
    private final LockedReachable lockedReachable;

    public SpinChecker(MinoFactory minoFactory, MinoRotationDetail minoRotationDetail, LockedReachable lockedReachable) {
        this.minoFactory = minoFactory;
        this.minoRotationDetail = minoRotationDetail;
        this.lockedReachable = lockedReachable;
    }

    public Optional<Spin> check(Field field, Operation operation, int fieldHeight, int clearedLine) {
        Rotate rotate = operation.getRotate();

        if (!SpinCommons.canTSpin(field, operation.getX(), operation.getY())) {
            return Optional.empty();
        }

        // 優先度の高いスピンを探索
        Spin maxSpin = null;
        int maxPriority = -1;

        // 左回転, 右回転
        for (RotateDirection direction : RotateDirection.values()) {
            RotateDirection beforeDirection = RotateDirection.reverse(direction);

            Mino before = minoFactory.create(Piece.T, rotate.get(beforeDirection));
            int[][] patterns = minoRotationDetail.getPatternsFrom(before, direction);

            List<Spin> spins = getSpins(
                    field, operation, before, patterns, direction, fieldHeight, clearedLine
            );
            for (Spin spin : spins) {
                int priority = getSpinPriority(spin);
                if (maxSpin == null || maxPriority < priority) {
                    maxSpin = spin;
                    maxPriority = priority;
                }
            }
        }

        return maxSpin != null ? Optional.of(maxSpin) : Optional.empty();
    }

    private List<Spin> getSpins(
            Field fieldWithoutT, Operation operation,
            Mino before, int[][] patterns, RotateDirection direction,
            int maxHeight, int clearedLine
    ) {
        List<Spin> spins = new ArrayList<>();

        for (int[] pattern : patterns) {
            // 開店前の位置に移動
            int beforeX = operation.getX() - pattern[0];
            int beforeY = operation.getY() - pattern[1];

            if (beforeX + before.getMinX() < 0 || 10 <= beforeX + before.getMaxX()) {
                continue;
            }

            if (beforeY + before.getMinY() < 0) {
                continue;
            }

            if (!fieldWithoutT.canPut(before, beforeX, beforeY)) {
                continue;
            }

            SpinResult spinResult = minoRotationDetail.getKicks(fieldWithoutT, direction, before, beforeX, beforeY);

            if (spinResult == SpinResult.NONE) {
                continue;
            }

            // 回転後に元の場所に戻る
            if (spinResult.getToX() != operation.getX() || spinResult.getToY() != operation.getY()) {
                continue;
            }

            // 回転前の位置に移動できる
            if (!lockedReachable.checks(fieldWithoutT, before, beforeX, beforeY, maxHeight)) {
                continue;
            }

            Spin spin = SpinCommons.getSpins(fieldWithoutT, spinResult, clearedLine);
            spins.add(spin);
        }

        return spins;
    }

    private int getSpinPriority(searcher.spins.spin.Spin spin) {
        int clearedLine = spin.getClearedLine();

        switch (spin.getSpin()) {
            case Mini: {
                return clearedLine * 10 + 1;
            }
            case Regular: {
                switch (spin.getName()) {
                    case Iso: {
                        return clearedLine * 10 + 2;
                    }
                    case Fin: {
                        return clearedLine * 10 + 3;
                    }
                    case Neo: {
                        return clearedLine * 10 + 4;
                    }
                    case NoName: {
                        return clearedLine * 10 + 5;
                    }
                }
            }
        }

        throw new IllegalStateException();
    }
}
