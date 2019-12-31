/* ===========================================================================
 * Copyright 2019 Trikzon
 *
 * Flora-Doubling is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * File: Config.java
 * Date: 2019-12-30
 * Revision:
 * Author: Trikzon
 * =========================================================================== */
package io.github.trikzon.floradoubling.fabric;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class Config {

    public static ConfigObject CONFIG;

    public static final File MOD_CONFIG_FILE = new File("./config/" + FloraDoubling.MOD_ID + ".json");

    public static void initConfig() {
        Gson gson = new Gson();
        try {
            CONFIG = gson.fromJson(new FileReader(MOD_CONFIG_FILE), ConfigObject.class);
        } catch (FileNotFoundException e) {
            CONFIG = save();
        }
    }

    private static ConfigObject save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ConfigObject defaultConfig = new ConfigObject(false);
        String config = gson.toJson(defaultConfig);

        try (FileWriter file = new FileWriter(MOD_CONFIG_FILE)) {
            file.write(config);
            file.flush();
        } catch (IOException e) {
            File dir = new File("./config/");
            dir.mkdir();
            save();
        }

        return defaultConfig;
    }

    static class ConfigObject {

        public boolean doubleWitherRose;

        public ConfigObject(boolean doubleWitherRose) {
            this.doubleWitherRose = doubleWitherRose;
        }
    }
}