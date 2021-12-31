package me.vphydra.schematicreplacer;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import net.royawesome.jlibnoise.module.combiner.Max;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.api.IAsyncWorldEdit;
import org.primesoft.asyncworldedit.api.IWorld;
import org.primesoft.asyncworldedit.api.blockPlacer.IBlockPlacer;
import org.primesoft.asyncworldedit.api.blockPlacer.IBlockPlacerEntry;
import org.primesoft.asyncworldedit.api.blockPlacer.IBlockPlacerListener;
import org.primesoft.asyncworldedit.api.blockPlacer.IBlockPlacerPlayer;
import org.primesoft.asyncworldedit.api.blockPlacer.entries.IJobEntry;
import org.primesoft.asyncworldedit.api.configuration.IPermissionGroup;
import org.primesoft.asyncworldedit.api.configuration.IWorldEditConfig;
import org.primesoft.asyncworldedit.api.playerManager.IPlayerEntry;
import org.primesoft.asyncworldedit.api.playerManager.IPlayerManager;
import org.primesoft.asyncworldedit.api.utils.IAsyncCommand;
import org.primesoft.asyncworldedit.api.utils.IFuncParamEx;
import org.primesoft.asyncworldedit.api.worldedit.IAsyncEditSessionFactory;
import org.primesoft.asyncworldedit.api.worldedit.IAweEditSession;
import org.primesoft.asyncworldedit.api.worldedit.ICancelabeEditSession;
import org.primesoft.asyncworldedit.api.worldedit.IThreadSafeEditSession;

import java.io.File;
import java.io.*;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public class SchematicReplacer extends JavaPlugin{


    /*
    Author: Conor
    Date: 05/08/2020
     */

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(command.getName().equals("replacer")) {
            IAsyncWorldEdit aweAPI = (IAsyncWorldEdit)Bukkit.getServer().getPluginManager().getPlugin("AsyncWorldEdit");
            File file = new File(getServer().getPluginManager().getPlugin("WorldEdit").getDataFolder(), "/schematics/" + args[4] + ".schem");

            //gets world based on name and converts it to a sk89q WorldEdit world.
            World world = Bukkit.getWorld(args[0]);
            com.sk89q.worldedit.world.World weworld = BukkitAdapter.adapt(world);

            IBlockPlacer blockPlacer = aweAPI.getBlockPlacer();
            IPlayerManager playerManager = aweAPI.getPlayerManager();
            BlockBag bb = null;
            IAsyncEditSessionFactory esFactory = (IAsyncEditSessionFactory)WorldEdit.getInstance().getEditSessionFactory();



            IPlayerEntry awePlayer = playerManager.getConsolePlayer();

            //coordinates taking from player arguments
            double x = Double.parseDouble(args[1]);
            double y = Double.parseDouble(args[2]);
            double z = Double.parseDouble(args[3]);

            //attempt to load shemactic
            ClipboardFormat format = ClipboardFormats.findByFile(file);

            try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                Clipboard clipboard = reader.read();


                    IThreadSafeEditSession es = esFactory.getThreadSafeEditSession(weworld, -1, bb, awePlayer);

                    blockPlacer.performAsAsyncJob(es, awePlayer, "Schematic Replacer Job", new IFuncParamEx<Integer, ICancelabeEditSession, MaxChangedBlocksException>() {
                        @Override
                        public Integer execute(ICancelabeEditSession iCancelabeEditSession) throws MaxChangedBlocksException {
                            try {
                                Operation operation = new ClipboardHolder(clipboard).createPaste(iCancelabeEditSession)
                                        .to(BlockVector3.at(x, y, z)).ignoreAirBlocks(false).build(); //changed ignore to false may cause lag.

                                try {
                                    Operations.complete(operation);
                                    iCancelabeEditSession.flushSession();

                                } catch (WorldEditException e) {
                                    e.printStackTrace();
                                }
                            }catch (Exception e)
                            {


                            }
                            return 1;
                        }
                    });

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
