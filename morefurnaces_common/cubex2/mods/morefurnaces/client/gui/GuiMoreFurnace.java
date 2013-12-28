package cubex2.mods.morefurnaces.client.gui;

import cubex2.mods.morefurnaces.FurnaceType;
import cubex2.mods.morefurnaces.inventory.ContainerIronFurnace;
import cubex2.mods.morefurnaces.tileentity.TileEntityIronFurnace;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiMoreFurnace extends GuiContainer
{
    public enum GUI
    {
        IRON(176, 166, 56, 36, 176, 12, new int[] { 79 }, new int[] { 34 }, 176, 14, "ironfurnace.png", FurnaceType.IRON),
        GOLD(216, 166, 63, 36, 216, 12, new int[] { 79 }, new int[] { 34 }, 216, 14, "goldfurnace.png", FurnaceType.GOLD),
        DIAMOND(216, 202, 63, 54, 216, 12, new int[] { 79 }, new int[] { 52 }, 216, 14, "diamondfurnace.png", FurnaceType.DIAMOND),
        OBSIDIAN(176, 196, 56, 66, 176, 12, new int[] {
                79,
                79 }, new int[] { 17, 43 }, 176, 14, "obsidianfurnace.png", FurnaceType.OBSIDIAN),
        NETHERRACK(176, 166, 56, 36, 176, 12, new int[] { 79 }, new int[] { 34 }, 176, 14, "netherrackfurnace.png", FurnaceType.NETHERRACK);
        private int xSize;
        private int ySize;
        private int burnDestX;
        private int burnDestY;
        private int burnSrcX;
        private int burnSrcY;
        private int[] cookDestX;
        private int[] cookDestY;
        private int cookSrcX;
        private int cookSrcY;
        private ResourceLocation guiLocation;
        private FurnaceType mainType;

        private GUI(int xSize, int ySize, int burnDestX, int burnDestY, int burnSrcX, int burnSrcY, int[] cookDestX, int[] cookDestY, int cookSrcX, int cookSrcY, String guiTexture, FurnaceType mainType)
        {
            this.xSize = xSize;
            this.ySize = ySize;
            this.burnDestX = burnDestX;
            this.burnDestY = burnDestY;
            this.burnSrcX = burnSrcX;
            this.burnSrcY = burnSrcY;
            this.cookDestX = cookDestX;
            this.cookDestY = cookDestY;
            this.cookSrcX = cookSrcX;
            this.cookSrcY = cookSrcY;
            guiLocation = new ResourceLocation("morefurnaces", "textures/gui/" + guiTexture);
            this.mainType = mainType;
        }

        protected Container makeContainer(IInventory player, TileEntityIronFurnace furnace)
        {
            return new ContainerIronFurnace(player, furnace, mainType);
        }

        public static GuiMoreFurnace buildGui(FurnaceType type, IInventory invPlayer, TileEntityIronFurnace invFurnace)
        {
            return new GuiMoreFurnace(values()[invFurnace.getType().ordinal()], invPlayer, invFurnace);
        }
    }

    private GUI type;
    private TileEntityIronFurnace furnace;

    public GuiMoreFurnace(GUI type, IInventory invPlayer, TileEntityIronFurnace invFurnace)
    {
        super(type.makeContainer(invPlayer, invFurnace));
        this.type = type;
        xSize = type.xSize;
        ySize = type.ySize;
        furnace = invFurnace;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        // TODO
        mc.renderEngine.bindTexture(type.guiLocation);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        int var7;

        if (furnace.isBurning())
        {
            var7 = furnace.getBurnTimeRemainingScaled(12);
            this.drawTexturedModalRect(x + type.burnDestX, y + type.burnDestY + 12 - var7, type.burnSrcX, type.burnSrcY - var7, 14, var7 + 2);
        }

        for (int id = 0; id < type.cookDestX.length; id++)
        {
            var7 = furnace.getCookProgressScaled(id, 24);
            this.drawTexturedModalRect(x + type.cookDestX[id], y + type.cookDestY[id], type.cookSrcX, type.cookSrcY, var7 + 1, 16);
        }
    }
}
