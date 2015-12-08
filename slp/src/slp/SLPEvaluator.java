package slp;

import java.io.IOException;

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
		Operator op = expr.op;
		if (op != Operator.MINUS)
			throw new RuntimeException("Encountered unexpected operator " + op);
		Integer value = expr.operand.accept(this, env);
		return new Integer(- value.intValue());
	}

	public Integer visit(BinaryOpExpr expr, Environment env) {
		Integer lhsValue = expr.lhs.accept(this, env);
		int lhsInt = lhsValue.intValue();
		Integer rhsValue = expr.rhs.accept(this, env);
		int rhsInt = rhsValue.intValue();
		int result;
		switch (expr.op) {
		case DIVIDE:
			if (rhsInt == 0)
				throw new RuntimeException("Attempt to divide by zero: " + expr);
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
			throw new RuntimeException("Encountered unexpected operator type: " + expr.op);
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

	public Integer visit(Class clss, Environment env) {
		env.setCurrentClass(clss);
		clss.dclrList.accept(this, env);
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

	public Integer visit(CallStmt callStmt, Environment d) {
		// TODO Auto-generated method stub
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

	public Integer visit(VirtualCall virtualCall, Environment d) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer visit(StaticCall staticCall, Environment d) {
		// TODO Auto-generated method stub
		return null;
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