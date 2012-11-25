package cubex2.mods.morefurnaces;

import java.util.Arrays;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemHoe;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ItemSword;
import net.minecraft.src.ItemTool;
import net.minecraft.src.Material;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.Packet;
import net.minecraft.src.TileEntity;

public class TileEntityIronFurnace extends TileEntity implements IInventory {

	private FurnaceType type;
	private ItemStack[] furnaceContents;
	private byte facing;
	private boolean isActive = false;;
	private int ticksSinceSync = 0;

	public int[] furnaceCookTime;
	public int furnaceBurnTime = 0;
	public int currentItemBurnTime = 0;

	// public int furnaceCookTime = 0;

	public TileEntityIronFurnace() {
		this(FurnaceType.IRON);
	}

	protected TileEntityIronFurnace(FurnaceType type) {
		super();
		this.type = type;
		furnaceCookTime = new int[type.parallelSmelting];
		Arrays.fill(furnaceCookTime, 0);
		this.furnaceContents = new ItemStack[getSizeInventory()];
	}

	@Override
	public int getSizeInventory() {
		return type.getNumSlots();
	}

	public byte getFacing() {
		return this.facing;
	}

	public void setFacing(byte value) {
		this.facing = value;
	}

	public boolean isActive() {
		return isActive;
	}

	public FurnaceType getType() {
		return this.type;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return this.furnaceContents[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (this.furnaceContents[i] != null) {
			ItemStack var3;

			if (this.furnaceContents[i].stackSize <= j) {
				var3 = this.furnaceContents[i];
				this.furnaceContents[i] = null;
				return var3;
			}
			else {
				var3 = this.furnaceContents[i].splitStack(j);

				if (this.furnaceContents[i].stackSize == 0) {
					this.furnaceContents[i] = null;
				}

				return var3;
			}
		}
		else {
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if (this.furnaceContents[i] != null) {
			ItemStack stack = this.furnaceContents[i];
			this.furnaceContents[i] = null;
			return stack;
		}
		else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack stack) {
		this.furnaceContents[i] = stack;

		if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
			stack.stackSize = this.getInventoryStackLimit();
		}

	}

	@Override
	public String getInvName() {
		return type.name();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		NBTTagList var2 = nbtTagCompound.getTagList("Items");
		this.furnaceContents = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < var2.tagCount(); ++i) {
			NBTTagCompound var4 = (NBTTagCompound) var2.tagAt(i);
			byte j = var4.getByte("Slot");

			if (j >= 0 && j < this.furnaceContents.length) {
				this.furnaceContents[j] = ItemStack.loadItemStackFromNBT(var4);
			}
		}

		this.furnaceBurnTime = nbtTagCompound.getShort("BurnTime");
		this.currentItemBurnTime = getItemBurnTime(this.furnaceContents[type.getFirstFuelSlot()]);
		NBTTagList cookList = nbtTagCompound.getTagList("CookTimes");
		furnaceCookTime = new int[type.parallelSmelting];
		for (int i = 0; i < cookList.tagCount(); ++i) {
			NBTTagCompound tag = (NBTTagCompound) cookList.tagAt(i);
			byte cookId = tag.getByte("Id");
			int cookTime = tag.getInteger("Time");
			furnaceCookTime[cookId] = cookTime;

		}
		this.facing = nbtTagCompound.getByte("facing");
		this.isActive = nbtTagCompound.getBoolean("isActive");
		if (worldObj != null)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		nbtTagCompound.setShort("BurnTime", (short) this.furnaceBurnTime);
		NBTTagList cookList = new NBTTagList();
		for (int i = 0; i < furnaceCookTime.length; i++) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setByte("Id", (byte) i);
			tag.setInteger("Time", furnaceCookTime[i]);
			cookList.appendTag(tag);
		}

		nbtTagCompound.setTag("CookTimes", cookList);

		nbtTagCompound.setByte("facing", facing);
		nbtTagCompound.setBoolean("isActive", isActive);
		NBTTagList var2 = new NBTTagList();

