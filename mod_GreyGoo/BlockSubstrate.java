package StevenGreyGoo.mod_GreyGoo;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.block.Block;
public class BlockSubstrate extends Block
{
    public BlockSubstrate(int i, int j)
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
}
