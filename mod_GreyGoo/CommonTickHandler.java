package StevenGreyGoo.mod_GreyGoo;
import java.util.EnumSet;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
public class CommonTickHandler implements ITickHandler
{
    boolean hasDone = false;
    boolean didSave = false;
    boolean hasChecked = false;
    int tickCounterRWE = 0;
    int tickCounterF = 0;
    int tickCounterB = 0;
    int tickCounterRM = 0;
    int tickCounterRW = 0;
    int tickCounterOR = 0;
    int tickCounterOW = 0;
    int tickCounterOP = 0;
    int tickCounterRE = 0;
    int resetCounter = 0;

    int tickCounterSycnValues = 0;

    String lastworldname;

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {}

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        if (type.equals(EnumSet.of(TickType.SERVER)))
        {
            onTickInGame();
        }
    }

    public EnumSet ticks()
    {
        return EnumSet.of(TickType.SERVER);
    }

    public String getLabel()
    {
        return null;
    }

    private void onTickInGame()
    {
        if (tickCounterRWE > mod_GreyGoo.BlueRedSpreadTime && mod_GreyGoo.RapidWaterEaterisSpreading)
        {
            mod_GreyGoo.RapidWaterEaterisSpreading = false;
            tickCounterRWE = 0;
        }
        else if(mod_GreyGoo.RapidWaterEaterisSpreading)
        {
            ++tickCounterRWE;

          
        }

        if (tickCounterF > mod_GreyGoo.RedGreenSpreadTime && mod_GreyGoo.FreezerisSpreading)
        {
            mod_GreyGoo.FreezerisSpreading = false;
            tickCounterF = 0;
        }
        else if(mod_GreyGoo.FreezerisSpreading)
        {
            ++tickCounterF;

            
        }

        if (tickCounterRM > mod_GreyGoo.BrownRedSpreadTime && mod_GreyGoo.RapidMinerisSpreading)
        {
            mod_GreyGoo.RapidMinerisSpreading = false;
            tickCounterRM = 0;
        }
        else if(mod_GreyGoo.RapidMinerisSpreading)
        {
            ++tickCounterRM;

          
        }

        if (tickCounterB > mod_GreyGoo.GreenWhteSpreadTime && mod_GreyGoo.BubbleisSpreading)
        {
            mod_GreyGoo.BubbleisSpreading = false;
            tickCounterB = 0;
        }
        else if(mod_GreyGoo.BubbleisSpreading)
        {
            ++tickCounterB;

           
        }

        if (tickCounterOR > mod_GreyGoo.OrangeRedSpreadTime && mod_GreyGoo.OrangeRedIsSpreading)
        {

            mod_GreyGoo.OrangeRedIsSpreading = false;
            tickCounterOR = 0;
        }
        else if(mod_GreyGoo.OrangeRedIsSpreading)
        {

            ++tickCounterOR;

            
        }

        if (tickCounterOP > mod_GreyGoo.OrangePurpleSpreadTime && mod_GreyGoo.OrangePurpleIsSpreading)
        {
            mod_GreyGoo.OrangePurpleIsSpreading = false;
            tickCounterOP = 0;
        }
        else if(mod_GreyGoo.OrangePurpleIsSpreading)
        {
            ++tickCounterOP;

           
        }

        if (tickCounterOW > mod_GreyGoo.OrangeWhiteSpreadTime && mod_GreyGoo.OrangeWhiteIsSpreading)
        {
            mod_GreyGoo.OrangeWhiteIsSpreading = false;
            tickCounterOW = 0;
        }
        else if(mod_GreyGoo.OrangeWhiteIsSpreading)
        {
            ++tickCounterOW;

           
        }

        if (tickCounterRE > mod_GreyGoo.PurpleRedSpreadTime && mod_GreyGoo.RapidEaterIsSpreading)
        {
            mod_GreyGoo.RapidEaterIsSpreading = false;
            tickCounterRE = 0;
        }
        else if(mod_GreyGoo.RapidEaterIsSpreading)
        {
           // mod_GreyGoo.proxy.printStringClient(String.valueOf(tickCounterRE));

            ++tickCounterRE;

            
        }

       mod_GreyGoo.instance.spreadLimiter.resetSpreadCounters();
    }
}