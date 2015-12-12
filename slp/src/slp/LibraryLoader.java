package slp;

public class LibraryLoader {
	public static final String NAME="Library";
	
	public void load(Environment env){
		Class libraryClss=GetLibraryClass();
		env.addTypeEntry(libraryClss);
		env.addDclrs(libraryClss);
	}
	
	
	public Class GetLibraryClass()
	{
		Class clss= new Class(0, NAME, GetLibraryMethods());
		clss.isSealed=true;
		return clss;
	}
	
	private DclrList GetLibraryMethods()
	{
		DclrList methods=new DclrList();
		
		methods.addMethod(GetPrintlnMethod());
		methods.addMethod(GetPrintMethod());
		methods.addMethod(GetPrintIMethod());
		methods.addMethod(GetPrintBMethod());
		
		methods.addMethod(GetReadiMethod());
		methods.addMethod(GetReadlnMethod());
		methods.addMethod(GetEofMethod());
		
		methods.addMethod(GetStoiMethod());
		
		methods.addMethod(GetItosMethod());
		methods.addMethod(GetStoaMethod());
		methods.addMethod(GetStosMethod());
		
		methods.addMethod(GetRandomMethod());
		methods.addMethod(GetTimeMethod());
		methods.addMethod(GetExitMethod());
		
		return methods;
	}
	
	
	
	private Method GetPrintlnMethod()
	{	
		FormalsList formalsList=new FormalsList();
		formalsList.addStmt(new Formals(0, GetStringType(), "s"));
		return new Method(0, true, null, "println", formalsList, null);
	}
		
	private Method GetPrintMethod()
	{	
		FormalsList formalsList=new FormalsList();
		formalsList.addStmt(new Formals(0, GetStringType(), "s"));
		return new Method(0, true, null, "print", formalsList, null);
	}
	
	private Method GetPrintIMethod()
	{	
		FormalsList formalsList=new FormalsList();
		formalsList.addStmt(new Formals(0, GetIntType(), "i"));
		return new Method(0, true, null, "printi", formalsList, null);
	}
	
	private Method GetPrintBMethod()
	{	
		FormalsList formalsList=new FormalsList();
		formalsList.addStmt(new Formals(0, GetBoolType(), "b"));
		return new Method(0, true, null, "printb", formalsList, null);
	}
	
	
	private Method GetReadiMethod()
	{	
		FormalsList formalsList=new FormalsList();
		return new Method(0, true, GetIntType(), "readi", formalsList, null);
	}
	
	private Method GetReadlnMethod()
	{	
		FormalsList formalsList=new FormalsList();
		return new Method(0, true, GetStringType(), "readln", formalsList, null);
	}
	
	private Method GetEofMethod()
	{	
		FormalsList formalsList=new FormalsList();
		return new Method(0, true, GetBoolType(), "eof", formalsList, null);
	}
	
	
	private Method GetStoiMethod()
	{	
		FormalsList formalsList=new FormalsList();
		formalsList.addStmt(new Formals(0, GetStringType(), "s"));
		formalsList.addStmt(new Formals(0, GetIntType(), "n"));
		return new Method(0, true, GetIntType(), "stoi", formalsList, null);
	}
	
	
	private Method GetItosMethod()
	{	
		FormalsList formalsList=new FormalsList();
		formalsList.addStmt(new Formals(0, GetIntType(), "i"));
		return new Method(0, true, GetStringType(), "itos", formalsList, null);
	}
	
	private Method GetStoaMethod()
	{	
		FormalsList formalsList=new FormalsList();
		formalsList.addStmt(new Formals(0, GetStringType(), "s"));
		Type array= GetIntType();
		array.addDimension();
		return new Method(0, true, array, "stoa", formalsList, null);
	}
	
	private Method GetStosMethod()
	{	
		FormalsList formalsList=new FormalsList();
		Type array= GetIntType();
		array.addDimension();
		formalsList.addStmt(new Formals(0, array, "a"));
		return new Method(0, true, GetStringType(), "stos", formalsList, null);
	}
	
	
	private Method GetRandomMethod()
	{	
		FormalsList formalsList=new FormalsList();
		formalsList.addStmt(new Formals(0, GetIntType(), "i"));
		return new Method(0, true, GetIntType(), "random", formalsList, null);
	}
	
	private Method GetTimeMethod()
	{	
		FormalsList formalsList=new FormalsList();
		return new Method(0, true, GetIntType(), "time", formalsList, null);
	}
	
	private Method GetExitMethod()
	{	
		FormalsList formalsList=new FormalsList();
		formalsList.addStmt(new Formals(0, GetIntType(), "i"));
		return new Method(0, true, null, "exit", formalsList, null);
	}
	
	
	
	
	
	
	private Type GetIntType()
	{
		Type type= new Type(0);
		type.setName(Environment.INT);
		return type;
	}
	
	private Type GetStringType()
	{
		Type type= new Type(0);
		type.setName(Environment.STRING);
		return type;
	}
	
