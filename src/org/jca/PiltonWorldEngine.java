package org.jca;

import static java.util.stream.Collectors.toList;
import static org.jca.RectangularGridGeometry.wrappedModulo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;


/**
 * Implements the tiny universe described in Barry Pilton's "a small world" article, which originally appeared in
 * pages 49-53 of <a href="https://ianstewartjoat.weebly.com/manifold-5.html">Issue 5, 1969</a> of the University of
 * Warwick's student-run <a href="https://ianstewartjoat.weebly.com/manifold-magazine.html">Manifold Magazine</a>.<br>
 * The same article was later collected into
 * <a href="https://www.amazon.com/Seven-Years-Manifold-1968-1980-Stewart/dp/0906812070">Seven Years of Manifold</a>.
 * <p>
 * Pilton's "world" makes for a small, but nontrivial programming exercise.
 * It is several times more complicated than printing Hello World or computing Fibonacci Sequence values,
 * but its implementation is agreeably small and does not take that long (with some careful thought).
 * Different languages encourage different implementations.
 * <p>
 * 2018-4-29<br>
 * This implementation is currently at the "seems to work" stage, and has not been cleaned up or refactored into "good" code.
 * 
 * @author ksdj (coder-hat)
 */
public class PiltonWorldEngine
{
    public static final int CELL_COLS = 7;
    public static final int CELL_ROWS = 7;
    
    /**
     * The current time step of this {@link PiltonWorldEngine} object.
     */
    private int timestep;
    
    private List<PiltonParticle> nowParticles;
    
    
    public PiltonWorldEngine() {
        resetWorld();
    }
    
    
    public static void main(String[] args) {
        PiltonWorldEngine pwEngine = new PiltonWorldEngine();
        
        List<PiltonParticle> particles = new ArrayList<>();
        particles.add(new PiltonParticle(3, 2, 1));
        pwEngine.setParticles(particles);
        
        IntStream.range(0, 5).forEach(i -> {pwEngine.doSimulationStep(); pwEngine.printParticles();});
    }
    
    /**
     * Resets this object's world state to time zero, with no particles.
     */
    public void resetWorld() {
        timestep = 0;
        nowParticles = new ArrayList<>();
    }
    
    public void setParticles(List<PiltonParticle> particles) {
        nowParticles = new ArrayList<>(particles);
    }
    
    public void doSimulationStep() {
        timestep += 1;  // time must increment before particle processing
        nowParticles = decayParticles(coalesceParticles(moveParticles(nowParticles)));
    }
    
    protected Set<PiltonParticle> findMolecule(PiltonParticle p, List<PiltonParticle> particles) {
        Set<PiltonParticle> molecule = new HashSet<>();
        molecule.add(p);
        List<PiltonParticle> adjacents = particles.stream().filter(o -> p.isAdjacent(o, CELL_COLS, CELL_ROWS)).collect(toList());
        if (!adjacents.isEmpty()) {            
            for (PiltonParticle o : adjacents) {
                List<PiltonParticle> remaining = new LinkedList<PiltonParticle>(particles);
                remaining.remove(p);
                remaining.removeAll(adjacents);
                molecule.addAll(findMolecule(o, remaining));
            }
            return molecule;
        } else {
            return molecule;
        }
    }
    
    protected PiltonParticle moveParticle(PiltonParticle p, List<PiltonParticle> others) {
        if (timestep % p.mass() == 0) {
            int x = 1 + others.stream().mapToInt(PiltonParticle::x).sum();
            int y = 1 + others.stream().mapToInt(PiltonParticle::y).sum();
            return new PiltonParticle(x % CELL_COLS, y % CELL_ROWS, p.mass());
        } else {
            return new PiltonParticle(p);
        }
    }
    
