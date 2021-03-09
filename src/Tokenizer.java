// Kunal Patil
import java.io.*;
import java.util.List;
import java.util.LinkedList;

// tokenizer class for core interpreter
public class Tokenizer{

    private Tokenizer() {}
    
    // string of special symbol and whitespace characters
    private static final String SPECIAL_SINGLE = ";,=![]()+-*<>";
    private static final String LOGICAL_AND = "&&";
    private static final String LOGICAL_OR = "||";
    private static final String WHITESPACE = "\n\t\r ";
    private static final String ERROR_STRING = "._&^%$#@~";

    // token_list will contain tokens before they are tokenized "program, begin, end"
    private static List<String> token_list = new LinkedList<String>();
    // token_list_post will contain tokens after they are tokenized "1, 2, 3"
    private static List<Integer> token_list_post = new LinkedList<Integer>();
    private static int token_index = 0;

    // returns info about current token. repeated calls return same token
    public static String getToken() {
        return token_list.get(token_index); 
    }

    // skips current token. next token becomes current. returns new token
    public static void skipToken() {
        token_index++;
        //return token_list.get(token_index);
    }

    // returns the value of the current (integer) token or error if current not integer
    public static String intVal(){
        if (token_list_post.get(token_index) == 31) { 
            int original_token_index = token_index;
            token_index++;
            return token_list.get(original_token_index);
        } 
        else { return "Error: Current token is not an integer"; }
    }

    // returns the name (string) of current (id) token or error if current not id
    public static String idName(){
        if (token_list_post.get(token_index) == 32) { 
            int original_token_index = token_index;
            token_index++;
            return token_list.get(original_token_index);
        }
        else { return "Error: Current token is not an id"; }
    }

