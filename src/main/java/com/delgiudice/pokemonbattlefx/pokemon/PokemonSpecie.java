package com.delgiudice.pokemonbattlefx.pokemon;

import com.delgiudice.pokemonbattlefx.BattleApplication;
import com.delgiudice.pokemonbattlefx.attributes.Enums;
import com.delgiudice.pokemonbattlefx.move.MoveTemplate;
import com.delgiudice.pokemonbattlefx.attributes.Type;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


public class PokemonSpecie{

    private static double ANIMATED_ALLY_SCALE = 6, ANIMATED_ENEMY_SCALE = 3.5;

    // List of all animated sprites parsed from site
    private static final List<String> frontSpritesAnim = new ArrayList<>(), backSpritesAnim = new ArrayList<>();

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
    // frontSprite - path to image of the Pokemon front sprite (enemy)
    // backSprite - path to image of the Pokemon back sprite (ally)
    private String frontSprite, backSprite, frontSpriteAnim, backSpriteAnim;
    // Internet sprites loaded into memory
    private Image frontSpriteLoaded = null, backSpriteLoaded = null;
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


    private static void initSprites(String url, List<String> urlList) {
        NodeList imgs;
        List<String> srcs = new ArrayList<String>();
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.setRequestProperty("User-Agent", "Wget/1.13.4 (linux-gnu)");
            InputStream input = conn.getInputStream();
            Document document = new Tidy().parseDOM(input, null);
            imgs = document.getElementsByTagName("img");
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (int i = 0; i < imgs.getLength(); i++) {
            srcs.add(imgs.item(i).getAttributes().getNamedItem("src").getNodeValue());
        }

        for (String src: srcs) {
            if (src.contains(".gif") && src.contains("sprites"))
                    urlList.add(src);

        }
    }

    private static void initSpriteDatabase() {
       initSprites("", backSpritesAnim);
       initSprites("", frontSpritesAnim);
    }

    // Loading images from the Web: https://itecnote.com/tecnote/javafx-play-gif-image-in-imageview/
    // The image is loaded twice to first probe the width and height of the image
    // Resizing an animated image once loaded is not supported by the implemented methods
    private Image createImageUrl(String url, boolean front, boolean thumbnail) throws IOException {
        URLConnection conn = new URL(url).openConnection();
        conn.setRequestProperty("User-Agent", "Wget/1.13.4 (linux-gnu)");
        Image image;
        try (InputStream stream = conn.getInputStream()) {
            if (thumbnail)
                return new Image(stream, 50, 50, true, false);
            image = new Image(stream);
        }
        conn = new URL(url).openConnection();
        conn.setRequestProperty("User-Agent", "Wget/1.13.4 (linux-gnu)");
        try (InputStream stream = conn.getInputStream()) {
            return new Image(stream, image.getWidth() * (front ? ANIMATED_ENEMY_SCALE : ANIMATED_ALLY_SCALE),
                    image.getHeight() * (front ? ANIMATED_ENEMY_SCALE : ANIMATED_ALLY_SCALE),
                    true, false);
        }
    }

    private Image createImage(String sprite, boolean align, int scaleFactor) {
        Image image;
        URL frontSpriteUrl = getClass().getResource(sprite);

        if (frontSpriteUrl != null)
            //image = new Image(frontSprite, 200, 200, true, false);
            image = new Image(sprite);
        else
            image = new Image("sprites/default.png");

        if (align)
            image = alignBottom(image);
        image = resample(image, scaleFactor);

        return image;
    }

    private Image createAnimImage(String sprite, boolean front, boolean align, boolean thumbnail) {
        Image image;
        String spriteAnim = sprite.split("\\.")[0] + ".gif";
        URL frontSpriteUrl = getClass().getResource(spriteAnim);

        if (frontSpriteUrl != null) {
            if (!thumbnail) {
                image = new Image(spriteAnim);
                image = new Image(spriteAnim, image.getWidth() * (front ? ANIMATED_ENEMY_SCALE : ANIMATED_ALLY_SCALE),
                        image.getHeight() * (front ? ANIMATED_ENEMY_SCALE : ANIMATED_ALLY_SCALE),
                        true, false);
            }
            else
                image = new Image(spriteAnim, 50, 50, true, false);
        }
        else {
            image = createImage(sprite, align, thumbnail ? 2 : 5);
        }

        return image;
    }

