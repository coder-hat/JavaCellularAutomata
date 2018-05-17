package org.jca;

import static java.util.stream.Collectors.toList;
import static org.jca.RectangularGridGeometry.wrappedModulo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.IntStream;

/**
 * Implements the tiny universe described in Barry Pilton's "a small world" article, which originally appeared in pages
 * 49-53 of <a href="https://ianstewartjoat.weebly.com/manifold-5.html">Issue 5, 1969</a> of the University of Warwick's
 * student-run <a href="https://ianstewartjoat.weebly.com/manifold-magazine.html">Manifold Magazine</a>.<br>
 * The same article was later collected into
 * <a href="https://www.amazon.com/Seven-Years-Manifold-1968-1980-Stewart/dp/0906812070">Seven Years of Manifold</a>.
 * <p>
 * Pilton's "world" makes for a small, but nontrivial programming exercise. It is several times more complicated than
 * printing Hello World or computing Fibonacci Sequence values, but its implementation is agreeably small and does not
 * take that long (with some careful thought). Different languages encourage different implementations.
 * <p>
 * 2018-4-29<br>
 * This implementation is currently at the "seems to work" stage, and has not been cleaned up or refactored into "good"
 * code.
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

    /**
     * The set of {@link PiltonParticles} that comprise the PiltonWorldEngine's "universe" at the current
     * {@link PiltonWorldEngine#timestep timestep}.
     */
    private List<PiltonParticle> nowParticles;

    public PiltonWorldEngine() {
        reset();
    }

    public static void main(String[] args) {
        PiltonWorldEngine pwEngine = new PiltonWorldEngine();

        List<PiltonParticle> particles = new ArrayList<>();
        particles.add(new PiltonParticle(3, 2, 1));
        pwEngine.setParticles(particles);

        IntStream.range(0, 5).forEach(i -> {
            pwEngine.doSimulationStep();
            pwEngine.printParticles();
        });
    }

    /**
     * Resets this object's world state to time zero, with no particles.
     */
    public void reset() {
        timestep = 0;
        nowParticles = new ArrayList<>();
    }

    public int getTimestep() {
        return timestep;
    }

    public List<PiltonParticle> getParticles() {
        return new ArrayList<>(nowParticles);
    }

    public void setParticles(List<PiltonParticle> particles) {
        nowParticles = new ArrayList<>(particles);
    }

    public void doSimulationStep() {
        timestep += 1; // time must increment before particle processing
        nowParticles = coalesceParticles(decayParticles(coalesceParticles(moveParticles(nowParticles))));
    }

    protected Set<PiltonParticle> findMolecule(PiltonParticle p, List<PiltonParticle> particles) {
        Set<PiltonParticle> molecule = new HashSet<>();
        molecule.add(p);
        List<PiltonParticle> adjacents = particles.stream().filter(o -> p.isAdjacent(o, CELL_COLS, CELL_ROWS))
                .collect(toList());
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

    /**
     * Moves (or leaves unmoved) the specified particle according to the rules in Pilton's original paper , and relative
     * to the current {@link PiltonWorldEngine#timestep timestep}.<br>
     * (See {@link PiltonWorldEngine#moveParticles(List) moveParticles} method's description for additional details of
     * the movement rules.)
     * 
     * @param p
     *            The particle to (possibly) move.
     * @param others
     *            All the other particles in the world state that are not part of particle p's molecule.
     *            
     * @return A new particle at the new position, or the same particle if it does not move.
     */
    protected PiltonParticle moveParticle(PiltonParticle p, List<PiltonParticle> others) {
        if (timestep % p.mass() == 0) {
            int x = 1 + others.stream().mapToInt(PiltonParticle::x).sum();
            int y = 1 + others.stream().mapToInt(PiltonParticle::y).sum();
            return new PiltonParticle(x % CELL_COLS, y % CELL_ROWS, p.mass());
        } else {
            return p;
        }
    }

    /**
     * Moves (or leaves unmoved) each of the specified particles according to the rules in Pilton's original paper, and
     * relative to the current {@link PiltonWorldEngine#timestep timestep}.<br>
     * <blockquote> ... Heavy particles move slowly; so a particle of mass m will only move at time t if m divides
     * t.<br>
     * The Law of Motion will be:<br>
     * Xp(t) = SUM(Xq(t-1)) + 1<br>
     * Yp(t) = SUM(Yq(t-1)) + 1<br>
     * where the SUMmations are over all particles q not in the same molecule as p. (The empty sum, of course, is zero.)
     * </blockquote>
     * 
     * @param particles
     *            The particles to (possibly) move.
     *            
     * @return A new list of particles in the new positions.
     */
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

    /**
     * Implements a Comparator that compares PiltonParticle objects based only on their x and y location values.
     * Particle mass is ignored. The ordering prioritizes x over y (sort by column, then by row), so, for example,
     * particles x3y0m1, x5y0m2, and x6,y0m2 are all "less" than x0y1m1.
     * <p>
     * The {@link PiltonWorldEngine#coalesceParticles(List) coalesceParticles} method uses a {@link TreeMap} and this
     * Comparator implementation to aggregate masses at specific locations.
     * <p>
     * The coalesceParticles code could instantiate this inline as a lambda or anonymous function, but declaring and
     * defining it here avoids repeated instances.
     * 
     * @author ksdj (coder-hat)
     */
    private static class LocationComparator implements Comparator<PiltonParticle>
    {
        @Override
        public int compare(PiltonParticle p1, PiltonParticle p2) {
            if (p1.x() < p2.x()) {
                return -1;
            } else if (p1.x() > p2.x()) {
                return 1;
            } else {
                if (p1.y() < p2.y()) {
                    return -1;
                } else if (p1.y() > p2.y()) {
                    return 1;
                }
            }
            return 0;
        }
    }

    /**
     * Single-instance of the {@link LocationComparator} required by the coalesceParticles code.
     */
    private static final Comparator<PiltonParticle> PARTICLE_LOCATION_COMPARATOR = new LocationComparator();

    protected List<PiltonParticle> coalesceParticles(List<PiltonParticle> particles) {
        Map<PiltonParticle, Integer> nu = new TreeMap<>(PARTICLE_LOCATION_COMPARATOR);
        particles.stream().forEach(p -> nu.put(p, (nu.containsKey(p) ? nu.get(p) + p.mass() : p.mass())));
        return nu.entrySet().stream().map(en -> new PiltonParticle(en.getKey().x(), en.getKey().y(), en.getValue()))
                .collect(toList());
    }

    /**
     * Determines whether or not the specified particle can decay, and how it will decay for the current timestep.<br>
     * According to the original paper (with "t" denoting the current timestep) <blockquote> ... a particle of mass m at
     * (x,y) will decay if and only if<br>
     * (1) t is a multiple of m,<br>
     * (2) x or y == t (mod 7)<br>
     * If x == t (mod 7), it decays into two particles, each of mass m, at points (x+/-1, y).<br>
     * If y == t (mod 7), it decays into two particles, each of mass m, at (x,y+/-1).<br>
     * If both x and y = t, it does both, becoming four particles at (x+/-1,y+/-1). </blockquote>
     * 
     * @param p
     *            The particle to (possibly) decay.
     * 
     * @return a list containing either the original particle, or the particles it decayed into.
     */
    protected List<PiltonParticle> decayParticle(PiltonParticle p) {
        List<PiltonParticle> nu = new ArrayList<>();
        if (timestep % p.mass() == 0) {
            boolean xDecays = p.x() == (timestep % CELL_COLS);
            boolean yDecays = p.y() == (timestep % CELL_ROWS);
            if (xDecays && yDecays) {
                nu.add(new PiltonParticle(wrappedModulo(p.x() - 1, CELL_COLS), wrappedModulo(p.y() - 1, CELL_ROWS),
                        p.mass()));
                nu.add(new PiltonParticle(wrappedModulo(p.x() + 1, CELL_COLS), wrappedModulo(p.y() - 1, CELL_ROWS),
                        p.mass()));
                nu.add(new PiltonParticle(wrappedModulo(p.x() - 1, CELL_COLS), wrappedModulo(p.y() + 1, CELL_ROWS),
                        p.mass()));
                nu.add(new PiltonParticle(wrappedModulo(p.x() + 1, CELL_COLS), wrappedModulo(p.y() + 1, CELL_ROWS),
                        p.mass()));
            } else if (xDecays) {
                nu.add(new PiltonParticle(wrappedModulo(p.x() - 1, CELL_COLS), p.y(), p.mass()));
                nu.add(new PiltonParticle(wrappedModulo(p.x() + 1, CELL_COLS), p.y(), p.mass()));
            } else if (yDecays) {
                nu.add(new PiltonParticle(p.x(), wrappedModulo(p.y() - 1, CELL_ROWS), p.mass()));
                nu.add(new PiltonParticle(p.x(), wrappedModulo(p.y() + 1, CELL_ROWS), p.mass()));
            } else {
                nu.add(p);
            }
        } else {
            nu.add(p);
        }
        return nu;
    }

    /**
     * Performs {@link PiltonWorldEngine#decayParticle(PiltonParticle) decayParticle} for each particle in the specified
     * list of particles, and returns the list of all resulting particles.
     * 
     * @param particles
     *            The list of particles to decay.
     * @return A list of the resulting particles.
     */
    protected List<PiltonParticle> decayParticles(List<PiltonParticle> particles) {
        return particles.stream().flatMap(p -> decayParticle(p).stream()).collect(toList());
    }

    // ----- Helpful debug methods

    public void printParticles() {
        System.out.println("t=" + timestep);
        System.out.println(nowParticles);
    }
}
