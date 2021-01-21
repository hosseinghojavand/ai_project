package com.ai.project;

import com.ai.project.base.Action;
import com.ai.project.base.AgentData;
import com.ai.project.base.BaseAgent;
import com.ai.project.base.TurnData;

import java.io.IOException;
import java.util.*;

public  class Agent
{
    private static  SampleNode[] sampleNodes;
    private static QNode[][][] q_table;
    private  static boolean is_qtable_filled = false;

    private static double EPSILON = 0.4;
    private static int N_EPISODES = 32;


    private  static int active_nodes;

    private  static Queue<Action> actions = new LinkedList<>();

    private  static boolean has_rand_action = false;
    private  static Thread fill_q_table_thread;


    private static List<BackUp> backups = new ArrayList<>();


    private static int episode_score = 0;

    public static int n_sites = 0;


    static boolean is_thread_running = false;

    static Integer [] all_episodes_score;

    public static class QLearningAgent extends BaseAgent {

        public QLearningAgent() throws IOException {
            super();


        }

        void print_map(char[][] map, int size) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++)
                    System.out.print(map[i][j]);
                System.out.println();
            }

        }


        @Override
        public Action doTurn(TurnData turnData) {

        /*System.out.println("---------------------------------------------------");
        System.out.println("turns left = " + turnData.turnsLeft);
        System.out.println("---------------------------------------------------");*/

            if (!is_qtable_filled) {
                fill_q_table_thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        init(turnData);
                    }
                });
            }


            if (is_qtable_filled) {

                if (turnData.turnsLeft == maxTurns) {
                    for (SampleNode sampleNode : sampleNodes) {
                        sampleNode.is_active = true;
                    }
                    active_nodes = sampleNodes.length;
                    actions = new LinkedList<>();
                    has_rand_action = false;
                }




                if (actions.isEmpty()) {

                    //if (active_nodes > 0) {
                    int keep_ind = 0;
                    int keep_score = -100;

                    double random_double = Math.random();
                    if (random_double > EPSILON) {
                        //System.out.println("not by chance");
                        QNode qNode = q_table[turnData.agentData[0].position.row][turnData.agentData[0].position.column][(sampleNodes.length - active_nodes) / n_sites];

                        //  find maximum active index
                        for (int i = 0; i < qNode.node_data.length; i++) {
                            if ((qNode.node_data[i] > keep_score) && sampleNodes[i].is_active) {
                                keep_score = qNode.node_data[i];
                                keep_ind = i;
                            }
                        }
                    } else {
                        //System.out.println("by chance");
                        int random_int = (int) (Math.random() * active_nodes);
                        for (int i = 0; i < sampleNodes.length; i++) {
                            if (sampleNodes[i].is_active) {
                                if (random_int == 0) {
                                    keep_ind = i;
                                    keep_score = q_table[turnData.agentData[0].position.row][turnData.agentData[0].position.column][(sampleNodes.length - active_nodes) / n_sites].node_data[i];
                                }
                                random_int--;
                            }
                        }
                    }

                    if (keep_score > -100) {
                        for (SampleNode sampleNode : sampleNodes) {
                            if (sampleNode.diamond.id == sampleNodes[keep_ind].diamond.id)
                                sampleNode.is_active = false;
                        }
                    }


                    /*System.out.println("*******************");

                    System.out.println("agent = " + turnData.agentData[0].position.row + turnData.agentData[0].position.column);

                    System.out.print("diamond_row = " + sampleNodes[keep_ind].diamond.row +
                            ", diamond_column = " + sampleNodes[keep_ind].diamond.column + " to ");

                    System.out.println("site_row = " + sampleNodes[keep_ind].site.row +
                            ", site_column = " + sampleNodes[keep_ind].site.column);

                    System.out.println("*******************");*/


                    if (has_rand_action) {
                        //System.out.println("cancel rand action before generating actions1");
                        has_rand_action = false;
                        return Action.LEFT;
                    }
                    generate_actions_for(turnData, sampleNodes[keep_ind].diamond, sampleNodes[keep_ind].site);

                    if (turnData.turnsLeft >= actions.size()-1) {
                        episode_score += sampleNodes[keep_ind].diamond.value;
                        all_episodes_score[N_EPISODES-1] = episode_score;
                        q_table[turnData.agentData[0].position.row][turnData.agentData[0].position.column][(sampleNodes.length - active_nodes) / n_sites].node_data[keep_ind] = episode_score;
                        backups.add(new BackUp(turnData.agentData[0].position.row, turnData.agentData[0].position.column, (sampleNodes.length - active_nodes) / n_sites, keep_ind));

                       /* System.out.println("------------------------------------------------------------------");
                        System.out.println("agent row = " + turnData.agentData[0].position.row);
                        System.out.println("agent column = " + turnData.agentData[0].position.column);
                        System.out.println("turn = " + (sampleNodes.length - active_nodes) / 2);
                        System.out.println("ind = " + keep_ind);
                        System.out.println("score = " + episode_score);*/


                    }

                    active_nodes -= n_sites;

                    if (!actions.isEmpty())
                        return actions.poll();
                    else {
                        //System.out.println("make rand action --- list not read");
                        return make_rand_action(turnData);
                    }
                /*} else
                    return make_rand_action(turnData);*/
                } else {
                    if (has_rand_action) {
                        //System.out.println("cancel rand action before generating actions2");
                        has_rand_action = false;
                        return Action.LEFT;
                    } else
                        return actions.poll();
                }

            } else {
                if (!is_thread_running) {
                    fill_q_table_thread.start();
                    is_thread_running = true;
                }

                //System.out.println("make rand action -- filling q table");
                return make_rand_action(turnData);
            }

        }

        private void generate_actions_for(TurnData turnData, Diamond diamond, Site site) {

            int agent_row = turnData.agentData[0].position.row;
            int agent_column = turnData.agentData[0].position.column;

            char[][] map = new char[turnData.map.length][turnData.map.length];
            for (int m = 0; m < gridSize; m++) {
                for (int j = 0; j < gridSize; j++)
                    map[m][j] = turnData.map[m][j];
            }

            if (find_actions_for(diamond.sid, map.length, map, diamond.row, diamond.column, agent_row, agent_column)) {
                map[diamond.row][diamond.column] = '.';
                agent_row = diamond.row;
                agent_column = diamond.column;

                find_actions_for('a', map.length, map, site.row, site.column, agent_row, agent_column);
            }
        }


        private Action make_rand_action(TurnData turnData) {

            if (has_rand_action) {
                has_rand_action = false;
                return Action.LEFT;
            } else {
                AgentData agentData = turnData.agentData[0];
                if (agentData.position.column + 1 < gridSize) {
                    if (turnData.map[agentData.position.row][agentData.position.column + 1] == '*')
                        return Action.RIGHT;
                } else {
                    return Action.RIGHT;
                }
                if (agentData.position.column - 1 >= 0) {
                    if (turnData.map[agentData.position.row][agentData.position.column - 1] == '*')
                        return Action.LEFT;
                } else {
                    return Action.LEFT;
                }
                if (agentData.position.row + 1 < gridSize) {
                    if (turnData.map[agentData.position.row + 1][agentData.position.column] == '*')
                        return Action.DOWN;
                } else {
                    return Action.DOWN;
                }
                if (agentData.position.row - 1 >= 0) {
                    if (turnData.map[agentData.position.row - 1][agentData.position.column] == '*')
                        return Action.UP;
                } else {
                    return Action.UP;
                }

                has_rand_action = true;
                return Action.RIGHT;
            }


        }


        private void init(TurnData turnData) {

            all_episodes_score = new Integer[N_EPISODES];

            for (int i = 0 ; i < N_EPISODES ; i++)
                all_episodes_score[i] = 0;

            List<Diamond> diamonds = new ArrayList<>();
            List<Site> sites = new ArrayList<>();




            //  finding diamonds and sites in map
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    if (turnData.map[i][j] == '0' ||
                            turnData.map[i][j] == '1' ||
                            turnData.map[i][j] == '2' ||
                            turnData.map[i][j] == '3' ||
                            turnData.map[i][j] == '4') {
                        Diamond diamond = new Diamond(diamonds.size(), turnData.map[i][j], i, j);
                        diamond.generate_value();
                        diamonds.add(diamond);
                    } else if (turnData.map[i][j] == 'a') {
                        Site site = new Site(sites.size(), i, j);
                        sites.add(site);
                    }
                }
            }


            n_sites = sites.size();

            sampleNodes = new SampleNode[diamonds.size() * sites.size()];
            q_table = new QNode[gridSize][gridSize][diamonds.size()];


            //  filling q table with random data
            //  visit constructor of QNode
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    for (int k = 0; k < diamonds.size(); k++) {
                        q_table[i][j][k] = new QNode(diamonds.size() * sites.size());
                    }
                }
            }

            //  generating sample node array
            int ind = 0;
            for (Diamond diamond : diamonds) {
                for (Site site : sites) {
                    sampleNodes[ind] = new SampleNode(diamond, site);
                    ind++;
                }
            }
            active_nodes = diamonds.size() * sites.size();

            is_qtable_filled = true;
            System.out.println("q table is filled with data");
            is_thread_running = false;

        }

        private boolean is_in_explored_set(List<Node> explored_set, Node node) {
            for (Node node1 : explored_set) {
                if (node1.equals(node))
                    return true;
            }
            return false;

        }

        private boolean check_can_go(char data, char goal) {
            if (goal == 'a') {
                return data != '*';
            } else {
                if (data == goal)
                    return true;
                else {
                    return data != '*' &&
                            data != '0' &&
                            data != '1' &&
                            data != '2' &&
                            data != '3' &&
                            data != '4';
                }
            }
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
                        if (check_can_go(map[node.row + 1][node.column], goal)) {
                            expanded_node = new Node(node.row + 1, node.column);
                            expanded_node.distance_to_goal = Math.abs(expanded_node.row - row) + Math.abs(expanded_node.column - column);
                            expanded_node.parent = node;
                            expanded_node.hoop = expanded_node.parent.hoop + 1;
                            expanded_node.data = map[node.row + 1][node.column];
                            if (expanded_node.data == goal) {
                                if ((node.row + 1 == row) && (node.column == column)) {
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
                        if (check_can_go(map[node.row - 1][node.column], goal)) {
                            expanded_node = new Node(node.row - 1, node.column);
                            expanded_node.distance_to_goal = Math.abs(expanded_node.row - row) + Math.abs(expanded_node.column - column);
                            expanded_node.parent = node;
                            expanded_node.hoop = expanded_node.parent.hoop + 1;
                            expanded_node.data = map[node.row - 1][node.column];
                            if (expanded_node.data == goal) {

                                if ((node.row - 1 == row) && (node.column == column)) {
                                    fill_actions(expanded_node);
                                    return true;
                                }
                            } else {
                                frontier.add(expanded_node);
                            }
                        }
                    }
                    if (node.column + 1 < grid_size) {
                        if (check_can_go(map[node.row][node.column + 1], goal)) {
                            expanded_node = new Node(node.row, node.column + 1);
                            expanded_node.distance_to_goal = Math.abs(expanded_node.row - row) + Math.abs(expanded_node.column - column);
                            expanded_node.parent = node;
                            expanded_node.hoop = expanded_node.parent.hoop + 1;
                            expanded_node.data = map[node.row][node.column + 1];
                            if (expanded_node.data == goal) {

                                if ((node.row == row) && (node.column + 1 == column)) {
                                    fill_actions(expanded_node);
                                    return true;
                                }
                            } else {
                                frontier.add(expanded_node);
                            }
                        }
                    }
                    if (node.column - 1 >= 0) {
                        if (check_can_go(map[node.row][node.column - 1], goal)) {
                            expanded_node = new Node(node.row, node.column - 1);
                            expanded_node.distance_to_goal = Math.abs(expanded_node.row - row) + Math.abs(expanded_node.column - column);
                            expanded_node.parent = node;
                            expanded_node.hoop = expanded_node.parent.hoop + 1;
                            expanded_node.data = map[node.row][node.column - 1];
                            if (expanded_node.data == goal) {

                                if ((node.row == row) && (node.column - 1 == column)) {
                                    fill_actions(expanded_node);
                                    return true;
                                }
                            } else {
                                frontier.add(expanded_node);
                            }
                        }
                    }

                    sortFrontier(frontier);
                }


            }

            return false;
        }


        private LinkedList<Node> sortFrontier(Queue<Node> frontier) {

            Node[] array;
            array = frontier.toArray(new Node[frontier.size()]);

            Node min = array[0];
            int min_index = 0;
            for (int i = 0; i < frontier.size(); i++) {
                if (array[i].distance_to_goal + array[i].hoop < min.distance_to_goal + min.hoop) {
                    min = array[i];
                    min_index = i;
                } else if (array[i].distance_to_goal + array[i].hoop ==
                        min.distance_to_goal + min.hoop && array[i].distance_to_goal < min.distance_to_goal) {
                    min = array[i];
                    min_index = i;
                }
            }

            Node tmp = array[0];
            array[0] = array[min_index];
            array[min_index] = tmp;

            return new LinkedList<Node>(Arrays.asList(array));


        }


    }

    public static void main(String[] args) {

        Thread thread = null;
        final Process[] p = new Process[1];
        while (N_EPISODES > 0) {
            System.out.println("-----------------------------------------------------------");
            System.out.println("episode " + N_EPISODES);
            try {

                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            p[0] = Runtime.getRuntime().exec("python3 AI991-Server.pex");
                        } catch (Exception e) {
                        }
                    }
                });

                thread.start();

                try { Thread.sleep(2000); } catch (Exception e) { }

                String winner = new QLearningAgent().play();

                for (BackUp backUp : backups)
                    q_table[backUp.agent_row][backUp.agent_column][backUp.turn].node_data[backUp.ind] += episode_score;

                episode_score = 0;
                backups.clear();

                if(EPSILON >0.1)
                {
                    EPSILON -=((EPSILON -0.1)/((N_EPISODES*3) /4));
                }
                System.out.println("WINNER: " + winner);

                p[0].destroy();
                thread.stop();

            } catch (IOException e) {
                e.printStackTrace();
                p[0].destroy();
                thread.stop();
            }

            N_EPISODES--;
        }


        System.out.println("--------------------------------------------------------------------------------------");
        System.out.println("results:");

        System.out.print("EPISODE:    ");
        for (int i = 0 ; i < all_episodes_score.length ; i++)
        {
            System.out.print(i +"   ");
        }
        System.out.println("");
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.print("SCORES:    ");
        for (Integer integer : all_episodes_score) {
            System.out.print(integer + "   ");
        }

    }

}



