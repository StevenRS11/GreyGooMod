package StevenGreyGoo.mod_GreyGoo;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class BlockElevatorGoo extends Block
{
    protected BlockElevatorGoo(int i, int j)
    {
        super(i, j, Material.ground);
        setTickRandomly(false);
    }
    @Override
    public String getTextureFile()
    {
        return "/GooBlockTextures.png";
    }

    private void elevate(World world, int i, int j, int k)
    {
        if (!world.isAirBlock(i, j + 5, k))
        {
            world.setBlockWithNotify(i, j - 1, k, 0);
            world.setBlockWithNotify(i, j + 2, k, 0);
            world.setBlockWithNotify(i, j + 3, k, 0);
            world.setBlockWithNotify(i, j + 1, k, blockID);
        }
    }

    public void onEntityWalking(World world, int i, int j, int k, Entity entity)
    {
        elevate(world, i, j, k);
    }
}
