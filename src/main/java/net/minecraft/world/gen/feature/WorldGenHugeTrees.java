package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling.TreeGenerator;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.bukkit.BlockChangeDelegate; // CraftBukkit

public class WorldGenHugeTrees extends WorldGenerator implements net.minecraft.block.BlockSapling.TreeGenerator   // CraftBukkit add interface
{
    /** The base height of the tree */
    private final int baseHeight;

    /** Sets the metadata for the wood blocks used */
    private final int woodMetadata;

    /** Sets the metadata for the leaves used in huge trees */
    private final int leavesMetadata;

    public WorldGenHugeTrees(boolean par1, int par2, int par3, int par4)
    {
        super(par1);
        this.baseHeight = par2;
        this.woodMetadata = par3;
        this.leavesMetadata = par4;
    }

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        // CraftBukkit start - moved to generate
        return this.generate((BlockChangeDelegate) par1World, par2Random, par3, par4, par5);
    }

    public boolean generate(BlockChangeDelegate world, Random random, int i, int j, int k)
    {
        // CraftBukkit end
        int l = random.nextInt(3) + this.baseHeight;
        boolean flag = true;

        if (j >= 1 && j + l + 1 <= 256)
        {
            int i1;
            int j1;
            int k1;
            int l1;

            for (i1 = j; i1 <= j + 1 + l; ++i1)
            {
                byte b0 = 2;

                if (i1 == j)
                {
                    b0 = 1;
                }

                if (i1 >= j + 1 + l - 2)
                {
                    b0 = 2;
                }

                for (j1 = i - b0; j1 <= i + b0 && flag; ++j1)
                {
                    for (k1 = k - b0; k1 <= k + b0 && flag; ++k1)
                    {
                        if (i1 >= 0 && i1 < 256)
                        {
                            l1 = world.getTypeId(j1, i1, k1);

                            if (l1 != 0 && l1 != Block.leaves.blockID && l1 != Block.grass.blockID && l1 != Block.dirt.blockID && l1 != Block.wood.blockID && l1 != Block.sapling.blockID)
                            {
                                flag = false;
                            }
                        }
                        else
                        {
                            flag = false;
                        }
                    }
                }
            }

            if (!flag)
            {
                return false;
            }
            else
            {
                i1 = world.getTypeId(i, j - 1, k);

                if ((i1 == Block.grass.blockID || i1 == Block.dirt.blockID) && j < 256 - l - 1)
                {
                    world.setRawTypeId(i, j - 1, k, Block.dirt.blockID);
                    world.setRawTypeId(i + 1, j - 1, k, Block.dirt.blockID);
                    world.setRawTypeId(i, j - 1, k + 1, Block.dirt.blockID);
                    world.setRawTypeId(i + 1, j - 1, k + 1, Block.dirt.blockID);
                    this.a(world, i, k, j + l, 2, random);

                    for (int i2 = j + l - 2 - random.nextInt(4); i2 > j + l / 2; i2 -= 2 + random.nextInt(4))
                    {
                        float f = random.nextFloat() * 3.1415927F * 2.0F;
                        k1 = i + (int)(0.5F + MathHelper.cos(f) * 4.0F);
                        l1 = k + (int)(0.5F + MathHelper.sin(f) * 4.0F);
                        this.a(world, k1, l1, i2, 0, random);

                        for (int j2 = 0; j2 < 5; ++j2)
                        {
                            k1 = i + (int)(1.5F + MathHelper.cos(f) * (float) j2);
                            l1 = k + (int)(1.5F + MathHelper.sin(f) * (float) j2);
                            this.setTypeAndData(world, k1, i2 - 3 + j2 / 2, l1, Block.wood.blockID, this.woodMetadata);
                        }
                    }

                    for (j1 = 0; j1 < l; ++j1)
                    {
                        k1 = world.getTypeId(i, j + j1, k);

                        if (k1 == 0 || k1 == Block.leaves.blockID)
                        {
                            this.setTypeAndData(world, i, j + j1, k, Block.wood.blockID, this.woodMetadata);

                            if (j1 > 0)
                            {
                                if (random.nextInt(3) > 0 && world.isEmpty(i - 1, j + j1, k))
                                {
                                    this.setTypeAndData(world, i - 1, j + j1, k, Block.vine.blockID, 8);
                                }

                                if (random.nextInt(3) > 0 && world.isEmpty(i, j + j1, k - 1))
                                {
                                    this.setTypeAndData(world, i, j + j1, k - 1, Block.vine.blockID, 1);
                                }
                            }
                        }

                        if (j1 < l - 1)
                        {
                            k1 = world.getTypeId(i + 1, j + j1, k);

                            if (k1 == 0 || k1 == Block.leaves.blockID)
                            {
                                this.setTypeAndData(world, i + 1, j + j1, k, Block.wood.blockID, this.woodMetadata);

                                if (j1 > 0)
                                {
                                    if (random.nextInt(3) > 0 && world.isEmpty(i + 2, j + j1, k))
                                    {
                                        this.setTypeAndData(world, i + 2, j + j1, k, Block.vine.blockID, 2);
                                    }

                                    if (random.nextInt(3) > 0 && world.isEmpty(i + 1, j + j1, k - 1))
                                    {
                                        this.setTypeAndData(world, i + 1, j + j1, k - 1, Block.vine.blockID, 1);
                                    }
                                }
                            }

                            k1 = world.getTypeId(i + 1, j + j1, k + 1);

                            if (k1 == 0 || k1 == Block.leaves.blockID)
                            {
                                this.setTypeAndData(world, i + 1, j + j1, k + 1, Block.wood.blockID, this.woodMetadata);

                                if (j1 > 0)
                                {
                                    if (random.nextInt(3) > 0 && world.isEmpty(i + 2, j + j1, k + 1))
                                    {
                                        this.setTypeAndData(world, i + 2, j + j1, k + 1, Block.vine.blockID, 2);
                                    }

                                    if (random.nextInt(3) > 0 && world.isEmpty(i + 1, j + j1, k + 2))
                                    {
                                        this.setTypeAndData(world, i + 1, j + j1, k + 2, Block.vine.blockID, 4);
                                    }
                                }
                            }

                            k1 = world.getTypeId(i, j + j1, k + 1);

                            if (k1 == 0 || k1 == Block.leaves.blockID)
                            {
                                this.setTypeAndData(world, i, j + j1, k + 1, Block.wood.blockID, this.woodMetadata);

                                if (j1 > 0)
                                {
                                    if (random.nextInt(3) > 0 && world.isEmpty(i - 1, j + j1, k + 1))
                                    {
                                        this.setTypeAndData(world, i - 1, j + j1, k + 1, Block.vine.blockID, 8);
                                    }

                                    if (random.nextInt(3) > 0 && world.isEmpty(i, j + j1, k + 2))
                                    {
                                        this.setTypeAndData(world, i, j + j1, k + 2, Block.vine.blockID, 4);
                                    }
                                }
                            }
                        }
                    }

                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }
    }

    // CraftBukkit - Changed signature
    private void a(BlockChangeDelegate world, int i, int j, int k, int l, Random random)
    {
        byte b0 = 2;

        for (int i1 = k - b0; i1 <= k; ++i1)
        {
            int j1 = i1 - k;
            int k1 = l + 1 - j1;

            for (int l1 = i - k1; l1 <= i + k1 + 1; ++l1)
            {
                int i2 = l1 - i;

                for (int j2 = j - k1; j2 <= j + k1 + 1; ++j2)
                {
                    int k2 = j2 - j;

                    if ((i2 >= 0 || k2 >= 0 || i2 * i2 + k2 * k2 <= k1 * k1) && (i2 <= 0 && k2 <= 0 || i2 * i2 + k2 * k2 <= (k1 + 1) * (k1 + 1)) && (random.nextInt(4) != 0 || i2 * i2 + k2 * k2 <= (k1 - 1) * (k1 - 1)) && !Block.opaqueCubeLookup[world.getTypeId(l1, i1, j2)])
                    {
                        this.setTypeAndData(world, l1, i1, j2, Block.leaves.blockID, this.leavesMetadata);
                    }
                }
            }
        }
    }
}