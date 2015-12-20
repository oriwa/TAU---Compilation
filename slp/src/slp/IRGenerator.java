package slp;

import java.io.IOException;

public class IRGenerator implements PropagatingVisitor<IREnvironment, IRVisitResult> {
	
	

	private static final String MAIN_METHOD="main";
	
	protected ASTNode root;
	
	/** Constructs an SLP interpreter for the given AST.
	 * 
	 * @param root An SLP AST node.
	 */
	public IRGenerator(ASTNode root) {
		this.root = root;
	}
	
	/** Interprets the AST passed to the constructor.
	 * @throws IOException 
	 */
	public void Generate() throws IOException {
		IREnvironment env = new IREnvironment();
		root.accept(this, env);
		env.commitIR();
	}

	public IRVisitResult visit(Program program, IREnvironment env) {
		initTypeTable(program.classList,env);
		program.classList.accept(this, env);
		return null;
	}
	
	private void initTypeTable(ClassList classList, IREnvironment env)  {
		 
		for (Class clss : classList.classes) {
			env.addTypeEntry(clss);
		}
		for(Class clss:classList.classes){
			env.addDclrs(clss);
		}
	}

	public IRVisitResult visit(ClassList classes, IREnvironment env) {
		for (Class clss : classes.classes) {
			clss.accept(this, env);
		}
		return null;
	}

	public IRVisitResult visit(Class clss, IREnvironment env) {
		env.setCurrentClass(clss);
		
		clss.dclrList.accept(this, env);
		
		env.setCurrentClass(null);
		
		return null;
	}

	public IRVisitResult visit(DclrList list, IREnvironment env) {
		for (Method method : list.methods) {
			method.accept(this, env);
		}
		return null;
	}

	public IRVisitResult visit(Field field, IREnvironment env) {
		// TODO Auto-generated method stub
		return null;
	}

	public IRVisitResult visit(ExtraIDs extraIDs, IREnvironment env) {
		// TODO Auto-generated method stub
		return null;
	}

	public IRVisitResult visit(Method method, IREnvironment env) {
		TypeEntry currentClassType= env.getCurrentClassType();
		
		env.setSymbolTable(currentClassType.getScope(method.isStatic));
		env.writeSectionHeader(currentClassType.getEntryName()+"."+method.name);
		
		boolean isMainMethod= Validator.isMainMethod(method);
		if(isMainMethod)
			env.writeLabel("_ic_main");
		else
			env.writeLabel("_"+currentClassType.getEntryName()+"_"+method.name);
		env.setCurrentMethod(method);
		env.enterScope();
		method.formalsList.accept(this, env);
		method.stmtList.accept(this, env);

		if(Validator.isMainMethod(method))
			env.writeLine("Library __exit(0),"+IREnvironment.RDUMMY);
		else if(method.type==null)
			env.writeLine("Return 9999");
		env.resetRegister();
		env.writeSectionBottom();
		env.leaveScope();
		env.setCurrentMethod(null);
		env.setSymbolTable(null);
		return null;
	}

	public IRVisitResult visit(FormalsList formalsList, IREnvironment env) {
		for (Formals f : formalsList.formals){
			f.accept(this, env);
		}
		return null;
	}

	public IRVisitResult visit(Formals formals, IREnvironment env) {
		formals.type.accept(this, env);
		
		env.addToEnv(formals);
		return null;
	}

	public IRVisitResult visit(Type type, IREnvironment env) {
		// TODO Auto-generated method stub
		return null;
	}

	public IRVisitResult visit(StmtList stmts, IREnvironment env) {
		env.enterScope();
		for (Stmt st : stmts.statements) {
			st.accept(this, env);
		}
		env.leaveScope();
		return null;
	}

	public IRVisitResult visit(Stmt stmt, IREnvironment env) {
		throw new UnsupportedOperationException("Unexpected visit of Stmt!");
	}

	public IRVisitResult visit(AssignStmt stmt, IREnvironment env) {
		// TODO Auto-generated method stub
		return null;
	}

	public IRVisitResult visit(CallStmt callStmt, IREnvironment env) {
		// TODO Auto-generated method stub
		return null;
	}

	public IRVisitResult visit(ReturnStmt returnStmt, IREnvironment env) {
		 
		String returnValue="9999";
		if(returnStmt.expr!=null)
		{
			IRVisitResult exprResult= returnStmt.expr.accept(this,env);
			returnValue=exprResult.value.toString();
		}
		env.writeLine("Return "+returnValue);
		return null;
	}

