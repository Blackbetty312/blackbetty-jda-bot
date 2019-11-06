package com.blackbetty;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import javax.annotation.Nonnull;

public class AudioEcho extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        User author = message.getAuthor();
        String content = message.getContentRaw();
        Guild guild = event.getGuild();

        if(author.isBot()) {
            return;
        }
        if(content.equals("!echo")) {
            onEchoCommand(event);
        }
        if(content.equals("!leave")) {
            disconnectFrom(event);
        }

        changeActivity(content);



    }

    private void changeActivity(String content) {
        String title = content.substring(content.indexOf(" "));
        if(content.startsWith("!playing ")) {
            Main.jda.getPresence().setActivity(Activity.playing(title));
        } else if (content.startsWith("!listening ")){
            Main.jda.getPresence().setActivity(Activity.listening(title));
        } else if (content.startsWith("!watching ")) {
            Main.jda.getPresence().setActivity(Activity.watching(title));
        }
    }

    private void onEchoCommand(GuildMessageReceivedEvent event) {
        Member member = event.getMember();
        GuildVoiceState voiceState = member.getVoiceState();
        VoiceChannel voiceChannel = voiceState.getChannel();

        if(voiceChannel != null) {
            connectTo(voiceChannel);
            onConnecting(voiceChannel, event.getChannel());
        } else {
            onUnknownChannel(event.getChannel(), "your voice channel");
        }

    }

    private void disconnectFrom(GuildMessageReceivedEvent event) {
        Member member = event.getMember();
        GuildVoiceState voiceState = member.getVoiceState();
        VoiceChannel voiceChannel = voiceState.getChannel();
        Guild guild = voiceChannel.getGuild();
        AudioManager audioManager = guild.getAudioManager();
        audioManager.closeAudioConnection();
    }

    private void connectTo(VoiceChannel channel) {
        Guild guild = channel.getGuild();
        AudioManager audioManager = guild.getAudioManager();
        EchoHandler echoHandler = new EchoHandler();

        audioManager.setSendingHandler(echoHandler);
        audioManager.setReceivingHandler(echoHandler);
        audioManager.openAudioConnection(channel);
    }
    private void onConnecting(VoiceChannel channel, TextChannel textChannel) {
        textChannel.sendMessage("Connecting to " + channel.getName()).queue();

    }
    private void onUnknownChannel(MessageChannel channel, String comment)
    {
        channel.sendMessage("Unable to connect to ``" + comment + "``, no such channel!").queue();
    }
}
