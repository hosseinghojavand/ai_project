package com.company;

import java.util.ArrayList;
import java.util.List;

public class Candidate {

    Diamond diamond;
    int cost;
    Node explored_node;
    int agent_id;


    List<Candidate> enemy = new ArrayList<>();

    public Candidate(Diamond diamond , int cost)
    {
        this.diamond = diamond;
        this.cost = cost;
        explored_node = new Node();
    }

    public Candidate(Diamond diamond , int cost , int agent_id)
    {
        this.diamond = diamond;
        this.agent_id = agent_id;
        this.cost = cost;
        explored_node = new Node();
    }

    public Candidate()
    {

    }
}
