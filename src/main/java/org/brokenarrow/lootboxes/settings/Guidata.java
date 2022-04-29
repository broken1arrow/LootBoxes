package org.brokenarrow.lootboxes.settings;

import java.util.List;

public final class Guidata {
	private final int menuSize;
	private final String menuTitle;
	private final String menuFillSpace;
	private final int menuMaxAmountOfItems;
	private final boolean glow;
	private final String displayname;
	private final String slot;
	private final String icon;
	private final List<String> lore;

	private Guidata(Builder builder) {
		this.menuSize = builder.menuSize;
		this.menuTitle = builder.menuTitle;
		this.menuFillSpace = builder.menuFillSpace;
		this.menuMaxAmountOfItems = builder.menuMaxAmountOfItems;
		this.glow = builder.glow;
		this.displayname = builder.displayname;
		this.slot = builder.slot;
		this.icon = builder.icon;
		this.lore = builder.lore;
	}

	public int getMenuSize() {
		return menuSize;
	}

	public String getMenuTitle() {
		return menuTitle;
	}

	public String getMenuFillSpace() {
		return menuFillSpace;
	}

	public int getMenuMaxAmountOfItems() {
		return menuMaxAmountOfItems;
	}

	public boolean isGlow() {
		return glow;
	}

	public String getDisplayname() {
		return displayname;
	}

	public String getSlot() {
		return slot;
	}

	public String getIcon() {
		return icon;
	}

	public List<String> getLore() {
		return lore;
	}

	public static class Builder {
		private int menuSize;
		private String menuTitle = "";
		private String menuFillSpace;
		private int menuMaxAmountOfItems;
		private boolean glow;
		private String displayname = "";
		private String slot;
		private String icon = "";
		private List<String> lore;

		public Builder setMenuSize(int menuSize) {
			this.menuSize = menuSize;
			return this;
		}

		public Builder setMenuTitle(String menuTitle) {
			this.menuTitle = menuTitle;
			return this;
		}

		public Builder setMenuFillSpace(String menuFillSpace) {
			this.menuFillSpace = menuFillSpace;
			return this;
		}

		public Builder setMenuMaxAmountOfItems(int menuMaxAmountOfItems) {
			this.menuMaxAmountOfItems = menuMaxAmountOfItems;
			return this;
		}

		public Builder setGlow(boolean glow) {
			this.glow = glow;
			return this;
		}

		public Builder setDisplayname(String displayname) {
			this.displayname = displayname;
			return this;
		}

		public Builder setSlot(String slot) {
			this.slot = slot;
			return this;
		}

		public Builder setIcon(String icon) {
			this.icon = icon;
			return this;
		}

		public Builder setLore(List<String> lore) {
			this.lore = lore;
			return this;
		}

		public Guidata build() {
			return new Guidata(this);
		}
	}
}