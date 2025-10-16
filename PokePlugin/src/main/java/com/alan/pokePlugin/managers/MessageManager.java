package com.alan.pokePlugin.managers;

import com.alan.pokePlugin.PokePlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class MessageManager {

    private final PokePlugin plugin;
    private File messagesFile;
    private FileConfiguration messages;

    public MessageManager(PokePlugin plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    public void loadMessages() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            try {
                messagesFile.getParentFile().mkdirs();
                InputStream in = plugin.getResource("messages.yml");
                if (in != null) {
                    Files.copy(in, messagesFile.toPath());
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create messages.yml: " + e.getMessage());
            }
        }

        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public String getMessage(String path) {
        String message = messages.getString(path, "&cMessage not found: " + path);
        return message.replace("&", "§");
    }

    public String getMessage(String path, String placeholder, String value) {
        return getMessage(path).replace(placeholder, value);
    }

    public List<String> getMessageList(String path) {
        return messages.getStringList(path).stream()
                .map(s -> s.replace("&", "§"))
                .collect(Collectors.toList());
    }

    public String formatMessage(String path, Object... replacements) {
        String message = getMessage(path);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace("{" + replacements[i] + "}", String.valueOf(replacements[i + 1]));
            }
        }
        return message;
    }

    public void reloadMessages() {
        loadMessages();
    }
}