package org.jca;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PiltonWorldEngineTest
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
    public void testPiltonWorldEngine() {
        PiltonWorldEngine pwEngine = new PiltonWorldEngine();
        assertThat("initial timestep zero", pwEngine.getTimestep(), equalTo(0));
        assertThat("no initial particles", pwEngine.getParticles().isEmpty(), equalTo(true));
    }

    /**
     * Tests the first 10 steps of a {@link PiltonWorldEngine}.
     * The engine object is configured according to the original paper's description
     * and its first 10 timesteps (including start state) should match the values
     * noted in the paper.
     * <p>
     * Admittedly, this is more of a regression test than a simple unit test.
     */
    @Test
    public void testDoSimulationStep() {
        List<List<PiltonParticle>> allExpected = new ArrayList<>();
        allExpected.add(buildParticles(new int[] {3,2,1})); // t=0
        allExpected.add(buildParticles(new int[] {0,0, 0,2, 2,0, 2,2}, 1)); // t=1
        allExpected.add(buildParticles(new int[] {5,5, 5,3, 3,5, 3,3}, 1)); // t=2
        allExpected.add(buildParticles(new int[] {5,5, 5,0, 0,5, 0,0}, 1)); // t=3
        allExpected.add(buildParticles(new int[] {6,6, 5,6, 6,5, 5,5, 6,3, 5,3, 3,6, 3,5, 3,3}, 1)); // t=4
        allExpected.add(buildParticles(new int[] {0,0,4, 4,2,2, 2,4,2, 6,6,1, 6,4,1, 4,6,1, 4,4,1})); // t=5
        allExpected.add(buildParticles(new int[] {0,0,5, 2,4,2, 4,2,2, 0,2,1, 2,0,1, 2,2,1})); // t=6
        allExpected.add(buildParticles(new int[] {0,0,5, 2,4,3, 4,2,3, 2,2,1})); // t=7
        allExpected.add(buildParticles(new int[] {0,0,6, 2,4,3, 4,2,3})); // t=8
        allExpected.add(buildParticles(new int[] {0,0,6, 5,3,3, 3,5,3})); // t=9
        
        PiltonWorldEngine pwEngine = new PiltonWorldEngine();
        pwEngine.setParticles(allExpected.get(0));
        
        final String fmt = "t=%1$s expect=%2$s actual=%3$s";
        
        List<PiltonParticle> expect = allExpected.get(pwEngine.getTimestep());
        List<PiltonParticle> actual = pwEngine.getParticles();
        String msg = String.format(fmt, pwEngine.getTimestep(), expect, actual);
        assertTrue("Initial " + msg, orderIgnoredEqual(actual, expect));
        
        while (pwEngine.getTimestep() < 10) {
            pwEngine.doSimulationStep();
            expect = allExpected.get(pwEngine.getTimestep());
            actual = pwEngine.getParticles();
            msg = String.format(fmt, pwEngine.getTimestep(), expect, actual);
            assertTrue(msg, orderIgnoredEqual(actual, expect));
        }
    }
    
    
    @Test
    public void testCoalesceParticles() {
        List<PiltonParticle> allParticles = buildParticles(new int[] {0,0, 4,2, 2,4, 0,0, 5,5, 2,4, 0,0, 4,2, 0,0}, 1);
        List<PiltonParticle> expect = new ArrayList<>(buildParticles(new int[] {0,0,4, 4,2,2, 2,4,2, 5,5,1}));
        PiltonWorldEngine pwEngine = new PiltonWorldEngine();
        List<PiltonParticle> actual = pwEngine.coalesceParticles(allParticles);
        String msg = String.format("expect=%1$s actual=%2$s", expect, actual);
        assertTrue(msg, orderIgnoredEqual(actual, expect));
    }

    @Test
    public void testFindMolecule() {
        List<PiltonParticle> allParticles = buildParticles(new int[] {6,6, 5,6, 6,5, 5,5, 6,3, 5,3, 3,6, 3,5, 3,3}, 1);
        
        Set<PiltonParticle> expect1 = new HashSet<>(buildParticles(new int[] {6,6, 5,6, 6,5, 5,5}, 1)); 
        Set<PiltonParticle> expect2 = new HashSet<>(buildParticles(new int[] {6,3, 5,3}, 1)); 
        Set<PiltonParticle> expect3 = new HashSet<>(buildParticles(new int[] {3,6, 3,5}, 1)); 
        Set<PiltonParticle> expect4 = new HashSet<>(buildParticles(new int[] {3,3}, 1)); 
        
        Map<PiltonParticle, Set<PiltonParticle>> allExpect = new HashMap<>();
        expect1.stream().forEach(p -> allExpect.put(p, expect1));
        expect2.stream().forEach(p -> allExpect.put(p, expect2));
        expect3.stream().forEach(p -> allExpect.put(p, expect3));
        expect4.stream().forEach(p -> allExpect.put(p, expect4));

        PiltonWorldEngine pwEngine = new PiltonWorldEngine();
        
        for (PiltonParticle p : allExpect.keySet()) {
            Set<PiltonParticle> expect = allExpect.get(p);
            Set<PiltonParticle> actual = pwEngine.findMolecule(p, allParticles);
            String msg = String.format("p=%1$s expect=%2$s actual=%3$s", p, expect, actual);
            assertTrue(msg, orderIgnoredEqual(actual, expect));
        }
    }
    
    //---- Helper Methods
    
    // NOTE 2018-5-04
    // Using a private check-method because the recommended Hamcrest method doesn't seem
    // to be availble:
    // import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
    // See:
    // https://stackoverflow.com/questions/22807328/assertequals-2-lists-ignore-order
    
    private static boolean orderIgnoredEqual(Collection<PiltonParticle> particles1, Collection<PiltonParticle> particles2) {
        for (PiltonParticle p : particles2) {
            if (!particles1.contains(p)) {
                return false;
            }
        }
        return true;
    }
    
    private static List<PiltonParticle> buildParticles(int[] pv, int mass) {
        final int particleCount = pv.length - 1;
        List<PiltonParticle> nu = new ArrayList<>();
        for (int i = 0; i < particleCount; i += 2) {
            nu.add(new PiltonParticle(pv[i], pv[i+1], mass));
        }
        return nu;
    }

    private static List<PiltonParticle> buildParticles(int[] pv) {
        final int particleCount = pv.length - 2;
        List<PiltonParticle> nu = new ArrayList<>();
        for (int i = 0; i < particleCount; i += 3) {
            nu.add(new PiltonParticle(pv[i], pv[i+1], pv[i+2]));
        }
        return nu;
    }

}
