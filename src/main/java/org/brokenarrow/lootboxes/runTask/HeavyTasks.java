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

	private static final Map<Object, Map<Long, LinkedList<HeavyLoad>>> map = new HashMap<>();
	private HeavyLoad firstReschudleElement;

	public HeavyTasks() {
		taskIDNumber = runTaskTimer(Lootboxes.getInstance(), 0L, 10L).getTaskId();
	}

	public void start() {
		if (Bukkit.getScheduler().isCurrentlyRunning(taskIDNumber) || Bukkit.getScheduler().isQueued(taskIDNumber))
			Bukkit.getScheduler().cancelTask(taskIDNumber);
		new HeavyTasks();
	}


	public void addLoad(final HeavyLoad task) {
		workloadDeque.add(task);
		//test.add(task);
	}

	public boolean isContainsMaxAmountInQueue(final Object object, final int maxAmountIncache) {
		return map.containsKey(object) && map.get(object).size() >= (maxAmountIncache == 0 ? 1 : maxAmountIncache);
	}

	public void setMaxAmountEachEntityCanQueue(Object object, final long TimeBeforeRemoveSeconds, final HeavyLoad task) {
		final Map<Long, LinkedList<HeavyLoad>> addData = new HashMap<>();
		final LinkedList<HeavyLoad> linkedList = new LinkedList<>();
		if (map.containsKey(object)) {
			addData.putAll(map.get(object));
			for (final Object key : map.keySet())
				if (key.equals(object)) {
					object = key;
				}
		}
		linkedList.add(task);
		addData.put(TimeBeforeRemoveSeconds + 1000, linkedList);
		map.put(object, addData);
	}

	private boolean cumputeTasks(final HeavyLoad task) {
		if (task != null) {
			if (task.reschedule()) {
				addLoad(task);
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

	private double maxMSPerTick(final HeavyLoad task) {
		if (task != null) {
			return task.getMilliPerTick();
		}
		return 5.0;
	}

	@Override
	public void run() {
		checkIfTimeFinish();
		final long stoptime = (long) (System.nanoTime() + (1000_000 * MAX_MS_PER_TICK));
		final long stoptimeNew = System.nanoTime();

		while (!workloadDeque.isEmpty() && System.nanoTime() <= stoptime) {
			final HeavyLoad heavyload = workloadDeque.poll();
			if (!(System.nanoTime() <= stoptimeNew + (1000_000 * maxMSPerTick(heavyload)))) continue;
			if (!delayTasks(heavyload, amount)) continue;
			/*if (heavyload instanceof SpawnCustomEffects) {
				heavyload.compute();
				break;
			}*/
			heavyload.compute();
			if (cumputeTasks(heavyload)) return;
		}
		this.amount++;
/*		HeavyLoad heavyload;
		do {
			heavyload = workloadDeque.poll();
			if (!(System.currentTimeMillis() <= stoptime)) continue;
			if (!delayTasks(heavyload, amount)) continue;
			if (heavyload instanceof SpawnCustomEffects) {
				heavyload.compute();
				break;
			}
			heavyload.compute();
			if (cumputeTasks(heavyload)) return;

		} while (!workloadDeque.isEmpty() && !(System.currentTimeMillis() <= stoptimeNew + maxMSPerTick(heavyload)));
*/

	}

	public void checkIfTimeFinish() {
		final Set<Long> list = new LinkedHashSet<>();
		if (this.amount % 20 * 3 == 0) {
			for (final Object object : map.keySet()) {
				if (object != null)
					for (final Map.Entry<Long, LinkedList<HeavyLoad>> entity : map.get(object).entrySet()) {

						if (System.currentTimeMillis() >= entity.getKey()) {
							list.add(entity.getKey());
						}
					}
				list.forEach(time -> map.get(object).remove(time));
			}
		}
	}

}

