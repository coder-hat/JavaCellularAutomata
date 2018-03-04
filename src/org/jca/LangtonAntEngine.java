package org.jca;

/**
 * This engine's ant behavior uses the rules originated by Chris Langton in 1986.<br>
 * The following summary is from <a href="https://en.wikipedia.org/wiki/Langton%27s_ant">Wikipedia's Langton Ant</a> entry:
 * <ul>
 * <li>At a white square, turn 90 degrees right, flip the color of the square, move forward one unit</li>
 * <li>At a black square, turn 90 degrees left, flip the color of the square, move forward one unit/li>
 * </ul>
 * 
 * @author coder-hat
 */
public class LangtonAntEngine
{
    public enum CellState { WHITE, BLACK }
    
    private RectangularGridGeometry grid;
    
    private CellState[] cells;
    
    /**
     * Linear index of the ant's current grid location. 
     */
    private int iAnt;
    
    /**
     * The direction that the ant is currently facing.
     */
    private RectangularGridGeometry.Direction antFacing;
    
    private RectangularGridGeometry.Direction initialFacing;
    
    
    // TODO 2018-2-24 isTorus -vs- noTorus behavior neither determined nor implemented yet.
    
    public LangtonAntEngine(int gridWidth, int gridHeight, boolean isTorus, RectangularGridGeometry.Direction initialFacing) {
        grid = new RectangularGridGeometry(gridWidth, gridHeight, isTorus);
        cells = new CellState[grid.getCellCount()];
        this.initialFacing = initialFacing;
        reset();
    }
    
    
    public void moveAnt() {
        antFacing = cells[iAnt]  == CellState.WHITE ? antFacing.rotateRight90() : antFacing.rotateLeft90();
        flipCellState(iAnt);
        iAnt = grid.getAdjacentI(iAnt, antFacing);
    }
    
    public RectangularGridGeometry getGrid() {
        return grid;
    }
    
    public CellState getState(int iCell) {
        return cells[iCell];
    }
    
    public int getAntLocation() {
        return iAnt;
    }
    
    public RectangularGridGeometry.Direction getAntFacing() {
        return antFacing;
    }
    
    public void reset() {
        for (int iCell = 0; iCell < cells.length; ++iCell) {
            cells[iCell] = CellState.WHITE;
        }
        iAnt = grid.getCenterI();
        antFacing = initialFacing;    
    }
    
    private void flipCellState(int iCell) {
        if (cells[iCell] == CellState.WHITE) {
            cells[iCell] = CellState.BLACK;
        } else {
            cells[iCell] = CellState.WHITE;
        }
    }
}
