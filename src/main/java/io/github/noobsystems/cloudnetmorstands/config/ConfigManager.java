package io.github.noobsystems.cloudnetmorstands.config;

import io.github.noobsystems.cloudnetmorstands.data.CloudNetArmorStand;
import io.github.noobsystems.cloudnetmorstands.data.CloudNetUtils;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Singleton
public class ConfigManager {

    private final CloudNetUtils cloudNetUtils;

    private final File file;
    private final YamlConfiguration configuration;

    @Getter
    private Component prefix;
    @Getter
    private int actionBarDistance;
    private String actionBarMessage;

    @Inject
    public ConfigManager(JavaPlugin plugin, CloudNetUtils cloudNetUtils) {
        this.cloudNetUtils = cloudNetUtils;

        this.file = new File(plugin.getDataFolder(), "config.yml");
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
        setDefaultValues();
        loadDefaultValues();
    }

    private void setDefaultValues() {
        if (!this.configuration.contains("prefix")) {
            this.configuration.set("prefix", "§bCloudNet-ArmorStands §8| ");
        }
        if (!this.configuration.contains("actionbar.distance")) {
            this.configuration.set("actionbar.distance", 3);
        }
        if (!this.configuration.contains("actionbar.message")) {
            this.configuration.set("actionbar.message", "§7There are %armorstand_color%%non_ingame_players% §7players waiting to start a round§8...");
        }
        saveConfig();
    }

    private void loadDefaultValues() {
        this.prefix = LegacyComponentSerializer.legacySection().deserialize(this.configuration.getString("prefix"));
        this.actionBarDistance = this.configuration.getInt("actionbar.distance");
        this.actionBarMessage = this.configuration.getString("actionbar.message");
    }

    public Component getActionBarMessage(CloudNetArmorStand cloudNetArmorStand) {
        return LegacyComponentSerializer.legacySection().deserialize(this.actionBarMessage
                .replace("%armorstand_color%", cloudNetArmorStand.getColor())
                .replace("%non_ingame_players%", String.valueOf(this.cloudNetUtils.getNonIngamePlayersFromTask(cloudNetArmorStand.getTask()))));
    }

    public void addArmorStand(CloudNetArmorStand cloudNetArmorStand) {
        this.configuration.set("armorstands." + cloudNetArmorStand.getTask() + ".location", cloudNetArmorStand.getLocation());
        this.configuration.set("armorstands." + cloudNetArmorStand.getTask() + ".color", cloudNetArmorStand.getColor());
        this.configuration.set("armorstands." + cloudNetArmorStand.getTask() + ".material", cloudNetArmorStand.getMaterial().toString().toUpperCase());
        this.configuration.set("armorstands." + cloudNetArmorStand.getTask() + ".inventoryname", LegacyComponentSerializer.legacySection().serialize(cloudNetArmorStand.getInventoryName()));
        this.configuration.set("armorstands." + cloudNetArmorStand.getTask() + ".titles", cloudNetArmorStand.getTitles());
        saveConfig();
    }

    public void deleteArmorStand(CloudNetArmorStand cloudNetArmorStand) {
        this.configuration.set("armorstands." + cloudNetArmorStand.getTask(), null);
        saveConfig();
    }

    public List<CloudNetArmorStand> loadArmorStands() {
        return this.configuration.getConfigurationSection("armorstands").getKeys(false).stream().map(key -> new CloudNetArmorStand(
                key,
                this.configuration.getLocation("armorstands." + key + ".location"),
                this.configuration.getString("armorstands." + key + ".color"),
                Material.valueOf(this.configuration.getString("armorstands." + key + ".material")),
                LegacyComponentSerializer.legacySection().deserialize(this.configuration.getString("armorstands." + key + ".inventoryname")),
                this.configuration.getStringList("armorstands." + key + ".titles")
        )).toList();
    }

    private void saveConfig() {
        try {
            this.configuration.save(this.file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}
