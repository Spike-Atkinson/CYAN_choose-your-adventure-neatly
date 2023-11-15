package CYAN_Mutual;

public class Stat {
    private String name;
    private int current;
    private int max;

    public Stat(String name, int init, int max){
        this.name = name;
        this.current = init;
        this.max = max;
        if (this.current > this.max){
            this.current = this.max;
        }
    }

    public String getName(){
        return name;
    }
    public int getCurrent(){
        return current;
    }
    public int getMax(){
        return max;
    }
    public void setName(String name){
        this.name = name;
    }
    public boolean setCurrent(int init){
        if (init >= 0 && init <= max){
            this.current = init;
            return true;
        }else{
            this.current = max;
        }
        return false;
    }
    public boolean setMax(int max){
        if (max > 0 && max < 9999){
            this.max = max;
            return true;
        }
        return false;
    }

}
