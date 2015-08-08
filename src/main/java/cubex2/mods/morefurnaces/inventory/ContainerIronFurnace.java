package cubex2.mods.morefurnaces.inventory;


import cubex2.mods.morefurnaces.FurnaceType;
import cubex2.mods.morefurnaces.tileentity.TileEntityIronFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Iterator;

public class ContainerIronFurnace extends Container
{
    private FurnaceType type;
    private EntityPlayer player;
    private TileEntityIronFurnace furnace;
    private int lastCookTime[];
    private int lastBurnTime = 0;
    private int lastItemBurnTime = 0;

    public ContainerIronFurnace(IInventory invPlayer, TileEntityIronFurnace invFurnace, FurnaceType type)
    {
        furnace = invFurnace;
        player = ((InventoryPlayer) invPlayer).player;
        this.type = type;
        lastCookTime = new int[type.parallelSmelting];

        int slotId = 0;
        for (int i = 0; i < type.parallelSmelting; i++)
        {
            addSlotToContainer(new Slot(invFurnace, slotId++, type.mainInputX[i], type.mainInputY[i]));
            for (int y = type.getNumInputRows() - 1; y >= 0; y--)
            {
                for (int x = type.getInputSlotsPerRow() - 1; x >= 0; x--)
                {
                    addSlotToContainer(new Slot(invFurnace, slotId++, type.inputX[i] + x * 18, type.inputY[i] + y * 18));
                }
            }
        }

        if (type.fuelSlots > 0)
        {
            addSlotToContainer(new SlotFurnaceFuel(invFurnace, slotId++, type.mainFuelX, type.mainFuelY));
            for (int y = 0; y < type.getNumFuelRows(); y++)
            {
                for (int x = type.getFuelSlotsPerRow() - 1; x >= 0; x--)
                {
                    addSlotToContainer(new Slot(invFurnace, slotId++, type.fuelX + x * 18, type.fuelY + y * 18));
                }
            }
        }

        for (int i = 0; i < type.parallelSmelting; i++)
        {
            addSlotToContainer(new SlotFurnaceOutput(player, invFurnace, slotId++, type.mainOutputX[i], type.mainOutputY[i]));
            for (int y = type.getNumOutputRows() - 1; y >= 0; y--)
            {
                for (int x = 0; x < type.getOutputSlotsPerRow(); x++)
                {
                    addSlotToContainer(new SlotFurnaceOutput(player, invFurnace, slotId++, type.outputX[i] + x * 18, type.outputY[i] + y * 18));
                }
            }
        }

        for (int row = 0; row < 3; row++)
        {
            for (int column = 0; column < 9; column++)
            {
                addSlotToContainer(new Slot(invPlayer, column + row * 9 + 9, type.inventoryX + column * 18, type.inventoryY + row * 18));
            }
        }

        for (int column = 0; column < 9; column++)
        {
            addSlotToContainer(new Slot(invPlayer, column, type.inventoryX + column * 18, type.inventoryY + 58));
        }
    }

    @Override
    public void addCraftingToCrafters(ICrafting icrafting)
    {
        super.addCraftingToCrafters(icrafting);

        for (int i = 0; i < type.parallelSmelting; i++)
        {
            icrafting.sendProgressBarUpdate(this, i, furnace.furnaceCookTime[i]);
        }

        icrafting.sendProgressBarUpdate(this, type.parallelSmelting, furnace.furnaceBurnTime);
        icrafting.sendProgressBarUpdate(this, type.parallelSmelting + 1, furnace.currentItemBurnTime);
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        Iterator iterator = crafters.iterator();

        while (iterator.hasNext())
        {
            ICrafting var2 = (ICrafting) iterator.next();

            for (int i = 0; i < type.parallelSmelting; i++)
            {
                if (lastCookTime[i] != furnace.furnaceCookTime[i])
                {
                    var2.sendProgressBarUpdate(this, i, furnace.furnaceCookTime[i]);
                }
            }

            if (lastBurnTime != furnace.furnaceBurnTime)
            {
                var2.sendProgressBarUpdate(this, type.parallelSmelting, furnace.furnaceBurnTime);
            }

            if (lastItemBurnTime != furnace.currentItemBurnTime)
            {
                var2.sendProgressBarUpdate(this, type.parallelSmelting + 1, furnace.currentItemBurnTime);
            }
        }

        for (int i = 0; i < type.parallelSmelting; i++)
        {
            lastCookTime[i] = furnace.furnaceCookTime[i];
        }
        lastBurnTime = furnace.furnaceBurnTime;
        lastItemBurnTime = furnace.currentItemBurnTime;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int i, int j)
    {
        if (i < type.parallelSmelting)
        {
            furnace.furnaceCookTime[i] = j;
        }

        if (i == type.parallelSmelting)
        {
            furnace.furnaceBurnTime = j;
        }

        if (i == type.parallelSmelting + 1)
        {
            furnace.currentItemBurnTime = j;
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return furnace.isUseableByPlayer(player);
    }

    private boolean isOutputSlot(int i)
    {
        return i >= type.getFirstOutputSlot(0) && i <= type.getLastOutputSlot(type.parallelSmelting - 1);
    }

    private boolean isInputSlot(int i)
    {
        return i >= type.getFirstInputSlot(0) && i <= type.getLastInputSlot(type.parallelSmelting - 1);
    }

    private boolean isFuelSlot(int i)
    {
        return i >= type.getFirstFuelSlot() && i <= type.getLastFuelSlot();
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int i)
    {
        ItemStack stack = null;
        Slot slot = (Slot) inventorySlots.get(i);

        if (slot != null && slot.getHasStack())
        {
            ItemStack stack1 = slot.getStack();
            stack = stack1.copy();

            if (isOutputSlot(i))
            {
                if (!this.mergeItemStack(stack1, type.getNumSlots(), type.getNumSlots() + 36, true))
                    return null;

                slot.onSlotChange(stack1, stack);
            } else if (!isInputSlot(i) && !isFuelSlot(i))
            {
                if (FurnaceRecipes.instance().getSmeltingResult(stack1) != null)
                {
                    if (!this.mergeItemStack(stack1, 0, type.getFirstFuelSlot(), false))
                        return null;
                } else if (TileEntityIronFurnace.isItemFuel(stack1))
                {
                    if (!this.mergeItemStack(stack1, type.getFirstFuelSlot(), type.getFirstOutputSlot(0), false))
                        return null;
                } else if (i >= type.getNumSlots() && i < type.getNumSlots() + 27)
                {
                    if (!this.mergeItemStack(stack1, type.getNumSlots() + 27, type.getNumSlots() + 36, false))
                        return null;
                } else if (i >= type.getNumSlots() + 27 && i < type.getNumSlots() + 36 && !this.mergeItemStack(stack1, type.getNumSlots(), type.getNumSlots() + 27, false))
                    return null;
            } else if (!this.mergeItemStack(stack1, type.getNumSlots(), type.getNumSlots() + 36, false))
                return null;

            if (stack1.stackSize == 0)
            {
                slot.putStack((ItemStack) null);
            } else
            {
                slot.onSlotChanged();
            }

            if (stack1.stackSize == stack.stackSize)
                return null;

            slot.onPickupFromSlot(player, stack1);
        }

        return stack;
    }
}
