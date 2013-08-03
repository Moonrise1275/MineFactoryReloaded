package powercrystals.minefactoryreloaded.item;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemFactoryBucket extends ItemBucket
{
	private int _fluidBlockID;
	
	public ItemFactoryBucket(int id, int fluidBlockID)
	{
		super(id, fluidBlockID);
		setCreativeTab(MFRCreativeTab.tab);
		_fluidBlockID = fluidBlockID;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.itemIcon = par1IconRegister.registerIcon("minefactoryreloaded:" + getUnlocalizedName());
	}
	
	@Override
	public boolean tryPlaceContainedLiquid(World world, int x, int y, int z)
	{
		if(_fluidBlockID <= 0)
		{
			return false;
		}
		else if(!world.isAirBlock(x, y, z) && world.getBlockMaterial(x, y, z).isSolid())
		{
			return false;
		}
		else
		{
			Material material = world.getBlockMaterial(x, y, z);
			if (!world.isRemote && !material.isSolid() && !material.isLiquid())
			{
				world.destroyBlock(x, y, z, true);
			}
			world.setBlock(x, y, z, _fluidBlockID, 0, 3);
			return true;
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubItems(int itemId, CreativeTabs creativeTab, List subTypes)
	{
		subTypes.add(new ItemStack(itemId, 1, 0));
	}
}
