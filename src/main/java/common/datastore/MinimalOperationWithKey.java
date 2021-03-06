package common.datastore;

import core.mino.Piece;
import core.mino.Mino;
import core.srs.Rotate;

public class MinimalOperationWithKey implements MinoOperationWithKey {
    private final Mino mino;
    private final int x;
    private final int y;
    private final long needDeletedKey;

    public MinimalOperationWithKey(Mino mino, int x, int y, long needDeletedKey) {
        this.mino = mino;
        this.x = x;
        this.y = y;
        this.needDeletedKey = needDeletedKey;
    }

    @Override
    public Mino getMino() {
        return mino;
    }

    @Override
    public Piece getPiece() {
        return mino.getPiece();
    }

    @Override
    public Rotate getRotate() {
        return mino.getRotate();
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public long getNeedDeletedKey() {
        return needDeletedKey;
    }

    @Override
    public long getUsingKey() {
        throw new UnsupportedOperationException("No using key");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof MinoOperationWithKey))
            return false;

        return MinoOperationWithKey.defaultEquals(this, (MinoOperationWithKey) o);
    }

    @Override
    public int hashCode() {
        return MinoOperationWithKey.defaultHash(mino, x, y, needDeletedKey);
    }

    @Override
    public String toString() {
        return "MinimalOperationWithKey{" +
                "mino=" + mino.getPiece() + "-" + mino.getRotate() +
                ", x=" + x +
                ", y=" + y +
                ", needDeletedKey=" + needDeletedKey +
                '}';
    }
}
