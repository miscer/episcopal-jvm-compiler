package episcopal.discrete;

import episcopal.Distribution;
import episcopal.discrete.DiscreteSample;

public class FlipDistribution implements Distribution {
    private float p;

    public FlipDistribution(float p) {
        this.p = p;
    }

    @Override
    public DiscreteSample<Boolean> sample() {
        DiscreteSample<Boolean> result = new DiscreteSample<>();
        result.add(true, p);
        result.add(false, 1 - p);
        return result;
    }
}
