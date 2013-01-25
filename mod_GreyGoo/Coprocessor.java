package StevenGreyGoo.mod_GreyGoo;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class Coprocessor extends Block
{
    protected Coprocessor(int par1, int textureIndex, boolean par2)
    {
        super(par1, textureIndex, Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public String getTextureFile()
    {
        return "/GooBlockTextures.png";
    }
}
