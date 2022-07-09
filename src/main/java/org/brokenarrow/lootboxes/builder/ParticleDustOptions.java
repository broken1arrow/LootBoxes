package org.brokenarrow.lootboxes.builder;

import org.bukkit.Color;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.bukkit.Color.fromRGB;

public final class ParticleDustOptions implements ConfigurationSerializable {

	private final Color fromColor;
	private final Color toColor;
	private final float size;

	public ParticleDustOptions(final Color fromColor, final float size) {
		this(fromColor, null, size);
	}

	public ParticleDustOptions(@NotNull final Color fromColor, final Color toColor, final float size) {
		this.fromColor = fromColor;
		this.toColor = toColor;
		this.size = size;
	}

	public Color getFromColor() {
		return fromColor;
	}

	public Color getToColor() {
		return toColor;
	}

	public float getSize() {
		return size;
	}

	/**
	 * Need format colors like #,#,# or # # #. Is in this order rgb.
	 * Suports from 0 to 255. for example 0,255,50.
	 *
	 * @param s string you want to convert to rgb.
	 * @return color class with your set colors.
	 */
	public static Color convertToColor(@NotNull String s) {

		if (s.contains(",")) {
			String[] string;
			int size = (string = s.split(",")).length;
			if (size == 3)
				return fromRGB(numberCheck(string[0]), numberCheck(string[1]), numberCheck(string[2]));

		} else if (s.contains(" ")) {
			String[] string;
			int size = (string = s.split(" ")).length;
			if (size == 3)
				return fromRGB(numberCheck(string[0]), numberCheck(string[1]), numberCheck(string[2]));
		}
		return null;
	}

	public static int numberCheck(String number) {
		try {
			return Integer.parseInt((number));
		} catch (NumberFormatException e) {
		}
		return 0;
	}

	@Override
	public String toString() {
		return "ParticleDustOptions{" +
				"fromColor=" + fromColor +
				", toColor=" + toColor +
				", particleSize=" + size +
				'}';
	}

	@NotNull
	@Override
	public Map<String, Object> serialize() {
		final Map<String, Object> particleData = new LinkedHashMap<>();
		particleData.put("From_color", this.fromColor);
		if (this.toColor != null)
			particleData.put("To_color", this.toColor);
		particleData.put("Particle_size", this.size);
		return particleData;
	}

	public static ParticleDustOptions deserialize(final Map<String, Object> map) {
		final Color fromColor = (Color) map.get("From_color");
		final Color toColor = (Color) map.get("To_color");
		final Double size = (Double) map.getOrDefault("Particle_size", 1);

		return new ParticleDustOptions(fromColor, toColor, (float) (size == null ? 0.5 : size));
	}
}


