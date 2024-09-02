# ServerToClientCommands
Allows admin clients to run server commands.

## What is a server and client command?
Mindustry splits commands into two different types: Server commands and client commands.<br>
A client command is one that is expected to be executed by a client (a player) while a server is expected to be executed from the server console. <br>
Client commands have no good reason to be ran on the server, as they are something like `/vote`, but server commands could be very useful for clients, such as `/pause` or `/ban`.

## What does this plugin do?
This plugin automatically turns all server commands into client commands (the console can still execute the server commands)<br>
It only allows admins to run server commands, but that means that you can now pause the server or ban people right from your game!
