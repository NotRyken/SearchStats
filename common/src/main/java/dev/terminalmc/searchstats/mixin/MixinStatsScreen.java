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

package dev.terminalmc.searchstats.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.terminalmc.searchstats.SearchStats;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StatsScreen.class)
public abstract class MixinStatsScreen extends Screen {
    @Shadow
    @Nullable
    private ObjectSelectionList<?> activeList;
    @Shadow
    @Nullable
    private StatsScreen.GeneralStatisticsList statsList;
    @Shadow
    @Nullable
    StatsScreen.ItemStatisticsList itemStatsList;
    @Shadow
    @Nullable
    private StatsScreen.MobsStatisticsList mobsStatsList;
    @Unique
    private EditBox searchstats$searchField;
    
    @Shadow
    public abstract void setActiveList(@Nullable ObjectSelectionList<?> activeList);

    @Shadow
    public abstract void initLists();

    public MixinStatsScreen(Component text) {
        super(text);
    }

    @Inject(
            method = "init", 
            at = @At("RETURN")
    )
    private void resetSearch(CallbackInfo ci) {
        SearchStats.setSearchString("");
    }
    
    @WrapOperation(
            method = "initButtons",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/Button;builder(Lnet/minecraft/network/chat/Component;Lnet/minecraft/client/gui/components/Button$OnPress;)Lnet/minecraft/client/gui/components/Button$Builder;"
            )
    )
    private Button.Builder wrapBuilder(Component message, Button.OnPress onPress, Operation<Button.Builder> original) {
        // Clear filter when switching between lists
        return Button.builder(message, (button) -> {
            ObjectSelectionList<?> oldActiveList = activeList;
            onPress.onPress(button);
            if (searchstats$searchField != null && activeList != oldActiveList) {
                searchstats$searchField.setValue("");
                SearchStats.setSearchString(searchstats$searchField.getValue());
                searchstats$recreateStatsLists();
            }
        });
    }
    
    @WrapOperation(
            method = "initButtons", 
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/layouts/HeaderAndFooterLayout;addTitleHeader(Lnet/minecraft/network/chat/Component;Lnet/minecraft/client/gui/Font;)V"
            )
    )
    public void createSearchField(HeaderAndFooterLayout instance, Component message, 
                                  Font font, Operation<Void> original) {
        // Horizontal linearlayout with the screen title and search field
        int height = 18;
        
        LinearLayout layout = instance.addToHeader(LinearLayout.horizontal()).spacing(5);
        layout.addChild(new StringWidget(font.width(message.getString()), height, message, font));
        
        searchstats$searchField = new EditBox(font, 145, height, Component.empty());
        searchstats$searchField.setCanLoseFocus(false);
        searchstats$searchField.setFocused(true);
        layout.addChild(searchstats$searchField);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchstats$searchField != null && searchstats$searchField.keyPressed(keyCode, scanCode, modifiers)) {
            SearchStats.setSearchString(searchstats$searchField.getValue());
            searchstats$recreateStatsLists();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (searchstats$searchField != null && searchstats$searchField.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }    
    
    @Override
    public boolean charTyped(char chr, int keyCode) {
        if (searchstats$searchField.charTyped(chr, keyCode)) {
            SearchStats.setSearchString(searchstats$searchField.getValue());
            searchstats$recreateStatsLists();
            return true;
        }
        return super.charTyped(chr, keyCode);
    }
    
    @Unique
    private void searchstats$recreateStatsLists() {
        if (activeList == statsList) {
            initLists();
            setActiveList(statsList);
        } else if (activeList == itemStatsList) {
            initLists();
            setActiveList(itemStatsList);
        } else if (activeList == mobsStatsList) {
            initLists();
            setActiveList(mobsStatsList);
        }
    }
}
