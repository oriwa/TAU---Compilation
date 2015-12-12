package slp;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class TypeEntry {
	public static final boolean STATIC_SCOPE=true,INSTANCE_SCOPE=false;
	
	private final int entryId;
	private final String entryName;
	private final boolean isPrimitive;
	private final Class entryClass;
	
	private TypeEntry extending=null;
	private Map<String,SymbolEntry> staticScope;
	private Map<String,SymbolEntry> instanceScope;
	
	
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
		this.staticScope=new HashMap<String,SymbolEntry>();
		this.instanceScope=new HashMap<String,SymbolEntry>();	
	}
	


	public int getEntryId() {
		return entryId;
	}
	
	public String getEntryName() {
		String arrBrackets =new String(new char[this.getTypeDimension()]).replace("\0","[]");
		return entryName+arrBrackets;
	}

	public void setPrimitive(boolean primitive){
		isPrimitive = primitive;
	}
	public boolean isPrimitive() {
		return isPrimitive;
	}

	public Class getEntryClass() {
		return entryClass;
	}

	public int getTypeDimension() {
		return 0;
	}

	
	
	public void addToScopes(SymbolEntry entry, boolean isStatic){
		if(isStatic){
			staticScope.put(entry.getEntryName(), entry);
		}
		instanceScope.put(entry.getEntryName(), entry);
	}
	
	private void addToScopes(Map<String,SymbolEntry> staticParentScope,Map<String,SymbolEntry> instanceParentScope){
		staticScope.putAll(staticParentScope);
		instanceScope.putAll(instanceParentScope);	
	}
	
	
	public SymbolTable getScope(boolean isStatic){
		
		SymbolTable result = new SymbolTable();
		if(!isPrimitive){
			Map<String,SymbolEntry> creator= isStatic?staticScope:instanceScope;
			Collection<SymbolEntry> entries=creator.values();
			for(SymbolEntry entry:entries ){
				result.addToScope(entry);
			}	
		}
		return result;
	}

	public void expandScope(TypeEntry extendsTypeEntry) {
		this.addToScopes(extendsTypeEntry.staticScope, extendsTypeEntry.instanceScope);
		this.extending=extendsTypeEntry;
	}
	
	public TypeEntry getParentType(){
		return extending;
	}

	public boolean isNameTaken(String name) {
		return  staticScope.containsKey(name)||instanceScope.containsKey(name);
	}
	
}
