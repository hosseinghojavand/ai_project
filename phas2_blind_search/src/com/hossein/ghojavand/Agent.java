package com.hossein.ghojavand;

import com.hossein.ghojavand.base.Action;
import com.hossein.ghojavand.base.AgentData;
import com.hossein.ghojavand.base.BaseAgent;
import com.hossein.ghojavand.base.TurnData;

import java.io.IOException;
import java.sql.Time;
import java.util.*;

public class Agent extends BaseAgent {

    private int HOME = 1 , DIAMOND = 2;


    private Stack<Action> actions = new Stack<>();
    private Queue<Node> frontier = new LinkedList<>();
    private List<Node> explored_set = new ArrayList<>();

    private boolean is_home_found = false;

    private long  algo_start_time;
    private  int desicion_time_limit;

    private boolean is_algo_completed =false;
    private boolean time_out_happend = false;

    private boolean is_data_reset = false;

    private boolean has_rand_action =false;


    private String action = "";

    boolean is_diamond_found = false;
    int mode = DIAMOND;

    public Agent() throws IOException {
        super();
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
                    find_route(turnData, DIAMOND);
                }
            }).start();
        }


        if (mode == DIAMOND) {
            if (is_diamond_found) {
                if (!has_rand_action) {
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
                        System.out.println("time out happend");
                        timer.cancel();
                    }
                }, desicion_time_limit);

                while (!time_out_happend) {
                    System.out.print("");
                }
                return make_rand_action(turnData);

            }
        }
        else
        {
            if (is_home_found) {
                if (has_rand_action) {
                    has_rand_action = false;
                    return Action.LEFT;
                } else {
                    if (!actions.isEmpty()) {
                        return actions.pop();
                    } else
                        System.out.println("nang bar to bad");
                }

            } else {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        time_out_happend = true;
                        System.out.println("time out happend");
                        timer.cancel();
                    }
                }, desicion_time_limit);

                while (!time_out_happend) {
                    System.out.print("");
                }
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



        actions = new Stack<>();
        frontier = new LinkedList<>();
        explored_set = new ArrayList<>();


        int grid_size = turnData.map.length;
        if (frontier.isEmpty()) {
            AgentData agent = turnData.agentData[0];
            Node first_node = new Node(agent.position.row, agent.position.column);
            frontier.add(first_node);
        }

        while (!frontier.isEmpty()) {

            /*if (new Date().getTime() - algo_start_time >= desicion_time_limit - 15)
            {
                time_out_happend = true;
                return false;
            }*/

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
                            fill_actions(expanded_node);

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
                if (node.row - 1 >= 0) {
                    if (turnData.map[node.row - 1][node.column] != '*') {
                        expanded_node = new Node(node.row - 1, node.column);
                        expanded_node.parent = node;
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
                        expanded_node.parent = node;
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
                        expanded_node.parent = node;
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
        }
        return false;
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
            if(turnData.map[agentData.position.row-1][agentData.position.column-1] == '*')
                return Action.UP;
        }

        has_rand_action = true;
        return Action.RIGHT;


    }

    private boolean is_goal(Node node , int mode)
    {
        if (mode == DIAMOND)
            return node.data == '0' ||
                    node.data == '1' ||
                    node.data == '2' ||
                    node.data == '3' ||
                    node.data == '4';

        return node.data == 'a';

    }


}
