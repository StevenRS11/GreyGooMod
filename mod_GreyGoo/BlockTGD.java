package StevenGreyGoo.mod_GreyGoo;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.block.Block;
public class BlockTGD extends Block
{
    private boolean hasTicked;

    protected BlockTGD(int i, int j)
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

    protected Random rand;
    Random random = new Random();

    public int tickRate()
    {
        return 5;
    }

    private int numgen4(Random random)
    {
        int i = random.nextInt(4);
        return i;
    }

    private int numgen6(Random random)
    {
        int i = random.nextInt(6);
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
            return 1;
        }
    }

    private void assimilate(World world, int i, int j, int k, Random random)
    {
        {
            int l = -3;
            int i1 = -3;
            int j1 = -3;
            byte byte0 = 0;
            int k1 = 0;
            boolean flag = false;
            boolean flag2 = false;
            boolean flag3 = false;
            int l1 = -3;
            int i2 = -3;
            int j2 = -3;

            for (; l1 < 4; l1++)
            {
                for (; i2 < 4; i2++)
                {
                    for (; j2 < 4; j2++)
                    {
                        boolean flag1;

                        if (world.getBlockId(i + l1, j + i2, k + j2) != 0 && world.getBlockId(i + l1, j + i2, k + j2) != mod_GreyGoo.BlockCancer2.blockID && world.getBlockId(i + l1, j + i2, k + j2) != blockID)
                        {
                            flag1 = true;
                        }
                    }

                    j2 = -3;
                }

                i2 = -3;
            }

            if (j < mod_GreyGoo.bloomheight || j > mod_GreyGoo.bloomheight + 13 && j < 240)
            {
                for (; l < 4; l++)
                {
                    for (; j1 < 4; j1++)
                    {
                        for (; i1 < 4; i1++)
                        {
                            if (world.getBlockId(i + l, j + Math.abs(i1), k + j1) == mod_GreyGoo.BlockCleaner.blockID)
                            {
                                world.setBlockWithNotify(i, j, k, mod_GreyGoo.BlockCleaner.blockID);
                                continue;
                            }

                            if (world.getBlockId(i + l, j + Math.abs(i1), k + j1) == mod_GreyGoo.BlockTGDinert.blockID)
                            {
                                world.setBlockWithNotify(i, j, k, mod_GreyGoo.BlockTGDinert.blockID);
                                continue;
                            }

                            if (Math.abs(l) + Math.abs(i1) + Math.abs(j1) != 1 || flag2 || world.getBlockId(i + l, j + Math.abs(i1), k + j1) == mod_GreyGoo.BlockOrangeRed.blockID || world.getBlockId(i + l, j + Math.abs(i1), k + j1) == mod_GreyGoo.BlockBlack.blockID  || world.getBlockId(i + l, j + Math.abs(i1), k + j1) == mod_GreyGoo.BlockWall.blockID || world.getBlockId(i + l, j + Math.abs(i1), k + j1) == mod_GreyGoo.BlockInert.blockID || world.getBlockId(i + l, j + Math.abs(i1), k + j1) == mod_GreyGoo.BlockFreezer.blockID || world.getBlockId(i + l, j + Math.abs(i1), k + j1) == mod_GreyGoo.BlockGreyEater.blockID || world.getBlockId(i + l, j + Math.abs(i1), k + j1) == mod_GreyGoo.BlockWall.blockID || world.getBlockId(i + l, j + Math.abs(i1), k + j1) == mod_GreyGoo.BlockTGDinert.blockID || world.getBlockId(i + l, j + Math.abs(i1), k + j1) == Block.chest.blockID)
                            {
                                continue;
                            }

                            int k2 = -1;
                            int i3 = -1;
                            int k3 = -1;
                            k1 = byte0;

                            

                            if (k1 < 3 && l % 2 == 0)
                            {
                                int l3 = numgen6(random);
                                world.setBlockWithNotify(i, j + Math.abs(i1), k, blockID);
                                mod_GreyGoo.instance.spreadLimiter.TGDspreadlimiter(true);

                                if (random.nextInt(8) == 0)
                                {
                                    world.scheduleBlockUpdate(i, j + Math.abs(i1), k, blockID, random.nextInt(15));
                                }

                                if (l3 == 1)
                                {
                                    world.setBlockWithNotify(i + l, j, k + j1, blockID);
                                    mod_GreyGoo.instance.spreadLimiter.TGDspreadlimiter(true);
                                }

                                world.setBlockWithNotify(i, j + Math.abs(i1) + 2, k, blockID);
                                mod_GreyGoo.instance.spreadLimiter.TGDspreadlimiter(true);
                                byte0 = -1;
                            }
                            else
                            {
                                int i4 = numgen6(random);
                                world.setBlock(i, j + Math.abs(i1), k, blockID);
                                mod_GreyGoo.instance.spreadLimiter.TGDspreadlimiter(true);

                                if (i4 == 1)
                                {
                                    world.setBlockWithNotify(i - l, j, k - j1, blockID);
                                    mod_GreyGoo.instance.spreadLimiter.TGDspreadlimiter(true);
                                }

                                world.setBlockWithNotify(i, j + Math.abs(i1) + 1, k, blockID);
                                mod_GreyGoo.instance.spreadLimiter.TGDspreadlimiter(true);
                                byte0 = -1;

                                if (random.nextInt(10) == 0)
                                {
                                    world.scheduleBlockUpdate(i, j + Math.abs(i1) + 1, k, blockID, random.nextInt(15));
                                }
                            }

                            if (k1 < 2)
                            {
                                world.setBlockWithNotify(i, j + Math.abs(i1 + 2), k, blockID);
                                mod_GreyGoo.instance.spreadLimiter.TGDspreadlimiter(true);
                                byte0 = -1;

                                if (random.nextInt(11) == 0)
                                {
                                    world.scheduleBlockUpdate(i, j + Math.abs(i1) + 2, k, blockID, random.nextInt(15));
                                }
                            }

                            world.setBlockWithNotify(i, j, k, mod_GreyGoo.BlockTGDinert.blockID);
                        }

                        i1 = -3;
                    }

                    j1 = -3;
                }

                flag2 = true;
            }

            if (j == mod_GreyGoo.bloomheight || j == 240)
            {
                if (!world.isRemote)
                {
                    if (!mod_GreyGoo.instance.TGDbloom && !world.isRemote)
                    {
                        mod_GreyGoo.instance.DidBloomchanger();
                        mod_GreyGoo.proxy.writeNBTToFile(world);
                    }
                }

                if (mod_GreyGoo.totalnumberofTGDgolems < mod_GreyGoo.maxnumberofTGDgolems && random.nextInt(200) == 0)
                {
                    if (!world.isRemote)
                    {
                        EntityTGDGolem creeper = new EntityTGDGolem(world);
                        creeper.setLocationAndAngles(i, j - 4, k, 5, 6);
                        world.spawnEntityInWorld(creeper);
                    }
                }

                int l2 = 1;

                for (; l2 < 4; l2++)
                {
                    int j3 = numgen6(random);

                    if (world.getBlockId(i - j3, j, k) != mod_GreyGoo.BlockCleaner.blockID && world.getBlockId(i - j3, j, k) != mod_GreyGoo.BlockTGDinert.blockID)
                    {
                        world.setBlockWithNotify(i - j3, j, k, blockID);
                        mod_GreyGoo.instance.spreadLimiter.TGDspreadlimiter(true);

                        if (random.nextInt(12) == 0)
                        {
                            world.scheduleBlockUpdate(i - j3, j, k, blockID, random.nextInt(8));
                        }
                    }

                    if (world.getBlockId(i + j3, j, k) != mod_GreyGoo.BlockCleaner.blockID && world.getBlockId(i + j3, j, k) != mod_GreyGoo.BlockTGDinert.blockID)
                    {
                        world.setBlockWithNotify(i + j3, j, k, blockID);
                        mod_GreyGoo.instance.spreadLimiter.TGDspreadlimiter(true);

                        if (random.nextInt(12) == 0)
                        {
                            world.scheduleBlockUpdate(i + j3, j, k, blockID, random.nextInt(8));
                        }
                    }

                    if (world.getBlockId(i, j, k - j3) != mod_GreyGoo.BlockCleaner.blockID && world.getBlockId(i, j, k - j3) != mod_GreyGoo.BlockTGDinert.blockID)
                    {
                        world.setBlockWithNotify(i, j, k - j3, blockID);
                        mod_GreyGoo.instance.spreadLimiter.TGDspreadlimiter(true);

                        if (random.nextInt(12) == 0)
                        {
                            world.scheduleBlockUpdate(i, j, k - j3, blockID, random.nextInt(8));
                        }
                    }

                    if (world.getBlockId(i, j, k + j3) != mod_GreyGoo.BlockCleaner.blockID && world.getBlockId(i, j, k + j3) != mod_GreyGoo.BlockTGDinert.blockID)
                    {
                        world.setBlockWithNotify(i, j, k + j3, blockID);
                        mod_GreyGoo.instance.spreadLimiter.TGDspreadlimiter(true);

                        if (random.nextInt(12) == 0)
                        {
                            world.scheduleBlockUpdate(i, j, k + j3, blockID, random.nextInt(8));
                        }
                    }

                    world.setBlockWithNotify(i, j, k, mod_GreyGoo.BlockTGDinert.blockID);
                }
            }
        }
    }

    private void decay(World world, int i, int j, int k)
    {
        int l = -5;
        int i1 = -5;
        int j1 = -5;
        int k1 = 0;
        int l1 = 0;

        for (; i1 < 4; i1++)
        {
            for (; j1 < 4; j1++)
            {
                for (; l < 4; l++)
                {
                    if (world.getBlockId(i + l, j, k + j1) == blockID && Math.abs(l) + Math.abs(j1) != 0 && Math.abs(l) + Math.abs(j1) == 4)
                    {
                        k1++;
                    }

                    if (world.getBlockId(i + l, j, k + j1) == 0 && Math.abs(l) + Math.abs(j1) != 0 && Math.abs(l) + Math.abs(j1) == 4)
                    {
                        l1++;
                    }
                }

                l = -5;
            }

            j1 = -5;
        }

        if (k1 != 0)
        {
            int i2 = -1;
            int j2 = -1;
            int k2 = -1;

            for (; j2 < 2; j2++)
            {
                for (; k2 < 2; k2++)
                {
                    for (; i2 < 2; i2++)
                    {
                        if (world.getBlockId(i + i2, j, k + k2) == blockID)
                        {
                            world.setBlockWithNotify(i + i2, j, k + k2, mod_GreyGoo.BlockTGDinert.blockID);
                            world.setBlockWithNotify(i, j - 1, k, mod_GreyGoo.BlockTGDinert.blockID);
                            continue;
                        }

                        if (world.getBlockId(i, j - 1, k) == blockID)
                        {
                            world.setBlockWithNotify(i, j - 1, k, mod_GreyGoo.BlockTGDinert.blockID);
                        }
                    }

                    i2 = -1;
                }

                k2 = -1;
            }
        }
    }

    public void updateTick(World world, int i, int j, int k, Random random)
    {
        if (mod_GreyGoo.isGooActive(this.blockID, world) && !world.isRemote &&  mod_GreyGoo.instance.spreadLimiter.TGDspreadlimiter(false))
        {
            assimilate(world, i, j, k, random);
            decay(world, i, j, k);
        }
    }

    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
    {
        if (!world.isRemote && !entityplayer.isSneaking())
        {
            Random random = new Random();
            assimilate(world, i, j, k, random);
            return true;
        }

        return false;
    }
}
