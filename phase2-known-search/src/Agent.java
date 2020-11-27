package src;

import src.base.Action;
import src.base.AgentData;
import src.base.BaseAgent;
import src.base.TurnData;
import java.io.IOException;
import java.util.*;

public class Agent extends BaseAgent {

    private int HOME = 1 , DIAMOND = 2;

    int Goal_ROW,Goal_COLUMN;


    private Stack<Action> actions = new Stack<>();
    private Queue<Node> frontier = new LinkedList<>();
    private List<Node> explored_set = new ArrayList<>();

    public Agent() throws IOException {
        super();
    }

    private int[] GoalFinder(char[][] map) {

        for(int i=0;i< map.length;i++ )
            for(int j=0; j< map.length;j++)
                if(map[i][j] == '0'||map[i][j] == '1'||map[i][j] == '2'||map[i][j] == '3'||map[i][j] == '4')
                    return new int[]{i,j};

     return new int[]{-1,-1};
    }

    @Override
    public Action doTurn(TurnData turnData) {

        // find diamond indexes
        int[] indexes  = GoalFinder(turnData.map);
         Goal_ROW = indexes[0];  Goal_COLUMN= indexes[1];


        //finds diamond path
        if (turnData.turnsLeft == maxTurns)
           find_route(turnData , DIAMOND);



        while (!actions.isEmpty())
            return actions.pop();

        //finds home
        find_route(turnData , HOME);

        while (!actions.isEmpty())
            return actions.pop();


        //just to return sth
        return Action.values()[(int) (Math.random() * Action.values().length)];

    }


    private boolean is_in_explored_set(Node node)
    {
        for (Node node1 : explored_set)
        {
            if (node1.equals(node))
                return true;
        }
        return false;

    }

    private Action find_action_to_parent(Node node)
    {
        if (node.parent.row == node.row)
        {
            if (node.parent.column - node.column == -1)
            {
                System.out.println("right");
                return Action.RIGHT;
            }
            else
            {
                System.out.println("left");
                return Action.LEFT;
            }
        }
        else
        {
            if (node.parent.row - node.row == -1)
            {
                System.out.println("down");
                return Action.DOWN;
            }
            else
            {
                System.out.println("up");
                return Action.UP;
            }
        }
    }

    private void print_path(Node node)
    {
        for( Node node2 : explored_set){

            System.out.println("["+ node2.row+","+node2.column+"]"+"\n/////////////////////////////");
        }

        Node node1 = node;
        while (node1!=null)
        {
            if (node1.parent!=null)
                actions.add(find_action_to_parent(node1));
            node1 = node1.parent;
        }


    }
    private boolean find_route(TurnData turnData , int mode)
    {

        explored_set.clear();

        actions = new Stack<>();
        frontier = new LinkedList<>();
        explored_set = new ArrayList<>();


        int grid_size = turnData.map.length;
        AgentData agent = turnData.agentData[0];
        Node first_node = new Node(agent.position.row,agent.position.column);
        frontier.add(first_node);

        while (!frontier.isEmpty()) {
            Node node = frontier.poll();

            //changed algorithm here
            if (!is_in_explored_set(node))
            {
                explored_set.add(node);

                Node expanded_node;

                if (node.row + 1 < grid_size) {
                    if (turnData.map[node.row + 1][node.column] != '*') {
                        expanded_node = new Node(node.row + 1, node.column);
                        expanded_node.parent = node;
                        expanded_node.data = turnData.map[node.row + 1][node.column];
                        if (is_goal(expanded_node , mode)) {
                            print_path(expanded_node);
                            return true;

                        } else {
                            frontier.add(expanded_node);
                        }
                    }
                }
                if (node.row - 1 >= 0) {
                    if (turnData.map[node.row - 1][node.column] != '*') {
                        expanded_node = new Node(node.row - 1, node.column);
                        expanded_node.parent = node;
                        expanded_node.data = turnData.map[node.row - 1][node.column];
                        if (is_goal(expanded_node , mode)) {
                            print_path(expanded_node );
                            return true;
                        } else {
                            frontier.add(expanded_node);
                        }
                    }
                }
                if (node.column + 1 < grid_size) {
                    if (turnData.map[node.row][node.column + 1] != '*') {
                        expanded_node = new Node(node.row, node.column + 1);
                        expanded_node.parent = node;
                        expanded_node.data = turnData.map[node.row][node.column + 1];
                        if (is_goal(expanded_node ,mode)) {
                            print_path(expanded_node );
                            return true;
                        } else {
                            frontier.add(expanded_node);
                        }
                    }
                }
                if (node.column - 1 >= 0) {
                    if (turnData.map[node.row][node.column - 1] != '*') {
                        expanded_node = new Node(node.row, node.column - 1);
                        expanded_node.parent = node;
                        expanded_node.data = turnData.map[node.row][node.column - 1];
                        if (is_goal(expanded_node , mode)) {
                            print_path(expanded_node );
                            return true;
                        } else {
                            frontier.add(expanded_node);
                        }
                    }
                }
            }
            /*
            else drops it and goes on
             */

            if(mode==DIAMOND) {
                frontier = sortFrontier(frontier);
            }

        }
        return false;
    }

    private LinkedList<Node> sortFrontier(Queue<Node> frontier) {

        Node[] array;
        array= frontier.toArray(new Node[frontier.size()]);

        Iterator<Node> itr= frontier.iterator();

            for (int i = 0; i < frontier.size()-1; i++)
                for (int j = 0; j < frontier.size()-i-1; j++)
                    if (Math.abs(array[j].row-Goal_ROW)+Math.abs(array[j].column-Goal_COLUMN)  + array[j].row+array[j].column  >=
                            Math.abs(array[j+1].row-Goal_ROW)+Math.abs(array[j+1].column-Goal_COLUMN) + array[j].row+array[j].column )
                    {
                        // swap arr[j+1] and arr[j]
                        Node temp = array[j];
                        array[j] = array[j+1];
                        array[j+1] = temp;
                    }
            return new LinkedList<Node>(Arrays.asList(array));

    }

    private boolean is_goal(Node node , int mode)
    {
        if (mode == DIAMOND)
            return  node.data == '0' ||
                    node.data == '1' ||
                    node.data == '2' ||
                    node.data == '3' ||
                    node.data == '4';

        return node.data == 'a';

    }


}
