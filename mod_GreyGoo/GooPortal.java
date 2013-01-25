package StevenGreyGoo.mod_GreyGoo;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GooPortal extends BlockContainer
{
    Random rand = new Random();
    public World world;
    protected GooPortal(int i, int j)
    {
        super(i, j, Material.ground);
        setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }
    @Override
    public String getTextureFile()
    {
        return "/GooBlockTextures.png";
    }

   

    public TileEntity createNewTileEntity(World par1World)
    {
        TileEntity tile = new TileEntityGooPortal();
        return tile;
    }
    public int quantityDropped(Random par1Random)
    {
        
            return 1;
        
       
    }

    public void updateTick(World world, int i, int j, int k, Random random)
    {
        
    }

    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
    {
    	
    	//world.removeBlockTileEntity(i, j, k);
    	
    //	world.setBlockTileEntity(i, j, k, createNewTileEntity(world));
        if (!world.isRemote && !entityplayer.isSneaking()&&entityplayer instanceof EntityPlayerMP)
        {
        	//mod_GreyGoo.instance.proxy.printStringClient("send?");
        //	world.removeBlockTileEntity(i, j, k);
        	TileEntityGooPortal  tileentity =(TileEntityGooPortal) world.getBlockTileEntity(i, j, k);
        	tileentity.flag=true;
        	tileentity.sendCounter=0;
        	//tileentity.findAll(0);
        	//tileentity.findAll(1);
        	//tileentity.findAll(2);
        	//mod_GreyGoo.instance.dimHelper.loadBackupDim(entityplayer.worldObj);
        	//mod_GreyGoo.instance.dimHelper.gooTeleport(world.provider.dimensionId) .getChunkProvider().loadChunk(i/16, k/16);
           
            
           // if(entityplayer instanceof EntityPlayerMP&&!world.isRemote&&(tileentity.spreadHelper.foundBlockCoords.size()==68))
        	if((world.provider.dimensionId==mod_GreyGoo.instance.dimensionID||world.provider.dimensionId==0))
	
        	{
           		
            	tileentity.send();    	
                
            } 
        	else
        	{
        		mod_GreyGoo.proxy.printStringClient("Invalid dim to Teleport, Rainbow Goo activated");
        	}
            return true;
        }
    	
 
        return false;
    }

   

    
    
}
