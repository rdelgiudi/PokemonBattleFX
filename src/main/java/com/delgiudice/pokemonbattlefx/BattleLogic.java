package com.delgiudice.pokemonbattlefx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Time;
import java.util.*;

public class BattleLogic {

    private final BattleController controller;

    private static final String POKEMON_FAINTED_STRING = "%s fainted!";
    private static final String POKEMON_SENT_OUT_STRING = "Go! %s!";
    private static final String MOVE_NO_EFFECT_STRING = "It doesn't affect%n%s...";
    private static final String RECHARGE_INFO = "%s needs to recharge!";
    private static final Font MAIN_FONT = Font.font("Monospaced");

    private Player player;
    private NpcTrainer enemy;

    private final FXMLLoader summaryLoader;
    private final Scene summaryScene;
    private final Scene teamBuilderScene;

    private int currentAllyPokemon = 0, currentEnemyPokemon = 0;
    private boolean enemySentOut, allySentOut;
    private final HashMap<Enums.BattlefieldCondition, Integer> allyBattlefieldConditions =
            new HashMap<Enums.BattlefieldCondition, Integer>();
    private final HashMap<Enums.BattlefieldCondition, Integer> enemyBattlefieldConditions =
            new HashMap<Enums.BattlefieldCondition, Integer>();
    private final HashMap<Enums.Spikes, Integer> allySpikes =
            new HashMap<Enums.Spikes, Integer>();
    private final HashMap<Enums.Spikes, Integer> enemySpikes =
            new HashMap<Enums.Spikes, Integer>();

    boolean inBattle;

    public BattleLogic(BattleController controller, Player player, NpcTrainer enemy, Scene teamBuilderScene) {
        this.controller = controller;
        inBattle = true;

        this.player = player;
        this.enemy = enemy;

        summaryLoader = new FXMLLoader(BattleApplication.class.getResource("summary-view.fxml"));
        try {
            summaryScene = new Scene(summaryLoader.load(), 1280, 720);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        this.teamBuilderScene = teamBuilderScene;

        initBattleLoop();
    }

    private void setup() {
        Pokemon.generatePokemonExamples();

        // For testing purposes only, delete later
        Pokemon allyPokemon = new Pokemon(Pokemon.getPokemonExamples().get(PokemonEnum.BLASTOISE));
        player = new Player("Red",  allyPokemon);

        player.addPokemon(new Pokemon(Pokemon.getPokemonExamples().get(PokemonEnum.VENOSAUR)));

        player.addPokemon(new Pokemon(Pokemon.getPokemonExamples().get(PokemonEnum.CHARIZARD)));

        Pokemon enemyPokemon = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.VENOSAUR), 50, Ability.NONE,
                new Move(MoveTemplate.getMoveMap().get(MoveEnum.TAIL_WHIP)));
        //Pokemon enemyPokemon = new Pokemon(PokemonSpecie.getPokemonMap().get("Rattata"), 50, Ability.GUTS,
        //        new Move(MoveTemplate.getMoveMap().get("Scratch")) , new Move(MoveTemplate.getMoveMap().get("Growl")),
        //        new Move(MoveTemplate.getMoveMap().get("Quick Attack")));

        enemy = new NpcTrainer("Joey", Enums.TrainerTypes.YOUNGSTER ,enemyPokemon);

