package net.groundmc.vpnkick;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public final class VpnKick extends Plugin {

    private static VpnKick instance;

    private Configuration config = null;

    @NotNull
    static VpnKick getInstance() {
        return instance;
    }

    private void saveDefaultConfig() {
        if (!getDataFolder().exists())
            //noinspection ResultOfMethodCallIgnored
            getDataFolder().mkdir();

        final File file = new File(getDataFolder(), "config.yml");


        if (!file.exists()) {
            try (final InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public final void onEnable() {
        instance = this;
        saveDefaultConfig();
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
            ProxyServer.getInstance().getPluginManager().registerListener(this, new JoinHandler());
        } catch (IOException e) {
            getLogger().throwing("VpnKick", "onLoad", e);
        }
    }

    @Nullable
    final Configuration getConfig() {
        return config;
    }
}
