package io.github.noobsystems.cloudnetmorstands.data;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class CloudNetArmorStand {

    private final String task;
    private final Location location;
    private final String color;
    private final Material material;
    private final Component inventoryName;
    private final List<String> titles;
    private final Map<Player, TextDisplay> textDisplays = new HashMap<>();

    @Setter
    private ArmorStand armorStand;

    public CloudNetArmorStand(String task, Location location, String color, Material material, Component inventoryName, List<String> titles) {
        this.task = task;
        this.location = location;
        this.color = color;
        this.material = material;
        this.inventoryName = inventoryName;
        this.titles = titles;
    }

    public Component getComponentTitle(CloudNetUtils cloudNetUtils) {
        return Component.join(JoinConfiguration.newlines(), this.titles.stream()
                .map(title -> title.replace("%online_players%", String.valueOf(cloudNetUtils.getOnlinePlayersFromTask(this.task))))
                .map(title -> LegacyComponentSerializer.legacySection().deserialize(title))
                .toList());
    }

}
