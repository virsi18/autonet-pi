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
 * Categorization of autonomous vehicular settlements processed by AutoNet-Pi.
 */
public enum SettlementType {
    DYNAMIC_TOLL_CLEARANCE("Highway & Bridge Autonomous Gantry Toll"),
    BATTERY_SWAP_SETTLEMENT("Modular Battery Swapping Credit Exchange"),
    V2G_ENERGY_INJECTION("Vehicle-to-Grid (V2G) Energy Feed-in Tariff"),
    AUTONOMOUS_FLEET_ROUTING("On-Demand Robotaxi Autonomous Fare Clearance"),
    LANE_PRIORITY_AUCTION("Dynamic High-Occupancy / Fast-Lane Micro-Auction");

    private final String description;

    SettlementType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
