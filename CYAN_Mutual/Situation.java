package CYAN_Mutual;

import CYAN_Writer.Writer;
import CYAN_Writer.SituationEditor;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.JTextPane;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Situation {
    public int number;
    public int layer;
    public String title;
    public String imageName;
    public String desc; // deprecated, only here to provide support for previous versions of stories, w
    public String[] descriptions; //description paragraphs. make this an array later with optional checks for each paragraph
    private ArrayList<Link> links;
    private transient JButton button;
    private transient JLabel jumps;
    private transient SituationEditor editor;
    public transient ArrayList<Situation> jumpsFrom;


    public Situation(int number, int layer) {
        this.number = number;
        this.layer = layer;
        this.title = "";
        this.descriptions = new String[2];
        this.links = new ArrayList<>();
        initialise();
    }
    public void initialise(){
        jumpsFrom = new ArrayList<>();
        if (desc != null && !desc.equals("")){
            descriptions = new String[2];
            descriptions[0] = desc;
            desc = null;
        }
        makeButton();
    }
    public void makeButton(){
        button = new JButton();
        button.setPreferredSize(new Dimension(110, 60));
        button.setMargin(new Insets(5, 0, 5, 0));
        button.setForeground(Color.CYAN);
        button.setBackground(Color.DARK_GRAY);
        setButtonText();
        final Situation thisSit = this;
        this.button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("S" + number + " clicked");
                openSitEditor();
                }
        });
    }
    public JButton getButton() {
        return button;
    }
    public JLabel getJumpsTab(){return jumps;}

    public void setButtonText() {
        int boxLineLength = 9;
        String buttonText = "<HTML>";
        if (title != null) {
            int titleLength = title.length();
            if (titleLength <= boxLineLength) {
                buttonText += title;
            } else if (titleLength > boxLineLength) {
                buttonText += title.substring(0, boxLineLength);
                if (title.charAt(boxLineLength-1) != ' ' && title.charAt(boxLineLength) != ' ') {
                    buttonText += '-';
               }
                buttonText += "<br>";
            }
            int twoLines = boxLineLength*2;
            if (titleLength > boxLineLength && titleLength <= twoLines) {
                buttonText += title.substring(boxLineLength, titleLength);
            } else if (titleLength > twoLines) {
                buttonText += title.substring(boxLineLength, twoLines-1) + "...";
            }

            if (titleLength != 0) {
                buttonText += "<br>";
            }
        }
        buttonText += "S" + number + "</HTML>";
        button.setText(buttonText);
        if (title != "" && title != null) {
            button.setToolTipText(title);
        }
    }
    public void openSitEditor(){
        if (editor != null){
            System.out.println("found");
            editor.toFront();
        }
        else{
            editor = new SituationEditor(this);
        }
    }
    public ArrayList<Link> getLinks(){
        return links;
    }
    public void removeLink(Link l){
        links.remove(l);
    }
    public void addLink(Link l){
        links.add(l);
    }
    public void closeEditor(){
        if (editor != null) {
            editor.dispose();
            editor = null;
        }
    }
    public void updateSitEdit(){
        if ( editor != null) {
            editor.updateOptions();
        }
    }
    public void jumpInfoBox() {
        boolean anyJumpsTo = false;
        for (Link l : links) {
            Situation to = l.getTo();
            if (to != null && (to.layer < layer - 1 || to.layer > layer + 1)) {
                anyJumpsTo = true;
                break;
            }
        }

        if (jumpsFrom.size() != 0 || anyJumpsTo) {

            final JWindow jumpDisplay = new JWindow();
            Point f = button.getLocation();
            if (jumps != null){
                Writer.oview.remove(jumps);}
            jumps = new JLabel("Jumps");
            jumps.setBounds(f.x + 3, f.y - 15, 45, 15);
            jumps.setFont(new Font("arial", Font.PLAIN, 14));
            jumps.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    String jumps = "";
                    String jumpsTo = "";
                    String jumpsFr = "";
                    for (Link l : links) {
                        Situation sTo = l.getTo();
                        if (sTo != null) {
                            if (sTo.layer < layer - 1 || sTo.layer > layer + 1) {
                                if (jumpsTo.length() == 0) {
                                    jumpsTo += "Jumps To: S" + sTo.number;
                                } else {
                                    jumpsTo += ", S" + sTo.number;
                                }
                            }
                        }
                    }
                    for (Situation jf : jumpsFrom) {
                        if (jumpsFr.length() == 0) {
                            jumpsFr += "Jumps From: S" + jf.number;
                        } else {
                            jumpsFr += ", S" + jf.number;
                        }
                    }

                    if (jumpsTo.length() != 0 && jumpsFr.length() != 0) {
                        jumps += jumpsTo + "\n" + jumpsFr;
                    } else {
                        jumps += jumpsTo + jumpsFr;
                    }
                    JTextPane jumpsText = new JTextPane();
                    jumpsText.setText(jumps);
                    jumpsText.setSize(100, 20 + (int) (jumps.length() * 0.7));

                    jumpDisplay.getContentPane().add(jumpsText);
                    jumpDisplay.setSize(200, jumpsText.getHeight());
                    jumpDisplay.setLocationRelativeTo(button);
                    jumpDisplay.setLocation(jumpDisplay.getX(), jumpDisplay.getY() + jumpsText.getHeight() / 2 - 26);
                    jumpDisplay.setVisible(true);

                }

                @Override
                public void mouseExited(MouseEvent e) {
                    jumpDisplay.dispose();
                }
            });
            Writer.oview.add(jumps);
        }else {
            if (jumps != null) {
                Writer.oview.remove(jumps);
                jumps = null;
            }
        }
    }
}
