package slp;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Evaluates straight line programs.
 */
public class SLPEvaluator implements PropagatingVisitor<Environment, VisitResult> {
	
	

	
	protected ASTNode root;
	
	/** Constructs an SLP interpreter for the given AST.
	 * 
	 * @param root An SLP AST node.
	 */
	public SLPEvaluator(ASTNode root) {
		this.root = root;
	}
	
	/** Interprets the AST passed to the constructor.
	 */
	public void evaluate() {
		Environment env = new Environment();
		root.accept(this, env);
	}
	
	
	
	
	public VisitResult visit(StmtList stmts, Environment env) {
		env.enterScope();
		boolean hasReturnStatement=false;
		for (Stmt st : stmts.statements) {
			VisitResult stmtResult=st.accept(this, env);
			if(stmtResult.hasReturnStatement)
				hasReturnStatement=true;
		}
		Scope prevScope=env.leaveScope();
		VisitResult visitResult=new VisitResult(hasReturnStatement);
		visitResult.prevScope=prevScope;
		return visitResult;
	}

	public VisitResult visit(Stmt stmt, Environment env) {
		throw new UnsupportedOperationException("Unexpected visit of Stmt!");
	}



	public VisitResult visit(AssignStmt stmt, Environment env) {

		VisitResult locationResult=stmt.location.accept(this, env);
		VisitResult rhsResult=stmt.rhs.accept(this, env);
		Validator.validateInitialized(rhsResult, stmt.line, env);
		env.validateTypeMismatch(locationResult.type, rhsResult.type, stmt.line);
		if(locationResult.uninitializedId!=null)
			env.setEntryInitialized(locationResult.uninitializedId);
		
		return new VisitResult();
	}

	public VisitResult visit(Expr expr, Environment env) {
		throw new UnsupportedOperationException("Unexpected visit of Expr!");
	}



	public VisitResult visit(VarExpr expr, Environment env) {
		VisitResult visitResult=new VisitResult();
		if(expr.target_expr!=null)
		{
			 
			VisitResult targetResult=expr.target_expr.accept(this, env);
			Validator.validateInitialized(targetResult, expr.line, env);

			SymbolEntry symbolEntry=null;
			if(targetResult.type!=null)
			{
				SymbolTable symbolTable=targetResult.type.getScope(false);
				symbolEntry=symbolTable.getEntryByName(expr.name);
			}
			if(symbolEntry==null || symbolEntry instanceof MethodSymbolEntry)
				env.handleSemanticError(expr.name +" cannot be resolved or is not a field"  ,expr.line);
			
			visitResult.type=symbolEntry.getEntryTypeID();
		}
		else
		{ 
			SymbolEntry symbolEntry=env.getSymbolEntry(expr.name);
			if(symbolEntry==null)
				env.handleSemanticError(expr.name +" cannot be resolved to a variable"  ,expr.line);
			visitResult.type=symbolEntry.getEntryTypeID();
			visitResult.isInitialized=symbolEntry.getIsInitialized();
			if(!visitResult.isInitialized)
				visitResult.uninitializedId=expr.name;
		}
		return visitResult;
	}

	public VisitResult visit(NumberExpr expr, Environment env) {

		return new VisitResult(env.getTypeEntry(Environment.INT),expr.value);
	}

	public VisitResult visit(UnaryOpExpr expr, Environment env) {
		VisitResult value = expr.operand.accept(this, env);	
		Validator.validateInitialized(value, expr.line, env);
		Validator.validateIlegalOp(value.type, expr.op, expr.line,env);
		return new VisitResult(value.type);
	}

