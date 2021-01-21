package com.company;

public class Candidate {

    Diamond diamond;
    int cost;
    Node explored_node;

    public Candidate(Diamond diamond , int cost)
    {
        this.diamond = diamond;
        this.cost = cost;
        explored_node = new Node();
    }

    public Candidate()
    {

    }
}
