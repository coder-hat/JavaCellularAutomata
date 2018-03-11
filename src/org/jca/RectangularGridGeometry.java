package org.jca;

import java.util.ArrayList;
import java.util.List;

/**
 * A RectangularGridGeometry object contains the dimensions of a rectangular grid of locations.<br>
 * No actual data is stored: only the dimensions and whether or not the grid is a torus.
 * <p>
 * In addition to dimensions and torus status, RectangularGridGeometry provides:
 * <ul>
 * <li>An enum defining {@link Direction directions} relative to a given cell location within the grid.</li>
 * <li>Methods that translate between 2d coordinates and 1d linear indices.</li>
 * <li>Methods that provide the linear indices of a given cell location's adjacent cells.</li>
 * </ul>
 * Method behavior depends upon whether the grid is created as a torus or not. When the grid is not a torus, methods
 * called with off-grid coordinates or indices, or that result in adjacent locations that are off-grid will return the
 * {@link RectangularGridGeometry#OFF_GRID_INDEX OFF_GRID_INDEX} value. When the grid is a torus, the same conditions
 * cause the method to "wrap around" the toroidal surface the grid represents.<br>
 * For example, after the following code runs ...
 * <pre>RectangularGridGeometry g = new RectangularGridGeometry(4, 5, false);
 * int i = getAdjacentI(0, Direction.ABOVE_LEFT);</pre>
 * ... the value of i is {@link RectangularGridGeometry#OFF_GRID_INDEX OFF_GRID_INDEX}.<br>
 * However, after the following code runs ...<pre>
 * RectangularGridGeometry g = new RectangularGridGeometry(4, 5, true);
 * int i = getAdjacentI(0, Direction.ABOVE_LEFT);</pre>
 * ... the value of is 19 -- the linear index of the cell in the lower right corner of the grid, since on a torus it is
 * also the grid directly above and to the left of the cell at linear index 0.
 * <p>
 * A cell's linear index is its sequential index from the upper left-hand corner of the grid, traversing left-to-right
 * across the columns of a row, down the rows of the grid.
 * 
 * @author coder-hat
 */
public class RectangularGridGeometry
{
    /**
     * RectangularGridGeometry get-methods return this value when the grid object is not a torus,
     * and the input argument(s) of a get-method results in a index or coordinate value that lies
     * outside of the row, column or linear index range of the grid object's dimensions.
     */
    public static int OFF_GRID_INDEX = -1;
    
    /**
     * Enumerates the 8 adjacent locations relative to a given iCell in a RectangularGridGeometry grid.
     * <pre>
     * ABOVE_LEFT | ABOVE | ABOVE_RIGHT
     * -----------+-------+------------
     * LEFT       | iCell |       RIGHT
     * -----------+-------+------------
     * BELOW_LEFT | BELOW | BELOW_RIGHT
     * </pre>
     * Iterating through the ordinals of the values results in a sequence that begins with ABOVE
     * and proceeds clockwise around iCell.
     * <p>
     * @author coder-hat
     */
    public static enum Direction {
        ABOVE(0, -1),
        ABOVE_RIGHT(1,-1),
        RIGHT(1,0),
        BELOW_RIGHT(1,1),
        BELOW(0,1),
        BELOW_LEFT(-1,1),
        LEFT(-1,0),
        ABOVE_LEFT(-1,-1);
        
        private int dx;
        private int dy;
        
        private Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
        
        public static Direction[] Sides = { ABOVE, RIGHT, BELOW, LEFT };
        
        public static Direction[] Corners = { ABOVE_RIGHT, BELOW_RIGHT, BELOW_LEFT, ABOVE_LEFT };
        
        public Direction rotateLeft() {
            return getOffset(this, -1);
        }
        
        public Direction rotateRight() {
            return getOffset(this, 1);
        }
        
        public Direction rotateLeft90() {
            return getOffset(this, -2);
        }
        
        public Direction rotateRight90() {
            return getOffset(this, 2);
        }
        
        public Direction rotate180() {
            return getOffset(this, 4);
        }
        
        public int getDx() { return dx; }
        
        public int getDy() { return dy; }
        
        private static Direction getOffset(Direction d, int offset) {
            final Direction[] vals = Direction.values();
            final int count = vals.length;
            final int iOffs = wrappedModulo(d.ordinal() + offset, count);
            return vals[iOffs];
        }
    }
    
    private int rowCount = 1;
    private int colCount = 1;
    private int cellCount = 1;
    private boolean isTorus = false;
    
