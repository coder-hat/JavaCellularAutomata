package org.jca;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
    /**
     * A Map&lt;timestep, List&lt;PiltonParticle&gt;&gt; object that contains the states (timestep and Particles) of
     * Pilton's Small World listed in <a href="https://ianstewartjoat.weebly.com/manifold-5.html">the text of the
     * original article</a>, assuming an initial state of: &lt;0, [x3y2m1]&gt;.
     * <p>
     * The keySet() of timesteps is not contiguous -- the original article elided some steps in the example provided --
     * hence the use of a Map instead of a List or Array; the timestep cannot be implied by index.
     */
    private final static Map<Integer, List<PiltonParticle>> EXPECT_WORLD_SEQUENCE;
    static {
        EXPECT_WORLD_SEQUENCE = new HashMap<>();
        EXPECT_WORLD_SEQUENCE.put(0,buildParticles(new int[] {3,2,1}));
        EXPECT_WORLD_SEQUENCE.put(1,buildParticles(new int[] {0,0, 0,2, 2,0, 2,2}, 1));
        EXPECT_WORLD_SEQUENCE.put(2,buildParticles(new int[] {5,5, 5,3, 3,5, 3,3}, 1));
        EXPECT_WORLD_SEQUENCE.put(3,buildParticles(new int[] {5,5, 5,0, 0,5, 0,0}, 1));
        EXPECT_WORLD_SEQUENCE.put(4,buildParticles(new int[] {6,6, 5,6, 6,5, 5,5, 6,3, 5,3, 3,6, 3,5, 3,3}, 1));
        EXPECT_WORLD_SEQUENCE.put(5,buildParticles(new int[] {0,0,4, 4,2,2, 2,4,2, 6,6,1, 6,4,1, 4,6,1, 4,4,1}));
        EXPECT_WORLD_SEQUENCE.put(6,buildParticles(new int[] {0,0,5, 2,4,2, 4,2,2, 0,2,1, 2,0,1, 2,2,1}));
        EXPECT_WORLD_SEQUENCE.put(7,buildParticles(new int[] {0,0,5, 2,4,3, 4,2,3, 2,2,1}));
        EXPECT_WORLD_SEQUENCE.put(8,buildParticles(new int[] {0,0,6, 2,4,3, 4,2,3}));
        EXPECT_WORLD_SEQUENCE.put(9,buildParticles(new int[] {0,0,6, 5,3,3, 3,5,3})); 
        EXPECT_WORLD_SEQUENCE.put(12,buildParticles(new int[] {2,2,6, 4,6,3, 6,4,3})); 
        EXPECT_WORLD_SEQUENCE.put(15,buildParticles(new int[] {2,2,6, 2,0,3, 0,2,3})); 
        EXPECT_WORLD_SEQUENCE.put(18,buildParticles(new int[] {3,3,6, 3,5,3, 5,3,3})); 
        EXPECT_WORLD_SEQUENCE.put(21,buildParticles(new int[] {3,3,6, 2,6,3, 2,1,3, 6,2,3, 1,2,3})); 
        EXPECT_WORLD_SEQUENCE.put(24,buildParticles(new int[] {5,5,6, 6,2,3, 6,0,3, 2,6,3, 0,6,3})); 
        EXPECT_WORLD_SEQUENCE.put(27,buildParticles(new int[] {5,5,6, 0,4,3, 0,5,3, 0,0,6, 5,0,3})); 
        EXPECT_WORLD_SEQUENCE.put(30,buildParticles(new int[] {3,3,6, 6,1,6, 1,1,6, 1,6,6})); 
        EXPECT_WORLD_SEQUENCE.put(36,buildParticles(new int[] {2,2,6, 6,4,6, 4,4,6, 4,6,6})); 
        EXPECT_WORLD_SEQUENCE.put(42,buildParticles(new int[] {1,1,6, 4,6,6, 6,6,6, 6,4,6}));
        // Bottom half of sequence from the original paper commented-out below.
        // Based on the rules described in the same paper, this portion of the sequence is actually wrong.
        // Hypothesis: there was a typo or miscalculation made in the original example sequence.
        // By-hand calculation (according to the paper's rules) from t=15 through t=42 gives
        // the bottom half of the sequence above.
        //EXPECT_WORLD_SEQUENCE.put(15,buildParticles(new int[] {2,2,6, 3,0,3, 0,3,3})); 
        //EXPECT_WORLD_SEQUENCE.put(18,buildParticles(new int[] {3,3,6, 3,5,6, 3,6,3, 5,3,6, 6,3,3, 5,5,6})); 
        //EXPECT_WORLD_SEQUENCE.put(21,buildParticles(new int[] {3,3,6, 3,5,6, 6,1,3, 5,3,6, 1,6,3, 5,5,6})); 
        //EXPECT_WORLD_SEQUENCE.put(24,buildParticles(new int[] {0,0,6, 0,5,6, 4,2,3, 5,0,6, 2,4,3, 5,5,6})); 
        //EXPECT_WORLD_SEQUENCE.put(27,buildParticles(new int[] {0,0,6, 0,1,3, 1,0,3, 0,5,6, 1,5,3, 5,0,6, 5,1,3, 5,5,6})); 
        //EXPECT_WORLD_SEQUENCE.put(30,buildParticles(new int[] {3,3,12, 3,1,9, 1,3,9, 6,6,6})); 
        //EXPECT_WORLD_SEQUENCE.put(36,buildParticles(new int[] {4,4,12, 4,6,9, 6,4,9, 0,0,6, 0,2,6, 2,0,6, 2,2,6})); 
    }
    
    final static String fmt = "t=%1$s expect=%2$s actual=%3$s";

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
    
    @Test
    public void testSetAndGetParticles() {
        PiltonWorldEngine pwEngine = new PiltonWorldEngine();
        assertThat("no initial particles", pwEngine.getParticles().isEmpty(), equalTo(true));
        for (int t : EXPECT_WORLD_SEQUENCE.keySet()) {
            List<PiltonParticle> expect = EXPECT_WORLD_SEQUENCE.get(t);
            pwEngine.setParticles(EXPECT_WORLD_SEQUENCE.get(t));
            List<PiltonParticle> actual = pwEngine.getParticles();
            assertThat(String.format(fmt, t, expect, actual), actual, equalTo(expect));
        }
    }
    
    @Test
    public void testDoSimulationStep() {
        PiltonWorldEngine pwEngine = new PiltonWorldEngine();
        pwEngine.setParticles(EXPECT_WORLD_SEQUENCE.get(0));
        
        final int maxTimestep = EXPECT_WORLD_SEQUENCE.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
        assertTrue("Has-data check", maxTimestep > 0);
        
        while (pwEngine.getTimestep() < maxTimestep) {
            pwEngine.doSimulationStep();
            final int t = pwEngine.getTimestep();
            if (EXPECT_WORLD_SEQUENCE.containsKey(t)) {
                List<PiltonParticle> expect = EXPECT_WORLD_SEQUENCE.get(t);
                List<PiltonParticle> actual = pwEngine.getParticles();
                String msg = String.format(fmt, pwEngine.getTimestep(), expect, actual);
                assertTrue(msg, orderIgnoredEqual(actual, expect));
            }
        }
    }
    
    @Test
    public void testCoalesceParticles() {
        PiltonWorldEngine pwEngine = new PiltonWorldEngine();
        
        List<PiltonParticle> allParticles = buildParticles(new int[] {0,0, 4,2, 2,4, 0,0, 5,5, 2,4, 0,0, 4,2, 0,0}, 1);
        List<PiltonParticle> expect = buildParticles(new int[] {0,0,4, 4,2,2, 2,4,2, 5,5,1});
        List<PiltonParticle> actual = pwEngine.coalesceParticles(allParticles);
        String msg = String.format("expect=%1$s actual=%2$s", expect, actual);
        assertTrue(msg, orderIgnoredEqual(actual, expect));
        
        allParticles = buildParticles(new int[] {0,0,4, 4,2,2, 2,4,2, 0,0,1, 0,2,1, 2,0,1, 2,2,1});
        expect = buildParticles(new int[] {0,0,5, 2,4,2, 4,2,2, 0,2,1, 2,0,1, 2,2,1});
        actual = pwEngine.coalesceParticles(allParticles);
        msg = String.format("expect=%1$s actual=%2$s", expect, actual);
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
    // to be available:
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
