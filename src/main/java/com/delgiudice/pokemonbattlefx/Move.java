package com.delgiudice.pokemonbattlefx;

import java.util.HashMap;

///Class which allows to describe a move
public class Move {
    String name;
    private byte power, accuracy, pp, statusProb = 0, hits = 1, statUp = 0, maxpp;
    private boolean priority = false, twoturn = false, lifesteal;
    private Enums.Subtypes subtype;
    private Type type;
    private Enums.StatusType statusType = null;
    private static HashMap<String ,Move> moveList = new HashMap<>();

    public String getName() {
        return name;
    }

    public byte getPower() {
        return power;
    }

    public byte getAccuracy() {
        return accuracy;
    }

    public byte getPp() {
        return pp;
    }

    public byte getMaxpp() {
        return maxpp;
    }

    public byte getStatusProb() {
        return statusProb;
    }

    public byte getHits() {
        return hits;
    }

    public byte getStatUp(){
        return statUp;}

    public boolean isPriority() {
        return priority;
    }

    public boolean isTwoturn() {
        return twoturn;
    }

    public boolean isLifesteal() {
        return lifesteal;
    }

    public Enums.Subtypes getSubtype() {
        return subtype;
    }
    public Type getType() {
        return type;
    }
    public Enums.StatusType getStatusType() {
        return statusType;}

    public static HashMap<String ,Move> getMoveList() {
        return moveList;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    public void setPp(byte pp) {
        this.pp = pp;
    }

    public Move(String name, byte power, byte accuracy, byte pp, Enums.Subtypes subtype, Type type,
                byte statusProb, boolean lifesteal, byte hits, boolean priority, boolean twoturn) {
        this.name = name;
        this.power = power;
        this.accuracy = accuracy;
        this.pp = pp;
        this.maxpp = pp;
        this.subtype = subtype;
        this.statusProb = statusProb;
        this.lifesteal = lifesteal;
        this.hits = hits;
        this.priority = priority;
        this.twoturn = twoturn;
        this.type = type;
    }

    public Move(String name, byte power, byte accuracy, byte pp, Enums.Subtypes subtype, Type type, Enums.StatusType statusType, byte statUp) {
        this.name = name;
        this.power = power;
        this.accuracy = accuracy;
        this.pp = pp;
        this.maxpp = pp;
        this.subtype = subtype;
        this.type = type;
        this.statusType = statusType;
        this.statUp = statUp;
    }

    public Move(String name, byte power, byte accuracy, byte pp, Enums.Subtypes subtype, Type type) {
        this.name = name;
        this.power = power;
        this.accuracy = accuracy;
        this.pp = pp;
        this.maxpp = pp;
        this.subtype = subtype;
        this.statusProb = 0;
        this.lifesteal = false;
        this.hits = 0;
        this.priority = false;
        this.twoturn = false;
        this.type = type;
    }

    public Move(Move original) {
        this.name = original.name;
        this.power = original.power;
        this.accuracy = original.accuracy;
        this.maxpp = original.maxpp;
        this.pp = this.maxpp;
        this.statusProb = original.statusProb;
        this.hits = original.hits;
        this.statUp = original.statUp;
        this.priority = original.priority;
        this.twoturn = original.twoturn;
        this.lifesteal = original.lifesteal;
        this.subtype = original.subtype;
        this.type = original.type;
        this.statusType = original.statusType;
    }

    ///initializes list of available moves
    public static void setMoveList(){
        Type.setTypeList(); //init types
        Move newmove = new Move("Tackle", (byte)40, (byte)100, (byte)35, Enums.Subtypes.PHYSICAL, Type.typeList.get(0));
        moveList.put(newmove.getName(), newmove);
        newmove = new Move("Growl", (byte)0, (byte)100, (byte)40, Enums.Subtypes.STATUS, Type.typeList.get(0), Enums.StatusType.ATTACK, (byte)-1);
        moveList.put(newmove.getName(), newmove);
        newmove = new Move("Vine Whip", (byte)45, (byte)100, (byte)25, Enums.Subtypes.PHYSICAL, Type.typeList.get(4));
        moveList.put(newmove.getName(), newmove);
        newmove = new Move("Scratch", (byte)40, (byte)100, (byte)35, Enums.Subtypes.PHYSICAL, Type.typeList.get(0));
        moveList.put(newmove.getName(), newmove);
        newmove = new Move("Ember", (byte)(40), (byte)100, (byte)25, Enums.Subtypes.SPECIAL, Type.typeList.get(1));
        moveList.put(newmove.getName(), newmove);
        newmove = new Move("Tail Whip", (byte)0, (byte)100, (byte)30, Enums.Subtypes.STATUS, Type.typeList.get(0), Enums.StatusType.DEFENSE, (byte)-1);
        moveList.put(newmove.getName(), newmove);
        newmove = new Move("Quick Attack", (byte)40, (byte)100, (byte)30, Enums.Subtypes.PHYSICAL, Type.typeList.get(0));
        newmove.setPriority(true);
        moveList.put(newmove.getName(), newmove);
    }

}
