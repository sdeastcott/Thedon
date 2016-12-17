import java.util.ArrayList;

public class Evaluator {
    private Lexeme global;
    private Environment environment;

    public Evaluator() {
        environment = new Environment();
        global = environment.create();
    }

    private Lexeme eval(Lexeme tree, Lexeme env) {
        if (tree == null) {
            return null;
        }

        switch (tree.type) {
            case "STRING":
            case "BOOLEAN":
            case "SEMICOLON":
            case "INTEGER":
            case "NULL":
            case "ARRAYLIST":
            case "LAMBDADEF": return tree;
            case "VARIABLE": return environment.lookup(env, tree);
            case "NOT": return evalNot(tree, env);
            case "NEGATIVE": return evalNegative(tree, env);

            // operators
            case "PLUS": return evalPlus(tree, env);
            case "MINUS": return evalMinus(tree, env);
            case "TIMES": return evalTimes(tree, env);
            case "DIVIDE": return evalDivide(tree, env);
            case "EXP": return evalExp(tree, env);
            case "MOD": return evalMod(tree, env);
            case "EQUAL": return evalEqual(tree, env);
            case "LESS_THAN": return evalLessThan(tree, env);
            case "GREATER_THAN": return evalGreaterThan(tree, env);
            case "NOT_EQUAL": return evalNotEqual(tree, env);
            case "LESS_THAN_EQUAL_TO": return evalLessThanEqualTo(tree, env);
            case "GREATER_THAN_EQUAL_TO": return evalGreaterThanEqualTo(tree, env);
            case "ASSIGN": return evalAssign(tree, env);

            // boolean
            case "AND":
            case "OR": return evalShortCircuitOperator(tree, env);

            // built-in functions
            case "PRINT": return evalPrint(tree, env);
            case "PRINTLN": return evalPrintln(tree, env);

            // variables and function definitions
            case "PROGRAM": return evalProgram(tree, env);
            case "DEFINITION": return evalDefinition(tree, env);
            case "VARDEF": return evalVarDef(tree, env);
            case "VARASSIGN": return evalVarAssign(tree, env);
            case "FUNCTIONDEF": return evalFunctionDef(tree, env);
            case "FUNCTIONCALL": return evalFunctionCall(tree, env);
            case "ARRAYDEF": return evalArrayDef(tree, env);
            case "ARRAYACCESS": return evalArrayAccess(tree, env);
            case "ARGLIST": return evalArgList(tree, env);
            case "PARENEXPR": return evalParenExpr(tree, env);
            case "LAMBDACALL": return evalLambdaCall(tree, env); // TODO
            case "BLOCK": return evalBlock(tree, env);
            case "DOT": return evalDot(tree, env);

            // statements
            case "STATEMENTLIST": return evalStatementList(tree, env);
            case "STATEMENT": return evalStatement(tree, env);
            case "IFSTATEMENT": return evalIfStatement(tree, env);
            case "OPTELSE": return evalOptElse(tree, env);
            case "WHILE": return evalWhile(tree, env);

            default:
                System.err.printf("\nFatal error in Evaluator.java: bad expression %s\n", tree.type);
                System.exit(1);
                return null;
        }
    }


    private Lexeme evalNot(Lexeme t, Lexeme env) {
        Lexeme result = eval(t.left, env);
        result.bool = !result.bool;
        return result;
    }

    private Lexeme evalNegative(Lexeme t, Lexeme env) {
        Lexeme result = eval(t.right, env);
        result.integer *= -1;
        return result;
    }

    private Lexeme evalPlus(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        return new Lexeme("INTEGER", left.integer + right.integer);
    }

    private Lexeme evalMinus(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        return new Lexeme("INTEGER", left.integer - right.integer);
    }

    private Lexeme evalTimes(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        return new Lexeme("INTEGER", left.integer * right.integer);
    }

    private Lexeme evalDivide(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        return new Lexeme("INTEGER", left.integer / right.integer);
    }

    private Lexeme evalExp(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        int result = (int) Math.pow((double) left.integer, (double) right.integer);
        return new Lexeme("INTEGER", result);
    }

    private Lexeme evalMod(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        return new Lexeme("INTEGER", left.integer % right.integer);
    }

    private Lexeme evalEqual(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);

