package org.jca;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;


public class PiltonWorldForm extends JFrame
{
    /**
     * Default serialization ID added via Eclipse
     */
    private static final long serialVersionUID = 1L;
    
    private PiltonWorldEngine simEngine;
    private RectangularGridGeometry grid = new RectangularGridGeometry(PiltonWorldEngine.CELL_COLS, PiltonWorldEngine.CELL_ROWS, true);
    
    private SimulationRunner simEngineRunner;
    
    private JLabel lblStatus;
    
    private JTextArea txtParticles;
    
    private PiltonWorldDisplayPanel pnlGrid;
    
    private JPanel pnlButtons;
    
    private JButton btnStep;
    private JButton btnStart;
    private JButton btnStop;
    private JButton btnReset;
    
    
    public PiltonWorldForm(PiltonWorldEngine simEngine) {
        this.simEngine = simEngine;
        
        Font lblfont = new Font(Font.MONOSPACED, Font.PLAIN, 10);
        // Use the width of a capital W as a rule-of-thumb basic width unit
        final int wCharWidth = this.getFontMetrics(lblfont).charWidth('W');
        
        lblStatus = new JLabel();
        // Create padding around label text.
        // Credit due to Andre L. S.'s blog entry, "Inserting padding into a JLabel" 
        // at:
        // http://www.andrels.com/wp-en_US/2009/08/inserting-padding-into-a-jlabel/
        lblStatus.setBorder(BorderFactory.createEmptyBorder(5, 3, 3, 3)); // (top, left, bot, right)
        lblStatus.setText(makeStatusText());
        
        txtParticles = new JTextArea();
        txtParticles.setBorder(BorderFactory.createEmptyBorder(5,3,0,3));
        txtParticles.setBackground(Color.cyan);
        txtParticles.setPreferredSize(new Dimension(30 * wCharWidth, -1));
        txtParticles.setEditable(false);
        txtParticles.setLineWrap(true);  // turn on line wrap
        txtParticles.setWrapStyleWord(true);  // wrap at word boundaries

        pnlGrid = new PiltonWorldDisplayPanel(grid, simEngine);
        
        btnStep = new JButton("STEP");
        btnStep.addActionListener(new StepSimulatorAction());
        
        btnStart = new JButton("START");
        btnStart.addActionListener(new RunSimulatorAction());
        
        btnStop = new JButton("STOP");
        btnStop.addActionListener(new StopSimulatorAction());
        
        btnReset = new JButton("RESET");
        btnReset.addActionListener(new ResetSimulatorAction());
        
        pnlButtons = new JPanel();
        pnlButtons.add(btnStep);        
        pnlButtons.add(btnStart);        
        pnlButtons.add(btnStop);        
        pnlButtons.add(btnReset);

        this.add(lblStatus, BorderLayout.NORTH);
        this.add(pnlGrid, BorderLayout.CENTER);
        this.add(txtParticles, BorderLayout.EAST);
        this.add(pnlButtons, BorderLayout.SOUTH);
        
        this.setTitle(String.format("Pilton Tiny World : %1$sx%2$s Universe", PiltonWorldEngine.CELL_COLS, PiltonWorldEngine.CELL_ROWS));
    }
    
    private String makeStatusText(){
        return String.format("t=%1$s particle count=%2$s", simEngine.getTimestep(), simEngine.getParticles().size());
    }
    
    private String makeParticlesText() {
        return simEngine.getParticles().toString();
    }
    
    //----- Inner classes
    
    private class StepSimulatorAction implements ActionListener
    {
        @Override public void actionPerformed(ActionEvent e) {
            simEngine.doSimulationStep();
            lblStatus.setText(makeStatusText());
            txtParticles.setText(makeParticlesText());
            pnlGrid.repaint();
        }
    }
    
    private class RunSimulatorAction implements ActionListener
    {
        @Override public void actionPerformed(ActionEvent e) {
            try {
                simEngineRunner = new SimulationRunner();
                simEngineRunner.execute();
                
                btnStep.setEnabled(false);
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                btnReset.setEnabled(false);
            }
            catch (Exception ex) {
                btnStep.setEnabled(true);
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                btnReset.setEnabled(true);
            }
        }
    }
    
    private class StopSimulatorAction implements ActionListener
    {
        @Override public void actionPerformed(ActionEvent e) {
            if (simEngineRunner != null) {
                simEngineRunner.cancel(true);
            }
        }
    }
    
    private class ResetSimulatorAction implements ActionListener
    {
        @Override public void actionPerformed(ActionEvent e) {
            simEngine.reset();
            simEngine.setParticles(new ArrayList<PiltonParticle>(Arrays.asList(new PiltonParticle(3, 2, 1))));
            lblStatus.setText(makeStatusText());
            txtParticles.setText(makeParticlesText());
            pnlGrid.repaint();
        }
    }
    
    private class SimulationRunner extends SwingWorker<Void, PiltonWorldEngine>
    {
        public SimulationRunner() {
        }

        @Override protected Void doInBackground() throws Exception {
            if (simEngine != null) {
                while (!isCancelled()) {
                    simEngine.doSimulationStep();
                    publish(simEngine);
                    try {
                        Thread.sleep(15); // milliseconds
                    }
                    catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        // e.printStackTrace();
                    }
                }
            }
            return null;
        }
        
        @Override protected void process(List<PiltonWorldEngine> engineRefs) {
            for (PiltonWorldEngine ae : engineRefs) {
                synchronized (ae) {
                    pnlGrid.repaint();
                    lblStatus.setText(makeStatusText());
                    txtParticles.setText(makeParticlesText());
                }
            }
        }
        
        @Override protected void done() {
            btnStep.setEnabled(true);
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            btnReset.setEnabled(true);
        }
    }

}
