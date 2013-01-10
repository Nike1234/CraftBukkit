package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;

// CraftBukkit start
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockSpreadEvent;
// CraftBukkit end

public class BlockFire extends Block
{
    /** The chance this block will encourage nearby blocks to catch on fire */
    private int[] chanceToEncourageFire = new int[256];

    /**
     * This is an array indexed by block ID the larger the number in the array the more likely a block type will catch
     * fires
     */
    private int[] abilityToCatchFire = new int[256];

    protected BlockFire(int par1, int par2)
    {
        super(par1, par2, Material.fire);
        this.setTickRandomly(true);
    }

    /**
     * This method is called on a block after all other blocks gets already created. You can use it to reference and
     * configure something on the block that needs the others ones.
     */
    public void initializeBlock()
    {
        this.setBurnRate(Block.planks.blockID, 5, 20);
        this.setBurnRate(Block.woodDoubleSlab.blockID, 5, 20);
        this.setBurnRate(Block.woodSingleSlab.blockID, 5, 20);
        this.setBurnRate(Block.fence.blockID, 5, 20);
        this.setBurnRate(Block.stairCompactPlanks.blockID, 5, 20);
        this.setBurnRate(Block.stairsWoodBirch.blockID, 5, 20);
        this.setBurnRate(Block.stairsWoodSpruce.blockID, 5, 20);
        this.setBurnRate(Block.stairsWoodJungle.blockID, 5, 20);
        this.setBurnRate(Block.wood.blockID, 5, 5);
        this.setBurnRate(Block.leaves.blockID, 30, 60);
        this.setBurnRate(Block.bookShelf.blockID, 30, 20);
        this.setBurnRate(Block.tnt.blockID, 15, 100);
        this.setBurnRate(Block.tallGrass.blockID, 60, 100);
        this.setBurnRate(Block.cloth.blockID, 30, 60);
        this.setBurnRate(Block.vine.blockID, 15, 100);
    }

