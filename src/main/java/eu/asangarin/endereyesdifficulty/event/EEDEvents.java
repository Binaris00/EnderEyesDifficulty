package eu.asangarin.endereyesdifficulty.event;

import eu.asangarin.endereyesdifficulty.EnderDifficulty;
import eu.asangarin.endereyesdifficulty.level.DifficultySaveData;
import eu.asangarin.endereyesdifficulty.util.CreateWorldScreenUtils;
import net.darkhax.gamestages.event.GameStageEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber(modid = EnderDifficulty.MODID)
public class EEDEvents {
//	@SubscribeEvent
//	public void onServerStart(ServerStartedEvent event) {
//		MinecraftServer server = event.getServer();
//		DifficultySaveData dsd = EnderDifficulty.getDifficultyData(server);
//		if(!dsd.isInitialized()) {
//			dsd.initialize(server.overworld().getDifficulty());
//			server.setDifficulty(Difficulty.HARD, true);
//		}
//	}

	@SubscribeEvent
	public void onCheckGameStage(GameStageEvent.Check event) {
		CreateWorldScreenUtils.Difficulty difficulty = switch (event.getStageName()) {
			case "nightmare" -> CreateWorldScreenUtils.Difficulty.NIGHTMARE;
			case "hard" -> CreateWorldScreenUtils.Difficulty.HARD;
			case "normal" -> CreateWorldScreenUtils.Difficulty.NORMAL;
			default -> null;
		};

		if(difficulty != null)
			event.setHasStage(checkDifficulty(difficulty));
	}

	private boolean checkDifficulty(CreateWorldScreenUtils.Difficulty difficulty) {
		DifficultySaveData dsd = EnderDifficulty.getDifficultyData(ServerLifecycleHooks.getCurrentServer());
		return dsd.getEnderDifficulty() == difficulty;
	}
}
