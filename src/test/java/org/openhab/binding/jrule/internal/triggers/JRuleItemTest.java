package org.openhab.binding.jrule.internal.triggers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.openhab.automation.jrule.items.JRuleItemRegistry;
import org.openhab.core.events.Event;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.events.ItemEventFactory;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.types.StringType;
import org.openhab.core.types.UnDefType;

public class JRuleItemTest extends JRuleAbstractTest {

    @Test
    public void testSendCommand() throws ItemNotFoundException, InterruptedException {
        JRuleItemRules rule = initRule(JRuleItemRules.class);
        // Set item state in ItemRegistry
        setState(new StringItem(JRuleItemRules.TARGET_ITEM), UnDefType.UNDEF);
        JRuleItemRegistry.get(JRuleItemRules.TARGET_ITEM, TargetItem.class);
        fireEvents(List.of(itemChangeEvent(JRuleItemRules.TRIGGER_ITEM, "2", "1")));
        verify(rule, times(1)).testSendCommand();
        Thread.sleep(3000); // Wait for timer inside rule to execute
        assertTrue(eventPublisher.hasCommandEvent(JRuleItemRules.TARGET_ITEM, "command"));
        assertTrue(eventPublisher.hasCommandEvent(JRuleItemRules.TARGET_ITEM, "timedCommand"));
        assertTrue(eventPublisher.hasCommandEvent(JRuleItemRules.TARGET_ITEM, "nestedTimedCommand"));
    }

    private Event itemChangeEvent(String item, String from, String to) {
        return ItemEventFactory.createStateChangedEvent(item, new StringType(to), new StringType(from));
    }
}
