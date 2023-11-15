package CYAN_Writer;

import CYAN_Mutual.Link;
import CYAN_Mutual.Situation;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class Overview extends JPanel implements Scrollable{



    Overview() {
        setLayout(null);
        setPreferredSize(new Dimension(Writer.OVERVIEW_WIDTH, Writer.OVERVIEW_HEIGHT));
        setBackground(Color.gray);
    }

    public void drawSitBoxes() {
        removeAll();
        ArrayList<Situation> mainSits = Writer.situations;
        //get the number of layers
        int numOfLayers = 0;
        for (Situation sit : mainSits) {
            if (sit.layer > numOfLayers) {
                numOfLayers = sit.layer;
            }
        }
        int preferredXSize = Writer.OVERVIEW_WIDTH + (200 * (numOfLayers-4)); //+200
        int[] stackHeights = new int[numOfLayers];
        for (int l = 0; l < numOfLayers; l++){
            for (Situation sit : Writer.situations){
                if (sit.layer == l+1){
                    stackHeights[l] += 1; // need to assign 0 first time?
                }
            }
        }
        // get highest from stackHeights
        int maxHeight = 1;
        for (int h = 0; h < stackHeights.length; h++){
            if (stackHeights[h] > maxHeight){
                maxHeight = stackHeights[h];
            }
        }
        int preferredYSize = Writer.OVERVIEW_HEIGHT - 100 + (80 * (maxHeight - 4)); //+70

        setPreferredSize(new Dimension(preferredXSize, preferredYSize));
        if (Writer.scrollPane != null) {
            Writer.scrollPane.updateUI();
        }
        ArrayList<Point> layers = new ArrayList<>();
        int x = 100;
        for(int i = 0; i <= numOfLayers; i++) {
            layers.add(new Point(x, 50));
            if (i < numOfLayers -1) {
                x += 200;
            } else {
                x += 166;
            }
        }

        //add a button that adds another layer in between two layers if there are no links in between those layers
        for (int l = 0; l < layers.size()-2; l++){
            JButton addLayer= new JButton("+");
            addLayer.setForeground(Color.CYAN);
            addLayer.setBackground(Color.DARK_GRAY);
            addLayer.setFont(new Font("arial", Font.BOLD, 20));
            addLayer.setMargin(new Insets(0, 0, 0, 0));
            addLayer.setBounds(layers.get(l).x +140, 15,25,25);

            final int layer = l+1;
            boolean blocked = false;
            for (Situation sit : mainSits){
                if (sit.layer == layer) {
                    for (Link link : sit.getLinks()) {
                        if (link.getFrom() != null && link.getFrom().layer == layer && link.getTo() != null && link.getTo().layer == layer+1){
                            blocked = true;
                            break;
                        }
                    }
                }else if (sit.layer == layer+1){
                    for (Link link : sit.getLinks()) {
                        if (link.getFrom() != null && link.getFrom().layer == layer +1 && link.getTo() != null && link.getTo().layer == layer){
                            blocked = true;
                        }
                    }
                }
                if (blocked){break;}
            }
            if (blocked){
                addLayer.setEnabled(false);
                addLayer.setToolTipText("You must remove the links between these two layers in order to add a new layer between them.");
            }

            addLayer.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    for (int i = 0; i < mainSits.size(); i ++){
                        Situation sit = mainSits.get(i);
                        if (sit.layer > layer){
                            sit.layer++;
                        }
                    }
                    mainSits.add(new Situation(Writer.unusedSNum(), layer+1));
                    drawSitBoxes();
                    Writer.updateSitEditors();
                }
            });
            add(addLayer);
        }

        int ySpacing = 80;
        // add situation buttons to co-ordinates relating to their horizontal position, incrementing vertical position of that column each time.
        for (Situation sit : mainSits){
            JButton button = sit.getButton();
            Point p = layers.get(sit.layer-1);
            button.setBounds(p.x, p.y, 110, 60);
            p.y += ySpacing;

            add(button);

            sit.jumpInfoBox();

        }

        // add '+' buttons to the bottom using incremented co-ordinates. + one more increment.
        for (int i = 0; i < layers.size(); i++){
            Point end = layers.get(i);
            JButton addSituation = new JButton("+");
            addSituation.setForeground(Color.CYAN);
            addSituation.setBackground(Color.DARK_GRAY);
            addSituation.setFont(new Font("arial", Font.BOLD, 20));
            addSituation.setMargin(new Insets(0, 0, 0, 0));
            addSituation.setBounds(end.x +33, end.y,45,35);
            final int thisLayer = i;
            addSituation.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Writer.situations.add(new Situation(Writer.unusedSNum(), thisLayer+1));
                    drawSitBoxes();
                    Writer.updateSitEditors();
                }
            });
            add(addSituation);
        }
        repaint();
    }
    public void paintComponent(Graphics g) {
        // Key is layer and value is how many links between situations on that layer
        Map<Integer, Integer> interLayer = new HashMap<>();
        super.paintComponent(g);
        if (Writer.situations != null){
            ArrayList<Situation> mainSits = Writer.situations;
            ArrayList<Link> links = new ArrayList<>();
            for (Situation sit : mainSits) {
                links.addAll(sit.getLinks());
            }

            for (int l = 0; l < links.size(); l++) {
                Link link = links.get(l);
                if (link.getTo() != null) {
                    Situation from = link.getFrom();
                    Situation to = link.getTo();

                    Point f = from.getButton().getLocation();
                    //System.out.println("From location: " + f.x + ", " + f.y);
                    Point t = to.getButton().getLocation();
                    //System.out.println("To location: " + t.x + ", " + t.y);

                    if (to.layer - from.layer == 1 || from.layer - to.layer == 1) {
                        if (from.layer < to.layer) {
                            g.setColor(Color.CYAN);
                            g.drawLine(f.x + 110, f.y + 25, t.x, t.y + 25);
                        } else if (from.layer > to.layer) {
                            g.setColor(Color.RED);
                            g.drawLine(f.x, f.y + 35, t.x + 110, t.y + 35);
                        }
                    } else if (to.layer == from.layer) {
                        int layer = link.getFrom().layer;
                        // store how many times this layer has linked to another on this layer
                        if (interLayer.containsKey(layer)) {
                            interLayer.put(layer, interLayer.get(layer) + 1);
                        } else {
                            interLayer.put(layer, 1);
                        }

                        ArrayList<Integer> sitLayer = new ArrayList<>();
                        for (Situation sit : mainSits) {
                            if (sit.layer == from.layer) {
                                sitLayer.add(sit.number);
                            }
                        }

                        if (sitLayer.indexOf(from.number) < sitLayer.indexOf(to.number)) {
                            g.setColor(new Color(10, 10, 200));
                        } else {
                            g.setColor(new Color(255, 150, 0));
                        }

                        int otherLinks = interLayer.get(layer);
                        int xGap = 2;
                        int yGap = 2;
                        if (interLayer.get(layer) % 2 != 0) {
                            g.drawLine(f.x + 110, f.y + (yGap * otherLinks), f.x + 112 + (xGap * otherLinks), f.y + (yGap * otherLinks)); //out
                            g.drawLine(f.x + 112 + (xGap * otherLinks), f.y + (yGap * otherLinks), t.x + 112 + (xGap * otherLinks), t.y + (yGap * otherLinks)); //line
                            g.drawLine(t.x + 112 + (xGap * otherLinks), t.y + (yGap * otherLinks), t.x + 110, t.y + (yGap * otherLinks)); // in
                        } else if (interLayer.get(layer) % 2 == 0) {
                            g.drawLine(f.x, f.y + (yGap * otherLinks - 1), f.x - xGap - (xGap * (otherLinks - 1)), f.y + (yGap * otherLinks - 1)); //out
                            g.drawLine(f.x - xGap - (xGap * (otherLinks - 1)), f.y + (yGap * otherLinks - 1), t.x - xGap - (xGap * (otherLinks - 1)), t.y + (yGap * otherLinks - 1)); //line
                            g.drawLine(t.x, t.y + (yGap * otherLinks - 1), t.x - xGap - (xGap * (otherLinks - 1)), t.y + (yGap * otherLinks - 1)); // in
                        }
                    } else {
                        g.setColor(Color.cyan);
                        g.fillRect(f.x, f.y - 15, 45, 15);
                        g.fillRect(t.x, t.y - 15, 45, 15);

                        add(new JPanel() {
                            @Override
                            public void setLocation(Point p) {
                                super.setLocation(f.x, f.y - 15);
                            }

                            @Override
                            public void setSize(int width, int height) {
                                super.setSize(45, 15);
                            }
                        });
                    }
                }
            }
            super.repaint();
        }
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return true;
    }

}