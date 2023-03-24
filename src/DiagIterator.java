import java.util.Iterator;

class DiagIterator implements Iterable<Integer>{
  int startIdx;
  int direction;
  
  public DiagIterator(int startIdx, int direction){
    this.startIdx=startIdx;
    this.direction=direction;
  }
  
  public static DiagIterator iter(int start, int dir){
    return new DiagIterator(start,dir);
  }
  
  public Iterator<Integer> iterator(){
    return new DiagIteratorIter(this.startIdx, this.direction);
  }
  
  private class DiagIteratorIter implements Iterator<Integer>{
        private int delta;
        /** current index this iterator is at */
        private int idx;
        private int endIdx;
    
        DiagIteratorIter(int startIdx, int direction){
            this.delta=direction;
            
            this.idx=startIdx;
            int col = startIdx%8;
            int row = startIdx/8;
            int maxIter = 0;
            if(direction==0){
              this.delta=-9;
              maxIter=Math.min(col,row);
            }else if(direction==1){
              this.delta=-7;
              maxIter=Math.min(7-col,row);
            }else if(direction==2){
              this.delta=7;
              maxIter=Math.min(col,7-row);
            }else{
              this.delta=9;
              maxIter=Math.min(7-col,7-row);
            }
            this.endIdx=maxIter*delta+startIdx;
        }
        /** are there still words in this dictionary that we haven't iterated through yet? */
        public boolean hasNext() {
            return idx!=endIdx;
        }
        /** gets the word at index and then iterates the index */
        public Integer next() {
            return idx+=delta;
        }
    }
}