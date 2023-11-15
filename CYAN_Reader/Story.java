package CYAN_Reader;

import CYAN_Mutual.Link;
import CYAN_Mutual.Situation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;

public class Story extends JPanel implements Scrollable {
    private Situation currentSituation;

    private JPanel imgPanel;
    private Image myPicture;

    Story (){
        this.currentSituation = Reader.situations.get(0);
        showSituation();
    }
    Story (Situation sit){
        this.currentSituation = sit;
        showSituation();
    }

    void showSituation(){
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.darkGray);
        setSize(new Dimension(Reader.WINDOW_WIDTH-20, 800));
        int xMargin = 0;
        int yMargin = 0;
        JLabel title = new JLabel(currentSituation.title);
        title.setForeground(Color.CYAN);
        title.setBounds(xMargin, yMargin, 350, 30);
        //title.setSize(400, 30);
        //title.setLocation(xMargin, yMargin);
        add(title);
        add(new JLabel(" "));

        imgPanel = new JPanel();
        if (currentSituation.imageName != null) {
            String imgPath = new File(Reader.storyPath).getParent() + System.getProperty("file.separator") + "assets" + System.getProperty("file.separator") + currentSituation.imageName;
            imgPanel = new JPanel() {

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    int width = getWidth() -10;
                    int height = getHeight() -10;

                    // Calculate the scaled width and height while preserving the aspect ratio
                    int scaledWidth = width;
                    int scaledHeight = (int) (((double) width / myPicture.getWidth(null)) * myPicture.getHeight(null));

                    if (scaledHeight > height) {
                        scaledHeight = height;
                        scaledWidth = (int) (((double) height / myPicture.getHeight(null)) * myPicture.getWidth(null));
                    }

                    // Draw the scaled image
                    Image scaledImage = myPicture.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                    int x = (width - scaledWidth) / 2;
                    int y = (height - scaledHeight) / 2;
                    g.drawImage(scaledImage, x, y, null);

                    // Update the preferred size of the imgPanel
                    imgPanel.setPreferredSize(new Dimension(scaledWidth, scaledHeight));

                }
            };
        imgPanel.setBackground(Color.darkGray);
            /* todo make the image shrink when frame is made smaller
            // Add a ComponentListener to imgPanel
            imgPanel.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    imgPanel.repaint(); // Redraw the image when the panel's size changes
                }
            }); */

        imgPanel.setPreferredSize(new Dimension(getWidth(), getHeight()));
            try {
                myPicture = ImageIO.read(new File(imgPath)).getScaledInstance(-1, -1, Image.SCALE_SMOOTH);

            } catch (Exception ex) {
                System.out.println(ex);
            }
            add(imgPanel);
        }
        // description
        JLabel desc = new JLabel();
        desc.setForeground(Color.CYAN);
        if (currentSituation.descriptions != null) {
            //desc.setContentType("text/html");
            //desc.setEditable(false);
            desc.setFont(new Font("arial", Font.PLAIN, 20));

            if (currentSituation.descriptions[1] != null && !currentSituation.descriptions[1].equals("") && Reader.player.visited.contains(currentSituation.number)) {
                currentSituation.descriptions[1].replaceAll("\n", "<br>");
                desc.setText("<html>" + currentSituation.descriptions[1] + "</html>");
            } else {
                if (currentSituation.descriptions[0] != null) {

                    currentSituation.descriptions[0] = currentSituation.descriptions[0].replaceAll("\n", "<br>");
                    desc.setText("<html>" + currentSituation.descriptions[0] + "</html>");
                }
            }

            add(desc);
        }
        add(new JLabel(" "));


        int i = 0;


        for (Link link : currentSituation.getLinks()) {
            if (link.getCondition() == null || (link.getCondition().isTrue() && !link.conditionInverted) || (!link.getCondition().isTrue() && link.conditionInverted)){
                JButton choice = new JButton(link.text);
                choice.setBackground(Color.BLACK);
                choice.setForeground(Color.CYAN);

                choice.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        removeAll();
                        if (link.getChangesEvents() != null){
                            for (int i = 0; i < link.getChangesEvents().size(); i++) {
                                link.getChangesEvents().get(i).change(link.boolsApplied.get(i));
                            }
                        }
                        Reader.player.visited.add(currentSituation.number);
                        if (link.getTo() != null) {
                            currentSituation = link.getTo();
                        }
                        showSituation();
                    }
                });
                i++;
                add(choice);
            }



            //double descHeight = desc.getPreferredSize().width / ((Reader.frame.getWidth())) * (desc.getPreferredSize().height * 1.3);
            //setPreferredSize(new Dimension(Reader.WINDOW_WIDTH-20, title.getPreferredSize().height + imgPanel.getPreferredSize().height + (int)descHeight + 30*i));
        }
        repaint();
        updateUI();

    }
    Situation getCurrent(){
        return currentSituation;
    }

    @Override
    public Dimension getPreferredSize() {
        if (isPreferredSizeSet()) {
            return super.getPreferredSize();
        }
        int width = 0;
        int height = 20;
        for (Component component : getComponents()) {
            if (component instanceof JLabel) {
                Dimension labelSize = component.getPreferredSize();
                height += labelSize.height;
            }
            else if (component.isPreferredSizeSet()){
                height += component.getPreferredSize().height;
            }
            else {
                // Calculate the maximum width and height of the components
                height += component.getHeight();
            }
            width = Math.max(width, component.getWidth());
        }
        return new Dimension(width, height);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 30;
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
