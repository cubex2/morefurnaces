package cubex2.mods.morefurnaces;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		ByteArrayDataInput dat = ByteStreams.newDataInput(packet.data);
		int x = dat.readInt();
		int y = dat.readInt();
		int z = dat.readInt();
		byte typ = dat.readByte();
		boolean isActive = dat.readBoolean();
		byte facing = dat.readByte();
		World world = MoreFurnaces.proxy.getClientWorld();
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof TileEntityIronFurnace) {
			TileEntityIronFurnace furnace = (TileEntityIronFurnace) te;
			furnace.setFacing(facing);
			world.markBlockNeedsUpdate(x, y, z);
			world.updateAllLightTypes(x, y, z);
		}
	}

	public static Packet getPacket(TileEntityIronFurnace furnace) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(15);
		DataOutputStream dos = new DataOutputStream(bos);
		int x = furnace.xCoord;
		int y = furnace.yCoord;
		int z = furnace.zCoord;
		int typ = furnace.getType().ordinal();
		try {
			dos.writeInt(x);
			dos.writeInt(y);
			dos.writeInt(z);
			dos.writeByte(typ);
			dos.writeBoolean(furnace.isActive());
			dos.writeByte(furnace.getFacing());
		} catch (IOException e) {
			e.printStackTrace();
		}
		Packet250CustomPayload pkt = new Packet250CustomPayload();
		pkt.channel = "MoreFurnaces";
		pkt.data = bos.toByteArray();
		pkt.length = bos.size();
		pkt.isChunkDataPacket = true;
		return pkt;
	}

}
