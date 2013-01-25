package StevenGreyGoo.mod_GreyGoo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import StevenGreyGoo.mod_GreyGooClient.*;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = "GreyGooMod", name = "Grey Goo Mod", version = "2.0.2")

@NetworkMod(clientSideRequired = true, serverSideRequired = false,
        clientPacketHandlerSpec =
                @SidedPacketHandler(channels = {"TutorialMod" }, packetHandler = ClientPacketHandler.class),
        serverPacketHandlerSpec =
                @SidedPacketHandler(channels = {"TutorialMod" }, packetHandler = ServerPacketHandler.class))

public class mod_GreyGoo
{
    @SidedProxy(clientSide = "StevenGreyGoo.mod_GreyGooClient.ClientProxy", serverSide = "StevenGreyGoo.mod_GreyGoo.ClientProxy")
    public static CommonProxy proxy;
    public SpreadLimiter spreadLimiter=new SpreadLimiter();

    @Instance("GreyGooMod")
    public static mod_GreyGoo instance = new mod_GreyGoo();

    public String directory;

    @PreInit
    public void PreInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new EventHookContainer());

        try
        {
            directory = event.getModConfigurationDirectory().getCanonicalPath();
        }
        catch (Exception e)
        {
            proxy.printStringClient("Error-- Could not access save directory, this will cause problems with textures and worldgen");
        }

        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        

        BlockGreyGooID = config.getBlock("BlockGreyGooID", 199).getInt();
        BlockCancerID = config.getBlock("BlockCancerID", 198).getInt();
        BlockCancer2ID = config.getBlock("BlockCancer2ID", 197).getInt();
        BlockTGDID = config.getBlock("BlockTGDID", 196).getInt();
        BlockTGDinertID = config.getBlock("BlockTGDinertID", 195).getInt();
        BlockCleanerID = config.getBlock("BlockCleanerID", 194).getInt();
        BlockWallID = config.getBlock("BlockWallID", 193).getInt();
        BlockInertID = config.getBlock("BlockInertID", 192).getInt();
        BlockWaterEaterID = config.getBlock("BlockWaterEaterID", 191).getInt();
        BlockAirEaterID = config.getBlock("BlockAirEaterID", 190).getInt();
        BlockGreyEaterID = config.getBlock("BlockGreyEaterID", 189).getInt();
        BlockMinerGooID = config.getBlock("BlockMinerGooID", 188).getInt();
        BlockRapidEaterID = config.getBlock("BlockRapidEaterID", 187).getInt();
        BlockDataStorageID = config.getBlock("BlockDataStorageID", 186).getInt();
        BlockElevatorGooID = config.getBlock("BlockElevatorGooID", 185).getInt();
        BlockRapidMinerID = config.getBlock("BlockRapidMinerID", 184).getInt();
        BlockBubbleID = config.getBlock("BlockBubbleID", 183).getInt();
        BlockFreezerID = config.getBlock("BlockFreezerID", 182).getInt();
        BlockGravityGooID = config.getBlock("BlockGravityGooID", 181).getInt();
        BlockBlackID = config.getBlock("BlockBlackID", 180).getInt();
        BlockRestorerID = config.getBlock("BlockRestorerID", 179).getInt();
        BlockOrangeRedID = config.getBlock("BlockOrangeRedID", 178).getInt();
        BlockOrangeWhiteID = config.getBlock("BlockOrangeWhiteID", 177).getInt();
        BlockOrangePurpleID = config.getBlock("BlockOrangePurpleID", 176).getInt();
        BlockYellowWhiteID = config.getBlock("BlockYellowWhiteID", 175).getInt();
        BlockRapidWaterEaterID = config.getBlock("BlockRapidWaterEaterID", 174).getInt();
        BlockSubstrateID = config.getBlock("BlockSubstrateID", 173).getInt();
        GooPortalID = config.getBlock("GooPortalID", 202).getInt();

        
        CoprocessorID = config.getBlock("CoprocessorID", 172).getInt();
        AssemblerID = config.getBlock("AssemblerID", 171).getInt();
        ProgrammerID = config.getBlock("ProgrammerID", 170).getInt();
        HomogenizerID = config.getBlock("HomogenizerID", 169).getInt();
        CompilerID = config.getBlock("CompilerID", 168).getInt();
        EMPArrayID = config.getBlock("EMPArrayID", 201).getInt();
        EMPArraySecondaryID = config.getBlock("EMPArraySecondaryID", 200).getInt();
        
        
        ItemMatrixRedID  = config.getItem("ItemMatrixRedID", 401).getInt();
        ItemMatrixGreenID = config.getItem("ItemMatrixGreenID", 402).getInt();
        ItemMatrixBlueID = config.getItem("ItemMatrixBlueID", 403).getInt();
        ItemMatrixPurpleID = config.getItem("ItemMatrixPurpleID", 404).getInt();
        ItemMatrixWhiteID = config.getItem("ItemMatrixWhiteID", 405).getInt();
        ItemMatrixBrownID = config.getItem("ItemMatrixBrownID", 406).getInt();
        ItemMatrixOrangeID = config.getItem("ItemMatrixOrangeID", 407).getInt();
        ItemMatrixYellowID = config.getItem("ItemMatrixYellowID", 428).getInt();
        ItemMatrixOrangeRedID = config.getItem("ItemMatrixOrangeRedID", 409).getInt();
        ItemMatrixYellowRedID = config.getItem("ItemMatrixYellowRedID", 410).getInt();
        ItemMatrixWhiteGreenID = config.getItem("ItemMatrixWhiteGreenID", 411).getInt();
        ItemMatrixBrownRedID = config.getItem("ItemMatrixBrownRedID", 412).getInt();
        ItemMatrixBlueRedID = config.getItem("ItemMatrixBlueRedID", 413).getInt();
        ItemMatrixPurpleRedID = config.getItem("ItemMatrixPurpleRedID", 414).getInt();
        ItemMatrixGreenRedID = config.getItem("ItemMatrixGreenRedID", 415).getInt();
        ItemMatrixOrangeWhiteID = config.getItem("ItemMatrixOrangeWhiteID", 416).getInt();
        ItemMatrixOrangePurpleID = config.getItem("ItemMatrixOrangePurpleID", 426).getInt();
        ItemMatrixGreyID = config.getItem("ItemMatrixGreyID", 427).getInt();        
        ItemMatrixRainbowID = config.getItem("ItemMatrixRainbowID", 431).getInt();

      
        ItemModifierGreyID = config.getItem("ItemModifierGreyID", 417).getInt();
        ItemModifierRedID = config.getItem("ItemModifierRedID", 418).getInt();
        ItemModifierGreenID = config.getItem("ItemModifierGreenID", 419).getInt();
        ItemModifierBlueID = config.getItem("ItemModifierBlueID", 420).getInt();
        ItemModifierPurpleID = config.getItem("ItemModifierPurpleID", 421).getInt();
        ItemModifierWhiteID = config.getItem("ItemModifierWhiteID", 422).getInt();
        ItemModifierBrownID = config.getItem("ItemModifierBrownID", 423).getInt();
        ItemModifierOrangeID = config.getItem("ItemModifierOrangeID", 424).getInt();
        ItemModifierYellowID = config.getItem("ItemModifierYellowID", 425).getInt();
        ItemNanoLensID = config.getItem("ItemNanoLensID", 429).getInt();
        ItemNanoLatheID = config.getItem("ItemNanoLatheID", 428).getInt();
        
        ItemModifierGreyID = BlockGreyEaterID + 4000;
        ItemModifierRedID = BlockCleanerID + 4000;
        ItemModifierGreenID = BlockInertID + 4000;
        ItemModifierBlueID = BlockWaterEaterID + 4000;
        ItemModifierPurpleID = BlockGreyGooID + 4000;
        ItemModifierWhiteID = BlockAirEaterID + 4000;
        ItemModifierBrownID = BlockMinerGooID + 4000;
        ItemModifierOrangeID = BlockWallID + 4000;
        ItemModifierYellowID = BlockGravityGooID + 4000;
        
        
        bloomheight = config.get("INT", "bloomheight", 146).getInt();
        GooActive = config.get("BOOLEAN", "GooActive", true).getBoolean(true);
        
        
        TGDworldgenfrequecy = config.get("INT", "TGDworldgenfrequecy", 300).getInt();
        
        
        GeneralSpreadScale = config.get("INT", "Value between 0 and 100, where 0 is no spread reduction and 100 never spreading at all-- general goos", 0).getInt();
       	RestorerSpreadScale = config.get("INT", "Value between 0 and 100, where 0 is no spread reduction and 100 never spreading at all-- Restorer/rainbow goo", 0).getInt();

        DestroyerSpreadScale = config.get("INT", "Value between 0 and 100, where 0 is no spread reduction and 100 never spreading at all-- Destroyer series", 0).getInt();
       
        TGDSpreadScale = config.get("INT", "Value between 0 and 100, where 0 is no spread reduction and 100 never spreading at all-- TGD spread speed", 0).getInt();
        FallingSpreadScale = config.get("INT", "Value between 0 and 100, where 0 is no spread reduction and 100 never spreading at all--- Falling spread speed", 0).getInt();

        maxnumberofTGDgolems = config.get("INT", "maxnumberofTGDgolems", 80).getInt();
        totalnumberofTGDgolems = 0;
        debugMode = config.get("BOOLEAN", "debugMode", false).getBoolean(false);
        
       
        mystcraftAgeOnly= config.get("BOOLEAN", "If set to true, enables mystcraft whitelist. Only ages listed there will allow goos to spread", false).getBoolean(false);
       Property myststrin= new Property("IntegerList","",Property.Type.STRING);
       myststrin= config.get("STRING", "List of ages that goos can spread in, only applies if mystCraftAgeOnly=true. List of comma separated ints, <3,7,6,9>", "");
        //this.mystcraftAgeWhitelist
       String values = myststrin.value;
       String compiled="";
       
       int size = values.length();
       int count=0;
       while(size+1>=count)
       {
    	   
    	   int charpos=0;
    	   while(count+charpos<size&&values.charAt(count+charpos)!=',')
    	   {
    	   char cha=values.charAt(count+charpos);
    	   compiled=cha+compiled;
    	   charpos++;
    	   compiled.replace(",", "");
    	   compiled.replace(" ", "");
    	  
    	   }
    	   if(!compiled.isEmpty())
    	   {
    	   this.mystcraftAgeBlacklist.add(Integer.parseInt(compiled));
    	   }
    	   compiled="";
    	   count++;
    	   count=count+charpos;
     
       }
    //   System.out.println(mystcraftAgeWhitelist);
      
        
        
        
      
       OrangeRedSpreadTime = config.get("INT", "OrangeRedSpreadTime", 400).getInt();
       OrangeWhiteSpreadTime = config.get("INT", "OrangeWhiteSpreadTime", 400).getInt();
       OrangePurpleSpreadTime = config.get("INT", "OrangePurpleSpreadTime", 400).getInt();
       PurpleRedSpreadTime = config.get("INT", "PurpleRedSpreadTime", 150).getInt();
       GreenWhteSpreadTime = config.get("INT", "GreenWhteSpreadTime", 70).getInt();
       BrownRedSpreadTime = config.get("INT", "BrownRedSpreadTime", 130).getInt();
       BlueRedSpreadTime = config.get("INT", "BlueRedSpreadTime", 300).getInt();
       RedGreenSpreadTime = config.get("INT", "RedGreenSpreadTime", 800).getInt();
        config.save();
    }

    public static List mineTheseOnly = new ArrayList();
    public static List gooNeverEatThese = new ArrayList();
    public static List cleanerList = new ArrayList();
    public static List Modifiers = new ArrayList();
    public static List Matrices = new ArrayList();
    public static List NeverRestoreThese = new ArrayList();
    public static List allGooBlocks = new ArrayList();
    public static List allGooBlockNames = new ArrayList();

    public static final int GuiProgrammerID = 4;
    public static final int GuiAssemblerID = 0;
    public static final int GuiCompilerID = 1;
    public static final int GuiHomogenizerID = 2;

    public static boolean mystcraftAgeOnly;
    public static List mystcraftAgeBlacklist=new ArrayList();
    public int FreezerTexture;
    public double FreezerOpacity;
    public double FreezerLight;
    public static int defaultFreezerTexure;
    public static int lastDimID=0;
    
    
    public static boolean OrangeRedIsSpreading = false;
    public static boolean OrangePurpleIsSpreading = false;
    public static boolean OrangeWhiteIsSpreading = false;
    public static boolean RapidEaterIsSpreading = false;
    public static boolean RapidWaterEaterisSpreading = false;
    public static boolean RapidMinerisSpreading = false;
    public static boolean BubbleisSpreading = false;
    public static boolean FreezerisSpreading = false;
    public static HashMap worldsWithPortal= new HashMap();

    public static int OrangeWhiteRenderID;
    
    public static int dimensionID; 
    public static int dimensionIDBackUp;
    public static int EMPArraySecondaryID;
  
    public static int OrangeRedSpreadTime;
    public static int OrangeWhiteSpreadTime;
    public static int OrangePurpleSpreadTime;
    public static int PurpleRedSpreadTime;
    public static int GreenWhteSpreadTime;
    public static int BrownRedSpreadTime;
    public static int BlueRedSpreadTime;
    public static int RedGreenSpreadTime;
    
    
    public static int  GeneralSpreadScale;
    public static int	RestorerSpreadScale ;

    public static int DestroyerSpreadScale;
   
    public static int  TGDSpreadScale;
    public static int  FallingSpreadScale;

    public static int GooPortalID;

    public static int EMPArrayID;

    public static int CoprocessorID;

    public static int BlockGreyGooID;

    public static int BlockCancerID;

    public static int BlockCancer2ID;

    public static int BlockTGDID;

    public static int BlockTGDinertID;

    public static int BlockCleanerID ;

    public static int BlockWallID ;

    public static int BlockInertID ;

    public static int BlockWaterEaterID;

    public static int BlockAirEaterID ;

    public static int BlockGreyEaterID;

    public static int BlockMinerGooID ;

    public static int BlockRapidEaterID ;

    public static int BlockDataStorageID;

    public static int BlockElevatorGooID;

    public static int BlockRapidMinerID ;

    public static int BlockBubbleID ;

    public static int BlockFreezerID;

    public static int BlockGravityGooID;

    public static int BlockBlackID;

    public static int BlockRestorerID;

    public static int BlockOrangeRedID;

    public static int BlockOrangeWhiteID;

    public static int BlockOrangePurpleID;

    public static int BlockYellowWhiteID;

    public static int AssemblerID;

    public static int ProgrammerID;
    public static int BlockRapidWaterEaterID;

    public  boolean TGDbloom;
    public  boolean debugMode;

    public static int bloomheight;

    public static int ItemNanoLensID;
    public static int ItemNanoLatheID;

    public static int BlockSubstrateID;

    public static int ItemModifierGreyID;
    public static int ItemModifierRedID ;
    public static int ItemModifierGreenID;
    public static int ItemModifierBlueID ;
    public static int ItemModifierPurpleID;
    public static int ItemModifierWhiteID ;
    public static int ItemModifierBrownID ;
    public static int ItemModifierOrangeID ;
    public static int ItemModifierYellowID;
    public static int ItemMatrixRedID  ;
    public static int ItemMatrixGreenID;
    public static int ItemMatrixBlueID;
    public static int ItemMatrixPurpleID;
    public static int ItemMatrixWhiteID;
    public static int ItemMatrixBrownID;
    public static int ItemMatrixOrangeID;
    public static int ItemMatrixYellowID;
    public static int ItemMatrixOrangeRedID;
    public static int ItemMatrixYellowRedID;
    public static int ItemMatrixWhiteGreenID;
    public static int ItemMatrixBrownRedID;
    public static int ItemMatrixBlueRedID;
    public static int ItemMatrixPurpleRedID;
    public static int ItemMatrixGreenRedID;
    public static int ItemMatrixOrangeWhiteID;
    public static int ItemMatrixRainbowID;
    public static boolean hasLoadedBackupDims=false;


    public static int ItemMatrixOrangePurpleID;

    public static int ItemMatrixGreyID;

    public static int ReplicatorID ;

    public static int HomogenizerID;

    public static int CompilerID;

    public static int AdvancedCompilerID;

    public static boolean GooActive;

    public static int TGDworldgenfrequecy;

    

    public static int maxnumberofTGDgolems;
    public static int totalnumberofTGDgolems = 0;


    private GuiHandler guiHandler = new GuiHandler();
    public static HashMap inactiveGooList = new HashMap();
    
   

    public static  Block BlockGreyGoo;
    public static  Block BlockCancer ;
    public static  Block BlockCancer2;
    public static  Block BlockTGD ;
    public static  Block BlockTGDinert ;
    public static  Block BlockCleaner ;
    public static  Block BlockWall ;
    public static  Block BlockInert ;
    public static  Block BlockWaterEater ;
    public static  Block BlockAirEater ;
    public static  Block BlockGreyEater ;
    public static  Block BlockMinerGoo ;
    public static  Block BlockElevatorGoo ;
    public static  Block BlockRapidMiner ;
    public static  Block BlockBubble ;
    public static  Block BlockOrangeRed ;
    public static  Block BlockOrangePurple ;
    public static  Block BlockOrangeWhite ;
    public static  Block BlockRapidEater ;
    public static  Block BlockRestorer ;
    public static  Block BlockFreezer ;
    public static  Block BlockBlack ;
    public static  Block BlockGravityGoo ;
    public static Block BlockSubstrate ;
    public static Block BlockRapidWaterEater ;

    public static Item ItemNanoLens ;

    public static Item ItemNanoLathe ;

    public static Item ItemModifierGrey ;

    public static Item ItemModifierRed ;

    public static Item ItemModifierYellow ;

    public static Item ItemModifierPurple ;

    public static Item ItemModifierOrange ;

    public static Item ItemModifierBlue ;

    public static Item ItemModifierWhite ;

    public static Item ItemModifierGreen ;

    public static Item ItemModifierBrown ;

    public static Item ItemMatrixRed ;

    public static Item ItemMatrixYellow ;

    public static Item ItemMatrixPurple ;

    public static Item ItemMatrixOrange ;

    public static Item ItemMatrixBlue ;

    public static Item ItemMatrixWhite ;

    public static Item ItemMatrixGreen ;

    public static Item ItemMatrixBrown ;
    
    public static Item ItemMatrixRainbow ;

    public static Item ItemMatrixWhiteGreen ;

    public static Item ItemMatrixBrownRed ;

    public static Item ItemMatrixBlueRed ;

    public static Item ItemMatrixOrangeRed ;

    public static Item ItemMatrixOrangeWhite ;

    public static Item ItemMatrixOrangePurple ;

    public static Item ItemMatrixYellowRed ;

    public static Item ItemMatrixPurpleRed ;

    public static Item ItemMatrixGrey ;

    public static Item ItemMatrixGreenRed ;

    
    public static  Block Coprocessor;
    public static  Block GooPortal;
    public static  Block Programmer ;
    public static  Block Assembler ;
    public static  Block Homogenizer ;
    public static  Block Compiler;
    public static  Block EMPArray ;
    public static  Block EMPArraySecondary ;
    public GooDimensionHelper dimHelper = new GooDimensionHelper();

    public static GreyGooTGDworldgen worldGen = new GreyGooTGDworldgen();

    

    @Init
    public void Init(FMLInitializationEvent event)

    {
    	BlockRestorer = (new BlockRestorer(BlockRestorerID, 9)).setHardness(0.0F).setLightValue(1.0F).setBlockName("RainbowGoo");

        BlockFreezer = (new BlockFreezer(BlockFreezerID, 11)).setHardness(0.0F).setLightValue(0.5F).setBlockName("Green-RedGoo");
        BlockGreyGoo = (new BlockGreyGoo(BlockGreyGooID, 8)).setHardness(0.0F).setLightValue(0.625F).setBlockName("PurpleGoo");
        BlockCancer = (new BlockCancer(BlockCancerID, 1)).setHardness(0.0F).setLightValue(0.625F).setBlockName("Tumor");
        BlockCancer2 = (new BlockCancer2(BlockCancer2ID, 0)).setHardness(0.0F).setLightValue(0.225F).setBlockName("Plague");
        BlockTGD = (new BlockTGD(BlockTGDID, 10)).setHardness(1.0F).setLightValue(0.825F).setBlockName("TheGreatDestroyer");
        BlockTGDinert = (new BlockTGDinert(BlockTGDinertID, 14)).setHardness(0.0F).setLightValue(0.225F).setBlockName("Inert Destroyer ");
        BlockCleaner = (new BlockCleaner(BlockCleanerID, 13)).setHardness(0.0F).setLightValue(1.0F).setBlockName("RedGoo");
        BlockWall = (new BlockWall(BlockWallID, 12)).setBlockUnbreakable().setLightValue(0.4F).setBlockName("OrangeGoo");
        BlockInert = (new BlockInert(BlockInertID, 19)).setHardness(0.0F).setLightValue(0.1F).setBlockName("GreenGoo");
        BlockWaterEater = (new BlockWaterEater(BlockWaterEaterID, 4)).setHardness(0.0F).setLightValue(0.6F).setBlockName("BlueGoo");
        BlockAirEater = (new BlockAirEater(BlockAirEaterID, 15)).setHardness(0.0F).setLightValue(0.1F).setBlockName("WhiteGoo");
        BlockGreyEater = (new BlockGreyEater(BlockGreyEaterID, 18)).setHardness(0.0F).setLightValue(0.4F).setBlockName("GreyGoo");
        BlockMinerGoo = (new BlockMinerGoo(BlockMinerGooID, 6)).setHardness(0.0F).setLightValue(0.9F).setBlockName("MinerGoo");
        BlockElevatorGoo = (new BlockElevatorGoo(BlockElevatorGooID, 0)).setHardness(0.0F).setLightValue(0.5F).setBlockName("ElevatorGoo");
        BlockRapidMiner = (new BlockRapidMiner(BlockRapidMinerID, 3)).setHardness(0.0F).setLightValue(1.0F).setBlockName("Brown-RedGoo");
        BlockBubble = (new BlockBubble(BlockBubbleID, 2)).setHardness(0.0F).setLightValue(0.1F).setBlockName("White-GreenGoo");
        BlockOrangeRed = (new BlockOrangeRed(BlockOrangeRedID, 22)).setBlockUnbreakable().setLightValue(0.5F).setBlockName("OrangeRedGoo");
        BlockOrangePurple = (new BlockOrangePurple(BlockOrangePurpleID, 21)).setBlockUnbreakable().setLightValue(0.5F).setBlockName("OrangePurpleGoo");
        BlockOrangeWhite = (new BlockOrangeWhite(BlockOrangeWhiteID, 20)).setHardness(0.0F).setLightValue(0.5F).setBlockName("OrangeWhiteGoo");
        BlockRapidEater = (new BlockRapidEater(BlockRapidEaterID, 23)).setHardness(0.0F).setLightValue(0.5F).setBlockName("PurpleRedGoo");
        BlockBlack = (new BlockBlack(BlockBlackID, 16)).setHardness(0.0F).setLightValue(0.0F).setBlockName("Darkness");
        BlockGravityGoo = (new BlockGravityGoo(BlockGravityGooID, 5)).setHardness(0.0F).setLightValue(0.5F).setBlockName("Yellow Goo");
        BlockSubstrate = (new BlockSubstrate(BlockSubstrateID, 17).setHardness(0.0F).setLightValue(0.0F).setBlockName("fsgsfgyhewf"));
        BlockRapidWaterEater = new BlockRapidWaterEater(BlockRapidWaterEaterID, 7).setHardness(0.0F).setLightValue(0.0F).setBlockName("fsgsfg897yhewf");
      
    	GooPortal = (new GooPortal(GooPortalID, 56)).setHardness(0.9F).setLightValue(0.7F).setBlockName("GooPortal");
        Coprocessor = (new Coprocessor(CoprocessorID, 53 , false)).setHardness(0.9F).setLightValue(0.0F).setBlockName("Coprocessor");
        Programmer = (new Programmer(ProgrammerID, 1, false)).setHardness(0.9F).setLightValue(0.0F).setBlockName("Programmer");
        Assembler = (new Assembler(AssemblerID, 1, false)).setHardness(0.9F).setLightValue(0.0F).setBlockName("Assembler");
        Homogenizer = (new Homogenizer(HomogenizerID, 1, false)).setHardness(0.9F).setLightValue(0.0F).setBlockName("Homogenizer");
        Compiler = (new Compiler(CompilerID, 1, false)).setHardness(0.9F).setLightValue(0.0F).setBlockName("Compiler");
        EMPArray = (new EMPArray(EMPArrayID, 55)).setHardness(0.9F).setLightValue(1.0F).setBlockName("EMPArray");
        EMPArraySecondary = (new EMPArraySecondary(EMPArraySecondaryID, 55)).setHardness(0.9F).setLightValue(0.0F).setBlockName("EMPArraySecondary");
        
        
        ItemNanoLens = (new ItemNanolens(ItemNanoLensID)).setItemName("NanoLens");
        ItemNanoLathe = (new ItemNanoLathe(ItemNanoLatheID)).setItemName("NanoLathe");
        ItemModifierGrey = (new ItemModifierGrey(ItemModifierGreyID)).setItemName("erfgdsfsdfg");
        ItemModifierRed = (new ItemModifierRed(ItemModifierRedID)).setItemName("erfgsdfg");
        ItemModifierYellow = (new ItemModifierYellow(ItemModifierYellowID)).setItemName("Browadfanwgoo");
        ItemModifierPurple = (new ItemModifierPurple(ItemModifierPurpleID)).setItemName("aerew");
        ItemModifierOrange = (new ItemModifierOrange(ItemModifierOrangeID)).setItemName("ererw");
        ItemModifierBlue = (new ItemModifierBlue(ItemModifierBlueID)).setItemName("adfadf");
        ItemModifierWhite = (new ItemModifierWhite(ItemModifierWhiteID)).setItemName("adadd");
        ItemModifierGreen = (new ItemModifierGreen(ItemModifierGreenID)).setItemName("adfa");
        ItemModifierBrown = (new ItemModifierBrown(ItemModifierBrownID)).setItemName("Brownwgoo");
      
        
        ItemMatrixRed = (new ItemMatrixRed(ItemMatrixRedID)).setItemName("erfgsd4fg");
        ItemMatrixYellow = (new ItemMatrixYellow(ItemMatrixYellowID)).setItemName("Browad4fanwgoo");
        ItemMatrixPurple = (new ItemMatrixPurple(ItemMatrixPurpleID)).setItemName("ae4rew");
        ItemMatrixOrange = (new ItemMatrixOrange(ItemMatrixOrangeID)).setItemName("er4erw");
        ItemMatrixBlue = (new ItemMatrixBlue(ItemMatrixBlueID)).setItemName("adf4adf");
        ItemMatrixWhite = (new ItemMatrixWhite(ItemMatrixWhiteID)).setItemName("a4dadd");
        ItemMatrixGreen = (new ItemMatrixGreen(ItemMatrixGreenID)).setItemName("ad4fa");
        ItemMatrixBrown = (new ItemMatrixBrown(ItemMatrixBrownID)).setItemName("Bro4wnwgoo");
        ItemMatrixWhiteGreen = (new ItemMatrixWhiteGreen(ItemMatrixWhiteGreenID)).setItemName("a4dffdadd");
        ItemMatrixBrownRed = (new ItemMatrixBrownRed(ItemMatrixBrownRedID)).setItemName("a4ggffdadd");
        ItemMatrixBlueRed = (new ItemMatrixBlueRed(ItemMatrixBlueRedID)).setItemName("a4dghdadd");
        ItemMatrixOrangeRed = (new ItemMatrixOrangeRed(ItemMatrixOrangeRedID)).setItemName("a4wwghgdadd");
        ItemMatrixOrangeWhite = (new ItemMatrixOrangeRed(ItemMatrixOrangeWhiteID)).setItemName("a4ghrergdadd");
        ItemMatrixOrangePurple = (new ItemMatrixOrangeRed(ItemMatrixOrangePurpleID)).setItemName("a4ereghgdadd");
        ItemMatrixYellowRed = (new ItemMatrixYellowRed(ItemMatrixYellowRedID)).setItemName("Browad4fanwswegoo");
        ItemMatrixPurpleRed = (new ItemMatrixPurpleRed(ItemMatrixPurpleRedID)).setItemName("Browad4fasfnwswegoo");
        ItemMatrixGrey = (new ItemMatrixGrey(ItemMatrixGreyID)).setItemName("Browad4fasfnwserwerwegoo");
        ItemMatrixRainbow = (new ItemMatrixRainbow(ItemMatrixRainbowID)).setItemName("Rainbow Matrix");
        ItemMatrixGreenRed = (new ItemMatrixGreenRed(ItemMatrixGreenRedID)).setItemName("a4dghasddadd");
        
        
        OrangeWhiteRenderID = 65;
        
        dimensionID = dimHelper.getNextFreeDimId();
       // dimHelper.registerProviderType(dimensionID, GooWorldProvider.class, true);
        dimHelper.registerDimension(dimensionID,0);
        
		
       
		
     //   this.dimHelper.dimNames=new ArrayList();
        
      //  int dimensionID = DimensionManager.getNextFreeDimId();
        
   //    DimensionManager.registerProviderType(dimensionID, GooWorldProvider.class, true);
		// this.dimHelper.dimNames.add(0, dimensionID);
	//	DimensionManager.registerDimension(dimensionID,-1);
		
		
	//	 dimensionID = DimensionManager.getNextFreeDimId();
		//DimensionManager.registerProviderType(dimensionID, GooWorldProvider.class, true);
		// this.dimHelper.dimNames.add(0, dimensionID);
		//DimensionManager.registerDimension(dimensionID,0);
		
	//	 dimensionID = DimensionManager.getNextFreeDimId();
		//DimensionManager.registerProviderType(dimensionID, GooWorldProvider.class, true);
	//	 this.dimHelper.dimNames.add(2, dimensionID);
	//	DimensionManager.registerDimension(dimensionID,1);
        
        
       
       
       // dimensionIDBackUp = DimensionManager.getNextFreeDimId();
        //DimensionManager.registerProviderType(dimensionIDBackUp, GooWorldProvider.class, true);
        //DimensionManager.registerDimension(dimensionIDBackUp,dimensionIDBackUp);
        //DimensionManager.
        
        GameRegistry.registerWorldGenerator(worldGen);
        WorldType plagueWorld = new WorldType(8, "Plague World", 1);
        BiomeGenBase addedBiomes[] = WorldType.DEFAULT.getBiomesForWorldType();
        List biomelist = Arrays.asList(addedBiomes);
        int size = addedBiomes.length;

        while (size > 0)
        {
            plagueWorld.addNewBiome(addedBiomes[size - 1]);
            System.out.println(addedBiomes[size - 1].biomeName);
            --size;
        }

        this.allGooBlocks.add(BlockInertID);
        this.allGooBlocks.add(BlockCancerID);
        this.allGooBlocks.add(BlockTGDID);
        this.allGooBlocks.add(BlockTGDinertID);
        this.allGooBlocks.add(BlockBlackID);
        this.allGooBlocks.add(BlockGreyGooID);
        this.allGooBlocks.add(BlockGreyEaterID);
        this.allGooBlocks.add(BlockWallID);
        this.allGooBlocks.add(BlockAirEaterID);
        this.allGooBlocks.add(BlockWaterEaterID);
        this.allGooBlocks.add(BlockMinerGooID);
        this.allGooBlocks.add(BlockRapidMinerID);
        this.allGooBlocks.add(BlockRapidWaterEaterID);
        this.allGooBlocks.add(BlockBubbleID);
        this.allGooBlocks.add(BlockFreezerID);
        this.allGooBlocks.add(BlockOrangeRedID);
        this.allGooBlocks.add(BlockOrangeWhiteID);
        this.allGooBlocks.add(BlockOrangePurpleID);
        this.allGooBlocks.add(BlockGravityGooID);
        this.allGooBlocks.add(BlockCleanerID);
        this.allGooBlocks.add(BlockCancer2ID);
        this.allGooBlocks.add(BlockRapidEaterID);
        this.allGooBlocks.add(BlockRestorerID);

        Modifiers.add(ItemModifierBlueID + 256);
        Modifiers.add(ItemModifierGreenID + 256);
        Modifiers.add(ItemModifierPurpleID + 256);
        Modifiers.add(ItemModifierOrangeID + 256);
        Modifiers.add(ItemModifierWhiteID + 256);
        Modifiers.add(ItemModifierBrownID + 256);
        Modifiers.add(ItemModifierYellowID + 256);
        Modifiers.add(ItemModifierRedID + 256);
        Modifiers.add(ItemModifierGreyID + 256);
        Matrices.add(ItemMatrixBlueID + 256);
        Matrices.add(ItemMatrixGreenID + 256);
        Matrices.add(ItemMatrixPurpleID + 256);
        Matrices.add(ItemMatrixOrangeID + 256);
        Matrices.add(ItemMatrixWhiteID + 256);
        Matrices.add(ItemMatrixBrownID + 256);
        Matrices.add(ItemMatrixYellowID + 256);
        Matrices.add(ItemMatrixYellowRedID + 256);
        Matrices.add(ItemMatrixOrangeRedID + 256);
        Matrices.add(ItemMatrixBlueRedID + 256);
        Matrices.add(ItemMatrixPurpleRedID + 256);
        Matrices.add(ItemMatrixGreyID + 256);
        Matrices.add(ItemMatrixRedID + 256);
        Matrices.add(ItemMatrixWhiteGreenID + 256);
        Matrices.add(ItemMatrixBrownRedID + 256);
        Matrices.add(ItemMatrixOrangeWhiteID + 256);
        Matrices.add(ItemMatrixOrangePurpleID + 256);
        Matrices.add(ItemMatrixRainbowID + 256);

        mineTheseOnly.add(Block.gravel.blockID);
        mineTheseOnly.add(Block.stone.blockID);
        mineTheseOnly.add(Block.sand.blockID);
        mineTheseOnly.add(Block.sandStone.blockID);
        mineTheseOnly.add(Block.netherrack.blockID);
        mineTheseOnly.add(Block.slowSand.blockID);
        mineTheseOnly.add(Block.blockClay.blockID);
        
        
        gooNeverEatThese.add(EMPArray.blockID);
        gooNeverEatThese.add(EMPArraySecondary.blockID);
        gooNeverEatThese.add(BlockRestorer.blockID);
        gooNeverEatThese.add(BlockFreezer.blockID);
        gooNeverEatThese.add(BlockCleaner.blockID);
        gooNeverEatThese.add(BlockInert.blockID);
        gooNeverEatThese.add(BlockWall.blockID);
        gooNeverEatThese.add(BlockOrangeRed.blockID);
        gooNeverEatThese.add(Block.chest.blockID);
        gooNeverEatThese.add(Block.enderChest.blockID);
      //  gooNeverEatThese.add(Block.snow.blockID);
        
        cleanerList.add(BlockRestorerID);
        cleanerList.add(BlockCancer.blockID);
        cleanerList.add(BlockTGD.blockID);
        cleanerList.add(BlockTGDinert.blockID);
        cleanerList.add(BlockCancer2.blockID);
        cleanerList.add(BlockWaterEater.blockID);
        cleanerList.add(BlockGreyEater.blockID);
        cleanerList.add(BlockGreyGoo.blockID);
        cleanerList.add(BlockAirEater.blockID);
        cleanerList.add(BlockMinerGoo.blockID);
        cleanerList.add(BlockRapidMiner.blockID);
        cleanerList.add(BlockBubble.blockID);
        cleanerList.add(BlockWall.blockID);
        cleanerList.add(BlockBlack.blockID);
        cleanerList.add(BlockGravityGoo.blockID);
        cleanerList.add(BlockFreezer.blockID);
        cleanerList.add(BlockRapidWaterEater.blockID);
        cleanerList.add(BlockOrangeRed.blockID);
        cleanerList.add(BlockRapidEater.blockID);
        cleanerList.add(BlockOrangePurple.blockID);
        cleanerList.add(BlockOrangeWhite.blockID);
        
        
        NeverRestoreThese.add(this.BlockRestorerID);
        NeverRestoreThese.add(this.BlockInertID);
        NeverRestoreThese.add(this.EMPArrayID);
        NeverRestoreThese.add(this.EMPArraySecondaryID);
        NeverRestoreThese.add(Block.blockEmerald.blockID);
        NeverRestoreThese.add(Block.blockDiamond.blockID);
        NeverRestoreThese.add(Block.blockSteel.blockID);
        NeverRestoreThese.add(Block.blockGold.blockID);
        NeverRestoreThese.add(Block.blockLapis.blockID);
        NeverRestoreThese.add(Block.oreCoal.blockID);
        NeverRestoreThese.add(Block.oreIron.blockID);
        NeverRestoreThese.add(Block.oreDiamond.blockID);
        NeverRestoreThese.add(Block.oreEmerald.blockID);
        NeverRestoreThese.add(Block.oreGold.blockID);
        NeverRestoreThese.add(Block.oreLapis.blockID);
        NeverRestoreThese.add(Block.glowStone.blockID);
        NeverRestoreThese.add(Block.oreRedstone.blockID);
        NeverRestoreThese.add(Block.oreLapis.blockID);
        NeverRestoreThese.add(Block.doorSteel.blockID);
        NeverRestoreThese.add(Block.doorWood.blockID);
        NeverRestoreThese.add(Block.vine.blockID);
        NeverRestoreThese.add(Block.waterStill.blockID);
        NeverRestoreThese.add(Block.waterMoving.blockID);
        NeverRestoreThese.add(Block.lavaStill.blockID);
        NeverRestoreThese.add(Block.lavaMoving.blockID);
        NeverRestoreThese.add(Block.chest.blockID);
        NeverRestoreThese.add(Block.enderChest.blockID);
        NeverRestoreThese.add(Block.enchantmentTable.blockID);
        NeverRestoreThese.add(Block.bookShelf.blockID);
        NeverRestoreThese.add(-1);
        
   

        
        
        FreezerTexture = this.BlockFreezer.blockIndexInTexture;
        FreezerOpacity = this.BlockFreezer.lightOpacity[BlockFreezerID];
        FreezerLight = this.BlockFreezer.lightValue[BlockFreezerID];
        EntityRegistry.registerModEntity(EntityFallingGravityGoo.class, "FallingGravityGoo", BlockGravityGooID, this, 64, 20, true);
        EntityRegistry.registerGlobalEntityID(EntityTGDGolem.class, "TGD Golem", EntityRegistry.findGlobalUniqueEntityId());
        proxy.registerRenderers();
        proxy.loadTextures();
        NetworkRegistry.instance().registerGuiHandler(instance, guiHandler);
        
        GameRegistry.registerTileEntity(TileEntityGooPortal.class, "TileEntityGooPortal");
        GameRegistry.registerTileEntity(TileEntityCompiler.class, "TileEntityCompiler");
        GameRegistry.registerTileEntity(TileEntityAssembler.class, "TileEntityAssembler");
        GameRegistry.registerTileEntity(TileEntityHomogenizer.class, "TileEntityHomogenizer");
        GameRegistry.registerTileEntity(TileEntityProgrammer.class, "TileEntityProgrammer");
        GameRegistry.registerTileEntity(TileEntityBlack.class, "TileEntityBlack");
        GameRegistry.registerTileEntity(TileEntityEMPArray.class, "TileEntityEMPArray");
        GameRegistry.registerTileEntity(TileEntityCancer2.class, "TileEntityCancer2");
        TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
        TickRegistry.registerTickHandler(new CommonTickHandler(), Side.SERVER);
        GameRegistry.registerBlock(BlockSubstrate);
        GameRegistry.registerBlock(BlockRapidWaterEater);
        GameRegistry.registerBlock(Coprocessor);
        GameRegistry.registerBlock(BlockOrangeRed);
        GameRegistry.registerBlock(BlockOrangeWhite);
        GameRegistry.registerBlock(BlockOrangePurple);
        GameRegistry.registerBlock(BlockCancer);
        GameRegistry.registerBlock(BlockCancer2);
        GameRegistry.registerBlock(BlockTGD);
        GameRegistry.registerBlock(BlockTGDinert);
        GameRegistry.registerBlock(BlockMinerGoo);
        GameRegistry.registerBlock(BlockCleaner);
        GameRegistry.registerBlock(BlockInert);
        GameRegistry.registerBlock(BlockWall);
        GameRegistry.registerBlock(BlockWaterEater);
        GameRegistry.registerBlock(BlockAirEater);
        GameRegistry.registerBlock(BlockGreyEater);
        GameRegistry.registerBlock(BlockRapidMiner);
        GameRegistry.registerBlock(BlockFreezer);
        GameRegistry.registerBlock(BlockRapidEater);
        GameRegistry.registerBlock(EMPArray);
        GameRegistry.registerBlock(EMPArraySecondary);
        GameRegistry.registerBlock(BlockBubble);
        GameRegistry.registerBlock(BlockBlack);
        GameRegistry.registerBlock(BlockGravityGoo);
        GameRegistry.registerBlock(BlockGreyGoo);
        GameRegistry.registerBlock(Assembler);
        GameRegistry.registerBlock(Compiler);
        GameRegistry.registerBlock(Homogenizer);
        GameRegistry.registerBlock(Programmer);
        GameRegistry.registerBlock(BlockRestorer);
        GameRegistry.registerBlock(GooPortal);

        LanguageRegistry.addName(GooPortal, "GooPortalCore");
        LanguageRegistry.addName(Compiler, "Assembler");
        LanguageRegistry.addName(Homogenizer, "Homogenizer");
        LanguageRegistry.addName(Programmer, "Programmer");
        LanguageRegistry.addName(EMPArray, "EMP Array");
        LanguageRegistry.addName(Assembler, "Compiler");
        LanguageRegistry.addName(ItemNanoLens, "NanoLens");
        LanguageRegistry.addName(ItemNanoLathe, "NanoLathe");
        LanguageRegistry.addName(BlockRapidWaterEater, "Blue-Red Goo");
        LanguageRegistry.addName(Coprocessor, "Coprocessor");
        LanguageRegistry.addName(BlockOrangeRed, "Orange-Red Goo");
        LanguageRegistry.addName(BlockOrangePurple, "Orange-Purple Goo");
        LanguageRegistry.addName(BlockOrangeWhite, "Orange-White Goo");
        LanguageRegistry.addName(BlockGreyGoo, "Purple Goo");
        LanguageRegistry.addName(BlockGravityGoo, "Yellow Goo");
        LanguageRegistry.addName(BlockCancer, "Tumor");
        LanguageRegistry.addName(BlockRestorer, "Rainbow Goo");

        LanguageRegistry.addName(BlockCancer2, "Plague");
        LanguageRegistry.addName(BlockTGD, "The Great Destroyer");
        LanguageRegistry.addName(BlockTGDinert, "Inert Destroyer");
        LanguageRegistry.addName(BlockCleaner, "Red Goo");
        LanguageRegistry.addName(BlockInert, "Green Goo");
        LanguageRegistry.addName(BlockWall, "Orange Goo");
        LanguageRegistry.addName(BlockWaterEater, "Blue Goo");
        LanguageRegistry.addName(BlockGreyEater, "Grey Goo");
        LanguageRegistry.addName(BlockAirEater, "White Goo");
        LanguageRegistry.addName(BlockMinerGoo, "Brown Goo");
        LanguageRegistry.addName(BlockElevatorGoo, "Elevator Goo");
        LanguageRegistry.addName(BlockRapidMiner, "Brown-Red Goo");
        LanguageRegistry.addName(BlockBubble, "White-Green Goo");
        LanguageRegistry.addName(BlockFreezer, "Green-Red Goo");
        LanguageRegistry.addName(BlockBlack, "Darkness");
        LanguageRegistry.addName(ItemModifierBlue, "Water Affinity");
        LanguageRegistry.addName(ItemModifierRed, "Agressive Trait");
        LanguageRegistry.addName(ItemModifierOrange, "Linear Tendency");
        LanguageRegistry.addName(ItemModifierPurple, "Unstable Trait");
        LanguageRegistry.addName(ItemModifierWhite, "Gaseous Affinity");
        LanguageRegistry.addName(ItemModifierYellow, "Dense Trait");
        LanguageRegistry.addName(ItemModifierBrown, "Mineral Aversion");
        LanguageRegistry.addName(ItemModifierGreen, "Stable Trait");
        LanguageRegistry.addName(ItemModifierGrey, "Neutral Trait");
        LanguageRegistry.addName(ItemMatrixBlue, "Blue Nanite Matrix");
        LanguageRegistry.addName(ItemMatrixRed, "Red Nanite Matrix");
        LanguageRegistry.addName(ItemMatrixOrange, "Orange Nanite Matrix");
        LanguageRegistry.addName(ItemMatrixPurple, "Purple Nanite Matrix");
        LanguageRegistry.addName(ItemMatrixWhite, "White Nanite Matrix");
        LanguageRegistry.addName(ItemMatrixYellow, "Yellow Nanite Matrix");
        LanguageRegistry.addName(ItemMatrixBrown, "Brown Nanite Matrix");
        LanguageRegistry.addName(ItemMatrixGreen, "Green Nanite Matrix ");
        LanguageRegistry.addName(ItemMatrixOrangeRed, "Orange-Red Nanite Matrix");
        LanguageRegistry.addName(ItemMatrixWhiteGreen, "White-Green Nanite Matrix");
        LanguageRegistry.addName(ItemMatrixGreenRed, "Red-Green Nanite Matrix");
        LanguageRegistry.addName(ItemMatrixYellowRed, "Yellow-Red Nanite Matrix");
        LanguageRegistry.addName(ItemMatrixBrownRed, "Brown-Red Nanite Matrix");
        LanguageRegistry.addName(ItemMatrixBlueRed, "Blue-Red Nanite Matrix ");
        LanguageRegistry.addName(ItemMatrixGrey, "Grey Nanite Matrix ");
        LanguageRegistry.addName(ItemMatrixRainbow, "Rainbow Nanite Matrix ");

        LanguageRegistry.addName(ItemMatrixOrangeWhite, "Orange-White Nanite Matrix ");
        LanguageRegistry.addName(ItemMatrixOrangePurple, "OrangePurple Nanite Matrix ");
        LanguageRegistry.addName(BlockRapidEater, "Purple-Red Goo");
        LanguageRegistry.addName(ItemModifierGrey, "Neutral Modifier ");
        LanguageRegistry.addName(ItemMatrixPurpleRed, "Purple-Red Nanite Matrix");
        LanguageRegistry.addName(BlockSubstrate, "Substrate");
        
        GameRegistry.addShapelessRecipe(new ItemStack(this.ItemMatrixRainbow, 1), 
        		new ItemStack(ItemModifierGreen), 
        		new ItemStack(ItemModifierGrey), 
        		new ItemStack(ItemModifierBrown), 
        		new ItemStack(ItemModifierPurple), 
        		new ItemStack(ItemModifierYellow), 
        		new ItemStack(ItemModifierOrange), 
        		new ItemStack(ItemModifierWhite), 
        		new ItemStack(ItemModifierBlue), 
        		new ItemStack(ItemModifierRed));
        
        GameRegistry.addRecipe(new ItemStack(GooPortal, 1), new Object[]
                {
                    "iyi", "yxy", "iyi", 'x', EMPArray,  'y', ItemNanoLens, 'i', BlockSubstrate
                });
        GameRegistry.addRecipe(new ItemStack(BlockGreyEater, 1), new Object[]
                {
                    "xx", "xx", 'x', BlockSubstrate
                });
        GameRegistry.addRecipe(new ItemStack(ItemNanoLens, 4), new Object[]
                {
                    "igi", "ldl", "igi", 'i', Item.ingotIron, 'g', Block.glass, 'l', Item.ingotGold, 'd', Item.diamond
                });
        GameRegistry.addRecipe(new ItemStack(ItemNanoLathe, 1), new Object[]
                {
                    "i i", "ldl", "igi", 'i', Item.ingotIron, 'g', Item.diamond, 'l', Item.ingotGold, 'd', Item.diamond
                });
        GameRegistry.addRecipe(new ItemStack(Compiler, 1), new Object[]
                {
                    "iyi", " x ", "ili", 'x', BlockSubstrate, 'z', Block.workbench, 'l', ItemNanoLathe, 'y', ItemNanoLens, 'i', Item.ingotIron
                });
        GameRegistry.addRecipe(new ItemStack(Programmer, 1), new Object[]
                {
                    "iyi", " x ", "ili", 'x', BlockSubstrate, 'z', Block.workbench, 'l', ItemNanoLens, 'y', ItemNanoLens, 'i', Item.ingotIron
                });
        GameRegistry.addRecipe(new ItemStack(Coprocessor, 4), new Object[]
                {
                    "qiq", "izi", "qiq", 'i', BlockSubstrate, 'z', ItemNanoLens, 'q', Item.ingotIron
                });
        GameRegistry.addRecipe(new ItemStack(Assembler, 1), new Object[]
                {
                    " y ", "xzx", "iii", 'x', BlockSubstrate, 'z', Block.workbench, 'y', ItemNanoLens, 'i', Item.ingotIron
                });
        GameRegistry.addRecipe(new ItemStack(Homogenizer, 1), new Object[]
                {
                    "iyi", "izi", "ixi", 'x', Block.chest , 'y', Block.pistonBase, 'z', ItemNanoLathe, 'i', Item.ingotIron
                });
        GameRegistry.addRecipe(new ItemStack(EMPArray, 4), new Object[]
                {
                    "iyi", "yxy", "iyi", 'x', Programmer , 'y', BlockTGDinert, 'i', BlockSubstrate
                });
    }

    @PostInit
    public void PostInit(FMLPostInitializationEvent event)
    {
    	
    }

    @SideOnly(Side.CLIENT)
    public boolean RenderCustomBlock(RenderBlocks renderblock, IBlockAccess iblockaccess, int i, int j, int k, Block block, int l)
    {
        if (l == this.OrangeWhiteRenderID)
        {
            return RenderBlockOrangeWhite(block, i, j, k, renderblock);
        }
        else
        {
            return false;
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean RenderBlockOrangeWhite(Block block, int i, int j, int k, RenderBlocks renderblocks)
    {
        block.setBlockBounds(0.0F, 0.0F, 0.0F, 1F, 1F, 1F);
        renderblocks.renderStandardBlock(block, i, j, k);
        block.setBlockBounds(0.2F, 0.5F, 0.2F, 0.8F, 1.0F, 0.8F);
        renderblocks.renderStandardBlock(block, i, j, k);
        return true;
    }
    
   

    public static int getTimeToFinish(World world, int x, int y, int z, int baseTime, int subtracter)
    {
        int finishTime;
        int numberOfCoprocessors = 1;;
        int i1 = -1;
        int j1 = -1;
        int l = -1;

        for (; i1 < 2; i1++)
        {
            for (; j1 < 2; j1++)
            {
                for (; l < 2; l++)
                {
                    if (world.getBlockId(x + l, y + i1, z + j1) == mod_GreyGoo.Coprocessor.blockID)
                    {
                        ++numberOfCoprocessors;
                        continue;
                    }
                }

                l = -1;
            }

            j1 = -1;
        }

        if (numberOfCoprocessors > 25)
        {
            numberOfCoprocessors = 25;
        }

        int divisor = (numberOfCoprocessors / 2);

        if (divisor < 1)
        {
            divisor = 1;
        }

        finishTime = (baseTime / divisor);
        finishTime = finishTime - (numberOfCoprocessors / 2) * subtracter;
        return finishTime;
    }

    public static int countSurroundingBlock(World world, int x, int y, int z, int idToFind)
    {
        int count = 0;;
        int i1 = -1;
        int j1 = -1;
        int l = -1;

        for (; i1 < 2; i1++)
        {
            for (; j1 < 2; j1++)
            {
                for (; l < 2; l++)
                {
                    if (world.getBlockId(x + l, y + i1, z + j1) == idToFind)
                    {
                        ++count;
                        continue;
                    }
                }

                l = -1;
            }

            j1 = -1;
        }

        return count;
    }

    public static boolean isGooActive(int blockID, World world)
    {
    	//proxy.printStringClient(String.valueOf(mystcraftAgeOnly));
        if (mod_GreyGoo.inactiveGooList.containsKey(blockID))
        {
        	if((Integer)inactiveGooList.get(blockID)==world.provider.dimensionId)
        	{
            return false;
        	}
        }

        if (!mod_GreyGoo.GooActive)
        {
            return false;
        }
        if(mod_GreyGoo.mystcraftAgeOnly&&mod_GreyGoo.mystcraftAgeBlacklist.contains(world.provider.dimensionId))
        {
        	
        	
        	return false;
        		
        }
        else
        {
            return true;
        }
    }

    
    public void DidBloomchanger()
    {
        if (TGDbloom == false)
        {
            this.TGDbloom = true;
        }
    }
}