    /**
     * Sets the burn rate for a block. The larger abilityToCatchFire the more easily it will catch. The larger
     * chanceToEncourageFire the faster it will burn and spread to other blocks. Args: blockID, chanceToEncourageFire,
     * abilityToCatchFire
     */
    private void setBurnRate(int par1, int par2, int par3)
    {
        this.chanceToEncourageFire[par1] = par2;
        this.abilityToCatchFire[par1] = par3;
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return null;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 3;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random par1Random)
    {
        return 0;
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate()
    {
        return 30;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if (par1World.getGameRules().getGameRuleBooleanValue("doFireTick"))
        {
            boolean var6 = par1World.getBlockId(par2, par3 - 1, par4) == Block.netherrack.blockID;

            if (par1World.provider instanceof WorldProviderEnd && par1World.getBlockId(par2, par3 - 1, par4) == Block.bedrock.blockID)
            {
                var6 = true;
            }

            if (!this.canPlaceBlockAt(par1World, par2, par3, par4))
            {
                fireExtinguished(par1World, par2, par3, par4); // CraftBukkit - invalid place location
            }

            if (!var6 && par1World.isRaining() && (par1World.canLightningStrikeAt(par2, par3, par4) || par1World.canLightningStrikeAt(par2 - 1, par3, par4) || par1World.canLightningStrikeAt(par2 + 1, par3, par4) || par1World.canLightningStrikeAt(par2, par3, par4 - 1) || par1World.canLightningStrikeAt(par2, par3, par4 + 1)))
            {
                fireExtinguished(par1World, par2, par3, par4); // CraftBukkit - extinguished by rain
            }
            else
            {
                int var7 = par1World.getBlockMetadata(par2, par3, par4);

                if (var7 < 15)
                {
                    par1World.setBlockMetadata(par2, par3, par4, var7 + par5Random.nextInt(3) / 2);
                }

                par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate() + par5Random.nextInt(10));

                if (!var6 && !this.canNeighborBurn(par1World, par2, par3, par4))
                {
                    if (!par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4) || var7 > 3)
                    {
                        par1World.setBlockWithNotify(par2, par3, par4, 0);
                    }
                }
                else if (!var6 && !this.canBlockCatchFire((IBlockAccess) par1World, par2, par3 - 1, par4) && var7 == 15 && par5Random.nextInt(4) == 0)
                {
                    fireExtinguished(par1World, par2, par3, par4); // CraftBukkit - burn out
                }
                else
                {
                    boolean var8 = par1World.isBlockHighHumidity(par2, par3, par4);
                    byte var9 = 0;

                    if (var8)
                    {
                        var9 = -50;
                    }

                    this.tryToCatchBlockOnFire(par1World, par2 + 1, par3, par4, 300 + var9, par5Random, var7);
                    this.tryToCatchBlockOnFire(par1World, par2 - 1, par3, par4, 300 + var9, par5Random, var7);
                    this.tryToCatchBlockOnFire(par1World, par2, par3 - 1, par4, 250 + var9, par5Random, var7);
                    this.tryToCatchBlockOnFire(par1World, par2, par3 + 1, par4, 250 + var9, par5Random, var7);
                    this.tryToCatchBlockOnFire(par1World, par2, par3, par4 - 1, 300 + var9, par5Random, var7);
                    this.tryToCatchBlockOnFire(par1World, par2, par3, par4 + 1, 300 + var9, par5Random, var7);
                    // CraftBukkit start - call to stop spread of fire
                    org.bukkit.Server server = par1World.getServer();
                    org.bukkit.World bworld = par1World.getWorld();
                    BlockIgniteEvent.IgniteCause igniteCause = BlockIgniteEvent.IgniteCause.SPREAD;
                    org.bukkit.block.Block fromBlock = bworld.getBlockAt(par2, par3, par4);
                    // CraftBukkit end

                    for (int var10 = par2 - 1; var10 <= par2 + 1; ++var10)
                    {
                        for (int var11 = par4 - 1; var11 <= par4 + 1; ++var11)
                        {
                            for (int var12 = par3 - 1; var12 <= par3 + 4; ++var12)
                            {
                                if (var10 != par2 || var12 != par3 || var11 != par4)
                                {
                                    int var13 = 100;

                                    if (var12 > par3 + 1)
                                    {
                                        var13 += (var12 - (par3 + 1)) * 100;
                                    }

                                    int var14 = this.getChanceOfNeighborsEncouragingFire(par1World, var10, var12, var11);

                                    if (var14 > 0)
                                    {
                                        int var15 = (var14 + 40 + par1World.difficultySetting * 7) / (var7 + 30);

                                        if (var8)
                                        {
                                            var15 /= 2;
                                        }

                                        if (var15 > 0 && par5Random.nextInt(var13) <= var15 && (!par1World.isRaining() || !par1World.canLightningStrikeAt(var10, var12, var11)) && !par1World.canLightningStrikeAt(var10 - 1, var12, par4) && !par1World.canLightningStrikeAt(var10 + 1, var12, var11) && !par1World.canLightningStrikeAt(var10, var12, var11 - 1) && !par1World.canLightningStrikeAt(var10, var12, var11 + 1))
                                        {
                                            int var16 = var7 + par5Random.nextInt(5) / 4;

                                            if (var16 > 15)
                                            {
                                                var16 = 15;
                                            }

                                            // CraftBukkit start - call to stop spread of fire
                                            org.bukkit.block.Block block = bworld.getBlockAt(var10, var12, var11);

                                            if (block.getTypeId() != Block.fire.blockID)
                                            {
                                                BlockIgniteEvent event = new BlockIgniteEvent(block, igniteCause, null);
                                                server.getPluginManager().callEvent(event);

                                                if (event.isCancelled())
                                                {
                                                    continue;
                                                }

                                                org.bukkit.block.BlockState blockState = bworld.getBlockAt(var10, var12, var11).getState();
                                                blockState.setTypeId(this.blockID);
                                                blockState.setData(new org.bukkit.material.MaterialData(this.blockID, (byte) var16));
                                                BlockSpreadEvent spreadEvent = new BlockSpreadEvent(blockState.getBlock(), fromBlock, blockState);
                                                server.getPluginManager().callEvent(spreadEvent);

                                                if (!spreadEvent.isCancelled())
                                                {
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

    public boolean func_82506_l()
    {
        return false;
    }

    private void tryToCatchBlockOnFire(World par1World, int par2, int par3, int par4, int par5, Random par6Random, int par7)
    {
        int var8 = this.abilityToCatchFire[par1World.getBlockId(par2, par3, par4)];

        if (par6Random.nextInt(par5) < var8)
        {
            boolean var9 = par1World.getBlockId(par2, par3, par4) == Block.tnt.blockID;
            // CraftBukkit start
            org.bukkit.block.Block theBlock = par1World.getWorld().getBlockAt(par2, par3, par4);
            BlockBurnEvent event = new BlockBurnEvent(theBlock);
            par1World.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled())
            {
                return;
            }

            // CraftBukkit end

            if (par6Random.nextInt(par7 + 10) < 5 && !par1World.canLightningStrikeAt(par2, par3, par4))
            {
                int var10 = par7 + par6Random.nextInt(5) / 4;

                if (var10 > 15)
                {
                    var10 = 15;
                }

                par1World.setBlockAndMetadataWithNotify(par2, par3, par4, this.blockID, var10);
            }
            else
            {
                par1World.setBlockWithNotify(par2, par3, par4, 0);
            }

            if (var9)
            {
                Block.tnt.onBlockDestroyedByPlayer(par1World, par2, par3, par4, 1);
            }
        }
    }

    /**
     * Returns true if at least one block next to this one can burn.
     */
    private boolean canNeighborBurn(World par1World, int par2, int par3, int par4)
    {
        return this.canBlockCatchFire((IBlockAccess) par1World, par2 + 1, par3, par4) ? true : (this.canBlockCatchFire((IBlockAccess) par1World, par2 - 1, par3, par4) ? true : (this.canBlockCatchFire((IBlockAccess) par1World, par2, par3 - 1, par4) ? true : (this.canBlockCatchFire((IBlockAccess) par1World, par2, par3 + 1, par4) ? true : (this.canBlockCatchFire((IBlockAccess) par1World, par2, par3, par4 - 1) ? true : this.canBlockCatchFire((IBlockAccess) par1World, par2, par3, par4 + 1)))));
    }

    /**
     * Gets the highest chance of a neighbor block encouraging this block to catch fire
     */
    private int getChanceOfNeighborsEncouragingFire(World par1World, int par2, int par3, int par4)
    {
        byte var5 = 0;

        if (!par1World.isAirBlock(par2, par3, par4))
        {
            return 0;
        }
        else
        {
            int var6 = this.getChanceToEncourageFire(par1World, par2 + 1, par3, par4, var5);
            var6 = this.getChanceToEncourageFire(par1World, par2 - 1, par3, par4, var6);
            var6 = this.getChanceToEncourageFire(par1World, par2, par3 - 1, par4, var6);
            var6 = this.getChanceToEncourageFire(par1World, par2, par3 + 1, par4, var6);
            var6 = this.getChanceToEncourageFire(par1World, par2, par3, par4 - 1, var6);
            var6 = this.getChanceToEncourageFire(par1World, par2, par3, par4 + 1, var6);
            return var6;
        }
    }

    /**
     * Returns if this block is collidable (only used by Fire). Args: x, y, z
     */
    public boolean isCollidable()
    {
        return false;
    }

    /**
     * Checks the specified block coordinate to see if it can catch fire.  Args: blockAccess, x, y, z
     */
    public boolean canBlockCatchFire(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return this.chanceToEncourageFire[par1IBlockAccess.getBlockId(par2, par3, par4)] > 0;
    }

    /**
     * Retrieves a specified block's chance to encourage their neighbors to burn and if the number is greater than the
     * current number passed in it will return its number instead of the passed in one.  Args: world, x, y, z,
     * curChanceToEncourageFire
     */
    public int getChanceToEncourageFire(World par1World, int par2, int par3, int par4, int par5)
    {
        int var6 = this.chanceToEncourageFire[par1World.getBlockId(par2, par3, par4)];
        return var6 > par5 ? var6 : par5;
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
    {
        return par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4) || this.canNeighborBurn(par1World, par2, par3, par4);
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        if (!par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4) && !this.canNeighborBurn(par1World, par2, par3, par4))
        {
            fireExtinguished(par1World, par2, par3, par4); // CraftBukkit - fuel block gone
        }
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        if (par1World.provider.dimensionId > 0 || par1World.getBlockId(par2, par3 - 1, par4) != Block.obsidian.blockID || !Block.portal.tryToCreatePortal(par1World, par2, par3, par4))
        {
            if (!par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4) && !this.canNeighborBurn(par1World, par2, par3, par4))
            {
                fireExtinguished(par1World, par2, par3, par4); // CraftBukkit - fuel block broke
            }
            else
            {
                par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate() + par1World.rand.nextInt(10));
            }
        }
    }

    // CraftBukkit start
    private void fireExtinguished(World world, int x, int y, int z)
    {
        if (org.bukkit.craftbukkit.event.CraftEventFactory.callBlockFadeEvent(world.getWorld().getBlockAt(x, y, z), 0).isCancelled() == false)
        {
            world.setBlockWithNotify(x, y, z, 0);
        }
    }
    // CraftBukkit end
}