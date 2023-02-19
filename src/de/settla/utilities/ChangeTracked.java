package de.settla.utilities;

public interface ChangeTracked {

    boolean isDirty();

    void setDirty(boolean dirty);

}
