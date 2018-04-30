package org.jca;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PiltonParticleTest
{
    // "Reference" particle
    final static PiltonParticle expectP;
    final static int expectX;
    final static int expectY;
    final static int expectMass;
    static {
        expectX = 3;
        expectY = 2;
        expectMass = 1;
        expectP = new PiltonParticle(expectX, expectY, expectMass);
    }
    
    
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

//    @Test
//    public void testHashCode() {
//        fail("Not yet implemented");
//    }

    @Test
    public void testPiltonParticle() {
        PiltonParticle p = new PiltonParticle(1, 2, 3);
        assertTrue("class check", p.getClass() == PiltonParticle.class);
    }

    @Test
    public void testX() {
        assertThat("Reference Particle", expectP.x(), equalTo(expectX));
    }

    @Test
    public void testY() {
        assertThat("Reference Particle", expectP.y(), equalTo(expectY));
    }

    @Test
    public void testMass() {
        assertThat("Reference Particle", expectP.mass(), equalTo(expectMass));
    }

    @Test
    public void testIsAdjacent() {
        final int cols = 7;
        final int rows = 7;

        PiltonParticle p1 = new PiltonParticle(6, 0, 1);
        Map<PiltonParticle, Boolean> pOthers = new HashMap<>();
        pOthers.put(new PiltonParticle(5, 6, 1), false);
        pOthers.put(new PiltonParticle(6, 6, 1), true);
        pOthers.put(new PiltonParticle(0, 6, 1), false);
        pOthers.put(new PiltonParticle(5, 0, 1), true);
        pOthers.put(new PiltonParticle(6, 0, 1), false);
        pOthers.put(new PiltonParticle(0, 0, 1), true);
        pOthers.put(new PiltonParticle(5, 1, 1), false);
        pOthers.put(new PiltonParticle(6, 1, 1), true);
        pOthers.put(new PiltonParticle(0, 1, 1), false);
        for (PiltonParticle po : pOthers.keySet()) {
            String msg = String.format("p1=%1$s po=%2$s", p1, po);
            assertThat(msg, p1.isAdjacent(po, cols, rows), equalTo(pOthers.get(po)));
        }

        p1 = new PiltonParticle(0, 6, 1);
        pOthers.clear();
        pOthers.put(new PiltonParticle(6, 5, 1), false);
        pOthers.put(new PiltonParticle(0, 5, 1), true);
        pOthers.put(new PiltonParticle(1, 1, 1), false);
        pOthers.put(new PiltonParticle(6, 6, 1), true);
        pOthers.put(new PiltonParticle(0, 6, 1), false);
        pOthers.put(new PiltonParticle(1, 6, 1), true);
        pOthers.put(new PiltonParticle(6, 0, 1), false);
        pOthers.put(new PiltonParticle(0, 0, 1), true);
        pOthers.put(new PiltonParticle(1, 0, 1), false);
    }
    
    @Test
    public void testIsAdjacentToAny() {        
        List<PiltonParticle> particles = new ArrayList<>();
        int[] x = {6, 5, 6};
        int[] y = {6, 6, 5};
        for (int i=0; i < y.length; ++i) {
            particles.add(new PiltonParticle(x[i], y[i], i));
        }

        PiltonParticle p = new PiltonParticle(5, 5, 1);
        assertThat(p.isAdjacentToAny(particles, 7, 7), equalTo(true));
    }

    @Test
    public void testEqualsObject() {
        PiltonParticle p = new PiltonParticle(expectX, expectY, expectMass);
        assertThat("all equal", p.equals(expectP), equalTo(true));
        
        p = new PiltonParticle(expectX + 1, expectY, expectMass);
        assertThat("x off-by-1", p.equals(expectP), equalTo(false));
        
        p = new PiltonParticle(expectX, expectY + 1, expectMass);
        assertThat("y off-by-1", p.equals(expectP), equalTo(false));
        
        p = new PiltonParticle(expectX, expectY, expectMass + 1);
        assertThat("mass off-by-1", p.equals(expectP), equalTo(false));
    }

    @Test
    public void testToString() {
        String expect = String.format("x%1$sy%2$sm%3$s", expectX, expectY, expectMass);
        String actual = expectP.toString();
        assertThat("Reference Particle", actual, equalTo(expect));
    }

}
