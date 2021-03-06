package searcher.pack;

import core.mino.MinoFactory;
import core.mino.MinoShifter;
import lib.Randoms;
import org.junit.jupiter.api.Test;
import searcher.pack.separable_mino.AllSeparableMinoFactory;
import searcher.pack.separable_mino.SeparableMino;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SeparableMinosTest {
    private final Set<SeparableMino> minos = createSeparableMinoSet();

    private Set<SeparableMino> createSeparableMinoSet() {
        Randoms randoms = new Randoms();
        MinoFactory minoFactory = new MinoFactory();
        MinoShifter minoShifter = new MinoShifter();

        int fieldHeight = randoms.nextIntClosed(1, 10);
        int fieldWidth = randoms.nextIntClosed(1, 4);
        SizedBit sizedBit = new SizedBit(fieldWidth, fieldHeight);

        AllSeparableMinoFactory separableMinoFactory = new AllSeparableMinoFactory(minoFactory, minoShifter, sizedBit.getWidth(), sizedBit.getHeight(), sizedBit.getFillBoard());
        return separableMinoFactory.create();
    }

    @Test
    void create() {
        ArrayList<SeparableMino> minos1 = new ArrayList<>(minos);
        ArrayList<SeparableMino> minos2 = new ArrayList<>(minos);
        Collections.shuffle(minos2);

        SeparableMinos separableMinos1 = new SeparableMinos(minos1);
        SeparableMinos separableMinos2 = new SeparableMinos(minos2);

        assertThat(separableMinos1.getMinos()).isEqualTo(separableMinos2.getMinos());
    }

    @Test
    void toIndex() {
        ArrayList<SeparableMino> minos1 = new ArrayList<>(minos);
        ArrayList<SeparableMino> minos2 = new ArrayList<>(minos);
        Collections.shuffle(minos2);

        SeparableMinos separableMinos1 = new SeparableMinos(minos1);
        SeparableMinos separableMinos2 = new SeparableMinos(minos2);

        for (SeparableMino mino : minos)
            assertThat(separableMinos1.toIndex(mino)).isEqualTo(separableMinos2.toIndex(mino));
    }
}