    protected List<PiltonParticle> moveParticles(List<PiltonParticle> particles) {
        List<PiltonParticle> nxtParticles = new ArrayList<>();
        LinkedList<PiltonParticle> unprocessed = new LinkedList<>(particles);
        while (!unprocessed.isEmpty()) {
            PiltonParticle p = unprocessed.removeFirst();
            Set<PiltonParticle> molecule = findMolecule(p, particles);
            List<PiltonParticle> notMolecule = new LinkedList<>(particles);
            notMolecule.removeAll(molecule);
            for (PiltonParticle m : molecule) {
                nxtParticles.add(moveParticle(m, notMolecule));
            }
            unprocessed.removeAll(molecule);
        }
        return nxtParticles;
    }
    
    protected List<PiltonParticle> coalesceParticles(List<PiltonParticle> particles) {
        Map<PiltonParticle, Integer> nu = new HashMap<>();
        for (PiltonParticle p : particles) {
            if (!nu.containsKey(p)) {
                nu.put(p, p.mass());
            } else {
                nu.put(p, nu.get(p) + p.mass());
            }
        }
        return nu.entrySet().stream().map(en -> new PiltonParticle(en.getKey().x(), en.getKey().y(), en.getValue())).collect(toList());
    }
    
    protected List<PiltonParticle> decayParticle(PiltonParticle p) {
        List<PiltonParticle> nu = new ArrayList<>();
        if (timestep % p.mass() == 0) {
            boolean xDecays = p.x() == (timestep % CELL_COLS);
            boolean yDecays = p.y() == (timestep % CELL_ROWS);
            if (xDecays && yDecays) {
                nu.add(new PiltonParticle(wrappedModulo(p.x() - 1, CELL_COLS), wrappedModulo(p.y() - 1, CELL_ROWS), p.mass()));
                nu.add(new PiltonParticle(wrappedModulo(p.x() + 1, CELL_COLS), wrappedModulo(p.y() - 1, CELL_ROWS), p.mass()));
                nu.add(new PiltonParticle(wrappedModulo(p.x() - 1, CELL_COLS), wrappedModulo(p.y() + 1, CELL_ROWS), p.mass()));
                nu.add(new PiltonParticle(wrappedModulo(p.x() + 1, CELL_COLS), wrappedModulo(p.y() + 1, CELL_ROWS), p.mass()));
            } else if (xDecays) {
                nu.add(new PiltonParticle(wrappedModulo(p.x() - 1, CELL_COLS), p.y(), p.mass()));
                nu.add(new PiltonParticle(wrappedModulo(p.x() + 1, CELL_COLS), p.y(), p.mass()));               
            } else if (yDecays) {
                nu.add(new PiltonParticle(p.x(), wrappedModulo(p.y() - 1, CELL_ROWS), p.mass()));
                nu.add(new PiltonParticle(p.x(), wrappedModulo(p.y() + 1, CELL_ROWS), p.mass()));
            } else {
                nu.add(new PiltonParticle(p));
            }
        } else {
            nu.add(new PiltonParticle(p));
        }
        return nu;
    }
    
    protected List<PiltonParticle> decayParticles(List<PiltonParticle> particles) {
        return particles.stream().flatMap(p -> decayParticle(p).stream()).collect(toList());
    }
    
    //----- Helpful debug methods
    
    public void printParticles() {
        System.out.println("t=" + timestep);
        System.out.println(nowParticles);
    }
    
    public void printWorld() {
        char[][] cg = new char[CELL_COLS][CELL_ROWS];
        for (int y = 0; y < CELL_ROWS; ++y) {
            for (int x = 0; x < CELL_COLS; ++x) {
                cg[x][y] = '.';
            }
        }
        nowParticles.stream().forEach(p -> cg[p.x()][p.y()] = 'O');
        
        System.out.println("t=" + timestep);
        for (int y = 0; y < CELL_ROWS; ++y) {
            for (int x = 0; x < CELL_COLS; ++x) {
                System.out.print(cg[x][y]);
            }
            System.out.println();
        }
    }
}

