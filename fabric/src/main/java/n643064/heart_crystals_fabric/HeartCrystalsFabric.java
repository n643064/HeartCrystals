package n643064.heart_crystals_fabric;

import n643064.heart_crystals.HealthState;
import n643064.heart_crystals.HeartCrystalItem;
import n643064.heart_crystals.HeartCrystalsCommon;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Objects;

import static n643064.heart_crystals.Config.CONFIG;
import static n643064.heart_crystals.HeartCrystalsCommon.*;

public class HeartCrystalsFabric implements ModInitializer
{

    public static final Item HEART_CRYSTAL_DUST = new Item(new Item.Properties()
            .rarity(Rarity.UNCOMMON)
            .stacksTo(64));
    public static final Item HEART_CRYSTAL_SHARD = new Item(new Item.Properties()
            .rarity(Rarity.RARE)
            .stacksTo(64));
    public static final HeartCrystalItem HEART_CRYSTAL = new HeartCrystalItem(new Item.Properties()
            .rarity(Rarity.EPIC)
            .stacksTo(64));

    @Override
    public void onInitialize()
    {
        HeartCrystalsCommon.init();
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MODID, "heart_crystal_dust"), HEART_CRYSTAL_DUST);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MODID, "heart_crystal_shard"), HEART_CRYSTAL_SHARD);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MODID, "heart_crystal"), HEART_CRYSTAL);


        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries ->
        {
            entries.accept(new ItemStack(HEART_CRYSTAL_DUST));
            entries.accept(new ItemStack(HEART_CRYSTAL_SHARD));
            entries.accept(new ItemStack(HEART_CRYSTAL));
        });

        final LootPool.Builder loot = new LootPool.Builder()
                .setRolls(UniformGenerator.between(0, 4))
                .with(LootItem.lootTableItem(HEART_CRYSTAL_SHARD).build());

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) ->
        {
            if (CONFIG.addHeartCrystalShardsToDungeonLoot() && DUNGEON_LOOT.contains(id))
            {
                tableBuilder.withPool(loot);
            }
        });


        ServerEntityEvents.ENTITY_LOAD.register((entity, world) ->
        {
            if (!(entity instanceof Player player))
            {
                return;
            }
            final HealthState state = HealthState.get(world.getServer());
            final String s = player.getScoreboardName();
            if (!state.map.containsKey(s))
            {
                setupNewPlayer(s, state, player);
            }
        });

        ServerPlayerEvents.COPY_FROM.register(((oldPlayer, newPlayer, alive) ->
        {
            final HealthState state = HealthState.get(newPlayer.server);
            final String s = newPlayer.getScoreboardName();
            final int v;
            if (!state.map.containsKey(s))
            {
                setupNewPlayer(s, state, newPlayer);
            }
            if (CONFIG.resetOnDeath())
            {
                v = CONFIG.starterLife();
                state.map.put(s, v);
            } else
            {
                v = Math.max(1, state.map.get(s) - CONFIG.lifeLostOnDeath());
                state.map.put(s, v);
            }
            Objects.requireNonNull(newPlayer.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(v);
            newPlayer.setHealth(v);
        }));
    }
}