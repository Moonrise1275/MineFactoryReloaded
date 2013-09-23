package powercrystals.minefactoryreloaded.core;

import java.util.Random;

import powercrystals.minefactoryreloaded.tile.machine.TileEntityGrinder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;

public class GrindingDamage extends DamageSource
{
	public TileEntityGrinder grinder;
	protected int _msgCount;
	protected Random _rand;
	
	public GrindingDamage(TileEntityGrinder source)
	{
		this(source, null, 1);
	}
	
	public GrindingDamage(TileEntityGrinder source, String type)
	{
		this(source, type, 1);
	}
	
	public GrindingDamage(TileEntityGrinder source, String type, int deathMessages)
	{
		super(type == null ? "mfr.grinder" : type);
		grinder = source;
		setDamageBypassesArmor();
		setDamageAllowedInCreativeMode();
		_msgCount = Math.max(deathMessages, 1);
		_rand = new Random();
	}
	
	@Override
	public ChatMessageComponent getDeathMessage(EntityLivingBase par1EntityLivingBase)
	{
		EntityLivingBase entityliving1 = par1EntityLivingBase.func_94060_bK();
		String s = "death.attack." + this.damageType;
		if (_msgCount > 0)
		{
			int msg = _rand.nextInt(_msgCount);
			if (msg != 0)
			{
				s += "." + msg;
			}
		}
		String s1 = s + ".player";
		if (entityliving1 != null && StatCollector.func_94522_b(s1))
			return ChatMessageComponent.func_111066_d(StatCollector.translateToLocalFormatted(s1, new Object[] {par1EntityLivingBase.getTranslatedEntityName(), entityliving1.getTranslatedEntityName()}));
		return ChatMessageComponent.func_111066_d(StatCollector.translateToLocalFormatted(s, new Object[] {par1EntityLivingBase.getTranslatedEntityName()}));
	}
}
