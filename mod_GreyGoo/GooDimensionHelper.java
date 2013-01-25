package StevenGreyGoo.mod_GreyGoo;

import java.util.HashMap;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.DimensionManager;

public class GooDimensionHelper extends DimensionManager
{
	
	/**
	public GooDimensionHelper()
	{
		
		
	}
	public HashMap dimensionList=new HashMap();
	
	public HashMap dimEntry(int dimID, int linkToID, int isBackup,int providerID)
	{
		HashMap hash=new HashMap();
		hash.put(0, dimID);
		hash.put(1,linkToID);
		hash.put(2, isBackup);
		hash.put(3, providerID);
		return hash;
		
	}
	public int dimListgetter(int Dimension, int slot)
	{
		HashMap hash=(HashMap) this.dimensionList.get(Dimension);
		return (Integer) hash.get(slot);
	}
	**/
	public int rainbowSource;
	public int gooPortalDestination;
	public int gooPortalPrevious;
//	public HashMap dimLink=new HashMap(); 
	public HashMap loadedDims=new HashMap(); 
	public String saveDataPath;
	public HashMap restoreCache=new HashMap(); 
	

	public int dimIndex=-10;
	public World holder;
	public Chunk currentFreshChunk=null;
	int cfcChunkx;
	int cfcChunkz;
	int blockMetaData;
	public Chunk generateFreshChunk(World world, int i, int j, int k)
	{
		WorldProvider provider= world.provider;
		//String classname=world.getChunkProvider().getClass().getName();
		//IChunkProvider chunkmaker =  (provider.terrainType == provider.terrainType.FLAT ? new ChunkProviderFlat(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled(), world.getWorldInfo().func_82571_y()) : new ChunkProviderGenerate(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled()));
		IChunkProvider chunkmaker=provider.createChunkGenerator();
		Chunk chunk=chunkmaker.provideChunk(i >> 4, k >> 4);
		chunkmaker.populate(chunkmaker, i >> 4, k >> 4);
		
		//World test= new World();
		cfcChunkx=i >> 4;
		cfcChunkz=k >> 4;
		String key =(String.valueOf(cfcChunkx)+String.valueOf(cfcChunkz));
		restoreCache.put(key,chunk);
			return chunk;
		
		//world.provider.getChunkProvider();
		
	}
	public int getBlockIDfromFreshChunk(World world, int i, int j, int k)
	{
		if(!(world.provider.dimensionId==0||world.provider.dimensionId==mod_GreyGoo.dimensionID))
		{
		if(currentFreshChunk==null||i >> 4!=cfcChunkx||k >> 4!=cfcChunkz)
		{
			if(restoreCache.containsKey((String.valueOf(i >> 4)+String.valueOf(k >> 4))))
					{
					currentFreshChunk=(Chunk)restoreCache.get((String.valueOf(i >> 4)+String.valueOf(k >> 4)));
					}
			else
			{
			
			currentFreshChunk=generateFreshChunk( world,  i,  j,  k);
			} 
		}
		int x=(i % 16)< 0 ? (i % 16)+16 : (i % 16);
		int y=j;
		int z=(k % 16)< 0 ? (k % 16)+16 : (k % 16);
		
		
		
		
	
		
			this.blockMetaData=currentFreshChunk.getBlockMetadata(x, y, z);
			return currentFreshChunk.getBlockID(x, y, z);
			
	
		
		
		
		}
		else if(world.provider.dimensionId==0)
		{
			
			try
			{
				World backupWorld=getWorld((mod_GreyGoo.dimensionID));
				this.blockMetaData=backupWorld.getBlockMetadata(i, j, k);
				return backupWorld.getBlockId(i, j, k);
			}
			catch(Exception e)
			{
				
			
				initDimension(mod_GreyGoo.dimensionID);
				createProviderFor(mod_GreyGoo.dimensionID);
				World backupWorld=getWorld((mod_GreyGoo.dimensionID));
				this.blockMetaData=backupWorld.getBlockMetadata(i, j, k);
				return backupWorld.getBlockId(i, j, k);
				
				
			
			}
			
		}
		else
		{
			
			World backupWorld=getWorld((0));
			this.blockMetaData=backupWorld.getBlockMetadata(i, j, k);
			return backupWorld.getBlockId(i, j, k);
			
		}
	}
	/**
	public WorldServer gooTeleport(int starterdim)
	{
		loadBackupDim(getWorld(starterdim));
		HashMap hash = (HashMap) dimensionList.get(starterdim);
		
		//mod_GreyGoo.proxy.printStringClient(String.valueOf(hash.get(0)));

		//mod_GreyGoo.proxy.printStringClient(String.valueOf(hash.get(1)));
		//initDimension(starterdim);
		//createProviderFor((Integer)hash.get(1));
	
		try
		{
			//getWorld((Integer)hash.get(1)).provider.getSeed();
		}
		catch(Exception e)
		{
			
			//int dimensionID=this.dimListgetter(starterdim,1);
		//	registerDimension(dimensionID,getProviderType(this.dimListgetter(starterdim,3)));
			initDimension(dimensionID);
			createProviderFor(dimensionID);
			
			//initDimension((Integer)hash.get(1));
			//createProviderFor((Integer)hash.get(1));
		}
		return getWorld((Integer)hash.get(1));
		
		
		
		
		
	}
	/**
	 * 
	 *
	 */
	/**
	public void initBackups()
	{
		mod_GreyGoo.hasLoadedBackupDims=true;
		this.load();
		if(!this.dimensionList.isEmpty())
		{
			Set allDimIds=dimensionList.keySet();
			
			Iterator itr =allDimIds.iterator();
			while(itr.hasNext())
			{
				HashMap dimInfo=(HashMap)dimensionList.get(itr.next());
				if((Integer)dimInfo.get(2)==1)
				{
					this.getNextFreeDimId();
					registerDimension((Integer)dimInfo.get(0),(Integer)dimInfo.get(3));
					initDimension((Integer)dimInfo.get(0));
					

				}
				
			}
			
			
			
			
			
		}
		
	}
	**/
	/**
	public int gooTeleportInt(int starterdim)
	{
		loadBackupDim(getWorld(starterdim));
		HashMap hash = (HashMap) dimensionList.get(starterdim);
		
		mod_GreyGoo.proxy.printStringClient(String.valueOf(hash.get(0)));
		//initDimension(starterdim);
		mod_GreyGoo.proxy.printStringClient(String.valueOf(hash.get(1)));
		//createProviderFor((Integer)hash.get(1));
		try
		{
			getWorld((Integer)hash.get(1)).provider.getSeed();
		}
		catch(Exception e)
		{
			int dimensionID=this.dimListgetter(starterdim,0);
		//	registerDimension(dimensionID,getProviderType(this.dimListgetter(starterdim,3)));
			initDimension(dimensionID);
			createProviderFor(dimensionID);
		}
		
		return ((Integer)hash.get(1));
		
		
		
	}
	
	**/
	
