package com.delgiudice.pokemonbattlefx;

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
    BLASTOISE("Blastoise");

    private final String name;

    PokemonEnum(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
