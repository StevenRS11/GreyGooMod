package StevenGreyGoo.mod_GreyGooClient;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import StevenGreyGoo.mod_GreyGoo.mod_GreyGoo;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;




public class RenderOrangeWhiteGoo implements ISimpleBlockRenderingHandler
{
	
   
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
    	
         return false;
    }

    public boolean shouldRender3DInInventory()
    {
         return true;
    }

    public int getRenderId()
    {
         return 65;
    }

	@Override
	public void renderInventoryBlock(Block block,
			int metadata, int modelID, RenderBlocks renderblocks) 
	
	{
		Tessellator tessellator = Tessellator.instance;

        if(modelID == mod_GreyGoo.OrangeWhiteRenderID)
        {
        	
            //block.setBlockBoundsForItemRender();

                block.setBlockBounds(0.0F, 0.0F, 0.0F, 1F, 1F, 1F);
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                float f2 = 0.0F;
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, -1F, 0.0F);
                renderblocks.renderBottomFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(0));
                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, 1.0F, 0.0F);
                renderblocks.renderTopFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(1));
                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, 0.0F, -1F);
                tessellator.setTranslation(0.0F, 0.0F, f2);
                renderblocks.renderEastFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(2));
                tessellator.setTranslation(0.0F, 0.0F, -f2);
                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, 0.0F, 1.0F);
                tessellator.setTranslation(0.0F, 0.0F, -f2);
                renderblocks.renderWestFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(3));
                tessellator.setTranslation(0.0F, 0.0F, f2);
                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setNormal(-1F, 0.0F, 0.0F);
                tessellator.setTranslation(f2, 0.0F, 0.0F);
                renderblocks.renderNorthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(4));
                tessellator.setTranslation(-f2, 0.0F, 0.0F);
                tessellator.draw();
                tessellator.startDrawingQuads();
                tessellator.setNormal(1.0F, 0.0F, 0.0F);
                tessellator.setTranslation(-f2, 0.0F, 0.0F);
                renderblocks.renderSouthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(5));
                tessellator.setTranslation(f2, 0.0F, 0.0F);
                tessellator.draw();
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        }
        
       
		
		
	}}