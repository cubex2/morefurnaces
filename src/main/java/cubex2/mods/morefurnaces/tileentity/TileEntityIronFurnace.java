package cubex2.mods.morefurnaces.tileentity;

import cubex2.mods.morefurnaces.FurnaceType;
import cubex2.mods.morefurnaces.MoreFurnaces;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.*;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
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
        worldObj.addBlockEvent(pos, MoreFurnaces.blockFurnaces, 1, facing & 0xFF);
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
        return ItemStackHelper.getAndSplit(furnaceContents, i, j);
    }

    @Override
    public ItemStack removeStackFromSlot(int i)
    {
        return ItemStackHelper.getAndRemove(furnaceContents, i);
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack stack)
    {
        boolean flag = stack != null && stack.isItemEqual(furnaceContents[i]) && ItemStack.areItemStackTagsEqual(stack, furnaceContents[i]);
        furnaceContents[i] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }

        if (!flag && i < type.parallelSmelting)
        {
            furnaceCookTime[i] = 0;
            markDirty();
        }
    }

    @Override
    public String getName()
    {
        return type.name();
    }

    @Override
    public boolean hasCustomName()
    {
        return false;
    }

    @Override
    public ITextComponent getDisplayName()
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
            IBlockState state = worldObj.getBlockState(pos);
            worldObj.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound)
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

        return nbtTagCompound;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    public float getCookProgress(int id)
    {
        return furnaceCookTime[id] / (float) getSpeed();
    }

    @SideOnly(Side.CLIENT)
    public float getBurnTimeRemaining()
    {
        if (currentItemBurnTime == 0)
        {
            currentItemBurnTime = getSpeed();
        }

        return furnaceBurnTime / (float) currentItemBurnTime;
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

                IBlockState state = worldObj.getBlockState(pos);
                worldObj.notifyBlockUpdate(pos, state, state, 3);
            } else if (type.fuelSlots == 0)
            {
                if (isActive != isBurning())
                {
                    currentItemBurnTime = furnaceBurnTime = 3600;
                    inventoryChanged = true;
                    isActive = this.isBurning();

                    IBlockState state = worldObj.getBlockState(pos);
                    worldObj.notifyBlockUpdate(pos, state, state, 3);
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
        if (worldObj != null && !worldObj.isRemote) return true;
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

            if (stack.getItem() instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.AIR)
            {
                Block block = Block.getBlockFromItem(item);

                if (block == Blocks.WOODEN_SLAB)
                    return 150;

                if (block.getDefaultState().getMaterial() == Material.WOOD)
                    return 300;

                if (block == Blocks.COAL_BLOCK)
                    return 16000;
            }
            if (item instanceof ItemTool && ((ItemTool) item).getToolMaterialName().equals("WOOD")) return 200;
            if (item instanceof ItemSword && ((ItemSword) item).getToolMaterialName().equals("WOOD")) return 200;
            if (item instanceof ItemHoe && ((ItemHoe) item).getMaterialName().equals("WOOD")) return 200;
            if (item == Items.STICK) return 100;
            if (item == Items.COAL) return 1600;
            if (item == Items.LAVA_BUCKET) return 20000;
            if (item == Item.getItemFromBlock(Blocks.SAPLING)) return 100;
            if (item == Items.BLAZE_ROD) return 2400;
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
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        if (index == 2)
        {
            return false;
        } else if (index != 1)
        {
            return true;
        } else
        {
            ItemStack itemstack = this.furnaceContents[1];
            return isItemFuel(stack) || SlotFurnaceFuel.isBucket(stack) && (itemstack == null || itemstack.getItem() != Items.BUCKET);
        }
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
        Arrays.fill(furnaceContents, null);
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
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
    {
        if (direction == EnumFacing.DOWN && index == 1)
        {
            Item item = stack.getItem();

            if (item != Items.WATER_BUCKET && item != Items.BUCKET)
            {
                return false;
            }
        }

        return true;
    }

}
