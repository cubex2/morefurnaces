package cubex2.mods.morefurnaces.proxies;

import cpw.mods.fml.common.network.IGuiHandler;
import cubex2.mods.morefurnaces.FurnaceType;
import cubex2.mods.morefurnaces.client.gui.GuiMoreFurnace;
import cubex2.mods.morefurnaces.inventory.ContainerIronFurnace;
import cubex2.mods.morefurnaces.tileentity.TileEntityIronFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CommonProxy implements IGuiHandler
{

    public void registerRenderInformation()
    {

    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityIronFurnace)
            return GuiMoreFurnace.GUI.buildGui(FurnaceType.values()[ID], player.inventory, (TileEntityIronFurnace) te);
        else
            return null;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityIronFurnace)
        {
            TileEntityIronFurnace furnace = (TileEntityIronFurnace) te;
            return new ContainerIronFurnace(player.inventory, furnace, furnace.getType());
        }
        else
            return null;
    }

    public World getClientWorld()
    {
        return null;
    }

}
