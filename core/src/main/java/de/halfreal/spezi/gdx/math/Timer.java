package de.halfreal.spezi.gdx.math;

import java.util.TimerTask;

import com.badlogic.gdx.Gdx;

public class Timer {

	public static abstract class Task extends TimerTask {

		private Runnable runUiRunnable = new Runnable() {

			@Override
			public void run() {
				runUI();
			}
		};

		@Override
		public void run() {
			Gdx.app.postRunnable(runUiRunnable);
		}

		public abstract void runUI();

	}

	private static java.util.Timer timer;

	static {
		timer = new java.util.Timer();

	}

	public static void schedule(Task task) {
		task.run();
	};

	public static void schedule(Task task, float delay) {
		if (delay < 0) {
			delay = 0f;
		}
		timer.schedule(task, (long) (delay * 1000));
	};

	public static void schedule(Task task, float delay, float repeatDelay) {
		if (delay < 0) {
			delay = 0f;
		}

		if (repeatDelay < 0) {
			repeatDelay = 0f;
		}

		timer.schedule(task, (long) (delay * 1000), (long) (repeatDelay * 1000));
	};

}
