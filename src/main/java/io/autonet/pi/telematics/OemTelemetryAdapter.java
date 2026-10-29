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

import io.autonet.pi.model.OemBrand;
import io.autonet.pi.model.TelematicsFrame;
import io.autonet.pi.model.VehicleProfile;

/**
 * Standard Telematics Abstraction Layer (TAL) adapter interface
 * implemented for each automotive OEM architecture.
 */
public interface OemTelemetryAdapter {

    /**
     * Target OEM brand.
     */
    OemBrand getOemBrand();

    /**
     * OEM proprietary onboard telematics platform / OS name.
     */
    String getPlatformName();

    /**
     * Creates a validated vehicle profile for this OEM.
     */
    VehicleProfile createProfile(String modelName, String vin, String propulsionType, double bondPi);

    /**
     * Ingests or synthesizes a cryptographically signed telematics frame.
     */
    TelematicsFrame generateFrame(VehicleProfile profile, double speedKmh, double socPercent, double odoKm, double regenKwh);
}
