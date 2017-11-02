package episcopal.runtime;

/**
 * Represents an Episcopal program. The class in the compiled bytecode will extend this class and implement the run
 * method that will evaluate the program.
 */
public abstract class Program {
    /**
     * Runs the program and prints out the result value. If there is an error, shows the error message.
     */
    public void print() {
        try {
            RuntimeValue result = run();
            System.out.println(result);
        } catch (RuntimeException error) {
            System.err.printf("Runtime error: %s\n", error.getMessage());
        }
    }

    /**
     * Runs the program and returns the result value
     * @return Program result value
     * @throws RuntimeException Thrown if there is a runtime error during the execuction
     */
    public abstract RuntimeValue run() throws RuntimeException;
}