    /**
     * Constructs a {@link RectangularGridGeometry} object with the specified number of columns, rows, and torus flag.
     * @param colCount The number of columns in the grid.
     * @param rowCount The number of rows in the grid.
     * @param isTorus Whether or not the grid is a torus.
     */
    public RectangularGridGeometry(int colCount, int rowCount, boolean isTorus) {
        this.colCount = colCount;
        this.rowCount = rowCount;
        this.cellCount = colCount * rowCount;  // store product once, to avoid multiple multiplies in getCellCount()
        this.isTorus = isTorus;
    }
    
    /**
     * @return The number of columns in the grid.
     */
    public int getColCount() {
        return colCount;
    }
    
    /**
     * @return The number of rows in the grid.
     */
    public int getRowCount() {
        return rowCount;
    }
    
    /**
     * Get the total number of cells in the grid.<br>
     * The same result is available via ({@link RectangularGridGeometry#getColCount() getColCount} *
     * {@link RectangularGridGeometry#getRowCount() getRowCount}) however calling getCellCount does not incur the
     * multiplication op.
     * 
     * @return The total number of cells in the grid.
     */
    public int getCellCount() {
        return cellCount;
    }
    
    /**
     * Gets the linear index of the cell nearest the center of the 2d grid.
     * <p>
     * The precise location depends upon whether the grid has even or odd
     * numbers of rows and/or columns.<br>
     * When the row or column count is odd, the actual center index of the row
     * or column is used to calculated the linear index.<br>
     * When the row or column count is even, the lower index of the two "center"
     * indices of the row or column is used to calculate the linear index.
     * 
     * @return The linear index of the cell nearest the center of the 2d grid.
     */
    public int getCenterI() {
        final int xCell = colCount / 2 - (colCount % 2 == 0 ? 1 : 0);
        final int yCell = rowCount / 2 - (rowCount % 2 == 0 ? 1 : 0);
        return getI(xCell, yCell);
    }
    
    /**
     * Gets the linear index of the cell at the specified 2d grid location.
     * <p>
     * When the grid object is not a torus
     * 
     * @param xCell
     *            The x-coordinate (column index) of the cell to get the linear
     *            index of.
     * @param yCell
     *            The y-coordinate (row index) of the cell to get the linear
     *            index of.
     * 
     * @return The linear index of the cell at the specified 2d grid location,
     *         or {@link RectangularGridGeometry#OFF_GRID_INDEX OFF_GRID_INDEX}
     *         if the grid is not a torus and either of the specified 2d
     *         coordinates are off-grid.
     */
    public int getI(int xCell, int yCell) {
        if (xCell < 0 || xCell >= colCount) {
            if (isTorus) {
                xCell = wrappedModulo(xCell, colCount);
            } else {
                return OFF_GRID_INDEX;
            }
        }
        if (yCell < 0 || yCell >= rowCount) {
            if (isTorus) {
                yCell = wrappedModulo(yCell, rowCount);
            } else {
                return OFF_GRID_INDEX;
            }
        }
        return colCount * yCell + xCell;
    }
    
    /**
     * Computes the x-coordinate (column index) corresponding to the specified iCell.<br>
     * When the grid object is a torus and the specified iCell value is less than zero or greater than
     * {@link RectangularGridGeometry#getCellCount() getCellCount}, the iCell value is mapped onto the grid via a call
     * to {@link RectangularGridGeometry#wrappedModulo(int, int) wrappedModulo} prior to computing the x-coordinate.<br>
     * A not-torus grid returns {@link RectangularGridGeometry#OFF_GRID_INDEX OFF_GRID_INDEX} for an off-grid iCell value.
     * 
     * @param iCell
     *            The linear index of the cell location to get the x-coordinate of.
     *            
     * @return the x-coordinate (column index) of iCell, or OFF_GRID_INDEX.
     */
    public int getX(int iCell) {
        if (iCell < 0 || iCell >= cellCount) {
            if (isTorus) {
                iCell = wrappedModulo(iCell, cellCount);
            } else {
                return OFF_GRID_INDEX;
            }
        }
        return iCell % colCount;
    }
    
