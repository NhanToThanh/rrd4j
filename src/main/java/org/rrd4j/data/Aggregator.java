package org.rrd4j.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Aggregator {
    private final long timestamps[], step;
    private final double[] values;

    Aggregator(Source s) {
        this.timestamps = s.getTimestamps();
        this.values = s.getValues();
        this.step = timestamps[1] - timestamps[0];
    }

    // The calculation are always made, some micro-benchmark show no real difference
    Aggregates getAggregates(long tStart, long tEnd) {
        Aggregates agg = new Aggregates();
        long totalSeconds = 0;
        int cnt = 0;
        int lslstep = 0;
        double SUMx = 0.0;
        double SUMy = 0.0;
        double SUMxy = 0.0;
        double SUMxx = 0.0;
        double SUMyy = 0.0;
        double stdevM = 0.0;
        double stdevS = 0.0;

        for (int i = 0; i < timestamps.length; i++) {
            long left = Math.max(timestamps[i] - step, tStart);
            long right = Math.min(timestamps[i], tEnd);
            long delta = right - left;

            // delta is only > 0 when the time stamp for a given buck is within the range of tStart and tEnd
            if (delta > 0) {
                double value = values[i];

                if (!Double.isNaN(value)) {
                    totalSeconds += delta;
                    cnt++;

                    SUMx += lslstep;
                    SUMxx += lslstep * lslstep;
                    SUMy  += value;
                    SUMxy += lslstep * value;
                    SUMyy += value * value;

                    if (cnt == 1) {
                        agg.last = agg.first = agg.total = agg.min = agg.max = value;
                        stdevM = value;
                        stdevS = 0;
                    }
                    else {
                        if (delta >= step) {  // an entire bucket is included in this range
                            agg.last = value;
                        }

                        agg.min = Math.min(agg.min, value);
                        agg.max = Math.max(agg.max, value);
                        agg.total += value;

                        // See Knuth TAOCP vol 2, 3rd edition, page 232 and http://www.johndcook.com/standard_deviation.html
                        double ds = value - stdevM;                            
                        stdevM += ds/cnt;
                        stdevS += stdevS + ds*(value - stdevM);
                    }
                }
                lslstep++;
            }
        }

        if(cnt > 0) {
            agg.average = SUMy / totalSeconds;
            agg.stdev = Math.sqrt(( (cnt > 1) ? stdevS/(cnt - 1) : 0.0 ));

            /* Bestfit line by linear least squares method */
            agg.lslslope = (SUMx * SUMy - cnt * SUMxy) / (SUMx * SUMx - cnt * SUMxx);
            agg.lslint = (SUMy - agg.lslslope * SUMx) / cnt;
            agg.lslcorrel =
                    (SUMxy - (SUMx * SUMy) / cnt) /
                    Math.sqrt((SUMxx - (SUMx * SUMx) / cnt) * (SUMyy - (SUMy * SUMy) / cnt));            
        }

        return agg;
    }

    double getPercentile(long tStart, long tEnd, double percentile) {
        List<Double> valueList = new ArrayList<Double>();
        // create a list of included datasource values (different from NaN)
        for (int i = 0; i < timestamps.length; i++) {
            long left = Math.max(timestamps[i] - step, tStart);
            long right = Math.min(timestamps[i], tEnd);
            if (right > left && !Double.isNaN(values[i])) {
                valueList.add(new Double(values[i]));
            }
        }
        // create an array to work with
        int count = valueList.size();
        if (count > 1) {
            double[] valuesCopy = new double[count];
            for (int i = 0; i < count; i++) {
                valuesCopy[i] = valueList.get(i).doubleValue();
            }
            // sort array
            Arrays.sort(valuesCopy);
            // skip top (100% - percentile) values
            double topPercentile = (100.0 - percentile) / 100.0;
            count -= (int) Math.ceil(count * topPercentile);
            // if we have anything left...
            if (count > 0) {
                return valuesCopy[count - 1];
            }
        }
        // not enough data available
        return Double.NaN;
    }
}
