package episcopal.continuous;

import episcopal.Sample;
import episcopal.discrete.DiscreteSample;

/**
 * Continuous sample does not contain any discrete values, but is able to work with the cumulative probability of
 * its distribution.
 *
 * The sample can also be shifted and scaled by adding and multiplying it. This affects the resulting cumulative
 * probability. For example, if we have a sample that returns 50% cumulative probability for value 10 and we shift the
 * sample by 5, it will return 50% probability for value 15.
 */
public class ContinuousSample implements Sample {
    private float a;
    private float b;

    private ContinuousDistribution distribution;

    /**
     * Creates a new sample of the specified continuous distribution
     *
     * @param distribution Sampled distribution
     * @param a Value to add when calculating cumulative probability
     * @param b Value to multiple when calculating cumulative probability
     */
    public ContinuousSample(ContinuousDistribution distribution, float a, float b) {
        this.distribution = distribution;
        this.a = a;
        this.b = b;
    }

    /**
     * Creates a new sample of the specified continuous distribution
     *
     * @param distribution Sampled distribution
     */
    public ContinuousSample(ContinuousDistribution distribution) {
        this(distribution, 0, 1);
    }

    /**
     * Creates a new continuous sample with the same distribution, shifted by the specified amount
     * @param n Shift amount
     * @return Shifted sample
     */
    public ContinuousSample add(float n) {
        return new ContinuousSample(distribution, a + n, b);
    }

    /**
     * Creates a new continuous sample with the same distribution, scaled by the specified amount
     * @param n Scale amount
     * @return Scaled sample
     */
    public ContinuousSample multiply(float n) {
        return new ContinuousSample(distribution, a, b * n);
    }

    /**
     * Calculates cumulative probability of the specified value, after shifting and multiplying it
     * @param n Value
     * @return Cumulative probability for the value
     */
    public DiscreteSample<Boolean> cumulative(float n) {
        float p = distribution.cumulative(n * b + a);
        return new DiscreteSample<Boolean>().add(true, p).add(false, 1 - p);
    }

    public String toString() {
        return String.format("ContinuousSample{%s %f + %fx}", distribution, a, b);
    }
}
