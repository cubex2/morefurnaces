package cubex2.mods.morefurnaces.inventory;


import cubex2.cxlibrary.inventory.ContainerCX;
import cubex2.mods.morefurnaces.FurnaceType;
import cubex2.mods.morefurnaces.tileentity.TileEntityIronFurnace;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ContainerIronFurnace extends ContainerCX
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
            addSlotToContainer(new SlotInput("furnace", invFurnace.getItemHandler(), slotId++));
        }

        for (int i = 0; i < type.getNumFuelSlots(); i++)
        {
            addSlotToContainer(new SlotFuel("furnace", invFurnace.getItemHandler(), slotId++));
        }

        for (int i = 0; i < type.getNumOutputSlots(); i++)
        {
            addSlotToContainer(new SlotOutput("furnace", player, invFurnace.getItemHandler(), slotId++));
        }

        addPlayerSlots(invPlayer);
    }

    @Override
    public void addListener(IContainerListener listener)
    {
        super.addListener(listener);
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (IContainerListener listener : listeners)
        {
            for (int i = 0; i < type.parallelSmelting; i++)
            {
                if (lastCookTime[i] != furnace.furnaceCookTime[i])
                {
                    listener.sendWindowProperty(this, i, furnace.furnaceCookTime[i]);
                }
            }

            if (lastBurnTime != furnace.furnaceBurnTime)
            {
                listener.sendWindowProperty(this, type.parallelSmelting, furnace.furnaceBurnTime);
            }

            if (lastItemBurnTime != furnace.currentItemBurnTime)
            {
                listener.sendWindowProperty(this, type.parallelSmelting + 1, furnace.currentItemBurnTime);
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
        BlockPos pos = furnace.getPos();
        return player.world.getTileEntity(pos) == furnace && player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
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
    protected boolean transferStackInSlot(Slot slot, int index, ItemStack stack1, ItemStack stack)
    {
        if (isOutputSlot(index))
        {
            if (!this.mergeItemStack(stack1, type.getNumSlots(), type.getNumSlots() + 36, true))
                return true;

            slot.onSlotChange(stack1, stack);
        } else if (!isInputSlot(index) && !isFuelSlot(index))
        {
            if (!FurnaceRecipes.instance().getSmeltingResult(stack1).isEmpty())
            {
                if (!this.mergeItemStack(stack1, 0, type.getFirstFuelSlot(), false))
                    return true;
            } else if (TileEntityFurnace.isItemFuel(stack1))
            {
                if (!this.mergeItemStack(stack1, type.getFirstFuelSlot(), type.getFirstOutputSlot(0), false))
                    return true;
            } else if (index >= type.getNumSlots() && index < type.getNumSlots() + 27)
            {
                if (!this.mergeItemStack(stack1, type.getNumSlots() + 27, type.getNumSlots() + 36, false))
                    return true;
            } else if (index >= type.getNumSlots() + 27 && index < type.getNumSlots() + 36 && !this.mergeItemStack(stack1, type.getNumSlots(), type.getNumSlots() + 27, false))
                return true;
        } else if (!this.mergeItemStack(stack1, type.getNumSlots(), type.getNumSlots() + 36, false))
            return true;

        return false;
    }
}
