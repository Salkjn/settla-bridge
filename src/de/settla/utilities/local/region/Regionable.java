package de.settla.utilities.local.region;

import de.settla.utilities.local.region.form.Vector;

public interface Regionable {

	/**
	 * @return vector the minimum.
	 */
	Vector minimum();
	
	/**
	 * @return vector the maximum.
	 */
	Vector maximum();
	
	/**
     * Check to see if a point is inside this region.
     *
     * @param vector The point to check
     * @return Whether {@code pt} is in this region
     */
	boolean overlaps(Vector vector);
	
	/**
	 * Check to see if a region has intersection with this region.
	 * 
	 * @param region the region to test.
	 * @return true if the region intersect with this region.
	 */
	boolean intersect(Region region);
	
	/**
	 * Moves this region by the vector.
	 * 
	 * @param vector the vector to move.
	 * @return a new region instance.
	 */
	Regionable move(Vector vector);
	
}
