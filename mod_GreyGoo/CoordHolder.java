package StevenGreyGoo.mod_GreyGoo;

public class CoordHolder
{
	public int xCoord;
	public int yCoord;
	public int zCoord;
	
	CoordHolder(int x, int y, int z)
	{
		xCoord=x;
		yCoord=y;
		zCoord=z;
	}
	
	public static CoordHolder newCoordHolder(int x, int y, int z)
	{
		
		return new CoordHolder( x,  y,  z);
		
			
		
	}
	
	
}