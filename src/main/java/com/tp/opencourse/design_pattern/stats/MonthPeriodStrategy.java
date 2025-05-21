package com.tp.opencourse.design_pattern.stats;

import java.util.HashMap;
import java.util.Map;

public class MonthPeriodStrategy implements PeriodStrategy {
    @Override
    public Map<Integer, String> getLabels() {
        return new HashMap<>() {{
            put(1, "Jan");
            put(2, "Feb");
            put(3, "Mar");
            put(4, "Apr");
            put(5, "May");
            put(6, "Jun");
            put(7, "Jul");
            put(8, "Aug");
            put(9, "Sep");
            put(10, "Oct");
            put(11, "Nov");
            put(12, "Dec");
        }};
    }

    @Override
    public int getRange() {
        return 12;
    }
}
