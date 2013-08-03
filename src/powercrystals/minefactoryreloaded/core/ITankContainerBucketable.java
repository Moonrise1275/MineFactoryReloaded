package powercrystals.minefactoryreloaded.core;

import net.minecraftforge.fluids.IFluidHandler;
/**
 * @author Emy
 *
 * Extends the ITankContainer interface to allow manual draining/filling via buckets.
 * 
 * what am I even doing here
 */
public interface ITankContainerBucketable extends IFluidHandler
{
	/**
	 * Called to determine if the ITankContainer should be filled by buckets.
	 * @return True if the ITankContainer is allowed to be filled manually (with buckets)
	 */
	public boolean allowBucketFill();
	
	/**
	 * Called to determine if the ITankContainer should be drained by buckets.
	 * @return True if the ITankContainer is allowed to be drained manually (with buckets)
	 */
	public boolean allowBucketDrain();
}
