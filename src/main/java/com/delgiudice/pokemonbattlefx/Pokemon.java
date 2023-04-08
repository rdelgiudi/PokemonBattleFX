package com.delgiudice.pokemonbattlefx;

import java.util.*;

public class Pokemon {
    private String name;
    private int hp;
    private byte level;
    private List<Move> moveList = new ArrayList<>();
    private LinkedHashMap<String, Integer> stats = new LinkedHashMap<>();
    private Enums.Status status;
    private int[] ivs = {0, 0, 0, 0, 0, 0};
    private Enums.Nature nature;
    private HashMap<String, Integer> statModifiers = new HashMap<>();
    private PokemonSpecie specie;

    public String getName() {
        return name;
    }

    public String getOName() {
        return specie.getName();
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public byte getLevel() {
        return level;
    }

    public HashMap<String, Integer> getStatModifiers() {
        return statModifiers;
    }

    public List<Move> getMoveList() {
        return moveList;
    }

    public Move getMoveList(int move) {
        return moveList.get(move);
    }

    public LinkedHashMap<String, Integer> getStats() {
        return stats;
    }

    public int getStats(String stat)
    {
        return stats.get(stat);
    }

    public Enums.Status getStatus() {
        return status;
    }

    public void setStatus(Enums.Status status) {
        this.status = status;
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
        return stats.get("Max HP");
    }

    Pokemon(PokemonSpecie specie, int level, Move move1)
    {
        this.name = specie.getName();
        this.specie = specie;
        this.level = (byte)level;
        moveList.add(move1);
        generateVariables();
        this.hp = stats.get("Max HP");
    }
    Pokemon(PokemonSpecie specie, int level, Move move1, Move move2)
    {
        this.name = specie.getName();
        this.specie = specie;
        this.level = (byte)level;
        moveList.add(move1);
        moveList.add(move2);
        generateVariables();
        this.hp = stats.get("Max HP");
    }
    Pokemon(PokemonSpecie specie, int level, Move move1, Move move2, Move move3)
    {
        this.name = specie.getName();
        this.specie = specie;
        this.level = (byte)level;
        moveList.add(move1);
        moveList.add(move2);
        moveList.add(move3);
        generateVariables();
        this.hp = stats.get("Max HP");
    }
    Pokemon(PokemonSpecie specie, int level, Move move1, Move move2, Move move3, Move move4)
    {
        this.name = specie.getName();
        this.specie = specie;
        this.level = (byte)level;
        moveList.add(move1);
        moveList.add(move2);
        moveList.add(move3);
        moveList.add(move4);
        generateVariables();
        this.hp = stats.get("Max HP");
    }
    Pokemon(Pokemon original)
    {
        this.name = original.name;
        this.specie = original.specie;
        this.hp = stats.get("Max HP");
        for (Move move : original.getMoveList())
        {
            moveList.add(move);
        }
    }

    public void levelUp()       //raises level, updates stats
    {
        int[] oldStats = new int[6];
        int i = 0;
        for(Map.Entry<String, Integer> stat : stats.entrySet()) {
            String statname = stat.getKey();
            int statval = stat.getValue();
            oldStats[i] = statval;
            i++;
        }
        level++;
        calculateStats();
        System.out.println("\n" + name + " leveled up to level " + level + "!\n");
        System.out.println("Stats increase:");
        i = 0;
        for (Map.Entry<String, Integer> stat : stats.entrySet()){
            String statname = stat.getKey();
            int statval = stat.getValue();
            int statDiff = statval - oldStats[i];
            i++;
            System.out.println(statname + ": +" + statDiff);
        }
        System.out.println("\nCurrent stats:");
        for (Map.Entry<String, Integer> stat : stats.entrySet()){
            String statname = stat.getKey();
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
        Random g = new Random();
        for (int i = 0; i < 6; i++)
        {
            ivs[i] = g.nextInt(32);
        }
    }

    public void generateNature()        //generates nature
    {
        Random g = new Random();
        int nature_num = g.nextInt(25);
        nature = Enums.Nature.valueOf(nature_num);
    }

    public void calculateStats()        //calculates pokemon statistics based on ivs and nature and level
    {
        int stat, basestat;
        basestat = specie.getBaseStats().get("Max HP");     //uses different formula, that's why it's separate
        stat = (int)(((2 * basestat + ivs[0])*level/100) + level + 10);
        stats.put("Max HP", stat);
        hp = stat;
        Iterator<Map.Entry<String, Integer>> it = specie.getBaseStats().entrySet().iterator();
        int i = 1;
        while(it.hasNext()) {       //iterates statistics, checks nature buffs/debuffs and calculates
            Map.Entry<String, Integer> pair = it.next();
            if (!pair.getKey().equals("Max HP"))  {
                basestat = pair.getValue();
                float naturemod = 0;
                switch (nature.getStatTab()[i - 1]) {
                    case 0 -> naturemod = 1;
                    case 1 -> naturemod = 1.1f;
                    case -1 -> naturemod = 0.9f;
                    default -> System.out.println("ERROR: Nature out of expected range!");
                }
                stat = (int)((((2 * basestat + ivs[0])*level/100) + 5) * naturemod);
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
}
