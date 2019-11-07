package com.blackbetty;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.UUID;

public class KomiksCommand extends ListenerAdapter {

    private final String komiksDirPath = "/home/pi/komiks/";

    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        String content = message.getContentRaw();
        if(!message.getAttachments().isEmpty() && content.equals("!komiks")) {
            Message.Attachment msgAttachment = message.getAttachments().get(0);
            String ext = msgAttachment.getFileName().substring(msgAttachment.getFileName().lastIndexOf("."));
            Long time = System.currentTimeMillis();
            String fullPath = komiksDirPath + time + ext;
            String tmp;
            message.getAttachments().get(0).downloadToFile(fullPath)
                    .thenAccept(x -> convertToComics(fullPath))
                    .thenAccept(x -> event.getChannel().sendFile(new File(komiksDirPath + "komiks-" + time + ext)).queue());
        }
    }

    private void convertToComics(String filePathName) {
        String ext = filePathName.substring(filePathName.lastIndexOf("."));
        String boundary = UUID.randomUUID().toString();
        String url = "https://face.bubble.ru/_api/face";
        File f = new File(filePathName);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        builder.addBinaryBody("image", f, ContentType.MULTIPART_FORM_DATA, "file" + ext)
                .setContentType(ContentType.MULTIPART_FORM_DATA)
                .setBoundary(boundary)
                .build();
        HttpEntity multipart = builder.build();
        httppost.setEntity(multipart);
        try {
            CloseableHttpResponse response = client.execute(httppost);
            convertInputStreamToFile(response.getEntity().getContent(), f.getName());
            client.close();
            System.out.println(response.getStatusLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void convertInputStreamToFile(InputStream is, String filename) throws IOException {
        OutputStream outputStream = null;
        try {
            File file = new File(komiksDirPath + "komiks-" + filename);
            outputStream = new FileOutputStream(file);

            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = is.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

}
