package com.delgiudice.pokemonbattlefx;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

///Class which allows to describe a move
public class MoveTemplate {

    // moveMap - a map of all moves available
    private static final HashMap<MoveEnum, MoveTemplate> moveMap = new HashMap<>();

    //name - name of the move in enum form, can also return a string if needed
    MoveEnum name;
    // power - move power, used for damage calculations
    // accuracy - move accuracy, used for hit calculations
    private final int power, accuracy;
    // maxpp - maximum amount of PP (Power Points) that a move has
    // hits - the amount of hits that a move inflicts (each move's critical hit and damage is calculated separately)
    // statChange - the amount of stages of a stat that a move changes (typically from -3 to 3)
    // secondaryStatChange - holds the same information as statChange, in case a move changes a set of stats in different
    // ways than the primary change
    // critIncrease - the amount of stages of critical hit chance that a move changes (typically 1 or 2)
    // critTemporaryIncrease - the amount of stages of critical hit chance that the move changes for the time of its
    // execution
    // priority - priority of a given move, between -7 and 5
    private int maxpp, hits = 1, statChange = 0, secondaryStatChange = 0, critIncrease = 0, critTemporaryIncrease = 0,
            priority = 0;
    // statusProb - probability of inflicting status effects after move execution, typically 1 for status moves, <1 otherwise
    // statChangeProb - probability of changing a stat, typically 1 for status moves, <1 otherwise
    // recoil - the percentage of damage dealt that should be converted to recoil except the move Struggle, that deals a
    // percentage of max HP instead
    // lifesteal - the percentage of damage dealt that should be converted to health restoration for the user
    // hprestore - the percentage of user's max HP to be restored by this move (only for status moves)
    private float statusProb = 0, statChangeProb = 0, recoil = 0, lifesteal = 0, hpRestore = 0;

    // twoturn - determines if a move has a charging phase before executing
    // self - determines whether a move's secondary effect affects its user
    // trap - determines whether a move is a trap move, trap moves hurt the target for 2 to 5 turns and prevent them
    // from switching
    // charging - determines whether a twoturn move is purely a charging move, i.e. doesn't involve a semi invulnerable
    // state when charging the move
    // multiturn - determines whether a move is a charging move
    // recoilUserHp - determines if a move calculates recoil based on user max HP instead of damage dealt
    // contactMove - check if the move makes contact for calculations related to some Pokemon abilities,
    // such as static
    // multiturnConfusion - checks if the move causes confusion at the end of its execution (such as Outrage)
    private boolean twoturn = false, self = false, trap = false, charging = false, multiturn = false,
                    recoilUserHp = false, statUpDuringCharging = false, contactMove = false, multiturnConfusion = false;

    // moveDescription - contains the description of the move
    private String moveDescription = "No description";

    // subtype - subtype of move, Physical, Special or Status
    private final Enums.Subtypes subtype;
    // type - type of move, for example: Fire, Grass, Water, Normal
    private final Type type;
    // statTypes - a list of stat changes that the move inflicts
    // secondaryStatTypes - another list of stat changes that the move inflicts, used in case they increase or decrease
    // by a different value than the primary stat change
    private List<Enums.StatType> statTypes = new LinkedList<>(), secondaryStatTypes = new LinkedList<>();
    // status - status effect that the move inflicts
    private Enums.Status status = Enums.Status.NONE;
    // subStatus - secondary status effect that the move inflicts, secondary effects usually don't last very long or can
    // be cured by switching out a Pokemon
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

    public boolean isContactMove() {
        return contactMove;
    }

    public float getHpRestore() {
        return hpRestore;
    }

    public boolean isCharging() {
        return charging;
    }

