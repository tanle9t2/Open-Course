package com.tp.opencourse.dto.event;

import java.util.Date;
import java.util.UUID;

public interface Event {
    UUID getEventId();
    Date getDate();
}
