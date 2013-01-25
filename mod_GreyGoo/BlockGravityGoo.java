package StevenGreyGoo.mod_GreyGoo;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BlockGravityGoo extends Block
{
    private  boolean fallInstantly = false;
    
    private Random random = new Random();
   

    @Override
    public String getTextureFile()
    {
        return "/GooBlockTextures.png";
    }

   

    public BlockGravityGoo(int par1, int par2)
    {
        super(par1, par2, Material.sand);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setTickRandomly(false);
    }

    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        if (!par1World.isRemote && canFallBelow(par1World, par2, par3 - 1, par4))
        {
            par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate());
        }
        else if (!par1World.isRemote && canFallBelow(par1World, par2, par3 - 2, par4))
        {
            par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate());
        }
    }
    public int quantityDropped(Random par1Random)
    {
        if (par1Random.nextInt(20) == 1)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    public int idDropped(int i, Random rand, int j)
    {
        return this.blockID + 4256;
    }

    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        if (!par1World.isRemote && canFallBelow(par1World, par2, par3 - 1, par4))
        {
            par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate());
        }
    }

    private void assimilate(World par1World, int i, int j, int k)
    {
        int l = -1;
        int i1 = -1;
        int j1 = -1;

        for (; i1 < 2; i1++)
        {
            for (; j1 < 2; j1++)
            {
                for (; l < 2; l++)
                {
                    if (par1World.getBlockId(i + l, j + i1, k + j1) == mod_GreyGoo.BlockCleaner.blockID)
                    {
                        par1World.setBlock(i, j, k, mod_GreyGoo.BlockCleaner.blockID);
                        continue;
                    }

                    if (par1World.getBlockId(i + l, j + i1, k + j1) != Block.chest.blockID &&par1World.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockGravityGooID&& par1World.getBlockId(i + l, j + i1, k + j1) != Block.bedrock.blockID && !par1World.isAirBlock(i + l, j + i1, k + j1) && Math.abs(l) + Math.abs(i1) + Math.abs(j1) == 1&&!mod_GreyGoo.gooNeverEatThese.contains(par1World.getBlockId(i + l, j + i1, k + j1)))
                    {
                        boolean flag = true;

                        if ((par1World.getBlockId(i + l, j + i1, k + j1) == Block.waterMoving.blockID || par1World.getBlockId(i + l, j + i1, k + j1) == Block.waterStill.blockID || par1World.getBlockId(i + l, j + i1, k + j1) == Block.lavaMoving.blockID || par1World.getBlockId(i + l, j + i1, k + j1) == Block.lavaStill.blockID) && Math.abs(l) + Math.abs(i1) + Math.abs(j1) == 1)
                        {
                            flag = false;
                        }

                        if (!par1World.isRemote && flag == true )
                        {
                            mod_GreyGoo.instance.spreadLimiter.numberoffallingLimiter(true);
                            par1World.setBlockWithNotify(i + l, j + i1, k + j1, blockID);
                        }
                    }
                }

                l = -1;
            }

            j1 = -1;
        }
    }
    

    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        

            if ( mod_GreyGoo.isGooActive(this.blockID, par1World)&&!par1World.isRemote&&mod_GreyGoo.instance.spreadLimiter.numberoffallingLimiter(false)&&mod_GreyGoo.isGooActive(this.blockID, par1World))
            {
            	assimilate(par1World, par2, par3, par4);
                this.tryToFall(par1World, par2, par3, par4);
            }
            else
            {
            	 par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate()+random.nextInt(25));
            }

           

    }

    private void tryToFall(World par1World, int par2, int par3, int par4)
    {
        if (canFallBelow(par1World, par2, par3 - 1, par4) && par3 >= 0 && !par1World.isRemote)
        {
            byte var8 = 32;

            if (par1World.checkChunksExist(par2 - var8, par3 - var8, par4 - var8, par2 + var8, par3 + var8, par4 + var8))
            {
                //if (par1World.getClosestPlayer(par2, par3, par4, 100) != null)
                {
                    EntityFallingGravityGoo var10 = new EntityFallingGravityGoo(par1World, (double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), mod_GreyGoo.BlockGravityGooID);
                    par1World.spawnEntityInWorld(var10);
                   
                }
            }
        }
      
        	par1World.setBlockWithNotify(par2, par3, par4, 0);
        
    }

    public  int tickRate()
    {
        

        return 5 + random.nextInt(5) ;
    }

    public static boolean canFallBelow(World par0World, int par1, int par2, int par3)
    {
        int var4 = par0World.getBlockId(par1, par2, par3);
        
        	if(par0World.isRemote)
        {
        	return false;
        }

        if (var4 == 0)
        {
            return true;
        }
        else if (var4 == Block.fire.blockID)
        {
            return true;
        }
        else
        {
            Material var5 = Block.blocksList[var4].blockMaterial;
            return var5 == Material.water ? true : var5 == Material.lava;
        }
        
    }
    public boolean onBlockActivated(World par1World, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
    {
        if (!par1World.isRemote && !entityplayer.isSneaking())
        {
        	assimilate(par1World, i, j, k);
            this.tryToFall(par1World, i, j, k);
        }

        return false;
    }
}
