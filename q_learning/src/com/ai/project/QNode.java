package com.ai.project;

public class QNode {

    public Integer [] node_data;

    public QNode(int n)
    {
        int min = -20;
        int max = -1;

        node_data = new Integer[n];
        for(int i =0 ; i < n ; i++)
        {
            node_data[i] = (int)(Math.random() * (max - min + 1) + min);
        }

    }

}
