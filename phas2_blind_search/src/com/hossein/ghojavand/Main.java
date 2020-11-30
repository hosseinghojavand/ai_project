package com.hossein.ghojavand;


import java.io.IOException;



public class Main {


    public static void main(String[] args) {


        try {

            //choose which agent will run

            //String winner = new Agent2().play();
            //String winner = new BlindSearchAgent().play();
            String winner = new InformedSearchAgent().play();


            System.out.println("WINNER: " + winner);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
