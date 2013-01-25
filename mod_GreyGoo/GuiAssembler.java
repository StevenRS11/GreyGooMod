package StevenGreyGoo.mod_GreyGoo;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

public class GuiAssembler extends GuiContainer
{
    private TileEntityAssembler AssemblerInventory;

    public GuiAssembler(InventoryPlayer par1InventoryPlayer, TileEntityAssembler par2TileEntityAssembler)
    {
        super(new ContainerAssembler(par1InventoryPlayer, par2TileEntityAssembler));
        this.AssemblerInventory = par2TileEntityAssembler;
    }

    protected void drawGuiContainerForegroundLayer()
    {
    }
    public static GuiAssembler buildGUI(InventoryPlayer playerInventory, TileEntityAssembler assemblyInventory)
    {
        return new GuiAssembler(playerInventory, assemblyInventory);
    }

    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        int var4 = this.mc.renderEngine.getTexture("/gui/Assembler.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(var4);
        int var5 = (this.width - this.xSize) / 2;
        int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
        int var7;
        var7 = (this.AssemblerInventory.getCookProgressScaled(24));
        this.drawTexturedModalRect(var5 + 79, var6 + 34, 176, 14, var7 + 1, 16);
    }
}
