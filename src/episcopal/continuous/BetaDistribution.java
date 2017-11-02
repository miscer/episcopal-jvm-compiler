package episcopal.continuous;

/**
 * Beta distribution
 *
 * Continuous distribution with two parameters, pseudocounts a and b.
 *
 * @see ContinuousDistribution
 */
public class BetaDistribution extends ContinuousDistribution {
    private float a, b;

    /**
     * Creates a new beta distribution with the specified pseudocounts
     * @param a a
     * @param b b
     */
    public BetaDistribution(float a, float b) {
        this.a = a;
        this.b = b;
    }

    /**
     * @see ContinuousDistribution#cumulative(float)
     */
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
