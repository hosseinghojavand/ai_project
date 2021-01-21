package com.company.base;

public final class AgentData {

    public final String name;
    public final Position position;
    public final Integer carrying;
    public final int[] collected, countRequired;
    public final int score;

    public AgentData(String name, Position position, Integer carrying, int[] collected, int score, int[] countRequired) {
        this.name = name;
        this.position = position;
        this.carrying = carrying;
        this.collected = collected;
        this.score = score;
        this.countRequired = countRequired;
    }

}
