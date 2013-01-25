package StevenGreyGoo.mod_GreyGoo;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class EMPArray extends BlockContainer
{
    protected EMPArray(int i, int j)
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

    public int quantityDropped(Random par1Random)
    {
        return 1;
    }

    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
    {
        if (world.getBlockTileEntity(i, j, k) != null)
        {
        }

        return true;
    }
    public int idDropped(int i, Random rand, int j)
    {
        return this.blockID + 4256;
    }

    public TileEntity createNewTileEntity(World par1World)
    {
        TileEntity tile = new TileEntityEMPArray();
        return tile;
    }
}
