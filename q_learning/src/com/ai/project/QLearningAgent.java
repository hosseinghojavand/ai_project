package com.ai.project;

import com.ai.project.base.Action;
import com.ai.project.base.BaseAgent;
import com.ai.project.base.TurnData;

import java.io.IOException;

public class QLearningAgent extends BaseAgent {


    public QLearningAgent() throws IOException {
        super();
    }

    @Override
    public Action doTurn(TurnData turnData) {
        return Action.DOWN;
    }
}
