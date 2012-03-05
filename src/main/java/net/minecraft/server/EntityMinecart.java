package net.minecraft.server;

// CraftBukkit start
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.util.Vector;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
// CraftBukkit end

import forge.ForgeHooks;
import forge.IMinecartCollisionHandler;
import forge.MinecraftForge;

public class EntityMinecart extends Entity implements IInventory {

    protected ItemStack[] items;
    protected int e;
    protected boolean f;
    public int type;
    public double b;
    public double c;
    protected static final int[][][] matrix = new int[][][] { { { 0, 0, -1}, { 0, 0, 1}}, { { -1, 0, 0}, { 1, 0, 0}}, { { -1, -1, 0}, { 1, 0, 0}}, { { -1, 0, 0}, { 1, -1, 0}}, { { 0, 0, -1}, { 0, -1, 1}}, { { 0, -1, -1}, { 0, 0, 1}}, { { 0, 0, 1}, { 1, 0, 0}}, { { 0, 0, 1}, { -1, 0, 0}}, { { 0, 0, -1}, { -1, 0, 0}}, { { 0, 0, -1}, { 1, 0, 0}}};
    protected int h;
    protected double i;
    protected double j;
    protected double k;
    protected double l;
    protected double m;
    protected double velocityX;
    protected double velocityY;
    protected double velocityZ;

	public static float defaultMaxSpeedRail = 0.4f;
	public static float defaultMaxSpeedGround = 0.4f;
	public static float defaultMaxSpeedAirLateral = 0.4f;
	public static float defaultMaxSpeedAirVertical = -1f;
	public static double defaultDragAir = 0.94999998807907104D;
	protected boolean canUseRail = true;
	protected boolean canBePushed = true;
	private static IMinecartCollisionHandler collisionHandler = null;

	/* Instance versions of the above physics properties */
	protected float maxSpeedRail;
	protected float maxSpeedGround;
	protected float maxSpeedAirLateral;
	protected float maxSpeedAirVertical;
	protected double dragAir;
    // CraftBukkit start
    public boolean slowWhenEmpty = true;
    private double derailedX = 0.5;
    private double derailedY = 0.5;
    private double derailedZ = 0.5;
    private double flyingX = 0.95;
    private double flyingY = 0.95;
    private double flyingZ = 0.95;
    public double maxSpeed = 0.4D;
    public List<HumanEntity> transaction = new ArrayList<HumanEntity>();
    private int maxStack = MAX_STACK;

    public ItemStack[] getContents() {
        return this.items;
    }

    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    public List<HumanEntity> getViewers() {
        return transaction;
    }

    public InventoryHolder getOwner() {
        org.bukkit.entity.Entity cart = getBukkitEntity();
        if(cart instanceof InventoryHolder) return (InventoryHolder) cart;
        return null;
    }

    public void setMaxStackSize(int size) {
        maxStack = size;
    }
    // CraftBukkit end

    public EntityMinecart(World world) {
        super(world);
        this.items = new ItemStack[27]; // CraftBukkit
        this.e = 0;
        this.f = false;
        this.bf = true;
        this.b(0.98F, 0.7F);
        this.height = this.length / 2.0F;
        maxSpeedRail = defaultMaxSpeedRail;
        maxSpeedGround = defaultMaxSpeedGround;
        maxSpeedAirLateral = defaultMaxSpeedAirLateral;
        maxSpeedAirVertical = defaultMaxSpeedAirVertical;
        dragAir = defaultDragAir;
    }
    public EntityMinecart(World world, int type)
    {
    	this(world);
    	this.type = type;
    }

    protected boolean g_() {
        return false;
    }

    protected void b() {
        this.datawatcher.a(16, new Byte((byte) 0));
        this.datawatcher.a(17, new Integer(0));
        this.datawatcher.a(18, new Integer(1));
        this.datawatcher.a(19, new Integer(0));
    }

    public AxisAlignedBB b_(Entity entity) {
    	if (getCollisionHandler() != null)
    	{
    		return getCollisionHandler().getCollisionBox(this, entity);
    	}
        return entity.boundingBox;
    }

    public AxisAlignedBB h() {
    	if (getCollisionHandler() != null)
    	{
    		return getCollisionHandler().getBoundingBox(this);
    	}
        return null;
    }

    public boolean e_() {
        return canBePushed;
    }

    public EntityMinecart(World world, double d0, double d1, double d2, int i) {
        this(world);
        this.setPosition(d0, d1 + (double) this.height, d2);
        this.motX = 0.0D;
        this.motY = 0.0D;
        this.motZ = 0.0D;
        this.lastX = d0;
        this.lastY = d1;
        this.lastZ = d2;
        this.type = i;

        this.world.getServer().getPluginManager().callEvent(new VehicleCreateEvent((Vehicle) this.getBukkitEntity())); // CraftBukkit
    }

    public double x_() {
        return (double) this.length * 0.0D - 0.30000001192092896D;
    }

