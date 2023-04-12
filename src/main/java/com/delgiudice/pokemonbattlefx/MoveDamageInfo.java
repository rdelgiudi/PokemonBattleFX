package com.delgiudice.pokemonbattlefx;

public record MoveDamageInfo(int damage, boolean critical, float typeEffect) {
    public static final int NOT_APPLICABLE = -1;
}
