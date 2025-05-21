package com.tp.opencourse.design_pattern.login;

import com.tp.opencourse.design_pattern.stats.MonthPeriodStrategy;
import com.tp.opencourse.design_pattern.stats.PeriodStrategy;
import com.tp.opencourse.design_pattern.stats.QuarterPeriodStrategy;
import org.springframework.stereotype.Component;

public class PermissionStrategyFactory {
    public static PermissionCheckingStrategy getStrategy(String roleType) {
        return switch (roleType) {
            // Add more cases like "Quarter" etc.
            case "TEACHER" -> new TeacherPermissionStrategy();
            default -> new StudentPermissionStrategy();
        };
    }
}
