package org.jca;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.jca.RectangularGridGeometry.Direction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RectangularGridGeometryTest
{

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testRectangularGridGeometryIntIntBoolean() {
        // Tests the smallest allowable grid (1x1) for both no/is-Torus
        RectangularGridGeometry grid = new RectangularGridGeometry(1, 1, false);
        for (int iAdj : grid.getAdjacentIndices(0, Direction.values())) {
            assertThat("1x1 noTorus", iAdj, equalTo(RectangularGridGeometry.OFF_GRID_INDEX));
        }
        grid = new RectangularGridGeometry(1, 1, true);
        for (int iAdj : grid.getAdjacentIndices(0, Direction.values())) {
            assertThat("1x1 isTorus", iAdj, equalTo(0));
        }
    }
    
    @Test
    public void testRotateLeft() {
        assertThat("rotateLeft", Direction.ABOVE.rotateLeft(), equalTo(Direction.ABOVE_LEFT));
    }
    
    @Test 
    public void testRotateRight() {
        assertThat("rotateRight", Direction.ABOVE_LEFT.rotateRight(), equalTo(Direction.ABOVE));
    }
    
    @Test
    public void testRotateLeft90() {
        Direction[] expect = { Direction.LEFT, Direction.BELOW, Direction.RIGHT, Direction.ABOVE };
        Direction source = Direction.ABOVE;
        for (int i=0; i < 12; ++i) {
            Direction actual = source.rotateLeft90();
            assertThat(String.format("Rotate 90 ccw source=%1$s", source), actual, equalTo(expect[i % 4]));
            source = actual;
        }
    }

    @Test
    public void testRotateRight90() {
        Direction[] expect = { Direction.LEFT, Direction.ABOVE, Direction.RIGHT, Direction.BELOW };
        Direction source = Direction.BELOW;
        for (int i=0; i < 12; ++i) {
            Direction actual = source.rotateRight90();
            assertThat(String.format("Rotate 90 cw source=%1$s", source), actual, equalTo(expect[i % 4]));
            source = actual;
        }
    }
    
    @Test
    public void testRotate180() {
        Direction[] source = { Direction.BELOW, Direction.BELOW_LEFT, Direction.LEFT, Direction.BELOW_RIGHT };
        Direction[] expect = { Direction.ABOVE, Direction.ABOVE_RIGHT, Direction.RIGHT, Direction.ABOVE_LEFT };
        for (int i=0; i < source.length; ++i) {
            assertThat(String.format("Opposite source[%1$s]=%2$s", i, source[i]), source[i].rotate180(), equalTo(expect[i]));
        }
    }

    @Test
    public void testSquareGridGeometry() {
        int rows = 8;
        int cols = 6;
        boolean isTorus = true;
        RectangularGridGeometry grid = new RectangularGridGeometry(cols, rows, isTorus);
        assertThat(grid, not(equalTo(null)));
    }
    
    @Test
    public void testToString() {
        // This test expects a specific format.
        // It replicates what the toString() override does and confirms the output is the same.
        int rows = 8;
        int cols = 6;
        boolean isTorus = true;
        RectangularGridGeometry grid = new RectangularGridGeometry(cols, rows, isTorus);
        String expect = String.format("{%1s ColCount=%2$s RowCount=%3$s IsTorus=%4$s}", grid.getClass().getSimpleName(), cols, rows, isTorus);
        String actual = grid.toString();
        assertThat(actual, equalTo(expect));
    }

    @Test
    public void testGetI() {
        int rows = 3;
        int cols = 5;
        boolean isTorus = false;
        String fmt = "5x3 noTorus x=%1$s y=%2$s";
        RectangularGridGeometry grid = new RectangularGridGeometry(cols, rows, isTorus);
        int expectI = 0;
        for (int y = 0; y < rows; ++y) {
            for (int x = 0; x < cols; ++x) {
                int actualI = grid.getI(x, y);
                assertThat(String.format(fmt, x, y), actualI, equalTo(expectI));
                ++expectI;
            }
        }
        
        isTorus = true;
        fmt = "5x3 isTorus x=%1$s y=%2$s";
        grid = new RectangularGridGeometry(cols, rows, isTorus);

        int x = 5;
        int y = 3;
        expectI = 0;
        int actualI = grid.getI(x, y);
        assertThat(String.format(fmt, x, y), actualI, equalTo(expectI));

        x = 6;
        y = 4;
        actualI = grid.getI(x, y);
        expectI = 6;
        assertThat(String.format(fmt, x, y), actualI, equalTo(expectI));

        x = 7;
        y = 5;
        expectI = 12;
        actualI = grid.getI(x, y);
        assertThat(String.format(fmt, x, y), actualI, equalTo(expectI));
    }

    @Test
    public void testGetX() {
        //  0  1  2  3  4  5  6
        //  7  8  9 10 11 12 13
        // 14 15 16 17 18 19 20
        // 21 22 23 24 25 26 27
        // 28 29 30 31 32 33 34
        int rows = 5;
        int cols = 7;
        boolean isTorus = true;
        RectangularGridGeometry grid = new RectangularGridGeometry(cols, rows, isTorus);

        int expect = 2;
        for (int iCell = 2; iCell < 35; iCell += 8) {
            int actual = grid.getX(iCell);
            assertThat(String.format("%1$s i=%2$s", grid, iCell), actual, equalTo(expect));
            expect++;
        }
    }

    @Test
    public void testGetY() {
        //  0  1  2  3  4  
        //  5  6  7  8  9 
        // 10 11 12 13 14
        // 15 16 17 18 19
        // 20 21 22 23 24
        // 25 26 27 28 29
        // 30 31 32 33 34
        int rows = 7;
        int cols = 5;
        boolean isTorus = true;
        RectangularGridGeometry grid = new RectangularGridGeometry(cols, rows, isTorus);

        int expect = 1;
        for (int iCell = 5; iCell < 30; iCell += 6) {
            int actual = grid.getY(iCell);
            String msg = String.format("%1$s i=%2$s", grid, iCell);
            //System.out.println(msg);
            assertThat(msg, actual, equalTo(expect));
            expect++;
        }
    }
    
    @Test
    public void testGetAdjacentIndex() {
        RectangularGridGeometry grid = new RectangularGridGeometry(4, 5, false);
        assertThat("noTorus", grid.getAdjacentI(0, Direction.ABOVE_LEFT), equalTo(RectangularGridGeometry.OFF_GRID_INDEX));

        grid = new RectangularGridGeometry(4, 5, true);
        assertThat("noTorus", grid.getAdjacentI(0, Direction.ABOVE_LEFT), equalTo(grid.getCellCount() - 1));
    }

    @Test
    public void testGetAdjacentIndices() {
        int rows = 3;
        int cols = 3;
        boolean isTorus = false;
        RectangularGridGeometry grid = new RectangularGridGeometry(cols, rows, isTorus);
        // 3x3 Test Grid:
        // 0 1 2
        // 3 4 5 
        // 6 7 8
        
        // Basic Tests: results should be the same isTorus || noTorus.
        
        List<Integer> expectAdj = Arrays.asList(1, 5, 7, 3);  // order is Above, Right, Below, Left
        List<Integer> actualAdj = grid.getAdjacentIndices(4, Direction.Sides);  // center of 3x3 grid
        assertThat(grid + " Sides adj for center cell", actualAdj, equalTo(expectAdj));
        
        expectAdj = Arrays.asList(2, 8, 6, 0); // order is Above-R, Below-R, Below-L, Above-L
        actualAdj = grid.getAdjacentIndices(4, Direction.Corners);
        assertThat(grid + " Corners adj for center cell", actualAdj, equalTo(expectAdj));
        
        expectAdj = Arrays.asList(1, 2, 5, 8, 7, 6, 3, 0);  // all adjacent cells, clockwise from Above.
        actualAdj = grid.getAdjacentIndices(4, Direction.values());
        assertThat(grid + "All adj for center cell", actualAdj, equalTo(expectAdj));
        
        // Corner Test: tests differences between isTorus vs noTorus
        final int x = RectangularGridGeometry.OFF_GRID_INDEX;
        expectAdj = Arrays.asList(5, x, x, x, x, x, 7, 4);  // all adjacent cells, clockwise from Above.
        actualAdj = grid.getAdjacentIndices(8, Direction.values());
        assertThat(grid + "All adj for lower-left cell", actualAdj, equalTo(expectAdj));
        
        isTorus = true;
        grid = new RectangularGridGeometry(cols, rows, isTorus);
        
        expectAdj = Arrays.asList(5, 3, 6, 0, 2, 1, 7, 4);  // all adjacent cells, clockwise from Above.
        actualAdj = grid.getAdjacentIndices(8, Direction.values());
        assertThat(grid + "All adj for lower-left cell", actualAdj, equalTo(expectAdj));
    }
    
    @Test
    public void testWrappedModulo() {
        int[] testValues = { -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5 };
        int[] expectRaw  = { -2, -1,  0, -2, -1, 0, 1, 2, 0, 1, 2 };
        int[] expectWrap = {  1,  2,  0,  1,  2, 0, 1, 2, 0, 1, 2 };
        int base = 3;
        for (int i = 0; i < testValues.length; ++i) {
            int testVal = testValues[i];
            int actualRaw = testVal % base;
            int actualWrap = RectangularGridGeometry.wrappedModulo(testVal, base);
            //System.out.println(testVal + " % " + base + " = " + actualRaw + " | " + actualWrap);
            assertThat("raw mod", actualRaw, equalTo(expectRaw[i]));
            assertThat("wrap mod", actualWrap, equalTo(expectWrap[i]));
        }
    }
    // The above code produces:
    // -5 % 3 = -2 | 1
    // -4 % 3 = -1 | 2
    // -3 % 3 =  0 | 0
    // -2 % 3 = -2 | 1
    // -1 % 3 = -1 | 2
    //  0 % 3 =  0 | 0
    //  1 % 3 =  1 | 1
    //  2 % 3 =  2 | 2
    //  3 % 3 =  0 | 0
    //  4 % 3 =  1 | 1
    //  5 % 3 =  2 | 2
    //
    // Contrast above with Python behavior:
    //
    // Python 3.6.2 (v3.6.2:5fd33b5926, Jul 16 2017, 20:11:06)
    // [GCC 4.2.1 (Apple Inc. build 5666) (dot 3)] on darwin
    // Type "help", "copyright", "credits" or "license" for more information.
    // >>> for i in range(-5,6): print("{0} % {1} = {2}".format(i,3,(i%3)))
    // -5 % 3 = 1
    // -4 % 3 = 2
    // -3 % 3 = 0
    // -2 % 3 = 1
    // -1 % 3 = 2
    //  0 % 3 = 0
    //  1 % 3 = 1
    //  2 % 3 = 2
    //  3 % 3 = 0
    //  4 % 3 = 1
    //  5 % 3 = 2
    // >>>
}
