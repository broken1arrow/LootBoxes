package org.brokenarrow.lootboxes.runTask;


import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.api.HeavyLoad;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class HeavyTasks extends BukkitRunnable {

	private static int taskIDNumber = -1;

	private int amount = 0;
	private static final double MAX_MS_PER_TICK = 5.5;

	private static final Queue<HeavyLoad> workloadDeque = new ConcurrentLinkedDeque<>();

	private static final Map<Object, List<Long>> map = new HashMap<>();
	private HeavyLoad firstReschudleElement;

	public HeavyTasks() {
		taskIDNumber = runTaskTimer(Lootboxes.getInstance(), 0L, 10L).getTaskId();
	}

	public void start() {
		if (Bukkit.getScheduler().isCurrentlyRunning(taskIDNumber) || Bukkit.getScheduler().isQueued(taskIDNumber))
			Bukkit.getScheduler().cancelTask(taskIDNumber);
		new HeavyTasks();
	}

	public boolean isContainsMaxAmountInQueue(final Object object, final int maxAmountIncache) {
		return map.containsKey(object) && map.get(object).size() >= (maxAmountIncache == 0 ? 1 : maxAmountIncache);
	}

	/**
	 * Add a task to preform, this will not limit amount of task you add.
	 *
	 * @param task you want to add.
	 * @return true if it can add the task.
	 */
	public boolean addTask(final HeavyLoad task) {
		return workloadDeque.add(task);
	}

	/**
	 * Add a task and limit amount you want it to add of same type.
	 * You limit it with the taskName and amount of same task max it can hold.
	 *
	 * @param taskName         the name of the task.
	 * @param timeBeforeRemove the time in seconds when it shall remove the task.
	 * @param maxAmountIncache amount of same task name you want it to run the task
	 * @param task             the task you want it to execute.
	 * @return true if it can add the task.
	 */
	public boolean addTask(Object taskName, final long timeBeforeRemove, final int maxAmountIncache, final HeavyLoad task) {
		List<Long> longList = map.get(taskName);
		if (!isContainsMaxAmountInQueue(taskName, maxAmountIncache)) {
			if (longList == null)
				longList = new ArrayList<>();

			longList.add(System.currentTimeMillis() + (timeBeforeRemove * 1000));
			map.put(taskName, longList);
			this.addTask(task);
			return true;
		}
		return false;
	}

	private boolean cumputeTasks(final HeavyLoad task) {
		if (task != null) {
			if (task.reschedule()) {
				addTask(task);
				if (firstReschudleElement == null) {
					firstReschudleElement = task;
				} else {
					return firstReschudleElement != task;
				}
				return true;
			}
		}
		return true;
	}

	private boolean delayTasks(final HeavyLoad task, final int amount) {
		if (task != null)
			return task.computeWithDelay(amount);
		return false;
	}

	@Override
	public void run() {
		checkIfTimeFinish();
		final long stoptime = (long) (System.nanoTime() + (1000_000 * MAX_MS_PER_TICK));

		while (!workloadDeque.isEmpty() && System.nanoTime() <= stoptime) {
			final HeavyLoad heavyload = workloadDeque.poll();
			if (!delayTasks(heavyload, amount)) continue;
			heavyload.computeTask();
			if (cumputeTasks(heavyload)) break;
		}
		this.amount++;
	}

	public void checkIfTimeFinish() {

		if (this.amount % 20 == 0) {
			map.forEach((key, value) -> value.removeIf(time -> System.currentTimeMillis() >= time));
		}

	}

}

