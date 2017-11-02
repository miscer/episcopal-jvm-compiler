package episcopal.discrete;

import episcopal.Distribution;

/**
 * Bernoulli distribution
 *
 * Discrete distribution generating 1 and 0 with the specified probability
 */
public class BernoulliDistribution implements Distribution {
    private float p;

    /**
     * @param p Probability of generating 1
     */
    public BernoulliDistribution(float p) {
        this.p = p;
    }

    /**
     * Creates a discrete sample containing 1 with probability p and 0 with 1 - p
     * @return Sample of the distribution
     */
    @Override
    public DiscreteSample<Integer> sample() {
        DiscreteSample<Integer> result = new DiscreteSample<>();
        result.add(1, p);
        result.add(0, 1 - p);
        return result;
    }
}
