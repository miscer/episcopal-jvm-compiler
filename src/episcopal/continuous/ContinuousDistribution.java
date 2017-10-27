package episcopal.continuous;

import episcopal.Distribution;

public abstract class ContinuousDistribution implements Distribution {
    public ContinuousSample sample() {
        return new ContinuousSample(this);
    }

    abstract public float cumulative(float n);
}
