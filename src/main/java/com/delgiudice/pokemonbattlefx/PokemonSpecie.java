package com.delgiudice.pokemonbattlefx;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;


public class PokemonSpecie {

    // pokemonMap - a map of all available Pokemon species
    private static final HashMap<PokemonEnum, PokemonSpecie> pokemonMap = new HashMap<>();
    // name - name of the Pokemon in form of an enum, can also return string if needed
    private PokemonEnum name;
    // pokedexNumber - the number in the PokeDex occupied by this specie
    private int pokedexNumber;
    // type - type of the Pokemon
    private Type[] type = {new Type(Enums.Types.NO_TYPE), new Type(Enums.Types.NO_TYPE)};
    // baseStats - base statistics of a specie, from which their actual stats are calculated
    private LinkedHashMap<Enums.StatType, Integer> baseStats = new LinkedHashMap<>();
    // frontSprite - image of the Pokemon front sprite (enemy)
    // backSprite - image of the Pokemon back sprite (ally)
    private Image frontSprite, backSprite, frontSpriteAnim, backSpriteAnim;
    public int getPokedexNumber() {
        return pokedexNumber;
    }

    public static HashMap<PokemonEnum, PokemonSpecie> getPokemonMap() {
        return pokemonMap;
    }

    public PokemonEnum getName() {
        return name;
    }

    public Type[] getType() {
        return type;
    }

    public Image getFrontSprite() {
        return frontSprite;}

    //public Image getFrontSpriteAnim() {
    //    return frontSpriteAnim;}

    //public Image getBackSpriteAnim() {
    //    return backSpriteAnim;}

    public Image getBackSprite() {
        return backSprite;
    }

    public void setPokedexNumber(int pokedexNumber) {
        this.pokedexNumber = pokedexNumber;
    }

    public HashMap<Enums.StatType, Integer> getBaseStats() {
        return baseStats;
    }

    public PokemonSpecie(int pokedexNumber, PokemonEnum name, Type type1, Type type2, int maxHp, int attack, int defense, int spAttack, int spDefense,
                  int speed){
        this.name = name;
        this.type[0] = type1;
        this.type[1] = type2;
        baseStats.put(Enums.StatType.MAX_HP, maxHp);
        baseStats.put(Enums.StatType.ATTACK, attack);
        baseStats.put(Enums.StatType.DEFENSE, defense);
        baseStats.put(Enums.StatType.SPECIAL_ATTACK, spAttack);
        baseStats.put(Enums.StatType.SPECIAL_DEFENSE, spDefense);
        baseStats.put(Enums.StatType.SPEED, speed);
        frontSprite = new Image("default.png");
        backSprite = new Image("default.png");
        this.frontSprite = resample(this.frontSprite, 10);
        this.backSprite = resample(this.backSprite, 10);
        this.frontSpriteAnim = null;
        this.backSpriteAnim = null;
        this.pokedexNumber = pokedexNumber;
    }

    public PokemonSpecie(int pokedexNumber, PokemonEnum name, Type type1, Type type2, int maxHp, int attack, int defense, int spAttack, int spDefense,
                  int speed, String frontSprite, String backSprite){
        this.name = name;
        this.type[0] = type1;
        this.type[1] = type2;
        baseStats.put(Enums.StatType.MAX_HP, maxHp);
        baseStats.put(Enums.StatType.ATTACK, attack);
        baseStats.put(Enums.StatType.DEFENSE, defense);
        baseStats.put(Enums.StatType.SPECIAL_ATTACK, spAttack);
        baseStats.put(Enums.StatType.SPECIAL_DEFENSE, spDefense);
        baseStats.put(Enums.StatType.SPEED, speed);
        URL frontSpriteUrl = getClass().getResource(frontSprite);
        URL backSrpiteUrl = getClass().getResource(backSprite);
        //String frontSpriteAnim = frontSprite.split("\\.")[0];

        //frontSpriteAnim = frontSpriteAnim + "_anim.png";
        //URL frontSpriteAnimUrl = getClass().getResource(frontSpriteAnim);

        if (frontSpriteUrl != null)
            this.frontSprite = new Image(frontSprite);
        else
            this.frontSprite = new Image("default.png");

        if (backSrpiteUrl != null)
            this.backSprite = new Image(backSprite);
        else
            this.backSprite = new Image("default.png");
        //if (frontSpriteAnimUrl != null)
        //    this.frontSpriteAnim = new Image(frontSpriteAnim);
        //else
        //    this.frontSpriteAnim = null;


        this.frontSprite = resample(this.frontSprite, 10);
        this.backSprite = resample(this.backSprite, 10);
        //if (this.frontSpriteAnim != null)
        //    this.frontSpriteAnim = resample(this.frontSpriteAnim, 10);

        this.pokedexNumber = pokedexNumber;
    }

