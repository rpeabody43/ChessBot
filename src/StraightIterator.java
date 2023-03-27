import java.util.Iterator;

class StraightIterator implements Iterable<Integer> {
    int startIdx;
    int direction;

    public StraightIterator(int startIdx, int direction) {
        this.startIdx = startIdx;
        this.direction = direction;
    }

    public static StraightIterator iter(int start, int dir) {
        return new StraightIterator(start, dir);
    }

    public Iterator<Integer> iterator() {
        return new StraightIteratorIter(this.startIdx, this.direction);
    }

    private class StraightIteratorIter implements Iterator<Integer> {
        private int delta;
        /**
         * current index this iterator is at
         */
        private int idx;
        private int endIdx;

        StraightIteratorIter(int startIdx, int direction) {
            this.delta = direction;

            this.idx = startIdx;
            int col = startIdx % 8;
            int row = startIdx / 8;
            int maxIter = 0;
            if (direction == 0) {
                this.delta = -1;
                maxIter = col;
            } else if (direction == 1) {
                this.delta = -8;
                maxIter = row;
            } else if (direction == 2) {
                this.delta = 1;
                maxIter = 7 - col;
            } else {
                this.delta = 8;
                maxIter = 7 - row;
            }
            this.endIdx = maxIter * delta + startIdx;
        }

        /**
         * are there still words in this dictionary that we haven't iterated through yet?
         */
        public boolean hasNext() {
            return idx != endIdx;
        }

        /**
         * gets the word at index and then iterates the index
         */
        public Integer next() {
            return idx += delta;
        }
    }
}