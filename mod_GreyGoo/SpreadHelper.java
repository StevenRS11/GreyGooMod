package StevenGreyGoo.mod_GreyGoo;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;




public class SpreadHelper
{
	/**
     * determines if it should only check around in a shell
     */
	public boolean onlyCheckMaxRadius=false;
	public boolean checkCubeOutline=false;
	
	/**
     * list of block coords in vec3 format that fit the conditions. index 0 is size, null if no blocks found satisfy conditions
     */
	public List foundBlockCoords = new ArrayList();

	private boolean findNegOrPos=true;
	public List blocksToFind= new ArrayList();
	private int xBase;
	private int yBase;
	private int zBase;
	private int searchRadius;
	public World worldObj;
	public boolean doDiagonals=false;
	
	/**
     * Class to find blocks that satsify conditions-
     * 
     * world
     * 
     * x
     * y
     * z
     * 
     * r- radius of search
     * 
     * flag- include diagonals in search
     * flag2- true; find blocks that satisfy, false, find blocks that dont
     */
	 public SpreadHelper(World world,int x, int y, int z, int r, Boolean flag, Boolean flag2)
	 {
		 worldObj=world;
		 xBase=x;
		 yBase=y;
		 zBase=z;
		 doDiagonals=flag;
		 searchRadius=r;
		 //foundBlockCoords.add(0, 0);
		 this.findNegOrPos=flag2;
	       
	 }
	
	 
	 public SpreadHelper(World world)
	 {
		 worldObj=world;
		 xBase=0;
		 yBase=0;
		 zBase=0;
		 doDiagonals=false;
		 searchRadius=0;
		 //foundBlockCoords.add(0, null);
	       
	 }
	 
	 
	
	 
	private boolean positiveIDChecker(CoordHolder location)
	{
		 int xLoc=(location.xCoord);
		 int yLoc=(location.yCoord);
		 int zLoc=(location.zCoord);
		// boolean flag=true;
 
		 
		 if(!blocksToFind.contains(this.worldObj.getBlockId(xLoc,yLoc,zLoc)))
		 {
			 return false;
		 }
		 if(checkCubeOutline)
		 {
				if(((MathHelper.abs(yLoc-this.yBase)==3&&MathHelper.abs(xLoc-this.xBase)==3))||((MathHelper.abs(zLoc-this.zBase)==3&&MathHelper.abs(yLoc-this.yBase)==3))||((MathHelper.abs(zLoc-this.zBase)==3&&MathHelper.abs(xLoc-this.xBase)==3)))
 
				{
					return true;
				}
				else
				{
					return false;
				}
		 }

		 
		 if(onlyCheckMaxRadius)
		 {		
			 if(!doDiagonals)
			 {
				 
				 if((Math.abs(xLoc-this.xBase) + Math.abs(yLoc-this.yBase) + Math.abs(zLoc-this.zBase) == searchRadius))
				 {
					 return true;
				 }
			 
			 	return false;
			 }
			 else
			 {
				 if((Math.abs(xLoc-this.xBase) == searchRadius)||(Math.abs(yLoc-this.yBase) == searchRadius)||(Math.abs(zLoc-this.zBase) == searchRadius))
				 {
					 return true;
				 }
			 
			 	return false; 
			 }
			 
		 }
		 
		 if(!(Math.abs(xLoc-this.xBase) + Math.abs(yLoc-this.yBase) + Math.abs(zLoc-this.zBase) <= searchRadius)&&!doDiagonals)
		 {
			 return false;
		 }
		
		
			 return true;
		 
		 
		 
		 
	 }
	
