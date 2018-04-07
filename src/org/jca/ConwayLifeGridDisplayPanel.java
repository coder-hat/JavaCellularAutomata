package org.jca;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

/**
 * @deprecated 2018-4-07 Use RectangularGridDisplayPanel instance (with appropriate ctor args) instead.
 */
@Deprecated // 2018-4-07 Use RectangularGridDisplayPanel instance (with appropriate ctor args) instead.
public class ConwayLifeGridDisplayPanel extends JPanel
{
    /**
     * Default serialization value created by Eclipse.
     */
    private static final long serialVersionUID = 1L;
    
    private static Map<ConwayLifeEngine.CellState, Color> cellColors;
    static {
        cellColors = new HashMap<>();
        cellColors.put(ConwayLifeEngine.CellState.DEAD, Color.white);
        cellColors.put(ConwayLifeEngine.CellState.LIVE, Color.green);
    }
    
    private final int pxCellSepSize = 1;
    private final int pxCellSize = 11;
    private final int pxShim = (pxCellSepSize + pxCellSize);
    private final int pyShim = pxShim;
    
    private ConwayLifeEngine simEngine;
    private RectangularGridGeometry simGrid;
    
    /**
     * The total width (in pixels) of the grid display.
     */
    private int pxTotalWide;
    /**
     * The total height (in pixels) of the grid display.
     */
    private int pxTotalHigh;
    
    public ConwayLifeGridDisplayPanel(ConwayLifeEngine antEngine) {
        this.simEngine = antEngine;
        simGrid = this.simEngine.getGrid();
        pxTotalWide = pxShim * simGrid.getColCount() + pxCellSepSize;
        pxTotalHigh = pxShim * simGrid.getRowCount() + pxCellSepSize;
        setBackground(Color.lightGray);
        setPreferredSize(new Dimension(pxTotalWide, pxTotalHigh));
        //setDoubleBuffered(true);  // 2018-3-04 Runtime testing indicates no flicker with this commented-out.
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int iCell = 0; iCell < simGrid.getCellCount(); ++iCell) {
            g.setColor(cellColors.get(simEngine.getState(iCell)));
            g.fillRect(getXpx(iCell), getYpx(iCell), pxCellSize, pxCellSize);
        }
    }
    
    private int getXpx(int iCell) {
        return pxCellSepSize + (pxShim * simGrid.getX(iCell));
    }
    
    private int getYpx(int iCell) {
        return pxCellSepSize + (pyShim * simGrid.getY(iCell));
    }
}
