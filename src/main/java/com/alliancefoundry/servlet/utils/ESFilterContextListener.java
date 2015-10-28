package com.alliancefoundry.servlet.utils;

import com.codahale.metrics.servlet.InstrumentedFilterContextListener;
import com.codahale.metrics.MetricRegistry;

/**
 * Created by: Paul Bernard
 */
public class ESFilterContextListener extends InstrumentedFilterContextListener {

    public static final MetricRegistry REGISTRY = new MetricRegistry();

    @Override
    protected MetricRegistry getMetricRegistry() {
        return REGISTRY;
    }

}