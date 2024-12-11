package eu.asangarin.endereyesdifficulty;

import com.mojang.logging.LogUtils;
import eu.asangarin.endereyesdifficulty.event.EEDEvents;
import eu.asangarin.endereyesdifficulty.event.WorldLoadListener;
import eu.asangarin.endereyesdifficulty.level.DifficultySaveData;
import eu.asangarin.endereyesdifficulty.network.EEDNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EnderDifficulty.MODID)
public class EnderDifficulty {
	public static final String MODID = "enderdifficulty";
	public static final Logger LOGGER = LogUtils.getLogger();

	public EnderDifficulty() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::commonSetup);
		MinecraftForge.EVENT_BUS.register(new EEDEvents());
		MinecraftForge.EVENT_BUS.register(new WorldLoadListener());
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		EEDNetworking.register();
	}

	public static DifficultySaveData getDifficultyData(MinecraftServer server) {
		return server.overworld().getDataStorage().computeIfAbsent(DifficultySaveData::load, DifficultySaveData::createNew, "enderdifficulty");
	}
}
