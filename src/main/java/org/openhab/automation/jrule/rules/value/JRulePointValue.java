package org.openhab.automation.jrule.rules.value;

import java.math.BigDecimal;

public class JRulePointValue implements JRuleValue {
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final BigDecimal altitude;

    public JRulePointValue(BigDecimal latitude, BigDecimal longitude, BigDecimal altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public JRulePointValue(String toFullString) {

    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public BigDecimal getAltitude() {
        return altitude;
    }
}
