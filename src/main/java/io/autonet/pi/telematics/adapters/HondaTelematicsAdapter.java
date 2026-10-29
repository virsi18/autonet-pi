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
 * Adapter for Honda e:Architecture and Honda Sensing 360 Telematics Gateway.
 * Supports Honda Mobile Power Pack e: battery swapping telemetry.
 */
public class HondaTelematicsAdapter implements OemTelemetryAdapter {

    private final CanBusCryptoEngine cryptoEngine;

    public HondaTelematicsAdapter(CanBusCryptoEngine cryptoEngine) {
        this.cryptoEngine = cryptoEngine;
    }

    @Override
    public OemBrand getOemBrand() {
        return OemBrand.HONDA;
    }

    @Override
    public String getPlatformName() {
        return "Honda e:Architecture / Honda Sensing 360 Core";
    }

    @Override
    public VehicleProfile createProfile(String modelName, String vin, String propulsionType, double bondPi) {
        String did = cryptoEngine.generateVehicleDid("HONDA", vin);
        String vinHash = cryptoEngine.hashBytes(vin.getBytes());
        String hsmPub = "HSM-HONDA-PUB-" + UUID.randomUUID().toString().substring(0, 8);
        return new VehicleProfile(did, OemBrand.HONDA, modelName, vinHash, propulsionType, hsmPub, bondPi, 99.4, Instant.now());
    }

    @Override
    public TelematicsFrame generateFrame(VehicleProfile profile, double speedKmh, double socPercent, double odoKm, double regenKwh) {
        Instant now = Instant.now();
        String frameId = "FRM-HND-" + UUID.randomUUID().toString().substring(0, 8);
        String arbId = "0x18F00100"; // Honda e:Powertrain CAN-FD arbitration ID
        String proof = cryptoEngine.computeFrameProof(
            profile.vehicleDid(), now.toEpochMilli(), speedKmh, socPercent, odoKm, arbId);
        return new TelematicsFrame(
            frameId, profile.vehicleDid(), now,
            35.6895, 139.6917, // Tokyo Metropolitan Coordinates
            speedKmh, socPercent, regenKwh, odoKm, arbId, proof
        );
    }
}
