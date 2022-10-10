package moze_intel.projecte.network.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import moze_intel.projecte.PEPermissions;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.utils.TransmutationEMCFormatter;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.math.BigInteger;

public class EMCCMD {
    private enum ActionType {
        ADD,
        REMOVE,
        SET,
        GET,
        TEST
    }
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext context) {
         return Commands.literal("emc")
            .requires(PEPermissions.COMMAND)
            .then(Commands.literal("add")
                .requires(PEPermissions.COMMAND_EMC_ADD)
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("value", StringArgumentType.string())
                        .executes((ctx) -> handle(ctx, ActionType.ADD))
                    )
                )
            )
            .then(Commands.literal("remove")
                .requires(PEPermissions.COMMAND_EMC_REMOVE)
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("value", StringArgumentType.string())
                        .executes((ctx) -> handle(ctx, ActionType.REMOVE))
                    )
                )
            )
            .then(Commands.literal("set")
                .requires(PEPermissions.COMMAND_EMC_SET)
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("value", StringArgumentType.string())
                        .executes((ctx) -> handle(ctx, ActionType.SET))
                    )
                )
            )
            .then(Commands.literal("test")
                .requires(PEPermissions.COMMAND_EMC_TEST)
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("value", StringArgumentType.string())
                        .executes((ctx) -> handle(ctx, ActionType.TEST))
                    )
                )
            )
            .then(Commands.literal("get")
                .requires(PEPermissions.COMMAND_EMC_GET)
                .then(Commands.argument("player", EntityArgument.player())
                    .executes((ctx) -> handle(ctx, ActionType.GET))
                )
            );
    }

    private static MutableComponent formatEMC(BigInteger emc) {
        return MutableComponent.create(TransmutationEMCFormatter.formatEMC(emc).getContents()).withStyle(ChatFormatting.GRAY);
    }



    private static int handle(CommandContext<CommandSourceStack> ctx, ActionType action) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        IKnowledgeProvider provider;
        try {
            provider = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY).orElseThrow(() -> new IllegalStateException("Failed to get knowledge provider."));
        } catch(IllegalStateException ignore) {
            ctx.getSource().sendFailure(PELang.COMMAND_PROVIDER_FAIL.translateColored(ChatFormatting.RED));
            return 0;
        }
        if (action == ActionType.GET) {
            ctx.getSource().sendSuccess(PELang.COMMAND_EMC_GET_SUCCESS.translate(player.getDisplayName(), formatEMC(provider.getEmc())), true);
            return 1;
        }
        String val = StringArgumentType.getString(ctx, "value");
        @Nullable BigInteger value = null;

        try {
            value = new BigInteger(val);
            switch (action) {
                case ADD -> {
                    if (value.compareTo(BigInteger.ZERO) < 0) {
                        action = ActionType.REMOVE;
                        value = value.abs();
                    }
                }
                case REMOVE -> {
                    if (value.compareTo(BigInteger.ZERO) < 0) {
                        action = ActionType.ADD;
                        value = value.abs();
                    }
                }
                case SET, TEST -> {
                    if (value.compareTo(BigInteger.ZERO) < 0) value = null;
                }
            }
        } catch (NumberFormatException ignore) {}
        if(value == null) {
            ctx.getSource().sendFailure(PELang.COMMAND_EMC_INVALID.translateColored(ChatFormatting.RED, val));
            return 0;
        }

        int response = 1;
        BigInteger newEMC = provider.getEmc();
        switch(action) {
            case ADD -> {
                newEMC = newEMC.add(value);
                ctx.getSource().sendSuccess(PELang.COMMAND_EMC_ADD_SUCCESS.translate(formatEMC(value), player.getDisplayName(), formatEMC(newEMC)), true);
            }
            case REMOVE -> {
                newEMC = newEMC.subtract(value);
                ctx.getSource().sendSuccess(PELang.COMMAND_EMC_REMOVE_SUCCESS.translate(formatEMC(value), player.getDisplayName(), formatEMC(newEMC)), true);
            }
            case SET -> {
                newEMC = value;
                ctx.getSource().sendSuccess(PELang.COMMAND_EMC_SET_SUCCESS.translate(player.getDisplayName(), formatEMC(value)), true);
            }
            case TEST -> {
                boolean canTake = newEMC.compareTo(value) >= 0;
                if(canTake) {
                    ctx.getSource().sendSuccess(PELang.COMMAND_EMC_TEST_SUCCESS.translateColored(ChatFormatting.GREEN, player.getDisplayName(), formatEMC(value)), true);
                } else {
                    ctx.getSource().sendFailure(PELang.COMMAND_EMC_TEST_FAIL.translateColored(ChatFormatting.RED, player.getDisplayName(), formatEMC(value)));
                    response = 0;
                }
            }
        }

        if(response == 1 && action != ActionType.TEST) {
            if (newEMC.compareTo(BigInteger.ZERO) < 0) {
                ctx.getSource().sendFailure(PELang.COMMAND_EMC_NEGATIVE.translateColored(ChatFormatting.RED, formatEMC(value), player.getDisplayName()));
                return 0;
            }
            provider.setEmc(newEMC);
            provider.syncEmc(player);
        }
        return response;
    }
}
