package CYAN_Writer;

import CYAN_Mutual.Player;
import CYAN_Mutual.Stat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class PlayerEditor extends JFrame{
    //public static JFrame frame;
    public static JPanel boundary;

    PlayerEditor(Player player) {
        setTitle("CYAN Reader - Editor" + " (Player)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Writer.playerWindow = null;
            }
        });
        setSize(500,400);
        setLocationRelativeTo(null);
        setVisible(true);
        boundary = new JPanel();
        boundary.setLayout(new FlowLayout());
        boundary.setBounds(0, 0, 500, 400);
        add(boundary);

        nameBox();

        if (player.stats.size() != 0) {
            makeStatGrid();
        }else{
            firstStat();
        }
        if (player.inventory != null) {
            createInventory();
        }else{
            // fillInventorygrid
        }
    }

    private void nameBox(){
        // name field and label
        JPanel top = new JPanel();
        top.setBounds(0, 0, 500, 50);

        JTextField nameBox = new JTextField(Writer.player.name, 30);
        nameBox.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                Writer.player.name = nameBox.getText();
            }
        });

        JLabel nameLabel = new JLabel("Name");
        top.add(nameLabel);
        top.add(nameBox);
        boundary.add(top);
    }

    private void firstStat(){
        JButton addAStat = new JButton("Add Stat to Player");
        boundary.add(addAStat);
        addAStat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Stat newStat = new Stat("Stat1", 10, 10);
                Writer.player.stats.add(newStat);
                boundary.remove(addAStat);
                makeStatGrid();
            }
        });
    }
    private void makeStatGrid(){
        // attribute fields and labels panel
        JPanel statGrid = new JPanel(); // attribute name column
        statGrid.setBounds(0, 100, 200, 300);
        GridLayout grid = new GridLayout(2, 3);
        statGrid.setLayout(grid);

        // grid headers
        JLabel[] headers = {new JLabel("STATS"), new JLabel("INITIAL"), new JLabel("MAX")};
        for (int i = 0; i < 3; i++) {
            statGrid.add(headers[i]);
        }

        // Grid row fields
        ArrayList<StatRow> rows = new ArrayList<StatRow>();

        // add existing player stats to rows List
        for (int i = 0; i < Writer.player.stats.size(); i++) {
            Stat statp = Writer.player.stats.get(i);
            // row
            rows.add(new StatRow(statp));
        }

        for (int e = 0; e < rows.size(); e += 1) {
            grid.setRows(grid.getRows() + 1);
            statGrid.add(rows.get(e).textBox);
            statGrid.add(rows.get(e).initSpinner);
            statGrid.add(rows.get(e).maxSpinner);
        }
        JButton addNewStat = new JButton("Add New Stat");
        statGrid.add(addNewStat);
        addNewStat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String com = e.getActionCommand();
                System.out.println(com + " Clicked");
                grid.setRows(grid.getRows() + 1);
                Stat newStat = new Stat("Stat" + (rows.size() + 1), 10, 10);
                Writer.player.stats.add(newStat);
                rows.add(new StatRow(newStat));
                statGrid.remove(addNewStat);
                statGrid.add(rows.get(rows.size() - 1).textBox);
                statGrid.add(rows.get(rows.size() - 1).initSpinner);
                statGrid.add(rows.get(rows.size() - 1).maxSpinner);
                statGrid.add(addNewStat);
                statGrid.updateUI();

            }
        });
        boundary.add(statGrid);
        boundary.updateUI();
    }
    private void createInventory(){
        JButton addIn = new JButton("Add Inventory");
        boundary.add(addIn);
        addIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // call inventory grid
                // make add item buttons
                // make remove inventory button
                boundary.remove(addIn);

            }
        });
        boundary.updateUI();
    }

}
