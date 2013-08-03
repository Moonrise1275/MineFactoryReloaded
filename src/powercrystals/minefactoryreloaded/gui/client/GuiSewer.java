package powercrystals.minefactoryreloaded.gui.client;

import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidTank;

import org.lwjgl.opengl.GL11;

import powercrystals.minefactoryreloaded.gui.container.ContainerSewer;
import powercrystals.minefactoryreloaded.tile.machine.TileEntitySewer;

public class GuiSewer extends GuiFactoryInventory
{
	public GuiSewer(ContainerSewer container, TileEntitySewer tileentity)
	{
		super(container, tileentity);
		ySize = 180;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRenderer.drawString(_tileEntity.getInvName(), 8, 6, 4210752);
		fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		FluidTank tank = _tileEntity.getTank();
		if(tank != null && tank.getFluidAmount() > 0)
		{
			int tankSize = tank.getFluidAmount() * _tankSizeMax / tank.getCapacity();
			drawTank(152, 75, tank.getFluid().fluidID, tankSize);
		}
	}
	
	@Override
	protected void drawTooltips(int mouseX, int mouseY)
	{
		if(isPointInRegion(152, 15, 16, 60, mouseX, mouseY))
		{
			FluidTank tank = _tileEntity.getTank();
			if (tank != null && tank.getFluidAmount() > 0)
			{
				drawBarTooltip(tank.getFluid().getFluid().getLocalizedName(), "mB", tank.getFluidAmount(), tank.getCapacity(), mouseX, mouseY);
			}
		}
	}
}
