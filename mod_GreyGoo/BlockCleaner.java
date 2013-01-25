package StevenGreyGoo.mod_GreyGoo;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class BlockCleaner extends Block
{
    Random random;

    protected BlockCleaner(int i, int j)
    {
        super(i, j, Material.ground);
        random = new Random();
        setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }
    @Override
    public String getTextureFile()
    {
        return "/GooBlockTextures.png";
    }

    private int numgen4(Random random1)
    {
        int i = random1.nextInt(7);
        return i;
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

    private void clean(World world, int i, int j, int k)
    {
        int l = -1;
        int i1 = -1;
        int j1 = -1;
        int k1 = numgen4(random);
        int l1 = -3;
        int i2 = -3;
        int j2 = -3;
        boolean flag = false;

        for (; l1 < 4; l1++)
        {
            for (; i2 < 4; i2++)
            {
                for (; j2 < 4; j2++)
                {
                    if (mod_GreyGoo.cleanerList.contains(world.getBlockId(i + l1, j + i2, k + j2)))
                    {
                        flag = true;
                    }
                }

                j2 = -3;
            }

            i2 = -3;
        }

        for (; i1 < 3; i1++)
        {
            for (; j1 < 3; j1++)
            {
                for (; l < 3; l++)
                {
                    if (Math.abs(j1) + Math.abs(l) + Math.abs(i1) < 3 && mod_GreyGoo.cleanerList.contains(world.getBlockId(i + l, j + i1, k + j1)))
                    {
                        world.setBlockWithNotify(i + l, j + i1, k + j1, blockID);
                    }

                    if (!flag && Math.abs(j1) + Math.abs(l) + Math.abs(i1) != 0 && world.getBlockId(i + l, j + i1, k + j1) == this.blockID)
                    {
                        world.setBlockWithNotify(i + l, j + i1, k + j1, 0);
                    }

                    if (flag && k1 == 1)
                    {
                        world.scheduleBlockUpdate(i, j + i1, k, blockID, random.nextInt(12));
                    }

                    if (flag && k1 == 2)
                    {
                        world.scheduleBlockUpdate(i + l, j, k, blockID, random.nextInt(12));
                    }

                    if (flag && k1 == 3)
                    {
                        world.scheduleBlockUpdate(i, j, k + j1, blockID, random.nextInt(12));
                    }
                }

                l = -2;
            }

            j1 = -2;
        }
    }

    private void decay(World world, int i, int j, int k)
    {
        world.setBlockWithNotify(i, j, k, 0);
    }

    public void updateTick(World world, int i, int j, int k, Random random1)
    {
        if (!world.isRemote && mod_GreyGoo.isGooActive(this.blockID, world))
        {
            clean(world, i, j, k);
            decay(world, i, j, k);
        }
    }
    public void onEntityWalking(World world, int i, int j, int k, Entity entity)
    {
        if (entity instanceof EntityTGDGolem)
        {
            entity.attackEntityFrom(DamageSource.generic, 50);
        }
    }

    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
    {
        if (!world.isRemote && !entityplayer.isSneaking())
        {
            clean(world, i, j, k);
            return true;
        }

        return false;
    }
}
