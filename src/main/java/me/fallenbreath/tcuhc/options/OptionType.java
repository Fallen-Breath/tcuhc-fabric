package me.fallenbreath.tcuhc.options;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class OptionType {
	
	protected Object value;
	
	public abstract String getIncString();
	public abstract String getDecString();
	public abstract void applyInc();
	public abstract void applyDec();
	public abstract void setValue(Object nvalue);
	public abstract void setStringValue(String nvalue);
	public Object getValue() { return value; }
	public String getStringValue() { return getValue().toString(); }
	
	@Override
	public String toString() {
		return getStringValue();
	}
	
	static abstract class NumbericType<T> extends OptionType {
		
		protected T min, max, step;
		private String inc, dec;
		
		public NumbericType(T min, T max, T step) {
			this.min = min;
			this.max = max;
			this.step = step;
			inc = "+ " + step;
			dec = "- " + step;
		}
		
		@Override public String getIncString() { return inc; }
		@Override public String getDecString() { return dec; }
		
	}
	
	public static class IntegerType extends NumbericType<Integer> {
		
		public IntegerType(int min, int max, int step) {
			super(min, max, step);
			value = 0;
		}
		@Override public void applyInc() { value = Math.min(max, (int) value + step); }
		@Override public void applyDec() { value = Math.max(min, (int) value - step); }
		@Override public void setValue(Object nvalue) { value = Math.min(max, Math.max(min, (int) nvalue)); }
		@Override public void setStringValue(String nvalue) { setValue(Integer.parseInt(nvalue)); }
		
	}
	
	public static class FloatType extends NumbericType<Float> {
		
		public FloatType(float min, float max, float step) {
			super(min, max, step);
			value = 0.0f;
		}
		@Override public void applyInc() { value = Math.min(max, (float) value + step); }
		@Override public void applyDec() { value = Math.max(min, (float) value - step); }
		@Override public void setValue(Object nvalue) { value = Math.min(max, Math.max(min, (float) nvalue)); }
		@Override public void setStringValue(String nvalue) { setValue(Float.parseFloat(nvalue)); }
		
	}
	
	public static class BooleanType extends OptionType {
		
		public BooleanType() {
			value = false;
		}
		@Override public String getIncString() { return "true"; }
		@Override public String getDecString() { return "false"; }
		@Override public void applyInc() { value = true; }
		@Override public void applyDec() { value = false; }
		@Override public void setValue(Object nvalue) { value = (boolean) nvalue; }
		@Override public void setStringValue(String nvalue) { setValue(Boolean.parseBoolean(nvalue)); }
		
	}
	
	public static class EnumType extends OptionType {
		
		private final Class enumClass;
		private Object[] enums;
		
		private Method getMethod(Class clazz, String func, Class ... params) {
			try {
				return clazz.getMethod(func, params);
			} catch (IllegalArgumentException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		private Object invokeMethod(Method method, Object obj, Object ... params) {
			try {
				return method.invoke(obj, params);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		public EnumType(Class enums) {
			enumClass = enums;
			this.enums = (Object[]) invokeMethod(getMethod(enums, "values"), null);
			value = 0;
		}
		@Override public String getIncString() { return enums[((int) value + 1) % enums.length].toString(); }
		@Override public String getDecString() { return enums[((int) value + enums.length - 1) % enums.length].toString(); }
		@Override public void applyInc() { value = ((int) value + 1) % enums.length; }
		@Override public void applyDec() { value = ((int) value + enums.length - 1) % enums.length; }
		@Override public void setValue(Object nvalue) {
			for (int i = 0; i < enums.length; i++) {
				if (enums[i].equals(nvalue)) {
					value = i;
					break;
				}
			}
		}
		@Override public Object getValue() { return enums[(int) value]; }
		@Override public void setStringValue(String nvalue) { setValue(invokeMethod(getMethod(enumClass, "valueOf", String.class), null, nvalue)); }
		
	}
	
}
