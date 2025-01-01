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

package org.openhab.automation.jrule.persistence;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.automation.jrule.rules.value.JRuleDecimalValue;
import org.openhab.automation.jrule.rules.value.JRuleValue;
import org.openhab.core.types.TimeSeries;

/**
 * Automatically Generated Class for Items - DO NOT EDIT!
 *
 * @author Robert Delbrück - Initial contribution
 *         Collect all from PersistenceExtension with regex: (.* )?.* .*\(((.* )?.* .*,?)+\)
 */
public interface JRulePersistence {
    default void persist() {
        persist((String) null);
    }

    void persist(@Nullable String serviceId);

    default void persist(ZonedDateTime timestamp, JRuleValue state) {
        persist(timestamp, state, null);
    }

    void persist(ZonedDateTime timestamp, JRuleValue state, @Nullable String serviceId);

    default void persist(ZonedDateTime timestamp, String stateString) {
        persist(timestamp, stateString, null);
    }

    void persist(ZonedDateTime timestamp, String stateString, @Nullable String serviceId);

    default void persist(TimeSeries timeSeries) {
        persist(timeSeries, null);
    }

    void persist(TimeSeries timeSeries, @Nullable String serviceId);

    default Optional<JRuleHistoricState> historicState(ZonedDateTime timestamp) {
        return historicState(timestamp, null);
    }

    Optional<JRuleHistoricState> historicState(ZonedDateTime timestamp, @Nullable String serviceId);

