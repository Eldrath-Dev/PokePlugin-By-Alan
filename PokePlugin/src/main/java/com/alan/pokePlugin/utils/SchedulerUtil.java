package com.alan.pokePlugin.utils;

import com.alan.pokePlugin.PokePlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class SchedulerUtil {

    /**
     * Run a task on the entity's region (Folia) or main thread (Bukkit/Spigot/Paper)
     */
    public static void runTask(PokePlugin plugin, Entity entity, Runnable task) {
        if (plugin.isFolia()) {
            // Use Folia's entity scheduler
            entity.getScheduler().run(plugin, scheduledTask -> task.run(), null);
        } else {
            // Use Bukkit scheduler for Paper/Spigot/Bukkit/Purpur
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    /**
     * Run a delayed task on the entity's region (Folia) or main thread (Bukkit/Spigot/Paper)
     */
    public static void runTaskLater(PokePlugin plugin, Entity entity, Runnable task, long delayTicks) {
        if (plugin.isFolia()) {
            // Use Folia's entity scheduler with delay
            entity.getScheduler().runDelayed(plugin, scheduledTask -> task.run(), null, delayTicks);
        } else {
            // Use Bukkit scheduler with delay
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }

    /**
     * Run an asynchronous task (works on all platforms)
     */
    public static void runTaskAsync(PokePlugin plugin, Runnable task) {
        if (plugin.isFolia()) {
            // Use Folia's global region scheduler for async operations
            Bukkit.getGlobalRegionScheduler().run(plugin, scheduledTask -> task.run());
        } else {
            // Use Bukkit's async scheduler
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }

    /**
     * Run a repeating task on the entity's region (Folia) or main thread (Bukkit/Spigot/Paper)
     */
    public static void runTaskTimer(PokePlugin plugin, Entity entity, Runnable task, long delay, long period) {
        if (plugin.isFolia()) {
            // Use Folia's entity scheduler with repeating task
            entity.getScheduler().runAtFixedRate(plugin, scheduledTask -> task.run(), null, delay, period);
        } else {
            // Use Bukkit scheduler with repeating task
            Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period);
        }
    }

    /**
     * Run a task on the main thread/global region (for non-entity specific tasks)
     */
    public static void runTaskGlobal(PokePlugin plugin, Runnable task) {
        if (plugin.isFolia()) {
            // Use Folia's global region scheduler
            Bukkit.getGlobalRegionScheduler().run(plugin, scheduledTask -> task.run());
        } else {
            // Use Bukkit scheduler
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    /**
     * Run a delayed task on the main thread/global region
     */
    public static void runTaskLaterGlobal(PokePlugin plugin, Runnable task, long delayTicks) {
        if (plugin.isFolia()) {
            // Use Folia's global region scheduler with delay
            Bukkit.getGlobalRegionScheduler().runDelayed(plugin, scheduledTask -> task.run(), delayTicks);
        } else {
            // Use Bukkit scheduler with delay
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }
}