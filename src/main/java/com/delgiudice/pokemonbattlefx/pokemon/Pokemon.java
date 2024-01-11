package com.delgiudice.pokemonbattlefx.pokemon;

import com.delgiudice.pokemonbattlefx.attributes.Enums;
import com.delgiudice.pokemonbattlefx.attributes.Type;
import com.delgiudice.pokemonbattlefx.move.Move;
import com.delgiudice.pokemonbattlefx.move.MoveEnum;
import com.delgiudice.pokemonbattlefx.move.MoveTemplate;
import com.delgiudice.pokemonbattlefx.trainer.Trainer;

import java.security.SecureRandom;
import java.util.*;

public class Pokemon {
    // pokemonExamples - a map of Pokemon with example moves, level and ability, for quickly assembling a team without
    // the need to input it all manually
    private static final HashMap<PokemonEnum, Pokemon> pokemonExamples = new HashMap<>();
    // name - name of the Pokemon, identical to the name of the specie unless nicknamed
    private String name;
    // owner - owner of the Pokemon
    private Trainer owner;
    // hp - current HP
    // level - current level
    private int hp, level;
    // moveList - a list of moves that can be performed by the Pokemon
    private List<Move> moveList = new ArrayList<>();
    // stats - statistics calculated for the Pokemon, the calculations involve: base stats, level, nature, ivs
    private LinkedHashMap<Enums.StatType, Integer> stats = new LinkedHashMap<>();
    // status - current status effect inflicted on the Pokemon
    private Enums.Status status = Enums.Status.NONE;
    // poisonCounter - a counter used when a Pokemon is badly poisoned, increases with each turn
    // sleepCounter - a counter used when a Pokemon sleeps, the counter decreases with each turn
    // critIncrease - critical hit chance increases applied to the Pokemon (Focus Energy, items)
    private int poisonCounter = 1, sleepCounter = 0, critIncrease = 0;
    // state - information if Pokemon is in a self-induced state, caused by some moves
    private Enums.States state = Enums.States.NONE;
    // stateMove - move that is linked with state in some way, for twoturn and multiturn it's a move that the Pokemon
    // is forced to use for a set amount of time
    // trapMove - a trap move that is currently affecting the Pokemon
    private Move stateMove = null, trapMove = null;
    // stateCounter - counter that is used to keep track of moves left or the amount of moves during state
    // trappedTimer - counter that tracks how many more turns the Pokemon should be affected by trapping moves
    private int stateCounter = 0, confusionTimer = 0, trappedTimer = 0;
    // ivs - Individual Values - random values between 0-31 that increase stats
    private int[] ivs = {0, 0, 0, 0, 0, 0};
    // nature - the nature of the Pokemon, that sometimes impact some of its stats
    private Enums.Nature nature;
    // statModifiers - modifiers applied on the Pokemon during battle
    private HashMap<Enums.StatType, Integer> statModifiers = new HashMap<>();
    // specie - specie of the Pokemon
    private PokemonSpecie specie;
    // subStatuses - currently applied subStatuses
    private List<Enums.SubStatus> subStatuses = new ArrayList<>();
    // ability - ability of the Pokemon
    private Ability ability = Ability.NONE;
    // trapped - determines whether the Pokemon is currently under effects of a trapping move
    // laserFocusActive - check whether Laser Focus condition should be active
    private boolean trapped = false, laserFocusActive = false;

    // substituteHp - tracks the amount of hp that the Pokemon's substitute has
    private int substituteHp = 0;

    public String getBattleName() {
        if (owner.isPlayer())
            return name;
        else
            return "The foe's " + name;
    }

    public String getBattleNameMiddle() {
        if (owner.isPlayer())
            return name;
        else
            return "the foe's " + name;
    }

    public String getName() {
        return name;
    }

    public PokemonEnum getOriginalName() {
        return specie.getName();
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
        if (this.hp <= 0)
            status = Enums.Status.FAINTED;
    }

    public int getLevel() {
        return level;
    }

    public Ability getAbility() {
        return ability;
    }

    public Move getStateMove() {
        return stateMove;
    }

    public int getStateCounter() {
        return stateCounter;
    }

    public static HashMap<PokemonEnum, Pokemon> getPokemonExamples() {
        return pokemonExamples;
    }

    public HashMap<Enums.StatType, Integer> getStatModifiers() {
        return statModifiers;
    }

