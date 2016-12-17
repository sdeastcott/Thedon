public class Interpreter {
    static Evaluator evaluator;
    static Parser parser;

    public static void main(String[] args) throws Exception {
        evaluator = new Evaluator();
        parser = new Parser();
        evaluator.runEval(parser.parse(args[0]));
    }
}