package searcher.pack;

import common.comparator.OperationWithKeyComparator;
import common.datastore.BlockField;
import common.datastore.OperationWithKey;

import java.util.Comparator;
import java.util.List;

public class MinoFieldComparator implements Comparator<IMinoField> {
    public static int compareMinoField(IMinoField o1, IMinoField o2) {
        BlockField blockField1 = o1.getBlockField();
        BlockField blockField2 = o2.getBlockField();
        int compareBlockField = blockField1.compareTo(blockField2);
        if (compareBlockField != 0)
            return compareBlockField;

        List<OperationWithKey> operations1 = o1.getOperations();
        List<OperationWithKey> operations2 = o2.getOperations();
        int compareSize = Integer.compare(operations1.size(), operations2.size());
        if (compareSize != 0)
            return compareSize;

        for (int index = 0; index < operations1.size(); index++) {
            int compare = OperationWithKeyComparator.compareOperationWithKey(operations1.get(index), operations2.get(index));
            if (compare != 0)
                return compare;
        }

        return 0;
    }

    @Override
    public int compare(IMinoField o1, IMinoField o2) {
        return compareMinoField(o1, o2);
    }
}