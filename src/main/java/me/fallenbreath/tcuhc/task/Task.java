package me.fallenbreath.tcuhc.task;

public class Task {
	
	public void onUpdate() {}
	public boolean hasFinished() { return true; }
	public void onFinish() {}
	
	public static class TaskTimer extends Task {
		
		private int delay, interval;
		private boolean canceled;
		
		public TaskTimer(int delay, int interval) {
			this.delay = delay;
			this.interval = interval;
		}
		
		public final void setCanceled() {
			canceled = true;
		}
		
		@Override
		public final void onUpdate() {
			if (--delay <= 0) {
				onTimer();
				if (interval > 0) {
					delay = interval;
				} else canceled = true;
			}
		}
		
		public void onTimer() {}
		
		@Override
		public final boolean hasFinished() {
			return canceled;
		}
	}

}
