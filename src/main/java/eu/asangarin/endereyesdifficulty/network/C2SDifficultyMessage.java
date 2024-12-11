package eu.asangarin.endereyesdifficulty.network;

import eu.asangarin.endereyesdifficulty.EnderDifficulty;
import eu.asangarin.endereyesdifficulty.level.DifficultySaveData;
import eu.asangarin.endereyesdifficulty.util.CreateWorldScreenUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SDifficultyMessage {
	private final int difficultyId;

	public C2SDifficultyMessage(CreateWorldScreenUtils.Difficulty difficulty) {
		difficultyId = difficulty.getSaveId();
	}

	public C2SDifficultyMessage(FriendlyByteBuf buf) {
		difficultyId = buf.readInt();
	}

	public void toBytes(FriendlyByteBuf buf) {
		buf.writeInt(difficultyId);
	}

	public void handle(Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context ctx = supplier.get();
		ctx.enqueueWork(() -> {
			ServerPlayer player = ctx.getSender();
			if(player != null) {
				MinecraftServer server = player.getServer();
				if (server != null && (player.hasPermissions(2) || isSingleplayerOwner(player, server))) {
					DifficultySaveData dsd = EnderDifficulty.getDifficultyData(server);
					dsd.setDifficulty(CreateWorldScreenUtils.Difficulty.fromSaveId(difficultyId));
				}
			}
		});
		ctx.setPacketHandled(true);
	}

	private boolean isSingleplayerOwner(ServerPlayer player, MinecraftServer server) {
		return server.isSingleplayerOwner(player.getGameProfile());
	}
}
