package src;

public class Node {
    public int row = 0;
    public int column = 0;
    public char data;
    //public int distance_to_goal;
    public Node parent = null;

    public Node(int row , int column)
    {
        this.row = row;
        this.column = column;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final Node other = (Node) obj;
        return this.row == other.row && this.column == other.column;
    }
}
