package me.andreasmelone.stcc;

import arc.*;
import arc.util.*;
import arc.struct.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.ui.dialogs.*;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.*;
import java.util.concurrent.atomic.*;

public class ServerToClientCommandsPlugin extends Plugin {
    Seq<CommandHandler.Command> serverCommands = null;
    List<Player> players = new ArrayList<>();
    Timer timer = new Timer();
    TextCollector cout = new TextCollector(System.out);
    String serverPrefix = "server_";

    @Override
    public void init() {
        System.setOut(new PrintStream(cout));
        cout.onPrint(s -> {
            players.forEach(p -> {
                if(!p.admin()) return;

                p.sendMessage(clearPrefix(clear(s)));
            });
        });
        Events.on(PlayerJoin.class, (e) -> {
            players.add(e.player);
        });
        Events.on(PlayerLeave.class, (e) -> {
            players.remove(e.player);
        });
    }

    @Override
    public void registerServerCommands(CommandHandler handler){
        serverCommands = handler.getCommandList();
    }

    @Override
    public void registerClientCommands(CommandHandler handler){
        AtomicBoolean init = new AtomicBoolean(false);
        timer.schedule(() -> {
            if(serverCommands == null || init.get()) return;
            init.set(true);
            registerCommands(serverCommands, handler);
        }, 0, 1);
    }

    public void registerCommands(Seq<CommandHandler.Command> serverCommands, CommandHandler clientHandler) {
        Seq<CommandHandler.Command> clientCommands = clientHandler.getCommandList();

        serverCommands.forEach(c -> {
            if(!hasCommand(clientCommands, c)) {
                clientHandler.<Player>register(c.text, c.paramText, c.description, (args, p) -> { 
                    if(!p.admin()) {
                        p.sendMessage("This command can only be run by an admin!");
                        return;
                    }
                    getRunner(c).accept(args, null);
                });
            }
            clientHandler.<Player>register(serverPrefix + c.text, c.paramText, c.description, (args, p) -> {
                    if(!p.admin()) {
                        p.sendMessage("This command can only be run by an admin!");
                        return; 
                    }
                    getRunner(c).accept(args, null);
                });
        });
    }

    public static boolean hasCommand(Seq<CommandHandler.Command> handler, CommandHandler.Command cmd) {
        for(CommandHandler.Command c : handler) {
            if(c.text.equals(cmd.text)) return true;
        }
        return false;
    }

    public static CommandHandler.CommandRunner<?> getRunner(CommandHandler.Command cmd) {
        try {
            Field field = CommandHandler.Command.class.getDeclaredField("runner");
            field.setAccessible(true);
            return (CommandHandler.CommandRunner<?>) field.get(cmd);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String clear(String input) {
        String ansiRegex = "\u001B\\[[;\\d]*m";
        return input.replaceAll(ansiRegex, "");
    }

    public static String clearPrefix(String input) {
        String prefixRegex = "^\\[\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}] \\[\\w]";
        return input.replaceFirst(prefixRegex, "").trim();
    }
}
