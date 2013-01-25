package StevenGreyGoo.mod_GreyGoo;


import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class TileEntityGooPortal extends TileEntity
{
	SpreadHelper spreadHelper= new SpreadHelper(worldObj, this.xCoord,this.yCoord, this.zCoord, 3, true, true);
	public boolean flag=true;
	public boolean sending=false;
	public int sendCounter=0;
	World destinationDim; 
	public int iteratorCount=0;
	public int timer=0;
	public int timer1=0;
	public int timer2=0;
	CoordHolder coordEMP;
	CoordHolder coordInert;
	CoordHolder coordElse;
	public boolean EMPDone=false;
	public boolean inertDone=false;
	public boolean clearDone=false;
	
	List EMPblocks;
	List Inertblocks;
	List Clearblocks;
	
	@Override
    public boolean canUpdate()
    {
        return true;
    }
    public TileEntityGooPortal()
    {
    	
    	
    	
		
		
		
    }

    public void findAll(int which)
    {
    	spreadHelper.worldObj=this.worldObj;
		spreadHelper.setBase(xCoord, yCoord, zCoord);
    	if( which==0)
    	{
		spreadHelper.checkCubeOutline=true;
		spreadHelper.foundBlockCoords.clear();
		spreadHelper.blocksToFind.clear();
		spreadHelper.setNegOrPosSearch(false);
		
		EMPblocks=spreadHelper.findBlocks();
		System.out.println("EMP");
    	}
    	
    	if( which==1)
    	{
    		spreadHelper.checkCubeOutline=false;
    		spreadHelper.foundBlockCoords.clear();
    		spreadHelper.blocksToFind.clear();
    		spreadHelper.setNegOrPosSearch(false);
    		spreadHelper.addID(mod_GreyGoo.EMPArraySecondaryID);
    		spreadHelper.onlyCheckMaxRadius=true;
    		Inertblocks=spreadHelper.findBlocks();
    		System.out.println("Inert");
    	}
		
    	if( which==2)
    	{
    		
    		spreadHelper.foundBlockCoords.clear();
    		spreadHelper.blocksToFind.clear();
    		
    		spreadHelper.addID(mod_GreyGoo.EMPArraySecondaryID);
    		spreadHelper.addID(mod_GreyGoo.GooPortalID);
    		spreadHelper.addID(mod_GreyGoo.BlockInertID);
    		spreadHelper.onlyCheckMaxRadius=false;
    		System.out.println("clear");
		
		
		 Clearblocks=spreadHelper.findBlocks();
    	}
		
    }
    @Override
    public void updateEntity()
    {
    	if(!this.worldObj.isRemote)
    	{
    		if(!mod_GreyGoo.worldsWithPortal.containsKey(worldObj.provider.dimensionId))
    				{
    				mod_GreyGoo.worldsWithPortal.put(worldObj.provider.dimensionId,this );
    				}
    		
    		
    	
    		boolean flag2=true;

    		int i;
    		int j;
    		int k;
    	
    		if (this.worldObj.getBlockId(this.xCoord, yCoord, zCoord) != mod_GreyGoo.instance.GooPortal.blockID)
    		{
    			this.worldObj.removeBlockTileEntity(this.xCoord,  this.yCoord,  this.zCoord);
    			this.invalidate();
    			this.updateContainingBlockInfo();
    			clearGooPortalTileEntity();
    		}
        
    		
        
    	}
    	if(sending&&sendCounter<=200&&(this.worldObj.provider.dimensionId==mod_GreyGoo.instance.dimensionID||this.worldObj.provider.dimensionId==0))
    	{
    		
    		send();
    		
    	}
        
    }
    public void buildPortal()
    {
    	
    	if(timer==0)
    	{
    		findAll(0);
    	}
    
    	sending=true;
    
    	
    	
		
		
		
			if(timer<EMPblocks.size()&&!EMPDone)
			{
				//System.out.println("empMake");
				coordEMP=(CoordHolder)EMPblocks.get(timer);
				
				
					destinationDim.setBlockWithNotify(coordEMP.xCoord, coordEMP.yCoord, coordEMP.zCoord, mod_GreyGoo.EMPArraySecondary.blockID);
					this.worldObj.setBlockWithNotify(coordEMP.xCoord, coordEMP.yCoord, coordEMP.zCoord, mod_GreyGoo.EMPArraySecondary.blockID);
					
					
					timer++;
			}
			else if(!EMPDone)
		    	
	    	{
				findAll(1);
	    		EMPDone=true;
	    		
	    	}
			
			
				if(EMPDone&&timer1<Inertblocks.size())
				{
				
					coordInert=(CoordHolder)Inertblocks.get(timer1);
					
					
					
				destinationDim.setBlockWithNotify(coordInert.xCoord, coordInert.yCoord, coordInert.zCoord, mod_GreyGoo.BlockInert.blockID);
						this.worldObj.setBlockWithNotify(coordInert.xCoord, coordInert.yCoord, coordInert.zCoord, mod_GreyGoo.BlockInert.blockID);
						timer1++;
						
					
				}
				else if(EMPDone&&!inertDone)
				      
		        {
		        	inertDone=true;
		        	findAll(2);
		        }
				
					if(inertDone&&timer2<Clearblocks.size())
					{
					
						coordElse=(CoordHolder)Clearblocks.get(timer2);
						
						
						{
							destinationDim.setBlockWithNotify(coordElse.xCoord, coordElse.yCoord, coordElse.zCoord, 0);
							this.worldObj.setBlockWithNotify(coordElse.xCoord, coordElse.yCoord, coordElse.zCoord, 0);
							timer2++;
						
						}
					}
					else if(inertDone)
					{
						this.worldObj.setBlockMetadata(xCoord, yCoord, zCoord, 1);
						sendCounter=200;
						
					}
					
				
				
				
					
			
			
			
			
			
			
			
			
    }
    public void send()
    {
    	mod_GreyGoo.instance.dimHelper.getBlockIDfromFreshChunk( this.worldObj,  this.xCoord,  this.yCoord,  this.zCoord);
    	
    	
    	

    	if(sendCounter<1&&!this.worldObj.isRemote&&(this.worldObj.provider.dimensionId==0||this.worldObj.provider.dimensionId==mod_GreyGoo.dimensionID))
    	{
    		
    		
    	
    		
    		mod_GreyGoo.proxy.printStringClient("Charging...");
    		if(this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord)!=1)
			{
    			mod_GreyGoo.proxy.printStringClient("Building...");
			
			}
    		
    		if(this.worldObj.provider.dimensionId==0)
    		{
    			 destinationDim = mod_GreyGoo.instance.dimHelper.getWorld(mod_GreyGoo.dimensionID);
    		}
    		else
    		{
    			 destinationDim = mod_GreyGoo.instance.dimHelper.getWorld(0);
    		}
    		
    		if(!destinationDim.isRemote&&!this.worldObj.isRemote)
			{
    	
    		//if(this.spreadHelper.foundBlockCoords.size()==68)
			
    		sendCounter=1;
    		
    			destinationDim.setBlockWithNotify(xCoord, yCoord, zCoord, mod_GreyGoo.GooPortalID);
    			destinationDim.setBlockMetadata(xCoord, yCoord, zCoord, 1);
    		}
    	}
    			if(this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord)!=1)
    			{
    			buildPortal();
    			
    			}
    			else
    			{
    				sendCounter=200;
    				sending=true;
    			}
