package CYAN_Writer;

import CYAN_Mutual.Stat;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;

public class StatRow {
    Stat stat;
    JTextField textBox;
    SpinnerNumberModel initModel;
    SpinnerNumberModel maxModel;
    JSpinner initSpinner;
    JSpinner maxSpinner;

    StatRow(Stat statp){
        this.stat = statp;
        this.textBox = new JTextField(this.stat.getName(), 10);
        this.initModel = new SpinnerNumberModel(this.stat.getCurrent(), 0, stat.getMax(), 1);
        this.maxModel = new SpinnerNumberModel(this.stat.getMax(), 1, 9999, 1);
        this.initSpinner = new JSpinner(initModel);
        this.maxSpinner = new JSpinner(maxModel);


        this.textBox.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                stat.setName(textBox.getText());
            }
        });

        this.initSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                stat.setCurrent((int)initModel.getValue());

            }
        });


        this.maxSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if ((int)initModel.getValue() > (int)maxModel.getValue()){
                    stat.setCurrent((int)maxModel.getValue());
                    initModel.setValue(maxModel.getValue());
                }
                stat.setMax((int)maxModel.getValue());
                initModel.setMaximum((int)maxModel.getValue());
            }
        });
    }

}
