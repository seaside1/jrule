package org.openhab.binding.jrule.internal.triggers.itemchange;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openhab.automation.jrule.rules.event.JRuleEvent;
import org.openhab.binding.jrule.internal.triggers.JRuleAbstractTest;
import org.openhab.core.events.Event;
import org.openhab.core.items.events.ItemEventFactory;
import org.openhab.core.library.types.StringType;

public class JRuleItemChangeTest extends JRuleAbstractTest {

    @Test
    public void testItemChange_no_from_to() {
        JRuleItemChangeRules rule = initRule(new JRuleItemChangeRules());
        // Only last event should trigger rule method
        fireEvents(
                List.of(itemChangeEvent("other_item", "2", "1"), itemChangeEvent(JRuleItemChangeRules.ITEM, "2", "1")));
        verify(rule, times(1)).itemChange(Mockito.any(JRuleEvent.class));
    }

    @Test
    public void testItemChange_from() {
        JRuleItemChangeRules rule = initRule(new JRuleItemChangeRules());
        // Only last event should trigger rule method
        fireEvents(List.of(itemChangeEvent(JRuleItemChangeRules.ITEM_FROM, "2", "1"),
                itemChangeEvent(JRuleItemChangeRules.ITEM_FROM, "1", "2")));
        verify(rule, times(1)).itemChangeFrom(Mockito.any(JRuleEvent.class));
    }

    @Test
    public void testItemChange_to() {
        JRuleItemChangeRules rule = initRule(new JRuleItemChangeRules());
        // Only last event should trigger rule method
        fireEvents(List.of(itemChangeEvent(JRuleItemChangeRules.ITEM_TO, "1", "2"),
                itemChangeEvent(JRuleItemChangeRules.ITEM_TO, "2", "1")));
        verify(rule, times(1)).itemChangeTo(Mockito.any(JRuleEvent.class));
    }

    @Test
    public void testItemChange_from_to() {
        JRuleItemChangeRules rule = initRule(new JRuleItemChangeRules());
        // Only last event should trigger rule method
        fireEvents(List.of(itemChangeEvent(JRuleItemChangeRules.ITEM_FROM_TO, "2", "1"),
                itemChangeEvent(JRuleItemChangeRules.ITEM_FROM_TO, "3", "2"),
                itemChangeEvent(JRuleItemChangeRules.ITEM_FROM_TO, "1", "2")));
        verify(rule, times(1)).itemChangeFromTo(Mockito.any(JRuleEvent.class));
    }

    // Syntactic sugar
    private Event itemChangeEvent(String item, String from, String to) {
        return ItemEventFactory.createStateChangedEvent(item, new StringType(to), new StringType(from));
    }
}
