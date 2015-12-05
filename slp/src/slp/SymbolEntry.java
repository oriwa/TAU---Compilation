package slp;

public  class SymbolEntry {
	private final String entryName;
	private final int lineOfDefinition;
	private final Type entryType;
	
	public SymbolEntry(String entryName, Type entryType, int lineOfDefinition){
		this.entryName=entryName;
		this.entryType=entryType;
		this.lineOfDefinition=lineOfDefinition;
	}
	
	public Type getEntryType(){
		return entryType;
	}
	
	public String getEntryName(){
		return entryName;
	}
	
	public int definedAt() {
		return lineOfDefinition;
	}

	
}
