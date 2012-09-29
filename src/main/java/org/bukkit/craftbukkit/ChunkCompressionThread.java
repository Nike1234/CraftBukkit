package org.bukkit.craftbukkit;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.Deflater;

import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet51MapChunk;
import net.minecraft.src.Packet56MapChunks;

public final class ChunkCompressionThread implements Runnable {

    private static final ChunkCompressionThread instance = new ChunkCompressionThread();
    private static boolean isRunning = false;

    private final int QUEUE_CAPACITY = 1024 * 10;
    private final HashMap<EntityPlayerMP, Integer> queueSizePerPlayer = new HashMap<EntityPlayerMP, Integer>();
    private final BlockingQueue<QueuedPacket> packetQueue = new LinkedBlockingQueue<QueuedPacket>(QUEUE_CAPACITY);

    private final int CHUNK_SIZE = 16 * 256 * 16 * 5 / 2;
    private final int REDUCED_DEFLATE_THRESHOLD = CHUNK_SIZE / 4;
    private final int DEFLATE_LEVEL_CHUNKS = 6;
    private final int DEFLATE_LEVEL_PARTS = 1;

    private final Deflater deflater = new Deflater();
    private byte[] deflateBuffer = new byte[CHUNK_SIZE + 100];

    public static void startThread() {
        if (!isRunning) {
            isRunning = true;
            new Thread(instance).start();
        }
    }

    public void run() {
        while (true) {
            try {
                handleQueuedPacket(packetQueue.take());
            } catch (InterruptedException ie) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleQueuedPacket(QueuedPacket queuedPacket) {
        addToPlayerQueueSize(queuedPacket.player, -1);

        // Compress the packet if necessary
        if (queuedPacket.compress == 1) {
            handleMapChunk((Packet51MapChunk) queuedPacket.packet);
        } else if (queuedPacket.compress == 2) {
            handleMapChunkBulk((Packet56MapChunks) queuedPacket.packet);
        }

        sendToNetworkQueue(queuedPacket);
    }

    private void handleMapChunkBulk(Packet56MapChunks packet) {
        if (packet.field_73587_e != null) {
            return;
        }

        int dataSize = packet.field_73591_h.length;
        if (deflateBuffer.length < dataSize + 100) {
            deflateBuffer = new byte[dataSize + 100];
        }

        deflater.reset();
        deflater.setLevel(dataSize < REDUCED_DEFLATE_THRESHOLD ? DEFLATE_LEVEL_PARTS : DEFLATE_LEVEL_CHUNKS);
        deflater.setInput(packet.field_73591_h);
        deflater.finish();
        int size = deflater.deflate(deflateBuffer);
        if (size == 0) {
            size = deflater.deflate(deflateBuffer);
        }

        // copy compressed data to packet
        packet.field_73587_e = new byte[size];
        packet.field_73585_g = size;
        System.arraycopy(deflateBuffer, 0, packet.field_73587_e, 0, size);
    }

    private void handleMapChunk(Packet51MapChunk packet) {
        // If 'packet.buffer' is set then this packet has already been compressed.
        if (packet.chunkData != null) {
            return;
        }

        int dataSize = packet.field_73596_g.length;
        if (deflateBuffer.length < dataSize + 100) {
            deflateBuffer = new byte[dataSize + 100];
        }

        deflater.reset();
        deflater.setLevel(dataSize < REDUCED_DEFLATE_THRESHOLD ? DEFLATE_LEVEL_PARTS : DEFLATE_LEVEL_CHUNKS);
        deflater.setInput(packet.field_73596_g);
        deflater.finish();
        int size = deflater.deflate(deflateBuffer);
        if (size == 0) {
            size = deflater.deflate(deflateBuffer);
        }

        // copy compressed data to packet
        packet.chunkData = new byte[size];
        packet.tempLength = size;
        System.arraycopy(deflateBuffer, 0, packet.chunkData, 0, size);
    }

    private void sendToNetworkQueue(QueuedPacket queuedPacket) {
        queuedPacket.player.playerNetServerHandler.netManager.addToSendQueue(queuedPacket.packet);
    }

    public static void sendPacket(EntityPlayerMP player, Packet packet) {
        int compressType = 0;

        if (packet instanceof Packet51MapChunk) {
            compressType = 1;
        } else if (packet instanceof Packet56MapChunks) {
            compressType = 2;
        }

        instance.addQueuedPacket(new QueuedPacket(player, packet, compressType));
    }

    private void addToPlayerQueueSize(EntityPlayerMP player, int amount) {
        synchronized (queueSizePerPlayer) {
            Integer count = queueSizePerPlayer.get(player);
            amount += (count == null) ? 0 : count;
            if (amount == 0) {
                queueSizePerPlayer.remove(player);
            } else {
                queueSizePerPlayer.put(player, amount);
            }
        }
    }

    public static int getPlayerQueueSize(EntityPlayerMP player) {
        synchronized (instance.queueSizePerPlayer) {
            Integer count = instance.queueSizePerPlayer.get(player);
            return count == null ? 0 : count;
        }
    }

    private void addQueuedPacket(QueuedPacket task) {
        addToPlayerQueueSize(task.player, +1);

        while (true) {
            try {
                packetQueue.put(task);
                return;
            } catch (InterruptedException e) {
            }
        }
    }

    private static class QueuedPacket {
        final EntityPlayerMP player;
        final Packet packet;
        final int compress;

        QueuedPacket(EntityPlayerMP player, Packet packet, int compress) {
            this.player = player;
            this.packet = packet;
            this.compress = compress;
        }
    }
}