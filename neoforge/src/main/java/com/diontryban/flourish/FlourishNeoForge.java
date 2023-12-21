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

package com.diontryban.flourish;

import com.diontryban.ash_api.modloader.NeoForgeModInitializer;
import com.diontryban.flourish.client.FlourishClientNeoForge;
import net.neoforged.fml.common.Mod;

@Mod(Flourish.MOD_ID)
public class FlourishNeoForge extends NeoForgeModInitializer {
    public FlourishNeoForge() {
        super(Flourish.MOD_ID, Flourish::new, FlourishClientNeoForge::new);
    }
}
