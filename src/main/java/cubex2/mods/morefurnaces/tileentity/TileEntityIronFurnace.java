package cubex2.mods.morefurnaces.tileentity;

import cubex2.mods.morefurnaces.FurnaceType;
import cubex2.mods.morefurnaces.MoreFurnaces;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.*;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;

public class TileEntityIronFurnace extends TileEntity implements ISidedInventory, ITickable
{
    public int[] furnaceCookTime;
    public int furnaceBurnTime = 0;
    public int currentItemBurnTime = 0;

    private FurnaceType type;
    private ItemStack[] furnaceContents;
    private byte facing;
    private boolean isActive = false;

    private int ticksSinceSync = 0;

    private boolean updateLight = false;

    public TileEntityIronFurnace()
    {
        this(FurnaceType.IRON);
    }

    protected TileEntityIronFurnace(FurnaceType type)
    {
        super();
        this.type = type;
        furnaceCookTime = new int[type.parallelSmelting];
        Arrays.fill(furnaceCookTime, 0);
        furnaceContents = new ItemStack[getSizeInventory()];
    }

    public int getSpeed()
    {
        return MoreFurnaces.ironSpeed;
    }

    public float getConsumptionRate()
    {
        return MoreFurnaces.ironConsumptionRate;
    }

    @Override
    public int getSizeInventory()
    {
        return type.getNumSlots();
    }

    public byte getFacing()
    {
        return facing;
    }

    public void setFacing(byte value)
    {
        facing = value;
    }

    public boolean isActive()
    {
        return isActive;
    }

    public FurnaceType getType()
    {
        return type;
    }

    @Override
    public ItemStack getStackInSlot(int i)
    {
        return furnaceContents[i];
    }

