package StevenGreyGoo.mod_GreyGoo;
import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
public class ContainerHomogenizer extends Container
{
    private TileEntityHomogenizer Homogenizer;
    private int lastCookTime = 0;
    private int lastBurnTime = 0;
    private int lastItemBurnTime = 0;

    public ContainerHomogenizer(InventoryPlayer par1InventoryPlayer, TileEntityHomogenizer par2TileEntityHomogenizer)
    {
        this.Homogenizer = par2TileEntityHomogenizer;
        this.addSlotToContainer(new Slot(par2TileEntityHomogenizer, 0, 26, 41));
        this.addSlotToContainer(new Slot(par2TileEntityHomogenizer, 1, 26 + 18, 41 + 18));
        this.addSlotToContainer(new Slot(par2TileEntityHomogenizer, 2, 26 - 18, 41 - 18));
        this.addSlotToContainer(new Slot(par2TileEntityHomogenizer, 3, 26 + 18, 41 - 18));
        this.addSlotToContainer(new Slot(par2TileEntityHomogenizer, 4, 26 - 18, 41 + 18));
        this.addSlotToContainer(new Slot(par2TileEntityHomogenizer, 5, 26, 41 + 18));
        this.addSlotToContainer(new Slot(par2TileEntityHomogenizer, 6, 26, 41 - 18));
        this.addSlotToContainer(new Slot(par2TileEntityHomogenizer, 7, 26 - 18, 41));
        this.addSlotToContainer(new Slot(par2TileEntityHomogenizer, 8, 26 + 18, 41));
        this.addSlotToContainer(new SlotHomogenizer(par1InventoryPlayer.player, par2TileEntityHomogenizer, 9,  133, 41));
        this.addSlotToContainer(new SlotHomogenizer(par1InventoryPlayer.player, par2TileEntityHomogenizer, 10, 133 + 18, 41 + 18));
        this.addSlotToContainer(new SlotHomogenizer(par1InventoryPlayer.player, par2TileEntityHomogenizer, 11, 133 - 18, 41 - 18));
        this.addSlotToContainer(new SlotHomogenizer(par1InventoryPlayer.player, par2TileEntityHomogenizer, 12, 133 + 18, 41 - 18));
        this.addSlotToContainer(new SlotHomogenizer(par1InventoryPlayer.player, par2TileEntityHomogenizer, 13, 133 - 18, 41 + 18));
        this.addSlotToContainer(new SlotHomogenizer(par1InventoryPlayer.player, par2TileEntityHomogenizer, 14, 133 + 18, 41));
        this.addSlotToContainer(new SlotHomogenizer(par1InventoryPlayer.player, par2TileEntityHomogenizer, 15, 133 - 18, 41));
        this.addSlotToContainer(new SlotHomogenizer(par1InventoryPlayer.player, par2TileEntityHomogenizer, 16, 133, 41 + 18));
        this.addSlotToContainer(new SlotHomogenizer(par1InventoryPlayer.player, par2TileEntityHomogenizer, 17, 133, 41 - 18));
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
        par1ICrafting.sendProgressBarUpdate(this, 1, this.Homogenizer.HomogenizerCookTime);
        par1ICrafting.sendProgressBarUpdate(this, 0, this.Homogenizer.HomogenizerCookTime);
    }

    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        Iterator var1 = this.crafters.iterator();

        while (var1.hasNext())
        {
            ICrafting var2 = (ICrafting)var1.next();

            if (this.lastBurnTime != this.Homogenizer.HomogenizerCookTime)
            {
                var2.sendProgressBarUpdate(this, 1, this.Homogenizer.HomogenizerCookTime);
            }
        }

        this.lastBurnTime = this.Homogenizer.HomogenizerCookTime;
    }

    public void updateProgressBar(int par1, int par2)
    {
        if (par1 == 1)
        {
            this.Homogenizer.HomogenizerCookTime = par2;
        }
    }

    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return this.Homogenizer.isUseableByPlayer(par1EntityPlayer);
    }
    
    @Override
    protected void retrySlotClick(int par1, int par2, boolean par3, EntityPlayer par4EntityPlayer)
    {
        //this.slotClick(par1, par2, 1, par4EntityPlayer);
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
                if (TileEntityHomogenizer.isItemFuel(var4))
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
