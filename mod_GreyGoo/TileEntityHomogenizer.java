package StevenGreyGoo.mod_GreyGoo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityHomogenizer extends TileEntity implements IInventory, ISidedInventory
{
    private ItemStack[] HomogenizerItemStacks = new ItemStack[18];

    public int HomogenizerBurnTime = 0;

    public int currentItemBurnTime = 0;

    public int HomogenizerCookTime = 0;

    public int getSizeInventory()
    {
        return this.HomogenizerItemStacks.length;
    }

    public ItemStack getStackInSlot(int par1)
    {
        return this.HomogenizerItemStacks[par1];
    }
    public static int baseTime = 1000;
    public static int modifier = 6;

    public ItemStack decrStackSize(int par1, int par2)
    {
        if (this.HomogenizerItemStacks[par1] != null)
        {
            ItemStack var3;

            if (this.HomogenizerItemStacks[par1].stackSize <= par2)
            {
                var3 = this.HomogenizerItemStacks[par1];
                this.HomogenizerItemStacks[par1] = null;
                return var3;
            }
            else
            {
                var3 = this.HomogenizerItemStacks[par1].splitStack(par2);

                if (this.HomogenizerItemStacks[par1].stackSize == 0)
                {
                    this.HomogenizerItemStacks[par1] = null;
                }

                return var3;
            }
        }
        else
        {
            return null;
        }
    }

    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (this.HomogenizerItemStacks[par1] != null)
        {
            ItemStack var2 = this.HomogenizerItemStacks[par1];
            this.HomogenizerItemStacks[par1] = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        this.HomogenizerItemStacks[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    public String getInvName()
    {
        return "container.Homogenizer";
    }

    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
        this.HomogenizerItemStacks = new ItemStack[this.getSizeInventory()];

        for (int var3 = 0; var3 < var2.tagCount(); ++var3)
        {
            NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
            byte var5 = var4.getByte("Slot");

            if (var5 >= 0 && var5 < this.HomogenizerItemStacks.length)
            {
                this.HomogenizerItemStacks[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }

        this.HomogenizerBurnTime = par1NBTTagCompound.getShort("BurnTime");
        this.HomogenizerCookTime = par1NBTTagCompound.getShort("CookTime");
        this.currentItemBurnTime = getItemBurnTime(this.HomogenizerItemStacks[1]);
    }

    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setShort("BurnTime", (short)this.HomogenizerBurnTime);
        par1NBTTagCompound.setShort("CookTime", (short)this.HomogenizerCookTime);
        NBTTagList var2 = new NBTTagList();

        for (int var3 = 0; var3 < this.HomogenizerItemStacks.length; ++var3)
        {
            if (this.HomogenizerItemStacks[var3] != null)
            {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                this.HomogenizerItemStacks[var3].writeToNBT(var4);
                var2.appendTag(var4);
            }
        }

        par1NBTTagCompound.setTag("Items", var2);
    }

    public int getInventoryStackLimit()
    {
        return 64;
    }

    @SideOnly(Side.CLIENT)

    public int getCookProgressScaled(int par1)
    {
        return this.HomogenizerCookTime * par1 / mod_GreyGoo.getTimeToFinish(this.worldObj, this.xCoord, this.yCoord, this.zCoord, baseTime, modifier);
    }

    @SideOnly(Side.CLIENT)

    public int getBurnTimeRemainingScaled(int par1)
    {
        if (this.currentItemBurnTime == 0)
        {
            this.currentItemBurnTime = mod_GreyGoo.getTimeToFinish(this.worldObj, this.xCoord, this.yCoord, this.zCoord, baseTime, modifier);
        }

        return this.HomogenizerBurnTime * par1 / this.currentItemBurnTime;
    }

    public boolean isBurning()
    {
        return this.HomogenizerBurnTime > 0;
    }

    public void updateEntity()
    {
        boolean var1 = this.HomogenizerBurnTime > 0;
        boolean var2 = false;

        if (this.HomogenizerBurnTime > 0)
        {
            --this.HomogenizerBurnTime;
        }

        if (!this.worldObj.isRemote)
        {
            if (this.HomogenizerBurnTime == 0 && this.canSmelt())
            {
                this.currentItemBurnTime = this.HomogenizerBurnTime = mod_GreyGoo.getTimeToFinish(this.worldObj, this.xCoord, this.yCoord, this.zCoord, baseTime, modifier);

                if (this.HomogenizerBurnTime > 0)
                {
                    var2 = true;
                }
            }

            if (this.isBurning() && this.canSmelt())
            {
                ++this.HomogenizerCookTime;

                if (this.HomogenizerCookTime > mod_GreyGoo.getTimeToFinish(this.worldObj, this.xCoord, this.yCoord, this.zCoord, baseTime, modifier))
                {
                    this.HomogenizerCookTime = 0;
                    this.smeltItem();
                    var2 = true;
                }
            }
            else
            {
                this.HomogenizerCookTime = 0;
            }

            if (var1 != this.HomogenizerBurnTime > 0)
            {
                var2 = true;
            }
        }

        if (var2)
        {
            this.onInventoryChanged();
        }
    }

    private boolean canSmelt()
    {
        int slotsIn = 0;
        int slotsOut = 9;
        boolean isinput = false;
        boolean isoutput = false;

        while (slotsIn < 9)
        {
            if (this.HomogenizerItemStacks[slotsIn] != null && this.HomogenizerItemStacks[slotsIn].getItem().itemID != mod_GreyGoo.BlockSubstrateID)
            {
                isinput = true;
                break;
            }
            else
            {
                ++slotsIn;
            }
        }

        while (slotsOut < 18)
        {
            if (this.HomogenizerItemStacks[slotsOut] == null || (this.HomogenizerItemStacks[slotsOut].getItem().itemID == mod_GreyGoo.BlockSubstrateID && this.HomogenizerItemStacks[slotsOut].stackSize < 65))
            {
                isoutput = true;
                break;
            }
            else
            {
                ++slotsOut;
            }
        }

        if ((isoutput && isinput))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void smeltItem()
    {
        if (this.canSmelt())
        {
            int outputslot = 9;
            ItemStack var1 = new ItemStack(mod_GreyGoo.BlockSubstrate);
            int coprocessorcount = mod_GreyGoo.countSurroundingBlock(this.worldObj, this.xCoord, this.yCoord, this.zCoord, mod_GreyGoo.Coprocessor.blockID);

            while (outputslot < 18)
            {
                if (this.HomogenizerItemStacks[outputslot] == null)
                {
                    this.HomogenizerItemStacks[outputslot] = var1.copy();
                    HomogenizerItemStacks[outputslot].stackSize += (var1.stackSize + coprocessorcount - 1);
                    break;
                }
                else if (this.HomogenizerItemStacks[outputslot].isItemEqual(var1) && HomogenizerItemStacks[outputslot].stackSize < 64)
                {
                    HomogenizerItemStacks[outputslot].stackSize += (var1.stackSize + coprocessorcount);
                    break;
                }
                else
                {
                    ++outputslot;
                }
            }

            boolean gotstuff = false;
            boolean didgetred = false;
            boolean didgetiron = false;
            boolean tempgotstuff = false;
            int inputslot = 0;

            while (inputslot < 9)
            {
                if (this.HomogenizerItemStacks[inputslot] != null)
                {
                    if (this.HomogenizerItemStacks[inputslot].stackSize > 0)
                    {
                        --this.HomogenizerItemStacks[inputslot].stackSize;

                        if (this.HomogenizerItemStacks[inputslot].stackSize < 1)
                        {
                            this.HomogenizerItemStacks[inputslot] = null;
                        }

                        break;
                    }
                }

                ++inputslot;
            }
        }
    }

    public static int getItemBurnTime(ItemStack par0ItemStack)
    {
        if (par0ItemStack == null)
        {
            return 0;
        }
        else
        {
            return 200;
        }
    }

    public static boolean isItemFuel(ItemStack par0ItemStack)
    {
        return getItemBurnTime(par0ItemStack) > 0;
    }

    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
    }

    public void openChest() {}

    public void closeChest() {}

    @Override
    public int getStartInventorySide(ForgeDirection side)
    {
        if (side == ForgeDirection.DOWN)
        {
            return 1;
        }

        if (side == ForgeDirection.UP)
        {
            return 0;
        }

        return 2;
    }

    @Override
    public int getSizeInventorySide(ForgeDirection side)
    {
        return 1;
    }
}
