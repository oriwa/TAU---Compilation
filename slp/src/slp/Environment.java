package slp;

import java.util.*;

import slp.SymbolEntry.ReferenceRole;

/** Represents a state during the evaluation of a program. 
 */
public class Environment {
	
	public static final String INT="int";
	public static final String BOOLEAN="boolean";
	public static final String STRING="string";

	private static final String[] PRIMITIVE_TYPES={INT,BOOLEAN,STRING};
	
	
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
	private int loopDepth;
	private int mainMethodNumber=0;
	
	
	public Environment(){
		initPrimitiveTypeEntrys();
	}
	
	public void handleSemanticError(String error,int line)
	{
		System.out.println("ERROR in line +"+line+": "+error);
		System.exit(1);
		
	}
	
	public void validateTypeMismatch(TypeEntry expectedType,TypeEntry actualType,int line)
	{

		if(!isDimensionEqual(expectedType,actualType) || !isA(expectedType,actualType))
			handleSemanticError("type mismatch: cannot convert from "+actualType.getEntryName()+" to "+expectedType.getEntryName(),line);
			
	}
	private boolean isDimensionEqual(TypeEntry type1, TypeEntry type2) {
		if(type1.getClass()==type2.getClass()){
			if(type1.getClass()==ArrayTypeEntry.class){
				ArrayTypeEntry arr1 =(ArrayTypeEntry)type1;
				ArrayTypeEntry arr2 =(ArrayTypeEntry)type2;
				return arr1.getTypeDimension()==arr2.getTypeDimension();
			}
		}
		ArrayTypeEntry arr;
		if(type1.getClass()==ArrayTypeEntry.class){
			arr =(ArrayTypeEntry)type1;
			
		}else{
			arr = (ArrayTypeEntry)type2;
		}
		return arr.getTypeDimension()==0;
	}

	public boolean isA(TypeEntry potentialAncestor,TypeEntry potentialdescendant) {
		if(potentialdescendant.getEntryId()==potentialAncestor.getEntryId()){
			return true;
		}
		if(potentialdescendant.isPrimitive()){
			return false;
		}
		String parentName=potentialdescendant.getEntryClass().extends_name;
		if(parentName!=null){ //else no-parent
			return isA(typeTableMap.get(parentName),potentialAncestor);
		}
		return false;
		
	}
	
	public void validateTypeMismatch(String expectedTypeName,Integer actualTypeId,int line)
	{
		validateTypeMismatch(getTypeEntry(expectedTypeName),getTypeEntry(actualTypeId), line);
	}
	

	
	public void addToEnv(Class clss) throws /*Semantic*/Exception{
			
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
			handleSemanticError("type \""+typeName+"\" is undefined",lineDefined);
		}
		
		if(symbolTable.isInCurrentScope(SymbolId)){
			String errorMsg="ERROR: multiple definitions of "+SymbolId;
			String note="note: first defined in line: "+symbolTable.getEntryByName(SymbolId).definedAt();
			handleSemanticError(errorMsg+"\n"+note,lineDefined);
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
		return loopDepth>0;
	}

	public void setIsInLoop(boolean isInLoop) {
		if (isInLoop) 
			loopDepth++;
		else 
			loopDepth--;

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