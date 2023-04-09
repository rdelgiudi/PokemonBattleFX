package com.delgiudice.pokemonbattlefx;

import java.util.HashMap;

///Class which allows to describe a move
public class MoveTemplate {
    String name;
    private int power, accuracy, hits = 1, statUp = 0, maxpp;
    private float statusProb, statUpProb, recoil;
    private boolean priority = false, twoturn = false, lifesteal = false, self = false;
    private Enums.Subtypes subtype;
    private Type type;
    private Enums.StatType statType = null;
    private Enums.Status status = Enums.Status.NONE;
    private static HashMap<String , MoveTemplate> moveMap = new HashMap<>();

    public String getName() {
        return name;
    }

    public int getPower() {
        return power;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public int getMaxpp() {
        return maxpp;
    }

    public float getStatusProb() {
        return statusProb;
    }

    public float getStatUpProb() {
        return statUpProb;
    }

    public int getHits() {
        return hits;
    }

    public int getStatUp(){
        return statUp;}

    public Enums.Status getStatus() {
        return status;
    }

    public boolean isPriority() {
        return priority;
    }

    public boolean isTwoturn() {
        return twoturn;
    }

    public boolean isLifesteal() {
        return lifesteal;
    }

    public boolean isSelf() {
        return self;
    }

    public Enums.Subtypes getSubtype() {
        return subtype;
    }
    public Type getType() {
        return type;
    }
    public Enums.StatType getStatType() {
        return statType;}

    public static HashMap<String , MoveTemplate> getMoveMap() {
        return moveMap;
    }

    public float getRecoil() {
        return recoil;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public void setTwoturn(boolean twoturn) {
        this.twoturn = twoturn;
    }

    public void setLifesteal(boolean lifesteal) {
        this.lifesteal = lifesteal;
    }

    public void setRecoil(float recoil) {
        this.recoil = recoil;
    }

    public void setStatUpProb(float statUpProb) {
        this.statUpProb = statUpProb;
    }

    public void setStatus(Enums.Status status) {
        this.status = status;
    }

    public void setStatusProb(float prob) {
        this.statusProb = prob;
    }

    public MoveTemplate(String name, int power, int accuracy, int pp, Enums.Subtypes subtype, Type type,
                        Enums.StatType statType, int statUp, boolean self, float statUpProb) {
        this.name = name;
        this.power = power;
        this.accuracy = accuracy;
        this.maxpp = pp;
        this.subtype = subtype;
        this.type = type;
        this.statType = statType;
        this.statUp = statUp;
        this.self = self;
        this.statUpProb = statUpProb;
    }

    public MoveTemplate(String name, int power, int accuracy, int pp, Enums.Subtypes subtype, Type type) {
        this.name = name;
        this.power = power;
        this.accuracy = accuracy;
        this.maxpp = pp;
        this.subtype = subtype;
        this.statusProb = 0;
        this.lifesteal = false;
        this.hits = 1;
        this.priority = false;
        this.twoturn = false;
        this.type = type;
    }

    public MoveTemplate(MoveTemplate original) {
        this.name = original.name;
        this.power = original.power;
        this.accuracy = original.accuracy;
        this.maxpp = original.maxpp;
        this.statusProb = original.statusProb;
        this.hits = original.hits;
        this.statUp = original.statUp;
        this.priority = original.priority;
        this.twoturn = original.twoturn;
        this.lifesteal = original.lifesteal;
        this.subtype = original.subtype;
        this.type = original.type;
        this.statType = original.statType;
        this.self = original.self;
    }

    ///initializes list of available moves
    public static void setMoveList(){
        Type.setTypeList(); //init types
        MoveTemplate newmove = new MoveTemplate("Tackle", 40, 100, 35, Enums.Subtypes.PHYSICAL,
                Type.typeMap.get(Enums.Types.NORMAL));
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate("Growl", 0, 100, 40, Enums.Subtypes.STATUS,
                Type.typeMap.get(Enums.Types.NORMAL), Enums.StatType.ATTACK, -1, false, 1);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate("Vine Whip", 45, 100, 25, Enums.Subtypes.PHYSICAL,
                Type.typeMap.get(Enums.Types.GRASS));
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate("Scratch", 40, 100, 35, Enums.Subtypes.PHYSICAL,
                Type.typeMap.get(Enums.Types.NORMAL));
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate("Ember", 40, 100, 25, Enums.Subtypes.SPECIAL,
                Type.typeMap.get(Enums.Types.FIRE));
        newmove.setStatus(Enums.Status.BURNED);
        newmove.setStatusProb(0.1f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate("Tail Whip", 0, 100, 30, Enums.Subtypes.STATUS,
                Type.typeMap.get(Enums.Types.NORMAL), Enums.StatType.DEFENSE, -1, false, 1);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate("Quick Attack", 40, 100, 30, Enums.Subtypes.PHYSICAL,
                Type.typeMap.get(Enums.Types.NORMAL));
        newmove.setPriority(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate("Water Gun", 40, 100, 25, Enums.Subtypes.SPECIAL,
                Type.typeMap.get(Enums.Types.WATER));
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate("Double Kick", 30, 100, 30, Enums.Subtypes.PHYSICAL,
                Type.typeMap.get(Enums.Types.FIGHTING));
        newmove.setHits(2);
        moveMap.put(newmove.getName(), newmove);
    }

}
