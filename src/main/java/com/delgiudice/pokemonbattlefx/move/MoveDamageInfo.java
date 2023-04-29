package com.delgiudice.pokemonbattlefx.move;

public class MoveDamageInfo {
    public static final int NOT_APPLICABLE = -1;
    public int damage;
    public boolean critical;
    public float typeEffect;

    public MoveDamageInfo(int damage, boolean critical, float typeEffect) {
        this.damage = damage;
        this.critical = critical;
        this.typeEffect = typeEffect;
    }
}