    public PokemonSpecie(int pokedexNumber, PokemonEnum name, Type type1, int maxHp, int attack, int defense, int spAttack, int spDefense, int speed){
        this.name = name;
        this.type[0] = type1;
        baseStats.put(Enums.StatType.MAX_HP, maxHp);
        baseStats.put(Enums.StatType.ATTACK, attack);
        baseStats.put(Enums.StatType.DEFENSE, defense);
        baseStats.put(Enums.StatType.SPECIAL_ATTACK, spAttack);
        baseStats.put(Enums.StatType.SPECIAL_DEFENSE, spDefense);
        baseStats.put(Enums.StatType.SPEED, speed);
        frontSprite = new Image("default.png");
        backSprite = new Image("default.png");

        this.frontSprite = resample(this.frontSprite, 10);
        this.backSprite = resample(this.backSprite, 10);

        this.pokedexNumber = pokedexNumber;
    }

    public PokemonSpecie(int pokedexNumber, PokemonEnum name, Type type1, int maxHp, int attack, int defense, int spAttack, int spDefense, int speed,
                  String frontSprite, String backSprite){
        this.name = name;
        this.type[0] = type1;
        baseStats.put(Enums.StatType.MAX_HP, maxHp);
        baseStats.put(Enums.StatType.ATTACK, attack);
        baseStats.put(Enums.StatType.DEFENSE, defense);
        baseStats.put(Enums.StatType.SPECIAL_ATTACK, spAttack);
        baseStats.put(Enums.StatType.SPECIAL_DEFENSE, spDefense);
        baseStats.put(Enums.StatType.SPEED, speed);
        URL frontSpriteUrl = getClass().getResource(frontSprite);
        URL backSrpiteUrl = getClass().getResource(backSprite);

        if (frontSpriteUrl != null)
            this.frontSprite = new Image(frontSprite);
        else
            this.frontSprite = new Image("default.png");

        if (backSrpiteUrl != null)
            this.backSprite = new Image(backSprite);
        else
            this.backSprite = new Image("default.png");

        this.frontSprite = resample(this.frontSprite, 10);
        this.backSprite = resample(this.backSprite, 10);

        this.pokedexNumber = pokedexNumber;
    }

    public PokemonSpecie(PokemonSpecie original) {
        this.name = original.name;
        this.type = original.type;
        this.baseStats = original.baseStats;
        this.frontSprite = original.frontSprite;
        this.backSprite = original.backSprite;
        this.pokedexNumber = original.pokedexNumber;
    }

    public static void setPokemonMap(){        //fills pokemon list, maybe some alternatives on how to execute this?
        MoveTemplate.setMoveMap(); //first we initialize movelist

        PokemonSpecie newpkmn = new PokemonSpecie(1,PokemonEnum.BULBASAUR, Type.getTypeMap().get(Enums.Types.GRASS),
                Type.getTypeMap().get(Enums.Types.POISON), 45, 49, 49, 65, 65, 45,
                "/bulbasaur_front.png", "/bulbasaur_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(2, PokemonEnum.IVYSAUR, Type.getTypeMap().get(Enums.Types.GRASS),
                Type.getTypeMap().get(Enums.Types.POISON), 60, 62, 63, 80, 80, 60,
                "/ivysaur_front.png", "/ivysaur_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(3, PokemonEnum.VENOSAUR, Type.getTypeMap().get(Enums.Types.GRASS),
                Type.getTypeMap().get(Enums.Types.POISON), 80, 82, 83, 100, 100, 80,
                "/venosaur_front.png", "/venosaur_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(4,PokemonEnum.CHARMANDER, Type.getTypeMap().get(Enums.Types.FIRE),
                39, 52, 43, 60, 50, 65, "/charmander_front.png",
                "/charmander_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(5, PokemonEnum.CHARMELEON, Type.getTypeMap().get(Enums.Types.FIRE),
                58, 64, 58, 80, 65, 80, "charmeleon_front.png",
                "/charmeleon_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(6, PokemonEnum.CHARIZARD, Type.getTypeMap().get(Enums.Types.FIRE),
                Type.getTypeMap().get(Enums.Types.FLYING), 78, 84, 78, 109, 85, 100,
                "/charizard_front.png", "/charizard_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(19,PokemonEnum.RATTATA, Type.getTypeMap().get(Enums.Types.NORMAL),
                30, 56, 35, 25, 35, 72, "/rattata_front.png",
                "/rattata_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(7,PokemonEnum.SQUIRTLE, Type.getTypeMap().get(Enums.Types.WATER), 44, 48,
                65, 50, 64, 43, "/squirtle_front.png",
                "/squirtle_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(66,PokemonEnum.MACHOP, Type.getTypeMap().get(Enums.Types.FIGHTING), 70, 80,
                50, 35, 35, 35, "/machop_front.png", "/machop_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);
    }

    //https://stackoverflow.com/questions/16089304/javafx-imageview-without-any-smoothing
    private Image resample(Image input, int scaleFactor) {

        final int W = (int) input.getWidth();
        final int H = (int) input.getHeight();
        final int S = scaleFactor;

        WritableImage output = new WritableImage(
                W * S,
                H * S
        );
        PixelReader reader = input.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                final int argb = reader.getArgb(x, y);
                for (int dy = 0; dy < S; dy++) {
                    for (int dx = 0; dx < S; dx++) {
                        writer.setArgb(x * S + dx, y * S + dy, argb);
                    }
                }
            }
        }

        return output;
    }
}


