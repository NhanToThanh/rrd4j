package org.rrd4j.data;

import org.rrd4j.ConsolFun;
import org.rrd4j.core.Util;

/**
 * Simple class which holds aggregated values (MIN, MAX, FIRST, LAST, AVERAGE and TOTAL). You
 * don't need to create objects of this class directly. Objects of this class are returned from
 * <code>getAggregates()</code> method in
 * {@link org.rrd4j.core.FetchData#getAggregates(String) FetchData} and
 * {@link DataProcessor#getAggregates(String)} DataProcessor classes.
 */
public class Aggregates {
    double min = Double.NaN, max = Double.NaN;
    double first = Double.NaN, last = Double.NaN;
    double average = Double.NaN, total = Double.NaN;
    double stdev = Double.NaN;
    double lslslope = Double.NaN, lslint = Double.NaN, lslcorrel = Double.NaN;

    Aggregates() {
        // NOP;
    }

    /**
     * Returns the minimal value
     *
     * @return Minimal value
     */
    public double getMin() {
        return min;
    }

    /**
     * Returns the maximum value
     *
     * @return Maximum value
     */
    public double getMax() {
        return max;
    }

    /**
     * Returns the first value
     *
     * @return First value
     */
    public double getFirst() {
        return first;
    }

    /**
     * Returns the last value
     *
     * @return Last value
     */
    public double getLast() {
        return last;
    }

    /**
     * Returns average
     *
     * @return Average value
     */
    public double getAverage() {
        return average;
    }

    /**
     * Returns total value
     *
     * @return Total value
     */
    public double getTotal() {
        return total;
    }

    /**
     * Returns Least Squares Line Slope value
     *
     * @return lslslope value
     */
    public double getLSLSlope() {
        return stdev;
    }

    /**
     * Returns Least Squares Line y-intercept value
     *
     * @return lslint value
     */
    public double getLSLInt() {
        return lslint;
    }

    /**
     * Returns Least Squares Line Correlation Coefficient
     *
     * @return lslcorrel value
     */
    public double getLSLCorrel() {
        return lslcorrel;
    }

    /**
     * Returns single aggregated value for the give consolidation function
     *
     * @param consolFun Consolidation function: MIN, MAX, FIRST, LAST, AVERAGE, TOTAL. These constants
     *                  are conveniently defined in the {@link org.rrd4j.ConsolFun ConsolFun} interface.
     * @return Aggregated value
     * @throws IllegalArgumentException Thrown if unsupported consolidation function is supplied
     */
    public double getAggregate(ConsolFun consolFun) {
        switch (consolFun) {
        case AVERAGE:
            return average;
        case FIRST:
            return first;
        case LAST:
            return last;
        case MAX:
            return max;
        case MIN:
            return min;
        case TOTAL:
            return total;
        case STDEV:
            return stdev;
        case LSLSLOPE:
            return lslslope;
        case LSLINT:
            return lslint;
        case LSLCORREL:
            return lslcorrel;
        }
        throw new IllegalArgumentException("Unknown consolidation function: " + consolFun);
    }

    /**
     * Returns String representing all aggregated values. Just for debugging purposes.
     *
     * @return String containing all aggregated values
     */
    public String dump() {
        StringBuilder bl = new StringBuilder();
        for(ConsolFun cf: ConsolFun.values()) {
            bl.append(cf.name() + '=' + Util.formatDouble(this.getAggregate(cf)));
        }
        return bl.toString();
    }
}