    public int getPriority() {
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

    public int getSecondaryStatChange() {
        return secondaryStatChange;
    }

    public List<Enums.StatType> getSecondaryStatTypes() {
        return secondaryStatTypes;
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

    public String getMoveDescription() {
        return moveDescription;
    }

    public boolean isStatUpDuringCharging() {
        return statUpDuringCharging;
    }

    public boolean isRecoilUserHp() {
        return recoilUserHp;
    }

    public boolean isMultiturnConfusion() {
        return multiturnConfusion;
    }

    public void setPriority(int priority) {
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

    public void setStatChange(int statChange) {
        this.statChange = statChange;
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

    public void setMultiturnConfusion(boolean multiturnConfusion) {
        this.multiturnConfusion = multiturnConfusion;
    }

    public void setSubStatus(Enums.SubStatus subStatus) {
        this.subStatus = subStatus;
    }

    public void setMultiturn(boolean multiturn) {
        this.multiturn = multiturn;
    }

    public void setStatUpDuringCharging(boolean statUpDuringCharging) {
        this.statUpDuringCharging = statUpDuringCharging;
    }

    public void setSecondaryStatChange(int secondaryStatChange) {
        this.secondaryStatChange = secondaryStatChange;
    }

    public void setMoveDescription(String moveDescription) {
        this.moveDescription = moveDescription;
    }

    public MoveTemplate(MoveEnum name, int power, int accuracy, int pp, Enums.Subtypes subtype, Type type,
                        boolean contactMove, Enums.StatType statTypes, int statChange, boolean self,
                        float statChangeProb) {
        this.name = name;
        this.power = power;
        this.accuracy = accuracy;
        this.maxpp = pp;
        this.subtype = subtype;
        this.type = type;
        this.contactMove = contactMove;
        this.statTypes.add(statTypes);
        this.statChange = statChange;
        this.self = self;
        this.statChangeProb = statChangeProb;
    }

    public MoveTemplate(MoveEnum name, int power, int accuracy, int pp, Enums.Subtypes subtype, Type type,
                        boolean contactMove) {
        this.name = name;
        this.power = power;
        this.accuracy = accuracy;
        this.maxpp = pp;
        this.subtype = subtype;
        this.type = type;
        this.contactMove = contactMove;
    }

    public static MoveTemplate getMove(MoveEnum moveEnum) {
        return moveMap.get(moveEnum);
    }

    ///initializes list of available moves
    public static void setMoveMap(){
        Type.setTypeList(); //init types

        //This is not a move, it should only be used to process confusion damage
        MoveTemplate newmove = new MoveTemplate(MoveEnum.CONFUSION_DAMAGE, 40, 0, 1, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.NO_TYPE), false);
        moveMap.put(newmove.getName(), newmove);

        //Struggle, used only when all moves are out of PP
        newmove = new MoveTemplate(MoveEnum.STRUGGLE, 50, 0, 1, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.NO_TYPE), true);
        newmove.setRecoil(1/4f);
        newmove.setRecoilUserHp(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.TACKLE, 40, 100, 35, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.NORMAL), true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.GROWL, 0, 100, 40, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.NORMAL), false, Enums.StatType.ATTACK, -1,
                false, 1);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.VINE_WHIP, 45, 100, 25, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.GRASS), true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SCRATCH, 40, 100, 35, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.NORMAL), true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.EMBER, 40, 100, 25, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.FIRE), false);
        newmove.setStatus(Enums.Status.BURNED);
        newmove.setStatusProb(0.1f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.TAIL_WHIP, 0, 100, 30, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.NORMAL), false, Enums.StatType.DEFENSE, -1, false, 1);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.QUICK_ATTACK, 40, 100, 30, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.NORMAL), true);
        newmove.setPriority(1);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.WATER_GUN, 40, 100, 25, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.WATER), false);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.DOUBLE_KICK, 30, 100, 30, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.FIGHTING), true);
        newmove.setHits(2);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SLASH, 70, 100, 20, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.NORMAL), true);
        newmove.setCritTemporaryIncrease(1);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.DRAGON_BREATH, 60, 100, 20, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.DRAGON), false);
        newmove.setStatus(Enums.Status.PARALYZED);
        newmove.setStatusProb(0.3f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.FLARE_BLITZ, 120, 100, 15, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.FIRE), true);
        newmove.setRecoil(1.0f/3.0f);
        newmove.setStatus(Enums.Status.BURNED);
        newmove.setStatChangeProb(0.1f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.FIRE_SPIN, 35, 85, 15, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.FIRE), false);
        newmove.setTrap(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.DIG, 80, 100, 10, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.GROUND), true);
        newmove.setTwoturn(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.RAZOR_LEAF, 55, 95, 25, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.GRASS), false);
        newmove.setCritTemporaryIncrease(1);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SOLAR_BEAM, 120, 100, 10, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.GRASS), false);
        newmove.setTwoturn(true);
        newmove.setCharging(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.DOUBLE_EDGE, 120, 100, 15, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.NORMAL), true);
        newmove.setRecoil(1.0f/3.0f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SLEEP_POWDER, 0, 75, 15, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.GRASS), false);
        newmove.setStatus(Enums.Status.SLEEPING);
        newmove.setStatusProb(1.0f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SEED_BOMB, 80, 100, 15, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.GRASS), false);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SYNTHESIS, 0, 0, 5, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.GRASS), false);
        newmove.setHpRestore(0.5f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SWEET_SCENT, 0, 100, 20, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.NORMAL), false, Enums.StatType.EVASIVENESS, -1,
                false, 1);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.OUTRAGE, 120, 100, 10, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.DRAGON), true);
        newmove.setMultiturn(true);
        newmove.setMultiturnConfusion(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.PETAL_DANCE, 120, 100, 10, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.GRASS), true);
        newmove.setMultiturn(true);
        newmove.setMultiturnConfusion(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.PETAL_BLIZZARD, 90, 100, 15, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.GRASS), false);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.CONFUSE_RAY, 0, 100, 10, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.GHOST), false);
        newmove.setSubStatus(Enums.SubStatus.CONFUSED);
        newmove.setStatusProb(1.0f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.WILL_O_WISP, 0, 85, 15, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.FIRE), false);
        newmove.setStatus(Enums.Status.BURNED);
        newmove.setStatusProb(1.0f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.INFERNO, 100, 50, 5, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.FIRE), false);
        newmove.setStatus(Enums.Status.BURNED);
        newmove.setStatChangeProb(1.0f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SCARY_FACE, 0, 100, 10, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.NORMAL), false, Enums.StatType.SPEED, -2, false, 1.0f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.FIRE_FANG, 65, 95, 15, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.FIRE), true);
        newmove.setStatus(Enums.Status.BURNED);
        newmove.setStatChangeProb(0.1f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.DRAGON_CLAW, 80, 100, 15, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.DRAGON), true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.AIR_SLASH, 75, 95, 15, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.FLYING), false);
        newmove.setSubStatus(Enums.SubStatus.FLINCHED);
        newmove.setStatusProb(0.3f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.HEAT_WAVE, 95, 90, 10, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.FIRE), false);
        newmove.setStatus(Enums.Status.BURNED);
        newmove.setStatChangeProb(0.1f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.DRAGON_DANCE, 0, 0, 20, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.DRAGON), false, Enums.StatType.ATTACK, 1, true, 1.0f);
        newmove.getStatTypes().add(Enums.StatType.SPEED);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.EARTHQUAKE, 100, 100, 10, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.GROUND), false);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.HYDRO_PUMP, 110, 80, 5, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.WATER), false);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.AQUA_TAIL, 90, 90, 10, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.WATER), true);
        moveMap.put(newmove.getName(), newmove);


        newmove = new MoveTemplate(MoveEnum.SKULL_BASH, 130, 100, 10, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.NORMAL), true);
        newmove.getStatTypes().add(Enums.StatType.DEFENSE);
        newmove.setStatChange(1);
        newmove.setStatUpDuringCharging(true);
        newmove.setTwoturn(true);
        newmove.setCharging(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SHELL_SMASH, 0, 0, 15, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.NORMAL), false, Enums.StatType.DEFENSE, -1,
                true, 1.0f);
        newmove.getStatTypes().add(Enums.StatType.SPECIAL_DEFENSE);
        newmove.setSecondaryStatChange(2);
        newmove.getSecondaryStatTypes().add(Enums.StatType.ATTACK);
        newmove.getSecondaryStatTypes().add(Enums.StatType.SPECIAL_ATTACK);
        newmove.getSecondaryStatTypes().add(Enums.StatType.SPEED);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.ROLLOUT, 30, 90, 20, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap(Enums.Types.ROCK), true);
        newmove.setMultiturn(true);
        newmove.setMultiturnConfusion(false);
        newmove.setHits(1);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.WHIRLPOOL, 35, 85, 15, Enums.Subtypes.SPECIAL,
                Type.getTypeMap(Enums.Types.WATER), false);
        newmove.setTrap(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.ICE_PUNCH, 75, 100, 15, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap(Enums.Types.ICE), true);
        newmove.setStatus(Enums.Status.FROZEN);
        newmove.setStatusProb(0.1f);
        moveMap.put(newmove.getName(), newmove);
    }

}
