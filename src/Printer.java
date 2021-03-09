// Kunal Patil
public class Printer {
    private Printer() {}

    public static void printProg(prog parseTree) {
        System.out.println("program");
        print_decl_seq(parseTree.getDeclSeq());
        System.out.println("begin");
        print_stmt_seq(parseTree.getStmtSeq(), 1);
        System.out.println("end");
    }

    private static void print_decl_seq(decl_seq declSeq) {
        System.out.print(" ");
        print_decl(declSeq.getDecl());
        if (declSeq.getChoice() == 1) {
            print_decl_seq(declSeq.getDeclSeq());
        }
    }

    private static void print_decl(decl decl) {
        System.out.print("int ");
        print_idList(decl.getIdList());
        System.out.println(";");
    }

    private static void print_idList(id_list idList) {
        System.out.print(idList.getId());
        if (idList.getChoice() == 1) {
            System.out.print(", ");
            print_idList(idList.getIdList());
        }
    }

    private static void print_stmt_seq(stmt_seq stmtSeq, int spaces) {
        add_spaces(spaces);
        print_stmt(stmtSeq.getStmt(), spaces);
        if (stmtSeq.getChoice() == 1) {
            print_stmt_seq(stmtSeq.getStmtSeq(), spaces);
        }
    }

    private static void print_stmt(stmt stmt, int spaces) {
        if (stmt.getChoice() == 0) { print_assign(stmt.getAssign()); }
        else if (stmt.getChoice() == 1) { print_if(stmt.getIf_stmt(), spaces); }
        else if (stmt.getChoice() == 2) { print_loop(stmt.getLoop(), spaces); }
        else if (stmt.getChoice() == 3) { print_in(stmt.getIn()); }
        else if (stmt.getChoice() == 4) { print_out(stmt.getOut()); }
        System.out.println(";");
    }

    private static void print_assign(assign asgnStmt) {
        System.out.print(asgnStmt.getLeft() + " = ");
        print_exp(asgnStmt.getExp());
    }

    private static void print_if(if_stmt ifStmt, int spaces) {
        System.out.print("if ");
        print_cond(ifStmt.getCond());
        System.out.println(" then");
        print_stmt_seq(ifStmt.getStmtSeq(), spaces + 1);
        if (ifStmt.getChoice() == 1) {
            add_spaces(spaces);
            System.out.println("else");
            print_stmt_seq(ifStmt.getStmtSeqElse(), spaces + 1);
        }
        add_spaces(spaces);
        System.out.print("end");
    }

    private static void print_loop(loop loopStmt, int spaces) {
        System.out.print("while ");
        print_cond(loopStmt.getCond());
        System.out.println(" loop");
        print_stmt_seq(loopStmt.getStmtSeq(), spaces + 1);
        add_spaces(spaces);
        System.out.print("end");
    }

    private static void print_in(in inStmt) {
        System.out.print("read ");
        print_idList(inStmt.getIdList());
    }

    private static void print_out(out outStmt) {
        System.out.print("write ");
        print_idList(outStmt.getIdList());
    }

    private static void print_cond(cond condition) {
        if (condition.getChoice() == 0) {
            print_comp(condition.getComp());
        } else if (condition.getChoice() == 1) {
            System.out.print("!");
            print_cond(condition.getNotCond());
        } else if (condition.getChoice() == 2) {
            System.out.print("[");
            print_cond(condition.firstCond());
            System.out.print(" && ");
            print_cond(condition.secondCond());
            System.out.print("]");
        } else if (condition.getChoice() == 3) {
            System.out.print("[");
            print_cond(condition.firstCond());
            System.out.print(" || ");
            print_cond(condition.secondCond());
            System.out.print("]");
        }
    }

    private static void print_comp(comp comparison) {
        System.out.print("(");
        print_op(comparison.getOp1());
        print_comp_op(comparison.getCompOp());
        print_op(comparison.getOp2());
        System.out.print(")");
    }
    
    private static void print_op(op operand) {
        if (operand.getChoice() == 0) { System.out.print(operand.getIntVal()); }
        else if (operand.getChoice() == 1) { System.out.print(operand.getIdName()); }
        else if (operand.getChoice() == 2) {
            System.out.print("(");
            print_exp(operand.getExp());
            System.out.print("(");
        }
    }

    private static void print_comp_op(comp_op compOp) {
        if (compOp.getOp().equals("==") || compOp.getOp().equals(">") || compOp.getOp().equals("<") || compOp.getOp().equals("<=") || compOp.getOp().equals(">=") || compOp.getOp().equals("!=")) {
            System.out.print(compOp.getOp());
        }
    }

    private static void print_exp(exp expression) {
        print_fac(expression.getFac());
        if (expression.getChoice() == 1) { 
            System.out.print(" + ");
            print_exp(expression.getExp());
        }
        else if (expression.getChoice() == 2) {
            System.out.print(" - ");
            print_exp(expression.getExp());
        }
    }

    private static void print_fac(fac factor) {
        print_op(factor.getOp());
        if (factor.getChoice() == 1) {
            System.out.print("*");
            print_fac(factor.getFac());
        }
    }

    private static void add_spaces(int spaces) {for (int i = 0; i < spaces; i++) System.out.print(" ");}
}
