package org.jca;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

import org.jca.LangtonAntEngine.CellState;
import org.jca.RectangularGridGeometry.Direction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LangtonAntEngineTest
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
    public void testMoveAnt() {
        int moves = 7;
        CellState[] expect = { 
                CellState.WHITE, CellState.WHITE, CellState.WHITE, CellState.WHITE, CellState.WHITE,
                CellState.WHITE, CellState.WHITE, CellState.BLACK, CellState.BLACK, CellState.WHITE,
                CellState.WHITE, CellState.WHITE, CellState.WHITE, CellState.BLACK, CellState.WHITE,
                CellState.WHITE, CellState.BLACK, CellState.BLACK, CellState.WHITE, CellState.WHITE,
                CellState.WHITE, CellState.WHITE, CellState.WHITE, CellState.WHITE, CellState.WHITE,
                CellState.WHITE, CellState.WHITE, CellState.WHITE, CellState.WHITE, CellState.WHITE,
        };
        int expectLocation = 11;
        Direction expecFacing = Direction.ABOVE;
        
        LangtonAntEngine antEngine = new LangtonAntEngine(5, 5, false, Direction.LEFT);
        
        for (int i=0; i < moves; i++) {
            antEngine.doSimulationStep();
        }

        assertThat(String.format("ant location after %1$s moves", moves), antEngine.getAntLocation(), equalTo(expectLocation));
        assertThat(String.format("ant facing after %1$s moves", moves), antEngine.getAntFacing(), equalTo(expecFacing));
        for (int i=0; i < 25; i++) {
            assertThat(String.format("cell[%1$s]", i), antEngine.getState(i), equalTo(expect[i]));
        }
    }

}
