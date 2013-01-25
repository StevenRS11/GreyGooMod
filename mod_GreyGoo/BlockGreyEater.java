package StevenGreyGoo.mod_GreyGoo;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.block.Block;
public class BlockGreyEater extends Block
{
    Random rand = new Random();
    private int thisMetaData;
    protected BlockGreyEater(int i, int j)
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
                    int blockToEat = world.getBlockId(i + l, j + i1, k + j1);

                    if (blockToEat == mod_GreyGoo.BlockCleaner.blockID)
                    {
                        world.setBlock(i, j, k, mod_GreyGoo.BlockCleaner.blockID);
                        continue;
                    }
                    else if (blockToEat == mod_GreyGoo.BlockCancer.blockID)
                    {
                        world.setBlock(i, j, k, mod_GreyGoo.BlockCancer.blockID);
                        continue;
                    }
                    else if (!world.isAirBlock(i + l, j + i1, k + j1)  && !mod_GreyGoo.gooNeverEatThese.contains(blockToEat) && blockToEat != this.blockID&& Math.abs(l) + Math.abs(i1) + Math.abs(j1) == 1)
                    {
                        {
                        	mod_GreyGoo.instance.spreadLimiter.spreadLimiter(true);

                            if (blockToEat == Block.waterStill.blockID)
                            {
                                world.setBlockAndMetadataWithNotify(i + l, j + i1, k + j1, blockID, 5);
                            }
                            else if (blockToEat == Block.gravel.blockID)
                            {
                                world.setBlockAndMetadataWithNotify(i + l, j + i1, k + j1, blockID, 3);
                            }
                            else if (blockToEat == Block.sand.blockID)
                            {
                                world.setBlockAndMetadataWithNotify(i + l, j + i1, k + j1, blockID, 3);
                            }
                            else if (blockToEat == Block.oreIron.blockID)
                            {
                                world.setBlockAndMetadataWithNotify(i + l, j + i1, k + j1, blockID, 7);
                            }
                            else if (blockToEat == Block.oreCoal.blockID)
                            {
                                world.setBlockAndMetadataWithNotify(i + l, j + i1, k + j1, blockID, 7);
                            }
                            else if (blockToEat == Block.leaves.blockID)
                            {
                                world.setBlockAndMetadataWithNotify(i + l, j + i1, k + j1, blockID, 4);
                            }
                            else if (blockToEat == Block.wood.blockID)
                            {
                                world.setBlockAndMetadataWithNotify(i + l, j + i1, k + j1, blockID, 4);
                            }
                            else if (world.isAirBlock(i + l, j + i1 + 1, k + j1) && rand.nextInt(5) == 1)
                            {
                                world.setBlockAndMetadataWithNotify(i + l, j + i1, k + j1, blockID, 8);
                            }
                            else
                            {
                                world.setBlockAndMetadataWithNotify(i + l, j + i1, k + j1, blockID, 0);
                            }
                        }
                    }
                }

                l = -1;
            }

            j1 = -1;
        }
    }

    public void onBlockAdded(World world, int i, int j, int k)
    {
        int chance1 = rand.nextInt(100);
        int chance2 = rand.nextInt(500);
        int chance3 = rand.nextInt(3000);
        int chance4 = rand.nextInt(3);

        if (chance1 == 1)
        {
            world.setBlockMetadataWithNotify(i, j, k, 1);
        }

        if (chance1 == 2)
        {
            world.setBlockMetadataWithNotify(i, j, k, 2);
        }

        if (chance2 == 3)
        {
            world.setBlockMetadataWithNotify(i, j, k, 3);
        }

        if (chance2 == 4)
        {
            world.setBlockMetadataWithNotify(i, j, k, 4);
        }

        if (chance2 == 5)
        {
            world.setBlockMetadataWithNotify(i, j, k, 5);
        }

        if (chance1 == 6)
        {
            world.setBlockMetadataWithNotify(i, j, k, 6);
        }

        if (chance2 == 7)
        {
            world.setBlockMetadataWithNotify(i, j, k, 7);
        }

        if (chance1 == 8)
        {
            world.setBlockMetadataWithNotify(i, j, k, 8);
        }

        if (chance3 == 9)
        {
            world.setBlockWithNotify(i, j, k, mod_GreyGoo.BlockCancerID);
        }

        super.onBlockAdded(world, i, j, k);
    }

    public int idDropped(int i, Random rand, int j)
    {
        thisMetaData = i;

        switch (i)
        {
            case 0:
                return mod_GreyGoo.ItemModifierGrey.itemID;

            case 1:
                return mod_GreyGoo.ItemModifierGreen.itemID;

            case 2:
                return mod_GreyGoo.ItemModifierOrange.itemID;

            case 3:
                return mod_GreyGoo.ItemModifierYellow.itemID;

            case 4:
                return mod_GreyGoo.ItemModifierPurple.itemID;

            case 5:
                return mod_GreyGoo.ItemModifierBlue.itemID;

            case 6:
                return mod_GreyGoo.ItemModifierRed.itemID;

            case 7:
                return mod_GreyGoo.ItemModifierBrown.itemID;

            case 8:
                return mod_GreyGoo.ItemModifierWhite.itemID;

            default :
                return mod_GreyGoo.ItemModifierGrey.itemID;
        }
    }

    public int quantityDropped(Random par1Random)
    {
        return 1;
    }
    public int getRenderColor(int i)
    {
        if (i == 1)
        {
            return 0xDCFADC;
        }

        if (i == 2)
        {
            return 0xFFDCB4;
        }

        if (i == 3)
        {
            return 0xFAFADC;
        }

        if (i == 4)
        {
            return 0xFADCFA;
        }

        if (i == 5)
        {
            return 0xDCDCFA;
        }

        if (i == 6)
        {
            return 0xFADCDC;
        }

        if (i == 7)
        {
            return 0xECD8C6;
        }

        if (i == 8)
        {
            return 0xE0E0E0;
        }
        else
        {
            return 0xFFFFFF;
        }
    }
    public int colorMultiplier(IBlockAccess iblockaccess, int i, int j, int k)
    {
        return getRenderColor(iblockaccess.getBlockMetadata(i, j, k));
    }

    public int getBlockTextureFromSideAndMetadata(int par1, int par2)
    {
        return this.blockIndexInTexture;
    }

    public void updateTick(World world, int i, int j, int k, Random random)
    {
        if (mod_GreyGoo.isGooActive(this.blockID, world) && mod_GreyGoo.instance.spreadLimiter.spreadLimiter(false))
        {

            if( !world.isRemote)
            {
            	 if (random.nextInt(10000) == 9)
                 {
                     world.setBlockWithNotify(i, j, k, mod_GreyGoo.BlockCancerID);
                 }
            	 else
            	 {
                assimilate(world, i, j, k);
            	 }
            }
        }
    }

    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
    {
        if (!world.isRemote && !entityplayer.isSneaking())
        {
            assimilate(world, i, j, k);
            assimilate(world, i + 1, j, k);
            assimilate(world, i - 1, j, k);
            assimilate(world, i, j, k + 1);
            assimilate(world, i, j, k - 1);
            assimilate(world, i, j + 1, k);
            assimilate(world, i, j - 1, k);
            return true;
        }

        return false;
    }

    public void onEntityWalking(World world, int i, int j, int k, Entity entity)
    {
        if (!world.isRemote)
        {
            assimilate(world, i, j, k);

            assimilate(world, i + 1, j, k);
            assimilate(world, i - 1, j, k);
            assimilate(world, i, j, k + 1);
            assimilate(world, i, j, k - 1);
            assimilate(world, i, j + 1, k);
            assimilate(world, i, j - 1, k);
        }
    }
}
