package n643064.heart_crystals;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;

import static n643064.heart_crystals.HeartCrystalsCommon.MODID;


public class HealthState extends SavedData
{

    public static final String ID = MODID + " HealthState";

    public final HashMap<String, Integer> map;
    public HealthState()
    {
         map = new HashMap<>();
    }

    @Override
    public CompoundTag save(CompoundTag nbt)
    {
        for (String s : map.keySet())
        {
            nbt.putInt(s, map.get(s));
        }
        return nbt;
    }

    public static HealthState fromNbt(CompoundTag nbt)
    {
        HealthState h = new HealthState();
        for (String s : nbt.getAllKeys())
        {
            h.map.put(s, nbt.getInt(s));
        }
        return h;
    }

    public static HealthState get(MinecraftServer server)
    {
        DimensionDataStorage manager = server.overworld().getDataStorage();
        return manager.computeIfAbsent(HealthState::fromNbt, HealthState::new, ID);
    }

}
