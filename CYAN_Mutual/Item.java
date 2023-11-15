package CYAN_Mutual;

public class Item {
    // image file or polygon // icon
     private String name;
    // message to player on receipt
    // image

    Item(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
