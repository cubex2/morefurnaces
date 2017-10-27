package cubex2.mods.morefurnaces;

import cubex2.mods.morefurnaces.tileentity.*;
import net.minecraft.util.IStringSerializable;

public enum FurnaceType implements IStringSerializable
{
    IRON(2, 2, 2, 1, 150, 1.25f, "Iron Furnace", TileEntityIronFurnace.class),
    GOLD(4, 5, 4, 1, 80, 2.0f, "Gold Furnace", TileEntityGoldFurnace.class),
    DIAMOND(7, 9, 7, 1, 40, 4.0f, "Diamond Furnace", TileEntityDiamondFurnace.class),
    OBSIDIAN(2, 2, 2, 2, 150, 2.0f, "Obsidian Furnace", TileEntityObsidianFurnace.class),
    NETHERRACK(1, 1, 0, 1, 1800, 1.0f, "Netherrack Furnace", TileEntityNetherrackFurnace.class),
    COPPER(2, 2, 2, 1, 150, 1.25f, "Copper Furnace", TileEntityCopperFurnace.class),
    SILVER(4, 5, 4, 1, 100, 1.75f, "Silver Furnace", TileEntitySilverFurnace.class);

    final int inputSlots;
    final int outputSlots;
    public final int fuelSlots;
    public final int parallelSmelting;

    public final int[] inputSlotIds;
    public final int[] outputSlotIds;
    public final int[] fuelSlotIds;

    public final int speed;
    public final float consumptionRate;
    public final String friendlyName;
    public final Class<? extends TileEntityIronFurnace> clazz;

    FurnaceType(int inputSlots, int outputSlots, int fuelSlots,
                int parallelSmelting, int speed, float consumptionRate, String friendlyName,
                Class<? extends TileEntityIronFurnace> clazz)
    {
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
        this.fuelSlots = fuelSlots;
        this.parallelSmelting = parallelSmelting;

        inputSlotIds = new int[inputSlots * parallelSmelting];
        outputSlotIds = new int[outputSlots * parallelSmelting];
        fuelSlotIds = new int[fuelSlots];

        for (int i = 0; i < inputSlotIds.length; i++)
        {
            inputSlotIds[i] = i;
        }
        for (int i = 0; i < fuelSlotIds.length; i++)
        {
            fuelSlotIds[i] = i + inputSlotIds.length;
        }
        for (int i = 0; i < outputSlotIds.length; i++)
        {
            outputSlotIds[i] = i + inputSlotIds.length + fuelSlotIds.length;
        }

        this.speed = speed;
        this.consumptionRate = consumptionRate;
        this.friendlyName = friendlyName;
        this.clazz = clazz;
    }

    public static TileEntityIronFurnace makeEntity(int meta)
    {
        try
        {
            TileEntityIronFurnace te = values()[meta].clazz.newInstance();
            return te;
        } catch (InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isInputSlot(int slotId)
    {
        return arrayContains(inputSlotIds, slotId);
    }

    public boolean isFuelSlot(int slotId)
    {
        return arrayContains(fuelSlotIds, slotId);
    }

    public boolean isOutputSlot(int slotId)
    {
        return arrayContains(outputSlotIds, slotId);
    }

    public static boolean arrayContains(int[] i, int i1)
    {
        for (int i2 : i)
            if (i2 == i1)
                return true;
        return false;
    }

    public int getNumInputSlots()
    {
        return parallelSmelting * inputSlots;
    }

    public int getNumFuelSlots()
    {
        return fuelSlots;
    }

    public int getNumOutputSlots()
    {
        return parallelSmelting * outputSlots;
    }

    public int getFirstInputSlot(int id)
    {
        return 0 + id * inputSlots;
    }

    public int getLastInputSlot(int id)
    {
        return getFirstInputSlot(id) + inputSlots - 1;
    }

    public int getFirstFuelSlot()
    {
        return getLastInputSlot(parallelSmelting - 1) + 1;
    }

    public int getLastFuelSlot()
    {
        return getFirstFuelSlot() + fuelSlots - 1;
    }

    public int getFirstOutputSlot(int id)
    {
        return getLastFuelSlot() + 1 + id * outputSlots;
    }

    public int getLastOutputSlot(int id)
    {
        return getFirstOutputSlot(id) + outputSlots - 1;
    }

    public int getNumSlots()
    {
        return parallelSmelting * (inputSlots + outputSlots) + fuelSlots;
    }

    @Override
    public String getName()
    {
        return name().toLowerCase();
    }
}
