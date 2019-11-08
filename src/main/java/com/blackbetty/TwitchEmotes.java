package com.blackbetty;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Set;

public class TwitchEmotes extends ListenerAdapter {
    private final int PAGES_TO_READ_FROM_API = 3;
    private HashMap<String, String> emotes = null;

    TwitchEmotes() {
        emotes = getEmotes();
        System.out.println("emotki pobrane");
    }

    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        Message message = event.getMessage();
        String content = message.getContentRaw();
        if(event.getMessage().getContentRaw().equals("!emotes list")) {
            Set<String> emoteNames = emotes.keySet();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setDescription(String.join(", ", emoteNames));
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }
        for(String emote : emotes.keySet()) {
            String link = emotes.get(emote);
            if(content.contentEquals(emote)) {
                MessageBuilder builder = new MessageBuilder();
//                InputStream file = null;
//                try {
//                    file = new URL(link).openStream();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                builder.append(file);
//                event.getChannel().sendFile(file, emote + ".png").queue();
                MessageEmbed embed = new EmbedBuilder()
                        .setTitle(message.getAuthor().getName())
                        .setThumbnail(link)
                        .build();
                event.getChannel().sendMessage(embed).queue();
                message.delete().queue();
            }
        }
    }

    private JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        URL apiPath = new URL(url);
        HttpURLConnection apiPathConnection = (HttpURLConnection) apiPath.openConnection();
        InputStream is = apiPathConnection.getInputStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private String parseCdnUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1, url.length());
    }

    private HashMap<String, String> getEmotes() {
        String mainDomain = "https://cdn.frankerfacez.com/";
        HashMap<String, String> emotes = new HashMap<>();
        try {
            JSONObject jsonObject = readJsonFromUrl("https://api.frankerfacez.com/v1/emoticons?sort=count-desc");
            JSONArray jsonArray = jsonObject.getJSONArray("emoticons");
            String actualPage = jsonObject.getJSONObject("_links").getString("self");
            for (int j = 0; j < PAGES_TO_READ_FROM_API; j++) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    emotes.put(jsonArray.getJSONObject(i).getString("name"), mainDomain + parseCdnUrl(jsonArray.getJSONObject(i).getJSONObject("urls").getString("1")));
                }
                String next = readJsonFromUrl(actualPage).getJSONObject("_links").getString("next");
                System.out.println(actualPage);
                actualPage = next;
                jsonArray = readJsonFromUrl(next).getJSONArray("emoticons");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return emotes;
    }
}
