package StevenGreyGoo.mod_GreyGoo;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;

public class AssemblerRecipes
{
    private static final AssemblerRecipes smeltingBase = new AssemblerRecipes();

    private Map smeltingList = new HashMap();
    private Map experienceList = new HashMap();
    private Map metaSmeltingList = new HashMap();

    public static final AssemblerRecipes smelting()
    {
        return smeltingBase;
    }

    private AssemblerRecipes()
    {
        this.addSmelting(mod_GreyGoo.ItemModifierRed.itemID, mod_GreyGoo.ItemModifierRed.itemID, new ItemStack(mod_GreyGoo.ItemMatrixRed), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierYellow.itemID, mod_GreyGoo.ItemModifierYellow.itemID, new ItemStack(mod_GreyGoo.ItemMatrixYellow), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierOrange.itemID, mod_GreyGoo.ItemModifierOrange.itemID, new ItemStack(mod_GreyGoo.ItemMatrixOrange), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierGreen.itemID, mod_GreyGoo.ItemModifierGreen.itemID, new ItemStack(mod_GreyGoo.ItemMatrixGreen), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierWhite.itemID, mod_GreyGoo.ItemModifierWhite.itemID, new ItemStack(mod_GreyGoo.ItemMatrixWhite), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierPurple.itemID, mod_GreyGoo.ItemModifierPurple.itemID, new ItemStack(mod_GreyGoo.ItemMatrixPurple), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierBrown.itemID, mod_GreyGoo.ItemModifierBrown.itemID, new ItemStack(mod_GreyGoo.ItemMatrixBrown), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierBlue.itemID, mod_GreyGoo.ItemModifierBlue.itemID, new ItemStack(mod_GreyGoo.ItemMatrixBlue), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierGrey.itemID, mod_GreyGoo.ItemModifierGrey.itemID, new ItemStack(mod_GreyGoo.ItemMatrixGrey), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierGrey.itemID, mod_GreyGoo.ItemModifierGrey.itemID, new ItemStack(mod_GreyGoo.ItemMatrixGrey), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierGrey.itemID, mod_GreyGoo.ItemModifierGreen.itemID, new ItemStack(mod_GreyGoo.ItemMatrixGreen), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierGrey.itemID, mod_GreyGoo.ItemModifierBlue.itemID, new ItemStack(mod_GreyGoo.ItemMatrixBlue), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierGrey.itemID, mod_GreyGoo.ItemModifierYellow.itemID, new ItemStack(mod_GreyGoo.ItemMatrixYellow), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierGrey.itemID, mod_GreyGoo.ItemModifierPurple.itemID, new ItemStack(mod_GreyGoo.ItemMatrixPurple), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierGrey.itemID, mod_GreyGoo.ItemModifierOrange.itemID, new ItemStack(mod_GreyGoo.ItemMatrixOrange), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierGrey.itemID, mod_GreyGoo.ItemModifierWhite.itemID, new ItemStack(mod_GreyGoo.ItemMatrixWhite), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierGrey.itemID, mod_GreyGoo.ItemModifierBrown.itemID, new ItemStack(mod_GreyGoo.ItemMatrixBrown), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierGrey.itemID, mod_GreyGoo.ItemModifierRed.itemID, new ItemStack(mod_GreyGoo.ItemMatrixRed), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierGrey.itemID, mod_GreyGoo.ItemModifierGreen.itemID, new ItemStack(mod_GreyGoo.ItemMatrixGreen), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierRed.itemID, mod_GreyGoo.ItemModifierGreen.itemID, new ItemStack(mod_GreyGoo.ItemMatrixGreenRed), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierRed.itemID, mod_GreyGoo.ItemModifierBlue.itemID, new ItemStack(mod_GreyGoo.ItemMatrixBlueRed), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierRed.itemID, mod_GreyGoo.ItemModifierYellow.itemID, new ItemStack(mod_GreyGoo.ItemMatrixYellowRed), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierRed.itemID, mod_GreyGoo.ItemModifierPurple.itemID, new ItemStack(mod_GreyGoo.ItemMatrixPurpleRed), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierRed.itemID, mod_GreyGoo.ItemModifierOrange.itemID, new ItemStack(mod_GreyGoo.ItemMatrixOrangeRed), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierRed.itemID, mod_GreyGoo.ItemModifierWhite.itemID, new ItemStack(mod_GreyGoo.ItemMatrixGrey), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierRed.itemID, mod_GreyGoo.ItemModifierBrown.itemID, new ItemStack(mod_GreyGoo.ItemMatrixBrownRed), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierRed.itemID, mod_GreyGoo.ItemModifierRed.itemID, new ItemStack(mod_GreyGoo.ItemMatrixRed), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierGreen.itemID, mod_GreyGoo.ItemModifierBlue.itemID, new ItemStack(mod_GreyGoo.ItemMatrixGrey), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierGreen.itemID, mod_GreyGoo.ItemModifierYellow.itemID, new ItemStack(mod_GreyGoo.ItemMatrixYellowRed), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierGreen.itemID, mod_GreyGoo.ItemModifierPurple.itemID, new ItemStack(mod_GreyGoo.ItemMatrixGrey), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierGreen.itemID, mod_GreyGoo.ItemModifierOrange.itemID, new ItemStack(mod_GreyGoo.ItemMatrixGrey), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierGreen.itemID, mod_GreyGoo.ItemModifierWhite.itemID, new ItemStack(mod_GreyGoo.ItemMatrixWhiteGreen), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierGreen.itemID, mod_GreyGoo.ItemModifierBrown.itemID, new ItemStack(mod_GreyGoo.ItemMatrixGrey), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierGreen.itemID, mod_GreyGoo.ItemModifierGreen.itemID, new ItemStack(mod_GreyGoo.ItemMatrixGreen), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierBlue.itemID, mod_GreyGoo.ItemModifierBlue.itemID, new ItemStack(mod_GreyGoo.ItemMatrixBlue), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierBlue.itemID, mod_GreyGoo.ItemModifierYellow.itemID, new ItemStack(mod_GreyGoo.ItemMatrixGrey), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierBlue.itemID, mod_GreyGoo.ItemModifierPurple.itemID, new ItemStack(mod_GreyGoo.ItemMatrixGrey), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierBlue.itemID, mod_GreyGoo.ItemModifierOrange.itemID, new ItemStack(mod_GreyGoo.ItemMatrixGrey), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierBlue.itemID, mod_GreyGoo.ItemModifierWhite.itemID, new ItemStack(mod_GreyGoo.ItemMatrixGrey), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierBlue.itemID, mod_GreyGoo.ItemModifierBrown.itemID, new ItemStack(mod_GreyGoo.ItemMatrixGrey), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierBrown.itemID, mod_GreyGoo.ItemModifierBrown.itemID, new ItemStack(mod_GreyGoo.ItemMatrixBrown), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierBrown.itemID, mod_GreyGoo.ItemModifierYellow.itemID, new ItemStack(mod_GreyGoo.ItemMatrixGrey), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierBrown.itemID, mod_GreyGoo.ItemModifierPurple.itemID, new ItemStack(mod_GreyGoo.ItemMatrixGrey), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierBrown.itemID, mod_GreyGoo.ItemModifierOrange.itemID, new ItemStack(mod_GreyGoo.ItemMatrixGrey), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierBrown.itemID, mod_GreyGoo.ItemModifierWhite.itemID, new ItemStack(mod_GreyGoo.ItemMatrixGrey), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierWhite.itemID, mod_GreyGoo.ItemModifierWhite.itemID, new ItemStack(mod_GreyGoo.ItemMatrixWhite), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierWhite.itemID, mod_GreyGoo.ItemModifierYellow.itemID, new ItemStack(mod_GreyGoo.ItemMatrixGrey), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierWhite.itemID, mod_GreyGoo.ItemModifierPurple.itemID, new ItemStack(mod_GreyGoo.ItemMatrixGrey), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierWhite.itemID, mod_GreyGoo.ItemModifierOrange.itemID, new ItemStack(mod_GreyGoo.ItemMatrixOrangeWhite), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierOrange.itemID, mod_GreyGoo.ItemModifierOrange.itemID, new ItemStack(mod_GreyGoo.ItemMatrixOrange), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierOrange.itemID, mod_GreyGoo.ItemModifierYellow.itemID, new ItemStack(mod_GreyGoo.ItemMatrixGrey), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierOrange.itemID, mod_GreyGoo.ItemModifierPurple.itemID, new ItemStack(mod_GreyGoo.ItemMatrixOrangePurple), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierPurple.itemID, mod_GreyGoo.ItemModifierPurple.itemID, new ItemStack(mod_GreyGoo.ItemMatrixPurple), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierPurple.itemID, mod_GreyGoo.ItemModifierYellow.itemID, new ItemStack(mod_GreyGoo.ItemMatrixGrey), 0.7F);
        this.addSmelting(mod_GreyGoo.ItemModifierYellow.itemID, mod_GreyGoo.ItemModifierYellow.itemID, new ItemStack(mod_GreyGoo.ItemMatrixYellow), 0.7F);
    }

    public void addSmelting(int par1, int par2, ItemStack par2ItemStack, float par3)
    {
        int theComboValue;
        String firstvalue;
        String secondvalue;
        String comboString;
        firstvalue =	Integer.toString(par1);
        secondvalue = Integer.toString(par2);
        comboString = firstvalue + secondvalue;
        this.smeltingList.put(comboString, par2ItemStack);
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

    public ItemStack getSmeltingResult(ItemStack item, ItemStack item2)
    {
        if (item == null || item2 == null)
        {
            return null;
        }

        int theComboValue;
        String firstvalue;
        String secondvalue;
        String comboString;
        int par1 = item.getItem().itemID;
        int par2 = item2.getItem().itemID;
        firstvalue =	Integer.toString(par1);
        secondvalue = Integer.toString(par2);
        comboString = firstvalue + secondvalue;

        if ((ItemStack)smeltingList.get(comboString) != null)
        {
            return (ItemStack)smeltingList.get(comboString);
        }
        else
        {
            comboString = secondvalue + firstvalue;
            return (ItemStack)smeltingList.get(comboString);
        }
    }
}
