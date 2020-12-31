package com.ai.project;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class Main {

    static List<Site> list = new ArrayList<>();

    public static void main(String[] args) {

        try {

            String winner = new ExhaustiveSearchAgent().play();
            System.out.println("WINNER: " + winner);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*list.add(new Site(list.size() , 1 ,2));
        list.add(new Site(list.size() , 1 ,2));

        List<List<Site>> result = make_all_permutions(5);

        for(int i =0 ; i < result.size() ; i++)
        {
            for (int j = 0 ; j < result.get(i).size() ; j++)
            {
                System.out.print(result.get(i).get(j).id);
            }
            System.out.println("");
        }*/


    }

    private static List<Site> cpy (List<Site> original)
    {
        List<Site> tmp = new ArrayList<>();
        for (int i = 0 ; i < original.size();i++)
        {
            tmp.add(original.get(i));
        }
        return tmp;
    }


    private static List<List<Site>> make_all_permutions(int n)
    {
        List<List<Site>> ll = new ArrayList<>();
        List<List<Site>> result = new ArrayList<>();
        List<Site> temp = new ArrayList<>();

        if (n==1) {
            for (int i = 0; i < list.size(); i++) {
                temp = new ArrayList<>();
                temp.add(list.get(i));
                ll.add(new ArrayList<>(new ArrayList<>(temp)));
            }
            return ll;
        }
        else
        {
            result = make_all_permutions(n-1);
            for(int i =0; i < list.size() ; i++)
            {
                for(int j =0; j < result.size() ; j++)
                {
                    temp = new ArrayList<>();
                    temp.add(list.get(i));
                    temp.addAll(result.get(j));
                    ll.add(new ArrayList<>(temp));
                }
            }
            return new ArrayList<>(ll);
        }
    }



}
