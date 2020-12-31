package com.ai.project;

import com.ai.project.base.Action;
import com.ai.project.base.BaseAgent;
import com.ai.project.base.TurnData;

import java.io.IOException;
import java.util.*;

public class ExhaustiveSearchAgent extends BaseAgent {

    private int HOME = 1, DIAMOND = 2;

    private Queue<Action> actions = new LinkedList<>();

    private List<Choice> diamind_orders = new ArrayList<>();
    private List<List<Site>> site_orders = new ArrayList<>();

    private List<Site> sites = new ArrayList<>();



    //public List<AbstractMap.SimpleEntry<List<Diamond> , Integer>> diamind_orders = new ArrayList<>();

    boolean is_startup = true;

    int best_choice_index = 0;

    public ExhaustiveSearchAgent() throws IOException {
        super();
    }

    @Override
    public Action doTurn(TurnData turnData) {

        if (turnData.turnsLeft == maxTurns) {
            long algorithm_start_time = new Date().getTime();

            List<Diamond> diamonds = find_diamonds_in_map(turnData);
            sites = find_sites_in_map(turnData);

            fill_diamonds_orders(diamonds.size(), diamonds);

            site_orders = fill_sites_orders(diamonds.size());

            best_choice_index = find_best_order(turnData);
            //System.out.println("site_orders.size = " + site_orders.size());

            /*int indd = 0;
            for(int i = 0 ; i < diamind_orders.get(best_choice_index).diamonds_list.size() ; i++)
            {
                System.out.print(diamind_orders.get(best_choice_index).diamonds_list.get(i).sid);
                System.out.print("--");
                System.out.print(site_orders.get(diamind_orders.get(best_choice_index).chosed_home_order_ind).get(indd).row
                                +"" + site_orders.get(diamind_orders.get(best_choice_index).chosed_home_order_ind).get(indd).column);
                indd++;
                System.out.print("--");

            }
            System.out.println("");*/

            generate_actions(turnData , diamind_orders.get(best_choice_index).diamonds_list);
            //generate_actions2(diamind_orders.get(best_choice_index));


            System.out.println("algorithm time = " + (new Date().getTime() - algorithm_start_time) + " ms");
        }


        if (!actions.isEmpty())
            return actions.poll();

        return Action.DOWN.UP;
    }


    private void generate_actions2(Choice choice) {
        actions = new LinkedList<>();

        for (int i = 0; i < choice.nodes.size(); i++) {
            //System.out.println(choice.nodes.get(i).row  + " " + choice.nodes.get(i).column);
            fill_actions(choice.nodes.get(i));
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
            }
            sortFrontier(frontier);

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
        char[][] map;
        int total_distance;
        int agent_row , agent_column;
        int GOAL_ROW = 0, GOAL_COLUMN = 0;

        for(int i = 0; i < diamind_orders.size() ; i++)
        {
            int keep_score = 0;
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
                total_distance = 0;
                explore_queue = diamind_orders.get(i).diamonds_list;


                for (int j = 0; j < explore_queue.size(); j++) {
                    Node diamond = find_diamond_distance(
                            explore_queue.get(j).sid, turnData.map.length, map, explore_queue.get(j).row, explore_queue.get(j).column
                            , agent_row, agent_column);


                    if (diamond.hoop > 0 && (total_distance + diamond.hoop < turnData.turnsLeft)) {
                        //char c = map[agent_row][agent_column];
                        //map[agent_row][agent_column] = '.';
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

                            //diamind_orders.get(i).nodes.add(diamond);
                            //diamind_orders.get(i).nodes.add(home);

                        } else {
                            if (score >keep_score) {
                                keep_score = score;
                                diamind_orders.get(i).chosed_home_order_ind = k;
                                diamind_orders.get(i).score = score;
                            }
                            break;
                        }
                    } else {
                        if (score >keep_score) {
                            keep_score = score;
                            diamind_orders.get(i).chosed_home_order_ind = k;
                            diamind_orders.get(i).score = score;
                        }
                        break;
                    }
                }
                if (score >keep_score) {
                    keep_score = score;
                    diamind_orders.get(i).chosed_home_order_ind = k;
                    diamind_orders.get(i).score = score;
                }

            }


        }

        int keep = 0;
        int index = 0;
        for (int i = 0; i < diamind_orders.size() ; i++)
        {
            if (diamind_orders.get(i).score > keep)
            {
                keep = diamind_orders.get(i).score;
                index = i;
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
            }

            frontier = sortFrontier(frontier);

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
