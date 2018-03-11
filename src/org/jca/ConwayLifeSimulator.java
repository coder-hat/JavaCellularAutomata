package org.jca;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class ConwayLifeSimulator
{
    public static void main(String[] args) {
        ConwayLifeEngine simEngine = new ConwayLifeEngine(75, 55, true);
        ConwayLifeForm simForm = new ConwayLifeForm(simEngine);
        simForm.setResizable(false);
        simForm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        simForm.pack();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                simForm.setVisible(true);
            }
        });
    }
}
