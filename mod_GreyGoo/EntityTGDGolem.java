package StevenGreyGoo.mod_GreyGoo;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityTGDGolem extends EntityGolem

{
    private int attackTimer;
    private int field_70826_g = 0;
    private static boolean[] carriableBlocks = new boolean[256];
    private boolean isalive = false;
    private boolean diddie = false;
    public  List TGDgolems = new ArrayList();
    public boolean isthisagolem = true;
    int ai1 = 4;
    int ai2 = 6;

    public EntityTGDGolem(World par1World)
    {
        super(par1World);
        this.texture = "/mob/TGDGolem.png";
        this.setSize(1.4F, 2.9F);
        this.moveSpeed = 0.6F;
        this.getNavigator().setAvoidsWater(false);
        this.tasks.addTask(1, new EntityAIAttackOnCollide(this, 0.40F, true));
        this.tasks.addTask(2, new EntityAIMoveTowardsTarget(this, 0.40F, 50.0F));
        this.tasks.addTask(4, new EntityAIReplicate(this, 0.16F));
        this.tasks.addTask(6, new EntityAIWander(this, 0.16F));
        this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 16.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityMob.class, 40.0F, 0, false, true));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntitySlime.class, 40.0F, 0, false, true));
        this.targetTasks.addTask(5, new EntityAINearestAttackableTarget(this, EntityAnimal.class, 40.0F, 0, false, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 40.0F, 0, true));
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(16, new Byte((byte)0));
        this.dataWatcher.addObject(17, new Byte((byte)0));
        this.dataWatcher.addObject(18, new Byte((byte)0));
    }

    public int getcount(World world)
    {
        int i = 0;
        int count = 0;
        int max = world.loadedEntityList.size();

        while (i < max)
        {
            Entity golem = (Entity)world.loadedEntityList.get(i);

            if (EntityList.getEntityString(golem) == "TGD Golem")
            {
                ++count;
            }

            ++i;
        }

        return count;
    }

    public boolean isAIEnabled()
    {
        return true;
    }

    protected void updateAITick()
    {
        super.updateAITick();
    }

    public int getMaxHealth()
    {
        return 15;
    }

    protected int decreaseAirSupply(int par1)
    {
        return par1;
    }

    public void onLivingUpdate()
    {
        if (getcount(worldObj) > mod_GreyGoo.maxnumberofTGDgolems - mod_GreyGoo.maxnumberofTGDgolems / 15)
        {
            this.setDead();
        }

        if (getcount(worldObj) > mod_GreyGoo.maxnumberofTGDgolems - mod_GreyGoo.maxnumberofTGDgolems / 10)
        {
            ai1 = 6;
            ai2 = 4;
        }
        else
        {
            ai1 = 4;
            ai2 = 6;
        }

        if (!isalive && !this.worldObj.isRemote)
        {
            ++mod_GreyGoo.totalnumberofTGDgolems;
            isalive = true;
        }

        int i;
        int j;
        int k;
        i = MathHelper.floor_double(this.posX);
        j = MathHelper.floor_double(this.posY) - 1;
        k = MathHelper.floor_double(this.posZ);

        if (this.worldObj.getBlockId(i , j , k) != Block.chest.blockID && this.worldObj.getBlockId(i, j, k) != mod_GreyGoo.BlockCancerID && this.worldObj.getBlockId(i, j, k) != mod_GreyGoo.BlockCancer2ID && this.worldObj.getBlockId(i, j, k) != mod_GreyGoo.BlockTGDinertID && this.worldObj.getBlockId(i, j, k) != 0 && this.worldObj.getBlockId(i, j, k) != mod_GreyGoo.BlockCleanerID)
        {
            this.worldObj.setBlockWithNotify(i, j, k, mod_GreyGoo.BlockTGDinertID);
        }

        int xr = this.rand.nextInt(20);
        int yr = this.rand.nextInt(20);
        int zr = this.rand.nextInt(20);

        if (this.attackTimer > 0)
        {
            --this.attackTimer;
        }

        if (this.motionX * this.motionX + this.motionZ * this.motionZ > 2.500000277905201E-7D && this.rand.nextInt(5) == 0)
        {
            int var6 = MathHelper.floor_double(this.posX);
            int var2 = MathHelper.floor_double(this.posY - 0.20000000298023224D - (double)this.yOffset);
            int var3 = MathHelper.floor_double(this.posZ);
            int var4 = this.worldObj.getBlockId(var6, var2, var3);

           
        }

        super.onLivingUpdate();
    }

    public boolean attackEntityAsMob(Entity par1Entity)
    {
        this.attackTimer = 10;
        this.worldObj.setEntityState(this, (byte)4);
        boolean var2 = par1Entity.attackEntityFrom(DamageSource.causeMobDamage(this), 7 + this.rand.nextInt(15));

        if (var2)
        {
            par1Entity.motionY += 0.4000000059604645D;
        }

        this.worldObj.playSoundAtEntity(this, "mob.irongolem.throw", 1.0F, 1.0F);
        return var2;
    }

    public void handleHealthUpdate(byte par1)
    {
        if (par1 == 4)
        {
            this.attackTimer = 10;
            this.worldObj.playSoundAtEntity(this, "mob.irongolem.throw", 1.0F, 1.0F);
        }
        else
        {
            super.handleHealthUpdate(par1);
        }
    }

    public int getAttackTimer()
    {
        return this.attackTimer;
    }

    protected String getLivingSound()
    {
        return "none";
    }

    protected String getHurtSound()
    {
        return "mob.irongolem.hit";
    }

    protected String getDeathSound()

    {
        if (!diddie && !this.worldObj.isRemote)
        {
            --mod_GreyGoo.totalnumberofTGDgolems;
            diddie = true;
        }

        return "mob.irongolem.death";
    }

    protected void fall(float par1) {}

    protected void playStepSound(int par1, int par2, int par3, int par4)
    {
        this.worldObj.playSoundAtEntity(this, "mob.irongolem.walk", 1.0F, 1.0F);
    }

    protected void dropFewItems(boolean par1, int par2)
    {}

    public void setCarried(int par1)
    {
        this.dataWatcher.updateObject(16, Byte.valueOf((byte)(par1 & 255)));
    }

    public int getCarried()
    {
        return this.dataWatcher.getWatchableObjectByte(16);
    }

    public void setCarryingData(int par1)
    {
        this.dataWatcher.updateObject(17, Byte.valueOf((byte)(par1 & 255)));
    }

    public int getCarryingData()
    {
        return this.dataWatcher.getWatchableObjectByte(17);
    }
    protected boolean teleportRandomly()
    {
        double var1 = this.posX + (this.rand.nextDouble() - 0.5D) * 64.0D;
        double var3 = this.posY + (double)(this.rand.nextInt(64) - 32);
        double var5 = this.posZ + (this.rand.nextDouble() - 0.5D) * 64.0D;
        return this.teleportTo(var1, var3, var5);
    }

    protected boolean teleportTo(double par1, double par3, double par5)
    {
        double var7 = this.posX;
        double var9 = this.posY;
        double var11 = this.posZ;
        this.posX = par1;
        this.posY = par3;
        this.posZ = par5;
        boolean var13 = false;
        int var14 = MathHelper.floor_double(this.posX);
        int var15 = MathHelper.floor_double(this.posY);
        int var16 = MathHelper.floor_double(this.posZ);
        int var18;

        if (this.worldObj.blockExists(var14, var15, var16))
        {
            boolean var17 = false;

            while (!var17 && var15 > 0)
            {
                var18 = this.worldObj.getBlockId(var14, var15 - 1, var16);

                if (var18 != 0 && Block.blocksList[var18].blockMaterial.blocksMovement())
                {
                    var17 = true;
                }
                else
                {
                    --this.posY;
                    --var15;
                }
            }

            if (var17)
            {
                this.setPosition(this.posX, this.posY, this.posZ);

                if (this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(this.boundingBox))
                {
                    var13 = true;
                }
            }
        }

        if (!var13)
        {
            this.setPosition(var7, var9, var11);
            return false;
        }
        else
        {
            short var30 = 128;

            for (var18 = 0; var18 < var30; ++var18)
            {
                double var19 = (double)var18 / ((double)var30 - 1.0D);
                float var21 = (this.rand.nextFloat() - 0.5F) * 0.2F;
                float var22 = (this.rand.nextFloat() - 0.5F) * 0.2F;
                float var23 = (this.rand.nextFloat() - 0.5F) * 0.2F;
                double var24 = var7 + (this.posX - var7) * var19 + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2.0D;
                double var26 = var9 + (this.posY - var9) * var19 + this.rand.nextDouble() * (double)this.height;
                double var28 = var11 + (this.posZ - var11) * var19 + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2.0D;
                this.worldObj.spawnParticle("portal", var24, var26, var28, (double)var21, (double)var22, (double)var23);
            }

            this.worldObj.playSoundEffect(var7, var9, var11, "mob.endermen.portal", 1.0F, 1.0F);
            this.worldObj.playSoundAtEntity(this, "mob.endermen.portal", 1.0F, 1.0F);
            return true;
        }
    }

    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
    {
        if (par1DamageSource.getEntity() instanceof EntityPlayer)
        {
            this.func_70819_e(true);
        }

        if (this.rand.nextInt(3) == 0)
        {
            if (this.teleportRandomly())
            {
                return true;
            }
        }

        return super.attackEntityFrom(par1DamageSource, par2);
    }

    public void func_70819_e(boolean par1)
    {
        this.dataWatcher.updateObject(18, Byte.valueOf((byte)(par1 ? 1 : 0)));
    }

    static
    {
        carriableBlocks[Block.grass.blockID] = true;
        carriableBlocks[Block.dirt.blockID] = true;
        carriableBlocks[Block.sand.blockID] = true;
        carriableBlocks[Block.gravel.blockID] = true;
        carriableBlocks[Block.plantYellow.blockID] = true;
        carriableBlocks[Block.plantRed.blockID] = true;
        carriableBlocks[Block.mushroomBrown.blockID] = true;
        carriableBlocks[Block.mushroomRed.blockID] = true;
        carriableBlocks[Block.tnt.blockID] = true;
        carriableBlocks[Block.cactus.blockID] = true;
        carriableBlocks[Block.blockClay.blockID] = true;
        carriableBlocks[Block.pumpkin.blockID] = true;
        carriableBlocks[Block.melon.blockID] = true;
        carriableBlocks[Block.mycelium.blockID] = true;
    }
}
