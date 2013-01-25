package StevenGreyGoo.mod_GreyGoo;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class GooTeleporter extends Teleporter
{
	 public GooTeleporter(WorldServer par1WorldServer) {
		super(par1WorldServer);
		// TODO Auto-generated constructor stub
	}


	    /**
	     * Create a new portal near an entity.
	     */
	   
	    @Override
	    public boolean func_85188_a(Entity par1Entity)
	    {
	    	return true;
	    }
	    @Override
	    public void func_85189_a(long par1)
	    {
	    
	    }
}
