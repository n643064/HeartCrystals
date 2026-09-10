package n643064.heart_crystals;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static n643064.heart_crystals.HeartCrystalsCommon.MODID;


public class HealthData extends SavedData
{

    public static final String ID = MODID + " HealthState";

    public final HashMap<String, Integer> map;
    public HealthData()
    {
         map = new HashMap<>();
    }

    protected static Codec<HealthData> CODEC = Codec.of(new Encoder<>()
    {
        @Override
        public <T> DataResult<T> encode(HealthData healthData, DynamicOps<T> dynamicOps, T t)
        {
            final Map<T, T> targetMap = new HashMap<>();
            healthData.map.forEach((k, v) ->
                    targetMap.put(dynamicOps.createString(k), dynamicOps.createInt(v)));

            return DataResult.success(dynamicOps.createMap(targetMap));
        }
    }, new Decoder<>()
    {
        @Override
        public <T> DataResult<Pair<HealthData, T>> decode(DynamicOps<T> dynamicOps, T t)
        {
            final HealthData healthData = new HealthData();
            if (t != null)
                dynamicOps.getMap(t).getOrThrow().entries().forEach(p ->
                        healthData.map.put(dynamicOps.getStringValue(p.getFirst()).getOrThrow(), (Integer) dynamicOps.getNumberValue(p.getSecond()).getOrThrow()));

            return DataResult.success(Pair.of(healthData, t));
        }
    });

    private static final SavedDataType<@NotNull HealthData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(MODID, "health_data"),
            HealthData::new,
            CODEC,
            null
    );


    public static HealthData get(ServerLevel serverLevel)
    {
        if (serverLevel == null)
            return new HealthData();
        return serverLevel.getDataStorage().computeIfAbsent(TYPE);
    }

}
