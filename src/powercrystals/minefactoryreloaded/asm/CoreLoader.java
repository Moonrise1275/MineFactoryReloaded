package powercrystals.minefactoryreloaded.asm;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class CoreLoader implements IFMLLoadingPlugin
{
	@Deprecated
	@Override
	public String[] getLibraryRequestClass()
	{
		return null;
	}

	@Override
	public String[] getASMTransformerClass()
	{
		return new String[] {
				"powercrystals.minefactoryreloaded.asm.PCCAccessTransformer",
				"powercrystals.minefactoryreloaded.asm.PCCASMTransformer"
		};
	}

	@Override
	public String getModContainerClass()
	{
		return null;
	}

	@Override
	public String getSetupClass()
	{
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data)
	{
		// Nothing
	}
}
