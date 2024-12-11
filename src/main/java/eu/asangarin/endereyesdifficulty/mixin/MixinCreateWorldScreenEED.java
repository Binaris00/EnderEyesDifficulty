package eu.asangarin.endereyesdifficulty.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import eu.asangarin.endereyesdifficulty.EnderDifficulty;
import eu.asangarin.endereyesdifficulty.client.SelectionWidget;
import eu.asangarin.endereyesdifficulty.util.CreateWorldScreenUtils;
import eu.asangarin.endereyesdifficulty.util.CreateWorldScreenUtils.Difficulty;
import eu.asangarin.endereyesdifficulty.util.CreateWorldScreenUtils.Gamemode;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldGenSettingsComponent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings({"DataFlowIssue", "CallToPrintStackTrace"})
@Mixin(CreateWorldScreen.class)
public abstract class MixinCreateWorldScreenEED extends Screen {
	@Unique
	private static final Component ENTER_SEED_TEXT = Component.translatable("selectWorld.enterSeed");
	@Unique
	private static final Component DESC_ENTER_SEED = Component.translatable("selectWorld.seedInfo");
	@Unique
	private static final Component ENTER_WORLD_TEXT = Component.translatable("selectWorld.enterName");

	@Unique
	private static final Component DESC_ALLOW_CHEATS = Component.translatable("selectWorld.allowCommands.info");

	@Unique
	private static final Component DESC_GAMEMODE_SURVIVAL_LINE1 = Component.translatable("selectWorld.gameMode.survival.line1");
	@Unique
	private static final Component DESC_GAMEMODE_SURVIVAL_LINE2 = Component.translatable("selectWorld.gameMode.survival.line2");
	@Unique
	private static final Component DESC_GAMEMODE_CREATIVE_LINE1 = Component.translatable("selectWorld.gameMode.creative.line1");
	@Unique
	private static final Component DESC_GAMEMODE_CREATIVE_LINE2 = Component.translatable("selectWorld.gameMode.creative.line2");
	@Unique
	private static final Component DESC_GAMEMODE_HARDCORE_LINE1 = Component.translatable("selectWorld.gameMode.hardcore.line1");
	@Unique
	private static final Component DESC_GAMEMODE_HARDCORE_LINE2 = Component.translatable("selectWorld.gameMode.hardcore.line2");

	@Unique
	private static final Component DESC_DIFFICULTY_NORMAL_LINE1 = Component.translatable("endereyes.difficulty.normal.description.1");
	@Unique
	private static final Component DESC_DIFFICULTY_NORMAL_LINE2 = Component.translatable("endereyes.difficulty.normal.description.2");
	@Unique
	private static final Component DESC_DIFFICULTY_HARD_LINE1 = Component.translatable("endereyes.difficulty.hard.description.1");
	@Unique
	private static final Component DESC_DIFFICULTY_HARD_LINE2 = Component.translatable("endereyes.difficulty.hard.description.2");
	@Unique
	private static final Component DESC_DIFFICULTY_NIGHTMARE_LINE1 = Component.translatable("endereyes.difficulty.nightmare.description.1");
	@Unique
	private static final Component DESC_DIFFICULTY_NIGHTMARE_LINE2 = Component.translatable("endereyes.difficulty.nightmare.description.2");

	@Unique
	private static final ResourceLocation BTN_TEXTURE_SURVIVAL = new ResourceLocation(EnderDifficulty.MODID, "textures/world/gamemode/background_survival.png");
	@Unique
	private static final ResourceLocation BTN_TEXTURE_CREATIVE = new ResourceLocation(EnderDifficulty.MODID, "textures/world/gamemode/background_creative.png");
	@Unique
	private static final ResourceLocation BTN_TEXTURE_HARDCORE = new ResourceLocation(EnderDifficulty.MODID, "textures/world/gamemode/background_hardcore.png");

	@Unique
	private static final ResourceLocation BTN_TEXTURE_NORMAL = new ResourceLocation(EnderDifficulty.MODID, "textures/world/difficulty/background_normal.png");
	@Unique
	private static final ResourceLocation BTN_TEXTURE_HARD = new ResourceLocation(EnderDifficulty.MODID, "textures/world/difficulty/background_hard.png");
	@Unique
	private static final ResourceLocation BTN_TEXTURE_NIGHTMARE = new ResourceLocation(EnderDifficulty.MODID, "textures/world/difficulty/background_nightmare.png");

