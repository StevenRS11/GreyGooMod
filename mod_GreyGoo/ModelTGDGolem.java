package StevenGreyGoo.mod_GreyGoo;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

public class ModelTGDGolem extends ModelBase
{
    public ModelRenderer TGDGolemHead;

    public ModelRenderer TGDGolemBody;

    public ModelRenderer TGDGolemRightArm;

    public ModelRenderer TGDGolemLeftArm;

    public ModelRenderer TGDGolemLeftLeg;

    public ModelRenderer TGDGolemRightLeg;

    public ModelTGDGolem()
    {
        this(0.0F);
    }

    public ModelTGDGolem(float par1)
    {
        this(par1, -7.0F);
    }

    public ModelTGDGolem(float par1, float par2)
    {
        short var3 = 128;
        short var4 = 128;
        this.TGDGolemHead = (new ModelRenderer(this)).setTextureSize(var3, var4);
        this.TGDGolemHead.setRotationPoint(0.0F, 0.0F + par2, -2.0F);
        this.TGDGolemHead.setTextureOffset(0, 0).addBox(-4.0F, -12.0F, -5.5F, 8, 10, 8, par1);
        this.TGDGolemHead.setTextureOffset(24, 0).addBox(-1.0F, -5.0F, -7.5F, 2, 4, 2, par1);
        this.TGDGolemBody = (new ModelRenderer(this)).setTextureSize(var3, var4);
        this.TGDGolemBody.setRotationPoint(0.0F, 0.0F + par2, 0.0F);
        this.TGDGolemBody.setTextureOffset(0, 40).addBox(-9.0F, -2.0F, -6.0F, 18, 12, 11, par1);
        this.TGDGolemBody.setTextureOffset(0, 70).addBox(-4.5F, 10.0F, -3.0F, 9, 5, 6, par1 + 0.5F);
        this.TGDGolemRightArm = (new ModelRenderer(this)).setTextureSize(var3, var4);
        this.TGDGolemRightArm.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.TGDGolemRightArm.setTextureOffset(60, 21).addBox(-13.0F, -2.5F, -3.0F, 4, 30, 6, par1);
        this.TGDGolemLeftArm = (new ModelRenderer(this)).setTextureSize(var3, var4);
        this.TGDGolemLeftArm.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.TGDGolemLeftArm.setTextureOffset(60, 58).addBox(9.0F, -2.5F, -3.0F, 4, 30, 6, par1);
        this.TGDGolemLeftLeg = (new ModelRenderer(this, 0, 22)).setTextureSize(var3, var4);
        this.TGDGolemLeftLeg.setRotationPoint(-4.0F, 18.0F + par2, 0.0F);
        this.TGDGolemLeftLeg.setTextureOffset(37, 0).addBox(-3.5F, -3.0F, -3.0F, 6, 16, 5, par1);
        this.TGDGolemRightLeg = (new ModelRenderer(this, 0, 22)).setTextureSize(var3, var4);
        this.TGDGolemRightLeg.mirror = true;
        this.TGDGolemRightLeg.setTextureOffset(60, 0).setRotationPoint(5.0F, 18.0F + par2, 0.0F);
        this.TGDGolemRightLeg.addBox(-3.5F, -3.0F, -3.0F, 6, 16, 5, par1);
    }

    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7);
        this.TGDGolemHead.render(par7);
        this.TGDGolemBody.render(par7);
        this.TGDGolemLeftLeg.render(par7);
        this.TGDGolemRightLeg.render(par7);
        this.TGDGolemRightArm.render(par7);
        this.TGDGolemLeftArm.render(par7);
    }

    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6)
    {
        this.TGDGolemHead.rotateAngleY = par4 / (180F / (float)Math.PI);
        this.TGDGolemHead.rotateAngleX = par5 / (180F / (float)Math.PI);
        this.TGDGolemLeftLeg.rotateAngleX = -1.5F * this.func_78172_a(par1, 13.0F) * par2;
        this.TGDGolemRightLeg.rotateAngleX = 1.5F * this.func_78172_a(par1, 13.0F) * par2;
        this.TGDGolemLeftLeg.rotateAngleY = 0.0F;
        this.TGDGolemRightLeg.rotateAngleY = 0.0F;
    }

    public void setLivingAnimations(EntityLiving par1EntityLiving, float par2, float par3, float par4)
    {
        EntityTGDGolem var5 = (EntityTGDGolem)par1EntityLiving;
        int var6 = var5.getAttackTimer();

        if (var6 > 0)
        {
            this.TGDGolemRightArm.rotateAngleX = -2.0F + 1.5F * this.func_78172_a((float)var6 - par4, 10.0F);
            this.TGDGolemLeftArm.rotateAngleX = -2.0F + 1.5F * this.func_78172_a((float)var6 - par4, 10.0F);
        }
    }

    private float func_78172_a(float par1, float par2)
    {
        return (Math.abs(par1 % par2 - par2 * 0.5F) - par2 * 0.25F) / (par2 * 0.25F);
    }
}