    @Override
    public ItemStack decrStackSize(int i, int j)
    {
        if (furnaceContents[i] != null)
        {
            ItemStack itemstack;

            if (furnaceContents[i].stackSize <= j)
            {
                itemstack = furnaceContents[i];
                furnaceContents[i] = null;
                return itemstack;
            } else
            {
                itemstack = furnaceContents[i].splitStack(j);

                if (furnaceContents[i].stackSize == 0)
                {
                    furnaceContents[i] = null;
                }

                return itemstack;
            }
        } else
            return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i)
    {
        if (furnaceContents[i] != null)
        {
            ItemStack stack = furnaceContents[i];
            furnaceContents[i] = null;
            return stack;
        } else
            return null;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack stack)
    {
        furnaceContents[i] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }

    }

    @Override
    public String getCommandSenderName()
    {
        return type.name();
    }

    @Override
    public boolean hasCustomName()
    {
        return false;
    }

    @Override
    public IChatComponent getDisplayName()
    {
        return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readFromNBT(nbtTagCompound);
        NBTTagList var2 = nbtTagCompound.getTagList("Items", 10);
        furnaceContents = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < var2.tagCount(); ++i)
        {
            NBTTagCompound var4 = var2.getCompoundTagAt(i);
            byte j = var4.getByte("Slot");

            if (j >= 0 && j < furnaceContents.length)
            {
                furnaceContents[j] = ItemStack.loadItemStackFromNBT(var4);
            }
        }

        furnaceBurnTime = nbtTagCompound.getShort("BurnTime");
        currentItemBurnTime = (int) (getItemBurnTime(furnaceContents[type.getFirstFuelSlot()]) / getConsumptionRate());
        NBTTagList cookList = nbtTagCompound.getTagList("CookTimes", 10);
        furnaceCookTime = new int[type.parallelSmelting];
        for (int i = 0; i < cookList.tagCount(); ++i)
        {
            NBTTagCompound tag = cookList.getCompoundTagAt(i);
            byte cookId = tag.getByte("Id");
            int cookTime = tag.getInteger("Time");
            furnaceCookTime[cookId] = cookTime;

        }
        facing = nbtTagCompound.getByte("facing");
        isActive = nbtTagCompound.getBoolean("isActive");
        if (worldObj != null)
        {
            worldObj.markBlockForUpdate(pos);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeToNBT(nbtTagCompound);
        nbtTagCompound.setShort("BurnTime", (short) furnaceBurnTime);
        NBTTagList cookList = new NBTTagList();
        for (int i = 0; i < furnaceCookTime.length; i++)
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setByte("Id", (byte) i);
            tag.setInteger("Time", furnaceCookTime[i]);
            cookList.appendTag(tag);
        }

        nbtTagCompound.setTag("CookTimes", cookList);

        nbtTagCompound.setByte("facing", facing);
        nbtTagCompound.setBoolean("isActive", isActive);
        NBTTagList var2 = new NBTTagList();

        for (int var3 = 0; var3 < furnaceContents.length; ++var3)
        {
            if (furnaceContents[var3] != null)
            {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte) var3);
                furnaceContents[var3].writeToNBT(var4);
                var2.appendTag(var4);
            }
        }

        nbtTagCompound.setTag("Items", var2);
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @SideOnly(Side.CLIENT)
    public int getCookProgressScaled(int id, int i)
    {
        return furnaceCookTime[id] * i / getSpeed();
    }

    @SideOnly(Side.CLIENT)
    public int getBurnTimeRemainingScaled(int i)
    {
        if (currentItemBurnTime == 0)
        {
            currentItemBurnTime = getSpeed();
        }

        return furnaceBurnTime * i / currentItemBurnTime;
    }

    public boolean isBurning()
    {
        return furnaceBurnTime > 0;
    }

    @Override
    public void update()
    {

        if (++ticksSinceSync % 20 * 4 == 0)
        {
            worldObj.addBlockEvent(pos, MoreFurnaces.blockFurnaces, 1, facing & 0xFF);
            worldObj.addBlockEvent(pos, MoreFurnaces.blockFurnaces, 2, (byte) (isActive ? 1 : 0));
        }
        boolean var1 = this.isBurning();
        boolean inventoryChanged = false;

        if (this.isBurning() && type.fuelSlots > 0)
        {
            --furnaceBurnTime;
        }

        if (updateLight && worldObj != null)
        {
            worldObj.checkLightFor(EnumSkyBlock.SKY, pos);
            updateLight = false;
        }

        if (!worldObj.isRemote)
        {
            if (updateStacks())
            {
                inventoryChanged = true;
            }

            boolean canSmelt = false;
            for (int i = 0; i < type.parallelSmelting; i++)
            {
                if (canSmelt(i))
                {
                    canSmelt = true;
                    break;
                }
            }
            if (furnaceBurnTime == 0 && canSmelt && type.fuelSlots > 0)
            {
                int slot = type.getFirstFuelSlot();
                currentItemBurnTime = furnaceBurnTime = (int) (getItemBurnTime(furnaceContents[slot]) / getConsumptionRate());
                if (this.isBurning())
                {
                    inventoryChanged = true;
                    if (furnaceContents[slot] != null)
                    {
                        --furnaceContents[slot].stackSize;

                        if (furnaceContents[slot].stackSize == 0)
                        {
                            furnaceContents[slot] = furnaceContents[slot].getItem().getContainerItem(furnaceContents[slot]);
                        }
                    }
                }
            }

            for (int i = 0; i < type.parallelSmelting; i++)
            {
                if (this.isBurning() && this.canSmelt(i))
                {
                    ++furnaceCookTime[i];

                    if (furnaceCookTime[i] == getSpeed())
                    {
                        furnaceCookTime[i] = 0;
                        this.smeltItem(i);
                        inventoryChanged = true;
                    }
                } else
                {
                    furnaceCookTime[i] = 0;
                }
            }

            if (var1 != this.isBurning() && type.fuelSlots > 0)
            {
                inventoryChanged = true;
                isActive = this.isBurning();
                worldObj.markBlockForUpdate(pos);
            } else if (type.fuelSlots == 0)
            {
                if (isActive != isBurning())
                {
                    currentItemBurnTime = furnaceBurnTime = 3600;
                    inventoryChanged = true;
                    isActive = this.isBurning();
                    worldObj.markBlockForUpdate(pos);
                }
            }
        }

        if (inventoryChanged)
        {
            this.markDirty();
        }
    }

    @Override
    public boolean receiveClientEvent(int i, int j)
    {
        if (i == 1)
        {
            facing = (byte) j;
            return true;
        } else if (i == 2)
        {
            isActive = j == 1;
            if (worldObj != null)
                worldObj.checkLightFor(EnumSkyBlock.BLOCK, pos);
            else
                updateLight = true;
            return true;
        }
        return super.receiveClientEvent(i, j);
    }

    private boolean updateStacks()
    {
        boolean invChanged = false;
        for (int id = 0; id < type.parallelSmelting; id++)
        {
            int startSlot = type.getFirstInputSlot(id);
            int endSlot = type.getLastInputSlot(id);
            for (int i = startSlot + 1; i <= endSlot; i++)
            {
                if (furnaceContents[startSlot] == null && furnaceContents[i] != null)
                {
                    furnaceContents[startSlot] = furnaceContents[i].copy();
                    furnaceContents[i] = null;
                    invChanged = true;
                }
            }

            startSlot = type.getFirstOutputSlot(id);
            endSlot = type.getLastOutputSlot(id);
            ItemStack result = null;
            if (furnaceContents[type.getFirstInputSlot(id)] != null)
            {
                result = FurnaceRecipes.instance().getSmeltingResult(furnaceContents[type.getFirstInputSlot(id)]);
            }
            if (result != null)
            {
                for (int i = startSlot + 1; i <= endSlot; i++)
                {
                    if (furnaceContents[startSlot] != null && (furnaceContents[startSlot].stackSize >= furnaceContents[startSlot].getMaxStackSize() - result.stackSize + 1 || !result.isItemEqual(furnaceContents[startSlot])))
                    {
                        if (furnaceContents[i] == null)
                        {
                            furnaceContents[i] = furnaceContents[startSlot].copy();
                            furnaceContents[startSlot] = null;
                            invChanged = true;
                        } else if (furnaceContents[i].isItemEqual(furnaceContents[startSlot])
                                && furnaceContents[i].stackSize < furnaceContents[i].getMaxStackSize()
                                && furnaceContents[startSlot].stackSize > 0)
                        {
                            int emptySlots = furnaceContents[i].getMaxStackSize() - furnaceContents[i].stackSize;
                            int adding = Math.min(furnaceContents[startSlot].stackSize, emptySlots);
                            furnaceContents[i].stackSize += adding;
                            furnaceContents[startSlot].stackSize -= adding;
                            if (furnaceContents[i].stackSize == 0)
                            {
                                furnaceContents[i] = null;
                            }
                            if (furnaceContents[startSlot].stackSize == 0)
                            {
                                furnaceContents[startSlot] = null;
                            }
                            invChanged = true;
                        }
                    }
                }
            }
        }

        if (type.fuelSlots > 0)
        {
            int startSlot = type.getFirstFuelSlot();
            int endSlot = type.getLastFuelSlot();
            for (int i = startSlot; i <= endSlot; i++)
            {
                if (furnaceContents[startSlot] == null && furnaceContents[i] != null)
                {
                    furnaceContents[startSlot] = furnaceContents[i].copy();
                    furnaceContents[i] = null;
                    invChanged = true;
                }
            }
        }

        return invChanged;
    }

    /**
     * Returns true if the furnace can smelt an item, i.e. has a source item, destination stack isn't full, etc.
     */
    private boolean canSmelt(int id)
    {
        if (furnaceContents[type.getFirstInputSlot(id)] == null)
            return false;
        else
        {
            ItemStack var1 = FurnaceRecipes.instance().getSmeltingResult(furnaceContents[type.getFirstInputSlot(id)]);
            if (var1 == null)
                return false;
            if (furnaceContents[type.getFirstOutputSlot(id)] == null)
                return true;
            if (!furnaceContents[type.getFirstOutputSlot(id)].isItemEqual(var1))
                return false;
            int result = furnaceContents[type.getFirstOutputSlot(id)].stackSize + var1.stackSize;
            return result <= getInventoryStackLimit() && result <= var1.getMaxStackSize();
        }
    }

    /**
     * Turn one item from the furnace source stack into the appropriate smelted item in the furnace result stack
     */
    public void smeltItem(int id)
    {
        if (this.canSmelt(id))
        {
            ItemStack var1 = FurnaceRecipes.instance().getSmeltingResult(furnaceContents[type.getFirstInputSlot(id)]);

            if (furnaceContents[type.getFirstOutputSlot(id)] == null)
            {
                furnaceContents[type.getFirstOutputSlot(id)] = var1.copy();
            } else if (furnaceContents[type.getFirstOutputSlot(id)].isItemEqual(var1))
            {
                furnaceContents[type.getFirstOutputSlot(id)].stackSize += var1.stackSize;
            }

            --furnaceContents[type.getFirstInputSlot(id)].stackSize;

            if (furnaceContents[type.getFirstInputSlot(id)].stackSize <= 0)
            {
                furnaceContents[type.getFirstInputSlot(id)] = null;
            }
        }
    }

    /**
     * Returns the number of ticks that the supplied fuel item will keep the furnace burning, or 0 if the item isn't fuel
     */
    public static int getItemBurnTime(ItemStack stack)
    {
        if (stack == null)
            return 0;
        else
        {
            Item item = stack.getItem();

            if (stack.getItem() instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.air)
            {
                Block block = Block.getBlockFromItem(item);

                if (block == Blocks.wooden_slab)
                    return 150;

                if (block.getMaterial() == Material.wood)
                    return 300;

                if (block == Blocks.coal_block)
                    return 16000;
            }
            if (item instanceof ItemTool && ((ItemTool) item).getToolMaterialName().equals("WOOD"))
                return 200;
            if (item instanceof ItemSword && ((ItemSword) item).getToolMaterialName().equals("WOOD"))
                return 200;
            if (item instanceof ItemHoe && ((ItemHoe) item).getMaterialName().equals("WOOD"))
                return 200;
            if (item == Items.stick)
                return 100;
            if (item == Items.coal)
                return 1600;
            if (item == Items.lava_bucket)
                return 20000;
            if (item == Item.getItemFromBlock(Blocks.sapling))
                return 100;
            if (item == Items.blaze_rod)
                return 2400;
            return GameRegistry.getFuelValue(stack);
        }
    }

    public static boolean isItemFuel(ItemStack stack)
    {
        return getItemBurnTime(stack) > 0;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return worldObj.getTileEntity(pos) != this ? false : player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player)
    {
    }

    @Override
    public void closeInventory(EntityPlayer player)
    {
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(pos, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemstack)
    {
        return type.isOutputSlot(slot) ? false : type.isFuelSlot(slot) ? isItemFuel(itemstack) : true;
    }

    @Override
    public int getField(int id)
    {
        return 0;
    }

    @Override
    public void setField(int id, int value)
    {

    }

    @Override
    public int getFieldCount()
    {
        return 0;
    }

    @Override
    public void clear()
    {
        for (int i = 0; i < furnaceContents.length; i++)
        {
            furnaceContents = null;
        }
    }

    @Override
    public int[] getSlotsForFace(EnumFacing facing)
    {
        return facing == EnumFacing.DOWN ? type.outputSlotIds : facing == EnumFacing.UP ? type.inputSlotIds : type.fuelSlotIds;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemstack, EnumFacing facing)
    {
        return isItemValidForSlot(slot, itemstack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack itemstack, EnumFacing facing)
    {
        return facing != EnumFacing.DOWN || !type.isFuelSlot(slot) || itemstack.getItem() == Items.bucket;
    }

}
