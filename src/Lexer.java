import java.io.FileReader;
import java.io.PushbackReader;
import java.io.BufferedReader;

class Lexer {
    char ch;
    int line;
    String filename;
    PushbackReader input;

    public Lexer(String fname) throws Exception {
        filename = fname;
        input = new PushbackReader(new BufferedReader(new FileReader(filename)));
        line = 1;
    }

    public void skipWhiteSpace() throws Exception {
        while (ch == ' ' || ch == '\n' || ch == '\t' || ch == '\r') {
            if (ch == '\n') {
                line++;
            }
            ch = (char) input.read();
        }

        // check for comment
        if (ch == '$') {
            ch = (char) input.read();
            while (ch != '\n') {
                ch = (char) input.read();
            }
            skipWhiteSpace();
        }
    }

    public Lexeme lexString() throws Exception {
        String token = "";
        ch = (char) input.read();

        while (ch != '\"') {
            if (ch == '\\') ch = (char) input.read();
            token += ch;
            ch = (char) input.read();
        }

        return new Lexeme("STRING", token);
    }

    public Lexeme lexVariableOrKeyword() throws Exception {
        String token = "";

        while (Character.isDigit(ch) || Character.isLetter(ch) || ch == '_' || ch == '-' || ch == '?' || ch == '!') {
            token += ch;
            ch = (char) input.read();
        }

        input.unread(ch);

        if (token.equals("while")) {
            return new Lexeme("WHILE");
        }
        else if (token.equals("if")) {
            return new Lexeme("IF");
        }
        else if (token.equals("else")) {
            return new Lexeme("ELSE");
        }
        else if (token.equals("and")) {
            return new Lexeme("AND");
        }
        else if (token.equals("or")) {
            return new Lexeme("OR");
        }
        else if (token.equals("True")) {
            return new Lexeme("BOOLEAN", true);
        }
        else if (token.equals("False")) {
            return new Lexeme("BOOLEAN", false);
        }
        else if (token.equals("def")) {
            return new Lexeme("DEF");
        }
        else if (token.equals("null")) {
            return new Lexeme("NULL");
        }
        else if (token.equals("var")) {
            return new Lexeme("VAR");
        }
        else if (token.equals("print")) {
            return new Lexeme("PRINT");
        }
        else if (token.equals("println")) {
            return new Lexeme(("PRINTLN"));
        }
        else if (token.equals("lambda")) {
            return new Lexeme("LAMBDA");
        }
        else {
            return new Lexeme("VARIABLE", token);
        }
    }

    public Lexeme lexNumber() throws Exception {
        String token = "";

        while (Character.isDigit(ch)) {
            token += ch;
            ch = (char) input.read();
        }

        input.unread(ch);
        return new Lexeme("INTEGER", Integer.parseInt(token));
    }

    public Lexeme lexOperator() throws Exception {
        String token = "";

        while (ch == '!' || ch == '>' || ch == '<' || ch == '=') {
            token += ch;
            ch = (char) input.read();
        }

        input.unread(ch);
        switch (token) {
            case "!": return new Lexeme("NOT");
            case "=": return new Lexeme("ASSIGN");
            case ">": return new Lexeme("GREATER_THAN");
            case "<": return new Lexeme("LESS_THAN");
            case "!=": return new Lexeme("NOT_EQUAL");
            case "==": return new Lexeme("EQUAL");
            case ">=": return new Lexeme("GREATER_THAN_EQUAL_TO");
            case "<=": return new Lexeme("LESS_THAN_EQUAL_TO");
            default: return new Lexeme("UNKNOWN");
        }
    }

    public Lexeme lex() throws Exception {
        ch = (char) input.read();
        skipWhiteSpace();
        if (ch == -1 || ch == 65535) return new Lexeme("END_OF_INPUT");

        switch (ch) {
            case '(':
                return new Lexeme("OPEN_PAREN");
            case ')':
                return new Lexeme("CLOSE_PAREN");
            case '{':
                return new Lexeme("OPEN_BRACE");
            case '}':
                return new Lexeme("CLOSE_BRACE");
            case '[':
                return new Lexeme("OPEN_BRACKET");
            case ']':
                return new Lexeme("CLOSE_BRACKET");
            case '|':
                return new Lexeme("VBAR");
            case ',':
                return new Lexeme("COMMA");
            case '+':
                return new Lexeme("PLUS");
            case '*':
                return new Lexeme("TIMES");
            case '-':
                return new Lexeme("MINUS");
            case '/':
                return new Lexeme("DIVIDE");
            case ';':
                return new Lexeme("SEMICOLON");
            case '.':
                return new Lexeme("DOT");
            case '%':
                return new Lexeme("MOD");
            case '^':
                return new Lexeme("EXP");
            case '!':
            case '<':
            case '>':
            case '=':
                return lexOperator();

            default:
                if (Character.isDigit(ch)) {
                    return lexNumber();
                } else if (ch == '\"') {
                    return lexString();
                } else if (Character.isLetter(ch)) {
                    return lexVariableOrKeyword();
                } else {
                    return new Lexeme("UNKNOWN", ch);
                }
        }
    }
}
