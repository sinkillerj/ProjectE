package moze_intel.projecte.utils;

import moze_intel.projecte.PECore;
import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.scoreboard.ScoreCriteriaReadOnly;

public final class AchievementHandler
{
	public final static IScoreCriteria SCOREBOARD_EMC = new ScoreCriteriaReadOnly(PECore.MODID + ":emc_score");

	public static void init()
	{
	}
}
