package n643064.heart_crystals;


import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Objects;

import static n643064.heart_crystals.Config.CONFIG;


public class HeartCrystalItem extends Item
{
    public HeartCrystalItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand)
    {
        final ItemStack stack = player.getItemInHand(interactionHand);
        if (level.isClientSide)
        {
            return InteractionResultHolder.fail(stack);
        }
        final HealthState state = HealthState.get(Objects.requireNonNull(player.getServer()));
        final String name = player.getScoreboardName();
        final int v;
        if (state.map.containsKey(name))
        {
            v = state.map.get(player.getScoreboardName()) + CONFIG.lifeIncrement();
        } else
        {
            v = CONFIG.starterLife() + CONFIG.lifeIncrement();
        }

        if (v <= CONFIG.maxLife())
        {
            state.map.put(name, v);
            state.setDirty();
            Objects.requireNonNull(player.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(Math.max(1, v));
            stack.shrink(1);
            player.playNotifySound(SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.PLAYERS, 0.8f, 2f);
            return InteractionResultHolder.success(stack);
        }
        player.displayClientMessage(Component.translatable("heart_crystals.limit_reached").withStyle(ChatFormatting.DARK_RED), true);
        return InteractionResultHolder.fail(stack);
    }


    @Override
    public boolean isFoil(ItemStack itemStack)
    {
        return true;
    }
}
