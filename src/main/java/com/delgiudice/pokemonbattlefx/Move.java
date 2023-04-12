package com.delgiudice.pokemonbattlefx;

import java.util.List;

public class Move {

    private MoveTemplate template;

    private int pp;

    public MoveEnum getName() {
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

    public int getStatChange(){
        return template.getStatChange();}

    public int getPp(){
        return pp;
    }

    public Enums.Subtypes getSubtype() {
        return template.getSubtype();
    }
    public Type getType() {
        return template.getType();
    }
    public List<Enums.StatType> getStatTypes() {
        return template.getStatTypes();}

    public float getStatChangeProb() {
        return template.getStatChangeProb();
    }

    public Enums.Status getStatus() {
        return template.getStatus();
    }

    public Enums.SubStatus getSubStatus() {
        return template.getSubStatus();
    }

    public MoveTemplate getTemplate() {
        return template;
    }

    public int getCritIncrease() {
        return template.getCritIncrease();
    }

    public float getHpRestore() {
        return template.getHpRestore();
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

    public boolean isTrap() {
        return template.isTrap();
    }

    public boolean isMultiturn() {
        return template.isMultiturn();
    }

    public float getLifesteal() {
        return template.getLifesteal();
    }

    public boolean isSelf() {
        return template.isSelf();
    }

    public float getRecoil() {
        return template.getRecoil();
    }

    public boolean isRecoilHpDamage() {
        return template.isRecoilUserHp();
    }

    public Move(MoveTemplate template) {
        this.template = template;
        pp = template.getMaxpp();
    }
}
