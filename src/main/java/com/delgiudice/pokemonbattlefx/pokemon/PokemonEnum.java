package com.delgiudice.pokemonbattlefx.pokemon;

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

    PokemonEnum(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