	@Unique
	private static final SelectionWidget.ArrowTextures DEFAULT_ARROW_TEXTURES = new SelectionWidget.ArrowTextures(
			new ResourceLocation(EnderDifficulty.MODID, "textures/world/arrow_left_normal.png"),
			new ResourceLocation(EnderDifficulty.MODID, "textures/world/arrow_left_hover.png"),
			new ResourceLocation(EnderDifficulty.MODID, "textures/world/arrow_right_normal.png"),
			new ResourceLocation(EnderDifficulty.MODID, "textures/world/arrow_right_hover.png")
	);

	@Shadow private EditBox nameEdit;
	@Shadow private boolean worldGenSettingsVisible;
	@Final
	@Shadow
	public WorldGenSettingsComponent worldGenSettingsComponent;
	@Shadow private boolean commandsChanged;
	@Shadow private boolean commands;

	@Shadow private CycleButton<?> commandsButton;
	@Shadow private CycleButton<?> modeButton;
	@Shadow private CycleButton<?> difficultyButton;
	@Shadow private Button moreOptionsButton;
	@Shadow private Button dataPacksButton;
	@Shadow private Button gameRulesButton;
	@Shadow private Button createButton;

	@Unique
	private AbstractWidget eed$cancelButton = null;
	@Unique
	private AdvancedButton eed$customCommandsButton = null;
	@Unique
	private AdvancedButton eed$backButton;

	@Unique
	private Color eed$headerFooterColor;

	@Unique
	private Difficulty eed$selectedDifficulty = Difficulty.NORMAL;

	@Unique
	private SelectionWidget<Gamemode> eed$gamemodeSelection;
	@Unique
	private SelectionWidget<Difficulty> eed$difficultySelection;

	protected MixinCreateWorldScreenEED(Component titleIn) {
		super(titleIn);
	}

