package moze_intel.projecte.client.lang;

import moze_intel.projecte.PECore;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.data.DataGenerator;

//TODO: Finish transitioning things over to this (blocks and items etc, and other i18n calls)
public class PELangProvider extends BaseLanguageProvider {

    public PELangProvider(DataGenerator gen) {
        super(gen, PECore.MODID);
    }

    @Override
    protected void addTranslations() {
        add(PELang.PROJECTE, "ProjectE");
        add(PELang.HIGH_ALCHEMIST, "High alchemist %s has joined the server");
        add(PELang.SECONDS, "%s seconds");
        add(PELang.EVERY_TICK, "%s seconds (every tick)");

        add(PELang.CURRENT_MODE, "Mode: %s");
        add(PELang.NIGHT_VISION, "Night Vision: %s");
        add(PELang.STEP_ASSIST, "Step Assist: %s");
        add(PELang.SHOWBAG_NAMED, "%s (%s)");
        add(PELang.EMC_MAX_GEN_RATE, "Maximum Generation Rate: %s EMC/s");
        add(PELang.EMC_MAX_OUTPUT_RATE, "Maximum Output Rate: %s EMC/s");
        add(PELang.EMC_MAX_GEN_RATE, "Maximum Storage: %s EMC");

        add(PELang.EMC, "%s EMC");
        add(PELang.EMC_TOOLTIP, "EMC: %s");
        add(PELang.EMC_STACK_TOOLTIP, "Stack EMC: %s");
        add(PELang.EMC_TOOLTIP_WITH_SELL, "EMC: %s (%s)");
        add(PELang.EMC_STACK_TOOLTIP_WITH_SELL, "Stack EMC: %s (%s)");
        add(PELang.EMC_STORED, "Stored EMC: %s");

        add(PELang.TOOLTIP_STORED_XP, "Stored XP: %s");
    }
}