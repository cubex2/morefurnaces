package cubex2.mods.morefurnaces.inventory;

import cubex2.cxlibrary.inventory.ISlotCX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SlotFuel extends SlotItemHandler implements ISlotCX
{
    private final String name;
    private final ItemHandlerFurnace inventoryIn;

    public SlotFuel(String name, ItemHandlerFurnace inventoryIn, int slotIndex)
    {
        super(inventoryIn, slotIndex, -2000, -2000);
        this.inventoryIn = inventoryIn;
        this.name = name;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn)
    {
        inventoryIn.slotChecksEnabled = false;
        boolean allow = super.canTakeStack(playerIn);
        inventoryIn.slotChecksEnabled = true;

        return allow;
    }

    @Nonnull
    @Override
    public ItemStack decrStackSize(int amount)
    {
        inventoryIn.slotChecksEnabled = false;
        ItemStack stack = super.decrStackSize(amount);
        inventoryIn.slotChecksEnabled = true;

        return stack;
    }

    public boolean isItemValid(ItemStack stack)
    {
        return TileEntityFurnace.isItemFuel(stack) || SlotFurnaceFuel.isBucket(stack);
    }

    public int getItemStackLimit(ItemStack stack)
    {
        return SlotFurnaceFuel.isBucket(stack) ? 1 : super.getItemStackLimit(stack);
    }
}
