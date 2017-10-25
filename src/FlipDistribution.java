public class FlipDistribution implements Distribution {
    private float p;

    public FlipDistribution(float p) {
        this.p = p;
    }

    @Override
    public Result<Boolean> sample() {
        Result<Boolean> result = new Result<>();
        result.add(true, p);
        result.add(false, 1 - p);
        return result;
    }
}
