package com.ai.project;

import java.util.ArrayList;
import java.util.List;

public class Choice {

    boolean active = true;

    public List<Diamond> diamonds_list;
    public List<Site> sites_list;
    public int score;


    public Choice()
    {
        diamonds_list  = new ArrayList<>();
        sites_list = new ArrayList<>();
        score = 0 ;
    }

    public Choice(List<Diamond> diamonds_list , int score)
    {
        this.diamonds_list  = diamonds_list;
        sites_list = new ArrayList<>();
        this.score = score ;
    }

}
