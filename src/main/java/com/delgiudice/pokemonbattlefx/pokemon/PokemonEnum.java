package com.delgiudice.pokemonbattlefx.pokemon;

import java.util.HashMap;

public enum PokemonEnum {
    BULBASAUR("Bulbasaur"),
    IVYSAUR("Ivysaur"),
    VENOSAUR("Venosaur"),
    CHARMANDER("Charmander"),
    CHARMELEON("Charmeleon"),
    CHARIZARD("Charizard"),
    RATTATA("Rattata"),
    SQUIRTLE("Squirtle"),
    MACHOP("Machop"),
    WARTORTLE("Wartortle"),
    BLASTOISE("Blastoise"),
    CATERPIE("Caterpie"),
    METAPOD("Metapod"),
    BUTTERFREE("Butterfree"),
    WEEDLE("Weedle"),
    KAKUNA("Kakuna"),
    BEEDRILL("Beedrill"),
    PIDGEY("Pidgey"),
    PIDGEOTTO("Pidgeotto"),
    PIDGEOT("Pidgeot"),
    RATICATE("Raticate"),
    SPEAROW("Spearow"),
    FEAROW("Fearow"),
    EKANS("Ekans"),
    ARBOK("Arbok"),
    PIKACHU("Pikachu"),
    RAICHU("Raichu"),
    SANDSHREW("Sandshrew");

    private final String name;

    private static HashMap<String, PokemonEnum> map = new HashMap<>();

    static {
        for (PokemonEnum pokemonEnum : PokemonEnum.values()) {
            map.put(pokemonEnum.name, pokemonEnum);
        }
    }

    public static PokemonEnum findByName(String name) {
        return map.get(name);
    }

    PokemonEnum(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
