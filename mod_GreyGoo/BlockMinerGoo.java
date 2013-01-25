package StevenGreyGoo.mod_GreyGoo;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.block.Block;
public class BlockMinerGoo extends Block
{
    protected BlockMinerGoo(int i, int j)
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

    protected void mine(World world, int i, int j, int k)
    {
        if (world.getBlockMetadata(i, j, k) != 2)
        {
            boolean hasFood = false;
            int l = -1;
            int i1 = -1;
            int j1 = -1;

            for (; i1 < 2; i1++)
            {
                for (; j1 < 2; j1++)
                {
                    for (; l < 2; l++)
                    {
                        if (world.getBlockId(i + l, j + i1, k + j1) == oreDiamond.blockID || world.isAirBlock(i + l, j + i1, k + j1) || Math.abs(l) + Math.abs(i1) + Math.abs(j1) != 1 || world.getBlockId(i + l, j + i1, k + j1) == mod_GreyGoo.BlockWall.blockID || world.getBlockId(i + l, j + i1, k + j1) == mod_GreyGoo.BlockInert.blockID || world.getBlockId(i + l, j + i1, k + j1) == Block.oreGold.blockID || world.getBlockId(i + l, j + i1, k + j1) == Block.oreIron.blockID || world.getBlockId(i + l, j + i1, k + j1) == Block.oreCoal.blockID || world.getBlockId(i + l, j + i1, k + j1) == Block.oreLapis.blockID || world.getBlockId(i + l, j + i1, k + j1) == mod_GreyGoo.BlockWall.blockID || world.getBlockId(i + l, j + i1, k + j1) == mod_GreyGoo.BlockInert.blockID || world.getBlockId(i + l, j + i1, k + j1) == Block.bedrock.blockID || world.getBlockId(i + l, j + i1, k + j1) == Block.chest.blockID || world.getBlockId(i + l, j + i1, k + j1) == Block.oreRedstone.blockID || world.getBlockId(i + l, j + i1, k + j1) == Block.oreDiamond.blockID || world.getBlockId(i + l, j + i1, k + j1) == mod_GreyGoo.BlockBlack.blockID || world.getBlockId(i + l, j + i1, k + j1) == blockID || world.getBlockId(i + l, j + i1, k + j1) == blockID)
                        {
                            continue;
                        }

                        if ((mod_GreyGoo.gooNeverEatThese.contains(world.getBlockId(i + l, j + i1, k + j1)) || !(mod_GreyGoo.mineTheseOnly.contains(world.getBlockId(i + l, j + i1, k + j1)))))
                        {
                            continue;
                        }

                        world.setBlockWithNotify(i + l, j + i1, k + j1, blockID);
                        hasFood = true;
                        mod_GreyGoo.instance.spreadLimiter.spreadLimiter(true);
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
            mine(world, i, j, k);
        }
    }

    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
    {
        if (!world.isRemote && !entityplayer.isSneaking())
        {
            mine(world, i, j, k);
            return true;
        }

        return false;
    }
}