	public IRVisitResult visit(IfStmt ifStmt, IREnvironment env) {
		IRVisitResult exprResult= ifStmt.expr.accept(this,env); 
		
		boolean createScope = !(ifStmt.ifStmt instanceof StmtList);
		if (createScope)
			env.enterScope();
		String ifLabelKey = env.getLabelKey();
		env.writeLine("JumpFalse "+ifLabelKey+","+exprResult.value);
		ifStmt.ifStmt.accept(this,env); 		
		if (createScope)
			env.leaveScope();
		if(ifStmt.elseStmt!=null)
		{
			String elseLabelKey = env.getLabelKey();
			env.writeLine("Jump "+elseLabelKey);
			env.writeLabel(ifLabelKey);
			createScope = !(ifStmt.elseStmt instanceof StmtList);
			if (createScope)
				env.enterScope();
			ifStmt.elseStmt.accept(this,env);
			if (createScope)
				env.leaveScope();
			env.writeLabel(elseLabelKey);
		}
		else
		{
			env.writeLabel(ifLabelKey);
		}
		

		return null;
	}

	public IRVisitResult visit(WhileStmt whileStmt, IREnvironment env) {
		
		

		String whileLabelKey = env.getLabelKey();
		String stopLabelKey = env.getLabelKey();
		
		env.writeLabel(whileLabelKey);
		IRVisitResult whileExprResult =whileStmt.expr.accept(this,env);  
		

		env.writeLine("JumpFalse "+stopLabelKey+","+whileExprResult.value);
		
		boolean createScope = !(whileStmt.stmt instanceof StmtList);
		if (createScope)
			env.enterScope();		
		env.pushWhileLabels(whileLabelKey, stopLabelKey);
		whileStmt.stmt.accept(this,env);  
		
		env.writeLine("Jump "+whileLabelKey);
		env.writeLabel(stopLabelKey);
		
		env.popWhileLabels();
		if (createScope)
			env.leaveScope();		
		
							
		return null;
	}

	public IRVisitResult visit(BreakStmt breakStmt, IREnvironment env) {
		WhileLabels whileLabels=env.getCurrentWhileLabels();
		env.writeLine("Jump "+whileLabels.stopLabelKey);
		return null;
	}

	public IRVisitResult visit(ContinueStmt continueStmt, IREnvironment env) {
		WhileLabels whileLabels=env.getCurrentWhileLabels();
		env.writeLine("Jump "+whileLabels.whileLabelKey);
		return null;
	}

	public IRVisitResult visit(DeclarationStmt declarationStmt, IREnvironment env) {
		// TODO Auto-generated method stub
		return null;
	}

	public IRVisitResult visit(VirtualCall virtualCall, IREnvironment env) {
		// TODO Auto-generated method stub
		return null;
	}

	public IRVisitResult visit(StaticCall staticCall, IREnvironment env) {
		// TODO Auto-generated method stub
		return null;
	}

	public IRVisitResult visit(ExprList expressions, IREnvironment env) {
		// TODO Auto-generated method stub
		return null;
	}

	public IRVisitResult visit(Expr expr, IREnvironment env) {
		// TODO Auto-generated method stub
		return null;
	}

	public IRVisitResult visit(LocationExpr expr, IREnvironment env) {
		// TODO Auto-generated method stub
		return null;
	}

	public IRVisitResult visit(ThisExpr thisExpr, IREnvironment env) {
		String registerKey= env.getRegisterKey();
		env.writeLine("Move this,"+registerKey );
	
		return new IRVisitResult(env.getCurrentClassType(),registerKey);
	}

	public IRVisitResult visit(InstantExpr expr, IREnvironment env) {
		TypeEntry classType= env.getTypeEntry(expr.className);  
		String registerKey= env.getRegisterKey();
		int typeSize= (classType.fieldMap.size()+1)*4;
		env.writeLine("Library __allocateObject("+typeSize+"),"+registerKey);
		env.writeLine("MoveField "+classType.getUniqueName()+","+registerKey+".0");
		return new IRVisitResult(classType,registerKey);
	}

	public IRVisitResult visit(VarExpr expr, IREnvironment env) {
		// TODO Auto-generated method stub
		return null;
	}

	public IRVisitResult visit(ArrayVarExpr expr, IREnvironment env) {
		// TODO Auto-generated method stub
		return null;
	}

	public IRVisitResult visit(ArrayLenExpr expr, IREnvironment env) {
		// TODO Auto-generated method stub
		return null;
	}

	public IRVisitResult visit(ArrayAllocExpr expr, IREnvironment env) {
		// TODO Auto-generated method stub
		return null;
	}

	public IRVisitResult visit(NumberExpr expr, IREnvironment env) {
		// TODO Auto-generated method stub
		return null;
	}

	public IRVisitResult visit(StringExpr expr, IREnvironment env) {
		String stringLitralKey=env.getStringLitralKey(expr.value);		 
		return new IRVisitResult(env.getTypeEntry(Environment.STRING),stringLitralKey);
	}

	public IRVisitResult visit(BooleanExpr expr, IREnvironment env) {
		// TODO Auto-generated method stub
		return null;
	}

	public IRVisitResult visit(NullExpr expr, IREnvironment env) {
		// TODO Auto-generated method stub
		return null;
	}

	public IRVisitResult visit(UnaryOpExpr expr, IREnvironment env) {
		// TODO Auto-generated method stub
		return null;
	}

	public IRVisitResult visit(BinaryOpExpr expr, IREnvironment env) {
		// TODO Auto-generated method stub
		return null;
	}

	 

}
