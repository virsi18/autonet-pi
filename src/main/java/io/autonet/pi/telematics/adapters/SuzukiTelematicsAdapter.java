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

package io.autonet.pi.telematics.adapters;

import io.autonet.pi.model.OemBrand;
import io.autonet.pi.model.TelematicsFrame;
import io.autonet.pi.model.VehicleProfile;
import io.autonet.pi.telematics.CanBusCryptoEngine;
import io.autonet.pi.telematics.OemTelemetryAdapter;
import java.time.Instant;
import java.util.UUID;

/**
 * Adapter for Suzuki Motor Corporation Smart Hybrid & S-CNG Eco-Matrix.
 * Provides micro-mobility telematics and high-efficiency urban transport proofs.
 */
public class SuzukiTelematicsAdapter implements OemTelemetryAdapter {

    private final CanBusCryptoEngine cryptoEngine;

    public SuzukiTelematicsAdapter(CanBusCryptoEngine cryptoEngine) {
        this.cryptoEngine = cryptoEngine;
    }

    @Override
    public OemBrand getOemBrand() {
        return OemBrand.SUZUKI;
    }

    @Override
    public String getPlatformName() {
        return "Suzuki Smart Hybrid & S-CNG Eco-Drive Platform";
    }

    @Override
    public VehicleProfile createProfile(String modelName, String vin, String propulsionType, double bondPi) {
        String did = cryptoEngine.generateVehicleDid("SUZUKI", vin);
        String vinHash = cryptoEngine.hashBytes(vin.getBytes());
        String hsmPub = "HSM-SUZUKI-PUB-" + UUID.randomUUID().toString().substring(0, 8);
        return new VehicleProfile(did, OemBrand.SUZUKI, modelName, vinHash, propulsionType, hsmPub, bondPi, 98.4, Instant.now());
    }

    @Override
    public TelematicsFrame generateFrame(VehicleProfile profile, double speedKmh, double socPercent, double odoKm, double regenKwh) {
        Instant now = Instant.now();
        String frameId = "FRM-SZK-" + UUID.randomUUID().toString().substring(0, 8);
        String arbId = "0x18F00700"; // Suzuki Powertrain Arbitration ID
        String proof = cryptoEngine.computeFrameProof(
            profile.vehicleDid(), now.toEpochMilli(), speedKmh, socPercent, odoKm, arbId);
        return new TelematicsFrame(
            frameId, profile.vehicleDid(), now,
            34.7042, 137.7348, // Hamamatsu Coordinates
            speedKmh, socPercent, regenKwh, odoKm, arbId, proof
        );
    }
}
