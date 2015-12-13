package slp;

import java.util.*;

import java_cup.symbol;


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
	private SymbolTable symbolTable;
	
	
	
	private ArrayList<TypeEntry> typeTable=new ArrayList<TypeEntry>();
	
	/**
	 * 	Maps the name of the declared classes to their corresponding TypeEntry
	 *  Would be built when first scanning the root ASTNode for class declarations
	 * */
	private Map<String,TypeEntry> typeTableMap = new HashMap<String,TypeEntry>();
	
 
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
		if(!validateTypeMismatch(expectedType,actualType)) {
			String actualTypeName = actualType == null ? "void" : actualType.getEntryName();
			handleSemanticError("type mismatch: cannot convert from "+ actualTypeName +" to "+expectedType.getEntryName(),line);
		}
	}
	
	private boolean validateTypeMismatch(TypeEntry expectedType,TypeEntry actualType){
		int expectedDimension = expectedType.getTypeDimension();
		if (actualType == null)
			return false;
		
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


	public SymbolEntry addDeclaration(TypeEntry type,String name,int line){
		if (symbolTable.isInCurrentScope(name)) {
			String errorMsg = "ERROR: multiple definitions of " + name;
			String note = "note: first defined in line: "
					+ symbolTable.getEntryByName(name).definedAt();
			handleSemanticError(errorMsg + "\n" + note, line);
		}
		SymbolEntry newSymbol = new SymbolEntry(name, type, line);
	     symbolTable.addToScope(newSymbol);
		return newSymbol;
	}

	public void addToEnv(Formals formals) {
	
			SymbolEntry symbolEntry =addDeclaration(Validator.validateType( formals.type, this),formals.name, formals.line);
			symbolEntry.setIsInitialized(true);
		
	}
 
//	public void addToEnv(FormalsList formals) {
//		for (Formals f : formals.formals) {
//			SymbolEntry symbolEntry =addToEnv(f.name, f.type.name, f.line, true);
//			symbolEntry.setIsInitialized(true);
//		}
//	}

//	private SymbolEntry addToEnv(String typeName, String SymbolId, int lineDefined,boolean isMethod)  {
//		TypeEntry type = getTypeEntry(typeName);
//		if (type == null) {
//			handleSemanticError("type \"" + typeName + "\" is undefined",
//					lineDefined);
//		}
//
//		if (symbolTable.isInCurrentScope(SymbolId)) {
//			String errorMsg = "ERROR: multiple definitions of " + SymbolId;
//			String note = "note: first defined in line: "
//					+ symbolTable.getEntryByName(SymbolId).definedAt();
//			handleSemanticError(errorMsg + "\n" + note, lineDefined);
//		}
//		SymbolEntry newSymbol ;
//		if(isMethod){
//			newSymbol = new MethodSymbolEntry(SymbolId, type, lineDefined);
//			
//		}
//		newSymbol = new SymbolEntry(SymbolId, type, lineDefined);
//		symbolTable.addToScope(newSymbol);
//		return newSymbol;
//	}


	public SymbolEntry getSymbolEntry(String refName)
	{
		return symbolTable.getEntryByName(refName);
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
		
		TypeEntry previousDef=getTypeEntry(clss.name);
		if(previousDef!=null){
			String errorMsg="ERROR: multiple definitions of class \""+clss.name+"\"";
			String note="note: first defined in line: "+previousDef.getEntryClass().line;
			handleSemanticError(errorMsg+"\n"+note,clss.line);
		}
		
		TypeEntry typeEntry = new TypeEntry(typeTable.size(), clss.name, clss);
		if(clss.extends_name!=null){
			TypeEntry extendsTypeEntry= getTypeEntry(clss.extends_name);
			if(extendsTypeEntry!=null)
			{
				Validator.validateLibraryInstantiation(typeEntry, this, clss.line);
				Class extendsClass=extendsTypeEntry.getEntryClass();
				if(!extendsClass.isSealed){
					typeEntry.expandScope(extendsTypeEntry);
				}
				else{
					handleSemanticError("can not extend form class" + clss.extends_name,clss.line);	
				}
				
			}
			else
			{					
				handleSemanticError("class \""+clss.extends_name +"\" is undefined, classes can only extend previously defined classes",clss.line);
			}
		}
		
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

	
	public SymbolTable getSymbolTable() {
		return symbolTable;
	}

	public void setSymbolTable(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}
	
	public void enterScope() {
		symbolTable.pushScope();
	}

	public Scope leaveScope() {
		return symbolTable.popScope();
	}
	
	
	public void setEntryInitialized(String refName) {
		
		symbolTable.setEntryInitialized(refName);
	}


	TypeEntry getExprType(String varName) {	
		return symbolTable.getEntryByName(varName).getEntryTypeID();
	}

	public TypeEntry getCurrentClassType() {
		if(currentClass==null)
			return null;
		return getTypeEntry(currentClass.name);
	}
 

	public MethodSymbolEntry getMethodInClass(String methodName,  boolean isStatic, TypeEntry classType){
		SymbolTable symbolTable = classType.getScope(isStatic);
		SymbolEntry symbolEntry = symbolTable.getEntryByName(methodName);
		if (symbolEntry instanceof MethodSymbolEntry)
			return (MethodSymbolEntry)symbolEntry;
		return null;
	}

	public MethodSymbolEntry getCurrentMethodType() {
		return getMethodInClass(currentMethod.name,currentMethod.isStatic,getCurrentClassType());
	}

	public void setCurrentMethodType(MethodSymbolEntry currentMethodType) {
		this.currentMethodType = currentMethodType;
	}

	public void addDclrs(Class clss) {
	
		TypeEntry clssType=getTypeEntry(clss.name);
		
		SymbolTable instanceScope = clssType.getScope(TypeEntry.INSTANCE_SCOPE);
		
		
		
		Set<String> alreadySeen= new HashSet<String>();
		for (Dclr dclr : clss.dclrList.declarations){
			if(dclr.getClass()==Method.class){
				Method method=(Method)dclr;
				if(alreadySeen.contains(method.name)){
					handleSemanticError("Duplicate definition " + method.name + " in type " + clss.name,clss.line);	
				}
				alreadySeen.add(method.name);
				SymbolEntry parentSymbol=instanceScope.getEntryByName(method.name);
				if(parentSymbol!=null && parentSymbol instanceof MethodSymbolEntry){
					handleSemanticError("Duplicate definition " + method.name + " in type " + clss.name,clss.line);
				}
				

			 
				
				TypeEntry methodType=null;
				if(method.type!=null)
				{
					 methodType =  Validator.validateType(method.type, this); 
					Validator.validateLibraryInstantiation(methodType,this,method.line);
				}
				MethodSymbolEntry methodSymbol =new MethodSymbolEntry(method.name, methodType, method.line);
				TypeEntry tmpArgType;
				for(Formals formal:method.formalsList.formals){

				
				
					

					tmpArgType=  Validator.validateType(formal.type, this);
					methodSymbol.addToArgs(tmpArgType);
				}
				clssType.addToScopes(methodSymbol, method.isStatic);
			}else{//dclr is Field
				Field field=(Field)dclr;

				TypeEntry fieldType = Validator.validateType(field.type, this);
				Validator.validateLibraryInstantiation(fieldType,this,field.line);
				

				
				addField(field.name, alreadySeen, instanceScope, fieldType,clssType, field.line);
				
				for(String id : field.extraIDs.ids){

					addField(id, alreadySeen, instanceScope, fieldType,clssType, field.line);


				}
				
			}
					
		}

	}
	private void addField(String name,Set<String> alreadySeen,SymbolTable instanceScope,TypeEntry type,TypeEntry clssType, int line){

		if(alreadySeen.contains(name)||clssType.isNameTaken(name)){
			handleSemanticError("Duplicate definition " + name + " in type " + clssType.getEntryName(),line);	
		}
		alreadySeen.add(name);
		SymbolEntry parentSymbol=instanceScope.getEntryByName(name);
		if(parentSymbol!=null && parentSymbol instanceof MethodSymbolEntry){
			handleSemanticError("Duplicate definition " + name + " in type " + clssType.getEntryName(),line);
		}
		
		SymbolEntry fieldSymbol =new SymbolEntry(name, type, line);
		fieldSymbol.setIsInitialized(true);
		
		clssType.addToScopes(fieldSymbol, false);
	}
}

