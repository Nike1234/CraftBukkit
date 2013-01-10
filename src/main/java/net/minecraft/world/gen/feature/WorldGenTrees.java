package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling.TreeGenerator;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

import org.bukkit.BlockChangeDelegate; // CraftBukkit

public class WorldGenTrees extends WorldGenerator implements net.minecraft.block.BlockSapling.TreeGenerator   // CraftBukkit add interface
{
    /** The minimum height of a generated tree. */
    private final int minTreeHeight;

    /** True if this tree should grow Vines. */
    private final boolean vinesGrow;

    /** The metadata value of the wood to use in tree generation. */
    private final int metaWood;

    /** The metadata value of the leaves to use in tree generation. */
    private final int metaLeaves;

    public WorldGenTrees(boolean par1)
    {
        this(par1, 4, 0, 0, false);
    }

    public WorldGenTrees(boolean par1, int par2, int par3, int par4, boolean par5)
    {
        super(par1);
        this.minTreeHeight = par2;
        this.metaWood = par3;
        this.metaLeaves = par4;
        this.vinesGrow = par5;
    }

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        // CraftBukkit start - moved to generate
        return this.generate((BlockChangeDelegate) par1World, par2Random, par3, par4, par5);
    }

    public boolean generate(BlockChangeDelegate world, Random random, int i, int j, int k)
    {
        // CraftBukkit end
        int l = random.nextInt(3) + this.minTreeHeight;
        boolean flag = true;

        if (j >= 1 && j + l + 1 <= 256)
        {
            int i1;
            byte b0;
            int j1;
            int k1;

            for (i1 = j; i1 <= j + 1 + l; ++i1)
            {
                b0 = 1;

                if (i1 == j)
                {
                    b0 = 0;
                }

                if (i1 >= j + 1 + l - 2)
                {
                    b0 = 2;
                }

                for (int l1 = i - b0; l1 <= i + b0 && flag; ++l1)
                {
                    for (j1 = k - b0; j1 <= k + b0 && flag; ++j1)
                    {
                        if (i1 >= 0 && i1 < 256)
                        {
                            k1 = world.getTypeId(l1, i1, j1);

                            if (k1 != 0 && k1 != Block.leaves.blockID && k1 != Block.grass.blockID && k1 != Block.dirt.blockID && k1 != Block.wood.blockID)
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
                    this.setType(world, i, j - 1, k, Block.dirt.blockID);
                    b0 = 3;
                    byte b1 = 0;
                    int i2;
                    int j2;
                    int k2;

                    for (j1 = j - b0 + l; j1 <= j + l; ++j1)
                    {
                        k1 = j1 - (j + l);
                        i2 = b1 + 1 - k1 / 2;

                        for (j2 = i - i2; j2 <= i + i2; ++j2)
                        {
                            k2 = j2 - i;

                            for (int l2 = k - i2; l2 <= k + i2; ++l2)
                            {
                                int i3 = l2 - k;

                                if ((Math.abs(k2) != i2 || Math.abs(i3) != i2 || random.nextInt(2) != 0 && k1 != 0) && world.isEmpty(j2, j1, l2))
                                {
                                    this.setTypeAndData(world, j2, j1, l2, Block.leaves.blockID, this.metaLeaves);
                                }
                            }
                        }
                    }

                    for (j1 = 0; j1 < l; ++j1)
                    {
                        k1 = world.getTypeId(i, j + j1, k);

                        if (k1 == 0 || k1 == Block.leaves.blockID)
                        {
                            this.setTypeAndData(world, i, j + j1, k, Block.wood.blockID, this.metaWood);

                            if (this.vinesGrow && j1 > 0)
                            {
                                if (random.nextInt(3) > 0 && world.isEmpty(i - 1, j + j1, k))
                                {
                                    this.setTypeAndData(world, i - 1, j + j1, k, Block.vine.blockID, 8);
                                }

                                if (random.nextInt(3) > 0 && world.isEmpty(i + 1, j + j1, k))
                                {
                                    this.setTypeAndData(world, i + 1, j + j1, k, Block.vine.blockID, 2);
                                }

                                if (random.nextInt(3) > 0 && world.isEmpty(i, j + j1, k - 1))
                                {
                                    this.setTypeAndData(world, i, j + j1, k - 1, Block.vine.blockID, 1);
                                }

                                if (random.nextInt(3) > 0 && world.isEmpty(i, j + j1, k + 1))
                                {
                                    this.setTypeAndData(world, i, j + j1, k + 1, Block.vine.blockID, 4);
                                }
                            }
                        }
                    }

                    if (this.vinesGrow)
                    {
                        for (j1 = j - 3 + l; j1 <= j + l; ++j1)
                        {
                            k1 = j1 - (j + l);
                            i2 = 2 - k1 / 2;

                            for (j2 = i - i2; j2 <= i + i2; ++j2)
                            {
                                for (k2 = k - i2; k2 <= k + i2; ++k2)
                                {
                                    if (world.getTypeId(j2, j1, k2) == Block.leaves.blockID)
                                    {
                                        if (random.nextInt(4) == 0 && world.getTypeId(j2 - 1, j1, k2) == 0)
                                        {
                                            this.b(world, j2 - 1, j1, k2, 8);
                                        }

                                        if (random.nextInt(4) == 0 && world.getTypeId(j2 + 1, j1, k2) == 0)
                                        {
                                            this.b(world, j2 + 1, j1, k2, 2);
                                        }

                                        if (random.nextInt(4) == 0 && world.getTypeId(j2, j1, k2 - 1) == 0)
                                        {
                                            this.b(world, j2, j1, k2 - 1, 1);
                                        }

                                        if (random.nextInt(4) == 0 && world.getTypeId(j2, j1, k2 + 1) == 0)
                                        {
                                            this.b(world, j2, j1, k2 + 1, 4);
                                        }
                                    }
                                }
                            }
                        }

                        if (random.nextInt(5) == 0 && l > 5)
                        {
                            for (j1 = 0; j1 < 2; ++j1)
                            {
                                for (k1 = 0; k1 < 4; ++k1)
                                {
                                    if (random.nextInt(4 - j1) == 0)
                                    {
                                        i2 = random.nextInt(3);
                                        this.setTypeAndData(world, i + Direction.offsetX[Direction.footInvisibleFaceRemap[k1]], j + l - 5 + j1, k + Direction.offsetZ[Direction.footInvisibleFaceRemap[k1]], Block.cocoaPlant.blockID, i2 << 2 | k1);
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

    // CraftBukkit - Changed world to BlockChangeDelegate
    private void b(BlockChangeDelegate world, int i, int j, int k, int l)
    {
        this.setTypeAndData(world, i, j, k, Block.vine.blockID, l);
        int i1 = 4;

        while (true)
        {
            --j;

            if (world.getTypeId(i, j, k) != 0 || i1 <= 0)
            {
                return;
            }

            this.setTypeAndData(world, i, j, k, Block.vine.blockID, l);
            --i1;
        }
    }
}