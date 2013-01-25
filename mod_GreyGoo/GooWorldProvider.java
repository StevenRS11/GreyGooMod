package StevenGreyGoo.mod_GreyGoo;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;

public class GooWorldProvider extends WorldProvider 
{
	public int dimensionIDREAL;
	World customworldObj=super.worldObj;
	
	public GooWorldProvider()
	{
		super.isHellWorld=false;
	}
	
	public boolean isHellWorld = false;
	
        public int getDimensionID()
        {
                return this.dimensionId;
        }
        
        public boolean isSkyColored()
        {
            return true;
        }
        
        public boolean renderClouds()
        {
                        return true;
        }

        public boolean renderVoidFog()
        {
                        return false;
        }

        public float setSunSize()
        {
                        return 0.0F;
        }

        public float setMoonSize()
        {
                        return 0.0F;
        }

        public boolean renderStars()
        {
                        return true;
        }

        public boolean darkenSkyDuringRain()
        {
                        return false;
        }

        public String getRespawnMessage()
        {
                        return "Your a derp.";
        }

        public void registerWorldChunkManager()
        {
        	
        	//super.worldObj.getWorldInfo().setWorldName("GooWorldBackupfor");
        	super.isHellWorld=false;
            worldChunkMgr =new GooWorldChunkManager(worldObj);//worldObj.getWorldChunkManager();
        }
        
        public IChunkProvider getChunkProvider()
        {
        	super.isHellWorld=false;
        		return  new GooChunkProvider(worldObj, dimensionId, true);
        }

		@Override
		public String getDimensionName() {
			// TODO Auto-generated method stub
			return "GooWorldBackupfor";
			
		}
}