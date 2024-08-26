package io.github.maninmyvan.dyeableshulkers;

import io.github.maninmyvan.dyeableshulkers.event.ShulkerDyeEvent;
import io.github.maninmyvan.dyeableshulkers.utils.Version;
import org.bukkit.*;
import org.bukkit.entity.*;
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
import static org.bukkit.inventory.EquipmentSlot.HAND;
import static org.bukkit.inventory.EquipmentSlot.OFF_HAND;

public class DyeableShulkers extends JavaPlugin implements Listener {
    private final static Version server;

    static {
        String ver = Bukkit.getBukkitVersion().split("-")[0];
        String[] split = ver.split("\\.");

        // can sometimes not exist
        int patch = 0;
        try {
            patch = Integer.decode(split[2]);
        } catch (ArrayIndexOutOfBoundsException ignored) {}

        server = new Version(Integer.decode(split[0]), Integer.decode(split[1]), patch);
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

    @EventHandler(priority = EventPriority.MONITOR)
    public static void onInteractEntity(@NotNull PlayerInteractEntityEvent event) {
        if (event.isCancelled()
                || event.getRightClicked().getType() != SHULKER
                || event.getPlayer().getGameMode() == SPECTATOR
        ) return;

        if (!event.getPlayer().hasPermission("dyeableshulkers.dye")) {
            return; // the player does not have permission to dye shulkers
        }

        if (event.getHand() == OFF_HAND) {
            if (getColorOrNull(event.getPlayer().getInventory().getItemInMainHand()) != null) {
                return; // the client can send an interact on mainhand and offhand, ignore offhand if mainhand is a dye
            }
        }

        final ItemStack itemStack = event.getHand() == HAND
                ? event.getPlayer().getInventory().getItemInMainHand()
                : event.getPlayer().getInventory().getItemInOffHand();

        final Optional<DyeColor> optional = getColorOrNull(itemStack);
        if (optional == null) {
            return; // this item will not change the color of the shulker
        }

        final DyeColor newColor = optional.orElse(server.isOlderThan(1, 13, 0) ? PURPLE : null);

        final Shulker shulker = (Shulker) event.getRightClicked();
        if (shulker.getColor() == newColor) {
            return;
        }

        final ShulkerDyeEvent dyeEvent = new ShulkerDyeEvent(shulker, event.getPlayer(), newColor);
        Bukkit.getPluginManager().callEvent(dyeEvent);

        if (dyeEvent.isCancelled()) {
            return;
        }

        shulker.setColor(dyeEvent.getColor() == null && server.isOlderThan(1, 13, 0) ? PURPLE : dyeEvent.getColor());

        if (event.getPlayer().getGameMode() != CREATIVE) {
            itemStack.setAmount(itemStack.getAmount() - 1);
        }

        if (server.isNewerThanOrEquals(1, 15, 2)) {
            if (event.getPlayer().hasPermission("dyeableshulkers.dye.swing")) {
                if (event.getHand() == HAND) event.getPlayer().swingMainHand();
                else event.getPlayer().swingOffHand();
            }

            if (server.isNewerThanOrEquals(1, 17, 0) && event.getPlayer().hasPermission("dyeableshulkers.dye.sound")) {
                shulker.getWorld().playSound(shulker.getLocation(), ITEM_DYE_USE, PLAYERS, 1, 1);
            }
        }
    }

    // TODO: allow removing colors in 1.13+
    private static @Nullable Optional<DyeColor> getColorOrNull(@NotNull ItemStack itemStack) {
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
