package net.minecraft.item.crafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class CraftingManager
{
    /** The static instance of this class */
    private static final CraftingManager instance = new CraftingManager();
    // CraftBukkit start
    public List recipes = new ArrayList(); // private -> public
    public IRecipe lastRecipe;
    public org.bukkit.inventory.InventoryView lastCraftView;
    // CraftBukkit end

    /**
     * Returns the static instance of this class
     */
    public static final CraftingManager getInstance()
    {
        return instance;
    }

    // CraftBukkit - private -> public
    public CraftingManager()
    {
        (new RecipesTools()).addRecipes(this);
        (new RecipesWeapons()).addRecipes(this);
        (new RecipesIngots()).addRecipes(this);
        (new RecipesFood()).addRecipes(this);
        (new RecipesCrafting()).addRecipes(this);
        (new RecipesArmor()).addRecipes(this);
        (new RecipesDyes()).addRecipes(this);
        this.recipes.add(new RecipesArmorDyes());
        this.recipes.add(new RecipesMapCloning());
        this.recipes.add(new RecipesMapExtending());
        this.recipes.add(new RecipeFireworks());
        this.func_92051_a(new ItemStack(Item.paper, 3), new Object[] { "###", Character.valueOf('#'), Item.reed});
        this.addShapelessRecipe(new ItemStack(Item.book, 1), new Object[] {Item.paper, Item.paper, Item.paper, Item.leather});
        this.addShapelessRecipe(new ItemStack(Item.writableBook, 1), new Object[] {Item.book, new ItemStack(Item.dyePowder, 1, 0), Item.feather});
        this.func_92051_a(new ItemStack(Block.fence, 2), new Object[] { "###", "###", Character.valueOf('#'), Item.stick});
        this.func_92051_a(new ItemStack(Block.cobblestoneWall, 6, 0), new Object[] { "###", "###", Character.valueOf('#'), Block.cobblestone});
        this.func_92051_a(new ItemStack(Block.cobblestoneWall, 6, 1), new Object[] { "###", "###", Character.valueOf('#'), Block.cobblestoneMossy});
        this.func_92051_a(new ItemStack(Block.netherFence, 6), new Object[] { "###", "###", Character.valueOf('#'), Block.netherBrick});
        this.func_92051_a(new ItemStack(Block.fenceGate, 1), new Object[] { "#W#", "#W#", Character.valueOf('#'), Item.stick, Character.valueOf('W'), Block.planks});
        this.func_92051_a(new ItemStack(Block.jukebox, 1), new Object[] { "###", "#X#", "###", Character.valueOf('#'), Block.planks, Character.valueOf('X'), Item.diamond});
        this.func_92051_a(new ItemStack(Block.music, 1), new Object[] { "###", "#X#", "###", Character.valueOf('#'), Block.planks, Character.valueOf('X'), Item.redstone});
        this.func_92051_a(new ItemStack(Block.bookShelf, 1), new Object[] { "###", "XXX", "###", Character.valueOf('#'), Block.planks, Character.valueOf('X'), Item.book});
        this.func_92051_a(new ItemStack(Block.blockSnow, 1), new Object[] { "##", "##", Character.valueOf('#'), Item.snowball});
        this.func_92051_a(new ItemStack(Block.blockClay, 1), new Object[] { "##", "##", Character.valueOf('#'), Item.clay});
        this.func_92051_a(new ItemStack(Block.brick, 1), new Object[] { "##", "##", Character.valueOf('#'), Item.brick});
        this.func_92051_a(new ItemStack(Block.glowStone, 1), new Object[] { "##", "##", Character.valueOf('#'), Item.lightStoneDust});
        this.func_92051_a(new ItemStack(Block.cloth, 1), new Object[] { "##", "##", Character.valueOf('#'), Item.silk});
        this.func_92051_a(new ItemStack(Block.tnt, 1), new Object[] { "X#X", "#X#", "X#X", Character.valueOf('X'), Item.gunpowder, Character.valueOf('#'), Block.sand});
        this.func_92051_a(new ItemStack(Block.stoneSingleSlab, 6, 3), new Object[] { "###", Character.valueOf('#'), Block.cobblestone});
        this.func_92051_a(new ItemStack(Block.stoneSingleSlab, 6, 0), new Object[] { "###", Character.valueOf('#'), Block.stone});
        this.func_92051_a(new ItemStack(Block.stoneSingleSlab, 6, 1), new Object[] { "###", Character.valueOf('#'), Block.sandStone});
        this.func_92051_a(new ItemStack(Block.stoneSingleSlab, 6, 4), new Object[] { "###", Character.valueOf('#'), Block.brick});
        this.func_92051_a(new ItemStack(Block.stoneSingleSlab, 6, 5), new Object[] { "###", Character.valueOf('#'), Block.stoneBrick});
        this.func_92051_a(new ItemStack(Block.stoneSingleSlab, 6, 6), new Object[] { "###", Character.valueOf('#'), Block.netherBrick});
        this.func_92051_a(new ItemStack(Block.woodSingleSlab, 6, 0), new Object[] { "###", Character.valueOf('#'), new ItemStack(Block.planks, 1, 0)});
        this.func_92051_a(new ItemStack(Block.woodSingleSlab, 6, 2), new Object[] { "###", Character.valueOf('#'), new ItemStack(Block.planks, 1, 2)});
        this.func_92051_a(new ItemStack(Block.woodSingleSlab, 6, 1), new Object[] { "###", Character.valueOf('#'), new ItemStack(Block.planks, 1, 1)});
        this.func_92051_a(new ItemStack(Block.woodSingleSlab, 6, 3), new Object[] { "###", Character.valueOf('#'), new ItemStack(Block.planks, 1, 3)});
        this.func_92051_a(new ItemStack(Block.ladder, 3), new Object[] { "# #", "###", "# #", Character.valueOf('#'), Item.stick});
        this.func_92051_a(new ItemStack(Item.doorWood, 1), new Object[] { "##", "##", "##", Character.valueOf('#'), Block.planks});
        this.func_92051_a(new ItemStack(Block.trapdoor, 2), new Object[] { "###", "###", Character.valueOf('#'), Block.planks});
        this.func_92051_a(new ItemStack(Item.doorSteel, 1), new Object[] { "##", "##", "##", Character.valueOf('#'), Item.ingotIron});
        this.func_92051_a(new ItemStack(Item.sign, 3), new Object[] { "###", "###", " X ", Character.valueOf('#'), Block.planks, Character.valueOf('X'), Item.stick});
        this.func_92051_a(new ItemStack(Item.cake, 1), new Object[] { "AAA", "BEB", "CCC", Character.valueOf('A'), Item.bucketMilk, Character.valueOf('B'), Item.sugar, Character.valueOf('C'), Item.wheat, Character.valueOf('E'), Item.egg});
        this.func_92051_a(new ItemStack(Item.sugar, 1), new Object[] { "#", Character.valueOf('#'), Item.reed});
        this.func_92051_a(new ItemStack(Block.planks, 4, 0), new Object[] { "#", Character.valueOf('#'), new ItemStack(Block.wood, 1, 0)});
        this.func_92051_a(new ItemStack(Block.planks, 4, 1), new Object[] { "#", Character.valueOf('#'), new ItemStack(Block.wood, 1, 1)});
        this.func_92051_a(new ItemStack(Block.planks, 4, 2), new Object[] { "#", Character.valueOf('#'), new ItemStack(Block.wood, 1, 2)});
        this.func_92051_a(new ItemStack(Block.planks, 4, 3), new Object[] { "#", Character.valueOf('#'), new ItemStack(Block.wood, 1, 3)});
        this.func_92051_a(new ItemStack(Item.stick, 4), new Object[] { "#", "#", Character.valueOf('#'), Block.planks});
        this.func_92051_a(new ItemStack(Block.torchWood, 4), new Object[] { "X", "#", Character.valueOf('X'), Item.coal, Character.valueOf('#'), Item.stick});
        this.func_92051_a(new ItemStack(Block.torchWood, 4), new Object[] { "X", "#", Character.valueOf('X'), new ItemStack(Item.coal, 1, 1), Character.valueOf('#'), Item.stick});
        this.func_92051_a(new ItemStack(Item.bowlEmpty, 4), new Object[] { "# #", " # ", Character.valueOf('#'), Block.planks});
        this.func_92051_a(new ItemStack(Item.glassBottle, 3), new Object[] { "# #", " # ", Character.valueOf('#'), Block.glass});
        this.func_92051_a(new ItemStack(Block.rail, 16), new Object[] { "X X", "X#X", "X X", Character.valueOf('X'), Item.ingotIron, Character.valueOf('#'), Item.stick});
        this.func_92051_a(new ItemStack(Block.railPowered, 6), new Object[] { "X X", "X#X", "XRX", Character.valueOf('X'), Item.ingotGold, Character.valueOf('R'), Item.redstone, Character.valueOf('#'), Item.stick});
        this.func_92051_a(new ItemStack(Block.railDetector, 6), new Object[] { "X X", "X#X", "XRX", Character.valueOf('X'), Item.ingotIron, Character.valueOf('R'), Item.redstone, Character.valueOf('#'), Block.pressurePlateStone});
        this.func_92051_a(new ItemStack(Item.minecartEmpty, 1), new Object[] { "# #", "###", Character.valueOf('#'), Item.ingotIron});
        this.func_92051_a(new ItemStack(Item.cauldron, 1), new Object[] { "# #", "# #", "###", Character.valueOf('#'), Item.ingotIron});
        this.func_92051_a(new ItemStack(Item.brewingStand, 1), new Object[] { " B ", "###", Character.valueOf('#'), Block.cobblestone, Character.valueOf('B'), Item.blazeRod});
        this.func_92051_a(new ItemStack(Block.pumpkinLantern, 1), new Object[] { "A", "B", Character.valueOf('A'), Block.pumpkin, Character.valueOf('B'), Block.torchWood});
        this.func_92051_a(new ItemStack(Item.minecartCrate, 1), new Object[] { "A", "B", Character.valueOf('A'), Block.chest, Character.valueOf('B'), Item.minecartEmpty});
        this.func_92051_a(new ItemStack(Item.minecartPowered, 1), new Object[] { "A", "B", Character.valueOf('A'), Block.stoneOvenIdle, Character.valueOf('B'), Item.minecartEmpty});
        this.func_92051_a(new ItemStack(Item.boat, 1), new Object[] { "# #", "###", Character.valueOf('#'), Block.planks});
        this.func_92051_a(new ItemStack(Item.bucketEmpty, 1), new Object[] { "# #", " # ", Character.valueOf('#'), Item.ingotIron});
        this.func_92051_a(new ItemStack(Item.flowerPot, 1), new Object[] { "# #", " # ", Character.valueOf('#'), Item.brick});
        this.func_92051_a(new ItemStack(Item.flintAndSteel, 1), new Object[] { "A ", " B", Character.valueOf('A'), Item.ingotIron, Character.valueOf('B'), Item.flint});
        this.func_92051_a(new ItemStack(Item.bread, 1), new Object[] { "###", Character.valueOf('#'), Item.wheat});
        this.func_92051_a(new ItemStack(Block.stairCompactPlanks, 4), new Object[] { "#  ", "## ", "###", Character.valueOf('#'), new ItemStack(Block.planks, 1, 0)});
        this.func_92051_a(new ItemStack(Block.stairsWoodBirch, 4), new Object[] { "#  ", "## ", "###", Character.valueOf('#'), new ItemStack(Block.planks, 1, 2)});
        this.func_92051_a(new ItemStack(Block.stairsWoodSpruce, 4), new Object[] { "#  ", "## ", "###", Character.valueOf('#'), new ItemStack(Block.planks, 1, 1)});
        this.func_92051_a(new ItemStack(Block.stairsWoodJungle, 4), new Object[] { "#  ", "## ", "###", Character.valueOf('#'), new ItemStack(Block.planks, 1, 3)});
        this.func_92051_a(new ItemStack(Item.fishingRod, 1), new Object[] { "  #", " #X", "# X", Character.valueOf('#'), Item.stick, Character.valueOf('X'), Item.silk});
        this.func_92051_a(new ItemStack(Item.carrotOnAStick, 1), new Object[] { "# ", " X", Character.valueOf('#'), Item.fishingRod, Character.valueOf('X'), Item.carrot}).func_92048_c();
        this.func_92051_a(new ItemStack(Block.stairCompactCobblestone, 4), new Object[] { "#  ", "## ", "###", Character.valueOf('#'), Block.cobblestone});
        this.func_92051_a(new ItemStack(Block.stairsBrick, 4), new Object[] { "#  ", "## ", "###", Character.valueOf('#'), Block.brick});
        this.func_92051_a(new ItemStack(Block.stairsStoneBrickSmooth, 4), new Object[] { "#  ", "## ", "###", Character.valueOf('#'), Block.stoneBrick});
        this.func_92051_a(new ItemStack(Block.stairsNetherBrick, 4), new Object[] { "#  ", "## ", "###", Character.valueOf('#'), Block.netherBrick});
        this.func_92051_a(new ItemStack(Block.stairsSandStone, 4), new Object[] { "#  ", "## ", "###", Character.valueOf('#'), Block.sandStone});
        this.func_92051_a(new ItemStack(Item.painting, 1), new Object[] { "###", "#X#", "###", Character.valueOf('#'), Item.stick, Character.valueOf('X'), Block.cloth});
        this.func_92051_a(new ItemStack(Item.itemFrame, 1), new Object[] { "###", "#X#", "###", Character.valueOf('#'), Item.stick, Character.valueOf('X'), Item.leather});
        this.func_92051_a(new ItemStack(Item.appleGold, 1, 0), new Object[] { "###", "#X#", "###", Character.valueOf('#'), Item.goldNugget, Character.valueOf('X'), Item.appleRed});
        this.func_92051_a(new ItemStack(Item.appleGold, 1, 1), new Object[] { "###", "#X#", "###", Character.valueOf('#'), Block.blockGold, Character.valueOf('X'), Item.appleRed});
        this.func_92051_a(new ItemStack(Item.goldenCarrot, 1, 0), new Object[] { "###", "#X#", "###", Character.valueOf('#'), Item.goldNugget, Character.valueOf('X'), Item.carrot});
        this.func_92051_a(new ItemStack(Block.lever, 1), new Object[] { "X", "#", Character.valueOf('#'), Block.cobblestone, Character.valueOf('X'), Item.stick});
        this.func_92051_a(new ItemStack(Block.tripWireSource, 2), new Object[] { "I", "S", "#", Character.valueOf('#'), Block.planks, Character.valueOf('S'), Item.stick, Character.valueOf('I'), Item.ingotIron});
        this.func_92051_a(new ItemStack(Block.torchRedstoneActive, 1), new Object[] { "X", "#", Character.valueOf('#'), Item.stick, Character.valueOf('X'), Item.redstone});
        this.func_92051_a(new ItemStack(Item.redstoneRepeater, 1), new Object[] { "#X#", "III", Character.valueOf('#'), Block.torchRedstoneActive, Character.valueOf('X'), Item.redstone, Character.valueOf('I'), Block.stone});
        this.func_92051_a(new ItemStack(Item.pocketSundial, 1), new Object[] { " # ", "#X#", " # ", Character.valueOf('#'), Item.ingotGold, Character.valueOf('X'), Item.redstone});
        this.func_92051_a(new ItemStack(Item.compass, 1), new Object[] { " # ", "#X#", " # ", Character.valueOf('#'), Item.ingotIron, Character.valueOf('X'), Item.redstone});
        this.func_92051_a(new ItemStack(Item.emptyMap, 1), new Object[] { "###", "#X#", "###", Character.valueOf('#'), Item.paper, Character.valueOf('X'), Item.compass});
        this.func_92051_a(new ItemStack(Block.stoneButton, 1), new Object[] { "#", Character.valueOf('#'), Block.stone});
        this.func_92051_a(new ItemStack(Block.woodenButton, 1), new Object[] { "#", Character.valueOf('#'), Block.planks});
        this.func_92051_a(new ItemStack(Block.pressurePlateStone, 1), new Object[] { "##", Character.valueOf('#'), Block.stone});
        this.func_92051_a(new ItemStack(Block.pressurePlatePlanks, 1), new Object[] { "##", Character.valueOf('#'), Block.planks});
        this.func_92051_a(new ItemStack(Block.dispenser, 1), new Object[] { "###", "#X#", "#R#", Character.valueOf('#'), Block.cobblestone, Character.valueOf('X'), Item.bow, Character.valueOf('R'), Item.redstone});
        this.func_92051_a(new ItemStack(Block.pistonBase, 1), new Object[] { "TTT", "#X#", "#R#", Character.valueOf('#'), Block.cobblestone, Character.valueOf('X'), Item.ingotIron, Character.valueOf('R'), Item.redstone, Character.valueOf('T'), Block.planks});
        this.func_92051_a(new ItemStack(Block.pistonStickyBase, 1), new Object[] { "S", "P", Character.valueOf('S'), Item.slimeBall, Character.valueOf('P'), Block.pistonBase});
        this.func_92051_a(new ItemStack(Item.bed, 1), new Object[] { "###", "XXX", Character.valueOf('#'), Block.cloth, Character.valueOf('X'), Block.planks});
        this.func_92051_a(new ItemStack(Block.enchantmentTable, 1), new Object[] { " B ", "D#D", "###", Character.valueOf('#'), Block.obsidian, Character.valueOf('B'), Item.book, Character.valueOf('D'), Item.diamond});
        this.func_92051_a(new ItemStack(Block.anvil, 1), new Object[] { "III", " i ", "iii", Character.valueOf('I'), Block.blockSteel, Character.valueOf('i'), Item.ingotIron});
        this.addShapelessRecipe(new ItemStack(Item.eyeOfEnder, 1), new Object[] {Item.enderPearl, Item.blazePowder});
        this.addShapelessRecipe(new ItemStack(Item.fireballCharge, 3), new Object[] {Item.gunpowder, Item.blazePowder, Item.coal});
        this.addShapelessRecipe(new ItemStack(Item.fireballCharge, 3), new Object[] {Item.gunpowder, Item.blazePowder, new ItemStack(Item.coal, 1, 1)});
        // Collections.sort(this.recipes, new RecipeSorter(this)); // CraftBukkit - moved below
        this.sort(); // CraftBukkit - call new sort method
        System.out.println(this.recipes.size() + " recipes");
    }

    // CraftBukkit start
    public void sort()
    {
        Collections.sort(this.recipes, new RecipeSorter(this));
    }
    // CraftBukkit end

    // CraftBukkit - default -> public
    public ShapedRecipes func_92051_a(ItemStack par1ItemStack, Object... par2ArrayOfObj)
    {
        String var3 = "";
        int var4 = 0;
        int var5 = 0;
        int var6 = 0;

        if (par2ArrayOfObj[var4] instanceof String[])
        {
            String[] var7 = (String[])((String[])par2ArrayOfObj[var4++]);

            for (int var8 = 0; var8 < var7.length; ++var8)
            {
                String var9 = var7[var8];
                ++var6;
                var5 = var9.length();
                var3 = var3 + var9;
            }
        }
        else
        {
            while (par2ArrayOfObj[var4] instanceof String)
            {
                String var11 = (String)par2ArrayOfObj[var4++];
                ++var6;
                var5 = var11.length();
                var3 = var3 + var11;
            }
        }

        HashMap var12;

        for (var12 = new HashMap(); var4 < par2ArrayOfObj.length; var4 += 2)
        {
            Character var13 = (Character)par2ArrayOfObj[var4];
            ItemStack var14 = null;

            if (par2ArrayOfObj[var4 + 1] instanceof Item)
            {
                var14 = new ItemStack((Item)par2ArrayOfObj[var4 + 1]);
            }
            else if (par2ArrayOfObj[var4 + 1] instanceof Block)
            {
                var14 = new ItemStack((Block)par2ArrayOfObj[var4 + 1], 1, -1);
            }
            else if (par2ArrayOfObj[var4 + 1] instanceof ItemStack)
            {
                var14 = (ItemStack)par2ArrayOfObj[var4 + 1];
            }

            var12.put(var13, var14);
        }

        ItemStack[] var15 = new ItemStack[var5 * var6];

        for (int var16 = 0; var16 < var5 * var6; ++var16)
        {
            char var10 = var3.charAt(var16);

            if (var12.containsKey(Character.valueOf(var10)))
            {
                var15[var16] = ((ItemStack)var12.get(Character.valueOf(var10))).copy();
            }
            else
            {
                var15[var16] = null;
            }
        }

        ShapedRecipes var17 = new ShapedRecipes(var5, var6, var15, par1ItemStack);
        this.recipes.add(var17);
        return var17;
    }

    // CraftBukkit - default -> public
    public void addShapelessRecipe(ItemStack par1ItemStack, Object... par2ArrayOfObj)
    {
        ArrayList var3 = new ArrayList();
        Object[] var4 = par2ArrayOfObj;
        int var5 = par2ArrayOfObj.length;

        for (int var6 = 0; var6 < var5; ++var6)
        {
            Object var7 = var4[var6];

            if (var7 instanceof ItemStack)
            {
                var3.add(((ItemStack)var7).copy());
            }
            else if (var7 instanceof Item)
            {
                var3.add(new ItemStack((Item)var7));
            }
            else
            {
                if (!(var7 instanceof Block))
                {
                    throw new RuntimeException("Invalid shapeless recipy!");
                }

                var3.add(new ItemStack((Block)var7));
            }
        }

        this.recipes.add(new ShapelessRecipes(par1ItemStack, var3));
    }

    public ItemStack findMatchingRecipe(InventoryCrafting par1InventoryCrafting, World par2World)
    {
        int var3 = 0;
        ItemStack var4 = null;
        ItemStack var5 = null;
        int var6;

        for (var6 = 0; var6 < par1InventoryCrafting.getSizeInventory(); ++var6)
        {
            ItemStack var7 = par1InventoryCrafting.getStackInSlot(var6);

            if (var7 != null)
            {
                if (var3 == 0)
                {
                    var4 = var7;
                }

                if (var3 == 1)
                {
                    var5 = var7;
                }

                ++var3;
            }
        }

        if (var3 == 2 && var4.itemID == var5.itemID && var4.stackSize == 1 && var5.stackSize == 1 && Item.itemsList[var4.itemID].isDamageable())
        {
            Item var11 = Item.itemsList[var4.itemID];
            int var13 = var11.getMaxDamage() - var4.getItemDamageForDisplay();
            int var8 = var11.getMaxDamage() - var5.getItemDamageForDisplay();
            int var9 = var13 + var8 + var11.getMaxDamage() * 5 / 100;
            int var10 = var11.getMaxDamage() - var9;

            if (var10 < 0)
            {
                var10 = 0;
            }

            // CraftBukkit start - construct a dummy repair recipe
            ItemStack result = new ItemStack(var4.itemID, 1, var10);
            List<ItemStack> ingredients = new ArrayList<ItemStack>();
            ingredients.add(var4.copy());
            ingredients.add(var5.copy());
            ShapelessRecipes recipe = new ShapelessRecipes(result.copy(), ingredients);
            par1InventoryCrafting.currentRecipe = recipe;
            result = CraftEventFactory.callPreCraftEvent(par1InventoryCrafting, result, lastCraftView, true);
            return result;
            // CraftBukkit end
        }
        else
        {
            for (var6 = 0; var6 < this.recipes.size(); ++var6)
            {
                IRecipe var12 = (IRecipe)this.recipes.get(var6);

                if (var12.matches(par1InventoryCrafting, par2World))
                {
                    // CraftBukkit start - INVENTORY_PRE_CRAFT event
                    par1InventoryCrafting.currentRecipe = var12;
                    ItemStack result = var12.getCraftingResult(par1InventoryCrafting);
                    return CraftEventFactory.callPreCraftEvent(par1InventoryCrafting, result, lastCraftView, false);
                    // CraftBukkit end
                }
            }

            return null;
        }
    }

    /**
     * returns the List<> of all recipes
     */
    public List getRecipeList()
    {
        return this.recipes;
    }
}