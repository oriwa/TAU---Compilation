package slp;

import java.util.*;


public class SymbolTable {
	
	/**
	 * 	Maps 	between (keys):
	 * 		 		names of variables(local)/arguments/class members (fields+methods),
	 * 			to (values):
	 * 				A stack of entries (SymbolEntry) that represents information about 
	 * 				the context of the most relevant declaration of that key. 
	 * */
	private Map<String, Stack<SymbolEntry>> map;
	
	/**
	 * 	A stack of scopes:
	 * 	Each scope referes to a list of destinations in 'map',
	 *  where a SymbokEntry has been added using 'addToScope()'
	 * */
	private Stack<List<Stack<SymbolEntry>>> scopes;
	
	private Stack<HashSet<SymbolEntry>> scopesInitializedEntries;
	
	public SymbolTable(){
		scopes=new Stack<List<Stack<SymbolEntry>>>(); 
		scopesInitializedEntries=new Stack<HashSet<SymbolEntry>>(); 
		map =new HashMap<String, Stack<SymbolEntry>>();
	}
	
	/**
	 * Dismiss the last Scope that has been defined (do when exiting the scope)	
	 * */
	public Scope popScope(){
		HashSet<SymbolEntry>  scopeInitializedEntries=scopesInitializedEntries.pop();
		resetInitialized(scopeInitializedEntries);
		List<Stack<SymbolEntry>> lastScope= scopes.pop();
		List<SymbolEntry> items=new ArrayList<SymbolEntry>();
		for(Stack<SymbolEntry> entryList: lastScope){
			items.add(entryList.pop());
			if(entryList.isEmpty())entryList=null;
		}
		return new Scope(items,scopeInitializedEntries);
	}
	


	/**
	 * 	create an entirely new scope of names
	 * */
	public void pushScope(){
		scopes.push(new LinkedList<Stack<SymbolEntry>>());
		scopesInitializedEntries.push(new HashSet<SymbolEntry>());
	}
	
	/**
	 * 	Add a new SymbolEntry to the last defined scope
	 * 	use case:
	 * 		+	for every field or method declared in a class
	 * 		+	for every method argument at method start
	 * 		+	for every new local variable that is being declared
	 * */
	public void addToScope(SymbolEntry entry){
		String entryName=entry.getEntryName();
		Stack<SymbolEntry> entriesForName =map.get(entryName);
		if(entriesForName==null) {
			entriesForName = new Stack<SymbolEntry>();
			map.put(entryName, entriesForName);
		}
		entriesForName.push(entry);		
		scopes.peek().add(entriesForName);
	}
	
	/**
	 *  given a reference by name of a variable/argument/class member
	 *  finds and returns the information about its declaration (of the nearest scope) 
	 * */
	public SymbolEntry getEntryByName(String refName){
		Stack<SymbolEntry> entriesForName = map.get(refName);
		if(entriesForName !=null){
			return entriesForName.peek();
		}
		return null;
	}
	
	public boolean isInCurrentScope(String refName){
		if(map.containsKey(refName)){
			Stack<SymbolEntry> scopeStack = map.get(refName);
			if(scopeStack.peek().isArg()) return true;
			return scopeStack.size()==scopes.size();
		}
		return false;
	}
	
	
	
	public void setEntryInitialized(String refName) {
	
		SymbolEntry symbolEntry=getEntryByName(refName);
		if(!symbolEntry.getIsInitialized())			
		{
			symbolEntry.setIsInitialized(true);
			scopesInitializedEntries.peek().add(symbolEntry);
		}
	}
	
	public int getScopeDepth()
	{
		return scopes.size();
	}
	
	
	private void resetInitialized(HashSet<SymbolEntry> prevScopeInitializedEntries)
	{
		for (SymbolEntry symbolEntry : prevScopeInitializedEntries) {
			symbolEntry.setIsInitialized(false);
		}
	}
	
}