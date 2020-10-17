package zeroBzeroT.betterhubpvp;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class BetterHubPvp extends JavaPlugin implements Listener {
    final static List<Integer> weekdaysWithSpawnWithers = Arrays.asList(Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY);

    final static Calendar calendar = Calendar.getInstance();

    File configFile;
    FileConfiguration config;
    int spawnRadius = 500;

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

        spawnRadius = config.getInt("protected-radius");

        getServer().getPluginManager().registerEvents(this, this);
    }

    public void loadYamls() {
        try {
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (isNotAtSpawn(event.getLocation()))
            return;

        if (event.getEntity().getType() == EntityType.PIG_ZOMBIE
                || (event.getEntity().getType() == EntityType.WITHER && !weekdaysWithSpawnWithers.contains(calendar.get(Calendar.DAY_OF_WEEK)))) {

            getLogger().info(ChatColor.YELLOW + "Stopped " + event.getEntity().getType().name() + " from spawning at spawn.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Location location = event.getBlockClicked().getLocation();

        if (isNotAtSpawn(location) || !location.getWorld().getEnvironment().equals(World.Environment.NETHER))
            return;

        String userName = event.getPlayer().getName();
        getLogger().info(ChatColor.YELLOW + userName + " tried to empty a bucket at the nether hub.");
        event.setCancelled(true);
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

    public boolean isNotAtSpawn(Location loc) {
        return !(Math.abs(loc.getX()) <= spawnRadius) || !(Math.abs(loc.getZ()) <= spawnRadius);
    }
}