	@Inject(at = @At("TAIL"), method = "init()V")
	private void onInit(CallbackInfo info) {
		this.eed$headerFooterColor = new Color(0, 0, 0, 190);

		this.eed$cancelButton = null;
		for (Widget widget : this.renderables) {
			if (widget instanceof AbstractWidget) {
				if (((AbstractWidget) widget).getMessage().getString().equals(CommonComponents.GUI_CANCEL.getString())) {
					this.eed$cancelButton = (AbstractWidget) widget;
					break;
				}
			}
		}

		this.removeWidget(this.modeButton);

		this.removeWidget(this.commandsButton);

		this.removeWidget(this.difficultyButton);

		this.nameEdit.y = 40;

		int midX = this.width / 2;
		int topY = this.nameEdit.y;

		CreateWorldScreenUtils.Gamemode selectedGamemode = this.eed$getVanillaGameMode();

		eed$gamemodeSelection = new SelectionWidget<>(
				new SelectionWidget.SelectionData<>("Survival", BTN_TEXTURE_SURVIVAL, Gamemode.SURVIVAL)
						.withInfo(DESC_GAMEMODE_SURVIVAL_LINE1, DESC_GAMEMODE_SURVIVAL_LINE2),
				new SelectionWidget.SelectionData<>("Creative", BTN_TEXTURE_CREATIVE, Gamemode.CREATIVE)
						.withInfo(DESC_GAMEMODE_CREATIVE_LINE1, DESC_GAMEMODE_CREATIVE_LINE2),
				new SelectionWidget.SelectionData<>("Hardcore", BTN_TEXTURE_HARDCORE, Gamemode.HARDCORE)
						.withInfo(DESC_GAMEMODE_HARDCORE_LINE1, DESC_GAMEMODE_HARDCORE_LINE2),
				DEFAULT_ARROW_TEXTURES, this::eed$setVanillaGameMode);
		eed$gamemodeSelection.init(midX, topY, selectedGamemode);
		addRenderableWidget(eed$gamemodeSelection.getLeftArrowWidget());
		addRenderableWidget(eed$gamemodeSelection.getRightArrowWidget());
		addRenderableWidget(eed$gamemodeSelection.getLeftButtonWidget());
		addRenderableWidget(eed$gamemodeSelection.getMidButtonWidget());
		addRenderableWidget(eed$gamemodeSelection.getRightButtonWidget());
		topY += this.eed$gamemodeSelection.getY();

		eed$difficultySelection = new SelectionWidget<>(
				new SelectionWidget.SelectionData<>("Normal", BTN_TEXTURE_NORMAL, Difficulty.NORMAL)
					.withInfo(DESC_DIFFICULTY_NORMAL_LINE1, DESC_DIFFICULTY_NORMAL_LINE2),
				new SelectionWidget.SelectionData<>("Hard", BTN_TEXTURE_HARD, Difficulty.HARD)
					.withInfo(DESC_DIFFICULTY_HARD_LINE1, DESC_DIFFICULTY_HARD_LINE2),
				new SelectionWidget.SelectionData<>("Nightmare", BTN_TEXTURE_NIGHTMARE, Difficulty.NIGHTMARE)
					.withInfo(DESC_DIFFICULTY_NIGHTMARE_LINE1, DESC_DIFFICULTY_NIGHTMARE_LINE2),
				DEFAULT_ARROW_TEXTURES, this::eed$setDifficulty);
		eed$difficultySelection.init(midX, topY, eed$selectedDifficulty);
		addRenderableWidget(eed$difficultySelection.getLeftArrowWidget());
		addRenderableWidget(eed$difficultySelection.getRightArrowWidget());
		addRenderableWidget(eed$difficultySelection.getLeftButtonWidget());
		addRenderableWidget(eed$difficultySelection.getMidButtonWidget());
		addRenderableWidget(eed$difficultySelection.getRightButtonWidget());

		//Data Packs Button
		this.dataPacksButton.x = midX + 5;
		this.dataPacksButton.y = topY + 38 + 50 + 10;

		//More World Options Button
		this.moreOptionsButton.x = midX - this.dataPacksButton.getWidth() - 5;
		this.moreOptionsButton.y = topY + 38 + 50 + 10;

		//Allow Cheats Button
		int allowCheatsX = midX - this.commandsButton.getWidth() - 5;
		int allowCheatsY = topY + 38 + 50 + 10 + 20 + 4;
		this.eed$customCommandsButton = this.addRenderableWidget(new AdvancedButton(allowCheatsX, allowCheatsY, this.commandsButton.getWidth(), this.commandsButton.getHeight(), Component.translatable("selectWorld.allowCommands").getString(), false, (press) -> {
			this.commandsChanged = true;
			this.commands = !this.commands;
		}) {
			@Override
			public @NotNull Component getMessage() {
				return eed$optionStatus(super.getMessage(), commands);
			}

			protected @NotNull MutableComponent createNarrationMessage() {
				return super.createNarrationMessage().append(". ").append(Component.translatable("selectWorld.allowCommands.info"));
			}
		});

		// ALLOW CHEATS TOOLTIP
		this.eed$customCommandsButton.setDescription(DESC_ALLOW_CHEATS.getString());

		//Gamerules Button
		this.gameRulesButton.x = midX + 5;
		this.gameRulesButton.y = topY + 38 + 50 + 10 + 20 + 4;

		//More World Options Button
		this.difficultyButton.x = midX - (this.difficultyButton.getWidth() / 2);
		this.difficultyButton.y = topY + 38 + 50 + 10 + 20 + 4 + 20 + 4;

		//Back Button (More World Options -> Normal Menu)
		this.eed$backButton = this.addRenderableWidget(new AdvancedButton((this.width / 2) + 5, this.height - 28 + 3, 150, 20, CommonComponents.GUI_BACK.getString(), false,
				(press) -> this.toggleWorldGenSettingsVisibility()));

		//Handle big screen layout
		if ((this.createButton.y - this.difficultyButton.y) >= 100) {
			eed$gamemodeSelection.handleBigLayout();
			eed$difficultySelection.handleBigLayout();

			this.moreOptionsButton.y += 20;
			this.dataPacksButton.y += 20;

			this.eed$customCommandsButton.y += 20;
			this.gameRulesButton.y += 20;

			this.difficultyButton.y += 50;

		}

		this.createButton.y += 3;
		if (this.eed$cancelButton != null) {
			this.eed$cancelButton.y += 3;
		}

		this.refreshWorldGenSettingsVisibility();

	}

