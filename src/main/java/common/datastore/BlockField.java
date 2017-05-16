package common.datastore;

import common.comparator.FieldComparator;
import core.field.Field;
import core.field.FieldFactory;
import core.mino.Block;

import java.util.Comparator;
import java.util.EnumMap;

// TODO: unittest
public class BlockField implements Comparable<BlockField> {
    private static final Comparator<Field> FIELD_COMPARATOR = new FieldComparator();
    private static final Field EMPTY_FIELD = FieldFactory.createField(1);

    private final int height;
    private final EnumMap<Block, Field> map = new EnumMap<>(Block.class);

    public BlockField(int height) {
        this.height = height;
    }

    public void merge(Field field, Block block) {
        map.computeIfAbsent(block, b -> FieldFactory.createField(height)).merge(field);
    }

    public Field get(Block block) {
        return map.getOrDefault(block, EMPTY_FIELD);
    }

    public Field getMergedField() {
        Field field = FieldFactory.createField(height);
        for (Field fieldEachBlock : map.values())
            field.merge(fieldEachBlock);
        return field;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockField that = (BlockField) o;
        return map.equals(that.map);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public int compareTo(BlockField o) {
        for (Block block : Block.values()) {
            Field field = this.map.getOrDefault(block, EMPTY_FIELD);
            Field oField = o.map.getOrDefault(block, EMPTY_FIELD);
            int compare = FIELD_COMPARATOR.compare(field, oField);
            if (compare != 0)
                return compare;
        }
        return 0;
    }
}
