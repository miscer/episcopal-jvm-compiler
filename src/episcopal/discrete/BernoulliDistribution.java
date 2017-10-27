package episcopal.discrete;

import episcopal.Distribution;

public class BernoulliDistribution implements Distribution {
    private float p;

    public BernoulliDistribution(float p) {
        this.p = p;
    }

    @Override
    public DiscreteSample<Integer> sample() {
        DiscreteSample<Integer> result = new DiscreteSample<>();
        result.add(1, p);
        result.add(0, 1 - p);
        return result;
    }
}
