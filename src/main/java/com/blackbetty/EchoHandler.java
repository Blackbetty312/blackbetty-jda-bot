package com.blackbetty;

import net.dv8tion.jda.api.audio.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EchoHandler implements AudioSendHandler, AudioReceiveHandler {

    private final Queue<byte[]> queue = new ConcurrentLinkedQueue<>();

    /*  receive handler     */
    @Override
    public boolean canReceiveCombined() {
        return queue.size() < 10;
    }

    @Override
    public void handleCombinedAudio(@Nonnull CombinedAudio combinedAudio) {
        if (combinedAudio.getUsers().isEmpty())
            return;

        byte[] data = combinedAudio.getAudioData(1.0f); // volume at 100% = 1.0 (50% = 0.5 / 55% = 0.55)
        queue.add(data);
    }

    /*      send handler    */
    @Override
    public boolean canProvide() {
        return !queue.isEmpty();
    }

    @Nullable
    @Override
    public ByteBuffer provide20MsAudio() {
        byte[] data = queue.poll();
        return data == null ? null : ByteBuffer.wrap(data);
    }

    @Override
    public boolean isOpus() {
        return false;
    }
}