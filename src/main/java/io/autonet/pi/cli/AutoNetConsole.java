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

package io.autonet.pi.cli;

import io.autonet.pi.constants.AutoConstants;
import io.autonet.pi.mesh.AutomotiveMeshRegistry;
import io.autonet.pi.model.SettlementRecord;
import io.autonet.pi.model.TelematicsFrame;
import io.autonet.pi.model.VehicleProfile;
import io.autonet.pi.settlement.V2XSettlementEngine;
import java.util.List;

/**
 * Terminal HUD and Telemetry Console for AutoNet-Pi.
 */
public class AutoNetConsole {

    public static final String ANSI_RESET  = "\u001B[0m";
    public static final String ANSI_CYAN   = "\u001B[36m";
    public static final String ANSI_GREEN  = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE   = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_BOLD   = "\u001B[1m";

    public void printBanner() {
        System.out.println(ANSI_CYAN + ANSI_BOLD);
        System.out.println("   ___         _          _  _       _         ___  _ ");
        System.out.println("  /   \\ _   _ | |_  ___  | \\| | ___ | |_      | _ \\(_) ");
        System.out.println(" / /\\ /| | | || __|/ _ \\ | .` |/ -_)|  _|  _  |  _/| |");
        System.out.println("/_/  \\_\\\\_,_/ \\__|\\___/ |_|\\_|\\___| \\__| (_) |_|  |_|");
        System.out.println(" >> AUTONOMOUS V2X TELEMATICS & MULTI-OEM MOBILITY MESH <<");
        System.out.println(" >> HONDA | YAMAHA | TOYOTA | TESLA | KAWASAKI | SUZUKI <<");
        System.out.println(ANSI_RESET);
    }

    public void printFleetOverview(AutomotiveMeshRegistry registry) {
        System.out.println(ANSI_PURPLE + ANSI_BOLD + "=== REGISTERED MULTI-OEM FLEET MATRIX ===" + ANSI_RESET);
        System.out.printf("%-10s | %-24s | %-28s | %-12s | %-16s%n",
            "OEM", "Model Name", "Vehicle DID", "Propulsion", "Bond Collateral");
        System.out.println("-".repeat(98));

        for (VehicleProfile p : registry.getAllProfiles().values()) {
            System.out.printf("%-10s | %-24s | %-28s | %-12s | %6.2f Pi ($%,.0f)%n",
                p.oemBrand().name(),
                p.modelName(),
                truncate(p.vehicleDid(), 28),
                p.propulsionType(),
                p.bondedInsurancePi(),
                p.getBondedInsuranceUsd());
        }
        System.out.println("-".repeat(98));
        System.out.printf("Total Enrolled Fleet: %d vehicles | Aggregate Bond: %.2f Pi ($%,.2f USD at GCV $314,159)%n%n",
            registry.getTotalEnrolledVehicles(),
            registry.getTotalFleetBondPi(),
            registry.getTotalFleetBondUsd());
    }

    public void printTelematicsStream(AutomotiveMeshRegistry registry) {
        System.out.println(ANSI_GREEN + ANSI_BOLD + "=== LIVE CAN BUS CRYPTOGRAPHIC TELEMETRY STREAM ===" + ANSI_RESET);
        System.out.printf("%-14s | %-28s | %-9s | %-7s | %-10s | %-18s%n",
            "Frame ID", "Vehicle DID", "Speed", "SoC %", "Odometer", "Cryptographic Proof");
        System.out.println("-".repeat(98));

        for (VehicleProfile p : registry.getAllProfiles().values()) {
            registry.getLatestFrame(p.vehicleDid()).ifPresent(f -> {
                System.out.printf("%-14s | %-28s | %6.1f km | %5.1f%% | %8.1f km | %s...%n",
                    f.frameId(),
                    truncate(f.vehicleDid(), 28),
                    f.speedKmh(),
                    f.stateOfChargePercent(),
                    f.odometerKm(),
                    truncate(f.cryptographicProof(), 16));
            });
        }
        System.out.println("-".repeat(98) + "\n");
    }

    public void printSettlementLedger(V2XSettlementEngine engine) {
        System.out.println(ANSI_YELLOW + ANSI_BOLD + "=== V2X AUTONOMOUS SETTLEMENT CLEARING (GCV $314,159.00 USD ANCHOR) ===" + ANSI_RESET);
        System.out.printf("%-14s | %-26s | %-22s | %-15s | %-12s%n",
            "Tx ID", "Vehicle DID", "Type", "Settled (uPi)", "USD Equiv");
        System.out.println("-".repeat(98));

        List<SettlementRecord> records = engine.getSettlementLedger();
        for (SettlementRecord r : records) {
            System.out.printf("%-14s | %-26s | %-22s | %10.4f uPi | $%,8.4f%n",
                r.txId(),
                truncate(r.vehicleDid(), 26),
                truncate(r.type().name(), 22),
                r.getAmountMicroPi(),
                r.getAmountUsd());
        }
        System.out.println("-".repeat(98));
        System.out.printf("Total Cleared: %.8f Pi (%d transactions) | Total Fiat Volume: $%,.4f USD%n",
            engine.getTotalSettledPi(),
            records.size(),
            engine.getTotalSettledUsd());
        System.out.printf("Ledger Cumulative Hash: %s%n%n", engine.getCumulativeLedgerHash());
    }

    private static String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() <= maxLen ? text : text.substring(0, maxLen - 3) + "...";
    }
}
