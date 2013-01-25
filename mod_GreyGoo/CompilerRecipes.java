package StevenGreyGoo.mod_GreyGoo;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;

public class CompilerRecipes
{
    private static final CompilerRecipes smeltingBase = new CompilerRecipes();

    private Map smeltingList = new HashMap();
    private Map experienceList = new HashMap();
    private Map metaSmeltingList = new HashMap();

    public static final CompilerRecipes smelting()
    {
        return smeltingBase;
    }

    private CompilerRecipes()
    {
        this.addSmelting(mod_GreyGoo.ItemMatrixRed.itemID, new ItemStack(mod_GreyGoo.BlockCleaner), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemMatrixYellow.itemID, new ItemStack(mod_GreyGoo.BlockGravityGoo), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemMatrixOrange.itemID, new ItemStack(mod_GreyGoo.BlockWall), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemMatrixGreen.itemID, new ItemStack(mod_GreyGoo.BlockInert), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemMatrixWhite.itemID, new ItemStack(mod_GreyGoo.BlockAirEater), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemMatrixPurple.itemID, new ItemStack(mod_GreyGoo.BlockGreyGoo), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemMatrixBrown.itemID, new ItemStack(mod_GreyGoo.BlockMinerGoo), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemMatrixBrownRed.itemID, new ItemStack(mod_GreyGoo.BlockRapidMiner), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemMatrixBlue.itemID, new ItemStack(mod_GreyGoo.BlockWaterEater), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemMatrixPurpleRed.itemID, new ItemStack(mod_GreyGoo.BlockRapidEater), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemMatrixWhiteGreen.itemID, new ItemStack(mod_GreyGoo.BlockBubble), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemMatrixGreenRed.itemID, new ItemStack(mod_GreyGoo.BlockFreezer), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemMatrixOrangeWhite.itemID, new ItemStack(mod_GreyGoo.BlockOrangeWhite), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemMatrixOrangePurple.itemID, new ItemStack(mod_GreyGoo.BlockOrangePurple), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemMatrixOrangeRed.itemID, new ItemStack(mod_GreyGoo.BlockOrangeRed), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemMatrixGrey.itemID, new ItemStack(mod_GreyGoo.BlockGreyEater), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemMatrixBlueRed.itemID, new ItemStack(mod_GreyGoo.BlockRapidWaterEater), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemMatrixRainbow.itemID, new ItemStack(mod_GreyGoo.BlockRestorer), 0.7F);

    }

    public void addSmelting(int par1, ItemStack par2ItemStack, float par3)
    {
        this.smeltingList.put(Integer.valueOf(par1), par2ItemStack);
        this.experienceList.put(Integer.valueOf(par2ItemStack.itemID), Float.valueOf(par3));
    }

    @Deprecated
    public ItemStack getSmeltingResult(int par1)
    {
        return (ItemStack)this.smeltingList.get(Integer.valueOf(par1));
    }

    public Map getSmeltingList()
    {
        return this.smeltingList;
    }

    public float getExperience(int par1)
    {
        return this.experienceList.containsKey(Integer.valueOf(par1)) ? ((Float)this.experienceList.get(Integer.valueOf(par1))).floatValue() : 0.0F;
    }

    public void addSmelting(int itemID, int metadata, ItemStack itemstack)
    {
        metaSmeltingList.put(Arrays.asList(itemID, metadata), itemstack);
    }

    public ItemStack getSmeltingResult(ItemStack item)
    {
        if (item == null)
        {
            return null;
        }

        ItemStack ret = (ItemStack)metaSmeltingList.get(Arrays.asList(item.itemID, item.getItemDamage()));

        if (ret != null)
        {
            return ret;
        }

        return (ItemStack)smeltingList.get(Integer.valueOf(item.itemID));
    }
}
