package eu.asangarin.endereyesdifficulty.client;

import de.keksuccino.konkrete.gui.content.AdvancedButton;
import eu.asangarin.endereyesdifficulty.util.CreateWorldScreenUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class SelectionWidget<T extends Enum<T>> {
	private ToggleModeButton leftButton;
	private ToggleModeButton midButton;
	private ToggleModeButton rightButton;

	private AdvancedButton leftArrow;
	private AdvancedButton rightArrow;

	private final SelectionData<T> leftData, midData, rightData;

	private final ArrowTextures arrowTextures;

	private final Consumer<T> valueConsumer;

	public SelectionWidget(SelectionData<T> leftData, SelectionData<T> midData, SelectionData<T> rightData, ArrowTextures arrowTextures, Consumer<T> valueConsumer) {
		this.leftData = leftData;
		this.midData = midData;
		this.rightData = rightData;
		this.arrowTextures = arrowTextures;
		this.valueConsumer = valueConsumer;
	}

	public void init(int midX, int topY, T testSubject) {
		this.leftArrow = new AdvancedButton(midX - 145 - 40 - 10, topY + 38 + (45 / 2) - 20, 40, 40, "",
				(press) -> switchTo(CreateWorldScreenUtils.Direction.LEFT));
		this.leftArrow.setBackgroundNormal(arrowTextures.leftArrowTexture);
		this.leftArrow.setBackgroundHover(arrowTextures.leftArrowHoverTexture);

		this.rightArrow = new AdvancedButton(midX + 55 + 90 + 10, topY + 38 + (45 / 2) - 20, 40, 40, "",
				(press) -> switchTo(CreateWorldScreenUtils.Direction.RIGHT));
		this.rightArrow.setBackgroundNormal(arrowTextures.rightArrowTexture);
		this.rightArrow.setBackgroundHover(arrowTextures.rightArrowHoverTexture);


		this.leftButton = new ToggleModeButton(midX - 145, topY + 38, 90, 45, leftData.texture, leftData.name, false,
			(press) -> setValue(leftData.value));
		this.leftButton.setInfoText(leftData.info1, leftData.info2);
		if (leftData.test(testSubject))
			this.leftButton.setSelected(true);

		this.midButton = new ToggleModeButton(midX - 45, topY + 38, 90, 45, midData.texture, midData.name, false,
			(press) -> setValue(midData.value));
		this.midButton.setInfoText(midData.info1, midData.info2);
		if (midData.test(testSubject))
			this.midButton.setSelected(true);

		this.rightButton = new ToggleModeButton(midX + 55, topY + 38, 90, 45, rightData.texture, rightData.name, false,
			(press) -> setValue(rightData.value));
		this.rightButton.setInfoText(rightData.info1, rightData.info2);
		if (rightData.test(testSubject))
			this.rightButton.setSelected(true);

		// SHOW GAMEMODE INFO
		leftButton.showInfo = false;
		midButton.showInfo = false;
		rightButton.showInfo = false;
	}

	public void show() {
		this.leftButton.visible = true;
		this.midButton.visible = true;
		this.rightButton.visible = true;
		this.leftArrow.visible = true;
		this.rightArrow.visible = true;
	}

	public void hide() {
		this.leftButton.visible = false;
		this.midButton.visible = false;
		this.rightButton.visible = false;
		this.leftArrow.visible = false;
		this.rightArrow.visible = false;
	}

	public void switchTo(CreateWorldScreenUtils.Direction direction) {
		if (direction == CreateWorldScreenUtils.Direction.LEFT) {
			if (this.getSelectedValue() == leftData.value) {
				this.setValue(rightData.value);
			} else if (this.getSelectedValue() == midData.value) {
				this.setValue(leftData.value);
			} else if (this.getSelectedValue() == rightData.value) {
				this.setValue(midData.value);
			}
		}
		if (direction == CreateWorldScreenUtils.Direction.RIGHT) {
			if (this.getSelectedValue() == leftData.value) {
				this.setValue(midData.value);
			} else if (this.getSelectedValue() == midData.value) {
				this.setValue(rightData.value);
			} else if (this.getSelectedValue() == rightData.value) {
				this.setValue(leftData.value);
			}
		}
	}

	public void setValue(T value) {
		if (value == midData.value) {
			this.midButton.setSelected(true);
			this.leftButton.setSelected(false);
			this.rightButton.setSelected(false);
		} else if (value == rightData.value) {
			this.rightButton.setSelected(true);
			this.leftButton.setSelected(false);
			this.midButton.setSelected(false);
		} else {
			this.leftButton.setSelected(true);
			this.midButton.setSelected(false);
			this.rightButton.setSelected(false);
		}
		valueConsumer.accept(value);
	}

	private T getSelectedValue() {
		if (this.midButton.isSelected())
			return midData.value;
		if (this.rightButton.isSelected())
			return rightData.value;
		return leftData.value;
	}

	public AdvancedButton getLeftArrowWidget() {
		return leftArrow;
	}

	public AdvancedButton getRightArrowWidget() {
		return rightArrow;
	}

	public ToggleModeButton getLeftButtonWidget() {
		return leftButton;
	}

	public ToggleModeButton getMidButtonWidget() {
		return midButton;
	}

	public ToggleModeButton getRightButtonWidget() {
		return rightButton;
	}

	public void handleBigLayout() {
		this.leftButton.y += 10;
		this.midButton.y += 10;
		this.rightButton.y += 10;

		this.leftArrow.y += 10;
		this.rightArrow.y += 10;
	}

	public int getY() {
		return midButton.y;
	}

	public static class ArrowTextures {
		public final ResourceLocation leftArrowTexture;
		public final ResourceLocation leftArrowHoverTexture;
		public final ResourceLocation rightArrowTexture;
		public final ResourceLocation rightArrowHoverTexture;

		public ArrowTextures(ResourceLocation leftArrowTexture, ResourceLocation leftArrowHoverTexture, ResourceLocation rightArrowTexture, ResourceLocation rightArrowHoverTexture) {
			this.leftArrowTexture = leftArrowTexture;
			this.leftArrowHoverTexture = leftArrowHoverTexture;
			this.rightArrowTexture = rightArrowTexture;
			this.rightArrowHoverTexture = rightArrowHoverTexture;
		}
	}

	public static class SelectionData<T extends Enum<T>> {
		public final String name;
		public final ResourceLocation texture;
		public final T value;

		public String info1, info2;

		public SelectionData(String name, ResourceLocation texture, T value) {
			this.name = name;
			this.texture = texture;
			this.value = value;
		}

		public boolean test(T value) {
			return this.value == value;
		}

		public SelectionData<T> withInfo(Component info1, Component info2) {
			this.info1 = info1.getString();
			this.info2 = info2.getString();
			return this;
		}
	}
}
