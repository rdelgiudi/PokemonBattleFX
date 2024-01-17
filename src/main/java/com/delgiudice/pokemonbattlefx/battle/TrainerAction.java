package com.delgiudice.pokemonbattlefx.battle;

import com.delgiudice.pokemonbattlefx.attributes.Enums;

public class TrainerAction {

    public static String NO_ACTION_NAME = "-1";
    public static int NO_TARGET = -1;
    public Enums.ActionTypes actionType;
    // Defines name of the action, it can be one of the following data:
    //move index, pokemon index (swapping), item name
    public String actionName;
    // Defines the target of the action, used mainly to determine item use target
    public int target = NO_TARGET;
    // Target move of pp restoring, for items that restore pp of only a single move
    public int ppRestoreTarget = NO_TARGET;

    public TrainerAction(Enums.ActionTypes actionType, String actionName) {
        this.actionType = actionType;
        this.actionName = actionName;
    }

    public TrainerAction(Enums.ActionTypes actionType, String actionName, int target) {
        this.actionType = actionType;
        this.actionName = actionName;
        this.target = target;
    }

    public TrainerAction(Enums.ActionTypes actionType, String actionName, int target, int ppRestoreTarget) {
        this.actionType = actionType;
        this.actionName = actionName;
        this.target = target;
        this.ppRestoreTarget = ppRestoreTarget;
    }
}
