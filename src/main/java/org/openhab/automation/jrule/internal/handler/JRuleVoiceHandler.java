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
package org.openhab.automation.jrule.internal.handler;

import org.openhab.core.audio.AudioHTTPServer;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.net.NetworkAddressService;
import org.openhab.core.voice.VoiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link JRuleVoiceHandler} is responsible for handling commands and status
 * updates for JRule State Machines.
 *
 * @author Joseph (Seaside) Hagberg - Initial contribution
 */
public class JRuleVoiceHandler {

    private static volatile JRuleVoiceHandler instance;

    private VoiceManager voiceManager;
    private AudioHTTPServer audioHTTPServer;
    private NetworkAddressService networkAddressService;

    public VoiceManager getVoiceManager() {
        return voiceManager;
    }

    private final Logger logger = LoggerFactory.getLogger(JRuleVoiceHandler.class);

    private JRuleVoiceHandler() {
    }

    public static JRuleVoiceHandler get() {
        if (instance == null) {
            synchronized (JRuleVoiceHandler.class) {
                if (instance == null) {
                    instance = new JRuleVoiceHandler();
                }
            }
        }
        return instance;
    }

    public void setVoiceManager(VoiceManager voiceManager) {
        this.voiceManager = voiceManager;
    }

    public void say(String text) {
        voiceManager.say(text);
    }

    public void say(String text, int volumePercent) {
        final PercentType volume = new PercentType(volumePercent);
        voiceManager.say(text, volume);
    }

    public void say(String text, String voiceId, String sinkId) {
        voiceManager.say(text, voiceId, sinkId);
    }

    public void say(String text, String voiceId, String sinkId, int volumePercent) {
        final PercentType volume = new PercentType(volumePercent);
        voiceManager.say(text, voiceId, sinkId, volume);
    }

    public AudioHTTPServer getAudioHTTPServer() {
        return audioHTTPServer;
    }

    public void setAudioHTTPServer(AudioHTTPServer audioHTTPServer) {
        this.audioHTTPServer = audioHTTPServer;
    }

    public NetworkAddressService getNetworkAddressService() {
        return networkAddressService;
    }

    public void setNetworkAddressService(NetworkAddressService networkAddressService) {
        this.networkAddressService = networkAddressService;
    }
}