    private Image loadSpriteImage(boolean front, boolean align, boolean thumbnail) {
        if (front && frontSpriteLoaded != null)
            return frontSpriteLoaded;
        else if (!front && backSpriteLoaded != null)
            return backSpriteLoaded;

        Image image;
        String sprite = front ? frontSprite : backSprite;
        String spriteAnim = front ? frontSpriteAnim : backSpriteAnim;

        if (BattleApplication.isUseLocalAnimSprites()) {
            image = createAnimImage(sprite, front, align, thumbnail);
        }
        else if (BattleApplication.isUseInternetSprites()) {
            try {
                image = createImageUrl(spriteAnim, front, thumbnail);
            } catch (IOException e) {
                image = createImage(sprite, align, thumbnail ? 2 : 5);
            }

        }
        else {
            image = createImage(sprite, align, thumbnail ? 2 : 5);
        }
        return image;
    }

    public void loadNetImage(boolean onlyFront) {
        frontSpriteLoaded = loadSpriteImage(true, false, false);
        if (!onlyFront)
            backSpriteLoaded = loadSpriteImage(false, false, false);
    }

    public static void unloadNetImages() {
        for (PokemonSpecie pokemonSpecie : pokemonMap.values()) {
            pokemonSpecie.frontSpriteLoaded = null;
            pokemonSpecie.backSpriteLoaded = null;
        }
        System.gc();
    }

    public Image getFrontSprite() {
        return loadSpriteImage(true, false, false);
    }

    public Image getFrontSpriteBattle() {
        return loadSpriteImage(true, true, false);
    }

    public Image getFrontSpriteThumbnail() {
        return loadSpriteImage(true, false, true);
    }

    public Image getBackSpriteBattle() {
        return loadSpriteImage(false, true, false);
    }

    public Image getBackSprite() {
        return loadSpriteImage(false, false, false);
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
        frontSprite = "sprites/default.png";
        backSprite = "sprites/default.png";
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
        this.frontSprite = frontSprite;
        this.backSprite = backSprite;
        this.pokedexNumber = pokedexNumber;

        if (BattleApplication.isUseInternetSprites()) {
            searchForAnimSprite();
        }
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
        frontSprite = "sprites/default.png";
        backSprite = "sprites/default.png";
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
        this.frontSprite = frontSprite;
        this.backSprite = backSprite;
        this.pokedexNumber = pokedexNumber;

        if (BattleApplication.isUseInternetSprites()) {
            searchForAnimSprite();
        }
    }

    public PokemonSpecie(PokemonSpecie original) {
        this.name = original.name;
        this.type = original.type;
        this.baseStats = original.baseStats;
        this.frontSprite = original.frontSprite;
        this.backSprite = original.backSprite;
        this.pokedexNumber = original.pokedexNumber;
    }

    private void searchForAnimSprite() {
        String spriteUrl = "https://www.pokencyclopedia.info";
        String pokedexNumFormat = String.format("%03d", pokedexNumber);
        String pokedexNumFormatGender = pokedexNumFormat + "_m";
        Optional<String> frontSpriteLoc = frontSpritesAnim.stream().filter(string -> string.contains(pokedexNumFormatGender)).findFirst();
        if (!frontSpriteLoc.isPresent())
            frontSpriteLoc = frontSpritesAnim.stream().filter(string -> string.contains(pokedexNumFormat)).findFirst();

        if (frontSpriteLoc.isPresent()) {
            String frontSpriteAnimTemp = frontSpriteLoc.get().substring(2);
            frontSpriteAnim = spriteUrl + frontSpriteAnimTemp;
        }

        Optional<String> backSpriteLoc = backSpritesAnim.stream().filter(string -> string.contains(pokedexNumFormatGender)).findFirst();
        if (!backSpriteLoc.isPresent())
            backSpriteLoc = backSpritesAnim.stream().filter(string -> string.contains(pokedexNumFormat)).findFirst();

        if (backSpriteLoc.isPresent()) {
            String backSpriteAnimTemp = backSpriteLoc.get().substring(2);
            backSpriteAnim = spriteUrl + backSpriteAnimTemp;
        }
    }

