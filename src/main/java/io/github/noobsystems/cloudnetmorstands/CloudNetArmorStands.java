package io.github.noobsystems.cloudnetmorstands;

import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.ext.platforminject.api.PlatformEntrypoint;
import eu.cloudnetservice.ext.platforminject.api.stereotype.Command;
import eu.cloudnetservice.ext.platforminject.api.stereotype.Dependency;
import eu.cloudnetservice.ext.platforminject.api.stereotype.PlatformPlugin;
import io.github.noobsystems.cloudnetmorstands.commands.ArmorStandCommand;
import io.github.noobsystems.cloudnetmorstands.config.ConfigManager;
import io.github.noobsystems.cloudnetmorstands.data.CloudNetArmorStand;
import io.github.noobsystems.cloudnetmorstands.data.CloudNetUtils;
import io.github.noobsystems.cloudnetmorstands.listeners.*;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@Singleton
@PlatformPlugin(
        platform = "bukkit",
        name = "CloudNet-ArmorStands",
        version = "1.0-SNAPSHOT",
        pluginFileNames = "plugin.yml",
        api = "1.20",
        authors = "NoobSystems",
        description = "CloudNet-v4 compatible ArmorStands for selecting a server",
        commands = @Command(name = "armorstand"),
        dependencies =  @Dependency(name = "CloudNet-Bridge")
)
public class CloudNetArmorStands implements PlatformEntrypoint {

    public static final List<CloudNetArmorStand> ARMOR_STANDS = new ArrayList<>();

    @Inject
    public CloudNetArmorStands(JavaPlugin plugin, PluginMessagingListener pluginMessagingListener, ConfigManager configManager, CloudNetUtils cloudNetUtils) {
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "noobcore:main", pluginMessagingListener);
        ARMOR_STANDS.addAll(configManager.loadArmorStands());

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            ARMOR_STANDS.forEach(cloudNetArmorStand -> cloudNetArmorStand.getLocation().getNearbyEntitiesByType(ArmorStand.class, 2).stream().findFirst().ifPresentOrElse(armorStand -> {
                armorStand.getPassengers().forEach(Entity::remove);

                TextDisplay textDisplay = (TextDisplay) armorStand.getWorld().spawnEntity(armorStand.getLocation(), EntityType.TEXT_DISPLAY);
                textDisplay.setBackgroundColor(Color.BLACK.setAlpha(75));
                textDisplay.setBillboard(Display.Billboard.VERTICAL);
                textDisplay.text(cloudNetArmorStand.getComponentTitle(cloudNetUtils));

                armorStand.addPassenger(textDisplay);
                cloudNetArmorStand.setArmorStand(armorStand);
            }, () -> {
                ArmorStand armorStand = (ArmorStand) cloudNetArmorStand.getLocation().getWorld().spawnEntity(cloudNetArmorStand.getLocation(), EntityType.ARMOR_STAND);
                armorStand.setArms(true);
                armorStand.setGravity(false);
                armorStand.setCanMove(false);

                TextDisplay textDisplay = (TextDisplay) cloudNetArmorStand.getLocation().getWorld().spawnEntity(cloudNetArmorStand.getLocation(), EntityType.TEXT_DISPLAY);
                textDisplay.setBackgroundColor(Color.BLACK.setAlpha(75));
                textDisplay.setBillboard(Display.Billboard.VERTICAL);
                textDisplay.text(cloudNetArmorStand.getComponentTitle(cloudNetUtils));

                armorStand.addPassenger(textDisplay);
                cloudNetArmorStand.setArmorStand(armorStand);
            }));
        }, 20 * 3);
    }

    @Inject
    private void registerListeners(
            JavaPlugin plugin,
            PluginManager pluginManager,
            EventManager eventManager,
            InteractAtEntityListener interactAtEntityListener,
            InventoryClickListener inventoryClickListener,
            PlayerMoveListener playerMoveListener,
            CloudServiceListener cloudServiceListener
    ) {
        pluginManager.registerEvents(interactAtEntityListener, plugin);
        pluginManager.registerEvents(inventoryClickListener, plugin);

        eventManager.registerListeners(cloudServiceListener);
    }

    @Inject
    private void registerCommands(JavaPlugin plugin, ArmorStandCommand armorStandCommand) {
        plugin.getCommand("armorstand").setExecutor(armorStandCommand);
        plugin.getCommand("armorstand").setTabCompleter(armorStandCommand);
    }

}