	private boolean negativeIDChecker(CoordHolder location)
	{
		

	
		int xLoc=(location.xCoord);
		 int yLoc=(location.yCoord);
		 int zLoc=(location.zCoord);
		// boolean flag=true;

		 
		 if(blocksToFind.contains(this.worldObj.getBlockId(xLoc,yLoc,zLoc)))
		 {
			 return false;
			 
			 
		 }
		 if(checkCubeOutline)
		 {
				if(((MathHelper.abs(yLoc-this.yBase)==3&&MathHelper.abs(xLoc-this.xBase)==3))||((MathHelper.abs(zLoc-this.zBase)==3&&MathHelper.abs(yLoc-this.yBase)==3))||((MathHelper.abs(zLoc-this.zBase)==3&&MathHelper.abs(xLoc-this.xBase)==3)))
 
				{
					return true;
				}
				else
				{
					return false;
				}
		 }

		 
		 if(onlyCheckMaxRadius)
		 {		
			 if(!doDiagonals)
			 {
				 
				 if((Math.abs(xLoc-this.xBase) + Math.abs(yLoc-this.yBase) + Math.abs(zLoc-this.zBase) == searchRadius))
				 {
					 return true;
				 }
			 
			 	return false;
			 }
			 else 
			 {
				 if((Math.abs(xLoc-this.xBase) == searchRadius)||(Math.abs(yLoc-this.yBase) == searchRadius)||(Math.abs(zLoc-this.zBase) == searchRadius))
				 {
					 return true;
				 }
			 
			 	return false; 
			 }
			 
		 }
		 
		 if(!(Math.abs(xLoc-this.xBase) + Math.abs(yLoc-this.yBase) + Math.abs(zLoc-this.zBase) <= searchRadius)&&!doDiagonals)
		 {
			 return false;
		 }
		
		
			 return true;
		
	}
	 
	
	/**
     * Adds a block to the list of blocks checked. Args id
     */
	 
	 public void addID(int id)
	 {
		 this.blocksToFind.add(id);
		 
		 
	 }
	 /**
	     * removes a block from the list of blocks checked. Args id
	     */

	 public void removeID(int id)

	 {
		 this.blocksToFind.remove(this.blocksToFind.indexOf(id));
	 
	 }
	 
	 
	 /**
	     * clears the list of blocks checked. 
	     */
	 public void clearIDCheckList()

	 {
		 this.blocksToFind.clear();
	 
	 }
	 
	 /**
	     * searches an area surrounding a block defined by r against the list of blocks in this.blocksToFind
	     */
	
	 public List findBlocks()
	 {
		 this.foundBlockCoords.clear();
		 
		 if(!this.worldObj.isRemote)
		 {
		
		 int indexCounter=0;
		 int xCount=-searchRadius;
		 int yCount=-searchRadius;
		 int zCount=-searchRadius;
		 
		 while (xCount<=searchRadius)
		 {
			 while(yCount<=searchRadius)
			 {
				 while(zCount<=searchRadius)
				 {
					 CoordHolder location= CoordHolder.newCoordHolder(xCount+xBase, yCount+yBase,zCount+zBase);

					 
					 if(this.findNegOrPos)
					 {
						 if(this.positiveIDChecker(location))
						 {
							 this.foundBlockCoords.add(indexCounter, location);	
							 indexCounter++;
												 
						 }
					 }
					 else
					 if(!this.findNegOrPos)
					 {
						 if(this.negativeIDChecker(location))
						 {
							 this.foundBlockCoords.add(indexCounter, location);	
							 indexCounter++;
												 
						 }
					 }
					 
					 zCount++;
				 }
				 zCount=-searchRadius;
				 yCount++;
			 }
			 yCount=-searchRadius;
			 xCount++;
		 }
		 
		 return foundBlockCoords;
		 }
		 else
		 {
			 return null;
		 }
	 }
	 /**
	     * determines whether the searching function should search for blocks in the list or search for blocks not in the list. 
	     */
	 public void setNegOrPosSearch(boolean flag)
	 {
		 this.findNegOrPos=flag;
	 }
	 
	 /**
	     * sets where the search should begin
	     */
	 public void setBase(int x,int y, int z)
	 {
		 xBase=x;
		 yBase=y;
		 zBase=z;
		 
	 }
	
}