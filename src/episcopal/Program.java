package episcopal;

public abstract class Program {
    public void print() {
        Sample sample = run();
        System.out.println(sample);
    }

    public abstract Sample run();
}
