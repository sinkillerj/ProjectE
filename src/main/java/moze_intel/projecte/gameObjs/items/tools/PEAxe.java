package moze_intel.projecte.gameObjs.items.tools;

import java.util.List;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.gameObjs.IMatterType;
import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes;
import moze_intel.projecte.utils.ToolHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class PEAxe extends PETool implements IItemCharge {

	public PEAxe(IMatterType matterType, int numCharges, Properties props) {
		super(matterType, BlockTags.MINEABLE_WITH_AXE, numCharges, props.attributes(PETool.createAttributes(matterType, 5, -3)));
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		if (player == null) {
			return InteractionResult.PASS;
		}
		Level level = context.getLevel();
		BlockState blockState = level.getBlockState(context.getClickedPos());
		return ToolHelper.performActions(context, blockState, ToolHelper.stripLogsAOE(context, blockState, 0),
				(ctx, state) -> ToolHelper.scrapeAOE(ctx, state, 0),
				(ctx, state) -> ToolHelper.waxOffAOE(ctx, state, 0),
				(ctx, state) -> {
					if (state.is(BlockTags.LOGS)) {
						return ToolHelper.clearTagAOE(ctx.getLevel(), ctx.getPlayer(), ctx.getHand(), ctx.getItemInHand(), 0, BlockTags.LOGS);
					}
					return InteractionResult.PASS;
				});
	}
}
