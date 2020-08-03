package me.vphydra.schematicreplacer;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public final class SchematicReplacer extends JavaPlugin {


    File file = new File(getServer().getPluginManager().getPlugin("WorldEdit").getDataFolder(), "/schematics/test.schem");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(command.getName().equals("repair")) {

            Player player = (Player)sender;
            World world = player.getWorld();

            com.sk89q.worldedit.world.World weworld = BukkitAdapter.adapt(world);

            getLogger().info("got into the command");
            ClipboardFormat format = ClipboardFormats.findByFile(file);

            try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {

                Clipboard clipboard = reader.read();
                getLogger().info("Reading the file");


                try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(weworld, -1)) {

                    getLogger().info("about to place in world");
                    Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
                            .to(BlockVector3.at(100, 100, 100)).ignoreAirBlocks(true).build();

                    try {
                        Operations.complete(operation);
                        editSession.flushSession();

                    } catch (WorldEditException e) {
                        e.printStackTrace();
                    }
                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            }
        }




        return super.onCommand(sender, command, label, args);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
