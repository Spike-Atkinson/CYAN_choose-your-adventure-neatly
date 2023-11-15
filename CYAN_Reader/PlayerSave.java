package CYAN_Reader;

import CYAN_Mutual.StoryEvent;
import CYAN_Mutual.Player;
import CYAN_Mutual.Situation;

import java.util.ArrayList;

public class PlayerSave {
    public String storyTitle;
    public String username;
    public Player player;
    public ArrayList<StoryEvent> events;
    public Situation currentSituation;

    PlayerSave(String title, String username, Player player, ArrayList<StoryEvent> events, Situation currentSituation){
        this.storyTitle = title;
        this.username = username;
        this.player = player;
        this.events = events;
        this.currentSituation = currentSituation;
    }
}
