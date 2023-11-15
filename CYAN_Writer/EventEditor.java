package CYAN_Writer;

import CYAN_Mutual.StoryEvent;
import CYAN_Mutual.Link;
import CYAN_Mutual.Situation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EventEditor extends JFrame{

    private JPanel boundary;
    private JPanel eventsGrid;
    EventEditor(){
        setSize(600, 600);

        boundary = new JPanel();
        boundary.setLayout(new FlowLayout());
        boundary.setBackground(Color.DARK_GRAY);
        boundary.setPreferredSize(new Dimension(500, 250));

        eventsGrid = new JPanel();
        eventsGrid.setBackground(Color.DARK_GRAY);

        boundary.add(eventsGrid);

        JScrollPane js = new JScrollPane(boundary);
        js.setPreferredSize(new Dimension(getWidth(), getHeight()));
        js.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        js.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(js);

        displayEventsList();

        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                Writer.eventEditor = null;
            }
        });
    }
    public void displayEventsList(){
        System.out.println("Displaying events list");
        eventsGrid.removeAll();
        GridLayout grid = new GridLayout(Writer.events.size() +2, 1);
        grid.setHgap(10);
        eventsGrid.setLayout(grid);
        eventsGrid.setBounds(0, 0, getWidth(), (Writer.events.size() + 2) * 30);
        eventsGrid.setPreferredSize(new Dimension(getWidth(), (Writer.events.size() + 2) * 40));
        //headers
        JPanel header = new JPanel();
        JLabel nameH = new JLabel("Name");
        nameH.setPreferredSize(new Dimension(200, 30));
        JLabel initH = new JLabel("<html>Initially True?</html>");
        initH.setPreferredSize(new Dimension(50, 40));
        JLabel typeH = new JLabel("Type");
        header.add(nameH);
        header.add(initH);
        header.add(typeH);

        eventsGrid.add(header);

        for (StoryEvent event: Writer.events){
            eventsGrid.add(makeEventRow(event));
        }

        JButton addEvent = new JButton("Add new StoryEvent");
        addEvent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StoryEvent event = new StoryEvent("StoryEvent " + (Writer.events.size() + 1));
                Writer.events.add(event);

                eventsGrid.remove(addEvent);
                grid.setRows(grid.getRows() + 1);
                eventsGrid.add(makeEventRow(event));
                eventsGrid.add(addEvent);
                eventsGrid.setPreferredSize(new Dimension(getWidth(), (Writer.events.size() + 2) * 40));
                boundary.setPreferredSize(eventsGrid.getPreferredSize());
                eventsGrid.updateUI();
                boundary.updateUI();
                //displayEventsList();
            }
        });
        eventsGrid.add(addEvent);
        boundary.setPreferredSize(eventsGrid.getPreferredSize());
        boundary.updateUI();
    }
    private JPanel makeEventRow(StoryEvent event){
        JPanel eventRow = new JPanel();
        //todo make it so that no 2 events can have the same name, both in GUI and in StoryEvent.name variable
        JTextField name = new JTextField(event.name, 30);
        name.setBounds(0,0, 200, 30);
        name.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                event.name = name.getText();
            }
        });
        eventRow.add(name);

        JCheckBox start = new JCheckBox();
        start.setLocation(90, 0);
        if(event.type == StoryEvent.types.SIMPLE) {
            start.setSelected(event.isTrue());
        }
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                event.setIsTrue(start.isSelected());
            }
        });
        eventRow.add(start);
        if(event.type != StoryEvent.types.SIMPLE){start.setEnabled(false);}

        String[] types = new String[3];
        types[0] = "Simple";
        types[1] = "Sequence";
        types[2] = "Player";

        JComboBox type = new JComboBox<>(types);
        type.setSelectedIndex(event.type.ordinal());
        type.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = (String)type.getSelectedItem();
                switch (s){
                    case ("Simple"):
                        event.type = StoryEvent.types.SIMPLE;
                        displayEventsList();
                        break;
                    case ("Sequence"):
                        event.type = StoryEvent.types.SEQUENCE;
                        displayEventsList();
                        break;
                    case ("Player"):
                        event.type = StoryEvent.types.PLAYER;
                        displayEventsList();
                        break;
                }
            }
        });
        eventRow.add(type);

        if (event.type == StoryEvent.types.SEQUENCE) {
            final JButton sequence = new JButton("Sequence...");
            if (Writer.events.size() < 3){sequence.setEnabled(false);}
            sequence.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SequenceEditor seq = new SequenceEditor(event);
                    seq.setLocationRelativeTo(sequence);
                }
            });
            eventRow.add(sequence);
        }

        JButton delete = new JButton("X");
        delete.setBackground(Color.RED);
        delete.setPreferredSize(new Dimension(30,30));
        delete.setMargin(new Insets(0, 0, 0,0));
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // todo "Are you sure?" message followed by any links and descriptions that use this event
                Writer.events.remove(event);
                for (Situation sit : Writer.situations){
                    for (Link l : sit.getLinks()){
                        if (l.getCondition() == event){
                            l.setCondition(null);
                        }
                        for (int i = 0; i < l.getChangesEvents().size(); i++) {
                            if (l.getChangesEvents().get(i) == event) {
                                l.removeChangeEvent(i);
                            }
                        }
                    }
                }
                displayEventsList();
            }
        });
        eventRow.add(delete);

        return eventRow;
    }
}
