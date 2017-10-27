package cubex2.mods.morefurnaces.items;

import cubex2.mods.morefurnaces.FurnaceType;
import cubex2.mods.morefurnaces.MoreFurnaces;
import cubex2.mods.morefurnaces.blocks.BlockMoreFurnaces;
import cubex2.mods.morefurnaces.tileentity.TileEntityIronFurnace;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class ItemUpgrade extends Item
{
    public ItemUpgrade()
    {
        setUnlocalizedName("morefurnacesupgrade");
        setRegistryName("upgrade");
        setCreativeTab(CreativeTabs.MISC);
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return super.getUnlocalizedName(stack) + "." + Upgrades.values()[stack.getMetadata()].getUnlocalizedName();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        if (isInCreativeTab(tab))
        {
            for (int i = 0; i < Upgrades.values().length; i++)
            {
                subItems.add(new ItemStack(this, 1, i));
            }
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack stack = playerIn.getHeldItem(hand);
        Upgrades upgrade = Upgrades.values()[stack.getMetadata()];

        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == MoreFurnaces.blockFurnaces)
        {
            if (useOnModFurnace(playerIn, world, pos, stack, upgrade)) return EnumActionResult.SUCCESS;
        } else if (state.getBlock() == Blocks.FURNACE || state.getBlock() == Blocks.LIT_FURNACE)
        {
            if (upgrade.isVanillaUpgrade())
            {
                if (useOnVanillaFurnace(playerIn, world, pos, stack, upgrade)) return EnumActionResult.SUCCESS;
            }
        }

        return EnumActionResult.PASS;
    }

    private boolean useOnVanillaFurnace(EntityPlayer playerIn, World world, BlockPos pos, ItemStack stack, Upgrades upgrade)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileEntityFurnace)
        {
            TileEntityFurnace furnace = (TileEntityFurnace) te;
            boolean upgraded = upgradeVanillaFurnace(world, pos, furnace, upgrade.getUpgradedType());

            if (upgraded && !playerIn.capabilities.isCreativeMode)
            {
                stack.shrink(1);
            }

            return true;
        }
        return false;
    }

    private boolean useOnModFurnace(EntityPlayer playerIn, World world, BlockPos pos, ItemStack stack, Upgrades upgrade)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileEntityIronFurnace)
        {
            TileEntityIronFurnace furnace = (TileEntityIronFurnace) te;
            FurnaceType furnaceType = furnace.getType();

            if (upgrade.canUpgrade(furnaceType))
            {
                boolean upgraded = upgradeFurnace(world, pos, furnace, upgrade.getUpgradedType());

                if (upgraded && !playerIn.capabilities.isCreativeMode)
                {
                    stack.shrink(1);
                }

                return true;
            }
        }
        return false;
    }

    private boolean upgradeFurnace(World world, BlockPos pos, TileEntityIronFurnace furnace, FurnaceType to)
    {
        FurnaceType from = furnace.getType();

        TileEntityIronFurnace newFurnace = FurnaceType.makeEntity(to.ordinal());

        if (newFurnace != null)
        {
            int[][] fromSlotIds = new int[][] {from.inputSlotIds, from.fuelSlotIds, from.outputSlotIds};

            copyInventory(furnace, fromSlotIds, newFurnace);

            world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockMoreFurnaces.VARIANT, to));
            world.setTileEntity(pos, newFurnace);

            newFurnace.copyStateFrom(furnace);

            return true;
        } else
        {
            return false;
        }
    }

    private boolean upgradeVanillaFurnace(World world, BlockPos pos, TileEntityFurnace furnace, FurnaceType to)
    {
        byte facing = (byte) world.getBlockState(pos).getValue(BlockFurnace.FACING).ordinal();
        TileEntityIronFurnace newFurnace = FurnaceType.makeEntity(to.ordinal());

        if (newFurnace != null)
        {
            int[][] fromSlotIds = new int[][] {new int[] {0}, new int[] {1}, new int[] {2}};

            copyInventory(furnace, fromSlotIds, newFurnace);

            world.setBlockState(pos, MoreFurnaces.blockFurnaces.getDefaultState().withProperty(BlockMoreFurnaces.VARIANT, to));
            world.setTileEntity(pos, newFurnace);

            newFurnace.copyStateFrom(furnace, facing);

            return true;
        } else
        {
            return false;
        }
    }

    private void copyInventory(ICapabilityProvider from, int[][] fromSlotIds, TileEntityIronFurnace to)
    {
        FurnaceType type = to.getType();
        int[][] toSlotIds = new int[][] {type.inputSlotIds, type.fuelSlotIds, type.outputSlotIds};

        IItemHandlerModifiable fromHandler = (IItemHandlerModifiable) from.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        IItemHandlerModifiable toHandler = (IItemHandlerModifiable) to.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        copyInventory(fromHandler, fromSlotIds, toHandler, toSlotIds);
    }

    private void copyInventory(IItemHandlerModifiable from, int[][] fromSlotIds, IItemHandlerModifiable to, int[][] toSlotIds)
    {
        for (int i = 0; i < fromSlotIds.length; i++)
        {
            int[] slotIds = fromSlotIds[i];

            for (int j = 0; j < slotIds.length; j++)
            {
                int fromSlot = slotIds[j];
                if (j < toSlotIds[i].length)
                {
                    int toSlot = toSlotIds[i][j];

                    to.setStackInSlot(toSlot, from.getStackInSlot(fromSlot));
                    from.setStackInSlot(fromSlot, ItemStack.EMPTY);
                }
            }
        }
    }
}
