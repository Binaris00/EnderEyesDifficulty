package eu.asangarin.endereyesdifficulty.level;

import eu.asangarin.endereyesdifficulty.util.CreateWorldScreenUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class DifficultySaveData extends SavedData {
	private boolean initialized;
	private CreateWorldScreenUtils.Difficulty enderDifficulty;

	private DifficultySaveData(boolean init, CreateWorldScreenUtils.Difficulty diff) {
		this.initialized = init;
		this.enderDifficulty = diff;
	}

	@Override
	public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
		tag.putBoolean("Initialized", initialized);
		tag.putInt("EnderDifficulty", enderDifficulty.getSaveId());
		return tag;
	}

	public static DifficultySaveData load(CompoundTag tag) {
		boolean init = tag.getBoolean("Initialized");
		CreateWorldScreenUtils.Difficulty diff = CreateWorldScreenUtils.Difficulty.fromSaveId(tag.getInt("EnderDifficulty"));
		return new DifficultySaveData(init, diff);
	}

	public static DifficultySaveData createNew() {
		return new DifficultySaveData(false, CreateWorldScreenUtils.Difficulty.NORMAL);
	}

	public void initialize(Difficulty difficulty) {
		this.enderDifficulty = switch (difficulty) {
			case NORMAL -> CreateWorldScreenUtils.Difficulty.HARD;
			case HARD -> CreateWorldScreenUtils.Difficulty.NIGHTMARE;
			default -> CreateWorldScreenUtils.Difficulty.NORMAL;
		};
		this.initialized = true;
		setDirty();
	}

	public CreateWorldScreenUtils.Difficulty getEnderDifficulty() {
		return enderDifficulty;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setDifficulty(CreateWorldScreenUtils.Difficulty difficulty) {
		enderDifficulty = difficulty;
		setDirty();
	}
}
