package StevenGreyGoo.mod_GreyGoo;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.block.Block;
public class BlockRestorer extends Block
{
    Random random = new Random();
    WorldServer backup;
    private static boolean hasLoaded;
    private GooDimensionHelper dimHelper=mod_GreyGoo.instance.dimHelper;

    protected BlockRestorer(int i, int j)
    {
        super(i, j, Material.ground);
        setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabBlock);
        
    }
    @Override
    public String getTextureFile()
    {
        return "/GooBlockTextures.png";
    }
    @Override
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLiving par5EntityLiving) 
    {
    //	if(par5EntityLiving instanceof EntityPlayerMP&&!par1World.isRemote)
    //	{
    		
    	//	mod_GreyGoo.instance.dimHelper.loadBackupDim((WorldServer)par1World);
    	
    //	}
    	
    	
    	
    }   
    
    
    private void restore(World world, int i, int j, int k)
    {
    	
    	boolean flag=true;
    	boolean flag1=true;
    	
    		//if(hasLoaded)
    		
        //	backup =(WorldServer)mod_GreyGoo.instance.dimHelper.gooTeleport(world.provider.dimensionId);
        	int backupBlockID=dimHelper.getBlockIDfromFreshChunk(world, i , j , k );
        	int backupMetadata=dimHelper.blockMetaData;
        	
    	if(mod_GreyGoo.worldsWithPortal.containsKey(world.provider.dimensionId) )
    	{
    	
    	if(world==backup)
    	{
    		System.out.println("problem");
    	}
    	
    	if(world.getBlockMetadata(i, j, k)==1&&world.getBlockId(i, j, k)==this.blockID)

        {
    		if(mod_GreyGoo.NeverRestoreThese.contains(backupBlockID))
   		 	{
    			world.setBlockWithNotify(i, j, k,0);
    			
    			//backup.setBlockWithNotify(i, j, k,Block.stone.blockID);

   		 	}
    		else
    		{
    			//System.out.println("shouldReplace");
    			world.setBlockAndMetadataWithNotify(i, j, k,backupBlockID,backupMetadata);
    		}
        }
    	else if((world.getBlockMetadata(i, j, k)==2)&&world.getBlockId(i, j, k)==this.blockID)
    			{
    				world.setBlockAndMetadataWithNotify(i, j, k,backupBlockID,backupMetadata);
    			}
    	
        
    	
    	
    	 int l = -1;
         int i1 = -1;
         int j1 = -1;

       
         {
             for (; i1 < 2; i1++)
             {
                 for (; j1 < 2; j1++)
                 {
                     for (; l < 2; l++)
                     {
                    	 int  backupWorldBlockID=dimHelper.getBlockIDfromFreshChunk(world,i + l, j + i1, k + j1);
                    	 int thisWorldBlockID=world.getBlockId(i + l, j + i1, k + j1);
                    	
                    	 

                         if (!mod_GreyGoo.instance.NeverRestoreThese.contains(thisWorldBlockID)&&!mod_GreyGoo.instance.NeverRestoreThese.contains(backupWorldBlockID)&&(Math.abs(l) + Math.abs(i1) + Math.abs(j1) == 1)&&thisWorldBlockID!=backupWorldBlockID )
                         {
                        	 flag1=false;
                        	 if(random.nextBoolean())
                        	 {
                        		 mod_GreyGoo.instance.spreadLimiter.Restorerspreadlimiter(true);
                        		 
                        		 world.setBlockWithNotify(i + l, j + i1, k + j1, blockID);
                        		 world.setBlockMetadata(i + l, j + i1, k + j1,0);
                        		 world.scheduleBlockUpdate(i + l, j + i1, k + j1, blockID, random.nextInt(25)+random.nextInt(10));
                        	 }
                        	 else
                        	 {
                        		 
                        		 flag=false;
                        	 }
                        	                         	
                         }
                                                                        
                     }

                     l = -1;
                 }

                 j1 = -1;
             }
         }
         backupBlockID=dimHelper.getBlockIDfromFreshChunk(world, i , j , k );
        
        
       
    	 

        
        	 if(flag&&world.getBlockId(i, j, k)==this.blockID)
        	 {
        		 
        		 world.setBlockMetadataWithNotify(i, j, k, 1);
        	 }
        	 else if(flag1&&world.getBlockId(i, j, k)==this.blockID)
        	 {
        		 world.setBlockMetadataWithNotify(i, j, k, 2);
        	 }
        	 
           	 world.scheduleBlockUpdate(i , j , k , blockID, random.nextInt(25)+random.nextInt(10));

    	}
    	else
    	{
    		mod_GreyGoo.instance.proxy.printStringClient("No nearyb Goo Portal Core detected");
    	}
         
    }
    	
    

    private void decay(World world, int i, int j, int k)
    {
    	
    		//world.setBlockWithNotify(i, j, k, 0);
    	

    }

    public int quantityDropped(Random par1Random)
    {
            return 0;
    }
   
    public void updateTick(World world, int i, int j, int k, Random random)
    {
    	
    	 if(!world.isRemote&&mod_GreyGoo.isGooActive(this.blockID, world))

    	{
    		
    		if(mod_GreyGoo.instance.spreadLimiter.Restorerspreadlimiter(false))
    		{
    			restore(world, i, j, k);
                decay(world, i, j, k);
    		}
    		else
    		{
    		world.scheduleBlockUpdate(i, j, k, this.blockID, random.nextInt(25)+ random.nextInt(25)+ random.nextInt(25));
    	}
    		}
        
    }

    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
    {
       if (!world.isRemote && !entityplayer.isSneaking())
        {
        	//mod_GreyGoo.instance.dimHelper.loadBackupDim((WorldServer)world);
            restore(world, i, j, k);
          	world.scheduleBlockUpdate(i , j , k , blockID, random.nextInt(25)+random.nextInt(10));

            return true;
        }

        return false;
    }
}
