package com.delgiudice.pokemonbattlefx;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;


public class PokemonSpecie {
    private String name;
    private int pokedexNumber;
    private Type[] type = {new Type(Enums.Types.NOTYPE), new Type(Enums.Types.NOTYPE)};
    private LinkedHashMap<String, Integer> baseStats = new LinkedHashMap<>();
    private static HashMap<String, PokemonSpecie> pokemonMap = new HashMap<>();
    private Image frontSprite, backSprite, frontSpriteAnim, backSpriteAnim;

    public int getPokedexNumber() {
        return pokedexNumber;
    }

    public static HashMap<String, PokemonSpecie> getPokemonMap() {
        return pokemonMap;
    }

    public String getName() {
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

    public HashMap<String, Integer> getBaseStats() {
        return baseStats;
    }

    PokemonSpecie(int pokedexNumber ,String name, Type type1, Type type2, int maxHp, int attack, int defense, int spAttack, int spDefense,
                  int speed){
        this.name = name;
        this.type[0] = type1;
        this.type[1] = type2;
        baseStats.put("Max HP", maxHp);
        baseStats.put("Attack", attack);
        baseStats.put("Defense", defense);
        baseStats.put("Special Attack", spAttack);
        baseStats.put("Special Defense", spDefense);
        baseStats.put("Speed", speed);
        frontSprite = new Image("default.png");
        backSprite = new Image("default.png");
        this.frontSprite = resample(this.frontSprite, 10);
        this.backSprite = resample(this.backSprite, 10);
        this.frontSpriteAnim = null;
        this.backSpriteAnim = null;
        this.pokedexNumber = pokedexNumber;
    }

    PokemonSpecie(int pokedexNumber ,String name, Type type1, Type type2, int maxHp, int attack, int defense, int spAttack, int spDefense,
                  int speed, String frontSprite, String backSprite){
        this.name = name;
        this.type[0] = type1;
        this.type[1] = type2;
        baseStats.put("Max HP", maxHp);
        baseStats.put("Attack", attack);
        baseStats.put("Defense", defense);
        baseStats.put("Special Attack", spAttack);
        baseStats.put("Special Defense", spDefense);
        baseStats.put("Speed", speed);
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

    PokemonSpecie(int pokedexNumber ,String name, Type type1, int maxHp, int attack, int defense, int spAttack, int spDefense, int speed){
        this.name = name;
        this.type[0] = type1;
        baseStats.put("Max HP", maxHp);
        baseStats.put("Attack", attack);
        baseStats.put("Defense", defense);
        baseStats.put("Special Attack", spAttack);
        baseStats.put("Special Defense", spDefense);
        baseStats.put("Speed", speed);
        frontSprite = new Image("default.png");
        backSprite = new Image("default.png");

        this.frontSprite = resample(this.frontSprite, 10);
        this.backSprite = resample(this.backSprite, 10);

        this.pokedexNumber = pokedexNumber;
    }

    PokemonSpecie(int pokedexNumber ,String name, Type type1, int maxHp, int attack, int defense, int spAttack, int spDefense, int speed,
                  String frontSprite, String backSprite){
        this.name = name;
        this.type[0] = type1;
        baseStats.put("Max HP", maxHp);
        baseStats.put("Attack", attack);
        baseStats.put("Defense", defense);
        baseStats.put("Special Attack", spAttack);
        baseStats.put("Special Defense", spDefense);
        baseStats.put("Speed", speed);
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

        PokemonSpecie newpkmn = new PokemonSpecie(1,"Bulbasaur", Type.typeMap.get(Enums.Types.GRASS),
                Type.typeMap.get(Enums.Types.POISON), 45, 49, 49, 65, 65, 45,
                "/bulbasaur_front.png", "/bulbasaur_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(2, "Ivysaur", Type.typeMap.get(Enums.Types.GRASS),
                Type.typeMap.get(Enums.Types.POISON), 60, 62, 63, 80, 80, 60,
                "/ivysaur_front.png", "/ivysaur_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(3, "Venosaur", Type.typeMap.get(Enums.Types.GRASS),
                Type.typeMap.get(Enums.Types.POISON), 80, 82, 83, 100, 100, 80,
                "/venosaur_front.png", "/venosaur_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(4,"Charmander", Type.typeMap.get(Enums.Types.FIRE),
                39, 52, 43, 60, 50, 65, "/charmander_front.png",
                "/charmander_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(5, "Charmeleon", Type.typeMap.get(Enums.Types.FIRE),
                58, 64, 58, 80, 65, 80, "charmeleon_front.png",
                "/charmeleon_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(6, "Charizard", Type.typeMap.get(Enums.Types.FIRE),
                Type.typeMap.get(Enums.Types.FLYING), 78, 84, 78, 109, 85, 100,
                "/charizard_front.png", "/charizard_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(19,"Rattata", Type.typeMap.get(Enums.Types.NORMAL),
                30, 56, 35, 25, 35, 72, "/rattata_front.png",
                "/rattata_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(7,"Squirtle", Type.typeMap.get(Enums.Types.WATER), 44, 48,
                65, 50, 64, 43, "/squirtle_front.png",
                "/squirtle_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(66,"Machop", Type.typeMap.get(Enums.Types.FIGHTING), 70, 80,
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


