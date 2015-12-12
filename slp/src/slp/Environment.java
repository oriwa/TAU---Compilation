package slp;

import java.util.*;


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
	
	private TypeEntry currentClassType;
	private MethodSymbolEntry currentMethodType;
	private Class currentClass;
	private Method currentMethod;
	private int loopDepth;
	private int mainMethodNumber=0;
	
	
	public Environment(){
		initPrimitiveTypeEntrys();
	}
	
	public void handleSemanticError(String error,int line)
	{
		System.out.println("ERROR in line "+line+": "+error);
		System.exit(1);
	}
	
	public void validateTypeMismatch(TypeEntry expectedType,TypeEntry actualType,int line)
	{
		//TODO: Add inheritance support
		if(!isDimensionEqual(expectedType,actualType) || !isA(expectedType,actualType))
			handleSemanticError("type mismatch: cannot convert from "+actualType.getEntryName()+" to "+expectedType.getEntryName(),line);
			
	}
	
	public void validateTypeMismatch(String expectedTypeName,TypeEntry actualType,int line)
	{
		validateTypeMismatch(getTypeEntry(expectedTypeName),actualType, line);
	}
	
	public void validateTypeMismatch(String expectedTypeName,Integer actualTypeId,int line)
	{
		validateTypeMismatch(getTypeEntry(expectedTypeName),getTypeEntry(actualTypeId), line);
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

	public boolean isA(TypeEntry potentialAncestor,
			TypeEntry potentialdescendant) {
		if (potentialdescendant.getEntryId() == potentialAncestor.getEntryId()) {
			return true;
		}
		if (potentialdescendant.isPrimitive()) {
			return false;
		}
		String parentName = potentialdescendant.getEntryClass().extends_name;
		if (parentName != null) { // else no-parent
			return isA(typeTableMap.get(parentName), potentialAncestor);
		}
		return false;
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
			Validator.handleSemanticError(errorMsg+"\n"+note,clss.line);
		}
		
		TypeEntry typeEntry = new TypeEntry(typeTable.size(), clss.name, clss);
		if(clss.extends_name!=null){
			TypeEntry extendsTypeEntry= getTypeEntry(clss.extends_name);
			if(extendsTypeEntry!=null)
			{
				Class extendsClass=extendsTypeEntry.getEntryClass();
				if(!extendsClass.isSealed){
					typeEntry.expandScope(extendsTypeEntry);
				}
				else{
					Validator.handleSemanticError("can not extend form class" + clss.extends_name,clss.line);	
				}
				
			}
			else
			{					
				Validator.handleSemanticError("class \""+clss.extends_class.name +"\" is undefined, classes can only extend previously defined classes",clss.line);
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

	public void addDclrs(Class clss) {
	
		TypeEntry clssType=getTypeEntry(clss.name);
		//SymbolTable staticScope = clssType.getScope(TypeEntry.STATIC_SCOPE);
		//SymbolTable instanceScope = clssType.getScope(TypeEntry.INSTANCE_SCOPE);
		
		
		
		Set<String> alreadySeen= new HashSet<String>();
		for (Dclr dclr : clss.dclrList.declarations){
			if(dclr.getClass()==Method.class){
				Method method=(Method)dclr;
				if(alreadySeen.contains(method.name)){
					Validator.handleSemanticError("Duplicate definition " + method.name + " in type " + clss.name,clss.line);	
				}
				
				alreadySeen.add(method.name);
				
				TypeEntry methodType = this.getTypeEntry(method.type.name);
				if(methodType==null){
					Validator.handleSemanticError("type \"" + method.type.name + "\" is undefined", method.line);				
				}
				
				MethodSymbolEntry methodSymbol =new MethodSymbolEntry(method.name, methodType, method.line);
				TypeEntry tmpArgType;
				for(Formals formal:method.formalsList.formals){
					tmpArgType=this.getTypeEntry(formal.type.name);
					if(tmpArgType==null){
						Validator.handleSemanticError("type \"" + method.type.name + "\" is undefined", method.line);
					}
					methodSymbol.addToArgs(tmpArgType);
				}
				clssType.addToScopes(methodSymbol, method.isStatic);
			}else{//dclr is Field
				Field field=(Field)dclr;
				TypeEntry fieldType = this.getTypeEntry(field.type.name);
				if(fieldType==null){
					Validator.handleSemanticError("type \"" + field.type.name + "\" is undefined", field.line);				
				}
				if(alreadySeen.contains(field.name)||clssType.isNameTaken(field.name)){
					Validator.handleSemanticError("Duplicate definition " + field.name + " in type " + clss.name,clss.line);	
				}
				alreadySeen.add(field.name);
				SymbolEntry fieldSymbol =new SymbolEntry(field.name, clssType, field.line);
				
				clssType.addToScopes(fieldSymbol, false);
				for(String id : field.extraIDs.ids){
					if(alreadySeen.contains(id)||clssType.isNameTaken(id)){
						Validator.handleSemanticError("Duplicate definition " + id + " in type " + clss.name,clss.line);	
					}
					alreadySeen.add(id);
					fieldSymbol =new SymbolEntry(id, clssType, field.line);
				}
				
			}
					
		}

	}
}