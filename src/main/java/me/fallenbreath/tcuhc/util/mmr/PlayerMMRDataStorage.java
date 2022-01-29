package me.fallenbreath.tcuhc.util.mmr;

import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.function.Function;

public class PlayerMMRDataStorage extends LinkedHashMap<UUID, PlayerMMRDataStorage.MMRItem> {
	public static class MMRItem {
		private static final double BASE_PERFORMANCE_POINT = 10;
		private static final double MIN_PERFORMANCE_POINT = BASE_PERFORMANCE_POINT / 2;
		/**
		 * Diminishing Marginal Factor
		 * f(0) = 1, f(1) = 0.87, f(3) = 0.67, f(5)=0.57, f(10) = 0.4
		 */
		private static final Function<Double, Double> DIMINISHING_MARGINAL = x -> 1 / (0.15 * x + 1);

		private final String name;
		private int winStreak;
		private double performancePoint;

		public MMRItem(String name) {
			this.name = name;
			this.winStreak = 0;
			this.performancePoint = BASE_PERFORMANCE_POINT;
		}

		public double getPerformancePoint() {
			return performancePoint;
		}

		public int getWinStreak() {
			return winStreak;
		}

		public void addPerformancePoint(double delta) {
			if (delta > 0 && this.performancePoint > BASE_PERFORMANCE_POINT || delta < 0 && this.performancePoint < BASE_PERFORMANCE_POINT) {
				// apply diminishing marginal factor
				double distance = Math.abs(this.performancePoint - BASE_PERFORMANCE_POINT);
				delta *= DIMINISHING_MARGINAL.apply(distance);
			}
			this.performancePoint = Math.max(this.performancePoint + delta, MIN_PERFORMANCE_POINT);
		}

		public void setWinStreak(int winStreak) {
			this.winStreak = winStreak;
		}

		public String getName() {
			return name;
		}
	}
}
