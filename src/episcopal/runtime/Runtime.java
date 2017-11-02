package episcopal.runtime;

import episcopal.Distribution;
import episcopal.Observations;
import episcopal.Operators;
import episcopal.continuous.BetaDistribution;
import episcopal.continuous.ContinuousDistribution;
import episcopal.continuous.NormalDistribution;
import episcopal.discrete.BernoulliDistribution;
import episcopal.discrete.DiscreteSample;
import episcopal.discrete.FlipDistribution;

public class Runtime {

    public static RuntimeValue constant(Object value) throws RuntimeException {
        if (value instanceof Integer) {
            return new RuntimeValue(RuntimeValue.Type.DISCRETE_INT_SAMPLE, DiscreteSample.create(value));
        }

        if (value instanceof Float) {
            return new RuntimeValue(RuntimeValue.Type.DISCRETE_FLOAT_SAMPLE, DiscreteSample.create(value));
        }

        if (value instanceof Boolean) {
            return new RuntimeValue(RuntimeValue.Type.DISCRETE_BOOL_SAMPLE, DiscreteSample.create(value));
        }

        throw new RuntimeException("Incompatible constant");
    }

    public static RuntimeValue add(RuntimeValue left, RuntimeValue right) throws RuntimeException {
        if (isDiscreteIntSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_INT_SAMPLE,
                    Operators.addIntegers(left.getDiscreteIntSample(), right.getDiscreteIntSample())
            );
        }

