package studio.trc.bukkit.litesignin.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface SignInSubCommand
{
    /**
     * Execute sub command.
     * @param sender Command sender.
     * @param subCommand The sub command.
     * @param args arguments.
     */
    void execute(CommandSender sender, String subCommand, String... args);
    
    /**
     * Sub command's name.
     */
    String getName();
    
    /**
     * Tab complete
     * @param sender Command sender.
     * @param subCommand The sub command.
     * @param args arguments.
     */
    List<String> tabComplete(CommandSender sender, String subCommand, String... args);
    
    /**
     * Sub command type.
     */
    SignInSubCommandType getCommandType();
    
    /**
     * @param args arguments.
     */
    default List<String> tabGetPlayersName(String[] args, int length) {
        if (args.length == length) {
            List<String> names = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                String name = player.getName();
                if (name.toLowerCase().startsWith(args[length - 1].toLowerCase())) {
                    names.add(name);
                }
            }
            return names;
        }
        return new ArrayList<>();
    }

    default List<String> getTabElements(String[] args, int length, Collection<String> elements) {
        if (args.length == length) {
            List<String> names = new ArrayList<>();
            for (String element : elements) {
                if (element.toLowerCase().startsWith(args[length - 1].toLowerCase())) {
                    names.add(element);
                }
            }
            return names;
        }
        return new ArrayList<>();
    }
}
