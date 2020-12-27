package com.ai.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Main {

    public  static  List<List<Diamond>> orders = new ArrayList<>();

    public static void main(String[] args) {

        try {

            String winner = new ExhaustiveSearchAgent().play();
            System.out.println("WINNER: " + winner);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*List<Diamond> diamonds = new ArrayList<>();

        for(int i = 0 ; i < 5 ; i++)
        {
            Diamond diamond = new Diamond(i , (char)(i+48) ,2 , 5);
            diamond.generate_value();
            diamonds.add(diamond);
        }


        long algo_start_time = new Date().getTime();

        //Diamond [] a = {diamonds.get(0) , diamonds.get(1) , diamonds.get(2) , diamonds.get(3) , diamonds.get(4)};

        printAllRecursive( diamonds.size(), diamonds);
//        printAllRecursive(diamonds.size() , diamonds);

        for(int i = 0 ; i < orders.size() ; i++)
        {
            for (int j = 0 ; j < orders.get(i).size() ; j++)
            {
                System.out.print(orders.get(i).get(j).sid);
            }
            System.out.println("");
        }

        System.out.println("size= " +  orders.size());
        System.out.println(new Date().getTime() - algo_start_time);*/
    }


    public static void printAllRecursive(int n, List<Diamond> elements) {

        if(n == 1) {
            printArray(elements);
        } else {
            for(int i = 0; i < n-1; i++) {
                printAllRecursive(n - 1, elements);
                if(n % 2 == 0) {
                    //System.out.println("befor: " + elements.get(i).sid + "-" + elements.get(n-1).sid );
                    swap(elements, i, n-1);
                    //System.out.println("befor: " + elements.get(i).sid + "-" + elements.get(n-1).sid );
                } else {
                    swap(elements, 0, n-1);
                }
            }
            printAllRecursive(n - 1, elements);
        }
    }

    private static void swap2(Diamond[] input, int a, int b) {
        Diamond tmp = input[a];
        input[a] = input[b];
        input[b] = tmp;
    }

    private static void swap(List<Diamond> input, int a, int b) {

        Diamond tmp = new Diamond(input.get(a).id , input.get(a).sid , input.get(a).row , input.get(a).column);
        Diamond bb = new Diamond(input.get(b).id , input.get(b).sid , input.get(b).row , input.get(b).column);
        input.set(a , bb);
        input.set(b ,tmp);
    }
    private static void printArray(List<Diamond> input) {

        List<Diamond> cpy= new ArrayList<>();
        for (int i =0 ; i <input.size() ; i++ )
            cpy.add(input.get(i));

        orders.add(cpy);
    }
}
