package com.company;

import com.company.base.Action;
import com.company.base.BaseAgent;
import com.company.base.TurnData;
import java.io.IOException;
import java.util.*;

public class Agent extends BaseAgent {

    private List<Diamond> all_diamonds = new ArrayList<>();

    private boolean is_ygy_completed = false;
    private int is_task_finished = 0;
    private List<Thread> pool = new ArrayList<>();


    private Queue<Action> actions = new LinkedList<>();

    private int my_agent_id = 0;
    private int ygy_strategy_process = 0;

    private int five_diamond_strategy_process = 0;
    private boolean is_going_to_diamond = true;

    private List<Diamond> my_diamonds = new ArrayList<>();


    private Diamond current_goal_diamond = new Diamond();


    private Map<Character , Integer> required = new HashMap<>();



    public Agent() throws IOException {
        super();


        System.out.println("MY NAME: " + (char)(name.charAt(0)));
        System.out.println("MY HOME: " + (char)(name.charAt(0)+32));
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
                    my_agent_id = i;
                    break;
                }
            }

            for (int i = 0 ; i < turnData.agentData[my_agent_id].countRequired.length ; i++)
            {
                required.put((char)(i+48),turnData.agentData[my_agent_id].countRequired[i]);
                System.out.println("req " + i +": " + turnData.agentData[my_agent_id].countRequired[i] );
            }

        }

        all_diamonds = find_all_diamonds(turnData);
        List<Diamond> ygy = is_there_ygy(all_diamonds);


        if (actions.isEmpty() && turnData.agentData[my_agent_id].carrying!=null)
        {
            check_for_diamond_proccess(current_goal_diamond);

            //check ygy process
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


            int agent_row = turnData.agentData[my_agent_id].position.row;
            int agent_column = turnData.agentData[my_agent_id].position.column;

            Integer site_row=0 , site_col=0;
            char[][] map = new char[turnData.map.length][turnData.map.length];
            for (int m = 0; m < gridSize; m++) {
                for (int j = 0; j < gridSize; j++)
                {
                    map[m][j] = turnData.map[m][j];
                    if (map[m][j] == (name.charAt(0)+32))
                    {
                        site_row = m;
                        site_col = j;
                    }
                }
            }

            find_actions_for((char)(name.charAt(0)+32), map.length, map, site_row, site_col, agent_row, agent_column);
            is_going_to_diamond = false;
        }

        if (actions.isEmpty() && turnData.agentData[my_agent_id].carrying==null)
            sync_diamonds(turnData);



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

            long algorithm_start_time = new Date().getTime();

            List<Candidate> req_diamonds = new ArrayList<>();
            for (Diamond diamond : ygy) {
                if (diamond.sid == target) {
                    req_diamonds.add(new Candidate(diamond, 0));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            find_path_to_diamond(false , 0 , diamond, req_diamonds.size() - 1, turnData , req_diamonds , my_agent_id);
                        }
                    }).start();
                    try { Thread.sleep(1); } catch (Exception ignored) { }
                }
            }

            while (is_task_finished < req_diamonds.size()) {
            }

            is_task_finished = 0;


            Candidate choosed = new Candidate();
            choosed.cost = 1500;


            for (Candidate candidate : req_diamonds)
                if (candidate.cost < choosed.cost)
                    choosed = candidate;

            current_goal_diamond = choosed.diamond;

            fill_actions(choosed.explored_node);

            System.out.println("algorithm time = " + (new Date().getTime() - algorithm_start_time) + " ms");

            if (actions.size() > 0) {
                System.out.println("actions got");
                return actions.poll();
            }

        }
        else
        {
            List<Character> choose_req = new ArrayList<>();
            List<Character> others = new ArrayList<>();
            for (Map.Entry<Character,Integer> entry : required.entrySet())
            {
                if (entry.getValue() ==1 || entry.getValue() ==2)
                    choose_req.add(entry.getKey());
                else if (entry.getValue() >2)
                    others.add(entry.getKey());
            }

            if (choose_req.size() ==0)
                choose_req = others;

            long algorithm_start_time = new Date().getTime();

            if (choose_req.size()>0) {

                List<Candidate> req_diamonds = new ArrayList<>();

                for (Character character : choose_req) {
                    for (Diamond diamond : all_diamonds) {
                        if (character == diamond.sid) {
                            req_diamonds.add(new Candidate(diamond, 0));
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    find_path_to_diamond(false , 0 , diamond, req_diamonds.size() - 1
                                            , turnData , req_diamonds , my_agent_id);
                                }
                            }).start();

                            try {
                                Thread.sleep(1);
                            } catch (Exception ignored) {
                            }

                            for (int i = 0 ; i < agentCount ; i++)
                            {
                                if (!turnData.agentData[i].name.equals(name))
                                {
                                    req_diamonds.get(req_diamonds.size()-1).enemy.add(new Candidate(diamond, 0 , i));
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            find_path_to_diamond(true,req_diamonds.get(req_diamonds.size()-1).enemy.size()-1
                                                    , diamond, req_diamonds.size() - 1, turnData , req_diamonds , my_agent_id);
                                        }
                                    }).start();

                                    try {
                                        Thread.sleep(1);
                                    } catch (Exception ignored) {
                                    }
                                }
                            }
                        }
                    }
                }


                while (is_task_finished < (req_diamonds.size() *agentCount)) {}


                is_task_finished = 0;


                Candidate choosed = new Candidate();
                Candidate shortest = new Candidate();
                choosed.cost = Integer.MAX_VALUE;


                for (Candidate candidate : req_diamonds)
                {
                    if (candidate.cost < choosed.cost)
                    {
                        shortest = candidate;
                        int validator = 0 ;
                        for(Candidate enemy: candidate.enemy)
                        {
                            if (candidate.cost <=enemy.cost-1)
                                validator++;
                        }
                        if (validator == candidate.enemy.size())
                            choosed = candidate;

                    }
                    else if(candidate.cost == choosed.cost)
                    {
                        if (candidate.diamond.get_value() > choosed.diamond.value)
                        {
                            shortest = candidate;
                            int validator = 0 ;
                            for(Candidate enemy: candidate.enemy)
                            {
                                if (candidate.cost <=enemy.cost-1)
                                    validator++;
                            }
                            if (validator == candidate.enemy.size())
                                choosed = candidate;
                        }
                    }
                }

                if (choosed.cost == Integer.MAX_VALUE) {
                    current_goal_diamond = shortest.diamond;
                    fill_actions(shortest.explored_node);
                }
                else
                {
                    current_goal_diamond = choosed.diamond;
                    fill_actions(choosed.explored_node);
                }

                System.out.println("algorithm time = " + (new Date().getTime() - algorithm_start_time) + " ms");


                if (actions.size() > 0) {
                    System.out.println("actions got");
                    return actions.poll();
                }
            }
        }



       return  Action.UP;
    }



    private void sync_diamonds(TurnData turnData)
    {
        required.clear();
        for (int i = 0 ; i < turnData.agentData[my_agent_id].countRequired.length ; i++)
            required.put((char)(i+48),turnData.agentData[my_agent_id].countRequired[i]);


        for (int i = 0 ; i <turnData.agentData[my_agent_id].collected.length ; i++ )
        {

            required.put((char)(turnData.agentData[my_agent_id].collected[i]+48) , required.get((char)(turnData.agentData[my_agent_id].collected[i]+48))-1);
        }
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


    private void find_path_to_diamond(boolean is_enemy, int enemy_id, Diamond diamond , int ind, TurnData turnData , List<Candidate>req_diamonds , int agent_id)
    {
        Node node = find_diamond_distance(diamond , turnData , agent_id);

        if (is_enemy)
        {
            req_diamonds.get(ind).enemy.get(enemy_id).cost = node.hoop;
            req_diamonds.get(ind).enemy.get(enemy_id).explored_node = node;
        }
        else {

            req_diamonds.get(ind).cost = node.hoop;
            req_diamonds.get(ind).explored_node = node;
        }
        is_task_finished++;
    }

    private  Node find_diamond_distance(Diamond goal , TurnData turnData , int agent_id)
    {

        int agent_row = turnData.agentData[agent_id].position.row;
        int agent_column = turnData.agentData[agent_id].position.column;

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
        if (goal == (char)(name.charAt(0)+32))
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
