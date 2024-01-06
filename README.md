# CloudNet-ArmorStands

CloudNet-ArmorStands is a CloudNet-v4 compatible Paper-plugin that provides ArmorStands to select a server from a CloudNet task.

## Description
The plugin spawns ArmorStands or uses already-existing ones to open an inventory when right-clicking them.
The inventory contains a list of all servers of the task that is associated with the ArmorStand.

To create a new ArmorStand, execute the command `/armorstand create <task name>`. The plugin will then spawn an ArmorStand
at your location. If you want to use an already-existing ArmorStand or a custom one, just delete the ArmorStand summoned
by the plugin and place your custom one at the same location.

To delete an ArmorStand, execute the command `/armorstand delete <task name>`. The ArmorStand will then be removed.

## Config

A typical config file from this plugin looks like this:
```yml
prefix: '§bCloudNet-ArmorStands §8| ' # The prefix which will be shown when executing the armorstand command
actionbar:
  # If you look at an ArmorStand created by this plugin, you will get an action bar that shows the amount of
  # players on non-ingame services of the task the ArmorStand belongs to
  distance: 3 # Set this to -1, if you want to disable the action bar feature
  message: §7There are %armorstand_color%%non_ingame_players% §7players waiting to start a round§8...
armorstands:
  BedWars: # The task name of the ArmorStand
    location:
      ==: org.bukkit.Location
      world: world
      x: 9.680095847992382
      y: 59.0
      z: 20.49772773083464
      pitch: 0.74989176
      yaw: -90.30063
    color: §b # The color of this ArmorStand, this can be used in the action bar message with the placeholder %armorstand_color%
    material: SPONGE # The material of the items in the inventory that will open when right-clicking the ArmorStand
    inventoryname: §8⮩ §bBedWars # The inventory title
    titles: # The title which will be displayed over the ArmorStand, you can use multiple lines
      - §bBedWars
      - §7
      - §b%online_players% §7Players # %online_players% will be replaced with the amount of players on services of the task
```
Make sure to copy the config file created by the plugin to the appropriate template folder.

## Permissions

- `/armorstand` - Permission: `cloudnetarmorstands.command`

## Compatibility
This plugin is only compatible with Paper and CloudNet version 4.

## Contributing
First of all, thank you for considering participating in this project. Thus, we are very open to any form of constructive
feedback, we appreciate pull requests to update and enhance this system. We don't have a very strict policy towards PRs.
Please keep everything clear and consider opening one PR per feature if you plan to make several changes.