package bots;

import bots.missions.Mission;

import java.util.Queue;

public class Protocol<T> implements Mission<T> {
    public Queue<Mission> protocol;

    public State act(T t){
        return State.FINISHED;
    }

}
