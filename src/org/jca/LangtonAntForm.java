package org.jca;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class LangtonAntForm extends JFrame
{
    /**
     * Default serialization ID added via Eclipse
     */
    private static final long serialVersionUID = 1L;
    
    private LangtonAntEngine antEngine;
   
    private JPanel gridPanel;
    
    private JButton btnStep;
    
    public LangtonAntForm(LangtonAntEngine antEngine) {
        this.antEngine = antEngine;

        gridPanel = new LangtonAntGridDisplayPanel(this.antEngine);
        
        btnStep = new JButton("STEP");
        btnStep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                antEngine.moveAnt();
                gridPanel.repaint();
            }
        });
        
        this.add(gridPanel, BorderLayout.CENTER);
        this.add(btnStep, BorderLayout.SOUTH);
    }
}
