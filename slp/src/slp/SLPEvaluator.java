package slp;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Evaluates straight line programs.
 */
public class SLPEvaluator implements PropagatingVisitor<Environment, VisitResult> {
	
	

	private static final String MAIN_METHOD="main";
	
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
			SymbolTable symbolTable=targetResult.type.getScope(false);
			SymbolEntry symbolEntry=symbolTable.getEntryByName(expr.name);
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
			if(visitResult.isInitialized)
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
		
		Validator.validateIlegalOp(lhsValue.type,lhsValue.type,expr.op, expr.line,env);


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
		
		
//		int result = 0;
//		switch (expr.op) {
//		case DIVIDE:
//			if (rhsInt == 0)
//				env.handleSemanticError("Attempt to divide by zero: " + expr, expr.line);
//			result = lhsInt / rhsInt;
//			break;
//		case MINUS:
//			result = lhsInt - rhsInt;
//			break;
//		case MULTIPLY:
//			result = lhsInt * rhsInt;
//			break;
//		case PLUS:
//			result = lhsInt + rhsInt;
//			break;
//		case LT:
//			result = lhsInt < rhsInt ? 1 : 0;
//			break;
//		case GT:
//			result = lhsInt > rhsInt ? 1 : 0;
//			break;
//		case LTE:
//			result = lhsInt <= rhsInt ? 1 : 0;
//			break;
//		case GTE:
//			result = lhsInt >= rhsInt ? 1 : 0;
//			break;
//		case LAND:
//			result = (lhsInt!=0 && rhsInt!=0) ? 1 : 0;
//			break;
//		case LOR:
//			result = (lhsInt!=0 || rhsInt!=0) ? 1 : 0;
//			break;
//		default:
//			env.handleSemanticError("Encountered unexpected operator type: " + expr.op, expr.line);
//		}

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
		env.enterScope();
			
		clss.dclrList.accept(this, env);
		
		env.leaveScope();
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
		
//		for (Dclr decleration : list.declarations) {
//			decleration.accept(this, env);
//		}

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
		
		if(IsMainMethod(method))
			env.addMainMethodNumber();
		
