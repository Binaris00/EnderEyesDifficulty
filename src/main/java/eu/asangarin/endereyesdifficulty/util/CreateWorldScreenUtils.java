package eu.asangarin.endereyesdifficulty.util;

import net.minecraft.network.chat.Component;

@SuppressWarnings("CallToPrintStackTrace")
public class CreateWorldScreenUtils {
	public enum Gamemode {
		SURVIVAL,
		CREATIVE,
		HARDCORE
	}

	public enum Difficulty {
		NORMAL(0),
		HARD(1),
		NIGHTMARE(2);

		private final String id;
		private final int saveId;

		Difficulty(int saveId) {
			this.id = name().toLowerCase();
			this.saveId = saveId;
		}

		public int getSaveId() {
			return saveId;
		}

		public net.minecraft.world.Difficulty toVanilla() {
			return switch (this) {
				case NIGHTMARE -> net.minecraft.world.Difficulty.HARD;
				case HARD -> net.minecraft.world.Difficulty.NORMAL;
				default -> net.minecraft.world.Difficulty.EASY;
			};
		}

		public static Difficulty fromSaveId(int saveId) {
			return switch (saveId) {
				case 1 -> HARD;
				case 2 -> NIGHTMARE;
				default -> NORMAL;
			};
		}

		public static Difficulty fromVanilla(net.minecraft.world.Difficulty diff) {
			return switch (diff) {
				case NORMAL -> HARD;
				case HARD -> NIGHTMARE;
				default -> NORMAL;
			};
		}

		public Component getDisplayName() {
			return Component.translatable("endereyes.difficulty." + id + ".name");
		}
	}

	public enum Direction {
		LEFT,
		RIGHT
	}

	public static Object getVanillaGameModeEnumElement(Gamemode gm) {
		try {
			Class<?> c = getVanillaGameModeEnumClass();
			assert c != null;
			Object[] enumElements = c.getEnumConstants();
			if (gm == Gamemode.HARDCORE) {
				return enumElements[1];
			} else if (gm == Gamemode.CREATIVE) {
				return enumElements[2];
			} else {
				return enumElements[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Class<?> getVanillaGameModeEnumClass() {
		try {
			return Class.forName("net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$SelectedGameMode");
		} catch (ClassNotFoundException e) {
			try {
				return Class.forName("net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$SelectedGameMode");
			} catch (ClassNotFoundException e2) {
				e2.printStackTrace();
			}
		}
		return null;
	}
}
