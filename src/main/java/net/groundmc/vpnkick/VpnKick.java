package net.groundmc.vpnkick;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public final class VpnKick extends Plugin {


    private static VpnKick instance;

    private Configuration config = null;

    static VpnKick getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        saveDefaultConfig();
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            getLogger().throwing("VpnKick", "onLoad", e);
        }

    }

    private void saveDefaultConfig() {
        if (!getDataFolder().exists())
            //noinspection ResultOfMethodCallIgnored
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");


        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onEnable() {
        ProxyServer.getInstance().getPluginManager().registerListener(this, new JoinHandler());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    Configuration getConfig() {
        return config;
    }
}