	public VisitResult visit(BinaryOpExpr expr, Environment env) {
		VisitResult lhsValue = expr.lhs.accept(this, env);
		Validator.validateInitialized(lhsValue, expr.line, env);
		VisitResult rhsValue = expr.rhs.accept(this, env);
		Validator.validateInitialized(rhsValue, expr.line, env);
		
		Validator.validateIlegalOp(lhsValue.type,rhsValue.type,expr.op, expr.line,env);


		switch (expr.op){
		case PLUS:
			if(lhsValue.type.getEntryId()==env.getTypeEntry(Environment.STRING).getEntryId())
				return new VisitResult(env.getTypeEntry(Environment.STRING));
			else
				return new VisitResult(env.getTypeEntry(Environment.INT));
				
		case MINUS:
		case MULTIPLY:
		case DIVIDE:
		case MOD:
			return new VisitResult(env.getTypeEntry(Environment.INT));
		default:
			//All other operations yield boolean result
			return new VisitResult(env.getTypeEntry(Environment.BOOLEAN));
		}
		
	}

	public VisitResult visit(Program program, Environment env)  {
		initTypeTable(program.classList,env);
		program.classList.accept(this, env);
		if(env.getMainMethodNumber()==0)
			env.handleSemanticError("no method main found , a program must have exactly one method main with the signature static void main (string[] args) { ... }",0);
		if(env.getMainMethodNumber()>1)
			env.handleSemanticError("more then one method main found , a program must have exactly one method main with the signature static void main (string[] args) { ... }",0);
		return null;
	}
	

	private void initTypeTable(ClassList classList, Environment env)  {
		initLibrary(env);
		for (Class clss : classList.classes) {
			env.addTypeEntry(clss);
		}
		for(Class clss:classList.classes){
			env.addDclrs(clss);
		}
	}
	

	private void initLibrary(Environment env) {
		LibraryLoader loader=new LibraryLoader();
		loader.load(env);	
	}

	public VisitResult visit(ClassList classes, Environment env) {
		for (Class clss : classes.classes) {
			clss.accept(this, env);
		}
		return null;
	}

	public VisitResult visit(Class clss, Environment env)  {
		env.setCurrentClass(clss);
			
		clss.dclrList.accept(this, env);
		
		env.setCurrentClass(null);
		
		return null;
	}

	public VisitResult visit(DclrList list, Environment env) {
		for (Field field : list.fields) {
			field.accept(this, env);
		}
		for (Method method : list.methods) {
			method.accept(this, env);
		}
		return null;
	}
	

