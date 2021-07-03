package me.fallenbreath.tcuhc.task;

import com.google.common.collect.Lists;

import java.util.List;

public class Taskable {
	
	private List<Task> tasks = Lists.newArrayList(),
			toRemove = Lists.newArrayList();
	
	public Taskable addTask(Task task) {
		tasks.add(task);
		return this;
	}
	
	public void updateTasks() {
		for (int i = 0; i < tasks.size(); i++)
			tasks.get(i).onUpdate();
		tasks.stream().filter(task -> task.hasFinished()).forEach(toRemove::add);
		toRemove.forEach(task -> task.onFinish());
		tasks.removeAll(toRemove);
		toRemove.clear();
	}

}
