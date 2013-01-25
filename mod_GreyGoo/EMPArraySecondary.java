package StevenGreyGoo.mod_GreyGoo;
import net.minecraft.block.Block;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EMPArraySecondary extends Block
{
    protected EMPArraySecondary(int i, int j)
    {
        super(i, j, Material.ground);
        setTickRandomly(false);
    }
    @Override
    public String getTextureFile()
    {
        return "/GooBlockTextures.png";
    }

    public int quantityDropped(Random par1Random)
    {
        return 1;
    }

    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
    {
        int count = 0;

        if (world.getBlockId(i, j + 1, k) == mod_GreyGoo.EMPArraySecondaryID)
        {
            ++count;
        }

        if (world.getBlockId(i, j + 2, k) == mod_GreyGoo.EMPArraySecondaryID)
        {
            ++count;
        }

        if (world.getBlockId(i - 1, j + 1, k) == mod_GreyGoo.EMPArraySecondaryID)
        {
            ++count;
        }

        if (world.getBlockId(i + 1, j + 1, k) == mod_GreyGoo.EMPArraySecondaryID)
        {
            ++count;
        }

        if (world.getBlockId(i, j + 1, k - 1) == mod_GreyGoo.EMPArraySecondaryID)
        {
            ++count;
        }

        if (world.getBlockId(i, j + 1, k + 1) == mod_GreyGoo.EMPArraySecondaryID)
        {
            ++count;
        }

        if (world.getBlockId(i - 2, j + 1, k) == mod_GreyGoo.EMPArraySecondaryID)
        {
            ++count;
        }

        if (world.getBlockId(i + 2, j + 1, k) == mod_GreyGoo.EMPArraySecondaryID)
        {
            ++count;
        }

        if (world.getBlockId(i, j + 1, k - 2) == mod_GreyGoo.EMPArraySecondaryID)
        {
            ++count;
        }

        if (world.getBlockId(i, j + 1, k + 2) == mod_GreyGoo.EMPArraySecondaryID)
        {
            ++count;
        }

        if (world.getBlockId(i - 2, j + 2, k) == mod_GreyGoo.EMPArraySecondaryID)
        {
            ++count;
        }

        if (world.getBlockId(i + 2, j + 2, k) == mod_GreyGoo.EMPArraySecondaryID)
        {
            ++count;
        }

        if (world.getBlockId(i, j + 2, k - 2) == mod_GreyGoo.EMPArraySecondaryID)
        {
            ++count;
        }

        if (world.getBlockId(i, j + 2, k + 2) == mod_GreyGoo.EMPArraySecondaryID)
        {
            ++count;
        }

        if (world.getBlockId(i - 2, j + 3, k) == mod_GreyGoo.EMPArraySecondaryID)
        {
            ++count;
        }

        if (world.getBlockId(i + 2, j + 3, k) == mod_GreyGoo.EMPArraySecondaryID)
        {
            ++count;
        }

        if (world.getBlockId(i, j + 3, k - 2) == mod_GreyGoo.EMPArraySecondaryID)
        {
            ++count;
        }

        if (world.getBlockId(i, j + 3, k + 2) == mod_GreyGoo.EMPArraySecondaryID)
        {
            ++count;
        }

        if (count == 18)
        {
            world.setBlockWithNotify(i, j, k, mod_GreyGoo.EMPArrayID);
        }

        return false;
    }
    public int idDropped(int i, Random rand, int j)
    {
        return mod_GreyGoo.EMPArrayID;
    }
}