    public static void setPokemonMap(){        //fills pokemon list, maybe some alternatives on how to execute this?
        MoveTemplate.setMoveMap(); //first we initialize movelist
        if (BattleApplication.isUseInternetSprites())
            initSpriteDatabase();

        PokemonSpecie newpkmn = new PokemonSpecie(1,PokemonEnum.BULBASAUR, Type.getTypeMap().get(Enums.Types.GRASS),
                Type.getTypeMap().get(Enums.Types.POISON), 45, 49, 49, 65, 65, 45,
                "/sprites/bulbasaur_front.png", "/sprites/bulbasaur_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(2, PokemonEnum.IVYSAUR, Type.getTypeMap().get(Enums.Types.GRASS),
                Type.getTypeMap().get(Enums.Types.POISON), 60, 62, 63, 80, 80, 60,
                "/sprites/ivysaur_front.png", "/sprites/ivysaur_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(3, PokemonEnum.VENOSAUR, Type.getTypeMap().get(Enums.Types.GRASS),
                Type.getTypeMap().get(Enums.Types.POISON), 80, 82, 83, 100, 100, 80,
                "/sprites/venosaur_front.png", "/sprites/venosaur_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(4,PokemonEnum.CHARMANDER, Type.getTypeMap().get(Enums.Types.FIRE),
                39, 52, 43, 60, 50, 65, "/sprites/charmander_front.png",
                "/sprites/charmander_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(5, PokemonEnum.CHARMELEON, Type.getTypeMap().get(Enums.Types.FIRE),
                58, 64, 58, 80, 65, 80, "/sprites/charmeleon_front.png",
                "/sprites/charmeleon_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(6, PokemonEnum.CHARIZARD, Type.getTypeMap().get(Enums.Types.FIRE),
                Type.getTypeMap().get(Enums.Types.FLYING), 78, 84, 78, 109, 85, 100,
                "/sprites/charizard_front.png", "/sprites/charizard_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(19,PokemonEnum.RATTATA, Type.getTypeMap().get(Enums.Types.NORMAL),
                30, 56, 35, 25, 35, 72, "/sprites/rattata_front.png",
                "/sprites/rattata_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(7,PokemonEnum.SQUIRTLE, Type.getTypeMap().get(Enums.Types.WATER), 44, 48,
                65, 50, 64, 43, "/sprites/squirtle_front.png",
                "/sprites/squirtle_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(8, PokemonEnum.WARTORTLE, Type.getTypeMap(Enums.Types.WATER), 59, 63,
                80, 65, 80, 58, "/sprites/wartortle_front.png",
                "/sprites/wartortle_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(66,PokemonEnum.MACHOP, Type.getTypeMap().get(Enums.Types.FIGHTING), 70, 80,
                50, 35, 35, 35, "/sprites/machop_front.png", "/sprites/machop_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(9, PokemonEnum.BLASTOISE, Type.getTypeMap(Enums.Types.WATER), 79,
                83, 100, 85, 105, 78, "/sprites/blastoise_front.png",
                "/sprites/blastoise_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(10, PokemonEnum.CATERPIE, Type.getTypeMap(Enums.Types.BUG), 45,
                30, 35, 20, 20, 45, "/sprites/caterpie_front.png",
                "/sprites/caterpie_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(11, PokemonEnum.METAPOD, Type.getTypeMap(Enums.Types.BUG), 50,
                20, 55, 25, 25, 30, "/sprites/metapod_front.png",
                "/sprites/metapod_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(12, PokemonEnum.BUTTERFREE, Type.getTypeMap(Enums.Types.BUG), Type.getTypeMap(Enums.Types.FLYING),
                60, 45, 50, 90, 80, 70, "/sprites/butterfree_front.png",
                "/sprites/butterfree_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(13, PokemonEnum.WEEDLE, Type.getTypeMap(Enums.Types.BUG), Type.getTypeMap(Enums.Types.POISON),
                40, 35, 30, 20, 20, 50, "/sprites/weedle_front.png",
                "/sprites/weedle_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(14, PokemonEnum.KAKUNA, Type.getTypeMap(Enums.Types.BUG), Type.getTypeMap(Enums.Types.POISON),
                45, 25, 50, 25, 25, 35, "/sprites/kakuna_front.png",
                "/sprites/kakuna_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(15, PokemonEnum.BEEDRILL, Type.getTypeMap(Enums.Types.BUG), Type.getTypeMap(Enums.Types.POISON),
                65, 90, 40, 45, 80, 75, "/sprites/beedrill_front.png",
                "/sprites/beedrill_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(16, PokemonEnum.PIDGEY, Type.getTypeMap(Enums.Types.NORMAL), Type.getTypeMap(Enums.Types.FLYING),
                40, 45, 40, 35, 35, 56, "/sprites/pidgey_front.png",
                "/sprites/pidgey_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(17, PokemonEnum.PIDGEOTTO, Type.getTypeMap(Enums.Types.NORMAL), Type.getTypeMap(Enums.Types.FLYING),
                63, 60, 55, 50, 50 ,71, "/sprites/pidgeotto_front.png",
                "/sprites/pidgeotto_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(18, PokemonEnum.PIDGEOT, Type.getTypeMap(Enums.Types.NORMAL), Type.getTypeMap(Enums.Types.FLYING),
                83, 80, 75, 70, 70, 101, "/sprites/pidgeot_front.png",
                "/sprites/pidgeot_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(20, PokemonEnum.RATICATE, Type.getTypeMap(Enums.Types.NORMAL), 55,
                81, 60, 50, 70, 97, "/sprites/raticate_front.png",
                "/sprites/raticate_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(21, PokemonEnum.SPEAROW, Type.getTypeMap(Enums.Types.NORMAL), Type.getTypeMap(Enums.Types.FLYING),
                40, 60, 30, 31, 31, 70, "/sprites/spearow_front.png",
                "/sprites/spearow_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(22, PokemonEnum.FEAROW, Type.getTypeMap(Enums.Types.NORMAL), Type.getTypeMap(Enums.Types.FLYING),
                65, 90, 65, 61, 61, 100, "/sprites/fearow_front.png",
                "/sprites/fearow_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(23, PokemonEnum.EKANS, Type.getTypeMap(Enums.Types.POISON), 35,
                60, 44, 40, 54, 55, "/sprites/ekans_front.png",
                "/sprites/ekans_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(24, PokemonEnum.ARBOK, Type.getTypeMap(Enums.Types.POISON), 60,
            95, 69, 65, 79, 80, "/sprites/arbok_front.png",
            "/sprites/arbok_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(25, PokemonEnum.PIKACHU, Type.getTypeMap(Enums.Types.ELECTRIC), 35,
                55, 40, 50, 50, 90, "/sprites/pikachu_front.png",
                "/sprites/pikachu_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(26, PokemonEnum.RAICHU, Type.getTypeMap(Enums.Types.ELECTRIC), 60,
                90, 55, 90, 80, 110, "/sprites/raichu_front.png",
                "/sprites/raichu_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);

        newpkmn = new PokemonSpecie(27, PokemonEnum.SANDSHREW, Type.getTypeMap(Enums.Types.GROUND), 50,
                75, 85, 20, 30, 40, "/sprites/sandshrew_front.png",
                "/sprites/sandshrew_back.png");
        pokemonMap.put(newpkmn.getName(), newpkmn);
    }

    //https://stackoverflow.com/questions/16089304/javafx-imageview-without-any-smoothing
    public static Image resample(Image input, int scaleFactor) {

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

    public static Image alignBottom(Image input) {
        final int W = (int) input.getWidth();
        final int H = (int) input.getHeight();

        WritableImage output = new WritableImage(W, H);

        PixelReader reader = input.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        double maxY = 0;

        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                Color color = reader.getColor(x, y);
                double opacity = color.getOpacity();
                if (opacity != 0) {
                    maxY = Math.max(maxY, y);
                }
            }
        }

        double distance = input.getHeight() - 1 - maxY;
        int dist = (int) distance;

        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                final int argb = reader.getArgb(x, y);
                final Color color = reader.getColor(x, y);
                if (color.getOpacity() != 0)
                    writer.setArgb(x, y + dist, argb);
            }
        }

        return output;
    }

//    private Image alignBackSprite(Image input) {
//        final int W = (int) input.getWidth();
//        final int H = (int) input.getHeight();
//
//        WritableImage output = new WritableImage(W, H);
//
//        PixelReader reader = input.getPixelReader();
//        PixelWriter writer = output.getPixelWriter();
//
//        double maxY = 0;
//
//        for (int y = 0; y < H; y++) {
//            for (int x = 0; x < W; x++) {
//                Color color = reader.getColor(x, y);
//                double opacity = color.getOpacity();
//                if (opacity != 0) {
//                    maxY = Math.max(maxY, y);
//                }
//            }
//        }
//
//        double distance = (2.0/3.0) * input.getWidth();
//        int dist = (int) (maxY - distance);
//        if (dist <= 0) return input;
//
//        for (int y = 0; y < H; y++) {
//            for (int x = 0; x < W; x++) {
//                final int argb = reader.getArgb(x, y);
//                final Color color = reader.getColor(x, y);
//                if (color.getOpacity() != 0)
//                    writer.setArgb(x, y - dist, argb);
//            }
//        }
//
//        return output;
//    }
}