	/**
	public void save()
	{
		World world=FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[0];
			
			FileOutputStream saveFile = null;
			try
			{
				
				 File dataStore = world.getSaveHandler().getMapFileFromName("idcounts");
		            String dirFolder = dataStore.getCanonicalPath();
				
		          
		         dirFolder = dirFolder.replace("idcounts.dat", "GooDimData");
		         saveFile = new FileOutputStream(dirFolder);
		         ObjectOutputStream save = new ObjectOutputStream(saveFile);
		         save.writeObject((HashMap)this.dimensionList);
		         save.close();
		        // System.out.println(String.valueOf(this.dimensionList));
			}
			catch(Exception e)
			{
				 e.printStackTrace();
				System.out.println("Could not save data-- SEVERE");
			}
			
		         
			
			

	}
	public void load()
	{
		
		FileInputStream saveFile = null;
		
		World world=FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[0];
		try
		{
			
			 File dataStore = world.getSaveHandler().getMapFileFromName("idcounts");
	            String dirFolder = dataStore.getCanonicalPath();
	           
	         dirFolder = dirFolder.replace("idcounts.dat", "GooDimData");
	         saveFile = new FileInputStream(dirFolder);
	         ObjectInputStream save = new ObjectInputStream(saveFile);
	         dimensionList.putAll((HashMap)save.readObject());
	         save.close();
	       //  System.out.println(String.valueOf(this.dimensionList));

	        
		}
		catch(Exception e)
		{
			this.dimensionList.clear();
			 e.printStackTrace();
			//System.out.println("Could not read data-- SEVERE");
		}
		 	}
	**/
	
