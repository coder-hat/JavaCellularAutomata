package org.jca;

import java.util.Random;

import org.jca.RectangularGridGeometry.Direction;

/**
 * A simulator engine implementing John Conway's
 * <a href="https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life">Game of Life</a>.
 * <p>
 * From Wikpedia:
 * <blockquote cite="https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life">
 * At each step in time, the following transitions occur:
 * <ol>
 * <li>Any live cell with fewer than two live neighbours dies, as if caused by underpopulation.</li>
 * <li>Any live cell with two or three live neighbours lives on to the next generation.</li>
 * <li>Any live cell with more than three live neighbours dies, as if by overpopulation.</li>
 * <li>Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.</li>
 * </ol>
 * The initial pattern constitutes the seed of the system. The first generation is created by applying the above rules
 * simultaneously to every cell in the seed—births and deaths occur simultaneously, and the discrete moment at which
 * this happens is sometimes called a tick (in other words, each generation is a pure function of the preceding one).
 * The rules continue to be applied repeatedly to create further generations.
 * </blockquote>
 *
 * @author ksdj (coder-hat)
 */
public class ConwayLifeEngine {
    
    /**
     * The possible states a grid cell can be in.
     */
    public static enum CellState { DEAD, LIVE }

    private RectangularGridGeometry grid;
    
    private CellState[] cells;
    
    /**
     * The number of cells in the {@link cells} array currently in {@link CellState#LIVE}.<br>
     * <i>Warning!</i><br>
     * Any code that changes the state of any cell in {@link cells} is responsible for updating this field's value.
     */
    private int liveCount;
    
    
    public ConwayLifeEngine(int colCount, int rowCount, boolean isTorus) {
        grid = new RectangularGridGeometry(colCount, rowCount, isTorus);
        cells = new CellState[grid.getCellCount()];
        reset();
    }

    
    public RectangularGridGeometry getGrid() {
        return grid;
    }
    
    
    /**
     * Resets the cells of the grid so that about 30% of the total are in {@link CellState#LIVE}.
     */
    public void reset() {
        final double liveThreshold = 0.30; // must be < this threshold to be LIVE
        liveCount = 0;
        Random rand = new Random();
        for (int iCell = 0; iCell < cells.length; ++iCell) {
            cells[iCell] = rand.nextDouble() < liveThreshold ? CellState.LIVE : CellState.DEAD;
            if (cells[iCell] == CellState.LIVE) ++liveCount;
        }
    }
    
    public void setGridState(CellState[] newCells) {
        liveCount = 0;
        for (int iCell=0; iCell < cells.length; ++iCell) {
            cells[iCell] = newCells[iCell];
            if (cells[iCell] == CellState.LIVE) ++liveCount;
        }
    }
    
    /**
     * Gets the state of the specified cell.
     * 
     * @param iCell
     *            The linear index of the cell to get the state of.
     * @return The {@link CellState} of iCell.
     */
    public CellState getState(int iCell) {
        return cells[iCell];
    }
    
    /**
     * Sets iCell's {@link CellState} to the specified newState.
     * 
     * @param iCell
     *            The linear index of the cell to set the state of.
     * @param newState
     *            The state value to set for iCell.
     */
    public void setState(int iCell, CellState newState) {
        CellState oldState = cells[iCell];
        cells[iCell] = newState;
        if (oldState != newState) {
            liveCount = liveCount + ((newState == CellState.LIVE) ? 1 : -1);
        }
    }
    
    public void doSimulationStep() {
        CellState[] nextCells = new CellState[grid.getCellCount()];
        liveCount = 0;
        for (int iCell=0; iCell < cells.length; ++iCell) {
            nextCells[iCell] = getNextState(iCell);
            if (nextCells[iCell] == CellState.LIVE) ++liveCount;
        }
        cells = nextCells;
    }
    
    /**
     * A debug/utility method that prints an "ascii art" representation of the grid state to stdout.
     */
    public void printGridState() {
        System.out.print("GRID STATE BELOW");
        for (int iCell = 0; iCell < grid.getCellCount(); ++iCell) {
            if (iCell % grid.getRowCount() == 0) System.out.println();
            System.out.print(getState(iCell) == CellState.LIVE ? "#" : ".");
        }
        System.out.println("\nGRID STATE ABOVE");
    }
    
    /**
     * Determines the next state for the specified cell based on the cell's current state and the state of its eight,
     * adjacent cells.
     * 
     * @param iCell
     *            The linear index of the cell to compute the next state of.
     *            
     * @return The next {@link CellState} for iCell.
     */
    protected CellState getNextState(int iCell) {
        final int adjLiveCount = getAdjacentLiveCount(iCell);
        if (cells[iCell] == CellState.LIVE) {
            return adjLiveCount == 2 || adjLiveCount == 3 ? CellState.LIVE : CellState.DEAD;
        } else {
            return adjLiveCount == 3 ? CellState.LIVE : CellState.DEAD;
        }
    }
    
    /**
     * @return The number of cells currently in state {@link CellState#LIVE}.
     */
    public int getLiveCount() {
        return liveCount;
    }
    
    /**
     * Determines the number of adjacent, live cells adjacent to the specified cell.
     * 
     * @param iCell
     *            The linear index of the cell to count live, adjacent cells for.
     *            
     * @return The number of live cells adjacent to iCell.
     */
    protected int getAdjacentLiveCount(int iCell) {
        int adjLiveCount = 0;
        for (int iAdj : grid.getAdjacentIndices(iCell, Direction.values())) {
            if (cells[iAdj] == CellState.LIVE) adjLiveCount++;
        }
        return adjLiveCount;
    }
}
