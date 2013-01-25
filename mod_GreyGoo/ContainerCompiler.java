package StevenGreyGoo.mod_GreyGoo;
import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
public class ContainerCompiler extends Container
{
    private TileEntityCompiler Compiler;
    private int lastCookTime = 0;
    private int lastBurnTime = 0;
    private int lastItemBurnTime = 0;

    public ContainerCompiler(InventoryPlayer par1InventoryPlayer, TileEntityCompiler par2TileEntityCompiler)
    {
        this.Compiler = par2TileEntityCompiler;
        this.addSlotToContainer(new Slot(par2TileEntityCompiler, 3, 17, 33 - 18));
        this.addSlotToContainer(new SlotCompiler(par1InventoryPlayer.player, par2TileEntityCompiler, 2, 142, 33));
        this.addSlotToContainer(new Slot(par2TileEntityCompiler, 0, 17, 33 + 16));
        this.addSlotToContainer(new Slot(par2TileEntityCompiler, 4,  78 - 18, 43));
        this.addSlotToContainer(new Slot(par2TileEntityCompiler, 5, 78 - 18, 43 - 18));
        this.addSlotToContainer(new Slot(par2TileEntityCompiler, 6, 78, 43 - 18));
        this.addSlotToContainer(new Slot(par2TileEntityCompiler, 7, 78, 43));
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
        par1ICrafting.sendProgressBarUpdate(this, 1, this.Compiler.CompilerCookTime);
        par1ICrafting.sendProgressBarUpdate(this, 0, this.Compiler.CompilerCookTime);
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

            if (this.lastBurnTime != this.Compiler.CompilerCookTime)
            {
                var2.sendProgressBarUpdate(this, 1, this.Compiler.CompilerCookTime);
            }
        }

        this.lastBurnTime = this.Compiler.CompilerCookTime;
    }

    public void updateProgressBar(int par1, int par2)
    {
        if (par1 == 1)
        {
            this.Compiler.CompilerCookTime = par2;
        }
    }

    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return this.Compiler.isUseableByPlayer(par1EntityPlayer);
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
                if (TileEntityCompiler.isItemFuel(var4))
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
