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

/**
 * This class contains static method used by the compiled bytecode. This way we need to use bytecode only to call these
 * methods and let the Java compiler compile the runtime code.
 */
public class Runtime {

    /**
     * Creates a new discrete sample from an Integer, Float or Boolean instance.
     * @param value Integer, Float or Boolean
     * @return Discrete sample runtime value
     */
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

    /**
     * Adds two runtime values. Represents the plus operator.
     * @param left Left operand value
     * @param right Right operand value
     * @return Added values
     */
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

    /**
     * Subtracts two runtime values. Represents the minus operator.
     * @param left Left operand value
     * @param right Right operand value
     * @return Subtracted values
     */
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

    /**
     * Multiplies two runtime values. Represents the times operator.
     * @param left Left operand value
     * @param right Right operand value
     * @return Multiplied values
     */
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

    /**
     * Divides two runtime values. Represents the over operator.
     * @param left Left operand value
     * @param right Right operand value
     * @return Divided values
     */
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

    /**
     * ANDs two runtime values. Represents the and operator.
     * @param left Left operand value
     * @param right Right operand value
     * @return ANDed values
     */
    public static RuntimeValue and(RuntimeValue left, RuntimeValue right) throws RuntimeException {
        if (isDiscreteBoolSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_BOOL_SAMPLE,
                    Operators.and(left.getDiscreteBoolSample(), right.getDiscreteBoolSample())
            );
        }

        throw new RuntimeException("AND-ing incompatible types");
    }

    /**
     * ORs two runtime values. Represents the or operator.
     * @param left Left operand value
     * @param right Right operand value
     * @return ORed values
     */
    public static RuntimeValue or(RuntimeValue left, RuntimeValue right) throws RuntimeException {
        if (isDiscreteBoolSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_BOOL_SAMPLE,
                    Operators.or(left.getDiscreteBoolSample(), right.getDiscreteBoolSample())
            );
        }

        throw new RuntimeException("OR-ing incompatible types");
    }

    /**
     * Checks if two values are equal. Represents the equals operator.
     * @param left Left operand value
     * @param right Right operand value
     * @return Boolean or float discrete sample
     */
    public static RuntimeValue equal(RuntimeValue left, RuntimeValue right) throws RuntimeException {
        if (isDiscreteIntSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_BOOL_SAMPLE,
                    Operators.equalIntegers(left.getDiscreteIntSample(), right.getDiscreteIntSample())
            );
        }

        if (isDiscreteFloatSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_BOOL_SAMPLE,
                    Operators.equalFloats(left.getDiscreteFloatSample(), right.getDiscreteFloatSample())
            );
        }

        if (isDiscreteBoolSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_BOOL_SAMPLE,
                    Operators.equalBooleans(left.getDiscreteBoolSample(), right.getDiscreteBoolSample())
            );
        }

        if ((isDiscreteFloatSample(left) && isContinuousSample(right)) ||
                (isDiscreteFloatSample(right) && isContinuousSample(left))) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_BOOL_SAMPLE,
                    new DiscreteSample<Boolean>().add(false, 1).add(true, 0)
            );
        }

        throw new RuntimeException("Comparing incompatible types");
    }

    /**
     * Checks if left operand is less than the right. Represents the less than operator.
     * @param left Left operand value
     * @param right Right operand value
     * @return Boolean or float discrete sample
     */
    public static RuntimeValue lessThan(RuntimeValue left, RuntimeValue right) throws RuntimeException {
        if (isDiscreteIntSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_BOOL_SAMPLE,
                    Operators.lessThanIntegers(left.getDiscreteIntSample(), right.getDiscreteIntSample())
            );
        }

        if (isDiscreteFloatSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_BOOL_SAMPLE,
                    Operators.lessThanFloats(left.getDiscreteFloatSample(), right.getDiscreteFloatSample())
            );
        }

        if (isDiscreteFloatSample(right) && isContinuousSample(left)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_BOOL_SAMPLE,
                    Operators.lessThanContinuous(left.getContinuousSample(), right.getDiscreteFloatSample())
            );
        }

        if (isDiscreteFloatSample(left) && isContinuousSample(right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_BOOL_SAMPLE,
                    Operators.not(Operators.lessThanContinuous(right.getContinuousSample(), left.getDiscreteFloatSample()))
            );
        }

        throw new RuntimeException("Comparing incompatible types");
    }

    /**
     * Checks if left operand is greater than the right. Represents the greater than operator.
     * @param left Left operand value
     * @param right Right operand value
     * @return Boolean or float discrete sample
     */
    public static RuntimeValue greaterThan(RuntimeValue left, RuntimeValue right) throws RuntimeException {
        if (isDiscreteIntSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_BOOL_SAMPLE,
                    Operators.greaterThanIntegers(left.getDiscreteIntSample(), right.getDiscreteIntSample())
            );
        }

        if (isDiscreteFloatSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_BOOL_SAMPLE,
                    Operators.greaterThanFloats(left.getDiscreteFloatSample(), right.getDiscreteFloatSample())
            );
        }

        if (isDiscreteFloatSample(right) && isContinuousSample(left)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_BOOL_SAMPLE,
                    Operators.not(Operators.lessThanContinuous(left.getContinuousSample(), right.getDiscreteFloatSample()))
            );
        }

        if (isDiscreteFloatSample(left) && isContinuousSample(right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_BOOL_SAMPLE,
                    Operators.lessThanContinuous(right.getContinuousSample(), left.getDiscreteFloatSample())
            );
        }

        throw new RuntimeException("Comparing incompatible types");
    }

    /**
     * Creates a Bernoulli distribution
     * @param p Probability value
     * @return Bernoulli distribution
     */
    public static RuntimeValue bernoulli(RuntimeValue p) throws RuntimeException {
        if (isDiscreteFloatSample(p) && hasSingleValue(p.getDiscreteFloatSample())) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISTRIBUTION,
                    new BernoulliDistribution(p.getDiscreteFloatSample().single())
            );
        }

        throw new RuntimeException("Unable to create Bernoulli distribution");
    }

    /**
     * Creates a Beta distribution
     * @param a Pseudocount a
     * @param b Pseudocount b
     * @return Beta distribution
     */
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

    /**
     * Creates a Normal distribution
     * @param m Mean
     * @param sd Standard deviation
     * @return Normal distribution
     */
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

    /**
     * Creates a Flip distribution
     * @param p Probability value
     * @return Flip distribution
     */
    public static RuntimeValue flip(RuntimeValue p) throws RuntimeException {
        if (isDiscreteFloatSample(p) && hasSingleValue(p.getDiscreteFloatSample())) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISTRIBUTION,
                    new FlipDistribution(p.getDiscreteFloatSample().single())
            );
        }

        throw new RuntimeException("Unable to create Flip distribution");
    }

    /**
     * Creates a sample of a distribution
     * @param value Distribution value
     * @return Discrete integer or boolean sample, or continuous sample
     */
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

    /**
     * Observes a sample. If the sample is true, returns the result value. If it is false, throws runtime exception.
     * @param sample Sample to observe
     * @param result Result if observation is true
     * @return Result value
     */
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
