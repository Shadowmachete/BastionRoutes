# BastionRoutes

A mod for Fabric on Minecraft 1.16.1 to help speedrunners learn bastion routes. The mod can record waypoints of routes to indicate where to go.

## Features
### Practicing a route
1. Manually set the current route using `/setcurrentroute [name]` ~~Choose any route from the menu for the bastion you are practicing~~ (menu not developed yet)

**`/viewroutes [bastion type]` can be used to see all available routes for the bastion type, the bastion type is optional and will default to the current bastion.**

2. Go to the bastion (e.g. in Llama's Bastion Practice map)
3. Follow the labelled waypoints to practice the route
### Recording a route
1. Run the `/record [route name]` command. The route will be recorded for the current bastion type you are in.

**WARNING: Recording routes with the same name and bastion type will override the old route.**

3. Go to the locations to place the waypoints in order and run `/add [name]` at each point. The name is optional and a default numbering will be used otherwise.
3. Run `/save` to stop the recording and save the route
### Configuring routes (WIP)
Colours of all waypoints, next waypoint and completed waypoints can be configured directly in the menu.
Coordinate offsets of all waypoints can be manually configured in the menu or changed by rerecording the route.

## Installing
### Releases
Releases can be found in the releases section on this GitHub repository. All you need to do is move the mod jar into your `mods` folder and you should be good to go! You should be using fabric on 1.16.1.
### Building from sources
If you want to help with development or just want access to bleeding edge features, you can clone the repository and run the `build` task in gradle.

## Contributing
Contributors to the project are welcome!
### Feedback
You can give feedback/feature suggestions by opening an issue, or through discord.
UI suggestions are greatly appreciated!
### Code contributions
If you have a new feature or a bugfix, feel free to open a pull request. Make sure to include a detailed description of exactly what you are changing and if you are fixing an issue, what was causing the issue if it was unknown.

## Future plans
- Adding ghost blocks for placing and breaking of blocks in the bastion route
- Improvements on the UI/config screen
