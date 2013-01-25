package StevenGreyGoo.mod_GreyGoo;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
public class BlockCancer extends Block
{
    private boolean hasTicked;

    protected BlockCancer(int i, int j)
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
            int l = -3;
            int i1 = -3;
            int j1 = -3;
            byte byte0 = 0;
            boolean flag = false;
            boolean flag1 = false;
            int l1 = -3;
            int i2 = -3;
            int j2 = -3;

            for (; l1 < 4; l1++)
            {
                for (; i2 < 4; i2++)
                {
                    for (; j2 < 4; j2++)
                    {
                        if (world.getBlockId(i + l1, j + i2, k + j2) != 0 && world.getBlockId(i + l1, j + i2, k + j2) != mod_GreyGoo.BlockCancer.blockID && world.getBlockId(i + l1, j + i2, k + j2) != mod_GreyGoo.BlockTGDinert.blockID && world.getBlockId(i + l1, j + i2, k + j2) != mod_GreyGoo.BlockTGD.blockID  && world.getBlockId(i + l1, j + i2, k + j2) != mod_GreyGoo.BlockCancer2.blockID && world.getBlockId(i + l1, j + i2, k + j2) != mod_GreyGoo.BlockTGD.blockID && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockTGDinert.blockID && world.getBlockId(i + l1, j + i2, k + j2) != mod_GreyGoo.BlockBlack.blockID)
                        {
                            flag1 = true;
                        }
                    }

                    j2 = -3;
                }

                i2 = -3;
            }
            if (world.getBlockMetadata(i, j, k) != 2)
            {

            if (flag1)
            {
                for (; i1 < 4; i1++)
                {
                    for (; j1 < 4; j1++)
                    {
                        for (; l < 4; l++)
                        {
                            if (Math.abs(l) + Math.abs(i1) + Math.abs(j1) == 1 && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockBlack.blockID && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockInert.blockID && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockBlack.blockID && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockCancer2.blockID)
                            {
                                int k2 = -1;
                                int l2 = -1;
                                int i3 = -1;
                                int k1 = byte0;

                                for (; l2 < 3; l2++)
                                {
                                    for (; i3 < 3; i3++)
                                    {
                                        for (; k2 < 3; k2++)
                                        {
                                            if (world.getBlockId(i + l + k2, j + i1 + l2, k + j1 + i3) == mod_GreyGoo.BlockCancer.blockID && Math.abs(k2) + Math.abs(l2) + Math.abs(i3) != 0 && Math.abs(k2) + Math.abs(l2) + Math.abs(i3) < 3)
                                            {
                                                k1++;
                                            }
                                        }

                                        k2 = -2;
                                    }

                                    i3 = -2;
                                }

                                if (k1 < 3)
                                {
                                    hasFood = true;
                                    world.setBlockWithNotify(i + l, j + i1, k + j1, blockID);
                                    mod_GreyGoo.instance.spreadLimiter.Destroyerspreadlimiter(true);
                                    byte0 = -1;
                                }
                            }

                            if (world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockBlack.blockID && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockCancer.blockID && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockWall.blockID && Math.abs(l) + Math.abs(i1) + Math.abs(j1) < 5 && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockCleaner.blockID && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockBlack.blockID && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockCancer2.blockID && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockTGD.blockID && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockTGDinert.blockID && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockInert.blockID && !mod_GreyGoo.gooNeverEatThese.contains(world.getBlockId(i + l, j + i1, k + j1)))
                            {
                            	hasFood = true;
                                world.setBlockWithNotify(i + l, j + i1, k + j1, 0);
                            }
                        }

                        l = -3;
                    }

                    j1 = -3;
                }
            }
            }

            if (!flag1)
            {
                if (hasTicked)
                {
                    hasTicked = false;
                }
                else
                {
                    hasTicked = true;
                }

                if (hasTicked)
                {
                    world.setBlockWithNotify(i, j, k, mod_GreyGoo.BlockBlack.blockID);
                }
            }

            if (!hasFood)
            {
                world.setBlockMetadata(i, j, k, 2);
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

    public void updateTick(World world, int i, int j, int k, Random random)
    {
        if (mod_GreyGoo.isGooActive(this.blockID, world) && mod_GreyGoo.instance.spreadLimiter.Destroyerspreadlimiter(false))
        {
            assimilate(world, i, j, k);
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