    public List<Move> getMoveList() {
        return moveList;
    }

    public Move getMoveList(int move) {
        return moveList.get(move);
    }

    public LinkedHashMap<Enums.StatType, Integer> getStats() {
        return stats;
    }

    public int getStats(Enums.StatType stat)
    {
        return stats.get(stat);
    }

    public int getPoisonCounter() {
        return poisonCounter;
    }

    public int getSleepCounter() {
        return sleepCounter;
    }

    public Enums.States getState() {
        return state;
    }

    public List<Enums.SubStatus> getSubStatuses() {
        return subStatuses;
    }

    public Trainer getOwner() {
        return owner;
    }

    public boolean isTrapped() {
        return trapped;
    }

    public int[] getIvs() {
        return ivs;
    }

    public Enums.Nature getNature() {
        return nature;
    }

    public PokemonSpecie getSpecie() {
        return specie;
    }

    public Type[] getType() {
        return specie.getType();
    }

    public int getMaxHP() {
        return stats.get(Enums.StatType.MAX_HP);
    }

    public Enums.Status getStatus() {
        return status;
    }

    public int getCritIncrease() {
        return critIncrease;
    }

    public int getConfusionTimer() {
        return confusionTimer;
    }

    public int getTrappedTimer() {
        return trappedTimer;
    }

    public Move getTrapMove() {
        return trapMove;
    }

    public boolean isLaserFocusActive() {
        return laserFocusActive;
    }

    public int getPokedexNumber() {
        return specie.getPokedexNumber();
    }

    public int getSubstituteHp() {
        return substituteHp;
    }

    public void setLaserFocusActive(boolean laserFocusActive) {
        this.laserFocusActive = laserFocusActive;
    }

    public void setOwner(Trainer owner) {
        this.owner = owner;
    }

