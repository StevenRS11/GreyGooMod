package StevenGreyGoo.mod_GreyGoo;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tile_entity = world.getBlockTileEntity(x, y, z);

        if (tile_entity instanceof TileEntityAssembler)
        {
            return new ContainerAssembler(player.inventory, (TileEntityAssembler) tile_entity);
        }

        if (tile_entity instanceof TileEntityCompiler)
        {
            return new ContainerCompiler(player.inventory, (TileEntityCompiler) tile_entity);
        }

        if (tile_entity instanceof TileEntityHomogenizer)
        {
            return new ContainerHomogenizer(player.inventory, (TileEntityHomogenizer) tile_entity);
        }

        if (tile_entity instanceof TileEntityProgrammer)
        {
            return new ContainerProgrammer(player.inventory, (TileEntityProgrammer) tile_entity);
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tile_entity = world.getBlockTileEntity(x, y, z);

        if (tile_entity instanceof TileEntityAssembler)
        {
            return new GuiAssembler(player.inventory, (TileEntityAssembler) tile_entity);
        }

        if (tile_entity instanceof TileEntityCompiler)
        {
            return new GuiCompiler(player.inventory, (TileEntityCompiler) tile_entity);
        }

        if (tile_entity instanceof TileEntityHomogenizer)
        {
            return new GuiHomogenizer(player.inventory, (TileEntityHomogenizer) tile_entity);
        }

        if (tile_entity instanceof TileEntityProgrammer)
        {
            return new GuiProgrammer(player.inventory, (TileEntityProgrammer) tile_entity);
        }

        return null;
    }
}