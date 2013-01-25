package StevenGreyGoo.mod_GreyGoo;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class GreyGooTGDworldgen implements IWorldGenerator
{
    private int minableBlockId;
    private int numberOfBlocks;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world,	IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
    {
        int i = chunkX * 16;
        int j = mod_GreyGoo.bloomheight;
        int k = chunkZ * 16;
        int l = random.nextInt(mod_GreyGoo.TGDworldgenfrequecy * 3);
        int l2 = random.nextInt(mod_GreyGoo.TGDworldgenfrequecy * 2);

        if (3 == l)
        {
            mod_GreyGoo.instance.TGDbloom = false;
            mod_GreyGoo.instance.proxy.readNBTFromFile(world);

            if (mod_GreyGoo.instance.TGDbloom)
            {
                world.setBlockWithNotify(i, j, k, mod_GreyGoo.BlockTGD.blockID);
            }
        }

        if (3 == l2)
        {
            mod_GreyGoo.instance.TGDbloom = false;
            mod_GreyGoo.instance.proxy.readNBTFromFile(world);

            if (mod_GreyGoo.instance.TGDbloom)
            {
                world.setBlockWithNotify(i, 30, k, mod_GreyGoo.BlockTGD.blockID);
            }
        }

        if (world.getWorldInfo().getTerrainType().getWorldTypeName() == "Plague World")
        {
            if (random.nextInt(900) == 1)
            {
                mod_GreyGoo.instance.proxy.printStringClient("The Great Destroyer has awoken...");
                world.setBlockWithNotify(i, 30, k, mod_GreyGoo.BlockTGD.blockID);
            }

            if (random.nextInt(2) == 1)
            {
                //for (int c = 0; c < 20; c++)
                {
                    int rand = random.nextInt(16) + 60;

                    if (world.getBlockId(i, rand + 1, k) == 0 && world.getBlockId(i, rand, k) != 0)
                    {
                        if (mod_GreyGoo.instance.debugMode)
                        {
                            mod_GreyGoo.instance.proxy.printStringClient("Plague Spawn");
                        }

                        world.setBlockWithNotify(i, rand, k, mod_GreyGoo.BlockCancer2.blockID);
                        world.scheduleBlockUpdate(i, rand, k, mod_GreyGoo.BlockCancer2ID, 60);
                    }
                }
            }

            if (random.nextInt(200) == 1)
            {
                if (mod_GreyGoo.instance.debugMode)
                {
                    mod_GreyGoo.instance.proxy.printStringClient("Darkness spawn");
                }

                world.setBlockWithNotify(i, 60, k, mod_GreyGoo.BlockBlack.blockID);
            }

            if (random.nextInt(150) == 1)
            {
                int rand = random.nextInt(10) + 50;

                if (world.getBlockId(i, rand - 1, k) != 0)
                {
                    if (mod_GreyGoo.instance.debugMode)
                    {
                        mod_GreyGoo.instance.proxy.printStringClient("Tumor Spawn");
                    }

                    world.setBlockWithNotify(i, rand, k, mod_GreyGoo.BlockCancer.blockID);
                    world.scheduleBlockUpdate(i, rand, k, mod_GreyGoo.BlockCancer.blockID, 60);
                }
            }
        }
    }
}
