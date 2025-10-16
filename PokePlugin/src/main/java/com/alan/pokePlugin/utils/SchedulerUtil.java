package com.alan.pokePlugin.utils;

import com.alan.pokePlugin.PokePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.concurrent.CompletableFuture;

public class SchedulerUtil {

    /**
     * Run a task on the entity's region (Folia) or main thread (Bukkit/Spigot/Paper/Purpur)
     */
    public static void runTask(PokePlugin plugin, Entity entity, Runnable task) {
        if (plugin.isFolia()) {
            // Folia: Use regionized entity scheduler
            entity.getScheduler().run(plugin, scheduledTask -> task.run(), null);
        } else {
            // Bukkit/Spigot/Paper/Purpur: Use traditional main thread scheduler
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    /**
     * Run a delayed task on the entity's region (Folia) or main thread (Bukkit/Spigot/Paper/Purpur)
     */
    public static void runTaskLater(PokePlugin plugin, Entity entity, Runnable task, long delayTicks) {
        if (plugin.isFolia()) {
            // Folia: Use regionized entity scheduler with delay
            entity.getScheduler().runDelayed(plugin, scheduledTask -> task.run(), null, delayTicks);
        } else {
            // Bukkit/Spigot/Paper/Purpur: Use traditional delayed scheduler
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }

    /**
     * Run an asynchronous task (works on all platforms)
     */
    public static void runTaskAsync(PokePlugin plugin, Runnable task) {
        if (plugin.isFolia()) {
            // Folia: Use global region scheduler for async operations
            Bukkit.getAsyncScheduler().runNow(plugin, scheduledTask -> task.run());
        } else {
            // Bukkit/Spigot/Paper/Purpur: Use traditional async scheduler
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }

    /**
     * Run a task on the main thread/global region (for non-entity specific tasks)
     */
    public static void runTaskGlobal(PokePlugin plugin, Runnable task) {
        if (plugin.isFolia()) {
            // Folia: Use global region scheduler
            Bukkit.getGlobalRegionScheduler().run(plugin, scheduledTask -> task.run());
        } else {
            // Bukkit/Spigot/Paper/Purpur: Use main thread scheduler
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    /**
     * Run a delayed task on the main thread/global region
     */
    public static void runTaskLaterGlobal(PokePlugin plugin, Runnable task, long delayTicks) {
        if (plugin.isFolia()) {
            // Folia: Use global region scheduler with delay
            Bukkit.getGlobalRegionScheduler().runDelayed(plugin, scheduledTask -> task.run(), delayTicks);
        } else {
            // Bukkit/Spigot/Paper/Purpur: Use delayed main thread scheduler
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }

    /**
     * Run a repeating task on the entity's region (Folia) or main thread (Bukkit/Spigot/Paper/Purpur)
     */
    public static void runTaskTimer(PokePlugin plugin, Entity entity, Runnable task, long delay, long period) {
        if (plugin.isFolia()) {
            // Folia: Use regionized entity scheduler with repeating task
            entity.getScheduler().runAtFixedRate(plugin, scheduledTask -> task.run(), null, delay, period);
        } else {
            // Bukkit/Spigot/Paper/Purpur: Use traditional repeating scheduler
            Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period);
        }
    }

    /**
     * Run a task at a specific location (Folia-specific, falls back to global for others)
     */
    public static void runTaskAtLocation(PokePlugin plugin, Location location, Runnable task) {
        if (plugin.isFolia()) {
            // Folia: Use region scheduler for specific location
            Bukkit.getRegionScheduler().run(plugin, location, scheduledTask -> task.run());
        } else {
            // Bukkit/Spigot/Paper/Purpur: Use main thread scheduler (no location-specific scheduling)
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    /**
     * Execute a database operation asynchronously and return CompletableFuture
     */
    public static <T> CompletableFuture<T> runDatabaseTask(PokePlugin plugin, java.util.function.Supplier<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();

        if (plugin.isFolia()) {
            // Folia: Use async scheduler for database operations
            Bukkit.getAsyncScheduler().runNow(plugin, scheduledTask -> {
                try {
                    T result = task.get();
                    future.complete(result);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            });
        } else {
            // Bukkit/Spigot/Paper/Purpur: Use traditional async scheduler
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    T result = task.get();
                    future.complete(result);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            });
        }

        return future;
    }

    /**
     * Execute a void database operation asynchronously
     */
    public static CompletableFuture<Void> runDatabaseTaskVoid(PokePlugin plugin, Runnable task) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        if (plugin.isFolia()) {
            // Folia: Use async scheduler for database operations
            Bukkit.getAsyncScheduler().runNow(plugin, scheduledTask -> {
                try {
                    task.run();
                    future.complete(null);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            });
        } else {
            // Bukkit/Spigot/Paper/Purpur: Use traditional async scheduler
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    task.run();
                    future.complete(null);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            });
        }

        return future;
    }
}