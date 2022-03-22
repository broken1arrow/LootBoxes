package org.brokenarrow.lootboxes.untlity;

import org.bukkit.enchantments.Enchantment;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnchantmentList {

	private final List<Enchantment> enchantments;

	public EnchantmentList() {
		this.enchantments = Stream.of(Enchantment.values())
				.sorted(Comparator.comparing(o -> o.getKey().getKey())).collect(Collectors.toList());
	}

	public List<Enchantment> getEnchantments(String enchantMentsToSearchFor) {
		if (enchantMentsToSearchFor != null && !enchantMentsToSearchFor.isEmpty())
			return enchantments.stream().filter((enchantment) -> enchantment.getKey().getKey().contains(enchantMentsToSearchFor)).sorted(Comparator.comparing(o -> o.getKey().getKey())).collect(Collectors.toList());
		return enchantments;
	}
}
