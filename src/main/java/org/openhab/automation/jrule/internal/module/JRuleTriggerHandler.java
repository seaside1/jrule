package org.openhab.automation.jrule.internal.module;

import org.openhab.core.automation.ModuleHandlerCallback;
import org.openhab.core.automation.Trigger;
import org.openhab.core.automation.handler.BaseTriggerModuleHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Arne Seime - Initial Contribution
 */
public class JRuleTriggerHandler extends BaseTriggerModuleHandler {

    public static final String TRIGGER_PREFIX = JRuleRuleProvider.MODULE_PREFIX + "trigger.";
    private JRuleRuleProvider jRuleRuleProvider;
    private String ruleUID;

    private final Logger logger = LoggerFactory.getLogger(JRuleTriggerHandler.class);

    public JRuleTriggerHandler(Trigger module, JRuleRuleProvider jRuleRuleProvider, String ruleUID) {
        super(module);
        this.jRuleRuleProvider = jRuleRuleProvider;
        this.ruleUID = ruleUID;
    }

    @Override
    public void setCallback(ModuleHandlerCallback callback) {
        JRuleModuleEntry rule = jRuleRuleProvider.getRule(ruleUID);
        if (rule != null) {
            rule.ruleEnabled();
        } else {
            logger.error("Did not find rule {}, unable to enable", ruleUID);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        JRuleModuleEntry rule = jRuleRuleProvider.getRule(ruleUID);
        if (rule != null) {
            rule.ruleDisabled();
        } else {
            logger.error("Did not find rule {}, unable to disable", ruleUID);
        }
    }
}