    public void setNature(Enums.Nature nature) {
        this.nature = nature;
        calculateStats();
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setTrappedTimer(int trappedTimer) {
        this.trappedTimer = trappedTimer;
    }

    public void setStatus(Enums.Status status) {
        this.status = status;
    }

    public void setPoisonCounter(int poisonCounter) {
        this.poisonCounter = poisonCounter;
    }

    public void setSleepCounter(int sleepCounter) {
        this.sleepCounter = sleepCounter;
    }

    public void setTrapped(boolean trapped) {
        this.trapped = trapped;
    }

    public void setAbility(Ability ability) {
        this.ability = ability;
    }

    public void setCritIncrease(int critIncrease) {
        this.critIncrease = critIncrease;
    }

    public void setStateMove(Move stateMove) {
        this.stateMove = stateMove;
    }

    public void setStateCounter(int stateCounter) {
        this.stateCounter = stateCounter;
    }

    public void setState(Enums.States state) {
        this.state = state;
    }

    public void setTrapMove(Move trapMove) {
        this.trapMove = trapMove;
    }

    public void setConfusionTimer(int confusionTimer) {
        this.confusionTimer = confusionTimer;
    }

    public void setSubstituteHp(int substituteHp) {
        this.substituteHp = substituteHp;
    }

    public Pokemon(PokemonSpecie specie, int level, Ability ability, Move move1)
    {
        this.name = specie.getName().toString();
        this.specie = specie;
        this.level = level;
        this.ability = ability;
        moveList.add(move1);
        generateVariables();
        this.hp = stats.get(Enums.StatType.MAX_HP);
    }
    public Pokemon(PokemonSpecie specie, int level, Ability ability, Move move1, Move move2)
    {
        this.name = specie.getName().toString();
        this.specie = specie;
        this.level = level;
        this.ability = ability;
        moveList.add(move1);
        moveList.add(move2);
        generateVariables();
        this.hp = stats.get(Enums.StatType.MAX_HP);
    }
    public Pokemon(PokemonSpecie specie, int level, Ability ability, Move move1, Move move2, Move move3)
    {
        this.name = specie.getName().toString();
        this.specie = specie;
        this.level = level;
        this.ability = ability;
        moveList.add(move1);
        moveList.add(move2);
        moveList.add(move3);
        generateVariables();
        this.hp = stats.get(Enums.StatType.MAX_HP);
    }
    public Pokemon(PokemonSpecie specie, int level, Ability ability, Move move1, Move move2, Move move3, Move move4)
    {
        this.name = specie.getName().toString();
        this.specie = specie;
        this.level = level;
        this.ability = ability;
        moveList.add(move1);
        moveList.add(move2);
        moveList.add(move3);
        moveList.add(move4);
        generateVariables();
        this.hp = stats.get(Enums.StatType.MAX_HP);
    }
    public Pokemon(Pokemon original)
    {
        this.name = original.name;
        this.specie = original.specie;
        this.level = original.level;
        this.ability = original.ability;
        this.ivs = original.ivs.clone();
        this.nature = original.nature;
        calculateStats();
        for (Move move : original.getMoveList())
            moveList.add(new Move(move.getTemplate()));
        this.hp = stats.get(Enums.StatType.MAX_HP);
    }

    public boolean[] checkAvailableMoves() {
        boolean[] availableMoves = new boolean[4];

        for (int i=0; i < moveList.size(); i++) {
            if (moveList.get(i).getPp() > 0)
                availableMoves[i] = true;
        }

        return availableMoves;
    }

    public void restoreAll() {
        hp = getMaxHP();
        for (Move move : moveList)
            move.setPp(move.getMaxpp());
        status = Enums.Status.NONE;

        poisonCounter = 1;
        sleepCounter = 0;
        critIncrease = 0;

        state = Enums.States.NONE;

        stateMove = null;
        trapMove = null;

        stateCounter = 0;
        confusionTimer = 0;
        trappedTimer = 0;

        subStatuses.clear();
        statModifiers.clear();

        trapped = false;
        laserFocusActive = false;
    }

    public void levelUp()       //raises level, updates stats
    {
        int[] oldStats = new int[6];
        int i = 0;
        for(Map.Entry<Enums.StatType, Integer> stat : stats.entrySet()) {
            Enums.StatType statname = stat.getKey();
            int statval = stat.getValue();
            oldStats[i] = statval;
            i++;
        }
        level++;
        calculateStats();
        System.out.println("\n" + name + " leveled up to level " + level + "!\n");
        System.out.println("Stats increase:");
        i = 0;
        for (Map.Entry<Enums.StatType, Integer> stat : stats.entrySet()){
            Enums.StatType statname = stat.getKey();
            int statval = stat.getValue();
            int statDiff = statval - oldStats[i];
            i++;
            System.out.println(statname + ": +" + statDiff);
        }
        System.out.println("\nCurrent stats:");
        for (Map.Entry<Enums.StatType, Integer> stat : stats.entrySet()){
            Enums.StatType statname = stat.getKey();
            int statval = stat.getValue();
            System.out.println(statname + ": " + statval);
        }
    }

    public boolean containsType(Enums.Types type) {
        if (getType()[0].getTypeEnum() == type)
            return true;
        else return getType()[1].getTypeEnum() == type;
    }

    public void addMove(Move move)      //adding a move (in game through leveling or tms)
    {
        if(moveList.size() < 4) {
            moveList.add(move);
        }
        else {
            System.out.println(name + " already has 4 moves. Which one would you like to replace?");
            for(int i = 0; i < 4; i++)
            {
                System.out.println((i + 1) + ". " + moveList.get(i).getName());
            }
            Scanner s = new Scanner(System.in);
            boolean correctNum = false;
            int replace = 0;
            while(!correctNum) {
                replace = s.nextInt();
                if (replace <= 4)
                    correctNum = true;
            }
            --replace;
            moveList.set(replace, move);
        }
    }

    public void generateIVs()       //generates IV
    {
        SecureRandom g = new SecureRandom();
        for (int i = 0; i < 6; i++)
        {
            ivs[i] = g.nextInt(32);
        }
    }

    public void generateNature()        //generates nature
    {
        SecureRandom g = new SecureRandom();
        int nature_num = g.nextInt(25);
        nature = Enums.Nature.valueOf(nature_num);
    }

    public void calculateStats()        //calculates pokemon statistics based on ivs and nature and level
    {
        int stat, basestat;
        basestat = specie.getBaseStats().get(Enums.StatType.MAX_HP);     //uses different formula, that's why it's separate
        stat = (int)(Math.floor((2 * basestat + ivs[0])*level/100.0) + level + 10);
        stats.put(Enums.StatType.MAX_HP, stat);
        hp = stat;
        Iterator<Map.Entry<Enums.StatType, Integer>> it = specie.getBaseStats().entrySet().iterator();
        int i = 1;
        while(it.hasNext()) {       //iterates statistics, checks nature buffs/debuffs and calculates
            Map.Entry<Enums.StatType, Integer> pair = it.next();
            if (!pair.getKey().equals(Enums.StatType.MAX_HP))  {
                basestat = pair.getValue();
                float naturemod = 0;
                switch (nature.getStatTab()[i - 1]) {
                    case 0:
                        naturemod = 1;
                        break;
                    case 1:
                        naturemod = 1.1f;
                        break;
                    case -1:
                        naturemod = 0.9f;
                        break;
                    default:
                        System.out.println("ERROR: Nature out of expected range!");
                        break;
                }
                stat = (int)Math.floor((Math.floor((2 * basestat + ivs[i])*level/100.0) + 5) * naturemod);
                stats.put(pair.getKey(), stat);
                i++;
            }
        }
    }

    public void generateVariables()
    {
        generateNature();
        generateIVs();
        calculateStats();
    }

    public static void generatePokemonExamples() {
        PokemonSpecie.setPokemonMap();
        Pokemon example = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.BULBASAUR), 50, Ability.OVERGROW,
                new Move(MoveTemplate.getMoveMap().get(MoveEnum.RAZOR_LEAF)), new Move(MoveTemplate.getMoveMap().get(MoveEnum.DOUBLE_EDGE)),
                new Move(MoveTemplate.getMoveMap().get(MoveEnum.SOLAR_BEAM)), new Move(MoveTemplate.getMoveMap().get(MoveEnum.SLEEP_POWDER)));
        pokemonExamples.put(example.getOriginalName(), example);

