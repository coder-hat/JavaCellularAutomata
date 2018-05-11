package org.jca;

import java.util.Objects;

/**
 * Contains the state (2-d position and mass) of a single Pilton Particle, as described in 
 * <a href="https://ianstewartjoat.weebly.com/manifold-5.html">Pilton's original article</a>.
 * In the article particles are defined as having position in 2-d, x,y space, and integer mass.
 * <p>
 * This class tries to abide by Joshua Bloch's "Minimize Mutability" rules:
 * <ol>
 * <li>Don't provide any methods that modify the object's state.</li>
 * <li>Ensure that the class can't be extended.</li>
 * <li>Make all fields final.</li>
 * <li>Make all fields private.</li>
 * <li>Ensure exclusive access to any mutable components.</li>
 * </ol>
 *  
 * @author ksdj (code-hat)
 */
final class PiltonParticle
{
    private final int x;
    private final int y;
    private final int mass;
    
    private final int hashValue;
    private final String textValue;
    
    /**
     * Constructs a {@link PiltonParticle} with the specified state.
     * @param x Zero-based x-coordinate (column index) of particle's location.
     * @param y Zero-based y-coordinate (row index) of particle's location. 
     * @param mass the particle's mass.
     */
    public PiltonParticle(int x, int y, int mass) {
        this.x = x;
        this.y = y;
        this.mass = mass;
        this.hashValue = Objects.hash(x, y, mass);
        this.textValue = String.format("x%1$sy%2$sm%3$s", x, y, mass);
    }
        
    /**
     * @return The x-coordinate of this particle's location.
     */
    public int x() {
        return x;
    }
    
    /**
     * @return The y-coordinate of this particle's location.
     */
    public int y() {
        return y;
    }
    
    /**
     * @return The particle's mass.
     */
    public int mass() {
        return mass;
    }
    
    /**
     * Determines whether the specified other particle occupies the same location as this particle.
     * (i.e. whether the particles have the same x and y coordinates.)
     * 
     * @param other
     *            The particle to test for colocation.
     *            
     * @return true if the other particle and this particle are colocated, otherwise false.
     */
    public boolean isColocated(PiltonParticle other) {
        return other.x() == x && other.y() == y;
    }
    
    /**
     * Determines whether this particle is adjacent to the specified other particle in the context of the specified grid
     * dimensions.
     * <p>
     * To be adjacent, the other particle must be either directly (in the adjacent location) above, below, left or right
     * of this particle. Diagonal adjacency is ignored. The row and column coordinates wrap, so particles at opposite
     * ends of the same row or column are considered adjacent.<br>
     * Example:<br>
     * In the following 7x7 grid, A is adjacent to B and C, E is adjacent to F, and D is not adjacent to any other particles.
     * <pre>
     *    | 0 | 1 | 2 | 3 | 4 |
     * ---+---+---+---+---+---+
     *  0 |   |   |   |   | F |
     * ---+---+---+---+---+---+
     *  1 | C |   |   |   |   |
     * ---+---+---+---+---+---+
     *  2 | A |   |   |   | B |
     * ---+---+---+---+---+---+
     *  3 |   | D |   |   |   |
     * ---+---+---+---+---+---+
     *  4 | E |   |   |   |   |
     * ---+---+---+---+---+---+
     * </pre>
     * 
     * @param other
     *            The {@link PiltonParticle} to test for adjacency to.
     * 
     * @param numberOfCols
     *            the number of columns (X-dimension) in the grid.
     * @param numberOfRows
     *            the number of rows (Y-dimension) in the grid.
     * @return true if other particle is adjacent, false otherwise.
     */
    public boolean isAdjacent(PiltonParticle other, int numberOfCols, int numberOfRows) {
        boolean leftMatch = other.x() == RectangularGridGeometry.wrappedModulo(x - 1, numberOfCols);
        boolean rightMatch = other.x() == RectangularGridGeometry.wrappedModulo(x + 1, numberOfCols);
        boolean aboveMatch = other.y() == RectangularGridGeometry.wrappedModulo(y - 1, numberOfRows);
        boolean belowMatch = other.y() == RectangularGridGeometry.wrappedModulo(y + 1, numberOfRows);
        return ((leftMatch || rightMatch) && other.y == y) || ((aboveMatch || belowMatch) && other.x == x);
    }
    
    @Override public int hashCode() {
        return hashValue;
    }
    
    @Override public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof PiltonParticle)) return false;
        PiltonParticle o = (PiltonParticle)(obj);
        return o.x == x && o.y == y && o.mass == mass;
    }
    
    @Override public String toString() {
        return textValue;
    }
}