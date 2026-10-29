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
 * Adapter for Toyota Arene OS and Safety Sense V2X Connected Vehicle Architecture.
 * Bridges Toyota Mirai Hydrogen Fuel-Cell & bZ4X electric powertrains to Pi Network.
 */
public class ToyotaTelematicsAdapter implements OemTelemetryAdapter {

    private final CanBusCryptoEngine cryptoEngine;

    public ToyotaTelematicsAdapter(CanBusCryptoEngine cryptoEngine) {
        this.cryptoEngine = cryptoEngine;
    }

    @Override
    public OemBrand getOemBrand() {
        return OemBrand.TOYOTA;
    }

    @Override
    public String getPlatformName() {
        return "Toyota Woven Planet Arene OS / V2X Gateway";
    }

    @Override
    public VehicleProfile createProfile(String modelName, String vin, String propulsionType, double bondPi) {
        String did = cryptoEngine.generateVehicleDid("TOYOTA", vin);
        String vinHash = cryptoEngine.hashBytes(vin.getBytes());
        String hsmPub = "HSM-TOYOTA-PUB-" + UUID.randomUUID().toString().substring(0, 8);
        return new VehicleProfile(did, OemBrand.TOYOTA, modelName, vinHash, propulsionType, hsmPub, bondPi, 99.8, Instant.now());
    }

    @Override
    public TelematicsFrame generateFrame(VehicleProfile profile, double speedKmh, double socPercent, double odoKm, double regenKwh) {
        Instant now = Instant.now();
        String frameId = "FRM-TYT-" + UUID.randomUUID().toString().substring(0, 8);
        String arbId = "0x18F00500"; // Toyota Arene Powertrain Arbitration ID
        String proof = cryptoEngine.computeFrameProof(
            profile.vehicleDid(), now.toEpochMilli(), speedKmh, socPercent, odoKm, arbId);
        return new TelematicsFrame(
            frameId, profile.vehicleDid(), now,
            35.0844, 137.1560, // Toyota City Coordinates
            speedKmh, socPercent, regenKwh, odoKm, arbId, proof
        );
    }
}
