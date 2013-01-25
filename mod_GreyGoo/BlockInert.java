package StevenGreyGoo.mod_GreyGoo;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.block.Block;
public class BlockInert extends Block
{
    protected BlockInert(int i, int j)
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
       
        if (!world.isRemote && entityplayer.isSneaking())
        {
			
            mod_GreyGoo.instance.proxy.printStringClient("TGD Bloom status--" + String.valueOf(mod_GreyGoo.instance.TGDbloom));
            mod_GreyGoo.instance.proxy.printStringClient("Global Goo Active status--" + String.valueOf(mod_GreyGoo.instance.GooActive));
            mod_GreyGoo.instance.proxy.printStringClient(world.getWorldInfo().getTerrainType().getWorldTypeName());
           // mod_GreyGoo.instance.proxy.printStringClient(world.provider.getDimensionName());
            mod_GreyGoo.instance.proxy.printStringClient(String.valueOf(entityplayer.dimension)+" -CurrentDim");
        //   mod_GreyGoo.instance.proxy.printStringClient(String.valueOf(mod_GreyGoo.instance.dimHelper.gooTeleportInt((entityplayer.dimension))+" -DestiniationDim"));

           // mod_GreyGoo.instance.proxy.printStringClient(String.valueOf(world.getWorldInfo().getDimension()));

         //   if(entityplayer instanceof EntityPlayerMP)
        //    {
           // 	EntityPlayerMP entity= (EntityPlayerMP)entityplayer;
			//	entity.mcServer.getConfigurationManager().transferPlayerToDimension(entity, mod_GreyGoo.instance.dimHelper.gooTeleportInt(entity.worldObj.provider.dimensionId), new GooTeleporter());
			//	mod_GreyGoo.instance.proxy.printStringClient(String.valueOf(entityplayer.dimension)+" -CurrentDim");
		    //       mod_GreyGoo.instance.proxy.printStringClient(String.valueOf(mod_GreyGoo.instance.dimHelper.gooTeleportInt((entityplayer.dimension))+" -DestiniationDim"));

           // }
           

            if (!mod_GreyGoo.inactiveGooList.isEmpty())
            {
                mod_GreyGoo.instance.proxy.printStringClient("EMP Array status-- Active");
            }

            if (mod_GreyGoo.inactiveGooList.isEmpty())
            {
                mod_GreyGoo.instance.proxy.printStringClient("EMP Array status-- Inactive");
            }

            return true;
        }

        return false;
    }
    public int idDropped(int i, Random rand, int j)
    {
        return this.blockID;
    }
}
