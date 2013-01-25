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

public class TileEntityAssembler extends TileEntity implements IInventory, ISidedInventory
{
    private ItemStack[] AssemblerItemStacks = new ItemStack[3];

    public int AssemblerBurnTime = 0;
    public static int baseTime = 2000;
    public static int modifier = 13;

    public int currentItemBurnTime = 0;

    public int AssemblerCookTime = 0;

    public int getSizeInventory()
    {
        return this.AssemblerItemStacks.length;
    }

    public ItemStack getStackInSlot(int par1)
    {
        return this.AssemblerItemStacks[par1];
    }

    public ItemStack decrStackSize(int par1, int par2)
    {
        if (this.AssemblerItemStacks[par1] != null)
        {
            ItemStack var3;

            if (this.AssemblerItemStacks[par1].stackSize <= par2)
            {
                var3 = this.AssemblerItemStacks[par1];
                this.AssemblerItemStacks[par1] = null;
                return var3;
            }
            else
            {
                var3 = this.AssemblerItemStacks[par1].splitStack(par2);

                if (this.AssemblerItemStacks[par1].stackSize == 0)
                {
                    this.AssemblerItemStacks[par1] = null;
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
        if (this.AssemblerItemStacks[par1] != null)
        {
            ItemStack var2 = this.AssemblerItemStacks[par1];
            this.AssemblerItemStacks[par1] = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        this.AssemblerItemStacks[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    public String getInvName()
    {
        return "container.Assembler";
    }

    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
        this.AssemblerItemStacks = new ItemStack[this.getSizeInventory()];

        for (int var3 = 0; var3 < var2.tagCount(); ++var3)
        {
            NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
            byte var5 = var4.getByte("Slot");

            if (var5 >= 0 && var5 < this.AssemblerItemStacks.length)
            {
                this.AssemblerItemStacks[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }

        this.AssemblerBurnTime = par1NBTTagCompound.getShort("BurnTime");
        this.AssemblerCookTime = par1NBTTagCompound.getShort("CookTime");
        this.currentItemBurnTime = getItemBurnTime(this.AssemblerItemStacks[1]);
    }

    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setShort("BurnTime", (short)this.AssemblerBurnTime);
        par1NBTTagCompound.setShort("CookTime", (short)this.AssemblerCookTime);
        NBTTagList var2 = new NBTTagList();

        for (int var3 = 0; var3 < this.AssemblerItemStacks.length; ++var3)
        {
            if (this.AssemblerItemStacks[var3] != null)
            {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                this.AssemblerItemStacks[var3].writeToNBT(var4);
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
        return this.AssemblerCookTime * par1 /  mod_GreyGoo.getTimeToFinish(this.worldObj, this.xCoord, this.yCoord, this.zCoord, baseTime, modifier);
    }

    @SideOnly(Side.CLIENT)

    public int getBurnTimeRemainingScaled(int par1)
    {
        if (this.currentItemBurnTime == 0)
        {
            this.currentItemBurnTime = mod_GreyGoo.getTimeToFinish(this.worldObj, this.xCoord, this.yCoord, this.zCoord, baseTime, modifier);
        }

        return this.AssemblerBurnTime * par1 / this.currentItemBurnTime;
    }

    public boolean isBurning()
    {
        return this.AssemblerBurnTime > 0;
    }

    public void updateEntity()
    {
        boolean var1 = this.AssemblerBurnTime > 0;
        boolean var2 = false;

        if (this.AssemblerBurnTime > 0)
        {
            --this.AssemblerBurnTime;
        }

        if (!this.worldObj.isRemote)
        {
            if (this.AssemblerBurnTime == 0 && this.canSmelt())
            {
                this.currentItemBurnTime = this.AssemblerBurnTime = mod_GreyGoo.getTimeToFinish(this.worldObj, this.xCoord, this.yCoord, this.zCoord, baseTime, modifier);
            }

            if (this.isBurning() && this.canSmelt())
            {
                ++this.AssemblerCookTime;

                if (this.AssemblerCookTime > mod_GreyGoo.getTimeToFinish(this.worldObj, this.xCoord, this.yCoord, this.zCoord, baseTime, modifier))
                {
                    this.AssemblerCookTime = 0;
                    this.smeltItem();
                    var2 = true;
                }
            }
            else
            {
                this.AssemblerCookTime = 0;
            }

            if (var1 != this.AssemblerBurnTime > 0)
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
        if (this.AssemblerItemStacks[0] == null || this.AssemblerItemStacks[1] == null)
        {
            return false;
        }
        else
        {
            ItemStack var1 = AssemblerRecipes.smelting().getSmeltingResult(this.AssemblerItemStacks[0], this.AssemblerItemStacks[1]);

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
            ItemStack var1 = AssemblerRecipes.smelting().getSmeltingResult(this.AssemblerItemStacks[0], this.AssemblerItemStacks[1]);
            int coprocessorcount = (mod_GreyGoo.countSurroundingBlock(this.worldObj, this.xCoord, this.yCoord, this.zCoord, mod_GreyGoo.Coprocessor.blockID)) / 5;

            if (this.AssemblerItemStacks[2] == null)
            {
                this.AssemblerItemStacks[2] = var1.copy();
                AssemblerItemStacks[2].stackSize += (var1.stackSize + coprocessorcount - 1);
            }
            else if (this.AssemblerItemStacks[2].isItemEqual(var1))
            {
                AssemblerItemStacks[2].stackSize += var1.stackSize;
                AssemblerItemStacks[2].stackSize += (var1.stackSize + coprocessorcount);
            }

            --this.AssemblerItemStacks[0].stackSize;
            --this.AssemblerItemStacks[1].stackSize;

            if (this.AssemblerItemStacks[0].stackSize <= 0)
            {
                this.AssemblerItemStacks[0] = null;
            }

            if (this.AssemblerItemStacks[1].stackSize <= 0)
            {
                this.AssemblerItemStacks[1] = null;
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

        if (!mod_GreyGoo.Modifiers.contains(inputitemID))
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
