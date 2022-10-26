# Migrate JRule to JRuleXXX

## Triggers

### Item changed to ON
<code>@JRuleWhen(item = _MyTestSwitch.ITEM, trigger = _MyTestSwitch.TRIGGER_CHANGED_TO_ON)</code>

->

<code>@JRuleWhenItemChange(item = _MyTestSwitch.ITEM, to = JRuleSwitchItem.ON)</code>

### Item changed from OFF to ON
<code>@JRuleWhen(item = _MyTestSwitch2.ITEM, trigger = _MyTestSwitch2.TRIGGER_CHANGED_FROM_OFF_TO_ON)</code>

-> 

<code>@JRuleWhenItemChange(item = _MyTestSwitch2.ITEM, from = JRuleSwitchItem.OFF, to = JRuleSwitchItem.ON)</code>

### Item received command
<code>@JRuleWhen(item = __MyTestSwitch2.ITEM, trigger = __MyTestSwitch2.TRIGGER_RECEIVED_COMMAND)</code>

->

<code>@JRuleWhenItemReceivedCommand(item = _MyTestSwitch2.ITEM)</code>

### Item changed with conditional check
<code>@JRuleWhen(item = _MyTemperatureSensor.ITEM, trigger = _MyTemperatureSensor.TRIGGER_RECEIVED_UPDATE, lte = 20)</code>

-> 

<code>@JRuleWhenItemChange(item = _MyTemperatureSensor.ITEM, condition = @JRuleCondition(lte = 20))</code>

### Channel changed
<code>@JRuleWhen(channel = "binding:thing:buttonevent")</code>

->

<code>@JRuleWhenChannelTrigger(channel = "mqtt:topic:mqtt:generic:numberTrigger")</code>

### Cron
<code>@JRuleWhen(cron = "4 * * * * *")</code>

->

<code>@JRuleWhenCronTrigger(cron = "*/5 * * * * *")</code>

### Time
<code>@JRuleWhen(hours = 3)</code>

->

<code>@JRuleWhenTimeTrigger(hours=3)</code>

### Thing changed
<code>@JRuleWhen(thing = "*", trigger = JRuleThingStatusTrigger.TRIGGER_CHANGED, from = "ONLINE")</code>

-> 

<code>@JRuleWhenThingTrigger(from = JRuleThingStatus.ONLINE)</code>

## Precondition

### Item must be equal to
<code>@JRulePrecondition(item=_MyTestDisturbanceSwitch.ITEM, eq = "ON")</code>

-> 

<code>@JRulePrecondition(item = _MyTestDisturbanceSwitch.ITEM, condition = @JRuleCondition(eq = "ON"))</code>

## JRuleEvent
JRuleEvent is now abstract with 4 concrete implementations
- JRuleItemEvent
- JRuleChannelEvent
- JRuleTimerEvent
- JRuleThingEvent
Depending on the trigger that was fired, the respective event will be thrown.
You can use the abstract JRuleEvent as method parameter or a concrete one, but take care to be sure that just this event can be thrown.
Otherwise an exception will occur.

### Examples
#### Correct
Trigger for an Item will inject an JRuleItemEvent as method parameter
For the method you can use JRuleEvent (has to be cast to get the interesting information) or JRuleItemEvent

#### Incorrect
- Trigger for a Thing and use JRuleItemEvent as method parameter -> Exception
- Trigger for an Item and a Cron and use JRuleItemEvent -> Exception when the method will be trigger for cron