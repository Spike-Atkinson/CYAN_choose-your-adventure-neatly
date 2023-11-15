package CYAN_Mutual;

import CYAN_Writer.Writer;

import java.util.ArrayList;

public class StoryEvent {

    public int num;
    public String name;

    public enum boolChange {TRUE, FALSE, SWAP}

    private boolean isTrue;

    public enum types {SIMPLE, SEQUENCE, PLAYER} //linkchange events are set by choices, composite events are combinations of other events, check events check player attributes/items

    public types type;

    public enum boolType {AND, OR}
    public boolType seqBool;

    private ArrayList<Integer> conditNums;
    public transient ArrayList<StoryEvent> conditions;

    public StoryEvent(String name) {
        this.name = name;
        type = types.SIMPLE;
        isTrue = false;
        conditions = new ArrayList<>();
        conditNums = new ArrayList<>();
        num = Writer.unusedEventNum();
        seqBool = boolType.AND;
    }

    public void initialise(ArrayList<StoryEvent> mainEvents) {
        if (conditions == null) {
            conditions = new ArrayList<>();
        }
        if (conditNums != null && conditions.size() == 0) {
            for(Integer i : conditNums) {
                for (StoryEvent event : mainEvents) {
                    if (event.num == i) {
                        conditions.add(event);
                        break;
                    }
                }
            }
        }
    }

    public ArrayList<Integer> getNums(){
        return conditNums;
    }
    public void copyToNum() { //for serialisation purposes
        conditNums = new ArrayList<>();
        for (StoryEvent event : conditions){
            conditNums.add(event.num);
        }
    }
    public void change(boolChange bool){
        switch (bool.toString()) {
            case ("TRUE"):
                isTrue = true;
                break;
            case ("FALSE"):
                isTrue = false;
                break;
            case ("SWAP"):
                if (isTrue) {
                    isTrue = false;
                    break;
                } else {
                    isTrue = true;
                    break;
                }
        }
    }
    public void setIsTrue(boolean b){
        isTrue = b;
    }
    public boolean isTrue() {
        if (type == types.SEQUENCE) {
            if (seqBool == boolType.AND) {
                isTrue = true;
                for (StoryEvent event : conditions) {
                    if (!event.isTrue()) {
                        isTrue = false;
                        break;
                    }
                }
            } else if (seqBool == boolType.OR) {
                isTrue = false;
                for (StoryEvent event : conditions) {
                    if (event.isTrue()) {
                        isTrue = true;
                        break;
                    }
                }
            }
        }
        return isTrue;
    }
}
