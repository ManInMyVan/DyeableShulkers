package io.github.maninmyvan.dyeableshulkers.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public final class ShulkerDyeEvent extends Event implements Cancellable {
    private static final @NotNull HandlerList handlers = new HandlerList();

    private @Nullable DyeColor color;
    private final @NotNull Player player;
    private final Shulker shulker;

    private boolean cancelled;

    public ShulkerDyeEvent(@NotNull Shulker shulker, @NotNull Player player, @Nullable DyeColor color) {
        this.shulker = shulker;
        this.color = color;
        this.player = player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @SuppressWarnings("unused")
    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
