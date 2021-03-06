package com.company;

import com.company.base.Action;
import com.company.base.BaseAgent;
import com.company.base.TurnData;

import java.io.IOException;
import java.util.Scanner;

public class RandomAgent extends BaseAgent {

    private final Scanner scanner = new Scanner(System.in);

    public RandomAgent() throws IOException {
        super();
       /* System.out.println("MY NAME: " + name);
        System.out.println("PLAYER COUNT: " + agentCount);
        System.out.println("GRID SIZE: " + gridSize);
        System.out.println("MAX TURNS: " + maxTurns);
        System.out.println("DECISION TIME LIMIT: " + decisionTimeLimit);*/
    }

    @Override
    public Action doTurn(TurnData turnData) {
        /*for (AgentData agent : turnData.agentData) {
            System.out.println("AGENT " + agent.name);
            System.out.println("POSITION: (" + agent.position.row + ", " + agent.position.column + ")");
            System.out.println("CARRYING: " + agent.carrying);
            System.out.println("COLLECTED: " + Arrays.toString(agent.collected));
            System.out.println("SCORE: " + agent.score);
            System.out.println("REQUIREMENTS: " + Arrays.toString(agent.countRequired));
        }
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++)
                System.out.print(turnData.map[i][j]);
            System.out.println();
        }*/
        return Action.UP;
        //return Action.values()[(int) (Math.random() * Action.values().length)];
    }



    public static void main(String[] args) {
        try {
            String winner = new RandomAgent().play();
            System.out.println("WINNER: " + winner);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
