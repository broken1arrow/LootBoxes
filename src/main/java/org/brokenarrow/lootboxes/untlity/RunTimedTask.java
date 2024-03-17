package org.brokenarrow.lootboxes.untlity;

import org.brokenarrow.lootboxes.Lootboxes;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class RunTimedTask {


    public static BukkitTask runtask(Runnable task, boolean async) {
        if (async)
            return Bukkit.getScheduler().runTaskAsynchronously(Lootboxes.getInstance(), task);
        else
            return Bukkit.getScheduler().runTask(Lootboxes.getInstance(), task);
    }

    public static BukkitTask runtaskLater(long tick, Runnable task, boolean async) {
        if (async)
            return Bukkit.getScheduler().runTaskLaterAsynchronously(Lootboxes.getInstance(), task, tick);
        else
            return Bukkit.getScheduler().runTaskLater(Lootboxes.getInstance(), task, tick);
    }

    public static BukkitTask runTimedTask(long startTime, long tick, Runnable task, boolean async) {
        if (async)
            return Bukkit.getScheduler().runTaskTimerAsynchronously(Lootboxes.getInstance(), task, startTime, tick);
        else
            return Bukkit.getScheduler().runTaskTimer(Lootboxes.getInstance(), task, startTime, tick);
    }
}
