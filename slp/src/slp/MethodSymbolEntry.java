package slp;

import java.util.List;
import java.util.LinkedList;

public class MethodSymbolEntry extends SymbolEntry{
	private final List<TypeEntry> methodArgs;
	private final List<String> methodArgsNames;
	
	public MethodSymbolEntry(String entryName, TypeEntry type, int lineOfDefinition){
		super(entryName, type,lineOfDefinition);
		this.methodArgs=new LinkedList<TypeEntry>();
		this.methodArgsNames=new LinkedList<String>();
	}
	
	
	public void addToArgs(TypeEntry argType){
		methodArgs.add(argType);
	}
	
	public void addToArgs(TypeEntry argType,String name){
		addToArgs(argType);
		methodArgsNames.add(name);
	}
	
	public List<TypeEntry> getMethodArgs(){
		return methodArgs;
	}

	public List<String> getMethodArgsNames() {
		return methodArgsNames;
	}
}