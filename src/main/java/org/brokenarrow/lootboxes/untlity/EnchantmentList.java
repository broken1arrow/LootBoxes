package org.brokenarrow.lootboxes.untlity;

import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnchantmentList {

	private List<Enchantment> enchantments = new ArrayList<>();
	private boolean legacy;

	public EnchantmentList() {
		try {
			this.enchantments = Stream.of(Enchantment.values())
					.sorted(Comparator.comparing(o -> o.getKey().getKey())).collect(Collectors.toList());
		} catch (NoSuchMethodError ignored) {
			this.enchantments = Stream.of(Enchantment.values())
					.sorted(Comparator.comparing(Enchantment::getName)).collect(Collectors.toList());
			legacy = true;
		}

	}

	public List<Enchantment> getEnchantments(String enchantMentsToSearchFor) {
		if (enchantMentsToSearchFor != null && !enchantMentsToSearchFor.isEmpty())
			if (legacy)
				return enchantments.stream().filter((enchantment) -> enchantment.getName().contains(enchantMentsToSearchFor)).sorted(Comparator.comparing(Enchantment::getName)).collect(Collectors.toList());
			else
				return enchantments.stream().filter((enchantment) -> enchantment.getKey().getKey().contains(enchantMentsToSearchFor)).sorted(Comparator.comparing(o -> o.getKey().getKey())).collect(Collectors.toList());
		return enchantments;
	}
}
