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
			env.writeCode("Library __exit(0),"+IREnvironment.RDUMMY);
		else if(method.type==null)
			env.writeCode("Return 9999");
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

		IRVisitResult rhsResult=stmt.rhs.accept(this, env);
		String rhsRegisterKey = env.getRegisterKey();
		env.writeCode(rhsResult.moveInstruction+" "+rhsResult.value+","+rhsRegisterKey);
		IRVisitResult locationResult=stmt.location.accept(this, env);
		env.writeCode(locationResult.moveInstruction+" "+rhsRegisterKey+","+locationResult.value);
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
		env.writeCode("Return "+returnValue);
		return null;
	}

	public IRVisitResult visit(IfStmt ifStmt, IREnvironment env) {
		IRVisitResult exprResult= ifStmt.expr.accept(this,env); 
		
		boolean createScope = !(ifStmt.ifStmt instanceof StmtList);
		if (createScope)
			env.enterScope();
		String ifLabelKey = env.getLabelKey();
		env.writeCode("JumpFalse "+ifLabelKey+","+exprResult.value);
		ifStmt.ifStmt.accept(this,env); 		
		if (createScope)
			env.leaveScope();
		if(ifStmt.elseStmt!=null)
		{
			String elseLabelKey = env.getLabelKey();
			env.writeCode("Jump "+elseLabelKey+","+IREnvironment.RDUMMY);
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
		

		env.writeCode("JumpFalse "+stopLabelKey+","+whileExprResult.value);
		
		boolean createScope = !(whileStmt.stmt instanceof StmtList);
		if (createScope)
			env.enterScope();		
		env.pushWhileLabels(whileLabelKey, stopLabelKey);
		whileStmt.stmt.accept(this,env);  
		
		env.writeCode("Jump "+whileLabelKey+","+IREnvironment.RDUMMY);
		env.writeLabel(stopLabelKey);
		
		env.popWhileLabels();
		if (createScope)
			env.leaveScope();		
		
							
		return null;
	}

	public IRVisitResult visit(BreakStmt breakStmt, IREnvironment env) {
		WhileLabels whileLabels=env.getCurrentWhileLabels();
		env.writeCode("Jump "+whileLabels.stopLabelKey+","+IREnvironment.RDUMMY);
		return null;
	}

	public IRVisitResult visit(ContinueStmt continueStmt, IREnvironment env) {
		WhileLabels whileLabels=env.getCurrentWhileLabels();
		env.writeCode("Jump "+whileLabels.whileLabelKey+","+IREnvironment.RDUMMY);
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
		return expr.location.accept(this, env);
	}

	public IRVisitResult visit(ThisExpr thisExpr, IREnvironment env) {
		String registerKey= env.getRegisterKey();
		env.writeCode("Move this,"+registerKey );
	
		return new IRVisitResult(env.getCurrentClassType(),registerKey);
	}

	public IRVisitResult visit(InstantExpr expr, IREnvironment env) {
		TypeEntry classType= env.getTypeEntry(expr.className);  
		String registerKey= env.getRegisterKey();
		int typeSize= (classType.fieldMap.size()+1)*4;
		env.writeCode("Library __allocateObject("+typeSize+"),"+registerKey);
		env.writeCode("MoveField "+classType.getUniqueName()+","+registerKey+".0");
		return new IRVisitResult(classType,registerKey);
	}

	public IRVisitResult visit(VarExpr expr, IREnvironment env) {
		// TODO Auto-generated method stub
		return null;
	}

	public IRVisitResult visit(ArrayVarExpr expr, IREnvironment env) {
		IRVisitResult targetExprResult= expr.target_expr.accept(this, env); 
		IRVisitResult indexExprResult= expr.index_expr.accept(this, env); 		
		TypeEntry type;
		if(targetExprResult.type.getTypeDimension()-1==0)
			type=env.getTypeEntry(targetExprResult.type.getTypeName());
		else
			type=ArrayTypeEntry.makeArrayTypeEntry(targetExprResult.type,targetExprResult.type.getTypeDimension()-1);

		env.writeCode("Library __checkNullRef("+targetExprResult.value+"),"+IREnvironment.RDUMMY);
		env.writeCode("Library __checkArrayAccess("+targetExprResult.value+indexExprResult.value+"),"+IREnvironment.RDUMMY);
		IRVisitResult irVisitResult=new IRVisitResult(type,targetExprResult.value+"["+indexExprResult.value+"]");
		irVisitResult.moveInstruction="MoveArray";
		return irVisitResult;
	}

	public IRVisitResult visit(ArrayLenExpr expr, IREnvironment env) {
		
		IRVisitResult targetExprResult= expr.expr.accept(this, env);
		String registerKey= env.getRegisterKey();
		env.writeCode("Library __checkNullRef("+targetExprResult.value+"),"+IREnvironment.RDUMMY);
		env.writeCode("ArrayLength "+targetExprResult.value+","+registerKey);
		return new IRVisitResult(env.getTypeEntry(Environment.INT),registerKey);
	}

	public IRVisitResult visit(ArrayAllocExpr expr, IREnvironment env) {
		IRVisitResult exprResult= expr.expr.accept(this, env); 
		IRVisitResult typeResult=expr.type.accept(this, env);		 
		String registerKey= env.getRegisterKey();
		env.writeCode("Library __checkSize("+exprResult.value+"),"+IREnvironment.RDUMMY);
		env.writeCode("Library __allocateObject("+exprResult.value+"),"+registerKey);
		return  new IRVisitResult(ArrayTypeEntry.makeArrayTypeEntry(typeResult.type,typeResult.type.getTypeDimension()+1),registerKey);
	}

	public IRVisitResult visit(NumberExpr expr, IREnvironment env) {
		return new IRVisitResult(env.getTypeEntry(IREnvironment.INT),expr.value);
	}

	public IRVisitResult visit(StringExpr expr, IREnvironment env) {
		String stringLitralKey=env.getStringLitralKey(expr.value);		 
		return new IRVisitResult(env.getTypeEntry(IREnvironment.STRING),stringLitralKey);
	}

	public IRVisitResult visit(BooleanExpr expr, IREnvironment env) {
		return new IRVisitResult(env.getTypeEntry(IREnvironment.BOOLEAN),expr.value?1:0);
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
