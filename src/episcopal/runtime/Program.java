package episcopal.runtime;

public abstract class Program {
    public void print() {
        try {
            RuntimeValue result = run();
            System.out.println(result);
        } catch (RuntimeException error) {
            System.err.printf("Runtime error: %s\n", error.getMessage());
        }
    }

    public abstract RuntimeValue run() throws RuntimeException;
}
