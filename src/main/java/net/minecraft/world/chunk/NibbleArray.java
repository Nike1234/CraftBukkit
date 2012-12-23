package net.minecraft.world.chunk;

import java.util.Arrays; // Spigot

public class NibbleArray
{
    private byte[] data; // Spigot - remove final, make private (anyone directly accessing this is broken already)

    /**
     * Log base 2 of the chunk height (128); applied as a shift on Z coordinate
     */
    private final int depthBits;

    /**
     * Log base 2 of the chunk height (128) * width (16); applied as a shift on X coordinate
     */
    private final int depthBitsPlusFour;
    // Spigot start
    private byte trivialValue;
    private byte trivialByte;
    private int length;
    private static final int LEN2K = 2048; // Universal length used right now - optimize around this
    private static final byte[][] TrivLen2k;

    static
    {
        TrivLen2k = new byte[16][];

        for (int i = 0; i < 16; i++)
        {
            TrivLen2k[i] = new byte[LEN2K];
            Arrays.fill(TrivLen2k[i], (byte)(i | (i << 4)));
        }
    }

    // Try to convert array to trivial array
    public void detectAndProcessTrivialArray()
    {
        trivialValue = (byte)(data[0] & 0xF);
        trivialByte = (byte)(trivialValue | (trivialValue << 4));

        for (int i = 0; i < data.length; i++)
        {
            if (data[i] != trivialByte)
            {
                return;
            }
        }

        // All values matches, so array is trivial
        this.length = data.length;
        this.data = null;
    }

    // Force array to non-trivial state
    public void forceToNonTrivialArray()
    {
        if (this.data == null)
        {
            this.data = new byte[this.length];

            if (this.trivialByte != 0)
            {
                Arrays.fill(this.data, this.trivialByte);
            }
        }
    }

    // Test if array is in trivial state
    public boolean isTrivialArray()
    {
        return (this.data == null);
    }

    // Get value of all elements (only valid if array is in trivial state)
    public int getTrivialArrayValue()
    {
        return this.trivialValue;
    }

    // Get logical length of byte array for nibble data (whether trivial or non-trivial)
    public int getByteLength()
    {
        if (this.data == null)
        {
            return this.length;
        }
        else
        {
            return this.data.length;
        }
    }

    // Return byte encoding of array (whether trivial or non-trivial) - returns read-only array if trivial (do not modify!)
    public byte[] getValueArray()
    {
        if (this.data != null)
        {
            return this.data;
        }
        else
        {
            byte[] rslt;

            if (this.length == LEN2K)    // All current uses are 2k long, but be safe
            {
                rslt = TrivLen2k[this.trivialValue];
            }
            else
            {
                rslt = new byte[this.length];

                if (this.trivialByte != 0)
                {
                    Arrays.fill(rslt, this.trivialByte);
                }
            }

            return rslt;
        }
    }

    // Copy byte representation of array to given offset in given byte array
    public int copyToByteArray(byte[] dest, int off)
    {
        if (this.data == null)
        {
            Arrays.fill(dest, off, off + this.length, this.trivialByte);
            return off + this.length;
        }
        else
        {
            System.arraycopy(this.data, 0, dest, off, this.data.length);
            return off + this.data.length;
        }
    }

    // Resize array to given byte length
    public void resizeArray(int len)
    {
        if (this.data == null)
        {
            this.length = len;
        }
        else if (this.data.length != len)
        {
            byte[] newa = new byte[len];
            System.arraycopy(this.data, 0, newa, 0, ((this.data.length > len) ? len : this.data.length));
            this.data = newa;
        }
    }
    // Spigot end

    public NibbleArray(int par1, int par2)
    {
        // Spigot start
        //this.a = new byte[i >> 1];
        this.data = null; // Start off as trivial value (all same zero value)
        this.length = par1 >> 1;
        this.trivialByte = this.trivialValue = 0;
        // Spigot end
        this.depthBits = par2;
        this.depthBitsPlusFour = par2 + 4;
    }

    public NibbleArray(byte[] par1ArrayOfByte, int par2)
    {
        this.data = par1ArrayOfByte;
        this.depthBits = par2;
        this.depthBitsPlusFour = par2 + 4;
        detectAndProcessTrivialArray(); // Spigot
    }

    /**
     * Returns the nibble of data corresponding to the passed in x, y, z. y is at most 6 bits, z is at most 4.
     */
    public int get(int par1, int par2, int par3)
    {
        if (this.data == null)
        {
            return this.trivialValue;    // Spigot
        }

        int var4 = par2 << this.depthBitsPlusFour | par3 << this.depthBits | par1;
        int var5 = var4 >> 1;
        int var6 = var4 & 1;
        return var6 == 0 ? this.data[var5] & 15 : this.data[var5] >> 4 & 15;
    }

    /**
     * Arguments are x, y, z, val. Sets the nibble of data at x << 11 | z << 7 | y to val.
     */
    public void set(int par1, int par2, int par3, int par4)
    {
        // Spigot start
        if (this.data == null)
        {
            if (par4 != this.trivialValue)   // Not same as trivial value, array no longer trivial
            {
                this.data = new byte[this.length];

                if (this.trivialByte != 0)
                {
                    Arrays.fill(this.data, this.trivialByte);
                }
            }
            else
            {
                return;
            }
        }

        // Spigot end
        int var5 = par2 << this.depthBitsPlusFour | par3 << this.depthBits | par1;
        int var6 = var5 >> 1;
        int var7 = var5 & 1;

        if (var7 == 0)
        {
            this.data[var6] = (byte)(this.data[var6] & 240 | par4 & 15);
        }
        else
        {
            this.data[var6] = (byte)(this.data[var6] & 15 | (par4 & 15) << 4);
        }
    }
}
