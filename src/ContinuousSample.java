public class ContinuousSample implements Sample {
    private float a;
    private float b;

    private ContinuousDistribution distribution;

    public ContinuousSample(ContinuousDistribution distribution, float a, float b) {
        this.distribution = distribution;
        this.a = a;
        this.b = b;
    }

    public ContinuousSample(ContinuousDistribution distribution) {
        this(distribution, 0, 1);
    }

    public ContinuousSample add(float n) {
        return new ContinuousSample(distribution, a + n, b);
    }

    public ContinuousSample multiply(float n) {
        return new ContinuousSample(distribution, a, b * n);
    }

    public DiscreteSample<Boolean> cumulative(float n) {
        float p = distribution.cumulative(n * b + a);
        return new DiscreteSample<Boolean>().add(true, p).add(false, 1 - p);
    }

    public String toString() {
        return String.format("ContinuousSample{%s %f + %fx}", distribution, a, b);
    }
}
