package slp;

public  class SymbolEntry {
	private final String entryName;
	private final int lineOfDefinition;
	private final TypeEntry type;
	//private final ReferenceRole role;
	
	public SymbolEntry(String entryName, TypeEntry typeId, int lineOfDefinition){
		this.entryName=entryName;
		this.type=typeId;
		this.lineOfDefinition=lineOfDefinition;
		//this.role=role;
	}
	
	
	public TypeEntry getEntryTypeID(){
		return type;
	}
	
	public String getEntryName(){
		return entryName;
	}
	
	public int definedAt() {
		return lineOfDefinition;
	}

//	public ReferenceRole getRole() {
//		return role;
//	}
//
//	public enum ReferenceRole{
//		CLASS, FIELD, METHOD , LOCAL, ARGUMENT
//	}
}
