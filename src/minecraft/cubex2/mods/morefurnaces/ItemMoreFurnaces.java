package cubex2.mods.morefurnaces;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemMoreFurnaces extends ItemBlock{

	public ItemMoreFurnaces(int id) {
		super(id);
		setMaxDamage(0);
		setHasSubtypes(true);
	}
	
	@Override
	public int getMetadata(int i) {
		return i;
	}
	
	@Override
	public String getItemNameIS(ItemStack stack) {
		return FurnaceType.values()[stack.getItemDamage()].name() + "_furnace";
	}

}
