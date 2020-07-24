package zeroBzeroT.betterhubpvp;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class BetterHubPvp extends JavaPlugin implements Listener {
    final static List<Material> IllegalBlocksNetherHub = Arrays.asList(Material.LAVA, Material.LAVA_BUCKET, Material.STATIONARY_LAVA);

    File configFile;
    FileConfiguration config;
    int radius = 500;

    @Override
    public void onEnable() {
        configFile = new File(getDataFolder(), "config.yml");

        try {
            firstRun();
        } catch (Exception e) {
            e.printStackTrace();
        }

        config = new YamlConfiguration();
        loadYamls();

        radius = config.getInt("protected-radius");

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void loadYamls() {
        try {
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent e) {
        if (e.getEntity().getType() == EntityType.PIG_ZOMBIE || e.getEntity().getType() == EntityType.WITHER) {
            Location loc = e.getLocation();

            if (!witherSpawningAllowed(loc)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        Block block = event.getBlock();

        if (!block.getChunk().getWorld().getEnvironment().equals(World.Environment.NETHER))
            return;

        if (IllegalBlocksNetherHub.contains(block.getType())) {
            event.setCancelled(true);
        }
    }

    private void firstRun() {
        if (!configFile.exists()) {
            if (configFile.getParentFile().mkdirs()) {
                copy(getResource("config.yml"), configFile);
            }
        }
    }

    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean witherSpawningAllowed(Location loc) {
        return !(Math.abs(loc.getX()) < radius) || !(Math.abs(loc.getZ()) < radius);
    }
}