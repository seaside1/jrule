/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openhab.automation.jrule.exception.JRuleItemNotFoundException;
import org.openhab.automation.jrule.internal.items.JRuleInternalPlayerItem;
import org.openhab.automation.jrule.items.metadata.JRuleItemMetadata;
import org.openhab.automation.jrule.rules.value.JRuleNextPreviousValue;
import org.openhab.automation.jrule.rules.value.JRulePlayPauseValue;
import org.openhab.automation.jrule.rules.value.JRuleRewindFastforwardValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.items.GenericItem;
import org.openhab.core.library.items.PlayerItem;

/**
 * The {@link JRulePlayerItemTest}
 *
 * @author Robert Delbrück - Initial contribution
 */
class JRulePlayerItemTest extends JRuleItemTestBase {

    @Test
    public void testSendCommand(TestInfo testInfo) {
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
        verifyEventTypes(testInfo, 0, 3);
    }

    @Test
    public void testPostUpdate(TestInfo testInfo) {
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
        verifyEventTypes(testInfo, 2, 0);
    }

    @Override
    protected JRuleItem getJRuleItem() {
        return new JRuleInternalPlayerItem(ITEM_NAME, "Label", "Type", "Id",
                Map.of("Speech", new JRuleItemMetadata("SetLightState", Map.of("location", "Livingroom"))),
                List.of("Lighting", "Inside"));
    }

    @Override
    protected JRuleValue getDefaultCommand() {
        return JRulePlayPauseValue.PLAY;
    }

    @Override
    protected GenericItem getOhItem(String name) {
        return new PlayerItem(name);
    }

    @Test
    public void testForName() {
        Assertions.assertNotNull(JRulePlayerItem.forName(ITEM_NAME));
        Assertions.assertThrows(JRuleItemNotFoundException.class, () -> JRulePlayerItem.forName(ITEM_NON_EXISTING));
        Assertions.assertTrue(JRulePlayerItem.forNameOptional(ITEM_NAME).isPresent());
        Assertions.assertFalse(JRulePlayerItem.forNameOptional(ITEM_NON_EXISTING).isPresent());
    }

    protected <T extends JRuleGroupItem<? extends JRuleItem>> T groupForNameMethod(String name) {
        return (T) JRulePlayerGroupItem.forName(name);
    }

    protected <T extends JRuleGroupItem<? extends JRuleItem>> Optional<T> groupForNameOptionalMethod(String name) {
        return (Optional<T>) JRulePlayerGroupItem.forNameOptional(name);
    }
}
