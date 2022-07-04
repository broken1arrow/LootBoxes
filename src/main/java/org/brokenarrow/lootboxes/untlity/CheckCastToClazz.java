package org.brokenarrow.lootboxes.untlity;

import java.util.*;
import java.util.stream.Collectors;

public class CheckCastToClazz {


	public static <K, V> Map<K, V> castMap(final Map<?, ?> map, final Class<K> keyClazz, final Class<V> valueClazz) {
		final Map<K, V> convertMap = new LinkedHashMap<>();
		for (final Map.Entry<?, ?> entry : map.entrySet()) {
			final Object key = entry.getKey();
			final Object value = entry.getValue();
			if (keyClazz.isInstance(key) && valueClazz.isInstance(value)) {
				convertMap.put(keyClazz.cast(key), valueClazz.cast(value));
			}
		}
		return convertMap;
	}

	public static <K, V> Map<K, V> castMap1(final Map<?, ?> map, final Class<K> keyClazz, final Class<V> valueClazz) {
		return map.entrySet().stream().filter((entry) -> keyClazz.isInstance(entry.getKey()) && valueClazz.isInstance(entry.getValue()))
				.collect(Collectors.toMap(entry -> keyClazz.cast(entry.getKey()), e -> valueClazz.cast(e.getValue())));
	}

	public static <L> List<L> castList1(final List<?> list, final Class<L> clazz) {
		final List<L> convertList = new ArrayList<>();
		for (final Object entry : list) {
			if (clazz.isInstance(entry)) {
				convertList.add(clazz.cast(entry));
			}
		}
		return convertList;
	}

	public static <L> List<L> castList(final List<?> list, final Class<L> clazz) {
		return list.stream().filter(clazz::isInstance).map(clazz::cast).collect(Collectors.toList());
	}

	public static List<Object> convertObjectsToList(final Object... objects) {
		return Arrays.asList(objects);
	}

	public static <L> boolean isListThisClass(final List<?> list, final Class<L> clazz) {
		if (list == null || list.isEmpty()) return false;
		return list.stream().allMatch(clazz::isInstance);
	}
}
