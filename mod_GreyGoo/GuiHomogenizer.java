package StevenGreyGoo.mod_GreyGoo;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

public class GuiHomogenizer extends GuiContainer
{
    private TileEntityHomogenizer HomogenizerInventory;

    public GuiHomogenizer(InventoryPlayer par1InventoryPlayer, TileEntityHomogenizer par2TileEntityHomogenizer)
    {
        super(new ContainerHomogenizer(par1InventoryPlayer, par2TileEntityHomogenizer));
        this.HomogenizerInventory = par2TileEntityHomogenizer;
    }

    protected void drawGuiContainerForegroundLayer()
    {
    }
    public static GuiHomogenizer buildGUI(InventoryPlayer playerInventory, TileEntityHomogenizer assemblyInventory)
    {
        return new GuiHomogenizer(playerInventory, assemblyInventory);
    }

    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        int var4 = this.mc.renderEngine.getTexture("/gui/Homogenizer.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(var4);
        int var5 = (this.width - this.xSize) / 2;
        int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
        int var7;
        var7 = (this.HomogenizerInventory.getCookProgressScaled(24));
        this.drawTexturedModalRect(var5 + 75, var6 + 40, 176, 14, var7 + 1, 16);
    }
}