        example = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.IVYSAUR), 50, Ability.OVERGROW,
                new Move(MoveTemplate.getMoveMap().get(MoveEnum.SEED_BOMB)), new Move(MoveTemplate.getMoveMap().get(MoveEnum.SYNTHESIS)),
                new Move(MoveTemplate.getMoveMap().get(MoveEnum.SOLAR_BEAM)), new Move(MoveTemplate.getMoveMap().get(MoveEnum.SWEET_SCENT)));
        pokemonExamples.put(example.getOriginalName(), example);

        example = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.VENOSAUR), 50, Ability.OVERGROW,
                new Move(MoveTemplate.getMoveMap().get(MoveEnum.PETAL_BLIZZARD)), new Move(MoveTemplate.getMoveMap().get(MoveEnum.PETAL_DANCE)),
                new Move(MoveTemplate.getMoveMap().get(MoveEnum.SOLAR_BEAM)), new Move(MoveTemplate.getMoveMap().get(MoveEnum.DOUBLE_EDGE)));
        pokemonExamples.put(example.getOriginalName(), example);

        example = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.CHARMANDER), 50, Ability.BLAZE,
                new Move(MoveTemplate.getMoveMap().get(MoveEnum.SLASH)), new Move(MoveTemplate.getMoveMap().get(MoveEnum.DRAGON_BREATH)),
                new Move(MoveTemplate.getMoveMap().get(MoveEnum.FLARE_BLITZ)), new Move(MoveTemplate.getMoveMap().get(MoveEnum.FIRE_SPIN)));
        pokemonExamples.put(example.getOriginalName(), example);

        example = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.CHARMELEON), 50, Ability.BLAZE,
                new Move(MoveTemplate.getMoveMap().get(MoveEnum.INFERNO)), new Move(MoveTemplate.getMoveMap().get(MoveEnum.FIRE_FANG)),
                        new Move(MoveTemplate.getMoveMap().get(MoveEnum.SCARY_FACE)), new Move(MoveTemplate.getMoveMap().get(MoveEnum.DRAGON_CLAW)));
        pokemonExamples.put(example.getOriginalName(), example);

        example = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.CHARIZARD), 50, Ability.BLAZE,
                new Move(MoveTemplate.getMoveMap().get(MoveEnum.AIR_SLASH)), new Move(MoveTemplate.getMoveMap().get(MoveEnum.HEAT_WAVE)),
                new Move(MoveTemplate.getMoveMap().get(MoveEnum.FLARE_BLITZ)), new Move(MoveTemplate.getMoveMap().get(MoveEnum.DRAGON_DANCE)));
        pokemonExamples.put(example.getOriginalName(), example);

        example = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.SQUIRTLE), 50, Ability.TORRENT,
                new Move(MoveTemplate.getMoveMap().get(MoveEnum.AQUA_TAIL)), new Move(MoveTemplate.getMoveMap().get(MoveEnum.HYDRO_PUMP)),
                new Move(MoveTemplate.getMoveMap().get(MoveEnum.SKULL_BASH)), new Move(MoveTemplate.getMoveMap().get(MoveEnum.SHELL_SMASH)));
        pokemonExamples.put(example.getOriginalName(), example);

        example = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.WARTORTLE), 50, Ability.TORRENT,
                new Move(MoveTemplate.getMove(MoveEnum.HYDRO_PUMP)), new Move(MoveTemplate.getMove(MoveEnum.ICE_PUNCH)),
                new Move(MoveTemplate.getMove(MoveEnum.WHIRLPOOL)), new Move(MoveTemplate.getMove(MoveEnum.SHELL_SMASH)));
        pokemonExamples.put(example.getOriginalName(), example);

        example = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.BLASTOISE), 50, Ability.TORRENT,
                new Move(MoveTemplate.getMove(MoveEnum.ICE_BEAM)), new Move(MoveTemplate.getMove(MoveEnum.FLASH_CANNON)),
                new Move(MoveTemplate.getMove(MoveEnum.HYDRO_CANNON)), new Move(MoveTemplate.getMove(MoveEnum.SHELL_SMASH)));
        pokemonExamples.put(example.getOriginalName(), example);

        example = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.CATERPIE), 50, Ability.SHIELD_DUST,
                new Move(MoveTemplate.getMove(MoveEnum.TACKLE)), new Move(MoveTemplate.getMove(MoveEnum.STRING_SHOT)),
                new Move(MoveTemplate.getMove(MoveEnum.BUG_BITE)), new Move(MoveTemplate.getMove(MoveEnum.ELECTROWEB)));
        pokemonExamples.put(example.getOriginalName(), example);

        example = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.METAPOD), 50, Ability.SHED_SKIN,
                new Move(MoveTemplate.getMove(MoveEnum.IRON_DEFENSE)), new Move(MoveTemplate.getMove(MoveEnum.ELECTROWEB)));
        pokemonExamples.put(example.getOriginalName(), example);

        example = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.BUTTERFREE), 50, Ability.COMPOUND_EYES,
                new Move(MoveTemplate.getMove(MoveEnum.BUG_BUZZ)), new Move(MoveTemplate.getMove(MoveEnum.PSYCHIC)),
                new Move(MoveTemplate.getMove(MoveEnum.GIGA_DRAIN)), new Move(MoveTemplate.getMove(MoveEnum.TAILWIND)));
        pokemonExamples.put(example.getOriginalName(), example);

        example = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.WEEDLE), 50, Ability.SHIELD_DUST,
                new Move(MoveTemplate.getMove(MoveEnum.POISON_STING)), new Move(MoveTemplate.getMove(MoveEnum.BUG_BITE)),
                new Move(MoveTemplate.getMove(MoveEnum.STRING_SHOT)));
        pokemonExamples.put(example.getOriginalName(), example);

        example = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.KAKUNA), 50, Ability.SHIELD_DUST,
                new Move(MoveTemplate.getMove(MoveEnum.HARDEN)));
        pokemonExamples.put(example.getOriginalName(), example);

        example = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.BEEDRILL), 50, Ability.SWARM,
                new Move(MoveTemplate.getMove(MoveEnum.PIN_MISSILE)), new Move(MoveTemplate.getMove(MoveEnum.POISON_JAB)),
                new Move(MoveTemplate.getMove(MoveEnum.TOXIC_SPIKES)), new Move(MoveTemplate.getMove(MoveEnum.AGILITY)));
        pokemonExamples.put(example.getOriginalName(), example);

        example = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.PIDGEY), 50, Ability.KEEN_EYE,
                new Move(MoveTemplate.getMove(MoveEnum.AIR_SLASH)), new Move(MoveTemplate.getMove(MoveEnum.AERIAL_ACE)),
                new Move(MoveTemplate.getMove(MoveEnum.QUICK_ATTACK)), new Move(MoveTemplate.getMove(MoveEnum.SAND_ATTACK)));
        pokemonExamples.put(example.getOriginalName(), example);

        example = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.PIDGEOTTO), 50, Ability.KEEN_EYE,
                new Move(MoveTemplate.getMove(MoveEnum.HURRICANE)), new Move(MoveTemplate.getMove(MoveEnum.AERIAL_ACE)),
                new Move(MoveTemplate.getMove(MoveEnum.ROOST)), new Move(MoveTemplate.getMove(MoveEnum.DOUBLE_TEAM)));
        pokemonExamples.put(example.getOriginalName(), example);

        example = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.PIDGEOT), 50, Ability.KEEN_EYE,
                new Move(MoveTemplate.getMove(MoveEnum.HURRICANE)), new Move(MoveTemplate.getMove(MoveEnum.FLY)),
                new Move(MoveTemplate.getMove(MoveEnum.FEATHER_DANCE)), new Move(MoveTemplate.getMove(MoveEnum.HYPER_BEAM)));
        pokemonExamples.put(example.getOriginalName(), example);

        example = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.RATTATA), 50, Ability.GUTS,
                new Move(MoveTemplate.getMove(MoveEnum.BITE)), new Move(MoveTemplate.getMove(MoveEnum.TAKE_DOWN)),
                new Move(MoveTemplate.getMove(MoveEnum.ENDEAVOR)), new Move(MoveTemplate.getMove(MoveEnum.FOCUS_ENERGY)));
        pokemonExamples.put(example.getOriginalName(), example);

        example = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.RATICATE), 50, Ability.GUTS,
                new Move(MoveTemplate.getMove(MoveEnum.CRUNCH)), new Move(MoveTemplate.getMove(MoveEnum.DOUBLE_EDGE)),
                new Move(MoveTemplate.getMove(MoveEnum.SUPER_FANG)), new Move(MoveTemplate.getMove(MoveEnum.LASER_FOCUS)));
        pokemonExamples.put(example.getOriginalName(), example);

        example = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.SPEAROW), 50, Ability.KEEN_EYE,
                new Move(MoveTemplate.getMove(MoveEnum.DRILL_PECK)), new Move(MoveTemplate.getMove(MoveEnum.WING_ATTACK)),
                new Move(MoveTemplate.getMove(MoveEnum.TAKE_DOWN)), new Move(MoveTemplate.getMove(MoveEnum.ROOST)));
        pokemonExamples.put(example.getOriginalName(), example);

        example = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.FEAROW), 50, Ability.KEEN_EYE,
                new Move(MoveTemplate.getMove(MoveEnum.DRILL_PECK)), new Move(MoveTemplate.getMove(MoveEnum.DRILL_RUN)),
                new Move(MoveTemplate.getMove(MoveEnum.GIGA_IMPACT)), new Move(MoveTemplate.getMove(MoveEnum.FACADE)));
        pokemonExamples.put(example.getOriginalName(), example);

        example = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.EKANS), 50, Ability.INTIMIDATE,
                new Move(MoveTemplate.getMove(MoveEnum.SLUDGE_BOMB)), new Move(MoveTemplate.getMove(MoveEnum.SCREECH)),
                new Move(MoveTemplate.getMove(MoveEnum.BITE)), new Move(MoveTemplate.getMove(MoveEnum.GASTRO_ACID)));
        pokemonExamples.put(example.getOriginalName(), example);

        example = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.ARBOK), 50, Ability.INTIMIDATE,
                new Move(MoveTemplate.getMove(MoveEnum.GUNK_SHOT)), new Move(MoveTemplate.getMove(MoveEnum.COIL)),
                new Move(MoveTemplate.getMove(MoveEnum.THUNDER_FANG)), new Move(MoveTemplate.getMove(MoveEnum.CRUNCH)));
        pokemonExamples.put(example.getOriginalName(), example);

        example = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.PIKACHU), 50, Ability.STATIC,
                new Move(MoveTemplate.getMove(MoveEnum.THUNDERBOLT)), new Move(MoveTemplate.getMove(MoveEnum.IRON_TAIL)),
                new Move(MoveTemplate.getMove(MoveEnum.ELECTRO_BALL)), new Move(MoveTemplate.getMove(MoveEnum.QUICK_ATTACK)));
        pokemonExamples.put(example.getOriginalName(), example);

        example = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.RAICHU), 50, Ability.STATIC,
                new Move(MoveTemplate.getMove(MoveEnum.THUNDER)), new Move(MoveTemplate.getMove(MoveEnum.RAIN_DANCE)),
                new Move(MoveTemplate.getMove(MoveEnum.SUBSTITUTE)), new Move(MoveTemplate.getMove(MoveEnum.VOLT_SWITCH)));
        pokemonExamples.put(example.getOriginalName(), example);
    }
}
