package org.brokenarrow.lootboxes.untlity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CheckCastToClazz {


	public static <K, V> Map<K, V> castMap(Map<?, ?> map, Class<K> keyClazz, Class<V> valueClazz) {
		Map<K, V> convertMap = new LinkedHashMap<>();
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			if (keyClazz.isInstance(key) && valueClazz.isInstance(value)) {
				convertMap.put(keyClazz.cast(key), valueClazz.cast(value));
			}
		}
		return convertMap;
	}

	public static <K, V> Map<K, V> castMap1(Map<?, ?> map, Class<K> keyClazz, Class<V> valueClazz) {
		return map.entrySet().stream().filter((entry) -> keyClazz.isInstance(entry.getKey()) && valueClazz.isInstance(entry.getValue()))
				.collect(Collectors.toMap(entry -> keyClazz.cast(entry.getKey()), e -> valueClazz.cast(e.getValue())));
	}

	public static <L> List<L> castList1(List<?> list, Class<L> clazz) {
		List<L> convertList = new ArrayList<>();
		for (Object entry : list) {
			if (clazz.isInstance(entry)) {
				convertList.add(clazz.cast(entry));
			}
		}
		return convertList;
	}

	public static <L> List<L> castList(List<?> list, Class<L> clazz) {
		return list.stream().filter(clazz::isInstance).map(clazz::cast).collect(Collectors.toList());
	}
}
