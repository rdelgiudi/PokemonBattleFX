package com.delgiudice.pokemonbattlefx;

import java.util.HashMap;

///Class which allows to describe a move
public class MoveTemplate {
    String name;
    private int power, accuracy, hits = 1, statUp = 0, maxpp, critIncrease = 0, critTemporaryIncrease = 0;
    private float statusProb, statUpProb, recoil = 0, lifesteal = 0, hpRestore = 0;
    private boolean priority = false, twoturn = false, self = false, trap = false, charging = false, multiturn = false;
    private Enums.Subtypes subtype;
    private Type type;
    private Enums.StatType statType = null;
    private Enums.Status status = Enums.Status.NONE;

    private Enums.SubStatus subStatus = Enums.SubStatus.NONE;
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

    public Enums.SubStatus getSubStatus() {
        return subStatus;
    }

    public boolean isMultiturn() {
        return multiturn;
    }

    public float getHpRestore() {
        return hpRestore;
    }

    public boolean isCharging() {
        return charging;
    }

    public boolean isPriority() {
        return priority;
    }

    public boolean isTwoturn() {
        return twoturn;
    }

    public float getLifesteal() {
        return lifesteal;
    }

    public boolean isTrap() {
        return trap;
    }

    public int getCritIncrease() {
        return critIncrease;
    }

    public int getCritTemporaryIncrease() {
        return critTemporaryIncrease;
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

    public void setLifesteal(float lifesteal) {
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

    public void setTrap(boolean trap) {
        this.trap = trap;
    }

    public void setCharging(boolean charging) {
        this.charging = charging;
    }

    public void setHpRestore(float hpRestore) {
        this.hpRestore = hpRestore;
    }

    public void setCritIncrease(int critIncrease) {
        this.critIncrease = critIncrease;
    }

    public void setCritTemporaryIncrease(int critTemporaryIncrease) {
        this.critTemporaryIncrease = critTemporaryIncrease;
    }

    public void setMultiturn(boolean multiturn) {
        this.multiturn = multiturn;
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
        this.lifesteal = 0;
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
    public static void setMoveMap(){
        Type.setTypeList(); //init types

        MoveTemplate newmove = new MoveTemplate("Confusion Damage", 40, 0, 0, Enums.Subtypes.PHYSICAL,
                Type.typeMap.get(Enums.Types.NOTYPE));
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate("Tackle", 40, 100, 35, Enums.Subtypes.PHYSICAL,
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

        newmove = new MoveTemplate("Slash", 70, 100, 20, Enums.Subtypes.PHYSICAL,
                Type.typeMap.get(Enums.Types.NORMAL));
        newmove.setCritTemporaryIncrease(1);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate("Dragon Breath", 60, 100, 20, Enums.Subtypes.SPECIAL,
                Type.typeMap.get(Enums.Types.DRAGON));
        newmove.setStatus(Enums.Status.PARALYZED);
        newmove.setStatUpProb(0.3f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate("Flare Blitz", 120, 100, 15, Enums.Subtypes.PHYSICAL,
                Type.typeMap.get(Enums.Types.FIRE));
        newmove.setRecoil(1.0f/3.0f);
        newmove.setStatus(Enums.Status.BURNED);
        newmove.setStatUpProb(0.1f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate("Fire Spin", 35, 85, 15, Enums.Subtypes.SPECIAL,
                Type.typeMap.get(Enums.Types.FIRE));
        newmove.setTrap(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate("Dig", 80, 100, 10, Enums.Subtypes.PHYSICAL,
                Type.typeMap.get(Enums.Types.GROUND));
        newmove.setTwoturn(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate("Razor Leaf", 55, 95, 25, Enums.Subtypes.PHYSICAL,
                Type.typeMap.get(Enums.Types.GRASS));
        newmove.setCritTemporaryIncrease(1);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate("Solar Beam", 120, 100, 10, Enums.Subtypes.SPECIAL,
                Type.typeMap.get(Enums.Types.GRASS));
        newmove.setTwoturn(true);
        newmove.setCharging(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate("Double-Edge", 120, 100, 15, Enums.Subtypes.PHYSICAL,
                Type.typeMap.get(Enums.Types.NORMAL));
        newmove.setRecoil(1.0f/3.0f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate("Sleep Powder", 0, 75, 15, Enums.Subtypes.STATUS,
                Type.typeMap.get(Enums.Types.GRASS));
        newmove.setStatus(Enums.Status.SLEEPING);
        newmove.setStatusProb(1.0f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate("Seed Bomb", 80, 100, 15, Enums.Subtypes.PHYSICAL,
                Type.typeMap.get(Enums.Types.GRASS));
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate("Synthesis", 0, 0, 5, Enums.Subtypes.STATUS,
                Type.typeMap.get(Enums.Types.GRASS));
        newmove.setHpRestore(0.5f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate("Sweet Scent", 0, 100, 20, Enums.Subtypes.STATUS,
                Type.typeMap.get(Enums.Types.NORMAL), Enums.StatType.EVASIVENESS, -1, false, 1);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate("Outrage", 120, 100, 10, Enums.Subtypes.PHYSICAL,
                Type.typeMap.get(Enums.Types.DRAGON));
        newmove.setMultiturn(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate("Petal Dance", 120, 100, 10, Enums.Subtypes.SPECIAL,
                Type.typeMap.get(Enums.Types.GRASS));
        newmove.setMultiturn(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate("Petal Blizzard", 90, 100, 15, Enums.Subtypes.PHYSICAL,
                Type.typeMap.get(Enums.Types.GRASS));
        moveMap.put(newmove.getName(), newmove);

    }

}
