package StevenGreyGoo.mod_GreyGooClient;
import net.minecraft.src.ModLoader;
import net.minecraftforge.client.MinecraftForgeClient;
import StevenGreyGoo.mod_GreyGoo.*;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{
	@Override
	public void registerRenderers()
	{

		MinecraftForgeClient.preloadTexture(BLOCK_PNG);
		MinecraftForgeClient.preloadTexture(ITEM_PNG);
	//	MinecraftForgeClient.preloadTexture(WARP_PNG);
		RenderingRegistry.registerEntityRenderingHandler(EntityFallingGravityGoo.class, new RenderFallingGravityGoo());
		RenderingRegistry.registerEntityRenderingHandler(EntityTGDGolem.class, new RenderTGDGolem(new ModelTGDGolem(), 0.5F));
		RenderingRegistry.registerBlockHandler(new RenderOrangeWhiteGoo());
	}
	
	
	@Override
	public void loadTextures()
	{

		mod_GreyGoo.ItemNanoLens.setIconIndex(62).setItemName("NanoLens");  
		    
	    mod_GreyGoo.ItemNanoLathe.setIconIndex(63).setItemName("NanoLathe");  

	    mod_GreyGoo.ItemModifierGrey.setIconIndex(31).setItemName("erfgdsfsdfg");  

	    mod_GreyGoo.ItemModifierRed.setIconIndex(45).setItemName("erfgsdfg");  
	    
	    mod_GreyGoo.ItemModifierYellow.setIconIndex(47).setItemName("Browadfanwgoo");  

	    mod_GreyGoo.ItemModifierPurple.setIconIndex(44).setItemName("aerew");  

	    mod_GreyGoo.ItemModifierOrange.setIconIndex(43).setItemName("ererw");  

	    mod_GreyGoo.ItemModifierBlue.setIconIndex(28).setItemName("adfadf"); 
	    
	    mod_GreyGoo.ItemModifierWhite.setIconIndex(46).setItemName("adadd"); 
	    
	    mod_GreyGoo.ItemModifierGreen.setIconIndex(30).setItemName("adfa");  
	    
	    mod_GreyGoo.ItemModifierBrown.setIconIndex(29).setItemName("Brownwgoo");
	   
	    
		mod_GreyGoo.ItemMatrixRainbow.setIconIndex(61).setItemName("Rainbow Matrix");  

	    mod_GreyGoo.ItemMatrixRed.setIconIndex(38).setItemName("erfgsd4fg");  
	    
	    mod_GreyGoo.ItemMatrixYellow.setIconIndex(41).setItemName("Browad4fanwgoo");  

	    mod_GreyGoo.ItemMatrixPurple.setIconIndex(34).setItemName("ae4rew");  

	    mod_GreyGoo.ItemMatrixOrange.setIconIndex(17).setItemName("er4erw");  

	    mod_GreyGoo.ItemMatrixBlue.setIconIndex(9).setItemName("adf4adf"); 
	    
	    mod_GreyGoo.ItemMatrixWhite.setIconIndex(39).setItemName("a4dadd"); 
	    
	    mod_GreyGoo.ItemMatrixGreen.setIconIndex(20).setItemName("ad4fa");  
	    
	    mod_GreyGoo.ItemMatrixBrown.setIconIndex(25).setItemName("Bro4wnwgoo"); 
	    
	    mod_GreyGoo.ItemMatrixWhiteGreen.setIconIndex(1).setItemName("a4dffdadd"); 
	    
	    mod_GreyGoo.ItemMatrixBrownRed.setIconIndex(21).setItemName("a4ggffdadd"); 
	    
	    mod_GreyGoo.ItemMatrixBlueRed.setIconIndex(22).setItemName("a4dghdadd"); 
	    
	    mod_GreyGoo.ItemMatrixOrangeRed .setIconIndex(16).setItemName("a4wwghgdadd"); 

	    mod_GreyGoo.ItemMatrixOrangeWhite .setIconIndex(32).setItemName("a4ghrergdadd"); 

	    mod_GreyGoo.ItemMatrixOrangePurple.setIconIndex(35).setItemName("a4ereghgdadd"); 

	    mod_GreyGoo.ItemMatrixYellowRed .setIconIndex(15).setItemName("Browad4fanwswegoo");  

	    mod_GreyGoo.ItemMatrixPurpleRed.setIconIndex(36).setItemName("Browad4fasfnwswegoo");  

	    mod_GreyGoo.ItemMatrixGrey.setIconIndex(0).setItemName("Browad4fasfnwserwerwegoo");  

	    mod_GreyGoo.ItemMatrixGreenRed .setIconIndex(2).setItemName("a4dghasddadd"); 
	}
	@Override
	public  void printStringClient(String string)
	{
		
		ModLoader.getMinecraftInstance().ingameGUI.getChatGUI().printChatMessage(string);
	}
	
}