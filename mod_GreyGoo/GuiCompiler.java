package StevenGreyGoo.mod_GreyGoo;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

public class GuiCompiler extends GuiContainer
{
    private TileEntityCompiler CompilerInventory;

    public GuiCompiler(InventoryPlayer par1InventoryPlayer, TileEntityCompiler par2TileEntityCompiler)
    {
        super(new ContainerCompiler(par1InventoryPlayer, par2TileEntityCompiler));
        this.CompilerInventory = par2TileEntityCompiler;
    }

    protected void drawGuiContainerForegroundLayer()
    {
    }
    public static GuiCompiler buildGUI(InventoryPlayer playerInventory, TileEntityCompiler assemblyInventory)
    {
        return new GuiCompiler(playerInventory, assemblyInventory);
    }

    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        int var4 = this.mc.renderEngine.getTexture("/gui/Compiler.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(var4);
        int var5 = (this.width - this.xSize) / 2;
        int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
        int var7;
        var7 = (this.CompilerInventory.getCookProgressScaled(24));
        this.drawTexturedModalRect(var5 + 106, var6 + 34, 176, 14, var7 + 1, 16);
    }
}
