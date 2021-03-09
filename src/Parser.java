// Kunal Patil
public class Parser {
    private Parser() {}

    public static prog getParseTree() {
        prog tree = new prog();
        tree.parse();
        return tree;
    }
}

// program <decl seq> begin <stmt seq> end
class prog {
    private decl_seq declSeq;
    private stmt_seq stmtSeq;

    public void parse() {
        Tokenizer.kwCheck("program");
        declSeq = new decl_seq();
        declSeq.parse();
        Tokenizer.kwCheck("begin");
        stmtSeq = new stmt_seq();
        stmtSeq.parse();
        Tokenizer.kwCheck("end");
        Tokenizer.kwCheck("EOF");
    }

    public decl_seq getDeclSeq() {return declSeq;}
    public stmt_seq getStmtSeq() {return stmtSeq;}
}

// <decl> | <decl> <decl seq>
class decl_seq {
    private int choice = 0;
    private decl decl;
    private decl_seq declSeq;

    public void parse() {
        decl = new decl();
        decl.parse();
        // if the next token is not begin another decl must be parsed
        if (!Tokenizer.getToken().equals("begin")) {
            choice = 1;
            declSeq = new decl_seq();
            declSeq.parse();
        }
    }

    public int getChoice() {return choice;}
    public decl getDecl() {return decl;}
    public decl_seq getDeclSeq() {return declSeq;}
}

// int <id list>;
class decl {
    private id_list idList;

    public void parse() {
        Tokenizer.kwCheck("int");
        idList = new id_list();
        idList.parse();
        Tokenizer.kwCheck(";");
    }

    public id_list getIdList() {return idList;}

}

// <id> | <id>, <id list>
class id_list {
    private int choice = 0;
    private String id;
    private id_list idList;

    public void parse() {
        id = Tokenizer.idName();
        // more variables to parse
        if(Tokenizer.getToken().equals(",")) {
            choice = 1;
            Tokenizer.skipToken();
            idList = new id_list();
            idList.parse();
        }
    }

    public int getChoice() {return choice;}
    public String getId() {return id;}
    public id_list getIdList() {return idList;}
}

// <stmt seq> = <stmt> | <stmt> <stmt seq>
class stmt_seq {
    private int choice = 0;
    private stmt stmt;
    private stmt_seq stmtSeq;

    public void parse() {
        stmt = new stmt();
        stmt.parse();
        String token = Tokenizer.getToken();
        // keep parsing unless at an end or else
        if (!token.equals("end") && !token.equals("else")) {
            choice = 1;
            stmtSeq = new stmt_seq();
            stmtSeq.parse();
        }
    }

    public int getChoice() { return choice; }
    public stmt getStmt() { return stmt; }
    public stmt_seq getStmtSeq() { return stmtSeq; }
}

// <stmt> = <assign> | <if> | <loop> | <in> | <out>
class stmt {
    private int choice;
    private assign asgnStmt;
    private if_stmt ifStmt;
    private loop loopStmt;
    private in inStmt;
    private out outStmt;

    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public void parse() {
        String token = Tokenizer.getToken();
        if (token.equals("if")) {
            choice = 1;
            ifStmt = new if_stmt();
            ifStmt.parse();
        } else if (token.equals("while")) {
            choice = 2;
            loopStmt = new loop();
            loopStmt.parse();
        } else if (token.equals("read")) {
            choice = 3;
            inStmt = new in();
            inStmt.parse();
        } else if (token.equals("write")) {
            choice = 4;
            outStmt = new out();
            outStmt.parse();
        } else if (LETTERS.contains(String.valueOf(token.charAt(0)))) {
            choice = 0;
            asgnStmt = new assign();
            asgnStmt.parse();
        } else {
            System.out.println("Error: Expected a statement but got: " + token);
            System.exit(2);
        }
        Tokenizer.kwCheck(";");
    }

    public int getChoice() {return choice;}
    public assign getAssign() {return asgnStmt;}
    public if_stmt getIf_stmt() {return ifStmt;}
    public loop getLoop() {return loopStmt;}
    public in getIn() {return inStmt;}
    public out getOut() {return outStmt;}
}

// <assign> = <id> = <exp>;
class assign {
    private exp expression;
    private String left;

    public void parse() {
        left = Tokenizer.idName();
        // "="
        Tokenizer.kwCheck("=");
        // <exp>
        expression = new exp();
        expression.parse();
    }

    public exp getExp() {return expression;}
    public String getLeft() {return left;}
}

// <exp> = <fac> | <fac> + <exp> | <fac> - <exp>
class exp {
    private int choice = 0;
    private fac factor;
    private exp expression;
    private String op;

    public void parse() {
        factor = new fac();
        factor.parse();
        String token = Tokenizer.getToken();
        if (token.equals("+")) {
            choice = 1;
            op = token;
            Tokenizer.skipToken();
            expression = new exp();
            expression.parse();
        } else if (token.equals("-")) {
            choice = 2;
            op = token;
            Tokenizer.skipToken();
            expression = new exp();
            expression.parse();
        }
    }

    public int getChoice() {return choice;}
    public fac getFac() {return factor;}
    public exp getExp() {return expression;}
    public String getOp() {return op;}
}

// <fac> = <op> | <op> * <fac>
class fac {
    private int choice = 0;
    private op oper;
    private fac factor;

    public void parse() {
        oper = new op();
        oper.parse();
        if (Tokenizer.getToken().equals("*")) {
            choice = 1;
            Tokenizer.skipToken();
            factor = new fac();
            factor.parse();
        }
    }

