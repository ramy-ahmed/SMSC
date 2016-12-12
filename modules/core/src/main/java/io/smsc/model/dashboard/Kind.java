package io.smsc.model.dashboard;

import javax.persistence.Embeddable;

@Embeddable
public enum Kind {
    PIE_CHART,
    SERIAL_CHART,
    LINE_CHART,
    BAR_CHART,
    BUBBLE_CHART,
    FEEDBACK_STATUS,
    PROFIT_STATUS,
    ORDERS_STATUS,
    USERS_STATUS,

}
