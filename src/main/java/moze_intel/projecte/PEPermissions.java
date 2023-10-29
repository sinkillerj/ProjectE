package moze_intel.projecte;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContext;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContextKey;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionNode.PermissionResolver;
import net.minecraftforge.server.permission.nodes.PermissionType;
import net.minecraftforge.server.permission.nodes.PermissionTypes;
import org.jetbrains.annotations.Nullable;

public class PEPermissions {

	private static final List<PermissionNode<?>> NODES_TO_REGISTER = new ArrayList<>();
	private static final PermissionResolver<Boolean> PLAYER_IS_OP = (player, uuid, context) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS);
	private static final PermissionResolver<Boolean> ALWAYS_TRUE = (player, uuid, context) -> true;

	//Commands
	public static final CommandPermissionNode COMMAND = new CommandPermissionNode(node("command", PermissionTypes.BOOLEAN,
			(player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL)), Commands.LEVEL_ALL);

	public static final CommandPermissionNode COMMAND_DUMP_MISSING = nodeOpCommand("dump_missing");
	public static final CommandPermissionNode COMMAND_REMOVE_EMC = nodeOpCommand("remove_emc");
	public static final CommandPermissionNode COMMAND_RESET_EMC = nodeOpCommand("reset_emc");
	public static final CommandPermissionNode COMMAND_SET_EMC = nodeOpCommand("set_emc");
	public static final CommandPermissionNode COMMAND_SHOW_BAG = nodeOpCommand("show_bag");
	public static final CommandPermissionNode COMMAND_EMC = nodeOpCommand("emc");
	public static final CommandPermissionNode COMMAND_EMC_ADD = nodeSubCommand(COMMAND_EMC, "add");
	public static final CommandPermissionNode COMMAND_EMC_REMOVE = nodeSubCommand(COMMAND_EMC, "remove");
	public static final CommandPermissionNode COMMAND_EMC_SET = nodeSubCommand(COMMAND_EMC, "set");
	public static final CommandPermissionNode COMMAND_EMC_TEST = nodeSubCommand(COMMAND_EMC, "test");
	public static final CommandPermissionNode COMMAND_EMC_GET = nodeSubCommand(COMMAND_EMC, "get");
	public static final CommandPermissionNode COMMAND_KNOWLEDGE = nodeOpCommand("knowledge");
	public static final CommandPermissionNode COMMAND_KNOWLEDGE_CLEAR = nodeSubCommand(COMMAND_KNOWLEDGE, "clear");
	public static final CommandPermissionNode COMMAND_KNOWLEDGE_LEARN = nodeSubCommand(COMMAND_KNOWLEDGE, "learn");
	public static final CommandPermissionNode COMMAND_KNOWLEDGE_UNLEARN = nodeSubCommand(COMMAND_KNOWLEDGE, "unlearn");
	public static final CommandPermissionNode COMMAND_KNOWLEDGE_TEST = nodeSubCommand(COMMAND_KNOWLEDGE, "test");

	private static CommandPermissionNode nodeOpCommand(String nodeName) {
		PermissionNode<Boolean> node = node("command." + nodeName, PermissionTypes.BOOLEAN, PLAYER_IS_OP);
		return new CommandPermissionNode(node, Commands.LEVEL_GAMEMASTERS);
	}

	private static CommandPermissionNode nodeSubCommand(CommandPermissionNode parent, String nodeName) {
		//Because sub commands can assume that the parent was checked before getting to them, we can have a default resolver of always true
		// The main benefit for them to have their own node is just in case someone wants to do more restricting
		PermissionNode<Boolean> node = subNode(parent.node, nodeName, ALWAYS_TRUE);
		return new CommandPermissionNode(node, parent.fallbackLevel);
	}

	/**
	 * @apiNote For use in sub nodes that don't know if there parent has been checked yet.
	 */
	private static <T> PermissionNode<T> subNode(PermissionNode<T> parent, String nodeName) {
		return subNode(parent, nodeName, (player, uuid, context) -> getPermission(player, uuid, parent, context));
	}

	private static <T> PermissionNode<T> subNode(PermissionNode<T> parent, String nodeName, ResultTransformer<T> defaultRestrictionIncrease) {
		return subNode(parent, nodeName, (player, uuid, context) -> {
			T result = getPermission(player, uuid, parent, context);
			return defaultRestrictionIncrease.transform(player, uuid, result, context);
		});
	}

	private static <T> PermissionNode<T> subNode(PermissionNode<T> parent, String nodeName, PermissionResolver<T> defaultResolver) {
		String fullParentName = parent.getNodeName();
		//Strip the modid from the parent's node name
		String parentName = fullParentName.substring(fullParentName.indexOf('.') + 1);
		return node(parentName + "." + nodeName, parent.getType(), defaultResolver);
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

	private static <T> T getPermission(@Nullable ServerPlayer player, UUID playerUUID, PermissionNode<T> node, PermissionDynamicContext<?>... context) {
		if (player == null) {
			return PermissionAPI.getOfflinePermission(playerUUID, node, context);
		}
		return PermissionAPI.getPermission(player, node, context);
	}

	public record CommandPermissionNode(PermissionNode<Boolean> node, int fallbackLevel) implements Predicate<CommandSourceStack> {

		@Override
		public boolean test(CommandSourceStack source) {
			//See https://github.com/MinecraftForge/MinecraftForge/commit/f7eea35cb9b043aae0a3866a9578724aa7560585 for details on why
			// has permission is checked first and the implications
			return source.hasPermission(fallbackLevel) || source.source instanceof ServerPlayer player && PermissionAPI.getPermission(player, node);
		}
	}

	@FunctionalInterface
	private interface ResultTransformer<T> {

		T transform(@Nullable ServerPlayer player, UUID playerUUID, T resolved, PermissionDynamicContext<?>... context);
	}
}