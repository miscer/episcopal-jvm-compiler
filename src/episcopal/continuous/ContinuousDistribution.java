package episcopal.continuous;

import episcopal.Distribution;

/**
 * Continuous distribution does not contain discrete values as discrete distributions do. We can however calculate the
 * cumulative probability, i.e. given a value, what is the probability of a value less than this one occurring?
 *
 * Sampling a continuous distribution generates a continuous sample, which is then able to use the distribution to
 * calculate cumulative probability.
 *
 * Continuous distributions can generate only floating point values.
 */
public abstract class ContinuousDistribution implements Distribution {
    /**
     * @return Continuous sample of the distribution
     */
    public ContinuousSample sample() {
        return new ContinuousSample(this);
    }

    /**
     * Calculates the cumulative probability of the specified value
     * @param n Value
     * @return Cumulative probability
     */
    abstract public float cumulative(float n);
}
