package net.minecraft.server;

import java.util.Random;

// CraftBukkit start
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockSpreadEvent;
// CraftBukkit end
// Forge start
import net.minecraftforge.common.ForgeDirection;
import static net.minecraftforge.common.ForgeDirection.*;
// Forge end

public class BlockFire extends Block {

    private int[] a = new int[256];
    private int[] b = new int[256];

    protected BlockFire(int i, int j) {
        super(i, j, Material.FIRE);
        this.b(true);
    }

    public void t_() {
        // Forge start
        this.b = Block.blockFlammability;
        this.a = Block.blockFireSpreadSpeed;
        // Forge end
        this.a(Block.WOOD.id, 5, 20);
        this.a(Block.WOOD_DOUBLE_STEP.id, 5, 20);
        this.a(Block.WOOD_STEP.id, 5, 20);
        this.a(Block.FENCE.id, 5, 20);
        this.a(Block.WOOD_STAIRS.id, 5, 20);
        this.a(Block.BIRCH_WOOD_STAIRS.id, 5, 20);
        this.a(Block.SPRUCE_WOOD_STAIRS.id, 5, 20);
        this.a(Block.JUNGLE_WOOD_STAIRS.id, 5, 20);
        this.a(Block.LOG.id, 5, 5);
        this.a(Block.LEAVES.id, 30, 60);
        this.a(Block.BOOKSHELF.id, 30, 20);
        this.a(Block.TNT.id, 15, 100);
        this.a(Block.LONG_GRASS.id, 60, 100);
        this.a(Block.WOOL.id, 30, 60);
        this.a(Block.VINE.id, 15, 100);
    }

    private void a(int i, int j, int k) {
        Block.setBurnProperties(i, j, k); // Forge
    }

    public AxisAlignedBB e(World world, int i, int j, int k) {
        return null;
    }

    public boolean c() {
        return false;
    }

    public boolean b() {
        return false;
    }

    public int d() {
        return 3;
    }

    public int a(Random random) {
        return 0;
    }

    public int r_() {
        return 30;
    }

