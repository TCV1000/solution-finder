package searcher.checker;

import common.datastore.action.Action;
import common.datastore.pieces.Blocks;
import common.pattern.PiecesGenerator;
import common.tree.AnalyzeTree;
import core.action.candidate.Candidate;
import core.action.candidate.LockedCandidate;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;
import core.mino.MinoFactory;
import core.mino.MinoShifter;
import core.srs.MinoRotation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import searcher.common.validator.PerfectValidator;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CheckerNoHoldCountTest {
    private AnalyzeTree runTestCase(PiecesGenerator piecesGenerator, int maxClearLine, int maxDepth, String marks) {
        Field field = FieldFactory.createField(marks);

        // Initialize
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();
        MinoRotation minoRotation = new MinoRotation();
        PerfectValidator validator = new PerfectValidator();
        CheckerNoHold<Action> checker = new CheckerNoHold<>(minoFactory, validator);

        // Measure
        Candidate<Action> candidate = new LockedCandidate(minoFactory, minoShifter, minoRotation, maxClearLine);
        AnalyzeTree tree = new AnalyzeTree();

        for (Blocks pieces : piecesGenerator) {
            List<Block> blocks = pieces.getBlockList();
            boolean result = checker.check(field, blocks, candidate, maxClearLine, maxDepth);
            tree.set(result, blocks);
        }

        return tree;
    }

    @Test
    void testCase1() throws Exception {
        // Invoker
        PiecesGenerator piecesGenerator = new PiecesGenerator("*p7");
        int maxClearLine = 4;
        int maxDepth = 6;

        // Field
        String marks = "" +
                "X________X" +
                "X________X" +
                "XX______XX" +
                "XXXXXX__XX" +
                "";

        AnalyzeTree tree = runTestCase(piecesGenerator, maxClearLine, maxDepth, marks);

        // Source: reply in twitter from @fullfool_14
        assertThat(tree.getSuccessPercent()).isEqualTo(1439 / 5040.0);
    }

    @Test
    void testCase2() throws Exception {
        // Invoker
        PiecesGenerator piecesGenerator = new PiecesGenerator("*p4");
        int maxClearLine = 5;
        int maxDepth = 4;

        // Field
        String marks = "" +
                "____XXXXXX" +
                "____XXXXXX" +
                "___XXXXXXX" +
                "__XXXXXXXX" +
                "___XXXXXXX" +
                "";
        AnalyzeTree tree = runTestCase(piecesGenerator, maxClearLine, maxDepth, marks);

        // Source: reply in twitter from @fullfool_14
        assertThat(tree.getSuccessPercent()).isEqualTo(477 / 2520.0);
    }

    @Test
    void testCase3() throws Exception {
        // Invoker
        PiecesGenerator piecesGenerator = new PiecesGenerator("*p7");
        int maxClearLine = 4;
        int maxDepth = 6;

        // Field
        String marks = "" +
                "X_________" +
                "X___X_____" +
                "XXXXXXX___" +
                "XXXXXX____" +
                "";
        AnalyzeTree tree = runTestCase(piecesGenerator, maxClearLine, maxDepth, marks);

        // Source: reply in twitter from @fullfool_14
        assertThat(tree.getSuccessPercent()).isEqualTo(727 / 5040.0);
    }

    @Test
    void testCase4() throws Exception {
        // Invoker
        PiecesGenerator piecesGenerator = new PiecesGenerator("*p7");
        int maxClearLine = 4;
        int maxDepth = 6;

        // Field
        String marks = "" +
                "XXXX______" +
                "XXXX______" +
                "XXXX______" +
                "XXXX______" +
                "";
        AnalyzeTree tree = runTestCase(piecesGenerator, maxClearLine, maxDepth, marks);

        // Source: myself 20170415
        assertThat(tree.getSuccessPercent()).isEqualTo(1902 / 5040.0);
    }

    @Nested
    class BT {
        private final int maxClearLine = 4;
        private final int maxDepth = 6;
        private final String pattern = "*p7";
        private PiecesGenerator piecesGenerator;

        @BeforeEach
        void setUp() {
            this.piecesGenerator = new PiecesGenerator(pattern);
        }

        @Test
        void case1() {
            // Field
            String marks = "" +
                    "XX________" +
                    "XX________" +
                    "XXX______X" +
                    "XXXXXXX__X" +
                    "";

            // Source: twitter from @26_nameless 20170729
            AnalyzeTree tree = runTestCase(piecesGenerator, maxClearLine, maxDepth, marks);
            assertThat(tree.getSuccessPercent()).isEqualTo(1439 / 5040.0);
        }

        @Test
        void case2() {
            // Field
            String marks = "" +
                    "___XX_____" +
                    "___XX_____" +
                    "__XXX____X" +
                    "XXXXXXX__X" +
                    "";

            // Source: twitter from @26_nameless 20170729
            AnalyzeTree tree = runTestCase(piecesGenerator, maxClearLine, maxDepth, marks);
            assertThat(tree.getSuccessPercent()).isEqualTo(475 / 5040.0);
        }

        @Test
        void case3() {
            // Field
            String marks = "" +
                    "__________" +
                    "XX________" +
                    "XXXXX____X" +
                    "XXXXXXX__X" +
                    "";

            // Source: twitter from @26_nameless 20170729
            AnalyzeTree tree = runTestCase(piecesGenerator, maxClearLine, maxDepth, marks);
            assertThat(tree.getSuccessPercent()).isEqualTo(353 / 5040.0);
        }

        @Test
        void case4() {
            // Field
            String marks = "" +
                    "__________" +
                    "_XX_______" +
                    "XXXXX____X" +
                    "XXXXXXX__X" +
                    "";

            // Source: twitter from @26_nameless 20170729
            AnalyzeTree tree = runTestCase(piecesGenerator, maxClearLine, maxDepth, marks);
            assertThat(tree.getSuccessPercent()).isEqualTo(290 / 5040.0);
        }

        @Test
        void case5() {
            // Field
            String marks = "" +
                    "__________" +
                    "__XX______" +
                    "XXXXX____X" +
                    "XXXXXXX__X" +
                    "";

            // Source: twitter from @26_nameless 20170729
            AnalyzeTree tree = runTestCase(piecesGenerator, maxClearLine, maxDepth, marks);
            assertThat(tree.getSuccessPercent()).isEqualTo(434 / 5040.0);
        }

        @Test
        void case6() {
            // Field
            String marks = "" +
                    "__________" +
                    "___XX_____" +
                    "XXXXX____X" +
                    "XXXXXXX__X" +
                    "";

            // Source: twitter from @26_nameless 20170729
            AnalyzeTree tree = runTestCase(piecesGenerator, maxClearLine, maxDepth, marks);
            assertThat(tree.getSuccessPercent()).isEqualTo(328 / 5040.0);
        }

        @Test
        void case7() {
            // Field
            String marks = "" +
                    "__________" +
                    "_X__X_____" +
                    "XXXXX____X" +
                    "XXXXXXX__X" +
                    "";

            // Source: twitter from @26_nameless 20170729
            AnalyzeTree tree = runTestCase(piecesGenerator, maxClearLine, maxDepth, marks);
            assertThat(tree.getSuccessPercent()).isEqualTo(346 / 5040.0);
        }

        @Test
        void case8() {
            // Field
            String marks = "" +
                    "XXX_______" +
                    "XX________" +
                    "XX_______X" +
                    "XXXXXXX__X" +
                    "";

            // Source: twitter from @26_nameless 20170729
            AnalyzeTree tree = runTestCase(piecesGenerator, maxClearLine, maxDepth, marks);
            assertThat(tree.getSuccessPercent()).isEqualTo(843 / 5040.0);
        }
    }
}
