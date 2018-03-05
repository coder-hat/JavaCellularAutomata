package org.jca;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

public class LangtonAntGridDisplayPanel extends JPanel
{
    /**
     * Default serialization value created by Eclipse.
     */
    private static final long serialVersionUID = 1L;
    
    private static Map<LangtonAntEngine.CellState, Color> cellColors;
    static {
        cellColors = new HashMap<>();
        cellColors.put(LangtonAntEngine.CellState.WHITE, Color.white);
        cellColors.put(LangtonAntEngine.CellState.BLACK, Color.black);
    }
    private static Color antColor = Color.orange;
    
    private final int pxCellSepSize = 1;
    private final int pxCellSize = 11;
    private final int pxShim = (pxCellSepSize + pxCellSize);
    private final int pyShim = pxShim;
    
    private LangtonAntEngine antEngine;
    private RectangularGridGeometry antGrid;
    
    /**
     * The total width (in pixels) of the grid display.
     */
    private int pxTotalWide;
    /**
     * The total height (in pixels) of the grid display.
     */
    private int pxTotalHigh;
    
    public LangtonAntGridDisplayPanel(LangtonAntEngine antEngine) {
        this.antEngine = antEngine;
        antGrid = this.antEngine.getGrid();
        pxTotalWide = pxShim * antGrid.getColCount() + pxCellSepSize;
        pxTotalHigh = pxShim * antGrid.getRowCount() + pxCellSepSize;
        setBackground(Color.lightGray);
        setPreferredSize(new Dimension(pxTotalWide, pxTotalHigh));
        //setDoubleBuffered(true);  // 2018-3-04 Runtime testing indicates no flicker with this commented-out.
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int iCell = 0; iCell < antGrid.getCellCount(); ++iCell) {
            g.setColor(cellColors.get(antEngine.getState(iCell)));
            g.fillRect(getXpx(iCell), getYpx(iCell), pxCellSize, pxCellSize);
        }
        int iAntCell = antEngine.getAntLocation();
        g.setColor(antColor);
        g.fillRect(getXpx(iAntCell), getYpx(iAntCell), pxCellSize, pxCellSize);
    }
    
    private int getXpx(int iCell) {
        return pxCellSepSize + (pxShim * antGrid.getX(iCell));
    }
    
    private int getYpx(int iCell) {
        return pxCellSepSize + (pyShim * antGrid.getY(iCell));
    }
}
