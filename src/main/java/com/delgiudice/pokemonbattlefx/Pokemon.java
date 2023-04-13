package com.delgiudice.pokemonbattlefx;

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
    // twoTurnMove - twoturn move that is currently in use
    // multiTurnMove - a multiturn move that is currently in use
    // trapMove - a trap move that is currently affecting the Pokemon
    private Move twoTurnMove = null, multiTurnMove = null, trapMove = null;
    // twoTurnCounter - counter that was planned to be used when processing twoturn moves, currently unused
    // multiTurnCounter - counter that tracks how many multiturn moves are to be executed
    // trappedTimer - counter that tracks how many more turns the Pokemon should be affected by trapping moves
    private int twoTurnCounter = 0, multiTurnCounter = 0, confusionTimer = 0, trappedTimer = 0;
    // ivs - Individual Values - random values between 0-31 that increase stats
    private int[] ivs = {0, 0, 0, 0, 0, 0};
    // nature - the nature of the Pokemon, that sometimes impact some of its stats
    private Enums.Nature nature;
    // statModifiers - modifiers applied on the Pokemon during battle
    private HashMap<Enums.StatType, Integer> statModifiers = new HashMap<>();
    // specie - specie of the Pokemon
    private PokemonSpecie specie;
    // subStatuses - currently applied subStatuses
    private List<Enums.SubStatus> subStatuses = new LinkedList<>();
    // ability - ability of the Pokemon
    private Ability ability = Ability.NONE;
    // trapped - determines whether the Pokemon is currently under effects of a trapping move
    // underFocusEnergy - determines whether the Pokemon is under the effects of the move Focus Energy
    private boolean trapped, underFocusEnergy = false;

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
    }

    public int getLevel() {
        return level;
    }

    public Ability getAbility() {
        return ability;
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

    public Move getTwoTurnMove() {
        return twoTurnMove;
    }

    public Move getMultiTurnMove() {
        return multiTurnMove;
    }

    public int getTwoTurnCounter() {
        return twoTurnCounter;
    }

    public int getMultiTurnCounter() {
        return multiTurnCounter;
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

    public boolean isUnderFocusEnergy() {
        return underFocusEnergy;
    }

    public void setOwner(Trainer owner) {
        this.owner = owner;
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

    public void setUnderFocusEnergy(boolean underFocusEnergy) {
        this.underFocusEnergy = underFocusEnergy;
    }

    public void setTwoTurnMove(Move twoTurnMove) {
        this.twoTurnMove = twoTurnMove;
    }

    public void setMultiTurnMove(Move multiTurnMove) {
        this.multiTurnMove = multiTurnMove;
    }

    public void setTwoTurnCounter(int twoTurnCounter) {
        this.twoTurnCounter = twoTurnCounter;
    }

    public void setMultiTurnCounter(int multiTurnCounter) {
        this.multiTurnCounter = multiTurnCounter;
    }

    public void setTrapMove(Move trapMove) {
        this.trapMove = trapMove;
    }

    public void setConfusionTimer(int confusionTimer) {
        this.confusionTimer = confusionTimer;
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
        this.ivs = original.ivs;
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
    }
}
