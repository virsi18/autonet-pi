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

import io.autonet.pi.cli.AutoNetConsole;
import io.autonet.pi.mesh.AutomotiveMeshRegistry;
import io.autonet.pi.model.TelematicsFrame;
import io.autonet.pi.model.VehicleProfile;
import io.autonet.pi.settlement.V2XSettlementEngine;
import io.autonet.pi.telematics.CanBusCryptoEngine;
import io.autonet.pi.telematics.adapters.*;

/**
 * Entry point for the AutoNet-Pi Sovereign Automotive Telematics & V2X Mesh.
 */
public class Main {

    public static void main(String[] args) {
        String mode = args.length > 0 ? args[0].toLowerCase() : "demo";

        CanBusCryptoEngine cryptoEngine = new CanBusCryptoEngine();
        AutomotiveMeshRegistry registry = new AutomotiveMeshRegistry(cryptoEngine);
        V2XSettlementEngine settlementEngine = new V2XSettlementEngine(cryptoEngine);
        AutoNetConsole console = new AutoNetConsole();

        console.printBanner();

        // 1. Initialize Multi-OEM Telematics Adapters
        HondaTelematicsAdapter honda = new HondaTelematicsAdapter(cryptoEngine);
        YamahaTelematicsAdapter yamaha = new YamahaTelematicsAdapter(cryptoEngine);
        ToyotaTelematicsAdapter toyota = new ToyotaTelematicsAdapter(cryptoEngine);
        TeslaTelematicsAdapter tesla = new TeslaTelematicsAdapter(cryptoEngine);
        KawasakiTelematicsAdapter kawasaki = new KawasakiTelematicsAdapter(cryptoEngine);
        SuzukiTelematicsAdapter suzuki = new SuzukiTelematicsAdapter(cryptoEngine);

        // 2. Provision Fleet Profiles with GCV-Backed Insurance Bonds (1.0 Pi = $314,159 USD)
        VehicleProfile hondaCivic = honda.createProfile(
            "Honda Civic e:HEV Sensing", "JH4FL1H89PX1001", "HYBRID_ELECTRIC", 1.5);
        VehicleProfile yamahaMotoroid = yamaha.createProfile(
            "Yamaha Motoroid 2 Gyro-EV", "JYARN20E7PA0002", "MOTORCYCLE_ELECTRIC", 1.0);
        VehicleProfile toyotaMirai = toyota.createProfile(
            "Toyota Mirai FCEV Arene", "JTDFE1035PA0003", "HYDROGEN_FUEL_CELL", 2.0);
        VehicleProfile teslaModelS = tesla.createProfile(
            "Tesla Model S Plaid FSD", "5YJSA1E28PF0004", "BATTERY_ELECTRIC", 2.5);
        VehicleProfile kawasakiNinja = kawasaki.createProfile(
            "Kawasaki Ninja e-1 EV", "JKAZX10R9PA0005", "MOTORCYCLE_ELECTRIC", 1.0);
        VehicleProfile suzukiSwift = suzuki.createProfile(
            "Suzuki Swift Smart Hybrid", "JS2ZC33S8PA0006", "MILD_HYBRID", 1.0);

        // 3. Enroll into Automotive Mesh
        registry.enrollVehicle(hondaCivic);
        registry.enrollVehicle(yamahaMotoroid);
        registry.enrollVehicle(toyotaMirai);
        registry.enrollVehicle(teslaModelS);
        registry.enrollVehicle(kawasakiNinja);
        registry.enrollVehicle(suzukiSwift);

        // 4. Broadcast Cryptographically Verified Telematics Frames
        TelematicsFrame f1 = honda.generateFrame(hondaCivic, 72.5, 84.0, 14205.8, 18.2);
        TelematicsFrame f2 = yamaha.generateFrame(yamahaMotoroid, 48.0, 76.5, 3120.4, 6.4);
        TelematicsFrame f3 = toyota.generateFrame(toyotaMirai, 88.0, 92.0, 28450.1, 35.8);
        TelematicsFrame f4 = tesla.generateFrame(teslaModelS, 105.4, 68.0, 45210.9, 84.1);
        TelematicsFrame f5 = kawasaki.generateFrame(kawasakiNinja, 65.2, 81.0, 1890.3, 4.2);
        TelematicsFrame f6 = suzuki.generateFrame(suzukiSwift, 55.0, 89.5, 9650.7, 12.8);

        registry.ingestTelematicsFrame(f1);
        registry.ingestTelematicsFrame(f2);
        registry.ingestTelematicsFrame(f3);
        registry.ingestTelematicsFrame(f4);
        registry.ingestTelematicsFrame(f5);
        registry.ingestTelematicsFrame(f6);

        // 5. Execute Autonomous V2X Micropayments at GCV $314,159.00 USD
        // Honda: Autonomous Toll passing on Shuto Expressway ($2.40 USD)
        settlementEngine.settleToll(hondaCivic.vehicleDid(), "gantry:toll:shuto-c1-inner", 2.40);

        // Yamaha: Modular battery swap at Iwata Kiosk (3.5 kWh @ $0.22/kWh = $0.77 USD)
        settlementEngine.settleBatterySwap(yamahaMotoroid.vehicleDid(), "station:yamaha:kiosk-iwata-01", 3.5, 0.22);

        // Toyota: V2G peak grid injection (18.0 kWh @ $0.28/kWh = $5.04 USD credit)
        settlementEngine.settleV2gEnergyFeedIn(toyotaMirai.vehicleDid(), "substation:tepco:tokyo-bay-grid", 18.0, 0.28);

        // Tesla: Autonomous robotaxi fare clearance (14.2 km @ $1.50/km = $21.30 USD)
        settlementEngine.settleAutonomousTrip(teslaModelS.vehicleDid(), "did:chronos:pi:passenger_7721", 14.2, 1.50);

        // Kawasaki: Urban bridge toll crossing ($1.80 USD)
        settlementEngine.settleToll(kawasakiNinja.vehicleDid(), "gantry:toll:akashi-kaikyo-bridge", 1.80);

        // Suzuki: Micro-mobility battery swap credit (2.0 kWh @ $0.20/kWh = $0.40 USD)
        settlementEngine.settleBatterySwap(suzukiSwift.vehicleDid(), "station:suzuki:hamamatsu-hub-03", 2.0, 0.20);

        // 6. Render Terminal Telemetry HUD
        console.printFleetOverview(registry);
        console.printTelematicsStream(registry);
        console.printSettlementLedger(settlementEngine);

        System.out.println(AutoNetConsole.ANSI_CYAN + AutoNetConsole.ANSI_BOLD +
            ">>> AutoNet-Pi Mesh Active: Connected Fleets Harmonized under GCV $314,159 Sovereign Clearing. <<<" +
            AutoNetConsole.ANSI_RESET);
    }
}
