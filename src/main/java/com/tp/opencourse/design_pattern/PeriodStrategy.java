package com.tp.opencourse.design_pattern;

import java.util.Map;

public interface PeriodStrategy {
    Map<Integer, String> getLabels();
    int getRange();
}
