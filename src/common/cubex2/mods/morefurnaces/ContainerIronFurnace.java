package cubex2.mods.morefurnaces;

import java.util.Iterator;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.ICrafting;
import net.minecraft.src.IInventory;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
import net.minecraft.src.SlotFurnace;

public class ContainerIronFurnace extends Container {
	private FurnaceType type;
	private EntityPlayer player;
	private TileEntityIronFurnace furnace;
	private int lastCookTime[];
	private int lastBurnTime = 0;
	private int lastItemBurnTime = 0;

	public ContainerIronFurnace(IInventory invPlayer, TileEntityIronFurnace invFurnace, FurnaceType type) {
		furnace = invFurnace;
		player = ((InventoryPlayer) invPlayer).player;
		this.type = type;
		lastCookTime = new int[type.parallelSmelting];

		int slotId = 0;
		for (int i = 0; i < type.parallelSmelting; i++) {
			addSlotToContainer(new Slot(invFurnace, slotId++, type.mainInputX[i], type.mainInputY[i]));
			for (int y = type.getNumInputRows() - 1; y >= 0; y--) {
				for (int x = type.getInputSlotsPerRow() - 1; x >= 0; x--) {
					addSlotToContainer(new Slot(invFurnace, slotId++, type.inputX[i] + x * 18, type.inputY[i] + y * 18));
				}
			}
		}

		if (type.fuelSlots > 0) {
			addSlotToContainer(new Slot(invFurnace, slotId++, type.mainFuelX, type.mainFuelY));
			for (int y = 0; y < type.getNumFuelRows(); y++) {
				for (int x = type.getFuelSlotsPerRow() - 1; x >= 0; x--) {
					addSlotToContainer(new Slot(invFurnace, slotId++, type.fuelX + x * 18, type.fuelY + y * 18));
				}
			}
		}

		for (int i = 0; i < type.parallelSmelting; i++) {
			addSlotToContainer(new SlotFurnace(player, invFurnace, slotId++, type.mainOutputX[i], type.mainOutputY[i]));
			for (int y = type.getNumOutputRows() - 1; y >= 0; y--) {
				for (int x = 0; x < type.getOutputSlotsPerRow(); x++) {
					addSlotToContainer(new SlotFurnace(player, invFurnace, slotId++, type.outputX[i] + x * 18, type.outputY[i] + y * 18));
				}
			}
		}

		for (int row = 0; row < 3; row++)
			for (int column = 0; column < 9; column++)
				addSlotToContainer(new Slot(invPlayer, column + row * 9 + 9, type.inventoryX + column * 18, type.inventoryY + row * 18));

		for (int column = 0; column < 9; column++)
			addSlotToContainer(new Slot(invPlayer, column, type.inventoryX + column * 18, type.inventoryY + 58));
	}

	@Override
	public void addCraftingToCrafters(ICrafting icrafting) {
		super.addCraftingToCrafters(icrafting);

		for (int i = 0; i < type.parallelSmelting; i++) {
			icrafting.updateCraftingInventoryInfo(this, i, this.furnace.furnaceCookTime[i]);
		}

		icrafting.updateCraftingInventoryInfo(this, type.parallelSmelting, this.furnace.furnaceBurnTime);
		icrafting.updateCraftingInventoryInfo(this, type.parallelSmelting + 1, this.furnace.currentItemBurnTime);
	}

	@Override
	public void updateCraftingResults() {
		super.updateCraftingResults();
		Iterator iterator = this.crafters.iterator();

		while (iterator.hasNext()) {
			ICrafting var2 = (ICrafting) iterator.next();

			for (int i = 0; i < type.parallelSmelting; i++) {
				if (this.lastCookTime[i] != this.furnace.furnaceCookTime[i]) {
					var2.updateCraftingInventoryInfo(this, i, this.furnace.furnaceCookTime[i]);
				}
			}

			if (this.lastBurnTime != this.furnace.furnaceBurnTime) {
				var2.updateCraftingInventoryInfo(this, type.parallelSmelting, this.furnace.furnaceBurnTime);
			}

			if (this.lastItemBurnTime != this.furnace.currentItemBurnTime) {
				var2.updateCraftingInventoryInfo(this, type.parallelSmelting + 1, this.furnace.currentItemBurnTime);
			}
		}

		for (int i = 0; i < type.parallelSmelting; i++) {
			this.lastCookTime[i] = this.furnace.furnaceCookTime[i];
		}
		this.lastBurnTime = this.furnace.furnaceBurnTime;
		this.lastItemBurnTime = this.furnace.currentItemBurnTime;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int i, int j) {
		if (i < type.parallelSmelting) {
			this.furnace.furnaceCookTime[i] = j;
		}

		if (i == type.parallelSmelting) {
			this.furnace.furnaceBurnTime = j;
		}

		if (i == type.parallelSmelting + 1) {
			this.furnace.currentItemBurnTime = j;
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return furnace.isUseableByPlayer(player);
	}

	private boolean isOutputSlot(int i) {
		return i >= type.getFirstOutputSlot(0) && i <= type.getLastOutputSlot(type.parallelSmelting - 1);
	}

	private boolean isInputSlot(int i) {
		return i >= type.getFirstInputSlot(0) && i <= type.getLastInputSlot(type.parallelSmelting - 1);
	}

	private boolean isFuelSlot(int i) {
		return i >= type.getFirstFuelSlot() && i <= type.getLastFuelSlot();
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {
		ItemStack stack = null;
		Slot slot = (Slot) this.inventorySlots.get(i);

		if (slot != null && slot.getHasStack()) {
			ItemStack stack1 = slot.getStack();
			stack = stack1.copy();

			if (isOutputSlot(i)) {
				if (!this.mergeItemStack(stack1, type.getNumSlots(), type.getNumSlots() + 36, true)) {
					return null;
				}

				slot.onSlotChange(stack1, stack);
			}
			else if (!isInputSlot(i) && !isFuelSlot(i)) {
				if (FurnaceRecipes.smelting().getSmeltingResult(stack1) != null) {
					if (!this.mergeItemStack(stack1, 0, type.getFirstFuelSlot(), false)) {
						return null;
					}
				}
				else if (TileEntityIronFurnace.isItemFuel(stack1)) {
					if (!this.mergeItemStack(stack1, type.getFirstFuelSlot(), type.getFirstOutputSlot(0), false)) {
						return null;
					}
				}
				else if (i >= type.getNumSlots() && i < type.getNumSlots() + 27) {
					if (!this.mergeItemStack(stack1, type.getNumSlots() + 27, type.getNumSlots() + 36, false)) {
						return null;
					}
				}
				else if (i >= type.getNumSlots() + 27 && i < type.getNumSlots() + 36 && !this.mergeItemStack(stack1, type.getNumSlots(), type.getNumSlots() + 27, false)) {
					return null;
				}
			}
			else if (!this.mergeItemStack(stack1, type.getNumSlots(), type.getNumSlots() + 36, false)) {
				return null;
			}

			if (stack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			}
			else {
				slot.onSlotChanged();
			}

			if (stack1.stackSize == stack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(player, stack1);
		}

		return stack;
	}
}
