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
    private boolean has_rand_action =false;
    private  int desicion_time_limit;
    private boolean time_out_happend = false;

    private Stack<Action> actions = new Stack<>();
    private Queue<Node> frontier = new LinkedList<>();
    private List<Node> explored_set = new ArrayList<>();

    private boolean is_home_found = false;
    private boolean is_diamond_found = false;

    int mode = DIAMOND;

    public InformedSearchAgent() throws IOException {
        super();
    }

    private Map<Integer,Integer> DiamondFinder(char[][] map) {

        Map<Integer,Integer> diamonds = new HashMap<>();
        for(int i=0;i< map.length;i++ )
            for(int j=0; j< map.length;j++)
                if(map[i][j] == '0'||map[i][j] == '1'||map[i][j] == '2'||map[i][j] == '3'||map[i][j] == '4')
                    diamonds.put(i,j);

        return diamonds;
    }

    private Action make_rand_action(TurnData turnData)
    {
        AgentData agentData = turnData.agentData[0];
        if (agentData.position.column+1 <gridSize)
        {
            if(turnData.map[agentData.position.row][agentData.position.column+1] == '*')
                return Action.RIGHT;
        }
        if (agentData.position.column-1 >=0)
        {
            if(turnData.map[agentData.position.row][agentData.position.column-1] == '*')
                return Action.LEFT;
        }
        if (agentData.position.row +1 <gridSize)
        {
            if(turnData.map[agentData.position.row+1][agentData.position.column] == '*')
                return Action.DOWN;
        }
        if (agentData.position.row-1 >=0)
        {
            if(turnData.map[agentData.position.row-1][agentData.position.column] == '*')
                return Action.UP;
        }

        has_rand_action = true;
        return Action.RIGHT;


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

        time_out_happend = false;
        desicion_time_limit =(int)(decisionTimeLimit * 1000);
        desicion_time_limit -=10;

        if (turnData.turnsLeft == maxTurns)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // find diamond indexes
                    Map<Integer,Integer> diamonds = DiamondFinder(turnData.map);
                    for(Map.Entry<Integer,Integer> entry : diamonds.entrySet()){
                        Goal_ROW = entry.getKey();
                        Goal_COLUMN=entry.getValue();
                    }

                    find_route(turnData, DIAMOND);
                }
            }).start();
        }

        if (mode == DIAMOND)
        {
            if (is_diamond_found)
            {
                if (!has_rand_action)
                {
                    if (!actions.isEmpty()) {
                        return actions.pop();
                    } else
                    {
                        mode = HOME;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Map<Integer,Integer> homes = homeFinder(turnData.map);
                                int min=Integer.MAX_VALUE;
                                for(Map.Entry<Integer,Integer> entry : homes.entrySet())
                                    if(Math.abs(turnData.agentData[0].position.row - entry.getKey()) +
                                            Math.abs(turnData.agentData[0].position.column - entry.getValue()) < min ) {
                                        Goal_ROW = entry.getKey();
                                        Goal_COLUMN=entry.getValue();
                                    }

                                find_route(turnData , HOME);
                            }
                        }).start();
                        return make_rand_action(turnData);
                    }
                }
                else {
                    has_rand_action = false;
                    return Action.LEFT;

                }

            }
            else {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        time_out_happend = true;
                        //System.out.println("time out happend");
                        timer.cancel();
                    }
                }, desicion_time_limit);


                while (!time_out_happend)
                {
                    if (is_diamond_found)
                    {
                        timer.cancel();
                        break;
                    }
                }
                if (is_diamond_found)
                {
                    if (!has_rand_action)
                    {
                        if (!actions.isEmpty()) {
                            return actions.pop();
                        } else
                        {
                            mode = HOME;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    find_route(turnData, HOME);
                                }
                            }).start();
                            return make_rand_action(turnData);
                        }
                    }
                    else {
                        has_rand_action = false;
                        return Action.LEFT;

                    }
                }
                else
                    return make_rand_action(turnData);

            }
        }
        else
        {
            if (is_home_found) {
                if (!has_rand_action) {
                    if (!actions.isEmpty()) {
                        return actions.pop();
                    } else
                        System.out.println("algo finished");
                } else {
                    has_rand_action = false;
                    return Action.LEFT;
                }

            } else {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        time_out_happend = true;
                        //System.out.println("time out happend");
                        timer.cancel();
                    }
                }, desicion_time_limit);


                while (!time_out_happend)
                {
                    if (is_diamond_found)
                    {
                        timer.cancel();
                        break;
                    }
                }
                if (is_diamond_found)
                {
                    if (!has_rand_action) {
                        if (!actions.isEmpty()) {
                            return actions.pop();
                        } else
                            System.out.println("algo finished");
                    } else {
                        has_rand_action = false;
                        return Action.LEFT;
                    }
                }
                else
                    return make_rand_action(turnData);

            }
        }

        return make_rand_action(turnData);













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

    private void fill_actions(Node node)
    {
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
                            fill_actions(expanded_node);
                            if (mode == HOME)
                                is_home_found = true;
                            else
                                is_diamond_found = true;
                            return true;

                        } else {
                            frontier.add(expanded_node);
                            //addToFrontier(e);
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
                            fill_actions(expanded_node );
                            if (mode == HOME)
                                is_home_found = true;
                            else
                                is_diamond_found = true;
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
                            fill_actions(expanded_node );
                            if (mode == HOME)
                                is_home_found = true;
                            else
                                is_diamond_found = true;
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
                            fill_actions(expanded_node );
                            if (mode == HOME)
                                is_home_found = true;
                            else
                                is_diamond_found = true;
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

            frontier = sortFrontier();

            // addFrontier(node);

        }
        return false;
    }

    private LinkedList<Node> sortFrontier() {

        Node[] array;
        array= frontier.toArray(new Node[frontier.size()]);

        Node min = array[0];
        int min_index = 0;
        for (int i=0 ; i<frontier.size();i++)
        {
            if(array[i].distance_to_goal+array[i].hoop < min.distance_to_goal+min.hoop) {
                min = array[i];
                min_index = i;
            }
            else if(array[i].distance_to_goal + array[i].hoop ==
                    min.distance_to_goal+min.hoop && array[i].distance_to_goal<min.distance_to_goal)
            {
                min=array[i];
                min_index=i;
            }
        }

        Node tmp = array[0];
        array[0] = array[min_index];
        array[min_index] = tmp;

        return new LinkedList<Node>(Arrays.asList(array));


//        Node[] array;
//        array= frontier.toArray(new Node[frontier.size()]);
//
//            for (int i = 0; i < frontier.size()-1; i++)
//                for (int j = 0; j < frontier.size()-i-1; j++) {
//                    if (array[j].distance_to_goal + array[j].hoop >
//                            array[j + 1].distance_to_goal + array[j + 1].hoop) {
//                        // swap
//                        Node temp = array[j];
//                        array[j] = array[j + 1];
//                        array[j + 1] = temp;
//                    }
//                    else if(array[j].distance_to_goal + array[j].hoop ==
//                            array[j + 1].distance_to_goal + array[j + 1].hoop && array[j].distance_to_goal>array[j+1].distance_to_goal)
//                    {
//                        Node temp = array[j];
//                        array[j] = array[j + 1];
//                        array[j + 1] = temp;
//                    }
//
//                }
//
//
//      //  System.out.println("["+array[0].row+","+array[0].column+"]" + (array[0].distance_to_goal-array[0].hoop));
//
//        return new LinkedList<Node>(Arrays.asList(array));


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