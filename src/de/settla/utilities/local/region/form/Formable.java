package de.settla.utilities.local.region.form;

public interface Formable {
	
	/**
	 * @return the type of this form.
	 */
	String type();
	
	/**
	 * @return vector the minimum.
	 */
	Vector minimum();
	
	/**
	 * @return vector the maximum.
	 */
	Vector maximum();
	
	/**
     * Check to see if a point is inside this form.
     *
     * @param vector The point to check
     * @return Whether {@code pt} is in this form
     */
	boolean overlaps(Vector vector);
	
	/**
	 * Check to see if a form has intersection with this form.
	 * 
	 * @param form the form to test.
	 * @return true if the form intersect with this form.
	 */
	boolean intersect(Form form);
	
	/**
	 * Moves this form by the vector.
	 * 
	 * @param vector the vector to move.
	 * @return a new form instance.
	 */
	Form move(Vector vector);
	
}
