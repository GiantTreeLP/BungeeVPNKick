package net.groundmc.vpnkick;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public final class JoinHandler implements Listener {

    private final VpnKick vpnKick;

    @NotNull
    private final LoadingCache<String, Boolean> blockCache = CacheBuilder.newBuilder()
            .maximumSize(1024).build(CacheLoader.asyncReloading(new BlockCacheLoader(), Executors.newCachedThreadPool()));

    JoinHandler(VpnKick vpnKick) {
        this.vpnKick = vpnKick;
    }

    @EventHandler
    public final void kickVPN(@NotNull PreLoginEvent event) {
        if (blockCache.getUnchecked(event.getConnection().getAddress().getHostString())) {
            event.setCancelled(true);
            event.setCancelReason(new TextComponent("VPNs and proxies are not allowed!"));
        }
    }

    @EventHandler(priority = 127 /* Highest possible */)
    public final void hidePing(@NotNull ProxyPingEvent event) {
        if (blockCache.getUnchecked(event.getConnection().getAddress().getHostString())) {
            event.setResponse(new ServerPing(
                    new ServerPing.Protocol("No VPN here!", event.getResponse().getVersion().getProtocol()),
                    new ServerPing.Players(0, 0, new ServerPing.PlayerInfo[]{new ServerPing.PlayerInfo("No VPN allowed!", "")}),
                    new TextComponent("No VPN allowed!"),
                    ProxyServer.getInstance().getConfig().getFaviconObject()
            ));
        }
    }

    private final class BlockCacheLoader extends CacheLoader<String, Boolean> {

        @NotNull
        private static final String ENDPOINT = "http://v2.api.iphub.info/ip/";
        private final JsonParser parser = new JsonParser();

        private boolean isBlocked(@NotNull String address) {
            try {
                final URLConnection connection = new URL(ENDPOINT + address).openConnection();
                if (vpnKick.getConfig() != null) {
                    connection.addRequestProperty("X-Key", vpnKick.getConfig().getString("apikey"));
                } else {
                    return false;
                }

                final JsonElement element = parser.parse(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                return element.getAsJsonObject().get("block").getAsInt() == 1;
            } catch (IOException | NullPointerException e) {
                vpnKick.getLogger().throwing("JoinHandler", "isBlocked", e);
                return false;
            }
        }

        @Override
        @NotNull
        public final Boolean load(@NotNull String key) {
            return isBlocked(key);
        }
    }
}
