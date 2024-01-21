package com.delgiudice.pokemonbattlefx.battle;

import com.delgiudice.pokemonbattlefx.attributes.Enums;

/**
 * Data container class which holds the move that a Trainer wants to perform this turn
 */
public class TrainerAction {

    public static String NO_ACTION_NAME = "-1";
    public static int NO_TARGET = -1;
    /**
     * Defines type of action to be performed by Trainer.
     */
    public Enums.ActionTypes actionType;
    /**
     *  Defines name of the action, it can be one of the following data: move index, Pokémon index (swapping), item name.
     */
    public String actionName;
    /**
     * Defines the target of the action, used mainly to determine item use target.
     */
    public int target = NO_TARGET;
    /**
     * Target move of PP restoring, for items that restore PP of only a single move.
     */
    public int ppRestoreTarget = NO_TARGET;

    /**
     * Constructor for containing the next move used by Pokémon information.
     */
    public TrainerAction(Enums.ActionTypes actionType, String actionName) {
        this.actionType = actionType;
        this.actionName = actionName;
    }

    /**
     * Constructor for containing either a Pokémon swap or an item use information.
     */
    public TrainerAction(Enums.ActionTypes actionType, String actionName, int target) {
        this.actionType = actionType;
        this.actionName = actionName;
        this.target = target;
    }

    /**
     * Constructor for containing single move PP restore item information.
     */
    public TrainerAction(Enums.ActionTypes actionType, String actionName, int target, int ppRestoreTarget) {
        this.actionType = actionType;
        this.actionName = actionName;
        this.target = target;
        this.ppRestoreTarget = ppRestoreTarget;
    }
}
