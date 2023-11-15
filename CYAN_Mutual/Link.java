package CYAN_Mutual;

import CYAN_Writer.LinkEditor;
import CYAN_Writer.Writer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;

public class Link {
    public int number;
    public String text;
    public transient JTextField textBox;
    public transient JComboBox toSelect;
    public transient JButton events;

    private int conditNum;
    private transient StoryEvent condition;
    public boolean conditionInverted;

    private ArrayList<Integer> changeNums;
    private transient ArrayList<StoryEvent> changesEvents;
    public ArrayList<StoryEvent.boolChange> boolsApplied;
    public transient JButton delete;
    //private  type; // type examples; luck(weighted chance), player inventory item, player attribute,
    private int fromNum;
    private int toNum;
    private transient Situation from;
    private transient Situation to;

    public Link(int number, String type, Situation from){
        this.number = number;
        //this.type = type;
        this.from = from;
        this.fromNum = from.number;
        changesEvents = new ArrayList<>();
        boolsApplied = new ArrayList<>();
        makeRow();
    }

    public void initialise(ArrayList<StoryEvent> events){
        if (changeNums == null) {
            changeNums = new ArrayList<>();
        }
        changesEvents = new ArrayList<>();
        if (conditNum != 0){
            for (StoryEvent event : events){
                if (event.num == conditNum){
                    condition = event;
                    break;
                } }
            condition.initialise(events);
        }
        for (Integer cNum : changeNums){
            for (StoryEvent event : events){
                if (event.num == cNum){
                    changesEvents.add(event);
                    break;
                }
            }

            for (StoryEvent e : changesEvents) {
                e.initialise(events);
            }

        }
    }

    public StoryEvent getCondition(){return condition;}
    public void setCondition(StoryEvent condition){
        if (condition == null){
            conditNum = 0;
        }else{
            conditNum = condition.num;
        }
        this.condition = condition;
    }
    public ArrayList<StoryEvent> getChangesEvents(){
        return changesEvents;
    }
    public void setChangesEvents(ArrayList<StoryEvent> eventArray, ArrayList<StoryEvent.boolChange> boolArray) {
        changesEvents = eventArray;
        changeNums = new ArrayList<>();
        for (StoryEvent e : changesEvents) {
            changeNums.add(e.num);
        }
        boolsApplied = boolArray;
    }
    public void removeChangeEvent(int i){
        changesEvents.remove(i);
        changeNums.remove(i);
        boolsApplied.remove(i);
    }


    public void setTo(Situation to){
        this.to = to;
        if(to == null){
            toNum = 0;
        }else {
            toNum = to.number;
        }
    }
   // public String getType(){return type;}
    public Situation getFrom(){return from;}
    public Situation getTo(){return to;}

     public void getSitFromNum(ArrayList<Situation> situations){
        for (Situation sit : situations) {
            if (sit.number == fromNum) {
                from = sit;
            } else if (sit.number == toNum) {
                to = sit;
            }
        }
    }
     public void makeRow(){
        // text box for naming choice
        textBox = new JTextField(text, 20);
        textBox.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                text = textBox.getText();
            }
         });
        //create array of elements with action listeners inc. nowhere and add them to a JComboBox
        String[] sNumbers = new String[Writer.situations.size()];
        sNumbers[0] = "Nowhere";
        int index = 0;
        for (int i = 1; i < sNumbers.length; i++){
             Situation sit = Writer.situations.get(index);
             if (sit == from) {index += 1; sit = Writer.situations.get(index);}
             String sitTitle = "S" + sit.number;
             if (sit.title != null){ sitTitle += " " + sit.title;}
             sNumbers[i] = sitTitle;
             index += 1;
        }
        toSelect = new JComboBox<>(sNumbers);
        toSelect.setPreferredSize(new Dimension(100,30));
        if (to == null) {
            toSelect.setSelectedIndex(0);
        }else if (toNum < fromNum){
            toSelect.setSelectedIndex(toNum);
        }
        else{
            toSelect.setSelectedIndex(toNum - 1);
        }
        toSelect.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             JComboBox cb = (JComboBox)e.getSource();
             String s = (String)cb.getSelectedItem();
             if (s == "Nowhere"){
                 if (to != null && to.jumpsFrom.contains(from)){
                     to.jumpsFrom.remove(from);
                     to.jumpInfoBox();
                 }
                 setTo(null);
             }
             else if(s.contains(" ")) {
                 s = s.split(" ")[0];
                 toNum = Integer.parseInt(s.substring(1, s.length()));
             }
             getSitFromNum(Writer.situations);
             if (to != null && (from.layer < to.layer - 1 || from.layer > to.layer + 1)) {
                 to.jumpsFrom.add(from);
                 to.jumpInfoBox();
                 from.jumpInfoBox();


             }
             Writer.oview.repaint();
         }
        });

         final Link thisLink = this;
         events = new JButton("Events...");
         events.setBackground(Color.BLACK);
         events.setForeground(Color.CYAN);
         events.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 LinkEditor le = new LinkEditor(thisLink);
                 le.setLocationRelativeTo(events);
             }
         });

         delete = new JButton("X");
         delete.setBackground(Color.RED);

         delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                from.removeLink(thisLink);
                if (to != null && (to.layer > from.layer + 1 || to.layer < from.layer -1)) {
                    to.jumpsFrom.remove(from);
                    from.jumpInfoBox();
                    to.jumpInfoBox();
                }
                from.updateSitEdit();
            }
         });
    }
}
