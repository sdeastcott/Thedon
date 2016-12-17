class Parser {
    Lexer lexer;
    Lexeme currentLexeme;
    Lexeme output;

    public boolean check(String type) {
        return currentLexeme.type.equals(type);
    }

    public Lexeme advance() throws Exception {
        Lexeme oldLexeme = currentLexeme;
        currentLexeme = lexer.lex();
        return oldLexeme;
    }

    public Lexeme match(String type) throws Exception {
        matchNoAdvance(type);
        return advance();
    }

    public void matchNoAdvance(String type) {
        if (currentLexeme.type.equals("UNKNOWN")) {
            System.out.printf("EXCEPTION: SyntaxException\nline %d: invalid character\n", lexer.line);
            System.exit(0);
        }

        else if (!check(type)) {
            System.out.printf("EXCEPTION: SyntaxException\nline %d: expecting %s, found %s instead\n", lexer.line, type, currentLexeme.type);
            System.exit(0);
        }
    }


    /* ***************** */
    /* Pending Functions */
    /* ***************** */
    private boolean programPending() {
        return definitionPending();
    }

    private boolean definitionPending() {
        return check("VAR") || check("VARIABLE") || check("DEF") || check("OPEN_PAREN");
    }

    private boolean initPending() {
        return check("ASSIGN");
    }

    public boolean functionCallPending() {
        return check("OPEN_PAREN");
    }

    private boolean argListPending() {
        return exprPending();
    }

    private boolean exprPending() {
        return unaryPending();
    }

    private boolean statementListPending() {
        return statementPending();
    }

    private boolean statementPending() {
        return check("IF") || check("WHILE") || exprPending() || check("VAR") || check("DEF");
    }

    private boolean unaryPending() {
        return check("INTEGER") || check("STRING") || check("BOOLEAN") || check("MINUS") ||
                check("NOT") || check("OPEN_BRACKET") || check("NULL") || check("PRINT") ||
                check("PRINTLN") || check("VARIABLE") || check("OPEN_PAREN") || check("LAMBDA");
    }

    private boolean operatorPending() {
        return check("PLUS") || check("TIMES") || check("MINUS") || check("DIVIDE") ||
                check("MOD") || check("EXP") || check("AND") || check("OR") || check("NOT") ||
                check("LESS_THAN") || check("GREATER_THAN") || check("EQUAL") ||
                check("LESS_THAN_EQUAL_TO") || check("GREATER_THAN_EQUAL_TO") || check("NOT_EQUAL");
    }


    /* ************* */
    /* Grammar Rules */
    /* ************* */
    public Lexeme program() throws Exception {
        Lexeme a = definition();
        Lexeme b = null;
        if (programPending()) {
            b = program();
        }
        return new Lexeme("PROGRAM", a, b);
    }

    public Lexeme definition() throws Exception {
        if (check("VAR")) {
            return varDef();
        }
        else if (check("DEF")) {
            return functionDef();
        }
        else if (check("VARIABLE")) {
            Lexeme tree = new Lexeme("DEFINITION");
            tree.left = varExpr();
            match("SEMICOLON");
            return tree;
        }
        else {
            Lexeme tree = new Lexeme("DEFINITION");
            tree.left = parenExpr();
            match("SEMICOLON");
            return tree;
        }
    }

    public Lexeme init() throws Exception {
        match("ASSIGN");
        Lexeme a = expr();
        return new Lexeme("INIT", a, null);
    }

    public Lexeme varDef() throws Exception {
        match("VAR");
        Lexeme tree;
        Lexeme a = match("VARIABLE");
        if (initPending()) {
            tree = new Lexeme("VARDEF", a, init());
        }
        else {
            tree = new Lexeme("VARDEF", a, null);
        }
        match("SEMICOLON");
        return tree;
    }

    public Lexeme functionDef() throws Exception {
        match("DEF");
        Lexeme a = match("VARIABLE");
        match("OPEN_PAREN");
        Lexeme b = optParamList();
        match("CLOSE_PAREN");
        Lexeme c = block();
        return new Lexeme("FUNCTIONDEF", a, new Lexeme("GLUE", b, new Lexeme("GLUE", c, null)));
    }

    public Lexeme functionCall() throws Exception {
        match("OPEN_PAREN");
        Lexeme b = optArgList();
        match("CLOSE_PAREN");
        return b;
    }

    public Lexeme lambdaDef() throws Exception {
        match("LAMBDA");
        match("VBAR");
        Lexeme a = optParamList();
        match("VBAR");
        Lexeme b = block();
        return new Lexeme("LAMBDADEF", a, b);
    }

    public Lexeme expr() throws Exception {
        Lexeme tree = unary();
        if (operatorPending()) {
            Lexeme a = operator();
            a.left = tree;
            a.right = expr();
            tree = a;
        }
        return tree;
    }

    public Lexeme varExpr() throws Exception {
        Lexeme a = match("VARIABLE");
        if (functionCallPending()) {
            Lexeme b = functionCall();
            return new Lexeme("FUNCTIONCALL", a, b);
        }
        else if (initPending()) {
            Lexeme b = init();
            return new Lexeme("VARASSIGN", a, b);
        }
        else if (check("OPEN_BRACKET")) {
            match("OPEN_BRACKET");
            Lexeme b = unary();
            match("CLOSE_BRACKET");
            return new Lexeme("ARRAYACCESS", a, b);
        }
        else if (check("DOT")) {
            Lexeme b = match("DOT");
            b.left = a;
            b.right = varExpr();
            return b;
        }
        return a;
    }

    public Lexeme parenExpr() throws Exception {
        match("OPEN_PAREN");
        Lexeme a = expr();
        match("CLOSE_PAREN");
        Lexeme tree = new Lexeme("PARENEXPR", a, null);

        // check to see if it is a lambda call
        if (check("VBAR")) {
            match("VBAR");
            Lexeme b = optArgList();
            match("VBAR");
            tree = new Lexeme("LAMBDACALL", a, b);
        }
        return tree;
    }

    public Lexeme statement() throws Exception {
        Lexeme a = null;
        if (check("IF")) {
            a = ifStatement();
        }
        else if (check("WHILE")) {
            a = whileLoop();
        }
        else if (check("VAR")) {
            a = varDef();
        }
        else if (check("DEF")) {
            a = functionDef();
        }
        else {
            a = expr();
            match("SEMICOLON");
        }
        return new Lexeme("STATEMENT", a, null);
    }

    public Lexeme statementList() throws Exception {
        Lexeme a = statement();
        Lexeme b = null;
        if (statementListPending()) {
            b = statementList();
        }
        return new Lexeme("STATEMENTLIST", a, b);
    }

    public Lexeme optStatementList() throws Exception {
        if (statementListPending()) {
            return statementList();
        }
        return null;
    }

    public Lexeme paramList() throws Exception {
        Lexeme a = match("VARIABLE");
        Lexeme b = null;
        if (check("COMMA")) {
            match("COMMA");
            b = paramList();
        }
        return new Lexeme("PARAMLIST", a, b);
    }

    public Lexeme optParamList() throws Exception {
        if (check("VARIABLE")) {
            return paramList();
        }
        return null;
    }

    public Lexeme argList() throws Exception {
        Lexeme a = expr();
        Lexeme b = null;
        if (check("COMMA")) {
            match("COMMA");
            b = argList();
        }
        return new Lexeme("ARGLIST", a, b);
    }

    public Lexeme optArgList() throws Exception {
        if (argListPending()) {
            return argList();
        }
        return null;
    }

    public Lexeme unary() throws Exception {
        Lexeme tree;

        if (check("INTEGER")) {
            tree = match("INTEGER");
        }
        else if (check("STRING")) {
            tree = match("STRING");
        }
        else if (check("BOOLEAN")) {
            tree = match("BOOLEAN");
        }
        else if (check("MINUS")) {
            tree = match("MINUS");
            tree.type = "NEGATIVE";
            tree.left = unary();
            tree.right = null;
        }
        else if (check("NOT")) {
            tree = match("NOT");
            tree.left = unary();
            tree.right = null;
        }
        else if (check("OPEN_BRACKET")) {
            match("OPEN_BRACKET");
            Lexeme a = optArgList();
            match("CLOSE_BRACKET");
            tree = new Lexeme("ARRAYDEF", a, null);
        }
        else if (check("NULL")) {
            tree = match("NULL");
        }
        else if (check("PRINT")) {
            tree = match("PRINT");
            match("OPEN_PAREN");
            tree.left = optArgList();
            match("CLOSE_PAREN");
            tree.right = null;
        }
        else if (check("PRINTLN")) {
            tree = match("PRINTLN");
            match("OPEN_PAREN");
            tree.left = optArgList();
            match("CLOSE_PAREN");
            tree.right = null;
        }
        else if (check("VARIABLE")) {
            tree = varExpr();
        }
        else if (check("OPEN_PAREN")) {
            tree = parenExpr();
        }
        else if (check("LAMBDA")) {
            tree = lambdaDef();
        }
        else {
            tree = match("UNKNOWN");
        }

        return tree;
    }

    public Lexeme operator() throws Exception {
        if (check("PLUS")) {
            return match("PLUS");
        }
        else if (check("TIMES")) {
            return match("TIMES");
        }
        else if (check("MINUS")) {
            return match("MINUS");
        }
        else if (check("DIVIDE")) {
            return match("DIVIDE");
        }
        else if (check("MOD")) {
            return match("MOD");
        }
        else if (check("EXP")) {
            return match("EXP");
        }
        else if (check("AND")) {
            return match("AND");
        }
        else if (check("OR")) {
            return match("OR");
        }
        else if (check("NOT")) {
            return match("NOT");
        }
        else if (check("NOT_EQUAL")) {
            return match("NOT_EQUAL");
        }
        else if (check("LESS_THAN")) {
            return match("LESS_THAN");
        }
        else if (check("LESS_THAN_EQUAL_TO")) {
            return match("LESS_THAN_EQUAL_TO");
        }
        else if (check("GREATER_THAN")) {
            return match("GREATER_THAN");
        }
        else if (check("GREATER_THAN_EQUAL_TO")) {
            return match("GREATER_THAN_EQUAL_TO");
        }
        else {
            return match("EQUAL");
        }
    }

    public Lexeme ifStatement() throws Exception {
        match("IF");
        match("OPEN_PAREN");
        Lexeme a = expr();
        match("CLOSE_PAREN");
        Lexeme b = block();
        Lexeme c = optElse();
        return new Lexeme("IFSTATEMENT", a, new Lexeme("GLUE", b,
                new Lexeme("GLUE", c, null)));
    }

    public Lexeme optElse() throws Exception {
        if (check("ELSE")) {
            Lexeme a = match("ELSE");
            return new Lexeme("OPTELSE", a, block());
        }
        return null;

    }

    public Lexeme whileLoop() throws Exception {
        match("WHILE");
        match("OPEN_PAREN");
        Lexeme a = expr();
        match("CLOSE_PAREN");
        Lexeme b = block();
        return new Lexeme("WHILE", a, b);
    }

    public Lexeme block() throws Exception {
        match("OPEN_BRACE");
        Lexeme a = optStatementList();
        match("CLOSE_BRACE");
        return new Lexeme("BLOCK", a, null);
    }

    public Lexeme parse(String fname) throws Exception {
        lexer = new Lexer(fname);
        currentLexeme = lexer.lex();
        output = program();
        match("END_OF_INPUT");
        return output;
    }
}