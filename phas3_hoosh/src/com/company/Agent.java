package com.company;

import com.company.base.Action;
import com.company.base.BaseAgent;
import com.company.base.TurnData;
import java.io.IOException;
import java.util.*;

public class Agent extends BaseAgent {

    private List<Diamond> current_diamonds = new ArrayList<>();

    private boolean is_ygy_completed = false;
    private int is_task_finished = 0;
    private List<Thread> pool = new ArrayList<>();

    private List<Candidate> yellows = new ArrayList<>();

    private Queue<Action> actions = new LinkedList<>();

    private int my_agent_id = 0;
    private int ygy_strategy_process = 0;

    private int five_diamond_strategy_process = 0;
    private boolean is_going_to_diamond = true;

    private List<Diamond> my_diamonds = new ArrayList<>();


    private Diamond current_goal_daimond = new Diamond();

    public Agent() throws IOException {
        super();
        System.out.println("MY NAME: " + name);
        System.out.println("PLAYER COUNT: " + agentCount);
        System.out.println("GRID SIZE: " + gridSize);
        System.out.println("MAX TURNS: " + maxTurns);
        System.out.println("DECISION TIME LIMIT: " + decisionTimeLimit);
    }

    @Override
    public Action doTurn(TurnData turnData) {


        if (turnData.turnsLeft == maxTurns)
        {
            for (int i = 0 ; i < turnData.agentData.length ; i++)
            {
                if (turnData.agentData[i].name.equals(name))
                {
                    System.out.println( "row: "+turnData.agentData[i].position.row +
                                        "column: "+turnData.agentData[i].position.column);
                    my_agent_id = i;
                    break;
                }
            }

        }

        current_diamonds = find_all_diamonds(turnData);
        List<Diamond> ygy = is_there_ygy(current_diamonds);




        if (actions.isEmpty() && turnData.agentData[my_agent_id].carrying!=null)
        {
            check_for_diamond_proccess(current_goal_daimond);
            if (turnData.agentData[my_agent_id].carrying+48 == Diamond.YELLOW)
            {
                if (ygy_strategy_process ==0)
                    ygy_strategy_process++;
                else if (ygy_strategy_process == 2)
                    is_ygy_completed = true;
            }
            else if ((ygy_strategy_process == 1 ) && (turnData.agentData[my_agent_id].carrying+48 == Diamond.GREEN))
            {
                ygy_strategy_process++;
            }


            int agent_row = turnData.agentData[0].position.row;
            int agent_column = turnData.agentData[0].position.column;

            Integer site_row=0 , site_col=0;
            char[][] map = new char[turnData.map.length][turnData.map.length];
            for (int m = 0; m < gridSize; m++) {
                for (int j = 0; j < gridSize; j++)
                {
                    map[m][j] = turnData.map[m][j];
                    if (map[m][j] == 'a')
                    {
                        site_row = m;
                        site_col = j;
                    }
                }
            }

            find_actions_for('a', map.length, map, site_row, site_col, agent_row, agent_column);
            is_going_to_diamond = false;
        }



        if (!actions.isEmpty())
        {
            return actions.poll();
        }


        if (ygy.size() >0) {

            char target = '0';
            if (ygy_strategy_process == 0 || ygy_strategy_process == 2)
                target = Diamond.YELLOW;
            else if (ygy_strategy_process == 1)
                target = Diamond.GREEN;


            yellows.clear();
            for (Diamond diamond : ygy) {
                if (diamond.sid == target) {
                    yellows.add(new Candidate(diamond, 0));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            find_path_to_yellow_diamond(diamond, yellows.size() - 1, turnData);
                        }
                    }).start();
                    try { Thread.sleep(1); } catch (Exception e) { }
                    /*thread.start();
                    pool.add(thread);*/
                }
            }

            while (is_task_finished < yellows.size()) {
            }

            is_task_finished = 0;
            pool.clear();


            Candidate choosed = new Candidate();
            choosed.cost = 1500;
            System.out.println("size:"+yellows.size());
            for (int i = 0; i < yellows.size(); i++) {
//                System.out.println("ycose: " + yellows.get(i).explored_node.hoop + ", scost: " + choosed.cost);
//                System.out.println("row: " + yellows.get(i).diamond.row + " , col: " + yellows.get(i).diamond.column);
                if (yellows.get(i).cost < choosed.cost) {
                    choosed = yellows.get(i);
                }

            }

            current_goal_daimond = choosed.diamond;

            fill_actions(choosed.explored_node);

            if (actions.size() > 0) {
                System.out.println("actions got");
                return actions.poll();
            }

        }
        else
        {
            //TODO: check five diamonds
        }



       return  Action.UP;
    }




    private void check_for_diamond_proccess(Diamond diamond)
    {
        boolean is_there_is_one = false;
        for (int i = 0 ; i < my_diamonds.size() ; i++)
        {
            if (my_diamonds.get(i).sid == diamond.sid);
            {
                is_there_is_one = true;
            }
        }

        if (!is_there_is_one)
            five_diamond_strategy_process++;

        my_diamonds.add(diamond);
    }


    private void fill_actions(Node node) {
        if (node.parent.parent != null) {
            fill_actions(node.parent);
        }
        actions.add(find_action_to_parent(node));
    }

    private Action find_action_to_parent(Node node) {
        if (node.parent.row == node.row) {
            if (node.parent.column - node.column == -1) {
                //System.out.println("right");
                return Action.RIGHT;
            } else {
                //System.out.println("left");
                return Action.LEFT;
            }
        } else {
            if (node.parent.row - node.row == -1) {
                //System.out.println("down");
                return Action.DOWN;
            } else {
                //System.out.println("up");
                return Action.UP;
            }
        }
    }


    private void find_path_to_yellow_diamond(Diamond diamond , int ind, TurnData turnData)
    {
        Node node = find_diamond_distance(diamond , turnData);
        yellows.get(ind).cost = node.hoop;
        System.out.println(node.hoop);
        yellows.get(ind).explored_node = node;
        is_task_finished ++;
    }

    private  Node find_diamond_distance(Diamond goal , TurnData turnData)
    {

        int agent_row = turnData.agentData[0].position.row;
        int agent_column = turnData.agentData[0].position.column;

        char[][] map = new char[turnData.map.length][turnData.map.length];
        for (int m = 0; m < gridSize; m++) {
            for (int j = 0; j < gridSize; j++)
                map[m][j] = turnData.map[m][j];
        }


        Queue<Node> frontier = new LinkedList<>();
        List<Node> explored_set = new ArrayList<>();

        Node first_node = new Node(agent_row,agent_column);
        first_node.hoop=0;
        frontier.add(first_node);

        while (!frontier.isEmpty()) {

            Node node = frontier.poll();

            if (!is_in_explored_set(explored_set ,node))
            {
                explored_set.add(node);

                Node expanded_node;

                if (node.row + 1 < gridSize) {
                    if (check_can_go(map[node.row + 1][node.column] , goal.sid)) {
                        expanded_node = new Node(node.row + 1, node.column);
                        expanded_node.distance_to_goal=Math.abs(expanded_node.row-goal.row)+Math.abs(expanded_node.column-goal.column);
                        expanded_node.parent = node;
                        expanded_node.hoop=expanded_node.parent.hoop+1;
                        expanded_node.data = map[node.row + 1][node.column];
                        if (expanded_node.data == goal.sid) {

                            if ((node.row+1 == goal.row) && (node.column == goal.column)) {
                                return expanded_node;
                            }

                        } else {
                            frontier.add(expanded_node);
                            //addToFrontier(e);
                        }
                    }
                }
                if (node.row - 1 >= 0) {
                    if (check_can_go(map[node.row - 1][node.column],goal.sid)) {
                        expanded_node = new Node(node.row - 1, node.column);
                        expanded_node.distance_to_goal=Math.abs(expanded_node.row-goal.row)+Math.abs(expanded_node.column-goal.column);
                        expanded_node.parent = node;
                        expanded_node.hoop=expanded_node.parent.hoop+1;
                        expanded_node.data = map[node.row - 1][node.column];
                        if (expanded_node.data == goal.sid) {
                            if ((node.row-1 == goal.row) && (node.column == goal.column)) {
                                return expanded_node;
                            }
                        } else {
                            frontier.add(expanded_node);
                        }
                    }
                }
                if (node.column + 1 < gridSize) {
                    if (check_can_go(map[node.row][node.column + 1],goal.sid)) {
                        expanded_node = new Node(node.row, node.column + 1);
                        expanded_node.distance_to_goal=Math.abs(expanded_node.row-goal.row)+Math.abs(expanded_node.column-goal.column);
                        expanded_node.parent = node;
                        expanded_node.hoop=expanded_node.parent.hoop+1;
                        expanded_node.data = map[node.row][node.column + 1];
                        if (expanded_node.data == goal.sid) {
                            if ((node.row == goal.row) && (node.column+1 == goal.column)) {
                                return expanded_node;
                            }
                        } else {
                            frontier.add(expanded_node);
                        }
                    }
                }
                if (node.column - 1 >= 0) {
                    if (check_can_go(map[node.row][node.column - 1],goal.sid)) {
                        expanded_node = new Node(node.row, node.column - 1);
                        expanded_node.distance_to_goal=Math.abs(expanded_node.row-goal.row)+Math.abs(expanded_node.column-goal.column);
                        expanded_node.parent = node;
                        expanded_node.hoop=expanded_node.parent.hoop+1;
                        expanded_node.data = map[node.row][node.column - 1];
                        if (expanded_node.data == goal.sid) {
                            if ((node.row == goal.row) && (node.column-1 == goal.column)) {
                                return expanded_node;
                            }
                        } else {
                            frontier.add(expanded_node);
                        }
                    }
                }
                if (frontier.size() >0)
                    frontier = sortFrontier(frontier);
            }



        }


        return new Node();
    }


    private LinkedList<Node> sortFrontier(Queue<Node> frontier) {

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
    }


    private boolean is_in_explored_set(List<Node> explored_set , Node node)
    {
        for (Node node1 : explored_set)
        {
            if (node1.equals(node))
                return true;
        }
        return false;

    }


    //TODO: complete check_can_go
    private boolean check_can_go(char data , char goal)
    {
        if (goal == 'a')
        {
            return data != '*';
        }
        else
        {
            if (data == goal)
                return true;
            else
            {
                return  data != '*' &&
                        data != '0' &&
                        data != '1' &&
                        data != '2' &&
                        data != '3' &&
                        data != '4';
            }
        }
    }



    private boolean find_actions_for(char goal, int grid_size, char[][] map, int row,
                                     int column, int agent_row, int agent_column) {

        Queue<Node> frontier = new LinkedList<>();
        List<Node> explored_set = new ArrayList<>();


        Node first_node = new Node(agent_row, agent_column);
        first_node.hoop = 0;
        frontier.add(first_node);

        while (!frontier.isEmpty()) {
            Node node = frontier.poll();

            //changed algorithm here
            if (!is_in_explored_set(explored_set, node)) {
                explored_set.add(node);

                Node expanded_node;

                if (node.row + 1 < grid_size) {
                    if (check_can_go(map[node.row + 1][node.column],goal)) {
                        expanded_node = new Node(node.row + 1, node.column);
                        expanded_node.distance_to_goal = Math.abs(expanded_node.row - row) + Math.abs(expanded_node.column - column);
                        expanded_node.parent = node;
                        expanded_node.hoop = expanded_node.parent.hoop + 1;
                        expanded_node.data = map[node.row + 1][node.column];
                        if (expanded_node.data == goal) {
                            if ((node.row+1 == row) && (node.column == column)) {
                                fill_actions(expanded_node);
                                return true;
                            }

                        } else {
                            frontier.add(expanded_node);
                            //addToFrontier(e);
                        }
                    }
                }
                if (node.row - 1 >= 0) {
                    if (check_can_go(map[node.row - 1][node.column],goal)) {
                        expanded_node = new Node(node.row - 1, node.column);
                        expanded_node.distance_to_goal = Math.abs(expanded_node.row - row) + Math.abs(expanded_node.column - column);
                        expanded_node.parent = node;
                        expanded_node.hoop = expanded_node.parent.hoop + 1;
                        expanded_node.data = map[node.row - 1][node.column];
                        if (expanded_node.data == goal) {

                            if ((node.row-1 == row) && (node.column == column)) {
                                fill_actions(expanded_node);
                                return true;
                            }
                        } else {
                            frontier.add(expanded_node);
                        }
                    }
                }
                if (node.column + 1 < grid_size) {
                    if (check_can_go(map[node.row][node.column + 1],goal)) {
                        expanded_node = new Node(node.row, node.column + 1);
                        expanded_node.distance_to_goal = Math.abs(expanded_node.row - row) + Math.abs(expanded_node.column - column);
                        expanded_node.parent = node;
                        expanded_node.hoop = expanded_node.parent.hoop + 1;
                        expanded_node.data = map[node.row][node.column + 1];
                        if (expanded_node.data == goal) {

                            if ((node.row == row) && (node.column +1 == column)) {
                                fill_actions(expanded_node);
                                return true;
                            }
                        } else {
                            frontier.add(expanded_node);
                        }
                    }
                }
                if (node.column - 1 >= 0) {
                    if (check_can_go(map[node.row][node.column - 1],goal)) {
                        expanded_node = new Node(node.row, node.column - 1);
                        expanded_node.distance_to_goal = Math.abs(expanded_node.row - row) + Math.abs(expanded_node.column - column);
                        expanded_node.parent = node;
                        expanded_node.hoop = expanded_node.parent.hoop + 1;
                        expanded_node.data = map[node.row][node.column - 1];
                        if (expanded_node.data == goal) {

                            if ((node.row == row) && (node.column -1 == column)) {
                                fill_actions(expanded_node);
                                return true;
                            }
                        } else {
                            frontier.add(expanded_node);
                        }
                    }
                }
                if (frontier.size()>0)
                    frontier = sortFrontier(frontier);
            }


        }

        return false;
    }

    private List<Diamond> find_all_diamonds(TurnData turnData)
    {
        List<Diamond> diamonds = new ArrayList<>();
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (    turnData.map[i][j] == Diamond.GREEN ||
                        turnData.map[i][j] == Diamond.YELLOW||
                        turnData.map[i][j] == Diamond.BLUE  ||
                        turnData.map[i][j] == Diamond.RED   ||
                        turnData.map[i][j] == Diamond.GRAY
                    )
                {
                    Diamond diamond = new Diamond(diamonds.size() , turnData.map[i][j],i,j);
                    diamonds.add(diamond);
                }
            }
        }
        return diamonds;
    }


    private List<Diamond> is_there_ygy(List<Diamond> diamonds)
    {
        List<Diamond> ygy = new ArrayList<>();
        int y_count = 0;
        int g_count = 0;

        for (int i = 0; i < diamonds.size(); i++) {
                if (diamonds.get(i).sid == Diamond.YELLOW) {
                    y_count ++;
                    Diamond diamond = new Diamond(ygy.size() , Diamond.YELLOW , diamonds.get(i).row, diamonds.get(i).column);
                    ygy.add(diamond);
                }
                else if (diamonds.get(i).sid == Diamond.GREEN)
                {
                    g_count ++;
                    Diamond diamond = new Diamond(ygy.size() , Diamond.GREEN , diamonds.get(i).row, diamonds.get(i).column);
                    ygy.add(diamond);
                }
        }
        if (ygy_strategy_process ==0) {
            if (y_count >= 2 && g_count >= 1)
                return ygy;
        }
        else if (ygy_strategy_process ==1)
        {
            if (y_count >= 1 && g_count >= 1)
                return ygy;
        }
        else if (ygy_strategy_process ==2)
        {
            if (y_count >= 1)
                return ygy;
        }

        return  new ArrayList<>();


    }


    public static void main(String[] args) {
        try {
            String winner = new Agent().play();
            System.out.println("WINNER: " + winner);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
