package slp;

public class TypeEntry {
	private final int entryId;
	private final String entryName;
	private final boolean isPrimitive;
	private final Class entryClass;
	
	public TypeEntry(int entryId,String entryName){
		this.entryId=entryId;
		this.entryName=entryName;
		this.isPrimitive=true;
		this.entryClass=null;
	}
	
	public TypeEntry(int entryId,String entryName, Class entryClass){
		this.entryId=entryId;
		this.entryName=entryName;
		this.isPrimitive=false;
		this.entryClass=entryClass;
	}

	public int getEntryId() {
		return entryId;
	}
	
	public String getEntryName() {
		return entryName;
	}

	public boolean isPrimitive() {
		return isPrimitive;
	}

	public Class getEntryClass() {
		return entryClass;
	}

	
	
}
