package StevenGreyGoo.mod_GreyGoo;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.block.Block;
public class BlockRapidWaterEater extends Block
{
    protected BlockRapidWaterEater(int i, int j)
    {
        super(i, j, Material.ground);
        setTickRandomly(false);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }
    @Override
    public String getTextureFile()
    {
        return "/GooBlockTextures.png";
    }

    private Random random = new Random();

    protected void mine(World world, int i, int j, int k)
    {
        int l = -1;
        int i1 = -1;
        int j1 = -1;
        byte byte0 = -1;

        if (byte0 != world.getBlockMetadata(i, j, k))
        {
            for (; i1 < 2; i1++)
            {
                for (; j1 < 2; j1++)
                {
                    for (; l < 2; l++)
                    {
                        if (mod_GreyGoo.gooNeverEatThese.contains(world.getBlockId(i + l, j + i1, k + j1)))
                        {
                            break;
                        }

                        if ((world.getBlockId(i + l, j + i1, k + j1) == waterStill.blockID) || (world.getBlockId(i + l, j + i1, k + j1) == waterMoving.blockID) || (world.getBlockId(i + l, j + i1, k + j1) == lavaStill.blockID) || (world.getBlockId(i + l, j + i1, k + j1) == lavaMoving.blockID))
                        {
                            world.setBlockWithNotify(i + l, j + i1, k + j1, blockID);
                        }

                        world.scheduleBlockUpdate(i + l, j + i1, k + j1, blockID, random.nextInt(25)+random.nextInt(4));
                    }

                    l = -1;
                }

                j1 = -1;
            }
        }
    }

    public void updateTick(World world, int i, int j, int k, Random random)
    {
        if (mod_GreyGoo.isGooActive(this.blockID, world) && mod_GreyGoo.RapidWaterEaterisSpreading&&mod_GreyGoo.isGooActive(this.blockID, world))
        {
            mine(world, i, j, k);
            world.setBlock(i, j, k, 0);
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
            return mod_GreyGoo.BlockCleanerID + 4256;
        }
        else
        {
            return mod_GreyGoo.BlockWaterEaterID + 4256;
        }
    }

    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
    {
        if (!world.isRemote && !entityplayer.isSneaking())
        {
            mod_GreyGoo.RapidWaterEaterisSpreading = true;
            world.setBlockMetadataWithNotify(i, j, k, 0);
            mine(world, i, j, k);
            world.scheduleBlockUpdate(i+1, j, k, this.blockID, random.nextInt(5));
            world.scheduleBlockUpdate(i, j, k+1, this.blockID, random.nextInt(5));
            world.scheduleBlockUpdate(i, j, k-1, this.blockID, random.nextInt(5));
            world.scheduleBlockUpdate(i-1, j, k, this.blockID, random.nextInt(5));
            world.scheduleBlockUpdate(i, j-1, k, this.blockID, random.nextInt(5));

            return  true;
        }

        return false;
    }
}