    public boolean damageEntity(DamageSource damagesource, int i) {
        if (!this.world.isStatic && !this.dead) {
            // CraftBukkit start
            Vehicle vehicle = (Vehicle) this.getBukkitEntity();
            org.bukkit.entity.Entity passenger = (damagesource.getEntity() == null) ? null : damagesource.getEntity().getBukkitEntity();

            VehicleDamageEvent event = new VehicleDamageEvent(vehicle, passenger, i);
            this.world.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return true;
            }

            i = event.getDamage();
            // CraftBukkit end

            this.e(-this.n());
            this.d(10);
            this.aV();
            this.setDamage(this.getDamage() + i * 10);
            if (this.getDamage() > 40) {
                if (this.passenger != null) {
                    this.passenger.mount(this);
                }

                // CraftBukkit start
                VehicleDestroyEvent destroyEvent = new VehicleDestroyEvent(vehicle, passenger);
                this.world.getServer().getPluginManager().callEvent(destroyEvent);

                if (destroyEvent.isCancelled()) {
                    this.setDamage(40); // Maximize damage so this doesn't get triggered again right away
                    return true;
                }
                // CraftBukkit end

                this.die();
                dropCartAsItem();
                                }
            return true;
        } else {
            return true;
        }
    }

    public boolean o_() {
        return !this.dead;
    }

    public void die() {
        for (int i = 0; i < this.getSize(); ++i) {
            ItemStack itemstack = this.getItem(i);

            if (itemstack != null) {
                float f = this.random.nextFloat() * 0.8F + 0.1F;
                float f1 = this.random.nextFloat() * 0.8F + 0.1F;
                float f2 = this.random.nextFloat() * 0.8F + 0.1F;

                while (itemstack.count > 0) {
                    int j = this.random.nextInt(21) + 10;

                    if (j > itemstack.count) {
                        j = itemstack.count;
                    }

                    itemstack.count -= j;
                    // CraftBukkit - include enchantments in the new itemstack
                    EntityItem entityitem = new EntityItem(this.world, this.locX + (double) f, this.locY + (double) f1, this.locZ + (double) f2, new ItemStack(itemstack.id, j, itemstack.getData(), itemstack.getEnchantments()));
                    float f3 = 0.05F;

                    entityitem.motX = (double) ((float) this.random.nextGaussian() * f3);
                    entityitem.motY = (double) ((float) this.random.nextGaussian() * f3 + 0.2F);
                    entityitem.motZ = (double) ((float) this.random.nextGaussian() * f3);
                    this.world.addEntity(entityitem);
                }
            }
        }

        super.die();
    }

    public void G_() {
        // CraftBukkit start
        double prevX = this.locX;
        double prevY = this.locY;
        double prevZ = this.locZ;
        float prevYaw = this.yaw;
        float prevPitch = this.pitch;
        // CraftBukkit end

        if (this.m() > 0) {
            this.d(this.m() - 1);
        }

        if (this.getDamage() > 0) {
            this.setDamage(this.getDamage() - 1);
        }

        if (this.k() && this.random.nextInt(4) == 0 && type == 2 && getClass() == EntityMinecart.class) {
            this.world.a("largesmoke", this.locX, this.locY + 0.8D, this.locZ, 0.0D, 0.0D, 0.0D);
        }

        if (this.world.isStatic) {
            if (this.h > 0) {
                double d0 = this.locX + (this.i - this.locX) / (double) this.h;
                double d1 = this.locY + (this.j - this.locY) / (double) this.h;
                double d2 = this.locZ + (this.k - this.locZ) / (double) this.h;

                double d3;

                for (d3 = this.l - (double) this.yaw; d3 < -180.0D; d3 += 360.0D) {
                    ;
                }

                while (d3 >= 180.0D) {
                    d3 -= 360.0D;
                }

                this.yaw = (float) ((double) this.yaw + d3 / (double) this.h);
                this.pitch = (float) ((double) this.pitch + (this.m - (double) this.pitch) / (double) this.h);
                --this.h;
                this.setPosition(d0, d1, d2);
                this.c(this.yaw, this.pitch);
            } else {
                this.setPosition(this.locX, this.locY, this.locZ);
                this.c(this.yaw, this.pitch);
            }
        } else {
            this.lastX = this.locX;
            this.lastY = this.locY;
            this.lastZ = this.locZ;
            this.motY -= 0.03999999910593033D;
            int i = MathHelper.floor(this.locX);
            int j = MathHelper.floor(this.locY);
            int k = MathHelper.floor(this.locZ);

            if (BlockMinecartTrack.g(this.world, i, j - 1, k)) {
                --j;
            }

            // CraftBukkit
            double d4 = this.maxSpeed;
            double d5 = 0.0078125D;
            int l = this.world.getTypeId(i, j, k);

            if (canUseRail() && BlockMinecartTrack.d(l)) {
                Vec3D vec3d = this.h(this.locX, this.locY, this.locZ);
                int i1 = ((BlockMinecartTrack)Block.byId[l]).getBasicRailMetadata(world,this,i, j, k);

                this.locY = (double) j;
                boolean flag = false;
                boolean flag1 = false;

                if (l == Block.GOLDEN_RAIL.id) {
                    flag = (world.getData(i, j, k) & 8) != 0;
                    flag1 = !flag;
                }

                if (i1 >= 2 && i1 <= 5) {
                    this.locY = (double) (j + 1);
                }

                if (i1 == 2) {
                    this.motX -= d5;
                }

                if (i1 == 3) {
                    this.motX += d5;
                }

                if (i1 == 4) {
                    this.motZ += d5;
                }

                if (i1 == 5) {
                    this.motZ -= d5;
                }

                int[][] aint = matrix[i1];
                double d6 = (double) (aint[1][0] - aint[0][0]);
                double d7 = (double) (aint[1][2] - aint[0][2]);
                double d8 = Math.sqrt(d6 * d6 + d7 * d7);
                double d9 = this.motX * d6 + this.motZ * d7;

                if (d9 < 0.0D) {
                    d6 = -d6;
                    d7 = -d7;
                }

                double d10 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);

                this.motX = d10 * d6 / d8;
                this.motZ = d10 * d7 / d8;
                double d11;

                if (flag1 && shouldDoRailFunctions()) {
                    d11 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
                    if (d11 < 0.03D) {
                        this.motX *= 0.0D;
                        this.motY *= 0.0D;
                        this.motZ *= 0.0D;
                    } else {
                        this.motX *= 0.5D;
                        this.motY *= 0.0D;
                        this.motZ *= 0.5D;
                    }
                }

                d11 = 0.0D;
                double d12 = (double) i + 0.5D + (double) aint[0][0] * 0.5D;
                double d13 = (double) k + 0.5D + (double) aint[0][2] * 0.5D;
                double d14 = (double) i + 0.5D + (double) aint[1][0] * 0.5D;
                double d15 = (double) k + 0.5D + (double) aint[1][2] * 0.5D;

                d6 = d14 - d12;
                d7 = d15 - d13;
                double d16;
                double d17;
                double d18;

                if (d6 == 0.0D) {
                    this.locX = (double) i + 0.5D;
                    d11 = this.locZ - (double) k;
                } else if (d7 == 0.0D) {
                    this.locZ = (double) k + 0.5D;
                    d11 = this.locX - (double) i;
                } else {
                    d16 = this.locX - d12;
                    d18 = this.locZ - d13;
                    d17 = (d16 * d6 + d18 * d7) * 2.0D;
                    d11 = d17;
                }

                this.locX = d12 + d6 * d11;
                this.locZ = d13 + d7 * d11;
                this.setPosition(this.locX, this.locY + (double) this.height, this.locZ);
                moveMinecartOnRail(i, j, k);
                if (aint[0][1] != 0 && MathHelper.floor(this.locX) - i == aint[0][0] && MathHelper.floor(this.locZ) - k == aint[0][2]) {
                    this.setPosition(this.locX, this.locY + (double) aint[0][1], this.locZ);
                } else if (aint[1][1] != 0 && MathHelper.floor(this.locX) - i == aint[1][0] && MathHelper.floor(this.locZ) - k == aint[1][2]) {
                    this.setPosition(this.locX, this.locY + (double) aint[1][1], this.locZ);
                }
                applyDragAndPushForces();

                Vec3D vec3d1 = this.h(this.locX, this.locY, this.locZ);

                if (vec3d1 != null && vec3d != null) {
                    double d20 = (vec3d.b - vec3d1.b) * 0.05D;

                    d10 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
                    if (d10 > 0.0D) {
                        this.motX = this.motX / d10 * (d10 + d20);
                        this.motZ = this.motZ / d10 * (d10 + d20);
                    }

                    this.setPosition(this.locX, vec3d1.b, this.locZ);
                }

                int j1 = MathHelper.floor(this.locX);
                int k1 = MathHelper.floor(this.locZ);

                if (j1 != i || k1 != k) {
                    d10 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
                    this.motX = d10 * (double) (j1 - i);
                    this.motZ = d10 * (double) (k1 - k);
                }

                double d21;

                updatePushForces();
                if (shouldDoRailFunctions()) {
                	((BlockMinecartTrack)Block.byId[l]).onMinecartPass(world, this, i, j, k);
                }

                if (flag && shouldDoRailFunctions()) {
                    d21 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
                    if (d21 > 0.01D) {
                        double d22 = 0.06D;

                        this.motX += this.motX / d21 * d22;
                        this.motZ += this.motZ / d21 * d22;
                    } else if (i1 == 1) {
                        if (this.world.e(i - 1, j, k)) {
                            this.motX = 0.02D;
                        } else if (this.world.e(i + 1, j, k)) {
                            this.motX = -0.02D;
                        }
                    } else if (i1 == 0) {
                        if (this.world.e(i, j, k - 1)) {
                            this.motZ = 0.02D;
                        } else if (this.world.e(i, j, k + 1)) {
                            this.motZ = -0.02D;
                        }
                    }
                }
            } else {
            	moveMinecartOffRail(i, j, k);
            }

            this.pitch = 0.0F;
            double d23 = this.lastX - this.locX;
            double d24 = this.lastZ - this.locZ;

            if (d23 * d23 + d24 * d24 > 0.0010D) {
                this.yaw = (float) (Math.atan2(d24, d23) * 180.0D / 3.141592653589793D);
                if (this.f) {
                    this.yaw += 180.0F;
                }
            }

            double d25;

            for (d25 = (double) (this.yaw - this.lastYaw); d25 >= 180.0D; d25 -= 360.0D) {
                ;
            }

            while (d25 < -180.0D) {
                d25 += 360.0D;
            }

            if (d25 < -170.0D || d25 >= 170.0D) {
                this.yaw += 180.0F;
                this.f = !this.f;
            }

            this.c(this.yaw, this.pitch);

            // CraftBukkit start
            org.bukkit.World bworld = this.world.getWorld();
            Location from = new Location(bworld, prevX, prevY, prevZ, prevYaw, prevPitch);
            Location to = new Location(bworld, this.locX, this.locY, this.locZ, this.yaw, this.pitch);
            Vehicle vehicle = (Vehicle) this.getBukkitEntity();

            this.world.getServer().getPluginManager().callEvent(new VehicleUpdateEvent(vehicle));

            if (!from.equals(to)) {
                this.world.getServer().getPluginManager().callEvent(new VehicleMoveEvent(vehicle, from, to));
            }
            // CraftBukkit end

            AxisAlignedBB box = null;
            if (getCollisionHandler() != null)
            {
            	box = getCollisionHandler().getMinecartCollisionBox(this);
            }
            else
            {
            	box = boundingBox.grow(0.2D, 0.0D, 0.2D);
            }
            List list = this.world.getEntities(this, box);

            if (list != null && list.size() > 0) {
                for (int l1 = 0; l1 < list.size(); ++l1) {
                    Entity entity = (Entity) list.get(l1);

                    if (entity != this.passenger && entity.e_() && entity instanceof EntityMinecart) {
                        entity.collide(this);
                    }
                }
            }

            if (this.passenger != null && this.passenger.dead) {
                if (this.passenger.vehicle == this) {
                    this.passenger.vehicle = null;
                }

                this.passenger = null;
            }
            updateFuel();
			ForgeHooks.onMinecartUpdate(this, i, j, k);
        }
    }

    public Vec3D h(double d0, double d1, double d2) {
        int i = MathHelper.floor(d0);
        int j = MathHelper.floor(d1);
        int k = MathHelper.floor(d2);

        if (BlockMinecartTrack.g(this.world, i, j - 1, k)) {
            --j;
        }

        int l = this.world.getTypeId(i, j, k);

        if (BlockMinecartTrack.d(l)) {
            int i1 = ((BlockMinecartTrack)Block.byId[l]).getBasicRailMetadata(world, this, i, j, k);

            d1 = (double) j;

            if (i1 >= 2 && i1 <= 5) {
                d1 = (double) (j + 1);
            }

            int[][] aint = matrix[i1];
            double d3 = 0.0D;
            double d4 = (double) i + 0.5D + (double) aint[0][0] * 0.5D;
            double d5 = (double) j + 0.5D + (double) aint[0][1] * 0.5D;
            double d6 = (double) k + 0.5D + (double) aint[0][2] * 0.5D;
            double d7 = (double) i + 0.5D + (double) aint[1][0] * 0.5D;
            double d8 = (double) j + 0.5D + (double) aint[1][1] * 0.5D;
            double d9 = (double) k + 0.5D + (double) aint[1][2] * 0.5D;
            double d10 = d7 - d4;
            double d11 = (d8 - d5) * 2.0D;
            double d12 = d9 - d6;

            if (d10 == 0.0D) {
                d0 = (double) i + 0.5D;
                d3 = d2 - (double) k;
            } else if (d12 == 0.0D) {
                d2 = (double) k + 0.5D;
                d3 = d0 - (double) i;
            } else {
                double d13 = d0 - d4;
                double d14 = d2 - d6;
                double d15 = (d13 * d10 + d14 * d12) * 2.0D;

                d3 = d15;
            }

            d0 = d4 + d10 * d3;
            d1 = d5 + d11 * d3;
            d2 = d6 + d12 * d3;
            if (d11 < 0.0D) {
                ++d1;
            }

            if (d11 > 0.0D) {
                d1 += 0.5D;
            }

            return Vec3D.create(d0, d1, d2);
        } else {
            return null;
        }
    }

    protected void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("Type", this.type);
        if (this.isPoweredCart()) {
            nbttagcompound.setDouble("PushX", this.b);
            nbttagcompound.setDouble("PushZ", this.c);
            nbttagcompound.setInt("Fuel", this.e);
        } else if (getSize()>0) {
            NBTTagList nbttaglist = new NBTTagList();

            for (int i = 0; i < this.items.length; ++i) {
                if (this.items[i] != null) {
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                    nbttagcompound1.setByte("Slot", (byte) i);
                    this.items[i].save(nbttagcompound1);
                    nbttaglist.add(nbttagcompound1);
                }
            }

            nbttagcompound.set("Items", nbttaglist);
        }
    }

    protected void a(NBTTagCompound nbttagcompound) {
        this.type = nbttagcompound.getInt("Type");
        if (isPoweredCart()) {
            this.b = nbttagcompound.getDouble("PushX");
            this.c = nbttagcompound.getDouble("PushZ");
            this.e = nbttagcompound.getInt("Fuel");
        } else if (getSize()>0) {
            NBTTagList nbttaglist = nbttagcompound.getList("Items");

            this.items = new ItemStack[this.getSize()];

            for (int i = 0; i < nbttaglist.size(); ++i) {
                NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.get(i);
                int j = nbttagcompound1.getByte("Slot") & 255;

                if (j >= 0 && j < this.items.length) {
                    this.items[j] = ItemStack.a(nbttagcompound1);
                }
            }
        }
    }

    public void collide(Entity entity) {
    	ForgeHooks.onMinecartEntityCollision(this, entity);
    	if (getCollisionHandler() != null)
    	{
    		getCollisionHandler().onEntityCollision(this, entity);
    		return;
    	}
        if (!this.world.isStatic) {
            if (entity != this.passenger) {
                // CraftBukkit start
                Vehicle vehicle = (Vehicle) this.getBukkitEntity();
                org.bukkit.entity.Entity hitEntity = (entity == null) ? null : entity.getBukkitEntity();

                VehicleEntityCollisionEvent collisionEvent = new VehicleEntityCollisionEvent(vehicle, hitEntity);
                this.world.getServer().getPluginManager().callEvent(collisionEvent);

                if (collisionEvent.isCancelled()) {
                    return;
                }
                // CraftBukkit end

                if (entity instanceof EntityLiving && !(entity instanceof EntityHuman) && !(entity instanceof EntityIronGolem) && canBeRidden() && this.motX * this.motX + this.motZ * this.motZ > 0.01D && this.passenger == null && entity.vehicle == null) {
                    entity.mount(this);
                }

                double d0 = entity.locX - this.locX;
                double d1 = entity.locZ - this.locZ;
                double d2 = d0 * d0 + d1 * d1;

                // CraftBukkit - Collision
                if (d2 >= 9.999999747378752E-5D && !collisionEvent.isCollisionCancelled()) {
                    d2 = (double) MathHelper.sqrt(d2);
                    d0 /= d2;
                    d1 /= d2;
                    double d3 = 1.0D / d2;

                    if (d3 > 1.0D) {
                        d3 = 1.0D;
                    }

                    d0 *= d3;
                    d1 *= d3;
                    d0 *= 0.10000000149011612D;
                    d1 *= 0.10000000149011612D;
                    d0 *= (double) (1.0F - this.bR);
                    d1 *= (double) (1.0F - this.bR);
                    d0 *= 0.5D;
                    d1 *= 0.5D;
                    if (entity instanceof EntityMinecart) {
                        double d4 = entity.locX - this.locX;
                        double d5 = entity.locZ - this.locZ;
                        Vec3D vec3d = Vec3D.create(d4, 0.0D, d5).b();
                        Vec3D vec3d1 = Vec3D.create((double) MathHelper.cos(this.yaw * 3.1415927F / 180.0F), 0.0D, (double) MathHelper.sin(this.yaw * 3.1415927F / 180.0F)).b();
                        double d6 = Math.abs(vec3d.a(vec3d1));

                        if (d6 < 0.800000011920929D) {
                            return;
                        }

                        double d7 = entity.motX + this.motX;
                        double d8 = entity.motZ + this.motZ;

                        if (((EntityMinecart) entity).isPoweredCart() && !isPoweredCart()) {
                            this.motX *= 0.20000000298023224D;
                            this.motZ *= 0.20000000298023224D;
                            this.b_(entity.motX - d0, 0.0D, entity.motZ - d1);
                            entity.motX *= 0.949999988079071D;
                            entity.motZ *= 0.949999988079071D;
                        } else if (!((EntityMinecart) entity).isPoweredCart() && isPoweredCart()) {
                            entity.motX *= 0.20000000298023224D;
                            entity.motZ *= 0.20000000298023224D;
                            entity.b_(this.motX + d0, 0.0D, this.motZ + d1);
                            this.motX *= 0.949999988079071D;
                            this.motZ *= 0.949999988079071D;
                        } else {
                            d7 /= 2.0D;
                            d8 /= 2.0D;
                            this.motX *= 0.20000000298023224D;
                            this.motZ *= 0.20000000298023224D;
                            this.b_(d7 - d0, 0.0D, d8 - d1);
                            entity.motX *= 0.20000000298023224D;
                            entity.motZ *= 0.20000000298023224D;
                            entity.b_(d7 + d0, 0.0D, d8 + d1);
                        }
                    } else {
                        this.b_(-d0, 0.0D, -d1);
                        entity.b_(d0 / 4.0D, 0.0D, d1 / 4.0D);
                    }
                }
            }
        }
    }

    public int getSize() {
        return type==1 && getClass() == EntityMinecart.class ? 27 : 0;
    }

    public ItemStack getItem(int i) {
        return this.items[i];
    }

    public ItemStack splitStack(int i, int j) {
        if (this.items[i] != null) {
            ItemStack itemstack;

            if (this.items[i].count <= j) {
                itemstack = this.items[i];
                this.items[i] = null;
                return itemstack;
            } else {
                itemstack = this.items[i].a(j);
                if (this.items[i].count == 0) {
                    this.items[i] = null;
                }

                return itemstack;
            }
        } else {
            return null;
        }
    }

    public ItemStack splitWithoutUpdate(int i) {
        if (this.items[i] != null) {
            ItemStack itemstack = this.items[i];

            this.items[i] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    public void setItem(int i, ItemStack itemstack) {
        this.items[i] = itemstack;
        if (itemstack != null && itemstack.count > this.getMaxStackSize()) {
            itemstack.count = this.getMaxStackSize();
        }
    }

    public String getName() {
        return "container.minecart";
    }

    public int getMaxStackSize() {
        return maxStack; // CraftBukkit
    }

    public void update() {}

    public boolean b(EntityHuman entityhuman) {
    	if (!ForgeHooks.onMinecartInteract(this, entityhuman))
    	{
    		return true;
    	}
    	if (canBeRidden()) {
            if (this.passenger != null && this.passenger instanceof EntityHuman && this.passenger != entityhuman) {
                return true;
            }

            if (!this.world.isStatic) {
                entityhuman.mount(this);
            }
        } else if (getSize()>0) {
            if (!this.world.isStatic) {
                entityhuman.openContainer(this);
            }
        } else if (this.type == 2 && getClass() ==  EntityMinecart.class) {
            ItemStack itemstack = entityhuman.inventory.getItemInHand();

            if (itemstack != null && itemstack.id == Item.COAL.id) {
                if (--itemstack.count == 0) {
                    entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, (ItemStack) null);
                }

                this.e += 3600;
            }

            this.b = this.locX - entityhuman.locX;
            this.c = this.locZ - entityhuman.locZ;
        }

        return true;
    }

    public boolean a(EntityHuman entityhuman) {
        return this.dead ? false : entityhuman.j(this) <= 64.0D;
    }

    public boolean k() {
        return (this.datawatcher.getByte(16) & 1) != 0;
    }

    protected void a(boolean flag) {
        if (flag) {
            this.datawatcher.watch(16, Byte.valueOf((byte) (this.datawatcher.getByte(16) | 1)));
        } else {
            this.datawatcher.watch(16, Byte.valueOf((byte) (this.datawatcher.getByte(16) & -2)));
        }
    }

    public void f() {}

    public void g() {}

    public void setDamage(int i) {
        this.datawatcher.watch(19, Integer.valueOf(i));
    }

    public int getDamage() {
        return this.datawatcher.getInt(19);
    }

    public void d(int i) {
        this.datawatcher.watch(17, Integer.valueOf(i));
    }

    public int m() {
        return this.datawatcher.getInt(17);
    }

    public void e(int i) {
        this.datawatcher.watch(18, Integer.valueOf(i));
    }

    public int n() {
        return this.datawatcher.getInt(18);
    }

    // CraftBukkit start - methods for getting and setting flying and derailed velocity modifiers
    public Vector getFlyingVelocityMod() {
        return new Vector(flyingX, flyingY, flyingZ);
    }

    public void setFlyingVelocityMod(Vector flying) {
        flyingX = flying.getX();
        flyingY = flying.getY();
        flyingZ = flying.getZ();
    }

    public Vector getDerailedVelocityMod() {
        return new Vector(derailedX, derailedY, derailedZ);
    }

    public void setDerailedVelocityMod(Vector derailed) {
        derailedX = derailed.getX();
        derailedY = derailed.getY();
        derailedZ = derailed.getZ();
    }
    // CraftBukkit end
    
    /**
     * Drops the cart as a item. The exact item dropped is defined by getItemDropped().
     */
    public void dropCartAsItem()
    {
        for(ItemStack item : getItemsDropped())
        {
            a(item, 0);
        }
    }
    
    /**
     * Override this to define which items your cart drops when broken.
     * This does not include items contained in the inventory,
     * that is handled elsewhere.
     * @return A list of items dropped.
     */
    public List<ItemStack> getItemsDropped()
    {
        List<ItemStack> items = new ArrayList<ItemStack>();
        items.add(new ItemStack(Item.MINECART));
        
        switch(type)
        {
            case 1:
                items.add(new ItemStack(Block.CHEST));
                break;
            case 2:
                items.add(new ItemStack(Block.FURNACE));
                break;
        }
        return items;
    }
    
    /**
     * This function returns an ItemStack that represents this cart.
     * This should be an ItemStack that can be used by the player to place the cart.
     * This is the item that was registered with the cart via the registerMinecart function,
     * but is not necessary the item the cart drops when destroyed.
     * @return An ItemStack that can be used to place the cart.
     */
    public ItemStack getCartItem()
    {
        return MinecraftForge.getItemForCart(this);
    }
    
    /**
     * Returns true if this cart is self propelled.
     * @return True if powered.
     */
    public boolean isPoweredCart()
    {
        return type == 2 && getClass() == EntityMinecart.class;
    }

    /** 
     * Returns true if this cart is a storage cart
     * Some carts may have inventories but not be storage carts
     * and some carts without inventories may be storage carts.
     * @return True if this cart should be classified as a storage cart.
     */
    public boolean isStorageCart()
    {
        return type == 1 && getClass() == EntityMinecart.class;
    }

    /**
     * Returns true if this cart can be ridden by an Entity.
     * @return True if this cart can be ridden.
     */
    public boolean canBeRidden()
    {
        if(type == 0 && getClass() == EntityMinecart.class)
        {
            return true;
        }
        return false;
    }
    
    /** 
     * Returns true if this cart can currently use rails.
     * This function is mainly used to gracefully detach a minecart from a rail.
     * @return True if the minecart can use rails.
     */
    public boolean canUseRail()
    {
        return canUseRail;
    }

    /**
     * Set whether the minecart can use rails.
     * This function is mainly used to gracefully detach a minecart from a rail.
     * @param use Whether the minecart can currently use rails.
     */
    public void setCanUseRail(boolean use)
    {
        canUseRail = use;
    }    
        
    /** 
     * Return false if this cart should not call IRail.onMinecartPass() and should ignore Powered Rails.
     * @return True if this cart should call IRail.onMinecartPass().
     */
    public boolean shouldDoRailFunctions()
    {
        return true;
    }
    
    /**
     * Simply returns the minecartType variable.
     * @return minecartType
     */
    public int getMinecartType()
    {
        return type;
    }
    
    /**
     * Gets the current global Minecart Collision handler if none
     * is registered, returns null
     * @return The collision handler or null
     */
    public static IMinecartCollisionHandler getCollisionHandler()
    {
        return collisionHandler;
    }
    
    /**
     * Sets the global Minecart Collision handler, overwrites any
     * that is currently set.
     * @param handler The new handler
     */
    public static void setCollisionHandler(IMinecartCollisionHandler handler)
    {
        collisionHandler = handler;
    }
    
    /**
     * Carts should return their drag factor here
     * @return The drag rate.
     */
    protected double getDrag()
    {
        return passenger != null ? 1D : 0.96D;
    }   
    
    /**
     * Moved to allow overrides.
     * This code applies drag and updates push forces.
     */
    protected void applyDragAndPushForces()
    {
        if(isPoweredCart())
        {
            double d27 = MathHelper.sqrt(b * b + c * c);
            if(d27 > 0.01D)
            {
                b /= d27;
                c /= d27;
                double d29 = 0.04;
                motX *= 0.8D;
                motY *= 0.0D;
                motZ *= 0.8D;
                motX += b * d29;
                motZ += c * d29;
            } else
            {
                motX *= 0.9D;
                motY *= 0.0D;
                motZ *= 0.9D;
            }
        }
        motX *= getDrag();
        motY *= 0.0D;
        motZ *= getDrag();
/*        // CraftBukkit
        if (this.passenger != null || !this.slowWhenEmpty) {
            this.motX *= 0.996999979019165D;
            this.motY *= 0.0D;
            this.motZ *= 0.996999979019165D;
        } else {
            if (this.type == 2) {
                d17 = (double) MathHelper.sqrt(this.b * this.b + this.c * this.c);
                if (d17 > 0.01D) {
                    this.b /= d17;
                    this.c /= d17;
                    double d19 = 0.04D;

                    this.motX *= 0.800000011920929D;
                    this.motY *= 0.0D;
                    this.motZ *= 0.800000011920929D;
                    this.motX += this.b * d19;
                    this.motZ += this.c * d19;
                } else {
                    this.motX *= 0.8999999761581421D;
                    this.motY *= 0.0D;
                    this.motZ *= 0.8999999761581421D;
                }
            }

            this.motX *= 0.9599999785423279D;
            this.motY *= 0.0D;
            this.motZ *= 0.9599999785423279D;
        }*/
    }
    
    /**
     * Moved to allow overrides.
     * This code updates push forces.
     */
    protected void updatePushForces()
    {
        if(isPoweredCart())
        {
            double push = MathHelper.sqrt(b * b + c * c);
            if(push > 0.01D && motX * motX + motZ * motZ > 0.001D)
            {
                b /= push;
                c /= push;
                if(b * b + c * c < 0.0D)
                {
                    b = 0.0D;
                    c = 0.0D;
                } else
                {
                    b = motX;
                    c = motZ;
                }
            }
        }
    }
    
    /**
     * Moved to allow overrides.
     * This code handles minecart movement and speed capping when on a rail.
     */
    protected void moveMinecartOnRail(int i, int j, int k)
    {
        int id = world.getTypeId(i, j, k);
        if (!BlockMinecartTrack.d(id))
        {
            return;
        }
        float railMaxSpeed = ((BlockMinecartTrack)Block.byId[id]).getRailMaxSpeed(world, this, i, j, k);
        
        double maxSpeed = Math.min(railMaxSpeed, getMaxSpeedRail());
        double mX = motX;
        double mZ = motZ;
        if(passenger != null)
        {
            mX *= 0.75D;
            mZ *= 0.75D;
        }
        if(mX < -maxSpeed) mX = -maxSpeed;
        if(mX >  maxSpeed) mX =  maxSpeed;
        if(mZ < -maxSpeed) mZ = -maxSpeed;
        if(mZ >  maxSpeed) mZ =  maxSpeed;
        move(mX, 0.0D, mZ);
    }    
    
    /**
     * Moved to allow overrides.
     * This code handles minecart movement and speed capping when not on a rail.
     */
    protected void moveMinecartOffRail(int i, int j, int k)
    {
        double d2 = getMaxSpeedGround();
        if(!onGround)
        {
            d2 = getMaxSpeedAirLateral();
        }
        if(motX < -d2) motX = -d2;
        if(motX >  d2) motX =  d2;
        if(motZ < -d2) motZ = -d2;
        if(motZ >  d2) motZ =  d2;
        double moveY = motY;
        if(getMaxSpeedAirVertical() > 0 && motY > getMaxSpeedAirVertical())
        {
            moveY = getMaxSpeedAirVertical();
            if(Math.abs(motX) < 0.3f && Math.abs(motZ) < 0.3f)
            {
                moveY = 0.15f;
                motY = moveY;
            }
        }
        if(onGround)
        {
            motX *= 0.5D;
            motY *= 0.5D;
            motZ *= 0.5D;
        }
        move(motX, moveY, motZ);
        if(!onGround)
        {
            motX *= getDragAir();
            motY *= getDragAir();
            motZ *= getDragAir();
        }
        /*
         if (this.motX < -d4) {
                    this.motX = -d4;
                }

                if (this.motX > d4) {
                    this.motX = d4;
                }

                if (this.motZ < -d4) {
                    this.motZ = -d4;
                }

                if (this.motZ > d4) {
                    this.motZ = d4;
                }

                if (this.onGround) {
                    // CraftBukkit start
                    this.motX *= this.derailedX;
                    this.motY *= this.derailedY;
                    this.motZ *= this.derailedZ;
                    // CraftBukkit end
                }

                this.move(this.motX, this.motY, this.motZ);
                if (!this.onGround) {
                    // CraftBukkit start
                    this.motX *= this.flyingX;
                    this.motY *= this.flyingY;
                    this.motZ *= this.flyingZ;
                    // CraftBukkit end
                }

         */
    }
    
    /**
     * Moved to allow overrides.
     * This code applies fuel consumption.
     */   
    protected void updateFuel()
    {
        if (e > 0) e--;
        if (e <= 0) b = c = 0.0D;
        a(e > 0);
    }
    
    /**
     * Getters/setters for physics variables
     */
    
    /**
     * Returns the carts max speed.
     * Carts going faster than 1.1 cause issues with chunk loading.
     * Carts cant traverse slopes or corners at greater than 0.5 - 0.6.
     * This value is compared with the rails max speed to determine
     * the carts current max speed. A normal rails max speed is 0.4.
     * @return Carts max speed.
     */
    public float getMaxSpeedRail()
    {
        return maxSpeedRail;
    }

    public void setMaxSpeedRail(float value)
    {
        maxSpeedRail = value;
    }
    
    public float getMaxSpeedGround()
    {
        return maxSpeedGround;
    }
    
    public void setMaxSpeedGround(float value)
    {
        maxSpeedGround = value;
    }
    
    public float getMaxSpeedAirLateral()
    {
        return maxSpeedAirLateral;
    }
    
    public void setMaxSpeedAirLateral(float value)
    {
        maxSpeedAirLateral = value;
    }
    
    public float getMaxSpeedAirVertical()
    {
        return maxSpeedAirVertical;
    }
    
    public void setMaxSpeedAirVertical(float value)
    {
        maxSpeedAirVertical = value;
    }
    
    public double getDragAir()
    {
        return dragAir;
    }
    
    public void setDragAir(double value)
    {
        dragAir = value;
    }
}
