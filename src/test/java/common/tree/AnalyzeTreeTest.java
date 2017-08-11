package common.tree;

import common.datastore.pieces.LongBlocks;
import common.datastore.pieces.Blocks;
import common.pattern.PiecesGenerator;
import core.mino.Block;
import lib.Randoms;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

class AnalyzeTreeTest {
    @Test
    void success() {
        AnalyzeTree tree = new AnalyzeTree();
        tree.success(Arrays.asList(Block.I, Block.T, Block.O));
        assertThat(tree.isVisited(Arrays.asList(Block.I, Block.T, Block.O))).isTrue();
        assertThat(tree.isSucceed(Arrays.asList(Block.I, Block.T, Block.O))).isTrue();
    }

    @Test
    void fail() {
        AnalyzeTree tree = new AnalyzeTree();
        tree.fail(Arrays.asList(Block.Z, Block.J, Block.L));
        assertThat(tree.isVisited(Arrays.asList(Block.Z, Block.J, Block.L))).isTrue();
        assertThat(tree.isSucceed(Arrays.asList(Block.Z, Block.J, Block.L))).isFalse();
    }

    @Test
    void notVisited() {
        AnalyzeTree tree = new AnalyzeTree();
        assertThat(tree.isVisited(Arrays.asList(Block.O, Block.O, Block.O))).isFalse();
        assertThat(tree.isSucceed(Arrays.asList(Block.O, Block.O, Block.O))).isFalse();
    }

    @Test
    void show1() {
        AnalyzeTree tree = new AnalyzeTree();
        tree.success(Arrays.asList(Block.I, Block.T, Block.O));
        tree.success(Arrays.asList(Block.I, Block.T, Block.J));
        tree.success(Arrays.asList(Block.I, Block.T, Block.L));
        tree.fail(Arrays.asList(Block.I, Block.T, Block.S));
        tree.fail(Arrays.asList(Block.I, Block.T, Block.Z));

        assertThat(tree.getSuccessPercent()).isEqualTo(0.6);
        assertThat(tree.show())
                .contains("60.00%")
                .contains("3/5");
    }

    @Test
    void show2() {
        AnalyzeTree tree = new AnalyzeTree();
        tree.success(Arrays.asList(Block.I, Block.T));
        tree.success(Arrays.asList(Block.I, Block.S));
        tree.success(Arrays.asList(Block.S, Block.T));
        tree.success(Arrays.asList(Block.S, Block.O));
        tree.success(Arrays.asList(Block.S, Block.J));
        tree.success(Arrays.asList(Block.O, Block.I));
        tree.fail(Arrays.asList(Block.I, Block.I));
        tree.fail(Arrays.asList(Block.I, Block.Z));
        tree.fail(Arrays.asList(Block.Z, Block.I));
        tree.fail(Arrays.asList(Block.Z, Block.L));
        tree.fail(Arrays.asList(Block.Z, Block.L));  // same

        assertThat(tree.getSuccessPercent()).isCloseTo(0.5454545, offset(0.0000001));
        assertThat(tree.show())
                .contains("54.55%")
                .contains("6/11");
    }

    @Test
    void random() {
        Randoms randoms = new Randoms();
        for (int size = 1; size <= 7; size++) {
            PiecesGenerator generator = new PiecesGenerator("*p" + size);

            AnalyzeTree tree = new AnalyzeTree();
            HashSet<LongBlocks> success = new HashSet<>();
            HashSet<LongBlocks> failed = new HashSet<>();
            for (Blocks pieces : generator) {
                boolean flag = randoms.nextBoolean();
                List<Block> blocks = pieces.getBlocks();
                tree.set(flag, blocks);

                LongBlocks longPieces = new LongBlocks(blocks);
                if (flag) {
                    success.add(longPieces);
                } else {
                    failed.add(longPieces);
                }
            }

            boolean isSucceed = success.stream()
                    .allMatch(pieces -> {
                        List<Block> blocks = pieces.getBlocks();
                        return tree.isVisited(blocks) && tree.isSucceed(blocks);
                    });
            assertThat(isSucceed).isTrue();

            boolean isFailed = failed.stream()
                    .allMatch(pieces -> {
                        List<Block> blocks = pieces.getBlocks();
                        return tree.isVisited(blocks) && !tree.isSucceed(blocks);
                    });
            assertThat(isFailed).isTrue();

            double percent = (double) success.size() / (success.size() + failed.size());
            assertThat(tree.getSuccessPercent()).isCloseTo(percent, offset(0.0001));
        }
    }

    @Test
    @Tag("long")
    void randomLong() {
        Randoms randoms = new Randoms();
        for (int size = 8; size <= 11; size++) {
            PiecesGenerator generator = new PiecesGenerator("*p7, *p" + (size - 7));

            AnalyzeTree tree = new AnalyzeTree();
            HashSet<LongBlocks> success = new HashSet<>();
            HashSet<LongBlocks> failed = new HashSet<>();
            for (Blocks pieces : generator) {
                boolean flag = randoms.nextBoolean();
                List<Block> blocks = pieces.getBlocks();
                tree.set(flag, blocks);

                LongBlocks longPieces = new LongBlocks(blocks);
                if (flag) {
                    success.add(longPieces);
                } else {
                    failed.add(longPieces);
                }
            }

            boolean isSucceed = success.stream()
                    .allMatch(pieces -> {
                        List<Block> blocks = pieces.getBlocks();
                        return tree.isVisited(blocks) && tree.isSucceed(blocks);
                    });
            assertThat(isSucceed).isTrue();

            boolean isFailed = failed.stream()
                    .allMatch(pieces -> {
                        List<Block> blocks = pieces.getBlocks();
                        return tree.isVisited(blocks) && !tree.isSucceed(blocks);
                    });
            assertThat(isFailed).isTrue();

            double percent = (double) success.size() / (success.size() + failed.size());
            assertThat(tree.getSuccessPercent()).isCloseTo(percent, offset(0.0001));
        }
    }

    @Test
    void tree() {
        AnalyzeTree tree = new AnalyzeTree();
        tree.success(Arrays.asList(Block.S, Block.S));
        tree.success(Arrays.asList(Block.S, Block.T));
        tree.fail(Arrays.asList(Block.S, Block.T));
        tree.success(Arrays.asList(Block.Z, Block.Z));
        tree.fail(Arrays.asList(Block.O, Block.O));

        assertThat(tree.tree(1))
                .contains("60.0 %")
                .contains("S -> 66.7 %")
                .contains("Z -> 100.0 %")
                .contains("O -> 0.0 %")
                .doesNotContain("L")
                .doesNotContain("J");

        assertThat(tree.tree(2))
                .contains("60.0 %")
                .contains("SS -> 100.0 %")
                .contains("ST -> 50.0 %")
                .contains("ZZ -> 100.0 %")
                .contains("OO -> 0.0 %")
                .doesNotContain("L")
                .doesNotContain("J");

        assertThat(tree.tree(-1)).isEqualTo(tree.tree(2));
    }
}