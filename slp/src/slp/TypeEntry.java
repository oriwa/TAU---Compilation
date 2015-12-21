package slp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class TypeEntry {
	public static final boolean STATIC_SCOPE=true,INSTANCE_SCOPE=false;
	
	private final int entryId;
	private final String entryName;
	private final String uniqueName;
	private  boolean isPrimitive;
	private final Class entryClass;
	
	private TypeEntry extending=null;
	private Map<String,SymbolEntry> staticScope;
	private Map<String,SymbolEntry> instanceScope;

	public Map<String,Integer> fieldMap;
	public Map<String,Integer> dispatchVectorMap;
	public ArrayList<String> dispatchVector;
	
	
	public TypeEntry(int entryId,String entryName){
		this.entryId=entryId;
		this.entryName=entryName;
		this.isPrimitive=true;
		this.entryClass=null;
		this.uniqueName=null;
		
	}
	
	public TypeEntry(int entryId,String entryName, Class entryClass){
		this.entryId=entryId;
		this.entryName=entryName;
		this.uniqueName="_DV_"+entryName;
		this.isPrimitive=false;
		this.entryClass=entryClass;
		
		this.staticScope=new HashMap<String,SymbolEntry>();
		this.instanceScope=new HashMap<String,SymbolEntry>();	
		
		this.fieldMap=new HashMap<String,Integer>();
		this.dispatchVectorMap=new HashMap<String,Integer>();
		dispatchVector=new ArrayList<String>();
	}
	


	public int getEntryId() {
		return entryId;
	}
	
	public String getEntryName() { 
		String	arrBrackets =new String(new char[this.getTypeDimension()]).replace("\0","[]");
		return entryName+arrBrackets;
	}
	
	public String getUniqueName() { 
		return uniqueName;	
	}
	
	public String getTypeName() {
		return entryName;
	}

	public void setPrimitive(boolean primitive){
		isPrimitive = primitive;
	}
	public boolean isPrimitive() {
		return isPrimitive;
	}
	
	public boolean isNullable() {
		return !isPrimitive || (entryName==Environment.STRING);
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
		else
		{
			instanceScope.put(entry.getEntryName(), entry);
		}
	}
	
	private void addToScopes(Map<String,SymbolEntry> staticParentScope,Map<String,SymbolEntry> instanceParentScope){
		staticScope.putAll(staticParentScope);
		for (String key : instanceParentScope.keySet()) {
			if(!instanceScope.containsKey(key))
			{ 
				instanceScope.put(key, instanceParentScope.get(key));
			}
		}
	}
	
	
	public SymbolTable getScope(boolean isStatic){
		
		SymbolTable result = new SymbolTable();
		result.pushScope();
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
	
	public void InitDispatchVector()
	{
		for (SymbolEntry entry : instanceScope.values()) {
			if(entry instanceof MethodSymbolEntry)
			{
				dispatchVector.add(entry.uniqueName);
				dispatchVectorMap.put(entry.getEntryName(), dispatchVector.size());
			}
			else
			{
				fieldMap.put(entry.getEntryName(), fieldMap.size());
			}
			
		}
		
	}
	
	public String getDispatchVectorString()
	{
		String dispatchVectorString=uniqueName+": [";
		int count=0;
		for (String key : dispatchVector) {		
			dispatchVectorString+=key;
			count++;
			if(count!=dispatchVector.size())
				dispatchVectorString+=",";
		}
		dispatchVectorString+="]";
		return dispatchVectorString;
	}
	
	
	
	
}
