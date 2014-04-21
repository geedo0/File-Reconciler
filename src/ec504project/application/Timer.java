package ec504project.application;

import java.util.concurrent.TimeUnit;

public class Timer {
		private long startTime;
		private long endTime = -99999;
		public long nanosecondsElapsed;
		
		Timer(boolean startImmediately) {
			if(startImmediately)
				start();
		}
		
		public void start() {
			startTime = System.nanoTime();
		}
		
		public void stop() {
			endTime = System.nanoTime();
		}

		public void prettyPrintTime() {
			if(endTime < 0) {
				System.out.println("Timer has not ended!");
				return;
			}
			nanosecondsElapsed = endTime - startTime;
			long min = TimeUnit.NANOSECONDS.toMinutes(nanosecondsElapsed);
			long s = TimeUnit.NANOSECONDS.toSeconds(nanosecondsElapsed) % 60;
			long ms = TimeUnit.NANOSECONDS.toMillis(nanosecondsElapsed) % 1_000;
			long ns = nanosecondsElapsed % 1_000_000;
			System.out.println("Executed in " + min + " minutes " + s + " seconds " + ms + " milliseconds and " + ns + " nanoseconds!");
		}
}