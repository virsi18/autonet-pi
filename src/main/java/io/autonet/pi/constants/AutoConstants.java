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

package io.autonet.pi.constants;

import java.math.BigDecimal;

/**
 * Core constants, mathematical anchors, and OEM telemetry standards
 * for the AutoNet-Pi autonomous vehicular settlement mesh.
 */
public final class AutoConstants {

    private AutoConstants() {
        // Prevent instantiation
    }

    /**
     * Sovereign Global Consensus Value (GCV) economic anchor per 1.0 Pi.
     */
    public static final double GCV_VALUATION_USD = 314159.00;
    public static final BigDecimal GCV_BIGDECIMAL = BigDecimal.valueOf(GCV_VALUATION_USD);

    /**
     * Micro-Pi (10^-6 Pi) purchasing power equivalent in USD:
     * 1 uPi = $0.314159 USD
     */
    public static final double MICRO_PI_USD_VALUE = 0.314159;

    /**
     * Nano-Pi (10^-9 Pi) purchasing power equivalent in USD:
     * 1 nPi = $0.000314159 USD (Ideal for millisecond V2X micro-billing)
     */
    public static final double NANO_PI_USD_VALUE = 0.000314159;

    /**
     * AutoNet Protocol Specification & Release Version.
     */
    public static final String PROTOCOL_VERSION = "v1.0.0-PROD";
    public static final String NETWORK_NAME = "Pi-AutoNet-Sovereign-Mesh";

    /**
     * Standard CAN Bus 2.0B / CAN-FD Arbitrated Frame Size (bytes).
     */
    public static final int CAN_FRAME_MAX_PAYLOAD = 64;

    /**
     * V2X Communication standard IEEE 802.11p WAVE / C-V2X PC5 direct link band.
     */
    public static final String V2X_STANDARD = "3GPP C-V2X Rel-16 / IEEE 802.11bd";

    /**
     * Standard Minimum Bond Required per Autonomous Commercial Fleet Vehicle (in Pi).
     * 1.0 Pi = $314,159.00 USD bonded liability insurance reserve.
     */
    public static final double MIN_FLEET_BOND_PI = 1.0;
}
