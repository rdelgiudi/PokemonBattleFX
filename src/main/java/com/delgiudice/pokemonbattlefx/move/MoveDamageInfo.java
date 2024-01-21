package com.delgiudice.pokemonbattlefx.move;

import com.delgiudice.pokemonbattlefx.attributes.Type;
import com.delgiudice.pokemonbattlefx.battle.BattleLogic;

/**
 * Data container class which holds information about calculated move damage and other information.
 */
public class MoveDamageInfo {
    public static final int NOT_APPLICABLE = -1;
    /**
     * Damage dealt.
     */
    public int damage;
    /**
     * If the move was a critical hit, set this value to <code>true</code>.
     */
    public boolean critical;
    /**
     * Type effect multiplier.
     * @see BattleLogic#calculateTypeEffect(Move, Type, Type)
     */
    public float typeEffect;

    public MoveDamageInfo(int damage, boolean critical, float typeEffect) {
        this.damage = damage;
        this.critical = critical;
        this.typeEffect = typeEffect;
    }
}
