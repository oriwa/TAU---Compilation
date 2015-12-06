package slp;

import java.util.*;

/** Represents a state during the evaluation of a program. 
 */
public class Environment {
	
	

	private static final String[] PRIMITIVE_TYPES={"int","boolean","string"};
	
	/** Maps the names of variables to integer values.
	 * The same variable may appear in different VarExpr objects.  We use the
	 * name of the variable as a way of ensuring we a consistent mapping
	 * for each variable. 
	 */
	private Map<String,Integer> varToValue = new HashMap<String,Integer>();
	
	private ArrayList<TypeEntry> typeTable=new ArrayList<TypeEntry>();
	private Map<String,TypeEntry> typeTableMap = new HashMap<String,TypeEntry>();
	
	private Class currentClass;
	private Method currentMethod;
	private boolean isInLoop;
	private int mainMethodNumber=0;
	
	
	public Environment()
	{
		initPrimitiveTypeEntrys();
	}
	
	/** Updates the value of a variable.
	 * 
	 * @param v A variable expression.
	 * @param newValue The updated value.
	 */
	public void update(VarExpr v, int newValue) {
		varToValue.put(v.name, new Integer(newValue));
		// Actually, varToValue.put(v, newValue) works as well because of the
		// auto-boxing feature of Java 1.5, which automatically wraps newValue
		// with an Integer object.
	}
	
	/** Retrieves the value of the given variable.
	 * If the variable has not been initialized an exception is thrown.
	 * 
	 * @param v A variable expression.
	 * @return The value of the given variable in this state.
	 */
	public Integer get(VarExpr v) {
		if (!varToValue.containsKey(v.name)) {
			throw new RuntimeException("Attempt to access uninitialized variable: " + v.name);
		}
		return varToValue.get(v.name);
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
	
	
	
}