package com.snapvisual.aim.module;

public enum Category {
    COMBAT("Бой"),
    VISUAL("Визуал"),
    MOVEMENT("Движение"),
    PLAYER("Игрок");

    private final String displayName;
    Category(String displayName) { this.displayName = displayName; }
    public String getDisplayName() { return displayName; }
}
