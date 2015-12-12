package slp;

import java.util.HashSet;
import java.util.List;

public class Scope {
	
	
	public HashSet<SymbolEntry> scopeInitializedEntries;
	public List<SymbolEntry> items;

	public  Scope(List<SymbolEntry> items, HashSet<SymbolEntry> scopeInitializedEntries) {
		 this.items=items;
		 this.scopeInitializedEntries=scopeInitializedEntries;
	} 

}
