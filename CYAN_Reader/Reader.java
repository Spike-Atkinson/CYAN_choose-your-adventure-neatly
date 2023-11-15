package CYAN_Reader;

import CYAN_Mutual.*;
import CYAN_Mutual.StoryEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;


import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Reader {
    static JFrame frame;
    final static int WINDOW_WIDTH = 700;
    final static int WINDOW_HEIGHT = 400;

    static JPanel menu;
    static JButton begin;
    static JButton cont;

    static JPanel boundary;
    static Story story;
    static JScrollPane scrollPane;

    static String storyPath;
    static String savePath;
    static String username; // for save file name

    static Player player;
    static ArrayList<Item> items;
    static ArrayList<StoryEvent> events;
    static ArrayList<Situation> situations;
    static String Log; // show all previous descriptions and choice text


    public static void main(String[] args) {
        frame = new JFrame("CYAN Reader");
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        frame.setLayout(new BorderLayout(0, 0));

        boundary = new JPanel();
        boundary.setBounds(0,0,WINDOW_WIDTH, WINDOW_HEIGHT);
        boundary.setLayout(new BorderLayout(0, 30));

        menu = new JPanel();
        menu.setBackground(Color.GRAY);
        menu.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        begin = new JButton("Begin Story");
        cont = new JButton("Continue Story");

        begin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                begin.setEnabled(false);
                cont.setEnabled(false);
                if (!load(false)) {
                    begin.setEnabled(true);
                    cont.setEnabled(true);
                }
            }
        });

        cont.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                begin.setEnabled(false);
                cont.setEnabled(false);
                if (!load(true)) {
                    begin.setEnabled(true);
                    cont.setEnabled(true);
                }
            }
        });

        menu.add(begin);
        menu.add(cont);

        // save progress on close
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // todo make adjustments for if savePath is not null, it may be easier than having to check for save files
                if (username != null && storyPath != null) {
                    String title = storyPath.substring(storyPath.lastIndexOf(System.getProperty("file.separator")) + 1, storyPath.length() - 5);
                    Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
                    try {
                        final File folder = new File(storyPath.substring(0, storyPath.lastIndexOf(System.getProperty("file.separator"))));
                        System.out.println("folder: " + folder.getAbsolutePath());

                        FileWriter file = new FileWriter(storyPath.substring(0, storyPath.length() - 5) + "(" + username + ")-save" + ".json");
                        PlayerSave save = new PlayerSave(title, username, player, events, story.getCurrent());

                        String json = gson.toJson(save);
                        file.write(json);
                        file.close();
                    } catch (IOException ex) {
                        System.out.println("file not found?");
                    }
                }
                CYAN_Mutual.Main.main(null);
            }
        });


        frame.add(menu);
        frame.setVisible(true);
    }

    private static boolean load(boolean continuing) {
        final JFileChooser fcLoad = new JFileChooser();
        final File[] loadFile = new File[1];
        final FileFilter jsonFilterNoSave = new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else if (f.getName().endsWith(".json") && !(f.getName().endsWith(")-save.json"))) {
                    return true;
                }
                return false;
            }

            @Override
            public String getDescription() {
                return ".json";
            }
        };
        final FileFilter jsonFilterOnlySave = new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else if (f.getName().endsWith(")-save.json")) {
                    return true;
                }
                return false;
            }

            @Override
            public String getDescription() {
                return ".json save";
            }
        };
        if (!continuing) {
            fcLoad.setFileFilter(jsonFilterNoSave);
        } else {
            fcLoad.setFileFilter(jsonFilterOnlySave);
        }
        int loadReturn = fcLoad.showOpenDialog(null);// (begin)
        if (loadReturn == 0) {
            loadFile[0] = fcLoad.getSelectedFile();
            PlayerSave saveData = null;
            SaveState stateRead;
            try {
                if (continuing) {
                    savePath = loadFile[0].getAbsolutePath();
                    System.out.println("savePath: " + savePath);
                    saveData = new Gson().fromJson(new JsonReader(new FileReader(savePath)), PlayerSave.class);
                    storyPath = savePath.substring(0, savePath.lastIndexOf(System.getProperty("file.separator")) + 1) + saveData.storyTitle + ".json";
                    System.out.println("storyPath: " + storyPath);
                    stateRead = new Gson().fromJson(new FileReader(storyPath), SaveState.class);
                    System.out.println("storyPath 2: " + storyPath);
                    player = saveData.player;
                    events = saveData.events;
                    username = saveData.username;
                } else {
                    storyPath = loadFile[0].getAbsolutePath();
                    stateRead = new Gson().fromJson(new JsonReader(new FileReader(storyPath)), SaveState.class);
                    player = stateRead.player;
                    events = stateRead.events;

                }
                // from story .json either way
                items = stateRead.items;
                situations = stateRead.situations;

                for (StoryEvent event : events){
                    event.initialise(events);
                }
                for (Situation sit : situations) {
                    for (Link l : sit.getLinks()) {
                        l.initialise(events);
                        l.getSitFromNum(situations);
                    }
                }
            } catch (Exception e2) {
                System.out.println("Reader 200 Catch: " + e2);
            }
            if (!continuing) {
                //  prompt user for name (for save file name)
                final JWindow userWindow = new JWindow(frame);
                JPanel userPrompt = new JPanel();
                userPrompt.setBackground(Color.DARK_GRAY);
                //userPrompt.setLayout(new CardLayout());
                userWindow.setSize(300, 100);
                userPrompt.setSize(300, 100);
                JLabel prompt = new JLabel("Please enter your name.");
                prompt.setForeground(Color.CYAN);
                prompt.setLocation(30, -30);
                JTextField user = new JTextField("Reader", 20);
                user.setSize(new Dimension(100, 50));
                user.setLocation(30, 30);

                userPrompt.add(prompt);
                userPrompt.add(user);
                userWindow.getContentPane().add(userPrompt);

                user.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
                        Matcher m = p.matcher(user.getText());
                        boolean special = m.find();
                        if (user.getText().length() != 0 && !special) {
                            username = user.getText();
                            System.out.println("username: " + username);
                            menu.setVisible(false);
                            story = new Story();
                            boundary.add(story, BorderLayout.CENTER);
                            scrollPane = new JScrollPane(boundary);
                            scrollPane.setPreferredSize(new Dimension(frame.getWidth(), frame.getHeight()));
                            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

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

                            userWindow.dispose();
                        }
                    }
                });
                userWindow.setLocationRelativeTo(null);
                userWindow.setVisible(true);
                userWindow.requestFocus();
            }else{
                for (StoryEvent event : events){
                    event.initialise(events);
                }
                for (Link l : saveData.currentSituation.getLinks()){
                    l.initialise(events);
                    l.getSitFromNum(situations);
                }
                menu.setVisible(false);

                story = new Story(saveData.currentSituation);
                boundary.add(story, BorderLayout.CENTER);
                scrollPane = new JScrollPane(boundary);
                scrollPane.setPreferredSize(new Dimension(frame.getWidth(), frame.getHeight()));
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

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
            }
                return true;
            } else {
                return false;
            }
        }
    }

