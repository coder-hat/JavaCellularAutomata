package org.jca;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class PiltonWorldDisplayPanel extends JPanel
{
    /**
     * Default serialization value created by Eclipse 
     */
    private static final long serialVersionUID = 1L;
    
    private final int pxCellSepSize = 2;
    private final int pxCellSize = 25;
    private final int pxShim = (pxCellSepSize + pxCellSize);
    private final int pyShim = pxShim;

    private RectangularGridGeometry grid;

    private PiltonWorldEngine simEngine;

    
    /**
     * The total width (in pixels) of the grid display.
     */
    private int pxTotalWide;
    /**
     * The total height (in pixels) of the grid display.
     */
    private int pxTotalHigh;


    public PiltonWorldDisplayPanel(RectangularGridGeometry grid, PiltonWorldEngine simEngine) {
        // NOTE 2018-5-16
        // Passing an entire RectangularGridGeometry object in, and holding a reference to it
        // is currently extravagent -- only the row and column values are needed.
        // However, it's a fairly lightweight object, and passing it in keeps this classes
        // ctor close to that of the more general-purpose RectangularGridDisplayPanel.
        this.grid = grid;
        this.simEngine = simEngine;

        pxTotalWide = pxShim * this.grid.getColCount() + pxCellSepSize;
        pxTotalHigh = pxShim * this.grid.getRowCount() + pxCellSepSize;
        setPreferredSize(new Dimension(pxTotalWide, pxTotalHigh));
        
        setBackground(Color.lightGray);
        
        //setDoubleBuffered(true);  // 2018-3-04 Runtime testing indicates no flicker with this commented-out.
    }

    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (PiltonParticle p : simEngine.getParticles()) {
            g.setColor(Color.red);
            g.fillRect(getXpx(p.x()), getYpx(p.y()), pxCellSize, pxCellSize);
        }
    }
    
    private int getXpx(int xCell) {
        return pxCellSepSize + (pxShim * xCell);
    }
    
    private int getYpx(int yCell) {
        return pxCellSepSize + (pyShim * yCell);
    }
}
