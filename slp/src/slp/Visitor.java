package slp;

/** An interface for AST visitors.
 */
public interface Visitor {
	
	public void visit(Program program);
	public void visit(ClassList classes);
	public void visit(Class clss);
	public void visit(DclrList dclrList);
	public void visit(Field field);
	public void visit(ExtraIDs extraIDs);
	public void visit(Method method);
	public void visit(FormalsList formalsList);
	public void visit(Formals formals);
	public void visit(Type type);
	
	public void visit(StmtList stmts);
	public void visit(Stmt stmt);
	
	public void visit(AssignStmt stmt);
	public void visit(CallStmt callStmt);
	public void visit(ReturnStmt returnStmt);
	public void visit(IfStmt ifStmt);
	public void visit(WhileStmt whileStmt);	
	public void visit(BreakStmt breakStmt);
	public void visit(ContinueStmt continueStmt);
	public void visit(DeclarationStmt declarationStmt);
	
	public void visit(VirtualCall virtualCall);
	public void visit(StaticCall staticCall);
	
	public void visit(ExprList expressions);
	public void visit(Expr expr);
	
	public void visit(VarExpr expr);
	public void visit(ArrayVarExpr expr);
	public void visit(ArrayLenExpr expr);
	public void visit(ArrayAllocExpr expr);
	public void visit(NumberExpr expr);
	public void visit(StringExpr expr);
	public void visit(BooleanExpr expr);
	public void visit(NullExpr expr);
	public void visit(UnaryOpExpr expr);
	public void visit(BinaryOpExpr expr);
	public void visit(LocationExpr expr);
	public void visit(InstantExpr expr);
}