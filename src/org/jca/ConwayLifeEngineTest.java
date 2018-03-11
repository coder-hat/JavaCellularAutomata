package org.jca;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.jca.ConwayLifeEngine.CellState;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class ConwayLifeEngineTest {
    
    private static CellState[] blinkerA5x5 = {
            CellState.DEAD, CellState.DEAD, CellState.DEAD, CellState.DEAD, CellState.DEAD,
            CellState.DEAD, CellState.DEAD, CellState.LIVE, CellState.DEAD, CellState.DEAD, 
            CellState.DEAD, CellState.DEAD, CellState.LIVE, CellState.DEAD, CellState.DEAD, 
            CellState.DEAD, CellState.DEAD, CellState.LIVE, CellState.DEAD, CellState.DEAD, 
            CellState.DEAD, CellState.DEAD, CellState.DEAD, CellState.DEAD, CellState.DEAD
    };
    private static CellState[] blinkerB5x5 = {
            CellState.DEAD, CellState.DEAD, CellState.DEAD, CellState.DEAD, CellState.DEAD,
            CellState.DEAD, CellState.DEAD, CellState.DEAD, CellState.DEAD, CellState.DEAD, 
            CellState.DEAD, CellState.LIVE, CellState.LIVE, CellState.LIVE, CellState.DEAD, 
            CellState.DEAD, CellState.DEAD, CellState.DEAD, CellState.DEAD, CellState.DEAD, 
            CellState.DEAD, CellState.DEAD, CellState.DEAD, CellState.DEAD, CellState.DEAD
    };
    private ConwayLifeEngine blinkerEngine = new ConwayLifeEngine(5, 5, true);

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        blinkerEngine.setGridState(blinkerA5x5);
    }

    @After
    public void tearDown() throws Exception {
    }

// The ctor implicitly tested by other test methods.    
//    @Test
//    public void testConwayLifeEngine() {
//        
//    }

    @Test
    public void testGetGrid() {
        RectangularGridGeometry actual = blinkerEngine.getGrid();
        assertThat("cols", actual.getColCount(), equalTo(5));
        assertThat("rows", actual.getRowCount(), equalTo(5));
    }

    @Test
    public void testDoSimulationStep() {
        assertTrue("initial state", sameGridState(blinkerEngine, blinkerA5x5));
        for (int iStep = 1; iStep < 5; ++iStep) {
            blinkerEngine.doSimulationStep();
            // blinkerEngine.printGridState();      
            CellState[] expectGridState = (iStep % 2 == 0) ? blinkerA5x5 : blinkerB5x5;
            assertTrue("iStep=" + iStep, sameGridState(blinkerEngine, expectGridState));
        }
    }

    @Test
    public void testGetNextState() {
        assertTrue("initial state", sameGridState(blinkerEngine, blinkerA5x5));
        Map<Integer, CellState> expect = new HashMap<>(); // <iCell, CELL_STATE>
        expect.put(7, CellState.DEAD);
        expect.put(12, CellState.LIVE);
        expect.put(17, CellState.DEAD);
        for (int iCell : expect.keySet()) {
            CellState actual = blinkerEngine.getNextState(iCell);
            assertThat("iCell=" + iCell, actual, equalTo(expect.get(iCell)));
        }
    }

    @Test
    public void testGetLiveCount() {
        //  0  1  2  3  4
        //  5  6  7  8  9
        // 10 11 12 13 14
        // 15 16 17 18 19
        // 20 21 22 23 24
        assertTrue("initial state", sameGridState(blinkerEngine, blinkerA5x5));
        Map<Integer, Integer> expect = new HashMap<>(); // <iCell, adjLiveCount>
        expect.put(7, 1);
        expect.put(12, 2);
        expect.put(17, 1);
        for (int iCell : expect.keySet()) {
            int actual = blinkerEngine.getAdjacentLiveCount(iCell);
            assertThat("iCell=" + iCell, actual, equalTo(expect.get(iCell)));
        }
    }

    private boolean sameGridState(ConwayLifeEngine lifeEngine, CellState[] expectGridState) {
        for (int iCell = 0; iCell < lifeEngine.getGrid().getCellCount(); ++iCell) {
            if (lifeEngine.getState(iCell) != expectGridState[iCell]) return false;
        }
        return true;
    }
}
