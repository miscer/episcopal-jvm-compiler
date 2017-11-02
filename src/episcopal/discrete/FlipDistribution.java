package episcopal.discrete;

import episcopal.Distribution;

/**
 * Flip distribution
 *
 * Discrete distribution generating true and false with the specified probability
 */
public class FlipDistribution implements Distribution {
    private float p;

    /**
     * @param p Probability of generating true
     */
    public FlipDistribution(float p) {
        this.p = p;
    }

    /**
     * Creates a discrete sample containing true with probability p and false with 1 - p
     * @return Sample of the distribution
     */
    @Override
    public DiscreteSample<Boolean> sample() {
        DiscreteSample<Boolean> result = new DiscreteSample<>();
        result.add(true, p);
        result.add(false, 1 - p);
        return result;
    }
}
