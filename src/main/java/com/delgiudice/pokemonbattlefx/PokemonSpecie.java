package com.delgiudice.pokemonbattlefx;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class PokemonSpecie {
    private String name;
    private Type[] type = {new Type(Enums.Types.NOTYPE), new Type(Enums.Types.NOTYPE)};
    private LinkedHashMap<String, Integer> baseStats = new LinkedHashMap<>();
    private static HashMap<String, PokemonSpecie> pokemonMap = new HashMap<>();
    private Image frontSprite, backSprite;

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

    public Image getBackSprite() {
        return backSprite;
    }

    public HashMap<String, Integer> getBaseStats() {
        return baseStats;
    }

    PokemonSpecie(String name, Type type1, Type type2, int maxHp, int attack, int defense, int spAttack, int spDefense,
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
    }

    PokemonSpecie(String name, Type type1, Type type2, int maxHp, int attack, int defense, int spAttack, int spDefense,
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

        if (frontSpriteUrl != null)
            this.frontSprite = new Image(frontSprite);
        else
            this.frontSprite = new Image("default.png");

        if (backSrpiteUrl != null)
            this.backSprite = new Image(backSprite);
        else
            this.backSprite = new Image("default.png");

        this.frontSprite = resample(this.frontSprite, 5);
        this.backSprite = resample(this.backSprite, 5);
    }

    PokemonSpecie(String name, Type type1, int maxHp, int attack, int defense, int spAttack, int spDefense, int speed){
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

        this.frontSprite = resample(this.frontSprite, 5);
        this.backSprite = resample(this.backSprite, 5);
    }

    PokemonSpecie(String name, Type type1, int maxHp, int attack, int defense, int spAttack, int spDefense, int speed,
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

        this.frontSprite = resample(this.frontSprite, 5);
        this.backSprite = resample(this.backSprite, 5);
    }

    public PokemonSpecie(PokemonSpecie original) {
        this.name = original.name;
        this.type = original.type;
        this.baseStats = original.baseStats;
        this.frontSprite = original.frontSprite;
    }

    public static void setPokemonList(){        //fills pokemon list, maybe some alternatives on how to execute this?
        MoveTemplate.setMoveList(); //first we initialize movelist

        PokemonSpecie newpkmn = new PokemonSpecie("Bulbasaur", Type.typeMap.get(Enums.Types.GRASS),
                Type.typeMap.get(Enums.Types.POISON), 45, 49, 49, 65, 65, 45,
                "/bulbasaur_front.png", "/bulbasaur_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie("Charmander", Type.typeMap.get(Enums.Types.FIRE),
                39, 52, 43, 60, 50, 65, "/charmander_front.png",
                "/charmander_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie("Rattata", Type.typeMap.get(Enums.Types.NORMAL),
                30, 56, 35, 25, 35, 72, "/rattata_front.png",
                "/rattata_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie("Squirtle", Type.typeMap.get(Enums.Types.WATER), 44, 48,
                65, 50, 64, 43, "/squirtle_front.png",
                "/squirtle_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie("Machop", Type.typeMap.get(Enums.Types.FIGHTING), 70, 80,
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
