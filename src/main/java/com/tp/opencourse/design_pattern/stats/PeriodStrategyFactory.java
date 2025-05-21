package com.tp.opencourse.design_pattern.stats;

public class PeriodStrategyFactory {
    public static PeriodStrategy getStrategy(String roleType) {
        return switch (roleType) {
            // Add more cases like "Quarter" etc.
            case "STUDENT" -> new QuarterPeriodStrategy();
            default -> new MonthPeriodStrategy();
        };
    }
}
