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

package io.autonet.pi.telematics;

import io.autonet.pi.model.TelematicsFrame;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Cryptographic engine providing anti-tampering verification, frame hashing,
 * and provenance proofs for CAN bus and V2X telemetry.
 */
public class CanBusCryptoEngine {

    private final MessageDigest sha256;

    public CanBusCryptoEngine() {
        try {
            this.sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available in runtime", e);
        }
    }

    /**
     * Computes the SHA-256 hash of arbitrary byte inputs.
     */
    public synchronized String hashBytes(byte[] input) {
        sha256.reset();
        byte[] digest = sha256.digest(input);
        return HexFormat.of().formatHex(digest);
    }

    /**
     * Computes the cryptographic fingerprint of a telematics frame.
     */
    public String computeFrameProof(String vehicleDid, long epochMillis, double speed, double soc, double odo, String arbId) {
        String canonical = String.format("%s:%d:%.2f:%.2f:%.2f:%s:AUTONET_SALT_2026",
            vehicleDid, epochMillis, speed, soc, odo, arbId);
        return hashBytes(canonical.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Validates whether a telematics frame has valid cryptographic integrity.
     */
    public boolean verifyFrame(TelematicsFrame frame) {
        if (frame == null || frame.cryptographicProof() == null) {
            return false;
        }
        String expected = computeFrameProof(
            frame.vehicleDid(),
            frame.timestamp().toEpochMilli(),
            frame.speedKmh(),
            frame.stateOfChargePercent(),
            frame.odometerKm(),
            frame.canBusArbitrationId()
        );
        return expected.equalsIgnoreCase(frame.cryptographicProof());
    }

    /**
     * Generates a deterministic Vehicle DID from OEM brand and VIN.
     */
    public String generateVehicleDid(String oemCode, String vin) {
        String vinHash = hashBytes(vin.getBytes(StandardCharsets.UTF_8)).substring(0, 16);
        return "did:pi:auto:" + oemCode.toLowerCase() + ":" + vinHash;
    }
}
