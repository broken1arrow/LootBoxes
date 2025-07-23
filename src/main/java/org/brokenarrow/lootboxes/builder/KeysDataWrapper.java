package org.brokenarrow.lootboxes.builder;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KeysDataWrapper {
  private final KeysData keysData;

  public KeysDataWrapper(KeysData keysData) {
    this.keysData = keysData;
  }

  public KeysDataWrapper setKeyName(@NotNull final String keyName) {
    this.keysData.setKeyName(keyName);
    return this;
  }

  public KeysDataWrapper setDisplayName(@NotNull final String displayName) {
    this.keysData.setDisplayName(displayName);
    return this;
  }

  public KeysDataWrapper setLootTableLinked(@NotNull final String lootTableLinked) {
    this.keysData.setLootTableLinked(lootTableLinked);
    return this;
  }

  public KeysDataWrapper setAmountNeeded(@NotNull final int amountNeeded) {
    this.keysData.setAmountNeeded(amountNeeded);
    return this;
  }

  public KeysDataWrapper setItemType(@NotNull final Material itemType) {
    this.keysData.setItemType(itemType);
    return this;
  }

  public KeysDataWrapper setLore(@NotNull final List<String> lore) {
    this.keysData.setLore(lore);
    return this;
  }
}
