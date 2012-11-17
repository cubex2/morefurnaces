package cubex2.mods.morefurnaces;

import static net.minecraftforge.common.ForgeDirection.UP;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockMoreFurnaces extends BlockContainer {

	private Random random;

	protected BlockMoreFurnaces(int id) {
		super(id, Material.iron);
		setBlockName("ironFurnace");
		setHardness(2.5F);
		setStepSound(Block.soundMetalFootstep);
		setRequiresSelfNotify();
		setTextureFile("/cubex2/mods/morefurnaces/client/textures/textures.png");
		if (id >= 256) {
			disableStats();
		}
		random = new Random();
		setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public TileEntity createNewTileEntity(World w) {
		return null;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return FurnaceType.makeEntity(meta);
	}

	@Override
	public int idDropped(int i, Random random, int fortune) {
		return MoreFurnaces.blockFurnaces.blockID;
	}

	@Override
	public int damageDropped(int i) {
		return i;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te != null && te instanceof TileEntityIronFurnace) {
			return ((TileEntityIronFurnace) te).isActive() ? (int) (0.8 * 15) : 0;
		}
		else {
			return 0;
		}
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		this.setDefaultDirection(world, x, y, z);
		world.markBlockNeedsUpdate(x, y, z);
	}

	private void setDefaultDirection(World world, int x, int y, int z) {
		if (!world.isRemote) {
			int var5 = world.getBlockId(x, y, z - 1);
			int var6 = world.getBlockId(x, y, z + 1);
			int var7 = world.getBlockId(x - 1, y, z);
			int var8 = world.getBlockId(x + 1, y, z);
			byte facing = 3;

			if (Block.opaqueCubeLookup[var5] && !Block.opaqueCubeLookup[var6]) {
				facing = 3;
			}

			if (Block.opaqueCubeLookup[var6] && !Block.opaqueCubeLookup[var5]) {
				facing = 2;
			}

			if (Block.opaqueCubeLookup[var7] && !Block.opaqueCubeLookup[var8]) {
				facing = 5;
			}

			if (Block.opaqueCubeLookup[var8] && !Block.opaqueCubeLookup[var7]) {
				facing = 4;
			}

			TileEntity te = world.getBlockTileEntity(x, y, z);
			if (te != null && te instanceof TileEntityIronFurnace) {
				((TileEntityIronFurnace) te).setFacing(facing);
				world.markBlockNeedsUpdate(x, y, z);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
		int meta = world.getBlockMetadata(x, y, z);
		if (side == 1 || side == 0) {
			return meta * 16;
		}
		else {
			TileEntity te = world.getBlockTileEntity(x, y, z);
			if (te != null && te instanceof TileEntityIronFurnace) {
				TileEntityIronFurnace furnace = (TileEntityIronFurnace) te;
				if (side != furnace.getFacing()) {
					return meta * 16;
				}
				if (furnace.isActive()) {
					return meta * 16 + 3;
				}
				else {
					return meta * 16 + 2;
				}
			}
		}
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te != null && te instanceof TileEntityIronFurnace) {
			TileEntityIronFurnace tef = (TileEntityIronFurnace) te;
			if (tef.isActive()) {
				byte facing = tef.getFacing();
				float var7 = x + 0.5F;
				float var8 = y + 0.0F + random.nextFloat() * 6.0F / 16.0F;
				float var9 = z + 0.5F;
				float var10 = 0.52F;
				float var11 = random.nextFloat() * 0.6F - 0.3F;

				if (facing == 4) {
					world.spawnParticle("smoke", (var7 - var10), var8, (var9 + var11), 0.0D, 0.0D, 0.0D);
					world.spawnParticle("flame", (var7 - var10), var8, (var9 + var11), 0.0D, 0.0D, 0.0D);
				}
				else if (facing == 5) {
					world.spawnParticle("smoke", (var7 + var10), var8, (var9 + var11), 0.0D, 0.0D, 0.0D);
					world.spawnParticle("flame", (var7 + var10), var8, (var9 + var11), 0.0D, 0.0D, 0.0D);
				}
				else if (facing == 2) {
					world.spawnParticle("smoke", (var7 + var11), var8, (var9 - var10), 0.0D, 0.0D, 0.0D);
					world.spawnParticle("flame", (var7 + var11), var8, (var9 - var10), 0.0D, 0.0D, 0.0D);
				}
				else if (facing == 3) {
					world.spawnParticle("smoke", (var7 + var11), var8, (var9 + var10), 0.0D, 0.0D, 0.0D);
					world.spawnParticle("flame", (var7 + var11), var8, (var9 + var10), 0.0D, 0.0D, 0.0D);
				}
			}
		}
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int i, int j) {
		if (i == 1 || i == 0) {
			return j * 16;
		}
		if (i == 3) {
			return j * 16 + 2;
		}
		return j * 16;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float f1, float f2, float f3) {
		int meta = world.getBlockMetadata(x, y, z);
		if(meta == FurnaceType.NETHERRACK.ordinal() && ForgeDirection.getOrientation(side) == ForgeDirection.UP)
		{
			return false;
		}
		TileEntity te = world.getBlockTileEntity(x, y, z);

		if (te == null || !(te instanceof TileEntityIronFurnace)) {
			return true;
		}

		if (world.isRemote) {
			return true;
		}

		player.openGui(MoreFurnaces.instance, ((TileEntityIronFurnace) te).getType().ordinal(), world, x, y, z);
		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving living) {
		byte furnaceFacing = 0;
		int facing = MathHelper.floor_double((living.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

		if (facing == 0) {
			furnaceFacing = 2;
		}

		if (facing == 1) {
			furnaceFacing = 5;
		}

		if (facing == 2) {
			furnaceFacing = 3;
		}

		if (facing == 3) {
			furnaceFacing = 4;
		}

		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te != null && te instanceof TileEntityIronFurnace) {
			((TileEntityIronFurnace) te).setFacing(furnaceFacing);
			world.markBlockNeedsUpdate(x, y, z);
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te != null && te instanceof TileEntityIronFurnace) {
			TileEntityIronFurnace furnace = (TileEntityIronFurnace) te;
			for (int i = 0; i < furnace.getSizeInventory(); ++i) {
				ItemStack stack = furnace.getStackInSlot(i);

				if (stack != null) {
					float var10 = random.nextFloat() * 0.8F + 0.1F;
					float var11 = random.nextFloat() * 0.8F + 0.1F;
					float var12 = random.nextFloat() * 0.8F + 0.1F;

					while (stack.stackSize > 0) {
						int var13 = random.nextInt(21) + 10;

						if (var13 > stack.stackSize) {
							var13 = stack.stackSize;
						}

						stack.stackSize -= var13;
						EntityItem var14 = new EntityItem(world, (x + var10), (y + var11), (z + var12), new ItemStack(stack.itemID, var13, stack.getItemDamage()));

						if (stack.hasTagCompound()) {
							var14.item.setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
						}

						float var15 = 0.05F;
						var14.motionX = ((float) random.nextGaussian() * var15);
						var14.motionY = ((float) random.nextGaussian() * var15 + 0.2F);
						var14.motionZ = ((float) random.nextGaussian() * var15);
						world.spawnEntityInWorld(var14);
					}
				}
			}
		}
		super.breakBlock(world, x, y, z, par5, par6);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(int i, CreativeTabs tabs, List list) {
		for (FurnaceType type : FurnaceType.values()) {
			list.add(new ItemStack(this, 1, type.ordinal()));
		}
	}

	@Override
	public boolean isFireSource(World world, int x, int y, int z, int metadata, ForgeDirection side) {
		if (metadata == FurnaceType.NETHERRACK.ordinal() && side == UP)
			return true;
		return false;
	}

}
