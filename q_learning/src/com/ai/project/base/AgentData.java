package com.ai.project.base;

public final class AgentData {

    public final String name;
    public final Position position;
    public final Integer carrying;
    public final int[] collected;
    public final int score;

    public AgentData(String name, Position position, Integer carrying, int[] collected, int score) {
        this.name = name;
        this.position = position;
        this.carrying = carrying;
        this.collected = collected;
        this.score = score;
    }

}