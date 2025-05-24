package com.tp.opencourse.design_pattern.stats;

import java.util.HashMap;
import java.util.Map;

public class QuarterPeriodStrategy implements PeriodStrategy {
    @Override
    public Map<Integer, String> getLabels() {
        Map<Integer, String> labels = new HashMap<>();
        for (int i = 1; i <= getRange(); i++) {
            labels.put(i, String.valueOf(i));
        }
        return labels;
    }

    @Override
    public int getRange() {
        return 4;
    }
}
