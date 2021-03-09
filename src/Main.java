// Kunal Patil
public class Main {
    
    /*
     * main will pass the input file to be checked for tokenizer, or output an error
     */
    public static void main(String[] args) {
        // program
        String programFileName = args[0];
        // input data
        String dataFileName = args[1];

        // convert program to tokens
        try {
            Tokenizer.tokenCheck(programFileName);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Incorrect input. Please try: java Main programFileName dataFileName");
            System.exit(2);
        }

        // run parser
        prog parseTree = Parser.getParseTree();

        // print parsetree
        Printer.printProg(parseTree);

        // execute program with data
        try {
            Executor.execProg(parseTree, dataFileName);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Incorrect input. Please try: java Main programFileName dataFileName");
            System.exit(2);
        }
	}
}
