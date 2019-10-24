package cubex2.mods.morefurnaces.inventory;

import cubex2.mods.morefurnaces.FurnaceType;
import cubex2.mods.morefurnaces.tileentity.TileEntityIronFurnace;
import cubex2.mods.morefurnaces.tileentity.TileEntityNetherrackFurnace;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.EmptyHandler;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class ItemHandlerFurnace extends ItemStackHandler
{
    private final TileEntityIronFurnace tile;
    private final FurnaceType type;

    private final ItemHandlerMoveStacks[] inputHandlers;
    private final ItemHandlerMoveStacks[] outputHandlers;
    private final IItemHandlerModifiable fuelHandler;

    private final IItemHandlerModifiable bottomSideHandler;
    private final IItemHandlerModifiable topSideHandler;
    private final IItemHandlerModifiable sidesSideHandler;

    public boolean slotChecksEnabled = true;

    public ItemHandlerFurnace(TileEntityIronFurnace tile)
    {
        super(tile.getType().getNumSlots());

        this.tile = tile;
        type = tile.getType();

        fuelHandler = type.fuelSlots > 0
                      ? new ItemHandlerMoveStacks(this,
                                                  type.getFirstFuelSlot(),
                                                  type.getLastFuelSlot(),
                                                  0)
                      : new EmptyHandler();
        inputHandlers = new ItemHandlerMoveStacks[type.parallelSmelting];
        outputHandlers = new ItemHandlerMoveStacks[type.parallelSmelting];

        for (int i = 0; i < type.parallelSmelting; i++)
        {
            inputHandlers[i] = new ItemHandlerMoveStacks(this,
                                                         type.getFirstInputSlot(i),
                                                         type.getLastInputSlot(i),
                                                         0);

            int firstOutput = type.getFirstOutputSlot(i);
            int lastOutput = type.getLastOutputSlot(i);
            outputHandlers[i] = new ItemHandlerMoveStacks(this,
                                                          firstOutput, lastOutput,
                                                          range(1, lastOutput - firstOutput + 1));
        }

        bottomSideHandler = createBottomSideHandler();
        topSideHandler = new CombinedInvWrapper(inputHandlers);
        sidesSideHandler = fuelHandler;
    }

    private CombinedInvWrapper createBottomSideHandler()
    {
        IItemHandlerModifiable[] handlers = new IItemHandlerModifiable[outputHandlers.length + 1];
        System.arraycopy(outputHandlers, 0, handlers, 0, outputHandlers.length);
        handlers[handlers.length - 1] = fuelHandler;

        return new CombinedInvWrapper(handlers);
    }

    private int[] range(int min, int max)
    {
        if (min > max)
            return new int[0];

        int[] range = new int[max - min + 1];
        for (int i = 0; i < range.length; i++)
        {
            range[i] = min + i;
        }
        return range;
    }

	public IItemHandlerModifiable getHandlerForSide(@Nonnull EnumFacing facing) {
		if (FurnaceType.NETHERRACK.equals(type) && TileEntityNetherrackFurnace.class.isInstance(tile) && facing.equals(EnumFacing.getFront(tile.getFacing()).getOpposite())) {
			return topSideHandler;
		}

		if (facing == EnumFacing.DOWN)
			return bottomSideHandler;
		if (facing == EnumFacing.UP)
			return topSideHandler;
		return sidesSideHandler;
	}

    public void moveFuelStacks()
    {
        if (fuelHandler instanceof ItemHandlerMoveStacks)
            ((ItemHandlerMoveStacks) fuelHandler).moveStacks();
    }

    public void moveInputStacks()
    {
        Arrays.stream(inputHandlers).forEach(ItemHandlerMoveStacks::moveStacks);
    }

    public void moveOutputStacks()
    {
        Arrays.stream(outputHandlers).forEach(ItemHandlerMoveStacks::moveStacks);
    }

    public ItemHandlerMoveStacks[] getInputHandlers()
    {
        return inputHandlers;
    }

    public ItemHandlerMoveStacks[] getOutputHandlers()
    {
        return outputHandlers;
    }

    public IItemHandlerModifiable getFuelHandler()
    {
        return fuelHandler;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (!isStackValidForSlot(slot, stack))
            return stack;

        return super.insertItem(slot, stack, simulate);
    }

    private boolean isStackValidForSlot(int index, @Nonnull ItemStack stack)
    {
        if (!slotChecksEnabled)
            return true;
        
        if (type.isOutputSlot(index))
        {
            return false;
        } else if (type.isInputSlot(index))
        {
            return true;
        } else
        {
            return TileEntityFurnace.isItemFuel(stack) || SlotFurnaceFuel.isBucket(stack);
        }
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (slotChecksEnabled && type.isFuelSlot(slot))
        {
            Item item = getStackInSlot(slot).getItem();
            if (item != Items.WATER_BUCKET && item != Items.BUCKET)
                return ItemStack.EMPTY;
        }

        return super.extractItem(slot, amount, simulate);
    }

    @Override
    protected void onContentsChanged(int slot)
    {
        super.onContentsChanged(slot);

        tile.markDirty();
    }

    public void dropAllItems(World world, double x, double y, double z)
    {
        for(int i = 0; i < this.getSlots(); i++) {
            if(!this.getStackInSlot(i).isEmpty()) {
                InventoryHelper.spawnItemStack(world, x, y, z, this.getStackInSlot(i));
            }
        }
    }
}
