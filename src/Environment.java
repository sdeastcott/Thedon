class Environment {

    public Lexeme car(Lexeme env) {
        return env.left;
    }

    public Lexeme cdr(Lexeme env) {
        return env.right;
    }

    public Lexeme setCar(Lexeme env, Lexeme value) {
        env.left = value;
        return env.left;
    }

    public Lexeme setCdr(Lexeme env, Lexeme value) {
        env.right = value;
        return env.right;
    }

    public Lexeme cons(String type, Lexeme left, Lexeme right) {
        return new Lexeme(type, left, right);
    }

    public Lexeme makeTable(Lexeme variables, Lexeme values) {
        return cons("TABLE", variables, values);
    }

    public boolean sameVariable(Lexeme a, Lexeme b) {
        return a.string.equals(b.string);
    }


    /* ********************* */
    /* Environment Functions */
    /* ********************* */
    public Lexeme create() {
        return extend(null, null, null);
    }

    public Lexeme lookup(Lexeme env, Lexeme variable) {
        while (env != null) {
            Lexeme table = car(env);
            Lexeme variables = car(table);
            Lexeme values = cdr(table);
            while (variables != null) {
                if (sameVariable(variable, car(variables))) return car(values);
                variables = cdr(variables);
                values = cdr(values);
            }
            env = cdr(env);
        }
        System.out.printf("variable '%s' is undefined\n", variable.string);
        System.exit(1);
        return null;
    }

    public Lexeme update(Lexeme env, Lexeme variable, Lexeme value) {
        while (env != null) {
            Lexeme table = car(env);
            Lexeme variables = car(table);
            Lexeme values = cdr(table);
            while (variables != null) {
                if (sameVariable(variable, car(variables))) return setCar(values, value);
                variables = cdr(variables);
                values = cdr(values);
            }
            env = cdr(env);
        }
        System.out.printf("variable '%s' is undefined\n", variable.string);
        System.exit(1);
        return null;
    }

    public Lexeme insert(Lexeme env, Lexeme variable, Lexeme value) {
        Lexeme table = car(env);
        setCar(table, cons("GLUE", variable, car(table)));
        setCdr(table, cons("GLUE", value, cdr(table)));
        return value;
    }

    public Lexeme extend(Lexeme env, Lexeme variables, Lexeme values) {
        return new Lexeme("ENV", makeTable(variables, values), env);
    }
}