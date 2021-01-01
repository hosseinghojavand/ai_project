package com.ai.project;

public class BackUp {
    public int agent_row;
    public int agent_column;
    public int ind;
    public int turn;

    BackUp(int agent_row , int agent_column , int turn , int ind)
    {
        this.agent_row = agent_row;
        this.agent_column = agent_column;
        this.ind = ind;
        this.turn = turn;
    }
}
