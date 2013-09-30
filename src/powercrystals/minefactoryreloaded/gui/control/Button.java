package powercrystals.minefactoryreloaded.gui.control;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import powercrystals.minefactoryreloaded.gui.Control;
import powercrystals.minefactoryreloaded.gui.GuiRender;

public abstract class Button extends Control
{
	private static final ResourceLocation BUTTON_HOVER = new ResourceLocation("minefactoryreloaded", "textures/gui/button_hover.png");
	private static final ResourceLocation BUTTON_ENABLED = new ResourceLocation("minefactoryreloaded", "textures/gui/button_enabled.png");
	private static final ResourceLocation BUTTON_DISABLED = new ResourceLocation("minefactoryreloaded", "textures/gui/button_disabled.png");
	
	private String _text;
	
	public Button(GuiContainer containerScreen, int x, int y, int width, int height, String text)
	{
		super(containerScreen, x, y, width, height);
		_text = text;
	}

	public void setText(String text)
	{
		_text = text;
	}
	
	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks)
	{
		if(enabled && isPointInBounds(mouseX, mouseY))
		{
			containerScreen.mc.renderEngine.func_110577_a(BUTTON_HOVER);
		}
		else if(enabled)
		{
			containerScreen.mc.renderEngine.func_110577_a(BUTTON_ENABLED);
		}
		else
		{
			containerScreen.mc.renderEngine.func_110577_a(BUTTON_DISABLED);
		}
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GuiRender.drawTexturedModalRect(x,             y,              0,               0,                width / 2, height / 2);
		GuiRender.drawTexturedModalRect(x,             y + height / 2, 0,               256 - height / 2, width / 2, height / 2);
		GuiRender.drawTexturedModalRect(x + width / 2, y,              256 - width / 2, 0,                width / 2, height / 2);
		GuiRender.drawTexturedModalRect(x + width / 2, y + height / 2, 256 - width / 2, 256 - height / 2, width / 2, height / 2);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY)
	{
		String text = containerScreen.fontRenderer.trimStringToWidth(_text, width - 4);
		GuiRender.drawCenteredString(containerScreen.fontRenderer, text, x + width / 2, y + (height - 8) / 2, getTextColor(mouseX, mouseY));
	}
	
	protected int getTextColor(int mouseX, int mouseY)
	{
		if(enabled && isPointInBounds(mouseX, mouseY))
		{
			return 16777120;
		}
		else if(enabled)
		{
			return 14737632;
		}
		else
		{
			return -6250336;
		}
	}
	
	@Override
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton)
	{
		containerScreen.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
		if(mouseButton == 0)
		{
			onClick();
		}
		else if(mouseButton == 1)
		{
			onRightClick();
		}
		else if(mouseButton == 2)
		{
			onMiddleClick();
		}
		return true;
	}
	
	public abstract void onClick();
	
	public void onRightClick() { }
	public void onMiddleClick() { }
}
