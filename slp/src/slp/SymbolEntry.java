package slp;

public  class SymbolEntry {
	private final String entryName;
	private final int lineOfDefinition;
	private final TypeEntry type;
	
	private boolean isInitialized; 
	
	public String uniqueName;
	//private final ReferenceRole role;
	
	private boolean isArg=false;
	public void setIsArg() {isArg=true;}
	public boolean isArg() {return isArg;}
	
	
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


	public boolean getIsInitialized() {
		return isInitialized;
	}


	public void setIsInitialized(boolean isInitialized) {
		this.isInitialized = isInitialized;
	}

//	public ReferenceRole getRole() {
//		return role;
//	}
//
//	public enum ReferenceRole{
//		CLASS, FIELD, METHOD , LOCAL, ARGUMENT
//	}
}