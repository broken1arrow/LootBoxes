package org.brokenarrow.lootboxes.untlity;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParticleEffectList {


	private final List<Particle> particleList;

	public ParticleEffectList() {
		this.particleList = Stream.of(Particle.values()).filter(this::sortOut).sorted(Comparator.comparing(Enum::name)).collect(Collectors.toList());
		/*for (Particle effect : particleList) {
			System.out.println("particle: " + effect);
		}*/
	}

	public List<Particle> getParticleList(String enchantMentsToSearchFor) {
		if (enchantMentsToSearchFor != null && !enchantMentsToSearchFor.isEmpty())
			return particleList.stream().filter((effect) -> effect.name().contains(enchantMentsToSearchFor.toUpperCase())).sorted(Comparator.comparing(Enum::name)).collect(Collectors.toList());
		return particleList;

	}

	public boolean sortOut(final Particle particle) {
		return !(particle.name().equals("DUST_COLOR_TRANSITION") || particle.name().equals("LEGACY_BLOCK_CRACK")
				|| particle.name().equals("LEGACY_BLOCK_DUST") || particle.name().equals("LEGACY_FALLING_DUST")
				|| particle.name().equals("MOB_APPEARANCE"));
	}

	public ItemStack checkParticleList(final Particle particle) {
		switch (particle.name()) {
			case "ASH":
				return CreateItemUtily.of(Material.COAL).makeItemStack();
			case "BLOCK_CRACK":
				return CreateItemUtily.of(Material.CRACKED_STONE_BRICKS).makeItemStack();
			case "BLOCK_DUST":
				return CreateItemUtily.of(Material.HAY_BLOCK).makeItemStack();
			case "BLOCK_MARKER":
			case "BARRIER":
				return CreateItemUtily.of(Material.BARRIER).makeItemStack();
			case "BUBBLE_COLUMN_UP":
			case "BUBBLE_POP":
				return CreateItemUtily.of(Material.WATER_BUCKET).makeItemStack();
			case "CAMPFIRE_COSY_SMOKE":
			case "CAMPFIRE_SIGNAL_SMOKE":
				return CreateItemUtily.of(Material.CAMPFIRE).makeItemStack();
			case "CLOUD":
				return CreateItemUtily.of(Material.WHITE_WOOL).makeItemStack();
			case "COMPOSTER":
				return CreateItemUtily.of(Material.COMPOSTER).makeItemStack();
			case "CRIMSON_SPORE":
				return CreateItemUtily.of(Material.CRIMSON_FUNGUS).makeItemStack();
			case "CRIT":
			case "CRIT_MAGIC":
				return CreateItemUtily.of(Material.IRON_SWORD).makeItemStack();
			case "CURRENT_DOWN":
				return CreateItemUtily.of(Material.BUBBLE_CORAL).makeItemStack();
			case "DAMAGE_INDICATOR":
				return CreateItemUtily.of(Material.GOLDEN_SWORD).makeItemStack();
			case "DOLPHIN":
				return CreateItemUtily.of(Material.DOLPHIN_SPAWN_EGG).makeItemStack();
			case "DRAGON_BREATH":
				return CreateItemUtily.of(Material.DRAGON_BREATH).makeItemStack();
			case "DRIPPING_DRIPSTONE_LAVA":
			case "DRIPPING_DRIPSTONE_WATER":
				return CreateItemUtily.of(Material.POINTED_DRIPSTONE).makeItemStack();
			case "DRIPPING_HONEY":
				return CreateItemUtily.of(Material.BEE_NEST).makeItemStack();
			case "DRIPPING_OBSIDIAN_TEAR":
				return CreateItemUtily.of(Material.CRYING_OBSIDIAN).makeItemStack();
			case "DRIP_LAVA":
				return CreateItemUtily.of(Material.LAVA_BUCKET).makeItemStack();
			case "DRIP_WATER":
				return CreateItemUtily.of(Material.WATER_BUCKET).makeItemStack();
			case "ELECTRIC_SPARK":
				return CreateItemUtily.of(Material.LIGHTNING_ROD).makeItemStack();
			case "ENCHANTMENT_TABLE":
				return CreateItemUtily.of(Material.ENCHANTING_TABLE).makeItemStack();
			case "END_ROD":
				return CreateItemUtily.of(Material.END_ROD).makeItemStack();
			case "EXPLOSION_HUGE":
				return CreateItemUtily.of(Material.TNT_MINECART).makeItemStack();
			case "EXPLOSION_LARGE":
				return CreateItemUtily.of(Material.TNT).makeItemStack();
			case "EXPLOSION_NORMAL":
				return CreateItemUtily.of(Material.TNT).makeItemStack();
			case "FALLING_DRIPSTONE_LAVA":
				return CreateItemUtily.of(Material.POINTED_DRIPSTONE).makeItemStack();
			case "FALLING_DRIPSTONE_WATER":
				return CreateItemUtily.of(Material.POINTED_DRIPSTONE).makeItemStack();
			case "FALLING_DUST":
				return CreateItemUtily.of(Material.SAND).makeItemStack();
			case "FALLING_HONEY":
				return CreateItemUtily.of(Material.HONEY_BOTTLE).makeItemStack();
			case "FALLING_LAVA":
				return CreateItemUtily.of(Material.LAVA_BUCKET).makeItemStack();
			case "FALLING_NECTAR":
				return CreateItemUtily.of(Material.HONEYCOMB).makeItemStack();
			case "FALLING_OBSIDIAN_TEAR":
				return CreateItemUtily.of(Material.CRYING_OBSIDIAN).makeItemStack();
			case "FALLING_SPORE_BLOSSOM":
				return CreateItemUtily.of(Material.SPORE_BLOSSOM).makeItemStack();
			case "FALLING_WATER":
				return CreateItemUtily.of(Material.WATER_BUCKET).makeItemStack();
			case "FIREWORKS_SPARK":
				return CreateItemUtily.of(Material.FIREWORK_ROCKET).makeItemStack();
			case "FLAME":
				return CreateItemUtily.of(Material.TORCH).makeItemStack();
			case "FLASH":
				return CreateItemUtily.of(Material.FLINT_AND_STEEL).makeItemStack();
			case "GLOW":
			case "GLOW_SQUID_INK":
				return CreateItemUtily.of(Material.GLOW_INK_SAC).makeItemStack();
			case "HEART":
				return CreateItemUtily.of(Material.APPLE).makeItemStack();
			case "ITEM_CRACK":
				return CreateItemUtily.of(Material.CRACKED_NETHER_BRICKS).makeItemStack();
			case "LANDING_HONEY":
				return CreateItemUtily.of(Material.HONEYCOMB).makeItemStack();
			case "LANDING_LAVA":
				return CreateItemUtily.of(Material.LAVA_BUCKET).makeItemStack();
			case "LANDING_OBSIDIAN_TEAR":
				return CreateItemUtily.of(Material.CRYING_OBSIDIAN).makeItemStack();
			case "LAVA":
				return CreateItemUtily.of(Material.LAVA_BUCKET).makeItemStack();
			case "NAUTILUS":
				return CreateItemUtily.of(Material.CONDUIT).makeItemStack();
			case "NOTE":
				return CreateItemUtily.of(Material.NOTE_BLOCK).makeItemStack();
			case "PORTAL":
				return CreateItemUtily.of(Material.OBSIDIAN).makeItemStack();
			case "REDSTONE":
				return CreateItemUtily.of(Material.REDSTONE).makeItemStack();
			case "REVERSE_PORTAL":
				return CreateItemUtily.of(Material.END_PORTAL_FRAME).makeItemStack();
			case "SCRAPE":
				return CreateItemUtily.of(Material.COPPER_BLOCK).makeItemStack();
			case "SLIME":
				return CreateItemUtily.of(Material.SLIME_BLOCK).makeItemStack();
			case "SMALL_FLAME":
				return CreateItemUtily.of(Material.TORCH).makeItemStack();
			case "SMOKE_LARGE":
				return CreateItemUtily.of(Material.CAMPFIRE).makeItemStack();
			case "SMOKE_NORMAL":
				return CreateItemUtily.of(Material.CAMPFIRE).makeItemStack();
			case "SNEEZE":
				return CreateItemUtily.of(Material.PANDA_SPAWN_EGG).makeItemStack();
			case "SNOWBALL":
				return CreateItemUtily.of(Material.SNOWBALL).makeItemStack();
			case "SNOWFLAKE":
				return CreateItemUtily.of(Material.SNOWBALL).makeItemStack();
			case "SNOW_SHOVEL":
				return CreateItemUtily.of(Material.SNOW_BLOCK).makeItemStack();
			case "SOUL":
				return CreateItemUtily.of(Material.SOUL_SAND).makeItemStack();
			case "SOUL_FIRE_FLAME":
				return CreateItemUtily.of(Material.SOUL_TORCH).makeItemStack();
			case "SPELL":
				return CreateItemUtily.of(Material.WITCH_SPAWN_EGG).makeItemStack();
			case "SPELL_INSTANT":
				return CreateItemUtily.of(Material.WITCH_SPAWN_EGG).makeItemStack();
			case "SPELL_MOB":
				return CreateItemUtily.of(Material.WITCH_SPAWN_EGG).makeItemStack();
			case "SPELL_MOB_AMBIENT":
				return CreateItemUtily.of(Material.WITCH_SPAWN_EGG).makeItemStack();
			case "SPELL_WITCH":
				return CreateItemUtily.of(Material.WITCH_SPAWN_EGG).makeItemStack();
			case "SPIT":
				return CreateItemUtily.of(Material.LLAMA_SPAWN_EGG).makeItemStack();
			case "SPORE_BLOSSOM_AIR":
				return CreateItemUtily.of(Material.SPORE_BLOSSOM).makeItemStack();
			case "SQUID_INK":
				return CreateItemUtily.of(Material.INK_SAC).makeItemStack();
			case "SUSPENDED":
				return CreateItemUtily.of(Material.STONE).makeItemStack();
			case "SUSPENDED_DEPTH":
				return CreateItemUtily.of(Material.STONE).makeItemStack();
			case "SWEEP_ATTACK":
				return CreateItemUtily.of(Material.GOLDEN_SWORD).makeItemStack();
			case "TOTEM":
				return CreateItemUtily.of(Material.TOTEM_OF_UNDYING).makeItemStack();
			case "TOWN_AURA":
				return CreateItemUtily.of(Material.MYCELIUM).makeItemStack();
			case "VIBRATION":
				return CreateItemUtily.of(Material.SCULK_SENSOR).makeItemStack();
			case "VILLAGER_ANGRY":
				return CreateItemUtily.of(Material.VILLAGER_SPAWN_EGG).makeItemStack();
			case "VILLAGER_HAPPY":
				return CreateItemUtily.of(Material.VILLAGER_SPAWN_EGG).makeItemStack();
			case "WARPED_SPORE":
				return CreateItemUtily.of(Material.WARPED_HYPHAE).makeItemStack();
			case "WATER_BUBBLE":
				return CreateItemUtily.of(Material.WATER_BUCKET).makeItemStack();
			case "WATER_DROP":
				return CreateItemUtily.of(Material.WATER_BUCKET).makeItemStack();
			case "WATER_SPLASH":
				return CreateItemUtily.of(Material.WATER_BUCKET).makeItemStack();
			case "WATER_WAKE":
				return CreateItemUtily.of(Material.WATER_BUCKET).makeItemStack();
			case "WAX_OFF":
				return CreateItemUtily.of(Material.HONEYCOMB).makeItemStack();
			case "WAX_ON":
				return CreateItemUtily.of(Material.HONEYCOMB).makeItemStack();
			case "WHITE_ASH":
				return CreateItemUtily.of(Material.COAL).makeItemStack();
			default:
				return null;
			//throw new IllegalStateException("Unexpected value: " + particle.name());
		}

	}

	enum particles {

		DUST_COLOR_TRANSITION,
		LEGACY_BLOCK_CRACK,
		LEGACY_BLOCK_DUST,
		LEGACY_FALLING_DUST,
		MOB_APPEARANCE,
	}
}