    Optional<JRuleHistoricState> persistedState(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleHistoricState> persistedState(ZonedDateTime timestamp) {
        return persistedState(timestamp, null);
    }

    default Optional<ZonedDateTime> lastUpdate() {
        return lastUpdate(null);
    }

    Optional<ZonedDateTime> lastUpdate(@Nullable String serviceId);

    default Optional<ZonedDateTime> nextUpdate() {
        return nextUpdate(null);
    }

    Optional<ZonedDateTime> nextUpdate(@Nullable String serviceId);

    default Optional<ZonedDateTime> lastChange() {
        return lastChange(null);
    }

    Optional<ZonedDateTime> lastChange(@Nullable String serviceId);

    default Optional<ZonedDateTime> nextChange() {
        return nextChange(null);
    }

    Optional<ZonedDateTime> nextChange(@Nullable String serviceId);

    default Optional<JRuleHistoricState> previousState() {
        return previousState(null);
    }

    default Optional<JRuleHistoricState> previousState(boolean skipEqual) {
        return previousState(null);
    }

    Optional<JRuleHistoricState> previousState(@Nullable String serviceId);

    Optional<JRuleHistoricState> previousState(boolean skipEqual, @Nullable String serviceId);

    default Optional<JRuleHistoricState> nextState() {
        return nextState(null);
    }

    default Optional<JRuleHistoricState> nextState(boolean skipEqual) {
        return nextState(null);
    }

    Optional<JRuleHistoricState> nextState(@Nullable String serviceId);

    Optional<JRuleHistoricState> nextState(boolean skipEqual, @Nullable String serviceId);

    default Optional<Boolean> changedSince(ZonedDateTime timestamp) {
        return changedSince(timestamp, null);
    }

    Optional<Boolean> changedSince(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<Boolean> changedUntil(ZonedDateTime timestamp) {
        return changedUntil(timestamp, null);
    }

    default Optional<Boolean> changedBetween(ZonedDateTime begin, ZonedDateTime end) {
        return changedBetween(begin, end, null);
    }

    Optional<Boolean> changedBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId);

    Optional<Boolean> changedUntil(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<Boolean> updatedSince(ZonedDateTime timestamp) {
        return updatedSince(timestamp, null);
    }

    default Optional<Boolean> updatedUntil(ZonedDateTime timestamp) {
        return updatedUntil(timestamp, null);
    }

    default Optional<Boolean> updatedBetween(ZonedDateTime begin, ZonedDateTime end) {
        return updatedBetween(begin, end, null);
    }

    Optional<Boolean> updatedBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId);

    Optional<Boolean> updatedSince(ZonedDateTime timestamp, @Nullable String serviceId);

    Optional<Boolean> updatedUntil(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleHistoricState> maximumSince(ZonedDateTime timestamp) {
        return maximumSince(timestamp, null);
    }

    Optional<JRuleHistoricState> maximumSince(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleHistoricState> maximumUntil(ZonedDateTime timestamp) {
        return maximumUntil(timestamp, null);
    }

    Optional<JRuleHistoricState> maximumUntil(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleHistoricState> maximumBetween(ZonedDateTime begin, ZonedDateTime end) {
        return maximumBetween(begin, end, null);
    }

    Optional<JRuleHistoricState> maximumBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId);

    default Optional<JRuleHistoricState> minimumSince(ZonedDateTime timestamp) {
        return minimumSince(timestamp, null);
    }

    Optional<JRuleHistoricState> minimumSince(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleHistoricState> minimumUntil(ZonedDateTime timestamp) {
        return minimumUntil(timestamp, null);
    }

    Optional<JRuleHistoricState> minimumUntil(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleHistoricState> minimumBetween(ZonedDateTime begin, ZonedDateTime end) {
        return minimumBetween(begin, end, null);
    }

    Optional<JRuleHistoricState> minimumBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId);

    default Optional<JRuleValue> varianceSince(ZonedDateTime timestamp) {
        return varianceSince(timestamp, null);
    }

    default Optional<JRuleValue> varianceUntil(ZonedDateTime timestamp) {
        return varianceUntil(timestamp, null);
    }

    default Optional<JRuleValue> varianceBetween(ZonedDateTime begin, ZonedDateTime end) {
        return varianceBetween(begin, end, null);
    }

    Optional<JRuleValue> varianceBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId);

    Optional<JRuleValue> varianceSince(ZonedDateTime timestamp, @Nullable String serviceId);

    Optional<JRuleValue> varianceUntil(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleValue> deviationSince(ZonedDateTime timestamp) {
        return deviationSince(timestamp, null);
    }

    default Optional<JRuleValue> deviationUntil(ZonedDateTime timestamp) {
        return deviationUntil(timestamp, null);
    }

    default Optional<JRuleValue> deviationBetween(ZonedDateTime begin, ZonedDateTime end) {
        return deviationBetween(begin, end, null);
    }

    Optional<JRuleValue> deviationBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId);

    Optional<JRuleValue> deviationSince(ZonedDateTime timestamp, @Nullable String serviceId);

    Optional<JRuleValue> deviationUntil(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleValue> averageUntil(ZonedDateTime timestamp) {
        return averageUntil(timestamp, null);
    }

    default Optional<JRuleValue> averageBetween(ZonedDateTime begin, ZonedDateTime end) {
        return averageBetween(begin, end, null);
    }

    Optional<JRuleValue> averageBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId);

    default Optional<JRuleValue> averageSince(ZonedDateTime timestamp) {
        return averageSince(timestamp, null);
    }

    Optional<JRuleValue> averageSince(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleValue> sumSince(ZonedDateTime timestamp) {
        return sumSince(timestamp, null);
    }

    Optional<JRuleValue> sumSince(ZonedDateTime timestamp, @Nullable String serviceId);

    Optional<JRuleValue> averageUntil(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleValue> medianSince(ZonedDateTime timestamp) {
        return medianSince(timestamp, null);
    }

    default Optional<JRuleValue> medianUntil(ZonedDateTime timestamp) {
        return medianUntil(timestamp, null);
    }

    default Optional<JRuleValue> medianBetween(ZonedDateTime begin, ZonedDateTime end) {
        return medianBetween(begin, end, null);
    }

    Optional<JRuleValue> medianBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId);

    Optional<JRuleValue> medianSince(ZonedDateTime timestamp, @Nullable String serviceId);

    Optional<JRuleValue> medianUntil(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleValue> deltaSince(ZonedDateTime timestamp) {
        return deltaSince(timestamp, null);
    }

    default Optional<JRuleValue> deltaUntil(ZonedDateTime timestamp) {
        return deltaUntil(timestamp, null);
    }

    default Optional<JRuleValue> deltaBetween(ZonedDateTime begin, ZonedDateTime end) {
        return deviationBetween(begin, end, null);
    }

    Optional<JRuleValue> deltaSince(ZonedDateTime timestamp, @Nullable String serviceId);

    Optional<JRuleValue> deltaUntil(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> evolutionRate(ZonedDateTime timestamp) {
        return evolutionRate(timestamp, (String) null);
    }

    Optional<JRuleDecimalValue> evolutionRate(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> evolutionRateSince(ZonedDateTime timestamp) {
        return evolutionRateSince(timestamp, null);
    }

    Optional<JRuleDecimalValue> evolutionRateSince(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> evolutionRateUntil(ZonedDateTime timestamp) {
        return evolutionRateUntil(timestamp, (String) null);
    }

    Optional<JRuleDecimalValue> evolutionRateUntil(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> evolutionRate(ZonedDateTime begin, ZonedDateTime end) {
        return evolutionRate(begin, end, null);
    }

    Optional<JRuleDecimalValue> evolutionRate(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> evolutionRateBetween(ZonedDateTime begin, ZonedDateTime end) {
        return evolutionRateBetween(begin, end, null);
    }

    Optional<JRuleDecimalValue> evolutionRateBetween(ZonedDateTime begin, ZonedDateTime end,
            @Nullable String serviceId);

    default Optional<Long> countSince(ZonedDateTime timestamp) {
        return countSince(timestamp, null);
    }

    default Optional<Long> countUntil(ZonedDateTime timestamp) {
        return countUntil(timestamp, null);
    }

    default Optional<Long> countBetween(ZonedDateTime begin, ZonedDateTime end) {
        return countBetween(begin, end, null);
    }

    Optional<Long> countBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId);

    Optional<Long> countSince(ZonedDateTime begin, @Nullable String serviceId);

    Optional<Long> countUntil(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<Long> countStateChangesSince(ZonedDateTime timestamp) {
        return countStateChangesSince(timestamp, null);
    }

    Optional<Long> countStateChangesSince(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<Long> countStateChangesUntil(ZonedDateTime timestamp) {
        return countStateChangesUntil(timestamp, null);
    }

    Optional<Long> countStateChangesUntil(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<Long> countStateChangesBetween(ZonedDateTime begin, ZonedDateTime end) {
        return countStateChangesBetween(begin, end, null);
    }

    Optional<Long> countStateChangesBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId);

    default Optional<List<JRuleHistoricState>> getAllStatesSince(ZonedDateTime timestamp) {
        return getAllStatesSince(timestamp, null);
    }

    Optional<List<JRuleHistoricState>> getAllStatesSince(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<List<JRuleHistoricState>> getAllStatesUntil(ZonedDateTime timestamp) {
        return getAllStatesUntil(timestamp, null);
    }

    Optional<List<JRuleHistoricState>> getAllStatesUntil(ZonedDateTime timestamp, @Nullable String serviceId);

    default void removeAllStatesSince(ZonedDateTime timestamp) {
        removeAllStatesSince(timestamp, null);
    }

    default void removeAllStatesUntil(ZonedDateTime timestamp) {
        removeAllStatesUntil(timestamp, null);
    }

    default void removeAllStatesBetween(ZonedDateTime begin, ZonedDateTime end) {
        removeAllStatesBetween(begin, end, null);
    }

    void removeAllStatesBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId);

    void removeAllStatesSince(ZonedDateTime timestamp, @Nullable String serviceId);

    void removeAllStatesUntil(ZonedDateTime timestamp, @Nullable String serviceId);
}
