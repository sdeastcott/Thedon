all: Lexeme.class Lexer.class Parser.class Environment.class Evaluator.class Interpreter.class
	 chmod +x dpl


Lexeme.class: src/Lexeme.java
	javac -d . -classpath . src/Lexeme.java

Lexer.class:
	javac -d . -classpath . src/Lexer.java

Parser.class:
	javac -d . -classpath . src/Parser.java

Environment.class:
	javac -d . -classpath . src/Environment.java

Evaluator.class:
	javac -d . -classpath . src/Evaluator.java

Interpreter.class:
	javac -d . -classpath . src/Interpreter.java


clean:
	rm -f *.class


error1:
	cat samples/error1.don
error1x:
	thedon samples/error1.don

error2:
	cat samples/error2.don
error2x:
	thedon samples/error2.don

error3:
	cat samples/error3.don
error3x:
	thedon samples/error3.don

arrays:
	cat samples/arrays.don
arraysx:
	thedon samples/arrays.don

conditionals:
	cat samples/conditionals.don
conditionalsx:
	thedon samples/conditionals.don

recursion:
	cat samples/recursion.don
recursionx:
	thedon samples/recursion.don

iteration:
	cat samples/iteration.don
iterationx:
	thedon samples/iteration.don

functions:
	cat samples/functions.don
functionsx:
	thedon samples/functions.don

dictionary:
	cat samples/dictionary.don
dictionaryx:
	thedon samples/dictionary.don

problem:
	cat samples/problem.don
problemx:
	thedon samples/problem.don