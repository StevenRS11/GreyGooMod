package StevenGreyGoo.mod_GreyGoo;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BlockAirEater extends Block
{
    protected BlockAirEater(int i, int j)
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

    private void assimilate(World world, int i, int j, int k)
    {
        if (world.getBlockMetadata(i, j, k) != 2)
        {
        	SpreadHelper thisHelper=new SpreadHelper(world,i,j,k,1,false,true);
            boolean hasFood = false;
            thisHelper.addID(mod_GreyGoo.BlockCleanerID);
           
                        if (thisHelper.findBlocks().size()!=0)
                        {
                            world.setBlockWithNotify(i, j, k, mod_GreyGoo.BlockCleaner.blockID);
                        }
                        thisHelper.clearIDCheckList();
                        thisHelper.addID(0);
                        thisHelper.findBlocks();
                        
                        
                        int size=thisHelper.foundBlockCoords.size()-1;
                        while (size>-1)
                        {
                        	CoordHolder coords=(CoordHolder) thisHelper.foundBlockCoords.get(size);
                        	
                        	if (world.getBlockLightValue(coords.xCoord, coords.yCoord, coords.zCoord) > 6)
                        	{
                        		world.setBlockWithNotify(coords.xCoord, coords.yCoord, coords.zCoord, blockID);
                        		hasFood = true;
                        		mod_GreyGoo.instance.spreadLimiter.spreadLimiter(true);
                        	}
                        	size--;
                        }
                    
            if (!hasFood)
            {
                world.setBlockMetadata(i, j, k, 2);
            }
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

    public void updateTick(World world, int i, int j, int k, Random random)
    {
        if (mod_GreyGoo.isGooActive(this.blockID, world) && mod_GreyGoo.instance.spreadLimiter.spreadLimiter(false))
        {
            assimilate(world, i, j, k);
            decay(world, i, j, k);
        }
      
    }

    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
    {
        if (!world.isRemote && !entityplayer.isSneaking())
        {
            assimilate(world, i, j, k);
            return true;
        }

        return false;
    }
    private void decay(World world, int i, int j, int k)
    {
        int l = -1;
        int i1 = -1;
        int j1 = -1;
        boolean flag = false;

        for (; i1 < 2; i1++)
        {
            for (; j1 < 2; j1++)
            {
                for (; l < 2; l++)
                {
                    if (world.getBlockLightValue(i + l, j + i1, k + j1) < 2)
                    {
                        world.setBlock(i, j, k, 0);
                    }
                }

                l = -1;
            }

            j1 = -1;
        }
    }
}
