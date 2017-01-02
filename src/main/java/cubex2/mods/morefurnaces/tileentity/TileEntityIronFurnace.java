package cubex2.mods.morefurnaces.tileentity;

import cubex2.mods.morefurnaces.FurnaceType;
import cubex2.mods.morefurnaces.MoreFurnaces;
import mcp.MethodsReturnNonnullByDefault;
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
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TileEntityIronFurnace extends TileEntity implements ISidedInventory, ITickable
{
    public int[] furnaceCookTime;
    public int furnaceBurnTime = 0;
    public int currentItemBurnTime = 0;

    private final FurnaceType type;
    private NonNullList<ItemStack> furnaceContents;
    private byte facing;
    private boolean isActive = false;

    private int ticksSinceSync = 0;

    private boolean updateLight = false;

    @SuppressWarnings("unused")
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
        furnaceContents = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
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

    @Override
    public boolean isEmpty()
    {
        return furnaceContents.stream().allMatch(ItemStack::isEmpty);
    }

    public byte getFacing()
    {
        return facing;
    }

    public void setFacing(byte value)
    {
        facing = value;
        world.addBlockEvent(pos, MoreFurnaces.blockFurnaces, 1, facing & 0xFF);
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
        return furnaceContents.get(i);
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        return ItemStackHelper.getAndSplit(furnaceContents, index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        return ItemStackHelper.getAndRemove(furnaceContents, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        boolean flag = !stack.isEmpty() && stack.isItemEqual(getStackInSlot(index)) && ItemStack.areItemStackTagsEqual(stack, getStackInSlot(index));
        furnaceContents.set(index, stack);

        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit())
        {
            stack.setCount(this.getInventoryStackLimit());
        }

        if (!flag && index < type.parallelSmelting)
        {
            furnaceCookTime[index] = 0;
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
        furnaceContents = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(nbtTagCompound, furnaceContents);


        furnaceBurnTime = nbtTagCompound.getShort("BurnTime");
        currentItemBurnTime = (int) (getItemBurnTime(getStackInSlot(type.getFirstFuelSlot())) / getConsumptionRate());
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
        if (world != null)
        {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
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
        ItemStackHelper.saveAllItems(nbtTagCompound, furnaceContents);

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
            world.addBlockEvent(pos, MoreFurnaces.blockFurnaces, 1, facing & 0xFF);
            world.addBlockEvent(pos, MoreFurnaces.blockFurnaces, 2, (byte) (isActive ? 1 : 0));
        }

        boolean var1 = this.isBurning();
        boolean inventoryChanged = false;

        if (this.isBurning() && type.fuelSlots > 0)
        {
            --furnaceBurnTime;
        }

        if (updateLight && world != null)
        {
            world.checkLightFor(EnumSkyBlock.SKY, pos);
            updateLight = false;
        }

        if (!world.isRemote)
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
                ItemStack stack = getStackInSlot(slot);
                currentItemBurnTime = furnaceBurnTime = (int) (getItemBurnTime(stack) / getConsumptionRate());
                if (this.isBurning())
                {
                    inventoryChanged = true;
                    if (!stack.isEmpty())
                    {
                        stack.shrink(1);

                        if (stack.getCount() == 0)
                        {
                            furnaceContents.set(slot, stack.getItem().getContainerItem(stack));
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

                IBlockState state = world.getBlockState(pos);
                world.notifyBlockUpdate(pos, state, state, 3);
            } else if (type.fuelSlots == 0)
            {
                if (isActive != isBurning())
                {
                    currentItemBurnTime = furnaceBurnTime = 3600;
                    inventoryChanged = true;
                    isActive = this.isBurning();

                    IBlockState state = world.getBlockState(pos);
                    world.notifyBlockUpdate(pos, state, state, 3);
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
        if (world != null && !world.isRemote) return true;
        if (i == 1)
        {
            facing = (byte) j;
            return true;
        } else if (i == 2)
        {
            isActive = j == 1;
            if (world != null)
                world.checkLightFor(EnumSkyBlock.BLOCK, pos);
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
                if (furnaceContents.get(startSlot).isEmpty() && !furnaceContents.get(i).isEmpty())
                {
                    furnaceContents.set(startSlot, furnaceContents.get(i).copy());
                    furnaceContents.set(i, ItemStack.EMPTY);
                    invChanged = true;
                }
            }

            startSlot = type.getFirstOutputSlot(id);
            endSlot = type.getLastOutputSlot(id);

            ItemStack result = ItemStack.EMPTY;
            ItemStack input = furnaceContents.get(type.getFirstInputSlot(id));
            if (!input.isEmpty())
            {
                result = FurnaceRecipes.instance().getSmeltingResult(input);
            }
            if (!result.isEmpty())
            {
                for (int i = startSlot + 1; i <= endSlot; i++)
                {
                    ItemStack start = furnaceContents.get(startSlot);
                    if (!start.isEmpty() && (start.getCount() >= start.getMaxStackSize() - result.getCount() + 1 || !result.isItemEqual(start)))
                    {
                        ItemStack stack = furnaceContents.get(i);
                        if (stack.isEmpty())
                        {
                            furnaceContents.set(i, start.copy());
                            furnaceContents.set(startSlot, ItemStack.EMPTY);
                            invChanged = true;
                        } else if (stack.isItemEqual(start)
                                   && stack.getCount() < stack.getMaxStackSize()
                                   && start.getCount() > 0)
                        {
                            int emptySlots = stack.getMaxStackSize() - stack.getCount();
                            int adding = Math.min(start.getAnimationsToGo(), emptySlots);

                            stack.grow(adding);
                            start.shrink(adding);

                            if (stack.isEmpty())
                            {
                                furnaceContents.set(i, ItemStack.EMPTY);
                            }
                            if (furnaceContents.get(startSlot).isEmpty())
                            {
                                furnaceContents.set(startSlot, ItemStack.EMPTY);
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
                if (furnaceContents.get(startSlot).isEmpty() && !furnaceContents.get(i).isEmpty())
                {
                    furnaceContents.set(startSlot, furnaceContents.get(i).copy());
                    furnaceContents.set(i, ItemStack.EMPTY);
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
        int inputIndex = type.getFirstInputSlot(id);
        int outputIndex = type.getFirstOutputSlot(id);

        ItemStack input = furnaceContents.get(inputIndex);
        ItemStack output = furnaceContents.get(outputIndex);

        if (input.isEmpty())
        {
            return false;
        } else
        {
            ItemStack res = FurnaceRecipes.instance().getSmeltingResult(input);
            if (res.isEmpty())
                return false;
            if (output.isEmpty())
                return true;
            if (!output.isItemEqual(res))
                return false;
            int result = output.getCount() + res.getCount();
            return result <= getInventoryStackLimit() && result <= res.getMaxStackSize();
        }
    }

    /**
     * Turn one item from the furnace source stack into the appropriate smelted item in the furnace result stack
     */
    private void smeltItem(int id)
    {
        if (this.canSmelt(id))
        {
            int inputIndex = type.getFirstInputSlot(id);
            int outputIndex = type.getFirstOutputSlot(id);

            ItemStack input = furnaceContents.get(inputIndex);
            ItemStack output = furnaceContents.get(outputIndex);
            ItemStack result = FurnaceRecipes.instance().getSmeltingResult(input);

            if (output.isEmpty())
            {
                furnaceContents.set(outputIndex, result.copy());
            } else if (output.isItemEqual(result))
            {
                output.grow(result.getCount());
            }

            if (input.getItem() == Item.getItemFromBlock(Blocks.SPONGE) && input.getMetadata() == 1)
            {
                fillBucketInFuelSlots();
            }

            input.shrink(1);

            if (input.isEmpty())
            {
                furnaceContents.set(inputIndex, ItemStack.EMPTY);
            }
        }
    }

    private void fillBucketInFuelSlots()
    {
        int startIndex = type.getFirstFuelSlot();

        for (int i = 0; i < type.getNumFuelSlots(); i++)
        {
            ItemStack stack = furnaceContents.get(startIndex + i);

            if (!stack.isEmpty() && stack.getItem() == Items.BUCKET)
            {
                furnaceContents.set(startIndex + i, new ItemStack(Items.WATER_BUCKET));
                break;
            }
        }
    }

    /**
     * Returns the number of ticks that the supplied fuel item will keep the furnace burning, or 0 if the item isn't fuel
     */
    private static int getItemBurnTime(ItemStack stack)
    {
        if (stack.isEmpty())
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
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        return world.getTileEntity(pos) == this && player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
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
        return this.writeToNBT(new NBTTagCompound());
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
            ItemStack itemstack = this.furnaceContents.get(1);
            return isItemFuel(stack) || SlotFurnaceFuel.isBucket(stack) && (itemstack.isEmpty() || itemstack.getItem() != Items.BUCKET);
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
        furnaceContents.clear();
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

    private IItemHandler handlerTop = new SidedInvWrapper(this, net.minecraft.util.EnumFacing.UP);
    private IItemHandler handlerBottom = new SidedInvWrapper(this, net.minecraft.util.EnumFacing.DOWN);
    private IItemHandler handlerSide = new SidedInvWrapper(this, net.minecraft.util.EnumFacing.WEST);

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            if (facing == EnumFacing.DOWN)
                return (T) handlerBottom;
            else if (facing == EnumFacing.UP)
                return (T) handlerTop;
            else
                return (T) handlerSide;
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
}
