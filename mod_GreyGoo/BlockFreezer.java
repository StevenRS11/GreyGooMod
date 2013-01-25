package StevenGreyGoo.mod_GreyGoo;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BlockFreezer extends Block
{
    protected BlockFreezer(int i, int j)
    {
        super(i, j, Material.ground);
        setTickRandomly(false);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }
    @Override
    public String getTextureFile()

    {
        if (this.blockIndexInTexture == 11 || this.blockIndexInTexture == 0)
        {
            return "/GooBlockTextures.png";
        }
        else
        {
            return "/terrain.png";
        }
    }

    private Random random = new Random();
    public boolean isOpaqueCube()
    {
        return false;
    }
    public boolean renderAsNormalBlock()
    {
        return false;
    }

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
                        if (mod_GreyGoo.cleanerList.contains(world.getBlockId(i + l, j + i1, k + j1)) && world.getBlockId(i + l, j + i1, k + j1) != this.blockID)
                        {
                            world.setBlockWithNotify(i + l, j + i1, k + j1, blockID);
                            world.scheduleBlockUpdate(i + l, j + i1, k + j1, blockID, random.nextInt(25)+random.nextInt(4));
                        }
                    }

                    l = -1;
                }

                j1 = -1;
            }
        }
    }

    public void updateTick(World world, int i, int j, int k, Random random)
    {
        if (mod_GreyGoo.isGooActive(this.blockID, world) && mod_GreyGoo.FreezerisSpreading&&mod_GreyGoo.isGooActive(blockID, world))
        {
            mine(world, i, j, k);
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
            return mod_GreyGoo.BlockMinerGooID + 4256;
        }
    }

    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
    {
        if (!world.isRemote && !entityplayer.isSneaking())
        {
            mod_GreyGoo.FreezerisSpreading = true;
            mine(world, i, j, k);
            world.scheduleBlockUpdate(i, j, k, this.blockID, 5);
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