        if (isDiscreteFloatSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_FLOAT_SAMPLE,
                    Operators.addFloats(left.getDiscreteFloatSample(), right.getDiscreteFloatSample())
            );
        }

        if (isDiscreteFloatSample(left) && hasSingleValue(left.getDiscreteFloatSample()) && isContinuousSample(right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.CONTINUOUS_SAMPLE,
                    right.getContinuousSample().add(left.getDiscreteFloatSample().single())
            );
        }

        if (isDiscreteFloatSample(right) && hasSingleValue(right.getDiscreteFloatSample()) && isContinuousSample(left)) {
            return new RuntimeValue(
                    RuntimeValue.Type.CONTINUOUS_SAMPLE,
                    left.getContinuousSample().add(right.getDiscreteFloatSample().single())
            );
        }

        throw new RuntimeException("Adding incompatible types");
    }

    public static RuntimeValue subtract(RuntimeValue left, RuntimeValue right) throws RuntimeException {
        if (isDiscreteIntSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_INT_SAMPLE,
                    Operators.subtractIntegers(left.getDiscreteIntSample(), right.getDiscreteIntSample())
            );
        }

        if (isDiscreteFloatSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_FLOAT_SAMPLE,
                    Operators.subtractFloats(left.getDiscreteFloatSample(), right.getDiscreteFloatSample())
            );
        }

        if (isDiscreteFloatSample(left) && hasSingleValue(left.getDiscreteFloatSample()) && isContinuousSample(right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.CONTINUOUS_SAMPLE,
                    right.getContinuousSample().add(-1 * left.getDiscreteFloatSample().single())
            );
        }

        if (isDiscreteFloatSample(right) && hasSingleValue(right.getDiscreteFloatSample()) && isContinuousSample(left)) {
            return new RuntimeValue(
                    RuntimeValue.Type.CONTINUOUS_SAMPLE,
                    left.getContinuousSample().add(-1 * right.getDiscreteFloatSample().single())
            );
        }

        throw new RuntimeException("Subtracting incompatible types");
    }

    public static RuntimeValue multiply(RuntimeValue left, RuntimeValue right) throws RuntimeException {
        if (isDiscreteIntSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_INT_SAMPLE,
                    Operators.multiplyIntegers(left.getDiscreteIntSample(), right.getDiscreteIntSample())
            );
        }

        if (isDiscreteFloatSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_FLOAT_SAMPLE,
                    Operators.multiplyFloats(left.getDiscreteFloatSample(), right.getDiscreteFloatSample())
            );
        }

        if (isDiscreteFloatSample(left) && hasSingleValue(left.getDiscreteFloatSample()) && isContinuousSample(right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.CONTINUOUS_SAMPLE,
                    right.getContinuousSample().multiply(left.getDiscreteFloatSample().single())
            );
        }

        if (isDiscreteFloatSample(right) && hasSingleValue(right.getDiscreteFloatSample()) && isContinuousSample(left)) {
            return new RuntimeValue(
                    RuntimeValue.Type.CONTINUOUS_SAMPLE,
                    left.getContinuousSample().multiply(right.getDiscreteFloatSample().single())
            );
        }

        throw new RuntimeException("Multiplying incompatible types");
    }

    public static RuntimeValue divide(RuntimeValue left, RuntimeValue right) throws RuntimeException {
        if (isDiscreteIntSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_INT_SAMPLE,
                    Operators.divideIntegers(left.getDiscreteIntSample(), right.getDiscreteIntSample())
            );
        }

        if (isDiscreteFloatSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_FLOAT_SAMPLE,
                    Operators.divideFloats(left.getDiscreteFloatSample(), right.getDiscreteFloatSample())
            );
        }

        if (isDiscreteFloatSample(left) && hasSingleValue(left.getDiscreteFloatSample()) && isContinuousSample(right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.CONTINUOUS_SAMPLE,
                    right.getContinuousSample().multiply(1 / left.getDiscreteFloatSample().single())
            );
        }

        if (isDiscreteFloatSample(right) && hasSingleValue(right.getDiscreteFloatSample()) && isContinuousSample(left)) {
            return new RuntimeValue(
                    RuntimeValue.Type.CONTINUOUS_SAMPLE,
                    left.getContinuousSample().multiply(1 / right.getDiscreteFloatSample().single())
            );
        }

        throw new RuntimeException("Dividing incompatible types");
    }

    public static RuntimeValue and(RuntimeValue left, RuntimeValue right) throws RuntimeException {
        if (isDiscreteBoolSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_BOOL_SAMPLE,
                    Operators.and(left.getDiscreteBoolSample(), right.getDiscreteBoolSample())
            );
        }

        throw new RuntimeException("AND-ing incompatible types");
    }

    public static RuntimeValue or(RuntimeValue left, RuntimeValue right) throws RuntimeException {
        if (isDiscreteBoolSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_BOOL_SAMPLE,
                    Operators.or(left.getDiscreteBoolSample(), right.getDiscreteBoolSample())
            );
        }

        throw new RuntimeException("OR-ing incompatible types");
    }

    public static RuntimeValue equal(RuntimeValue left, RuntimeValue right) throws RuntimeException {
        if (isDiscreteIntSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_INT_SAMPLE,
                    Operators.equalIntegers(left.getDiscreteIntSample(), right.getDiscreteIntSample())
            );
        }

        if (isDiscreteFloatSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_FLOAT_SAMPLE,
                    Operators.equalFloats(left.getDiscreteFloatSample(), right.getDiscreteFloatSample())
            );
        }

        if (isDiscreteBoolSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_BOOL_SAMPLE,
                    Operators.equalBooleans(left.getDiscreteBoolSample(), right.getDiscreteBoolSample())
            );
        }

        if ((isDiscreteFloatSample(left) && hasSingleValue(left.getDiscreteFloatSample()) && isContinuousSample(right)) ||
                (isDiscreteFloatSample(right) && hasSingleValue(right.getDiscreteFloatSample()) && isContinuousSample(left))) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_FLOAT_SAMPLE,
                    DiscreteSample.create(0f)
            );
        }

        throw new RuntimeException("Comparing incompatible types");
    }

    public static RuntimeValue lessThan(RuntimeValue left, RuntimeValue right) throws RuntimeException {
        if (isDiscreteIntSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_INT_SAMPLE,
                    Operators.lessThanIntegers(left.getDiscreteIntSample(), right.getDiscreteIntSample())
            );
        }

        if (isDiscreteFloatSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_FLOAT_SAMPLE,
                    Operators.lessThanFloats(left.getDiscreteFloatSample(), right.getDiscreteFloatSample())
            );
        }

        if (isDiscreteFloatSample(right) && hasSingleValue(right.getDiscreteFloatSample()) && isContinuousSample(left)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_BOOL_SAMPLE,
                    Operators.lessThanContinuous(left.getContinuousSample(), right.getDiscreteFloatSample())
            );
        }

        if (isDiscreteFloatSample(left) && hasSingleValue(left.getDiscreteFloatSample()) && isContinuousSample(right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_BOOL_SAMPLE,
                    Operators.not(Operators.lessThanContinuous(left.getContinuousSample(), right.getDiscreteFloatSample()))
            );
        }

        throw new RuntimeException("Comparing incompatible types");
    }

    public static RuntimeValue greaterThan(RuntimeValue left, RuntimeValue right) throws RuntimeException {
        if (isDiscreteIntSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_INT_SAMPLE,
                    Operators.greaterThanIntegers(left.getDiscreteIntSample(), right.getDiscreteIntSample())
            );
        }

        if (isDiscreteFloatSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_FLOAT_SAMPLE,
                    Operators.greaterThanFloats(left.getDiscreteFloatSample(), right.getDiscreteFloatSample())
            );
        }

        if (isDiscreteFloatSample(right) && hasSingleValue(right.getDiscreteFloatSample()) && isContinuousSample(left)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_BOOL_SAMPLE,
                    Operators.not(Operators.lessThanContinuous(left.getContinuousSample(), right.getDiscreteFloatSample()))
            );
        }

        if (isDiscreteFloatSample(left) && hasSingleValue(left.getDiscreteFloatSample()) && isContinuousSample(right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_BOOL_SAMPLE,
                    Operators.lessThanContinuous(left.getContinuousSample(), right.getDiscreteFloatSample())
            );
        }

        throw new RuntimeException("Comparing incompatible types");
    }

    public static RuntimeValue bernoulli(RuntimeValue p) throws RuntimeException {
        if (isDiscreteFloatSample(p) && hasSingleValue(p.getDiscreteFloatSample())) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISTRIBUTION,
                    new BernoulliDistribution(p.getDiscreteFloatSample().single())
            );
        }

        throw new RuntimeException("Unable to create Bernoulli distribution");
    }

    public static RuntimeValue beta(RuntimeValue a, RuntimeValue b) throws RuntimeException {
        if (isDiscreteFloatSample(a) && hasSingleValue(a.getDiscreteFloatSample()) &&
                isDiscreteFloatSample(b) && hasSingleValue(b.getDiscreteFloatSample())) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISTRIBUTION,
                    new BetaDistribution(a.getDiscreteFloatSample().single(), b.getDiscreteFloatSample().single())
            );
        }

        throw new RuntimeException("Unable to create Beta distribution");
    }

    public static RuntimeValue normal(RuntimeValue m, RuntimeValue sd) throws RuntimeException {
        if (isDiscreteFloatSample(m) && hasSingleValue(m.getDiscreteFloatSample()) &&
                isDiscreteFloatSample(sd) && hasSingleValue(sd.getDiscreteFloatSample())) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISTRIBUTION,
                    new NormalDistribution(m.getDiscreteFloatSample().single(), sd.getDiscreteFloatSample().single())
            );
        }

        throw new RuntimeException("Unable to create Normal distribution");
    }

    public static RuntimeValue flip(RuntimeValue p) throws RuntimeException {
        if (isDiscreteFloatSample(p) && hasSingleValue(p.getDiscreteFloatSample())) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISTRIBUTION,
                    new FlipDistribution(p.getDiscreteFloatSample().single())
            );
        }

        throw new RuntimeException("Unable to create Flip distribution");
    }

    public static RuntimeValue sample(RuntimeValue value) throws RuntimeException {
        if (isDistribution(value)) {
            Distribution distribution = value.getDistribution();

            if (distribution instanceof BernoulliDistribution) {
                return new RuntimeValue(
                        RuntimeValue.Type.DISCRETE_INT_SAMPLE,
                        distribution.sample()
                );
            }

            if (distribution instanceof FlipDistribution) {
                return new RuntimeValue(
                        RuntimeValue.Type.DISCRETE_BOOL_SAMPLE,
                        distribution.sample()
                );
            }

            if (distribution instanceof ContinuousDistribution) {
                return new RuntimeValue(
                        RuntimeValue.Type.CONTINUOUS_SAMPLE,
                        distribution.sample()
                );
            }
        }

        throw new RuntimeException("Unable to sample value");
    }

    public static RuntimeValue observe(RuntimeValue sample, RuntimeValue result) throws RuntimeException {
        System.out.printf("%s %s\n", sample.getType(), result.getType());

        if (isDiscreteBoolSample(sample)) {
            Boolean value = Observations.select(sample.getDiscreteBoolSample());

            if (value) {
                return result;
            } else {
                throw new RuntimeException("Observation failed");
            }
        }

        throw new RuntimeException("Unable to observe value");
    }

    private static boolean isDiscreteIntSample(RuntimeValue value) {
        return value.getType() == RuntimeValue.Type.DISCRETE_INT_SAMPLE;
    }

    private static boolean isDiscreteFloatSample(RuntimeValue value) {
        return value.getType() == RuntimeValue.Type.DISCRETE_FLOAT_SAMPLE;
    }

    private static boolean isDiscreteBoolSample(RuntimeValue value) {
        return value.getType() == RuntimeValue.Type.DISCRETE_BOOL_SAMPLE;
    }

    private static boolean isContinuousSample(RuntimeValue value) {
        return value.getType() == RuntimeValue.Type.CONTINUOUS_SAMPLE;
    }

    private static boolean isDistribution(RuntimeValue value) {
        return value.getType() == RuntimeValue.Type.DISTRIBUTION;
    }

    private static boolean isDiscreteIntSamples(RuntimeValue left, RuntimeValue right) {
        return isDiscreteIntSample(left) && isDiscreteIntSample(right);
    }

    private static boolean isDiscreteFloatSamples(RuntimeValue left, RuntimeValue right) {
        return isDiscreteFloatSample(left) && isDiscreteFloatSample(right);
    }

    private static boolean isDiscreteBoolSamples(RuntimeValue left, RuntimeValue right) {
        return isDiscreteBoolSample(left) && isDiscreteBoolSample(right);
    }

    private static boolean hasSingleValue(DiscreteSample<?> sample) {
        return sample.single() != null;
    }

}
