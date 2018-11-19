package org.rrd4j.backends;

import org.rrd4j.core.RrdException;

/**
 * Wrap a exception generated by the backend store
 * @author Fabrice Bacchella
 *
 */
public class RrdBackendException extends RrdException {

    public RrdBackendException(String message) {
        super(message);
    }

    public RrdBackendException(String message, Throwable cause) {
        super(message, cause);
    }

}