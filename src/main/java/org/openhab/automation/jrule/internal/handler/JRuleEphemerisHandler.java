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
package org.openhab.automation.jrule.internal.handler;

import java.io.FileNotFoundException;
import java.net.URL;
import java.time.ZonedDateTime;

import org.openhab.core.ephemeris.EphemerisManager;

/**
 * The {@link JRuleEphemerisHandler} is responsible for handling interface with 
 * {@EphemerisManager}
 *
 * @author Gaël L'hopital - Initial contribution
 */
public class JRuleEphemerisHandler {

    private static volatile JRuleEphemerisHandler instance;

    private EphemerisManager ephemerisManager;

    private JRuleEphemerisHandler() {
    }

    public static JRuleEphemerisHandler get() {
        if (instance == null) {
            synchronized (JRuleEphemerisHandler.class) {
                if (instance == null) {
                    instance = new JRuleEphemerisHandler();
                }
            }
        }
        return instance;
    }

    public void setEphemerisManager(EphemerisManager ephemerisManager) {
        this.ephemerisManager = ephemerisManager;
    }
    
    public boolean isWeekend(ZonedDateTime date) {
        return ephemerisManager.isWeekend(date);
    }

    public boolean isInDayset(String daysetName, ZonedDateTime date){
        return ephemerisManager.isInDayset(daysetName, date);
    }

    public boolean isBankHoliday(ZonedDateTime date){
        return ephemerisManager.isBankHoliday(date);
    }

    public boolean isBankHoliday(ZonedDateTime date, URL resource){
        return ephemerisManager.isBankHoliday(date, resource);
    }

    public boolean isBankHoliday(ZonedDateTime date, String filename) throws FileNotFoundException{
        return ephemerisManager.isBankHoliday(date, filename);
    }

    public String getBankHolidayName(ZonedDateTime date){
        return ephemerisManager.getBankHolidayName(date);
    }

    public String getBankHolidayName(ZonedDateTime date, URL resource){
        return ephemerisManager.getBankHolidayName(date, resource);
    }

    public String getBankHolidayName(ZonedDateTime date, String filename) throws FileNotFoundException{
        return ephemerisManager.getBankHolidayName(date, filename);
    }

    public String getNextBankHoliday(ZonedDateTime startDate){
        return ephemerisManager.getNextBankHoliday(startDate);
    }

    public String getNextBankHoliday(ZonedDateTime startDate, URL resource){
        return ephemerisManager.getNextBankHoliday(startDate, resource);
    }

    public String getNextBankHoliday(ZonedDateTime startDate, String filename) throws FileNotFoundException{
        return ephemerisManager.getNextBankHoliday(startDate, filename);
    }

    public String getHolidayDescription(String holiday){
        return ephemerisManager.getHolidayDescription(holiday);
    }

    public long getDaysUntil(ZonedDateTime from, String searchedHoliday){
        return ephemerisManager.getDaysUntil(from, searchedHoliday);
    }

    public long getDaysUntil(ZonedDateTime from, String searchedHoliday, URL resource){
        return ephemerisManager.getDaysUntil(from, searchedHoliday, resource);
    }

    public long getDaysUntil(ZonedDateTime from, String searchedHoliday, String filename) throws FileNotFoundException{
        return ephemerisManager.getDaysUntil(from, searchedHoliday, filename);
    }

}
