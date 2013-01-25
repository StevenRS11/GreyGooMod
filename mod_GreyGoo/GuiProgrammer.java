package StevenGreyGoo.mod_GreyGoo;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

public class GuiProgrammer extends GuiContainer
{
    private TileEntityProgrammer ProgrammerInventory;

    public GuiProgrammer(InventoryPlayer par1InventoryPlayer, TileEntityProgrammer par2TileEntityProgrammer)
    {
        super(new ContainerProgrammer(par1InventoryPlayer, par2TileEntityProgrammer));
        this.ProgrammerInventory = par2TileEntityProgrammer;
    }

    protected void drawGuiContainerForegroundLayer()
    {
    }
    public static GuiProgrammer buildGUI(InventoryPlayer playerInventory, TileEntityProgrammer assemblyInventory)
    {
        return new GuiProgrammer(playerInventory, assemblyInventory);
    }

    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        int var4 = this.mc.renderEngine.getTexture("/gui/Programmer.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(var4);
        int var5 = (this.width - this.xSize) / 2;
        int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
        int var7;
        var7 = (this.ProgrammerInventory.getCookProgressScaled(24));
        this.drawTexturedModalRect(var5 + 79, var6 + 34, 176, 14, var7 + 1, 16);
    }
}
