/*
 From Gamepiaynmo: https://github.com/Gamepiaynmo/TC-UHC
 */

package me.fallenbreath.tcuhc.task;

import com.google.common.collect.Lists;

import java.util.List;

public class Taskable {
	
	private final List<Task> tasks = Lists.newArrayList();
	private final List<Task> toRemove = Lists.newArrayList();
	
	public Taskable addTask(Task task) {
		tasks.add(task);
		task.onAdd();
		return this;
	}

	public void updateTasks() {
		//noinspection ForLoopReplaceableByForEach
		for (int i = 0; i < tasks.size(); i++) {
			tasks.get(i).onUpdate();
		}
		tasks.stream().filter(Task::hasFinished).forEach(toRemove::add);
		toRemove.forEach(Task::onFinish);
		tasks.removeAll(toRemove);
		toRemove.clear();
	}

}