    /**
     * Computes the y-coordinate (row index) corresponding to the specified iCell.<br>
     * When the grid object is a torus and the specified iCell value is less than zero or greater than
     * {@link RectangularGridGeometry#getCellCount() getCellCount}, the iCell value is mapped onto the grid via a call
     * to {@link RectangularGridGeometry#wrappedModulo(int, int) wrappedModulo} prior to computing the y-coordinate.<br>
     * A not-torus grid returns {@link RectangularGridGeometry#OFF_GRID_INDEX OFF_GRID_INDEX} for an off-grid iCell
     * value.
     * 
     * @param iCell
     *            The linear index of the cell location to get the y-coordinate of.
     *            
     * @return the y-coordinate (row index) of iCell, or OFF_GRID_INDEX.
     */
    public int getY(int iCell) {
        if (iCell < 0 || iCell >= cellCount) {
            if (isTorus) {
                iCell = wrappedModulo(iCell, cellCount);
            } else {
                return OFF_GRID_INDEX;
            }                
        }
        return iCell / colCount;
    }
    
    /**
     * Gets the linear index of the cell adjacent to iCell in the specified Direction.
     * 
     * @param iCell
     *            The linear index of the cell to find the adjacent-cell location for.
     * @param facing
     *            The {@link Direction} that iCell is "facing" -- i.e. the direction to the adjacent cell.<br>
     *            Example: if facing = Direction.ABOVE, get the iAdj value for the cell above iCell in the grid.
     * 
     * @return The linear index of the adjacent cell in the specified Direction.<br>
     *         If iCell is on the edge of the grid, and the adjacent cell is off the grid, the result depends upon the
     *         grids torus state: a no-torus grid returns the {@link RectangularGridGeometry#OFF_GRID_INDEX}, but an
     *         is-torus grid returns the cell index of the opposite side or corner of the grid.
     * 
     */
    public int getAdjacentI(int iCell, Direction facing) {
        int xCell = getX(iCell);
        int yCell = getY(iCell);
        return getI(xCell + facing.getDx(), yCell + facing.getDy());
    }
        
    /**
     * Get the linear indices of the cells adjacent to iCell in each direction specified by the facings argument.
     * 
     * @param iCell
     *            The linear index of the cell to find the adjacent-cell location for.
     * @param facings
     *            The {link@ Direction}s away from iCell to get adjacent cell indices for.
     * @return A List of linear indices for the adjacent cells in the specified directions from iCell. If iCell is on
     *         the edge of the grid, and a given adjacent cell is off the grid, its value depends upon the grids torus
     *         state: a no-torus grid returns the {@link RectangularGridGeometry#OFF_GRID_INDEX} for that adjacent cell,
     *         but an is-torus grid returns the cell index of the opposite side or corner of the grid.
     */
    public List<Integer> getAdjacentIndices(int iCell, Direction[] facings) {
        List<Integer> adjCells = new ArrayList<Integer>(facings.length);
        int xCell = getX(iCell);
        int yCell = getY(iCell);
        for (Direction d : facings) {
            adjCells.add(getI(xCell + d.getDx(), yCell + d.getDy()));
        }
        return adjCells;
    }
    
    /**
     * Returns a brief description of this object's state.<br>
     * The following gives and example of the current format:
     * <pre>
     * {RectangularGridGeometry ColCount=5 RowCount=7 IsTorus=true}
     * </pre>
     * The above format is not canonical, and subject to possible change in future iterations of the code.
     */
    @Override
    public String toString() {
        return String.format("{%1$s ColCount=%2$s RowCount=%3$s IsTorus=%4$s}", this.getClass().getSimpleName(), colCount, rowCount, isTorus);
    }
    
    /**
     * Computes dividend % divisor, wrapping negative results by the divisor.
     * <p>
     * References:<br>
     * Wikipedia's
     * <a href="https://en.wikipedia.org/wiki/Modulo_operation">Modulo operation</a>
     * article, and Stackoverflow's items 
     * <a href="https://stackoverflow.com/questions/2215318/difference-between-modulus-implementation-in-python-vs-java">
     * Difference Between Modulus Implementation in Python Vs Java
     * </a>
     * and
     * <a href="https://stackoverflow.com/questions/3417183/modulo-of-negative-numbers/3417242#3417242">Modulo of negative numbers [duplicate]</a>
     * The code here is from Stephen Canon's answer to the last of these references.
     * 
     * @param dividend
     * @param divisor
     * @return the result of dividend % divisor wrapped about the [0, divisor] range. 
     */
    public static int wrappedModulo(int dividend, int divisor) {
        dividend = dividend % divisor;
        return dividend < 0 ? dividend + divisor : dividend;
    }
}
