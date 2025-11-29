package io.github.maninmyvan.dyeableshulkers;

import io.github.maninmyvan.dyeableshulkers.event.ShulkerDyeEvent;
import io.github.maninmyvan.dyeableshulkers.utils.Version;
import org.bstats.bukkit.Metrics;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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
        defaultColor = server.isOlderThan(1, 13, 0) ? DyeColor.PURPLE : null;
    }

    @Override
    public final void onEnable() {
        getLogger().info("Detected Server Version " + server);
        if (server.isOlderThan(1, 11, 0)) {
            getLogger().warning("Shulkers do not exist or are not dyeable in this version, please use 1.11+.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

    try {
            new Metrics(this, 28163);
        } catch (NoClassDefFoundError ignored) {}

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public static void onInteractEntity(@NotNull PlayerInteractEntityEvent event) {
        final Player player = event.getPlayer();

        if (event.getHand() == EquipmentSlot.OFF_HAND
                || player.getGameMode() == GameMode.SPECTATOR
                || !(event.getRightClicked() instanceof Shulker)
                || !player.hasPermission("dyeableshulkers.dye")
        ) return;

        final Shulker shulker = (Shulker) event.getRightClicked();

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        Optional<DyeColor> optional = getColor(itemStack);

        final boolean offhand = optional == null || optional.orElse(defaultColor) == shulker.getColor();
        if (offhand) {
            itemStack = player.getInventory().getItemInOffHand();
            optional = getColor(itemStack);
            if (optional == null || optional.orElse(defaultColor) == shulker.getColor()) {
                return;
            }
        }

        final ShulkerDyeEvent dyeEvent = new ShulkerDyeEvent(shulker, player, optional.orElse(defaultColor));
        Bukkit.getPluginManager().callEvent(dyeEvent);

        if (dyeEvent.isCancelled()) {
            return;
        }

        shulker.setColor(dyeEvent.getColor() == null ? defaultColor : dyeEvent.getColor());

        if (player.getGameMode() != GameMode.CREATIVE) {
            itemStack.setAmount(itemStack.getAmount() - 1);
        }

        if (server.isNewerThanOrEquals(1, 15, 2)) {
            if (player.hasPermission("dyeableshulkers.dye.swing")) {
                if (offhand) player.swingOffHand(); else player.swingMainHand();
            }

            if (server.isNewerThanOrEquals(1, 17, 0) && player.hasPermission("dyeableshulkers.dye.sound")) {
                shulker.getWorld().playSound(shulker.getLocation(), Sound.ITEM_DYE_USE, SoundCategory.PLAYERS, 1, 1);
            }
        }
    }

    // TODO: allow removing colors in 1.13+
    @SuppressWarnings("deprecation")
    private static @Nullable Optional<DyeColor> getColor(@NotNull ItemStack itemStack) {
        if (server.isOlderThan(1, 13, 0)) {
            if (itemStack.getType().getData() == Dye.class) {
                final DyeColor color = ((Dye) itemStack.getData()).getColor();
                return Optional.of(color == null ? DyeColor.BLACK : color);
            } else {
                return null;
            }
        }

        if (server.isOlderThan(1, 14, 0)) {
            switch (itemStack.getType()) {
                case BONE_MEAL: return Optional.of(DyeColor.WHITE);
                case INK_SAC: return Optional.of(DyeColor.BLACK);
                case COCOA_BEANS: return Optional.of(DyeColor.BROWN);
                case LAPIS_LAZULI: return Optional.of(DyeColor.BLUE);
            }
        }

        switch (itemStack.getType()) {
            case WHITE_DYE: return Optional.of(DyeColor.WHITE);
            case LIGHT_GRAY_DYE: return Optional.of(DyeColor.LIGHT_GRAY);
            case GRAY_DYE: return Optional.of(DyeColor.GRAY);
            case BLACK_DYE: return Optional.of(DyeColor.BLACK);
            case BROWN_DYE: return Optional.of(DyeColor.BROWN);
            case RED_DYE: return Optional.of(DyeColor.RED);
            case ORANGE_DYE: return Optional.of(DyeColor.ORANGE);
            case YELLOW_DYE: return Optional.of(DyeColor.YELLOW);
            case LIME_DYE: return Optional.of(DyeColor.LIME);
            case GREEN_DYE: return Optional.of(DyeColor.GREEN);
            case CYAN_DYE: return Optional.of(DyeColor.CYAN);
            case LIGHT_BLUE_DYE: return Optional.of(DyeColor.LIGHT_BLUE);
            case BLUE_DYE: return Optional.of(DyeColor.BLUE);
            case PURPLE_DYE: return Optional.of(DyeColor.PURPLE);
            case MAGENTA_DYE: return Optional.of(DyeColor.MAGENTA);
            case PINK_DYE: return Optional.of(DyeColor.PINK);
            default: return null;
        }
    }
}