		env.setCurrentMethod(method);
		method.formalsList.accept(this, env);
		VisitResult stmtListResult=method.stmtList.accept(this, env);
		if(!stmtListResult.hasReturnStatement && method.type!=null)
			env.handleSemanticError("this method must return a result of type "+method.type.name, method.line);
		env.setCurrentMethod(null);
		env.setSymbolTable(null);
		return null;
	} 
	 

	private boolean IsMainMethod(Method method) {

		if(method.isStatic && method.name==MAIN_METHOD && method.type==null)
		{
			if(method.formalsList.formals.size()==1)
			{
				Formals formal=method.formalsList.formals.get(0);
				if(formal.type.name==Environment.STRING&& formal.type.array_dimension==1)
					return true;
			}
		}					
		return false;
	}

	public VisitResult visit(FormalsList formalsList, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public VisitResult visit(Formals formals, Environment d) {
		// TODO Auto-generated method stub
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
			VisitResult exprResult= returnStmt.expr.accept(this,env);
			Validator.validateInitialized(exprResult, returnStmt.line, env);
			env.validateTypeMismatch(currentMethod.type.name, exprResult.type, returnStmt.line);
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
		boolean hasReturn=ifStmtResult.hasReturnStatement;
		if (createScope)
			ifScope=env.leaveScope();


		if(ifStmt.elseStmt!=null)
		{
			createScope = !(ifStmt.elseStmt instanceof StmtList);
			if (createScope)
				env.enterScope();
			VisitResult elseStmtResult=ifStmt.elseStmt.accept(this,env);
			Scope elseScope=elseStmtResult.prevScope;
			hasReturn=hasReturn && elseStmtResult.hasReturnStatement;
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
			env.validateTypeMismatch(declarationStmt.type.name, valueResult.type, declarationStmt.line);
			env.setEntryInitialized(declarationStmt.name);
		}
		return new VisitResult(false);
	}

	public VisitResult visit(VirtualCall virtualCall, Environment env) {
		VisitResult exprResult= virtualCall.expr.accept(this, env);
		TypeEntry exprType =exprResult.type;
		
		if (exprType.isPrimitive())
			env.handleSemanticError("Cannot invoke " + virtualCall.name + " on primitive type " + exprType.getEntryName(), virtualCall.line);

		Method m = env.getMethod(exprType.getEntryClass(), virtualCall.name);
		if (m == null)
			env.handleSemanticError("The method " + virtualCall.name + " is undefined for the type " + exprType.getEntryName(), virtualCall.line);

		if (m.isStatic)
			env.handleSemanticError("Cannot invoke static method " + virtualCall.name + " on an instance", virtualCall.line);

		boolean isCallValid = verifyMethodCall(m.formalsList.formals, virtualCall.callArgs.expressions, env);
		if (!isCallValid)
			env.handleSemanticError("The method " + virtualCall.name + " is undefined for the argumetns " + virtualCall.callArgs.expressions.toString(), virtualCall.line);

		TypeEntry type=env.getTypeEntry(m.type.name);
		if(m.type.array_dimension!=0)
			type=ArrayTypeEntry.makeArrayTypeEntry(type,m.type.array_dimension);
		return new VisitResult(type);
		
	}

	public VisitResult visit(StaticCall staticCall, Environment env) {
		String exprTypeName = staticCall.className;
		TypeEntry exprType = env.getTypeEntry(exprTypeName);
		
		if (exprType.isPrimitive())
			env.handleSemanticError("Cannot invoke " + staticCall.name + " on primitive type " + exprType.getEntryName(), staticCall.line);

		Method m = env.getMethod(exprType.getEntryClass(), staticCall.name);
		if (m == null)
			env.handleSemanticError("The method " + staticCall.name + " is undefined for the type " + exprType.getEntryName(), staticCall.line);

		if (!m.isStatic)
			env.handleSemanticError("Cannot invoke virtual method " + staticCall.name + " on class", staticCall.line);

		boolean isCallValid = verifyMethodCall(m.formalsList.formals, staticCall.callArgs.expressions, env);
		if (!isCallValid)
			env.handleSemanticError("The method " + staticCall.name + " is undefined for the argumetns " + staticCall.callArgs.expressions.toString(), staticCall.line);

		
		TypeEntry type=env.getTypeEntry(m.type.name);
		if(m.type.array_dimension!=0)
			type=ArrayTypeEntry.makeArrayTypeEntry(type,m.type.array_dimension);
		return new VisitResult(type);
	}

	public boolean verifyMethodCall(List<Formals> formals, List<Expr> callArgs, Environment env){
		if (formals.size() != callArgs.size())
			return false;
		
		for (int i = 0; i < formals.size(); i++){
			VisitResult exprResult=  callArgs.get(i).accept(this, env);
			TypeEntry exprType = exprResult.type;
			
			String formalTypeName = formals.get(0).type.name;
			TypeEntry formalType = env.getTypeEntry(formalTypeName);
			
			if (!exprType.equals(formalType))
				return false;
		}
		
		return true;
	}
	
	public VisitResult visit(ExprList expressions, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public VisitResult visit(ArrayVarExpr expr, Environment env) {

		VisitResult targetExprResult= expr.target_expr.accept(this, env);
		Validator.validateInitialized(targetExprResult, expr.line, env);
		if(targetExprResult.type.getTypeDimension()==0)
			env.handleSemanticError("the type of the expression must be an array type but it resolved to "+targetExprResult.type.getEntryName(), expr.line);
		
		VisitResult indexExprResult= expr.index_expr.accept(this, env);
		Validator.validateInitialized(indexExprResult, expr.line, env);
		env.validateTypeMismatch(Environment.INT, indexExprResult.type, expr.line);
		TypeEntry type;
		if(targetExprResult.type.getTypeDimension()-1==0)
			type=env.getTypeEntry(targetExprResult.type.getEntryName());
		else
			type=ArrayTypeEntry.makeArrayTypeEntry(targetExprResult.type,targetExprResult.type.getTypeDimension()-1);
		return new VisitResult(type);
	}

	public VisitResult visit(ArrayLenExpr expr, Environment env) {
		VisitResult targetExprResult= expr.expr.accept(this, env);
		Validator.validateInitialized(targetExprResult, expr.line, env);
		if(targetExprResult.type.getTypeDimension()==0)
			env.handleSemanticError("the type of the expression must be an array type but it resolved to "+targetExprResult.type.getEntryName(), expr.line);
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
		if(classType.getClass()!=null)
			env.handleSemanticError("cannot create an instance of type  "+expr.className, expr.line);
		return new VisitResult(classType);
	}

	public VisitResult visit(ThisExpr thisExpr, Environment env) {

		Method currentMethod=env.getCurrentMethod();
		if(!currentMethod.isStatic)
			env.handleSemanticError("cannot use this in a static context", thisExpr.line);
		return new VisitResult(env.getTypeEntry(currentMethod.name));
	}
	
 


}
