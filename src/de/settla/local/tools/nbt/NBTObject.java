package de.settla.local.tools.nbt;

public class NBTObject {

	protected Object nbt;
	
	protected NBTObject(){}
	
	public NBTObject(Object nbt) {
		this.nbt = nbt;
	}
	
	//int
	public int getInt(String key){
		return Ref.NBT.getInt(nbt, key);
	}
	
	public NBTObject setInt(String key, int val){
		Ref.NBT.setInt(nbt, key, val);
		return this;
	}
	
	//string
	public String getString(String key){
		return Ref.NBT.getString(nbt, key);
	}
	
	public NBTObject setString(String key, String val){
		Ref.NBT.setString(nbt, key, val);
		return this;
	}

	//double
	public double getDouble(String key){
		return Ref.NBT.getDouble(nbt, key);
	}
	
	public NBTObject setDouble(String key, double val){
		Ref.NBT.setDouble(nbt, key, val);
		return this;
	}
	
	//long
	public NBTObject setLong(String key, long val){
		Ref.NBT.setLong(nbt, key, val);
		return this;
	}
	
	public long getLong(String key){
		return (long) Ref.NBT.getLong(nbt, key);
	}
	
	public Object getNBT(){
		return nbt;
	}
	
}
