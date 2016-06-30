package cubex2.mods.morefurnaces.client.gui;

import cubex2.cxlibrary.gui.GuiCX;
import cubex2.cxlibrary.gui.GuiTexture;
import cubex2.cxlibrary.gui.control.HorizontalProgressBar;
import cubex2.cxlibrary.gui.control.ScreenContainer;
import cubex2.cxlibrary.gui.control.VerticalProgressBar;
import cubex2.mods.morefurnaces.FurnaceType;
import cubex2.mods.morefurnaces.inventory.ContainerIronFurnace;
import cubex2.mods.morefurnaces.lib.Textures;
import cubex2.mods.morefurnaces.tileentity.TileEntityIronFurnace;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class GuiMoreFurnace extends ScreenContainer
{
    public static ResourceLocation DATA_IRON = new ResourceLocation("morefurnaces", "gui/iron.json");
    public static ResourceLocation DATA_GOLD = new ResourceLocation("morefurnaces", "gui/gold.json");
    public static ResourceLocation DATA_DIAMOND = new ResourceLocation("morefurnaces", "gui/diamond.json");
    public static ResourceLocation DATA_OBSIDIAN = new ResourceLocation("morefurnaces", "gui/obsidian.json");
    public static ResourceLocation DATA_NETHERRACK = new ResourceLocation("morefurnaces", "gui/netherrack.json");

    public enum GUI
    {
        IRON(Textures.IRON, FurnaceType.IRON, DATA_IRON),
        GOLD(Textures.GOLD, FurnaceType.GOLD, DATA_GOLD),
        DIAMOND(Textures.DIAMOND, FurnaceType.DIAMOND, DATA_DIAMOND),
        OBSIDIAN(Textures.OBSIDIAN, FurnaceType.OBSIDIAN, DATA_OBSIDIAN),
        NETHERRACK(Textures.NETHERRACK, FurnaceType.NETHERRACK, DATA_NETHERRACK);

        private GuiTexture texture;
        private FurnaceType mainType;
        private ResourceLocation dataLocation;

        GUI(GuiTexture texture, FurnaceType mainType, ResourceLocation dataLocation)
        {
            this.texture = texture;
            this.mainType = mainType;
            this.dataLocation = dataLocation;
        }

        protected Container makeContainer(InventoryPlayer player, TileEntityIronFurnace furnace)
        {
            return new ContainerIronFurnace(player, furnace, mainType);
        }

        public static GuiScreen buildGui(InventoryPlayer invPlayer, TileEntityIronFurnace invFurnace)
        {
            return new GuiCX(new GuiMoreFurnace(values()[invFurnace.getType().ordinal()], invPlayer, invFurnace));
        }
    }

    private TileEntityIronFurnace furnace;

    private final HorizontalProgressBar[] cookBars;
    private final VerticalProgressBar fuelBar;

    public GuiMoreFurnace(GUI type, InventoryPlayer invPlayer, TileEntityIronFurnace invFurnace)
    {
        super(type.makeContainer(invPlayer, invFurnace), type.dataLocation);
        furnace = invFurnace;

        window.pictureBox("bg", type.texture, "bg").add();

        cookBars = new HorizontalProgressBar[type.mainType.parallelSmelting];
        for (int i = 0; i < cookBars.length; i++)
        {
            cookBars[i] = window.horizontalBar("cook" + i, type.texture, "cook").add();
        }
        fuelBar = window.verticalBar("fuel", type.texture, "fuel").add();
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        for (int i = 0; i < cookBars.length; i++)
        {
            cookBars[i].setProgress(furnace.getCookProgress(i));
        }
        fuelBar.setProgress(-1f + furnace.getBurnTimeRemaining());

        super.draw(mouseX, mouseY, partialTicks);
    }
}
