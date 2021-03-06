package common.parser;

import common.datastore.FullOperationWithKey;
import common.iterable.CombinationIterable;
import core.field.KeyOperators;
import core.mino.Piece;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.neighbor.SimpleOriginalPiece;
import core.srs.Rotate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractAllOperationFactory<T> {
    private final MinoFactory minoFactory;
    private final MinoShifter minoShifter;
    private final int fieldWidth;
    private final int fieldHeight;
    private final long deleteKeyMask;

    public AbstractAllOperationFactory(MinoFactory minoFactory, MinoShifter minoShifter, int fieldWidth, int fieldHeight) {
        this(minoFactory, minoShifter, fieldWidth, fieldHeight, Long.MAX_VALUE);
    }

    public AbstractAllOperationFactory(MinoFactory minoFactory, MinoShifter minoShifter, int fieldWidth, int fieldHeight, long deleteKeyMask) {
        this.minoFactory = minoFactory;
        this.minoShifter = minoShifter;
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        this.deleteKeyMask = deleteKeyMask;
    }

    public List<T> createList() {
        ArrayList<T> pieces = new ArrayList<>();

        for (Piece piece : Piece.values()) {
            for (Rotate rotate : minoShifter.getUniqueRotates(piece)) {
                Mino mino = minoFactory.create(piece, rotate);

                // ミノの高さを計算
                int minoHeight = mino.getMaxY() - mino.getMinY() + 1;

                // フィールドの高さ以上にミノを使う場合はおけない
                if (fieldHeight < minoHeight)
                    continue;

                // 行候補をリストにする
                ArrayList<Integer> lineIndexes = getLineIndexes(fieldHeight);

                // リストアップ
                ArrayList<T> piecesEachMino = generatePiecesEachMino(mino, lineIndexes, minoHeight);

                // 追加
                pieces.addAll(piecesEachMino);
            }
        }

        return pieces;
    }

    private ArrayList<Integer> getLineIndexes(int height) {
        ArrayList<Integer> lineIndexes = new ArrayList<>();
        for (int index = 0; index < height; index++)
            lineIndexes.add(index);
        return lineIndexes;
    }

    private ArrayList<T> generatePiecesEachMino(Mino mino, ArrayList<Integer> lineIndexes, int minoHeight) {
        ArrayList<T> pieces = new ArrayList<>();

        // ブロックが置かれる行を選択する
        CombinationIterable<Integer> combinationIterable = new CombinationIterable<>(lineIndexes, minoHeight);

        for (List<Integer> indexes : combinationIterable) {
            // ソートする
            indexes.sort(Integer::compare);

            // 一番下の行と一番上の行を取得
            int lowerY = indexes.get(0);
            int upperY = indexes.get(indexes.size() - 1);

            // ミノに挟まれる全ての行を含むdeleteKey
            long deleteKey = KeyOperators.getMaskForKeyAboveY(lowerY) & KeyOperators.getMaskForKeyBelowY(upperY + 1);
            long usingKey = 0L;

            assert Long.bitCount(deleteKey) == upperY - lowerY + 1;

            for (Integer index : indexes) {
                long bitKey = KeyOperators.getDeleteBitKey(index);

                // ブロックのある行のフラグを取り消す
                deleteKey &= ~bitKey;

                // ブロックのある行にフラグをたてる
                usingKey |= bitKey;
            }

            assert Long.bitCount(deleteKey) + indexes.size() == upperY - lowerY + 1;

            if ((deleteKeyMask & deleteKey) == deleteKey) {
                for (int x = -mino.getMinX(); x < fieldWidth - mino.getMaxX(); x++) {
                    FullOperationWithKey operationWithKey = new FullOperationWithKey(mino, x, lowerY - mino.getMinY(), deleteKey, usingKey);
                    pieces.add(parseOperation(operationWithKey, upperY, fieldHeight));
                }
            }
        }

        return pieces;
    }

    protected abstract T parseOperation(FullOperationWithKey operationWithKey, int upperY, int fieldHeight);

    public Set<T> create() {
        return new HashSet<>(createList());
    }
}
