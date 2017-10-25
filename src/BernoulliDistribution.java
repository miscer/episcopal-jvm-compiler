public class BernoulliDistribution implements Distribution {
    private float p;

    public BernoulliDistribution(float p) {
        this.p = p;
    }

    @Override
    public Result<Integer> sample() {
        Result<Integer> result = new Result<>();
        result.add(1, p);
        result.add(0, 1 - p);
        return result;
    }
}
