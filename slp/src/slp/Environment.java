package slp;

import java.util.*;


/** Represents a state during the evaluation of a program. 
 */
public class Environment {
	
	public static final String INT="int";
	public static final String BOOLEAN="boolean";
	public static final String STRING="string";
	public static final String NULL="null";
	
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
	
	private TypeEntry currentClassType;
	private MethodSymbolEntry currentMethodType;
	private Class currentClass;
	private Method currentMethod;
	private int loopDepth;
	private int mainMethodNumber=0;
	
	
	public Environment(){
		initPrimitiveTypeEntrys();
		TypeEntry nullEntry = new TypeEntry(typeTable.size(), NULL);
		nullEntry.setPrimitive(false);
		addTypeEntry(nullEntry);
	}
	
	public void handleSemanticError(String error,int line)
	{
		System.out.println("ERROR in line "+line+": "+error);
		System.exit(1);
	}
	
	public void validateTypeMismatch(TypeEntry expectedType,TypeEntry actualType,int line)
	{
		if(!validateTypeMismatch(expectedType,actualType))
			handleSemanticError("type mismatch: cannot convert from "+actualType.getEntryName()+" to "+expectedType.getEntryName(),line);
	}
	
	private boolean validateTypeMismatch(TypeEntry expectedType,TypeEntry actualType){
		int expectedDimension = expectedType.getTypeDimension();
		int actualDimension = actualType.getTypeDimension();
		
		if (expectedDimension != actualDimension)
			return false;
		
		boolean isArray = expectedDimension != 0;
		if (isArray && expectedType.getEntryId() != actualType.getEntryId())
			return false;
		
		if (!isA(expectedType, actualType))
			return false;
		
		return true;

	}
	
	public void validateTypeMismatch(String expectedTypeName,TypeEntry actualType,int line)
	{
		validateTypeMismatch(getTypeEntry(expectedTypeName),actualType, line);
	}
	
	public void validateTypeMismatch(String expectedTypeName,Integer actualTypeId,int line)
	{
		validateTypeMismatch(getTypeEntry(expectedTypeName),getTypeEntry(actualTypeId), line);
	}

	public boolean isA(TypeEntry potentialAncestor,
			TypeEntry potentialdescendant) {
		if (potentialdescendant.getEntryId() == potentialAncestor.getEntryId()) {
			return true;
		}
		if (potentialdescendant.isPrimitive()) {
			return false;
		}
		if (potentialAncestor.getEntryName() == NULL){
			return true;
		}
		String parentName = potentialdescendant.getEntryClass().extends_name;
		if (parentName != null) { // else no-parent
			return isA(typeTableMap.get(parentName), potentialAncestor);
		}
		return false;
	}

	public void addToEnv(Class clss) {
		addTypeEntry(clss);
	}

	public void addToEnv(DeclarationStmt dclr){
		addToEnv(dclr.name, dclr.type.name, dclr.line, false);
	}

	public void addToEnv(Field field)  {
		for (String id : field.extraIDs.ids) {
			addToEnv(field.name, id, field.line, false);
		}
	}

	public void addToEnv(Method method) {
		SymbolEntry methodSymbolEntry=addToEnv(method.name, method.type.name, method.line,true);
		this.setCurrentMethodType((MethodSymbolEntry) methodSymbolEntry);
		getCurrentClassType().addComponent(method.name, getTypeEntry(method.type.name));
	}

	public void addToEnv(FormalsList formals) {
		for (Formals f : formals.formals) {
			addToEnv(f.name, f.type.name, f.line, true);
		}
	}

	private SymbolEntry addToEnv(String typeName, String SymbolId, int lineDefined,boolean isMethod)  {
		TypeEntry type = getTypeEntry(typeName);
		if (type == null) {
			handleSemanticError("type \"" + typeName + "\" is undefined",
					lineDefined);
		}

		if (symbolTable.isInCurrentScope(SymbolId)) {
			String errorMsg = "ERROR: multiple definitions of " + SymbolId;
			String note = "note: first defined in line: "
					+ symbolTable.getEntryByName(SymbolId).definedAt();
			handleSemanticError(errorMsg + "\n" + note, lineDefined);
		}
		SymbolEntry newSymbol ;
		if(isMethod){
			newSymbol = new MethodSymbolEntry(SymbolId, type, lineDefined);
			
		}
		newSymbol = new SymbolEntry(SymbolId, type, lineDefined);
		symbolTable.addToScope(newSymbol);
		return newSymbol;
	}



	private void initPrimitiveTypeEntrys() {
		for (String type : PRIMITIVE_TYPES) {
			addTypeEntry(new TypeEntry(typeTable.size(), type));
		}
	}

	private void addTypeEntry(TypeEntry typeEntry) {
		typeTable.add(typeEntry);
		typeTableMap.put(typeEntry.getEntryName(), typeEntry);
	}

	public TypeEntry addTypeEntry(Class clss) {
		TypeEntry typeEntry = new TypeEntry(typeTable.size(), clss.name, clss);
		addTypeEntry(typeEntry);
		return typeEntry;
	}

	public TypeEntry getTypeEntry(int id) {
		if (typeTable.size() > id)
			return typeTable.get(id);
		return null;
	}

	public TypeEntry getTypeEntry(String name) {
		if (typeTableMap.containsKey(name))
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
		return loopDepth > 0;
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

	public void enterScope() {
		symbolTable.pushScope();
	}

	public void leaveScope() {
		symbolTable.popScope();
	}

	public TypeEntry getExprType(TypeEntry exprType, String fieldName) {
		
		// TODO Auto-generated method stub
		return null;
	}
	TypeEntry getExprType(String varName) {	
		return symbolTable.getEntryByName(varName).getEntryTypeID();
	}

	public TypeEntry getCurrentClassType() {
		return currentClassType;
	}

	public void setCurrentClassType(TypeEntry currentClassType) {
		this.currentClassType = currentClassType;
	}

	public MethodSymbolEntry getCurrentMethodType() {
		return currentMethodType;
	}

	public void setCurrentMethodType(MethodSymbolEntry currentMethodType) {
		this.currentMethodType = currentMethodType;
	}

}