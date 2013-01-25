package StevenGreyGoo.mod_GreyGoo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.block.Block;
public class CommonProxy implements IGuiHandler
{
    public static String BLOCK_PNG = "/GooBlockTextures.png";
    public static String ITEM_PNG = "/GooItemTextures.png";

    public  void registerRenderers()

    {
    }
    public void registerEntity(Class <? extends Entity > entity, String entityname, int id, Object mod, int trackingrange, int updateFreq, boolean updatevelo)
    {
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }

    public void loadTextures()
    {
    }

    public void writeNBTToFile(World world)
    {
        boolean flag = true;
        boolean secondTry = false;

        try
        {
            File dataStore = world.getSaveHandler().getMapFileFromName("idcounts");
            String dirFolder = dataStore.getCanonicalPath();
            dirFolder = dirFolder.replace("idcounts.dat", "");
           // System.out.println(dirFolder);

            if (!flag)
            {
                dirFolder.replace("saves/", FMLCommonHandler.instance().getMinecraftServerInstance().getFolderName());
                secondTry = true;
            }

            File file = new File(dirFolder, "GGMData.dat");

            if (!file.exists())
            {
                file.createNewFile();
            }

            FileOutputStream fileoutputstream = new FileOutputStream(file);
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setBoolean("TGDBloom", mod_GreyGoo.instance.TGDbloom);
            nbttagcompound.setBoolean("GooActive", mod_GreyGoo.instance.GooActive);
            nbttagcompound.setInteger("FreezerTexture", mod_GreyGoo.instance.FreezerTexture);

            
            CompressedStreamTools.writeCompressed(nbttagcompound, fileoutputstream);
            fileoutputstream.close();
        }
        catch (Exception exception)
        {
         //   exception.printStackTrace();

            if (!(exception instanceof NullPointerException))
            {
                mod_GreyGoo.instance.proxy.printStringClient("Could not save data");
            }

            flag = false;
        }
    }

    public void readNBTFromFile(World world)
    {
        boolean flag = true;
        boolean secondTry = false;

        try
        {
            File dataStore = world.getSaveHandler().getMapFileFromName("idcounts");
            String dirFolder = dataStore.getCanonicalPath();
            dirFolder = dirFolder.replace("idcounts.dat", "");

            if (!flag)
            {
                dirFolder.replace("saves/", FMLCommonHandler.instance().getMinecraftServerInstance().getFolderName());
                secondTry = true;
            }

            File file = new File(dirFolder, "GGMData.dat");

            if (!file.exists())
            {
                file.createNewFile();
                FileOutputStream fileoutputstream = new FileOutputStream(file);
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setBoolean("TGDBloom", false);
                nbttagcompound.setBoolean("GooActive", true);
                nbttagcompound.setInteger("FreezerTexture", 11);
                CompressedStreamTools.writeCompressed(nbttagcompound, fileoutputstream);
                fileoutputstream.close();
            }

            FileInputStream fileinputstream = new FileInputStream(file);
            NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(fileinputstream);

            if (nbttagcompound.hasKey("TGDBloom"))
            {
                mod_GreyGoo.instance.TGDbloom = nbttagcompound.getBoolean("TGDBloom");
            }

            if (nbttagcompound.hasKey("GooActive"))
            {
                mod_GreyGoo.instance.GooActive = nbttagcompound.getBoolean("GooActive");
            }

            if (nbttagcompound.hasKey("FreezerTexture"))
            {
                mod_GreyGoo.instance.BlockFreezer.blockIndexInTexture = nbttagcompound.getInteger("FreezerTexture");
            }

            fileinputstream.close();
        }
        catch (Exception exception)
        {
           // exception.printStackTrace();

            if (!(exception instanceof NullPointerException))
            {
                mod_GreyGoo.instance.proxy.printStringClient("Could not save data");
            }

            flag = false;
        }
    }

    public  void printStringClient(String string)
    {
    	
    }
}