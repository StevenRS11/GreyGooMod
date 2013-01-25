package StevenGreyGoo.mod_GreyGoo;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.block.Block;
public class BlockWaterEater extends Block
{
    protected BlockWaterEater(int i, int j)
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
            boolean noFood = true;
            int l = -1;
            int i1 = -1;
            int j1 = -1;

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

                        if ((world.getBlockId(i + l, j + i1, k + j1) == Block.waterMoving.blockID || world.getBlockId(i + l, j + i1, k + j1) == Block.waterStill.blockID || world.getBlockId(i + l, j + i1, k + j1) == Block.lavaMoving.blockID || world.getBlockId(i + l, j + i1, k + j1) == Block.lavaStill.blockID) && Math.abs(l) + Math.abs(i1) + Math.abs(j1) == 1)
                        {
                            noFood = false;
                            world.setBlockWithNotify(i + l, j + i1, k + j1, blockID);
                            mod_GreyGoo.instance.spreadLimiter.spreadLimiter(true);
                        }
                    }

                    l = -1;
                }

                j1 = -1;
            }

            if (noFood)
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
