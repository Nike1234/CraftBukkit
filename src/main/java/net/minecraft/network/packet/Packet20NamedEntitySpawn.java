package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

import java.io.IOException; // CraftBukkit

public class Packet20NamedEntitySpawn extends Packet
{
    /** The entity ID, in this case it's the player ID. */
    public int entityId;

    /** The player's name. */
    public String name;

    /** The player's X position. */
    public int xPosition;

    /** The player's Y position. */
    public int yPosition;

    /** The player's Z position. */
    public int zPosition;

    /** The player's rotation. */
    public byte rotation;

    /** The player's pitch. */
    public byte pitch;

    /** The current item the player is holding. */
    public int currentItem;
    private DataWatcher metadata;
    private List field_73517_j;

    public Packet20NamedEntitySpawn() {}

    public Packet20NamedEntitySpawn(EntityPlayer par1EntityPlayer)
    {
        this.entityId = par1EntityPlayer.entityId;

        // CraftBukkit start - limit name length to 16 characters
        if (par1EntityPlayer.username.length() > 16)
        {
            this.name = par1EntityPlayer.username.substring(0, 16);
        }
        else
        {
            this.name = par1EntityPlayer.username;
        }

        // CraftBukkit end
        this.xPosition = MathHelper.floor_double(par1EntityPlayer.posX * 32.0D);
        this.yPosition = MathHelper.floor_double(par1EntityPlayer.posY * 32.0D);
        this.zPosition = MathHelper.floor_double(par1EntityPlayer.posZ * 32.0D);
        this.rotation = (byte)((int)(par1EntityPlayer.rotationYaw * 256.0F / 360.0F));
        this.pitch = (byte)((int)(par1EntityPlayer.rotationPitch * 256.0F / 360.0F));
        ItemStack var2 = par1EntityPlayer.inventory.getCurrentItem();
        this.currentItem = var2 == null ? 0 : var2.itemID;
        this.metadata = par1EntityPlayer.getDataWatcher();
    }

    public void readPacketData(DataInputStream par1DataInputStream) throws IOException   // CraftBukkit
    {
        this.entityId = par1DataInputStream.readInt();
        this.name = readString(par1DataInputStream, 16);
        this.xPosition = par1DataInputStream.readInt();
        this.yPosition = par1DataInputStream.readInt();
        this.zPosition = par1DataInputStream.readInt();
        this.rotation = par1DataInputStream.readByte();
        this.pitch = par1DataInputStream.readByte();
        this.currentItem = par1DataInputStream.readShort();
        this.field_73517_j = DataWatcher.readWatchableObjects(par1DataInputStream);
    }

    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException   // CraftBukkit
    {
        par1DataOutputStream.writeInt(this.entityId);
        writeString(this.name, par1DataOutputStream);
        par1DataOutputStream.writeInt(this.xPosition);
        par1DataOutputStream.writeInt(this.yPosition);
        par1DataOutputStream.writeInt(this.zPosition);
        par1DataOutputStream.writeByte(this.rotation);
        par1DataOutputStream.writeByte(this.pitch);
        par1DataOutputStream.writeShort(this.currentItem);
        this.metadata.writeWatchableObjects(par1DataOutputStream);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleNamedEntitySpawn(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 28;
    }
}