        //enemy.addPokemon(new Pokemon(Pokemon.getPokemonExamples().get("Charmander")));

    }

    // function that initiates a battle, adding looping checks here should be avoided
    private void initBattleLoop() {

        boolean allyPokemonSelected = checkIfAllyAbleToBattle(true);
        boolean enemyPokemonSelected = checkIfEnemyAbleToBattle(true);

        if (!allyPokemonSelected) {
            battleLost();
        }

        if (!enemyPokemonSelected) {
            battleWon();
        }

        enemySentOut = true;
        allySentOut = true;

        controller.updateStatus(player.getParty(currentAllyPokemon), true).play();
        controller.updateStatus(enemy.getParty(currentEnemyPokemon), false).play();

        Timeline battleTextIntro = controller.getBattleTextAnimation(String.format("%s %s%nwants to battle!",
                enemy.getTrainerType().toString(), enemy.getName()), true);

        battleTextIntro.setDelay(Duration.seconds(1));

        Timeline enemyPokemonIntro = controller.getBattleTextAnimation(String.format("%s %s%nsends out %s!",
                enemy.getTrainerType().toString(), enemy.getName(),
                enemy.getParty(currentEnemyPokemon).getName()), true);
        controller.battleTextAdvanceByUserInput(battleTextIntro, enemyPokemonIntro);
        //enemyPokemonIntro.setDelay(Duration.seconds(1));
        //battleTextIntro.setOnFinished(e -> enemyPokemonIntro.play());

        //controller.setEnemyInformation(enemy.getParty(currentEnemyPokemon));
        Timeline enemyInfoAnimation = controller.getEnemyInfoAnimation(enemy.getParty(currentEnemyPokemon),
                enemy.getParty(currentEnemyPokemon).getHp());
        enemyPokemonIntro.setOnFinished(e -> enemyInfoAnimation.play());

        Timeline allyPokemonIntro = controller.getBattleTextAnimation(String.format(POKEMON_SENT_OUT_STRING,
                player.getParty(currentAllyPokemon).getBattleName()), true);
        allyPokemonIntro.setDelay(Duration.seconds(1));
        enemyInfoAnimation.setOnFinished(e -> allyPokemonIntro.play());

        Timeline allyInfoAnimation = controller.getAllyInfoAnimation(player.getParty(currentAllyPokemon),
                player.getParty(currentAllyPokemon).getHp());
        allyPokemonIntro.setOnFinished(e -> allyInfoAnimation.play());

        allyInfoAnimation.setOnFinished(e -> {
            battleLoop();
        });

        battleTextIntro.play();
    }

    private boolean checkIfAllyAbleToBattle(boolean set) {
        int index = 0;

        for (Pokemon pokemon : player.getParty()) {
            if (pokemon.getHp() > 0) {
                if (set)
                    switchPokemon(true, index);
                return true;
            }
            index++;
        }
        return false;
    }

    private boolean checkIfEnemyAbleToBattle(boolean set) {
        int index = 0;

        for (Pokemon pokemon : enemy.getParty()) {
            if (pokemon.getHp() > 0) {
                if (set)
                    switchPokemon(false, index);
                return true;
            }
            index++;
        }
        return false;
    }

    private void battleLost() {
        controller.switchToPlayerChoice(false);
        Stage stage = (Stage) controller.getFightButton().getScene().getWindow();

        Timeline battleLostMsg1 = controller.getBattleTextAnimation(String.format("%s is out%nof usable Pokemon!",
                player.getName()), true);
        Timeline battleLostMsg2 = controller.getBattleTextAnimation(String.format("%s whited out!", player.getName()),
                true);
        Timeline battleLostMsg3 = controller.getBattleTextAnimation("Returning to Team Builder...",
                true);
        Timeline paused = controller.generatePause(1000);

        //battleLostMsg2.setDelay(Duration.seconds(2));
        //battleLostMsg3.setDelay(Duration.seconds(2));
        controller.battleTextAdvanceByUserInput(battleLostMsg1, battleLostMsg2);
        controller.battleTextAdvanceByUserInput(battleLostMsg2 ,battleLostMsg3);
        //battleLostMsg1.setOnFinished(e -> battleLostMsg2.play());
        //battleLostMsg2.setOnFinished(e -> battleLostMsg3.play());
        battleLostMsg3.setOnFinished(e -> paused.play());
        paused.setOnFinished(e -> stage.setScene(teamBuilderScene));

        for (Pokemon pokemon : player.getParty()) {
            pokemon.restoreAll();
        }
        for (Pokemon pokemon : enemy.getParty()) {
            pokemon.restoreAll();
        }

        battleLostMsg1.play();
    }

    private void battleWon() {
        controller.switchToPlayerChoice(false);
        Stage stage = (Stage) controller.getFightButton().getScene().getWindow();

        Timeline battleWonMsg1 = controller.getBattleTextAnimation(String.format("%s defeated%n%s %s!", player.getName(),
                enemy.getTrainerType().toString(), enemy.getName()), true);
        Timeline battleWonMsg2 = controller.getBattleTextAnimation("Returning to Team Builder...", true);
        Timeline paused = controller.generatePause(1000);

        //battleWonMsg1.setDelay(Duration.seconds(2));
        //battleWonMsg2.setDelay(Duration.seconds(2));
        controller.battleTextAdvanceByUserInput(battleWonMsg1, battleWonMsg2);
        //battleWonMsg1.setOnFinished(e -> battleWonMsg2.play());
        battleWonMsg2.setOnFinished(e -> paused.play());
        paused.setOnFinished(e -> stage.setScene(teamBuilderScene));

        for (Pokemon pokemon : player.getParty()) {
            pokemon.restoreAll();
        }
        for (Pokemon pokemon : enemy.getParty()) {
            pokemon.restoreAll();
        }

        battleWonMsg1.play();
    }

    private void processToxicSpikeEffect(List<Timeline> battleTimeLine, Pokemon target) {
        boolean toxicSpikeImmuneType = target.containsType(Enums.Types.FLYING) ||
                target.containsType(Enums.Types.POISON) || target.containsType(Enums.Types.STEEL);
        boolean toxicSpikeImmuneAbility = false; //TODO: Add abilities

        HashMap<Enums.Spikes, Integer> spikes = target.getOwner().isPlayer() ? allySpikes : enemySpikes;

        if (!toxicSpikeImmuneType && !toxicSpikeImmuneAbility) {
            int spikeCount = spikes.get(Enums.Spikes.TOXIC_SPIKES);
            Timeline statusChange;
            if (spikeCount == 1)
                statusChange = processStatusChange(Enums.Status.POISONED, target);

            else
                statusChange = processStatusChange(Enums.Status.BADLY_POISONED, target);

            battleTimeLine.add(statusChange);
            battleTimeLine.add(controller.updateStatus(target, target.getOwner().isPlayer()));
            battleTimeLine.add(controller.generatePause(2000));
        }
    }

    private void applySentOutEffects(List<Timeline> battleTimeLine) {

        Pokemon allyPokemon = player.getParty(currentAllyPokemon);
        Pokemon enemyPokemon = enemy.getParty(currentEnemyPokemon);

        if (enemySentOut) {
            enemySentOut = false;

            if (enemySpikes.containsKey(Enums.Spikes.TOXIC_SPIKES)) {
                processToxicSpikeEffect(battleTimeLine, enemyPokemon);
            }

            boolean enemyFainted = enemy.getParty(currentEnemyPokemon).getHp() == 0;
            if (enemyFainted) {
                processEnemyFainted(battleTimeLine);
            }
        }
        else if (allySentOut) {
            allySentOut = false;

            if (allySpikes.containsKey(Enums.Spikes.TOXIC_SPIKES)) {
                processToxicSpikeEffect(battleTimeLine, allyPokemon);
            }

            boolean allyFainted = player.getParty(currentAllyPokemon).getHp() == 0;
            if (allyFainted) {
                processAllyFainted(battleTimeLine);
            }
        }

        if (checkBattleEnd(battleTimeLine))
            return;
    }

    private void battleLoop() {

        controller.wipeText();

        Pokemon allyPokemon = player.getParty(currentAllyPokemon);
        Pokemon enemyPokemon = enemy.getParty(currentEnemyPokemon);
        allyPokemon.getSubStatuses().remove(Enums.SubStatus.FLINCHED);
        enemyPokemon.getSubStatuses().remove(Enums.SubStatus.FLINCHED);
        Timeline done = controller.generatePause(1);

        Timeline playerChoiceDialog = controller.getBattleTextAnimation(String.format("What will%n%s do?",
                player.getParty(currentAllyPokemon).getBattleName()), false);
        done.setOnFinished(e -> {
            playerChoiceDialog.play();
            controller.switchToPlayerChoice(true);

            if (allyPokemon.getTwoTurnMove() != null) {
                battleTurn(allyPokemon.getTwoTurnMove());
                return;
            }
            if (allyPokemon.getMultiTurnMove() != null && allyPokemon.getMultiTurnCounter() > 0) {
                //allyPokemon.setMultiTurnCounter(allyPokemon.getMultiTurnCounter() - 1);
                battleTurn(allyPokemon.getMultiTurnMove());
                return;
            }

            if (allyPokemon.getSubStatuses().contains(Enums.SubStatus.RECHARGE)) {
                battleTurn(null);
                return;
            }
        });

        List<Timeline> moveStartTimeLine = new LinkedList<>();

        applySentOutEffects(moveStartTimeLine);


        if (moveStartTimeLine.size() > 0) {
            initAnimationQueue(moveStartTimeLine);
            moveStartTimeLine.get(moveStartTimeLine.size() - 1).setOnFinished(e -> done.play());
            moveStartTimeLine.get(0).play();
        }
        else {
            done.play();
        }
        //playerChoiceDialog.play();

        Button fightButton = controller.getFightButton();
        Button backButton = controller.getBackMoveButton();
        Button pokemonButton = controller.getPokemonButton();
        Button bagButton = controller.getBagButton();
        Button runButton = controller.getRunButton();
        List<Move> moveList = player.getParty(currentAllyPokemon).getMoveList();

        int index = 0;

        for (Move move : moveList) {
            Button moveButton = (Button)controller.getMoveGrid().getChildren().get(index++);
            if (move.getPp() > 0)
                moveButton.setOnAction(e -> {
                    battleTurn(move);
                });
            else
                moveButton.setOnAction(e -> {
                    controller.getMoveGrid().setDisable(true);
                    Timeline outOfPPInfo = controller.getBattleTextAnimation("This move is out of PP!", false);
                    Timeline playerChoiceDialog2 = controller.getBattleTextAnimation(String.format("What will%n%s do?",
                            player.getParty(currentAllyPokemon).getBattleName()), false);
                    playerChoiceDialog2.setDelay(Duration.seconds(1));
                    outOfPPInfo.setOnFinished(event -> playerChoiceDialog2.play());
                    playerChoiceDialog2.setOnFinished(event -> {
                        controller.getMoveGrid().setDisable(false);
                    });
                    outOfPPInfo.play();
                });
        }

        fightButton.setOnAction(e -> {
            boolean outOfPP = true;
            for (boolean available : player.getParty(currentAllyPokemon).checkAvailableMoves()) {
                if (available) {
                    outOfPP = false;
                    break;
                }
            }
            if(!outOfPP)
                controller.fightButtonPressed(player.getParty(currentAllyPokemon));
            else {
                controller.switchToPlayerChoice(false);
                Timeline noMovesLeftMessage = controller.getBattleTextAnimation(String.format("%s has no%nmoves left!",
                        player.getParty(currentAllyPokemon).getBattleName()), true);
                Timeline shortPause = controller.generatePause(1);
                shortPause.setOnFinished(event -> battleTurn(new Move(MoveTemplate.getMoveMap().get(MoveEnum.STRUGGLE))));
                controller.battleTextAdvanceByUserInput(noMovesLeftMessage, shortPause);
                noMovesLeftMessage.play();
            }
        });

        backButton.setOnAction(e -> {
            controller.backButtonPressed();
        });

        pokemonButton.setOnAction( e -> {
            controller.pokemonButtonPressed(player.getParty());
            setPokemonSwapListeners(false);
        });

    }

    private void setPokemonSwapListeners(boolean allyFainted) {

        int iLimit = Math.min(player.getParty().size() - 1, 2);
        int jLimit = player.getParty().size() > 3 ? 1: 0;
        controller.updateSelectPokemonButtons(player.getParty());

        if (allyFainted) {
            Timeline selectNewPokemonMessage = controller.getBattleTextAnimation("Select new Pokemon...",
                    false);
            selectNewPokemonMessage.play();
        }

        for (int j=0; j <= jLimit; j++) {
            for (int i = 0; i <= iLimit; i++) {
                int partyIndex = i + (3 * j);

                Button button = (Button) controller.getNodeFromGridPane(controller.getPokemonGrid(), i, j);
                Pokemon pokemon = player.getParty(partyIndex);
                ContextMenu contextMenu = new ContextMenu();
                MenuItem switchOut = new MenuItem("Switch out");
                MenuItem summary = new MenuItem("Summary");
                MenuItem cancel = new MenuItem("Cancel");
                button.setContextMenu(contextMenu);

                switchOut.setOnAction(event -> {

                    if (player.getParty(currentAllyPokemon).getTrapMove() != null && !allyFainted) {
                        controller.getPokemonGrid().setDisable(true);
                        Timeline trappedInfo = controller.getBattleTextAnimation(String.format(
                                "%s is trapped in vortex!%nCan't switch!",
                                player.getParty(currentAllyPokemon).getName()), false);
                        Timeline resetText = controller.getBattleTextAnimation(String.format("What will%n%s do?",
                                player.getParty(currentAllyPokemon).getName()), false);
                        resetText.setDelay(Duration.seconds(1));
                        trappedInfo.setOnFinished(actionEvent -> resetText.play());
                        resetText.setOnFinished(actionEvent -> {
                            controller.getPokemonGrid().setDisable(false);
                        });
                        trappedInfo.play();
                        return;
                    }

                    if (partyIndex == currentAllyPokemon) {
                        button.setDisable(allyFainted);
                        controller.getPokemonGrid().setDisable(true);
                        Timeline alreadyInBattleInfo = controller.getBattleTextAnimation(String.format(
                                "%s is already%nin battle!", pokemon.getBattleName()), false);
                        Timeline resetText = controller.getBattleTextAnimation(String.format("What will%n%s do?",
                                pokemon.getBattleName()), false);
                        resetText.setDelay(Duration.seconds(1));
                        alreadyInBattleInfo.setOnFinished(actionEvent -> resetText.play());
                        resetText.setOnFinished(actionEvent -> {
                            controller.getPokemonGrid().setDisable(false);
                        });
                        alreadyInBattleInfo.play();
                        return;
                    }

                    String currentAllyName = player.getParty(currentAllyPokemon).getBattleName();

                    controller.getPokemonGrid().setVisible(false);
                    controller.switchToPlayerChoice(false);

                    List<Timeline> battleTimeLine = new LinkedList<>();

                    if (!allyFainted) {
                        Timeline allyPokemonReturnText = controller.getBattleTextAnimation(String.format(
                                "That's enough %s,%ncome back!", currentAllyName), true);
                        allyPokemonReturnText.setDelay(Duration.seconds(0.1));
                        battleTimeLine.add(allyPokemonReturnText);

                        Timeline allyPokemonReturn = controller.getPokemonFaintedAnimation(true);
                        allyPokemonReturn.setDelay(Duration.seconds(1));
                        battleTimeLine.add(allyPokemonReturn);
                        battleTimeLine.add(controller.generatePause(1000));
                    }

                    switchPokemon(true, partyIndex);

                    //controller.setAllyInformation(player.getParty(currentAllyPokemon));
                    Timeline allyPokemonIntro = controller.getBattleTextAnimation(String.format(POKEMON_SENT_OUT_STRING,
                            player.getParty(currentAllyPokemon).getBattleName()), true);
                    battleTimeLine.add(allyPokemonIntro);

                    Timeline updateStatus = controller.updateStatus(player.getParty(currentAllyPokemon), true);
                    battleTimeLine.add(updateStatus);

                    Timeline allyInfoAnimation = controller.getAllyInfoAnimation(player.getParty(currentAllyPokemon),
                            player.getParty(currentAllyPokemon).getHp());
                    battleTimeLine.add(allyInfoAnimation);

                    //SecureRandom generator = new SecureRandom();
                    //int enemyMoveIndex = generator.nextInt(enemy.getParty(currentEnemyPokemon).getMoveList().size());
                    //Move enemyMove = enemy.getParty(currentEnemyPokemon).getMoveList(enemyMoveIndex);
                    if (!allyFainted) {
                        //Move enemyMove = generateEnemyMove(enemy.getParty(currentEnemyPokemon));
                        //List<Timeline> enemyMoveList = useMove(enemyMove, enemy.getParty(currentEnemyPokemon),
                        //        player.getParty(currentAllyPokemon), false);
                        //battleTimeLine.addAll(enemyMoveList);
                        //checkFainted(battleTimeLine);
                        initAnimationQueue(battleTimeLine);
                        battleTimeLine.get(battleTimeLine.size() - 1).setOnFinished(e -> battleTurn(null));
                        battleTimeLine.get(0).play();
                    }
                    else {
                        finalChecks(battleTimeLine);
                    }
                });

                summary.setOnAction(event -> {
                    Scene scene = button.getScene();

                    Stage stage = (Stage) button.getScene().getWindow();
                    SummaryController summaryController = summaryLoader.getController();
                    summaryController.setParty(player.getParty());
                    summaryController.displayPokemonStat(partyIndex);
                    summaryController.setPreviousScene(scene);

                    stage.setScene(summaryScene);
                    //stage.show();
                });

                //Button cancelButton = (Button) controller.getPokemonSubmenu().getChildren().get(2);

                cancel.setOnAction(event -> contextMenu.hide());

                contextMenu.getItems().addAll(switchOut, summary, cancel);

                button.setOnMouseClicked(e -> {
                    //Button switchOutButton = (Button)controller.getPokemonSubmenu().getChildren().get(0);
                    //Button summaryButton = (Button) controller.getPokemonSubmenu().getChildren().get(1);
                    contextMenu.show(button, e.getScreenX(), e.getScreenY());
                    //controller.getPokemonSubmenu().setLayoutX(e.getSceneX());
                    //controller.getPokemonSubmenu().setLayoutY(e.getSceneY() - controller.getPokemonSubmenu().getPrefHeight());
                    //controller.getPokemonSubmenu().setVisible(true);
                });
            }
        }

        Button button = controller.getPokemonBackButton();
        button.setText("Back");
        button.setFont(MAIN_FONT);
        button.setDisable(allyFainted);

        button.setOnAction(e -> {
            controller.getPokemonGrid().setVisible(false);
            controller.switchToPlayerChoice(true);
        });
    }

    private void processBattlefieldConditionsTimer() {
        for (Map.Entry<Enums.BattlefieldCondition, Integer> condition : allyBattlefieldConditions.entrySet()) {
            condition.setValue(condition.getValue() - 1);
        }
        for (Map.Entry<Enums.BattlefieldCondition, Integer> condition : enemyBattlefieldConditions.entrySet()) {
            condition.setValue(condition.getValue() - 1);
        }
    }

    private void processStatusEffectCounters(Pokemon pokemon) {
        if (pokemon.getStatus() == Enums.Status.SLEEPING && pokemon.getSleepCounter() > 0)
            pokemon.setSleepCounter(pokemon.getSleepCounter() - 1);
        if (pokemon.getSubStatuses().contains(Enums.SubStatus.CONFUSED) && pokemon.getConfusionTimer() > 0)
            pokemon.setConfusionTimer(pokemon.getConfusionTimer() - 1);
    }

    private void battleTurn(Move allyMove) {
        List<Timeline> battleTimeLine = new LinkedList<>();

        SecureRandom generator = new SecureRandom();
        applySentOutEffects(battleTimeLine);

        // sets timers to timer-1, then a check is made at the end of the turn that erases conditions that should be disabled
        processBattlefieldConditionsTimer();

        controller.getMoveGrid().setVisible(false);
        controller.switchToPlayerChoice(false);

        Pokemon allyPokemon = player.getParty(currentAllyPokemon);
        Pokemon enemyPokemon = enemy.getParty(currentEnemyPokemon);
        processStatusEffectCounters(allyPokemon);
        processStatusEffectCounters(enemyPokemon);

        Move enemyMove = !enemyPokemon.getSubStatuses().contains(Enums.SubStatus.RECHARGE) ?
                generateEnemyMove(enemyPokemon) : null;

        // Speed calculation
        double playerSpeed = allyPokemon.getStats().get(Enums.StatType.SPEED);
        double enemySpeed = enemyPokemon.getStats().get(Enums.StatType.SPEED);
        playerSpeed = allyPokemon.getStatus() == Enums.Status.PARALYZED ? playerSpeed / 2: playerSpeed;
        enemySpeed = enemyPokemon.getStatus() == Enums.Status.PARALYZED ? enemySpeed / 2: enemySpeed;
        int playerSpeedModifier = allyPokemon.getStatModifiers().get(Enums.StatType.SPEED);
        int enemySpeedModifier = enemyPokemon.getStatModifiers().get(Enums.StatType.SPEED);
        if (playerSpeedModifier >= 0)
            playerSpeed = playerSpeed * (playerSpeedModifier + 2) / 2.0;
        else {
            playerSpeed = playerSpeed * 2 / (2.0 - playerSpeedModifier);
        }
        if (enemySpeedModifier >= 0)
            enemySpeed = enemySpeed * (enemySpeedModifier + 2) / 2.0;
        else {
            enemySpeed = enemySpeed * 2 / (2.0 - enemySpeedModifier);
        }

        playerSpeed = Math.round(playerSpeed);
        enemySpeed = Math.round(enemySpeed);


        if (allyBattlefieldConditions.containsKey(Enums.BattlefieldCondition.TAILWIND))
            playerSpeed *= 2;
        if (enemyBattlefieldConditions.containsKey(Enums.BattlefieldCondition.TAILWIND))
            enemySpeed *= 2;

        boolean faster = playerSpeed > enemySpeed;
        boolean tied = playerSpeed == enemySpeed;
        //******************************************


        int allyPriority = allyMove != null ? allyMove.getPriority() : 0;
        int enemyPriority = enemyMove != null ? enemyMove.getPriority() : 0;

        // Move of higher priority is always faster
        if (allyPriority > enemyPriority) {
            battleTimeLine.addAll(processTurn(allyMove, allyPokemon, enemyMove, enemyPokemon));
        }
        else if (allyPriority < enemyPriority) {
            battleTimeLine.addAll(processTurn(enemyMove, enemyPokemon, allyMove, allyPokemon));
        }
        // If both or neither parties are using priority moves, being faster depends on speed
        else if (faster) {
            battleTimeLine.addAll(processTurn(allyMove, allyPokemon, enemyMove, enemyPokemon));
        }
        //On speed tie, the first attacker is randomized
        else if (tied) {
            int flip = generator.nextInt(2);
            if (flip == 1) {
                battleTimeLine.addAll(processTurn(allyMove, allyPokemon, enemyMove, enemyPokemon));
            }
            else {
                battleTimeLine.addAll(processTurn(enemyMove, enemyPokemon, allyMove, allyPokemon));
            }
        }
        //Here is what happens if enemy Pokemon is faster
        else {
            battleTimeLine.addAll(processTurn(enemyMove, enemyPokemon, allyMove, allyPokemon));
        }

        battleTurnEnd(battleTimeLine);
    }

    // For now enemy move is randomly generated, maybe gym leader AI implementation in the future
    private Move generateEnemyMove(Pokemon enemyPokemon) {
        Move enemyMove;

        if (enemyPokemon.getMultiTurnMove() != null & enemyPokemon.getTwoTurnMove() != null)
            throw new IllegalStateException("Both multiturn and two turn active at the same time");
        if (enemyPokemon.getMultiTurnMove() != null && enemyPokemon.getMultiTurnCounter() > 0) {
            enemyMove = enemyPokemon.getMultiTurnMove();
            return enemyMove;
        }
        if (enemyPokemon.getTwoTurnMove() != null) {
            enemyMove = enemyPokemon.getTwoTurnMove();
            return enemyMove;
        }
        if (enemyPokemon.getSubStatuses().contains(Enums.SubStatus.RECHARGE)) {
            return null;
        }
        boolean[] moveAvailable = enemyPokemon.checkAvailableMoves();
        List<Integer> availableIndices = new ArrayList<>();

        for (int i=0; i < moveAvailable.length; i++) {
            if (moveAvailable[i])
                availableIndices.add(i);
        }
        if (availableIndices.size() == 0) {
            enemyMove = new Move(MoveTemplate.getMoveMap().get(MoveEnum.STRUGGLE));
            return enemyMove;
        }

        SecureRandom generator = new SecureRandom();
        int enemyMoveIndex = generator.nextInt(availableIndices.size());

        enemyMove = enemyPokemon.getMoveList(availableIndices.get(enemyMoveIndex));

        return enemyMove;
    }

    private void battleTurnEnd(List<Timeline> battleTimeLine) {
        boolean allyFainted = player.getParty(currentAllyPokemon).getHp() == 0;
        boolean enemyFainted = enemy.getParty(currentEnemyPokemon).getHp() == 0;

        if (allyFainted) {
            processAllyFainted(battleTimeLine);
        }
        if (enemyFainted) {
            processEnemyFainted(battleTimeLine);
        }

        if (checkBattleEnd(battleTimeLine))
            return;

        applyStatusEffectDamage(battleTimeLine);
        //finalChecks(battleTimeLine, enemyFainted);
    }

    // Applies damaging status effect if applicable
    private void applyStatusEffectDamage(List<Timeline> battleTimeLine) {
        boolean allyFainted = player.getParty(currentAllyPokemon).getHp() == 0;
        boolean enemyFainted = enemy.getParty(currentEnemyPokemon).getHp() == 0;
        List<Timeline> allyStatusEffect = null;
        List<Timeline> enemyStatusEffect = null;

        if (!enemyFainted)
            enemyStatusEffect = processDamageStatusEffects(enemy.getParty(currentEnemyPokemon));

        enemyFainted = enemy.getParty(currentEnemyPokemon).getHp() == 0;

        if (enemyStatusEffect != null) {
            battleTimeLine.addAll(enemyStatusEffect);
            if (enemyFainted) {
                processEnemyFainted(battleTimeLine);
            }
        }

        if (!allyFainted)
            allyStatusEffect = processDamageStatusEffects(player.getParty(currentAllyPokemon));

        allyFainted = player.getParty(currentAllyPokemon).getHp() == 0;

        if (allyStatusEffect != null) {
            battleTimeLine.addAll(allyStatusEffect);
            if (allyFainted) {
                processAllyFainted(battleTimeLine);
            }
        }

        if (checkBattleEnd(battleTimeLine))
            return;

        applyTrappedEffects(battleTimeLine);
    }

    private void applyTrappedEffects(List<Timeline> battleTimeLine) {
        boolean allyFainted = player.getParty(currentAllyPokemon).getHp() == 0;
        boolean enemyFainted = enemy.getParty(currentEnemyPokemon).getHp() == 0;
        List<Timeline> allyTrappedEffect = null;
        List<Timeline> enemyTrappedEffect = null;


        if (!allyFainted)
            allyTrappedEffect = processDamageTrapped(player.getParty(currentAllyPokemon));
        allyFainted = player.getParty(currentAllyPokemon).getHp() == 0;

        if (allyTrappedEffect != null) {
            battleTimeLine.addAll(allyTrappedEffect);
            if (allyFainted) {
                processAllyFainted(battleTimeLine);
            }
        }

        if (!enemyFainted)
            enemyTrappedEffect = processDamageTrapped(enemy.getParty(currentEnemyPokemon));
        enemyFainted = enemy.getParty(currentEnemyPokemon).getHp() == 0;

        if (enemyTrappedEffect != null) {
            battleTimeLine.addAll(enemyTrappedEffect);
            if (enemyFainted) {
                processEnemyFainted(battleTimeLine);
            }
        }

        if (checkBattleEnd(battleTimeLine))
            return;

        finalChecks(battleTimeLine);
    }
    private boolean checkBattleEnd(List<Timeline> battleTimeLine) {
        boolean allyFainted = player.getParty(currentAllyPokemon).getHp() == 0;
        boolean enemyFainted = enemy.getParty(currentEnemyPokemon).getHp() == 0;

        if (allyFainted && enemyFainted) {
            if (!checkIfEnemyAbleToBattle(false) && checkIfAllyAbleToBattle(false)) {
                initAnimationQueue(battleTimeLine);

                battleTimeLine.get(battleTimeLine.size() - 1).setOnFinished(e -> {
                    battleWon();
                });

                battleTimeLine.get(0).play();
                return true;
            }
        }

        if (allyFainted) {
            initAnimationQueue(battleTimeLine);

            if (!checkIfAllyAbleToBattle(false)) {
                battleTimeLine.get(battleTimeLine.size() - 1).setOnFinished(e -> {
                    battleLost();
                });
            }

            battleTimeLine.get(0).play();
            return true;
        }

        if (enemyFainted) {
            if (!checkIfEnemyAbleToBattle(false)) {
                initAnimationQueue(battleTimeLine);

                battleTimeLine.get(battleTimeLine.size() - 1).setOnFinished(e -> {
                    battleWon();
                });

                battleTimeLine.get(0).play();
                return true;
            }
        }
        return false;
    }

    // Send out new ally Pokemon if available, else game over for the player, even if enemy also out of Pokemon
    // Send out new enemy Pokemon and config timeline list
    private void finalChecks(List<Timeline> battleTimeLine) {

        if (checkBattleEnd(battleTimeLine))
            return;

        boolean allyFainted = player.getParty(currentAllyPokemon).getHp() == 0;
        boolean enemyFainted = enemy.getParty(currentEnemyPokemon).getHp() == 0;

        if (allyFainted) {
            battleTimeLine.get(battleTimeLine.size() - 1).setOnFinished(e -> {
                controller.pokemonButtonPressed(player.getParty());
                controller.switchToPlayerChoice(true);
                setPokemonSwapListeners(true);
            });
        }

        if (enemyFainted) {

            checkIfEnemyAbleToBattle(true);

            Timeline enemyNewPokemonInfo = controller.getBattleTextAnimation(String.format("%s %s sends out%n%s!",
                            enemy.getTrainerType().toString(), enemy.getName(), enemy.getParty(currentEnemyPokemon).getName()),
                    true);
            //enemyNewPokemonInfo.setDelay(Duration.seconds(2));
            battleTimeLine.add(enemyNewPokemonInfo);

            Timeline updateStatus = controller.updateStatus(enemy.getParty(currentEnemyPokemon), false);
            battleTimeLine.add(updateStatus);
            battleTimeLine.add(controller.generatePause(1000));

            Timeline pokemonIntroAnimation = controller.getEnemyInfoAnimation(enemy.getParty(currentEnemyPokemon),
                    enemy.getParty(currentEnemyPokemon).getHp());
            battleTimeLine.add(pokemonIntroAnimation);
        }

        deleteEndedConditions(battleTimeLine);

        initAnimationQueue(battleTimeLine);

        battleTimeLine.get(battleTimeLine.size() - 1).setOnFinished(e -> {
            battleLoop();
        });

        battleTimeLine.get(0).play();
    }

    // Deletes battlefield conditions that have reached their end
    private void deleteEndedConditions(List<Timeline> battleTimeLine) {
        for (Map.Entry<Enums.BattlefieldCondition, Integer> condition : allyBattlefieldConditions.entrySet()) {
            if (condition.getValue() <= 0) {
                battleTimeLine.add(controller.getBattleTextAnimation(String.format("Ally's %s has ended!",
                        condition.getKey()), true));
                battleTimeLine.add(controller.generatePause(1500));
                allyBattlefieldConditions.remove(condition.getKey());
            }
        }

        for (Map.Entry<Enums.BattlefieldCondition, Integer> condition : enemyBattlefieldConditions.entrySet()) {
            if (condition.getValue() <= 0) {
                battleTimeLine.add(controller.getBattleTextAnimation(String.format("Foe's %s has ended!",
                        condition.getKey()), true));
                battleTimeLine.add(controller.generatePause(1500));
                enemyBattlefieldConditions.remove(condition.getKey());
            }
        }
    }

    private void initAnimationQueue(List<Timeline> battleTimeLine) {
        for (int i=1; i<battleTimeLine.size(); i++) {
            final int finalI = i;
            if (battleTimeLine.get(i-1).getOnFinished() == null)
                battleTimeLine.get(i-1).setOnFinished(e -> battleTimeLine.get(finalI).play());
        }
    }

    // Calculates damage dealt as a result of vortex traps like Fire Spin
    private List<Timeline> processDamageTrapped(Pokemon pokemon) {

        List<Timeline> timelineList = new LinkedList<>();

        if (pokemon.getTrappedTimer() > 0 && pokemon.getTrapMove() != null) {
            double damageDouble = 0;

            Timeline damageInfoTimeline = controller.getBattleTextAnimation(String.format("%s was hurt by%n%s!",
                    pokemon.getBattleName(), pokemon.getTrapMove().getName()), true);
            //damageInfoTimeline.setDelay(Duration.seconds(2));
            timelineList.add(damageInfoTimeline);

            damageDouble = Math.round(pokemon.getMaxHP() / 8.0);
            if (damageDouble == 0)
                damageDouble = 1;

            int damage = (int) damageDouble;
            int oldHp = pokemon.getHp();

            if (damage > pokemon.getHp())
                damage = pokemon.getHp();

            pokemon.setHp(pokemon.getHp() - damage);

            Timeline damageTimeline;
            if (pokemon.getOwner().isPlayer())
                damageTimeline = controller.getAllyHpAnimation(oldHp, pokemon.getHp(), pokemon.getMaxHP());
            else
                damageTimeline = controller.getEnemyHpAnimation(oldHp, pokemon.getHp(), pokemon.getMaxHP());

            timelineList.add(damageTimeline);
            timelineList.add(controller.generatePause(1000));

            pokemon.setTrappedTimer(pokemon.getTrappedTimer() - 1);

            return timelineList;
        }
        else if (pokemon.getTrappedTimer() == 0 && pokemon.getTrapMove() != null) {
            pokemon.setTrapMove(null);

            Timeline pokemonFreed = controller.getBattleTextAnimation
                    (String.format("%s was freed from the vortex!", pokemon.getBattleName()), true);
            //pokemonFreed.setDelay(Duration.seconds(2));
            timelineList.add(pokemonFreed);
            timelineList.add(controller.generatePause(1000));

            return timelineList;
        }

        return null;
    }

    //Processes damaging status effects (poison and burn)
    private List<Timeline> processDamageStatusEffects(Pokemon pokemon) {

        double damageDouble = 0;
        Timeline damageInfoTimeline = null;
        String poisonDamageMessage = "%s was hurt by%npoison!";
        boolean damagingStatusEffect = pokemon.getStatus() == Enums.Status.BURNED ||
                pokemon.getStatus() == Enums.Status.POISONED || pokemon.getStatus() == Enums.Status.BADLY_POISONED;

        if (pokemon.getAbility() == Ability.SHED_SKIN && damagingStatusEffect) {
            SecureRandom random = new SecureRandom();
            int rand = random.nextInt(3);

            if (rand == 0) {
                List<Timeline> timelineList = new LinkedList<>();
                pokemon.setStatus(Enums.Status.NONE);
                Timeline abilityPopup;
                if (pokemon.getOwner().isPlayer())
                    abilityPopup = controller.getAllyAbilityInfoAnimation(pokemon);
                else
                    abilityPopup = controller.getEnemyAbilityInfoAnimation(pokemon);

                timelineList.add(abilityPopup);
                timelineList.add(controller.generatePause(2000));
                damageInfoTimeline = controller.getBattleTextAnimation(String.format(
                        "%s was cured of its status effect!", pokemon.getBattleName()), true);
                //damageInfoTimeline.setDelay(Duration.seconds(2));
                timelineList.add(damageInfoTimeline);
                timelineList.add(controller.updateStatus(pokemon, pokemon.getOwner().isPlayer()));
                timelineList.add(controller.generatePause(1000));
                return timelineList;
            }
        }

        switch (pokemon.getStatus()) {
            case BURNED:
                damageDouble = Math.round(pokemon.getMaxHP() / 16.0);
                damageInfoTimeline = controller.getBattleTextAnimation(String.format("%s was hurt by%nburn!",
                        pokemon.getBattleName()), true);
                if (damageDouble == 0)
                    damageDouble = 1;
                break;
            case POISONED:
                damageDouble = Math.round(pokemon.getMaxHP() / 8.0);
                damageInfoTimeline = controller.getBattleTextAnimation(String.format(poisonDamageMessage,
                        pokemon.getBattleName()), true);
                if (damageDouble == 0)
                    damageDouble = 1;
                break;

            case BADLY_POISONED:
                damageDouble = Math.round(pokemon.getMaxHP() * pokemon.getPoisonCounter() / 16.0);
                damageInfoTimeline = controller.getBattleTextAnimation(String.format(poisonDamageMessage,
                        pokemon.getBattleName()), true);
                pokemon.setPoisonCounter(pokemon.getPoisonCounter() + 1);
                if (damageDouble == 0)
                    damageDouble = 1;
                break;
        }

        if (damageDouble > 0) {
            int damage = (int) damageDouble;
            int oldHp = pokemon.getHp();

            if (damage > pokemon.getHp())
                damage = pokemon.getHp();

            pokemon.setHp(pokemon.getHp() - damage);

            System.out.printf("%s took %d damage from %s%n", pokemon.getBattleName(), damage, pokemon.getStatus());

            Timeline damageTimeline;
            if (pokemon.getOwner().isPlayer())
                damageTimeline = controller.getAllyHpAnimation(oldHp, pokemon.getHp(), pokemon.getMaxHP());
            else
                damageTimeline = controller.getEnemyHpAnimation(oldHp, pokemon.getHp(), pokemon.getMaxHP());

            List<Timeline> timelineList = new LinkedList<>();

            if (damageInfoTimeline != null) {
                //damageInfoTimeline.setDelay(Duration.seconds(2));
                timelineList.add(damageInfoTimeline);
            }
            else
                throw new IllegalStateException();

            timelineList.add(damageTimeline);
            timelineList.add(controller.generatePause(1000));

            return timelineList;
        }

        return null;
    }

    // Processes fainting enemy
    private void processEnemyFainted(List<Timeline> battleTimeLine) {
        Timeline enemyPokemonFainted = controller.getPokemonFaintedAnimation(false);
        //enemyPokemonFainted.setDelay(Duration.seconds(2));

        battleTimeLine.add(enemyPokemonFainted);
        battleTimeLine.add(controller.generatePause(1000));

        Timeline enemyPokemonFaintedMessage = controller.getBattleTextAnimation(String.format(POKEMON_FAINTED_STRING,
                enemy.getParty(currentEnemyPokemon).getBattleName()), true);
        //enemyPokemonFaintedMessage.setDelay(Duration.seconds(1));

        battleTimeLine.add(enemyPokemonFaintedMessage);
        battleTimeLine.add(controller.generatePause(1));
        controller.battleTextAdvanceByUserInput(battleTimeLine.get(battleTimeLine.size() - 2),
                battleTimeLine.get(battleTimeLine.size() - 1));

        player.getParty(currentAllyPokemon).setTrapMove(null);
        player.getParty(currentAllyPokemon).setTrappedTimer(0);

        //finalChecks(battleTimeLine, enemyFainted);
    }

    // Processes fainting ally
    private void processAllyFainted(List<Timeline> battleTimeLine) {
        Timeline allyPokemonFainted = controller.getPokemonFaintedAnimation(true);
        //allyPokemonFainted.setDelay(Duration.seconds(2));

        battleTimeLine.add(allyPokemonFainted);

        Timeline allyPokemonFaintedMessage = controller.getBattleTextAnimation(String.format(POKEMON_FAINTED_STRING,
                player.getParty(currentAllyPokemon).getBattleName()), true);
        //allyPokemonFaintedMessage.setDelay(Duration.seconds(1));

        battleTimeLine.add(allyPokemonFaintedMessage);
        battleTimeLine.add(controller.generatePause(1));
        controller.battleTextAdvanceByUserInput(battleTimeLine.get(battleTimeLine.size() - 2),
                battleTimeLine.get(battleTimeLine.size() - 1));

        enemy.getParty(currentEnemyPokemon).setTrapMove(null);
        enemy.getParty(currentEnemyPokemon).setTrappedTimer(0);

        //battleTimeLine.get(0).play();
        //finalChecks(battleTimeLine, enemyFainted);

    }

    // Process turn depending on which Pokemon moves first
    private List<Timeline> processTurn(Move firstMove, Pokemon firstPokemon, Move secondMove, Pokemon secondPokemon) {

        List<Timeline> moveTimeLine = new LinkedList<>();

        //Random generator = new Random();
        if (firstMove == null && firstPokemon.getSubStatuses().contains(Enums.SubStatus.RECHARGE)) {
            Timeline firstRechargeMessage = controller.getBattleTextAnimation(String.format(
                    RECHARGE_INFO, firstPokemon.getBattleName()), true);
            moveTimeLine.add(firstRechargeMessage);
            moveTimeLine.add(controller.generatePause(2000));
            firstPokemon.getSubStatuses().remove(Enums.SubStatus.RECHARGE);
        }
        else if (firstMove == null)
            throw new IllegalStateException("First Pokemon move null and not recharging!");
        else
            moveTimeLine.addAll(useMove(firstMove, firstPokemon,
                secondPokemon, true));

        if (secondPokemon.getHp() == 0 || firstPokemon.getHp() == 0) {
            return moveTimeLine;
        }

        if (secondMove == null && secondPokemon.getSubStatuses().contains(Enums.SubStatus.RECHARGE)) {
            Timeline secondRechargeMessage = controller.getBattleTextAnimation(String.format(
                    RECHARGE_INFO, secondPokemon.getBattleName()), true);
            moveTimeLine.add(secondRechargeMessage);
            moveTimeLine.add(controller.generatePause(2000));
            secondPokemon.getSubStatuses().remove(Enums.SubStatus.RECHARGE);
        }
        else if (secondMove == null)
            throw new IllegalStateException("Second Pokemon move null and not recharging!");
        else
            moveTimeLine.addAll(useMove(secondMove,
                secondPokemon, firstPokemon, false));

        return moveTimeLine;
    }

    // Events when ally Pokemon is slower
    // TODO: old implementation, delete after checking the new one
    private List<Timeline> processTurnAllySlower(Move allyMove, Move enemyMove) {
        //Random generator = new Random();

        //int enemyMove = generator.nextInt(enemy.getParty(currentEnemyPokemon).getMoveList().size());
        List<Timeline> enemyMoveTimeLine = useMove(enemyMove, enemy.getParty(currentEnemyPokemon),
                player.getParty(currentAllyPokemon), true);

        if (enemy.getParty(currentEnemyPokemon).getHp() == 0 || player.getParty(currentAllyPokemon).getHp() == 0)  {
            return enemyMoveTimeLine;
        }
        List<Timeline> allyMoveTimeLine = useMove(allyMove, player.getParty(currentAllyPokemon),
                enemy.getParty(currentEnemyPokemon), false);

        enemyMoveTimeLine.addAll(allyMoveTimeLine);
        return enemyMoveTimeLine;
    }

    private void itemTurn(Item item) {

    }

    private void runTurn() {

    }

    private void processUserParalyzed(Pokemon user, List<Timeline> moveTimeLine) {
        Timeline paralysisInfo = controller.getBattleTextAnimation(String.format("%s is%nfully paralyzed!",
                user.getBattleName()), true);
        moveTimeLine.add(paralysisInfo);
        moveTimeLine.add(controller.generatePause(2000));
        System.out.println(user.getBattleName() + "is fully paralyzed!");
    }

    private void processUserAsleep(Pokemon user, List<Timeline> moveTimeLine) {
        Timeline sleepInfo = controller.getBattleTextAnimation(String.format("%s is%nfast asleep!", user.getBattleName()),
                true);
        //user.setSleepCounter(user.getSleepCounter() - 1);
        moveTimeLine.add(sleepInfo);
        moveTimeLine.add(controller.generatePause(2000));
        System.out.printf("%s is asleep!%n", user.getBattleName());
    }

    private void processUserWakeUp(Pokemon user, List<Timeline> moveTimeLine) {
        Timeline sleepInfo = controller.getBattleTextAnimation(String.format("%s woke up!", user.getBattleName()),
                true);
        user.setStatus(Enums.Status.NONE);
        Timeline statusChange = controller.updateStatus(user, user.getOwner().isPlayer());
        //statusChange.setDelay(Duration.seconds(1));

        moveTimeLine.add(sleepInfo);
        moveTimeLine.add(controller.generatePause(1000));
        moveTimeLine.add(statusChange);
        moveTimeLine.add(controller.generatePause(1000));
        System.out.printf("%s woke up!%n", user.getBattleName());

        user.setSleepCounter(0);
    }

    private void processUserFrozen(Pokemon user, List<Timeline> moveTimeLine) {
        Timeline frozenInfo = controller.getBattleTextAnimation(String.format("%s is frozen solid!",
                user.getBattleName()), true);
        moveTimeLine.add(frozenInfo);
        moveTimeLine.add(controller.generatePause(2000));
    }

    private void processUserThawOut(Pokemon user, List<Timeline> moveTimeLine) {
        Timeline frozenInfo = controller.getBattleTextAnimation(String.format("%s thawed out!", user.getBattleName()),
                true);
        moveTimeLine.add(frozenInfo);

        user.setStatus(Enums.Status.NONE);
        Timeline updateStatus = controller.updateStatus(user, user.getOwner().isPlayer());
        //updateStatus.setDelay(Duration.seconds(1));
        moveTimeLine.add(updateStatus);
        moveTimeLine.add(controller.generatePause(2000));
    }

    private void processMoveMissed(Pokemon user, List<Timeline> moveTimeLine) {
        System.out.println("Move missed!");
        Timeline moveMissedDialogue = controller.getBattleTextAnimation(String.format("%s's%nattack missed!",
                user.getBattleName()), true);
        //moveMissedDialogue.setDelay(Duration.seconds(2));

        moveTimeLine.add(moveMissedDialogue);
        processTwoTurnMoveComplete(moveTimeLine, user);
        moveTimeLine.add(controller.generatePause(2000));

        if (user.getMultiTurnMove() != null)
            checkMultiturnMoveInterruptEffect(moveTimeLine, user);
    }

    private void processTwoTurnMoveComplete(List<Timeline> moveTimeLine, Pokemon user) {

        user.setTwoTurnMove(null);

        if (!user.getOwner().isPlayer()) {
            Timeline revealEnemy = new Timeline(new KeyFrame(Duration.millis(1),
                    e -> controller.getEnemyPokemonSprite().setVisible(true)));
            moveTimeLine.add(revealEnemy);
        }
        else {
            Timeline revealAlly = new Timeline(new KeyFrame(Duration.millis(1),
                    e -> controller.getAllyPokemonSprite().setVisible(true)));
            moveTimeLine.add(revealAlly);
        }
    }

    private Timeline getTwoTurnMoveInfo(Move move, Pokemon user) {
        Timeline timeline;
        switch(move.getName()) {
            case DIG:
                timeline = controller.getBattleTextAnimation(String.format(
                    "%s dug underground!", user.getBattleName()), true);
                break;
            case SOLAR_BEAM:
                timeline = controller.getBattleTextAnimation(String.format(
                    "%s is taking in sunlight!", user.getBattleName()), true);
                break;
            case SKULL_BASH:
                timeline = controller.getBattleTextAnimation(String.format(
                        "%s tucked in its head!", user.getBattleName()), true);
                break;
            default:
                throw new IllegalStateException("Unidentified twoturn move: "
            + move.getName());
        }
        return timeline;
    }

    private int checkTwoTurnMiss(Move move, Pokemon target) {
        Move twoturnmove = target.getTwoTurnMove();
        if (twoturnmove == null || twoturnmove.isCharging())
            return 1;

        boolean digEarthquake = Objects.equals(twoturnmove.getName(), MoveEnum.DIG) && Objects.equals(move.getName(),
                MoveEnum.EARTHQUAKE);
        boolean diveWhirlpool = Objects.equals(twoturnmove.getName(), MoveEnum.DIVE) && Objects.equals(move.getName(),
                MoveEnum.WHIRLPOOL);

        if (digEarthquake) {
            return 2;
        }
        if (diveWhirlpool) {
            return 2;
        }

        return 0;
    }

    private List<Timeline> processHealthRestore(List<Timeline> moveTimeLine, Move move, Pokemon user) {
        if (user.getHp() != user.getMaxHP()) {
            double restoredHealth = user.getMaxHP() * move.getHpRestore();
            double healthChange = restoredHealth + user.getHp();
            if (healthChange > user.getMaxHP())
                healthChange = user.getMaxHP();
            int oldHp = user.getHp();
            int healthChangeInt = (int) Math.round(healthChange);
            user.setHp(healthChangeInt);
            Timeline userHealthChange;
            if (!user.getOwner().isPlayer())
                userHealthChange = controller.getEnemyHpAnimation(oldHp, user.getHp(), user.getMaxHP());

            else
                userHealthChange = controller.getAllyHpAnimation(oldHp, user.getHp(), user.getMaxHP());

            userHealthChange.setDelay(Duration.seconds(1));
            moveTimeLine.add(userHealthChange);

            Timeline userHealthInfo = controller.getBattleTextAnimation(String.format(
                    "%s restored health!", user.getBattleName()), true);

            System.out.printf("%s restored %d health%n", user.getBattleName(), healthChangeInt);
            moveTimeLine.add(userHealthInfo);

        }
        else {
            moveTimeLine.add(controller.generatePause(1000));
            Timeline userHealthInfo = controller.getBattleTextAnimation(String.format(
                    "%s's health%nis already full!", user.getBattleName()), true);
            moveTimeLine.add(userHealthInfo);
            //userHealthInfo.setDelay(Duration.seconds(1));
        }

        moveTimeLine.add(controller.generatePause(2000));
        return moveTimeLine;
    }

    private void processMultiturnMoveCompleted(List<Timeline> moveTimeLine, Pokemon user) {
        if (!user.getSubStatuses().contains(Enums.SubStatus.CONFUSED)) {
            Timeline userConfusedMsg = controller.getBattleTextAnimation(String.format("%s has become confused%ndue to fatigue!",
                    user.getBattleName()), true);
            //userConfusedMsg.setDelay(Duration.seconds(2));
            System.out.println(user.getBattleName() + " multiturn has ended, became confused");

            moveTimeLine.add(userConfusedMsg);
            applyConfusion(user);
        }
        processMultiturnMoveInterrupted(user);
    }

    private void processMultiturnMoveInterrupted(Pokemon user) {
        user.setMultiTurnMove(null);
        user.setMultiTurnCounter(0);
    }

    private void checkMultiturnMoveInterruptEffect(List<Timeline> moveTimeLine, Pokemon user) {
        if (user.getMultiTurnCounter() > 0 || !user.getMultiTurnMove().isMultiturnConfusion()) {
            processMultiturnMoveInterrupted(user);
        }
        else if (user.getMultiTurnCounter() == 0) {
            processMultiturnMoveCompleted(moveTimeLine, user);
        }
    }

    private void updateConfusionStatus(List<Timeline> moveTimeLine, Pokemon user) {

        Timeline snappedOutMessage = controller.getBattleTextAnimation(String.format(
                "%s snapped out of confusion!", user.getBattleName()), true);


        if (user.getConfusionTimer() == 0) {
            System.out.println(user.getBattleName() + " snapped out of confusion");
            user.getSubStatuses().remove(Enums.SubStatus.CONFUSED);
            moveTimeLine.add(snappedOutMessage);
            moveTimeLine.add(controller.generatePause(2000));
        }
        //else
            //user.setConfusionTimer(user.getConfusionTimer() - 1);
    }

    private void processConfusionHit(List<Timeline> moveTimeLine, Pokemon user) {
        Timeline messageConfusion = controller.getBattleTextAnimation("It hurt itself in its\nconfusion!", true);
        moveTimeLine.add(messageConfusion);
        moveTimeLine.add(controller.generatePause(500));

        MoveDamageInfo confusionDamageInfo = calculateMoveDamage(0,
                new Move(MoveTemplate.getMoveMap().get(MoveEnum.CONFUSION_DAMAGE)), user, user, 1, false);

        int confusionDamage = confusionDamageInfo.damage;
        if (confusionDamage > user.getHp())
            confusionDamage = user.getHp();

        int oldHp = user.getHp();
        user.setHp(user.getHp() - confusionDamage);

        Timeline generateHit;
        System.out.println(user.getBattleName() + " hurt itself in confusion: " + confusionDamage);

        if(!user.getOwner().isPlayer()) {
            generateHit = controller.getEnemyHpAnimation(oldHp, user.getHp(), user.getMaxHP());
        }
        else {
            generateHit = controller.getAllyHpAnimation(oldHp, user.getHp(), user.getMaxHP());
        }

        moveTimeLine.add(generateHit);
        moveTimeLine.add(controller.generatePause(2000));

        processMultiturnMoveInterrupted(user);
    }

    private void processUserFlinched(List<Timeline> moveTimeLine, Pokemon user) {
        Timeline messagePokemonFlinched = controller.getBattleTextAnimation(
                user.getBattleName() + " flinched!", true);
        moveTimeLine.add(messagePokemonFlinched);
        moveTimeLine.add(controller.generatePause(2000));
        if (user.getMultiTurnMove() != null)
            checkMultiturnMoveInterruptEffect(moveTimeLine, user);
        if (user.getTwoTurnMove() != null)
            processTwoTurnMoveComplete(moveTimeLine, user);
    }

    private float calculateMoveAccuracyModifier(Pokemon user, Pokemon target, Move move) {
        int statAccuracy = user.getStatModifiers().get(Enums.StatType.ACCURACY) - target.getStatModifiers().get(Enums.StatType.EVASIVENESS);
        if (statAccuracy > 6)
            statAccuracy = 6;
        else if (statAccuracy < -6)
            statAccuracy = -6;

        float accuracyModifier;
        if (statAccuracy < 0) {
            accuracyModifier = 3.0f / (3-statAccuracy);
        }
        else
            accuracyModifier = (float)(3+statAccuracy) / 3.0f;

        if (user.getAbility() == Ability.COMPOUND_EYES && !move.isOneHitKOMove()) {
            accuracyModifier *= 1.3;
        }

        return accuracyModifier;
    }

    private void processBattlefieldConditionMove(List<Timeline> moveTimeline, Move move, Pokemon user) {

        Timeline conditionMessage = null;

        switch (move.getCondition()) {
            case TAILWIND:
                if (user.getOwner().isPlayer() &&
                        !allyBattlefieldConditions.containsKey(Enums.BattlefieldCondition.TAILWIND)) {
                    conditionMessage = controller.getBattleTextAnimation("The Tailwind blew from behind your team!",
                            true);
                    allyBattlefieldConditions.put(move.getCondition(), 4);
                }
                else if (!user.getOwner().isPlayer() &&
                        !enemyBattlefieldConditions.containsKey(Enums.BattlefieldCondition.TAILWIND)){
                    conditionMessage = controller.getBattleTextAnimation("The Tailwind blew from behind the Foe's team!",
                            true);
                    enemyBattlefieldConditions.put(move.getCondition(), 4);
                }
                else {
                    conditionMessage = controller.getBattleTextAnimation("But it failed!",
                            true);
                }
        }

        moveTimeline.add(conditionMessage);
        moveTimeline.add(controller.generatePause(2000));
    }

    // Processes using a move, as well as status effects or accuracy checks that might prevent from using it
    private List<Timeline> useMove(Move move, Pokemon user, Pokemon target, boolean first) {

        // Initiating the list of animations to be performed during move and an RNG
        List<Timeline> moveTimeLine = new LinkedList<>();
        SecureRandom generator = new SecureRandom();

        // Decreases the multiturn move counter, this is done here because interrupting a multiturn move
        // on its last turn will result in the same effect as the move completing
        if (user.getMultiTurnCounter() > 0 && user.getMultiTurnMove() != null)
            user.setMultiTurnCounter(user.getMultiTurnCounter() - 1);

        // Check if user flinched, this takes precedence before any other status effects
        // boolean first check is redundant but should be performed just in case
        if (user.getSubStatuses().contains(Enums.SubStatus.FLINCHED) && !first) {
            processUserFlinched(moveTimeLine, user);
            return moveTimeLine;
        }
        //*****************************************************

        // Status processing, if user Pokemon is stopped by status effects, the function returns
        // Also when interrupted during multiturn moves, the algorithm checks if it would be the last turn
        // if yes then the target is also confused on top of other status ailments
        if (user.getStatus() == Enums.Status.PARALYZED) {
            int rand = generator.nextInt(4);
            if (rand == 0) {
                processUserParalyzed(user, moveTimeLine);
                if (user.getMultiTurnMove() != null)
                    checkMultiturnMoveInterruptEffect(moveTimeLine, user);
                if (user.getTwoTurnMove() != null)
                    processTwoTurnMoveComplete(moveTimeLine, user);
                return moveTimeLine;
            }
        }
        if (user.getStatus() == Enums.Status.SLEEPING && user.getSleepCounter() > 0) {
            processUserAsleep(user, moveTimeLine);
            if (user.getMultiTurnMove() != null)
                checkMultiturnMoveInterruptEffect(moveTimeLine, user);
            if (user.getTwoTurnMove() != null)
                processTwoTurnMoveComplete(moveTimeLine, user);
            return moveTimeLine;
        }
        else if(user.getStatus() == Enums.Status.SLEEPING && user.getSleepCounter() == 0) {
            processUserWakeUp(user, moveTimeLine);
        }

        if (user.getStatus() == Enums.Status.FROZEN) {
            int rand = generator.nextInt(5);
            boolean damagingFireMove = move.getType().getTypeEnum() == Enums.Types.FIRE &&
                    move.getSubtype() != Enums.Subtypes.STATUS;
            if (rand == 0 || damagingFireMove) {
                processUserThawOut(user, moveTimeLine);
            }
            else {
                processUserFrozen(user, moveTimeLine);
                if (user.getMultiTurnMove() != null)
                    checkMultiturnMoveInterruptEffect(moveTimeLine, user);
                if (user.getTwoTurnMove() != null)
                    processTwoTurnMoveComplete(moveTimeLine, user);
                return moveTimeLine;
            }
        }
        //***************************************

        // Checks related to confused Pokemon
        // Decreases confusion timer, checks if Pokemon will hit itself in confusion and deletes status if timer is 0
        if(user.getSubStatuses().contains(Enums.SubStatus.CONFUSED))
            updateConfusionStatus(moveTimeLine ,user);

        if (user.getSubStatuses().contains(Enums.SubStatus.CONFUSED)) {
            Timeline confuseMessage = controller.getBattleTextAnimation(String.format("%s is%nconfused!", user.getBattleName()),
                    true);
            System.out.println(user.getBattleName() + " is confused");
            moveTimeLine.add(confuseMessage);
            moveTimeLine.add(controller.generatePause(2000));

            int confuseInt = generator.nextInt(3);
            if (confuseInt == 0) {
                processConfusionHit(moveTimeLine, user);
                return moveTimeLine;
            }
        }
        //******************************************************

        // Checks related to twoturn moves, non charging moves set Pokemon to a semi-invulnerable state,
        // otherwise just lock the Pokemon out of a choice next turn during charging
        if (move.isTwoturn() && user.getTwoTurnMove() == null) {
            Timeline allyTwoTurnInfo = getTwoTurnMoveInfo(move, user);
            moveTimeLine.add(allyTwoTurnInfo);
            user.setTwoTurnMove(move);
            System.out.printf("%s used %s, first turn%n", user.getBattleName(), move.getName());
            if (!move.isCharging()) {
                if (user.getOwner().isPlayer()) {
                    Timeline allyTwoTurnStart = new Timeline(new KeyFrame(Duration.millis(1),
                            e -> controller.getAllyPokemonSprite().setVisible(false)));
                    moveTimeLine.add(allyTwoTurnStart);
                }
                else{
                    Timeline enemyTwoTurnStart = new Timeline(new KeyFrame(Duration.millis(1),
                            e -> controller.getEnemyPokemonSprite().setVisible(false)));
                    moveTimeLine.add(enemyTwoTurnStart);
                }
            }
            moveTimeLine.add(controller.generatePause(1000));

            if (move.isStatUpDuringCharging()) {
                processStatChange(moveTimeLine, move, user, true);
                moveTimeLine.add(controller.generatePause(1000));
            }

            return moveTimeLine;
        }
        //**************************************************************

        System.out.println(user.getBattleName() + " used " + move.getName());
        Timeline moveUsedDialog = controller.getBattleTextAnimation(user.getBattleName() + " used\n" + move.getName() + "!",
                true);

        moveTimeLine.add(moveUsedDialog);
        moveTimeLine.add(controller.generatePause(1500));

        // If Pokemon in progress of multiturn move, pp is not deducted
        if(user.getMultiTurnMove() == null)
            move.setPp(move.getPp() - 1);

        int moveAccuracy = move.getAccuracy();
        int twoTurnModifier = checkTwoTurnMiss(move, target);

        // If move applies a battlefield condition, apply the effect
        if (move.getCondition() != Enums.BattlefieldCondition.NONE) {
            processBattlefieldConditionMove(moveTimeLine, move, user);
            return moveTimeLine;
        }

        // Move hit calculations, if move misses the function ends
        if (moveAccuracy != 0) {
            float accuracyModifier = calculateMoveAccuracyModifier(user, target, move);
            float hit = moveAccuracy * accuracyModifier;
            int r = generator.nextInt(100) + 1;
            if (r > hit || twoTurnModifier == 0) {
                processMoveMissed(user, moveTimeLine);
                return moveTimeLine;
            }
        }
        //**********************************************

        boolean statusMovesExceptionGround = move.getType().getTypeEnum() == Enums.Types.ELECTRIC &&
                (target.getType()[0].getTypeEnum() == Enums.Types.ELECTRIC ||
                        target.getType()[0].getTypeEnum() == Enums.Types.ELECTRIC);

        Enums.Subtypes moveType = move.getSubtype();
        MoveDamageInfo damageInfo = new MoveDamageInfo(0, false, MoveDamageInfo.NOT_APPLICABLE);
        int i;
        int damage = 0;
        int hits = move.isMultiturn() ? 1 : move.getHits();

        if (hits == MoveTemplate.HITS_RANDOM) {
            int hitsRand = generator.nextInt(100);
            int hitsTemp;
            if (hitsRand < 35)
                hitsTemp = 2;
            if (hitsRand < 70)
                hitsTemp = 3;
            if (hitsRand < 85)
                hitsTemp = 4;
            else
                hitsTemp = 5;
            hits = hitsTemp;
        }

        for (i=0; i<hits; i++) {
            if (target.getHp() == 0)
                break;

            // Check move type and calculate damage
            switch (moveType) {
                case PHYSICAL:
                    damageInfo = calculateMoveDamage(0, move, user, target, twoTurnModifier, true);
                    break;
                case SPECIAL:
                    damageInfo = calculateMoveDamage(1, move, user, target, twoTurnModifier, true);
                    break;
                case STATUS:
                    if (move.getHpRestore() > 0)
                        return processHealthRestore(moveTimeLine, move, user);
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + moveType);
            }
            //****************************************

            // Type effect calculation, if the move can't hit a Pokemon the function returns
            // Status moves generally ignore all type interactions, however there are some exceptions to this rule
            // TODO:This check should be moved outside the for loop but it requires to change how type effect is checked

            if (damageInfo.typeEffect == 0 && (move.getSubtype() != Enums.Subtypes.STATUS || statusMovesExceptionGround)) {
                final Timeline effectInfo = controller.getBattleTextAnimation(String.format(MOVE_NO_EFFECT_STRING,
                                target.getBattleNameMiddle()), true);
                //effectInfo.setDelay(Duration.seconds(2));
                System.out.println("No effect on " + target.getBattleNameMiddle());

                moveTimeLine.add(effectInfo);
                moveTimeLine.add(controller.generatePause(2000));

                return moveTimeLine;
            }
            //****************************************************

            // Applying damage as well as the UI animations connected to it
            else if (damageInfo.damage > 0) {

                damage = damageInfo.damage;

                //List<Timeline> hitTimeline = new LinkedList<>();

                if (damage > target.getHp())
                    damage = target.getHp();

                int oldHp = target.getHp();
                target.setHp(target.getHp() - damage);

                final Timeline damageDealtAnimation;

                if (target.getOwner().isPlayer())
                    damageDealtAnimation = controller.getAllyHpAnimation(oldHp, target.getHp(), target.getMaxHP());
                else
                    damageDealtAnimation = controller.getEnemyHpAnimation(oldHp, target.getHp(), target.getMaxHP());

                //damageDealtAnimation.setDelay(Duration.seconds(2));

                moveTimeLine.add(damageDealtAnimation);
                moveTimeLine.add(controller.generatePause(1500));
                int displayHits = i+1;
                System.out.println("Hit " + displayHits + ": " + move.getName() + " dealt " + damage + " damage to " +
                        target.getBattleNameMiddle() + "!");

                if (damageInfo.critical) {
                    final Timeline criticalInfo = controller.getBattleTextAnimation("A critical hit!", true);
                    moveTimeLine.add(criticalInfo);
                    moveTimeLine.add(controller.generatePause(1000));
                }

            }
            //***************************************************************

        }

        // Apply recharge if recharge move
        if (move.isRecharge())
            user.getSubStatuses().add(Enums.SubStatus.RECHARGE);

        // Check if Pokemon in first turn of a multiturn move
        // generates the number of moves that it can use before being confused and
        // assigns appropriate variables
        if (move.isMultiturn() && user.getMultiTurnMove() == null && move.isMultiturnConfusion()) {
            int turns = generator.nextInt(2) + 1;
            user.setMultiTurnMove(move);
            user.setMultiTurnCounter(turns);
        }
        else if (move.isMultiturn() && user.getMultiTurnMove() == null) {
            if (move.getHits() == 1) {
                user.setMultiTurnMove(move);
                user.setMultiTurnCounter(4);
            }
        }
        //**********************************************************

        // Checks if this is the second part of a two turn move
        // if yes, then it resets the state of twoturn moves
        if (move.isTwoturn()) {
            processTwoTurnMoveComplete(moveTimeLine, user);
        }

        //  Other checks related to move effectiveness
        if (damageInfo.typeEffect >= 2 && moveType != Enums.Subtypes.STATUS) {
            final Timeline effectInfo = controller.getBattleTextAnimation("It's super effective!", true);
            //effectInfo.setDelay(Duration.seconds(2));
            moveTimeLine.add(effectInfo);
            moveTimeLine.add(controller.generatePause(2000));
        }
        else if (damageInfo.typeEffect < 1 && damageInfo.typeEffect > 0 && moveType != Enums.Subtypes.STATUS) {
            final Timeline effectInfo = controller.getBattleTextAnimation("It's not very effective...", true);
            //effectInfo.setDelay(Duration.seconds(2));
            moveTimeLine.add(effectInfo);
            moveTimeLine.add(controller.generatePause(2000));
        }
        //***************************************

        // Check related to number of hits for moves that hit multiple times
        if (i > 1 && damageInfo.typeEffect != 0) {
            Timeline hitsInformation = controller.getBattleTextAnimation(String.format("It hit %d time(s)!",
                    i), true);
            //hitsInformation.setDelay(Duration.seconds(2));
            moveTimeLine.add(hitsInformation);
            moveTimeLine.add(controller.generatePause(2000));
        }
        //********************************************************

        //Applies life restore if move has lifsteal
        if (move.getLifesteal() > 0) {
            processLifesteal(moveTimeLine, move, user, target, damage);
        }
        //******************************************************

        // Recoil handling
        float recoil = move.getTemplate().getRecoil();
        if (recoil > 0 && damage != 0 && move.getHits() == 1) {
            processRecoil(moveTimeLine, user, damage, move);
        }
        //********************************************************

        if (move.getSpikeType() != Enums.Spikes.NONE) {
            HashMap<Enums.Spikes, Integer> spikes = user.getOwner().isPlayer() ? enemySpikes : allySpikes;
            if (!spikes.containsKey(move.getSpikeType()))
                spikes.put(move.getSpikeType(), 1);
            else if (spikes.get(move.getSpikeType()) == 1)
                spikes.put(move.getSpikeType(), 2);
            else {
                Timeline failureMessage = controller.getBattleTextAnimation("But it failed!", true);
                moveTimeLine.add(failureMessage);
                moveTimeLine.add(controller.generatePause(1500));
                return moveTimeLine;
            }
            processSpikesPlaced(moveTimeLine ,user, move);
        }

        // Checks related to moves that increase or decrease stats
        boolean targetFainted = target.getHp() == 0;
        boolean userFainted = user.getHp() == 0;
        boolean targetProtectedAbility = move.getSubtype() != Enums.Subtypes.STATUS &&
                target.getAbility() == Ability.SHIELD_DUST;
        boolean secondaryEffectsImmune = targetFainted || targetProtectedAbility;

        //final List<Timeline> statChangeInfo;

        float prob = move.getStatChangeProb() * 100.0f;
        int rand = generator.nextInt(100) + 1;

        if (!move.getStatTypes().isEmpty() && move.isSelf() && prob >= rand && !userFainted) {
            processStatChange(moveTimeLine, move, user, true);
            if(move.getSecondaryStatTypes().size() > 0)
                processStatChange(moveTimeLine, move, user, false);
            //moveTimeLine.addAll(statChangeInfo);
        }
        else if (!move.getStatTypes().isEmpty() && prob >= rand && !secondaryEffectsImmune) {
            processStatChange(moveTimeLine, move, target, true);
            if(move.getSecondaryStatTypes().size() > 0)
                processStatChange(moveTimeLine, move, target, false);
            //moveTimeLine.addAll(statChangeInfo);
        }
        //*******************************************************

        // Check related to moves that increase critcal hit chance (only Focus Energy?)
        if (move.getCritIncrease() > 0) {
            Timeline critChangeInfo;
            if(!user.isUnderFocusEnergy()) {
                critChangeInfo = controller.getBattleTextAnimation(String.format("%s is getting pumped!", user.getBattleName()),
                        true);
                user.setCritIncrease(user.getCritIncrease() + 2);
                user.setUnderFocusEnergy(true);
            }
            else
                critChangeInfo = controller.getBattleTextAnimation("But it failed!", true);

            moveTimeLine.add(critChangeInfo);
            moveTimeLine.add(controller.generatePause(1500));
        }
        //*******************************************************

        // Checks related to moves that inflict status effects
        prob = Math.round(move.getStatusProb() * 100.0f);
        rand = generator.nextInt(100) + 1;

        boolean firePokemonImmunity = move.getStatus() == Enums.Status.BURNED &&
                (target.getType()[0].getTypeEnum() == Enums.Types.FIRE ||
                        target.getType()[1].getTypeEnum() == Enums.Types.FIRE);
        boolean electricPokemonImmunity = move.getStatus() == Enums.Status.PARALYZED &&
                (target.getType()[0].getTypeEnum() == Enums.Types.ELECTRIC ||
                        target.getType()[1].getTypeEnum() == Enums.Types.ELECTRIC);
        boolean poisonPokemonImmunity = (move.getStatus() == Enums.Status.POISONED ||
                move.getStatus() == Enums.Status.BADLY_POISONED) &&
                (target.getType()[0].getTypeEnum() == Enums.Types.POISON ||
                        target.getType()[1].getTypeEnum() == Enums.Types.POISON);

        boolean statusImmunity = firePokemonImmunity || electricPokemonImmunity || poisonPokemonImmunity;

        if (move.getStatus() != Enums.Status.NONE && prob >= rand && !secondaryEffectsImmune && !statusImmunity) {
            Timeline statusChangeInfo = processStatusChange(move.getStatus(), target);
            //statusChangeInfo.setDelay(Duration.seconds(1));
            moveTimeLine.add(statusChangeInfo);

            Timeline updateStatus = controller.updateStatus(target, target.getOwner().isPlayer());
            if (move.getStatus() == Enums.Status.SLEEPING || move.getStatus() == Enums.Status.FROZEN)
                processTwoTurnMoveComplete(moveTimeLine, user);
            //statusChangeInfo.setDelay(Duration.seconds(1));
            moveTimeLine.add(updateStatus);
            moveTimeLine.add(controller.generatePause(1000));
        }
        else if (move.getSubtype() == Enums.Subtypes.STATUS && statusImmunity) {
            final Timeline effectInfo = controller.getBattleTextAnimation(String.format(MOVE_NO_EFFECT_STRING,
                    target.getBattleNameMiddle()), true);
            //effectInfo.setDelay(Duration.seconds(2));

            System.out.println("No effect on " + target.getBattleNameMiddle());

            moveTimeLine.add(effectInfo);
            moveTimeLine.add(controller.generatePause(2000));
            return moveTimeLine;
        }
        //*********************************************************

        if (move.getSubStatus() == Enums.SubStatus.CONFUSED && prob >= rand && !secondaryEffectsImmune &&
        !target.getSubStatuses().contains(Enums.SubStatus.CONFUSED)) {
            moveTimeLine.add(applyConfusion(target));
            moveTimeLine.add(controller.generatePause(1000));
        }

        if (move.getSubStatus() == Enums.SubStatus.FLINCHED && prob >= rand && !secondaryEffectsImmune &&
                !target.getSubStatuses().contains(Enums.SubStatus.FLINCHED) && first) {
            target.getSubStatuses().add(Enums.SubStatus.FLINCHED);
        }

        //Trap target in vortex if its hp is higher than 0 and is not a Ghost type Pokemon
        boolean targetGhost = target.getType()[0].getTypeEnum() == Enums.Types.GHOST ||
                target.getType()[0].getTypeEnum() == Enums.Types.GHOST;
        if (!targetFainted && move.isTrap() && target.getTrapMove() == null && !targetGhost) {
            moveTimeLine.add(processInVortex(move ,target));
            moveTimeLine.add(controller.generatePause(1000));
        }
        //*****************************************************

        // If multiturn counter has reached 0 and causes confusion, then a multiturn move is disabled
        // and the target becomes confused
        if (move.isMultiturn() && user.getMultiTurnCounter() == 0 && user.getMultiTurnMove() != null &&
        move.isMultiturnConfusion())
            processMultiturnMoveCompleted(moveTimeLine, user);
        //*********************************************************************

        return moveTimeLine;
    }

    private void processSpikesPlaced(List<Timeline> moveTimeLine, Pokemon user, Move move) {
        switch (move.getSpikeType()) {
            case TOXIC_SPIKES:
                Timeline spikeMessage;
                if (user.getOwner().isPlayer())
                    spikeMessage = controller.getBattleTextAnimation(
                            "Spikes were scattered all around\nthe foe's team's feet!", true);
                else
                    spikeMessage = controller.getBattleTextAnimation(
                            "Spikes were scattered all around\nyour team's feet!", true);
                moveTimeLine.add(spikeMessage);
                moveTimeLine.add(controller.generatePause(2000));
        }
    }

    private void processLifesteal(List<Timeline> moveTimeLine, Move move, Pokemon user, Pokemon target, int damage) {
        float lifeStolen = move.getLifesteal() * damage;
        int lifeStolenInteger = Math.round(lifeStolen);

        if (lifeStolenInteger == 0)
            lifeStolenInteger = 1;

        int oldUserHp = user.getHp();

        if (lifeStolenInteger + oldUserHp >= user.getMaxHP())
            lifeStolenInteger = user.getMaxHP() - oldUserHp;

        user.setHp(oldUserHp + lifeStolenInteger);
        Timeline energyRestoreAnimation;

        if (user.getOwner().isPlayer())
            energyRestoreAnimation = controller.getAllyHpAnimation(oldUserHp, user.getHp(), user.getMaxHP());
        else
            energyRestoreAnimation = controller.getEnemyHpAnimation(oldUserHp, user.getHp(), user.getMaxHP());

        if (energyRestoreAnimation != null)
            moveTimeLine.add(energyRestoreAnimation);

        Timeline energyDrainedMessage = controller.getBattleTextAnimation(String.format(
                "%s had its%nenergy drained!", target.getBattleName()), true);
        moveTimeLine.add(energyDrainedMessage);

        moveTimeLine.add(controller.generatePause(2000));

        System.out.printf("%s drained %d HP from %s%n", user.getBattleName(), lifeStolenInteger ,target.getBattleNameMiddle());
    }

    private void processRecoil(List<Timeline> moveTimeLine, Pokemon user, int damage, Move move) {
        Timeline recoilText = controller.getBattleTextAnimation(String.format(
                "%s took damage%nfrom recoil!",user.getBattleName()),true);

        recoilText.setDelay(Duration.seconds(1));
        moveTimeLine.add(recoilText);

        double recoilDamage;
        if (!move.isRecoilHpDamage())
            recoilDamage = damage * move.getRecoil();
        else
            recoilDamage = 1/4f * user.getMaxHP();

        int recoilDamageInt = (int) Math.round(recoilDamage);
        if (recoilDamageInt > user.getHp())
            recoilDamageInt = user.getHp();

        if (recoilDamageInt == 0)
            recoilDamageInt = 1;

        int oldHp = user.getHp();
        user.setHp(user.getHp() - recoilDamageInt);

        Timeline recoilDealtAnimation;
        if(user.getOwner().isPlayer())
            recoilDealtAnimation = controller.getAllyHpAnimation(oldHp, user.getHp(), user.getMaxHP());
        else
            recoilDealtAnimation = controller.getEnemyHpAnimation(oldHp, user.getHp(), user.getMaxHP());

        moveTimeLine.add(recoilDealtAnimation);
        moveTimeLine.add(controller.generatePause(1000));
    }

    private Timeline applyConfusion(Pokemon target) {
        Timeline confusionMessage = controller.getBattleTextAnimation(String.format("%s became %nconfused!",
                target.getBattleName()), true);
        confusionMessage.setDelay(Duration.seconds(2));

        System.out.println(target.getBattleName() + " became confused!");

        SecureRandom generator = new SecureRandom();

        int turns = generator.nextInt(4) + 1;

        target.getSubStatuses().add(Enums.SubStatus.CONFUSED);
        target.setConfusionTimer(turns);

        return confusionMessage;
    }

    private Timeline processInVortex(Move move, Pokemon target) {
        Timeline pokemonTrappedMessage;

        pokemonTrappedMessage = controller.getBattleTextAnimation(String.format(
                        "%s was trapped in the vortex!", target.getBattleName()), true);
        //pokemonTrappedMessage.setDelay(Duration.seconds(2));

        SecureRandom generator = new SecureRandom();

        System.out.println(target.getBattleName() + " was trapped in vortex");
        int turns = 0;
        switch (move.getName()) {
            case FIRE_SPIN:
                int rand = generator.nextInt(256);
                if (rand < 96)
                    turns = 2;
                else if (rand < 192)
                    turns = 3;
                else if (rand < 224)
                    turns = 4;
                else
                    turns = 5;
                break;
            case WHIRLPOOL:
                turns = generator.nextInt(2) + 4;
                break;
            default:
                throw new IllegalStateException("Not a trapping move");
        }

        if (turns == 0)
            throw new IllegalStateException("An unexpected move arrived at this function: " + move.getName());
        target.setTrappedTimer(turns);
        target.setTrapMove(move);

        return pokemonTrappedMessage;
    }

    private Timeline processStatusChange(Enums.Status status, Pokemon target) {
        final Timeline statusChangeInfo;
        SecureRandom generator = new SecureRandom();

        if (target.getStatus() != Enums.Status.NONE) {
            statusChangeInfo = controller.getBattleTextAnimation(String.format("%s is already%n%s!",
                    target.getBattleName(), target.getStatus().toString()), true);
            System.out.printf("%s is already %s!%n", target.getBattleName(), target.getStatus().toString());
        }
        else {
            target.setStatus(status);
            statusChangeInfo = controller.getBattleTextAnimation(String.format("%s is%n%s!",
                    target.getBattleName(), status.toString()), true);
            System.out.printf("%s is %s!%n", target.getBattleName(), status.toString());
            if (status == Enums.Status.SLEEPING) {
                int sleepTurns = generator.nextInt(3) + 1;
                target.setSleepCounter(sleepTurns);
            }
        }

        return statusChangeInfo;
    }

    private void processStatChange(List<Timeline> moveTimeLine ,Move move, Pokemon target, boolean primary) {

        List<Enums.StatType> statTypes;
        if (primary)
            statTypes = move.getStatTypes();
        else
            statTypes = move.getSecondaryStatTypes();

        for (Enums.StatType statType : statTypes) {
            int change;
            if (primary)
                change = move.getStatChange();
            else
                change = move.getSecondaryStatChange();

            int currentStatModifier = target.getStatModifiers().get(statType);
            int statup = currentStatModifier + change;

            if (currentStatModifier == 6 && change > 0) {
                moveTimeLine.add(controller.getBattleTextAnimation(String.format("%s's%n%s can't go any higher!",
                        target.getBattleName(), statType), true));
                change = 0;
            } else if (currentStatModifier == -6 && change < 0) {
                moveTimeLine.add(controller.getBattleTextAnimation(String.format("%s's%n%s can't go any lower!",
                        target.getBattleName(), statType), true));
                change = 0;
            } else {
                if (statup <= 6 && statup >= -6)
                    target.getStatModifiers().put(statType, statup);
                else if (statup > 6) {
                    statup = 6;
                    change = statup - currentStatModifier;
                    target.getStatModifiers().put(statType, statup);
                } else {
                    statup = -6;
                    change = statup - currentStatModifier;
                    target.getStatModifiers().put(statType, statup);
                }
                switch (change) {
                    case 1:
                        moveTimeLine.add(controller.getBattleTextAnimation(String.format("%s's%n%s rose!",
                            target.getBattleName(), statType), true));
                        break;
                    case 2:
                        moveTimeLine.add(controller.getBattleTextAnimation(String.format("%s's%n%s rose sharply!",
                            target.getBattleName(), statType), true));
                        break;
                    case 3:
                        moveTimeLine.add(controller.getBattleTextAnimation(String.format("%s's%n%s rose drastically!",
                            target.getBattleName(), statType), true));
                        break;
                    case -1:
                        moveTimeLine.add(controller.getBattleTextAnimation(String.format("%s's%n%s fell!",
                            target.getBattleName(), statType), true));
                        break;
                    case -2:
                        moveTimeLine.add(controller.getBattleTextAnimation(String.format("%s's%n%s harshly fell!",
                            target.getBattleName(), statType), true));
                        break;
                    case -3:
                        moveTimeLine.add(controller.getBattleTextAnimation(String.format("%s's%n%s severely fell!",
                            target.getBattleName(), statType), true));
                        break;
                    default:
                        throw new IllegalStateException(
                            "Wrong change value, should be between -3 and 3 (excluding 0), is: " + change);
                }
            }
            moveTimeLine.add(controller.generatePause(1500));
            System.out.println(target.getBattleName() + " stat change to " + statType + ": " + change);
        }
    }

    private MoveDamageInfo calculateMoveDamage(int movetype, Move move, Pokemon user, Pokemon target, int twoTurnModifier,
                                       boolean canCrit)
    {
        SecureRandom generator = new SecureRandom();
        int bound;
        int critChanceTempIncrease = move.getTemplate().getCritTemporaryIncrease();
        int critChanceIncrease = user.getCritIncrease();
        int totalCritChanceIncrease = critChanceIncrease + critChanceTempIncrease;

        // Takes into consideration crit chance boosts, for 3 and over critical hit is guaranteed
        switch (totalCritChanceIncrease) {
            case 0:
                bound = 16;
                break;
            case 1:
                bound = 8;
                break;
            case 2:
                bound = 2;
                break;
            default:
                bound = 1;
                break;
        }

        int critNum = generator.nextInt(bound);
        boolean isCrit = false;
        float critMod;

        int attackTemp = 0;
        int attackMod = 0;
        int defenseTemp = 0;
        int defenseMod = 0;

        // Check if the executed move is a special or physical move, then grab the corresponding stats
        // Attack for user and defense for target (or Special Attack and Special Defense)
        switch (movetype) {
            case 0:
                attackTemp = user.getStats(Enums.StatType.ATTACK);
                attackMod = user.getStatModifiers().get(Enums.StatType.ATTACK);
                defenseTemp = target.getStats(Enums.StatType.DEFENSE);
                defenseMod = target.getStatModifiers().get(Enums.StatType.DEFENSE);
                break;
            case 1:
                attackTemp = user.getStats(Enums.StatType.SPECIAL_ATTACK);
                attackMod = user.getStatModifiers().get(Enums.StatType.SPECIAL_ATTACK);
                defenseTemp = target.getStats(Enums.StatType.SPECIAL_DEFENSE);
                defenseMod = target.getStatModifiers().get(Enums.StatType.SPECIAL_DEFENSE);
                break;

        }
        //Final effective attack and defense stat
        double attack;
        double defense;

        // Check if the hit is going to be critical, critical hits ignore negative (user-wise) stat changes and only
        // leave positive ones

        if (critNum == 0 && canCrit)
            isCrit = true;

        if (isCrit) {
            if(attackMod > 0)
                attack = (int)Math.round((double) attackTemp * (2 + attackMod)/2);
            else
                attack = attackTemp;

            if(defenseMod < 0) {
                double defense_multiplier = 2.0 / (2 - defenseMod);
                defense = (int) Math.round(defenseTemp * defense_multiplier);
            }
            else
                defense = defenseTemp;

            critMod = 1.5f;
        }
        else  {
            if(attackMod >= 0) {
                attack = (int) Math.round((double) attackTemp * (2 + attackMod) / 2.0);
            }
            else {
                double attack_multiplier = 2.0 / (2 - attackMod);
                attack = (int) Math.round(attackTemp * attack_multiplier);
            }

            if(defenseMod >= 0) {
                defense = (int) Math.round((double) defenseTemp * (2 + defenseMod) / 2.0);
            }
            else {
                double defense_multiplier = 2.0 / (2 - defenseMod);
                defense = (int) Math.round(defenseTemp * defense_multiplier);
            }
            critMod = 1;
        }

        // Check if an ability boosts the output of an attack
        double abilityMultiplier = processAbilityMultiplier(move, user);
        attack = attack * abilityMultiplier;

        //Miscellaneous stats calculation (stab, random factor, burn debuff)
        double part1 = ((2.0 * user.getLevel())/5.0) + 2;
        float rand = (generator.nextInt(16) + 85)/100.0f;
        float stab;
        if (move.getType().equals(user.getType()[0]) || move.getType().equals(user.getType()[1]))
            stab = 1.5f;
        else
            stab = 1f;
        float burn = 1;
        if (movetype == 0 && user.getStatus() == Enums.Status.BURNED)
            burn = 0.5f;

        //Checking how the type of the move is going to affect the damage
        float typeEffect = 1;
        if (move.getType().getNoEffectAgainst().equals(target.getType()[0].getTypeEnum())
                || move.getType().getNoEffectAgainst().equals(target.getType()[1].getTypeEnum())) {
            typeEffect = 0;
        }
        else {
            for (Enums.Types type : move.getType().getStrongAgainst()){
                if(type.equals(target.getType()[0].getTypeEnum()) || type.equals(target.getType()[1].getTypeEnum()))
                    typeEffect *= 2;
            }
            for (Enums.Types type : move.getType().getWeakAgainst()){
                if(type.equals(target.getType()[0].getTypeEnum()) || type.equals(target.getType()[1].getTypeEnum()))
                    typeEffect *= 0.5f;
            }
        }
        //Final damage calculations
        double modifier = critMod * rand * stab * typeEffect * burn * twoTurnModifier;
        int power = move.getPower();
        // if under the effect of a multiturn stacking move (multiturn + doesn't cause confusion + hits set to 1)
        // increase power twofold every turn it is used
        boolean nonConfusionMultiturn = move.isMultiturn() && !move.isMultiturnConfusion();
        if (nonConfusionMultiturn && move.getHits() == 1 && user.getMultiTurnMove() == move) {
            int powerMultiplier =  5 - user.getMultiTurnCounter();
            power *= powerMultiplier;
        }
        double damageDouble = Math.round(((((part1 * power * ((attack/defense)/50.0)) + 2) * modifier)));
        int damage = (int) damageDouble;

        System.out.println(user.getBattleName() + " used a " + move.getType().toString() + " type move!");

        //Critical hit message
        if(isCrit && typeEffect != 0) {
            System.out.println("A critical hit!");
        }

        //Type effect message
        if(typeEffect == 0)
            System.out.println("It doesn't affect " + target.getBattleName() + "...");
        else if(typeEffect > 1)
            System.out.println("It's super effective!");
        else if(typeEffect < 1)
            System.out.println("It's not very effective...");

        return new MoveDamageInfo(damage, isCrit, typeEffect);
    }

    private double processAbilityMultiplier(Move move, Pokemon user) {
        Enums.Types boostedType;
        switch (user.getAbility()) {
            case OVERGROW:
                boostedType = Enums.Types.GRASS;
                break;
            case BLAZE:
                boostedType = Enums.Types.FIRE;
                break;
            case TORRENT:
                boostedType = Enums.Types.WATER;
                break;
            case SWARM:
                boostedType = Enums.Types.BUG;
                break;
            default:
                return 1.0;
        }

        double hpThreshold = (double) user.getMaxHP() / 3;
        if (user.getHp() > hpThreshold && move.getType().getTypeEnum() != boostedType)
            return 1.0;
        return 1.5;
    }

    private void switchPokemon(boolean ally, int slot) {

        Pokemon pokemon, opponent;

        if (ally) {
            currentAllyPokemon = slot;
            pokemon = player.getParty(currentAllyPokemon);
            opponent = enemy.getParty(currentEnemyPokemon);
            allySentOut = true;
        }
        else {
            currentEnemyPokemon = slot;
            pokemon = enemy.getParty(currentEnemyPokemon);
            opponent = player.getParty(currentAllyPokemon);
            enemySentOut = true;
        }

        resetStats(pokemon);
        opponent.setTrapMove(null);
        opponent.setTrappedTimer(0);
    }

    private void resetStats(Pokemon pokemon)
    {
        pokemon.getStatModifiers().clear();
        for(Enums.StatType type: Enums.StatType.values()) {
            if (type != Enums.StatType.MAX_HP)
                pokemon.getStatModifiers().put(type, 0);
        }

        pokemon.setPoisonCounter(1);
        pokemon.getSubStatuses().clear();
        pokemon.setUnderFocusEnergy(false);
        pokemon.setCritIncrease(0);
        pokemon.setTwoTurnMove(null);
        pokemon.setMultiTurnMove(null);
        pokemon.setMultiTurnCounter(0);
        pokemon.setConfusionTimer(0);
    }
}
