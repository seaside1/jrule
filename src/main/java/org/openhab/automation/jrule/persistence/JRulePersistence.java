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
 * The {@link JRulePersistence}. Defines all openHAB core persistence in this interface.
 *
 * @author Robert Delbrück - Initial contribution
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

    default Optional<JRuleDecimalValue> historicStateAsDecimal(ZonedDateTime timestamp, @Nullable String serviceId) {
        return historicState(timestamp, serviceId).map(JRuleHistoricState::getValue)
                .map(v -> v.as(JRuleDecimalValue.class));
    }

    default Optional<JRuleDecimalValue> historicStateAsDecimal(ZonedDateTime timestamp) {
        return historicStateAsDecimal(timestamp, null);
    }

    default Optional<JRuleHistoricState> historicState(ZonedDateTime timestamp) {
        return historicState(timestamp, null);
    }

    Optional<JRuleHistoricState> historicState(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> persistedStateAsDecimal(ZonedDateTime timestamp, @Nullable String serviceId) {
        return persistedState(timestamp, serviceId).map(JRuleHistoricState::getValue)
                .map(v -> v.as(JRuleDecimalValue.class));
    }

    default Optional<JRuleDecimalValue> persistedStateAsDecimal(ZonedDateTime timestamp) {
        return persistedStateAsDecimal(timestamp, null);
    }

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

    default Optional<JRuleDecimalValue> previousStateAsDecimal(@Nullable String serviceId) {
        return previousState(serviceId).map(JRuleHistoricState::getValue).map(v -> v.as(JRuleDecimalValue.class));
    }

    default Optional<JRuleDecimalValue> previousStateAsDecimal() {
        return previousStateAsDecimal(null);
    }

    default Optional<JRuleHistoricState> previousState() {
        return previousState(null);
    }

    Optional<JRuleHistoricState> previousState(@Nullable String serviceId);

    default Optional<JRuleHistoricState> previousState(boolean skipEqual) {
        return previousState(null);
    }

    Optional<JRuleHistoricState> previousState(boolean skipEqual, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> nextStateAsDecimal(@Nullable String serviceId) {
        return nextState(serviceId).map(JRuleHistoricState::getValue).map(v -> v.as(JRuleDecimalValue.class));
    }

    default Optional<JRuleDecimalValue> nextStateAsDecimal() {
        return nextStateAsDecimal(null);
    }

    default Optional<JRuleHistoricState> nextState() {
        return nextState(null);
    }

    Optional<JRuleHistoricState> nextState(@Nullable String serviceId);

    default Optional<JRuleHistoricState> nextState(boolean skipEqual) {
        return nextState(null);
    }

    Optional<JRuleHistoricState> nextState(boolean skipEqual, @Nullable String serviceId);

    default Optional<Boolean> changedSince(ZonedDateTime timestamp) {
        return changedSince(timestamp, null);
    }

    Optional<Boolean> changedSince(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<Boolean> changedUntil(ZonedDateTime timestamp) {
        return changedUntil(timestamp, null);
    }

    Optional<Boolean> changedUntil(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<Boolean> changedBetween(ZonedDateTime begin, ZonedDateTime end) {
        return changedBetween(begin, end, null);
    }

    Optional<Boolean> changedBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId);

    default Optional<Boolean> updatedSince(ZonedDateTime timestamp) {
        return updatedSince(timestamp, null);
    }

    Optional<Boolean> updatedSince(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<Boolean> updatedUntil(ZonedDateTime timestamp) {
        return updatedUntil(timestamp, null);
    }

    Optional<Boolean> updatedUntil(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<Boolean> updatedBetween(ZonedDateTime begin, ZonedDateTime end) {
        return updatedBetween(begin, end, null);
    }

    Optional<Boolean> updatedBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> maximumSinceAsDecimal(ZonedDateTime timestamp, @Nullable String serviceId) {
        return maximumSince(timestamp, serviceId).map(JRuleHistoricState::getValue)
                .map(v -> v.as(JRuleDecimalValue.class));
    }

    default Optional<JRuleDecimalValue> maximumSinceAsDecimal(ZonedDateTime timestamp) {
        return maximumSinceAsDecimal(timestamp, null);
    }

    default Optional<JRuleHistoricState> maximumSince(ZonedDateTime timestamp) {
        return maximumSince(timestamp, null);
    }

    Optional<JRuleHistoricState> maximumSince(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> maximumUntilAsDecimal(ZonedDateTime timestamp, @Nullable String serviceId) {
        return maximumUntil(timestamp, serviceId).map(JRuleHistoricState::getValue)
                .map(v -> v.as(JRuleDecimalValue.class));
    }

    default Optional<JRuleDecimalValue> maximumUntilAsDecimal(ZonedDateTime timestamp) {
        return maximumUntilAsDecimal(timestamp, null);
    }

    default Optional<JRuleHistoricState> maximumUntil(ZonedDateTime timestamp) {
        return maximumUntil(timestamp, null);
    }

    Optional<JRuleHistoricState> maximumUntil(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> maximumBetweenAsDecimal(ZonedDateTime begin, ZonedDateTime end,
            @Nullable String serviceId) {
        return maximumBetween(begin, end, serviceId).map(JRuleHistoricState::getValue)
                .map(v -> v.as(JRuleDecimalValue.class));
    }

    default Optional<JRuleDecimalValue> maximumBetweenAsDecimal(ZonedDateTime begin, ZonedDateTime end) {
        return maximumBetweenAsDecimal(begin, end, null);
    }

    default Optional<JRuleHistoricState> maximumBetween(ZonedDateTime begin, ZonedDateTime end) {
        return maximumBetween(begin, end, null);
    }

    Optional<JRuleHistoricState> maximumBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> minimumSinceAsDecimal(ZonedDateTime timestamp, @Nullable String serviceId) {
        return minimumSince(timestamp, serviceId).map(JRuleHistoricState::getValue)
                .map(v -> v.as(JRuleDecimalValue.class));
    }

    default Optional<JRuleDecimalValue> minimumSinceAsDecimal(ZonedDateTime timestamp) {
        return minimumSinceAsDecimal(timestamp, null);
    }

    default Optional<JRuleHistoricState> minimumSince(ZonedDateTime timestamp) {
        return minimumSince(timestamp, null);
    }

    Optional<JRuleHistoricState> minimumSince(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> minimumUntilAsDecimal(ZonedDateTime timestamp, @Nullable String serviceId) {
        return minimumUntil(timestamp, serviceId).map(JRuleHistoricState::getValue)
                .map(v -> v.as(JRuleDecimalValue.class));
    }

    default Optional<JRuleDecimalValue> minimumUntilAsDecimal(ZonedDateTime timestamp) {
        return minimumUntilAsDecimal(timestamp, null);
    }

    default Optional<JRuleHistoricState> minimumUntil(ZonedDateTime timestamp) {
        return minimumUntil(timestamp, null);
    }

    Optional<JRuleHistoricState> minimumUntil(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> minimumBetweenAsDecimal(ZonedDateTime begin, ZonedDateTime end,
            @Nullable String serviceId) {
        return minimumBetween(begin, end, serviceId).map(JRuleHistoricState::getValue)
                .map(v -> v.as(JRuleDecimalValue.class));
    }

    default Optional<JRuleDecimalValue> minimumBetweenAsDecimal(ZonedDateTime begin, ZonedDateTime end) {
        return minimumBetweenAsDecimal(begin, end, null);
    }

    default Optional<JRuleHistoricState> minimumBetween(ZonedDateTime begin, ZonedDateTime end) {
        return minimumBetween(begin, end, null);
    }

    Optional<JRuleHistoricState> minimumBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> varianceSinceAsDecimal(ZonedDateTime timestamp) {
        return varianceSince(timestamp, null).map(v -> v.as(JRuleDecimalValue.class));
    }

    default Optional<JRuleValue> varianceSince(ZonedDateTime timestamp) {
        return varianceSince(timestamp, null);
    }

    Optional<JRuleValue> varianceSince(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> varianceUntilAsDecimal(ZonedDateTime timestamp) {
        return varianceUntil(timestamp, null).map(v -> v.as(JRuleDecimalValue.class));
    }

    default Optional<JRuleValue> varianceUntil(ZonedDateTime timestamp) {
        return varianceUntil(timestamp, null);
    }

    Optional<JRuleValue> varianceUntil(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> varianceBetweenAsDecimal(ZonedDateTime begin, ZonedDateTime end) {
        return varianceBetween(begin, end, null).map(v -> v.as(JRuleDecimalValue.class));
    }

    default Optional<JRuleValue> varianceBetween(ZonedDateTime begin, ZonedDateTime end) {
        return varianceBetween(begin, end, null);
    }

    Optional<JRuleValue> varianceBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> deviationSinceAsDecimal(ZonedDateTime timestamp) {
        return deviationSince(timestamp, null).map(v -> v.as(JRuleDecimalValue.class));
    }

    default Optional<JRuleValue> deviationSince(ZonedDateTime timestamp) {
        return deviationSince(timestamp, null);
    }

    Optional<JRuleValue> deviationSince(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> deviationUntilAsDecimal(ZonedDateTime timestamp) {
        return deviationUntil(timestamp, null).map(v -> v.as(JRuleDecimalValue.class));
    }

    default Optional<JRuleValue> deviationUntil(ZonedDateTime timestamp) {
        return deviationUntil(timestamp, null);
    }

    Optional<JRuleValue> deviationUntil(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> deviationBetweenAsDecimal(ZonedDateTime begin, ZonedDateTime end) {
        return deviationBetween(begin, end, null).map(v -> v.as(JRuleDecimalValue.class));
    }

    default Optional<JRuleValue> deviationBetween(ZonedDateTime begin, ZonedDateTime end) {
        return deviationBetween(begin, end, null);
    }

    Optional<JRuleValue> deviationBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> averageUntilAsDecimal(ZonedDateTime timestamp) {
        return averageUntil(timestamp, null).map(v -> v.as(JRuleDecimalValue.class));
    }

    default Optional<JRuleValue> averageUntil(ZonedDateTime timestamp) {
        return averageUntil(timestamp, null);
    }

    Optional<JRuleValue> averageUntil(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> averageBetweenAsDecimal(ZonedDateTime begin, ZonedDateTime end,
            @Nullable String serviceId) {
        return averageBetween(begin, end, serviceId).map(v -> v.as(JRuleDecimalValue.class));
    }

    default Optional<JRuleDecimalValue> averageBetweenAsDecimal(ZonedDateTime begin, ZonedDateTime end) {
        return averageBetweenAsDecimal(begin, end, null);
    }

    default Optional<JRuleValue> averageBetween(ZonedDateTime begin, ZonedDateTime end) {
        return averageBetween(begin, end, null);
    }

    Optional<JRuleValue> averageBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> averageSinceAsDecimal(ZonedDateTime timestamp, @Nullable String serviceId) {
        return averageSince(timestamp, serviceId).map(v -> v.as(JRuleDecimalValue.class));
    }

    default Optional<JRuleDecimalValue> averageSinceAsDecimal(ZonedDateTime timestamp) {
        return averageSinceAsDecimal(timestamp, null);
    }

    default Optional<JRuleValue> averageSince(ZonedDateTime timestamp) {
        return averageSince(timestamp, null);
    }

    Optional<JRuleValue> averageSince(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> sumSinceAsDecimal(ZonedDateTime timestamp, @Nullable String serviceId) {
        return sumSince(timestamp, serviceId).map(v -> v.as(JRuleDecimalValue.class));
    }

    default Optional<JRuleDecimalValue> sumSinceAsDecimal(ZonedDateTime timestamp) {
        return sumSinceAsDecimal(timestamp, null);
    }

    default Optional<JRuleValue> sumSince(ZonedDateTime timestamp) {
        return sumSince(timestamp, null);
    }

    Optional<JRuleValue> sumSince(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> medianSinceAsDecimal(ZonedDateTime timestamp, @Nullable String serviceId) {
        return medianSince(timestamp, serviceId).map(v -> v.as(JRuleDecimalValue.class));
    }

    default Optional<JRuleDecimalValue> medianSinceAsDecimal(ZonedDateTime timestamp) {
        return medianSinceAsDecimal(timestamp, null);
    }

    default Optional<JRuleValue> medianSince(ZonedDateTime timestamp) {
        return medianSince(timestamp, null);
    }

    Optional<JRuleValue> medianSince(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> medianUntilAsDecimal(ZonedDateTime timestamp, @Nullable String serviceId) {
        return medianUntil(timestamp, null).map(v -> v.as(JRuleDecimalValue.class));
    }

    default Optional<JRuleDecimalValue> medianUntilAsDecimal(ZonedDateTime timestamp) {
        return medianUntilAsDecimal(timestamp, null);
    }

    default Optional<JRuleValue> medianUntil(ZonedDateTime timestamp) {
        return medianUntil(timestamp, null);
    }

    Optional<JRuleValue> medianUntil(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> medianBetweenAsDecimal(ZonedDateTime begin, ZonedDateTime end,
            @Nullable String serviceId) {
        return medianBetween(begin, end, serviceId).map(v -> v.as(JRuleDecimalValue.class));
    }

    default Optional<JRuleDecimalValue> medianBetweenAsDecimal(ZonedDateTime begin, ZonedDateTime end) {
        return medianBetweenAsDecimal(begin, end, null);
    }

    default Optional<JRuleValue> medianBetween(ZonedDateTime begin, ZonedDateTime end) {
        return medianBetween(begin, end, null);
    }

    Optional<JRuleValue> medianBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> deltaSinceAsDecimal(ZonedDateTime timestamp, @Nullable String serviceId) {
        return deltaSince(timestamp, serviceId).map(v -> v.as(JRuleDecimalValue.class));
    }

    default Optional<JRuleDecimalValue> deltaSinceAsDecimal(ZonedDateTime timestamp) {
        return deltaSinceAsDecimal(timestamp, null);
    }

    default Optional<JRuleValue> deltaSince(ZonedDateTime timestamp) {
        return deltaSince(timestamp, null);
    }

    Optional<JRuleValue> deltaSince(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> deltaUntilAsDecimal(ZonedDateTime timestamp, @Nullable String serviceId) {
        return deltaUntil(timestamp, serviceId).map(v -> v.as(JRuleDecimalValue.class));
    }

    default Optional<JRuleDecimalValue> deltaUntilAsDecimal(ZonedDateTime timestamp) {
        return deltaUntilAsDecimal(timestamp, null);
    }

    default Optional<JRuleValue> deltaUntil(ZonedDateTime timestamp) {
        return deltaUntil(timestamp, null);
    }

    Optional<JRuleValue> deltaUntil(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> deltaBetweenAsDecimal(ZonedDateTime begin, ZonedDateTime end,
            @Nullable String serviceId) {
        return deltaBetween(begin, end, serviceId).map(v -> v.as(JRuleDecimalValue.class));
    }

    default Optional<JRuleDecimalValue> deltaBetweenAsDecimal(ZonedDateTime begin, ZonedDateTime end) {
        return deltaBetweenAsDecimal(begin, end, null);
    }

    default Optional<JRuleValue> deltaBetween(ZonedDateTime begin, ZonedDateTime end) {
        return deltaBetween(begin, end, null);
    }

    Optional<JRuleValue> deltaBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> evolutionRate(ZonedDateTime timestamp) {
        return evolutionRate(timestamp, (String) null);
    }

    Optional<JRuleDecimalValue> evolutionRate(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> evolutionRateSince(ZonedDateTime timestamp) {
        return evolutionRateSince(timestamp, null);
    }

    Optional<JRuleDecimalValue> evolutionRateSince(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<JRuleDecimalValue> evolutionRateUntil(ZonedDateTime timestamp) {
        return evolutionRateUntil(timestamp, null);
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

    Optional<Long> countSince(ZonedDateTime begin, @Nullable String serviceId);

    default Optional<Long> countUntil(ZonedDateTime timestamp) {
        return countUntil(timestamp, null);
    }

    Optional<Long> countUntil(ZonedDateTime timestamp, @Nullable String serviceId);

    default Optional<Long> countBetween(ZonedDateTime begin, ZonedDateTime end) {
        return countBetween(begin, end, null);
    }

    Optional<Long> countBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId);

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

    default Optional<List<JRuleHistoricState>> getAllStatesBetween(ZonedDateTime begin, ZonedDateTime end) {
        return getAllStatesBetween(begin, end, null);
    }

    Optional<List<JRuleHistoricState>> getAllStatesBetween(ZonedDateTime begin, ZonedDateTime end,
            @Nullable String serviceId);

    default void removeAllStatesSince(ZonedDateTime timestamp) {
        removeAllStatesSince(timestamp, null);
    }

    void removeAllStatesSince(ZonedDateTime timestamp, @Nullable String serviceId);

    default void removeAllStatesUntil(ZonedDateTime timestamp) {
        removeAllStatesUntil(timestamp, null);
    }

    void removeAllStatesUntil(ZonedDateTime timestamp, @Nullable String serviceId);

    default void removeAllStatesBetween(ZonedDateTime begin, ZonedDateTime end) {
        removeAllStatesBetween(begin, end, null);
    }

    void removeAllStatesBetween(ZonedDateTime begin, ZonedDateTime end, @Nullable String serviceId);
}
