package io.github.noobsystems.cloudnetmorstands.commands;

import eu.cloudnetservice.driver.provider.ServiceTaskProvider;
import eu.cloudnetservice.driver.service.ServiceTask;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import io.github.noobsystems.cloudnetmorstands.CloudNetArmorStands;
import io.github.noobsystems.cloudnetmorstands.config.ConfigManager;
import io.github.noobsystems.cloudnetmorstands.data.CloudNetArmorStand;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Singleton
public class ArmorStandCommand implements CommandExecutor, TabCompleter {

    private final ConfigManager configManager;
    private final ServiceTaskProvider serviceTaskProvider;

    @Inject
    public ArmorStandCommand(ConfigManager configManager, ServiceTaskProvider serviceTaskProvider) {
        this.configManager = configManager;
        this.serviceTaskProvider = serviceTaskProvider;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(this.configManager.getPrefix().append(Component.text("This command is only for players!").color(NamedTextColor.RED)));
            return false;
        }

        if (!player.hasPermission("cloudnetarmorstands.command")) {
            player.sendMessage(this.configManager.getPrefix().append(Component.text("You don't have permission to execute this command!").color(NamedTextColor.RED)));
            return false;
        }

        if (args.length == 2) {
            if (this.serviceTaskProvider.serviceTasks().stream().noneMatch(serviceTask -> serviceTask.name().equalsIgnoreCase(args[1]))) {
                player.sendMessage(this.configManager.getPrefix().append(Component.text("This task doesn't exist!").color(NamedTextColor.RED)));
                return false;
            }
            String task = this.serviceTaskProvider.serviceTasks().stream().filter(serviceTask -> serviceTask.name().equalsIgnoreCase(args[1])).findFirst().get().name();

            if (args[0].equalsIgnoreCase("create")) {
                if (CloudNetArmorStands.ARMOR_STANDS.stream().anyMatch(cloudNetArmorStand -> cloudNetArmorStand.getTask().equalsIgnoreCase(task))) {
                    player.sendMessage(this.configManager.getPrefix().append(Component.text("There is already an ArmorStand for this task!").color(NamedTextColor.RED)));
                    return false;
                }

                ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
                armorStand.setArms(true);
                armorStand.setGravity(false);
                armorStand.setCanMove(false);

                TextDisplay textDisplay = (TextDisplay) player.getWorld().spawnEntity(player.getLocation(), EntityType.TEXT_DISPLAY);
                textDisplay.setBackgroundColor(Color.BLACK.setAlpha(75));
                textDisplay.setBillboard(Display.Billboard.VERTICAL);
                textDisplay.text(Component.text(task));

                armorStand.addPassenger(textDisplay);

                CloudNetArmorStand cloudNetArmorStand = new CloudNetArmorStand(task, armorStand.getLocation(), "§7", Material.EGG, Component.text(task), List.of(task));
                cloudNetArmorStand.setArmorStand(armorStand);

                this.configManager.addArmorStand(cloudNetArmorStand);
                CloudNetArmorStands.ARMOR_STANDS.add(cloudNetArmorStand);

                player.sendMessage(this.configManager.getPrefix()
                        .append(Component.text("Der ArmorStand wurde erfolgreich gesetzt").color(NamedTextColor.GRAY))
                        .append(Component.text("!").color(NamedTextColor.DARK_GRAY)));
                return true;
            }

            if (args[0].equalsIgnoreCase("delete")) {
                if (CloudNetArmorStands.ARMOR_STANDS.stream().noneMatch(cloudNetArmorStand -> cloudNetArmorStand.getTask().equalsIgnoreCase(task))) {
                    player.sendMessage(this.configManager.getPrefix().append(Component.text("There is no ArmorStand for this task!").color(NamedTextColor.RED)));
                    return false;
                }

                CloudNetArmorStand cloudNetArmorStand = CloudNetArmorStands.ARMOR_STANDS.stream().filter(armorStand -> armorStand.getTask().equalsIgnoreCase(task)).findFirst().get();
                cloudNetArmorStand.getLocation().getNearbyEntitiesByType(ArmorStand.class, 2).stream().findFirst().ifPresent(armorStand -> {
                    armorStand.getPassengers().forEach(Entity::remove);
                    armorStand.remove();
                });
                CloudNetArmorStands.ARMOR_STANDS.remove(cloudNetArmorStand);

                this.configManager.deleteArmorStand(cloudNetArmorStand);

                player.sendMessage(this.configManager.getPrefix()
                        .append(Component.text("The ArmorStand has been successfully deleted").color(NamedTextColor.GRAY))
                        .append(Component.text("!").color(NamedTextColor.DARK_GRAY)));
                return true;
            }
        }

        sendUsage(player);
        return false;
    }

    private void sendUsage(Player player) {
        player.sendMessage(Component.empty());
        player.sendMessage(this.configManager.getPrefix().append(Component.text("/armorstand create <Task>").color(NamedTextColor.GRAY)));
        player.sendMessage(this.configManager.getPrefix().append(Component.text("/armorstand delete <Task>").color(NamedTextColor.GRAY)));
        player.sendMessage(Component.empty());
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("cloudnetarmorstands.command")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return Stream.of("create", "delete")
                    .filter(name -> name.regionMatches(true, 0, args[0], 0, args[0].length()))
                    .toList();
        } else if (args.length == 2) {
            return this.serviceTaskProvider.serviceTasks().stream()
                    .map(ServiceTask::name)
                    .filter(name -> name.regionMatches(true, 0, args[1], 0, args[1].length()))
                    .toList();
        }

        return Collections.emptyList();
    }
}
