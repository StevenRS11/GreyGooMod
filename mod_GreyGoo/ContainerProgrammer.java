package StevenGreyGoo.mod_GreyGoo;
import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
public class ContainerProgrammer extends Container
{
    private TileEntityProgrammer Programmer;
    private int lastCookTime = 0;
    private int lastBurnTime = 0;
    private int lastItemBurnTime = 0;

    public ContainerProgrammer(InventoryPlayer par1InventoryPlayer, TileEntityProgrammer par2TileEntityProgrammer)
    {
        this.Programmer = par2TileEntityProgrammer;
        this.addSlotToContainer(new Slot(par2TileEntityProgrammer, 0, 50, 35 + 18));
        this.addSlotToContainer(new Slot(par2TileEntityProgrammer, 1, 50, 35 - 18));
        this.addSlotToContainer(new SlotProgrammer(par1InventoryPlayer.player, par2TileEntityProgrammer, 2, 116, 35));
        int var3;

        for (var3 = 0; var3 < 3; ++var3)
        {
            for (int var4 = 0; var4 < 9; ++var4)
            {
                this.addSlotToContainer(new Slot(par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));
            }
        }

        for (var3 = 0; var3 < 9; ++var3)
        {
            this.addSlotToContainer(new Slot(par1InventoryPlayer, var3, 8 + var3 * 18, 142));
        }
    }

    public void addCraftingToCrafters(ICrafting par1ICrafting)
    {
        super.addCraftingToCrafters(par1ICrafting);
        par1ICrafting.sendProgressBarUpdate(this, 1, this.Programmer.ProgrammerCookTime);
        par1ICrafting.sendProgressBarUpdate(this, 0, this.Programmer.ProgrammerCookTime);
    }
    @Override
    protected void retrySlotClick(int par1, int par2, boolean par3, EntityPlayer par4EntityPlayer)
    {
        //this.slotClick(par1, par2, 1, par4EntityPlayer);
    }
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        Iterator var1 = this.crafters.iterator();

        while (var1.hasNext())
        {
            ICrafting var2 = (ICrafting)var1.next();

            if (this.lastBurnTime != this.Programmer.ProgrammerCookTime)
            {
                var2.sendProgressBarUpdate(this, 1, this.Programmer.ProgrammerCookTime);
            }
        }

        this.lastBurnTime = this.Programmer.ProgrammerCookTime;
    }

    public void updateProgressBar(int par1, int par2)
    {
        if (par1 == 1)
        {
            this.Programmer.ProgrammerCookTime = par2;
        }
    }

    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return this.Programmer.isUseableByPlayer(par1EntityPlayer);
    }

    public ItemStack func_82846_b(EntityPlayer par1EntityPlayer, int par1)
    {
        ItemStack var2 = null;
        Slot var3 = (Slot)this.inventorySlots.get(par1);

        if (var3 != null && var3.getHasStack())
        {
            ItemStack var4 = var3.getStack();
            var2 = var4.copy();

            if (par1 == 2)
            {
                if (!this.mergeItemStack(var4, 3, 38, true))
                {
                    return null;
                }

                var3.onSlotChange(var4, var2);
            }
            else if (par1 != 1 && par1 != 0)
            {
                if (TileEntityProgrammer.isItemFuel(var4))
                {
                    if (!this.mergeItemStack(var4, 0, 2, false))
                    {
                        return null;
                    }
                }
                else if (par1 >= 3 && par1 < 30)
                {
                    if (!this.mergeItemStack(var4, 30, 38, false))
                    {
                        return null;
                    }
                }
                else if (par1 >= 30 && par1 < 38 && !this.mergeItemStack(var4, 3, 30, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(var4, 3, 38, false))
            {
                return null;
            }

            if (var4.stackSize == 0)
            {
                var3.putStack((ItemStack)null);
            }
            else
            {
                var3.onSlotChanged();
            }

            if (var4.stackSize == var2.stackSize)
            {
                return null;
            }

            var3.onPickupFromSlot(par1EntityPlayer, var4);
        }

        return var2;
    }
}
