package de.settla.utilities.local.region;

import static com.google.common.base.Preconditions.checkNotNull;

import java.text.Normalizer;
import java.text.Normalizer.Form;

import javax.annotation.Nullable;

/**
 * Normal names are strings that are considered equal after they have been
 * normalized using Unicode's NFC form and made lowercase.
 */
public final class Normal {

    private final String name;
    @Nullable
    private final String normal;

    /**
     * Create a new instance.
     *
     * @param name a new instance
     */
    private Normal(String name) {
        checkNotNull(name);

        this.name = name;
        String normal = normalize(name);
        if (!normal.equals(name)) { // Simple comparison
            this.normal = normal;
        } else {
            this.normal = null;
        }
    }

    /**
     * Get the original name before normalization.
     *
     * @return the original name before normalization
     */
    public String getName() {
        return name;
    }

    /**
     * Get the normalized name.
     *
     * @return the normal name
     */
    public String getNormal() {
        return normal != null ? normal : name;
    }

    /**
     * Normalize a string according to the rules of this class.
     *
     * @param name an string
     * @return the normalized string
     */
    public static String normalize(String name) {
        return Normalizer.normalize(name.toLowerCase(), Form.NFC);
    }

    /**
     * Create a new instance.
     *
     * @param name the name
     * @return an instance
     */
    public static Normal normal(String name) {
        return new Normal(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Normal that = (Normal) o;
        
        return getNormal().equals(that.getNormal());

    }

    @Override
    public int hashCode() {
        return getNormal().hashCode();
    }

    /**
     * Return the un-normalized name.
     *
     * @return the un-normalized name
     */
    @Override
    public String toString() {
        return name;
    }

}
