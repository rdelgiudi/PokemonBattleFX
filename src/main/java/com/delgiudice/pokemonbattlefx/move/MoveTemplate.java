package com.delgiudice.pokemonbattlefx.move;

import com.delgiudice.pokemonbattlefx.attributes.Enums;
import com.delgiudice.pokemonbattlefx.attributes.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

///Class which allows to describe a move
public class MoveTemplate {
    // HITS_RANDOM - when the number of hits is randomly generated between 2 and 5
    public static int HITS_RANDOM = -1;
    // NOT_APPLICABLE - when a move doesn't have power (status moves) or accuracy (always hits)
    public static int NOT_APPLICABLE = 0;

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
    // recharge - determines whether a move needs a turn to recharge after use
    // oneHitKOMove - determines whether a move is a one hit KO move (if move hits enemy Pokemon faints)
    // switchOut - user switches out after move is successfully executed
    private boolean twoturn = false, self = false, trap = false, charging = false, multiturn = false,
                    recoilUserHp = false, statUpDuringCharging = false, contactMove = false, multiturnConfusion = false,
                    recharge = false, oneHitKOMove = false, switchOut = false;

    // moveDescription - contains the description of the move, displayed in summary menu
    private String moveDescription = "No description";

    // subtype - subtype of move, Physical, Special or Status
    private final Enums.Subtypes subtype;
    // type - type of move, for example: Fire, Grass, Water, Normal
    private final Type type;
    // statTypes - a list of stat changes that the move inflicts
    // secondaryStatTypes - another list of stat changes that the move inflicts, used in case they increase or decrease
    // by a different value than the primary stat change
    private List<Enums.StatType> statTypes = new ArrayList<>(), secondaryStatTypes = new ArrayList<>();
    // status - status effect that the move inflicts
    private Enums.Status status = Enums.Status.NONE;
    // subStatus - secondary status effect that the move inflicts, secondary effects usually don't last very long or can
    // be cured by switching out a Pokemon
    private Enums.SubStatus subStatus = Enums.SubStatus.NONE;
    // condition - condition applied on the battlefield, mostly temporary stat boosts
    private Enums.BattlefieldCondition condition = Enums.BattlefieldCondition.NONE;
    // spikeType - type of spike that the move represents, spikes scatter on enemy field and apply various effects on
    // Pokemon that are sent out
    private Enums.Spikes spikeType = Enums.Spikes.NONE;

    // weatherEffect - type of weather effect induced on the battlefield by the move
    private Enums.WeatherEffect weatherEffect = Enums.WeatherEffect.NONE;

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

