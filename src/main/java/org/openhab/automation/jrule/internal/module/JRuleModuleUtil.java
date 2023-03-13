package org.openhab.automation.jrule.internal.module;

/**
 *
 * @author Arne Seime - Initial Contribution
 */
public class JRuleModuleUtil {

    public static String toTriggerModuleUID(Class<?> annotation) {
        return JRuleTriggerHandler.TRIGGER_PREFIX + annotation.getSimpleName();
    }
}
