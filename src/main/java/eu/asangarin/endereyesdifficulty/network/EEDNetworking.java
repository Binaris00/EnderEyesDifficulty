package eu.asangarin.endereyesdifficulty.network;

import eu.asangarin.endereyesdifficulty.EnderDifficulty;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class EEDNetworking {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(EnderDifficulty.MODID, "main"), () -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	public static void register() {
		INSTANCE.messageBuilder(C2SDifficultyMessage.class, 0, NetworkDirection.PLAY_TO_SERVER)
				.decoder(C2SDifficultyMessage::new)
				.encoder(C2SDifficultyMessage::toBytes)
				.consumerMainThread(C2SDifficultyMessage::handle)
				.add();
	}
}
