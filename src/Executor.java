// Kunal Patil
import java.io.*;
import java.util.*;

public class Executor {
    private Executor() {}
    
    private static HashMap<String, Integer> variables = new HashMap<String, Integer>();
    private static List<Integer> inputData = new LinkedList<Integer>();

    public static void execProg(prog parseTree, String dataFileName) {
        // read in lines of file
        List<String> line_list = readData(dataFileName);
        // store data from lines in inputData linkedlist
        storeData(line_list);
        // execute program
        ex_prog(parseTree);
    }

    private static List readData(String fileName) {
        BufferedReader reader = null;
        List<String> line_list = new LinkedList<String>();
        try {
            reader = new BufferedReader(new FileReader(new File(fileName)));
            String line;
            while ((line = reader.readLine()) != null) line_list.add(line);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException ignored) {}
        }
        return line_list;
    }

    private static void storeData(List<String> line_list) {
        // for each line from the input program lines
        for (String line : line_list) {  
            // variable to keep track of start of token string  
            int token_start_index = 0;
            int line_length = line.length();
            // collect tokens from each line until end of line
            while (token_start_index < line_length) { 
                // variable to be added
                String token = new String();
                // variable to determine type of token (special/digit/etc)
                char start_char = line.charAt(token_start_index);   
                // use with start index to build token from a substring of line
                int token_end_index = token_start_index + 1;
                // token is a number
                if (Character.isDigit(start_char) || start_char == '-') {
                    // add 1 to token_end_index for every consecutive digit
                    while (token_end_index < line_length && Character.isDigit(line.charAt(token_end_index))) token_end_index++;
                    // get token using substring method. start index inclusive, end index exclusive
                    token = line.substring(token_start_index, token_end_index);
                }
                token_start_index = token_end_index;
                if (token.length() > 0) inputData.add(Integer.parseInt(token));
            }
        }
    }
    
    private static void ex_prog(prog program) {
        ex_decl_seq(program.getDeclSeq());
        ex_stmt_seq(program.getStmtSeq());
    }

    private static void ex_decl_seq(decl_seq declSeq) {
        ex_decl(declSeq.getDecl());
        if (declSeq.getChoice() == 1) {
            ex_decl_seq(declSeq.getDeclSeq());
        }
    }

    private static void ex_decl(decl decl) {
        ex_id_list(decl.getIdList());
    }

    private static void ex_id_list(id_list idList) {
        String variable = idList.getId();
        if (!variables.containsKey(variable)) {
            variables.put(variable, null);
        } else {
            System.out.println("Error: variable " + variable + " is already instantiated");
            System.exit(2);
        }

        if (idList.getChoice() == 1) ex_id_list(idList.getIdList());
    }

    private static void ex_stmt_seq(stmt_seq stmtSeq) {
        ex_stmt(stmtSeq.getStmt());
        if (stmtSeq.getChoice() == 1) {
            ex_stmt_seq(stmtSeq.getStmtSeq());
        }
    }

    private static void ex_stmt(stmt stmt) {
        int choice = stmt.getChoice();
        if (choice == 0) ex_assign(stmt.getAssign());
        else if (choice == 1) ex_if(stmt.getIf_stmt());
        else if (choice == 2) ex_loop(stmt.getLoop());
        else if (choice == 3) ex_in(stmt.getIn());
        else if (choice == 4) ex_out(stmt.getOut());
    }

    private static void ex_assign(assign asgnStmt) {
        String left = asgnStmt.getLeft();
        if (variables.containsKey(left)) {
            variables.put(left, ex_exp(asgnStmt.getExp()));
        } else {
            System.out.println("Error: variable " + left + " must be declared");
            System.exit(2);
        }
    }

    private static int ex_exp(exp expression) {
        int value = ex_fac(expression.getFac());
        if (expression.getChoice() == 1) value += ex_exp(expression.getExp());
        else if (expression.getChoice() == 2) value -= ex_exp(expression.getExp());
        return value;
    }

    private static int ex_fac(fac factor) {
        int value = ex_op(factor.getOp());
        if (factor.getChoice() == 1) value *= ex_fac(factor.getFac());
        return value;
    }

    private static int ex_op(op oper) {
        int value = 0;
        // <int>
        if (oper.getChoice() == 0) value = oper.getIntVal();
        // <id>
        else if (oper.getChoice() == 1) value = convertHelper(oper.getIdName());
        // (<exp>)
        else if (oper.getChoice() == 2) value = ex_exp(oper.getExp());
        return value;
    }

    // helper method to output the int value of a variable string
    private static int convertHelper(String idName) {
        int value = 0;
        if (variables.containsKey(idName)) {
            if (variables.get(idName) != null) value = variables.get(idName);
            else {
                System.out.println("Error: variable " + idName + " must be instantiated");
                System.exit(2);
            }
        } else {
            System.out.println("Error: variables " + idName + " must be declared");
            System.exit(2);
        }
        return value;
    }

    private static void ex_if(if_stmt ifStmt) {
        if (ex_cond(ifStmt.getCond())) {
            ex_stmt_seq(ifStmt.getStmtSeq());
        } else if (ifStmt.getChoice() == 1) {
            ex_stmt_seq(ifStmt.getStmtSeqElse());
        }
    }

    private static void ex_loop(loop loopStmt) {
        while (ex_cond(loopStmt.getCond())) {
            ex_stmt_seq(loopStmt.getStmtSeq());
        }
    }

    private static void ex_in(in inStmt) {
        id_list idList = inStmt.getIdList();
        readHelper(idList);
        while (idList.getChoice() == 1) {
            idList = idList.getIdList();
            readHelper(idList);
        }
    }

    // read helper method will check if variable is declared and then assign it the value of first input token
    private static void readHelper(id_list idList) {
        if (inputData.size() > 0) {
            if (variables.containsKey(idList.getId())) variables.put(idList.getId(), inputData.remove(0));
            else {
                System.out.println("Error: variable " + idList.getId() + " must be declared");
                System.exit(2);
            }
        } else {
            System.exit(2);
        }
    }

    private static void ex_out(out outStmt) {
        id_list idList = outStmt.getIdList();
        writeHelper(idList);
        while (idList.getChoice() == 1) {
            idList = idList.getIdList();
            writeHelper(idList);
        }
    }

    // write helper method will check if variable is declared and then assign it the value of first input token
    private static void writeHelper(id_list idList) {
        if (variables.containsKey(idList.getId())) {
            if (variables.get(idList.getId()) != null) System.out.println(idList.getId() + " = " + variables.get(idList.getId()));
            else {
                System.out.println("Error: variable " + idList.getId() + " must be instantiated");
                System.exit(2);
            }
        } else {
            System.out.println("Error: variable " + idList.getId() + " must be declared");
            System.exit(2);
        }
    }

    private static boolean ex_cond(cond condition) {
        boolean result = true;
        int choice = condition.getChoice();

        // <comp>
        if (choice == 0) result = ex_comp(condition.getComp());
        // !<cond>
        else if (choice == 1) result = !ex_cond(condition.getNotCond());
        // [<cond> && <cond>]
        else if (choice == 2) result = (ex_cond(condition.firstCond()) && ex_cond(condition.secondCond()));
        // [<cond> || <cond>]
        else if (choice == 3) result = (ex_cond(condition.firstCond()) || ex_cond(condition.secondCond()));
        return result;
    }

    private static boolean ex_comp(comp compare) {
        boolean result = true;
        comp_op compOp = compare.getCompOp();
        String comp_op = compOp.getOp();
        if (comp_op.equals("==")) result = (ex_op(compare.getOp1()) == ex_op(compare.getOp2()));
        else if (comp_op.equals("!=")) result = (ex_op(compare.getOp1()) != ex_op(compare.getOp2()));
        else if (comp_op.equals("<")) result = (ex_op(compare.getOp1()) < ex_op(compare.getOp2()));
        else if (comp_op.equals("<=")) result = (ex_op(compare.getOp1()) <= ex_op(compare.getOp2()));
        else if (comp_op.equals(">")) result = (ex_op(compare.getOp1()) > ex_op(compare.getOp2()));
        else if (comp_op.equals(">=")) result = (ex_op(compare.getOp1()) >= ex_op(compare.getOp2()));
        return result;
    }
}
