package slp;

import java.util.HashMap;
import java.util.Map;

public class TypeEntry {
	private final int entryId;
	private final String entryName;
	private final boolean isPrimitive;
	private final Class entryClass;
	private Map<String,TypeEntry> typeComponents=null;
	
	
	
	
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
		this.typeComponents=new HashMap<String,TypeEntry>();
		
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

	public TypeEntry getComponentByName(String name){
		if(isPrimitive){
			if(typeComponents.containsKey(name)){
				return typeComponents.get(name);
			}
		}
		return null;
	}
	public void addComponent(String name, TypeEntry type){
		typeComponents.put(name, type);
	}	
	
}
