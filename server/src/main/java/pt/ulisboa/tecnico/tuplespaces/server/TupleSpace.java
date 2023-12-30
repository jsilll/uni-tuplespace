package pt.ulisboa.tecnico.tuplespaces.server;

import java.util.List;
import java.util.ArrayList;

public class TupleSpace {
    private List<String[]> tuples;

    public TupleSpace() {
        this.tuples = new ArrayList<>();
    }

    public synchronized List<String[]> read() {
        return new ArrayList<>(tuples);
    }

    public synchronized void add(String... elements) {
        tuples.add(elements);
    }

    public synchronized String[] read(String... elements) { 
        for (String[] tuple : tuples) {
            boolean match = true;
            if (tuple.length != elements.length) {
                continue;
            }
            for (int i = 0; i < elements.length; i++) {
                if (!elements[i].equals("*") && !elements[i].equals(tuple[i])) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return tuple;
            }
        }
        return null;
    }

    public synchronized String[] take(String... elements) {
        for (String[] tuple : tuples) {
            boolean match = true;
            if (tuple.length != elements.length) {
                continue;
            }
            for (int i = 0; i < elements.length; i++) {
                if (!elements[i].equals("*") && !elements[i].equals(tuple[i])) {
                    match = false;
                    break;
                }
            }
            if (match) {
                tuples.remove(tuple);
                return tuple;
            }
        }
        return null;
    }

    @Override
    public synchronized String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (String[] tuple : tuples) {
            sb.append("(");
            for (String element : tuple) {
                sb.append(element);
                sb.append(", ");
            }
            sb.delete(sb.length() - 2, sb.length());
            sb.append("), ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append("]");
        return sb.toString();
    }
}
