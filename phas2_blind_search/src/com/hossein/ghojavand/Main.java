package com.hossein.ghojavand;

import com.hossein.ghojavand.base.Action;

import java.io.IOException;
import java.sql.Time;
import java.util.Date;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;


public class Main {

    public  static  Stack<Action> actions = new Stack<>();
    public static Action re()
    {
        int a = 1;
        if (!actions.isEmpty())
            return actions.pop();
        else
            return Action.UP;
    }
    public static void main(String[] args) {

        /*System.out.println(new Date().getTime());
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            @Override
            public void run() {
                System.out.println(new Date().getTime());
                timer.cancel();
            }
        },40);*/

        /*actions.add(Action.LEFT);
        System.out.println(new Date().getTime());
        re();
        System.out.println(new Date().getTime());*/
        try {
            //String winner = new Agent().play();
            String winner = new InformedSearchAgent().play();
            System.out.println("WINNER: " + winner);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
