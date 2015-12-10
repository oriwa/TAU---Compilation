package slp;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Evaluates straight line programs.
 */
public class SLPEvaluator implements PropagatingVisitor<Environment, Integer> {
	
	

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
	
	
	
	
	public Integer visit(StmtList stmts, Environment env) {
		for (Stmt st : stmts.statements) {
			st.accept(this, env);
		}
		return null;
	}

	public Integer visit(Stmt stmt, Environment env) {
		throw new UnsupportedOperationException("Unexpected visit of Stmt!");
	}



	public Integer visit(AssignStmt stmt, Environment env) {
		//Expr rhs = stmt.rhs;
		//Integer expressionValue = rhs.accept(this, env);
		//AssignStmt var = stmt.location;
		//env.update(var, expressionValue);
		return null;
	}

	public Integer visit(Expr expr, Environment env) {
		throw new UnsupportedOperationException("Unexpected visit of Expr!");
	}



	public Integer visit(VarExpr expr, Environment env) {
		//return env.get(expr);
		return null;
	}

	public Integer visit(NumberExpr expr, Environment env) {
		return new Integer(expr.value);		
		// return expr.value; also works in Java 1.5 because of auto-boxing
	}

	public Integer visit(UnaryOpExpr expr, Environment env) {
		Integer value = expr.operand.accept(this, env);
		int result = 0;
		switch (expr.op) {
			case MINUS:
				result = new Integer(- value.intValue());
				break;
			case LNEG:
				result = 1 - result;
				break;
			default:
				env.handleSemanticError("Encountered unexpected operator " + expr.op, expr.line);
		}
		return result;
	}

	public Integer visit(BinaryOpExpr expr, Environment env) {
		Integer lhsValue = expr.lhs.accept(this, env);
		int lhsInt = lhsValue.intValue();
		Integer rhsValue = expr.rhs.accept(this, env);
		int rhsInt = rhsValue.intValue();
		int result = 0;
		switch (expr.op) {
		case DIVIDE:
			if (rhsInt == 0)
				env.handleSemanticError("Attempt to divide by zero: " + expr, expr.line);
			result = lhsInt / rhsInt;
			break;
		case MINUS:
			result = lhsInt - rhsInt;
			break;
		case MULTIPLY:
			result = lhsInt * rhsInt;
			break;
		case PLUS:
			result = lhsInt + rhsInt;
			break;
		case LT:
			result = lhsInt < rhsInt ? 1 : 0;
			break;
		case GT:
			result = lhsInt > rhsInt ? 1 : 0;
			break;
		case LTE:
			result = lhsInt <= rhsInt ? 1 : 0;
			break;
		case GTE:
			result = lhsInt >= rhsInt ? 1 : 0;
			break;
		case LAND:
			result = (lhsInt!=0 && rhsInt!=0) ? 1 : 0;
			break;
		case LOR:
			result = (lhsInt!=0 || rhsInt!=0) ? 1 : 0;
			break;
		default:
			env.handleSemanticError("Encountered unexpected operator type: " + expr.op, expr.line);
		}
		return new Integer(result);
	}

