package de.settla.utilities.local.region;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.regex.Pattern;

import de.settla.utilities.ChangeTracked;
import de.settla.utilities.local.region.ChunkManager.RegionRegistery;
import de.settla.utilities.local.region.events.RegionListener;
import de.settla.utilities.local.region.form.Form;
import de.settla.utilities.local.region.form.Vector;
import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.Storable;

@Serial("Region")
public class Region extends RegionListener implements ChangeTracked, Comparable<Region>, Regionable, Rotatable, Storable {

	private static final Pattern VALID_ID_PATTERN = Pattern.compile("^[A-Za-z0-9_,'\\-\\+/]{1,}$");

	private final RegionRegistery regionRegistery = new RegionRegistery(this);
	private final String id;
	private final boolean transientRegion;
	private final Object lock = new Object();
	private int priority = 0;
	private Form form;
	private boolean dirty = true;

	@Override
	public Map<String, Object> serialize() {
		synchronized (lock) {
			Map<String, Object> map = Storable.super.serialize();
			map.put("form", form.serialize());
			map.put("priority", priority);
			map.put("id", id);
			map.put("trans", transientRegion);
			return map;
		}
	}

	/**
	 * Construct a new instance of this region.
	 *
	 * @param id
	 *            the name of this region
	 * @param transientRegion
	 *            whether this region should only be kept in memory and not be
	 *            saved
	 * @throws IllegalArgumentException
	 *             thrown if the ID is invalid (see {@link #isValidId(String)}
	 */
	public Region(Form form, int priority, boolean transientRegion, String id) {
		super();
		this.form = form;
		this.priority = priority;
		this.transientRegion = transientRegion;
		checkNotNull(id);

		if (!isValidId(id)) {
			throw new IllegalArgumentException("Invalid region ID: " + id);
		}

		this.id = Normal.normalize(id);
	}

	@SuppressWarnings("unchecked")
	public Region(Map<String, Object> map) {
		super();
		this.form = Form.deserialize((Map<String, Object>) map.get("form"));
		this.priority = ((Double) map.get("priority")).intValue();
		this.id = (String) map.get("id");
		this.transientRegion = (Boolean) map.get("trans");
	}

	/**
	 * @return the regionregistery of this region.
	 */
	public RegionRegistery getRegionRegistery() {
		synchronized (lock) {
			return regionRegistery;
		}
	}

	/**
	 * Gets the name of this region.
	 *
	 * @return the name
	 */
	public String id() {
		synchronized (lock) {
			return id;
		}
	}

	/**
	 * Gets the form of this region.
	 * 
	 * @return the form
	 */
	public Form getForm() {
		synchronized (lock) {
			return form;
		}
	}

	/**
	 * Sets the form of the region.
	 * 
	 * @param form
	 *            the form to set
	 */
	public void setForm(Form form) {
		synchronized (lock) {
			setDirty(true);
			this.form = form;
		}
	}

	/**
	 * Get the priority of the region, where higher numbers indicate a higher
	 * priority.
	 *
	 * @return the priority
	 */
	public int getPriority() {
		synchronized (lock) {
			return priority;
		}
	}

	/**
	 * Set the priority of the region, where higher numbers indicate a higher
	 * priority.
	 *
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(int priority) {
		synchronized (lock) {
			setDirty(true);
			this.priority = priority;
		}
	}

	/**
	 * @return <code>true</code> if this region should only be kept in memory
	 *         and not be saved
	 */
	public boolean isTransient() {
		synchronized (lock) {
			return transientRegion;
		}
	}

	/**
	 * @return <code>true</code> if this region is not transient and changes
	 *         have been made.
	 */
	@Override
	public boolean isDirty() {
		synchronized (lock) {
			if (isTransient()) {
				return false;
			}
			return dirty;
		}
	}

	@Override
	public void setDirty(boolean dirty) {
		synchronized (lock) {
			this.dirty = dirty;
		}
	}

	@Override
	public boolean equals(Object obj) {
		synchronized (lock) {
			if (!(obj instanceof Region)) {
				return false;
			}

			Region other = (Region) obj;
			return other.id().equals(id());
		}
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public int compareTo(Region other) {
		synchronized (lock) {
			if (getPriority() > other.getPriority()) {
				return -1;
			} else if (getPriority() < other.getPriority()) {
				return 1;
			}

			return id().compareTo(other.id());
		}
	}

	/**
	 * Checks to see if the given ID is a valid ID.
	 *
	 * @param id
	 *            the id to check
	 * @return whether the region id given is valid
	 */
	public static boolean isValidId(String id) {
		checkNotNull(id);
		return VALID_ID_PATTERN.matcher(id).matches();
	}

	@Override
	public Vector minimum() {
		synchronized (lock) {
			return form.minimum();
		}
	}

	@Override
	public Vector maximum() {
		synchronized (lock) {
			return form.maximum();
		}
	}

	@Override
	public boolean overlaps(Vector vector) {
		synchronized (lock) {
			return form.overlaps(vector);
		}
	}

	@Override
	public boolean intersect(Region region) {
		synchronized (lock) {
			return form.boundIntersect(region.form) ? form.intersect(region.form) : false;
		}
	}

	@Override
	public Region move(Vector vector) {
		synchronized (lock) {
			form = form.move(vector);
			return this;
		}
	}

	@Override
	public Region rotate90(Vector origin) {
		synchronized (lock) {
			form = (Form) form.rotate90(origin);
			return this;
		}
	}
	
}
