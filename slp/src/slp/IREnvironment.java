package slp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class IREnvironment {
	
	public static final String INT="int";
	public static final String BOOLEAN="boolean";
	public static final String STRING="string";
	public static final String NULL="null";
	
	public static final String RDUMMY="RDummy";
	
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
	

	private Map<String,String> stringLiteralsMap = new HashMap<String,String>();

	private LinkedList<String> outputLines=new LinkedList<String>();
	private LinkedList<String> stringLiterals=new LinkedList<String>();
	private LinkedList<String> dispatchVectors=new LinkedList<String>();
 
	private MethodSymbolEntry currentMethodType;
	private Class currentClass;
	private Method currentMethod;
	private Stack<WhileLabels> loopStack;
	private int mainMethodNumber=0;

	private int registerSerial=0;
	private int labelSerial=0;
	
	
	public IREnvironment(){
		loopStack=new Stack<WhileLabels>(); 
		initPrimitiveTypeEntrys();
		TypeEntry nullEntry = new TypeEntry(typeTable.size(), NULL);
		nullEntry.setPrimitive(false);
		addTypeEntry(nullEntry);
	}
	

	public void addToEnv(Class clss) {
		addTypeEntry(clss);
	}


	public SymbolEntry addDeclaration(TypeEntry type,String name,int line){
	
		SymbolEntry newSymbol = new SymbolEntry(name, type, line);
		newSymbol.uniqueName="v"+name+"name"+symbolTable.getScopeDepth();
	    symbolTable.addToScope(newSymbol);
		return newSymbol;
	}
	
	
	public  TypeEntry getType(Type type) {
		TypeEntry typeEntry=getTypeEntry(type.name);
		if(type.array_dimension!=0)
			typeEntry=ArrayTypeEntry.makeArrayTypeEntry(typeEntry,type.array_dimension);
		return typeEntry;
	}

	public void addToEnv(Formals formals) {
			SymbolEntry symbolEntry =addDeclaration(getType(formals.type),formals.name, formals.line);
			symbolEntry.uniqueName="p"+formals.name+"name";
			symbolEntry.setIsInitialized(true);
			symbolEntry.setIsArg();
	}
	
	
 



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

	public WhileLabels getCurrentWhileLabels() {
		return loopStack.peek();
	}

	public void pushWhileLabels(String whileLabelKey, String stopLabelKey) {
		loopStack.push(new WhileLabels(whileLabelKey,stopLabelKey));
	}
	
	public void popWhileLabels() {
		loopStack.pop();
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
		extendClass(clss,clssType);
		
		SymbolTable instanceScope = clssType.getScope(TypeEntry.INSTANCE_SCOPE);
		
		for (Dclr dclr : clss.dclrList.declarations){
			if(dclr.getClass()==Method.class){
				Method method=(Method)dclr;
				MethodSymbolEntry methodSymbol =new MethodSymbolEntry(method.name, method.type!=null?getType( method.type):null, method.line);
				methodSymbol.uniqueName="_"+clss.name+"_"+method.name;
				TypeEntry tmpArgType;
				for(Formals formal:method.formalsList.formals){			

					tmpArgType=  getType(formal.type);
					methodSymbol.addToArgs(tmpArgType);
				}
				clssType.addToScopes(methodSymbol, method.isStatic);
			}else{//dclr is Field
				Field field=(Field)dclr;

				TypeEntry fieldType =getType(field.type); 
				
				addField(field.name, instanceScope, fieldType,clssType, field.line);
				
				for(String id : field.extraIDs.ids){

					addField(id, instanceScope, fieldType,clssType, field.line);


				}
				
			}
					
		}
		clssType.InitDispatchVector();
		dispatchVectors.add(clssType.getDispatchVectorString());

	}
	private void extendClass(Class clss, TypeEntry clssType) {
		if(clss.extends_name!=null){
			TypeEntry extendsTypeEntry= getTypeEntry(clss.extends_name); 
				Class extendsClass=extendsTypeEntry.getEntryClass();
				if(!extendsClass.isSealed){
					clssType.expandScope(extendsTypeEntry);
				} 
				 
		}
		
	}

	private void addField(String name,SymbolTable instanceScope,TypeEntry type,TypeEntry clssType, int line){
		SymbolEntry fieldSymbol =new SymbolEntry(name, type, line);
		fieldSymbol.uniqueName="f"+name+"name";
		fieldSymbol.setIsInitialized(true);	
		clssType.addToScopes(fieldSymbol, false);
	}
	
	public String getStringLitralKey(String value)
	{
		if(!stringLiteralsMap.containsKey(value))
		{
			String key="str"+(stringLiteralsMap.size()+1);
			stringLiteralsMap.put(value, key);
			stringLiterals.add(key+": "+value);
		}
		return stringLiteralsMap.get(value);
	}
	
	public String getRegisterKey()
	{
		return "R"+registerSerial;
	}
	
	public void resetRegister()
	{
		registerSerial=0;
	}
	
	public String getLabelKey()
	{
		return "L"+labelSerial;
	}
	
	public void resetLabel()
	{
		labelSerial=0;
	}
	
	public void writeComment(String line)
	{
		writeLine("# " + line);
	}
	
	public void writeSectionHeader(String line)
	{
		writeLine("##############################");
		writeComment(line);
	}
	public void writeSectionBottom()
	{
		writeLine("##############################");
		writeLine("");
	}
	
	 
	
	public void writeLabel(String line)
	{
		writeLine( line+":");
	}
		
	public void writeCode(String line)
	{
		writeLine("		"+line);
	}
	
	
	public void writeLine(String line)
	{
		outputLines.add(line);
	}
	
	public void commitIR() throws IOException
	{
		File fout = new File("output.lr");
		FileOutputStream fos = new FileOutputStream(fout);
	 
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		
		writeSection(bw,"String literals",stringLiterals);
		writeSection(bw,"Dispatch vectors",dispatchVectors); 
		
		for (String value : outputLines) {

			bw.write(value);
			bw.newLine(); 
		}	
		
		bw.close();
	}


	private void writeSection(BufferedWriter bw,String name,LinkedList<String> content) throws IOException {
		bw.write("##############################");
		bw.newLine();
		bw.write("# "+name);
		bw.newLine(); 
		for (String value : content) {

			bw.write(value);
			bw.newLine(); 
		}
		bw.write("##############################");
		bw.newLine();
		bw.newLine();
	}
	
	
	
	
}
