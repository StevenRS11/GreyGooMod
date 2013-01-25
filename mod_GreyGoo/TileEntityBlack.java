package StevenGreyGoo.mod_GreyGoo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;


public class TileEntityBlack extends TileEntity
{
    public boolean canUpdate()
    {
        return true;
    }

    public void updateEntity()
    {
        if (this.worldObj.getBlockId(this.xCoord, yCoord, zCoord) != mod_GreyGoo.instance.BlockBlack.blockID)
        {
            this.worldObj.removeBlockTileEntity(this.xCoord,  this.yCoord,  this.zCoord);
            this.invalidate();
            this.updateContainingBlockInfo();
            clearBlackTileEntity();
        }

        Entity target = super.worldObj.getClosestPlayer(this.xCoord, this.yCoord, this.zCoord, 4);

        if (target instanceof EntityLiving && !this.worldObj.isRemote && this.worldObj.getBlockId(this.xCoord, yCoord, zCoord) == mod_GreyGoo.BlockBlackID)
        {
            ((EntityLiving) target).addPotionEffect(new PotionEffect(Potion.blindness.getId(), 50, 1));
        }
    }

    public void clearBlackTileEntity()
    {
        if (this.worldObj != null)
        {
            this.worldObj.removeBlockTileEntity(this.xCoord, this.yCoord, this.zCoord);
            this.invalidate();
        }
    }

    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
    }

    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
    }
}
