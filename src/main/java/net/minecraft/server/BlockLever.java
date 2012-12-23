package net.minecraft.server;

import org.bukkit.event.block.BlockRedstoneEvent; // CraftBukkit
// Forge start
import net.minecraftforge.common.ForgeDirection;
import static net.minecraftforge.common.ForgeDirection.*;
// Forge end

public class BlockLever extends Block {

    protected BlockLever(int i, int j) {
        super(i, j, Material.ORIENTABLE);
        this.a(CreativeModeTab.d);
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
        return 12;
    }

    public boolean canPlace(World world, int i, int j, int k, int l) {
        // Forge start
        ForgeDirection dir = ForgeDirection.getOrientation(l);
        return dir == DOWN && world.isBlockSolidOnSide(i, j + 1, k, DOWN) ? true : dir == UP && world.isBlockSolidOnSide(i, j - 1, k, UP) ? true : dir == NORTH && world.isBlockSolidOnSide(i, j, k + 1, NORTH) ? true : dir == SOUTH && world.isBlockSolidOnSide(i, j, k - 1, SOUTH) ? true : dir == WEST && world.isBlockSolidOnSide(i + 1, j, k, WEST) ? true : dir == EAST && world.isBlockSolidOnSide(i - 1, j, k, EAST);
        // Forge end
    }

    public boolean canPlace(World world, int i, int j, int k) {
        return world.isBlockSolidOnSide(i - 1, j, k, EAST) ? true : world.isBlockSolidOnSide(i + 1, j, k, WEST) ? true : world.isBlockSolidOnSide(i, j, k - 1, SOUTH) ? true : world.isBlockSolidOnSide(i, j, k + 1, NORTH) ? true : world.isBlockSolidOnSide(i, j - 1, k, UP) ? true : world.isBlockSolidOnSide(i, j + 1, k, DOWN); // Forge
    }

    public int getPlacedData(World world, int i, int j, int k, int l, float f, float f1, float f2, int i1) {
        int j1 = i1 & 8;
        int k1 = i1 & 7;

        k1 = -1;
        // Forge start
        if (l == 0 && world.isBlockSolidOnSide(i, j + 1, k, DOWN)) {
            k1 = world.random.nextBoolean() ? 0 : 7;
        }

        if (l == 1 && world.isBlockSolidOnSide(i, j - 1, k, UP)) {
            k1 = 5 + world.random.nextInt(2);
        }

        if (l == 2 && world.isBlockSolidOnSide(i, j, k + 1, NORTH)) {
            k1 = 4;
        }

        if (l == 3 && world.isBlockSolidOnSide(i, j, k - 1, SOUTH)) {
            k1 = 3;
        }

        if (l == 4 && world.isBlockSolidOnSide(i + 1, j, k, WEST)) {
            k1 = 2;
        }

        if (l == 5 && world.isBlockSolidOnSide(i - 1, j, k, EAST)) {
            k1 = 1;
        }
        // Forge end

        return k1 + j1;
    }

    public static int d(int i) {
        switch (i) {
        case 0:
            return 0;

        case 1:
            return 5;

        case 2:
            return 4;

        case 3:
            return 3;

        case 4:
            return 2;

        case 5:
            return 1;

        default:
            return -1;
        }
    }

    public void doPhysics(World world, int i, int j, int k, int l) {
        if (this.l(world, i, j, k)) {
            int i1 = world.getData(i, j, k) & 7;
            boolean flag = false;

            // Forge start
            if (!world.isBlockSolidOnSide(i - 1, j, k, EAST) && i1 == 1) {
                flag = true;
            }

            if (!world.isBlockSolidOnSide(i + 1, j, k, WEST) && i1 == 2) {
                flag = true;
            }

            if (!world.isBlockSolidOnSide(i, j, k - 1, SOUTH) && i1 == 3) {
                flag = true;
            }

            if (!world.isBlockSolidOnSide(i, j, k + 1, NORTH) && i1 == 4) {
                flag = true;
            }

            if (!world.isBlockSolidOnSide(i, j - 1, k, UP) && i1 == 5) {
                flag = true;
            }

            if (!world.isBlockSolidOnSide(i, j - 1, k, UP) && i1 == 6) {
                flag = true;
            }

            if (!world.isBlockSolidOnSide(i, j + 1, k, DOWN) && i1 == 0) {
                flag = true;
            }

            if (!world.isBlockSolidOnSide(i, j + 1, k, DOWN) && i1 == 7) {
                flag = true;
            }
            // Forge end

            if (flag) {
                this.c(world, i, j, k, world.getData(i, j, k), 0);
                world.setTypeId(i, j, k, 0);
            }
        }
    }

