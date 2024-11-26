/*
 * Copyright 2021 Guntram Blohm
 * Copyright 2024 TerminalMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.terminalmc.searchstats;

import dev.terminalmc.searchstats.util.ModLogger;

public class SearchStats {
    public static final String MOD_ID = "searchstats";
    public static final String MOD_NAME = "SearchStats";
    public static final ModLogger LOG = new ModLogger(MOD_NAME);

    private static String searchString = "";

    public static void setSearchString(String s) { 
        searchString = s.toLowerCase(); 
    }
    public static String getSearchString() { 
        return searchString; 
    }

    public static void init() {
    }
}
