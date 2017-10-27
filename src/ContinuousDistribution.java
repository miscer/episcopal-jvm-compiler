public abstract class ContinuousDistribution implements Distribution {
    public ContinuousSample sample() {
        return new ContinuousSample(this);
    }

    abstract float cumulative(float n);
}
