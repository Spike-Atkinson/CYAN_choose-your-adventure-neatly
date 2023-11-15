package CYAN_Writer;

import CYAN_Mutual.StoryEvent;
import CYAN_Mutual.Link;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class LinkEditor extends JFrame {
    private Link l;
    private JPanel boundary;
    private JComboBox conditionSelect;

    private GridLayout cGridLOM; // changes Grid Layout Manager
    private JPanel changes;

    private JButton addChangeButton;
    private ArrayList<JComboBox> eventChangeBoxes;
    private ArrayList<JComboBox> boolChangeBoxes;
    //private HashMap<StoryEvent, StoryEvent.boolChange> changePairs;
    private String[] eventNames; // String array of event names + "None"

    public LinkEditor(Link l) {
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                super.windowLostFocus(e);
                dispose();
            }
        });
        this.l = l;
        setUndecorated(true);
        setBounds(0, 0, 300, 300);
        setVisible(true);
        requestFocus();

        eventChangeBoxes = new ArrayList<>();
        boolChangeBoxes = new ArrayList<>();

        boundary = new JPanel();
        boundary.setBounds(0, 0, 300, 300);
        //boundary.setLayout(new GridLayout(2, 1));
        JScrollPane js = new JScrollPane(boundary);
        js.setPreferredSize(new Dimension(getWidth(), getHeight()));
        js.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        js.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(js);

        String[] events = new String[Writer.events.size() + 1];
        events[0] = "Always Display";
        for (StoryEvent e : Writer.events) {
            events[Writer.events.indexOf(e) + 1] = e.name;
        }
        JPanel condition = new JPanel();
        condition.setBounds(0, 0, 300, 150);
        condition.setLayout(new GridLayout(4, 1));
        condition.add(new JLabel(l.text));
        condition.add(new JLabel("Only display this choice if: "));
        conditionSelect = new JComboBox(events);
        if (l.getCondition() != null) {
            for (int i = 1; i < events.length; i++) {
                if (events[i].equals(l.getCondition().name)) {
                    conditionSelect.setSelectedIndex(i);
                }
            }
        }
        conditionSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                String s = (String) cb.getSelectedItem();
                if (s == "Always Display") {
                    l.setCondition(null);
                } else {
                    for (StoryEvent event : Writer.events) {
                        if (event.name == s) {
                            l.setCondition(event);
                            break;
                        }
                    }
                }
            }
        });
        condition.add(conditionSelect);

        JPanel trueOrFalse = new JPanel();
        JRadioButton t = new JRadioButton("True");
        t.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                l.conditionInverted = false;
            }
        });
        JRadioButton f = new JRadioButton("false");
        f.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                l.conditionInverted = true;
            }
        });
        ButtonGroup tf = new ButtonGroup();
        tf.add(t);
        tf.add(f);
        f.setSelected(l.conditionInverted);
        t.setSelected(!l.conditionInverted);
        trueOrFalse.add(t);
        trueOrFalse.add(f);

        condition.add(trueOrFalse);
        condition.setPreferredSize(new Dimension(300, 175));
        boundary.add(condition);

        ArrayList<String> simpleEvents = new ArrayList<>();
        simpleEvents.add("None");
        for (StoryEvent event : Writer.events) {
            if (event.type == StoryEvent.types.SIMPLE) {
                simpleEvents.add(event.name);
            }
        }
        eventNames = simpleEvents.toArray(new String[simpleEvents.size()]);
        makeChangesGrid();
    }
    private void makeChangesGrid(){
        changes = new JPanel();
        changes.setBounds(0, 0, 300, 150);
        cGridLOM = new GridLayout(1, 1);
        changes.setLayout(cGridLOM);

        addChangeButton = new JButton();
        if (l.getChangesEvents().size() == 0){
            addChangeButton.setText("Change an event when selected?");
            addChangeButton.setToolTipText("You can set events to be changed to true/false/swap when this choice is selected.");
        }
        else{
            addChangeButton.setText("Add event to change");
            addChangeButton.setToolTipText("You can set events to be changed to true/false/swap when this choice is selected.");
            cGridLOM.setRows(cGridLOM.getRows() +1);
            changes.add(new JLabel("If selected, change: "));
        }
        for (StoryEvent e : l.getChangesEvents()){
            addChange(e);
        }
        addChangeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addChange(null);
                boundary.setPreferredSize(new Dimension(300, 175 + cGridLOM.getRows() * 70));
            }
        });

        changes.add(addChangeButton);
        boundary.add(changes);
        boundary.setPreferredSize(new Dimension(300, 175 + cGridLOM.getRows() * 70));
    }
    private void addChange(StoryEvent e) {
        changes.remove(addChangeButton);
        JPanel change = new JPanel();
        change.setBounds(0, 0, 300,70);
        change.setBackground(Color.BLACK);
        change.setLayout(new GridLayout(2, 1));

        JComboBox changeBox = new JComboBox(eventNames);
        changeBox.setPreferredSize(new Dimension(280, 25));
        changeBox.setBounds(0, 0, 280, 35);
        if (e != null){
            for (int i = 1; i < eventNames.length; i++) {
                if (eventNames[i].equals(e.name)) {
                    changeBox.setSelectedIndex(i);
                    changeBox.setToolTipText(changeBox.getSelectedItem().toString());
                }
            }
        }else {
            changeBox.setSelectedIndex(0);
        }

        changeBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!changeBox.getSelectedItem().equals("None")) {
                    for (JComboBox other : eventChangeBoxes){
                        if (other != changeBox && other.getSelectedItem().equals(changeBox.getSelectedItem())){
                            changeBox.setSelectedIndex(0);
                        }
                    }
                }
                changeBox.setToolTipText(changeBox.getSelectedItem().toString());
                saveSelection();
            }
        });
        JPanel upper = new JPanel();
        upper. setBackground(Color.BLACK);
        upper.add(changeBox);
        change.add(upper);
        eventChangeBoxes.add(changeBox);

        JPanel lower = new JPanel();
        lower.setBackground(Color.BLACK);
        lower.add(new JLabel("to"));
        lower.setForeground(Color.CYAN);


        String[] toBool = new String[3];
        toBool[0] = "true";
        toBool[1] = "false";
        toBool[2] = "swap";

        JComboBox boolBox = new JComboBox<>(toBool);

        if (e != null) {
            for (int i = 0; i < eventChangeBoxes.size(); i++) {
                if (eventChangeBoxes.get(i).getSelectedItem().equals(e.name)) {
                    boolBox.setSelectedIndex(l.boolsApplied.get(i).ordinal());
                    break;
                }
            }
        }
        boolBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSelection();
            }
        });
        lower.add(boolBox);
        change.add(lower);
        boolChangeBoxes.add(boolBox);
        // add delete button to remove these comboBoxes being changed
        cGridLOM.setRows(cGridLOM.getRows()+1);
        changes.add(change);
        changes.add(addChangeButton);
        boundary.updateUI();
    }
    private void saveSelection(){
        ArrayList<StoryEvent> selection = new ArrayList<>();
        for (JComboBox combo : eventChangeBoxes){
            for (StoryEvent event : Writer.events){
                if(event.name.equals(combo.getSelectedItem().toString())){
                    selection.add(event);
                    break;
                }
            }
        }

        ArrayList<StoryEvent.boolChange> boolArray = new ArrayList<>();
        for (JComboBox bbox : boolChangeBoxes) {
            String s = bbox.getSelectedItem().toString();
            switch (s) {
                case ("true"):
                    boolArray.add(StoryEvent.boolChange.TRUE);
                    break;
                case ("false"):
                    boolArray.add(StoryEvent.boolChange.FALSE);
                    break;
                case ("swap"):
                    boolArray.add(StoryEvent.boolChange.SWAP);
                    break;
            }
        }
        l.setChangesEvents(selection, boolArray);

    }
}
