package StevenGreyGoo.mod_GreyGoo;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemMatrixPurple extends Item
{
    public ItemMatrixPurple(int par1)
    {
        super(par1);
        this.setHasSubtypes(false);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabs.tabMaterials);
    }
    @Override
    public String getTextureFile()
    {
        return "/GooItemTextures.png";
    }
}