	public VisitResult visit(Field field, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public VisitResult visit(ExtraIDs extraIDs, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public VisitResult visit(Method method, Environment env) {
		TypeEntry currentClassType= env.getCurrentClassType();
		
		env.setSymbolTable(currentClassType.getScope(method.isStatic));
		
		if(Validator.isMainMethod(method))
			env.addMainMethodNumber();
		
		env.setCurrentMethod(method);
		env.enterScope();
		method.formalsList.accept(this, env);
		VisitResult stmtListResult=method.stmtList.accept(this, env);
		if(!stmtListResult.hasReturnStatement && method.type!=null)
			env.handleSemanticError("this method must return a result of type "+method.type.name, method.line);
		env.leaveScope();
		env.setCurrentMethod(null);
		env.setSymbolTable(null);
		return null;
	} 
	 



	public VisitResult visit(FormalsList formalsList, Environment env) {
		for (Formals f : formalsList.formals){
			f.accept(this, env);
		}
		return null;
	}

	public VisitResult visit(Formals formals, Environment env) {
		formals.type.accept(this, env);
		
		env.addToEnv(formals);
		return null;
	}

	public VisitResult visit(Type type, Environment env) {
		return new VisitResult(Validator.validateType(type, env));
	}

	public VisitResult visit(CallStmt callStmt, Environment env) {
		callStmt.call.accept(this, env);
		return new VisitResult();
	}
	public VisitResult visit(ReturnStmt returnStmt, Environment env) {
		
		Method currentMethod=env.getCurrentMethod();
		if(returnStmt.expr==null)
		{
			if(currentMethod.type!=null)
			{
				env.handleSemanticError("this method must return a result of type "+currentMethod.type.name, returnStmt.line);				
			}
		}
		else
		{
			if (currentMethod.type == null)
				env.handleSemanticError("Void methods cannot retrun value." , currentMethod.line);
			VisitResult exprResult= returnStmt.expr.accept(this,env);
			Validator.validateInitialized(exprResult, returnStmt.line, env);
			
			env.validateTypeMismatch(env.getCurrentMethodType().getEntryTypeID(), exprResult.type, returnStmt.line);
		}
		return new VisitResult(true);
	}

	
	

	public VisitResult visit(IfStmt ifStmt, Environment env) {
		VisitResult exprResult= ifStmt.expr.accept(this,env);
		Validator.validateInitialized(exprResult, ifStmt.line, env);
		env.validateTypeMismatch(Environment.BOOLEAN,exprResult.type,ifStmt.line);
		
		boolean createScope = !(ifStmt.ifStmt instanceof StmtList);
		if (createScope)
			env.enterScope();		
		VisitResult ifStmtResult= ifStmt.ifStmt.accept(this,env);
		Scope ifScope=ifStmtResult.prevScope;
		boolean ifHasReturnStatement=ifStmtResult.hasReturnStatement;
		boolean hasReturn=false;
		if (createScope)
			ifScope=env.leaveScope();


		if(ifStmt.elseStmt!=null)
		{
			createScope = !(ifStmt.elseStmt instanceof StmtList);
			if (createScope)
				env.enterScope();
			VisitResult elseStmtResult=ifStmt.elseStmt.accept(this,env);
			Scope elseScope=elseStmtResult.prevScope;
			hasReturn=ifHasReturnStatement && elseStmtResult.hasReturnStatement;
			if (createScope)
				elseScope=env.leaveScope();
			setInitializedEntriesInBothScopes(ifScope,elseScope,env);

		}
		return new VisitResult(hasReturn);
	}

	private void setInitializedEntriesInBothScopes(Scope ifScope,Scope elseScope, Environment env) 
	{
		for (SymbolEntry entry : ifScope.scopeInitializedEntries) 
		{					
			if(elseScope.scopeInitializedEntries.contains(entry))
				env.setEntryInitialized(entry.getEntryName());							
		}
	}

	public VisitResult visit(WhileStmt whileStmt, Environment env) {
		VisitResult whileExprResult =whileStmt.expr.accept(this,env);
		Validator.validateInitialized(whileExprResult, whileStmt.line, env);
		env.validateTypeMismatch(Environment.BOOLEAN,whileExprResult.type,whileStmt.line);
		boolean isWhileTrue=false;
		if(whileExprResult.value!=null)
		{
			isWhileTrue=((Boolean)whileExprResult.value).booleanValue();
		}
		
		boolean createScope = !(whileStmt.stmt instanceof StmtList);
		if (createScope)
			env.enterScope();		
		env.setIsInLoop(true);
		VisitResult whileStmtResult=whileStmt.stmt.accept(this,env);
		Scope preScope=whileStmtResult.prevScope;
		boolean hasReturn=isWhileTrue&&whileStmtResult.hasReturnStatement;
		env.setIsInLoop(false);
		if (createScope)
			preScope=env.leaveScope();		
		
		if(isWhileTrue)
			setInitializedToOldEntries(preScope, env);
							
		return new VisitResult(hasReturn);
	}

	private void setInitializedToOldEntries(Scope preScope, Environment env) {
		
		HashSet<SymbolEntry> entries=new HashSet<SymbolEntry>(preScope.scopeInitializedEntries);
		for (SymbolEntry symbolEntry : preScope.items) {
			if(entries.contains(symbolEntry))
				entries.remove(symbolEntry);
		}
		for (SymbolEntry symbolEntry : entries) {
			env.setEntryInitialized(symbolEntry.getEntryName());
		}
	}

	public VisitResult visit(BreakStmt breakStmt, Environment env) {
		if(!env.getIsInLoop())
			env.handleSemanticError("break cannot be used outside of a while", breakStmt.line);
		return new VisitResult(false);
	}

	public VisitResult visit(ContinueStmt continueStmt, Environment env) {
		if(!env.getIsInLoop())
			env.handleSemanticError("continue cannot be used outside of a while", continueStmt.line);
		return new VisitResult(false);
	}

	public VisitResult visit(DeclarationStmt declarationStmt, Environment env) {
		VisitResult dclrType =declarationStmt.type.accept(this,env);
		Validator.validateLibraryInstantiation(dclrType.type, env, declarationStmt.line);
		
		env.addDeclaration(dclrType.type,declarationStmt.name,declarationStmt.line);
		if(declarationStmt.value!=null)
		{
			VisitResult valueResult = declarationStmt.value.accept(this,env);
			Validator.validateInitialized(valueResult, declarationStmt.line, env);
			env.validateTypeMismatch(dclrType.type, valueResult.type, declarationStmt.line);
			env.setEntryInitialized(declarationStmt.name);
		}
		return new VisitResult(false);
	}

	public VisitResult visit(VirtualCall virtualCall, Environment env) {
		TypeEntry exprType;
		if (virtualCall.expr == null)
			exprType = env.getCurrentClassType();
		else {
			VisitResult exprResult= virtualCall.expr.accept(this, env);
			exprType =exprResult.type;
		}
		if (exprType == null ||exprType.isPrimitive())
		{
			String actualTypeName = exprType == null ? "void" : exprType.getEntryName();
			env.handleSemanticError("Cannot invoke " + virtualCall.name + " on primitive type " + actualTypeName, virtualCall.line);
		}	
		MethodSymbolEntry m = env.getMethodInClass(virtualCall.name, false, exprType);
		if (m == null)
			env.handleSemanticError("The virtual method " + virtualCall.name + " is undefined for the type " + exprType.getEntryName(), virtualCall.line);

		boolean isCallValid = verifyMethodCall(m.getMethodArgs(), virtualCall.callArgs.expressions, env,virtualCall.line);
		if (!isCallValid)
			env.handleSemanticError("The virtual method " + virtualCall.name + " expected " + m.getMethodArgs().size() + " arguments. Passed " + virtualCall.callArgs.expressions.size() + ".", virtualCall.line);

		
		TypeEntry type = m.getEntryTypeID();
		if (type != null){	
			int returnTypeDimensions = type.getTypeDimension();
			if(returnTypeDimensions != 0)
				type = ArrayTypeEntry.makeArrayTypeEntry(type, returnTypeDimensions);
		}
		
		return new VisitResult(type);
		
	}

	public VisitResult visit(StaticCall staticCall, Environment env) {
		String exprTypeName = staticCall.className;
		TypeEntry exprType = env.getTypeEntry(exprTypeName);
		if(exprType==null)
			env.handleSemanticError(staticCall.className + " cannot be resolved", staticCall.line);
	
		if (exprType.isPrimitive())
			env.handleSemanticError("Cannot invoke " + staticCall.name + " on primitive type " + exprType.getEntryName(), staticCall.line);

		MethodSymbolEntry m = env.getMethodInClass(staticCall.name, true, exprType);
		if (m == null)
			env.handleSemanticError("The static method " + staticCall.name + " is undefined for the type " + exprType.getEntryName(), staticCall.line);

		boolean isCallValid = verifyMethodCall(m.getMethodArgs(), staticCall.callArgs.expressions, env,staticCall.line);
		if (!isCallValid)
			env.handleSemanticError("The static method " + staticCall.name + " expected " + m.getMethodArgs().size() + " arguments. Passed " + staticCall.callArgs.expressions.size() +".", staticCall.line);

		TypeEntry type = m.getEntryTypeID();
		if (type != null){	
			int returnTypeDimensions = type.getTypeDimension();
			if(returnTypeDimensions != 0)
				type = ArrayTypeEntry.makeArrayTypeEntry(type, returnTypeDimensions);
		}
		return new VisitResult(type);
	}

	private boolean verifyMethodCall(List<TypeEntry> formals, List<Expr> callArgs, Environment env,int line){
		if (formals.size() != callArgs.size())
			return false;
		
		for (int i = 0; i < formals.size(); i++){
			VisitResult exprResult=  callArgs.get(i).accept(this, env);
			TypeEntry exprType = exprResult.type;			
			env.validateTypeMismatch(formals.get(i), exprType,line);
		}
		
		return true;
	}
	
	public VisitResult visit(ExprList expressions, Environment env) {
		for (Expr e : expressions.expressions){
			e.accept(this, env);
		}
		return null;
	}

	public VisitResult visit(ArrayVarExpr expr, Environment env) {

		VisitResult targetExprResult= expr.target_expr.accept(this, env);
		Validator.validateInitialized(targetExprResult, expr.line, env);
		if(targetExprResult.type ==null || targetExprResult.type.getTypeDimension()==0)
		{
			String actualTypeName = targetExprResult.type == null ? "void" : targetExprResult.type.getEntryName();
			env.handleSemanticError("the type of the expression must be an array type but it resolved to "+actualTypeName, expr.line);
		}
		VisitResult indexExprResult= expr.index_expr.accept(this, env);
		Validator.validateInitialized(indexExprResult, expr.line, env);
		env.validateTypeMismatch(Environment.INT, indexExprResult.type, expr.line);
		TypeEntry type;
		if(targetExprResult.type.getTypeDimension()-1==0)
			type=env.getTypeEntry(targetExprResult.type.getTypeName());
		else
			type=ArrayTypeEntry.makeArrayTypeEntry(targetExprResult.type,targetExprResult.type.getTypeDimension()-1);
		return new VisitResult(type);
	}

	public VisitResult visit(ArrayLenExpr expr, Environment env) {
		VisitResult targetExprResult= expr.expr.accept(this, env);
		Validator.validateInitialized(targetExprResult, expr.line, env);
		
		if(targetExprResult.type ==null || targetExprResult.type.getTypeDimension()==0)
		{
			String actualTypeName = targetExprResult.type == null ? "void" : targetExprResult.type.getEntryName();
			env.handleSemanticError("the type of the expression must be an array type but it resolved to "+actualTypeName, expr.line);
		}
		return new VisitResult(env.getTypeEntry(Environment.INT));
	}
	

	public VisitResult visit(ArrayAllocExpr expr, Environment env) {
		VisitResult exprResult= expr.expr.accept(this, env);
		Validator.validateInitialized(exprResult, expr.line, env);
		env.validateTypeMismatch(Environment.INT, exprResult.type, expr.line);
		
		VisitResult typeResult=expr.type.accept(this, env);		
		Validator.validateLibraryInstantiation(typeResult.type, env, expr.line);
		
		return  new VisitResult(ArrayTypeEntry.makeArrayTypeEntry(typeResult.type,typeResult.type.getTypeDimension()+1));
	}

	public VisitResult visit(StringExpr expr, Environment env) {

		return new VisitResult(env.getTypeEntry(Environment.STRING),expr.value);
	}

	public VisitResult visit(BooleanExpr expr, Environment env) {

		return new VisitResult(env.getTypeEntry(Environment.BOOLEAN),expr.value);
	}

	public VisitResult visit(NullExpr expr, Environment env) { 
		return new VisitResult(env.getTypeEntry(Environment.NULL));
	}

	public VisitResult visit(InstantExpr expr, Environment env) {
		TypeEntry classType= env.getTypeEntry(expr.className); 
		if(classType.getClass()==null)
			env.handleSemanticError("cannot create an instance of type  "+expr.className, expr.line);
		return new VisitResult(classType);
	}

	public VisitResult visit(ThisExpr thisExpr, Environment env) {

		Method currentMethod=env.getCurrentMethod();
		if(currentMethod.isStatic)
			env.handleSemanticError("cannot use this in a static context", thisExpr.line);
		return new VisitResult(env.getCurrentClassType());
	}

	public VisitResult visit(LocationExpr expr, Environment env) {
		return expr.location.accept(this, env);
	}
	
 


}
