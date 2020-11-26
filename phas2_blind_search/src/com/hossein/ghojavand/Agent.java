package com.hossein.ghojavand;

import com.hossein.ghojavand.base.Action;
import com.hossein.ghojavand.base.AgentData;
import com.hossein.ghojavand.base.BaseAgent;
import com.hossein.ghojavand.base.TurnData;

import java.io.IOException;
import java.util.*;

public class Agent extends BaseAgent {

    private final Scanner scanner = new Scanner(System.in);

    private Stack<Node> route = new Stack<>();
    private Stack<Action> route2 = new Stack<>();

    Queue<Node> frontier = new LinkedList<>();


    List<Node> explored_set = new ArrayList<>();

    public Agent() throws IOException {
        super();

    }

    @Override
    public Action doTurn(TurnData turnData) {


        if (turnData.turnsLeft == maxTurns)
        {
           find_route(turnData);
        }

        while (!route2.isEmpty())
            return route2.pop();

        return Action.values()[(int) (Math.random() * Action.values().length)];

        //pop  => action


        /*System.out.println("TURN " + (maxTurns - turnData.turnsLeft) + "/" + maxTurns);
        for (AgentData agent : turnData.agentData) {
            System.out.println("AGENT " + agent.name);
            System.out.println("POSITION: (" + agent.position.row + ", " + agent.position.column + ")");
            System.out.println("CARRYING: " + agent.carrying);
            System.out.println("COLLECTED: " + Arrays.toString(agent.collected));
        }
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++)
                System.out.print(turnData.map[i][j]);
            System.out.println();
        }
        System.out.print("> ");
        String actionName = scanner.next().toUpperCase();
        if (actionName.equals("U"))
            return Action.UP;
        if (actionName.equals("D"))
            return Action.DOWN;
        if (actionName.equals("L"))
            return Action.LEFT;
        if (actionName.equals("R"))
            return Action.RIGHT;
        return Action.values()[(int) (Math.random() * Action.values().length)];*/
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
        Node node1 = node;
        while (node1!=null)
        {
            //System.out.println(node1.row + "-" + node1.column);
            if (node1.parent!=null)
                route2.add(find_action_to_parent(node1));
            node1 = node1.parent;
        }


    }
    private boolean find_route(TurnData turnData)
    {
        int grid_size = turnData.map.length;

        System.out.println(turnData.map[0][4]);
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
                        if (is_goal(expanded_node)) {
                            System.out.println("reached");
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
                        if (is_goal(expanded_node)) {
                            System.out.println("reached");
                            print_path(expanded_node);
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
                        if (is_goal(expanded_node)) {
                            System.out.println("reached");
                            print_path(expanded_node);
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
                        if (is_goal(expanded_node)) {
                            System.out.println("reached");
                            print_path(expanded_node);
                            return true;
                        } else {
                            frontier.add(expanded_node);
                        }
                    }
                }
            }
            else
            {
                //drops it and goes on
            }
        }
        return false;
    }

    private boolean is_goal(Node node)
    {
        return  node.data == '0' ||
                node.data == '1' ||
                node.data == '2' ||
                node.data == '3' ||
                node.data == '4';
    }


}
