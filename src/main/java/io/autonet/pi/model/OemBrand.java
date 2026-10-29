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

/**
 * Enumeration of supported Automotive Original Equipment Manufacturers (OEMs)
 * participating in the AutoNet-Pi V2X telematics and mobility settlement mesh.
 */
public enum OemBrand {
    HONDA(
        "Honda Motor Co., Ltd.",
        "Honda e:Architecture & Honda Sensing 360",
        "Mobile Power Pack e: Swappable Energy Protocol"
    ),
    YAMAHA(
        "Yamaha Motor Co., Ltd.",
        "Motoroid AI Gyro-Kinetic Autonomous Dynamics",
        "Y-Connect Intelligent Powertrain Telemetry"
    ),
    TOYOTA(
        "Toyota Motor Corporation",
        "Toyota Arene OS & Safety Sense V2X Gateway",
        "Mirai H2 Fuel-Cell / bZ4X Regenerative Grid Mesh"
    ),
    TESLA(
        "Tesla, Inc.",
        "Full Self-Driving (FSD) Autonomous Robotaxi Fleet",
        "Megapack Bi-directional V2G Supercharger Exchange"
    ),
    KAWASAKI(
        "Kawasaki Heavy Industries",
        "Ninja e-1 Rideology Telematics Telemetry Engine",
        "High-Torque Zero-Emission Kinetic Harvester"
    ),
    SUZUKI(
        "Suzuki Motor Corporation",
        "Smart Hybrid & S-CNG Eco-Matrix Platform",
        "Ultra-Lightweight Micro-Mobility Urban Mesh"
    ),
    BMW(
        "Bayerische Motoren Werke AG",
        "BMW ConnectedDrive eDrive Gen6 Architecture",
        "High-Performance Autonomous Dynamic Edge"
    ),
    HYUNDAI(
        "Hyundai Motor Company",
        "E-GMP 800V Ultra-Fast Bi-Directional V2X",
        "Autonomous Ioniq Robotaxi Urban Grid Dispatch"
    );

    private final String manufacturer;
    private final String telematicsArchitecture;
    private final String energyProtocol;

    OemBrand(String manufacturer, String telematicsArchitecture, String energyProtocol) {
        this.manufacturer = manufacturer;
        this.telematicsArchitecture = telematicsArchitecture;
        this.energyProtocol = energyProtocol;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getTelematicsArchitecture() {
        return telematicsArchitecture;
    }

    public String getEnergyProtocol() {
        return energyProtocol;
    }

    @Override
    public String toString() {
        return name() + " (" + manufacturer + ")";
    }
}
