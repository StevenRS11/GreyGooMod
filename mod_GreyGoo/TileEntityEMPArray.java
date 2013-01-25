package StevenGreyGoo.mod_GreyGoo;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityEMPArray extends TileEntity
{
    Random rand = new Random();
    public boolean canUpdate()
    {
        return true;
    }
    public int isCore = 0;

    public void updateEntity()
    {
        if (rand.nextInt(100) == 1 || this.isCore == 0 && !this.worldObj.isRemote)
        {
            int count = 0;

            if (this.worldObj.getBlockId(xCoord, yCoord + 1, zCoord) == mod_GreyGoo.EMPArraySecondaryID)
            {
                ++count;
            }

            if (this.worldObj.getBlockId(xCoord, yCoord + 2, zCoord) == mod_GreyGoo.EMPArraySecondaryID)
            {
                ++count;
            }

            if (this.worldObj.getBlockId(xCoord - 1, yCoord + 1, zCoord) == mod_GreyGoo.EMPArraySecondaryID)
            {
                ++count;
            }

            if (this.worldObj.getBlockId(xCoord + 1, yCoord + 1, zCoord) == mod_GreyGoo.EMPArraySecondaryID)
            {
                ++count;
            }

            if (this.worldObj.getBlockId(xCoord, yCoord + 1, zCoord - 1) == mod_GreyGoo.EMPArraySecondaryID)
            {
                ++count;
            }

            if (this.worldObj.getBlockId(xCoord, yCoord + 1, zCoord + 1) == mod_GreyGoo.EMPArraySecondaryID)
            {
                ++count;
            }

            if (this.worldObj.getBlockId(xCoord - 2, yCoord + 1, zCoord) == mod_GreyGoo.EMPArraySecondaryID)
            {
                ++count;
            }

            if (this.worldObj.getBlockId(xCoord + 2, yCoord + 1, zCoord) == mod_GreyGoo.EMPArraySecondaryID)
            {
                ++count;
            }

            if (this.worldObj.getBlockId(xCoord, yCoord + 1, zCoord - 2) == mod_GreyGoo.EMPArraySecondaryID)
            {
                ++count;
            }

            if (this.worldObj.getBlockId(xCoord, yCoord + 1, zCoord + 2) == mod_GreyGoo.EMPArraySecondaryID)
            {
                ++count;
            }

            if (this.worldObj.getBlockId(xCoord - 2, yCoord + 2, zCoord) == mod_GreyGoo.EMPArraySecondaryID)
            {
                ++count;
            }

            if (this.worldObj.getBlockId(xCoord + 2, yCoord + 2, zCoord) == mod_GreyGoo.EMPArraySecondaryID)
            {
                ++count;
            }

            if (this.worldObj.getBlockId(xCoord, yCoord + 2, zCoord - 2) == mod_GreyGoo.EMPArraySecondaryID)
            {
                ++count;
            }

            if (this.worldObj.getBlockId(xCoord, yCoord + 2, zCoord + 2) == mod_GreyGoo.EMPArraySecondaryID)
            {
                ++count;
            }

            if (this.worldObj.getBlockId(xCoord - 2, yCoord + 3, zCoord) == mod_GreyGoo.EMPArraySecondaryID)
            {
                ++count;
            }

            if (this.worldObj.getBlockId(xCoord + 2, yCoord + 3, zCoord) == mod_GreyGoo.EMPArraySecondaryID)
            {
                ++count;
            }

            if (this.worldObj.getBlockId(xCoord, yCoord + 3, zCoord - 2) == mod_GreyGoo.EMPArraySecondaryID)
            {
                ++count;
            }

            if (this.worldObj.getBlockId(xCoord, yCoord + 3, zCoord + 2) == mod_GreyGoo.EMPArraySecondaryID)
            {
                ++count;
            }

            if ((mod_GreyGoo.instance.allGooBlocks.contains(this.worldObj.getBlockId(xCoord, yCoord + 3, zCoord))) && count < 18)
            {
                ++count;
            }

            if (count != 18)
            {
            	try
            	{
            		this.worldObj.setBlockWithNotify(xCoord, yCoord, zCoord, mod_GreyGoo.EMPArraySecondaryID);
            		clearEMPArrayTileEntity();
            	}
            	catch(NullPointerException e)
            	{
            		
            		mod_GreyGoo.instance.proxy.printStringClient("Er, something went wrong. Not sure what.");
            		e.printStackTrace();
            		
            	}
            }

            if (count == 18)
            {
                this.isCore = 1;
            }
        }

        if (mod_GreyGoo.instance.allGooBlocks.contains(this.worldObj.getBlockId(this.xCoord, yCoord + 3, zCoord)) && !this.worldObj.isRemote)
        {
        	if(!mod_GreyGoo.inactiveGooList.containsKey(this.worldObj.getBlockId(this.xCoord, yCoord + 3, zCoord)))
        	{
        		
        		 mod_GreyGoo.inactiveGooList.put(this.worldObj.getBlockId(this.xCoord, yCoord + 3, zCoord),this.worldObj.provider.dimensionId);
                 this.worldObj.setBlockWithNotify(xCoord, yCoord + 2, zCoord, 0);
                 mod_GreyGoo.instance.proxy.printStringClient("Activated");
        	}
           
        }

        if (this.worldObj.getBlockId(this.xCoord, yCoord + 3, zCoord) == mod_GreyGoo.BlockTGD.blockID)
        {
            if (mod_GreyGoo.instance.TGDbloom)
            {
                mod_GreyGoo.instance.TGDbloom = false;
                mod_GreyGoo.instance.proxy.writeNBTToFile(this.worldObj);
            }
        }

        if (this.worldObj.getBlockId(this.xCoord, yCoord + 3, zCoord) == mod_GreyGoo.BlockSubstrateID)
        {
            mod_GreyGoo.instance.inactiveGooList.clear();
            this.worldObj.setBlockWithNotify(xCoord, yCoord + 3, zCoord, 0);
            mod_GreyGoo.instance.proxy.printStringClient("EMP Reset");
        }

        if (this.worldObj.getBlockId(this.xCoord, yCoord, zCoord) != mod_GreyGoo.instance.EMPArray.blockID)
        {
            this.worldObj.removeBlockTileEntity(this.xCoord,  this.yCoord,  this.zCoord);
            clearEMPArrayTileEntity();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        int i = nbt.getInteger(("Size"));

        try
        {
            this.isCore = nbt.getInteger("isCore");
        }
        catch (Exception e)
        {
            mod_GreyGoo.inactiveGooList.clear();
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        int i = 0;
        super.writeToNBT(nbt);
        nbt.setInteger("isCore", this.isCore);
    }

    public void clearEMPArrayTileEntity()
    {
        if (this.worldObj != null)
        {
            if (!mod_GreyGoo.inactiveGooList.isEmpty())
            {
                mod_GreyGoo.instance.proxy.printStringClient("EMP Reset");
            }

            mod_GreyGoo.inactiveGooList.clear();
            this.worldObj.markTileEntityForDespawn(this);
            this.worldObj.removeBlockTileEntity(this.xCoord, this.yCoord, this.zCoord);
            this.invalidate();
        }
    }
}
