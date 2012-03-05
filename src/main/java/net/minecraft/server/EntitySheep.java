package net.minecraft.server;

import java.util.Random;

// CraftBukkit start
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.Sheep;
// CraftBukkit end

import forge.IShearable;

public class EntitySheep extends EntityAnimal implements IShearable {

    public static final float[][] a = new float[][] { { 1.0F, 1.0F, 1.0F}, { 0.95F, 0.7F, 0.2F}, { 0.9F, 0.5F, 0.85F}, { 0.6F, 0.7F, 0.95F}, { 0.9F, 0.9F, 0.2F}, { 0.5F, 0.8F, 0.1F}, { 0.95F, 0.7F, 0.8F}, { 0.3F, 0.3F, 0.3F}, { 0.6F, 0.6F, 0.6F}, { 0.3F, 0.6F, 0.7F}, { 0.7F, 0.4F, 0.9F}, { 0.2F, 0.4F, 0.8F}, { 0.5F, 0.4F, 0.3F}, { 0.4F, 0.5F, 0.2F}, { 0.8F, 0.3F, 0.3F}, { 0.1F, 0.1F, 0.1F}};
    private int b;
    private PathfinderGoalEatTile c = new PathfinderGoalEatTile(this);

    public EntitySheep(World world) {
        super(world);
        this.texture = "/mob/sheep.png";
        this.b(0.9F, 1.3F);
        float f = 0.23F;

        this.ak().a(true);
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 0.38F));
        this.goalSelector.a(2, new PathfinderGoalBreed(this, f));
        this.goalSelector.a(3, new PathfinderGoalTempt(this, 0.25F, Item.WHEAT.id, false));
        this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 0.25F));
        this.goalSelector.a(5, this.c);
        this.goalSelector.a(6, new PathfinderGoalRandomStroll(this, f));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
    }

    protected boolean c_() {
        return true;
    }

    protected void z_() {
        this.b = this.c.f();
        super.z_();
    }

    public void e() {
        if (this.world.isStatic) {
            this.b = Math.max(0, this.b - 1);
        }

        super.e();
    }

    public int getMaxHealth() {
        return 8;
    }

    protected void b() {
        super.b();
        this.datawatcher.a(16, new Byte((byte) 0));
    }

    protected void dropDeathLoot(boolean flag, int i) {
        // CraftBukkit start - whole method
        java.util.List<org.bukkit.inventory.ItemStack> loot = new java.util.ArrayList<org.bukkit.inventory.ItemStack>();

        if (!this.isSheared()) {
            loot.add(new org.bukkit.inventory.ItemStack(org.bukkit.Material.WOOL, 1, (short) 0, (byte) this.getColor()));
        }

        CraftEventFactory.callEntityDeathEvent(this, loot);
        // CraftBukkit end
    }

    protected int getLootId() {
        return Block.WOOL.id;
    }

    public boolean b(EntityHuman entityhuman) {
        return super.b(entityhuman);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("Sheared", this.isSheared());
        nbttagcompound.setByte("Color", (byte) this.getColor());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setSheared(nbttagcompound.getBoolean("Sheared"));
        this.setColor(nbttagcompound.getByte("Color"));
    }

    protected String i() {
        return "mob.sheep";
    }

    protected String j() {
        return "mob.sheep";
    }

    protected String k() {
        return "mob.sheep";
    }

    public int getColor() {
        return this.datawatcher.getByte(16) & 15;
    }

    public void setColor(int i) {
        byte b0 = this.datawatcher.getByte(16);

        this.datawatcher.watch(16, Byte.valueOf((byte) (b0 & 240 | i & 15)));
    }

    public boolean isSheared() {
        return (this.datawatcher.getByte(16) & 16) != 0;
    }

    public void setSheared(boolean flag) {
        byte b0 = this.datawatcher.getByte(16);

        if (flag) {
            this.datawatcher.watch(16, Byte.valueOf((byte) (b0 | 16)));
        } else {
            this.datawatcher.watch(16, Byte.valueOf((byte) (b0 & -17)));
        }
    }

    public static int a(Random random) {
        int i = random.nextInt(100);

        return i < 5 ? 15 : (i < 10 ? 7 : (i < 15 ? 8 : (i < 18 ? 12 : (random.nextInt(500) == 0 ? 6 : 0))));
    }

    public EntityAnimal createChild(EntityAnimal entityanimal) {
        EntitySheep entitysheep = (EntitySheep) entityanimal;
        EntitySheep entitysheep1 = new EntitySheep(this.world);

        if (this.random.nextBoolean()) {
            entitysheep1.setColor(this.getColor());
        } else {
            entitysheep1.setColor(entitysheep.getColor());
        }

        return entitysheep1;
    }

    public void z() {
        // CraftBukkit start
        org.bukkit.event.entity.SheepRegrowWoolEvent event = new org.bukkit.event.entity.SheepRegrowWoolEvent((Sheep) this.getBukkitEntity());
        this.world.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            this.setSheared(false);
        }
        // CraftBukkit end

        if (this.isBaby()) {
            int i = this.getAge() + 1200;

            if (i > 0) {
                i = 0;
            }

            this.setAge(i);
        }
    }

	public boolean isShearable(ItemStack item, World world, int x, int y, int z) {
		return !isSheared() && !isBaby();
	}

	public ArrayList<ItemStack> onSheared(ItemStack item, World world, int x, int y, int z, int fortune) {
        // CraftBukkit start
        /*org.bukkit.event.player.PlayerShearEntityEvent event = new org.bukkit.event.player.PlayerShearEntityEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), this.getBukkitEntity());
        this.world.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }*/
        // CraftBukkit end
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		setSheared(true);
		int i = 1 + random.nextInt(3);
		for (int j = 0; j < i; j++)
		{
			ret.add(new ItemStack(Block.WOOL.id, 1, getColor()));
		}
		return ret;
    }
}
