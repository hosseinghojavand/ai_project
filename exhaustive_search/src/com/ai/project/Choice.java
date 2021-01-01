package com.ai.project;

import java.util.ArrayList;
import java.util.List;

public class Choice {

    public List<Diamond> diamonds_list;
    public List<Node> nodes;
    public int score;
    public int turns_left;
    public int depth;
    public int chosed_home_order_ind;

    public Choice()
    {
        diamonds_list  = new ArrayList<>();
        nodes = new ArrayList<>();
        score = 0 ;
        turns_left = 0;
        depth = 0;
        chosed_home_order_ind = 0;
    }

    public Choice(List<Diamond> diamonds_list , int score)
    {
        this.diamonds_list  = diamonds_list;
        nodes = new ArrayList<>();
        this.score = score ;
        depth = 0;
        chosed_home_order_ind = 0;
    }

}
