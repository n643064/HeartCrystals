package n643064.heart_crystals;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

import java.util.Objects;

import static n643064.heart_crystals.Config.CONFIG;
import static n643064.heart_crystals.HeartCrystalsCommon.*;

public class HeartCrystalsFabric implements ModInitializer
{

    public static final Item HEART_CRYSTAL_DUST = new Item(new Item.Properties()
            .rarity(Rarity.UNCOMMON)
            .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MODID, "heart_crystal_dust")))
            .stacksTo(64));
    public static final Item HEART_CRYSTAL_SHARD = new Item(new Item.Properties()
            .rarity(Rarity.RARE)
            .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MODID, "heart_crystal_shard")))
            .stacksTo(64));
    public static final HeartCrystalItem HEART_CRYSTAL = new HeartCrystalItem(new Item.Properties()
            .rarity(Rarity.EPIC)
            .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MODID, "heart_crystal")))
            .stacksTo(64));

    @Override
    public void onInitialize()
    {
        HeartCrystalsCommon.init();
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(MODID, "heart_crystal_dust"), HEART_CRYSTAL_DUST);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(MODID, "heart_crystal_shard"), HEART_CRYSTAL_SHARD);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(MODID, "heart_crystal"), HEART_CRYSTAL);


        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(entries ->
        {
            entries.accept(new ItemStack(HEART_CRYSTAL_DUST));
            entries.accept(new ItemStack(HEART_CRYSTAL_SHARD));
            entries.accept(new ItemStack(HEART_CRYSTAL));
        });

        final LootPool.Builder loot = new LootPool.Builder()
                .setRolls(ContextIntProviders.between(0, 4))
                .add(LootItem.lootTableItem(HEART_CRYSTAL_SHARD).build());

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) ->
        {
            if (CONFIG.addHeartCrystalShardsToDungeonLoot() && DUNGEON_LOOT.contains(key))
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
            final HealthData state = HealthData.get(world);
            final String s = player.getScoreboardName();
            if (!state.map.containsKey(s))
            {
                setupNewPlayer(s, state, player);
            }
        });

        ServerPlayerEvents.COPY_FROM.register(((oldPlayer, newPlayer, alive) ->
        {
            final HealthData state = HealthData.get(newPlayer.level());
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