package StevenGreyGoo.mod_GreyGoo;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCancer2 extends BlockContainer
{
    private EntityPlayer EntityPlayer;
    private boolean hasTicked;
    Random random = new Random();

    @Override
    public String getTextureFile()
    {
        return "/GooBlockTextures.png";
    }
    
    protected BlockCancer2(int i, int j)
    {
        super(i, j, Material.ground);
        setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }
    @Override
    public int quantityDropped(Random par1Random)
    {
        if (par1Random.nextInt(20) == 1)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    private void assimilate(World world, int i, int j, int k)
    {
    
    	boolean hasFood=false;
        int l = -1;
        int i1 = -1;
        int j1 = -1;
        boolean flag = false;
        int k1 = -3;
        int l1 = -3;
        int i2 = -3;
        boolean flag1 = false;

        for (; k1 < 5; k1++)
        {
            for (; l1 < 5; l1++)
            {
                for (; i2 < 5; i2++)
                {
                    if (world.getBlockId(i + k1, j + l1, k + i2) != 0 && world.getBlockId(i + k1, j + l1, k + i2) != mod_GreyGoo.BlockCancer.blockID && world.getBlockId(i + k1, j + l1, k + i2) != mod_GreyGoo.BlockCancer2.blockID)
                    {
                        flag1 = true;
                    }
                }

                i2 = -4;
            }

            l1 = -4;
        }
        if (world.getBlockMetadata(i, j, k) != 2)
        {

        for (; i1 < 2; i1++)
        {
            for (; j1 < 2; j1++)
            {
                for (; l < 2; l++)
                {
                    if (world.getBlockId(i + l, j + i1, k + j1) == mod_GreyGoo.BlockCleaner.blockID)
                    {
                        world.setBlockWithNotify(i, j, k, mod_GreyGoo.BlockCleaner.blockID);
                        continue;
                    }

                    if (!world.isAirBlock(i + l, j + i1, k + j1) && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockCleaner.blockID && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockBlack.blockID && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockCancer.blockID && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockWall.blockID && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockInert.blockID && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockFreezer.blockID && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockTGD.blockID && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockTGDinert.blockID && world.getBlockId(i + l, j + i1, k + j1) != blockID && world.getBlockId(i + l, j + i1, k + j1) != Block.chest.blockID && world.isAirBlock(i + l, j + i1 + 1, k + j1) && world.getBlockId(i + l, j + i1, k + j1) != this.blockID && !mod_GreyGoo.gooNeverEatThese.contains(world.getBlockId(i + l, j + i1, k + j1)) && !world.isRemote)
                    {
                    	hasFood=true;
                    
                    	 if (world.getClosestPlayer(i, j, k, 10) != null)
                         {
                    		 mod_GreyGoo.instance.spreadLimiter.Destroyerspreadlimiter(true);

                    		 world.setBlockWithNotify(i + l, j + i1, k + j1, blockID);

                       
                         }
                    	 else if(random.nextInt(3)==1)
                    	 {
                    		 mod_GreyGoo.instance.spreadLimiter.Destroyerspreadlimiter(true);

                    		 world.setBlockWithNotify(i + l, j + i1, k + j1, blockID); 
                    	 }
                    }
                }

                l = -1;
            }

            j1 = -1;
        }
        }
    

        if (!flag1)
        {
            if (hasTicked)
            {
                hasTicked = false;
            }
            else
            {
                hasTicked = true;
            }

            if (hasTicked && !world.isRemote)
            {
                world.setBlockWithNotify(i, j, k, mod_GreyGoo.BlockTGD.blockID);
            }
        }
        if(!hasFood&&world.getClosestPlayer(i, j, k, 20)==null)
        {
        	world.setBlockMetadata(i, j, k, 2);
        	world.removeBlockTileEntity(i, j, k);
        	
        }
    }
   
    @Override
    public void updateTick(World world, int i, int j, int k, Random random)
    {
        if (mod_GreyGoo.isGooActive(this.blockID, world) && !world.isRemote&&mod_GreyGoo.instance.spreadLimiter.Destroyerspreadlimiter(false))
        {
            assimilate(world, i, j, k);
        }

        byte byte0 = -1;
        byte byte1 = -1;
        byte byte2 = -1;
        boolean flag = false;
        int l = -3;
        int i1 = -3;
        int j1 = -3;
        boolean flag1 = false;

        for (; l < 5; l++)
        {
            for (; i1 < 5; i1++)
            {
                for (; j1 < 5; j1++)
                {
                    if (world.getBlockId(i + l, j + i1, k + j1) != 0 && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockCancer.blockID && world.getBlockId(i + l, j + i1, k + j1) != mod_GreyGoo.BlockCancer2.blockID)
                    {
                        flag1 = true;
                    }
                }

                j1 = -4;
            }

            i1 = -4;
        }

        if (!flag1)
        {
            if (hasTicked)
            {
                hasTicked = false;
            }
            else
            {
                hasTicked = true;
            }

            if (hasTicked && !world.isRemote)
            {
                world.setBlockWithNotify(i, j, k, mod_GreyGoo.BlockTGD.blockID);
            }
        }
    }
    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
    {
        if (!world.isRemote && !entityplayer.isSneaking())
        {
            assimilate(world, i, j, k);
            return true;
        }

        return false;
    }

    
    @Override
    public void onEntityWalking(World world, int i, int j, int k, Entity entity)
    {
        if (!world.isRemote)
        {
            assimilate(world, i, j, k);
        }

        if (entity instanceof EntityLiving)
        {
            ((EntityLiving) entity).addPotionEffect(new PotionEffect(Potion.poison.getId(), 50, 1));
            ((EntityLiving) entity).addPotionEffect(new PotionEffect(Potion.wither.getId(), 50, 1));
          
        }
    }

    public void randomDisplayTick(World world, int i, int j, int k, Random random)
    {
        if (random.nextInt(100) != 0);

        for (int l = 0; l < 4; l++)
        {
            double d = (float)i + random.nextFloat();
            double d1 = (float)j + random.nextFloat();
            double d2 = (float)k + random.nextFloat();
            double d3 = 0.0D;
            double d4 = 0.0D;
            double d5 = 0.0D;
            int i1 = random.nextInt(2) * 2 - 1;
            d3 = ((double)random.nextFloat() - 0.5D) * 0.5D;
            d4 = ((double)random.nextFloat() - 0.5D) * 0.5D;
            d5 = ((double)random.nextFloat() - 0.5D) * 0.5D;

            if (world.getBlockId(i - 1, j, k) == blockID || world.getBlockId(i + 1, j, k) == blockID)
            {
                d2 = (double)k + 0.5D + 0.25D * (double)i1;
                d5 = random.nextFloat() * 2.0F * (float)i1;
            }
            else
            {
                d = (double)i + 0.5D + 0.25D * (double)i1;
                d3 = random.nextFloat() * 2.0F * (float)i1;
            }

            world.spawnParticle("portal", d, d1, d2, d3, d4, d5);
        }
    }

    @Override
    public TileEntity createNewTileEntity(World var1)

    {
        return new TileEntityCancer2();
    }
}
