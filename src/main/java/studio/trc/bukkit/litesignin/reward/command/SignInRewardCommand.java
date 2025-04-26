package studio.trc.bukkit.litesignin.reward.command;

import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import studio.trc.bukkit.litesignin.Main;
import studio.trc.bukkit.litesignin.util.MessageUtil;
import studio.trc.bukkit.litesignin.util.PluginControl;

public class SignInRewardCommand
{
    private final SignInRewardCommandType type;
    private final String command;

    public SignInRewardCommand(SignInRewardCommandType type, String command) {
        this.type = type;
        this.command = command;
    }

    public SignInRewardCommandType getCommandType() {
        return type;
    }

    public String getCommand() {
        return command;
    }

    public void runWithThePlayer(Player player) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{player}", player.getName());
        String command_replaced = MessageUtil.replacePlaceholders(player, command, placeholders);
        PluginControl.runBukkitTask(() -> {
            switch (type) {
                case PLAYER: {
                    player.performCommand(MessageUtil.toColor(MessageUtil.toPlaceholderAPIResult(command_replaced, player)));
                    break;
                }
                case OP: {
                    if (player.isOp()) {
                        player.performCommand(MessageUtil.toColor(MessageUtil.toPlaceholderAPIResult(command_replaced, player)));
                    } else {
                        player.setOp(true);
                        try {
                            player.performCommand(MessageUtil.toColor(MessageUtil.toPlaceholderAPIResult(command_replaced, player)));
                        } catch (Throwable error) {
                            Main.getInstance().getLogger().log(Level.WARNING, "管理员命令执行错误 (" + player.getName() + "): " + command_replaced, error);
                        }
                        player.setOp(false);
                    }
                    break;
                }
                case SERVER: {
                    Main.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), MessageUtil.toColor(MessageUtil.toPlaceholderAPIResult(command_replaced, player)));
                    break;
                }
            }
        }, 0);
    }
}
