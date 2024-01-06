package io.github.noobsystems.cloudnetmorstands.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.github.noobsystems.cloudnetmorstands.CloudNetArmorStands;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PluginMessagingListener implements PluginMessageListener {

    private final JavaPlugin plugin;

    @Inject
    public PluginMessagingListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player receiver, @NotNull byte[] message) {
        if (channel.equals("noobcore:main")) {
            ByteArrayDataInput dataInput = ByteStreams.newDataInput(message);
            String subChannel = dataInput.readUTF();

            if (subChannel.equals("RESOURCE_PACK_STATUS") || subChannel.equals("RESOURCE_PACK_ALREADY_LOADED")) {
                Player player = Bukkit.getPlayer(dataInput.readUTF());

                if (player == null) {
                    return;
                }

                CloudNetArmorStands.ARMOR_STANDS.forEach(cloudNetArmorStand -> {
                    if (cloudNetArmorStand.getArmorStand().getPassengers().isEmpty()) {
                        return;
                    }

                    TextDisplay textDisplay = (TextDisplay) cloudNetArmorStand.getArmorStand().getPassengers().get(0);
                    player.hideEntity(this.plugin, textDisplay);
                    player.showEntity(this.plugin, textDisplay);
                });
            }
        }
    }

}