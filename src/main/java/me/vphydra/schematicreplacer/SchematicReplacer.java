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


    /*
    Author: Conor
    Date: 05/08/2020
     */

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(command.getName().equals("replacer")) {

            File file = new File(getServer().getPluginManager().getPlugin("WorldEdit").getDataFolder(), "/schematics/" + args[4] + ".schem");

            //gets world based on name and converts it to a sk89q WorldEdit world.
            World world = Bukkit.getWorld(args[0]);
            com.sk89q.worldedit.world.World weworld = BukkitAdapter.adapt(world);

            //coordinates taking from player arguments
            double x = Double.parseDouble(args[1]);
            double y = Double.parseDouble(args[2]);
            double z = Double.parseDouble(args[3]);

            ClipboardFormat format = ClipboardFormats.findByFile(file);

            try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                Clipboard clipboard = reader.read();



                try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(weworld, -1)) {

                    Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
                            .to(BlockVector3.at(x, y, z)).ignoreAirBlocks(false).build(); //changed ignore to false may cause lag.

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
