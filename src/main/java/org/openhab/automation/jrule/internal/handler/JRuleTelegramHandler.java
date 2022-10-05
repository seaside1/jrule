package org.openhab.automation.jrule.internal.handler;

import org.openhab.binding.telegram.internal.TelegramHandler;
import org.openhab.binding.telegram.internal.TelegramHandlerFactory;
import org.openhab.binding.telegram.internal.action.TelegramActions;
import org.openhab.core.io.net.http.internal.ExtensibleTrustManagerImpl;
import org.openhab.core.io.net.http.internal.WebClientFactoryImpl;
import org.openhab.core.thing.internal.BridgeImpl;

public class JRuleTelegramHandler {
    private static JRuleTelegramHandler instance;

    public static JRuleTelegramHandler get() {
        if (instance == null) {
            synchronized (JRuleTelegramHandler.class) {
                if (instance == null) {
                    instance = new JRuleTelegramHandler();
                }
            }
        }
        return instance;
    }

    public JRuleTelegramHandler() {
    }

    public void doIt() {
        TelegramActions telegramActions = new TelegramActions();
        TelegramHandlerFactory telegramHandlerFactory = new TelegramHandlerFactory(new WebClientFactoryImpl(new ExtensibleTrustManagerImpl()));
        telegramHandlerFactory.registerHandler(new BridgeImpl())
        telegramActions.setThingHandler(new TelegramHandler(null, null));
        telegramActions.sendTelegram("bla");
    }
}
