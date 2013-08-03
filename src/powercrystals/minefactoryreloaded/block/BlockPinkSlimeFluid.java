package powercrystals.minefactoryreloaded.block;

import net.minecraftforge.fluids.Fluid;

import powercrystals.minefactoryreloaded.entity.EntityPinkSlime;

public class BlockPinkSlimeFluid extends BlockFactoryFluid
{
	public BlockPinkSlimeFluid(int id, Fluid fluid, String liquidName)
	{
		super(id, fluid, liquidName);
	}
	
	@Override
	public void updateTick(net.minecraft.world.World world, int x, int y, int z, java.util.Random rand)
	{
		if(world.getBlockMetadata(x, y, z) == 0)
		{
			world.setBlockToAir(x, y, z);
			EntityPinkSlime s = new EntityPinkSlime(world);
			s.func_110161_a(null);
			s.setSlimeSize(1);
			s.setPosition(x, y + 0.5, z);
			world.spawnEntityInWorld(s);
		}
		else
		{
			super.updateTick(world, x, y, z, rand);
		}
	}
}
