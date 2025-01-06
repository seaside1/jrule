## BETA21
- Fixed MDC 'tag' rule by [querdenker2k](https://github.com/querdenker2k)  PR https://github.com/seaside1/jrule/pull/198
- Allow byte[] for http request by [querdenker2k](https://github.com/querdenker2k)  PR https://github.com/seaside1/jrule/pull/196 
- Dynamic rule builder by [rsopp](https://github.com/rsopp) PR https://github.com/seaside1/jrule/pull/195
- Added more methods (Dynamic rules) by [seaside1](https://github.com/seaside1) PR https://github.com/seaside1/jrule/pull/193

## BETA20
- Quantity item should extend number item by [querdenker2k](https://github.com/querdenker2k)  PR https://github.com/seaside1/jrule/pull/189
- InvocationTargetException the get stacktrace has changed by [querdenker2k](https://github.com/querdenker2k)  PR https://github.com/seaside1/jrule/pull/188  
- Add option to add metadata by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/187
- Do not use logprefix when empty by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/185
- MethodNotFound fix for primitive action parameters by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/184

## BETA19
-  Dynamic Rules by [seaside1](https://github.com/seaside1) PR https://github.com/seaside1/jrule/pull/177
-  Add persist by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/174
-  Updated Readme.md by [seime](https://github.com/seime) PR https://github.com/seaside1/jrule/pull/173
-  handle quantity types for item-change-conditions by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/172

## BETA18

-  Refactored timers and added method for checking for timer lock by [seaside1](https://github.com/seaside1) PR https://github.com/seaside1/jrule/pull/171
-  Added the ability to configure the name of the package in jrule.conf by [kuimovvg](https://github.com/kuimovvg) PR https://github.com/seaside1/jrule/pull/170
-  Add previousState and a test by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/168

## BETA17

-  Fix for thing actions without @ActionInput by [seime](https://github.com/seime)PR https://github.com/seaside1/jrule/pull/143
-  Store jar file as build artifact by [seime](https://github.com/seime)PR https://github.com/seaside1/jrule/pull/144
-  Merge test packages by [seime](https://github.com/seime)PR https://github.com/seaside1/jrule/pull/145
-  Only consider methods declared in rule class, not inherited methods by [seime](https://github.com/seime)PR https://github.com/seaside1/jrule/pull/147
-  Reduce loglevel to debug by [seime](https://github.com/seime)PR https://github.com/seaside1/jrule/pull/148
-  Send updates/commands to group items itself, not just members by [seime](https://github.com/seime)PR https://github.com/seaside1/jrule/pull/150
-  Reduce flawky builds by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/151
-  Notify for rule runs by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/152
-  Update to openhab 4.0.0 by [sealside1](https://github.com/sealside1) PR https://github.com/seaside1/jrule/pull/153
-  Add reschedule by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/155
-  Fix Timer:isRunning by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/157
-  Print useful exception trace while InvocationTargetException by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/160
-  Removed OS dependent configurations by [sfdumas](https://github.com/sfdumas) PR https://github.com/seaside1/jrule/pull/164

## BETA16

- Enrich memberOf with options to just listen on items or groups by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/95 
- Add http action by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/97
- Fixed typo by [thwint](https://github.com/thwint) PR https://github.com/seaside1/jrule/pull/98
- Log exception stacktrace by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/99
- Enhance and cleanup the internal timers by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/100
- Improve trigger channelname conversion and error reporting by [seime](https://github.com/seime)PR https://github.com/seaside1/jrule/pull/103
- Add previousCondition to JRuleWhenItemChange by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/104
- Let transform throw a runtime exception by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/106
- ForNameOptional, UnspecifiedGroupItem, QuantityItem by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/107
- Fix NPE with watchingForItem while not completely initialized  by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/110
- Fixed contact item issue by [seaside1](https://github.com/seaside1) PR https://github.com/seaside1/jrule/pull/114
- Add tests for historicState NPE, add toString for values by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/118
- Fix merge (transform exception) by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/119
- Add metadata and tags getters for items by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/121
- Add delayed execution, remove unused JRuleOr by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/122
- Remove illegal characters from generated channel name by [seime](https://github.com/seime)PR https://github.com/seaside1/jrule/pull/124
- Reduce loglevel for non-annotated java methods in rule classes by [seime](https://github.com/seime)PR https://github.com/seaside1/jrule/pull/125
- Fix member of by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/126
- Make Item constants constant by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/128
- Exclude lambdas while scanning methods by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/129
- Exclude persistence verifications by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/130
- Memberitems type specific by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/131
- Fix repeating timers by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/133
- Let JRuleItemEvent contain JRuleItem by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/134
- Restructure documentation by [seime](https://github.com/seime)PR https://github.com/seaside1/jrule/pull/135
- Fixed group item class generation by [gerrieg](https://github.com/gerrieg)PR https://github.com/seaside1/jrule/pull/138
- Remove direct item registry update causing new events to be produced by [seime](https://github.com/seime)PR https://github.com/seaside1/jrule/pull/140
- Spelling fixed [rdrxr](https://github.com/rdrxr) PR https://github.com/seaside1/jrule/pull/141

## BETA15

- BREAKING: All JRuleWhen has to be change to corresponding JRuleWhenItemChanged (as an example, look at JRule Examples
  documentation)
- JRule When refactoring by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/61
- Thing Channel triggers by [seime](https://github.com/seime) PR https://github.com/seaside1/jrule/pull/62
- Generate Actions by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/63
- Add option to get groupMembers as Items by [querdenker2k](https://github.com/querdenker2k)
  PR https://github.com/seaside1/jrule/pull/65
- Memberof Trigger by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/66
- Fix buffer being read twice and breaking classloading by [seime](https://github.com/seime)
  PR https://github.com/seaside1/jrule/pull/67
- Fix missing precondition support for timer rules by [seime](https://github.com/seime)
  PR https://github.com/seaside1/jrule/pull/68
- Fix timer trigger by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/70
- Initial tests for JRuleWhenItemChange triggers by [seime](https://github.com/seime)
  PR https://github.com/seaside1/jrule/pull/73
- Threadlocal logging - some improvements by [seime](https://github.com/seime)
  PR https://github.com/seaside1/jrule/pull/79
- Junit test for duplicate rule invocations by [seime](https://github.com/seime)
  PR https://github.com/seaside1/jrule/pull/75
- Add docker integration test by [querdenker2k](https://github.com/querdenker2k)
  PR https://github.com/seaside1/jrule/pull/77
- Include old thing status in event by [seime](https://github.com/seime) PR https://github.com/seaside1/jrule/pull/80
- Use thread safe list instead of arraylist by [seime](https://github.com/seime)
  PR https://github.com/seaside1/jrule/pull/81
- Defer to parent classloader if file not found by [seime](https://github.com/seime)
  PR https://github.com/seaside1/jrule/pull/83
- Fix inheritance in actions by [querdenker2k](https://github.com/querdenker2k)
  PR https://github.com/seaside1/jrule/pull/87
- Fix mqtt for tests by [querdenker2k](https://github.com/querdenker2k) PR https://github.com/seaside1/jrule/pull/91
- Fix ConcurrentModificationException in test by [querdenker2k](https://github.com/querdenker2k)
  PR https://github.com/seaside1/jrule/pull/92
- Added typing for thing channel triggers, ie `JRuleWhen(channel = binding_thing.triggerChannel)` instead of typing the
  channel id string

## BETA14

- Thing support in rules by [seime](https://github.com/seime) pr https://github.com/seaside1/jrule/pull/59
    - BREAKING: jrule-items.jar has been renamed to jrule-generated.jar
- Added missing sendCommand for StopMove commands by [seime](https://github.com/seime)
  pr https://github.com/seaside1/jrule/pull/57
- Fixed parsing of double value for Quantity type by [seime](https://github.com/seime)
  pr https://github.com/seaside1/jrule/pull/56
- Added generic action handler by [querdenker2k](https://github.com/querdenker2k)
  pr https://github.com/seaside1/jrule/pull/55 see exampe #34
- Refactoring of event for channel plus cleanup by [querdenker2k](https://github.com/querdenker2k)
  pr https://github.com/seaside1/jrule/pull/52
- Refactoring of persistance functions and item handling with exceptions
  by [querdenker2k](https://github.com/querdenker2k) pr https://github.com/seaside1/jrule/pull/51
- Added item id and fixes for generated items by [LumnitzF](https://github.com/LumnitzF)
  pr https://github.com/seaside1/jrule/pull/50
- Added MDC Logging tags to be used with elastic search (logstash,kibana and similar)
  by [querdenker2k](https://github.com/querdenker2k) pr https://github.com/seaside1/jrule/pull/49
- Fixed parsing of double values in rule conditions by [seime](https://github.com/seime)
  pr https://github.com/seaside1/jrule/pull/48
- Fixed parsing of UNDEF by [seime](https://github.com/seime) pr https://github.com/seaside1/jrule/pull/45

## BETA13

- Fixed a bug with naming of JRuleItems.java
- Fixed issues with post and sendCommand to groups

## BETA12

- Major refactoring by seime https://github.com/seaside1/jrule/pull/42
    - Replaces the templating mechanism with Freemarker, mainly to allow more advanced constructs such as loops -
      and to avoid all the repetitive code in the template files
    - Generates a new file Items.java which looks a bit like
      public class Items {
      public static MySwitchItem SwitchItem = JRuleItemRegistry.get("MySwitchItem", MySwitchItem.class);
      public static MyStringItem StringItem = JRuleItemRegistry.get("MyStringItem", MyStringItem.class);
      }
    - Adds a skeleton support for LocationItem (which was missing)
    - Adds a new field LABEL (item label)
    - Adds a few convenience methods such as getLabel() and getName()
    - Adds more typing of Group items

## BETA11

- Wrap TransformationException in JRuleExecutionException by seime https://github.com/seaside1/jrule/pull/39
- Add equivalent postUpdate logging as sendCommand by seime https://github.com/seaside1/jrule/pull/38
- Fix group sendCommand for UpDown by seime https://github.com/seaside1/jrule/pull/36
- Added eq and neq to channel event by gerrieg https://github.com/seaside1/jrule/pull/35
- Added support for ZonedDateTime in DateTimeItem by gerrieg https://github.com/seaside1/jrule/pull/34
- Fixed issued with undef item for state
- Added mocked eventbus for testing rules with junit

## BETA10

- Optimized items by gerrieg https://github.com/seaside1/jrule/pull/33
- Syntax change: event.getValue(), event.getValuesAsDouble() etc replaced with event.getState().getValue() and
  event.getState().getValueAsDouble()
- Syntax change JRuleSwitchItem.sendCommand(myItem, ON) replaced with JRuleSwitchItem.forName(myItem).sendCommand(ON)

## BETA9

- Fixed bug with item generation and forName overloading

## BETA8

- Added forName for items see example
  27 https://github.com/seaside1/jrule/commit/0952ae497d998c5728a85df407bfbf3f1909f8e9
- Added itemName and OldState by gerrieg https://github.com/seaside1/jrule/pull/31

## BETA7

- Fixed item for Number:Quantity, you can now send a quantity type in the command see example 26
- Added precondition see example 24 and 25 by seime: https://github.com/seaside1/jrule/pull/30
- Added possibility to use subdirs and packages for rules by seime: https://github.com/seaside1/jrule/pull/30

## BETA6

- Added seprate thread executors supplied by seime: https://github.com/seaside1/jrule/pull/23
- Added Windows Support by LumnitzF https://github.com/seaside1/jrule/pull/24
- Added cancellation of repeating timers by seime: https://github.com/seaside1/jrule/pull/26
- Null check on timers with futures by seime https://github.com/seaside1/jrule/pull/29
- Update to Openhab 3.3.0
- Fixed rescheduling of timers by seime: https://github.com/seaside1/jrule/pull/28
- Added check for repeating timers by seime: https://github.com/seaside1/jrule/pull/27
- Fixed repeated timer executing directly by seime: https://github.com/seaside1/jrule/pull/25
- Added transformation sevice example 23 by seime: https://github.com/seaside1/jrule/pull/20
- Improved compiler error logging by seime: https://github.com/seaside1/jrule/pull/12
- Added UP/Down support for group items by seime: https://github.com/seaside1/jrule/pull/13
- Quantity Type support by seime: https://github.com/seaside1/jrule/pull/14
- Configurable item-prefix by weberjn https://github.com/seaside1/jrule/pull/16
- Improve exception handling by No3x https://github.com/seaside1/jrule/pull/19
- Made thread executors configurable, default disabled
- Possible to build jrule standalone https://github.com/seaside1/jrule/pull/24 or together with Openhab-addons
- Cleaned up repostiory from binary files. Remove and old clones of jrule and clone a fresh repo

## BETA5

- Addes support for adding rules in jar-files, as an alternative.

## BETA4

- Added config for character to be used when generating items files

## BETA3

- Major refactoring of logging

## BETA2

- Fixed color item
- Added annotation for setting logger on a rule see example 21 and 22
- Optional to override getLogName on class
- Contact item update
- Rollershutter item support added
- UpDown, Increase Decrease support added to various items
- OnOff and Percent commands added to ColorItem

## BETA1

- Added color item see example 20
- Moved org.openhab.automation.jrule.rules.JRuleOnOffvalue, JRulePlayPause etc to
  org.openhab.automation.jrule.rules.value

## ALPHA12

- Fix some language typos, some refactor of java classes, improved initialization of singletons due to concurrency
  aspects

## ALPHA11

- Added check for working dir via system properties

## ALPHA10

- Added LatUpdate via JRulePersistenceExtentions see example 19

## ALPHA9

- Added cron expressions for rules see example 18
- Bug fix by @roth for reloading channel triggers

## ALPHA8

- Channel triggers provided by @roth see example 17

## ALPHA7

- Fixed bug with group member value was null for non StringType types

## ALPHA6

- Added group functionality getMember will return who triggered a change for a group

## ALPHA5

- Removed dependencies on slf4japi and eclipse annotations
- Added logInfo logDebug (to wrap slf4j and remove dep)
- Fixed compilation of rules to be more robust with internal dependencies

## ALPHA4

- Refactored completable futures
- Added 5 seconds of delay for initialization of the rule engine to avoid multiple reloads
- Added support for play & pause for player item
- Added commandLineExecute

## ALPHA3

- Fixed issue when reloading rules if they are changed with monitored items
- Fixed classpath issue when executing rules using 3rd party libraries

## ALPHA2

- Added possibility to include 3rd party libraries when developing rules

## ALPHA1

- Refactored internal jar dependencies and jar-generation
- Added eq comparator for number triggers in rules
