package org.jca;

import java.util.List;
import java.util.Objects;

/**
 * Contains the state (2-d position and mass) of a single Pilton Particle.
 *  
 * @author ksdj (code-hat)
 */
final class PiltonParticle
{
    private int x;
    private int y;
    private int mass;
    
    public PiltonParticle(int x, int y, int mass) {
        this.x = x;
        this.y = y;
        this.mass = mass;
    }
    
    public PiltonParticle(PiltonParticle other) {
        this.x = other.x;
        this.y = other.y;
        this.mass = other.mass;
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
     * @return This particle's mass.
     */
    public int mass() {
        return mass;
    }
    
    /**
     * Determines whether the specified other particle occupies the same location as this particle.
     * 
     * @param other
     *            The particle to test for colocation.
     *            
     * @return true if the other particle and this particle are colocated, otherwise false.
     */
    public boolean isColocated(PiltonParticle other) {
        return other.x() == x && other.y() == y;
    }
    
    public boolean isAdjacent(PiltonParticle other, int numberOfCols, int numberOfRows) {
        boolean leftMatch = other.x() == RectangularGridGeometry.wrappedModulo(x - 1, numberOfCols);
        boolean rightMatch = other.x() == RectangularGridGeometry.wrappedModulo(x + 1, numberOfCols);
        boolean aboveMatch = other.y() == RectangularGridGeometry.wrappedModulo(y - 1, numberOfRows);
        boolean belowMatch = other.y() == RectangularGridGeometry.wrappedModulo(y + 1, numberOfRows);
        return ((leftMatch || rightMatch) && other.y == y) || ((aboveMatch || belowMatch) && other.x == x);
    }
    
    public boolean isAdjacentToAny(List<PiltonParticle> others, int numberOfCols, int numberOfRows) {
        return others.stream().anyMatch(p -> this.isAdjacent(p, numberOfCols, numberOfRows));
    }
    
    @Override public int hashCode() {
        return Objects.hash(x, y, mass);
    }
    
    @Override public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof PiltonParticle)) return false;
        PiltonParticle o = (PiltonParticle)(obj);
        return o.x == x && o.y == y && o.mass == mass;
    }
    
    @Override public String toString() {
        return String.format("x%1$sy%2$sm%3$s", x, y, mass);
    }
}