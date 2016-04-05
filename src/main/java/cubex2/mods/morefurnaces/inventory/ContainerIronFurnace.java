package cubex2.mods.morefurnaces.inventory;


import cubex2.cxlibrary.inventory.SlotCX;
import cubex2.mods.morefurnaces.FurnaceType;
import cubex2.mods.morefurnaces.tileentity.TileEntityIronFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerIronFurnace extends Container
{
    private FurnaceType type;
    private EntityPlayer player;
    private TileEntityIronFurnace furnace;
    private int lastCookTime[];
    private int lastBurnTime = 0;
    private int lastItemBurnTime = 0;

    public ContainerIronFurnace(InventoryPlayer invPlayer, TileEntityIronFurnace invFurnace, FurnaceType type)
    {
        furnace = invFurnace;
        player = invPlayer.player;
        this.type = type;
        lastCookTime = new int[type.parallelSmelting];

        int slotId = 0;
        for (int i = 0; i < type.getNumInputSlots(); i++)
        {
            addSlotToContainer(new SlotCX("furnace", invFurnace, slotId++));
        }

        for (int i = 0; i < type.getNumFuelSlots(); i++)
        {
            addSlotToContainer(new SlotFuel("furnace", invFurnace, slotId++));
        }

        for (int i = 0; i < type.getNumOutputSlots(); i++)
        {
            addSlotToContainer(new SlotOutput("furnace", player, invFurnace, slotId++));
        }

        for (int i = 0; i < invPlayer.mainInventory.length; i++)
        {
            addSlotToContainer(new SlotCX("player", invPlayer, i));
        }
    }

    @Override
    public void onCraftGuiOpened(ICrafting icrafting)
    {
        super.onCraftGuiOpened(icrafting);
        icrafting.sendAllWindowProperties(this, furnace);

        /*for (int i = 0; i < type.parallelSmelting; i++)
        {
            icrafting.sendProgressBarUpdate(this, i, furnace.furnaceCookTime[i]);
        }

        icrafting.sendProgressBarUpdate(this, type.parallelSmelting, furnace.furnaceBurnTime);
        icrafting.sendProgressBarUpdate(this, type.parallelSmelting + 1, furnace.currentItemBurnTime);*/
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (ICrafting crafting : crafters)
        {
            for (int i = 0; i < type.parallelSmelting; i++)
            {
                if (lastCookTime[i] != furnace.furnaceCookTime[i])
                {
                    crafting.sendProgressBarUpdate(this, i, furnace.furnaceCookTime[i]);
                }
            }

            if (lastBurnTime != furnace.furnaceBurnTime)
            {
                crafting.sendProgressBarUpdate(this, type.parallelSmelting, furnace.furnaceBurnTime);
            }

            if (lastItemBurnTime != furnace.currentItemBurnTime)
            {
                crafting.sendProgressBarUpdate(this, type.parallelSmelting + 1, furnace.currentItemBurnTime);
            }
        }

        System.arraycopy(furnace.furnaceCookTime, 0, lastCookTime, 0, type.parallelSmelting);
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
        Slot slot = inventorySlots.get(i);

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
                slot.putStack(null);
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
