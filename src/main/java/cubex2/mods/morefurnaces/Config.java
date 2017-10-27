package cubex2.mods.morefurnaces;

import com.google.common.collect.Maps;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.Map;

public class Config
{
    private static final Map<FurnaceType, Integer> furanceSpeeds = Maps.newHashMap();
    private static final Map<FurnaceType, Float> consumptionRates = Maps.newHashMap();

    public static void init(File configFile)
    {
        Configuration config = new Configuration(configFile);
        try
        {
            config.load();

            for (FurnaceType type : FurnaceType.values())
            {
                int speed = config.get("General", type.name().toLowerCase() + "FurnaceSpeed", type.speed).getInt(type.speed);
                float consumptionRate = (float) config.get("General", type.name().toLowerCase() + "FurnaceConsumptionRate", type.consumptionRate).getDouble(type.consumptionRate);

                furanceSpeeds.put(type, speed);
                consumptionRates.put(type, consumptionRate);
            }
        } finally
        {
            config.save();
        }
    }

    public static int getFurnaceSpeed(FurnaceType type)
    {
        return furanceSpeeds.getOrDefault(type, type.speed);
    }

    public static float getConsumptionRate(FurnaceType type)
    {
        return consumptionRates.getOrDefault(type, type.consumptionRate);
    }
}
