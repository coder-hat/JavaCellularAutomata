package org.jca;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jca.RectangularGridGeometry.Direction;

public class LangtonAntSimulator
{
    
    public static void main(String[] args) {
        LangtonAntEngine antEngine = new LangtonAntEngine(100, 75, true, Direction.LEFT);
        LangtonAntForm antForm = new LangtonAntForm(antEngine);
        antForm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        antForm.pack();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                antForm.setVisible(true);
            }
        });
    }
}
