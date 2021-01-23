package com.company;

import java.io.IOException;

public class Main {



    public static void main(String[] args) {
        Thread thread = null;
        final Process[] p = new Process[1];

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

            try { Thread.sleep(3000); } catch (Exception e) { }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        new Agent().play();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            try { Thread.sleep(1000); } catch (Exception e) { }


            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        new Agent().play();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();*/

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        new RandomAgent().play();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();





        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