    private boolean l(World world, int i, int j, int k) {
        if (!this.canPlace(world, i, j, k)) {
            this.c(world, i, j, k, world.getData(i, j, k), 0);
            world.setTypeId(i, j, k, 0);
            return false;
        } else {
            return true;
        }
    }

    public void updateShape(IBlockAccess iblockaccess, int i, int j, int k) {
        int l = iblockaccess.getData(i, j, k) & 7;
        float f = 0.1875F;

        if (l == 1) {
            this.a(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
        } else if (l == 2) {
            this.a(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
        } else if (l == 3) {
            this.a(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
        } else if (l == 4) {
            this.a(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
        } else if (l != 5 && l != 6) {
            if (l == 0 || l == 7) {
                f = 0.25F;
                this.a(0.5F - f, 0.4F, 0.5F - f, 0.5F + f, 1.0F, 0.5F + f);
            }
        } else {
            f = 0.25F;
            this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.6F, 0.5F + f);
        }
    }

    public void attack(World world, int i, int j, int k, EntityHuman entityhuman) {}

    public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman, int l, float f, float f1, float f2) {
        if (world.isStatic) {
            return true;
        } else {
            int i1 = world.getData(i, j, k);
            int j1 = i1 & 7;
            int k1 = 8 - (i1 & 8);

            // CraftBukkit start - Interact Lever
            org.bukkit.block.Block block = world.getWorld().getBlockAt(i, j, k);
            int old = (k1 != 8) ? 1 : 0;
            int current = (k1 == 8) ? 1 : 0;

            BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, old, current);
            world.getServer().getPluginManager().callEvent(eventRedstone);

            if ((eventRedstone.getNewCurrent() > 0) != (k1 == 8)) {
                return true;
            }
            // CraftBukkit end

            world.setData(i, j, k, j1 + k1);
            world.e(i, j, k, i, j, k);
            world.makeSound((double) i + 0.5D, (double) j + 0.5D, (double) k + 0.5D, "random.click", 0.3F, k1 > 0 ? 0.6F : 0.5F);
            world.applyPhysics(i, j, k, this.id);
            if (j1 == 1) {
                world.applyPhysics(i - 1, j, k, this.id);
            } else if (j1 == 2) {
                world.applyPhysics(i + 1, j, k, this.id);
            } else if (j1 == 3) {
                world.applyPhysics(i, j, k - 1, this.id);
            } else if (j1 == 4) {
                world.applyPhysics(i, j, k + 1, this.id);
            } else if (j1 != 5 && j1 != 6) {
                if (j1 == 0 || j1 == 7) {
                    world.applyPhysics(i, j + 1, k, this.id);
                }
            } else {
                world.applyPhysics(i, j - 1, k, this.id);
            }

            return true;
        }
    }

    public void remove(World world, int i, int j, int k, int l, int i1) {
        if ((i1 & 8) > 0) {
            world.applyPhysics(i, j, k, this.id);
            int j1 = i1 & 7;

            if (j1 == 1) {
                world.applyPhysics(i - 1, j, k, this.id);
            } else if (j1 == 2) {
                world.applyPhysics(i + 1, j, k, this.id);
            } else if (j1 == 3) {
                world.applyPhysics(i, j, k - 1, this.id);
            } else if (j1 == 4) {
                world.applyPhysics(i, j, k + 1, this.id);
            } else if (j1 != 5 && j1 != 6) {
                if (j1 == 0 || j1 == 7) {
                    world.applyPhysics(i, j + 1, k, this.id);
                }
            } else {
                world.applyPhysics(i, j - 1, k, this.id);
            }
        }

        super.remove(world, i, j, k, l, i1);
    }

    public boolean b(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return (iblockaccess.getData(i, j, k) & 8) > 0;
    }

    public boolean c(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        int i1 = iblockaccess.getData(i, j, k);

        if ((i1 & 8) == 0) {
            return false;
        } else {
            int j1 = i1 & 7;

            return j1 == 0 && l == 0 ? true : (j1 == 7 && l == 0 ? true : (j1 == 6 && l == 1 ? true : (j1 == 5 && l == 1 ? true : (j1 == 4 && l == 2 ? true : (j1 == 3 && l == 3 ? true : (j1 == 2 && l == 4 ? true : j1 == 1 && l == 5))))));
        }
    }

    public boolean isPowerSource() {
        return true;
    }
}
