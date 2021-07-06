/*
 From Gamepiaynmo: https://github.com/Gamepiaynmo/TC-UHC
 */

package me.fallenbreath.tcuhc.options;

import me.fallenbreath.tcuhc.task.Task;
import me.fallenbreath.tcuhc.task.Taskable;

public class Option extends Taskable {
	
	private String optionId;
	private String optionName, optionDescript;
	
	private OptionType type;
	private Object defaultValue;
	private boolean needToSave;
	
	public Option(String id, String name, OptionType type, Object defaultValue) {
		optionId = id;
		optionName = name;
		this.type = type;
		this.defaultValue = defaultValue;
		type.setValue(defaultValue);
		this.addTask(Options.instance.taskSaveProperties);
	}
	
	public Object getValue() { return type.getValue(); }
	public int getIntegerValue() { return (int) type.getValue(); }
	public float getFloatValue() { return (float) type.getValue(); }
	public String getStringValue() { return type.getStringValue(); }
	public void incValue() { type.applyInc(); this.updateTasks(); }
	public void decValue() { type.applyDec(); this.updateTasks(); }
	public void setValue(Object value) { type.setValue(value); this.updateTasks(); }
	public void setStringValue(String value) { type.setStringValue(value); this.updateTasks(); }
	public void setInitialValue(String value) { type.setStringValue(value); }
	
	public String getName() { return optionName; }
	public String getDescription() { return optionDescript; }
	public String getIncString() { return type.getIncString(); }
	public String getDecString() { return type.getDecString(); }
	
	@Override
	public String toString() {
		return getStringValue();
	}
	
	@Override
	public Option addTask(Task task) {
		return (Option) super.addTask(task);
	}
	
	public Option setDescription(String des) {
		optionDescript = des;
		return this;
	}

	public Option setNeedToSave() {
		this.needToSave = true;
		return this;
	}

	public boolean needToSave() { return needToSave; }
	public String getId() { return optionId; }
	public void reset() { setValue(defaultValue); }
}
