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

package io.autonet.pi.mesh;

import io.autonet.pi.model.OemBrand;
import io.autonet.pi.model.TelematicsFrame;
import io.autonet.pi.model.VehicleProfile;
import io.autonet.pi.telematics.CanBusCryptoEngine;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Decentralized registry managing cross-OEM connected vehicle identities,
 * active telematics feeds, and fleet bond status.
 */
public class AutomotiveMeshRegistry {

    private final CanBusCryptoEngine cryptoEngine;
    private final Map<String, VehicleProfile> vehicleProfiles = new ConcurrentHashMap<>();
    private final Map<String, TelematicsFrame> latestFrames = new ConcurrentHashMap<>();

    public AutomotiveMeshRegistry(CanBusCryptoEngine cryptoEngine) {
        this.cryptoEngine = cryptoEngine;
    }

    /**
     * Enrolls an OEM vehicle into the AutoNet mesh.
     */
    public boolean enrollVehicle(VehicleProfile profile) {
        if (profile == null) {
            return false;
        }
        if (!profile.isBondCompliant()) {
            System.err.printf("[AUTONET] Vehicle %s rejected: Insufficient fleet insurance bond (< 1.0 Pi)%n",
                profile.vehicleDid());
            return false;
        }
        vehicleProfiles.put(profile.vehicleDid(), profile);
        return true;
    }

    /**
     * Ingests and cryptographically validates a telematics frame.
     */
    public boolean ingestTelematicsFrame(TelematicsFrame frame) {
        if (frame == null) {
            return false;
        }
        if (!vehicleProfiles.containsKey(frame.vehicleDid())) {
            System.err.printf("[AUTONET] Telematics frame rejected: Unregistered vehicle %s%n",
                frame.vehicleDid());
            return false;
        }
        if (!cryptoEngine.verifyFrame(frame)) {
            System.err.printf("[AUTONET] Cryptographic validation failed for frame %s%n",
                frame.frameId());
            return false;
        }
        latestFrames.put(frame.vehicleDid(), frame);
        return true;
    }

    public Optional<VehicleProfile> getProfile(String vehicleDid) {
        return Optional.ofNullable(vehicleProfiles.get(vehicleDid));
    }

    public Optional<TelematicsFrame> getLatestFrame(String vehicleDid) {
        return Optional.ofNullable(latestFrames.get(vehicleDid));
    }

    public Map<String, VehicleProfile> getAllProfiles() {
        return Collections.unmodifiableMap(vehicleProfiles);
    }

    public int getTotalEnrolledVehicles() {
        return vehicleProfiles.size();
    }

    public double getTotalFleetBondPi() {
        return vehicleProfiles.values().stream()
            .mapToDouble(VehicleProfile::bondedInsurancePi)
            .sum();
    }

    public double getTotalFleetBondUsd() {
        return vehicleProfiles.values().stream()
            .mapToDouble(VehicleProfile::getBondedInsuranceUsd)
            .sum();
    }

    public Map<OemBrand, Integer> getOemDistribution() {
        Map<OemBrand, Integer> distribution = new EnumMap<>(OemBrand.class);
        for (VehicleProfile p : vehicleProfiles.values()) {
            distribution.merge(p.oemBrand(), 1, Integer::sum);
        }
        return distribution;
    }
}