    public void b(World world, int i, int j, int k, Random random) {
        if (world.getGameRules().getBoolean("doFireTick")) {
            // Forge start
            Block base = Block.byId[world.getTypeId(i, j - 1, k)];
            boolean flag = base != null && base.isFireSource(world, i, j - 1, k, world.getData(i, j - 1, k), UP);
            // Forge end
            if (world.worldProvider instanceof WorldProviderTheEnd && world.getTypeId(i, j - 1, k) == Block.BEDROCK.id) {
                flag = true;
            }

            if (!this.canPlace(world, i, j, k)) {
                fireExtinguished(world, i, j, k); // CraftBukkit - invalid place location
            }

            if (!flag && world.N() && (world.D(i, j, k) || world.D(i - 1, j, k) || world.D(i + 1, j, k) || world.D(i, j, k - 1) || world.D(i, j, k + 1))) {
                fireExtinguished(world, i, j, k); // CraftBukkit - extinguished by rain
            } else {
                int l = world.getData(i, j, k);

                if (l < 15) {
                    world.setRawData(i, j, k, l + random.nextInt(3) / 2);
                }

                world.a(i, j, k, this.id, this.r_() + random.nextInt(10));
                if (!flag && !this.l(world, i, j, k)) {
                    if (!world.v(i, j - 1, k) || l > 3) {
                        world.setTypeId(i, j, k, 0);
                    }
                } else if (!flag && !this.canBlockCatchFire((IBlockAccess) world, i, j - 1, k, UP) && l == 15 && random.nextInt(4) == 0) { // Forge
                    fireExtinguished(world, i, j, k); // CraftBukkit - burn out
                } else {
                    boolean flag1 = world.E(i, j, k);
                    byte b0 = 0;

                    if (flag1) {
                        b0 = -50;
                    }

                    // Forge start
                    this.tryToCatchBlockOnFire(world, i + 1, j, k, 300 + b0, random, l, WEST);
                    this.tryToCatchBlockOnFire(world, i - 1, j, k, 300 + b0, random, l, EAST);
                    this.tryToCatchBlockOnFire(world, i, j - 1, k, 250 + b0, random, l, UP);
                    this.tryToCatchBlockOnFire(world, i, j + 1, k, 250 + b0, random, l, DOWN);
                    this.tryToCatchBlockOnFire(world, i, j, k - 1, 300 + b0, random, l, SOUTH);
                    this.tryToCatchBlockOnFire(world, i, j, k + 1, 300 + b0, random, l, NORTH);
                    // Forge end
                    // CraftBukkit start - call to stop spread of fire
                    org.bukkit.Server server = world.getServer();
                    org.bukkit.World bworld = world.getWorld();

                    BlockIgniteEvent.IgniteCause igniteCause = BlockIgniteEvent.IgniteCause.SPREAD;
                    org.bukkit.block.Block fromBlock = bworld.getBlockAt(i, j, k);
                    // CraftBukkit end

                    for (int i1 = i - 1; i1 <= i + 1; ++i1) {
                        for (int j1 = k - 1; j1 <= k + 1; ++j1) {
                            for (int k1 = j - 1; k1 <= j + 4; ++k1) {
                                if (i1 != i || k1 != j || j1 != k) {
                                    int l1 = 100;

                                    if (k1 > j + 1) {
                                        l1 += (k1 - (j + 1)) * 100;
                                    }

                                    int i2 = this.n(world, i1, k1, j1);

                                    if (i2 > 0) {
                                        int j2 = (i2 + 40 + world.difficulty * 7) / (l + 30);

                                        if (flag1) {
                                            j2 /= 2;
                                        }

                                        if (j2 > 0 && random.nextInt(l1) <= j2 && (!world.N() || !world.D(i1, k1, j1)) && !world.D(i1 - 1, k1, k) && !world.D(i1 + 1, k1, j1) && !world.D(i1, k1, j1 - 1) && !world.D(i1, k1, j1 + 1)) {
                                            int k2 = l + random.nextInt(5) / 4;

                                            if (k2 > 15) {
                                                k2 = 15;
                                            }

                                            // CraftBukkit start - call to stop spread of fire
                                            org.bukkit.block.Block block = bworld.getBlockAt(i1, k1, j1);

                                            if (block.getTypeId() != Block.FIRE.id) {
                                                BlockIgniteEvent event = new BlockIgniteEvent(block, igniteCause, null);
                                                server.getPluginManager().callEvent(event);

                                                if (event.isCancelled()) {
                                                    continue;
                                                }

                                                org.bukkit.block.BlockState blockState = bworld.getBlockAt(i1, k1, j1).getState();
                                                blockState.setTypeId(this.id);
                                                blockState.setData(new org.bukkit.material.MaterialData(this.id, (byte) k2));

                                                BlockSpreadEvent spreadEvent = new BlockSpreadEvent(blockState.getBlock(), fromBlock, blockState);
                                                server.getPluginManager().callEvent(spreadEvent);

                                                if (!spreadEvent.isCancelled()) {
                                                    blockState.update(true);
                                                }
                                            }
                                            // CraftBukkit end
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean l() {
        return false;
    }

    @Deprecated
    private void a(World world, int i, int j, int k, int l, Random random, int i1) {
        this.tryToCatchBlockOnFire(world, i, j, k, l, random, i1, UP); // Forge
    }
    // Forge start
    private void tryToCatchBlockOnFire(World world, int i, int j, int k, int l, Random random, int i1, ForgeDirection face) {
        int j1 = 0;
        Block block = Block.byId[world.getTypeId(i, j, k)];

        if (block != null) {
            j1 = block.getFlammability(world, i, j, k, world.getData(i, j, k), face);
        }
        // Forge end
        if (random.nextInt(l) < j1) {
            boolean flag = world.getTypeId(i, j, k) == Block.TNT.id;

            // CraftBukkit start
            org.bukkit.block.Block theBlock = world.getWorld().getBlockAt(i, j, k);

            BlockBurnEvent event = new BlockBurnEvent(theBlock);
            world.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return;
            }
            // CraftBukkit end

            if (random.nextInt(i1 + 10) < 5 && !world.D(i, j, k)) {
                int k1 = i1 + random.nextInt(5) / 4;

                if (k1 > 15) {
                    k1 = 15;
                }

                world.setTypeIdAndData(i, j, k, this.id, k1);
            } else {
                world.setTypeId(i, j, k, 0);
            }

            if (flag) {
                Block.TNT.postBreak(world, i, j, k, 1);
            }
        }
    }

    private boolean l(World world, int i, int j, int k) {
        return canBlockCatchFire(world, i + 1, j, k, WEST) ? true : canBlockCatchFire(world, i - 1, j, k, EAST) ? true : canBlockCatchFire(world, i, j - 1, k, UP) ? true : canBlockCatchFire(world, i, j + 1, k, DOWN) ? true : canBlockCatchFire(world, i, j, k - 1, SOUTH) ? true : canBlockCatchFire(world, i, j, k + 1, NORTH); // Forge
    }

    private int n(World world, int i, int j, int k) {
        byte b0 = 0;

        if (!world.isEmpty(i, j, k)) {
            return 0;
        } else {
            // Forge start
            int l = this.getChanceToEncourageFire(world, i + 1, j, k, b0, WEST);

            l = this.getChanceToEncourageFire(world, i - 1, j, k, l, EAST);
            l = this.getChanceToEncourageFire(world, i, j - 1, k, l, UP);
            l = this.getChanceToEncourageFire(world, i, j + 1, k, l, DOWN);
            l = this.getChanceToEncourageFire(world, i, j, k - 1, l, SOUTH);
            l = this.getChanceToEncourageFire(world, i, j, k + 1, l, NORTH);
            return l;
            // Forge end
        }
    }

    public boolean m() {
        return false;
    }

    // Forge start
    @Deprecated
    public boolean d(IBlockAccess iblockaccess, int i, int j, int k) {
        return canBlockCatchFire(iblockaccess, i, j, k, UP);
    }

    @Deprecated
    public int d(World world, int i, int j, int k, int l) {
        return this.getChanceToEncourageFire(world, i, j, k, l, UP);
    }
    // Forge end

    public boolean canPlace(World world, int i, int j, int k) {
        return world.v(i, j - 1, k) || this.l(world, i, j, k);
    }

    public void doPhysics(World world, int i, int j, int k, int l) {
        if (!world.v(i, j - 1, k) && !this.l(world, i, j, k)) {
            fireExtinguished(world, i, j, k); // CraftBukkit - fuel block gone
        }
    }

    public void onPlace(World world, int i, int j, int k) {
        if (world.worldProvider.dimension > 0 || world.getTypeId(i, j - 1, k) != Block.OBSIDIAN.id || !Block.PORTAL.i_(world, i, j, k)) {
            if (!world.v(i, j - 1, k) && !this.l(world, i, j, k)) {
                fireExtinguished(world, i, j, k); // CraftBukkit - fuel block broke
            } else {
                world.a(i, j, k, this.id, this.r_() + world.random.nextInt(10));
            }
        }
    }

    // CraftBukkit start
    private void fireExtinguished(World world, int x, int y, int z) {
        if (org.bukkit.craftbukkit.event.CraftEventFactory.callBlockFadeEvent(world.getWorld().getBlockAt(x, y, z), 0).isCancelled() == false) {
            world.setTypeId(x, y, z, 0);
        }
    }
    // CraftBukkit end
    // Forge start
    /**
     * Side sensitive version that calls the block function.
     * 
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param face The side the fire is coming from
     * @return True if the face can catch fire.
     */
    public boolean canBlockCatchFire(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        Block block = Block.byId[world.getTypeId(x, y, z)];
        return block != null ? block.isFlammable(world, x, y, z, world.getData(x, y, z), face) : false;
    }

    /**
     * Side sensitive version that calls the block function.
     * 
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param oldChance The previous maximum chance.
     * @param face The side the fire is coming from
     * @return The chance of the block catching fire, or oldChance if it is higher
     */
    public int getChanceToEncourageFire(World world, int x, int y, int z, int oldChance, ForgeDirection face) {
        int i = 0;
        Block block = Block.byId[world.getTypeId(x, y, z)];

        if (block != null)
        {
            i = block.getFireSpreadSpeed(world, x, y, z, world.getData(x, y, z), face);
        }

        return i > oldChance ? i : oldChance;
    }
    // Forge end
}
