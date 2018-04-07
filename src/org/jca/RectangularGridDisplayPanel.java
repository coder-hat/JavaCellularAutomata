package org.jca;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class RectangularGridDisplayPanel extends JPanel
{
    /**
     * Default serialization value created by Eclipse 
     */
    private static final long serialVersionUID = 1L;
    
    private final int pxCellSepSize = 1;
    private final int pxCellSize = 11;
    private final int pxShim = (pxCellSepSize + pxCellSize);
    private final int pyShim = pxShim;

    private RectangularGridGeometry grid;
    private IGridColorProvider colorist;
    
    /**
     * The total width (in pixels) of the grid display.
     */
    private int pxTotalWide;
    /**
     * The total height (in pixels) of the grid display.
     */
    private int pxTotalHigh;


    public RectangularGridDisplayPanel(RectangularGridGeometry grid, IGridColorProvider colorist) {
        this.grid = grid;
        this.colorist = colorist;

        pxTotalWide = pxShim * this.grid.getColCount() + pxCellSepSize;
        pxTotalHigh = pxShim * this.grid.getRowCount() + pxCellSepSize;
        setPreferredSize(new Dimension(pxTotalWide, pxTotalHigh));
        
        setBackground(this.colorist.getBackgroundColor());
        
        //setDoubleBuffered(true);  // 2018-3-04 Runtime testing indicates no flicker with this commented-out.
    }

    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int iCell = 0; iCell < grid.getCellCount(); ++iCell) {
            g.setColor(colorist.getCellColor(iCell));
            g.fillRect(getXpx(iCell), getYpx(iCell), pxCellSize, pxCellSize);
        }
    }
    
    private int getXpx(int iCell) {
        return pxCellSepSize + (pxShim * grid.getX(iCell));
    }
    
    private int getYpx(int iCell) {
        return pxCellSepSize + (pyShim * grid.getY(iCell));
    }
}
