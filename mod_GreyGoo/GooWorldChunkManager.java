package StevenGreyGoo.mod_GreyGoo;


import java.util.List;
import java.util.Random;

import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraftforge.common.DimensionManager;

public class GooWorldChunkManager extends WorldChunkManager
{
        private GenLayer genBiomes;

        /** A GenLayer containing the indices into BiomeGenBase.biomeList[] */
        private GenLayer biomeIndexLayer;

        /** The BiomeCache object for this world. */
        private BiomeCache biomeCache;

        /** A list of biomes that the player can spawn in. */
        private List biomesToSpawnIn;
        
        public World world;

        protected GooWorldChunkManager()
        {
             
                
        }

        public GooWorldChunkManager(long par1, WorldType par3WorldType)
        {
               
        }

        public GooWorldChunkManager(World par1World)
        {
                this(par1World.getSeed(), par1World.getWorldInfo().getTerrainType());
                world=par1World;
        }

        /**
         * Gets the list of valid biomes for the player to spawn in.
         */
        public List getBiomesToSpawnIn()
        {
                return biomesToSpawnIn;
        }

        /**
         * Returns the BiomeGenBase related to the x, z position on the world.
         */
        public BiomeGenBase getBiomeGenAt(int par1, int par2)
        {
        	GooDimensionHelper help =mod_GreyGoo.instance.dimHelper;
        	//int num =help.dimListgetter(world.provider.dimensionId, 1);
        	return DimensionManager.getWorld(0).getWorldChunkManager().getBiomeGenAt(par1, par2);
             
        }

        /**
         * Returns a list of rainfall values for the specified blocks. Args: listToReuse, x, z, width, length.
         */
        public float[] getRainfall(float par1ArrayOfFloat[], int par2, int par3, int par4, int par5)
        {
        	GooDimensionHelper help =mod_GreyGoo.instance.dimHelper;
        	//int num =help.dimListgetter(world.provider.dimensionId, 1);
        	return DimensionManager.getWorld(0).getWorldChunkManager().getRainfall(par1ArrayOfFloat, par2, par3, par4, par5);
        }

        /**
         * Return an adjusted version of a given temperature based on the y height
         */
        public float getTemperatureAtHeight(float par1, int par2)
        {
                return par1;
        }

        /**
         * Returns a list of temperatures to use for the specified blocks.  Args: listToReuse, x, y, width, length
         */
        public float[] getTemperatures(float par1ArrayOfFloat[], int par2, int par3, int par4, int par5)
        {
        	GooDimensionHelper help =mod_GreyGoo.instance.dimHelper;
        //	int num =help.dimListgetter(world.provider.dimensionId, 1);
        	return DimensionManager.getWorld(0).getWorldChunkManager().getTemperatures(par1ArrayOfFloat, par2, par3, par4, par5);
        }

        /**
         * Returns an array of biomes for the location input.
         */
        public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase par1ArrayOfBiomeGenBase[], int par2, int par3, int par4, int par5)
        {
        	GooDimensionHelper help =mod_GreyGoo.instance.dimHelper;
        	//int num =help.dimListgetter(world.provider.dimensionId, 1);
        	return DimensionManager.getWorld(0).getWorldChunkManager().getBiomesForGeneration(par1ArrayOfBiomeGenBase, par2, par3, par4, par5);
        }

        /**
         * Returns biomes to use for the blocks and loads the other data like temperature and humidity onto the
         * WorldChunkManager Args: oldBiomeList, x, z, width, depth
         */
        public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase par1ArrayOfBiomeGenBase[], int par2, int par3, int par4, int par5)
        {
        	GooDimensionHelper help =mod_GreyGoo.instance.dimHelper;
        	//int num =help.dimListgetter(world.provider.dimensionId, 1);
        	return DimensionManager.getWorld(0).getWorldChunkManager().loadBlockGeneratorData(par1ArrayOfBiomeGenBase, par2, par3, par4, par5);
        }

        /**
         * Return a list of biomes for the specified blocks. Args: listToReuse, x, y, width, length, cacheFlag (if false,
         * don't check biomeCache to avoid infinite loop in BiomeCacheBlock)
         */
        public BiomeGenBase[] getBiomeGenAt(BiomeGenBase par1ArrayOfBiomeGenBase[], int par2, int par3, int par4, int par5, boolean par6)
        {
        	GooDimensionHelper help =mod_GreyGoo.instance.dimHelper;
        	//int num =help.dimListgetter(world.provider.dimensionId, 1);
        	return DimensionManager.getWorld(0).getWorldChunkManager().getBiomeGenAt(par1ArrayOfBiomeGenBase, par2, par3, par4, par5, par6);
        }

        /**
         * checks given Chunk's Biomes against List of allowed ones
         */
        public boolean areBiomesViable(int par1, int par2, int par3, List par4List)
        {
               
                        
                                return false;
                        
             
        }

        /**
         * Finds a valid position within a range, that is once of the listed biomes.
         */
        public ChunkPosition findBiomePosition(int par1, int par2, int par3, List par4List, Random par5Random)
        {
        	GooDimensionHelper help =mod_GreyGoo.instance.dimHelper;
        	//int num =help.dimListgetter(world.provider.dimensionId, 1);
        	return DimensionManager.getWorld(0).getWorldChunkManager().findBiomePosition(par1, par2, par3, par4List, par5Random);
        }

        /**
         * Calls the WorldChunkManager's biomeCache.cleanupCache()
         */
        public void cleanupCache()
        {
        	
        	GooDimensionHelper help =mod_GreyGoo.instance.dimHelper;
       // 	int num =help.dimListgetter(world.provider.dimensionId, 1);
        	// DimensionManager.getWorld(num).getWorldChunkManager().cleanupCache();
        }
}