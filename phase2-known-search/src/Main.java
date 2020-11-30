package src;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        //System.out.println("Hello World!");
        try {
            String winner = new src.Agent().play();
            System.out.println("WINNER: " + winner);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
