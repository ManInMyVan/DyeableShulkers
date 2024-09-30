package io.github.maninmyvan.dyeableshulkers;

import io.github.maninmyvan.dyeableshulkers.event.ShulkerDyeEvent;
import io.github.maninmyvan.dyeableshulkers.utils.Version;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static org.bukkit.DyeColor.*;
import static org.bukkit.GameMode.CREATIVE;
import static org.bukkit.GameMode.SPECTATOR;
import static org.bukkit.Sound.ITEM_DYE_USE;
import static org.bukkit.SoundCategory.PLAYERS;
import static org.bukkit.entity.EntityType.SHULKER;
import static org.bukkit.inventory.EquipmentSlot.OFF_HAND;

@SuppressWarnings("OptionalAssignedToNull")
public class DyeableShulkers extends JavaPlugin implements Listener {
    private static final Version server;
    private static final DyeColor defaultColor;

    static {
        String ver = Bukkit.getBukkitVersion().split("-")[0];
        String[] split = ver.split("\\.");

        // can sometimes not exist
        int patch = 0;
        try {
            patch = Integer.decode(split[2]);
        } catch (ArrayIndexOutOfBoundsException ignored) {}

        server = new Version(Integer.decode(split[0]), Integer.decode(split[1]), patch);
        defaultColor = server.isOlderThan(1, 13, 0) ? PURPLE : null;
    }

    @Override
    public final void onEnable() {
        getLogger().info("Detected Server Version " + server);
        if (server.isOlderThan(1, 11, 0)) {
            getLogger().warning("Shulkers do not exist or are not dyeable in this version, please use 1.11+.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    public static @Nullable Optional<DyeColor> getColor(ItemStack itemStack, Shulker shulker) {
        final @Nullable Optional<DyeColor> color = getColor(itemStack);
        return color != null && color.orElse(defaultColor) != shulker.getColor() ? color : null;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public static void onInteractEntity(@NotNull PlayerInteractEntityEvent event) {
        if (event.getHand() == OFF_HAND
                || event.getRightClicked().getType() != SHULKER
                || event.getPlayer().getGameMode() == SPECTATOR
                || !event.getPlayer().hasPermission("dyeableshulkers.dye")
        ) return;

        final Shulker shulker = (Shulker) event.getRightClicked();

        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
        Optional<DyeColor> optional = getColor(itemStack);

        final boolean offhand = optional == null || optional.orElse(defaultColor) == shulker.getColor();
        if (offhand) {
            itemStack = event.getPlayer().getInventory().getItemInOffHand();
            optional = getColor(itemStack);
            if (optional == null || optional.orElse(defaultColor) == shulker.getColor()) {
                return;
            }
        }

        final ShulkerDyeEvent dyeEvent = new ShulkerDyeEvent(shulker, event.getPlayer(), optional.orElse(defaultColor));
        Bukkit.getPluginManager().callEvent(dyeEvent);

        if (dyeEvent.isCancelled()) {
            return;
        }

        shulker.setColor(dyeEvent.getColor() == null ? defaultColor : dyeEvent.getColor());

        if (event.getPlayer().getGameMode() != CREATIVE) {
            itemStack.setAmount(itemStack.getAmount() - 1);
        }

        if (server.isNewerThanOrEquals(1, 15, 2)) {
            if (event.getPlayer().hasPermission("dyeableshulkers.dye.swing")) {
                if (offhand) event.getPlayer().swingOffHand();
                else event.getPlayer().swingMainHand();
            }

            if (server.isNewerThanOrEquals(1, 17, 0) && event.getPlayer().hasPermission("dyeableshulkers.dye.sound")) {
                shulker.getWorld().playSound(shulker.getLocation(), ITEM_DYE_USE, PLAYERS, 1, 1);
            }
        }
    }

    // TODO: allow removing colors in 1.13+
    @SuppressWarnings("deprecation")
    private static @Nullable Optional<DyeColor> getColor(@NotNull ItemStack itemStack) {
        if (server.isOlderThan(1, 13, 0)) {
            if (itemStack.getType().getData() == Dye.class) {
                final DyeColor color = ((Dye) itemStack.getData()).getColor();
                return Optional.of(color == null ? BLACK : color);
            } else {
                return null;
            }
        }

        if (server.isOlderThan(1, 14, 0)) {
            switch (itemStack.getType()) {
                case BONE_MEAL: return Optional.of(WHITE);
                case INK_SAC: return Optional.of(BLACK);
                case COCOA_BEANS: return Optional.of(BROWN);
                case LAPIS_LAZULI: return Optional.of(BLUE);
            }
        }

        switch (itemStack.getType()) {
            case WHITE_DYE: return Optional.of(WHITE);
            case LIGHT_GRAY_DYE: return Optional.of(LIGHT_GRAY);
            case GRAY_DYE: return Optional.of(GRAY);
            case BLACK_DYE: return Optional.of(BLACK);
            case BROWN_DYE: return Optional.of(BROWN);
            case RED_DYE: return Optional.of(RED);
            case ORANGE_DYE: return Optional.of(ORANGE);
            case YELLOW_DYE: return Optional.of(YELLOW);
            case LIME_DYE: return Optional.of(LIME);
            case GREEN_DYE: return Optional.of(GREEN);
            case CYAN_DYE: return Optional.of(CYAN);
            case LIGHT_BLUE_DYE: return Optional.of(LIGHT_BLUE);
            case BLUE_DYE: return Optional.of(BLUE);
            case PURPLE_DYE: return Optional.of(PURPLE);
            case MAGENTA_DYE: return Optional.of(MAGENTA);
            case PINK_DYE: return Optional.of(PINK);
            default: return null;
        }
    }
}
