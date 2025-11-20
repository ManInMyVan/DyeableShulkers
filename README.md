# Features

With this plugin, shulkers can be dyed by interacting with them while holding any dye. Shulkers cannot be undyed on 1.13+ servers because I'm not sure how this mechanic should work. (If you have an idea, you can let me know by opening a GitHub issue proposing your idea.)

Dyeing a shulker requires the `dyeableshulkers.dye` permission.

On 1.15.2+ servers, the player will swing their hand upon dyeing a shulker. This can be disabled on a per-player basis by revoking the `dyeableshulkers.dye.swing` permission.

On 1.17+ servers, the `item.dye.use` sound will play upon dyeing a shulker. This can be disabled on a per-player basis by revoking the `dyeableshulkers.dye.sound` permission.

# API
The plugin calls `io.github.maninmyvan.dyeableshulkers.event.ShulkerDyeEvent` when a player successfully dyes a shulker. This event is cancellable.
The shulker that was dyed can be obtained with `event.getShulker()`, and the player that dyed the shulker with `event.getPlayer()`. The color that the shulker will be set to can be obtained with `event.getColor()` and changed with `event.setColor(color)`.
