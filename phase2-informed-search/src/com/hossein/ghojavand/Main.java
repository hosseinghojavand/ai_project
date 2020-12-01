package com.hossein.ghojavand;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {

            String winner = new InformedSearchAgent().play();
            System.out.println("WINNER: " + winner);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
