package eu.asangarin.endereyesdifficulty.mixin;

import eu.asangarin.endereyesdifficulty.EnderDifficulty;
import eu.asangarin.endereyesdifficulty.level.DifficultySaveData;
import eu.asangarin.endereyesdifficulty.network.C2SDifficultyMessage;
import eu.asangarin.endereyesdifficulty.network.EEDNetworking;
import eu.asangarin.endereyesdifficulty.util.CreateWorldScreenUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(OptionsScreen.class)
public class MixinOptionsScreenEED {
	@Unique
	private static final Difficulty[] eed$DIFFICULTIES = {
			Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD
	};

	@Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/OptionsScreen;createDifficultyButton(IIILjava/lang/String;Lnet/minecraft/client/Minecraft;)Lnet/minecraft/client/gui/components/CycleButton;"))
	private CycleButton<Difficulty> initDifficultyButton(int i, int width, int height, String text, Minecraft minecraft) {
		return eed$createDifficultyButton(i, width, height, text);
	}

	@Unique
	private static CycleButton<Difficulty> eed$createDifficultyButton(int p_193847_, int p_193848_, int p_193849_, String p_193850_) {
		DifficultySaveData dsd = EnderDifficulty.getDifficultyData(ServerLifecycleHooks.getCurrentServer());

		return CycleButton.<Difficulty>builder(diff -> CreateWorldScreenUtils.Difficulty.fromVanilla(diff).getDisplayName()).withValues(eed$DIFFICULTIES).withInitialValue(dsd.getEnderDifficulty().toVanilla()).create(p_193848_ / 2 - 155 + p_193847_ % 2 * 160, p_193849_ / 6 - 12 + 24 * (p_193847_ >> 1), 150, 20, Component.translatable(p_193850_), (p_193854_, diff) -> EEDNetworking.INSTANCE.sendToServer(new C2SDifficultyMessage(CreateWorldScreenUtils.Difficulty.fromVanilla(diff))));
	}
}