	private Type GetBoolType()
	{
		Type type= new Type(0);
		type.setName(Environment.BOOLEAN);
		return type;
	}
	
}
=======
package slp;

public class LibraryLoader {
	
	

	public static final String NAME="Library";
	
	
	
	public void load(Environment env){
		
	}
	
	
	public Class GetLibraryClass()
	{
		Class clss= new Class(0, NAME, GetLibraryMethods());
		clss.isSealed=true;
		return clss;
	}
	
	private DclrList GetLibraryMethods()
	{
		DclrList methods=new DclrList();
		
		methods.addMethod(GetPrintlnMethod());
		methods.addMethod(GetPrintMethod());
		methods.addMethod(GetPrintIMethod());
		methods.addMethod(GetPrintBMethod());
		
		methods.addMethod(GetReadiMethod());
		methods.addMethod(GetReadlnMethod());
		methods.addMethod(GetEofMethod());
		
		methods.addMethod(GetStoiMethod());
		
		methods.addMethod(GetItosMethod());
		methods.addMethod(GetStoaMethod());
		methods.addMethod(GetStosMethod());
		
		methods.addMethod(GetRandomMethod());
		methods.addMethod(GetTimeMethod());
		methods.addMethod(GetExitMethod());
		
		return methods;
	}
	
	
	
	private Method GetPrintlnMethod()
	{	
		FormalsList formalsList=new FormalsList();
		formalsList.addStmt(new Formals(0, GetStringType(), "s"));
		return new Method(0, true, null, "println", formalsList, null);
	}
		
	private Method GetPrintMethod()
	{	
		FormalsList formalsList=new FormalsList();
		formalsList.addStmt(new Formals(0, GetStringType(), "s"));
		return new Method(0, true, null, "print", formalsList, null);
	}
	
	private Method GetPrintIMethod()
	{	
		FormalsList formalsList=new FormalsList();
		formalsList.addStmt(new Formals(0, GetIntType(), "i"));
		return new Method(0, true, null, "printi", formalsList, null);
	}
	
	private Method GetPrintBMethod()
	{	
		FormalsList formalsList=new FormalsList();
		formalsList.addStmt(new Formals(0, GetBoolType(), "b"));
		return new Method(0, true, null, "printb", formalsList, null);
	}
	
	
	private Method GetReadiMethod()
	{	
		FormalsList formalsList=new FormalsList();
		return new Method(0, true, GetIntType(), "readi", formalsList, null);
	}
	
	private Method GetReadlnMethod()
	{	
		FormalsList formalsList=new FormalsList();
		return new Method(0, true, GetStringType(), "readln", formalsList, null);
	}
	
	private Method GetEofMethod()
	{	
		FormalsList formalsList=new FormalsList();
		return new Method(0, true, GetBoolType(), "eof", formalsList, null);
	}
	
	
	private Method GetStoiMethod()
	{	
		FormalsList formalsList=new FormalsList();
		formalsList.addStmt(new Formals(0, GetStringType(), "s"));
		formalsList.addStmt(new Formals(0, GetIntType(), "n"));
		return new Method(0, true, GetIntType(), "stoi", formalsList, null);
	}
	
	
	private Method GetItosMethod()
	{	
		FormalsList formalsList=new FormalsList();
		formalsList.addStmt(new Formals(0, GetIntType(), "i"));
		return new Method(0, true, GetStringType(), "itos", formalsList, null);
	}
	
	private Method GetStoaMethod()
	{	
		FormalsList formalsList=new FormalsList();
		formalsList.addStmt(new Formals(0, GetStringType(), "s"));
		Type array= GetIntType();
		array.addDimension();
		return new Method(0, true, array, "stoa", formalsList, null);
	}
	
	private Method GetStosMethod()
	{	
		FormalsList formalsList=new FormalsList();
		Type array= GetIntType();
		array.addDimension();
		formalsList.addStmt(new Formals(0, array, "a"));
		return new Method(0, true, GetStringType(), "stos", formalsList, null);
	}
	
	
	private Method GetRandomMethod()
	{	
		FormalsList formalsList=new FormalsList();
		formalsList.addStmt(new Formals(0, GetIntType(), "i"));
		return new Method(0, true, GetIntType(), "random", formalsList, null);
	}
	
	private Method GetTimeMethod()
	{	
		FormalsList formalsList=new FormalsList();
		return new Method(0, true, GetIntType(), "time", formalsList, null);
	}
	
	private Method GetExitMethod()
	{	
		FormalsList formalsList=new FormalsList();
		formalsList.addStmt(new Formals(0, GetIntType(), "i"));
		return new Method(0, true, null, "exit", formalsList, null);
	}
	
	
	private Type GetIntType()
	{
		Type type= new Type(0);
		type.setName(Environment.INT);
		return type;
	}
	
	private Type GetStringType()
	{
		Type type= new Type(0);
		type.setName(Environment.STRING);
		return type;
	}
	
	private Type GetBoolType()
	{
		Type type= new Type(0);
		type.setName(Environment.BOOLEAN);
		return type;
	}
	
}
