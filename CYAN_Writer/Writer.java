package CYAN_Writer;

import CYAN_Mutual.*;
import CYAN_Mutual.StoryEvent;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;

public class Writer {
    final static int OVERVIEW_WIDTH = 1000;
    final static int OVERVIEW_HEIGHT = 525;
    public static JFrame frame;
    public static JPanel boundary;
    public static Overview oview;
    public static JScrollPane scrollPane;
    public static PlayerEditor playerWindow;
    public static EventEditor eventEditor;
    public static Player player;
    public static ArrayList<Item> items;
    public static ArrayList<StoryEvent> events;
    public static GlobalConfig global;
    public static String workingDir;
    public static ArrayList<Situation> situations;

    public static void initialise(){
        player = new Player();
        items = new ArrayList<>();
        events = new ArrayList<>();
        situations = new ArrayList<>();
        global = new GlobalConfig();
    }
    public static void updateSitEditors(){
        for (Situation sit: situations){
            sit.updateSitEdit();
        }
    }

    public static int unusedSNum(){
        int s = 1;
        boolean unique = false;
        while (!unique) {
            for (Situation sit : Writer.situations) {
                if (s == sit.number){
                    s++;
                    break;
                }
                else if(Writer.situations.get(Writer.situations.size()-1) == sit){
                    unique = true;
                }
            }
        }
        return s;
    }

    public static int unusedEventNum(){
        int s = 1;
        boolean unique = false;
        ArrayList<Integer> nums = new ArrayList<>();
        for (StoryEvent event : Writer.events){
            nums.add(event.num);
        }
        while (!unique) {
            if (nums.contains(s)){
                s++;
            }
            else{
                unique = true;
            }
            /*
            for (StoryEvent event : Writer.events) {
                if (s == event.num){
                    unique = false;
                    s++;
                    break;
                }
            }*/
        }
        System.out.println(s);
        return s;
    }
    public static boolean hasLinksTo(Situation sit){
        for (Situation otherSit : situations){
            if (otherSit.layer == sit.layer ||  otherSit.layer == sit.layer - 1 || otherSit.layer == sit.layer +1){
                for (Link link : otherSit.getLinks()){
                    if (link.getTo() == sit){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public static void main(String[] args) {
        frame = new JFrame("CYAN Writer");
        frame.setSize(OVERVIEW_WIDTH, OVERVIEW_HEIGHT);
        frame.setLocationRelativeTo(null);

        frame.setLayout(new BorderLayout(0, 0));

        boundary = new JPanel();
        boundary.setBounds(0, 0, OVERVIEW_WIDTH, OVERVIEW_HEIGHT);
        boundary.setLayout(new BorderLayout(0, 30));
        initialise();

        MenuPanel menu = new MenuPanel();
        frame.add(menu, BorderLayout.NORTH);

        oview = new Overview();
        boundary.add(oview, BorderLayout.CENTER);
        scrollPane = new JScrollPane(boundary);
        scrollPane.setPreferredSize(new Dimension(frame.getWidth(), frame.getHeight()));

        scrollPane.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors(){
                this.thumbColor = Color.DARK_GRAY;
                this.thumbDarkShadowColor = Color.BLACK;
                this.thumbHighlightColor = Color.GRAY;
                this.scrollbar.setBackground(Color.GRAY);
            }
        });

        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors(){
                this.thumbColor = Color.DARK_GRAY;
                this.thumbDarkShadowColor = Color.BLACK;
                this.thumbHighlightColor = Color.GRAY;
                this.scrollbar.setBackground(Color.GRAY);
            }
        });

        frame.add(scrollPane, BorderLayout.CENTER);
        boundary.updateUI();

        // Make first Situation
        //situations.add(new Situation(1, 1));
        //oview.drawSitBoxes();

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                for (Situation sit : situations) {
                    sit.closeEditor();
                }
                if (eventEditor != null) {
                    eventEditor.dispose();
                }

                if (situations.size() != 0) {
                    JFrame warning = new JFrame("Warning!");
                    warning.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    warning.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            super.windowClosed(e);
                            Main.main(null);
                        }
                    });
                    warning.setSize(380, 200);
                    JPanel content = new JPanel();
                    content.setLayout(new BorderLayout());

                    JLabel saveFirst = new JLabel("<HTML>Do you want to save before quitting? Any unsaved progress will be lost.</HTML>");
                    content.add(saveFirst, BorderLayout.CENTER);
                    JButton save = new JButton("Save");
                    save.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (menu.save()) {
                                warning.dispose();
                                initialise();
                                Main.main(null);
                            }
                        }
                    });
                    content.add(save, BorderLayout.SOUTH);
                    warning.add(content);
                    warning.setVisible(true);
                }else{
                    System.out.println("opening Main");
                    Main.main(null);}
            }
        });

        frame.setVisible(true);
    }
}
