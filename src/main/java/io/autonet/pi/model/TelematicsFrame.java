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

import java.time.Instant;
import java.util.Objects;

/**
 * Standardized telematics frame broadcasted by connected vehicles over CAN-FD / C-V2X.
 */
public record TelematicsFrame(
    String frameId,
    String vehicleDid,
    Instant timestamp,
    double latitude,
    double longitude,
    double speedKmh,
    double stateOfChargePercent,
    double cumulativeRegenKwh,
    double odometerKm,
    String canBusArbitrationId,
    String cryptographicProof
) {
    public TelematicsFrame {
        Objects.requireNonNull(frameId, "frameId must not be null");
        Objects.requireNonNull(vehicleDid, "vehicleDid must not be null");
        Objects.requireNonNull(canBusArbitrationId, "canBusArbitrationId must not be null");
        Objects.requireNonNull(cryptographicProof, "cryptographicProof must not be null");
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }
}
