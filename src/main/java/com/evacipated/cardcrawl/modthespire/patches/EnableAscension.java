package com.evacipated.cardcrawl.modthespire.patches;

import com.evacipated.cardcrawl.modthespire.ModTheSpire;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.helpers.Prefs;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class EnableAscension
{
    @SpirePatch2(
        clz = UnlockTracker.class,
        method = "isAscensionUnlocked"
    )
    public static class UnlockAscension
    {
        public static boolean Postfix(boolean __result)
        {
            if (unlockAscension()) {
                return true;
            }
            return __result;
        }
    }

    @SpirePatch2(
        clz = CharacterOption.class,
        method = "updateHitbox"
    )
    public static class MaxAscension
    {
        public static ExprEditor Instrument()
        {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException
                {
                    if (m.getClassName().equals(Prefs.class.getName()) && m.getMethodName().equals("getInteger")) {
                        m.replace(
                            "$_ = " + EnableAscension.class.getName() + ".getInteger($0, $$);"
                        );
                    }
                }
            };
        }
    }

    public static int getInteger(Prefs prefs, String key, int def)
    {
        switch (key) {
            case "ASCENSION_LEVEL":
                if (unlockAscension()) {
                    return 20;
                }
                break;
            case "LAST_ASCENSION_LEVEL":
                if (!unlockAscension()) {
                    int ret = prefs.getInteger(key, def);
                    int max = prefs.getInteger("ASCENSION_LEVEL", 1);
                    return Math.min(ret, max);
                }
        }
        return prefs.getInteger(key, def);
    }

    private static boolean unlockAscension()
    {
        return ModTheSpire.MTS_CONFIG.getBool("ascensionUnlock", false);
    }
}
