package StevenGreyGoo.mod_GreyGoo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityCompiler extends TileEntity implements IInventory, ISidedInventory
{
    private ItemStack[] CompilerItemStacks = new ItemStack[8];

    public int CompilerBurnTime = 0;

    public static int baseTime = 4000;
    public static int modifier = 24;

    public int currentItemBurnTime = 0;

    public int CompilerCookTime = 0;

    public int getSizeInventory()
    {
        return this.CompilerItemStacks.length;
    }

    public ItemStack getStackInSlot(int par1)
    {
        return this.CompilerItemStacks[par1];
    }

    public ItemStack decrStackSize(int par1, int par2)
    {
        if (this.CompilerItemStacks[par1] != null)
        {
            ItemStack var3;

            if (this.CompilerItemStacks[par1].stackSize <= par2)
            {
                var3 = this.CompilerItemStacks[par1];
                this.CompilerItemStacks[par1] = null;
                return var3;
            }
            else
            {
                var3 = this.CompilerItemStacks[par1].splitStack(par2);

                if (this.CompilerItemStacks[par1].stackSize == 0)
                {
                    this.CompilerItemStacks[par1] = null;
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
        if (this.CompilerItemStacks[par1] != null)
        {
            ItemStack var2 = this.CompilerItemStacks[par1];
            this.CompilerItemStacks[par1] = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        this.CompilerItemStacks[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    public String getInvName()
    {
        return "container.Compiler";
    }

    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
        this.CompilerItemStacks = new ItemStack[this.getSizeInventory()];

        for (int var3 = 0; var3 < var2.tagCount(); ++var3)
        {
            NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
            byte var5 = var4.getByte("Slot");

            if (var5 >= 0 && var5 < this.CompilerItemStacks.length)
            {
                this.CompilerItemStacks[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }

        this.CompilerBurnTime = par1NBTTagCompound.getShort("BurnTime");
        this.CompilerCookTime = par1NBTTagCompound.getShort("CookTime");
        this.currentItemBurnTime = getItemBurnTime(this.CompilerItemStacks[1]);
    }

    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setShort("BurnTime", (short)this.CompilerBurnTime);
        par1NBTTagCompound.setShort("CookTime", (short)this.CompilerCookTime);
        NBTTagList var2 = new NBTTagList();

        for (int var3 = 0; var3 < this.CompilerItemStacks.length; ++var3)
        {
            if (this.CompilerItemStacks[var3] != null)
            {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                this.CompilerItemStacks[var3].writeToNBT(var4);
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
        return this.CompilerCookTime * par1 / mod_GreyGoo.getTimeToFinish(this.worldObj, this.xCoord, this.yCoord, this.zCoord, baseTime, modifier);
    }

    @SideOnly(Side.CLIENT)

    public int getBurnTimeRemainingScaled(int par1)
    {
        if (this.currentItemBurnTime == 0)
        {
            this.currentItemBurnTime = mod_GreyGoo.getTimeToFinish(this.worldObj, this.xCoord, this.yCoord, this.zCoord, baseTime, modifier);
        }

        return this.CompilerBurnTime * par1 / this.currentItemBurnTime;
    }

    public boolean isBurning()
    {
        return this.CompilerBurnTime > 0;
    }

    public void updateEntity()
    {
        boolean var1 = this.CompilerBurnTime > 0;
        boolean var2 = false;

        if (this.CompilerBurnTime > 0)
        {
            --this.CompilerBurnTime;
        }

        if (!this.worldObj.isRemote)
        {
            if (this.CompilerBurnTime == 0 && this.canSmelt() && this.hasStuff())
            {
                this.currentItemBurnTime = this.CompilerBurnTime = mod_GreyGoo.getTimeToFinish(this.worldObj, this.xCoord, this.yCoord, this.zCoord, baseTime, modifier);

                if (this.CompilerBurnTime > 0)
                {
                    var2 = true;

                    if (this.CompilerItemStacks[1] != null)
                    {
                        --this.CompilerItemStacks[1].stackSize;

                        if (this.CompilerItemStacks[1].stackSize == 0)
                        {
                            this.CompilerItemStacks[1] = this.CompilerItemStacks[1].getItem().getContainerItemStack(CompilerItemStacks[1]);
                        }
                    }
                }
            }

            if (this.isBurning() && this.canSmelt() && this.hasStuff())
            {
                ++this.CompilerCookTime;

                if (this.CompilerCookTime > mod_GreyGoo.getTimeToFinish(this.worldObj, this.xCoord, this.yCoord, this.zCoord, baseTime, modifier))
                {
                    this.CompilerCookTime = 0;
                    this.smeltItem();
                    var2 = true;
                }
            }
            else
            {
                this.CompilerCookTime = 0;
            }

            if (var1 != this.CompilerBurnTime > 0)
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
        if (this.CompilerItemStacks[0] == null)
        {
            return false;
        }

        if (this.CompilerItemStacks[3] == null)
        {
            return false;
        }
        else
        {
            ItemStack var1 = CompilerRecipes.smelting().getSmeltingResult(this.CompilerItemStacks[0]);

            if (var1 == null)
            {
                return false;
            }

            if (this.CompilerItemStacks[1] == null)
            {
                return true;
            }

            if (!this.CompilerItemStacks[1].isItemEqual(var1))
            {
                return false;
            }

            int result = CompilerItemStacks[1].stackSize + var1.stackSize;
            return (result <= getInventoryStackLimit() && result <= var1.getMaxStackSize());
        }
    }
    private boolean hasStuff()
    {
        int counter = 0;
        int counter2 = 0;

        if (this.CompilerItemStacks[0] == null)
        {
            return false;
        }

        if (this.CompilerItemStacks[3] == null)
        {
            return false;
        }

        if (this.CompilerItemStacks[3].itemID != mod_GreyGoo.BlockSubstrate.blockID)
        {
            return false;
        }

        int whichslot = 4;

        while (whichslot < 8)
        {
            if (this.CompilerItemStacks[whichslot] != null)
            {
                if ((this.CompilerItemStacks[whichslot].getItem().itemID == Item.redstone.itemID) && (this.CompilerItemStacks[whichslot].stackSize > 1))
                {
                    counter = 1;
                }

                if ((this.CompilerItemStacks[whichslot].getItem().itemID == Item.ingotIron.itemID) && (this.CompilerItemStacks[whichslot].stackSize > 1))
                {
                    counter2 = 1;
                }
            }

            ++whichslot;
        }

        if (counter + counter2 < 2)
        {
            return false;
        }
        else
        {
            ItemStack var1 = CompilerRecipes.smelting().getSmeltingResult(this.CompilerItemStacks[0]);

            if (var1 == null)
            {
                return false;
            }

            if (this.CompilerItemStacks[1] == null)
            {
                return true;
            }

            if (!this.CompilerItemStacks[1].isItemEqual(var1))
            {
                return false;
            }

            int result = CompilerItemStacks[1].stackSize + var1.stackSize;
            return (result <= getInventoryStackLimit() && result <= var1.getMaxStackSize());
        }
    }

    public void smeltItem()
    {
        if (this.canSmelt())
        {
            boolean gotstuff = false;
            boolean didgetred = false;
            boolean didgetiron = false;
            boolean tempgotstuff = false;
            int whichslot = 4;
            ItemStack var1 = CompilerRecipes.smelting().getSmeltingResult(this.CompilerItemStacks[0]);
            int coprocessorcount = (mod_GreyGoo.countSurroundingBlock(this.worldObj, this.xCoord, this.yCoord, this.zCoord, mod_GreyGoo.Coprocessor.blockID));
            int addToResult = 0;

            if (coprocessorcount > 10)
            {
                addToResult = 1;
            }

            if (coprocessorcount > 20)
            {
                addToResult = 2;
            }

            if (coprocessorcount == 25)
            {
                addToResult = 3;
            }

            if (this.CompilerItemStacks[2] == null)
            {
                this.CompilerItemStacks[2] = var1.copy();
                CompilerItemStacks[2].stackSize += (var1.stackSize + addToResult - 1);
            }
            else if (this.CompilerItemStacks[2].isItemEqual(var1))
            {
                CompilerItemStacks[2].stackSize += (var1.stackSize + addToResult);
            }

            --this.CompilerItemStacks[0].stackSize;

            if (this.CompilerItemStacks[0].stackSize == 0)
            {
                this.CompilerItemStacks[0] = null;
            }

            if (this.CompilerItemStacks[3].stackSize > 0)
            {
                --this.CompilerItemStacks[3].stackSize;

                if (this.CompilerItemStacks[3].stackSize == 0)
                {
                    this.CompilerItemStacks[3] = null;
                }
            }

            while (whichslot < 8)
            {
                tempgotstuff = false;

                if (this.CompilerItemStacks[whichslot] != null)
                {
                    if (this.CompilerItemStacks[whichslot].stackSize > 1)
                    {
                        if (this.CompilerItemStacks[whichslot].getItem().itemID == Item.redstone.itemID && !didgetred)
                        {
                            tempgotstuff = true;
                            didgetred = true;
                        }

                        if (this.CompilerItemStacks[whichslot].getItem().itemID == Item.ingotIron.itemID && !didgetiron)
                        {
                            tempgotstuff = true;
                            didgetiron = true;
                        }

                        if (tempgotstuff)
                        {
                            --this.CompilerItemStacks[whichslot].stackSize;
                            --this.CompilerItemStacks[whichslot].stackSize;
                            gotstuff = tempgotstuff;

                            if (this.CompilerItemStacks[whichslot].stackSize < 1)
                            {
                                this.CompilerItemStacks[whichslot] = null;
                            }
                        }
                    }
                }

                ++whichslot;
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

        if (!mod_GreyGoo.Matrices.contains(inputitemID))
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
