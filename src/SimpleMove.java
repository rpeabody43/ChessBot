public class SimpleMove {
    public int start;
    public int end;

    public SimpleMove () {
        setEmpty();
    }

    public boolean isEmpty () {
        return start == 0 && end == 0;
    }

    public void setEmpty () {
        start = 0;
        end = 0;
    }

    public void setFrom (Move m) {
        start = m.getStartIdx();
        end = m.getEndIdx();
    }
}
