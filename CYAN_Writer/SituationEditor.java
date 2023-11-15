package CYAN_Writer;

import CYAN_Mutual.Link;
import CYAN_Mutual.Situation;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.ToolTipUI;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static java.nio.file.Files.copy;

public class SituationEditor extends JFrame{
    private JPanel boundary;
    private Situation situation;

    private JPanel imgPanel;
    private Image myPicture;
    private JTextArea initDescBox;
    private JTextArea altDescBox;

    private JPanel choiceGrid;
    private JButton addNewChoice;
    private JButton delete;

    public SituationEditor(Situation situation) {
        this.situation = situation;
        setTitle("CYAN Writer - Editor" + " (Situation " + situation.number + ")");

        addWindowListener(new WindowAdapter() {
           public void windowClosing(WindowEvent e) {
                situation.closeEditor();
            }
        });

        setSize(500, 400);
        setLocationRelativeTo(situation.getButton());
        setVisible(true);
        makePanel();
        titleBox();
        imgImport();
        initDescPanel();
        altDescPanel();
        makeChoiceGrid();
        setPrefHeight();
    }

    private void makePanel() {
        boundary = new JPanel();
        boundary.setLayout(new FlowLayout());
        boundary.setBackground(Color.DARK_GRAY);
        boundary.setPreferredSize(new Dimension(500, 250));
        JScrollPane js = new JScrollPane(boundary);
        js.setPreferredSize(new Dimension(getWidth(), getHeight()));
        js.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        js.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(js);
    }

    private void titleBox() {
        // Title field and label
        JPanel top = new JPanel();
        top.setBackground(Color.DARK_GRAY);
        top.setBounds(0, 0, 500, 50);

        JTextField titleBox = new JTextField(situation.title, 30);
        titleBox.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);

                situation.title = titleBox.getText();
                situation.setButtonText();

