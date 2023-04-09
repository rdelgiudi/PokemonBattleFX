package com.delgiudice.pokemonbattlefx;

public class Move {

    private MoveTemplate template;

    private int pp;

    public String getName() {
        return template.getName();
    }

    public int getPower() {
        return template.getPower();
    }

    public int getAccuracy() {
        return template.getAccuracy();
    }

    public int getMaxpp() {
        return template.getMaxpp();
    }

    public float getStatusProb() {
        return template.getStatusProb();
    }

    public int getHits() {
        return template.getHits();
    }

    public int getStatUp(){
        return template.getStatUp();}

    public int getPp(){
        return pp;
    }

    public Enums.Subtypes getSubtype() {
        return template.getSubtype();
    }
    public Type getType() {
        return template.getType();
    }
    public Enums.StatType getStatType() {
        return template.getStatType();}

    public float getStatUpProb() {
        return template.getStatUpProb();
    }

    public Enums.Status getStatus() {
        return template.getStatus();
    }

    public MoveTemplate getTemplate() {
        return template;
    }

    public int getCritIncrease() {
        return template.getCritIncrease();
    }

    public boolean isCharging() {
        return template.isCharging();
    }

    public void setPp(int pp) {
        this.pp = pp;
    }

    public boolean isPriority() {
        return template.isPriority();
    }

    public boolean isTwoturn() {
        return template.isTwoturn();
    }

    public float getLifesteal() {
        return template.getLifesteal();
    }

    public boolean isSelf() {
        return template.isSelf();
    }

    public Move(MoveTemplate template) {
        this.template = template;
        pp = template.getMaxpp();
    }
}
