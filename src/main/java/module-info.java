module com.delgiudice.pokemonbattlefx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.delgiudice.pokemonbattlefx to javafx.fxml;
    exports com.delgiudice.pokemonbattlefx;
}