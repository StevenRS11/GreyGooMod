package StevenGreyGoo.mod_GreyGoo;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
public class EventHookContainer
{
    @ForgeSubscribe
    public void onWorldLoad(WorldEvent.Load event)
    {
    	if(!mod_GreyGoo.hasLoadedBackupDims)
    	{
    	//	mod_GreyGoo.instance.dimHelper.dimensionList.clear();
        	//mod_GreyGoo.instance.dimHelper.initBackups();

    	}
    	
        World world = event.world;
        mod_GreyGoo.worldsWithPortal.clear();
        mod_GreyGoo.instance.proxy.readNBTFromFile(world);
        mod_GreyGoo.instance.TGDbloom = false;
    }

    @ForgeSubscribe
    public void onWorldunload(WorldEvent.Unload event)
    {
        World world = event.world;
        mod_GreyGoo.instance.proxy.readNBTFromFile(world);
        mod_GreyGoo.instance.proxy.writeNBTToFile(world);
    }

    @ForgeSubscribe
    public void onWorldsave(WorldEvent.Save event)
    {
        World world = event.world;
        mod_GreyGoo.instance.proxy.readNBTFromFile(world);
        mod_GreyGoo.instance.proxy.writeNBTToFile(world);
    }
}