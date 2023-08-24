package org.brokenarrow.lootboxes.untlity;


import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ParticleEffect;
import org.brokenarrow.lootboxes.untlity.ServerVersion.Version;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.SpawnEgg;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParticleEffectList {


	private final List<Object> particleList;
	private final Map<String, ItemStack> particlesCached = new HashMap<>();
	private final ServerVersion version;
	private ParticlesUtility particlesUtility;

	public ParticleEffectList() {
		version = Lootboxes.getInstance().getServerVersion();
		if (version.atLeast(ServerVersion.Version.v1_9)) {
			particlesUtility = new ParticlesUtility(version);
			this.particleList = particlesUtility.getParticles();
		} else
			this.particleList = Stream.of(Effect.values()).filter(this::sortOut).sorted(Comparator.comparing(Enum::name)).collect(Collectors.toList());

		this.cacheParticleWithItemStack();
		if (version.olderThan(ServerVersion.Version.v1_13))
			itemsAddedIn1_12AndOlder();
		if (version.atLeast(ServerVersion.Version.v1_13))
			itemsAddedIn1_13();
		if (version.newerThan(ServerVersion.Version.v1_13))
			itemsAddedIn1_14();
		if (version.newerThan(ServerVersion.Version.v1_14))
			itemsAddedIn1_15();
		if (version.newerThan(ServerVersion.Version.v1_15))
			itemsAddedIn1_16();
		if (version.newerThan(ServerVersion.Version.v1_16))
			itemsAddedIn1_17();
	}

	public List<Object> getParticleList(final String particleToSearchFor) {
		if (particleToSearchFor != null && !particleToSearchFor.isEmpty())
			if (version.atLeast(ServerVersion.Version.v1_9) && particlesUtility != null)
				return particlesUtility.getParticlesList(particleToSearchFor);
			else
				return particleList.stream().map(Effect.class::cast).filter((effect) -> effect.name().contains(particleToSearchFor.toUpperCase())).sorted(Comparator.comparing(Enum::name)).collect(Collectors.toList());

		return particleList;
	}

	public boolean sortOut(final Effect effect) {
		if (effect.name().contains("ZOMBIE_"))
			return false;
		return !(effect.name().equals("DUST_COLOR_TRANSITION") || effect.name().equals("LEGACY_BLOCK_CRACK")
				|| effect.name().equals("LEGACY_BLOCK_DUST") || effect.name().equals("LEGACY_FALLING_DUST")
				|| effect.name().equals("MOB_APPEARANCE")|| effect.name().equals("RECORD_PLAY") || effect.name().equals("STEP_SOUND") || effect.name().contains("CLICK"));
	}

	private void cacheParticleWithItemStack() {
		this.particlesCached.put("ASH", CreateItemUtily.of(Material.COAL).makeItemStack());
		this.particlesCached.put("BLOCK_DUST", CreateItemUtily.of(Material.HAY_BLOCK).makeItemStack());
		this.particlesCached.put("BARRIER", CreateItemUtily.of(Material.BARRIER).makeItemStack());
		this.particlesCached.put("BUBBLE_COLUMN_UP", CreateItemUtily.of(Material.WATER_BUCKET).makeItemStack());
		this.particlesCached.put("BUBBLE_POP", CreateItemUtily.of(Material.WATER_BUCKET).makeItemStack());
		this.particlesCached.put("CRIT", CreateItemUtily.of(Material.IRON_SWORD).makeItemStack());
		this.particlesCached.put("CRIT_MAGIC", CreateItemUtily.of(Material.IRON_SWORD).makeItemStack());
		this.particlesCached.put("CURRENT_DOWN", CreateItemUtily.of(Material.WATER_BUCKET).makeItemStack());
		this.particlesCached.put("DRIP_LAVA", CreateItemUtily.of(Material.LAVA_BUCKET).makeItemStack());
		this.particlesCached.put("DRIP_WATER", CreateItemUtily.of(Material.WATER_BUCKET).makeItemStack());
		this.particlesCached.put("EXPLOSION_LARGE", CreateItemUtily.of(Material.TNT).makeItemStack());
		this.particlesCached.put("FALLING_DUST", CreateItemUtily.of(Material.SAND).makeItemStack());
		this.particlesCached.put("FALLING_LAVA", CreateItemUtily.of(Material.LAVA_BUCKET).makeItemStack());
		this.particlesCached.put("FALLING_WATER", CreateItemUtily.of(Material.WATER_BUCKET).makeItemStack());
		this.particlesCached.put("FLAME", CreateItemUtily.of(Material.TORCH).makeItemStack());
		this.particlesCached.put("FLASH", CreateItemUtily.of(Material.FLINT_AND_STEEL).makeItemStack());
		this.particlesCached.put("GLOW", CreateItemUtily.of(Material.BEACON).makeItemStack());
		this.particlesCached.put("HEART", CreateItemUtily.of(Material.APPLE).makeItemStack());
		this.particlesCached.put("LANDING_LAVA", CreateItemUtily.of(Material.LAVA_BUCKET).makeItemStack());
		this.particlesCached.put("LAVA", CreateItemUtily.of(Material.LAVA_BUCKET).makeItemStack());
		this.particlesCached.put("NOTE", CreateItemUtily.of(Material.NOTE_BLOCK).makeItemStack());
		this.particlesCached.put("PORTAL", CreateItemUtily.of(Material.OBSIDIAN).makeItemStack());
		this.particlesCached.put("REDSTONE", CreateItemUtily.of(Material.REDSTONE).makeItemStack());
		this.particlesCached.put("SLIME", CreateItemUtily.of(Material.SLIME_BLOCK).makeItemStack());
		this.particlesCached.put("SMALL_FLAME", CreateItemUtily.of(Material.TORCH).makeItemStack());
		this.particlesCached.put("SMOKE_LARGE", CreateItemUtily.of(Material.TORCH).makeItemStack());
		this.particlesCached.put("SMOKE_NORMAL", CreateItemUtily.of(Material.TORCH).makeItemStack());
		this.particlesCached.put("SNOW_SHOVEL", CreateItemUtily.of(Material.SNOW_BLOCK).makeItemStack());
		this.particlesCached.put("SOUL", CreateItemUtily.of(Material.SOUL_SAND).makeItemStack());
		this.particlesCached.put("SUSPENDED", CreateItemUtily.of(Material.STONE).makeItemStack());
		this.particlesCached.put("SUSPENDED_DEPTH", CreateItemUtily.of(Material.STONE).makeItemStack());
		this.particlesCached.put("SWEEP_ATTACK", CreateItemUtily.of(Material.DIAMOND_SWORD).makeItemStack());
		this.particlesCached.put("WATER_BUBBLE", CreateItemUtily.of(Material.WATER_BUCKET).makeItemStack());
		this.particlesCached.put("WATER_DROP", CreateItemUtily.of(Material.WATER_BUCKET).makeItemStack());
		this.particlesCached.put("WATER_SPLASH", CreateItemUtily.of(Material.WATER_BUCKET).makeItemStack());
		this.particlesCached.put("WATER_WAKE", CreateItemUtily.of(Material.WATER_BUCKET).makeItemStack());
		this.particlesCached.put("WHITE_ASH", CreateItemUtily.of(Material.COAL).makeItemStack());
	}

	private void itemsAddedIn1_12AndOlder() {
		this.particlesCached.put("BLAZE_SHOOT", CreateItemUtily.of("BLAZE_ROD").makeItemStack());
		this.particlesCached.put("BOW_FIRE", CreateItemUtily.of("BOW").makeItemStack());
		this.particlesCached.put("BLOCK_CRACK", CreateItemUtily.of("SMOOTH_BRICK").makeItemStack());
		this.particlesCached.put("CLOUD", CreateItemUtily.of("WOOL", "WHITE").makeItemStack());
		this.particlesCached.put("COLOURED_DUST", CreateItemUtily.of("REDSTONE").makeItemStack());
		this.particlesCached.put("DAMAGE_INDICATOR", CreateItemUtily.of("GOLD_SWORD").makeItemStack());
		this.particlesCached.put("DRAGON_BREATH", CreateItemUtily.of("DRAGONS_BREATH").makeItemStack());
		this.particlesCached.put("ENDER_SIGNAL", CreateItemUtily.of("ENDER_PEARL").makeItemStack());
		this.particlesCached.put("EXPLOSION", CreateItemUtily.of(Material.TNT).makeItemStack());
		this.particlesCached.put("EXPLOSION_HUGE", CreateItemUtily.of("EXPLOSIVE_MINECART").makeItemStack());
		this.particlesCached.put("FLYING_GLYPH", CreateItemUtily.of("ENCHANTMENT_TABLE").makeItemStack());
		this.particlesCached.put("FIREWORKS_SPARK", CreateItemUtily.of("FIREWORK").makeItemStack());
		this.particlesCached.put("ITEM_BREAK", CreateItemUtily.of("NETHER_BRICK").makeItemStack());
		this.particlesCached.put("LARGE_SMOKE", CreateItemUtily.of("GREEN_WOOL").makeItemStack());
		this.particlesCached.put("LAVADRIP", CreateItemUtily.of("ORANGE_WOOL").makeItemStack());
		this.particlesCached.put("LAVA_POP", CreateItemUtily.of("LAVA_BUCKET").makeItemStack());
		this.particlesCached.put("MOBSPAWNER_FLAMES", CreateItemUtily.of("TORCH").makeItemStack());
		this.particlesCached.put("SNOWBALL_BREAK", CreateItemUtily.of("SNOW_BALL").makeItemStack());
		this.particlesCached.put("SNOW_SHOVEL", CreateItemUtily.of("SNOW_BALL").makeItemStack());
		this.particlesCached.put("SMOKE", CreateItemUtily.of("FURNACE").makeItemStack());
		this.particlesCached.put("NOTE", CreateItemUtily.of("NOTE_BLOCK").makeItemStack());

		ItemStack itemStack = CreateItemUtily.of("MONSTER_EGG").makeItemStack();
		SpawnEgg spawnEgg = (SpawnEgg) itemStack.getData();

		ItemStack itemStackGhast = itemStack.clone();
		if (spawnEgg != null) {
			spawnEgg.setSpawnedType(EntityType.GHAST);
			itemStackGhast = spawnEgg.toItemStack();
		}
		this.particlesCached.put("GHAST_SHOOT", itemStackGhast);
		this.particlesCached.put("GHAST_SHRIEK", itemStackGhast);

		ItemStack itemStackSlime = itemStack.clone();
		if (spawnEgg != null) {
			spawnEgg.setSpawnedType(EntityType.SLIME);
			itemStackSlime = spawnEgg.toItemStack();
		}
		this.particlesCached.put("SLIME", itemStackSlime);


		ItemStack itemStackWitch = itemStack.clone();
		if (spawnEgg != null) {
			spawnEgg.setSpawnedType(EntityType.WITCH);
			itemStackWitch = spawnEgg.toItemStack();
		}

		this.particlesCached.put("MAGIC_CRIT", itemStackWitch);
		this.particlesCached.put("INSTANT_SPELL", itemStackWitch);
		this.particlesCached.put("WITCH_MAGIC", itemStackWitch);
		this.particlesCached.put("SPELL_MOB_AMBIENT", itemStackWitch);
		this.particlesCached.put("SPELL_WITCH", itemStackWitch);
		ItemStack itemStackLlama = itemStack.clone();
		if (spawnEgg != null && version.newerThan(Version.v1_9)) {
			spawnEgg.setSpawnedType(EntityType.LLAMA);
			itemStackLlama = spawnEgg.toItemStack();
		}

		this.particlesCached.put("SPIT", itemStackLlama);
		this.particlesCached.put("SQUID_INK", CreateItemUtily.of("INK_SACK").makeItemStack());
		ItemStack itemStackVillager = itemStack.clone();
		if (spawnEgg != null) {
			spawnEgg.setSpawnedType(EntityType.VILLAGER);
			itemStackVillager = spawnEgg.toItemStack();
		}
		this.particlesCached.put("TOTEM", CreateItemUtily.of("TOTEM").makeItemStack());
		this.particlesCached.put("TOWN_AURA", CreateItemUtily.of("MYCEL").makeItemStack());
		this.particlesCached.put("VILLAGER_THUNDERCLOUD", itemStackVillager);
		this.particlesCached.put("HAPPY_VILLAGER", itemStackVillager);

	}

	private void itemsAddedIn1_13() {
		this.particlesCached.put("BLOCK_CRACK", CreateItemUtily.of(Material.CRACKED_STONE_BRICKS).makeItemStack());
		this.particlesCached.put("CLOUD", CreateItemUtily.of(Material.WHITE_WOOL).makeItemStack());
		this.particlesCached.put("CURRENT_DOWN", CreateItemUtily.of(Material.BUBBLE_CORAL).makeItemStack());
		this.particlesCached.put("DAMAGE_INDICATOR", CreateItemUtily.of(Material.GOLDEN_SWORD).makeItemStack());
		this.particlesCached.put("DRAGON_BREATH", CreateItemUtily.of(Material.DRAGON_BREATH).makeItemStack());
		this.particlesCached.put("DOLPHIN", CreateItemUtily.of(Material.DOLPHIN_SPAWN_EGG).makeItemStack());
		this.particlesCached.put("ENCHANTMENT_TABLE", CreateItemUtily.of(Material.ENCHANTING_TABLE).makeItemStack());
		this.particlesCached.put("EXPLOSION_NORMAL", CreateItemUtily.of(Material.TNT).makeItemStack());
		this.particlesCached.put("EXPLOSION_HUGE", CreateItemUtily.of(Material.TNT_MINECART).makeItemStack());
		this.particlesCached.put("FIREWORKS_SPARK", CreateItemUtily.of(Material.FIREWORK_ROCKET).makeItemStack());
		this.particlesCached.put("ITEM_CRACK", CreateItemUtily.of(Material.CRACKED_NETHER_BRICKS).makeItemStack());
		this.particlesCached.put("NAUTILUS", CreateItemUtily.of(Material.CONDUIT).makeItemStack());
		this.particlesCached.put("END_ROD", CreateItemUtily.of(Material.END_ROD).makeItemStack());
		this.particlesCached.put("REVERSE_PORTAL", CreateItemUtily.of(Material.END_PORTAL_FRAME).makeItemStack());
		this.particlesCached.put("SNOWBALL", CreateItemUtily.of(Material.SNOWBALL).makeItemStack());
		this.particlesCached.put("SNOWFLAKE", CreateItemUtily.of(Material.SNOWBALL).makeItemStack());
		this.particlesCached.put("SPELL", CreateItemUtily.of(Material.WITCH_SPAWN_EGG).makeItemStack());
		this.particlesCached.put("SPELL_INSTANT", CreateItemUtily.of(Material.WITCH_SPAWN_EGG).makeItemStack());
		this.particlesCached.put("SPELL_MOB", CreateItemUtily.of(Material.WITCH_SPAWN_EGG).makeItemStack());
		this.particlesCached.put("SPELL_MOB_AMBIENT", CreateItemUtily.of(Material.WITCH_SPAWN_EGG).makeItemStack());
		this.particlesCached.put("SPELL_WITCH", CreateItemUtily.of(Material.WITCH_SPAWN_EGG).makeItemStack());
		this.particlesCached.put("SPIT", CreateItemUtily.of(Material.LLAMA_SPAWN_EGG).makeItemStack());
		this.particlesCached.put("SQUID_INK", CreateItemUtily.of(Material.INK_SAC).makeItemStack());
		this.particlesCached.put("TOTEM", CreateItemUtily.of(Material.TOTEM_OF_UNDYING).makeItemStack());
		this.particlesCached.put("TOWN_AURA", CreateItemUtily.of(Material.MYCELIUM).makeItemStack());
		this.particlesCached.put("VILLAGER_ANGRY", CreateItemUtily.of(Material.VILLAGER_SPAWN_EGG).makeItemStack());
		this.particlesCached.put("VILLAGER_HAPPY", CreateItemUtily.of(Material.VILLAGER_SPAWN_EGG).makeItemStack());
	}

	private void itemsAddedIn1_14() {
		this.particlesCached.put("CAMPFIRE_COSY_SMOKE", CreateItemUtily.of(Material.CAMPFIRE).makeItemStack());
		this.particlesCached.put("CAMPFIRE_SIGNAL_SMOKE", CreateItemUtily.of(Material.CAMPFIRE).makeItemStack());
		this.particlesCached.put("COMPOSTER", CreateItemUtily.of(Material.COMPOSTER).makeItemStack());
		this.particlesCached.put("DRIPPING_HONEY", CreateItemUtily.of(Material.BEE_NEST).makeItemStack());
		this.particlesCached.put("SMOKE_LARGE", CreateItemUtily.of(Material.CAMPFIRE).makeItemStack());
		this.particlesCached.put("SMOKE_NORMAL", CreateItemUtily.of(Material.CAMPFIRE).makeItemStack());
		this.particlesCached.put("SNEEZE", CreateItemUtily.of(Material.PANDA_SPAWN_EGG).makeItemStack());
	}

	private void itemsAddedIn1_15() {
		this.particlesCached.put("FALLING_NECTAR", CreateItemUtily.of(Material.HONEYCOMB).makeItemStack());
		this.particlesCached.put("LANDING_HONEY", CreateItemUtily.of(Material.HONEYCOMB).makeItemStack());
		this.particlesCached.put("FALLING_HONEY", CreateItemUtily.of(Material.HONEY_BOTTLE).makeItemStack());
	}

	private void itemsAddedIn1_16() {
		this.particlesCached.put("LANDING_OBSIDIAN_TEAR", CreateItemUtily.of(Material.CRYING_OBSIDIAN).makeItemStack());
		this.particlesCached.put("SOUL_FIRE_FLAME", CreateItemUtily.of(Material.SOUL_TORCH).makeItemStack());
		this.particlesCached.put("WARPED_SPORE", CreateItemUtily.of(Material.WARPED_HYPHAE).makeItemStack());
	}

	private void itemsAddedIn1_17() {
		this.particlesCached.put("BLOCK_MARKER", CreateItemUtily.of(Material.SHIELD).makeItemStack());
		this.particlesCached.put("DUST_COLOR_TRANSITION", CreateItemUtily.of(Material.REDSTONE).makeItemStack());
		this.particlesCached.put("DRIPPING_DRIPSTONE_LAVA", CreateItemUtily.of(Material.POINTED_DRIPSTONE).makeItemStack());
		this.particlesCached.put("DRIPPING_DRIPSTONE_WATER", CreateItemUtily.of(Material.POINTED_DRIPSTONE).makeItemStack());
		this.particlesCached.put("FALLING_DRIPSTONE_LAVA", CreateItemUtily.of(Material.POINTED_DRIPSTONE).makeItemStack());
		this.particlesCached.put("FALLING_DRIPSTONE_WATER", CreateItemUtily.of(Material.POINTED_DRIPSTONE).makeItemStack());
		this.particlesCached.put("FALLING_SPORE_BLOSSOM", CreateItemUtily.of(Material.SPORE_BLOSSOM).makeItemStack());
		this.particlesCached.put("ELECTRIC_SPARK", CreateItemUtily.of(Material.LIGHTNING_ROD).makeItemStack());
		this.particlesCached.put("GLOW_SQUID_INK", CreateItemUtily.of(Material.GLOW_INK_SAC).makeItemStack());
		this.particlesCached.put("SCRAPE", CreateItemUtily.of(Material.COPPER_BLOCK).makeItemStack());
		this.particlesCached.put("SPORE_BLOSSOM_AIR", CreateItemUtily.of(Material.SPORE_BLOSSOM).makeItemStack());
		this.particlesCached.put("VIBRATION", CreateItemUtily.of(Material.SCULK_SENSOR).makeItemStack());
		this.particlesCached.put("WAX_OFF", CreateItemUtily.of(Material.HONEYCOMB).makeItemStack());
		this.particlesCached.put("WAX_ON", CreateItemUtily.of(Material.HONEYCOMB).makeItemStack());

	}

	public ItemStack checkParticleList(final Object particle) {
		String name = "";
		if (version.atLeast(Version.v1_9))
			name = particle +"";
		if (particle instanceof Effect)
			name = ((Effect) particle).name();

		final ItemStack itemStack = this.particlesCached.get(name);
		if (itemStack != null)
			return itemStack.clone();
		if (version.atLeast(ServerVersion.Version.v1_13))
			return CreateItemUtily.of(Material.SMOOTH_STONE).makeItemStack().clone();
		else
			return CreateItemUtily.of("SMOOTH_STONE").makeItemStack().clone();
	}

	public boolean checkIfParticleOrEffect(final Object particle) {
		return (particlesUtility != null && particlesUtility.checkIfParticle(particle)) || particle instanceof Effect;
	}

	public boolean checkIfParticle(final Object particle) {
		if (particlesUtility == null) return false;
		return particlesUtility.checkIfParticle(particle);
	}

	public boolean checkParticleClass(final Object particle, Class<?> clazz) {
		if (particlesUtility == null) return false;
		return particlesUtility.checkParticleClass(particle, clazz);
	}

	public List<Object> getListOfParticles(final Collection<ParticleEffect> particles) {
		if (particlesUtility == null && version.atLeast(Version.v1_9)) {
			return particles.stream().map(effect -> effect.getSpigotParticle() != null ? effect.getSpigotParticle().getParticle(): "").collect(Collectors.toList());
		}
		return Collections.singletonList(particles.stream().map(effect -> effect.getEffect() != null ? effect.getEffect() : "").collect(Collectors.toList()));
	}

	enum particles {

		DUST_COLOR_TRANSITION,
		LEGACY_BLOCK_CRACK,
		LEGACY_BLOCK_DUST,
		LEGACY_FALLING_DUST,
		MOB_APPEARANCE,
	}
}
