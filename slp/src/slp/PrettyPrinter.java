package slp;

/** Pretty-prints an SLP AST.
 */
public class PrettyPrinter implements Visitor {
	protected final ASTNode root;
	private int depth;
	private void tabify(){
		
		System.out.print(new String(new char[depth]).replace("\0", "	"));
	}
	
	
	/** Constructs a printin visitor from an AST.
	 * 
	 * @param root The root of the AST.
	 */
	public PrettyPrinter(ASTNode root) {
		this.root = root;
		depth=0;
	}

	/** Prints the AST with the given root.
	 */
	public void print() {
		root.accept(this);
	}

	public void visit(StmtList stmts) {
		if(stmts.line!=-1){
			tabify();
			System.out.println(stmts.line+": Block of statements");
			depth++;
		}
		
		for (Stmt s : stmts.statements) {
			s.accept(this);
			//System.out.println();
		}
		if(stmts.line!=-1){
			depth--;
		}
	}

	public void visit(Stmt stmt) {
		throw new UnsupportedOperationException("Unexpected visit of Stmt abstract class");
	}
	
	
	
	public void visit(AssignStmt stmt) {
		tabify();
		System.out.println(stmt);
		depth++;
		stmt.location.accept(this);
		stmt.rhs.accept(this);
		depth--;
		
	}
	
	public void visit(Expr expr) {
		throw new UnsupportedOperationException("Unexpected visit of Expr abstract class");
	}	
	
	
	
	public void visit(LocationExpr expr){
		expr.location.accept(this);
	}
	
	public void visit(VarExpr expr) {
		tabify();
		System.out.println(expr);
		
		if(expr.target_expr!=null){
			depth++;
			expr.target_expr.accept(this);
			depth--;
		}
	}
	
	public void visit(NumberExpr expr) {
		tabify();
		System.out.println(expr);
	}
	
	public void visit(UnaryOpExpr expr) {
		tabify();
		System.out.println(expr);
		depth++;
		expr.operand.accept(this);
		depth--;
	}
	
	public void visit(BinaryOpExpr expr) {
		tabify();
		System.out.println(expr);
		depth++;
		expr.rhs.accept(this);
		expr.lhs.accept(this);
		depth--;
	}

	
	public void visit(Program program) {
		program.classList.accept(this);
	}

	
	public void visit(ClassList classes) {
		for(Class clss: classes.classes){
			clss.accept(this);
		}
	}

	
	public void visit(Class clss) {
		tabify();
		System.out.println(  clss);
		depth++;
		clss.dclrList.accept(this);
		depth--;
		
	}

	
	public void visit(DclrList dclrList) {
		for(Dclr dclr: dclrList.declarations){
			dclr.accept(this);
		}
	}

	
	public void visit(Field field) {
		tabify();
		System.out.println(field);
		depth++;
		field.type.accept(this);
		depth--;
	}

	
	public void visit(ExtraIDs extraIDs) {
		/*do nothing*/
		
	}

	
	public void visit(Method method) {
		tabify();
		System.out.println(method);
		depth++;
		if(method.type==null){
			tabify();
			System.out.println(method.line+": Primitive data type: void");
		}
		else method.type.accept(this);
		method.formalsList.accept(this);
		method.stmtList.accept(this);
		depth--;
	}

	
	public void visit(FormalsList formalsList) {
		for( Formals formal: formalsList.formals){
			formal.accept(this);
		}
	}

	
	public void visit(Formals formal) {
		tabify();
		System.out.println(formal);
		depth++;
			formal.type.accept(this);
		depth--;
	}

	
	public void visit(Type type) {
		tabify();
		System.out.println(type);
	}

	
	public void visit(CallStmt callStmt) {
		tabify();
		System.out.println(callStmt);
		depth++;
		callStmt.call.accept(this);
		depth--;
		
	}

	
	public void visit(ReturnStmt returnStmt) {
		tabify();
		System.out.println(returnStmt);
		depth++;
		returnStmt.expr.accept(this);
		depth--;
	}

	
	public void visit(IfStmt ifStmt) {
		tabify();
		System.out.println(ifStmt);
		depth++;
		ifStmt.expr.accept(this);
		ifStmt.ifStmt.accept(this);
		if(ifStmt.elseStmt!=null) {
			ifStmt.elseStmt.accept(this);
		}
		depth--;
	}

	
	public void visit(WhileStmt whileStmt) {
		tabify();
		System.out.println(whileStmt);
		depth++;
		whileStmt.expr.accept(this);
		whileStmt.stmt.accept(this);
		depth--;
	}


	public void visit(BreakStmt breakStmt) {
		tabify();
		System.out.println(breakStmt);
	}

	
	public void visit(ContinueStmt continueStmt) {
		tabify();
		System.out.println(continueStmt);
		
	}

	
	public void visit(DeclarationStmt declarationStmt) {
		tabify();
		System.out.println(declarationStmt);
		depth++;
		declarationStmt.type.accept(this);
		
		if(declarationStmt.value!=null){
			declarationStmt.value.accept(this);
		}
		depth--;
	}

	
	public void visit(VirtualCall virtualCall) {
		tabify();
		System.out.println(virtualCall);
		depth++;
		if(virtualCall.expr!=null) virtualCall.expr.accept(this);
		virtualCall.callArgs.accept(this);
		depth--;
		
	}

	
	public void visit(StaticCall staticCall) {
		tabify();
		System.out.println(staticCall);
		depth++;
		staticCall.callArgs.accept(this);
		depth--;
	}

	
	public void visit(ExprList expressions) {
		for(Expr expr: expressions.expressions){
			expr.accept(this);
		}
		
	}

	
	public void visit(ArrayVarExpr expr) {
		tabify();
		System.out.println(expr);
		depth++;
		expr.target_expr.accept(this);
		expr.index_expr.accept(this);
		depth--;
	}

	
	public void visit(ArrayLenExpr expr) {
		tabify();
		System.out.println(expr);
		depth++;
		expr.expr.accept(this);
		depth--;
	}

	
	public void visit(ArrayAllocExpr expr) {
		tabify();
		System.out.println(expr);
		depth++;
		expr.type.accept(this);
		expr.expr.accept(this);
		depth--;
		
	}

	
	public void visit(StringExpr expr) {
		tabify();
		System.out.println(expr);
		
	}

	
	public void visit(BooleanExpr expr) {
		tabify();
		System.out.println(expr);
		
	}

	
	public void visit(NullExpr expr) {
		tabify();
		System.out.println(expr);
		
	}

	
	public void visit(InstantExpr expr) {
		tabify();
		System.out.println(expr);
	}
}