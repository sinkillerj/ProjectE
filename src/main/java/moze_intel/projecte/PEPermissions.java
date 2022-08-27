package moze_intel.projecte;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContextKey;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionNode.PermissionResolver;
import net.minecraftforge.server.permission.nodes.PermissionType;
import net.minecraftforge.server.permission.nodes.PermissionTypes;

public class PEPermissions {

	private static final List<PermissionNode<?>> NODES_TO_REGISTER = new ArrayList<>();
	private static final PermissionResolver<Boolean> PLAYER_IS_OP = (player, uuid, context) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS);

	//Commands
	public static final CommandPermissionNode COMMAND = new CommandPermissionNode(node("command", PermissionTypes.BOOLEAN,
			(player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL)), Commands.LEVEL_ALL);

	public static final CommandPermissionNode COMMAND_CLEAR_KNOWLEDGE = nodeOpCommand("clear_knowledge");
	public static final CommandPermissionNode COMMAND_DUMP_MISSING = nodeOpCommand("dump_missing");
	public static final CommandPermissionNode COMMAND_REMOVE_EMC = nodeOpCommand("remove_emc");
	public static final CommandPermissionNode COMMAND_RESET_EMC = nodeOpCommand("reset_emc");
	public static final CommandPermissionNode COMMAND_SET_EMC = nodeOpCommand("set_emc");
	public static final CommandPermissionNode COMMAND_SHOW_BAG = nodeOpCommand("show_bag");

	private static CommandPermissionNode nodeOpCommand(String nodeName) {
		PermissionNode<Boolean> node = node("command." + nodeName, PermissionTypes.BOOLEAN, PLAYER_IS_OP);
		return new CommandPermissionNode(node, Commands.LEVEL_GAMEMASTERS);
	}

	@SafeVarargs
	private static <T> PermissionNode<T> node(String nodeName, PermissionType<T> type, PermissionResolver<T> defaultResolver, PermissionDynamicContextKey<T>... dynamics) {
		PermissionNode<T> node = new PermissionNode<>(PECore.MODID, nodeName, type, defaultResolver, dynamics);
		NODES_TO_REGISTER.add(node);
		return node;
	}

	public static void registerPermissionNodes(PermissionGatherEvent.Nodes event) {
		event.addNodes(NODES_TO_REGISTER);
	}

	public record CommandPermissionNode(PermissionNode<Boolean> node, int fallbackLevel) implements Predicate<CommandSourceStack> {

		@Override
		public boolean test(CommandSourceStack source) {
			return source.source instanceof ServerPlayer player ? PermissionAPI.getPermission(player, node) : source.hasPermission(fallbackLevel);
		}
	}
}