    public boolean isOneHitKOMove() {
        return oneHitKOMove;
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

    public Enums.BattlefieldCondition getCondition() {
        return condition;
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

    public Enums.Spikes getSpikeType() {
        return spikeType;
    }

    public Enums.WeatherEffect getWeatherEffect() {
        return weatherEffect;
    }

    public boolean isRecharge() {
        return recharge;
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

    public boolean isSwitchOut() {
        return switchOut;
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

    public void setOneHitKOMove(boolean oneHitKOMove) {
        this.oneHitKOMove = oneHitKOMove;
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

    public void setSwitchOut(boolean switchOut) {
        this.switchOut = switchOut;
    }

    public void setCondition(Enums.BattlefieldCondition condition) {
        this.condition = condition;
    }

    public void setRecharge(boolean recharge) {
        this.recharge = recharge;
    }

    public void setSecondaryStatChange(int secondaryStatChange) {
        this.secondaryStatChange = secondaryStatChange;
    }

    public void setMoveDescription(String moveDescription) {
        this.moveDescription = moveDescription;
    }

    public void setSpikeType(Enums.Spikes spikeType) {
        this.spikeType = spikeType;
    }

    public void setWeatherEffect(Enums.WeatherEffect weatherEffect) {
        this.weatherEffect = weatherEffect;
    }

    public void setSelf(boolean self) {
        this.self = self;
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
        MoveTemplate newmove = new MoveTemplate(MoveEnum.CONFUSION_DAMAGE, 40, NOT_APPLICABLE, 1, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.NO_TYPE), false);
        moveMap.put(newmove.getName(), newmove);

        //Struggle, used only when all moves are out of PP
        newmove = new MoveTemplate(MoveEnum.STRUGGLE, 50, NOT_APPLICABLE, 1, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.NO_TYPE), true);
        newmove.setMoveDescription("This attack is used in desperation only if the user has no remaining PP. It also damages the user a little.");
        newmove.setRecoil(1/4f);
        newmove.setRecoilUserHp(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.TACKLE, 40, 100, 35, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.NORMAL), true);
        newmove.setMoveDescription("A physical attack in which the user charges and slams into the target with its whole body.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.GROWL, NOT_APPLICABLE, 100, 40, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.NORMAL), false, Enums.StatType.ATTACK, -1,
                false, 1);
        newmove.setMoveDescription("The user growls in an endearing way, making opposing Pokémon less wary. This lowers their Attack stats.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.VINE_WHIP, 45, 100, 25, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.GRASS), true);
        newmove.setMoveDescription("The target is struck with slender, whiplike vines to inflict damage.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SCRATCH, 40, 100, 35, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.NORMAL), true);
        newmove.setMoveDescription("Hard, pointed, sharp claws rake the target to inflict damage.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.EMBER, 40, 100, 25, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.FIRE), false);
        newmove.setMoveDescription("The target is attacked with small flames. This may also leave the target with a burn.");
        newmove.setStatus(Enums.Status.BURNED);
        newmove.setStatusProb(0.1f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.TAIL_WHIP, NOT_APPLICABLE, 100, 30, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.NORMAL), false, Enums.StatType.DEFENSE, -1, false, 1);
        newmove.setMoveDescription("The user wags its tail cutely, making opposing Pokémon less wary. This lowers their Defense stats.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.QUICK_ATTACK, 40, 100, 30, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.NORMAL), true);
        newmove.setMoveDescription("The user lunges at the target to inflict damage, moving at blinding speed. This move always goes first.");
        newmove.setPriority(1);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.WATER_GUN, 40, 100, 25, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.WATER), false);
        newmove.setMoveDescription("The target is blasted with a forceful shot of water.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.DOUBLE_KICK, 30, 100, 30, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.FIGHTING), true);
        newmove.setMoveDescription("The user attacks by kicking the target twice in a row using two feet.");
        newmove.setHits(2);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SLASH, 70, 100, 20, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.NORMAL), true);
        newmove.setMoveDescription("The target is attacked with a slash of claws, scythes, or the like. This move has a heightened chance of landing a critical hit.");
        newmove.setCritTemporaryIncrease(1);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.DRAGON_BREATH, 60, 100, 20, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.DRAGON), false);
        newmove.setMoveDescription("The user exhales a mighty gust that inflicts damage. This may also leave the target with paralysis.");
        newmove.setStatus(Enums.Status.PARALYZED);
        newmove.setStatusProb(0.3f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.FLARE_BLITZ, 120, 100, 15, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.FIRE), true);
        newmove.setMoveDescription("The user cloaks itself in fire and charges the target to inflict damage. This also damages the user quite a lot and may leave the target with a burn.");
        newmove.setRecoil(1.0f/3.0f);
        newmove.setStatus(Enums.Status.BURNED);
        newmove.setStatChangeProb(0.1f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.FIRE_SPIN, 35, 85, 15, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.FIRE), false);
        newmove.setMoveDescription(" The user traps the target inside a fierce vortex of fire that inflicts damage for four to five turns.");
        newmove.setTrap(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.DIG, 80, 100, 10, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.GROUND), true);
        newmove.setMoveDescription("The user burrows into the ground on the first turn, then attacks on the next turn.");
        newmove.setTwoturn(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.RAZOR_LEAF, 55, 95, 25, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.GRASS), false);
        newmove.setMoveDescription("Sharp-edged leaves are launched to slash at opposing Pokémon. This move has a heightened chance of landing a critical hit.");
        newmove.setCritTemporaryIncrease(1);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SOLAR_BEAM, 120, 100, 10, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.GRASS), false);
        newmove.setMoveDescription("The user gathers light on the first turn, then blasts a bundled beam on the next turn.");
        newmove.setTwoturn(true);
        newmove.setCharging(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.DOUBLE_EDGE, 120, 100, 15, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.NORMAL), true);
        newmove.setMoveDescription("A reckless life-risking tackle in which the user rushes the target. This also damages the user quite a lot.");
        newmove.setRecoil(1.0f/3.0f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SLEEP_POWDER, NOT_APPLICABLE, 75, 15, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.GRASS), false);
        newmove.setMoveDescription("The user scatters a cloud of soporific dust that puts the target to sleep.");
        newmove.setStatus(Enums.Status.SLEEPING);
        newmove.setStatusProb(1.0f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SEED_BOMB, 80, 100, 15, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.GRASS), false);
        newmove.setMoveDescription("The user attacks by slamming a barrage of hard-shelled seeds down on the target from above.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SYNTHESIS, NOT_APPLICABLE, NOT_APPLICABLE, 5, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.GRASS), false);
        newmove.setMoveDescription("The user restores its own HP. The amount of HP regained varies with the weather.");
        newmove.setHpRestore(0.5f);
        newmove.setSelf(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SWEET_SCENT, NOT_APPLICABLE, 100, 20, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.NORMAL), false, Enums.StatType.EVASIVENESS, -1,
                false, 1);
        newmove.setMoveDescription("The user releases a scent that harshly lowers opposing Pokémon's evasiveness.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.OUTRAGE, 120, 100, 10, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.DRAGON), true);
        newmove.setMoveDescription("The user attacks by going on a rampage, then becomes fixated on using this move.");
        newmove.setMultiturn(true);
        newmove.setMultiturnConfusion(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.PETAL_DANCE, 120, 100, 10, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.GRASS), true);
        newmove.setMoveDescription("The user attacks the target by scattering petals, then becomes fixated on using this move.");
        newmove.setMultiturn(true);
        newmove.setMultiturnConfusion(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.PETAL_BLIZZARD, 90, 100, 15, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.GRASS), false);
        newmove.setMoveDescription("The user stirs up a violent petal blizzard and damages everything around it.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.CONFUSE_RAY, NOT_APPLICABLE, 100, 10, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.GHOST), false);
        newmove.setMoveDescription("The target is exposed to a sinister ray that causes confusion.");
        newmove.setSubStatus(Enums.SubStatus.CONFUSED);
        newmove.setStatusProb(1.0f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.WILL_O_WISP, NOT_APPLICABLE, 85, 15, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.FIRE), false);
        newmove.setMoveDescription("The user shoots a sinister flame at the target to inflict a burn.");
        newmove.setStatus(Enums.Status.BURNED);
        newmove.setStatusProb(1.0f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.INFERNO, 100, 50, 5, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.FIRE), false);
        newmove.setMoveDescription("The user attacks by engulfing the target in an intense fire. This leaves the target with a burn.");
        newmove.setStatus(Enums.Status.BURNED);
        newmove.setStatChangeProb(1.0f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SCARY_FACE, NOT_APPLICABLE, 100, 10, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.NORMAL), false, Enums.StatType.SPEED, -2, false, 1.0f);
        newmove.setMoveDescription("The user frightens the target with a scary face to harshly lower its Speed stat.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.FIRE_FANG, 65, 95, 15, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.FIRE), true);
        newmove.setMoveDescription("The user bites with flame-cloaked fangs. This may also make the target flinch or leave it with a burn.");
        newmove.setStatus(Enums.Status.BURNED);
        newmove.setStatChangeProb(0.1f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.DRAGON_CLAW, 80, 100, 15, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.DRAGON), true);
        newmove.setMoveDescription("The user slashes the target with huge, sharp claws to inflict damage.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.AIR_SLASH, 75, 95, 15, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.FLYING), false);
        newmove.setMoveDescription("The user attacks with a blade of air that slices even the sky. This may also make the target flinch.");
        newmove.setSubStatus(Enums.SubStatus.FLINCHED);
        newmove.setStatusProb(0.3f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.HEAT_WAVE, 95, 90, 10, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.FIRE), false);
        newmove.setMoveDescription("The user attacks by exhaling hot breath on opposing Pokémon. This may also leave them with a burn.");
        newmove.setStatus(Enums.Status.BURNED);
        newmove.setStatChangeProb(0.1f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.DRAGON_DANCE, NOT_APPLICABLE, NOT_APPLICABLE, 20, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.DRAGON), false, Enums.StatType.ATTACK, 1, true, 1.0f);
        newmove.setMoveDescription("The user vigorously performs a mystic, powerful dance that raises its Attack and Speed stats.");
        newmove.getStatTypes().add(Enums.StatType.SPEED);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.EARTHQUAKE, 100, 100, 10, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.GROUND), false);
        newmove.setMoveDescription("The user sets off an earthquake that strikes every Pokémon around it.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.HYDRO_PUMP, 110, 80, 5, Enums.Subtypes.SPECIAL,
                Type.getTypeMap().get(Enums.Types.WATER), false);
        newmove.setMoveDescription("The target is blasted by a huge volume of water launched under great pressure.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.AQUA_TAIL, 90, 90, 10, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.WATER), true);
        newmove.setMoveDescription("The user attacks by swinging its tail as if it were a vicious wave in a raging storm.");
        moveMap.put(newmove.getName(), newmove);


        newmove = new MoveTemplate(MoveEnum.SKULL_BASH, 130, 100, 10, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap().get(Enums.Types.NORMAL), true);
        newmove.setMoveDescription("The user tucks in its head to raise its Defense stat on the first turn, then rams the target on the next turn.");
        newmove.getStatTypes().add(Enums.StatType.DEFENSE);
        newmove.setStatChange(1);
        newmove.setStatUpDuringCharging(true);
        newmove.setTwoturn(true);
        newmove.setCharging(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SHELL_SMASH, NOT_APPLICABLE, NOT_APPLICABLE, 15, Enums.Subtypes.STATUS,
                Type.getTypeMap().get(Enums.Types.NORMAL), false, Enums.StatType.DEFENSE, -1,
                true, 1.0f);
        newmove.setMoveDescription("The user breaks its shell, which lowers its Defense and Sp. Def stats but sharply boosts its Attack, Sp. Atk, and Speed stats.");
        newmove.getStatTypes().add(Enums.StatType.SPECIAL_DEFENSE);
        newmove.setSecondaryStatChange(2);
        newmove.getSecondaryStatTypes().add(Enums.StatType.ATTACK);
        newmove.getSecondaryStatTypes().add(Enums.StatType.SPECIAL_ATTACK);
        newmove.getSecondaryStatTypes().add(Enums.StatType.SPEED);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.ROLLOUT, 30, 90, 20, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap(Enums.Types.ROCK), true);
        newmove.setMoveDescription("The user continually rolls into the target over five turns. This attack becomes more powerful each time it hits.");
        newmove.setMultiturn(true);
        newmove.setMultiturnConfusion(false);
        newmove.setHits(1);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.WHIRLPOOL, 35, 85, 15, Enums.Subtypes.SPECIAL,
                Type.getTypeMap(Enums.Types.WATER), false);
        newmove.setMoveDescription("The user traps the target inside a violent, swirling whirlpool that inflicts damage for four to five turns.");
        newmove.setTrap(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.ICE_PUNCH, 75, 100, 15, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap(Enums.Types.ICE), true);
        newmove.setMoveDescription("The target is attacked with an icy punch. This may also leave the target frozen.");
        newmove.setStatus(Enums.Status.FROZEN);
        newmove.setStatusProb(0.1f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.FLASH_CANNON, 80, 100, 10, Enums.Subtypes.SPECIAL,
                Type.getTypeMap(Enums.Types.STEEL), false);
        newmove.setMoveDescription("The user gathers all its light energy and releases it at once. This may also lower the target's Sp. Def stat.");
        newmove.getStatTypes().add(Enums.StatType.SPECIAL_DEFENSE);
        newmove.setStatChange(-1);
        newmove.setStatChangeProb(0.1f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.HYDRO_CANNON, 150, 90, 5, Enums.Subtypes.SPECIAL,
                Type.getTypeMap(Enums.Types.WATER), false);
        newmove.setMoveDescription("The target is hit with a watery blast. The user can't move on the next turn.");
        newmove.setRecharge(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.ICE_BEAM, 90, 100, 10, Enums.Subtypes.SPECIAL,
                Type.getTypeMap(Enums.Types.ICE), false);
        newmove.setMoveDescription("The target is struck with an icy-cold beam of energy. This may also leave the target frozen.");
        newmove.setStatus(Enums.Status.FROZEN);
        newmove.setStatusProb(0.1f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.STRING_SHOT, NOT_APPLICABLE, 95, 40, Enums.Subtypes.STATUS,
                Type.getTypeMap(Enums.Types.BUG), false, Enums.StatType.SPEED, -2,
                false, 1f);
        newmove.setMoveDescription("The user blows silk from its mouth that binds opposing Pokémon and harshly lowers their Speed stats.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.BUG_BITE, 60, 100, 20, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap(Enums.Types.BUG), true); //TODO: move should steal enemy berry and use it
        newmove.setMoveDescription("The user bites the target. If the target is holding a Berry, the user eats it and gains its effect.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.ELECTROWEB, 55, 95, 15, Enums.Subtypes.SPECIAL,
                Type.getTypeMap(Enums.Types.ELECTRIC), false, Enums.StatType.SPEED, -1,
                false, 1f);
        newmove.setMoveDescription("The user captures opposing Pokémon in an electric net to inflict damage. This also lowers their Speed stats.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.IRON_DEFENSE, NOT_APPLICABLE, NOT_APPLICABLE, 15, Enums.Subtypes.STATUS,
                Type.getTypeMap(Enums.Types.STEEL), false, Enums.StatType.DEFENSE, 2,
                true, 1f);
        newmove.setMoveDescription("The user hardens its body's surface like iron, sharply boosting its Defense stat.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.TAILWIND, NOT_APPLICABLE, NOT_APPLICABLE, 15, Enums.Subtypes.STATUS,
                Type.getTypeMap(Enums.Types.FLYING), false);
        newmove.setMoveDescription("The user whips up a turbulent whirlwind that boosts the Speed stats of itself and its allies for four turns.");
        newmove.setCondition(Enums.BattlefieldCondition.TAILWIND);
        newmove.setSelf(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.BUG_BUZZ, 90, 100, 10, Enums.Subtypes.SPECIAL,
                Type.getTypeMap(Enums.Types.BUG), false, Enums.StatType.SPECIAL_DEFENSE, -1,
                false, 0.1f);
        newmove.setMoveDescription("The user vibrates to generate a damaging sound wave. This may also lower the target's Sp. Def stat.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.PSYCHIC, 90, 100, 10, Enums.Subtypes.SPECIAL,
                Type.getTypeMap(Enums.Types.PSYCHIC), false, Enums.StatType.SPECIAL_DEFENSE, -1,
                false, 0.1f);
        newmove.setMoveDescription("The target is hit with a strong telekinetic force to inflict damage. This may also lower the target’s Sp. Def stat.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.GIGA_DRAIN, 75, 100, 10, Enums.Subtypes.SPECIAL,
                Type.getTypeMap(Enums.Types.GRASS), false);
        newmove.setMoveDescription("A nutrient-draining attack. The user's HP is restored by up to half the damage taken by the target.");
        newmove.setLifesteal(0.5f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.POISON_STING, 15, 100, 35, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap(Enums.Types.POISON), false);
        newmove.setMoveDescription("The user stabs the target with a poisonous stinger to inflict damage. This may also poison the target.");
        newmove.setStatus(Enums.Status.POISONED);
        newmove.setStatusProb(0.3f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.HARDEN, NOT_APPLICABLE, NOT_APPLICABLE, 30, Enums.Subtypes.STATUS,
                Type.getTypeMap(Enums.Types.NORMAL), false, Enums.StatType.DEFENSE, 1, true,
                1f);
        newmove.setMoveDescription("The user stiffens all the muscles in its body to boost its Defense stat.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.PIN_MISSILE, 25, 95, 20, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap(Enums.Types.BUG), false);
        newmove.setHits(HITS_RANDOM);
        newmove.setMoveDescription("The user attacks by shooting sharp spikes at the target. This move hits two to five times in a row.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.POISON_JAB, 80, 100, 20, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap(Enums.Types.POISON), true);
        newmove.setMoveDescription("The target is stabbed with a tentacle, an arm, or the like steeped in poison. This may also poison the target.");
        newmove.setStatus(Enums.Status.POISONED);
        newmove.setStatusProb(0.3f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.TOXIC_SPIKES, NOT_APPLICABLE, NOT_APPLICABLE, 20, Enums.Subtypes.STATUS,
                Type.getTypeMap(Enums.Types.POISON), false);
        newmove.setMoveDescription("The user lays a trap of poison spikes at the feet of the opposing team. The spikes will poison opposing Pokémon that switch into battle.");
        newmove.setSpikeType(Enums.Spikes.TOXIC_SPIKES);
        newmove.setSelf(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.AGILITY, NOT_APPLICABLE, NOT_APPLICABLE, 30, Enums.Subtypes.STATUS,
                Type.getTypeMap(Enums.Types.PSYCHIC), false, Enums.StatType.SPEED, 2, true,
                1.0f);
        newmove.setMoveDescription("The user relaxes and lightens its body to move faster. This sharply boosts its Speed stat.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.AERIAL_ACE, 60, NOT_APPLICABLE, 20, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap(Enums.Types.FLYING), true);
        newmove.setMoveDescription("The user confounds the target with speed, then slashes. This attack never misses.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SAND_ATTACK, NOT_APPLICABLE, NOT_APPLICABLE, 15, Enums.Subtypes.STATUS,
                Type.getTypeMap(Enums.Types.GROUND), false, Enums.StatType.ACCURACY, -1, false,
                1f);
        newmove.setMoveDescription("Sand is hurled in the target's face, lowering the target's accuracy.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.HURRICANE, 110, 70, 10, Enums.Subtypes.SPECIAL,
                Type.getTypeMap(Enums.Types.FLYING), false);
        newmove.setMoveDescription("The user attacks by wrapping its opponent in a fierce wind. This may also confuse the target.");
        newmove.setSubStatus(Enums.SubStatus.CONFUSED);
        newmove.setStatusProb(0.3f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.ROOST, NOT_APPLICABLE, NOT_APPLICABLE, 5, Enums.Subtypes.STATUS,
                Type.getTypeMap(Enums.Types.FLYING), false);
        newmove.setMoveDescription("The user lands and rests its body. This move restores the user's HP by up to half its max HP.");
        newmove.setHpRestore(0.5f);
        newmove.setSubStatus(Enums.SubStatus.ROOST);
        newmove.setSelf(true);
        newmove.setStatusProb(1f); // redundant
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.DOUBLE_TEAM, NOT_APPLICABLE, NOT_APPLICABLE, 15, Enums.Subtypes.STATUS,
                Type.getTypeMap(Enums.Types.NORMAL), false, Enums.StatType.EVASIVENESS, 1, true,
                1f);
        newmove.setMoveDescription("By moving rapidly, the user makes illusory copies of itself to boost its evasiveness.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.FEATHER_DANCE, NOT_APPLICABLE, NOT_APPLICABLE, 15, Enums.Subtypes.STATUS,
                Type.getTypeMap(Enums.Types.FLYING), false, Enums.StatType.ATTACK, -2, false,
                1f);
        newmove.setMoveDescription("The user covers the target's body with a mass of down that harshly lowers its Attack stat.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.FLY, 90, 95, 15, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap(Enums.Types.FLYING), true);
        newmove.setTwoturn(true);
        newmove.setMoveDescription("The user flies up into the sky on the first turn, then attacks on the next turn.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.HYPER_BEAM, 150, 90, 5, Enums.Subtypes.SPECIAL,
                Type.getTypeMap(Enums.Types.NORMAL), false);
        newmove.setRecharge(true);
        newmove.setMoveDescription("The target is attacked with a powerful beam. The user can't move on the next turn.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SUPER_FANG, NOT_APPLICABLE, 90, 10, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap(Enums.Types.NORMAL), true);
        newmove.setMoveDescription("The user chomps hard on the target with its sharp front fangs. This cuts the target's HP in half.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.FOCUS_ENERGY, NOT_APPLICABLE, NOT_APPLICABLE, 30, Enums.Subtypes.STATUS,
                Type.getTypeMap(Enums.Types.NORMAL), false);
        newmove.setSubStatus(Enums.SubStatus.FOCUS_ENERGY);
        newmove.setSelf(true);
        newmove.setMoveDescription("The user takes a deep breath and focuses so that its future attacks have a heightened chance of landing critical hits.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.BITE, 60, 100, 25, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap(Enums.Types.DARK), true);
        newmove.setMoveDescription("The target is bitten with viciously sharp fangs. This may also make the target flinch.");
        newmove.setSubStatus(Enums.SubStatus.FLINCHED);
        newmove.setStatusProb(0.3f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.TAKE_DOWN, 90, 85, 20, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap(Enums.Types.NORMAL), true);
        newmove.setMoveDescription("A reckless full-body charge attack for slamming into the target. This also damages the user a little.");
        newmove.setRecoil(0.25f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.ENDEAVOR, NOT_APPLICABLE, 100, 5, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap(Enums.Types.NORMAL), true);
        newmove.setMoveDescription("The user inflicts damage by cutting down the target's HP to roughly equal the user's HP.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.CRUNCH, 80, 100, 15, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap(Enums.Types.DARK), true, Enums.StatType.DEFENSE, -1, false,
                0.2f);
        newmove.setMoveDescription("The user crunches up the target with sharp fangs. This may also lower the target's Defense stat.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.LASER_FOCUS, NOT_APPLICABLE, NOT_APPLICABLE, 30, Enums.Subtypes.STATUS,
                Type.getTypeMap(Enums.Types.NORMAL), false);
        newmove.setMoveDescription("The user concentrates intensely. The attack on the next turn always results in a critical hit.");
        newmove.setSubStatus(Enums.SubStatus.LASER_FOCUS);
        newmove.setSelf(true);
        newmove.setStatusProb(1f);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.DRILL_PECK, 80, 100, 20, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap(Enums.Types.FLYING), true);
        newmove.setMoveDescription("A corkscrewing attack that strikes the target with a sharp beak acting as a drill.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.WING_ATTACK, 60, 100, 35, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap(Enums.Types.FLYING), true);
        newmove.setMoveDescription("The target is struck with large, imposing wings spread wide to inflict damage.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.DRILL_RUN, 80, 95, 10, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap(Enums.Types.GROUND), true);
        newmove.setMoveDescription("The user crashes into the target while rotating its body like a drill. This move has a heightened chance of landing a critical hit.");
        newmove.setCritTemporaryIncrease(1);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.GIGA_IMPACT, 150, 90, 5, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap(Enums.Types.NORMAL), true);
        newmove.setMoveDescription("The user charges at the target using every bit of its power. The user can't move on the next turn.");
        newmove.setRecharge(true);
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.FACADE, 70, 100, 20, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap(Enums.Types.NORMAL), true);
        newmove.setMoveDescription("This move's power is doubled if the user is poisoned, burned, or paralyzed.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SLUDGE_BOMB, 90, 100, 10, Enums.Subtypes.SPECIAL,
                Type.getTypeMap(Enums.Types.POISON), false);
        newmove.setStatus(Enums.Status.POISONED);
        newmove.setStatusProb(0.3f);
        newmove.setMoveDescription("The user hurls unsanitary sludge at the target to inflict damage. This may also poison the target.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.SCREECH, NOT_APPLICABLE, 85, 40, Enums.Subtypes.STATUS,
                Type.getTypeMap(Enums.Types.POISON), false);
        newmove.getStatTypes().add(Enums.StatType.DEFENSE);
        newmove.setStatChange(-2);
        newmove.setStatChangeProb(1f);
        newmove.setMoveDescription("An earsplitting screech harshly lowers the target's Defense stat.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.GASTRO_ACID, NOT_APPLICABLE, 100, 10, Enums.Subtypes.STATUS,
                Type.getTypeMap(Enums.Types.POISON), false);
        newmove.setSubStatus(Enums.SubStatus.GASTRO_ACID);
        newmove.setStatusProb(1f);
        newmove.setMoveDescription("The user hurls up its stomach acids on the target. The fluid eliminates the effect of the target's Ability.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.GUNK_SHOT, 120, 80, 5, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap(Enums.Types.POISON), false);
        newmove.setStatus(Enums.Status.POISONED);
        newmove.setStatusProb(0.3f);
        newmove.setMoveDescription("The user shoots filthy garbage at the target to attack. This may also poison the target.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.THUNDER_FANG, 65, 95, 15, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap(Enums.Types.ELECTRIC), true);
        newmove.setStatus(Enums.Status.PARALYZED);
        newmove.setSubStatus(Enums.SubStatus.FLINCHED);
        newmove.setStatusProb(0.1f);
        newmove.setMoveDescription("The user bites with electrified fangs. This may also make the target flinch or leave it with paralysis.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.COIL, NOT_APPLICABLE, NOT_APPLICABLE, 20, Enums.Subtypes.STATUS,
                Type.getTypeMap(Enums.Types.POISON), false);
        newmove.getStatTypes().add(Enums.StatType.ATTACK);
        newmove.getStatTypes().add(Enums.StatType.DEFENSE);
        newmove.getStatTypes().add(Enums.StatType.ACCURACY);
        newmove.setStatChange(1);
        newmove.setStatChangeProb(1f);
        newmove.setSelf(true);
        newmove.setMoveDescription("The user coils up and concentrates. This boosts its Attack and Defense stats as well as its accuracy.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.THUNDERBOLT, 90, 100, 15, Enums.Subtypes.SPECIAL,
                Type.getTypeMap(Enums.Types.ELECTRIC), false);
        newmove.setStatus(Enums.Status.PARALYZED);
        newmove.setStatusProb(0.1f);
        newmove.setMoveDescription("The user attacks the target with a strong electric blast. This may also leave the target with paralysis.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.IRON_TAIL, 100, 75, 15, Enums.Subtypes.PHYSICAL,
                Type.getTypeMap(Enums.Types.STEEL), true);
        newmove.getStatTypes().add(Enums.StatType.DEFENSE);
        newmove.setStatChange(-1);
        newmove.setStatChangeProb(0.3f);
        newmove.setSelf(false);
        newmove.setMoveDescription("The target is slammed with a steel-hard tail. This may also lower the target's Defense stat.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.ELECTRO_BALL, NOT_APPLICABLE, 100, 10, Enums.Subtypes.SPECIAL,
                Type.getTypeMap(Enums.Types.ELECTRIC), false);
        newmove.setMoveDescription("The user hurls an electric orb at the target. The faster the user is than the target, the greater the move's power.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.THUNDER, 110, 70, 10, Enums.Subtypes.SPECIAL,
                Type.getTypeMap(Enums.Types.ELECTRIC), false);
        newmove.setStatus(Enums.Status.PARALYZED);
        newmove.setStatusProb(0.3f);
        newmove.setMoveDescription("A wicked thunderbolt is dropped on the target to inflict damage. This may also leave the target with paralysis.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.RAIN_DANCE, NOT_APPLICABLE, NOT_APPLICABLE, 5, Enums.Subtypes.STATUS,
                Type.getTypeMap(Enums.Types.WATER), false);
        newmove.setWeatherEffect(Enums.WeatherEffect.RAIN);
        newmove.setMoveDescription("The user summons a heavy rain that falls for five turns, powering up Water-type attacks. The rain also lowers the power of Fire-type attacks.");
        moveMap.put(newmove.getName(), newmove);

        newmove = new MoveTemplate(MoveEnum.VOLT_SWITCH, 70, 100, 20, Enums.Subtypes.SPECIAL,
                Type.getTypeMap(Enums.Types.ELECTRIC), false);
        newmove.setSwitchOut(true);
        newmove.setMoveDescription("After making its attack, the user rushes back to switch places with a party Pokémon in waiting.");
        moveMap.put(newmove.getName(), newmove);
    }

}
