package n643064.heart_crystals_forge;

import n643064.heart_crystals.HealthState;
import n643064.heart_crystals.HeartCrystalItem;
import n643064.heart_crystals.HeartCrystalsCommon;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Objects;

import static n643064.heart_crystals.Config.CONFIG;
import static n643064.heart_crystals.HeartCrystalsCommon.*;


@Mod(MODID)
public class HeartCrystalsForge
{
    public HeartCrystalsForge(IEventBus modBus)
    {
        NeoForge.EVENT_BUS.register(new NeoForgeEvents());
        modBus.register(new ModEvents());
        HeartCrystalsCommon.init();
    }

    private Item HEART_CRYSTAL_DUST;
    private Item HEART_CRYSTAL_SHARD;
    private HeartCrystalItem HEART_CRYSTAL;
    private LootPool SHARD_POOL;

    public class ModEvents
    {
        @SubscribeEvent
        public void register(RegisterEvent event)
        {
            System.out.println("register event");
            event.register(BuiltInRegistries.ITEM.key(), helper ->
            {
                HEART_CRYSTAL_DUST = new Item(new Item.Properties()
                    .rarity(Rarity.UNCOMMON)
                    .stacksTo(64));

                HEART_CRYSTAL_SHARD = new Item(new Item.Properties()
                        .rarity(Rarity.RARE)
                        .stacksTo(64));
                HEART_CRYSTAL = new HeartCrystalItem(new Item.Properties()
                        .rarity(Rarity.EPIC)
                        .stacksTo(64));

                helper.register(ResourceLocation.fromNamespaceAndPath(MODID, "heart_crystal_dust"), HEART_CRYSTAL_DUST);
                helper.register(ResourceLocation.fromNamespaceAndPath(MODID, "heart_crystal_shard"), HEART_CRYSTAL_SHARD);
                helper.register(ResourceLocation.fromNamespaceAndPath(MODID, "heart_crystal"), HEART_CRYSTAL);
                SHARD_POOL = new LootPool.Builder().add(LootItem.lootTableItem(HEART_CRYSTAL_SHARD)).setRolls(UniformGenerator.between(0, 4)).build();
            });
        }

        @SubscribeEvent
        public void creativeModeTab(BuildCreativeModeTabContentsEvent event)
        {
            if (event.getTabKey() == CreativeModeTabs.INGREDIENTS)
            {
                event.accept(HEART_CRYSTAL_DUST);
                event.accept(HEART_CRYSTAL_SHARD);
                event.accept(HEART_CRYSTAL);
            }
        }
    }

    public class NeoForgeEvents
    {
        @SubscribeEvent
        public void loadLootTables(LootTableLoadEvent event)
        {
            //System.out.println("Load loot");
            final ResourceLocation name = event.getName();
            if (CONFIG.addHeartCrystalShardsToDungeonLoot() && DUNGEON_LOOT.contains(name))
            {
                event.getTable().addPool(SHARD_POOL);
            }
        }

        @SubscribeEvent
        public void loadEntity(EntityJoinLevelEvent event)
        {
            if (!(event.getEntity() instanceof Player player))
            {
                return;
            }
            final MinecraftServer server = event.getEntity().getServer();
            if (server == null) return;
            final HealthState state = HealthState.get(server);
            final String s = player.getScoreboardName();
            if (!state.map.containsKey(s))
            {
                setupNewPlayer(s, state, player);
            }
        }

        @SubscribeEvent
        public void copyFrom(PlayerEvent.Clone event)
        {
            if (event.isWasDeath())
            {
                final Player p = event.getEntity();
                final HealthState state = HealthState.get(Objects.requireNonNull(p.getServer()));
                final String s = p.getScoreboardName();
                final int v;
                if (!state.map.containsKey(s))
                {
                    setupNewPlayer(s, state, p);
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
                Objects.requireNonNull(p.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(v);
                p.setHealth(v);
            }
        }
    }
}