package cubex2.mods.morefurnaces;

import org.junit.Test;

import static org.junit.Assert.*;

public class FurnaceTypeTest
{
    @Test
    public void getFirstOutputSlot() throws Exception
    {
        assertEquals(1, FurnaceType.NETHERRACK.getFirstOutputSlot(0));
    }
}