package moze_intel.projecte.utils;

import net.minecraft.scoreboard.ScoreCriteria;

public class ReadOnlyScoreCriteria extends ScoreCriteria {
    // Expose protected superclass ctor
    public ReadOnlyScoreCriteria(String name) {
        super(name, true, RenderType.INTEGER);
    }
}