    /* tokenCheck method opens input file to prepare list of lines to be tokenized. 
    * makes call to tokenizer if successful or outputs error */
    public static void tokenCheck (String file) {
        List<String> line_list = new LinkedList<String>();
        BufferedReader reader = null;
        try{
            // read in each line from the file and add to linked list of lines
            reader = new BufferedReader(new FileReader(new File(file)));
            String line;
            while ((line = reader.readLine()) != null) line_list.add(line);
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException ignored) {}
        }
        // no error reading file; line_list to be tokenized
        tokenize(line_list);
    }

    /* tokenize takes in a list of each line from the file and updates token_list
    * to include all legal tokens */
    private static void tokenize (List<String> line_list) {
        
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
                
                // token is a digit or digit sequence: 31
                if (Character.isDigit(start_char)) {
                    // add 1 to token_end_index for every consecutive digit
                    while (token_end_index < line_length && Character.isDigit(line.charAt(token_end_index))) token_end_index++;
                    // get token using substring method. start index inclusive, end index exclusive
                    token = line.substring(token_start_index, token_end_index);
                //    System.out.println(31);
                    token_list_post.add(31);

                // token is a special symbol (not && or ||): 12 - 30
                } else if ((token_end_index < line_length) && (SPECIAL_SINGLE.contains(String.valueOf(start_char)))) {
                    // token is a single character special
                    if (line.charAt(token_end_index) != '=') token = line.substring(token_start_index, token_end_index);
                    // token is a double character special
                    else if (line.charAt(token_end_index) == '=') token = line.substring(token_start_index, ++token_end_index);
                    printTokens(token);
                }

                // token is "&&": 18
                else if ((token_end_index < line_length) && (line.substring(token_start_index, token_end_index + 1) == LOGICAL_AND)) {
                    token = line.substring(token_start_index, ++token_end_index);
                //    System.out.println(18);
                    token_list_post.add(18);
                }
                // token is "||": 19
                else if ((token_end_index < line_length) && (line.substring(token_start_index, token_end_index + 1) == LOGICAL_OR)) {
                    token = line.substring(token_start_index, ++token_end_index);
                //    System.out.println(19);
                    token_list_post.add(19);
                }

                // token begins with a letter: 32
                else if (!WHITESPACE.contains(String.valueOf(start_char))) {
                    // add 1 to token_end_index for every consecutive letter or digit
                    while (token_end_index < line_length && !WHITESPACE.contains(String.valueOf(line.charAt(token_end_index))) && !SPECIAL_SINGLE.contains(String.valueOf(line.charAt(token_end_index))) && !LOGICAL_AND.contains(String.valueOf(line.charAt(token_end_index))) && !LOGICAL_OR.contains(String.valueOf(line.charAt(token_end_index)))) token_end_index++;
                    token = line.substring(token_start_index, token_end_index);
                    printTokens(token);
                }

                for (int i = 0; i < token.length(); i++) {
                    if (ERROR_STRING.contains(String.valueOf((token.charAt(i))))) {
                        System.out.println("Error character(s) in token " + token);
                        System.exit(2);
                    }
                }

                // add token to list and set starting index of next token
                if (!WHITESPACE.contains(token)) { token_list.add(token); }
                token_start_index = token_end_index;
            }
        }
        token_list.add("EOF");
        token_list_post.add(33);
    //    System.out.println(33);
    }

    /* printTokens helper method prints tokens to console line by line.
    * called when token is an identifier to check if string is a reserved word
    * also used to check two-character special symbols */
	public static void printTokens(String token) {
        switch (token) {
            case "program": //System.out.println(1);
                token_list_post.add(1);
                break;
            case "begin": //System.out.println(2);
                token_list_post.add(2);
                break;
            case "end": //System.out.println(3);
                token_list_post.add(3);
                break;
            case "int": //System.out.println(4);
                token_list_post.add(4);
                break;
            case "if": //System.out.println(5);
                token_list_post.add(5);
                break;
            case "then": //System.out.println(6);
                token_list_post.add(6);
                break;
            case "else": //System.out.println(7);
                token_list_post.add(7);
                break;
            case "while": //System.out.println(8);
                token_list_post.add(8);
                break;
            case "loop": //System.out.println(9);
                token_list_post.add(9);
                break;
            case "read": //System.out.println(10);
                token_list_post.add(10);
                break;
            case "write": //System.out.println(11);
                token_list_post.add(11);
                break;
            case ";": //System.out.println(12);
                token_list_post.add(12);
                break;
            case ",": //System.out.println(13);
                token_list_post.add(13);
                break;
            case "=": //System.out.println(14);
                token_list_post.add(14);
                break;
            case "!": //System.out.println(15);
                token_list_post.add(15);
                break;
            case "[": //System.out.println(16);
                token_list_post.add(16);
                break;
            case "]": //System.out.println(17);
                token_list_post.add(17);
                break;
            case "&&": //System.out.println(18);
                token_list_post.add(18);
                break;
            case "||": //System.out.println(19);
                token_list_post.add(19);
                break;
            case "(": //System.out.println(20);
                token_list_post.add(20);
                break;
            case ")": //System.out.println(21);
                token_list_post.add(21);
                break;
            case "+": //System.out.println(22);
                token_list_post.add(22);
                break;
            case "-": //System.out.println(23);
                token_list_post.add(23);
                break;
            case "*": //System.out.println(24);
                token_list_post.add(24);
                break;
            case "!=": //System.out.println(25);
                token_list_post.add(25);
                break;
            case "==": //System.out.println(26);
                token_list_post.add(26);
                break;
            case "<": //System.out.println(27);
                token_list_post.add(27);
                break;
            case ">": //System.out.println(28);
                token_list_post.add(28);
                break;
            case "<=": //System.out.println(29);
                token_list_post.add(29);
                break;
            case ">=": //System.out.println(30);
                token_list_post.add(30);
                break;
            case "EOF": //System.out.println(33);
                token_list_post.add(33);
                break;
            default: //System.out.println(32);
                token_list_post.add(32);
                break;
        }
    }

    public static void kwCheck(String keyword) {
        String token = getToken();
        if (token.equals(keyword)) { skipToken(); }
        else { System.out.println("Error: Expected " + keyword + " but got " + token); }
    }
}
