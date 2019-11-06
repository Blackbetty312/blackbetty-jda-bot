package com.blackbetty;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class PapieszCommand extends ListenerAdapter {

    private Random random = new Random();
    private String mainDirMemy = "/home/pi/papieze";
    private File popeDir = new File(mainDirMemy);



    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if(event.getAuthor().isBot()) {
            return;
        }
        Message message = event.getMessage();
        String content = message.getContentRaw();
        if(content.equals("!papiesz")) {
            ArrayList<File> popes = new ArrayList<>(Arrays.asList(popeDir.listFiles(fileFilterNotDirs())));
            event.getChannel().sendFile(popes.get(random.nextInt(popes.size()))).queue();
        }
    }

    private FileFilter fileFilterNotDirs() {
        return new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        };
    }

    //TODO Add logs and errorLogs

}
