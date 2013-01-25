package StevenGreyGoo.mod_GreyGooClient;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

import org.lwjgl.opengl.GL11;

import StevenGreyGoo.mod_GreyGoo.EntityTGDGolem;


// Referenced classes of package net.minecraft.src:
//                      RenderLiving, JWorld_EntityExample, ModelBase, EntityLiving,
//                      Entity

public class RenderTGDGolem extends RenderLiving
{
	
	
        
        public RenderTGDGolem(ModelBase modelbase, float f)
        {
                super(modelbase, f);
        }
        

        public void RenderTGDGolem(EntityTGDGolem EntityTGDGolem, double d, double d1, double d2, float f, float f1)
        {

                super.doRenderLiving(EntityTGDGolem, d, d1, d2, f, f1);
        }

        public void doRenderLiving(EntityLiving entityliving, double d, double d1, double d2,
                        float f, float f1)
        {
                RenderTGDGolem((EntityTGDGolem)entityliving, d, d1, d2, f, f1);

        			
        	
        			
        }

        public void doRender(Entity entity, double d, double d1, double d2,
                        float f, float f1)
        {
                RenderTGDGolem((EntityTGDGolem)entity, d, d1, d2, f, f1);
        }
        
        //attempting to scale
        
         protected void preRenderScale(EntityTGDGolem entity, float f)
{
         GL11.glScalef(1.25F, 1.25F, 1.25F);
        }

        protected void preRenderCallback(EntityLiving entityliving, float f)
        {
         preRenderScale((EntityTGDGolem)entityliving, f);
        }
}