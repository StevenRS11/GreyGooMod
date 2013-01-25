package StevenGreyGoo.mod_GreyGooClient;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import StevenGreyGoo.mod_GreyGoo.EntityFallingGravityGoo;

public class RenderFallingGravityGoo extends Render
{
    private RenderBlocks renderBlocks = new RenderBlocks();

    public RenderFallingGravityGoo()
    {
    	
        this.shadowSize = 0.5F;
        
    }

    /**
     * The actual render method that is used in doRender
     */
    public void doRenderFallingGravityGoo(EntityFallingGravityGoo par1EntityFallingGravityGoo, double par2, double par4, double par6, float par8, float par9)
    {


        GL11.glPushMatrix();
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        this.loadTexture("/GooBlockTextures.png");
        Block var10 = Block.blocksList[par1EntityFallingGravityGoo.blockID];
        World var11 = par1EntityFallingGravityGoo.getWorld();
        GL11.glDisable(GL11.GL_LIGHTING);

      
            this.renderBlocks.blockAccess = var11;
            Tessellator var12 = Tessellator.instance;
            var12.startDrawingQuads();
            var12.setTranslation((double)((float)(-MathHelper.floor_double(par1EntityFallingGravityGoo.posX)) - 0.5F), (double)((float)(-MathHelper.floor_double(par1EntityFallingGravityGoo.posY)) - 0.5F), (double)((float)(-MathHelper.floor_double(par1EntityFallingGravityGoo.posZ)) - 0.5F));
            this.renderBlocks.renderBlockByRenderType(var10, MathHelper.floor_double(par1EntityFallingGravityGoo.posX), MathHelper.floor_double(par1EntityFallingGravityGoo.posY), MathHelper.floor_double(par1EntityFallingGravityGoo.posZ));
            var12.setTranslation(0.0D, 0.0D, 0.0D);
            var12.draw();
        
       

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {

    	
        this.doRenderFallingGravityGoo((EntityFallingGravityGoo)par1Entity, par2, par4, par6, par8, par9);
    }
}
