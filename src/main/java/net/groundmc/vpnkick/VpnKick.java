package net.groundmc.vpnkick;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public final class VpnKick extends Plugin {

    private Configuration config = null;

    private void saveDefaultConfig() {
        if (!getDataFolder().exists())
            //noinspection ResultOfMethodCallIgnored
            getDataFolder().mkdir();

        final File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (final InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                getLogger().throwing("VpnKick", "saveDefaultConfig", e);
            }
        }
    }

    @Override
    public final void onEnable() {
        saveDefaultConfig();
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
            getProxy().getPluginManager().registerListener(this, new JoinHandler(this));
        } catch (IOException e) {
            getLogger().throwing("VpnKick", "onLoad", e);
        }
    }

    @Nullable
    final Configuration getConfig() {
        return config;
    }
}
