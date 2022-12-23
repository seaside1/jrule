/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.automation.jrule.items;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openhab.automation.jrule.internal.items.JRuleInternalPlayerItem;
import org.openhab.automation.jrule.rules.value.*;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.PlayerItem;

/**
 * The {@link JRulePlayerItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRulePlayerItemTest extends JRuleItemTestBase {

    @Test
    public void testSendCommand() {
        JRulePlayerItem item = (JRulePlayerItem) getJRuleItem();
        item.sendCommand(JRulePlayPauseValue.PLAY);

        // play/pause
        Assertions.assertEquals(JRulePlayPauseValue.PLAY, item.getStateAsPlayPause());
        Assertions.assertEquals(JRulePlayPauseValue.PLAY, item.getStateAs(JRulePlayPauseValue.class));

        // send fastforward
        item.sendCommand(JRuleRewindFastforwardValue.FASTFORWARD);
        Assertions.assertEquals(JRuleRewindFastforwardValue.FASTFORWARD,
                item.getStateAs(JRuleRewindFastforwardValue.class));

        // send next
        item.sendCommand(JRuleNextPreviousValue.NEXT);
        Assertions.assertEquals(JRuleRewindFastforwardValue.FASTFORWARD, item.getStateAsRewindFastforward());

        // verify event calls
        verifyEventTypes(0, 3);
    }

    @Test
    public void testPostUpdate() {
        JRulePlayerItem item = (JRulePlayerItem) getJRuleItem();
        item.postUpdate(JRulePlayPauseValue.PLAY);

        // play/pause
        Assertions.assertEquals(JRulePlayPauseValue.PLAY, item.getStateAsPlayPause());
        Assertions.assertEquals(JRulePlayPauseValue.PLAY, item.getStateAs(JRulePlayPauseValue.class));

        // send fastforward
        item.postUpdate(JRuleRewindFastforwardValue.FASTFORWARD);
        Assertions.assertEquals(JRuleRewindFastforwardValue.FASTFORWARD,
                item.getStateAs(JRuleRewindFastforwardValue.class));

        // verify event calls
        verifyEventTypes(2, 0);
    }

    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalPlayerItem("Name", "Label", "Type", "Id");
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return JRulePlayPauseValue.PLAY;
    }

    @Override
    protected GenericItem getOhItem() {
        return new PlayerItem("Name");
    }
}
