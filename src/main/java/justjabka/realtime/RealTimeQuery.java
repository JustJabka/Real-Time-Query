package justjabka.realtime;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public final class RealTimeQuery extends JavaPlugin {

    private ZoneOffset zoneOffset;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadOffset();

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            commands.register(
                    Commands.literal("queryRealTime")
                            .then(Commands.literal("day")
                                    .executes(ctx -> queryDay(ctx.getSource())))
                            .then(Commands.literal("time")
                                    .executes(ctx -> queryTime(ctx.getSource())))
                            .then(Commands.literal("reload")
                                    .requires(source -> source.getSender().hasPermission("rtq.admin"))
                                    .executes(ctx -> {
                                        reloadConfig();
                                        loadOffset();
                                        ctx.getSource().getSender().sendMessage("RTQ reloaded!");
                                        return 1;
                                    }))
                            .build(),
                    "Gets real time",
                    java.util.List.of("rtq") // Aliases
            );
        });
    }

    private void loadOffset() {
        int hours = getConfig().getInt("timezone-offset", 0);
        this.zoneOffset = ZoneOffset.ofHours(hours);
    }

    private int queryDay(@NotNull CommandSourceStack source) {
        int dayOfWeek = ZonedDateTime.now().getDayOfWeek().getValue();
        source.getSender().sendMessage("Current day of the week: " + dayOfWeek);

        return dayOfWeek;
    }

    private int queryTime(@NotNull CommandSourceStack source) {
        ZonedDateTime now = ZonedDateTime.now(zoneOffset);
        // Format HHmm (18:30 -> 1830)
        int timeFormatted = now.getHour() * 100 + now.getMinute();

        source.getSender().sendMessage("Current real time: " + timeFormatted);

        return timeFormatted;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
