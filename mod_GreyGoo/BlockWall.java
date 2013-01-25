package StevenGreyGoo.mod_GreyGoo;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.block.Block;
public class BlockWall extends Block
{
    Random rand = new Random();
    int grew1;

    protected BlockWall(int i, int j)
    {
        super(i, j, Material.ground);
        grew1 = 0;
        setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }
    @Override
    public String getTextureFile()
    {
        return "/GooBlockTextures.png";
    }

    private int numgen4(Random random)
    {
        int i = random.nextInt(4);
        return i;
    }

    private int numgen30(Random random)
    {
        int i = random.nextInt(30);
        return i;
    }

    public void updateTick(World world, int i, int j, int k, Random random)
    {
        if (!world.isRemote && mod_GreyGoo.instance.spreadLimiter.spreadLimiter(false) && mod_GreyGoo.isGooActive(this.blockID, world))
        {
            grow(world, i, j, k, random);
        }
    }
    protected void grow(World world, int i, int j, int k, Random random)
    {
        byte byte0 = 10;
        char c = '\310';
        byte byte1 = -3;
        byte byte2 = -3;
        byte byte3 = -3;
        boolean flag = false;
        boolean flag1 = false;
        int j2 = 0;
        byte byte4 = -1;
        byte byterand5 = -1;
        byte byte6 = -1;
        int l = -1;
        int i1 = -1;
        int j1 = -1;
        boolean hasFood = false;

        if (world.getBlockMetadata(i, j, k) != 2)
        {
            for (; i1 < 2; i1++)
            {
                for (; j1 < 2; j1++)
                {
                    for (; l < 2; l++)
                    {
                        if (world.getBlockId(i + l, j + i1, k + j1) == mod_GreyGoo.BlockCleaner.blockID)
                        {
                            world.setBlock(i, j, k, mod_GreyGoo.BlockCleaner.blockID);
                            continue;
                        }

                        if ((world.getBlockId(i - l, j - i1, k - j1) == this.blockID || world.getBlockId(i - l, j - i1, k - j1) == mod_GreyGoo.BlockInert.blockID) && mod_GreyGoo.isGooActive(this.blockID, world))
                        {
                            if (Math.abs(l) + Math.abs(i1) + Math.abs(j1) < 2 && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockInert.blockID && (!mod_GreyGoo.gooNeverEatThese.contains(world.getBlockId(i + l, j + i1, k + j1)) || (world.getBlockId(i + l, j + i1, k + j1) == Block.snow.blockID)))
                            {
                                world.setBlockWithNotify(i + l, j + i1, k + j1, blockID);
                                mod_GreyGoo.instance.spreadLimiter.spreadLimiter(true);

                                if (rand.nextBoolean() && rand.nextBoolean() && rand.nextBoolean() && rand.nextBoolean())
                                {
                                }
                            }
                        }
                    }

                    l = -1;
                }

                j1 = -1;
            }

            if (!hasFood)
            {
                world.setBlockMetadata(i, j, k, 2);
            }
        }
    }

    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
    {
    	Random rand=new Random();
        if (!entityplayer.isSneaking() && !world.isRemote)
        	
        {
        	world.setBlockMetadata(i, j, k, 0);
            grow(world,i, j, k,rand);
            return true;
        }
        else
        {
            return false;
        }
    }
}
