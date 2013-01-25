package StevenGreyGoo.mod_GreyGoo;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockBlack extends BlockContainer
{
    Random rand = new Random();
    protected BlockBlack(int i, int j)
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
            boolean flag = false;
            int l = -3;
            int i1 = -3;
            int j1 = -3;

            for (; l < 4 && !flag; l++)
            {
                for (; i1 < 4 && !flag; i1++)
                {
                    for (; j1 < 4 && !flag; j1++)
                    {
                        if (world.getBlockId(i + l, j + i1, k + j1) != 0 && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockBlack.blockID && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockCancer2.blockID && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockTGD.blockID && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockTGDinert.blockID)
                        {
                            flag = true;
                        }
                    }

                    j1 = -5;
                }

                i1 = -5;
            }
            if (world.getBlockMetadata(i, j, k) != 2)
            {

            int k1 = -2;
            int l1 = -2;
            int i2 = -2;
            boolean flag1 = false;
            int numberofdark = 0;

            for (; l1 < 3; l1++)
            {
                for (; i2 < 3; i2++)
                {
                    for (; k1 < 3; k1++)
                    {
                        if (world.getBlockId(i + k1, j + l1, k + i2) == mod_GreyGoo.BlockBlack.blockID)
                        {
                            ++numberofdark;
                        }

                        if (world.getBlockId(i + k1, j + l1, k + i2) == mod_GreyGoo.BlockCleaner.blockID)
                        {
                            world.setBlockWithNotify(i, j, k, mod_GreyGoo.BlockCleaner.blockID);
                            l1 = 10;
                            i2 = 10;
                            k1 = 10;
                            break;
                        }

                        if (!world.isAirBlock(i + k1, j + l1, k + i2) && world.getBlockId(i + k1, j + l1, k + i2) != mod_GreyGoo.BlockCancer.blockID && world.getBlockId(i + k1, j + l1, k + i2) != mod_GreyGoo.BlockTGD.blockID && world.getBlockId(i + k1, j + l1, k + i2) != mod_GreyGoo.BlockTGDinert.blockID && world.getBlockId(i + k1, j + l1, k + i2) != mod_GreyGoo.BlockCancer2.blockID && world.getBlockId(i + k1, j + l1, k + i2) != mod_GreyGoo.BlockCleaner.blockID && world.getBlockId(i + k1, j + l1, k + i2) != mod_GreyGoo.BlockWall.blockID && world.getBlockId(i + k1, j + l1, k + i2) != mod_GreyGoo.BlockInert.blockID && world.getBlockId(i + k1, j + l1, k + i2) != mod_GreyGoo.BlockFreezer.blockID && world.getBlockId(i + k1, j + l1, k + i2) != blockID && world.getBlockId(i + k1, j + l1, k + i2) != Block.chest.blockID && !mod_GreyGoo.gooNeverEatThese.contains(world.getBlockId(i + k1, j + l1, k + i2)))
                        {
                            if (true)
                            {
                                if (rand.nextInt(60) == 1 && !(numberofdark > 15 && Math.abs(k1 + l1 + i2) < 3) && numberofdark < 100)
                                {
                                    world.setBlockWithNotify(i + k1, j + l1, k + i2, blockID);
                                    hasFood = true;
                                    mod_GreyGoo.instance.spreadLimiter.Destroyerspreadlimiter(true);
                                }
                            }
                        }
                    }

                    k1 = -3;
                }

                i2 = -3;
            }
            }

            if (!flag)
            {
                world.setBlock(i, j, k, mod_GreyGoo.BlockCancer2.blockID);
            }

            if (!hasFood)
            {
                world.setBlockMetadata(i, j, k, 2);
                world.removeBlockTileEntity(i, j, k);
            }
        
    }

    public TileEntity createNewTileEntity(World par1World)
    {
        TileEntity tile = new TileEntityBlack();
        return tile;
    }
    public int quantityDropped(Random par1Random)
    {
        if (par1Random.nextInt(10) == 1)
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
        if (mod_GreyGoo.isGooActive(this.blockID, world) && mod_GreyGoo.instance.spreadLimiter.Destroyerspreadlimiter(false) && !world.isRemote)
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

    public void onEntityWalking(World world, int i, int j, int k, Entity entity)
    {
        assimilate(world, i, j, k);
    }

    public void randomDisplayTick(World world, int i, int j, int k, Random random)
    {
        if (random.nextInt(100) != 0);

        for (int l = 0; l < 2; l++)
        {
            double d = (float)i + random.nextFloat();
            double d1 = (float)j + random.nextFloat();
            double d2 = (float)k + random.nextFloat();
            double d3 = 0.0D;
            double d4 = 0.0D;
            double d5 = 0.0D;
            int i1 = random.nextInt(2) * 2 - 1;
            d3 = ((double)random.nextFloat() - 0.5D) * 0.5D;
            d4 = ((double)random.nextFloat() - 0.5D) * 0.5D;
            d5 = ((double)random.nextFloat() - 0.5D) * 0.5D;

            if (world.getBlockId(i - 1, j, k) == blockID || world.getBlockId(i + 1, j, k) == blockID)
            {
                d2 = (double)k + 0.5D + 0.25D * (double)i1;
                d5 = random.nextFloat() * 2.0F * (float)i1;
            }
            else
            {
                d = (double)i + 0.5D + 0.25D * (double)i1;
                d3 = random.nextFloat() * 2.0F * (float)i1;
            }

            world.spawnParticle("portal", d, d1, d2, d3, d4, d5);
        }
    }
}
