package com.asintoto.replenish.commands;

import com.asintoto.replenish.Replenish;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
@RequiredArgsConstructor
public class MainCommand implements CommandExecutor, TabCompleter {
    private final Replenish plugin;

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!checkPerms(sender, "replenish.command.use")) return true;

        if(args.length < 1) {
            return missingArgs(sender);
        }

        String param = args[0];

        // Reload
        if(param.equalsIgnoreCase("reload")) {
            if(!checkPerms(sender, "replenish.command.reload")) return true;

            plugin.reload();
            String msg = plugin.getMessages().getString("admin.reload");
            sender.sendRichMessage(msg);
            return true;
        }

        // Set
        if(param.equalsIgnoreCase("set")) {
            if(!checkPerms(sender, "replenish.command.set")) return true;

            if(args.length < 3) {
                return missingArgs(sender);
            }

            String playerName = args[1];
            String enable = args[2];

            Player target = plugin.getServer().getPlayer(playerName);
            if(!checkPlayer(target)) return true;

            if(enable.equalsIgnoreCase("true")) {
                plugin.getStatusManager().enable(target);
                String msg = plugin.getMessages().getString("admin.set-on")
                        .replace("%player%", target.getName());
                sender.sendRichMessage(msg);
                return true;
            } else if(enable.equalsIgnoreCase("false")) {
                plugin.getStatusManager().disable(target);
                String msg = plugin.getMessages().getString("admin.set-off")
                        .replace("%player%", target.getName());
                sender.sendRichMessage(msg);
                return true;
            } else {
                return invalidArg(sender);
            }
        }

        // Toggle
        if(param.equalsIgnoreCase("toggle")) {
            if(!checkPerms(sender, "replenish.command.toggle")) return true;

            if(sender instanceof Player player) {
                boolean enable = plugin.getStatusManager().isEnabled(player);

                String response;

                if(enable) {
                    plugin.getStatusManager().disable(player);
                    response = plugin.getMessages().getString("player.toggle-off");
                } else {
                    plugin.getStatusManager().enable(player);
                    response = plugin.getMessages().getString("player.toggle-on");
                }

                player.sendRichMessage(response);
                return true;

            } else {
                String msg = plugin.getMessages().getString("error.not-a-player");
                sender.sendRichMessage(msg);
                return true;
            }
        }

        return missingArgs(sender);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 1) {
            List<String> completion = new ArrayList<>();
            if(sender.hasPermission("replenish.command.reload")) completion.add("reload");
            if(sender.hasPermission("replenish.command.set")) completion.add("set");
            if(sender.hasPermission("replenish.command.toggle")) completion.add("toggle");

            return completion.stream().filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).toList();
        }

        if(args[0].equalsIgnoreCase("set")) {
            if(args.length == 2) {
                return plugin.getServer().getOnlinePlayers().stream().map(Player::getName)
                        .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase())).toList();
            }

            if(args.length == 3) {
                return List.of("true", "false").stream().filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase())).toList();
            }
        }

        return List.of();
    }

    private boolean checkPerms(CommandSender sender, String permission) {
        if(!sender.hasPermission(permission)) {
            String msg = plugin.getMessages().getString("error.no-permission");
            sender.sendRichMessage(msg);
            return false;
        }
        return true;
    }

    private boolean missingArgs(CommandSender sender) {
        String msg = plugin.getMessages().getString("error.missing-args");
        sender.sendRichMessage(msg);
        return true;
    }

    private boolean checkPlayer(Player player) {
        if(player == null) {
            String msg = plugin.getMessages().getString("error.player-not-found");
            return false;
        }
        return true;
    }

    private boolean invalidArg(CommandSender sender) {
        String msg = plugin.getMessages().getString("error.invalid-arg");
        sender.sendRichMessage(msg);
        return true;
    }
}
