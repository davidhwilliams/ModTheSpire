package com.evacipated.cardcrawl.modthespire.patches;

import com.evacipated.cardcrawl.modthespire.ModTheSpire;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

@SpirePatch2(
    clz = UnlockTracker.class,
    method = "unlockAchievement"
)
@SpirePatch2(
    clz = UnlockTracker.class,
    method = "unlockLuckyDay"
)
public class EnableAchievements
{
    public static ExprEditor Instrument()
    {
        return new ExprEditor() {
            @Override
            public void edit(FieldAccess f) throws CannotCompileException
            {
                if (f.isReader() && f.getClassName().equals(Settings.class.getName()) && f.getFieldName().equals("isModded")) {
                    f.replace("$_ = !" + EnableAchievements.class.getName() + ".isAchievementsEnabled($proceed($$));");
                }
            }
        };
    }

    public static boolean isAchievementsEnabled(boolean isModded)
    {
        // Always allow achievements if isModded has been changed to false by something
        // like the Achievement Enabler mod
        if (!isModded) {
            return true;
        }
        return ModTheSpire.isAchievementsEnabled();
    }
}
