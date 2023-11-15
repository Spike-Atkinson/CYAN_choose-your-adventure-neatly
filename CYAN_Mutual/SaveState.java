package CYAN_Mutual;

import java.util.ArrayList;

public class SaveState {
    double version = 1.1;

    public GlobalConfig global;

    public Player player;
    public ArrayList<Item> items;
    public ArrayList<StoryEvent> events;
    public ArrayList<Situation> situations;




    public SaveState(GlobalConfig global, Player player, ArrayList<Item> items, ArrayList<StoryEvent> events, ArrayList<Situation> situations){
        this.global = global;
        this.player = player;
        this.items = items;
        this.events = events;
        this.situations = situations;
    }
}
