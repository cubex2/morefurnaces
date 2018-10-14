package cubex2.mods.morefurnaces.blocks;


import cubex2.mods.morefurnaces.FurnaceType;
import cubex2.mods.morefurnaces.MoreFurnaces;
import cubex2.mods.morefurnaces.tileentity.TileEntityIronFurnace;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
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
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockMoreFurnaces extends Block implements ITileEntityProvider
{
    public static final PropertyEnum<FurnaceType> VARIANT = PropertyEnum.create("variant", FurnaceType.class);
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    public BlockMoreFurnaces()
    {
        super(Material.IRON);

        setDefaultState(blockState.getBaseState().withProperty(VARIANT, FurnaceType.IRON).withProperty(ACTIVE, false));

        setUnlocalizedName("furnaceblock");
        setHardness(2.5F);
        setSoundType(SoundType.METAL);
        setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Override
    public Block setUnlocalizedName(String name)
    {
        super.setUnlocalizedName(name);
        setRegistryName("morefurnaces", name);
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
        return state.getValue(VARIANT).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        IProperty[] listed = new IProperty[] {VARIANT, FACING, ACTIVE};
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
            facing = EnumFacing.getFront(furnace.getFacing());
            if (facing == EnumFacing.DOWN || facing == EnumFacing.UP)
                facing = EnumFacing.NORTH;
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
    }

    private void setDefaultDirection(World world, BlockPos pos, IBlockState state)
    {
        if (!world.isRemote)
        {
            EnumFacing enumfacing = state.getValue(FACING);

            TileEntity te = world.getTileEntity(pos);
            if (te != null && te instanceof TileEntityIronFurnace)
            {
                enumfacing = EnumFacing.getFront(((TileEntityIronFurnace) te).getFacing());
            }

            IBlockState iblockstate = world.getBlockState(pos.north());
            IBlockState iblockstate1 = world.getBlockState(pos.south());
            IBlockState iblockstate2 = world.getBlockState(pos.west());
            IBlockState iblockstate3 = world.getBlockState(pos.east());


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

            if (te != null && te instanceof TileEntityIronFurnace)
            {
                ((TileEntityIronFurnace) te).setFacing((byte) enumfacing.ordinal());
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
                float x = pos.getX() + 0.5F;
                float y = pos.getY() + 0.0F + random.nextFloat() * 6.0F / 16.0F;
                float z = pos.getZ() + 0.5F;
                float var10 = 0.52F;
                float var11 = random.nextFloat() * 0.6F - 0.3F;

                if (facing == 4)
                {
                    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x - var10, y, z + var11, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle(EnumParticleTypes.FLAME, x - var10, y, z + var11, 0.0D, 0.0D, 0.0D);
                } else if (facing == 5)
                {
                    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + var10, y, z + var11, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle(EnumParticleTypes.FLAME, x + var10, y, z + var11, 0.0D, 0.0D, 0.0D);
                } else if (facing == 2)
                {
                    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + var11, y, z - var10, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle(EnumParticleTypes.FLAME, x + var11, y, z - var10, 0.0D, 0.0D, 0.0D);
                } else if (facing == 3)
                {
                    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + var11, y, z + var10, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle(EnumParticleTypes.FLAME, x + var11, y, z + var10, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float f1, float f2, float f3)
    {
        FurnaceType type = state.getValue(VARIANT);
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
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(FACING, placer.getHorizontalFacing().getOpposite());
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
        if (te instanceof TileEntityIronFurnace)
        {
            ((TileEntityIronFurnace) te).getItemHandler().dropAllItems(
                    world,
                    (double) pos.getX(), (double) pos.getY(), (double) pos.getZ()
            );
            world.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(world, pos, state);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tabs, NonNullList<ItemStack> list)
    {
        for (FurnaceType type : FurnaceType.values())
        {
            list.add(new ItemStack(this, 1, type.ordinal()));
        }
    }

    @Override
    public boolean isFireSource(World world, BlockPos pos, EnumFacing side)
    {
        FurnaceType type = world.getBlockState(pos).getValue(VARIANT);
        return type == FurnaceType.NETHERRACK && side == EnumFacing.UP;
    }

    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param)
    {
        super.eventReceived(state, worldIn, pos, id, param);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(id, param);
    }
}
