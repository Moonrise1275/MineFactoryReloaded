package powercrystals.minefactoryreloaded.gui.client;

import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidTank;

import org.lwjgl.opengl.GL11;

import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryInventory;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityBioFuelGenerator;

public class GuiBioFuelGenerator extends GuiFactoryInventory
{
	private static final int _barColorEnergy = (0)   | (0 << 8)   | (255 << 16) | (255 << 24);
	
	public GuiBioFuelGenerator(ContainerFactoryInventory container, TileEntityBioFuelGenerator tileentity)
	{
		super(container, tileentity);
		ySize = 165;
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
			drawTank(142, 75, tank.getFluid().fluidID, tankSize);
		}
		
		drawBar(160, 75, ((TileEntityBioFuelGenerator)_tileEntity).getBufferMax(), ((TileEntityBioFuelGenerator)_tileEntity).getBuffer(), _barColorEnergy);
	}
	
	@Override
	protected void drawTooltips(int mouseX, int mouseY)
	{
		if(isPointInRegion(142, 15, 16, 60, mouseX, mouseY))
		{
			FluidTank tank = _tileEntity.getTank();
			if (tank != null && tank.getFluidAmount() > 0)
			{
				drawBarTooltip(tank.getFluid().getFluid().getLocalizedName(), "mB", tank.getFluidAmount(), tank.getCapacity(), mouseX, mouseY);
			}
		}
		
		else if(isPointInRegion(161, 15, 8, 60, mouseX, mouseY))
		{
			drawBarTooltip("Energy", "MJ", ((TileEntityBioFuelGenerator)_tileEntity).getBuffer(), ((TileEntityBioFuelGenerator)_tileEntity).getBufferMax(), mouseX, mouseY);
		}
	}
}
