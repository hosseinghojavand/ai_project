package com.hossein.ghojavand;

import com.hossein.ghojavand.base.Action;
import com.hossein.ghojavand.base.AgentData;
import com.hossein.ghojavand.base.BaseAgent;
import com.hossein.ghojavand.base.TurnData;

import java.io.IOException;
import java.util.*;

public class Agent extends BaseAgent {

    private final Scanner scanner = new Scanner(System.in);

    private Deque<Node> route = new LinkedList<>();
    Queue<Node> frontier = new LinkedList<>();


    Tree<Node> explored_set = new Tree<>();

    public Agent() throws IOException {
        super();

    }

    @Override
    public Action doTurn(TurnData turnData) {


        if (route.size() == 0)
            find_route(turnData);

        pop  => action


        System.out.println("TURN " + (maxTurns - turnData.turnsLeft) + "/" + maxTurns);
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
        return Action.values()[(int) (Math.random() * Action.values().length)];
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






            if (node_to_explore.row + 1 < grid_size) {
                if (turnData.map[node_to_explore.row + 1][node_to_explore.column] != '*') {
                    Node node = new Node(node_to_explore.row + 1, node_to_explore.column);
                    if (is_goal(node)) {

                    } else {
                        frontier.add(node);
                    }
                }
            }
            if (node_to_explore.row - 1 >= 0) {
                if (turnData.map[node_to_explore.row - 1][node_to_explore.column] != '*') {
                    Node node = new Node(node_to_explore.row - 1, node_to_explore.column);
                    if (is_goal(node)) {

                    } else {
                        frontier.add(node);
                    }
                }
            }
            if (node_to_explore.column + 1 < grid_size) {
                if (turnData.map[node_to_explore.row][node_to_explore.column + 1] != '*') {
                    Node node = new Node(node_to_explore.row, node_to_explore.column + 1);
                    if (is_goal(node)) {

                    } else {
                        frontier.add(node);
                    }
                }
            }
            if (node_to_explore.column - 1 >= 0) {
                if (turnData.map[node_to_explore.row][node_to_explore.column - 1] != '*') {
                    Node node = new Node(node_to_explore.row, node_to_explore.column - 1);
                    if (is_goal(node)) {

                    } else {
                        frontier.add(node);
                    }
                }
            }
        }
        return false;
    }

    private boolean is_goal(Node node)
    {
        return  node.data == 0 ||
                node.data == 1 ||
                node.data == 2 ||
                node.data == 3 ||
                node.data == 4;
    }


}
