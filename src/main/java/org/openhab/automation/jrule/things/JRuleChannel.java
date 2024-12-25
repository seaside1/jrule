package org.openhab.automation.jrule.things;

public class JRuleChannel {
    private String channelUID;

    public JRuleChannel(String channelUID) {
        this.channelUID = channelUID;
    }

    public String getChannelUID() {
        return channelUID;
    }
}
