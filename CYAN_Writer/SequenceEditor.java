package CYAN_Writer;

import CYAN_Mutual.StoryEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SequenceEditor extends JFrame {

    private StoryEvent thisEvent; // the event that is true if this sequence is true
    private
    String[] existingEvents; // the string names of all the events bar thisEvent
    private ArrayList<StoryEvent> events; // a copy of Writer.Events with thisEvent removed
    private ArrayList<JComboBox> boxes; //the JComboBoxes
    private ArrayList<StoryEvent> selection;
    private JPanel boundary;
    private GridLayout grid;
    private JComboBox bool;
    //private JLabel b;
    private Dimension rowSize = new Dimension(275, 30);

    public SequenceEditor(StoryEvent thisEvent) {
        this.thisEvent = thisEvent;
        events = new ArrayList<>();
        for (StoryEvent event : Writer.events){
            if (event != thisEvent) {
                events.add(event);
            }
        }
        existingEvents = new String[Writer.events.size() + 1];
        boxes = new ArrayList<>();
        for (int i = 0; i < events.size(); i++){
            existingEvents[i] = events.get(i).name;
        }
        setUndecorated(true);
        setBounds(0, 0, 300, 300);
        setPreferredSize(new Dimension(300, 300));
        setVisible(true);
        requestFocus();

        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                super.windowLostFocus(e);
                dispose();
            }
        });

        boundary = new JPanel();

        boundary.setPreferredSize(rowSize);
        boundary.setBackground(Color.darkGray);
        grid = new GridLayout(4, 1);
        boundary.setLayout(grid);
        JScrollPane js = new JScrollPane(boundary);
        js.setPreferredSize(new Dimension(300, 300));
        js.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        js.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(js);
        displaySequence();

    }
        private JPanel newRowPanel(JComponent component){
            JPanel panel = new JPanel();
            panel.setPreferredSize(rowSize);
            panel.setBounds(0, 0, rowSize.width, rowSize.height);
            panel.setBackground(Color.BLACK);
            if (component != null){panel.add(component);}
        return panel;
        }
        private void displaySequence() {
            boundary.removeAll();
            boundary.setLayout(grid);
            boxes = new ArrayList<>();
            //grid.setRows(2);
            JLabel eventTrueIf = new JLabel(thisEvent.name + " is true if ");
            eventTrueIf.setForeground(Color.CYAN);
            //eventTrueIf.setPreferredSize(ROW);
            boundary.add(newRowPanel(eventTrueIf));
            makeBoolBox();
            JLabel following = new JLabel("of the following are true:");
            //following.setPreferredSize(ROW);
            following.setForeground(Color.CYAN);
            boundary.add(newRowPanel(following));

            // add combo boxes
            if (thisEvent.conditions.size() < 2) {
                 if(thisEvent.conditions.size() == 0){
                    makeEventBox(events.get(0));
                    makeEventBox(events.get(1));
                }else if(thisEvent.conditions.size() == 1){
                    makeEventBox(thisEvent.conditions.get(0));
                    for (StoryEvent event : events){
                        if (event != thisEvent.conditions.get(0)){
                            makeEventBox(event);
                            break;
                        }
                    }
                }
            }
            else {
                for (StoryEvent event : thisEvent.conditions) {
                    makeEventBox(event);
                }
            }
            saveSelection();
            JButton addToSeq = new JButton("Add to Sequence");
            addToSeq.setPreferredSize(rowSize);
            if (boxes.size() == Writer.events.size() -1){addToSeq.setEnabled(false);}
            addToSeq.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (StoryEvent event : events) {
                        if(!selection.contains(event)) {
                            thisEvent.conditions.add(event);
                            grid.setRows(4);
                            displaySequence();
                            break;
                        }
                    }
                    if (boxes.size() == Writer.events.size() -1){addToSeq.setEnabled(false);}
                }
            });
            boundary.add(newRowPanel(addToSeq));
            boundary.setPreferredSize(new Dimension(300, (grid.getRows() + 1) * rowSize.height));
            boundary.updateUI();
        }
        public void makeEventBox(StoryEvent event){ // the event that is in StoryEvent.events
            JPanel panel = newRowPanel(null);
            JComboBox eventJCombo = new JComboBox(existingEvents);
            eventJCombo.setPreferredSize(new Dimension(rowSize.width -30, rowSize.height));
            for(int i = 1; i < existingEvents.length; i++){
                if (existingEvents[i] == event.name){
                    eventJCombo.setSelectedIndex(i);
                }
            }
            eventJCombo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saveSelection();
                }
            });
            boxes.add(eventJCombo);
            grid.setRows(grid.getRows()+1);

            JButton delete = new JButton("X");
            delete.setBackground(Color.RED);
            delete.setPreferredSize(new Dimension(30,30));
            delete.setMargin(new Insets(0, 0, 0,0));
            delete.addActionListener(new ActionListener() {
                 @Override
                 public void actionPerformed(ActionEvent e) {
                    boxes.remove(eventJCombo);
                    saveSelection();
                    displaySequence();
                 }
            });
            panel.add(eventJCombo);
            if (boxes.size() > 2){
                panel.add(delete);
                eventJCombo.setPreferredSize(new Dimension(rowSize.width -40, rowSize.height));
            }
            else{
                eventJCombo.setPreferredSize(new Dimension(rowSize.width, rowSize.height));
            }
            boundary.add(panel);

        }
        private void makeBoolBox(){
            String[] bools = new String[2];
            bools[0] = "All";
            bools[1] = "Any";
            bool = new JComboBox(bools);
            //bool.setPreferredSize(ROW);
            bool.setSelectedIndex(thisEvent.seqBool.ordinal());
            bool.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    switch ((String)bool.getSelectedItem()) {
                        case ("All"):
                            thisEvent.seqBool = StoryEvent.boolType.AND;
                            break;
                        case ("Any"):
                            thisEvent.seqBool = StoryEvent.boolType.OR;
                            break;
                    }
                }
            });
            boundary.add(newRowPanel(bool));
        }
        private void saveSelection(){
           selection = new ArrayList<>();
            for (JComboBox combo : boxes){
                for (StoryEvent event : Writer.events){
                    if(event.name.equals(combo.getSelectedItem().toString())){
                        selection.add(event);
                    }
                }
            }
            thisEvent.conditions = selection;
            thisEvent.copyToNum();
        }
}
