package CYAN_Mutual;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Player {
    public String name;
    // Stores values like health, mana, stamina, experience, level, hunger, strength etc.
    public ArrayList<Stat> stats; // need a way to store current and Maximum values
    public Map<Item, Integer> inventory; // Item and quantity.
    public ArrayList<Integer> visited; // previously visited situations

    public Player(){
        this.name = null;
        this.stats = new ArrayList<Stat>();
        this.inventory = new TreeMap<>();
        this.visited = new ArrayList<>();
    }
}
