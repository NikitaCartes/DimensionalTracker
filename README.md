## Dimensional Tracker

Fabric mod that color nicknames based on their dimension.  
Compatible with VT AFK Display and [Sessility](https://modrinth.com/mod/sessility) Mod.

Green = Overworld  
Red = The Nether  
Purple = The End  

This mod utilizes vanilla scoreboard teams to color nicknames.

## Customization  
If you don't want to be detected, simply join a different team.  
You can modify your team's color, add a prefix or suffix using the standard Minecraft [`/team` command](https://minecraft.wiki/w/Commands/team).  
To add support for non-vanilla dimensions, create a team named `dimTracker.<dimension_name>`.

## [CurseForge](https://legacy.curseforge.com/minecraft/mc-mods/dimensional-tracker), [Modrinth](https://modrinth.com/mod/dimensionaltracker)

[Discord](https://discord.gg/UY4nhvUzaK)

### Why this mod instead of datapacks?  
Because of how datapacks work they should check dimensions every tick. It's okay for singleplayer, but on servers it can cause lag.  
In testing, I saw that it can take up to 30% of MSPT for 10 players:
![img.png](datapack.webp)

### Example of how it works using Immersive Portals

![image.webp](image.webp)