    public int getChoice() {return choice;}
    public op getOp() {return oper;}
    public fac getFac() {return factor;}
}

// <op> = <int> | <id> | (<exp>)
class op {
    private int choice;
    private int intVal;
    private String idName;
    private exp expression;

    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public void parse() {
        String token = Tokenizer.getToken();
        if (Character.isDigit(token.charAt(0))) {
            choice = 0;
            intVal = Integer.valueOf(Tokenizer.intVal());
        } else if (LETTERS.contains(String.valueOf(token.charAt(0)))) {
            choice = 1;
            idName = Tokenizer.idName();
        } else if (token.equals("(")) {
            choice = 2;
            Tokenizer.kwCheck("(");
            expression = new exp();
            expression.parse();
            Tokenizer.kwCheck(")");
        }
    }

    public int getChoice() {return choice;}
    public int getIntVal() {return intVal;}
    public String getIdName() {return idName;}
    public exp getExp() {return expression;}
}

// <if> = if <cond> then <stmt seq> end;
// | if <cond> then <stmt seq> else <stmt seq> end;
class if_stmt {
    private int choice = 0;
    private cond condition;
    private stmt_seq stmtSeq;
    private stmt_seq stmtSeqElse;

    public void parse() {
        Tokenizer.kwCheck("if");
        condition = new cond();
        condition.parse();
        Tokenizer.kwCheck("then");
        stmtSeq = new stmt_seq();
        stmtSeq.parse();
        // check for else token
        String token = Tokenizer.getToken();
        if (token.equals("else")) {
            choice = 1;
            Tokenizer.skipToken();
            stmtSeqElse = new stmt_seq();
            stmtSeqElse.parse();
        }
        Tokenizer.kwCheck("end");
    }

    public int getChoice() {return choice;}
    public cond getCond() {return condition;}
    public stmt_seq getStmtSeq() {return stmtSeq;}
    public stmt_seq getStmtSeqElse() {return stmtSeqElse;}
}

// <loop> = while <cond> loop <stmt seq> end;
class loop {
    private cond condition;
    private stmt_seq stmtSeq;

    public void parse() {
        Tokenizer.kwCheck("while");
        condition = new cond();
        condition.parse();
        Tokenizer.kwCheck("loop");
        stmtSeq = new stmt_seq();
        stmtSeq.parse();
        Tokenizer.kwCheck("end");
    }

    public cond getCond() {return condition;}
    public stmt_seq getStmtSeq() {return stmtSeq;}
}

// <in> = read <id list>
class in {
    private id_list idList;

    public void parse() {
        Tokenizer.kwCheck("read");
        idList = new id_list();
        idList.parse();
    }

    public id_list getIdList() {return idList;}
}

// <out> = write <id list>
class out {
    private id_list idList;

    public void parse() {
        Tokenizer.kwCheck("write");
        idList = new id_list();
        idList.parse();
    }

    public id_list getIdList() {return idList;}
}

// <cond> = <comp> | !<cond> | [<cond> && <cond>] | [<cond> || <cond>]
class cond {
    private int choice;
    private comp compare; // choice = 0
    private cond notCond; // choice = 1
    private cond firstCond; //  choice = 2 or 3
    private cond secondCond; // choice = 2 or 3
    private String op; // && or ||

    public void parse() {
        String token = Tokenizer.getToken();
        if (token.equals("!")) {
            choice = 1;
            Tokenizer.skipToken();
            notCond = new cond();
            notCond.parse();
        } else if (token.equals("[")) {
            // skip bracket
            Tokenizer.skipToken();
            firstCond = new cond();
            firstCond.parse();
            token = Tokenizer.getToken();
            // choice 2 or 3
            if (token.equals("&&")) {
                choice = 2;
                op = token;
                Tokenizer.skipToken();
            } else if (token.equals("||")) {
                choice = 3;
                op = token;
                Tokenizer.skipToken();
            }
            secondCond = new cond();
            secondCond.parse();
            Tokenizer.kwCheck("]");
        } else {
            choice = 0;
            compare = new comp();
            compare.parse();
        }
    }

    public int getChoice() {return choice;}
    public comp getComp() {return compare;}
    public cond getNotCond() {return notCond;}
    public cond firstCond() {return firstCond;}
    public cond secondCond() {return secondCond;}
    public String getOp() {return op;}
}

// <comp> = (<op> <comp op> <op>)
class comp {
    private op op1; // <op>
    private comp_op compOp; // <comp op>
    private op op2; // <op>

    public void parse() {
        Tokenizer.kwCheck("(");
        op1 = new op();
        op1.parse();
        compOp = new comp_op();
        compOp.parse();
        op2 = new op();
        op2.parse();
        Tokenizer.kwCheck(")");
    }

    public op getOp1() {return op1;}
    public comp_op getCompOp() {return compOp;}
    public op getOp2() {return op2;}
}

// <comp op> = != | == | < | > | <= | >=
class comp_op {
    private String op;

    public void parse() {
        String token = Tokenizer.getToken();
        if (token.equals("==") || token.equals("<") || token.equals(">") || token.equals("<=") || token.equals(">=") || token.equals("!=")) {
            op = token;
        } else {
            System.out.println("Error: Token should be a comparison operator. Token: " + token);
            System.exit(2);
        }
        Tokenizer.skipToken();
    }

    public String getOp() {return op;}
}
