package com.hossein.ghojavand;

import com.hossein.ghojavand.base.Action;

import java.io.IOException;
import java.sql.Time;
import java.util.Date;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;


public class Main {


    public static void main(String[] args) {


        try {

            //choose which agent will run

            //String winner = new BlindSearchAgent().play();
            String winner = new InformedSearchAgent().play();


            System.out.println("WINNER: " + winner);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
