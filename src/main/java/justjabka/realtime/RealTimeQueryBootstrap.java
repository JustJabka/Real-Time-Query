package justjabka.realtime;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@SuppressWarnings("UnstableApiUsage")
public class RealTimeQueryBootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            commands.register(
                    Commands.literal("queryRealTime")
                            .requires(sender -> sender.getSender().hasPermission("rtq.command.rtq"))
                            .then(Commands.literal("day")
                                    .executes(this::queryDay))
                            .then(Commands.literal("time")
                                    .executes(this::queryTime))
                            .build(),
                    "Queries real time",
                    java.util.List.of("rtq") // Aliases
            );
        });
    }

    private int queryDay(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();

        int day = getDateTime().getDayOfWeek().getValue();
        if (sender instanceof Player) {
            sender.sendRichMessage("The day is <day>", Placeholder.component("day", () -> Component.text(day)));
        }

        return day;
    }

    private int queryTime(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();

        int time = getDateTime().getHour() * 100 + getDateTime().getMinute(); // Format HHmm (18:30 -> 1830)
        if (sender instanceof Player) {
            sender.sendRichMessage("The time is <time>", Placeholder.component("time", () -> Component.text(time)));
        }

        return time;
    }

    private ZonedDateTime getDateTime() {
        return ZonedDateTime.now(ZoneOffset.UTC);
    }
}
