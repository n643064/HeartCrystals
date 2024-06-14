package n643064.heart_crystals;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

import java.util.HashSet;
import java.util.Objects;

import static n643064.heart_crystals.Config.CONFIG;


public class HeartCrystalsCommon
{
	public static final String MODID = "heart_crystals";

	public static final HashSet<ResourceLocation> DUNGEON_LOOT = new HashSet<>();
	static
	{
		DUNGEON_LOOT.add(BuiltInLootTables.ABANDONED_MINESHAFT);
		DUNGEON_LOOT.add(BuiltInLootTables.SIMPLE_DUNGEON);
		DUNGEON_LOOT.add(BuiltInLootTables.BURIED_TREASURE);
		DUNGEON_LOOT.add(BuiltInLootTables.END_CITY_TREASURE);
		DUNGEON_LOOT.add(BuiltInLootTables.DESERT_PYRAMID);
		DUNGEON_LOOT.add(BuiltInLootTables.JUNGLE_TEMPLE);
		DUNGEON_LOOT.add(BuiltInLootTables.WOODLAND_MANSION);
		DUNGEON_LOOT.add(BuiltInLootTables.BASTION_BRIDGE);
		DUNGEON_LOOT.add(BuiltInLootTables.BASTION_TREASURE);
		DUNGEON_LOOT.add(BuiltInLootTables.BASTION_OTHER);
	}

	public static void setupNewPlayer(String name, HealthState state, Player player)
	{
		state.map.put(name, CONFIG.starterLife());
		state.setDirty();
		Objects.requireNonNull(player.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(CONFIG.starterLife());
		player.setHealth(CONFIG.starterLife());
	}


	public static void init()
	{
		Config.setup();
	}
}
