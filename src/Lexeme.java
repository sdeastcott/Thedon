import java.util.ArrayList;

class Lexeme {
    String string;
    int integer;
    boolean bool;
    char character;
    ArrayList<Lexeme> array;

    String type;
    Lexeme left;
    Lexeme right;

    public Lexeme() {}

    public Lexeme (String type, Lexeme left, Lexeme right) {
        this.type = type;
        this.left = left;
        this.right = right;
    }

    public Lexeme(String type) {
        this.type = type;
    }

    public Lexeme(String type, String string) {
        this.type = type;
        this.string = string;
    }

    public Lexeme(String type, int integer) {
        this.type = type;
        this.integer = integer;
    }

    public Lexeme(String type, boolean bool) {
        this.type = type;
        this.bool = bool;
    }

    public Lexeme(String type, char character) {
        this.type = type;
        this.character = character;
    }

    public Lexeme(String type, ArrayList<Lexeme> arr) {
        this.type = type;
        this.array = arr;
    }

    public String toString() {
        String result = "";

        if (type.equals("INTEGER")) {
            return result + integer;
        }
        else if (type.equals("STRING") || type.equals("VARIABLE")) {
            return result + string;
        }
        else if (type.equals("BOOLEAN")) {
            return result + bool;
        }
        else if (type.equals("UNKNOWN")) {
            return result + character;
        }
        else {
            return type;
        }

    }
}