	public Integer visit(Program program, Environment env)  {
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
			if(clss.extends_name!=null)
			{
				TypeEntry extendsTypeEntry= env.getTypeEntry(clss.extends_name);
				if(extendsTypeEntry!=null)
				{
					Class extendsClass=extendsTypeEntry.getEntryClass();
					if(!extendsClass.isSealed)
						clss.extends_class=extendsClass;
					env.handleSemanticError("can not extend form class" + clss.extends_name,clss.line);
				}
				else
				{					
					env.handleSemanticError("class \""+clss.extends_class.name +"\" is undefined, classes can only extend previously defined classes",clss.line);
				}
			}
			TypeEntry previousDef=env.getTypeEntry(clss.name);
			if(previousDef!=null){
				String errorMsg="ERROR: multiple definitions of class \""+clss.name+"\"";
				String note="note: first defined in line: "+previousDef.getEntryClass().line;
				env.handleSemanticError(errorMsg+"\n"+note,clss.line);
			}
			env.addTypeEntry(clss);
		}		
	}
	

	private void initLibrary(Environment env) {
		LibraryLoader loader=new LibraryLoader();
		loader.load(env);	
	}

	public Integer visit(ClassList classes, Environment env) {
		for (Class clss : classes.classes) {
			clss.accept(this, env);
		}
		return null;
	}

	public Integer visit(Class clss, Environment env)  {
		env.setCurrentClass(clss);
		env.enterScope();
		
		//check duplicate methods
		Set<String> tempSet = new HashSet<>();
		for (Method method : clss.dclrList.methods){
			if (!tempSet.add(method.name)) // set.add returns false if already exists
				env.handleSemanticError("Duplicate method " + method.name + " in type " + clss.name,clss.line);
			
		}
		
		//check duplicate fields and add to environment
		tempSet = new HashSet<>();
		for (Field field : clss.dclrList.fields){
			if (!tempSet.add(field.name)) // set.add returns false if already exists
				env.handleSemanticError("Duplicate field " + clss.name + "." + field.name,clss.line);
			
			env.addToEnv(field);
		}
		
		clss.dclrList.accept(this, env);
		env.leaveScope();
		env.setCurrentClass(null);
		
		return null;
	}

	public Integer visit(DclrList list, Environment env) {
		for (Field field : list.fields) {
			field.accept(this, env);
		}
		for (Method method : list.methods) {
			method.accept(this, env);
		}
		for (Dclr decleration : list.declarations) {
			decleration.accept(this, env);
		}

		return null;
	}
	

	public Integer visit(Field field, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(ExtraIDs extraIDs, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(Method method, Environment env) {
		
		//TODO Add method id validation
		
		
		if(IsMainMethod(method))
			env.addMainMethodNumber();
		
		env.setCurrentMethod(method);
		method.formalsList.accept(this, env);
		method.stmtList.accept(this, env);
		env.setCurrentMethod(null);
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

	public Integer visit(FormalsList formalsList, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(Formals formals, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(Type type, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(CallStmt callStmt, Environment env) {
		callStmt.call.accept(this, env);
		return null;
	}

	public Integer visit(ReturnStmt returnStmt, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(IfStmt ifStmt, Environment env) {
		Integer exprTypeId= ifStmt.expr.accept(this,env);
		env.validateTypeMismatch(Environment.BOOLEAN,exprTypeId,ifStmt.line);
		ifStmt.ifStmt.accept(this,env);
		if(ifStmt.elseStmt!=null)
			ifStmt.elseStmt.accept(this,env);
		return null;
	}

	public Integer visit(WhileStmt whileStmt, Environment env) {
		Integer exprTypeId= whileStmt.expr.accept(this,env);
		env.validateTypeMismatch(Environment.BOOLEAN,exprTypeId,whileStmt.line);
		env.setIsInLoop(true);
		whileStmt.stmt.accept(this,env);
		env.setIsInLoop(false);
		return null;
	}

	public Integer visit(BreakStmt breakStmt, Environment env) {
		if(!env.getIsInLoop())
			env.handleSemanticError("break cannot be used outside of a while", breakStmt.line);
		return null;
	}

	public Integer visit(ContinueStmt continueStmt, Environment env) {
		if(!env.getIsInLoop())
			env.handleSemanticError("continue cannot be used outside of a while", continueStmt.line);
		return null;
	}

	public Integer visit(DeclarationStmt declarationStmt, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(VirtualCall virtualCall, Environment env) {
		int exprTypeId = virtualCall.expr.accept(this, env);
		TypeEntry exprType = env.getTypeEntry(exprTypeId);
		
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

		String returnTypeName = m.type.name;
		return env.getTypeEntry(returnTypeName).getEntryId();
		
	}

	public Integer visit(StaticCall staticCall, Environment env) {
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

		String returnTypeName = m.type.name;
		return env.getTypeEntry(returnTypeName).getEntryId();
	}

	public boolean verifyMethodCall(List<Formals> formals, List<Expr> callArgs, Environment env){
		if (formals.size() != callArgs.size())
			return false;
		
		for (int i = 0; i < formals.size(); i++){
			int exprTypeId = callArgs.get(i).accept(this, env);
			TypeEntry exprType = env.getTypeEntry(exprTypeId);
			
			String formalTypeName = formals.get(0).type.name;
			TypeEntry formalType = env.getTypeEntry(formalTypeName);
			
			if (!exprType.equals(formalType))
				return false;
		}
		
		return true;
	}
	public Integer visit(ExprList expressions, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(ArrayVarExpr expr, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(ArrayLenExpr expr, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(ArrayAllocExpr expr, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(StringExpr expr, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(BooleanExpr expr, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(NullExpr expr, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}


}