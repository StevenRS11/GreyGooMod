package StevenGreyGoo.mod_GreyGoo;

import java.util.Random;

import net.minecraft.util.MathHelper;




public class SpreadLimiter
{
		private static Random random =new Random();
		public static int maxspreadpertick=100;
	    public static int spreadthistick = 0;
	
	    public static int Destroyerspreadthistick = 0;
	    public static int Destroyermaxspreadpertick=70;
	  
	    public static int Restorerspreadthistick=0;
	    public static int Maxrestorerspreadpertick=100;

	    public static int TGDmaxspreadpertick=50;
	    public static int TGDspreadthistick = 0;
	   
	    public static int maxnumberoffalling=25;
	    public static int numberoffalling=0;

	    public  boolean spreadLimiter(boolean flag)
	    {
	    	 if (flag)
		        {
		            ++spreadthistick;
		        }


	    		int base=MathHelper.floor_double((spreadthistick-maxspreadpertick/maxspreadpertick));
	    		
	           //mod_GreyGoo.proxy.printStringClient(String.valueOf(base));

	    		if((random.nextInt(100)>(base+mod_GreyGoo.GeneralSpreadScale)))
	    		{
	    			return true;
	    		}
	    		else
	    		{
	    			return false;
	    		}
	    }
	    
	    public  boolean Destroyerspreadlimiter(boolean flag)
	    {
	        if (flag)
	        {
	            ++Destroyerspreadthistick;
	        }


    		int base=MathHelper.floor_double((Destroyerspreadthistick-Destroyermaxspreadpertick/Destroyermaxspreadpertick));
    		
           // mod_GreyGoo.proxy.printStringClient(String.valueOf(base));

    		if((random.nextInt(100)>(base+mod_GreyGoo.DestroyerSpreadScale)))
    		{
    			return true;
    		}
    		else
    		{
    			return false;
    		}
	    }

	    public  boolean TGDspreadlimiter(boolean flag)
	    {
	    	 if (flag)
		        {
		            ++TGDspreadthistick;
		        }


	    		int base=MathHelper.floor_double((TGDspreadthistick-TGDmaxspreadpertick/TGDmaxspreadpertick));
	    		
	           // mod_GreyGoo.proxy.printStringClient(String.valueOf(base));

	    		if((random.nextInt(100)>(base+mod_GreyGoo.TGDSpreadScale)))
	    		{
	    			return true;
	    		}
	    		else
	    		{
	    			return false;
	    		}
	    }
	    public  boolean Restorerspreadlimiter(boolean flag)
	    {
	    	 if (flag)
		        {
		            ++Restorerspreadthistick;
		        }


	    		int base=MathHelper.floor_double((Restorerspreadthistick-Maxrestorerspreadpertick/Maxrestorerspreadpertick));
	    		
	           

	    		if((random.nextInt(100)>(base+mod_GreyGoo.RestorerSpreadScale)))
	    		{
	    			return true;
	    		}
	    		else
	    		{
	    			return false;
	    		} 
	    		
	    }
	    
	    public  boolean numberoffallingLimiter(boolean flag)
	    {
	    	 if (flag)
		        {
		            ++numberoffalling;
		        }


	    		int base=MathHelper.floor_double((numberoffalling-maxnumberoffalling/maxnumberoffalling));
	    		
	           // mod_GreyGoo.proxy.printStringClient(String.valueOf(base));

	    		if((random.nextInt(100)>(base+mod_GreyGoo.FallingSpreadScale)))
	    		{
	    			return true;
	    		}
	    		else
	    		{
	    			return false;
	    		}
	    }

	    
	    public  void resetSpreadCounters()
	    {
	        spreadthistick = 0;
	        TGDspreadthistick = 0;
	        Destroyerspreadthistick = 0;
	        Restorerspreadthistick = 0;
	        this.numberoffalling=0;
	    }
}