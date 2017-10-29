package episcopal.runtime;

public abstract class Program {
    public void print() {
        RuntimeValue result = run();
        System.out.println(result);
    }

    public abstract RuntimeValue run();
}
