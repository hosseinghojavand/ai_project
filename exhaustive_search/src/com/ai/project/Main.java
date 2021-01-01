package com.ai.project;


import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        try {

            String winner = new ExhaustiveSearchAgent().play();
            System.out.println("WINNER: " + winner);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
