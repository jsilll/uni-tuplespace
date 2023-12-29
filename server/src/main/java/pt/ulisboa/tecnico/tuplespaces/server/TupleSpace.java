package pt.ulisboa.tecnico.tuplespaces.server;

import java.util.List;
import java.util.ArrayList;

public class TupleSpace {
    public class Tuple {
        private String[] elements;

        public Tuple(String... elements) {
            this.elements = elements;
        }

        public String[] getElements() {
            return elements;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("(");
            for (String element : elements) {
                sb.append(element);
                sb.append(", ");
            }
            sb.delete(sb.length() - 2, sb.length());
            sb.append(")");
            return sb.toString();
        }
    }

    private List<Tuple> tuples;

    public TupleSpace() {
        this.tuples = new ArrayList<>();
    }

    public synchronized void add(String... elements) {
        tuples.add(new Tuple(elements));
    }

    public synchronized Tuple read(String... elements) { 
        for (Tuple tuple : tuples) {
            boolean match = true;
            for (int i = 0; i < elements.length; i++) {
                if (!elements[i].equals("*") && !elements[i].equals(tuple.getElements()[i])) {
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

    public synchronized Tuple take(String... elements) {
        for (Tuple tuple : tuples) {
            boolean match = true;
            for (int i = 0; i < elements.length; i++) {
                if (!elements[i].equals("*") && !elements[i].equals(tuple.getElements()[i])) {
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
        for (Tuple tuple : tuples) {
            sb.append(tuple.toString());
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append("]");
        return sb.toString();
    }
}
