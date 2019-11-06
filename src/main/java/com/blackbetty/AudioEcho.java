package com.blackbetty;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class AudioEcho extends ListenerAdapter {



    private void onEchoCommand(GuildMessageReceivedEvent event) {
        Member member = event.getMember();
        GuildVoiceState voiceState = member.getVoiceState();
        VoiceChannel voiceChannel = voiceState.getChannel();



    }

    private void connectTo(VoiceChannel channel) {
        Guild guild = channel.getGuild();
        AudioManager audioManager = guild.getAudioManager();
        EchoHandler echoHandler = new EchoHandler();

        audioManager.setSendingHandler(echoHandler);
        audioManager.setReceivingHandler(echoHandler);
        audioManager.openAudioConnection(channel);
    }
}
