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
 * Encapsulates a cryptographically certified vehicle identity in the AutoNet-Pi ecosystem.
 */
public record VehicleProfile(
    String vehicleDid,
    OemBrand oemBrand,
    String modelName,
    String vinHash,
    String propulsionType,
    String hsmPublicKey,
    double bondedInsurancePi,
    double reputationScore,
    Instant registrationTimestamp
) {
    public VehicleProfile {
        Objects.requireNonNull(vehicleDid, "vehicleDid must not be null");
        Objects.requireNonNull(oemBrand, "oemBrand must not be null");
        Objects.requireNonNull(modelName, "modelName must not be null");
        Objects.requireNonNull(vinHash, "vinHash must not be null");
        Objects.requireNonNull(propulsionType, "propulsionType must not be null");
        Objects.requireNonNull(hsmPublicKey, "hsmPublicKey must not be null");
        if (registrationTimestamp == null) {
            registrationTimestamp = Instant.now();
        }
    }

    /**
     * Calculates the USD bonded collateral value based on GCV ($314,159.00 USD / Pi).
     */
    public double getBondedInsuranceUsd() {
        return bondedInsurancePi * AutoConstants.GCV_VALUATION_USD;
    }

    public boolean isBondCompliant() {
        return bondedInsurancePi >= AutoConstants.MIN_FLEET_BOND_PI;
    }
}
