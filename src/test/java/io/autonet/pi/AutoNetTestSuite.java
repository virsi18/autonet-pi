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

package io.autonet.pi;

import io.autonet.pi.constants.AutoConstants;
import io.autonet.pi.mesh.AutomotiveMeshRegistry;
import io.autonet.pi.model.OemBrand;
import io.autonet.pi.model.SettlementRecord;
import io.autonet.pi.model.TelematicsFrame;
import io.autonet.pi.model.VehicleProfile;
import io.autonet.pi.settlement.V2XSettlementEngine;
import io.autonet.pi.telematics.CanBusCryptoEngine;
import io.autonet.pi.telematics.adapters.*;

/**
 * Self-contained unit test suite verifying AutoNet-Pi cryptographic integrity,
 * multi-OEM adapter telemetry, and GCV $314,159 settlement mathematics.
 */
public class AutoNetTestSuite {

    private static int testsPassed = 0;
    private static int testsTotal = 0;

    public static void main(String[] args) {
        System.out.println("Running AutoNet-Pi Verification Test Suite...");

        testGcvConversionMathematics();
        testCanBusCryptoAndTamperResistance();
        testMultiOemAdapters();
        testMeshRegistryAndBondEnforcement();
        testV2xMicroSettlementClearing();
        testCumulativeLedgerIntegrity();

        System.out.println("----------------------------------------------------------------------");
        System.out.printf("Test Execution Complete: %d / %d tests passed successfully.%n", testsPassed, testsTotal);
        if (testsPassed != testsTotal) {
            System.exit(1);
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        testsTotal++;
        if (condition) {
            testsPassed++;
            System.out.println(" [PASS] " + testName);
        } else {
            System.err.println(" [FAIL] " + testName);
            throw new AssertionError("Test failed: " + testName);
        }
    }

    private static void testGcvConversionMathematics() {
        CanBusCryptoEngine crypto = new CanBusCryptoEngine();
        V2XSettlementEngine engine = new V2XSettlementEngine(crypto);

        // $314,159.00 USD must equal 1.0 Pi
        double piVal = engine.convertUsdToPi(AutoConstants.GCV_VALUATION_USD);
        assertTrue("GCV Conversion: $314,159.00 USD == 1.0 Pi", Math.abs(piVal - 1.0) < 1e-9);

        // $0.314159 USD must equal ~1 micro-Pi (10^-6 Pi)
        double microPiVal = engine.convertUsdToPi(0.314159);
        assertTrue("GCV Conversion: $0.314159 USD == 1 uPi", Math.abs(microPiVal - 1e-6) < 1e-9);
    }

    private static void testCanBusCryptoAndTamperResistance() {
        CanBusCryptoEngine crypto = new CanBusCryptoEngine();
        String vehicleDid = "did:pi:auto:honda:test1234";
        long epoch = System.currentTimeMillis();
        String arbId = "0x18F00100";

        String validProof = crypto.computeFrameProof(vehicleDid, epoch, 60.0, 80.0, 1000.0, arbId);
        TelematicsFrame validFrame = new TelematicsFrame(
            "F1", vehicleDid, java.time.Instant.ofEpochMilli(epoch), 0.0, 0.0, 60.0, 80.0, 10.0, 1000.0, arbId, validProof);
        assertTrue("Crypto Engine: Valid Frame Verification", crypto.verifyFrame(validFrame));

        // Tamper with speed
        TelematicsFrame tamperedFrame = new TelematicsFrame(
            "F1", vehicleDid, java.time.Instant.ofEpochMilli(epoch), 0.0, 0.0, 120.0, 80.0, 10.0, 1000.0, arbId, validProof);
        assertTrue("Crypto Engine: Tampered Speed Rejected", !crypto.verifyFrame(tamperedFrame));
    }

    private static void testMultiOemAdapters() {
        CanBusCryptoEngine crypto = new CanBusCryptoEngine();
        HondaTelematicsAdapter honda = new HondaTelematicsAdapter(crypto);
        YamahaTelematicsAdapter yamaha = new YamahaTelematicsAdapter(crypto);
        ToyotaTelematicsAdapter toyota = new ToyotaTelematicsAdapter(crypto);
        TeslaTelematicsAdapter tesla = new TeslaTelematicsAdapter(crypto);
        KawasakiTelematicsAdapter kawasaki = new KawasakiTelematicsAdapter(crypto);
        SuzukiTelematicsAdapter suzuki = new SuzukiTelematicsAdapter(crypto);

        assertTrue("OEM Brand Check: Honda", honda.getOemBrand() == OemBrand.HONDA);
        assertTrue("OEM Brand Check: Yamaha", yamaha.getOemBrand() == OemBrand.YAMAHA);
        assertTrue("OEM Brand Check: Toyota", toyota.getOemBrand() == OemBrand.TOYOTA);
        assertTrue("OEM Brand Check: Tesla", tesla.getOemBrand() == OemBrand.TESLA);
        assertTrue("OEM Brand Check: Kawasaki", kawasaki.getOemBrand() == OemBrand.KAWASAKI);
        assertTrue("OEM Brand Check: Suzuki", suzuki.getOemBrand() == OemBrand.SUZUKI);
    }

    private static void testMeshRegistryAndBondEnforcement() {
        CanBusCryptoEngine crypto = new CanBusCryptoEngine();
        AutomotiveMeshRegistry registry = new AutomotiveMeshRegistry(crypto);
        HondaTelematicsAdapter honda = new HondaTelematicsAdapter(crypto);

        // Compliant profile (bond >= 1.0 Pi)
        VehicleProfile compliant = honda.createProfile("Civic", "VIN1", "HEV", 1.0);
        assertTrue("Registry: Compliant Bond Accepted", registry.enrollVehicle(compliant));

        // Non-compliant profile (bond < 1.0 Pi)
        VehicleProfile underbonded = honda.createProfile("Fit", "VIN2", "HEV", 0.5);
        assertTrue("Registry: Under-bonded Vehicle Rejected", !registry.enrollVehicle(underbonded));
    }

    private static void testV2xMicroSettlementClearing() {
        CanBusCryptoEngine crypto = new CanBusCryptoEngine();
        V2XSettlementEngine engine = new V2XSettlementEngine(crypto);

        SettlementRecord toll = engine.settleToll("did:pi:auto:honda:1", "gantry:1", 3.14159);
        assertTrue("V2X Settlement: Toll Micro-Pi Amount (10 uPi)", Math.abs(toll.getAmountMicroPi() - 10.0) < 1e-3);
        assertTrue("V2X Settlement: Toll USD Valuation ($3.14 USD)", Math.abs(toll.getAmountUsd() - 3.14159) < 1e-3);

        SettlementRecord swap = engine.settleBatterySwap("did:pi:auto:yamaha:1", "kiosk:1", 4.0, 0.25);
        assertTrue("V2X Settlement: Battery Swap Amount ($1.00 USD)", Math.abs(swap.getAmountUsd() - 1.00) < 1e-3);
    }

    private static void testCumulativeLedgerIntegrity() {
        CanBusCryptoEngine crypto = new CanBusCryptoEngine();
        V2XSettlementEngine engine = new V2XSettlementEngine(crypto);

        engine.settleToll("did:pi:auto:honda:1", "gantry:1", 2.0);
        String hash1 = engine.getCumulativeLedgerHash();

        engine.settleToll("did:pi:auto:toyota:1", "gantry:2", 4.0);
        String hash2 = engine.getCumulativeLedgerHash();

        assertTrue("Ledger Hash Evolves deterministically", !hash1.equals(hash2));
        assertTrue("Ledger Records Count == 2", engine.getSettlementLedger().size() == 2);
    }
}