                if (titleBox.getText().length() == 0) {
                    situation.getButton().setToolTipText(null);
                }
            }
        });

        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setForeground(Color.CYAN);
        top.add(titleLabel);
        top.add(titleBox);
        boundary.add(top);
    }
    private void imgImport(){
        String assetsPath = Writer.workingDir + System.getProperty("file.separator") + "assets" + System.getProperty("file.separator");
        imgPanel = new JPanel(){
            JPanel thisPanel = this;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                thisPanel.setPreferredSize(new Dimension(500, 100));
                g.drawImage(myPicture, 0, 0, this);
            }
        };
        imgPanel.setBackground(Color.DARK_GRAY);
        if (situation.imageName != null){
            try {
                myPicture = ImageIO.read(new File(assetsPath + situation.imageName)).getScaledInstance(-1,50, Image.SCALE_FAST);
                JLabel img = new JLabel(situation.imageName);
                img.setForeground(Color.CYAN);
                boundary.add(img);
                boundary.updateUI();
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }


        JButton addImg = new JButton("Add Image");
        addImg.setBackground(Color.black);
        addImg.setForeground(Color.CYAN);
        FileFilter img = new FileFilter() {
            private final String[] imgExts = new String[] { ".jpg", ".jpeg", ".png", ".gif" };
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()){return true;}
                for (String ext : imgExts) {
                    if (f.getName().toLowerCase().endsWith(ext)) {
                        return true;
                    }
                }
                return false;
            }
            @Override
            public String getDescription() {
                return ".jpg\", \".jpeg\", \".png\", \".gif";
            }
        };

        final JFileChooser fcImg = new JFileChooser();
        final File[] loadImg = new File[1];
        fcImg.setFileFilter(img);

        addImg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                fcImg.setCurrentDirectory(new File(assetsPath));
                int loadReturn = fcImg.showOpenDialog(addImg);
                if (loadReturn == 0) {
                    loadImg[0] = fcImg.getSelectedFile();
                    if (!loadImg[0].getParent().equals(assetsPath.substring(0, assetsPath.lastIndexOf(System.getProperty("file.separator"))))){
                        try {
                            System.out.println(loadImg[0].getAbsolutePath());
                            System.out.println(Path.of(loadImg[0].getAbsolutePath()));
                            copy(Paths.get(loadImg[0].getAbsolutePath()), Paths.get(assetsPath + loadImg[0].getName()), StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                    String imgPath = assetsPath + loadImg[0].getName();
                    situation.imageName = loadImg[0].getName();
                    try {
                        myPicture = ImageIO.read(new File(imgPath)).getScaledInstance(-1,50, Image.SCALE_FAST);
                        JLabel filename = new JLabel(loadImg[0].getName());
                        filename.setForeground(Color.CYAN);
                        imgPanel.add(filename);
                        boundary.updateUI();
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                }
            }
        });
        JButton deleteImg = new JButton("X");
        deleteImg.setBackground(Color.RED);
        deleteImg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                situation.imageName = null;
                boundary.updateUI();
            }
        });

        imgPanel.add(addImg);
        if (situation.imageName != null){
            imgPanel.add(deleteImg);
        }
        boundary.add(imgPanel);
    }
    private JTextArea descBox(int i){
        final JTextArea descBox = new JTextArea(situation.descriptions[i], 3, 30);
        descBox.setLineWrap(true);

        descBox.setToolTipText("<HTML>Use HTML tags to format this text, For Example:<br> " +
                "<plaintext><span style=\"color:red;\">This will display in red text</span> </plaintext><br>" +
                "<plaintext><h1>Big Text</h1></plaintext><br> <plaintext><b>Bold Text</b> etc.</plaintext> </HTML>");

        descBox.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setPrefHeight();
            }
        });
        descBox.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                setPrefHeight();
            }
            @Override
            public void keyPressed(KeyEvent e) {
                setPrefHeight();
            }
            @Override
            public void keyReleased(KeyEvent e) {
                setPrefHeight();
            }
        });
        descBox.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                situation.descriptions[i] = descBox.getText();
                setPrefHeight();
            }
        });
        return descBox;
    }
    private void initDescPanel() {
        JPanel descPanel = new JPanel();
        descPanel.setBackground(Color.DARK_GRAY);
        descPanel.setBounds(0, 150, 500, 200);
        descPanel.setLayout(new FlowLayout());

        initDescBox = descBox(0);

        JLabel descLabel = new JLabel("Description: ");
        descLabel.setForeground(Color.CYAN);
        descPanel.add(descLabel);
        descPanel.add(initDescBox);
        boundary.add(descPanel);
    }
    private void altDescPanel() {
        JPanel descPanel = new JPanel();
        descPanel.setBackground(Color.DARK_GRAY);
        descPanel.setBounds(0, 350, 500, 100);
        descPanel.setLayout(new FlowLayout());

        altDescBox = descBox(1);


        JLabel descLabel = new JLabel(" Alt Description: ");
        descLabel.setForeground(Color.CYAN);
        descLabel.setToolTipText("This description will be displayed instead of the above description on subsequent visits to this situation.");
        descPanel.add(descLabel);
        descPanel.add(altDescBox);
        boundary.add(descPanel);
    }

    private void makeChoiceGrid(){
        choiceGrid = new JPanel();
        choiceGrid.setBackground(Color.DARK_GRAY);
        int linkSize = situation.getLinks().size();
        GridLayout grid = new GridLayout(situation.getLinks().size()+1, 1);
        choiceGrid.setLayout(grid);
        choiceGrid.setBounds(0, 250, 300, 100 + 34 * linkSize);
        choiceGrid.setPreferredSize(new Dimension(this.getWidth(), 50 + 34 * linkSize));

        for (Link l : situation.getLinks()) {
            JPanel row = new JPanel();
            row.setBackground(Color.DARK_GRAY);
            row.setPreferredSize(new Dimension(490, 30));
            Link link = l;
            link.makeRow();
            row.add(link.textBox);
            row.add(link.toSelect);
            row.add(link.events);
            row.add(link.delete);

            choiceGrid.add(row);
        }
        boundary.add(choiceGrid);

      addNewChoice = new JButton("Add New Choice");
      addNewChoice.setBackground(Color.BLACK);
      addNewChoice.setForeground(Color.CYAN);
        boundary.add(addNewChoice);
        final JFrame thisFrame = this;
        addNewChoice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int linkSize = situation.getLinks().size();
                choiceGrid.setBounds(0, 250, 300, 100 + 34 * linkSize);
                choiceGrid.setPreferredSize(new Dimension(thisFrame.getWidth(), 80 + 34 * linkSize));

                grid.setRows(grid.getRows() + 1);
                Link newLink = new Link(grid.getRows() -1, "Direct", situation);
                situation.addLink(newLink);
                System.out.println("Added new link to S" + situation.number);
                //add new link row
                JPanel row = new JPanel();
                row.setPreferredSize(new Dimension(thisFrame.getWidth(), 30));
                row.add(newLink.textBox);
                row.add(newLink.toSelect);
                row.add(newLink.events);
                row.add(newLink.delete);
                choiceGrid.add(row);
                setPrefHeight();
                choiceGrid.updateUI();
            }
        });

        if (situation.number != 1) {
            delete = new JButton("Delete This Situation");
            delete.setBackground(Color.red);
            setDeletable();
            final SituationEditor thisSitEditor = this;
            delete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFrame warning = new JFrame("Warning!");
                    warning.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                    warning.setSize(380, 200);
                    JPanel content = new JPanel();
                    content.setLayout(new BorderLayout());

                    JLabel sure = new JLabel("<HTML>Are you sure? this will delete the situation: \"S" + situation.number + " " + situation.title + ".\" </HTML>");
                    content.add(sure, BorderLayout.CENTER);

                    JButton confirm = new JButton("Confirm");
                    confirm.setBackground(Color.RED);
                    confirm.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            int layer = situation.layer;
                            Writer.situations.remove(situation);
                            boolean hasLayer = false;
                            for (Situation sit : Writer.situations){
                                if (sit.layer == layer){
                                    hasLayer = true;
                                    break;
                                }
                            }
                            if (hasLayer == false){
                                for (Situation sit : Writer.situations){
                                    if (sit.layer > layer){
                                        sit.layer--;
                                    }
                                }
                            }
                            Writer.oview.drawSitBoxes();
                            Writer.updateSitEditors();
                            warning.dispatchEvent(new WindowEvent(warning, WindowEvent.WINDOW_CLOSING));
                            thisSitEditor.dispatchEvent(new WindowEvent(thisSitEditor, WindowEvent.WINDOW_CLOSING));

                        }
                    });
                    content.add(confirm, BorderLayout.SOUTH);

                    warning.add(content);
                    warning.setVisible(true);
                }
            });
            boundary.add(delete);
        }
        boundary.updateUI();
    }
    public void updateOptions(){
        for (Link link : situation.getLinks()) {
            link.makeRow();
        }
        boundary.remove(choiceGrid);
        boundary.remove(addNewChoice);

        if (situation.number != 1) {
            boundary.remove(delete);
            makeChoiceGrid();
            setDeletable();
        }else{makeChoiceGrid();}
        setPrefHeight();
    }
    private void setDeletable(){
        boolean linksFrom = false;
        for (Link link : situation.getLinks()){
            if (link.getTo() != null){
                linksFrom = true;
            }
        }
        if (Writer.hasLinksTo(situation) || linksFrom){
            delete.setEnabled(false);
            delete.setToolTipText("cannot delete situation while there are links to/from this situation");
        }else {
            delete.setEnabled(true);
            delete.setToolTipText(null);
        }
    }
    public Situation getSituation(){
        return situation;
    }
    private void setPrefHeight(){
        int descHeight = initDescBox.getPreferredSize().height + altDescBox.getPreferredSize().height;
        boundary.setPreferredSize(new Dimension(getSize().width, 50 + imgPanel.getPreferredSize().height + descHeight + choiceGrid.getPreferredSize().height + 100));
    }
}

