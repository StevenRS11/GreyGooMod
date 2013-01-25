package StevenGreyGoo.mod_GreyGoo;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BlockBubble extends Block
{
    protected BlockBubble(int i, int j)
    {
        super(i, j, Material.ground);
        setTickRandomly(false);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }
    Random random = new Random();
    @Override
    public String getTextureFile()
    {
        return "/GooBlockTextures.png";
    }

    private void assimilate(World world, int i, int j, int k)
    {
        int l = -2;
        int i1 = -2;
        int j1 = -2;
        byte byte0 = 6;

        if (mod_GreyGoo.BubbleisSpreading)
        {
            for (; i1 < 3; i1++)
            {
                for (; j1 < 3; j1++)
                {
                    for (; l < 3; l++)
                    {
                        if (world.getBlockId(i + l, j + i1, k + j1) == mod_GreyGoo.BlockCleaner.blockID)
                        {
                            world.setBlock(i, j, k, mod_GreyGoo.BlockCleaner.blockID);
                            continue;
                        }

                        if (world.isAirBlock(i + l, j + i1, k + j1) && Math.abs(l) + Math.abs(i1) + Math.abs(j1) < 2 && world.getBlockLightValue(i + l, j + i1, k + j1) > 9)
                        {
                            world.setBlockWithNotify(i + l, j + i1, k + j1, blockID);
                            world.scheduleBlockUpdate(i + l, j + i1, k + j1, blockID, random.nextInt(15));
                        }
                    }

                    l = -2;
                }

                j1 = -2;
            }
        }
    }
    public int quantityDropped(Random par1Random)
    {
        if (par1Random.nextInt(15) == 1)
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
        if (rand.nextBoolean())
        {
            return mod_GreyGoo.BlockInertID + 4256;
        }
        else
        {
            return mod_GreyGoo.BlockAirEaterID + 4256;
        }
    }

    public void updateTick(World world, int i, int j, int k, Random random)
    {
        assimilate(world, i, j, k);

        if (mod_GreyGoo.BubbleisSpreading)
        {
            decay(world, i, j, k);
        }
    }

    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
    {
        if (!world.isRemote && !entityplayer.isSneaking())
        {
            mod_GreyGoo.BubbleisSpreading = true;
            assimilate(world, i, j, k);
            world.scheduleBlockUpdate(i, j, k, this.blockID, 5);
            return true;
        }

        return false;
    }

    private void decay(World world, int i, int j, int k)
    {
        int l = -2;
        int i1 = -2;
        int j1 = -2;
        boolean flag = false;

        for (; i1 < 3; i1++)
        {
            for (; j1 < 3; j1++)
            {
                for (; l < 3; l++)
                {
                    if (world.getBlockLightValue(i + l, j + i1, k + j1) < 2)
                    {
                        world.setBlock(i, j, k, 0);
                    }
                }

                l = -2;
            }

            j1 = -2;
        }
    }
}
