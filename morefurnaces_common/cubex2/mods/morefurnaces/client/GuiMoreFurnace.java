package cubex2.mods.morefurnaces.client;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;

import org.lwjgl.opengl.GL11;

import cubex2.mods.morefurnaces.ContainerIronFurnace;
import cubex2.mods.morefurnaces.FurnaceType;
import cubex2.mods.morefurnaces.TileEntityIronFurnace;

public class GuiMoreFurnace extends GuiContainer
{
	public enum GUI
	{
		IRON(176, 166, 56, 36, 176, 12, new int[] { 79 }, new int[] { 34 }, 176, 14, "/mods/morefurnaces/textures/guis/ironfurnace.png", FurnaceType.IRON),
		GOLD(216, 166, 63, 36, 216, 12, new int[] { 79 }, new int[] { 34 }, 216, 14, "/mods/morefurnaces/textures/guis/goldfurnace.png", FurnaceType.GOLD),
		DIAMOND(216, 202, 63, 54, 216, 12, new int[] { 79 }, new int[] { 52 }, 216, 14, "/mods/morefurnaces/textures/guis/diamondfurnace.png", FurnaceType.DIAMOND),
		OBSIDIAN(176, 196, 56, 66, 176, 12, new int[] {
				79,
				79 }, new int[] { 17, 43 }, 176, 14, "/mods/morefurnaces/textures/guis/obsidianfurnace.png", FurnaceType.OBSIDIAN),
		NETHERRACK(176, 166, 56, 36, 176, 12, new int[] { 79 }, new int[] { 34 }, 176, 14, "/mods/morefurnaces/textures/guis/netherrackfurnace.png", FurnaceType.NETHERRACK);
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
		private String guiTexture;
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
			this.guiTexture = guiTexture;
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
		this.xSize = type.xSize;
		this.ySize = type.ySize;
		this.furnace = invFurnace;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(type.guiTexture);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
		int var7;

		if (this.furnace.isBurning())
		{
			var7 = this.furnace.getBurnTimeRemainingScaled(12);
			this.drawTexturedModalRect(x + type.burnDestX, y + type.burnDestY + 12 - var7, type.burnSrcX, type.burnSrcY - var7, 14, var7 + 2);
		}

		for (int id = 0; id < type.cookDestX.length; id++)
		{
			var7 = this.furnace.getCookProgressScaled(id, 24);
			this.drawTexturedModalRect(x + type.cookDestX[id], y + type.cookDestY[id], type.cookSrcX, type.cookSrcY, var7 + 1, 16);
		}
	}
}
