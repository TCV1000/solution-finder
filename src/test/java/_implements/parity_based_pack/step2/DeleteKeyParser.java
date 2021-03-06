package _implements.parity_based_pack.step2;

import core.field.KeyOperators;
import core.mino.Piece;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.srs.Rotate;
import common.iterable.CombinationIterable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

class DeleteKeyParser {
    private final EnumMap<Piece, EnumMap<Rotate, List<DeleteKey>>> maps;

    DeleteKeyParser(MinoFactory minoFactory, int maxClearLine) {
        this.maps = initializeMaps(minoFactory, maxClearLine);
    }

    private EnumMap<Piece, EnumMap<Rotate, List<DeleteKey>>> initializeMaps(MinoFactory minoFactory, int maxClearLine) {
        EnumMap<Piece, EnumMap<Rotate, List<DeleteKey>>> maps = new EnumMap<>(Piece.class);
        for (Piece piece : Piece.values()) {
            EnumMap<Rotate, List<DeleteKey>> rotateMaps = maps.computeIfAbsent(piece, blk -> new EnumMap<>(Rotate.class));

            for (Rotate rotate : Rotate.values()) {
                Mino mino = minoFactory.create(piece, rotate);

                // ミノの高さを計算
                int minoHeight = mino.getMaxY() - mino.getMinY() + 1;

                // 行候補をリストにする
                ArrayList<Integer> lineIndexes = new ArrayList<>();
                for (int index = 0; index < maxClearLine; index++)
                    lineIndexes.add(index);

                // ブロックが置かれる行を選択する
                CombinationIterable<Integer> combinationIterable = new CombinationIterable<>(lineIndexes, minoHeight);

                // リストアップ
                ArrayList<DeleteKey> deleteLimitedMinos = new ArrayList<>();
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

                        // ブロックのある行のフラグをたてる
                        usingKey |= bitKey;
                    }

                    assert Long.bitCount(deleteKey) + indexes.size() == upperY - lowerY + 1;

                    deleteLimitedMinos.add(DeleteKey.create(mino, deleteKey, usingKey, lowerY, upperY));
                }

                rotateMaps.put(rotate, deleteLimitedMinos);
            }
        }

        return maps;
    }

    List<DeleteKey> parse(Mino mino) {
        return maps.get(mino.getPiece()).get(mino.getRotate());
    }
}
