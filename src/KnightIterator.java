import java.util.Iterator;
import java.util.ArrayList;

public class KnightIterator implements Iterable<Integer> {
    private final int startIdx;

    public KnightIterator (int startIdx) {
        this.startIdx = startIdx;
    }

    public Iterator<Integer> iterator() {
        return new KnightIteratorIter(startIdx);
    }

    public static KnightIterator iter (int start) {
        return new KnightIterator(start);
    }

    private class KnightIteratorIter implements Iterator<Integer> {
        private ArrayList<Integer> deltas;
        private int i;

        KnightIteratorIter(int idx) {
            deltas = new ArrayList<>();

            int column = idx % 8;

            int[] knightDeltas = {-17, -15, -10, -6, 6, 10, 15, 17};
            int[] hozChanges = {-1, 1, -2, 2, -2, 2, -1, 1};

            for (int i = 0; i < knightDeltas.length; i++) {
                int newIdx = idx + knightDeltas[i];
                int newCol = column + hozChanges[i];

                if (newIdx <= -1 || newIdx >= 8)
                    continue;
                if (newCol <= -1 || newCol >= 8)
                    continue;

                deltas.add(newIdx);
            }
        }

        public boolean hasNext() {
            return i < deltas.size();
        }

        public Integer next() {
            return deltas.get(i++);
        }
    }
}
