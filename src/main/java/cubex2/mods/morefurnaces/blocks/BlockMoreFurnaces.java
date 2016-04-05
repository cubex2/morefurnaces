package cubex2.mods.morefurnaces.blocks;


import cubex2.mods.morefurnaces.FurnaceType;
import cubex2.mods.morefurnaces.ModInformation;
import cubex2.mods.morefurnaces.MoreFurnaces;
import cubex2.mods.morefurnaces.items.ItemMoreFurnaces;
import cubex2.mods.morefurnaces.tileentity.TileEntityIronFurnace;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

public class BlockMoreFurnaces extends BlockContainer
{
    public static final PropertyEnum VARIANT = PropertyEnum.create("variant", FurnaceType.class);
    public static final PropertyEnum FACING = PropertyDirection.create("facing", EnumFacing.class, EnumFacing.HORIZONTALS);
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    public BlockMoreFurnaces()
    {
        super(Material.iron);

        setDefaultState(blockState.getBaseState().withProperty(VARIANT, FurnaceType.IRON).withProperty(FACING, EnumFacing.NORTH).withProperty(ACTIVE, false));

        setUnlocalizedName("furnaceBlock");
        setHardness(2.5F);
        setSoundType(SoundType.METAL);
        setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public Block setUnlocalizedName(String name)
    {
        super.setUnlocalizedName(name);
        setRegistryName("morefurnaces", name);
        GameRegistry.register(this);
        GameRegistry.register(new ItemMoreFurnaces(this), new ResourceLocation(ModInformation.ID, name));
        return this;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(VARIANT, FurnaceType.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return ((FurnaceType) state.getValue(VARIANT)).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        IProperty[] listed = new IProperty[]{VARIANT, FACING, ACTIVE};
        return new BlockStateContainer(this, listed);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        EnumFacing facing = EnumFacing.NORTH;
        boolean active = false;

        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileEntityIronFurnace)
        {
            TileEntityIronFurnace furnace = (TileEntityIronFurnace) te;
            facing = EnumFacing.values()[furnace.getFacing()];
            active = furnace.isActive();
        }

        return state.withProperty(FACING, facing).withProperty(ACTIVE, active);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return FurnaceType.makeEntity(meta);
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return getMetaFromState(state);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileEntityIronFurnace)
            return ((TileEntityIronFurnace) te).isActive() ? (int) (0.8 * 15) : 0;
        else
            return 0;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state)
    {
        super.onBlockAdded(world, pos, state);
        this.setDefaultDirection(world, pos, state);
        world.notifyBlockUpdate(pos, state, state, 3);
    }

    private void setDefaultDirection(World world, BlockPos pos, IBlockState state)
    {
        if (!world.isRemote)
        {
            IBlockState iblockstate = world.getBlockState(pos.north());
            IBlockState iblockstate1 = world.getBlockState(pos.south());
            IBlockState iblockstate2 = world.getBlockState(pos.west());
            IBlockState iblockstate3 = world.getBlockState(pos.east());
            EnumFacing enumfacing = (EnumFacing) state.getValue(FACING);

            if (enumfacing == EnumFacing.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock())
            {
                enumfacing = EnumFacing.SOUTH;
            } else if (enumfacing == EnumFacing.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock())
            {
                enumfacing = EnumFacing.NORTH;
            } else if (enumfacing == EnumFacing.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock())
            {
                enumfacing = EnumFacing.EAST;
            } else if (enumfacing == EnumFacing.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock())
            {
                enumfacing = EnumFacing.WEST;
            }

            TileEntity te = world.getTileEntity(pos);
            if (te != null && te instanceof TileEntityIronFurnace)
            {
                ((TileEntityIronFurnace) te).setFacing((byte) enumfacing.getIndex());
                world.notifyBlockUpdate(pos, state, state, 3);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileEntityIronFurnace)
        {
            TileEntityIronFurnace tef = (TileEntityIronFurnace) te;
            if (tef.isActive())
            {
                byte facing = tef.getFacing();
                float var7 = pos.getX() + 0.5F;
                float var8 = pos.getY() + 0.0F + random.nextFloat() * 6.0F / 16.0F;
                float var9 = pos.getZ() + 0.5F;
                float var10 = 0.52F;
                float var11 = random.nextFloat() * 0.6F - 0.3F;

                if (facing == 4)
                {
                    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, var7 - var10, var8, var9 + var11, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle(EnumParticleTypes.FLAME, var7 - var10, var8, var9 + var11, 0.0D, 0.0D, 0.0D);
                } else if (facing == 5)
                {
                    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, var7 + var10, var8, var9 + var11, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle(EnumParticleTypes.FLAME, var7 + var10, var8, var9 + var11, 0.0D, 0.0D, 0.0D);
                } else if (facing == 2)
                {
                    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, var7 + var11, var8, var9 - var10, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle(EnumParticleTypes.FLAME, var7 + var11, var8, var9 - var10, 0.0D, 0.0D, 0.0D);
                } else if (facing == 3)
                {
                    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, var7 + var11, var8, var9 + var10, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle(EnumParticleTypes.FLAME, var7 + var11, var8, var9 + var10, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing facing, float f1, float f2, float f3)
    {
        FurnaceType type = (FurnaceType) state.getValue(VARIANT);
        if (type == FurnaceType.NETHERRACK && facing == EnumFacing.UP)
            return false;
        TileEntity te = world.getTileEntity(pos);

        if (te == null || !(te instanceof TileEntityIronFurnace))
            return true;

        if (world.isRemote)
            return true;

        player.openGui(MoreFurnaces.instance, ((TileEntityIronFurnace) te).getType().ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack)
    {
        EnumFacing facing = living.getHorizontalFacing().getOpposite();

        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileEntityIronFurnace)
        {
            ((TileEntityIronFurnace) te).setFacing((byte) facing.ordinal());
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof IInventory)
        {
            InventoryHelper.dropInventoryItems(world, pos, (IInventory) te);
            world.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(world, pos, state);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tabs, List<ItemStack> list)
    {
        for (FurnaceType type : FurnaceType.values())
        {
            list.add(new ItemStack(this, 1, type.ordinal()));
        }
    }

    @Override
    public boolean isFireSource(World world, BlockPos pos, EnumFacing side)
    {
        FurnaceType type = (FurnaceType) world.getBlockState(pos).getValue(VARIANT);
        if (type == FurnaceType.NETHERRACK && side == EnumFacing.UP)
            return true;
        return false;
    }
}
