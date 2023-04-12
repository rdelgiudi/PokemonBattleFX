package com.delgiudice.pokemonbattlefx;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

///Class which allows to describe a move
public class MoveTemplate {

    private static final HashMap<MoveEnum, MoveTemplate> moveMap = new HashMap<>();

    MoveEnum name;
    private final int power, accuracy;
    private int maxpp, hits = 1, statChange = 0, critIncrease = 0, critTemporaryIncrease = 0;
    private float statusProb = 0, statChangeProb = 0, recoil = 0, lifesteal = 0, hpRestore = 0;
    private boolean priority = false, twoturn = false, self = false, trap = false, charging = false, multiturn = false,
                    recoilUserHp = false;
    private final Enums.Subtypes subtype;
    private final Type type;
    private List<Enums.StatType> statTypes = new LinkedList<>();
    private Enums.Status status = Enums.Status.NONE;
    private Enums.SubStatus subStatus = Enums.SubStatus.NONE;

    public MoveEnum getName() {
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

    public float getStatChangeProb() {
        return statChangeProb;
    }

    public int getHits() {
        return hits;
    }

    public int getStatChange(){
        return statChange;}

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
    public List<Enums.StatType> getStatTypes() {
        return statTypes;}

    public static HashMap<MoveEnum , MoveTemplate> getMoveMap() {
        return moveMap;
    }

    public float getRecoil() {
        return recoil;
    }

    public boolean isRecoilUserHp() {
        return recoilUserHp;
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

    public void setRecoilUserHp(boolean recoilUserHp) {
        this.recoilUserHp = recoilUserHp;
    }

    public void setStatChangeProb(float statChangeProb) {
        this.statChangeProb = statChangeProb;
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

    public void setSubStatus(Enums.SubStatus subStatus) {
        this.subStatus = subStatus;
    }

    public void setMultiturn(boolean multiturn) {
        this.multiturn = multiturn;
    }

    public MoveTemplate(MoveEnum name, int power, int accuracy, int pp, Enums.Subtypes subtype, Type type,
                        Enums.StatType statTypes, int statChange, boolean self, float statChangeProb) {
        this.name = name;
        this.power = power;
        this.accuracy = accuracy;
        this.maxpp = pp;
        this.subtype = subtype;
        this.type = type;
        this.statTypes.add(statTypes);
        this.statChange = statChange;
        this.self = self;
        this.statChangeProb = statChangeProb;
    }

    public MoveTemplate(MoveEnum name, int power, int accuracy, int pp, Enums.Subtypes subtype, Type type) {
        this.name = name;
        this.power = power;
        this.accuracy = accuracy;
        this.maxpp = pp;
        this.subtype = subtype;
        this.type = type;
    }

    public MoveTemplate(MoveTemplate original) {
        this.name = original.name;
        this.power = original.power;
        this.accuracy = original.accuracy;
        this.maxpp = original.maxpp;
        this.statusProb = original.statusProb;
        this.hits = original.hits;
        this.statChange = original.statChange;
        this.priority = original.priority;
        this.twoturn = original.twoturn;
        this.lifesteal = original.lifesteal;
        this.subtype = original.subtype;
        this.type = original.type;
        this.statTypes = original.statTypes;
        this.self = original.self;
    }

    ///initializes list of available moves
    public static void setMoveMap(){
        Type.setTypeList(); //init types

        //This is not a move, it should only be used to process confusion damage
        MoveTemplate newmove = new MoveTemplate(MoveEnum.CONFUSION_DAMAGE, 40, 0, 1, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.NO_TYPE));
        moveMap.put(newmove.getName(), newmove);

        //Struggle, used only when all moves are out of PP
        newmove = new MoveTemplate(MoveEnum.STRUGGLE, 50, 0, 1, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.NO_TYPE));
        newmove.setRecoil(1/4f);
        newmove.setRecoilUserHp(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.TACKLE, 40, 100, 35, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.NORMAL));
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.GROWL, 0, 100, 40, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.NORMAL), Enums.StatType.ATTACK, -1, false, 1);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.VINE_WHIP, 45, 100, 25, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.GRASS));
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SCRATCH, 40, 100, 35, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.NORMAL));
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.EMBER, 40, 100, 25, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.FIRE));
        newmove.setStatus(Enums.Status.BURNED);
        newmove.setStatusProb(0.1f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.TAIL_WHIP, 0, 100, 30, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.NORMAL), Enums.StatType.DEFENSE, -1, false, 1);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.QUICK_ATTACK, 40, 100, 30, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.NORMAL));
        newmove.setPriority(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.WATER_GUN, 40, 100, 25, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.WATER));
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.DOUBLE_KICK, 30, 100, 30, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.FIGHTING));
        newmove.setHits(2);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SLASH, 70, 100, 20, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.NORMAL));
        newmove.setCritTemporaryIncrease(1);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.DRAGON_BREATH, 60, 100, 20, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.DRAGON));
        newmove.setStatus(Enums.Status.PARALYZED);
        newmove.setStatusProb(0.3f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.FLARE_BLITZ, 120, 100, 15, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.FIRE));
        newmove.setRecoil(1.0f/3.0f);
        newmove.setStatus(Enums.Status.BURNED);
        newmove.setStatChangeProb(0.1f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.FIRE_SPIN, 35, 85, 15, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.FIRE));
        newmove.setTrap(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.DIG, 80, 100, 10, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.GROUND));
        newmove.setTwoturn(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.RAZOR_LEAF, 55, 95, 25, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.GRASS));
        newmove.setCritTemporaryIncrease(1);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SOLAR_BEAM, 120, 100, 10, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.GRASS));
        newmove.setTwoturn(true);
        newmove.setCharging(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.DOUBLE_EDGE, 120, 100, 15, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.NORMAL));
        newmove.setRecoil(1.0f/3.0f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SLEEP_POWDER, 0, 75, 15, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.GRASS));
        newmove.setStatus(Enums.Status.SLEEPING);
        newmove.setStatusProb(1.0f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SEED_BOMB, 80, 100, 15, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.GRASS));
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SYNTHESIS, 0, 0, 5, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.GRASS));
        newmove.setHpRestore(0.5f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SWEET_SCENT, 0, 100, 20, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.NORMAL), Enums.StatType.EVASIVENESS, -1, false, 1);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.OUTRAGE, 120, 100, 10, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.DRAGON));
        newmove.setMultiturn(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.PETAL_DANCE, 120, 100, 10, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.GRASS));
        newmove.setMultiturn(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.PETAL_BLIZZARD, 90, 100, 15, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.GRASS));
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.CONFUSE_RAY, 0, 100, 10, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.GHOST));
        newmove.setSubStatus(Enums.SubStatus.CONFUSED);
        newmove.setStatusProb(1.0f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.WILL_O_WISP, 0, 85, 15, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.FIRE));
        newmove.setStatus(Enums.Status.BURNED);
        newmove.setStatusProb(1.0f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.INFERNO, 100, 50, 5, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.FIRE));
        newmove.setStatus(Enums.Status.BURNED);
        newmove.setStatChangeProb(1.0f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SCARY_FACE, 0, 100, 10, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.NORMAL), Enums.StatType.SPEED, -2, false, 1.0f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.FIRE_FANG, 65, 95, 15, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.FIRE));
        newmove.setStatus(Enums.Status.BURNED);
        newmove.setStatChangeProb(0.1f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.DRAGON_CLAW, 80, 100, 15, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.DRAGON));
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.AIR_SLASH, 75, 95, 15, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.FLYING));
        newmove.setSubStatus(Enums.SubStatus.FLINCHED);
        newmove.setStatusProb(0.3f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.HEAT_WAVE, 95, 90, 10, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.FIRE));
        newmove.setStatus(Enums.Status.BURNED);
        newmove.setStatChangeProb(0.1f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.DRAGON_DANCE, 0, 0, 20, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.DRAGON), Enums.StatType.ATTACK, 1, true, 1.0f);
        newmove.getStatTypes().add(Enums.StatType.SPEED);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.EARTHQUAKE, 100, 100, 10, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.GROUND));
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.HYDRO_PUMP, 110, 80, 5, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.WATER));
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.AQUA_TAIL, 90, 90, 10, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.WATER));
        moveMap.put(newmove.getName(), newmove);
    }

}
