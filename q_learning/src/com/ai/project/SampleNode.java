package com.ai.project;

public class SampleNode {
    public boolean is_active;
    public Diamond diamond;
    public Site site;

    public SampleNode()
    {

    }
    public SampleNode(Diamond diamond , Site site)
    {
        this.is_active = true;
        this.diamond = diamond;
        this.site = site;
    }
}