		for (int var3 = 0; var3 < this.furnaceContents.length; ++var3) {
			if (this.furnaceContents[var3] != null) {
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte) var3);
				this.furnaceContents[var3].writeToNBT(var4);
				var2.appendTag(var4);
			}
		}

		nbtTagCompound.setTag("Items", var2);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@SideOnly(Side.CLIENT)
	public int getCookProgressScaled(int id, int i) {
		return this.furnaceCookTime[id] * i / type.speed;
	}

	@SideOnly(Side.CLIENT)
	public int getBurnTimeRemainingScaled(int i) {
		if (this.currentItemBurnTime == 0) {
			this.currentItemBurnTime = type.speed;
		}

		return this.furnaceBurnTime * i / this.currentItemBurnTime;
	}

	public boolean isBurning() {
		return this.furnaceBurnTime > 0;
	}

	@Override
	public void updateEntity() {

		if ((++ticksSinceSync % 20) * 4 == 0) {
			worldObj.addBlockEvent(xCoord, yCoord, zCoord, MoreFurnaces.blockFurnaces.blockID, 1, facing & 0xFF);
			worldObj.addBlockEvent(xCoord, yCoord, zCoord, MoreFurnaces.blockFurnaces.blockID, 2, (byte) (isActive ? 1 : 0));
		}
		boolean var1 = this.isBurning();
		boolean inventoryChanged = false;

		if (this.isBurning() && type.fuelSlots > 0) {
			--this.furnaceBurnTime;
		}

		if (!this.worldObj.isRemote) {
			if (updateStacks()) {
				inventoryChanged = true;
			}

			boolean canSmelt = false;
			for (int i = 0; i < type.parallelSmelting; i++) {
				if (canSmelt(i)) {
					canSmelt = true;
					break;
				}
			}
			if (this.furnaceBurnTime == 0 && canSmelt && type.fuelSlots > 0) {
				this.currentItemBurnTime = this.furnaceBurnTime = getItemBurnTime(this.furnaceContents[type.getFirstFuelSlot()]);
				if (this.isBurning()) {
					inventoryChanged = true;
					if (this.furnaceContents[type.getFirstFuelSlot()] != null) {
						--this.furnaceContents[type.getFirstFuelSlot()].stackSize;

						if (this.furnaceContents[type.getFirstFuelSlot()].stackSize == 0) {
							this.furnaceContents[type.getFirstFuelSlot()] = this.furnaceContents[type.getFirstFuelSlot()].getItem().getContainerItemStack(furnaceContents[type.getFirstFuelSlot()]);
						}
					}
				}
			}

			for (int i = 0; i < type.parallelSmelting; i++) {
				if (this.isBurning() && this.canSmelt(i)) {
					++this.furnaceCookTime[i];

					if (this.furnaceCookTime[i] == type.speed) {
						this.furnaceCookTime[i] = 0;
						this.smeltItem(i);
						inventoryChanged = true;
					}
				}
				else {
					this.furnaceCookTime[i] = 0;
				}
			}

			if (var1 != this.isBurning() && type.fuelSlots > 0) {
				inventoryChanged = true;
				isActive = this.isBurning();
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
			else if (type.fuelSlots == 0) {
				if (isActive != isBurning()) {
					currentItemBurnTime = furnaceBurnTime = 3600;
					inventoryChanged = true;
					isActive = this.isBurning();
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				}
			}
		}

		if (inventoryChanged) {
			this.onInventoryChanged();
		}
	}

	@Override
	public void receiveClientEvent(int i, int j) {
		if (i == 1) {
			facing = (byte) j;
		}
		else if (i == 2) {
			isActive = j == 1;
			worldObj.updateAllLightTypes(xCoord, yCoord, zCoord);
		}

	}

	private boolean updateStacks() {
		boolean invChanged = false;
		for (int id = 0; id < type.parallelSmelting; id++) {
			int startSlot = type.getFirstInputSlot(id);
			int endSlot = type.getLastInputSlot(id);
			for (int i = startSlot + 1; i <= endSlot; i++) {
				if (furnaceContents[startSlot] == null && furnaceContents[i] != null) {
					furnaceContents[startSlot] = furnaceContents[i].copy();
					furnaceContents[i] = null;
					invChanged = true;
				}
			}

			startSlot = type.getFirstOutputSlot(id);
			endSlot = type.getLastOutputSlot(id);
			ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(furnaceContents[type.getFirstInputSlot(id)]);
			if (result != null) {
				for (int i = startSlot + 1; i <= endSlot; i++) {
					if (furnaceContents[startSlot] != null && (furnaceContents[startSlot].stackSize >= (furnaceContents[startSlot].getMaxStackSize() - result.stackSize + 1) || !result.isItemEqual(furnaceContents[startSlot]))) {
						if (furnaceContents[i] == null) {
							furnaceContents[i] = furnaceContents[startSlot].copy();
							furnaceContents[startSlot] = null;
							invChanged = true;
						}
						else if (furnaceContents[i].isItemEqual(furnaceContents[startSlot]) && furnaceContents[i].stackSize < furnaceContents[i].getMaxStackSize() && furnaceContents[startSlot].stackSize > 0) {
							int emptySlots = furnaceContents[i].getMaxStackSize() - furnaceContents[i].stackSize;
							int adding = Math.min(furnaceContents[startSlot].stackSize, emptySlots);
							furnaceContents[i].stackSize += adding;
							furnaceContents[startSlot].stackSize -= adding;
							if (furnaceContents[i].stackSize == 0)
								furnaceContents[i] = null;
							if (furnaceContents[startSlot].stackSize == 0)
								furnaceContents[startSlot] = null;
							invChanged = true;
						}
					}
				}
			}
		}

		if (type.fuelSlots > 0) {
			int startSlot = type.getFirstFuelSlot();
			int endSlot = type.getLastFuelSlot();
			for (int i = startSlot; i <= endSlot; i++) {
				if (furnaceContents[startSlot] == null && furnaceContents[i] != null) {
					furnaceContents[startSlot] = furnaceContents[i].copy();
					furnaceContents[i] = null;
					invChanged = true;
				}
			}
		}

		return invChanged;
	}

	/**
	 * Returns true if the furnace can smelt an item, i.e. has a source item,
	 * destination stack isn't full, etc.
	 */
	private boolean canSmelt(int id) {
		if (this.furnaceContents[type.getFirstInputSlot(id)] == null) {
			return false;
		}
		else {
			ItemStack var1 = FurnaceRecipes.smelting().getSmeltingResult(this.furnaceContents[type.getFirstInputSlot(id)]);
			if (var1 == null)
				return false;
			if (this.furnaceContents[type.getFirstOutputSlot(id)] == null)
				return true;
			if (!this.furnaceContents[type.getFirstOutputSlot(id)].isItemEqual(var1))
				return false;
			int result = furnaceContents[type.getFirstOutputSlot(id)].stackSize + var1.stackSize;
			return (result <= getInventoryStackLimit() && result <= var1.getMaxStackSize());
		}
	}

	/**
	 * Turn one item from the furnace source stack into the appropriate smelted
	 * item in the furnace result stack
	 */
	public void smeltItem(int id) {
		if (this.canSmelt(id)) {
			ItemStack var1 = FurnaceRecipes.smelting().getSmeltingResult(this.furnaceContents[type.getFirstInputSlot(id)]);

			if (this.furnaceContents[type.getFirstOutputSlot(id)] == null) {
				this.furnaceContents[type.getFirstOutputSlot(id)] = var1.copy();
			}
			else if (this.furnaceContents[type.getFirstOutputSlot(id)].isItemEqual(var1)) {
				furnaceContents[type.getFirstOutputSlot(id)].stackSize += var1.stackSize;
			}

			--this.furnaceContents[type.getFirstInputSlot(id)].stackSize;

			if (this.furnaceContents[type.getFirstInputSlot(id)].stackSize <= 0) {
				this.furnaceContents[type.getFirstInputSlot(id)] = null;
			}
		}
	}

	/**
	 * Returns the number of ticks that the supplied fuel item will keep the
	 * furnace burning, or 0 if the item isn't fuel
	 */
	public static int getItemBurnTime(ItemStack stack) {
		if (stack == null) {
			return 0;
		}
		else {
			int var1 = stack.getItem().shiftedIndex;
			Item var2 = stack.getItem();

			if (stack.getItem() instanceof ItemBlock && Block.blocksList[var1] != null) {
				Block var3 = Block.blocksList[var1];

				if (var3 == Block.woodSingleSlab) {
					return 150;
				}

				if (var3.blockMaterial == Material.wood) {
					return 300;
				}
			}
			if (var2 instanceof ItemTool && ((ItemTool) var2).getToolMaterialName().equals("WOOD"))
				return 200;
			if (var2 instanceof ItemSword && ((ItemSword) var2).func_77825_f().equals("WOOD"))
				return 200;
			if (var2 instanceof ItemHoe && ((ItemHoe) var2).func_77842_f().equals("WOOD"))
				return 200;
			if (var1 == Item.stick.shiftedIndex)
				return 100;
			if (var1 == Item.coal.shiftedIndex)
				return 1600;
			if (var1 == Item.bucketLava.shiftedIndex)
				return 20000;
			if (var1 == Block.sapling.blockID)
				return 100;
			if (var1 == Item.blazeRod.shiftedIndex)
				return 2400;
			return GameRegistry.getFuelValue(stack);
		}
	}

	public static boolean isItemFuel(ItemStack stack) {
		return getItemBurnTime(stack) > 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}

	@Override
	public Packet getDescriptionPacket() {
		return PacketHandler.getPacket(this);
	}

}
