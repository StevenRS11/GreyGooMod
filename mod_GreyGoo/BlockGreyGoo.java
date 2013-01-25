package StevenGreyGoo.mod_GreyGoo;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.block.Block;
public class BlockGreyGoo extends Block
{
    protected BlockGreyGoo(int i, int j)
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
        boolean hasFood = false;
        
        SpreadHelper thisHelper=new SpreadHelper(world,i,j,k,1,false,true);
        

        if (world.getBlockMetadata(i, j, k) != 2)
        {
        	
        	
        	thisHelper.addID(mod_GreyGoo.BlockCleanerID);
        	
                        if (!(thisHelper.findBlocks().size()==0))
                        {
                            world.setBlock(i, j, k, mod_GreyGoo.BlockCleaner.blockID);
                           
                        }
                        else
                        {
                        
                        thisHelper.setNegOrPosSearch(false);
                        thisHelper.clearIDCheckList();
                    	thisHelper.blocksToFind=mod_GreyGoo.gooNeverEatThese;
                    	thisHelper.blocksToFind.add(this.blockID);
                    	thisHelper.blocksToFind.add(0);

                    	thisHelper.findBlocks();
                    	
                    	int size= thisHelper.foundBlockCoords.size()-1;
                    	CoordHolder hold;
                    	
                    	while (size>-1)
                    	{
                        	//mod_GreyGoo.instance.proxy.printStringClient(String.valueOf(size));

                    		 hold= (CoordHolder)thisHelper.foundBlockCoords.get(size);
                    		 world.setBlockWithNotify(hold.xCoord,hold.yCoord, hold.zCoord, blockID);
                             hasFood = true;
                             mod_GreyGoo.instance.spreadLimiter.spreadLimiter(true);
                             size--;
                    	}
                        }

                       
                    


            if (!hasFood)
            {
                world.setBlockMetadata(i, j, k, 2);
            }
        }
    }

    private void decay(World world, int i, int j, int k)
    {
        boolean flag = false;
        int l = 0;
        boolean flag1 = false;

        if (world.isAirBlock(i, j + 1, k) && world.isAirBlock(i + 1, j, k) && world.isAirBlock(i - 1, j, k) && world.isAirBlock(i, j, k + 1) && world.isAirBlock(i, j, k - 1))
        {
            while (!flag)
            {
                if (world.isAirBlock(i + 1, j + l, k) && world.isAirBlock(i - 1, j + l, k) && world.isAirBlock(i, j + l, k + 1) && world.isAirBlock(i, j + l, k - 1) && l > -100)
                {
                    l--;
                }
                else
                {
                    flag = true;

                    if (l >= -99);
                }
            }

            for (l = Math.abs(l); l != 0; l--)
            {
                world.setBlock(i, j - l, k, 0);
            }

            world.setBlock(i, j, k, 0);
        }
        else if (world.isAirBlock(i, j + 1, k))
        {
            world.setBlock(i, j, k, 0);
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
        if (mod_GreyGoo.isGooActive(this.blockID, world) && mod_GreyGoo.instance.spreadLimiter.spreadLimiter(false) && !world.isRemote)
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
}
