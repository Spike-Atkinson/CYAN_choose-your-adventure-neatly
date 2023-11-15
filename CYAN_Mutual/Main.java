package CYAN_Mutual;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import CYAN_Reader.*;
import CYAN_Writer.Writer;

public class Main {
    public static void main(String[] args) {
        JWindow mainMenu = new JWindow();
        mainMenu.setSize(200, 300);
        mainMenu.setLocationRelativeTo(null);
        mainMenu.setLayout(new BorderLayout());

        JPanel menu = new JPanel();
        GridLayout grid = new GridLayout(4, 1);

        menu.setSize(new Dimension(200, 300));
        menu.setLayout(grid);
        menu.setBackground(Color.DARK_GRAY);

        JLabel title = new JLabel("CYAN");
        Font font = new Font("Arial", Font.BOLD, 30);
        title.setForeground(Color.CYAN);
        title.setFont(font);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        menu.add(title);

        JButton writeStory = new JButton("Write Story");
        writeStory.setBackground(Color.DARK_GRAY);
        writeStory.setForeground(Color.CYAN);
        writeStory.setPreferredSize(new Dimension(200, 50));
        writeStory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Writer.main(null);
                mainMenu.dispose();
            }
        });
        menu.add(writeStory);

        JButton readStory = new JButton("Read Story");
        readStory.setBackground(Color.DARK_GRAY);
        readStory.setForeground(Color.CYAN);
        readStory.setPreferredSize(new Dimension(200, 70));
        readStory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Reader.main(null);
                mainMenu.dispose();
            }
        });
        menu.add(readStory);

        JButton close = new JButton("Close");
        close.setBackground(Color.DARK_GRAY);
        close.setForeground(Color.CYAN);
        close.setPreferredSize(new Dimension(200, 70));
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainMenu.dispose();
                System.exit(0);
            }
        });
        menu.add(close);

        mainMenu.add(menu, BorderLayout.CENTER);
        mainMenu.setVisible(true);
        mainMenu.toFront(); // todo menu doesn't toFront
    }
}
