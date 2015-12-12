package slp;

import java.util.List;
import java.util.LinkedList;

public class MethodSymbolEntry extends SymbolEntry{
	private final List<TypeEntry> methodArgs;
	
	public MethodSymbolEntry(String entryName, TypeEntry type, int lineOfDefinition){
		super(entryName, type,lineOfDefinition);
		this.methodArgs=new LinkedList<TypeEntry>();
	}
	
	public void addToArgs(TypeEntry argType){
		methodArgs.add(argType);
	}
	
	
}
