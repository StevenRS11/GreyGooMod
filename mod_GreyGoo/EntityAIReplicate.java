package StevenGreyGoo.mod_GreyGoo;

import java.util.Random;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityAIReplicate extends EntityAIBase
{
    private EntityCreature theCreature;
    private double golemX;
    private double golemY;
    private double golemZ;
    private float movementSpeed;
    private World theWorld;

    public EntityAIReplicate(EntityCreature par1EntityCreature, float par2)
    {
        this.theCreature = par1EntityCreature;
        this.movementSpeed = par2;
        this.theWorld = par1EntityCreature.worldObj;
        this.setMutexBits(1);
    }

    public boolean shouldExecute()
    {
        Vec3 var1 = this.findPossibleLocation();

        if (var1 == null)
        {
            return false;
        }
        else
        {
            this.golemX = var1.xCoord;
            this.golemY = var1.yCoord;
            this.golemZ = var1.zCoord;
            return true;
        }
    }

    public boolean continueExecuting()
    {
    	boolean flag=true;
        Random rand = this.theCreature.getRNG();

        if ((this.theCreature.getNavigator().getPath() != null))
        {
            if (this.theCreature.getNavigator().getPath().isFinished() && rand.nextInt(4) == 0  && mod_GreyGoo.instance.TGDbloom)
            {
            	
            	if(mod_GreyGoo.inactiveGooList.containsKey(mod_GreyGoo.BlockTGDID))
            		{
            			if((Integer)mod_GreyGoo.inactiveGooList.get(mod_GreyGoo.BlockTGDID)==this.theWorld.provider.dimensionId)
            			{
            				flag=false;
            			}
            		}
            			
            	if(flag)
            	{
                int x = MathHelper.floor_double(this.golemX);
                int z =  MathHelper.floor_double(this.golemZ);
                int y = MathHelper.floor_double(this.golemY);
                this.theWorld.setBlockWithNotify(MathHelper.floor_double(this.golemX) + 2,  y,  MathHelper.floor_double(this.golemZ) + 2, mod_GreyGoo.BlockTGDinertID);
                this.theWorld.setBlockWithNotify(MathHelper.floor_double(this.golemX) + 2,  y + 1,  MathHelper.floor_double(this.golemZ) + 2, mod_GreyGoo.BlockTGDinertID);
                this.theWorld.setBlockWithNotify(MathHelper.floor_double(this.golemX) + 2,  y + 2,  MathHelper.floor_double(this.golemZ) + 2, mod_GreyGoo.BlockTGDinertID);
                this.theWorld.setBlockWithNotify(MathHelper.floor_double(this.golemX) + 2,  y + 3,  MathHelper.floor_double(this.golemZ) + 2, mod_GreyGoo.BlockTGDinertID);
                this.theWorld.setBlockWithNotify(MathHelper.floor_double(this.golemX) + 1,  y + 2,  MathHelper.floor_double(this.golemZ) + 2, mod_GreyGoo.BlockTGDinertID);
                this.theWorld.setBlockWithNotify(MathHelper.floor_double(this.golemX) + 3,  y + 2,  MathHelper.floor_double(this.golemZ) + 2, mod_GreyGoo.BlockTGDinertID);
            	}
            }
        }

        return !this.theCreature.getNavigator().noPath();
    }

    public void startExecuting()
    {
        this.theCreature.getNavigator().tryMoveToXYZ(this.golemX, this.golemY, this.golemZ, this.movementSpeed);
    }

    private Vec3 findPossibleLocation()
    {
        Random var1 = this.theCreature.getRNG();

        for (int var2 = 0; var2 < 10; ++var2)
        {
            int var3 = MathHelper.floor_double(this.theCreature.posX + (double)var1.nextInt(60) - 30.0D);
            int var4 = MathHelper.floor_double(this.theCreature.boundingBox.minY + (double)var1.nextInt(8) - 4.0D);
            int var5 = MathHelper.floor_double(this.theCreature.posZ + (double)var1.nextInt(60) - 30.0D);

            if ((MathHelper.abs_int(MathHelper.floor_double(var3 - this.theCreature.posX))) + (MathHelper.abs_int(MathHelper.floor_double(var5 - this.theCreature.posZ))) > 17)
            {
                return this.theWorld.getWorldVec3Pool().getVecFromPool((double)var3, (double)var4, (double)var5);
            }
        }

        return null;
    }
}
