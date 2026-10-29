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

package io.autonet.pi.model;

import io.autonet.pi.constants.AutoConstants;
import java.time.Instant;
import java.util.Objects;

/**
 * An immutable ledger entry recording a verified V2X micro-settlement.
 */
public record SettlementRecord(
    String txId,
    String vehicleDid,
    String counterpartyId,
    SettlementType type,
    double amountPi,
    String unitsDescription,
    Instant timestamp,
    String settlementBlockHash
) {
    public SettlementRecord {
        Objects.requireNonNull(txId, "txId must not be null");
        Objects.requireNonNull(vehicleDid, "vehicleDid must not be null");
        Objects.requireNonNull(counterpartyId, "counterpartyId must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(unitsDescription, "unitsDescription must not be null");
        Objects.requireNonNull(settlementBlockHash, "settlementBlockHash must not be null");
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }

    /**
     * Valuation in USD based on Global Consensus Value ($314,159.00 USD / Pi).
     */
    public double getAmountUsd() {
        return amountPi * AutoConstants.GCV_VALUATION_USD;
    }

    /**
     * Micro-Pi amount (uPi = 10^-6 Pi).
     */
    public double getAmountMicroPi() {
        return amountPi * 1_000_000.0;
    }
}
