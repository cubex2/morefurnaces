package cubex2.mods.morefurnaces.proxies;

import cubex2.mods.morefurnaces.client.gui.GuiMoreFurnace;
import cubex2.mods.morefurnaces.inventory.ContainerIronFurnace;
import cubex2.mods.morefurnaces.tileentity.TileEntityIronFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler
{

    public void registerRenderInformation()
    {

    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        if (te != null && te instanceof TileEntityIronFurnace)
            return GuiMoreFurnace.GUI.buildGui(player.inventory, (TileEntityIronFurnace) te);
        else
            return null;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        if (te != null && te instanceof TileEntityIronFurnace)
        {
            TileEntityIronFurnace furnace = (TileEntityIronFurnace) te;
            return new ContainerIronFurnace(player.inventory, furnace, furnace.getType());
        } else
            return null;
    }

    public World getClientWorld()
    {
        return null;
    }

}
