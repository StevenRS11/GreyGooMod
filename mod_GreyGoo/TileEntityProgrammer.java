package StevenGreyGoo.mod_GreyGoo;
import net.minecraft.block.Block;
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

public class TileEntityProgrammer extends TileEntity implements IInventory, ISidedInventory
{
    public static int baseTime = 10000;
    public static int modifier = 50;
    private ItemStack[] ProgrammerItemStacks = new ItemStack[3];

    public int ProgrammerBurnTime = 0;

    public int currentItemBurnTime = 0;

    public int ProgrammerCookTime = 0;

    public int getSizeInventory()
    {
        return this.ProgrammerItemStacks.length;
    }

    public ItemStack getStackInSlot(int par1)
    {
        return this.ProgrammerItemStacks[par1];
    }

    public ItemStack decrStackSize(int par1, int par2)
    {
        if (this.ProgrammerItemStacks[par1] != null)
        {
            ItemStack var3;

            if (this.ProgrammerItemStacks[par1].stackSize <= par2)
            {
                var3 = this.ProgrammerItemStacks[par1];
                this.ProgrammerItemStacks[par1] = null;
                return var3;
            }
            else
            {
                var3 = this.ProgrammerItemStacks[par1].splitStack(par2);

                if (this.ProgrammerItemStacks[par1].stackSize == 0)
                {
                    this.ProgrammerItemStacks[par1] = null;
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
        if (this.ProgrammerItemStacks[par1] != null)
        {
            ItemStack var2 = this.ProgrammerItemStacks[par1];
            this.ProgrammerItemStacks[par1] = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        this.ProgrammerItemStacks[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    public String getInvName()
    {
        return "container.Programmer";
    }

    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
        this.ProgrammerItemStacks = new ItemStack[this.getSizeInventory()];

        for (int var3 = 0; var3 < var2.tagCount(); ++var3)
        {
            NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
            byte var5 = var4.getByte("Slot");

            if (var5 >= 0 && var5 < this.ProgrammerItemStacks.length)
            {
                this.ProgrammerItemStacks[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }

        this.ProgrammerBurnTime = par1NBTTagCompound.getShort("BurnTime");
        this.ProgrammerCookTime = par1NBTTagCompound.getShort("CookTime");
        this.currentItemBurnTime = getItemBurnTime(this.ProgrammerItemStacks[1]);
    }

    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setShort("BurnTime", (short)this.ProgrammerBurnTime);
        par1NBTTagCompound.setShort("CookTime", (short)this.ProgrammerCookTime);
        NBTTagList var2 = new NBTTagList();

        for (int var3 = 0; var3 < this.ProgrammerItemStacks.length; ++var3)
        {
            if (this.ProgrammerItemStacks[var3] != null)
            {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                this.ProgrammerItemStacks[var3].writeToNBT(var4);
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
        return this.ProgrammerCookTime * par1 /  mod_GreyGoo.getTimeToFinish(this.worldObj, this.xCoord, this.yCoord, this.zCoord, baseTime, modifier);
    }

    @SideOnly(Side.CLIENT)

    public int getBurnTimeRemainingScaled(int par1)
    {
        if (this.currentItemBurnTime == 0)
        {
            this.currentItemBurnTime = mod_GreyGoo.getTimeToFinish(this.worldObj, this.xCoord, this.yCoord, this.zCoord, baseTime, modifier);
        }

        return this.ProgrammerBurnTime * par1 / this.currentItemBurnTime;
    }

    public boolean isBurning()
    {
        return this.ProgrammerBurnTime > 0;
    }

    public void updateEntity()
    {
        boolean var1 = this.ProgrammerBurnTime > 0;
        boolean var2 = false;

        if (this.ProgrammerBurnTime > 0)
        {
            --this.ProgrammerBurnTime;
        }

        if (!this.worldObj.isRemote)
        {
            if (this.ProgrammerBurnTime == 0 && this.canSmelt())
            {
                this.currentItemBurnTime = this.ProgrammerBurnTime = mod_GreyGoo.getTimeToFinish(this.worldObj, this.xCoord, this.yCoord, this.zCoord, baseTime, modifier);
            }

            if (this.isBurning() && this.canSmelt())
            {
                ++this.ProgrammerCookTime;

                if (this.ProgrammerCookTime > mod_GreyGoo.getTimeToFinish(this.worldObj, this.xCoord, this.yCoord, this.zCoord, baseTime, modifier))
                {
                    this.ProgrammerCookTime = 0;
                    this.smeltItem();
                    var2 = true;
                }
            }
            else
            {
                this.ProgrammerCookTime = 0;
            }

            if (var1 != this.ProgrammerBurnTime > 0)
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
        if (this.ProgrammerItemStacks[1] == null)
        {
            return false;
        }

        if (this.ProgrammerItemStacks[0] == null)
        {
            return false;
        }

        int index = this.ProgrammerItemStacks[1].getItem().itemID;

        try
        {
            if (Block.blocksList[index] == null)
            {
                return false;
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            return false;
        }

        int index0 = this.ProgrammerItemStacks[0].getItem().itemID;

        if (index0 != mod_GreyGoo.BlockFreezerID)
        {
            return false;
        }

        if (this.ProgrammerItemStacks[0] == null || this.ProgrammerItemStacks[1] == null)
        {
            return false;
        }
        else
        {
            ItemStack var1 = this.ProgrammerItemStacks[0];

            if (var1 == null)
            {
                return false;
            }

            return true;
        }
    }

    public void smeltItem()
    {
        if (this.canSmelt())
        {
            ItemStack var1 = ProgrammerItemStacks[0];

            if (this.ProgrammerItemStacks[2] == null)
            {
                this.ProgrammerItemStacks[2] = var1.copy();
                int index = this.ProgrammerItemStacks[1].getItem().itemID;

                if (Block.blocksList[index] != null)
                {
                    Block block = Block.blocksList[index];

                    if (this.ProgrammerItemStacks[1].getItem().itemID == this.ProgrammerItemStacks[0].getItem().itemID)
                    {
                        mod_GreyGoo.BlockFreezer.blockIndexInTexture = 11;
                        mod_GreyGoo.BlockFreezer.setLightValue(0.5F);
                        mod_GreyGoo.instance.FreezerLight = 0.5;
                        mod_GreyGoo.instance.FreezerOpacity = block.lightOpacity[mod_GreyGoo.BlockInert.blockID];
                        mod_GreyGoo.BlockFreezer.setLightOpacity(mod_GreyGoo.BlockInert.lightOpacity[block.blockID]);
                        mod_GreyGoo.instance.proxy.writeNBTToFile(this.worldObj);
                    }
                    else
                    {
                        mod_GreyGoo.BlockFreezer.blockIndexInTexture = block.blockIndexInTexture;
                        mod_GreyGoo.instance.FreezerTexture = block.blockIndexInTexture;
                        mod_GreyGoo.BlockFreezer.setLightValue(block.lightValue[block.blockID]);
                        mod_GreyGoo.instance.FreezerLight = block.lightValue[block.blockID];
                        mod_GreyGoo.instance.FreezerOpacity = block.lightOpacity[block.blockID];
                        mod_GreyGoo.BlockFreezer.setLightOpacity(block.lightOpacity[block.blockID]);
                        mod_GreyGoo.instance.proxy.writeNBTToFile(this.worldObj);
                    }
                }
            }
            else if (this.ProgrammerItemStacks[2].isItemEqual(var1))
            {
                ProgrammerItemStacks[2].stackSize += var1.stackSize;
            }

            --this.ProgrammerItemStacks[0].stackSize;
            --this.ProgrammerItemStacks[1].stackSize;

            if (this.ProgrammerItemStacks[0].stackSize <= 0)
            {
                this.ProgrammerItemStacks[0] = null;
            }

            if (this.ProgrammerItemStacks[1].stackSize <= 0)
            {
                this.ProgrammerItemStacks[1] = null;
            }
        }
    }

    public static int getItemBurnTime(ItemStack par0ItemStack)
    {
        if (par0ItemStack == null)
        {
            return 0;
        }

        int inputitemID = par0ItemStack.getItem().itemID;

        if (inputitemID != mod_GreyGoo.BlockFreezerID)
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
