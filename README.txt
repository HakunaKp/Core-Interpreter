User manual:

This program was created using java version 11.0.8. There are no known bugs.

Make sure all files are in the same folder, then compile the program with the following command:
javac Main.java

After compilation, run the program with one input program to be tokenized and its corresponding data:
java Main inputProgram.txt inputData.txt

The program was tested with 3 test files. Run the program with these tests with the commands:
java Main test1.txt test1data.txt
java Main test2.txt
java Main test2.txt test2data1.txt
java Main test2.txt test2data2.txt
java Main test2.txt test2data3.txt
java Main test3.txt
java Main test3.txt test3data1.txt
java Main test3.txt test3data2.txt
java Main test3.txt test3data3.txt

Design of tokenizer:
First the program will take in an input file to be tokenized.
If the user has correctly inputted a file, it will be read in
and passed to the Tokenizer class. Here, each line is individually
searched for tokens. 

Two main variables:
token_list: tokens before they are tokenized: "program, begin, end"
token_list_post: tokens after tokenization: "1, 2, 3"

First check the line for a digit sequence with Character.isDigit()

Then check for the special symbols:
";,=![]()+-*<>"
"&&"
"||"
Also check if special symbol is one or two characters

Check if token contains any illegal characters like $,@,~ etc.
Check for white space "\n\t\r" and everything above, if not loop the string of letters
Check if string of letters is a reserved word with printTokens(String) helper method

In each of these conditions, a string of the token is made from a substring of the line
When printing the token to the screen, it is also added to token_list_post

Design of parser, printer, executor:
Parser.java builds the parse tree. There is a class for every non-terminal node. In each class the node
can access data from its child nodes. In the class prog, there are class methods for getDeclSeq() and
getStmtSeq() which can be used on prog objects in printer and executor to return their <decl seq> and <stmt seq> values.

The printer and executor will be similar to the parser in that there is a class for printing or executing every non-terminal node.
Ex: printProg in Printer.java will taken in a prog object. printProg will print the words "program", "begin", and "end",
and will make calls to print_decl_seq and print_stmt_seq, passing in values from prog.getDeclSeq() and prog.getStmtSeq().
Ex: print_decl_seq(parseTree.getDeclSeq());
This way of recursively passing in values ensures that the correct node is being accessed each time.
If a nonterminal has multiple choices (<decl seq> = <decl> | <decl> <decl seq>), this is evaluated with a getChoice() method
which is instantiated in the parser.
Terminal nodes are evaluated for their values from the intVal() and idName() methods in the Tokenizer.

TESTING

Run with command: java Main test1.txt test1data.txt

Test files. 
test1.txt
test1data.txt

test2.txt
test2data1.txt
test2data2.txt
test2data3.txt

test3.txt
test3data1.txt
test3data2.txt
test3data3.txt