	@Inject(at = @At("HEAD"), method = "render", cancellable = true)
	private void onRender(PoseStack matrix, int mouseX, int mouseY, float partialTicks, CallbackInfo info) {
		info.cancel();

		this.renderBackground(matrix);

		if (this.worldGenSettingsVisible) {
			eed$gamemodeSelection.hide();
			eed$difficultySelection.hide();
			this.eed$customCommandsButton.visible = false;
			this.eed$cancelButton.visible = false;
			this.moreOptionsButton.visible = false;
			this.eed$backButton.visible = true;
		} else {
			eed$gamemodeSelection.show();
			eed$difficultySelection.show();
			this.eed$customCommandsButton.visible = true;
			this.eed$cancelButton.visible = true;
			this.moreOptionsButton.visible = true;
			this.eed$backButton.visible = false;
		}

		//Render header
		fill(matrix, 0, 0, this.width, 10 + 12, this.eed$headerFooterColor.getRGB());

		//Render title

		//noinspection ConstantValue || BOLD TITLE
		if (true) {
			String titleString = "§l" + this.title.getString();
			int titleWidth = font.width(titleString);
			font.draw(matrix, titleString, ((float) this.width / 2) - ((float) titleWidth / 2), 7, -1);
		} else {
			drawCenteredString(matrix, this.font, this.title, this.width / 2, 7, -1);
		}

		if (this.worldGenSettingsVisible) {

			drawString(matrix, this.font, ENTER_SEED_TEXT, this.width / 2 - 100, 47, -6250336);
			drawString(matrix, this.font, DESC_ENTER_SEED, this.width / 2 - 100, 85, -6250336);
			this.worldGenSettingsComponent.render(matrix, mouseX, mouseY, partialTicks);

		} else {

			drawString(matrix, this.font, ENTER_WORLD_TEXT, this.width / 2 - 100, this.nameEdit.y - 12, -6250336);
			this.nameEdit.render(matrix, mouseX, mouseY, partialTicks);

		}

		//Render footer
		fill(matrix, 0, this.createButton.y - 5, this.width, this.height, this.eed$headerFooterColor.getRGB());

		for (Widget renderable : this.renderables)
			renderable.render(matrix, mouseX, mouseY, partialTicks);
	}

	@Inject(at = @At("TAIL"), method = "setWorldGenSettingsVisible")
	private void onSetWorldGenSettingsVisible(boolean b, CallbackInfo info) {
		if (this.worldGenSettingsComponent.isDebug()) {
			if (this.eed$customCommandsButton != null) {
				this.eed$customCommandsButton.visible = false;
			}
		} else {
			if (this.eed$customCommandsButton != null) {
				this.eed$customCommandsButton.visible = !b;
			}
		}
	}

	@Unique
	private void eed$setDifficulty(Difficulty difficulty) {
		eed$selectedDifficulty = difficulty;
		this.difficulty = switch (eed$selectedDifficulty) {
			case NORMAL -> net.minecraft.world.Difficulty.EASY;
			case HARD -> net.minecraft.world.Difficulty.NORMAL;
			case NIGHTMARE -> net.minecraft.world.Difficulty.HARD;
		};
	}

	@Unique
	private void eed$setVanillaGameMode(Gamemode gm) {
		try {
			Method m = ObfuscationReflectionHelper.findMethod(CreateWorldScreen.class, "m_100900_", CreateWorldScreenUtils.getVanillaGameModeEnumClass());
			m.invoke(this, CreateWorldScreenUtils.getVanillaGameModeEnumElement(gm));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Unique
	private Gamemode eed$getVanillaGameMode() {
		try {
			Field f = ObfuscationReflectionHelper.findField(CreateWorldScreen.class, "f_100858_");
			Object mode = f.get(this);

			if (mode != null) {
				Field f2 = null;
				try {
					f2 = CreateWorldScreenUtils.getVanillaGameModeEnumClass().getDeclaredField("f_101028_");
				} catch (Exception ignored) {}
				if (f2 == null) {
					f2 = CreateWorldScreenUtils.getVanillaGameModeEnumClass().getDeclaredField("name");
				}
				f2.setAccessible(true);
				String modeString = (String) f2.get(mode);
				if (modeString != null) {
					if (modeString.equals("survival")) {
						return Gamemode.SURVIVAL;
					}
					if (modeString.equals("hardcore")) {
						return Gamemode.HARDCORE;
					}
					if (modeString.equals("creative")) {
						return Gamemode.CREATIVE;
					}
					return Gamemode.SURVIVAL;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Shadow
	private void toggleWorldGenSettingsVisibility() {}

	@Shadow
	public void refreshWorldGenSettingsVisibility() {}

	@Shadow private net.minecraft.world.Difficulty difficulty;

	@SuppressWarnings("SameParameterValue")
	@Unique
	private static MutableComponent eed$optionStatus(Component c, boolean b) {
		return Component.translatable(b ? "options.on.composed" : "options.off.composed", c);
	}
}
