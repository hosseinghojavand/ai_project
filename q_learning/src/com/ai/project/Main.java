package com.ai.project;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;
import java.io.*;
import java.time.chrono.ThaiBuddhistChronology;

public class Main {

    public static void main(String[] args) {

        Thread thread = null;
        final Process[] p = new Process[1];
        while (true) {
            try {

                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            p[0] = Runtime.getRuntime().exec("python3 AI991-Server.pex");
                        }
                        catch (Exception e)
                        {

                        }
                    }
                });

                thread.start();

                try {
                    Thread.sleep(2000);
                }catch (Exception e)
                {

                }



                String winner = new LocalSearchAgent().play();
                System.out.println("WINNER: " + winner);

                p[0].destroy();
                thread.stop();

            } catch (IOException e) {
                e.printStackTrace();
                p[0].destroy();
                thread.stop();
            }
        }
    }
}
