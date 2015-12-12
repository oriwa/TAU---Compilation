package slp;

/** An interface for a propagating AST visitor.
 * The visitor passes down objects of type <code>DownType</code>
 * and propagates up objects of type <code>UpType</code>.
 */
public interface PropagatingVisitor<DownType,UpType> {
	
	public UpType visit(Program program, DownType d);
	public UpType visit(ClassList classes, DownType d);
	public UpType visit(Class clss, DownType d);
	public UpType visit(DclrList list, DownType d);
	public UpType visit(Field field, DownType d);
	public UpType visit(ExtraIDs extraIDs, DownType d);
	public UpType visit(Method method, DownType d);
	public UpType visit(FormalsList formalsList, DownType d);
	public UpType visit(Formals formals, DownType d);
	public UpType visit(Type type, DownType d);
	
	//stmt
	public UpType visit(StmtList stmts, DownType d);
	public UpType visit(Stmt stmt, DownType d);
	
	public UpType visit(AssignStmt stmt, DownType d);
	public UpType visit(CallStmt callStmt, DownType d);
	public UpType visit(ReturnStmt returnStmt, DownType d);
	public UpType visit(IfStmt ifStmt, DownType d);
	public UpType visit(WhileStmt whileStmt, DownType d);
	public UpType visit(BreakStmt breakStmt, DownType d);
	public UpType visit(ContinueStmt continueStmt, DownType d);
	public UpType visit(DeclarationStmt declarationStmt, DownType d);
	
	public UpType visit(VirtualCall virtualCall, DownType d);
	public UpType visit(StaticCall staticCall, DownType d);
	
	//expr
	public UpType visit(ExprList expressions, DownType d);
	public UpType visit(Expr expr, DownType d);
	
	public UpType visit(InstantExpr expr, DownType d);	
	public UpType visit(VarExpr expr, DownType d);
	public UpType visit(ArrayVarExpr expr, DownType d);
	public UpType visit(ArrayLenExpr expr, DownType d);
	public UpType visit(ArrayAllocExpr expr, DownType d);
	public UpType visit(NumberExpr expr, DownType d);
	public UpType visit(StringExpr expr, DownType d);
	public UpType visit(BooleanExpr expr, DownType d);
	public UpType visit(NullExpr expr, DownType d);
	public UpType visit(UnaryOpExpr expr, DownType d);
	public UpType visit(BinaryOpExpr expr, DownType d);
}