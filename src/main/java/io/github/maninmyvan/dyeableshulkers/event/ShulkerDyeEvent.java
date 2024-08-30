package io.github.maninmyvan.dyeableshulkers.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public final class ShulkerDyeEvent extends EntityEvent implements Cancellable {
    private static final @NotNull HandlerList handlers = new HandlerList();

    @Setter
    private @Nullable DyeColor color;
    private final @NotNull Player player;

    @Setter
    private boolean cancelled;

    public ShulkerDyeEvent(@NotNull Shulker entity, @NotNull Player player, @Nullable DyeColor color) {
        super(entity);
        this.color = color;
        this.player = player;
    }

    public @NotNull Shulker getEntity() {
        return (Shulker) this.entity;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
