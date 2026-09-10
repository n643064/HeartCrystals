package n643064.heart_crystals;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

import java.util.HashSet;
import java.util.Objects;

import static n643064.heart_crystals.Config.CONFIG;


public class HeartCrystalsCommon
{
	public static final String MODID = "heart_crystals";

	public static final HashSet<Identifier> DUNGEON_LOOT = new HashSet<>();
	static
	{
		DUNGEON_LOOT.add(BuiltInLootTables.ABANDONED_MINESHAFT.identifier());
		DUNGEON_LOOT.add(BuiltInLootTables.SIMPLE_DUNGEON.identifier());
		DUNGEON_LOOT.add(BuiltInLootTables.BURIED_TREASURE.identifier());
		DUNGEON_LOOT.add(BuiltInLootTables.END_CITY_TREASURE.identifier());
		DUNGEON_LOOT.add(BuiltInLootTables.DESERT_PYRAMID.identifier());
		DUNGEON_LOOT.add(BuiltInLootTables.JUNGLE_TEMPLE.identifier());
		DUNGEON_LOOT.add(BuiltInLootTables.WOODLAND_MANSION.identifier());
		DUNGEON_LOOT.add(BuiltInLootTables.BASTION_BRIDGE.identifier());
		DUNGEON_LOOT.add(BuiltInLootTables.BASTION_TREASURE.identifier());
		DUNGEON_LOOT.add(BuiltInLootTables.BASTION_OTHER.identifier());
	}

	public static void setupNewPlayer(String name, HealthData state, Player player)
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
