package StevenGreyGoo.mod_GreyGoo;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.common.DimensionManager;

public class GooChunkProvider implements IChunkProvider
{
        private World worldObj;
        private Random random;
        private final boolean useStructures;
        private MapGenVillage villageGen;

        public GooChunkProvider(World par1World, long par2, boolean par4)
        {
                villageGen = new MapGenVillage();
                worldObj = par1World;
                useStructures = par4;
                random = new Random(par2);
        }

        private void generate(byte par1ArrayOfByte[])
        {
                int i = par1ArrayOfByte.length / 256;

                for (int j = 0; j < 16; j++)
                {
                        for (int k = 0; k < 16; k++)
                        {
                                for (int l = 0; l < i; l++)
                                {
                                        int i1 = 0;

                                        if (l == 0)
                                        {
                                                i1 = Block.bedrock.blockID;
                                        }
                                        else if (l <= 2)
                                        {
                                                i1 = Block.dirt.blockID;
                                        }
                                        else if (l == 3)
                                        {
                                                i1 = Block.grass.blockID;
                                        }

                                        par1ArrayOfByte[j << 11 | k << 7 | l] = (byte)i1;
                                }
                        }
                }
        }

        /**
         * Creates an empty chunk ready to put data from the server in
         */
        public Chunk loadChunk(int par1, int par2)
        {
                return provideChunk(par1, par2);
        }

        /**
         * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
         * specified chunk from the map seed and chunk seed
         */
        public Chunk provideChunk(int par1, int par2)
        {
        	GooDimensionHelper help =mod_GreyGoo.instance.dimHelper;
        	//int num =help.dimListgetter(worldObj.provider.dimensionId, 1);
        	return DimensionManager.getWorld(0).getChunkProvider().provideChunk(par1, par2);
               
        }

        /**
         * Checks to see if a chunk exists at x, y
         */
        public boolean chunkExists(int par1, int par2)
        {
                return true;
        }

        /**
         * Populates chunk with ores etc etc
         */
        public void populate(IChunkProvider par1IChunkProvider, int par2, int par3)
        {
        	GooDimensionHelper help =mod_GreyGoo.instance.dimHelper;
        	//int num =help.dimListgetter(worldObj.provider.dimensionId, 1);
        	 DimensionManager.getWorld(0).getChunkProvider().populate(par1IChunkProvider, par2, par3);
        }

        /**
         * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
         * Return true if all chunks have been saved.
         */
        public boolean saveChunks(boolean par1, IProgressUpdate par2IProgressUpdate)
        {
                return true;
        }

        /**
         * Unloads the 100 oldest chunks from memory, due to a bug with chunkSet.add() never being called it thinks the list
         * is always empty and will not remove any chunks.
         */
        public boolean unload100OldestChunks()
        {
                return false;
        }

        /**
         * Returns if the IChunkProvider supports saving.
         */
        public boolean canSave()
        {
                return true;
        }

        /**
         * Converts the instance data to a readable string.
         */
        public String makeString()
        {
                return "FlatLevelSource";
        }

        /**
         * Returns a list of creatures of the specified type that can spawn at the given location.
         */
        public List getPossibleCreatures(EnumCreatureType par1EnumCreatureType, int par2, int par3, int par4)
        {
        	GooDimensionHelper help =mod_GreyGoo.instance.dimHelper;
        	//int num =help.dimListgetter(worldObj.provider.dimensionId, 1);
        	return DimensionManager.getWorld(0).getChunkProvider().getPossibleCreatures(par1EnumCreatureType, par2, par3, par4);
        }

        /**
         * Returns the location of the closest structure of the specified type. If not found returns null.
         */
        public ChunkPosition findClosestStructure(World par1World, String par2Str, int par3, int i, int j)
        {
                return null;
        }

		@Override
		public int getLoadedChunkCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		

		@Override
		public void recreateStructures(int var1, int var2) {
			// TODO Auto-generated method stub
			
		}

		

		
		

		
		
}
