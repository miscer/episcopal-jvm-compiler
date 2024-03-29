package episcopal.runtime;

import episcopal.Distribution;
import episcopal.continuous.ContinuousSample;
import episcopal.discrete.DiscreteSample;

/**
 * Represents a runtime value. It can contain a discrete or continuous sample, or a distribution. A separate class is
 * needed because we cannot use the instanceof operator to check the type of a discrete distribution, because of Java's
 * type erasure.
 */
public class RuntimeValue {
    public enum Type {
        DISCRETE_BOOL_SAMPLE,
        DISCRETE_INT_SAMPLE,
        DISCRETE_FLOAT_SAMPLE,
        CONTINUOUS_SAMPLE,
        DISTRIBUTION,
    };

    private Type type;
    private Object value;

    public RuntimeValue(Type type, Object value) {
        this.type = type;
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public DiscreteSample<Boolean> getDiscreteBoolSample() {
        return (DiscreteSample<Boolean>) value;
    }

    public DiscreteSample<Integer> getDiscreteIntSample() {
        return (DiscreteSample<Integer>) value;
    }

    public DiscreteSample<Float> getDiscreteFloatSample() {
        return (DiscreteSample<Float>) value;
    }

    public ContinuousSample getContinuousSample() {
        return (ContinuousSample) value;
    }

    public Distribution getDistribution() {
        return (Distribution) value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
