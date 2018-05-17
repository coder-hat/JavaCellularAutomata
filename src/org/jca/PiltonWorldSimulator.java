package org.jca;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class PiltonWorldSimulator
{
    public static void main(String[] args) {
        PiltonWorldEngine simEngine = new PiltonWorldEngine();
        simEngine.setParticles(new ArrayList<PiltonParticle>(Arrays.asList(new PiltonParticle(3, 2, 1))));
        PiltonWorldForm simForm = new PiltonWorldForm(simEngine);
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
