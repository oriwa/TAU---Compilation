package slp;

import java.io.IOException;
import java.util.List;

import slp.SymbolEntry.ReferenceRole;

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
		initLibrary(env);
		for (Class clss : classList.classes) {
			env.addTypeEntry(clss);
		}
		for(Class clss:classList.classes){
			env.addDclrs(clss);
		}
	}
	
	private void initLibrary(IREnvironment env) {
		LibraryLoader loader=new LibraryLoader();
		loader.load(env);	
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
		TypeEntry typeEntry=env.getTypeEntry(type.name); 
		if(type.array_dimension!=0)
			typeEntry=ArrayTypeEntry.makeArrayTypeEntry(typeEntry,type.array_dimension); 
		return new IRVisitResult(typeEntry,null);
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

		env.setIsLeftHandSideExpr(true);
		IRVisitResult locationResult=stmt.location.accept(this, env);
		env.setIsLeftHandSideExpr(false);
		env.writeInstruction(locationResult.moveInstruction, rhsResult.value, locationResult.value);
		return null;
	}

	public IRVisitResult visit(CallStmt callStmt, IREnvironment env) {
		callStmt.call.accept(this,env);
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
		IRVisitResult exprResult =ifStmt.expr.accept(this,env); 
		
		boolean createScope = !(ifStmt.ifStmt instanceof StmtList);
		if (createScope)
			env.enterScope();
		String ifLabelKey = env.getLabelKey();
		env.writeInstruction("Compare", 0,exprResult.value);
		env.writeCode("JumpTrue "+ifLabelKey);
		ifStmt.ifStmt.accept(this,env); 		
		if (createScope)
			env.leaveScope();
		if(ifStmt.elseStmt!=null)
		{
			String elseLabelKey = env.getLabelKey();
			env.writeCode("Jump "+elseLabelKey);
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
		IRVisitResult exprResult =whileStmt.expr.accept(this,env);  
		

		env.writeInstruction("Compare", 0,exprResult.value);
		env.writeCode("JumpTrue "+stopLabelKey);
		
		boolean createScope = !(whileStmt.stmt instanceof StmtList);
		if (createScope)
			env.enterScope();		
		env.pushWhileLabels(whileLabelKey, stopLabelKey);
		whileStmt.stmt.accept(this,env);  
		
		env.writeCode("Jump "+whileLabelKey);
		env.writeLabel(stopLabelKey);
		
		env.popWhileLabels();
		if (createScope)
			env.leaveScope();		
		
							
		return null;
	}

	public IRVisitResult visit(BreakStmt breakStmt, IREnvironment env) {
		WhileLabels whileLabels=env.getCurrentWhileLabels();
		env.writeCode("Jump "+whileLabels.stopLabelKey);
		return null;
	}

	public IRVisitResult visit(ContinueStmt continueStmt, IREnvironment env) {
		WhileLabels whileLabels=env.getCurrentWhileLabels();
		env.writeCode("Jump "+whileLabels.whileLabelKey);
		return null;
	}

	public IRVisitResult visit(DeclarationStmt declarationStmt, IREnvironment env) {
		IRVisitResult dclrType =declarationStmt.type.accept(this,env); 
		SymbolEntry  symbolEntry =env.addDeclaration(dclrType.type,declarationStmt.name,declarationStmt.line);
		if(declarationStmt.value!=null)
		{
			IRVisitResult valueResult = declarationStmt.value.accept(this,env);
			env.writeInstruction("Move", valueResult.value, symbolEntry.uniqueName);
		} 
		return null;
	}

	public IRVisitResult visit(VirtualCall virtualCall, IREnvironment env) {
		TypeEntry exprType;
		Object expValue;
		if (virtualCall.expr == null)
		{
			exprType = env.getCurrentClassType();
			String thisReg=getThisReg(env);
			expValue=thisReg;
		}
		else 
		{
			IRVisitResult exprResult= virtualCall.expr.accept(this, env);
			expValue=exprResult.value;
			exprType =exprResult.type;
			//env.writeCode("Library __checkNullRef("+expValue+"),"+IREnvironment.RDUMMY);
			//TODO: implement
		}
		int dispatchVectorIndex= exprType.dispatchVectorMap.get(virtualCall.name);
		MethodSymbolEntry m = env.getMethodInClass(virtualCall.name, false, exprType);

		String methodCallArgs = prepareMethodCallArgs(m.getMethodArgsNames(), virtualCall.callArgs.expressions, false, env);

		String registerKey=IREnvironment.RDUMMY;
		TypeEntry type = m.getEntryTypeID();
		if (type != null){	
			int returnTypeDimensions = type.getTypeDimension();
			if(returnTypeDimensions != 0)
				type = ArrayTypeEntry.makeArrayTypeEntry(type, returnTypeDimensions);
			registerKey= env.getRegisterKey();	
		}
 
		String op1= expValue+"."+dispatchVectorIndex+"(this="+expValue;
		if(methodCallArgs.equals(""))
			op1+=")";
		else
			op1+=","+methodCallArgs+")";
		env.writeInstruction("VirtualCall",op1, registerKey);
		
		
		return new IRVisitResult(type,registerKey);
	}

	private String prepareMethodCallArgs(List<String> methodArgs,List<Expr> expressions, boolean isLibraryClass, IREnvironment env) {
		
		String methodCallArgs="";
		int count=0;
		for (String methodArg : methodArgs) {
			IRVisitResult exrResult=expressions.get(count).accept(this, env);
			if (!isLibraryClass)
				methodCallArgs +=methodArg + "=";
			methodCallArgs+=exrResult.value;
			count++;
			if(count!=expressions.size())
				methodCallArgs+=",";
		}
		return methodCallArgs;

	}

	public IRVisitResult visit(StaticCall staticCall, IREnvironment env) {
		String exprTypeName = staticCall.className;
		TypeEntry exprType = env.getTypeEntry(exprTypeName);
		boolean isLibraryClass = Validator.isLibraryClass(exprTypeName);
		
		MethodSymbolEntry m = env.getMethodInClass(staticCall.name, true, exprType);

		String methodCallArgs = prepareMethodCallArgs(m.getMethodArgsNames(), staticCall.callArgs.expressions, isLibraryClass, env);
		 
		String registerKey=IREnvironment.RDUMMY;
		TypeEntry type = m.getEntryTypeID();
		if (type != null){	
			int returnTypeDimensions = type.getTypeDimension();
			if(returnTypeDimensions != 0)
				type = ArrayTypeEntry.makeArrayTypeEntry(type, returnTypeDimensions);
			registerKey= env.getRegisterKey();	
		}
		
		String op1 = m.uniqueName+"("+methodCallArgs+")";
		String instruction = "StaticCall";
		if (isLibraryClass){
			op1 = "__" + m.getEntryName() + "("+methodCallArgs+")";
			instruction = "Library";
		}
		env.writeInstruction(instruction,op1, registerKey);

		return new IRVisitResult(type,registerKey);
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
		String registerKey = getThisReg(env);
	
		return new IRVisitResult(env.getCurrentClassType(),registerKey);
	}

	private String getThisReg(IREnvironment env) {
		String registerKey= env.getRegisterKey();	
		env.writeInstruction("Move", "this", registerKey);
		return registerKey;
	}

	public IRVisitResult visit(InstantExpr expr, IREnvironment env) {
		TypeEntry classType= env.getTypeEntry(expr.className);  
		String registerKey= env.getRegisterKey();
		int typeSize= (classType.fieldMap.size()+1)*4;
		env.writeCode("Library __allocateObject("+typeSize+"),"+registerKey);
		env.writeInstruction("MoveField", classType.getUniqueName(), registerKey+".0");
		return new IRVisitResult(classType,registerKey);
	}

	public IRVisitResult visit(VarExpr expr, IREnvironment env) {

		boolean isLeftHandSideExpr=env.isLeftHandSideExpr();
		env.setIsLeftHandSideExpr(false);
		IRVisitResult visitResult=new IRVisitResult();
		if(expr.target_expr!=null)
		{
			IRVisitResult targetResult=expr.target_expr.accept(this, env);
			SymbolEntry symbolEntry=null;
			if(targetResult.type!=null)
			{
				SymbolTable symbolTable=targetResult.type.getScope(false);
				symbolEntry=symbolTable.getEntryByName(expr.name);
				//env.writeCode("Library __checkNullRef("+targetResult.value+"),"+IREnvironment.RDUMMY);
				//TODO: implement
				moveField(expr, env, isLeftHandSideExpr, visitResult, targetResult.type,targetResult.value);

			}			
			visitResult.type=symbolEntry.getEntryTypeID();
		}
		else
		{ 
			SymbolEntry symbolEntry=env.getSymbolEntry(expr.name);
			visitResult.type=symbolEntry.getEntryTypeID();
			if(symbolEntry.role!=ReferenceRole.FIELD)
			{
				if(!isLeftHandSideExpr)
				{ 
					String registerKey= env.getRegisterKey();
					env.writeInstruction("Move", symbolEntry.uniqueName,registerKey);
					visitResult.value=registerKey;
				}
				else
				{				
					visitResult.value=symbolEntry.uniqueName;
				}
			}
			else
			{
				TypeEntry type =env.getCurrentClassType();
				String thisRegisterKey = getThisReg(env);
				moveField(expr, env, isLeftHandSideExpr, visitResult, type,thisRegisterKey);
			}
		}
		return visitResult;
	}

	private void moveField(VarExpr expr, IREnvironment env,boolean isLeftHandSideExpr, IRVisitResult visitResult,TypeEntry type, Object typeRegisterKey) {
		Integer fieldIndex= type.fieldMap.get(expr.name);
		if(!isLeftHandSideExpr)
		{ 
			String registerKey= env.getRegisterKey();
			env.writeInstruction("MoveField",typeRegisterKey+"."+fieldIndex,registerKey);
			visitResult.value= registerKey;
		}
		else
		{ 
			visitResult.value= typeRegisterKey+"."+fieldIndex;
			visitResult.moveInstruction="MoveField";
		}
	}

	public IRVisitResult visit(ArrayVarExpr expr, IREnvironment env) {
		
		boolean isLeftHandSideExpr=env.isLeftHandSideExpr();
		env.setIsLeftHandSideExpr(false);
		
		IRVisitResult targetExprResult= expr.target_expr.accept(this, env); 
		IRVisitResult indexExprResult= expr.index_expr.accept(this, env); 		
		TypeEntry type;
		if(targetExprResult.type.getTypeDimension()-1==0)
			type=env.getTypeEntry(targetExprResult.type.getTypeName());
		else
			type=ArrayTypeEntry.makeArrayTypeEntry(targetExprResult.type,targetExprResult.type.getTypeDimension()-1);

		//env.writeCode("Library __checkNullRef("+targetExprResult.value+"),"+IREnvironment.RDUMMY);
		//env.writeCode("Library __checkArrayAccess("+targetExprResult.value+","+indexExprResult.value+"),"+IREnvironment.RDUMMY);
		//TODO: implement
		IRVisitResult irVisitResult=null;
		if(!isLeftHandSideExpr)
		{ 
			String registerKey= env.getRegisterKey();
			env.writeInstruction("MoveArray",targetExprResult.value+"["+indexExprResult.value+"]",registerKey);
			irVisitResult=new IRVisitResult(type,registerKey);
		}
		else
		{
			irVisitResult=new IRVisitResult(type,targetExprResult.value+"["+indexExprResult.value+"]");
			irVisitResult.moveInstruction="MoveArray";
		}
		return irVisitResult;
	}

	public IRVisitResult visit(ArrayLenExpr expr, IREnvironment env) {
		
		IRVisitResult targetExprResult= expr.expr.accept(this, env);
		String registerKey= env.getRegisterKey();
		//env.writeCode("Library __checkNullRef("+targetExprResult.value+"),"+IREnvironment.RDUMMY);
		//TODO: implement
		env.writeInstruction("ArrayLength",targetExprResult.value,registerKey);
		return new IRVisitResult(env.getTypeEntry(Environment.INT),registerKey);
	}

	public IRVisitResult visit(ArrayAllocExpr expr, IREnvironment env) {
		IRVisitResult exprResult= expr.expr.accept(this, env); 
		IRVisitResult typeResult=expr.type.accept(this, env);		 
		String registerKey= env.getRegisterKey();

		//env.writeCode("Library __checkSize("+exprResult.value+"),"+IREnvironment.RDUMMY);
		//TODO: implement
		env.writeCode("Library __allocateArray("+exprResult.value+"),"+registerKey);
		

		return  new IRVisitResult(ArrayTypeEntry.makeArrayTypeEntry(typeResult.type,typeResult.type.getTypeDimension()+1),registerKey);
	}

	public IRVisitResult visit(NumberExpr expr, IREnvironment env) {
		String registerKey= env.getRegisterKey();
		env.writeInstruction("Move", expr.value,registerKey);
		return new IRVisitResult(env.getTypeEntry(IREnvironment.INT),registerKey);
	}

	public IRVisitResult visit(StringExpr expr, IREnvironment env) {
		String stringLiteralKey=env.getStringLitralKey(expr.value);		 
		return new IRVisitResult(env.getTypeEntry(IREnvironment.STRING),stringLiteralKey);
	}

	public IRVisitResult visit(BooleanExpr expr, IREnvironment env) {
		String registerKey= env.getRegisterKey();
		env.writeInstruction("Move", expr.value?1:0,registerKey);
		return new IRVisitResult(env.getTypeEntry(IREnvironment.BOOLEAN),registerKey);
	}

	public IRVisitResult visit(NullExpr expr, IREnvironment env) {
		String registerKey= env.getRegisterKey();
		env.writeInstruction("Move", 0,registerKey);
		return new IRVisitResult(env.getTypeEntry(IREnvironment.NULL),registerKey);
	}

	public IRVisitResult visit(UnaryOpExpr expr, IREnvironment env) {
		IRVisitResult exprResult= expr.operand.accept(this, env);
		
		String registerKey= env.getRegisterKey();
		env.writeInstruction("Move", exprResult.value, registerKey);
		if(expr.op==Operator.LNEG){
			//if True==1 and False==0, then (bool Xor 1) == !bool
			env.writeInstruction("Xor", 1, registerKey);
		}
		else {//expr.op==Operator.MINUS
			env.writeInstruction("Mul", -1, registerKey);
		}
		
		return new IRVisitResult(exprResult.type,registerKey);
	}

	public IRVisitResult visit(BinaryOpExpr expr, IREnvironment env) {

		
		IRVisitResult rhsResult= expr.rhs.accept(this, env);
		IRVisitResult lhsResult=null;
		String cmprLabelKey = env.getLabelKey(),eventuallyLabelKey;
		if(expr.op!=Operator.LAND && expr.op!=Operator.LOR){
			lhsResult= expr.lhs.accept(this, env);
		}
		else eventuallyLabelKey=env.getLabelKey();
		
		
		
		TypeEntry exprType=null;
		String registerKey=env.getRegisterKey();
		
		env.writeInstruction("Move",rhsResult.value,registerKey);
		
		
		
		
		switch (expr.op){
		case EQUAL:
			exprType=env.getTypeEntry(Environment.BOOLEAN);
			
			writeConditionalBool(registerKey,lhsResult.value,"JumpFalse", env);
			
			break;
		case NEQUAL:
			exprType=env.getTypeEntry(Environment.BOOLEAN);
			
			writeConditionalBool(registerKey,lhsResult.value,"JumpTrue", env);
			break;
		case GTE:
			exprType=env.getTypeEntry(Environment.BOOLEAN);
			
			writeConditionalBool(registerKey,lhsResult.value,"JumpGE", env);
			break;
		case LTE:
			exprType=env.getTypeEntry(Environment.BOOLEAN);
			writeConditionalBool(registerKey,lhsResult.value,"JumpLE", env);
			break;
		case GT:
			exprType=env.getTypeEntry(Environment.BOOLEAN);
			
			writeConditionalBool(registerKey,lhsResult.value,"JumpG", env);
			break;
		case LT:
			exprType=env.getTypeEntry(Environment.BOOLEAN);
			writeConditionalBool(registerKey,lhsResult.value,"JumpL", env);
			break;
		case LOR:
			exprType=env.getTypeEntry(Environment.BOOLEAN);
			env.writeInstruction("Compare",0, registerKey );
			
			//short circuiting... if $(registerKey)!=0 then LOR is true and equals $(registerKey) 
			env.writeCode("JumpFalse"+" "+cmprLabelKey);
			
			//evaluating the other expr
			lhsResult= expr.lhs.accept(this, env);
			
			//$(registerKey) =$(lhsResult) 
			env.writeInstruction("Move",lhsResult.value,registerKey);
			
			env.writeLabel(cmprLabelKey);
			break;
		case LAND:
			exprType=env.getTypeEntry(Environment.BOOLEAN);
			
			env.writeInstruction("Compare",0, registerKey );
			
			//short circuiting... if $(registerKey)==0 then LAND is false and equals $(registerKey)
			env.writeCode("JumpTrue"+" "+cmprLabelKey);
			
			//evaluating the other expr
			lhsResult= expr.lhs.accept(this, env);
			
			//$(registerKey) =$(lhsResult)
			env.writeInstruction("Move",lhsResult.value,registerKey);
			
			env.writeLabel(cmprLabelKey);
			break;
		case DIVIDE:
			exprType=env.getTypeEntry(Environment.INT);
			
			//check zero division

			env.writeCode("Library __checkZero("+lhsResult+"),"+IREnvironment.RDUMMY);

			//env.writeCode("Library __checkZero("+lhsResult+"),"+IREnvironment.RDUMMY);
			//TODO: implement
			env.writeInstruction("Move", rhsResult.value,registerKey);
			env.writeInstruction("Div", lhsResult.value,registerKey);
			
			break;
		case MINUS:
			exprType=env.getTypeEntry(Environment.INT);
			
			env.writeInstruction("Sub", lhsResult.value,registerKey);
			break;
		case MOD:
			exprType=env.getTypeEntry(Environment.INT);
			
			env.writeInstruction("Mod", lhsResult.value,registerKey);
			break;
		case MULTIPLY:
			exprType=env.getTypeEntry(Environment.INT);
			
			env.writeInstruction("Mul", lhsResult.value,registerKey);
			break;
		case PLUS:
			exprType=lhsResult.type;
			if(exprType.getEntryId()==env.getTypeEntry(Environment.INT).getEntryId()){
				
				env.writeInstruction("Add", lhsResult.value,registerKey);	
			}
			else{
				env.writeCode("Library __stringCat("+lhsResult.value+","+registerKey+"),"+registerKey );	
			}
			
			break;
		default:
			break;
		}
		
		return new IRVisitResult(exprType,registerKey);
	}


	private void writeConditionalBool(String Reg, Object otherVal,String cmprInstruction,IREnvironment env) {
		String cmprTrueLable=env.getLabelKey();
		String eventuallyLable= env.getLabelKey();
		env.writeInstruction("Compare", otherVal,Reg);
		env.writeCode(cmprInstruction+" "+cmprTrueLable);
		env.writeInstruction("Move", 0,Reg);
		env.writeCode("Jump"+" "+eventuallyLable);
		env.writeLabel(cmprTrueLable);
		env.writeInstruction("Move", 1,Reg);
		env.writeLabel(eventuallyLable);
		
	}

	 

}
