package com.hossein.ghojavand;


import com.hossein.ghojavand.base.Action;
import com.hossein.ghojavand.base.AgentData;
import com.hossein.ghojavand.base.BaseAgent;
import com.hossein.ghojavand.base.TurnData;
import java.io.IOException;
import java.util.*;


public class InformedSearchAgent extends BaseAgent {

    private int HOME = 1 , DIAMOND = 2;

    int Goal_ROW,Goal_COLUMN;




    private Stack<Action> actions = new Stack<>();
    private Queue<Node> frontier = new LinkedList<>();
    private List<Node> explored_set = new ArrayList<>();

    public InformedSearchAgent() throws IOException {
        super();
    }

    private int[] DiamondFinder(char[][] map) {

        for(int i=0;i< map.length;i++ )
            for(int j=0; j< map.length;j++)
                if(map[i][j] == '0'||map[i][j] == '1'||map[i][j] == '2'||map[i][j] == '3'||map[i][j] == '4')
                    return new int[]{i,j};

        return new int[]{-1,-1};
    }

    private Map<Integer,Integer> homeFinder(char[][] map)
    {
        Map<Integer,Integer> homes = new HashMap<>();
        for(int i=0;i< map.length;i++ )
            for(int j=0; j< map.length;j++)
                if(map[i][j] == 'a')
                    homes.put(i,j);

        return homes;
    }

    @Override
    public Action doTurn(TurnData turnData) {


        // find diamond indexes
        int[] indexes  = DiamondFinder(turnData.map);
        Goal_ROW = indexes[0];  Goal_COLUMN= indexes[1];


        //finds diamond path
        if (turnData.turnsLeft == maxTurns)
        {
            long t1 = new Date().getTime();
            find_route(turnData , DIAMOND);
            System.out.println("algo time = " +(new Date().getTime() -t1));
        }



        while (!actions.isEmpty())
            return actions.pop();

        //find the closest home
        Map<Integer,Integer> homes = homeFinder(turnData.map);
        int min=Integer.MAX_VALUE;
        for(Map.Entry<Integer,Integer> entry : homes.entrySet())
            if(Math.abs(turnData.agentData[0].position.row - entry.getKey()) +
                    Math.abs(turnData.agentData[0].position.column - entry.getValue()) < min ) {
                Goal_ROW = entry.getKey();
                Goal_COLUMN=entry.getValue();
            }

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

            System.out.println("["+ node2.row+","+node2.column+"]  hoop:"+node2.hoop +"\n/////////////////////////////");
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

        int hoop=0;

        int grid_size = turnData.map.length;
        AgentData agent = turnData.agentData[0];
        Node first_node = new Node(agent.position.row,agent.position.column);
        first_node.hoop=0;
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
                        expanded_node.distance_to_goal=Math.abs(expanded_node.row-Goal_ROW)+Math.abs(expanded_node.column-Goal_COLUMN);
                        expanded_node.parent = node;
                        expanded_node.hoop=expanded_node.parent.hoop+1;
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
                        expanded_node.distance_to_goal=Math.abs(expanded_node.row-Goal_ROW)+Math.abs(expanded_node.column-Goal_COLUMN);
                        expanded_node.parent = node;
                        expanded_node.hoop=expanded_node.parent.hoop+1;
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
                        expanded_node.distance_to_goal=Math.abs(expanded_node.row-Goal_ROW)+Math.abs(expanded_node.column-Goal_COLUMN);
                        expanded_node.parent = node;
                        expanded_node.hoop=expanded_node.parent.hoop+1;
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
                        expanded_node.distance_to_goal=Math.abs(expanded_node.row-Goal_ROW)+Math.abs(expanded_node.column-Goal_COLUMN);
                        expanded_node.parent = node;
                        expanded_node.hoop=expanded_node.parent.hoop+1;
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

            frontier = sortFrontier(frontier);



        }
        return false;
    }

    private LinkedList<Node> sortFrontier(Queue<Node> frontier) {

        Node[] array;
        array= frontier.toArray(new Node[frontier.size()]);

        for (int i = 0; i < frontier.size()-1; i++)
            for (int j = 0; j < frontier.size()-i-1; j++) {
                if (array[j].distance_to_goal + array[j].hoop >
                        array[j + 1].distance_to_goal + array[j + 1].hoop) {
                    // swap
                    Node temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
                else if(array[j].distance_to_goal + array[j].hoop ==
                        array[j + 1].distance_to_goal + array[j + 1].hoop && array[j].distance_to_goal>array[j+1].distance_to_goal)
                {
                    Node temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }

            }


        //  System.out.println("["+array[0].row+","+array[0].column+"]" + (array[0].distance_to_goal-array[0].hoop));

        return new LinkedList<Node>(Arrays.asList(array));



    }

    public Comparator<Node> nodeComparator()
    {

        return new Comparator<Node>() {
            @Override
            public int compare(Node node1, Node node2) {

                if(Math.abs(node1.row-Goal_ROW)+Math.abs(node1.column-Goal_COLUMN)  + node1.row+node1.column  ==
                        Math.abs(node2.row-Goal_ROW)+Math.abs(node2.column-Goal_COLUMN) + node2.row+node2.column)
                    return 0;

                else if(Math.abs(node1.row-Goal_ROW)+Math.abs(node1.column-Goal_COLUMN)  + node1.row+node1.column  >
                        Math.abs(node2.row-Goal_ROW)+Math.abs(node2.column-Goal_COLUMN) + node2.row+node2.column)
                    return 1;


                return -1;
            }


        };

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