// /**
    			int count=-2;
    			while(count<=2)
    			{
    				try
    				{
    					
    				
    				//DimensionManager.getProvider(var5.getWorldInfo().getDimension()).getChunkProvider().loadChunk((xCoord/16)+count, (zCoord/16)+count);
    				count++;
    				}
    				catch(Exception e)
    				{
    					break;
    				}
    			}
    	//	
    		
    
    		
    	
    	
		if(sendCounter>=200&&!this.worldObj.isRemote)
		{
			spreadHelper.setBase(this.xCoord,yCoord, zCoord);
			spreadHelper.foundBlockCoords.clear();
			spreadHelper.blocksToFind.clear();
			spreadHelper.addID(mod_GreyGoo.EMPArraySecondaryID);
			spreadHelper.addID(mod_GreyGoo.BlockInertID);
			//spreadHelper.addID(mod_GreyGoo.GooPortalID);
			//spreadHelper.addID(0);
			spreadHelper.worldObj=this.worldObj;
			spreadHelper.setNegOrPosSearch(false);
			spreadHelper.onlyCheckMaxRadius=true;
			List list=spreadHelper.findBlocks();
			if(list.size()!=0)
			{
				mod_GreyGoo.proxy.printStringClient(String.valueOf(list.size()));
    			mod_GreyGoo.proxy.printStringClient("Teleporter Array damaged- try again");
    			//this.worldObj.setBlockMetadata(xCoord, yCoord, zCoord, 0);
    			
			}
			else if(this.worldObj.getClosestPlayer(this.xCoord, this.yCoord, this.zCoord, 4)==null)
			{
				
	    		mod_GreyGoo.proxy.printStringClient("Player of range- try again");

				
			}
			else
			{
				if(this.worldObj.getClosestPlayer(this.xCoord, this.yCoord, this.zCoord, 4) instanceof EntityPlayerMP&&!this.worldObj.isRemote)
				{
				EntityPlayerMP entity=(EntityPlayerMP)this.worldObj.getClosestPlayer(this.xCoord, this.yCoord, this.zCoord, 4);
				mod_GreyGoo.proxy.printStringClient("Teleport Successful");
				entity.mcServer.getConfigurationManager().transferPlayerToDimension(entity, destinationDim.provider.dimensionId, new GooTeleporter((WorldServer) this.worldObj));
				sendCounter=0;
				sending=false;
				}
			}
			
			
			sendCounter=0;
			sending=false;
		}
    }

    public void clearGooPortalTileEntity()
    {
        if (this.worldObj != null)
        {
            this.worldObj.removeBlockTileEntity(this.xCoord, this.yCoord, this.zCoord);
            this.invalidate();
        }
    }

    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
    }

    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
    }
}
