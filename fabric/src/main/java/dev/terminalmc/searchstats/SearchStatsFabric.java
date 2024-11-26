/*
 * SearchStats by TerminalMC
 *
 * To the extent possible under law, the person who associated CC0 with
 * SearchStats has waived all copyright and related or neighboring rights
 * to SearchStats.
 *
 * You should have received a copy of the CC0 legalcode along with this
 * work. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package dev.terminalmc.searchstats;

import net.fabricmc.api.ClientModInitializer;

public class SearchStatsFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Main initialization
        SearchStats.init();
    }
}
