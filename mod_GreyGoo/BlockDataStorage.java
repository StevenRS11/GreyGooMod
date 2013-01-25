package StevenGreyGoo.mod_GreyGoo;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockDataStorage extends Block
{
    protected BlockDataStorage(int i, int j)
    {
        super(i, j, Material.ground);
        setTickRandomly(false);
    }
    @Override
    public String getTextureFile()
    {
        return "/GooBlockTextures.png";
    }
}
