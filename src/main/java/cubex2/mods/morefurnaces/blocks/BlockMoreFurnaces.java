package cubex2.mods.morefurnaces.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cubex2.mods.morefurnaces.FurnaceType;
import cubex2.mods.morefurnaces.MoreFurnaces;
import cubex2.mods.morefurnaces.tileentity.TileEntityIronFurnace;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;
import java.util.Random;

public class BlockMoreFurnaces extends BlockContainer
{
    private Random random;

    public BlockMoreFurnaces()
    {
        super(Material.iron);
        setBlockName("ironFurnace");
        setHardness(2.5F);
        setStepSound(Block.soundTypeMetal);
        random = new Random();
        setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return FurnaceType.makeEntity(meta);
    }

    @Override
    public int damageDropped(int i)
    {
        return i;
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityIronFurnace)
            return ((TileEntityIronFurnace) te).isActive() ? (int) (0.8 * 15) : 0;
        else
            return 0;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        super.onBlockAdded(world, x, y, z);
        this.setDefaultDirection(world, x, y, z);
        world.markBlockForUpdate(x, y, z);
    }

    private void setDefaultDirection(World world, int x, int y, int z)
    {
        if (!world.isRemote)
        {
            Block blockZNeg = world.getBlock(x, y, z - 1);
            Block blockZPos = world.getBlock(x, y, z + 1);
            Block blockXNeg = world.getBlock(x - 1, y, z);
            Block blockXPos = world.getBlock(x + 1, y, z);
            byte facing = 3;

            if (blockZNeg.func_149730_j() && !blockZPos.func_149730_j())
            {
                facing = 3;
            }

            if (blockZPos.func_149730_j() && !blockZNeg.func_149730_j())
            {
                facing = 2;
            }

            if (blockXNeg.func_149730_j() && !blockXPos.func_149730_j())
            {
                facing = 5;
            }

            if (blockXPos.func_149730_j() && !blockXNeg.func_149730_j())
            {
                facing = 4;
            }

            TileEntity te = world.getTileEntity(x, y, z);
            if (te != null && te instanceof TileEntityIronFurnace)
            {
                ((TileEntityIronFurnace) te).setFacing(facing);
                world.markBlockForUpdate(x, y, z);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta < FurnaceType.values().length)
        {
            FurnaceType type = FurnaceType.values()[meta];
            if (side == 0)
                return type.icons[0];
            if (side == 1)
                return type.icons[1];
            TileEntity te = world.getTileEntity(x, y, z);
            if (te != null && te instanceof TileEntityIronFurnace)
            {
                TileEntityIronFurnace furnace = (TileEntityIronFurnace) te;
                if (side != furnace.getFacing())
                    return type.icons[2];
                return type.icons[furnace.isActive() ? 3 : 4];
            }
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityIronFurnace)
        {
            TileEntityIronFurnace tef = (TileEntityIronFurnace) te;
            if (tef.isActive())
            {
                byte facing = tef.getFacing();
                float var7 = x + 0.5F;
                float var8 = y + 0.0F + random.nextFloat() * 6.0F / 16.0F;
                float var9 = z + 0.5F;
                float var10 = 0.52F;
                float var11 = random.nextFloat() * 0.6F - 0.3F;

                if (facing == 4)
                {
                    world.spawnParticle("smoke", var7 - var10, var8, var9 + var11, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle("flame", var7 - var10, var8, var9 + var11, 0.0D, 0.0D, 0.0D);
                } else if (facing == 5)
                {
                    world.spawnParticle("smoke", var7 + var10, var8, var9 + var11, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle("flame", var7 + var10, var8, var9 + var11, 0.0D, 0.0D, 0.0D);
                } else if (facing == 2)
                {
                    world.spawnParticle("smoke", var7 + var11, var8, var9 - var10, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle("flame", var7 + var11, var8, var9 - var10, 0.0D, 0.0D, 0.0D);
                } else if (facing == 3)
                {
                    world.spawnParticle("smoke", var7 + var11, var8, var9 + var10, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle("flame", var7 + var11, var8, var9 + var10, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    @Override
    public IIcon getIcon(int side, int meta)
    {
        if (meta < FurnaceType.values().length)
        {
            FurnaceType type = FurnaceType.values()[meta];
            if (side == 0)
                return type.icons[0];
            if (side == 1)
                return type.icons[1];
            if (side == 3)
                return type.icons[4];
            return type.icons[2];
        }
        return null;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float f1, float f2, float f3)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == FurnaceType.NETHERRACK.ordinal() && ForgeDirection.getOrientation(side) == ForgeDirection.UP)
            return false;
        TileEntity te = world.getTileEntity(x, y, z);

        if (te == null || !(te instanceof TileEntityIronFurnace))
            return true;

        if (world.isRemote)
            return true;

        player.openGui(MoreFurnaces.instance, ((TileEntityIronFurnace) te).getType().ordinal(), world, x, y, z);
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack)
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

        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityIronFurnace)
        {
            ((TileEntityIronFurnace) te).setFacing(furnaceFacing);
            world.markBlockForUpdate(x, y, z);
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityIronFurnace)
        {
            TileEntityIronFurnace furnace = (TileEntityIronFurnace) te;
            for (int i = 0; i < furnace.getSizeInventory(); ++i)
            {
                ItemStack stack = furnace.getStackInSlot(i);

                if (stack != null)
                {
                    float var10 = random.nextFloat() * 0.8F + 0.1F;
                    float var11 = random.nextFloat() * 0.8F + 0.1F;
                    float var12 = random.nextFloat() * 0.8F + 0.1F;

                    while (stack.stackSize > 0)
                    {
                        int var13 = random.nextInt(21) + 10;

                        if (var13 > stack.stackSize)
                        {
                            var13 = stack.stackSize;
                        }

                        stack.stackSize -= var13;
                        EntityItem var14 = new EntityItem(world, x + var10, y + var11, z + var12, new ItemStack(stack.getItem(), var13, stack.getItemDamage()));

                        if (stack.hasTagCompound())
                        {
                            var14.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
                        }

                        float var15 = 0.05F;
                        var14.motionX = (float) random.nextGaussian() * var15;
                        var14.motionY = (float) random.nextGaussian() * var15 + 0.2F;
                        var14.motionZ = (float) random.nextGaussian() * var15;
                        world.spawnEntityInWorld(var14);
                    }
                }
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
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
    public boolean isFireSource(World world, int x, int y, int z, ForgeDirection side)
    {
        int metadata = world.getBlockMetadata(x, y, z);
        if (metadata == FurnaceType.NETHERRACK.ordinal() && side == ForgeDirection.UP)
            return true;
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        for (FurnaceType type : FurnaceType.values())
        {
            type.makeIcons(iconRegister);
        }
    }


}
