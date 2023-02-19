package de.settla.utilities.local.region.form;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import de.settla.utilities.local.region.Rotatable;

public abstract class Form implements Formable, Rotatable, FormSerializable {
	
	/**
	 * Register basic forms like BLOCK, CUBOID...
	 */
	public static void registerForms() {
		
		registerForm("UNITE", new Function<Map<String,Object>, Form>() {
			@Override
			public Form apply(Map<String, Object> map) {
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> array = (List<Map<String, Object>>) map.get("region");
				Map<String, Object> o1 = (Map<String, Object>) array.get(0);
				Map<String, Object> o2 = (Map<String, Object>) array.get(1);
				return unite(deserialize(o1), deserialize(o2));
			}
		});
		registerForm("INTERSECTION", new Function<Map<String,Object>, Form>() {
			@Override
			public Form apply(Map<String, Object> map) {
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> array = (List<Map<String, Object>>) map.get("region");
				Map<String, Object> o1 = (Map<String, Object>) array.get(0);
				Map<String, Object> o2 = (Map<String, Object>) array.get(1);
				return intersect(deserialize(o1), deserialize(o2));
			}
		});
		registerForm("CHUNK", map -> new ChunkForm(map));
		registerForm("CUBOID", map -> new Cuboid(map));
		registerForm("BLOCK_CUBOID", map -> new BlockCuboid(map));
		registerForm("EMPTY", map -> new Empty());
	}
	
	/**
	 * Unite two forms.
	 * 
	 * @param form1
	 * @param form2
	 * @return a new form.
	 */
	public static Form unite(Form form1, Form form2) {
		checkNotNull(form1);
		checkNotNull(form2);
		return new Form() {
			
			@Override
			public boolean overlaps(Vector vector) {
				return form1.overlaps(vector) || form2.overlaps(vector);
			}
			
			@Override
			public Vector minimum() {
				return Vector.getMinimum(form1.minimum(), form2.minimum());
			}
			
			@Override
			public Vector maximum() {
				return Vector.getMaximum(form1.maximum(), form2.maximum());
			}
			
			@Override
			public boolean intersect(Form form) {
				return form1.intersect(form) || form2.intersect(form);
			}

			@Override
			public Form move(Vector vector) {
				return unite(form1.move(vector), form2.move(vector));
			}

			@Override
			public Map<String, Object> serialize() {
				Map<String, Object> object = super.serialize();
				List<Map<String, Object>> array = new ArrayList<>();
				array.add(form1.serialize());
				array.add(form2.serialize());
				object.put("region", array);
				return object;
			}
			
			@Override
			public String type() {
				return "UNITE";
			}

			@Override
			public Form rotate90(Vector origin) {
				return unite((Form)form1.rotate90(origin), (Form)form2.rotate90(origin));
			}
		};
	}
	
	/**
	 * Intersect two forms.
	 * 
	 * @param form1
	 * @param form2
	 * @return a new form.
	 */
	public static Form intersect(Form form1, Form form2) {
		checkNotNull(form1);
		checkNotNull(form2);
		return new Form() {
			
			@Override
			public boolean overlaps(Vector vector) {
				return form1.overlaps(vector) && form2.overlaps(vector);
			}
			
			@Override
			public Vector minimum() {
				return Vector.getMinimum(form1.minimum(), form2.minimum());
			}
			
			@Override
			public Vector maximum() {
				return Vector.getMaximum(form1.maximum(), form2.maximum());
			}
			
			@Override
			public boolean intersect(Form form) {
				return form1.intersect(form) && form2.intersect(form);
			}

			@Override
			public Form move(Vector vector) {
				return intersect(form1.move(vector), form2.move(vector));
			}
			
			@Override
			public Map<String, Object> serialize() {
				Map<String, Object> object = super.serialize();
				List<Map<String, Object>> array = new ArrayList<>();
				array.add(form1.serialize());
				array.add(form2.serialize());
				object.put("region", array);
				return object;
			}

			@Override
			public String type() {
				return "INTERSECTION";
			}

			@Override
			public Form rotate90(Vector origin) {
				return intersect((Form)form1.rotate90(origin), (Form)form2.rotate90(origin));
			}
		};
	}
	
	/**
	 * Checks the bound intersection of the form with this.
	 * 
	 * @param form the form to check.
	 * @return true if the form intersect the bound of this form.
	 */
	public boolean boundIntersect(Form form) {
		checkNotNull(form);
		
        Vector max2 = form.maximum();
        Vector min1 = minimum();

        if (max2.getX() < min1.getX() || max2.getY() < min1.getY() || max2.getZ() < min1.getZ()) return false;
        
        Vector min2 = form.minimum();
        Vector max1 = maximum();
        
        if (min2.getX() > max1.getX() || min2.getY() > max1.getY() || min2.getZ() > max1.getZ()) return false;
        
        return true;
	}

    /**
     * Check to see if a point is inside this region.
     *
     * @param x the x coordinate to check
     * @param y the y coordinate to check
     * @param z the z coordinate to check
     * @return whether this region contains the point
     */
    public boolean overlaps(int x, int y, int z) {
        return overlaps(new Vector(x, y, z));
    }
    
	private static Map<String, DeserialForm> forms = new ConcurrentHashMap<>();
	private static final Object lock = new Object();
	
	/**
	 * Register a new form.
	 * 
	 * @param type the type of the new form.
	 * @param function which describes the deserialization. 
	 */
	public static void registerForm(String type, Function<Map<String, Object>, Form> function) {
		synchronized (lock) {
			forms.put(type, new DeserialForm(function));
		}
	}
	
	/**
	 * Construct a new instance.
	 *
	 * @param object
	 *            JSONObject
	 * @return region
	 */
	public static Form deserialize(Map<String, Object> map) {
		String type = (String)map.get("type");
		if(type == null)
			return null;
		synchronized (lock) {
			DeserialForm deserialForm = forms.get(type);
			return deserialForm == null ? null : deserialForm.function.apply(map);
		}
	}
	
	private static class DeserialForm {
		
		private final Function<Map<String, Object>, Form> function;
		
		public DeserialForm(Function<Map<String, Object>, Form> function) {
			this.function = function;
		}
		
	}
	
}
