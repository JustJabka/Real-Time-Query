package justjabka.realTimeQuery;

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

import java.time.DayOfWeek;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

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

        DayOfWeek day = getDateTime().getDayOfWeek();

        if (sender instanceof Player) {
            Locale senderLocale = ((Player) sender).locale();
            String dayFormatted = day.getDisplayName(TextStyle.FULL, senderLocale);

            sender.sendRichMessage("The day is <day>", Placeholder.component("day", () -> Component.text(dayFormatted)));
        }

        return day.getValue();
    }

    private int queryTime(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();

        final DateTimeFormatter TIME_FORMAT_MESSAGE = DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT);
        final DateTimeFormatter TIME_FORMAT_RETURN = DateTimeFormatter.ofPattern("HHmm", Locale.ROOT);

        int time = Integer.parseInt(TIME_FORMAT_RETURN.format(getDateTime()));

        if (sender instanceof Player) {
            String timeFormatted = TIME_FORMAT_MESSAGE.format(getDateTime());

            sender.sendRichMessage("The time is <time>", Placeholder.component("time", () -> Component.text(timeFormatted)));
        }

        return time;
    }

    private ZonedDateTime getDateTime() {
        return ZonedDateTime.now(ZoneOffset.UTC);
    }
}