        if (left.type.equals("INTEGER") && right.type.equals("INTEGER"))
            return new Lexeme("BOOLEAN", left.integer == right.integer);
        else if (left.type.equals("STRING") && right.type.equals("STRING"))
            return new Lexeme("BOOLEAN", left.string.equals(right.string));
        else if (left.type.equals("BOOLEAN") && right.type.equals("BOOLEAN"))
            return new Lexeme("BOOLEAN", left.bool == right.bool);
        else if (left.type.equals("NULL") && !right.type.equals("NULL"))
            return new Lexeme("BOOLEAN", false);
        else if (!left.type.equals("NULL") && right.type.equals("NULL"))
            return new Lexeme("BOOLEAN", false);
        else if (left.type.equals("NULL") && right.type.equals("NULL"))
            return new Lexeme("BOOLEAN", true);
        else {
            System.out.printf("\nFatal error in Evaluator.java: Type mismatch, attempting to compare %s with %s\n", left.type, right.type);
            System.exit(1);
            return null;
        }
    }

    private Lexeme evalLessThan(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);

        if (left.type.equals("INTEGER") && right.type.equals("INTEGER")) {
            return new Lexeme("BOOLEAN", left.integer < right.integer);
        } else if (left.type.equals("STRING") && right.type.equals("STRING")) {
            return new Lexeme("BOOLEAN", left.string.compareTo(right.string));
        } else {
            System.out.printf("\nFatal error in Evaluator.java: Type mismatch, attempting to compare %s with %s\n", left.type, right.type);
            System.exit(1);
            return null;
        }
    }

    private Lexeme evalGreaterThan(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);

        if (left.type.equals("INTEGER") && right.type.equals("INTEGER")) {
            return new Lexeme("BOOLEAN", left.integer > right.integer);
        } else if (left.type.equals("STRING") && right.type.equals("STRING")) {
            return new Lexeme("BOOLEAN", left.string.compareTo(right.string));
        } else {
            System.out.printf("\nFatal error in Evaluator.java: Type mismatch, attempting to compare %s with %s\n", left.type, right.type);
            System.exit(1);
            return null;
        }
    }

    private Lexeme evalNotEqual(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);

        if (left.type.equals("INTEGER") && right.type.equals("INTEGER"))
            return new Lexeme("BOOLEAN", left.integer != right.integer);
        else if (left.type.equals("STRING") && right.type.equals("STRING"))
            return new Lexeme("BOOLEAN", !left.string.equals(right.string));
        else if (left.type.equals("BOOLEAN") && right.type.equals("BOOLEAN"))
            return new Lexeme("BOOLEAN", left.bool != right.bool);
        else if (left.type.equals("NULL") && !right.type.equals("NULL"))
            return new Lexeme("BOOLEAN", true);
        else if (!left.type.equals("NULL") && right.type.equals("NULL"))
            return new Lexeme("BOOLEAN", true);
        else if (left.type.equals("NULL") && right.type.equals("NULL"))
            return new Lexeme("BOOLEAN", false);
        else {
            System.out.printf("\nFatal error in Evaluator.java: Type mismatch, attempting to compare %s with %s\n", left.type, right.type);
            System.exit(1);
            return null;
        }
    }

    private Lexeme evalLessThanEqualTo(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);

        if (left.type.equals("INTEGER") && right.type.equals("INTEGER")) {
            return new Lexeme("BOOLEAN", left.integer <= right.integer);
        } else if (left.type.equals("STRING") && right.type.equals("STRING")) {
            return new Lexeme("BOOLEAN", left.string.compareTo(right.string));
        } else {
            System.out.printf("\nFatal error in Evaluator.java: Type mismatch, attempting to compare %s with %s\n", left.type, right.type);
            System.exit(1);
            return null;
        }
    }

    private Lexeme evalGreaterThanEqualTo(Lexeme t, Lexeme env) {
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);

        if (left.type.equals("INTEGER") && right.type.equals("INTEGER")) {
            return new Lexeme("BOOLEAN", left.integer >= right.integer);
        } else if (left.type.equals("STRING") && right.type.equals("STRING")) {
            return new Lexeme("BOOLEAN", left.string.compareTo(right.string));
        } else {
            System.out.printf("\nFatal error in Evaluator.java: Type mismatch, attempting to compare %s with %s\n", left.type, right.type);
            System.exit(1);
            return null;
        }
    }

    private Lexeme evalShortCircuitOperator(Lexeme t, Lexeme env) {
        switch (t.type) {
            case "AND":
                return evalAnd(t, env);
            case "OR":
                return evalOr(t, env);
            default:
                return null;
        }
    }

    private Lexeme evalAnd(Lexeme t, Lexeme env) {
        if (eval(t.left, env).bool && eval(t.right, env).bool) {
            return new Lexeme("BOOLEAN", true);
        } else {
            return new Lexeme("BOOLEAN", false);
        }
    }

    private Lexeme evalOr(Lexeme t, Lexeme env) {
        if (eval(t.left, env).bool || eval(t.right, env).bool) {
            return new Lexeme("BOOLEAN", true);
        } else {
            return new Lexeme("BOOLEAN", false);
        }
    }


    private Lexeme evalPrint(Lexeme t, Lexeme env) {
        Lexeme eargs = evalArgList(t.left, env);
        while (eargs != null) {
            System.out.print(eargs.left);
            eargs = eargs.right;
        }
        return null;
    }

    public Lexeme evalPrintln(Lexeme t, Lexeme env) {
        Lexeme eargs = evalArgList(t.left, env);
        while (eargs != null) {
            System.out.print(eargs.left);
            eargs = eargs.right;
        }
        System.out.println();
        return null;
    }


    private Lexeme evalProgram(Lexeme t, Lexeme env) {
        Lexeme value = null;
        while (t != null) {
            value = eval(t.left, env);
            t = t.right;
        }
        return value;
    }

    private Lexeme evalDefinition(Lexeme t, Lexeme env) {
        Lexeme value = null;
        while (t != null) {
            value = eval(t.left, env);
            t = t.right;
        }
        return value;
    }

    private Lexeme evalVarDef(Lexeme t, Lexeme env) {
        if (t.right == null) {
            environment.insert(env, t.left, new Lexeme("NULL"));
            return null;
        }
        else {
            Lexeme value = eval(t.right.left, env);
            environment.insert(env, t.left, value);
            return value;
        }
    }

    private Lexeme evalVarAssign(Lexeme t, Lexeme env) {
        Lexeme lexeme = new Lexeme("ASSIGN", t.left, t.right.left);
        return eval(lexeme, env);
    }

    private Lexeme evalAssign(Lexeme t, Lexeme env) {
        Lexeme value = eval(t.right, env);

        if (t.left.type.equals("VARIABLE")) {
            environment.update(env, t.left, value);
        }
        else if (t.left.type.equals("DOT")) {
            Lexeme object = eval(t.left.left, env);
            environment.update(object, t.left.right, value);
        }
        return value;
    }

    private Lexeme evalFunctionDef(Lexeme t, Lexeme env) {
        Lexeme closure = new Lexeme("CLOSURE", env, t);
        environment.insert(env, t.left, closure);
        return closure;
    }

    private Lexeme evalFunctionCall(Lexeme t, Lexeme env) {
        Lexeme args = t.right;

        // Built-Ins
        if (t.left.string.equals("arr_append")) {
            Lexeme eargs = eval(args, env);
            Lexeme arr = eval(args.left, env);
            Lexeme value = eval(args.right.left, env);
            arr.array.add(value);
            return arr;
        }
        else if (t.left.string.equals("arr_remove")) {
            Lexeme arr = eval(args.left, env);
            Lexeme index = eval(args.right.left, env);
            arr.array.remove(index.integer);
            return arr;
        }
        else if (t.left.string.equals("arr_length")) {
            Lexeme arrDef = eval(args.left, env);
            Lexeme arr = eval(arrDef, env);
            return new Lexeme("INTEGER", arr.array.size());
        }
        else if (t.left.string.equals("split")) {
            Lexeme stringToSplit = eval(args.left, env);
            String[] splitString = stringToSplit.string.split("\\s+");
            ArrayList<Lexeme> list = new ArrayList();
            Lexeme arr = new Lexeme("ARRAYLIST", list);
            for (String s : splitString) {
                arr.array.add(new Lexeme("STRING", s));
            }
            return arr;
        }
        else if (t.left.string.equals("isInteger")) {
            Lexeme str = eval(args.left, env);
            if (str.string.matches("^-?\\d+$")) {
                return new Lexeme("BOOLEAN", true);
            }
            return new Lexeme("BOOLEAN", false);
        }
        else if (t.left.string.equals("stoi")) {
            Lexeme strToConvert = eval(args.left, env);
            return new Lexeme("INTEGER", Integer.parseInt(strToConvert.string));
        }

        Lexeme closure = eval(t.left, env);
        Lexeme params = closure.right.right.left;
        Lexeme body = closure.right.right.right.left;
        Lexeme senv = closure.left;
        Lexeme eargs = evalArgList(args, env);
        Lexeme xenv = environment.extend(senv, params, eargs);
        environment.insert(xenv, new Lexeme("VARIABLE", "this"), xenv);
        return eval(body, xenv);
    }

    private Lexeme evalArrayDef(Lexeme t, Lexeme env) {
        Lexeme arr = new Lexeme("EVALUATED");
        Lexeme evaluated = eval(t.left, env);
        ArrayList<Lexeme> ptrs = new ArrayList<>();
        Lexeme cursor = evaluated;

        while (cursor != null) {
            ptrs.add(cursor.left);
            cursor = cursor.right;
        }

        arr.left = evaluated;
        arr.right = null;
        arr.array = ptrs;
        return arr;
    }

    private Lexeme evalArrayAccess(Lexeme t, Lexeme env) {
        Lexeme arr = eval(t.left, env);
        Lexeme index = eval(t.right, env);
        Lexeme result = arr.array.get(index.integer);
        return result;
    }

    private Lexeme evalArgList(Lexeme t, Lexeme env) {
        Lexeme result = new Lexeme("EVALUATED");
        Lexeme ptr = result;
        while (t != null) {
            Lexeme value = eval(t.left, env);
            ptr.left = value;
            t = t.right;
            if (t != null) {
                ptr.right = new Lexeme("EVALUATED");
                ptr = ptr.right;
            }
        }
        return result;
    }

    private Lexeme evalParenExpr(Lexeme t, Lexeme env) {
        return eval(t.left, env);
    }

    private Lexeme evalLambdaCall(Lexeme t, Lexeme env) {
        if (t.left.type.equals("FUNCTIONCALL")) {
            Lexeme closure = environment.lookup(env, t.left.left);
            Lexeme eargs = eval(t.right, env);
            Lexeme params = closure.right.right.right.left.left.left.left.left;
            Lexeme body = closure.right.right.right.left.left.left.left.right;
            Lexeme xenv = environment.extend(env, params, eargs);
            return eval(body, xenv);
        }
        else {
            if (t.left.type.equals("ARRAYACCESS")) {
                Lexeme temp = eval(t.left, env);
                t.left = temp;
            }

            Lexeme eargs = eval(t.right, env);
            Lexeme params = t.left.left;
            Lexeme body = t.left.right;

            if (body == null) {
                Lexeme closure = environment.lookup(env, t.left);
                params = closure.left;
                body = closure.right;
            }

            Lexeme xenv = environment.extend(env, params, eargs);
            return eval(body, xenv);
        }
    }

    private Lexeme evalBlock(Lexeme t, Lexeme env) {
        Lexeme result = null;
        while (t != null) {
            result = eval(t.left, env);
            t = t.right;
        }
        return result;
    }

    private Lexeme evalDot(Lexeme t, Lexeme env) {
        Lexeme object = eval(t.left, env);
        if (t.right.type.equals("FUNCTIONCALL")) {
            Lexeme closure = eval(t.right.left, object);
            Lexeme args = t.right.right;
            Lexeme params = closure.right.right.left;
            Lexeme body = closure.right.right.right.left;
            Lexeme senv = closure.left;
            Lexeme eargs = evalArgList(args, env);
            Lexeme xenv = environment.extend(senv, params, eargs);
            environment.insert(xenv, new Lexeme("VARIABLE", "this"), xenv);
            return eval(body, xenv);
        }
        return eval(t.right, object);
        //return eval(t.right, environment.extend(env, object.left, object.right.left));
    }



    private Lexeme evalStatementList(Lexeme t, Lexeme env) {
        Lexeme value = null;
        while (t != null) {
            value = eval(t.left, env);
            t = t.right;
        }
        return value;
    }

    private Lexeme evalStatement(Lexeme t, Lexeme env) {
        return eval(t.left, env);
    }

    private Lexeme evalWhile(Lexeme t, Lexeme env) {
        Lexeme result = null;
        while (eval(t.left, env).bool) {
            result = eval(t.right, env);
        }
        return result;
    }

    private Lexeme evalIfStatement(Lexeme t, Lexeme env) {
        if (eval(t.left, env).bool) return eval(t.right.left, env);
        return eval(t.right.right.left, env);
    }

    private Lexeme evalOptElse(Lexeme t, Lexeme env) {
        return eval(t.right, env);
    }

    public Lexeme runEval(Lexeme tree) {
        return eval(tree, global);
    }
}
