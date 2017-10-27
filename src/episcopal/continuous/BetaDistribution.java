package episcopal.continuous;

public class BetaDistribution extends ContinuousDistribution {
    private float a, b;

    public BetaDistribution(float a, float b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public float cumulative(float n) {
        org.apache.commons.math3.distribution.BetaDistribution d = new org.apache.commons.math3.distribution.BetaDistribution(a, b);
        return (float) d.cumulativeProbability(n);
    }

    @Override
    public String toString() {
        return String.format("BetaDistribution{a=%f b=%f}", a, b);
    }
}
