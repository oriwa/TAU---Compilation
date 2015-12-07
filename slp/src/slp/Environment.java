package slp;

import java.util.*;
import slp.SymbolEntry.ReferenceRole;

/** Represents a state during the evaluation of a program. 
 */
public class Environment {
	
	private static final String[] PRIMITIVE_TYPES={"int","boolean","string"};
	
	
	/** Maps the names of variables to integer values.
	 * The same variable may appear in different VarExpr objects.  We use the
	 * name of the variable as a way of ensuring we a consistent mapping
	 * for each variable. 
	 */
	private SymbolTable symbolTable = new SymbolTable();
	
	
	
	private ArrayList<TypeEntry> typeTable=new ArrayList<TypeEntry>();
	
	/**
	 * 	Maps the name of the declared classes to their corresponding TypeEntry
	 *  Would be built when first scanning the root ASTNode for class declarations
	 * */
	private Map<String,TypeEntry> typeTableMap = new HashMap<String,TypeEntry>();
	
	private Class currentClass;
	private Method currentMethod;
	private boolean isInLoop;
	private int mainMethodNumber=0;
	
	
	public Environment(){
		initPrimitiveTypeEntrys();

		/* i'm not sure if it's the right time for that, 
		 * but it's imperative at least before visiting INSIDE the classes*/
		LibraryLoader loader=new LibraryLoader();
		loader.load(this);
	}

	
	public void addToEnv(Class clss) throws /*Semantic*/Exception{
		TypeEntry previousDef=getTypeEntry(clss.name);
		if(previousDef!=null){
			String errorMsg="ERROR: multiple definitions of class \""+clss.name+"\"";
			String note="note: first defined in line: "+previousDef.getEntryClass().line;
			throw new /*Semantic*/Exception(errorMsg+"\n"+note);
		}
		
		if(clss.extends_class!=null){
			if(getTypeEntry(clss.extends_class.name)==null){
				String errorMsg="ERROR: class \""+clss.extends_class.name +"\" is undefined";
				throw new /*Semantic*/Exception(errorMsg);
			}
		}
		addTypeEntry(clss);
	}
	
	public void addToEnv(DeclarationStmt dclr) throws /*Semantic*/Exception{
		addToEnv(dclr.name,dclr.type.name,dclr.line,ReferenceRole.LOCAL);
	}
	
	public void addToEnv(Field field) throws /*Semantic*/Exception{
		for(String id: field.extraIDs.ids){
			addToEnv(field.name,id,field.line,ReferenceRole.FIELD);
		}
	}
	
	public void addToEnv(Method method) throws /*Semantic*/Exception{
		addToEnv(method.name,method.type.name,method.line,ReferenceRole.METHOD);
	}
	
	public void addToEnv(FormalsList formals) throws /*Semantic*/Exception{
		for(Formals f:formals.formals){
			addToEnv(f.name,f.type.name,f.line,ReferenceRole.ARGUMENT);
		}		
	}
	
	private void addToEnv(String typeName, String SymbolId, int lineDefined,ReferenceRole role) throws /*Semantic*/Exception{
		TypeEntry type=getTypeEntry(typeName);
		if(type==null){
			throw new /*Semantic*/Exception("ERROR: type \""+typeName+"\" is undefined"); 
		}
		
		if(symbolTable.isInCurrentScope(SymbolId)){
			String errorMsg="ERROR: multiple definitions of "+SymbolId;
			String note="note: first defined in line: "+symbolTable.getEntryByName(SymbolId).definedAt();
			
			throw new /*Semantic*/Exception(errorMsg+"\n"+note);
		}
		
		SymbolEntry newSymbol= new SymbolEntry(SymbolId, type, lineDefined, role);
		symbolTable.addToScope(newSymbol);
	}	
	
	private void initPrimitiveTypeEntrys()
	{
		for (String type : PRIMITIVE_TYPES) {
			addTypeEntry(new TypeEntry(typeTable.size()-1,type));
		}
		
	}
	
	private void addTypeEntry(TypeEntry typeEntry)
	{
		typeTable.add(typeEntry);
		typeTableMap.put(typeEntry.getEntryName(), typeEntry);	
	}
	
	public TypeEntry addTypeEntry(Class clss)
	{ 
		TypeEntry typeEntry=new TypeEntry(typeTable.size()-1,clss.name,clss);
		addTypeEntry(typeEntry);
		return typeEntry;		 
	}
	
	
	public TypeEntry getTypeEntry(int id)
	{
		if(typeTable.size()>id)
			return typeTable.get(id);
		return null;
	}
	
	public TypeEntry getTypeEntry(String name)
	{
		if(typeTableMap.containsKey(name))
			return typeTableMap.get(name);
		return null;
	}

	public Class getCurrentClass() {
		return currentClass;
	}

	public void setCurrentClass(Class currentClass) {
		this.currentClass = currentClass;
	}

	public Method getCurrentMethod() {
		return currentMethod;
	}

	public void setCurrentMethod(Method currentMethod) {
		this.currentMethod = currentMethod;
	}

	public boolean getIsInLoop() {
		return isInLoop;
	}

	public void setIsInLoop(boolean isInLoop) {
		this.isInLoop = isInLoop;
	}

	public int getMainMethodNumber() {
		return mainMethodNumber;
	}

	public void addMainMethodNumber() {
		mainMethodNumber++;
	}
	
	public void enterScope(){
		symbolTable.pushScope();
	}
	public void leaveScope(){
		symbolTable.popScope();
	}
	
	
}