	/**
	public void loadBackupDim(WorldServer world)
	{
			this.load();
		
			if(!dimensionList.containsKey((world.provider.dimensionId))&&!world.isRemote||dimensionList.isEmpty()) //checks to see if the world has already been processed, ie, has a backup created and assigned to it. 
			{
				
				
				int dimensionID;
				
				
			
					//creates a dimension and pairs it if it doenst exist.
					mod_GreyGoo.proxy.printStringClient("GeneratedBackup");
					dimensionID = getNextFreeDimId();
					Class className = world.provider.getClass();
					
					dimensionList.put(world.provider.dimensionId,this.dimEntry(world.provider.dimensionId, dimensionID, 0,getProviderType(world.provider.dimensionId)));
					dimensionList.put(dimensionID,this.dimEntry(dimensionID,world.provider.dimensionId, 1,getProviderType(world.provider.dimensionId)));
					
					
					registerProviderType(dimensionID, GooWorldProvider.class, true);
					registerDimension(dimensionID,dimensionID);
					
					
					
					

					//initiateDim(dimensionID,world);
					//createProviderFor(dimensionID);
					//getWorld(dimensionID).getWorldInfo().setWorldName("GooDimBackUpfor"+String.valueOf(world.provider.dimensionId));
					
					//ReflectionHelper.setPrivateValue(WorldInfo.class,getWorld(dimensionID).getWorldInfo(),(world.provider.getSeed()),"randomSeed","field_76100_a");
				//	getWorld(dimensionID)
					//getWorld(dimensionID).getWorldInfo().setWorldName("GooDimBackUpfor"+String.valueOf(world.provider.dimensionId));
					dimensionList.put(world.provider.dimensionId,this.dimEntry(world.provider.dimensionId, dimensionID, 0,getProviderType(world.provider.dimensionId)));
					dimensionList.put(dimensionID,this.dimEntry(dimensionID,world.provider.dimensionId, 1,getProviderType(world.provider.dimensionId)));
					this.save();
				
				}
			else 
			{	
				int dimensionID=this.dimListgetter(world.provider.dimensionId,1);
				try
				{
					//System.out.println("Should Load?");
					boolean bob =!getWorld(dimensionID).isRemote;
					
				}
				catch(Exception e)
				{
					System.out.println("Loading");
				//	registerDimension(dimensionID,getProviderType(world.provider.dimensionId));
					initDimension(dimensionID);
					createProviderFor(dimensionID);
				}
			}
				
				//HashMap hash = (HashMap) dimensionList.get(world.getWorldInfo().getDimension());
								

			
				
				
				
				
	     
	}
	**/
	
	/**
	public void createBackups()
	{
		/**
		dimNames=new ArrayList();
		int dimensionID = getNextFreeDimId();
		dimNames.add(0, dimensionID);
		registerDimension(dimensionID,-1);
		
		 dimensionID = getNextFreeDimId();
		dimNames.add(1, dimensionID);
		registerDimension(dimensionID,0);
		
		 dimensionID = getNextFreeDimId();
		dimNames.add(2, dimensionID);
		registerDimension(dimensionID,1);
		
		/**
		dimensionID = getNextFreeDimId();
		registerProviderType(dimensionID, GooWorldProvider.class, true);
		dimNames.add(1, dimensionID);
		registerDimension(dimensionID,dimensionID);
		
		dimensionID = getNextFreeDimId();
		registerProviderType(dimensionID, GooWorldProvider.class, true);
		dimNames.add(2, dimensionID);
		registerDimension(dimensionID,dimensionID);
		**/
		
	//	Integer[] dimArray=getIDs();
		/**
		int dimIndex=dimArray.length-1;
		while(dimIndex>=0)
		{
			if(getWorld(dimIndex).getWorldInfo().getWorldName()!="GooWorldBackupfor")
			{
				dimensionID = getNextFreeDimId();
				registerProviderType(dimensionID, GooWorldProvider.class, true);
				registerDimension(dimensionID,dimIndex);
				dimNames.add(dimIndex, dimensionID);
			}
	     dimIndex--;
		}
		
		}

	
	**/
	
	
	
	
	
	
	
	
	
	
	
}