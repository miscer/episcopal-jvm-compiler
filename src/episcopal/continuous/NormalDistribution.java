package episcopal.continuous;

/**
 * Normal distribution
 *
 * Continuous distribution with two parameters, mean and standard deviation.
 *
 * @see ContinuousDistribution
 */
public class NormalDistribution extends ContinuousDistribution {
    private float m;
    private float sd;

    /**
     * @param m Mean
     * @param sd Standard deviation
     */
    public NormalDistribution(float m, float sd) {
        this.m = m;
        this.sd = sd;
    }

    /**
     * @see ContinuousDistribution#cumulative(float)
     */
    @Override
    public float cumulative(float n) {
        org.apache.commons.math3.distribution.NormalDistribution d = new org.apache.commons.math3.distribution.NormalDistribution(m, sd);
        return (float) d.cumulativeProbability(n);
    }

    @Override
    public String toString() {
        return String.format("NormalDistribution{m=%f sd=%f}", m, sd);
    }
}
