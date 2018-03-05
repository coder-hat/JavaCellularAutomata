package org.jca;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;


public class LangtonAntForm extends JFrame
{
    /**
     * Default serialization ID added via Eclipse
     */
    private static final long serialVersionUID = 1L;
    
    private LangtonAntEngine antEngine;
    
    private SimulationRunner antEngineRunner;
    
    private JLabel lblStatus;
    private String fmtStatus;
    
    private JPanel pnlGrid;
    
    private JPanel pnlButtons;
    
    private JButton btnStep;
    private JButton btnStart;
    private JButton btnStop;
    private JButton btnReset;
    
    public LangtonAntForm(LangtonAntEngine antEngine) {
        this.antEngine = antEngine;
        
        final int iDigits = String.valueOf(antEngine.getGrid().getCellCount()).length();
        final int xDigits = String.valueOf(antEngine.getGrid().getColCount()).length();
        final int yDigits = String.valueOf(antEngine.getGrid().getRowCount()).length();
        fmtStatus = "Ant at i=%1$" + iDigits + "s x=%2$" + xDigits + "s y=%3$" + yDigits + "s facing=%4$s";
        
        lblStatus = new JLabel();
        // Create padding around label text.
        // Credit due to Andre L. S.'s blog entry, "Inserting padding into a JLabel" 
        // at:
        // http://www.andrels.com/wp-en_US/2009/08/inserting-padding-into-a-jlabel/
        lblStatus.setBorder(BorderFactory.createEmptyBorder(5, 3, 3, 3));
        lblStatus.setText(makeStatusText());

        pnlGrid = new LangtonAntGridDisplayPanel(this.antEngine);
        
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
        this.add(pnlButtons, BorderLayout.SOUTH);
    }
    
    private String makeStatusText(){
        final int iAnt = antEngine.getAntLocation();
        final int xAnt = antEngine.getGrid().getX(iAnt);
        final int yAnt = antEngine.getGrid().getY(iAnt);
        return String.format(fmtStatus, iAnt, xAnt, yAnt, antEngine.getAntFacing());
    }
    
    //----- Inner classes
    
    private class StepSimulatorAction implements ActionListener
    {
        @Override public void actionPerformed(ActionEvent e) {
            antEngine.doSimulationStep();
            lblStatus.setText(makeStatusText());
            pnlGrid.repaint();
        }
    }
    
    private class RunSimulatorAction implements ActionListener
    {
        @Override public void actionPerformed(ActionEvent e) {
            try {
                antEngineRunner = new SimulationRunner();
                antEngineRunner.execute();
                
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
            if (antEngineRunner != null) {
                antEngineRunner.cancel(true);
            }
        }
    }
    
    private class ResetSimulatorAction implements ActionListener
    {
        @Override public void actionPerformed(ActionEvent e) {
            antEngine.reset();
            lblStatus.setText(makeStatusText());
            pnlGrid.repaint();
        }
    }
    
    private class SimulationRunner extends SwingWorker<Void, LangtonAntEngine>
    {
        public SimulationRunner() {
        }

        @Override protected Void doInBackground() throws Exception {
            if (antEngine != null) {
                while (!isCancelled()) {
                    antEngine.doSimulationStep();
                    publish(antEngine);
                    try {
                        Thread.sleep(60); // milliseconds
                    }
                    catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        // e.printStackTrace();
                    }
                }
            }
            return null;
        }
        
        @Override protected void process(List<LangtonAntEngine> engineRefs) {
            for (LangtonAntEngine ae : engineRefs) {
                synchronized (ae) {
                    pnlGrid.repaint();
                    lblStatus.setText(makeStatusText());
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
