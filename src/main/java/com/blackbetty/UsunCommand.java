package com.blackbetty;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsunCommand extends ListenerAdapter {

    private int messagesToDelete = 0;

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if(event.getAuthor().isBot()) {
            return;
        }

        Message message = event.getMessage();
        String content = message.getContentRaw();
        TextChannel textChannel = event.getChannel();

        if(content.startsWith("!usun")) {
            Pattern pattern = Pattern.compile("^!usun\\ ([1-9]|[1-9]|[1-9][0-9])$");
            Matcher matcher = pattern.matcher(content);
            messagesToDelete = 0;
            if (matcher.find()) {
                messagesToDelete = Integer.parseInt(matcher.group(1)) + 1;
            }
            if(messagesToDelete != 0) {
                MessageHistory msgHistory = new MessageHistory(textChannel);
                List<Message> msgs;
                msgs = msgHistory.retrievePast(messagesToDelete).complete();
                textChannel.deleteMessages(msgs).complete();
                event.getChannel().sendMessage(new MessageBuilder().append("Usunieto " + (messagesToDelete - 1) + " wiadomosci").build()).queue();

            }
        }
    }

    //TODO Add logs and errorLogs
}
