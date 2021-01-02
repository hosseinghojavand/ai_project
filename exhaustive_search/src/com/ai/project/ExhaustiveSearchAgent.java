package com.ai.project;

import com.ai.project.base.Action;
import com.ai.project.base.AgentData;
import com.ai.project.base.BaseAgent;
import com.ai.project.base.TurnData;

import javax.swing.*;
import java.io.IOException;
import java.util.*;
import java.util.Timer;

public class ExhaustiveSearchAgent extends BaseAgent {

    private int HOME = 1, DIAMOND = 2;

    private Queue<Action> actions = new LinkedList<>();

    private List<Choice> diamind_orders = new ArrayList<>();
    private List<List<Site>> site_orders = new ArrayList<>();

    private List<Site> sites = new ArrayList<>();

    int best_choice_index = 0;
    private boolean time_out_happend = false;
    boolean is_algorithm_finished = false;
    private  int desicion_time_limit;
    private boolean has_rand_action =false;

    public ExhaustiveSearchAgent() throws IOException {
        super();
    }

    @Override
    public Action doTurn(TurnData turnData) {

        time_out_happend = false;
        desicion_time_limit =(int)(decisionTimeLimit * 1000);
        desicion_time_limit -=10;

        if (turnData.turnsLeft == maxTurns) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    System.out.println("grid size = " + gridSize);
                    long algorithm_start_time = new Date().getTime();

                    List<Diamond> diamonds = find_diamonds_in_map(turnData);
                    sites = find_sites_in_map(turnData);

                    fill_diamonds_orders(diamonds.size(), diamonds);

                    site_orders = fill_sites_orders(diamonds.size());

                    best_choice_index = find_best_order(turnData);

                    generate_actions(turnData , diamind_orders.get(best_choice_index).diamonds_list);
                    is_algorithm_finished = true;
                    System.out.println("algorithm time = " + (new Date().getTime() - algorithm_start_time) + " ms");

                    System.out.println("score= " + diamind_orders.get(best_choice_index).score);
                }
            }).start();

        }

        if (is_algorithm_finished)
        {
            if (!has_rand_action)
            {
                if (!actions.isEmpty())
                    return actions.poll();
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
                    timer.cancel();
                }
            }, desicion_time_limit);


            while (!time_out_happend)
            {
                System.out.print("");
                if (is_algorithm_finished)
                {
                    timer.cancel();
                    break;
                }
            }
            if (is_algorithm_finished)
            {
                if (!has_rand_action)
                {
                    if (!actions.isEmpty())
                        return actions.poll();
                }
                else {
                    has_rand_action = false;
                    return Action.LEFT;

                }
            }
            else
                return make_rand_action(turnData);

        }


        return make_rand_action(turnData);
    }


    private Action make_rand_action(TurnData turnData)
    {

        if (has_rand_action)
        {
            has_rand_action = false;
            return Action.LEFT;
        }
        else {
            AgentData agentData = turnData.agentData[0];
            if (agentData.position.column + 1 < gridSize) {
                if (turnData.map[agentData.position.row][agentData.position.column + 1] == '*')
                    return Action.RIGHT;
            }
            else
            {
                return Action.RIGHT;
            }
            if (agentData.position.column - 1 >= 0) {
                if (turnData.map[agentData.position.row][agentData.position.column - 1] == '*')
                    return Action.LEFT;
            }
            else
            {
                return Action.LEFT;
            }
            if (agentData.position.row + 1 < gridSize) {
                if (turnData.map[agentData.position.row + 1][agentData.position.column] == '*')
                    return Action.DOWN;
            }
            else
            {
                return Action.DOWN;
            }
            if (agentData.position.row - 1 >= 0) {
                if (turnData.map[agentData.position.row - 1][agentData.position.column] == '*')
                    return Action.UP;
            }
            else
            {
                return Action.UP;
            }

            has_rand_action = true;
            return Action.RIGHT;
        }


    }



    private void generate_actions(TurnData turnData, List<Diamond> diamonds) {
        int agent_row = turnData.agentData[0].position.row;
        int agent_column = turnData.agentData[0].position.column;
        int GOAL_ROW = 0, GOAL_COLUMN = 0;

        char[][] map = new char[turnData.map.length][turnData.map.length];
        for (int m = 0; m < gridSize; m++) {
            for (int j = 0; j < gridSize; j++)
                map[m][j] = turnData.map[m][j];
        }


        int ind = 0;

        for (int i = 0; i < diamonds.size(); i++) {

            //print_map(map , map.length);
            if (find_actions_for(diamonds.get(i).sid, map.length, map, diamonds.get(i).row, diamonds.get(i).column,
                    agent_row, agent_column)) {
                map[diamonds.get(i).row][diamonds.get(i).column] = '.';
                agent_row = diamonds.get(i).row;
                agent_column = diamonds.get(i).column;

                //print_map(map , map.length);

                GOAL_ROW = site_orders.get(diamind_orders.get(best_choice_index).chosed_home_order_ind).get(ind).row;
                GOAL_COLUMN = site_orders.get(diamind_orders.get(best_choice_index).chosed_home_order_ind).get(ind).column;
                ind++;
                if (find_actions_for('a', map.length, map, GOAL_ROW, GOAL_COLUMN,
                        agent_row, agent_column)) {
                    agent_row = GOAL_ROW;
                    agent_column = GOAL_COLUMN;
                }

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


    private int get_choice_score(Choice choice, TurnData turnData) {
        int score = 0;
        int total_distance = 0;
        int agent_row = turnData.agentData[0].position.row;
        int agent_column = turnData.agentData[0].position.column;
        int GOAL_ROW = 0, GOAL_COLUMN = 0;

        char[][] map = new char[turnData.map.length][turnData.map.length];
        for (int m = 0; m < gridSize; m++) {
            for (int j = 0; j < gridSize; j++)
                map[m][j] = turnData.map[m][j];
        }


        for (int j = 0; j < choice.diamonds_list.size(); j++) {
            Node diamond = find_diamond_distance(
                    choice.diamonds_list.get(j).sid, turnData.map.length, map, choice.diamonds_list.get(j).row, choice.diamonds_list.get(j).column
                    , agent_row, agent_column);


            if (diamond.hoop > 0 && (total_distance + diamond.hoop < turnData.turnsLeft)) {
                map[choice.diamonds_list.get(j).row][choice.diamonds_list.get(j).column] = '.';
                agent_row = choice.diamonds_list.get(j).row;
                agent_column = choice.diamonds_list.get(j).column;

                Map<Integer, Integer> homes = homeFinder(map);
                int min = Integer.MAX_VALUE;
                for (Map.Entry<Integer, Integer> entry : homes.entrySet()) {
                    if (Math.abs(agent_row - entry.getKey()) +
                            Math.abs(agent_column - entry.getValue()) < min) {
                        GOAL_ROW = entry.getKey();
                        GOAL_COLUMN = entry.getValue();
                    }
                }

                Node home = find_diamond_distance('a', turnData.map.length, map, GOAL_ROW, GOAL_COLUMN, agent_row, agent_column);

                if (home.hoop > 0 && (total_distance + diamond.hoop + home.hoop <= turnData.turnsLeft)) {
                    agent_row = GOAL_ROW;
                    agent_column = GOAL_COLUMN;
                    score += choice.diamonds_list.get(j).value;
                    total_distance += (diamond.hoop + home.hoop);

                } else {
                    return score;
                }
            } else {
                return score;
            }
        }
        return score;
    }


    private int find_best_order(TurnData turnData) {

        List<Diamond> explore_queue;
        int score ;
        int turns_left;
        char[][] map;
        int total_distance;
        int agent_row , agent_column;
        int GOAL_ROW = 0, GOAL_COLUMN = 0;

        for(int i = 0; i < diamind_orders.size() ; i++)
        {
            int keep_score = 0;
            int keep_turns_left = 0;
            for(int k = 0 ; k < site_orders.size() ; k++) {
                int ind = 0;
                agent_row = turnData.agentData[0].position.row;
                agent_column = turnData.agentData[0].position.column;

                map = new char[turnData.map.length][turnData.map.length];
                for (int m = 0; m < gridSize; m++) {
                    for (int j = 0; j < gridSize; j++)
                        map[m][j] = turnData.map[m][j];
                }

                score = 0;
                turns_left = 0;
                total_distance = 0;
                explore_queue = diamind_orders.get(i).diamonds_list;


                for (int j = 0; j < explore_queue.size(); j++) {
                    Node diamond = find_diamond_distance(
                            explore_queue.get(j).sid, turnData.map.length, map, explore_queue.get(j).row, explore_queue.get(j).column
                            , agent_row, agent_column);


                    if (diamond.hoop > 0 && (total_distance + diamond.hoop < turnData.turnsLeft)) {
                        map[explore_queue.get(j).row][explore_queue.get(j).column] = '.';
                        agent_row = explore_queue.get(j).row;
                        agent_column = explore_queue.get(j).column;

                        GOAL_ROW = site_orders.get(k).get(ind).row;
                        GOAL_COLUMN = site_orders.get(k).get(ind).column;
                        ind++;

                        Node home = find_diamond_distance('a', turnData.map.length, map, GOAL_ROW, GOAL_COLUMN, agent_row, agent_column);

                        if (home.hoop > 0 && (total_distance + diamond.hoop + home.hoop <= turnData.turnsLeft)) {
                            agent_row = GOAL_ROW;
                            agent_column = GOAL_COLUMN;
                            score += explore_queue.get(j).value;
                            total_distance += (diamond.hoop + home.hoop);
                            turns_left = maxTurns - total_distance;

                        } else {
                            if (score >keep_score) {
                                keep_score = score;
                                keep_turns_left = turns_left;
                                diamind_orders.get(i).chosed_home_order_ind = k;
                                diamind_orders.get(i).score = score;
                                diamind_orders.get(i).turns_left = turns_left;

                            }
                            else if (score == keep_score)
                            {
                                if (turns_left > keep_turns_left)
                                {
                                    keep_score = score;
                                    keep_turns_left = turns_left;
                                    diamind_orders.get(i).chosed_home_order_ind = k;
                                    diamind_orders.get(i).score = score;
                                    diamind_orders.get(i).turns_left = turns_left;
                                }
                            }
                            break;
                        }
                    } else {
                        if (score >keep_score) {
                            keep_score = score;
                            keep_turns_left = turns_left;
                            diamind_orders.get(i).chosed_home_order_ind = k;
                            diamind_orders.get(i).score = score;
                            diamind_orders.get(i).turns_left = turns_left;
                        }
                        else if (score == keep_score)
                        {
                            if (turns_left > keep_turns_left)
                            {
                                keep_score = score;
                                keep_turns_left = turns_left;
                                diamind_orders.get(i).chosed_home_order_ind = k;
                                diamind_orders.get(i).score = score;
                                diamind_orders.get(i).turns_left = turns_left;
                            }
                        }
                        break;
                    }
                }
                if (score >keep_score) {
                    keep_score = score;
                    keep_turns_left = turns_left;
                    diamind_orders.get(i).chosed_home_order_ind = k;
                    diamind_orders.get(i).score = score;
                    diamind_orders.get(i).turns_left = turns_left;
                }
                else if (score == keep_score)
                {
                    if (turns_left > keep_turns_left)
                    {
                        keep_score = score;
                        keep_turns_left = turns_left;
                        diamind_orders.get(i).chosed_home_order_ind = k;
                        diamind_orders.get(i).score = score;
                        diamind_orders.get(i).turns_left = turns_left;
                    }
                }

            }


        }

        int keep = 0;
        int keep_turn = 0;
        int index = 0;
        for (int i = 0; i < diamind_orders.size() ; i++)
        {
            if (diamind_orders.get(i).score > keep)
            {
                keep = diamind_orders.get(i).score;
                keep_turn = diamind_orders.get(i).turns_left;
                index = i;
            }
            else if (diamind_orders.get(i).score == keep)
            {
                if (diamind_orders.get(i).turns_left>keep_turn)
                {
                    keep = diamind_orders.get(i).score;
                    keep_turn = diamind_orders.get(i).turns_left;
                    index = i;
                }
            }
        }


        return index;

    }

    void print_map(char[][] map,int  size)
    {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++)
                System.out.print(map[i][j]);
            System.out.println();
        }

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

    private  Node find_diamond_distance(char goal , int grid_size , char[][] map , int row , int column , int agent_row , int agent_column)
    {
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

                if (node.row + 1 < grid_size) {
                    if (check_can_go(map[node.row + 1][node.column] , goal)) {
                        expanded_node = new Node(node.row + 1, node.column);
                        expanded_node.distance_to_goal=Math.abs(expanded_node.row-row)+Math.abs(expanded_node.column-column);
                        expanded_node.parent = node;
                        expanded_node.hoop=expanded_node.parent.hoop+1;
                        expanded_node.data = map[node.row + 1][node.column];
                        if (expanded_node.data == goal) {

                            if ((node.row+1 == row) && (node.column == column)) {
                                return expanded_node;
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
                        expanded_node.distance_to_goal=Math.abs(expanded_node.row-row)+Math.abs(expanded_node.column-column);
                        expanded_node.parent = node;
                        expanded_node.hoop=expanded_node.parent.hoop+1;
                        expanded_node.data = map[node.row - 1][node.column];
                        if (expanded_node.data == goal) {
                            if ((node.row-1 == row) && (node.column == column)) {
                                return expanded_node;
                            }
                        } else {
                            frontier.add(expanded_node);
                        }
                    }
                }
                if (node.column + 1 < grid_size) {
                    if (check_can_go(map[node.row][node.column + 1],goal)) {
                        expanded_node = new Node(node.row, node.column + 1);
                        expanded_node.distance_to_goal=Math.abs(expanded_node.row-row)+Math.abs(expanded_node.column-column);
                        expanded_node.parent = node;
                        expanded_node.hoop=expanded_node.parent.hoop+1;
                        expanded_node.data = map[node.row][node.column + 1];
                        if (expanded_node.data == goal) {
                            if ((node.row == row) && (node.column+1 == column)) {
                                return expanded_node;
                            }
                        } else {
                            frontier.add(expanded_node);
                        }
                    }
                }
                if (node.column - 1 >= 0) {
                    if (check_can_go(map[node.row][node.column - 1],goal)) {
                        expanded_node = new Node(node.row, node.column - 1);
                        expanded_node.distance_to_goal=Math.abs(expanded_node.row-row)+Math.abs(expanded_node.column-column);
                        expanded_node.parent = node;
                        expanded_node.hoop=expanded_node.parent.hoop+1;
                        expanded_node.data = map[node.row][node.column - 1];
                        if (expanded_node.data == goal) {
                            if ((node.row == row) && (node.column-1 == column)) {
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



    private List<Diamond> find_diamonds_in_map(TurnData turnData) {
        List<Diamond> diamonds = new ArrayList<>();

        for (int i = 0; i < gridSize; i++)
            for (int j = 0; j < gridSize; j++) {
                if (turnData.map[i][j] == '0' ||
                        turnData.map[i][j] == '1' ||
                        turnData.map[i][j] == '2' ||
                        turnData.map[i][j] == '3' ||
                        turnData.map[i][j] == '4')
                {
                    Diamond diamond = new Diamond(diamonds.size(), turnData.map[i][j], i, j);
                    diamond.generate_value();
                    diamonds.add(diamond);
                }
            }

        return diamonds;
    }

    private List<Site> find_sites_in_map(TurnData turnData) {
        List<Site> sites = new ArrayList<>();

        for (int i = 0; i < gridSize; i++)
            for (int j = 0; j < gridSize; j++) {
                if(turnData.map[i][j] == 'a')
                {
                    Site site = new Site(site_orders.size(),i ,j);
                    sites.add(site);
                }
            }

        return sites;
    }



    private List<List<Site>> fill_sites_orders(int n) {
        List<List<Site>> ll = new ArrayList<>();
        List<List<Site>> result = new ArrayList<>();
        List<Site> temp = new ArrayList<>();

        if (n==1) {
            for (int i = 0; i < sites.size(); i++) {
                temp = new ArrayList<>();
                temp.add(sites.get(i));
                ll.add(new ArrayList<>(new ArrayList<>(temp)));
            }
            return ll;
        }
        else
        {
            result = fill_sites_orders(n-1);
            for(int i =0; i < sites.size() ; i++)
            {
                for(int j =0; j < result.size() ; j++)
                {
                    temp = new ArrayList<>();
                    temp.add(sites.get(i));
                    temp.addAll(result.get(j));
                    ll.add(new ArrayList<>(temp));
                }
            }
            return new ArrayList<>(ll);
        }
    }

    private void fill_diamonds_orders(int n , List<Diamond> diamonds) {
        if(n == 1)
        {
            List<Diamond> cpy= new ArrayList<>();
            for (int i =0 ; i <diamonds.size() ; i++ )
                cpy.add(diamonds.get(i));

            diamind_orders.add(new Choice(cpy , 0));
        }
        else {
            for(int i = 0; i < n-1; i++) {
                fill_diamonds_orders(n - 1, diamonds);
                if(n % 2 == 0) {
                    swap(diamonds, i, n-1);
                } else {
                    swap(diamonds, 0, n-1);
                }
            }
            fill_diamonds_orders(n - 1, diamonds);
        }
    }

    private static void swap(List<Diamond> input, int a, int b) {
        Diamond tmp = input.get(a);
        input.set(a , input.get(b));
        input.set(b ,tmp);
    }


    private static void swap_sites(List<Site> input, int a, int b) {
        Site tmp = input.get(a);
        input.set(a , input.get(b));
        input.set(b ,tmp);
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

}
