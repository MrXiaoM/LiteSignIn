package studio.trc.bukkit.litesignin.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import org.jetbrains.annotations.NotNull;
import studio.trc.bukkit.litesignin.config.ConfigurationType;
import studio.trc.bukkit.litesignin.config.ConfigurationUtil;
import studio.trc.bukkit.litesignin.util.MessageUtil;
import studio.trc.bukkit.litesignin.database.util.BackupUtil;
import studio.trc.bukkit.litesignin.database.util.RollBackUtil;
import studio.trc.bukkit.litesignin.util.SignInPluginUtils;

public class SignInCommand implements CommandExecutor, TabCompleter {
    public static final List<String> EMPTY = new ArrayList<>();
    @Getter
    private static final Map<String, SignInSubCommand> subCommands = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (BackupUtil.isBackupRunning()) {
            MessageUtil.sendMessage(sender, ConfigurationUtil.getConfig(ConfigurationType.MESSAGES), "Database-Management.Backup.BackingUp");
            return true;
        }
        if (RollBackUtil.isRollingback()) {
            MessageUtil.sendMessage(sender, ConfigurationUtil.getConfig(ConfigurationType.MESSAGES), "Database-Management.RollBack.RollingBack");
            return true;
        }
        if (args.length == 0) {
            MessageUtil.sendCommandMessage(sender, "Unknown-Command");
        } else /*if (args.length >= 1)*/ {
            callSubCommand(sender, args);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return getNormallyTabComplete(sender, args[0]);
        } else if (args.length > 1) {
            return tabComplete(sender, args);
        } else {
            return new ArrayList<>();
        }
    }
    
    private void callSubCommand(CommandSender sender, String[] args) {
        String subCommand = args[0].toLowerCase();
        if (subCommands.get(subCommand) == null) {
            MessageUtil.sendCommandMessage(sender, "Unknown-Command");
            return;
        }
        SignInSubCommand command = subCommands.get(subCommand);
        if (SignInPluginUtils.hasCommandPermission(sender, command.getCommandType().getCommandPermissionPath(), true)) {
            command.execute(sender, subCommand, args);
        }
    }

    private List<String> getCommands(CommandSender sender) {
        List<String> commands = new ArrayList<>();
        for (SignInSubCommand command : subCommands.values()) {
            if (SignInPluginUtils.hasCommandPermission(sender, command.getCommandType().getCommandPermissionPath(), false)) {
                commands.add(command.getName());
            }
        }
        return commands;
    }
    
    private List<String> getNormallyTabComplete(CommandSender sender, String args) {
        List<String> commands = getCommands(sender);
        if (args != null) {
            List<String> names = new ArrayList<>();
            for (String command : commands) {
                if (command.toLowerCase().startsWith(args.toLowerCase())) {
                    names.add(command);
                }
            }
            return names;
        }
        return commands;
    }
    
    private List<String> tabComplete(CommandSender sender, String[] args) {
        String subCommand = args[0].toLowerCase();
        if (subCommands.get(subCommand) == null) {
            return new ArrayList<>();
        }
        SignInSubCommand command = subCommands.get(subCommand);
        return SignInPluginUtils.hasCommandPermission(sender, command.getCommandType().getCommandPermissionPath(), false)
                ? subCommands.get(subCommand).tabComplete(sender, subCommand, args)
                : new ArrayList<>();
    }
}
