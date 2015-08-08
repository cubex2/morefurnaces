package cubex2.mods.morefurnaces.blocks;


import cubex2.mods.morefurnaces.FurnaceType;
import cubex2.mods.morefurnaces.MoreFurnaces;
import cubex2.mods.morefurnaces.tileentity.TileEntityIronFurnace;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

public class BlockMoreFurnaces extends BlockContainer
{
    public static final PropertyEnum VARIANT = PropertyEnum.create("variant", FurnaceType.class);
    public static final PropertyEnum FACING = PropertyDirection.create("facing", EnumFacing.class, EnumFacing.HORIZONTALS);
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    private Random random;

    public BlockMoreFurnaces()
    {
        super(Material.iron);

        setDefaultState(blockState.getBaseState().withProperty(VARIANT, FurnaceType.IRON).withProperty(FACING, EnumFacing.NORTH).withProperty(ACTIVE, false));

        setUnlocalizedName("furnaceBlock");
        setHardness(2.5F);
        setStepSound(Block.soundTypeMetal);
        random = new Random();
        setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public int getRenderType()
    {
        return 3;
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
    protected BlockState createBlockState()
    {
        IProperty[] listed = new IProperty[]{VARIANT, FACING, ACTIVE};
        return new BlockState(this, listed);
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
    public int getLightValue(IBlockAccess world, BlockPos pos)
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
        world.markBlockForUpdate(pos);
    }

    private void setDefaultDirection(World world, BlockPos pos, IBlockState state)
    {
        if (!world.isRemote)
        {
            Block block = world.getBlockState(pos.north()).getBlock();
            Block block1 = world.getBlockState(pos.south()).getBlock();
            Block block2 = world.getBlockState(pos.west()).getBlock();
            Block block3 = world.getBlockState(pos.east()).getBlock();
            EnumFacing enumfacing = EnumFacing.SOUTH;

            if (enumfacing == EnumFacing.NORTH && block.isFullBlock() && !block1.isFullBlock())
            {
                enumfacing = EnumFacing.SOUTH;
            } else if (enumfacing == EnumFacing.SOUTH && block1.isFullBlock() && !block.isFullBlock())
            {
                enumfacing = EnumFacing.NORTH;
            } else if (enumfacing == EnumFacing.WEST && block2.isFullBlock() && !block3.isFullBlock())
            {
                enumfacing = EnumFacing.EAST;
            } else if (enumfacing == EnumFacing.EAST && block3.isFullBlock() && !block2.isFullBlock())
            {
                enumfacing = EnumFacing.WEST;
            }

            TileEntity te = world.getTileEntity(pos);
            if (te != null && te instanceof TileEntityIronFurnace)
            {
                ((TileEntityIronFurnace) te).setFacing((byte) enumfacing.getIndex());
                world.markBlockForUpdate(pos);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random random)
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
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing facing, float f1, float f2, float f3)
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
        byte furnaceFacing = 0;
        int facing = MathHelper.floor_double(living.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

        if (facing == 0)
        {
            furnaceFacing = 2;
        }

        if (facing == 1)
        {
            furnaceFacing = 5;
        }

        if (facing == 2)
        {
            furnaceFacing = 3;
        }

        if (facing == 3)
        {
            furnaceFacing = 4;
        }

        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileEntityIronFurnace)
        {
            ((TileEntityIronFurnace) te).setFacing(furnaceFacing);
            world.markBlockForUpdate(pos);
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
    public void getSubBlocks(Item item, CreativeTabs tabs, List list)
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
