package com.tp.opencourse.design_pattern;

public class PeriodStrategyFactory {

    public static PeriodStrategy getStrategy(String periodType) {
        return switch (periodType) {
            // Add more cases like "Quarter" etc.
            case "Quarter" -> new QuarterPeriodStrategy();
            default -> new MonthPeriodStrategy();
        };
    }
}
