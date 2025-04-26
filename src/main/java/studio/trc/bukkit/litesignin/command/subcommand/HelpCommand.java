package studio.trc.bukkit.litesignin.command.subcommand;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import studio.trc.bukkit.litesignin.command.SignInSubCommand;
import studio.trc.bukkit.litesignin.command.SignInSubCommandType;
import studio.trc.bukkit.litesignin.util.MessageUtil;

import static studio.trc.bukkit.litesignin.command.SignInCommand.EMPTY;

public class HelpCommand
    implements SignInSubCommand
{
    @Override
    public void execute(CommandSender sender, String subCommand, String... args) {
        MessageUtil.sendCommandMessage(sender, "Help-Command");
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String subCommand, String... args) {
        return EMPTY;
    }

    @Override
    public SignInSubCommandType getCommandType() {
        return SignInSubCommandType.HELP;
    }
}
