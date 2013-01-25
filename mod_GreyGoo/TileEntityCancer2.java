package StevenGreyGoo.mod_GreyGoo;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;

public class TileEntityCancer2 extends TileEntity
{
    Random rand = new Random();
    public boolean canUpdate()
    {
        return true;
    }

    Random random = new Random();

    public void updateEntity()
    {
        if (worldObj.getClosestPlayer(xCoord, yCoord, zCoord, 20) != null && !worldObj.isRemote && mod_GreyGoo.isGooActive(mod_GreyGoo.BlockCancer2ID,this.worldObj))
        {
            worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, mod_GreyGoo.BlockCancer2ID, random.nextInt(18) + MathHelper.floor_double(Math.abs((this.worldObj.getClosestPlayer(xCoord, yCoord, zCoord, 20).getDistance(xCoord, yCoord, zCoord)))));
        }
        

        if (worldObj.getClosestPlayer(xCoord, yCoord, zCoord, 6) != null && rand.nextInt(12) == 1 && !worldObj.isRemote && mod_GreyGoo.isGooActive(mod_GreyGoo.BlockCancer2ID,this.worldObj))
        {
            EntityPlayer target = worldObj.getClosestPlayer(xCoord, yCoord, zCoord, 7);
            int x = (int) target.lastTickPosX;
            int y = (int) target.lastTickPosY;
            int z = (int) target.lastTickPosZ;

            if (xCoord - x > 0)
            {
                x = xCoord - 1;
            }
            else if (xCoord - x < 0)
            {
                x = xCoord + 1;
            }
            else if (xCoord - x == 0)
            {
                x = xCoord + (rand.nextInt(2) - rand.nextInt(2));
            }

            if (yCoord - y > 0)
            {
                y = yCoord - 1;
            }
            else if (yCoord - y < 0)
            {
                y = yCoord + 1;
            }
            else if (yCoord - y == 0)
            {
                y = yCoord + (rand.nextInt(2) - rand.nextInt(2));
            }

            if (zCoord - z > 0)
            {
                z = zCoord - 1;
            }
            else if (zCoord - z < 0)
            {
                z = zCoord + 1;
            }
            else if (zCoord - z == 0)
            {
                z = zCoord + (rand.nextInt(2) - rand.nextInt(2));
            }

            if (!mod_GreyGoo.gooNeverEatThese.contains(worldObj.getBlockId(x, y, z)) && !worldObj.isRemote)
            {
            	if(worldObj.getClosestPlayer(xCoord, yCoord, zCoord, 3)==null)
            	{
                worldObj.setBlockWithNotify(x, y, z, mod_GreyGoo.BlockCancer2ID);
            	}
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        int i = nbt.getInteger(("Size"));

        try
        {
        }
        catch (Exception e)
        {
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        int i = 0;
        super.writeToNBT(nbt);
    }
}
