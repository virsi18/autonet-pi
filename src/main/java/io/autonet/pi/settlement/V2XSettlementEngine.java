/*
 * Copyright 2026 AutoNet-Pi Sovereign Automotive Mesh Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.autonet.pi.settlement;

import io.autonet.pi.constants.AutoConstants;
import io.autonet.pi.model.SettlementRecord;
import io.autonet.pi.model.SettlementType;
import io.autonet.pi.telematics.CanBusCryptoEngine;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * High-throughput micro-settlement engine that clears V2X transactions
 * anchored to the Global Consensus Value ($314,159.00 USD / Pi).
 */
public class V2XSettlementEngine {

    private final CanBusCryptoEngine cryptoEngine;
    private final List<SettlementRecord> settlementLedger = new ArrayList<>();
    private String cumulativeLedgerHash = "0000000000000000000000000000000000000000000000000000000000000000";

    public V2XSettlementEngine(CanBusCryptoEngine cryptoEngine) {
        this.cryptoEngine = cryptoEngine;
    }

    /**
     * Converts a USD transaction value to the exact Pi denomination
     * under the Global Consensus Value (GCV $314,159.00 USD = 1.0 Pi).
     */
    public double convertUsdToPi(double usdAmount) {
        return usdAmount / AutoConstants.GCV_VALUATION_USD;
    }

    /**
     * Clears an autonomous highway or gantry toll crossing.
     */
    public synchronized SettlementRecord settleToll(String vehicleDid, String gantryId, double tollUsd) {
        double piAmount = convertUsdToPi(tollUsd);
        return recordSettlement(
            vehicleDid, gantryId, SettlementType.DYNAMIC_TOLL_CLEARANCE,
            piAmount, String.format("Toll Clearance: $%.2f USD", tollUsd)
        );
    }

    /**
     * Clears a modular battery swap (e.g. Honda Mobile Power Pack e: or Yamaha EV battery).
     */
    public synchronized SettlementRecord settleBatterySwap(String vehicleDid, String swapStationId, double kwhCapacity, double pricePerKwhUsd) {
        double totalUsd = kwhCapacity * pricePerKwhUsd;
        double piAmount = convertUsdToPi(totalUsd);
        return recordSettlement(
            vehicleDid, swapStationId, SettlementType.BATTERY_SWAP_SETTLEMENT,
            piAmount, String.format("%.2f kWh Swapped @ $%.2f/kWh", kwhCapacity, pricePerKwhUsd)
        );
    }

    /**
     * Clears Vehicle-to-Grid (V2G) regenerative energy feed-in credits paid to the vehicle.
     */
    public synchronized SettlementRecord settleV2gEnergyFeedIn(String vehicleDid, String gridSubstationId, double kwhInjected, double feedInTariffUsd) {
        double creditUsd = kwhInjected * feedInTariffUsd;
        double piAmount = convertUsdToPi(creditUsd);
        return recordSettlement(
            vehicleDid, gridSubstationId, SettlementType.V2G_ENERGY_INJECTION,
            piAmount, String.format("%.2f kWh Injected @ $%.2f/kWh", kwhInjected, feedInTariffUsd)
        );
    }

    /**
     * Clears autonomous fleet robotaxi passenger fare.
     */
    public synchronized SettlementRecord settleAutonomousTrip(String vehicleDid, String passengerDid, double distanceKm, double ratePerKmUsd) {
        double fareUsd = distanceKm * ratePerKmUsd;
        double piAmount = convertUsdToPi(fareUsd);
        return recordSettlement(
            vehicleDid, passengerDid, SettlementType.AUTONOMOUS_FLEET_ROUTING,
            piAmount, String.format("%.2f km Robotaxi Transit @ $%.2f/km", distanceKm, ratePerKmUsd)
        );
    }

    private SettlementRecord recordSettlement(String vehicleDid, String counterparty, SettlementType type, double amountPi, String desc) {
        String txId = "TX-V2X-" + UUID.randomUUID().toString().substring(0, 8);
        Instant now = Instant.now();
        String payload = String.format("%s:%s:%s:%.12f:%d:%s",
            cumulativeLedgerHash, txId, vehicleDid, amountPi, now.toEpochMilli(), type.name());
        cumulativeLedgerHash = cryptoEngine.hashBytes(payload.getBytes());

        SettlementRecord record = new SettlementRecord(
            txId, vehicleDid, counterparty, type, amountPi, desc, now, cumulativeLedgerHash
        );
        settlementLedger.add(record);
        return record;
    }

    public synchronized List<SettlementRecord> getSettlementLedger() {
        return Collections.unmodifiableList(settlementLedger);
    }

    public synchronized double getTotalSettledPi() {
        return settlementLedger.stream().mapToDouble(SettlementRecord::amountPi).sum();
    }

    public synchronized double getTotalSettledUsd() {
        return getTotalSettledPi() * AutoConstants.GCV_VALUATION_USD;
    }

    public synchronized String getCumulativeLedgerHash() {
        return cumulativeLedgerHash;
    }
}
