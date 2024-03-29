/*
 * This file is part of Flourish.
 * A copy of this program can be found at https://github.com/Trikzon/flourish.
 * Copyright (C) 2023 Dion Tryban
 *
 * Flourish is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Flourish is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Flourish. If not, see <https://www.gnu.org/licenses/>.
 */

package com.diontryban.flourish.options;

import com.diontryban.ash_api.options.ModOptions;
import com.google.gson.annotations.SerializedName;

public class FlourishOptions extends ModOptions {
    @SerializedName("wither_rose")
    public boolean witherRose = false;
    public boolean torchflower = false;
    @SerializedName("use_tall_flower_behavior")
    public boolean useTallFlowerBehavior = false;

    @Override
    protected int getVersion() {
        return 1;
    }
}
