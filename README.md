#Thedon

**Author: Steven Eastcott**

This language doesn't have much use. It was primarily made as an exercise to help understand and appreciate the innerworkings of programming languages. Thedon is built on Java. The name originates from a man known as "The Don", who is, in short, an OG.


###Building Thedon
To build the interpreter, simply run 'make'. To run a .don file, use the command 'thedon filename.don'

###Test Problem

The source code for the RPN calculator is in 'problem.don'. To change the test input, change the expression on line 84 of that file. Run the test problem using the command 'make problem'. The evaluated result will be printed to the console.


###Syntax
- Curly braces '{ }' are required for all code blocks
- All statements end in a semicolon ';'
- Objects are created using the 'this' keyword.


###Comments

Thedon only provides single line commenting.
$ This is a single line comment.


###Definitions

Defining functions and initializing variables is done using 'def' and 'var' respectively.
Existing variables can be redefined without using 'var'.

**Example Variable Definition:**

    var count = 1;
    count = 2;

**Example Function Definition:**

    def functionName(param1, param2) {
        println("params are ", param1, " ", param2);
    }

    $ call the function
    functionName("one", "two");


###Operators

Thedon provides a basic set of operators:

**Arithmetic:**
- plus '+'
- minus '-'
- multiply '*'
- divide '/'
- exponentiation '^'
- modulus '%'

**Comparison:**
- equal '='
- not equal '!='
- greater than '>'
- greater than or equal to '>='
- less than '<'
- less than or equal to '<='

**Unary:**
- logical not '!'
- negative '-'

**Short Circuiting:**
- logical and 'and'
- logical or 'or'

**Assignment:**
- assign '='


####Precedence

Thedon does not currently have built-in precedence. Therefore, it is recommended that you use parenthesized
expressions if you wish to maintain precedence.


###Object Orientation

Objects are defined using the 'this' keyword. A defined constructor isn't required.

    def Person(firstName, lastName) {
        def fullName() {
            println(firstName, " ", lastName);
        }
        this;
    }

    var me = Person("Steven", "Eastcott");
    println(me.firstName);
    println(me.lastName);
    me.fullName();


###Built-in Functions

Thedon provides several builtin functions: print, println, arr_append, arr_remove, arr_length, split, isInteger, stoi

- isInteger(str)
  - determines if 'str' can be represented as an integer
- stoi(obj)
  - attempts to convert 'obj' to an integer
- split(str)
  - Splits str into an array of characters
  - split("example") == ["e", "x", "a", "m", "p", "l", "e"]
- arr_length(array)
  - Return an INTEGER for the length of array
- arr_append(array, obj)
  - Appends 'obj' to the end of the 'array'
- arr_remove(array, index)
  - Removes the item at the specified 'index' from the 'array'
- print/println(args*)
  - prints out each arg separated by a space. println will end in a newline


###Arrays
####Defining Arrays

Arrays are defined like all other variables.

    var arr = [1, "2", 3];

####Accessing Arrays

Arrays use zero-based indexing, and access to each element is done in constant time.

    var firstItem = arr[0];


### Conditionals
Thedon provides the standard if-else statement. Predicates must be wrapped in parentheses.
The 'else if' statement is currently unavailable.


### Iteration
Thedon provides a while loop for iteration.

    var x = 0;
    while(x < 10) {
        println("Hello!");
        x = x + 1;
    }


###Lambdas

####Defining Lambdas

Lambdas are defined using the 'lambda' keyword. All statements inside the lambda block must end in a semicolon.
If a lambda is defined within a function, it must end in a semicolon.

    var myLambda = lambda(param1, param2) { println(param1, " ", param2); };

    def lambdaFunction() {
        lambda(x) {
            println(x);
        };
    }

####Calling Lambdas

The ID of the variable holding the lambda must be wrapped in parentheses '()' in order to be called.
The arguments must be wrapped in vertical bars '||'

    (myLambda)|arg1, arg2|;
    (lambdaFunction())|arg1|;
