package org.jca;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;


/**
 * Provides cell {@link Color} values based on {@link ConwayLifeEngine} {@link ConfwayLifeEngine#CellState CellState}
 * values.
 * 
 * @author ksdj (coder-hat)
 */
final class LifeGridColorist implements IGridColorProvider
{
    private static Map<ConwayLifeEngine.CellState, Color> cellColors;
    static {
        cellColors = new HashMap<>();
        cellColors.put(ConwayLifeEngine.CellState.DEAD, Color.white);
        cellColors.put(ConwayLifeEngine.CellState.LIVE, Color.green);
    }
    
    private ConwayLifeEngine simEngine;

    public LifeGridColorist(ConwayLifeEngine simEngine) {
        this.simEngine = simEngine;
    }
    
    @Override
    public Color getCellColor(int iCell) {
        return cellColors.get(simEngine.getState(iCell));
    }

    @Override
    public Color getBackgroundColor() {
        return Color.lightGray;
    }
}


public class ConwayLifeForm extends JFrame
{
    /**
     * Default serialization ID added via Eclipse
     */
    private static final long serialVersionUID = 1L;
    
    private ConwayLifeEngine simEngine;
    
    private SimulationRunner simEngineRunner;
    
    private JLabel lblStatus;
    private String fmtStatus;
    
    private RectangularGridDisplayPanel pnlGrid;
    
    private JPanel pnlButtons;
    
    private JButton btnStep;
    private JButton btnStart;
    private JButton btnStop;
    private JButton btnReset;
    
    
    public ConwayLifeForm(ConwayLifeEngine simEngine) {
        this.simEngine = simEngine;
        
        fmtStatus = "Live Cells Count: %1$s";
        
        lblStatus = new JLabel();
        // Create padding around label text.
        // Credit due to Andre L. S.'s blog entry, "Inserting padding into a JLabel" 
        // at:
        // http://www.andrels.com/wp-en_US/2009/08/inserting-padding-into-a-jlabel/
        lblStatus.setBorder(BorderFactory.createEmptyBorder(5, 3, 3, 3));
        lblStatus.setText(makeStatusText());

        pnlGrid = new RectangularGridDisplayPanel(this.simEngine.getGrid(), new LifeGridColorist(simEngine));
        
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
        
        this.setTitle("Conway's Game of Life");
    }
    
    private String makeStatusText(){
        return String.format(fmtStatus, simEngine.getLiveCount());
    }
    
    //----- Inner classes
    
    private class StepSimulatorAction implements ActionListener
    {
        @Override public void actionPerformed(ActionEvent e) {
            simEngine.doSimulationStep();
            lblStatus.setText(makeStatusText());
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
            lblStatus.setText(makeStatusText());
            pnlGrid.repaint();
        }
    }
    
    private class SimulationRunner extends SwingWorker<Void, ConwayLifeEngine>
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
        
        @Override protected void process(List<ConwayLifeEngine> engineRefs) {
            for (ConwayLifeEngine ae : engineRefs) {
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
