package CYAN_Writer;

import CYAN_Mutual.Link;
import CYAN_Mutual.SaveState;
import CYAN_Mutual.Situation;
import CYAN_Mutual.StoryEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class MenuPanel extends JPanel {

    JWindow titleWindow;

    MenuPanel() {
        setBackground(Color.GRAY);
        setBounds(0, 0, 200, 50);
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.DARK_GRAY);

        // add edit menu
        JMenu edit = new JMenu("Edit");
        edit.setForeground(Color.CYAN);
        edit.setBackground(Color.DARK_GRAY);
        edit.setEnabled(false);

        JMenuItem p = new JMenuItem("Player");
        p.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String com = e.getActionCommand();
                System.out.println(com + " Clicked");
                if (Writer.playerWindow != null) {
                    System.out.println("Grabbing focus");
                    Writer.playerWindow.toFront();
                } else {
                    Writer.playerWindow = new PlayerEditor(Writer.player);
                }
            }
        });

        JMenuItem i = new JMenuItem("Items");
        i.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String com = e.getActionCommand();
                System.out.println(com + " Clicked");

            }
        });

        JMenuItem e = new JMenuItem("Events");
        e.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String com = e.getActionCommand();
                System.out.println(com + " Clicked");
                if (Writer.eventEditor != null){
                    Writer.eventEditor.toFront();
                }else {
                    Writer.eventEditor = new EventEditor();
                }
            }
        });

        edit.add(p);
        edit.add(i);
        edit.add(e);

        //add file menu
        JMenu file = new JMenu("File");
        file.setForeground(Color.CYAN);
        file.setBackground(Color.DARK_GRAY);

        FileFilter json = new FileFilter() {
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

        JMenuItem save = new JMenuItem("Save Story");
        save.setEnabled(false);
        save.setBackground(Color.DARK_GRAY);
        save.setForeground(Color.CYAN);
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // todo make confirmation window
                save();
            }
        });

        JMenuItem create = new JMenuItem("New Story...");
        create.setBackground(Color.DARK_GRAY);
        create.setForeground(Color.CYAN);
        create.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                titleWindow = new JWindow(Writer.frame);
                titleWindow.setSize(300, 100);

                JPanel titlePrompt = new JPanel();
                titlePrompt.setBackground(Color.DARK_GRAY);
                titlePrompt.setSize(300, 100);

                JLabel prompt = new JLabel("Story Title (You can change this later):");
                prompt.setSize(250, 55);
                prompt.setForeground(Color.CYAN);
                prompt.setLocation(30, -30);

                JTextField title = new JTextField(20);
                title.setSize(new Dimension(100, 50));
                title.setLocation(30, 30);

                titlePrompt.add(prompt);
                titlePrompt.add(title);
                titleWindow.getContentPane().add(titlePrompt);

                title.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (title.getText().length() != 0) {
                            Writer.initialise();
                            Writer.global.storyTitle = title.getText();
                            titleWindow.dispose();
                            if (createDir(create)){
                                Writer.oview.drawSitBoxes();
                                save.setEnabled(true);
                                edit.setEnabled(true);
                               // Writer.boundary.updateUI();
                                System.out.println("display overview");
                            }
                        }
                    }
                });
                titleWindow.setLocationRelativeTo(null);
                titleWindow.setVisible(true);
                titleWindow.requestFocus();
            }
        });

        JMenuItem load = new JMenuItem("Load Story");
        load.setBackground(Color.DARK_GRAY);
        load.setForeground(Color.CYAN);
        //Create a file chooser
        final JFileChooser fcLoad = new JFileChooser();
        final File[] loadFile = new File[1];
        fcLoad.setFileFilter(json);
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (titleWindow != null) {
                    titleWindow.dispose();
                    titleWindow = null;
                }
                int loadReturn = fcLoad.showOpenDialog(load);
                if (loadReturn == 0) {
                    loadFile[0] = fcLoad.getSelectedFile();

                    String path = loadFile[0].getAbsolutePath();
                    Writer.workingDir = loadFile[0].getParent();
                    System.out.println(loadFile[0].getClass());
                    try {
                        System.out.println("Loading " + path);

                        SaveState StateRead = new Gson().fromJson(new FileReader(path), SaveState.class);
                        Writer.initialise();
                        Writer.global = StateRead.global;
                        Writer.player = StateRead.player;
                        Writer.items = StateRead.items;
                        Writer.events = StateRead.events;
                        Writer.situations = StateRead.situations;
                        for (Situation sit : Writer.situations) {
                            if (sit.jumpsFrom == null) {
                                sit.initialise();
                            }
                            for (StoryEvent event : Writer.events){
                                event.initialise(Writer.events);
                            }
                            for (Link l : sit.getLinks()) {
                                l.getSitFromNum(Writer.situations);
                                l.initialise(Writer.events);
                                Situation to = l.getTo();
                                if (to != null && (to.layer < sit.layer - 1 || to.layer > sit.layer + 1)) {
                                    if (to.jumpsFrom == null) {
                                        to.initialise();
                                    }
                                    to.jumpsFrom.add(sit);
                                }
                            }
                        }
                        System.out.println("Loaded " + path);
                    } catch (Exception e2) {
                        System.out.println("MenuPanel Load Failed: " + e2);
                    }
                    Writer.oview.drawSitBoxes();
                    save.setEnabled(true);
                    edit.setEnabled(true);
                    System.out.println("display Overview");
                }

            }
        });

        file.add(create);
        file.add(save);
        file.add(load);

        menuBar.add(file);
        menuBar.add(edit);

        add(menuBar);
    }

    public boolean createDir(JComponent parent) { //return true if successful, false otherwise
        //Create file chooser
        FileFilter dir = new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                return false;
            }
            @Override
            public String getDescription() {
                return "Directory";
            }
        };
        JFileChooser fcSave = new JFileChooser();
        fcSave.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fcSave.setFileFilter(dir);
        File[] saveFile = new File[1];
        int saveReturn = fcSave.showSaveDialog(parent);
        if (saveReturn == 0) {
            System.out.println("saveReturn == 0");
            saveFile[0] = fcSave.getSelectedFile();
            String path = "";
            if (saveFile[0].isDirectory()) {
                path = saveFile[0].getAbsolutePath() + System.getProperty("file.separator");
            }
            else {
                path = saveFile[0].getParent() + System.getProperty("file.separator");
            }
                System.out.println(path);
                File outer = new File(path + Writer.global.storyTitle);
                int i = 0;
                while (outer.isDirectory()) {
                    i++;
                    outer.renameTo(new File(path + Writer.global.storyTitle + "(" + i + ")"));
                }
                if (outer.mkdir()) {
                    System.out.println("Story directory created");
                    Writer.workingDir = outer.getAbsolutePath();
                    File assets = new File(Writer.workingDir + System.getProperty("file.separator") + "assets");
                    if (assets.mkdir()) {
                        System.out.println("assets directory created");
                        // Make first Situation
                        Writer.situations.add(new Situation(1, 1));
                        if (save()){
                            return true;
                        } else{
                            return false;
                        }
                    } else {
                        System.out.println("save failed (could not make assets directory)");
                        return false;
                    }
                } else {
                    System.out.println("save failed (could not make story directory)");
                    return false;
                }
        }else {
            return false;
        }
    }
    public boolean save(){
        String path = Writer.workingDir + System.getProperty("file.separator") + Writer.global.storyTitle + ".json";
        File newFile = new File(path);
        try {
            newFile.createNewFile();
        } catch (IOException ex) {
            System.out.println("New file failed");
            return false;
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

        try {
            FileWriter file = new FileWriter(path);
            SaveState ss = new SaveState(Writer.global, Writer.player, Writer.items, Writer.events, Writer.situations);
            String json = gson.toJson(ss);
            file.write(json);
            file.close();
        } catch (IOException ex) {
            System.out.println("file not found?");
            return false;
        }
        return true;
    }
}
