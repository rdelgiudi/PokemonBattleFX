package com.delgiudice.pokemonbattlefx.battle;

import com.delgiudice.pokemonbattlefx.*;
import com.delgiudice.pokemonbattlefx.attributes.Enums;
import com.delgiudice.pokemonbattlefx.attributes.Type;
import com.delgiudice.pokemonbattlefx.item.Item;
import com.delgiudice.pokemonbattlefx.move.Move;
import com.delgiudice.pokemonbattlefx.move.MoveDamageInfo;
import com.delgiudice.pokemonbattlefx.move.MoveEnum;
import com.delgiudice.pokemonbattlefx.move.MoveTemplate;
import com.delgiudice.pokemonbattlefx.pokemon.Ability;
import com.delgiudice.pokemonbattlefx.pokemon.Pokemon;
import com.delgiudice.pokemonbattlefx.trainer.NpcTrainer;
import com.delgiudice.pokemonbattlefx.trainer.Player;
import com.sun.java.accessibility.util.java.awt.ListTranslator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;

public class BattleLogic {

    private final BattleController controller;

    private static final String POKEMON_FAINTED_STRING = "%s fainted!";
    public static final String POKEMON_SENT_OUT_STRING = "Go! %s!";
    private static final String MOVE_NO_EFFECT_STRING = "It doesn't affect%n%s...";
    private static final String RECHARGE_INFO = "%s needs to recharge!";
    private static final Font MAIN_FONT = Font.font("Monospaced");

    private Player player;
    private NpcTrainer enemy;

    private final FXMLLoader swapPokemonLoader;
    private final Pane swapPokemonPane;
    private Pane teamBuilderPane;

    private int currentAllyPokemon = 0, currentEnemyPokemon = 0;
    private boolean allyFaintedProcessed = false, enemyFaintedProcessed = false;
    private Move enemyMemory = null;
    private boolean enemySentOut, allySentOut;
    private final HashMap<Enums.BattlefieldCondition, Integer> allyBattlefieldConditions =
            new HashMap<Enums.BattlefieldCondition, Integer>();
    private final HashMap<Enums.BattlefieldCondition, Integer> enemyBattlefieldConditions =
            new HashMap<Enums.BattlefieldCondition, Integer>();
    private final HashMap<Enums.Spikes, Integer> allySpikes =
            new HashMap<Enums.Spikes, Integer>();
    private final HashMap<Enums.Spikes, Integer> enemySpikes =
            new HashMap<Enums.Spikes, Integer>();
    private Pair<Enums.WeatherEffect, Integer> weatherEffect = new Pair<>(Enums.WeatherEffect.NONE, -1);

    public int getCurrentAllyPokemon() {
        return currentAllyPokemon;
    }

    public void setCurrentAllyPokemon(int currentAllyPokemon) {
        this.currentAllyPokemon = currentAllyPokemon;
    }

    public BattleLogic(BattleController controller) {
        this.controller = controller;

        swapPokemonLoader = new FXMLLoader(BattleApplication.class.getResource("swappokemon-view.fxml"));
        try {
            swapPokemonPane = swapPokemonLoader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    // resets the battlefield to default
    private void resetConditions() {
        allyBattlefieldConditions.clear();
        enemyBattlefieldConditions.clear();
        allySpikes.clear();
        enemySpikes.clear();
        currentAllyPokemon = 0;
        currentEnemyPokemon = 0;
        controller.wipeText(false);
        weatherEffect = new Pair<>(Enums.WeatherEffect.NONE, -1);
        controller.updateFieldWeatherEffect(weatherEffect.getKey()).play();
    }

    public void startBattle(Player player, NpcTrainer enemy, Pane teamBuilderPane) {
        resetConditions();
        controller.resetState();

        this.player = player;
        this.enemy = enemy;

        this.teamBuilderPane = teamBuilderPane;

        controller.startBattleThemePlayback();
        initBattleLoop();
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
        Timeline enemyInfoAnimation = controller.getIntroAnimation(enemy.getParty(currentEnemyPokemon),
                enemy.getParty(currentEnemyPokemon).getHp());
        enemyPokemonIntro.setOnFinished(e -> enemyInfoAnimation.play());

        Timeline allyPokemonIntro = controller.getBattleTextAnimation(String.format(POKEMON_SENT_OUT_STRING,
                player.getParty(currentAllyPokemon).getBattleName()), true);
        allyPokemonIntro.setDelay(Duration.seconds(1));
        enemyInfoAnimation.setOnFinished(e -> allyPokemonIntro.play());

        Timeline allyInfoAnimation = controller.getIntroAnimation(player.getParty(currentAllyPokemon),
                player.getParty(currentAllyPokemon).getHp());
        allyPokemonIntro.setOnFinished(e -> allyInfoAnimation.play());

        allyInfoAnimation.setOnFinished(e -> {
            battleLoop();
        });

        battleTextIntro.play();
    }

    private int getAllyAbleToBattleNum() {
        int num = 0;

        for (Pokemon pokemon : player.getParty()) {
            if (pokemon.getHp() > 0) {
                num++;
            }
        }
        return num;
    }

    private int getEnemyAbleToBattleNum() {
        int num = 0;

        for (Pokemon pokemon : enemy.getParty()) {
            if (pokemon.getHp() > 0) {
                num++;
            }
        }
        return num;
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
        paused.setOnFinished(e -> {
            controller.endBattleThemePlayback();
            stage.setTitle("Pokemon Battle FX");
            controller.getFightButton().getScene().setRoot(teamBuilderPane);
        });

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
        paused.setOnFinished(e -> {
            controller.endVictoryThemePlayback();
            stage.setTitle("Pokemon Battle FX");
            controller.getFightButton().getScene().setRoot(teamBuilderPane);
        });

        for (Pokemon pokemon : player.getParty()) {
            pokemon.restoreAll();
        }
        for (Pokemon pokemon : enemy.getParty()) {
            pokemon.restoreAll();
        }

        battleWonMsg1.play();
        controller.endBattleThemePlayback();
        controller.startVictoryThemePlayback();
        controller.stopLowHpEffect();
    }

    private void processToxicSpikeEffect(List<Timeline> battleTimeLine, Pokemon target) {
        boolean toxicSpikeImmuneType = target.containsType(Enums.Types.FLYING) ||
                target.containsType(Enums.Types.POISON) || target.containsType(Enums.Types.STEEL);
        boolean toxicSpikeImmuneAbility = false; //TODO: Add abilities (remember about Gastro Acid!)

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

            if (enemyPokemon.getAbility() == Ability.INTIMIDATE) {
                int change = -1;
                int currentStatModifier = allyPokemon.getStatModifiers().get(Enums.StatType.ATTACK);
                if (currentStatModifier > -6) {
                    int statup = currentStatModifier + change;
                    allyPokemon.getStatModifiers().put(Enums.StatType.ATTACK, statup);

                    Timeline abilityMessage = controller.getEnemyAbilityInfoAnimation(enemyPokemon);

                    Timeline intimidateMessage = controller.getBattleTextAnimation(String.format(
                            "%s's Intimidate\nlowers %s Attack!", enemyPokemon.getBattleName(),
                            allyPokemon.getBattleNameMiddle()), true);

                    battleTimeLine.add(abilityMessage);
                    battleTimeLine.add(intimidateMessage);
                    battleTimeLine.add(controller.generatePause(1000));
                }
            }

            boolean enemyFainted = enemy.getParty(currentEnemyPokemon).getHp() == 0;
            if (enemyFainted && !enemyFaintedProcessed) {
                processEnemyFainted(battleTimeLine);
            }
        }
        if (allySentOut) {
            allySentOut = false;

            if (allySpikes.containsKey(Enums.Spikes.TOXIC_SPIKES)) {
                processToxicSpikeEffect(battleTimeLine, allyPokemon);
            }

            if (allyPokemon.getAbility() == Ability.INTIMIDATE) {
                int change = -1;
                int currentStatModifier = enemyPokemon.getStatModifiers().get(Enums.StatType.ATTACK);
                if (currentStatModifier > -6) {
                    int statup = currentStatModifier + change;
                    enemyPokemon.getStatModifiers().put(Enums.StatType.ATTACK, statup);

                    Timeline abilityMessage = controller.getAllyAbilityInfoAnimation(allyPokemon);

                    Timeline intimidateMessage = controller.getBattleTextAnimation(String.format(
                            "%s's Intimidate\nlowers %s Attack!", allyPokemon.getBattleName(),
                            enemyPokemon.getBattleNameMiddle()), true);

                    battleTimeLine.add(abilityMessage);
                    battleTimeLine.add(intimidateMessage);
                    battleTimeLine.add(controller.generatePause(1000));
                }
            }

            boolean allyFainted = player.getParty(currentAllyPokemon).getHp() == 0;
            if (allyFainted && !allyFaintedProcessed) {
                processAllyFainted(battleTimeLine);
            }
        }

        if (checkBattleEnd(battleTimeLine))
            return;
    }

    private void battleLoop() {

        allyFaintedProcessed = false;
        enemyFaintedProcessed = false;

        controller.wipeText(false);

        Pokemon allyPokemon = player.getParty(currentAllyPokemon);
        Pokemon enemyPokemon = enemy.getParty(currentEnemyPokemon);

        // Check if laser focus was activated last turn, if expired deletes substatus
        if (allyPokemon.isLaserFocusActive())
            allyPokemon.setLaserFocusActive(false);
        else
            allyPokemon.getSubStatuses().remove(Enums.SubStatus.LASER_FOCUS);
        if (enemyPokemon.isLaserFocusActive())
            enemyPokemon.setLaserFocusActive(false);
        else
            enemyPokemon.getSubStatuses().remove(Enums.SubStatus.LASER_FOCUS);

        // Deletes substatuses that last only during the turn they are caused
        allyPokemon.getSubStatuses().remove(Enums.SubStatus.FLINCHED);
        enemyPokemon.getSubStatuses().remove(Enums.SubStatus.FLINCHED);
        allyPokemon.getSubStatuses().remove(Enums.SubStatus.ROOST);
        enemyPokemon.getSubStatuses().remove(Enums.SubStatus.ROOST);

        Timeline done = controller.generatePause(1);

        Timeline playerChoiceDialog = controller.getBattleText(String.format("What will%n%s do?",
                player.getParty(currentAllyPokemon).getBattleName()), false);
        done.setOnFinished(e -> {
            if (allyPokemon.getStateMove() != null && allyPokemon.getState() == Enums.States.TWOTURN) {
                battleTurn(allyPokemon.getStateMove());
                return;
            }
            else if (allyPokemon.getStateMove() != null && allyPokemon.getStateCounter() > 0 &&
                    allyPokemon.getState() == Enums.States.MULTITURN) {
                //allyPokemon.setMultiTurnCounter(allyPokemon.getMultiTurnCounter() - 1);
                battleTurn(allyPokemon.getStateMove());
                return;
            }
            else if (allyPokemon.getSubStatuses().contains(Enums.SubStatus.RECHARGE)) {
                battleTurn(null);
                return;
            }

            playerChoiceDialog.play();
            controller.switchToPlayerChoice(true);
        });

        List<Timeline> moveStartTimeLine = new ArrayList<>();

        deleteEndedConditions(moveStartTimeLine);
        applySentOutEffects(moveStartTimeLine);


        if (!moveStartTimeLine.isEmpty()) {
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
                    controller.getMoveGrid().setVisible(false);
                    controller.switchToPlayerChoice(false);
                    Timeline outOfPPInfo = controller.getBattleTextAnimation("This move is out of PP!", true);
                    Timeline playerChoiceDialog2 = controller.getBattleText(String.format("What will%n%s do?",
                            player.getParty(currentAllyPokemon).getBattleName()), false);
                    playerChoiceDialog2.setDelay(Duration.seconds(1));
                    outOfPPInfo.setOnFinished(event -> playerChoiceDialog2.play());
                    playerChoiceDialog2.setOnFinished(event -> {
                        controller.getMoveGrid().setVisible(true);
                        controller.switchToPlayerChoice(true);
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
            Stage stage = (Stage) pokemonButton.getScene().getWindow();
            SwapPokemonController swapPokemonController = swapPokemonLoader.getController();
            swapPokemonController.initVariables((Pane) pokemonButton.getScene().getRoot(), this, controller, player.getParty(),
                    false, Enums.SwitchContext.SWITCH_FIRST);
            pokemonButton.getScene().setRoot(swapPokemonPane);
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

    public void battleTurn(Move allyMove) {
        List<Timeline> battleTimeLine = new ArrayList<>();

        SecureRandom generator = new SecureRandom();
        applySentOutEffects(battleTimeLine);

        // sets timers to timer-1, then a check is made at the end of the turn that erases conditions that should be disabled
        processBattlefieldConditionsTimer();

        controller.getMoveGrid().setVisible(false);
        controller.switchToPlayerChoice(false);

        Pokemon allyPokemon = player.getParty(currentAllyPokemon);
        Pokemon enemyPokemon = enemy.getParty(currentEnemyPokemon);

        Move enemyMove;

        if (enemyMemory != null) {
            enemyMove = enemyMemory;
            enemyMemory = null;
        }
        else
            enemyMove = !enemyPokemon.getSubStatuses().contains(Enums.SubStatus.RECHARGE) ?
                generateEnemyMove(enemyPokemon) : null;

        // Speed calculation
        double playerSpeed, enemySpeed;
        double[] speeds;
        speeds = calculateEffectiveSpeeds(allyPokemon, enemyPokemon);
        playerSpeed = speeds[0];
        enemySpeed = speeds[1];

        boolean faster = playerSpeed > enemySpeed;
        boolean tied = playerSpeed == enemySpeed;
        //******************************************


        int allyPriority = allyMove != null ? allyMove.getPriority() : 0;
        int enemyPriority = enemyMove != null ? enemyMove.getPriority() : 0;

        // Move of higher priority is always faster
        if (allyPriority > enemyPriority) {
            if (allyMove != null && allyMove.isSwitchOut() && getAllyAbleToBattleNum() > 1) {
                initAllyTurnWithSwap(battleTimeLine, allyMove, allyPokemon, enemyPokemon, enemyMove, true);
                return;
            }
            battleTimeLine.addAll(processTurn(allyMove, allyPokemon, enemyMove, enemyPokemon));
        }
        else if (allyPriority < enemyPriority) {
            if (allyMove != null && allyMove.isSwitchOut() && getAllyAbleToBattleNum() > 1) {
                initAllyTurnWithSwap(battleTimeLine, allyMove, allyPokemon, enemyPokemon, enemyMove, false);
                return;
            }
            battleTimeLine.addAll(processTurn(enemyMove, enemyPokemon, allyMove, allyPokemon));
        }
        // If both or neither parties are using priority moves, being faster depends on speed
        else if (faster) {
            if (allyMove != null && allyMove.isSwitchOut() && getAllyAbleToBattleNum() > 1) {
                initAllyTurnWithSwap(battleTimeLine, allyMove, allyPokemon, enemyPokemon, enemyMove, true);
                return;
            }
            battleTimeLine.addAll(processTurn(allyMove, allyPokemon, enemyMove, enemyPokemon));
        }
        //On speed tie, the first attacker is randomized
        else if (tied) {
            int flip = generator.nextInt(2);
            if (flip == 1) {
                if (allyMove != null && allyMove.isSwitchOut() && getAllyAbleToBattleNum() > 1) {
                    initAllyTurnWithSwap(battleTimeLine, allyMove, allyPokemon, enemyPokemon, enemyMove, true);
                    return;
                }
                battleTimeLine.addAll(processTurn(allyMove, allyPokemon, enemyMove, enemyPokemon));
            }
            else {
                if (allyMove != null && allyMove.isSwitchOut() && getAllyAbleToBattleNum() > 1) {
                    initAllyTurnWithSwap(battleTimeLine, allyMove, allyPokemon, enemyPokemon, enemyMove, false);
                    return;
                }
                battleTimeLine.addAll(processTurn(enemyMove, enemyPokemon, allyMove, allyPokemon));
            }
        }
        //Here is what happens if enemy Pokemon is faster
        else {
            if (allyMove != null && allyMove.isSwitchOut() && getAllyAbleToBattleNum() > 1) {
                initAllyTurnWithSwap(battleTimeLine, allyMove, allyPokemon, enemyPokemon, enemyMove, false);
                return;
            }
            battleTimeLine.addAll(processTurn(enemyMove, enemyPokemon, allyMove, allyPokemon));
        }

        battleTurnEnd(battleTimeLine);
    }

    private void initAllyTurnWithSwap(List<Timeline> battleTimeLine ,Move allyMove, Pokemon allyPokemon, Pokemon enemyPokemon,
                                      Move enemyMove, boolean first ) {
        if (first)
            battleTimeLine.addAll(useMove(allyMove, allyPokemon, enemyPokemon, first));
        else
            battleTimeLine.addAll(processTurn(enemyMove, enemyPokemon, allyMove, allyPokemon));

        processFainted(battleTimeLine);
        if (checkBattleEnd(battleTimeLine))
            return;

        // If move switches out user, open switch menu when another party Pokemon is able to battle
        if (allyMove.isSwitchOut() && getAllyAbleToBattleNum() > 1 && allyPokemon.getHp() > 0) {
            Button pokemonButton = controller.getPokemonButton();
            battleTimeLine.get(battleTimeLine.size() - 1).setOnFinished(e -> {
                SwapPokemonController swapPokemonController = swapPokemonLoader.getController();
                Enums.SwitchContext switchContext = first ?
                        Enums.SwitchContext.SWITCH_FIRST_MOVE : Enums.SwitchContext.SWITCH_SECOND_MOVE;
                swapPokemonController.initVariables((Pane) pokemonButton.getScene().getRoot(),
                        this, controller, player.getParty(), true, switchContext);
                pokemonButton.getScene().setRoot(swapPokemonPane);
            });
        }
        else if (allyPokemon.getHp() == 0) {
            battleTurnEnd(battleTimeLine);
            return;
        }

        initAnimationQueue(battleTimeLine);
        battleTimeLine.get(0).play();
        if (first)
            enemyMemory = enemyMove;
    }

    private double[] calculateEffectiveSpeeds(Pokemon allyPokemon, Pokemon enemyPokemon) {
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

        return new double[]{playerSpeed, enemySpeed};
    }

    // For now enemy move is randomly generated, maybe gym leader AI implementation in the future
    private Move generateEnemyMove(Pokemon enemyPokemon) {
        Move enemyMove;

        if (enemyPokemon.getStateMove() != null && enemyPokemon.getStateCounter() > 0 &&
                enemyPokemon.getState() == Enums.States.MULTITURN) {
            enemyMove = enemyPokemon.getStateMove();
            return enemyMove;
        }
        if (enemyPokemon.getStateMove() != null) {
            enemyMove = enemyPokemon.getStateMove();
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

    private void processFainted(List<Timeline> battleTimeLine) {
        boolean allyFainted = player.getParty(currentAllyPokemon).getHp() == 0;
        boolean enemyFainted = enemy.getParty(currentEnemyPokemon).getHp() == 0;

        if (allyFainted && !allyFaintedProcessed) {
            processAllyFainted(battleTimeLine);
        }
        if (enemyFainted && !enemyFaintedProcessed) {
            processEnemyFainted(battleTimeLine);
        }
    }

    public void battleTurnEnd(List<Timeline> battleTimeLine) {
        processFainted(battleTimeLine);

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
            if (enemyFainted && !enemyFaintedProcessed) {
                processEnemyFainted(battleTimeLine);
            }
        }

        if (!allyFainted)
            allyStatusEffect = processDamageStatusEffects(player.getParty(currentAllyPokemon));

        allyFainted = player.getParty(currentAllyPokemon).getHp() == 0;

        if (allyStatusEffect != null) {
            battleTimeLine.addAll(allyStatusEffect);
            if (allyFainted && !allyFaintedProcessed) {
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
            if (allyFainted && !allyFaintedProcessed) {
                processAllyFainted(battleTimeLine);
            }
        }

        if (!enemyFainted)
            enemyTrappedEffect = processDamageTrapped(enemy.getParty(currentEnemyPokemon));
        enemyFainted = enemy.getParty(currentEnemyPokemon).getHp() == 0;

        if (enemyTrappedEffect != null) {
            battleTimeLine.addAll(enemyTrappedEffect);
            if (enemyFainted && !enemyFaintedProcessed) {
                processEnemyFainted(battleTimeLine);
            }
        }

        if (checkBattleEnd(battleTimeLine))
            return;

        //finalChecks(battleTimeLine);
        applyWeatherEffects(battleTimeLine);
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

                battleTimeLine.get(0).play();
                return true;
            }

        }

        if (enemyFainted) {
            if (!checkIfEnemyAbleToBattle(false)) {
                initAnimationQueue(battleTimeLine);

                if (!battleTimeLine.isEmpty()) {
                    battleTimeLine.get(battleTimeLine.size() - 1).setOnFinished(e -> {
                        battleWon();
                    });
                    battleTimeLine.get(0).play();
                }
                else
                    battleWon();
                return true;
            }
        }
        return false;
    }

    // Updates weather timer as well as applies additional effects (for example Hail and Sandstorm damage)
    private void applyWeatherEffects(List<Timeline> battleTimeLine) {

        Enums.WeatherEffect currentWeatherEffect = weatherEffect.getKey();

        switch (currentWeatherEffect) {
            case NONE:
                finalChecks(battleTimeLine);
                break;
            case RAIN:
                int weatherEffectTimer = weatherEffect.getValue();
                Timeline rainMessage;
                if (weatherEffectTimer > 0) {
                    weatherEffectTimer--;
                    weatherEffect = new Pair<>(currentWeatherEffect, weatherEffectTimer);
                    rainMessage = controller.getBattleTextAnimation("Rain continues to fall...", true);
                }
                else {
                    rainMessage = controller.getBattleTextAnimation("The rain stopped.", true);
                    weatherEffect = new Pair<>(Enums.WeatherEffect.NONE, -1);
                    battleTimeLine.add(controller.updateFieldWeatherEffect(weatherEffect.getKey()));
                }
                battleTimeLine.add(rainMessage);
                battleTimeLine.add(controller.generatePause(1000));
                break;
            default:
                throw new IllegalStateException("Unhandled weather condition: " + currentWeatherEffect);
        }

        finalChecks(battleTimeLine);
    }

    // Send out new ally Pokemon if available, else game over for the player, even if enemy also out of Pokemon
    // Send out new enemy Pokemon and config timeline list
    public void finalChecks(List<Timeline> battleTimeLine) {

        if (checkBattleEnd(battleTimeLine))
            return;

        boolean allyFainted = player.getParty(currentAllyPokemon).getHp() == 0;
        boolean enemyFainted = enemy.getParty(currentEnemyPokemon).getHp() == 0;

        if (allyFainted) {
            battleTimeLine.get(battleTimeLine.size() - 1).setOnFinished(e -> {
                //controller.pokemonButtonPressed(player.getParty());
                //controller.switchToPlayerChoice(true);
                //setPokemonSwapListeners(true);


                Button pokemonButton = controller.getPokemonButton();

                Stage stage = (Stage) pokemonButton.getScene().getWindow();
                SwapPokemonController swapPokemonController = swapPokemonLoader.getController();
                swapPokemonController.initVariables((Pane) pokemonButton.getScene().getRoot(), this, controller, player.getParty(),
                        true, Enums.SwitchContext.SWITCH_FAINTED);
                pokemonButton.getScene().setRoot(swapPokemonPane);
            });
            initAnimationQueue(battleTimeLine);
            battleTimeLine.get(0).play();
            return;
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

            Timeline pokemonIntroAnimation = controller.getIntroAnimation(enemy.getParty(currentEnemyPokemon),
                    enemy.getParty(currentEnemyPokemon).getHp());
            battleTimeLine.add(pokemonIntroAnimation);
        }

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

    public void initAnimationQueue(List<Timeline> battleTimeLine) {
        for (int i=1; i<battleTimeLine.size(); i++) {
            final int finalI = i;
            if (battleTimeLine.get(i-1).getOnFinished() == null)
                battleTimeLine.get(i-1).setOnFinished(e -> battleTimeLine.get(finalI).play());
        }
    }

    // Calculates damage dealt as a result of vortex traps like Fire Spin
    private List<Timeline> processDamageTrapped(Pokemon pokemon) {

        List<Timeline> timelineList = new ArrayList<>();

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

        if (pokemon.getAbility() == Ability.SHED_SKIN && damagingStatusEffect &&
                !pokemon.getSubStatuses().contains(Enums.SubStatus.GASTRO_ACID)) {
            SecureRandom random = new SecureRandom();
            int rand = random.nextInt(3);

            if (rand == 0) {
                List<Timeline> timelineList = new ArrayList<>();
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

            List<Timeline> timelineList = new ArrayList<>();

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

    private Timeline setFaintedStatus(Pokemon pokemon) {
        KeyFrame kf = new KeyFrame(Duration.millis(1), e -> pokemon.setStatus(Enums.Status.FAINTED));
        return new Timeline(kf);
    }

    // Processes fainting enemy
    private void processEnemyFainted(List<Timeline> battleTimeLine) {
        Timeline enemyPokemonFainted = controller.getPokemonFaintedAnimation(false);
        //enemyPokemonFainted.setDelay(Duration.seconds(2));

        battleTimeLine.add(enemyPokemonFainted);
        battleTimeLine.add(setFaintedStatus(enemy.getParty(currentEnemyPokemon)));
        battleTimeLine.add(controller.generatePause(1000));

        Timeline enemyPokemonFaintedMessage = controller.getBattleTextAnimation(String.format(POKEMON_FAINTED_STRING,
                enemy.getParty(currentEnemyPokemon).getBattleName()), true);
        System.out.println(enemy.getParty(currentEnemyPokemon).getBattleName() + " fainted!");
        //enemyPokemonFaintedMessage.setDelay(Duration.seconds(1));

        battleTimeLine.add(enemyPokemonFaintedMessage);
        battleTimeLine.add(controller.generatePause(1));
        controller.battleTextAdvanceByUserInput(battleTimeLine.get(battleTimeLine.size() - 2),
                battleTimeLine.get(battleTimeLine.size() - 1));

        player.getParty(currentAllyPokemon).setTrapMove(null);
        player.getParty(currentAllyPokemon).setTrappedTimer(0);
        enemyMemory = null;

        enemyFaintedProcessed = true;

        //finalChecks(battleTimeLine, enemyFainted);
    }

    // Processes fainting ally
    private void processAllyFainted(List<Timeline> battleTimeLine) {
        Timeline allyPokemonFainted = controller.getPokemonFaintedAnimation(true);
        //allyPokemonFainted.setDelay(Duration.seconds(2));

        battleTimeLine.add(allyPokemonFainted);
        battleTimeLine.add(setFaintedStatus(player.getParty(currentAllyPokemon)));

        Timeline allyPokemonFaintedMessage = controller.getBattleTextAnimation(String.format(POKEMON_FAINTED_STRING,
                player.getParty(currentAllyPokemon).getBattleName()), true);
        System.out.println(player.getParty(currentAllyPokemon).getBattleName() + " fainted!");
        //allyPokemonFaintedMessage.setDelay(Duration.seconds(1));

        battleTimeLine.add(allyPokemonFaintedMessage);
        battleTimeLine.add(controller.generatePause(1));
        controller.battleTextAdvanceByUserInput(battleTimeLine.get(battleTimeLine.size() - 2),
                battleTimeLine.get(battleTimeLine.size() - 1));

        enemy.getParty(currentEnemyPokemon).setTrapMove(null);
        enemy.getParty(currentEnemyPokemon).setTrappedTimer(0);

        allyFaintedProcessed = true;
        //battleTimeLine.get(0).play();
        //finalChecks(battleTimeLine, enemyFainted);

    }

    // Process turn depending on which Pokemon moves first (named firstPokemon and firstMove)
    private List<Timeline> processTurn(Move firstMove, Pokemon firstPokemon, Move secondMove, Pokemon secondPokemon) {

        List<Timeline> moveTimeLine = new ArrayList<>();

        //Random generator = new Random();
        if (firstMove == null && firstPokemon.getSubStatuses().contains(Enums.SubStatus.RECHARGE)) {
            Timeline firstRechargeMessage = controller.getBattleTextAnimation(String.format(
                    RECHARGE_INFO, firstPokemon.getBattleName()), true);
            moveTimeLine.add(firstRechargeMessage);
            moveTimeLine.add(controller.generatePause(2000));
            firstPokemon.getSubStatuses().remove(Enums.SubStatus.RECHARGE);
        }
        else if (firstMove != null) {
            moveTimeLine.addAll(useMove(firstMove, firstPokemon,
                    secondPokemon, true));
        }

        if (!firstPokemon.getOwner().isPlayer() && firstMove != null && firstMove.isSwitchOut())
            firstPokemon = enemy.getParty(currentEnemyPokemon);

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
        else if (secondMove != null)
            moveTimeLine.addAll(useMove(secondMove,
                secondPokemon, firstPokemon, false));

        return moveTimeLine;
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
        user.setSleepCounter(user.getSleepCounter() - 1);
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

        if (user.getStateMove() != null && user.getState() == Enums.States.MULTITURN)
            checkMultiturnMoveInterruptEffect(moveTimeLine, user);
    }

    private void processTwoTurnMoveComplete(List<Timeline> moveTimeLine, Pokemon user) {
        if (user.getState() == Enums.States.TWOTURN) {
            user.setStateMove(null);
            user.setState(Enums.States.NONE);

            if (!user.getOwner().isPlayer()) {
                Timeline revealEnemy = new Timeline(new KeyFrame(Duration.millis(1),
                        e -> controller.getEnemyPokemonSprite().setVisible(true)));
                moveTimeLine.add(revealEnemy);
            } else {
                Timeline revealAlly = new Timeline(new KeyFrame(Duration.millis(1),
                        e -> controller.getAllyPokemonSprite().setVisible(true)));
                moveTimeLine.add(revealAlly);
            }
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
            case FLY:
                timeline = controller.getBattleTextAnimation(String.format(
                        "%s flew%nup high!", user.getBattleName()), true);
                break;
            default:
                throw new IllegalStateException("Unidentified twoturn move: "
            + move.getName());
        }
        return timeline;
    }

    private int checkTwoTurnMiss(Move move, Pokemon target) {
        Move twoturnmove = target.getStateMove();

        if (target.getState() != Enums.States.TWOTURN || twoturnmove == null || twoturnmove.isCharging())
            return 1;

        MoveEnum twoturnName = twoturnmove.getName();
        MoveEnum attackingMoveName = move.getName();

        boolean digEarthquake = Objects.equals(twoturnName, MoveEnum.DIG) && Objects.equals(attackingMoveName,
                MoveEnum.EARTHQUAKE);
        if (digEarthquake) {
            return 2;
        }

        boolean diveWhirlpool = Objects.equals(twoturnName, MoveEnum.DIVE) && Objects.equals(attackingMoveName,
                MoveEnum.WHIRLPOOL);
        if (diveWhirlpool) {
            return 2;
        }

        boolean hurricaneInteraction = (Objects.equals(twoturnName, MoveEnum.FLY) ||
                Objects.equals(twoturnName, MoveEnum.BOUNCE) ||
                Objects.equals(twoturnName, MoveEnum.SKY_DROP)) && Objects.equals(attackingMoveName,
                MoveEnum.HURRICANE);
        if (hurricaneInteraction)
            return 1;

        return 0;
    }

    private List<Timeline> processHealthRestore(List<Timeline> moveTimeLine, Move move, Pokemon user) {
        if (user.getHp() != user.getMaxHP()) {
            double moveHpRestore = move.getHpRestore();

            if (weatherEffect.getKey() == Enums.WeatherEffect.RAIN && move.getName() == MoveEnum.SYNTHESIS)             // TODO: also affects Morning Sun and Moonlight
                moveHpRestore *= 0.5;

            double restoredHealth = user.getMaxHP() * moveHpRestore;
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

            System.out.printf("%s restored %d health%n", user.getBattleName(), healthChangeInt - oldHp);
            moveTimeLine.add(userHealthInfo);

            // If Roost used
            if (move.getSubStatus() == Enums.SubStatus.ROOST)
                user.getSubStatuses().add(Enums.SubStatus.ROOST);
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
            moveTimeLine.add(controller.generatePause(1500));
            applyConfusion(user);
        }
        processMultiturnMoveInterrupted(user);
    }

    private void processMultiturnMoveInterrupted(Pokemon user) {
        user.setStateMove(null);
        user.setStateCounter(0);
        user.setState(Enums.States.NONE);
    }

    private void checkMultiturnMoveInterruptEffect(List<Timeline> moveTimeLine, Pokemon user) {
        if (user.getState() == Enums.States.MULTITURN && (
                user.getStateCounter() > 0 || !user.getStateMove().isMultiturnConfusion())) {
            processMultiturnMoveInterrupted(user);
        }
        else if (user.getState() == Enums.States.MULTITURN && user.getStateCounter() == 0) {
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
        else
            user.setConfusionTimer(user.getConfusionTimer() - 1);
    }

    private void processConfusionHit(List<Timeline> moveTimeLine, Pokemon user) {
        Timeline messageConfusion = controller.getBattleTextAnimation("It hurt itself in its\nconfusion!", true);
        moveTimeLine.add(messageConfusion);
        moveTimeLine.add(controller.generatePause(500));

        MoveDamageInfo confusionDamageInfo = calculateMoveDamage(new Move(
                MoveTemplate.getMoveMap().get(MoveEnum.CONFUSION_DAMAGE)), user, user, 1, false, -1);

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
        if (user.getStateMove() != null) {
            if (user.getState() == Enums.States.MULTITURN)
                checkMultiturnMoveInterruptEffect(moveTimeLine, user);
            if (user.getState() == Enums.States.TWOTURN)
                processTwoTurnMoveComplete(moveTimeLine, user);
        }
    }

    private float calculateMoveAccuracyModifier(Pokemon user, Pokemon target, Move move) {
        int statAccuracy;
        if (user.getAbility() != Ability.KEEN_EYE || user.getSubStatuses().contains(Enums.SubStatus.GASTRO_ACID))
            statAccuracy = user.getStatModifiers().get(Enums.StatType.ACCURACY) -
                    target.getStatModifiers().get(Enums.StatType.EVASIVENESS);
        else
            statAccuracy = user.getStatModifiers().get(Enums.StatType.ACCURACY);

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

        if (user.getAbility() == Ability.COMPOUND_EYES && !move.isOneHitKOMove() && !user.getSubStatuses().contains(Enums.SubStatus.GASTRO_ACID)) {
            accuracyModifier *= 1.3;
        }

        return accuracyModifier;
    }

    private void processBattlefieldConditionMove(List<Timeline> moveTimeline, Move move, Pokemon user) {

        Timeline conditionMessage;

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
                break;
            default:
                throw new IllegalStateException("Unknown battlefield condition: " + move.getCondition());
        }

        moveTimeline.add(conditionMessage);
        moveTimeline.add(controller.generatePause(2000));
    }

    private void processWeatherConditionMove(List<Timeline> moveTimeLine, Move move, Pokemon user) {
        Timeline weatherMessage;
        Timeline weatherChange;
        Enums.WeatherEffect moveWeatherEffect = move.getWeatherEffect();

        if (moveWeatherEffect == weatherEffect.getKey()) {
            weatherMessage = controller.getBattleTextAnimation("But it failed!", true);
            moveTimeLine.add(weatherMessage);
            moveTimeLine.add(controller.generatePause(2000));
            return;
        }

        switch (moveWeatherEffect) {
            case RAIN:
                weatherMessage = controller.getBattleTextAnimation("It started to rain!", true);
                weatherEffect = new Pair<>(moveWeatherEffect, 5);
                weatherChange = controller.updateFieldWeatherEffect(moveWeatherEffect);
                System.out.println("Weather effect applied: " + moveWeatherEffect);
                break;
            default:
                throw new IllegalStateException("Unknown weather effect: " + move.getWeatherEffect());
        }

        moveTimeLine.add(weatherMessage);
        moveTimeLine.add(weatherChange);
        moveTimeLine.add(controller.generatePause(2000));
    }

    // Processes using a move, as well as status effects or accuracy checks that might prevent from using it
    private List<Timeline> useMove(Move move, Pokemon user, Pokemon target, boolean first) {

        // Initiating the list of animations to be performed during move and an RNG
        List<Timeline> moveTimeLine = new ArrayList<>();
        SecureRandom generator = new SecureRandom();

        // Decreases the multiturn move counter, this is done here because interrupting a multiturn move
        // on its last turn will result in the same effect as the move completing
        if (user.getStateCounter() > 0 && user.getStateMove() != null && user.getState() == Enums.States.MULTITURN)
            user.setStateCounter(user.getStateCounter() - 1);

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
                if (user.getStateMove() != null) {
                    if (user.getState() == Enums.States.MULTITURN)
                        checkMultiturnMoveInterruptEffect(moveTimeLine, user);
                    else if (user.getState() == Enums.States.TWOTURN)
                        processTwoTurnMoveComplete(moveTimeLine, user);
                }
                return moveTimeLine;
            }
        }

        if (user.getStatus() == Enums.Status.SLEEPING && user.getSleepCounter() > 0) {
            processUserAsleep(user, moveTimeLine);
            if (user.getStateMove() != null) {
                if (user.getState() == Enums.States.MULTITURN)
                    checkMultiturnMoveInterruptEffect(moveTimeLine, user);
                else if (user.getState() == Enums.States.TWOTURN)
                    processTwoTurnMoveComplete(moveTimeLine, user);
            }
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
                if (user.getStateMove() != null) {
                    if (user.getState() == Enums.States.MULTITURN)
                        checkMultiturnMoveInterruptEffect(moveTimeLine, user);
                    else if (user.getState() == Enums.States.TWOTURN)
                        processTwoTurnMoveComplete(moveTimeLine, user);
                }
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
        if (move.isTwoturn() && user.getStateMove() == null) {
            Timeline allyTwoTurnInfo = getTwoTurnMoveInfo(move, user);
            moveTimeLine.add(allyTwoTurnInfo);
            user.setStateMove(move);
            user.setState(Enums.States.TWOTURN);
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
        if(user.getState() != Enums.States.MULTITURN)
            move.setPp(move.getPp() - 1);

        int moveAccuracy = move.getAccuracy();

        if (weatherEffect.getKey() == Enums.WeatherEffect.RAIN && move.getName() == MoveEnum.THUNDER)
            moveAccuracy = MoveTemplate.NOT_APPLICABLE;

        int twoTurnModifier = checkTwoTurnMiss(move, target);

        // If move applies a battlefield condition, apply the effect
        if (move.getCondition() != Enums.BattlefieldCondition.NONE) {
            processBattlefieldConditionMove(moveTimeLine, move, user);
            return moveTimeLine;
        }

        // If move applies a weather condition, apply the effect
        if (move.getWeatherEffect() != Enums.WeatherEffect.NONE) {
            processWeatherConditionMove(moveTimeLine, move, user);
            return moveTimeLine;
        }

        // Type effect calculation, if the move can't hit a Pokemon the function returns
        // Status moves generally ignore all type interactions, however there are some exceptions to this rule
        Type firstTargetType = target.getType()[0];
        Type secondTargetType = target.getType()[1];

        // Check if target under Roost, which disables flying type interactions, effectively taking it away
        // if the target is monotype, it makes the target Normal type instead
        if (target.getSubStatuses().contains(Enums.SubStatus.ROOST)) {
            if (firstTargetType.getTypeEnum() == Enums.Types.FLYING)
                firstTargetType = secondTargetType.getTypeEnum() != Enums.Types.NO_TYPE ?
                        Type.getTypeMap(Enums.Types.NO_TYPE) : Type.getTypeMap(Enums.Types.NORMAL);
            else if (secondTargetType.getTypeEnum() == Enums.Types.FLYING)
                secondTargetType = Type.getTypeMap(Enums.Types.NO_TYPE);
        }

        boolean moveTypeNoEffectOnTarget = move.getType().getNoEffectAgainst().equals(firstTargetType.getTypeEnum())
                || move.getType().getNoEffectAgainst().equals(secondTargetType.getTypeEnum());

        boolean statusMovesExceptionGround = move.getType().getTypeEnum() == Enums.Types.ELECTRIC &&
                (firstTargetType.getTypeEnum() == Enums.Types.GROUND ||
                        secondTargetType.getTypeEnum() == Enums.Types.GROUND);

        if (!move.isSelf() && moveTypeNoEffectOnTarget && (move.getSubtype() != Enums.Subtypes.STATUS || statusMovesExceptionGround)) {
            final Timeline effectInfo = controller.getBattleTextAnimation(String.format(MOVE_NO_EFFECT_STRING,
                    target.getBattleNameMiddle()), true);
            //effectInfo.setDelay(Duration.seconds(2));
            System.out.println("No effect on " + target.getBattleNameMiddle());

            moveTimeLine.add(effectInfo);
            moveTimeLine.add(controller.generatePause(2000));

            return moveTimeLine;
        }
        //****************************************************

        // Move hit calculations, if move misses the function ends
        if (moveAccuracy != MoveTemplate.NOT_APPLICABLE) {
            float accuracyModifier = calculateMoveAccuracyModifier(user, target, move);
            float hit = moveAccuracy * accuracyModifier;
            int r = generator.nextInt(100) + 1;
            if (r > hit || twoTurnModifier == 0) {
                processMoveMissed(user, moveTimeLine);
                return moveTimeLine;
            }
        }
        //**********************************************

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
            else if (hitsRand < 70)
                hitsTemp = 3;
            else if (hitsRand < 85)
                hitsTemp = 4;
            else
                hitsTemp = 5;

            hits = hitsTemp;
        }

        for (i=0; i<hits; i++) {
            if (target.getHp() == 0)
                break;

            // Check move type and calculate damage

            // Non-standard damage move: non-status moves with no power stat
            // handled individually
            if (moveType != Enums.Subtypes.STATUS && move.getPower() == MoveTemplate.NOT_APPLICABLE) {
                damageInfo = processNonStandardDamagingMove(move, user, target);
            }
            // Standard damage moves: non-status moves with defined power stat
            else if (moveType != Enums.Subtypes.STATUS)
                damageInfo = calculateMoveDamage(move, user, target, twoTurnModifier, true, -1);
            // Health restore moves
            else {
                if (move.getHpRestore() > 0)
                    return processHealthRestore(moveTimeLine, move, user);
            }
            //****************************************

            // Applying damage as well as the UI animations connected to it
            if (damageInfo.damage > 0) {

                damage = damageInfo.damage;

                if (damage > target.getHp())
                    damage = target.getHp();

                int oldHp = target.getHp();
                target.setHp(target.getHp() - damage);

                final Timeline damageDealtAnimation, hpAnimation;

                Timeline damageSoundEffectTimeLine = controller.getHitEffectClipPlayback(damageInfo.typeEffect);
                moveTimeLine.add(damageSoundEffectTimeLine);

                damageDealtAnimation = controller.getMoveDamageAnimation(target.getOwner().isPlayer());

                if (target.getOwner().isPlayer()) {
                    hpAnimation = controller.getAllyHpAnimation(oldHp, target.getHp(), target.getMaxHP());
                }
                else {
                    hpAnimation = controller.getEnemyHpAnimation(oldHp, target.getHp(), target.getMaxHP());
                }

                moveTimeLine.add(damageDealtAnimation);
                moveTimeLine.add(hpAnimation);
                moveTimeLine.add(controller.generatePause(1500));
                int displayHits = i+1;
                //System.out.println("Hit " + displayHits + ": " + move.getName() + " dealt " + damage + " damage to " +
                //        target.getBattleNameMiddle() + "!");
                System.out.printf("Hit %d: %s dealt %d damage to %s (%d -> %d)%n", displayHits, move.getName(), damage,
                        target.getBattleNameMiddle(), oldHp, target.getHp());

                if (damageInfo.critical) {
                    final Timeline criticalInfo = controller.getBattleTextAnimation("A critical hit!", true);
                    moveTimeLine.add(criticalInfo);
                    moveTimeLine.add(controller.generatePause(1000));
                    moveTimeLine.add(controller.wipeText(true));
                }

            }
            else if (moveType != Enums.Subtypes.STATUS) {
                Timeline moveFailedMessage = controller.getBattleTextAnimation("But it failed!", true);
                moveTimeLine.add(moveFailedMessage);
                moveTimeLine.add(controller.generatePause(1500));
                return moveTimeLine;
            }
            //***************************************************************

            // Check if attacking Pokemon gets affected by enemy Static ability, this triggers every hit
            if (move.isContactMove() && target.getAbility() == Ability.STATIC && user.getStatus() == Enums.Status.NONE) {
                processStaticCheck(moveTimeLine, user, target);
            }

        }

        // Apply recharge if recharge move
        if (move.isRecharge())
            user.getSubStatuses().add(Enums.SubStatus.RECHARGE);

        // Check if Pokemon in first turn of a multiturn move
        // generates the number of moves that it can use before being confused and
        // assigns appropriate variables
        if (move.isMultiturn() && user.getStateMove() == null && move.isMultiturnConfusion() &&
                user.getState() == Enums.States.NONE) {
            int turns = generator.nextInt(2) + 1;
            user.setStateMove(move);
            user.setStateCounter(turns);
            user.setState(Enums.States.MULTITURN);
        }
        else if (move.isMultiturn() && user.getStateMove() == null && user.getState() == Enums.States.NONE) {
            if (move.getHits() == 1) {
                user.setStateMove(move);
                user.setStateCounter(4);
                user.setState(Enums.States.MULTITURN);
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

        //Applies life restore if move has lifesteal
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

        // Apply spikes to battlefield
        if (move.getSpikeType() != Enums.Spikes.NONE) {
            HashMap<Enums.Spikes, Integer> spikes = user.getOwner().isPlayer() ? enemySpikes : allySpikes;
            if (!spikes.containsKey(move.getSpikeType())) {
                spikes.put(move.getSpikeType(), 1);
                System.out.println("Placing " + move.getSpikeType() + " tier 1");
            }
            else if (spikes.get(move.getSpikeType()) == 1) {
                spikes.put(move.getSpikeType(), 2);
                System.out.println("Placing " + move.getSpikeType() + " tier 2");
            }
            else {
                Timeline failureMessage = controller.getBattleTextAnimation("But it failed!", true);
                moveTimeLine.add(failureMessage);
                moveTimeLine.add(controller.generatePause(1500));
                System.out.println("Placing " + move.getSpikeType() + "failed!");
                return moveTimeLine;
            }
            processSpikesPlaced(moveTimeLine, user, move);
        }
        //********************************************************

        // Checks related to moves that increase or decrease stats
        boolean targetFainted = target.getHp() == 0;
        boolean userFainted = user.getHp() == 0;
        boolean targetProtectedAbility = move.getSubtype() != Enums.Subtypes.STATUS &&
                target.getAbility() == Ability.SHIELD_DUST && !target.getSubStatuses().contains(Enums.SubStatus.GASTRO_ACID);
        boolean secondaryEffectsImmune = targetFainted || targetProtectedAbility;

        if (!move.getStatTypes().isEmpty()) {
            processStatUpApplication(moveTimeLine, move, user, target, userFainted, secondaryEffectsImmune);
            return moveTimeLine;
        }

        //*******************************************************

        // Apply applicable substatus TODO: Unify all substatus applying moves under this method
        if (move.getSubStatus() != Enums.SubStatus.NONE) {
            processSubStatusApplication(moveTimeLine, move, user, target, secondaryEffectsImmune, first);
            return moveTimeLine;
        }
        //*******************************************************

        // Checks related to moves that inflict status effects
        if (move.getStatus() != Enums.Status.NONE) {
            processStatusApplication(moveTimeLine, move, user, target, secondaryEffectsImmune);
            return moveTimeLine;
        }
        //*********************************************************

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
        if (move.isMultiturn() && user.getStateCounter() == 0 && user.getStateMove() != null &&
        move.isMultiturnConfusion() && user.getState() == Enums.States.MULTITURN)
            processMultiturnMoveCompleted(moveTimeLine, user);
        //*********************************************************************

        // If enemy move swaps Pokemon out
        if (!user.getOwner().isPlayer() && move.isSwitchOut() && getEnemyAbleToBattleNum() > 1) {
            int newPokemonIndex = -1;
            for (i=0; i<enemy.getParty().size(); i++) {
                if (enemy.getParty(i).getHp() > 0 && i != currentEnemyPokemon) {
                    newPokemonIndex = i;
                    break;
                }
            }
            switchPokemon(false, newPokemonIndex);

            Timeline enemyPokemonReturn = controller.getPokemonReturnAnimation(false);
            moveTimeLine.add(enemyPokemonReturn);
            moveTimeLine.add(controller.generatePause(1000));

            Timeline enemyNewPokemonInfo = controller.getBattleTextAnimation(String.format("%s %s sends out%n%s!",
                            enemy.getTrainerType().toString(), enemy.getName(), enemy.getParty(currentEnemyPokemon).getName()),
                    true);
            //enemyNewPokemonInfo.setDelay(Duration.seconds(2));
            moveTimeLine.add(enemyNewPokemonInfo);

            Timeline updateStatus = controller.updateStatus(enemy.getParty(currentEnemyPokemon), false);
            moveTimeLine.add(updateStatus);
            moveTimeLine.add(controller.generatePause(1000));

            Timeline pokemonIntroAnimation = controller.getIntroAnimation(enemy.getParty(currentEnemyPokemon),
                    enemy.getParty(currentEnemyPokemon).getHp());
            moveTimeLine.add(pokemonIntroAnimation);
        }

        return moveTimeLine;
    }

    private void processStaticCheck(List<Timeline> moveTimeLine, Pokemon user, Pokemon target) {

        SecureRandom generator = new SecureRandom();
        int staticRandom = generator.nextInt(10);

        if (staticRandom <= 2) {
            Timeline abilityInfo;
            user.setStatus(Enums.Status.PARALYZED);
            boolean isPlayerStaticCheck = target.getOwner().isPlayer();

            if (isPlayerStaticCheck) {
                abilityInfo = controller.getAllyAbilityInfoAnimation(target);
            } else {
                abilityInfo = controller.getEnemyAbilityInfoAnimation(target);
            }

            Timeline abilityMessage = controller.getBattleTextAnimation(String.format(
                    "%s's Static makes %s%nunable to move!", target.getBattleName(),
                    user.getBattleNameMiddle()), true);
            Timeline updateStatus = controller.updateStatus(user, !isPlayerStaticCheck);

            System.out.printf("%s affected by %s's Static, now paralyzed%n",
                    user.getBattleName(), target.getBattleNameMiddle());

            moveTimeLine.add(abilityInfo);
            moveTimeLine.add(abilityMessage);
            moveTimeLine.add(updateStatus);
            moveTimeLine.add(controller.generatePause(1000));
        }
    }

    private void processStatusApplication(List<Timeline> moveTimeLine, Move move, Pokemon user, Pokemon target,
                                          boolean secondaryEffectsImmune) {
        SecureRandom generator = new SecureRandom();
        int prob = Math.round(move.getStatusProb() * 100.0f);
        int rand = generator.nextInt(100) + 1;

        boolean statusImmunity = isStatusImmune(move, target);

        if (prob >= rand && !secondaryEffectsImmune && !statusImmunity) {
            Timeline statusChangeInfo = processStatusChange(move.getStatus(), target);
            //statusChangeInfo.setDelay(Duration.seconds(1));
            moveTimeLine.add(statusChangeInfo);

            Timeline updateStatus = controller.updateStatus(target, target.getOwner().isPlayer());
            if (move.getStatus() == Enums.Status.SLEEPING || move.getStatus() == Enums.Status.FROZEN) {
                checkMultiturnMoveInterruptEffect(moveTimeLine, target);
                processTwoTurnMoveComplete(moveTimeLine, target);
            }
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
        }
    }

    private boolean isStatusImmune(Move move, Pokemon target) {
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

        return firePokemonImmunity || electricPokemonImmunity || poisonPokemonImmunity;
    }

    private void processStatUpApplication(List<Timeline> moveTimeLine, Move move, Pokemon user, Pokemon target,
                                          boolean userFainted, boolean secondaryEffectsImmune) {
        SecureRandom generator = new SecureRandom();
        float prob = move.getStatChangeProb() * 100.0f;
        int rand = generator.nextInt(100) + 1;

        if (move.isSelf() && prob >= rand && !userFainted) {
            processStatChange(moveTimeLine, move, user, true);
            if (!move.getSecondaryStatTypes().isEmpty())
                processStatChange(moveTimeLine, move, user, false);
        }
        else if (prob >= rand && !secondaryEffectsImmune) {
            processStatChange(moveTimeLine, move, target, true);
            if (!move.getSecondaryStatTypes().isEmpty())
                processStatChange(moveTimeLine, move, target, false);
        }
    }

    private void processSubStatusApplication(List<Timeline> moveTimeLine ,Move move, Pokemon user, Pokemon target,
                                             boolean secondaryEffectsImmune, boolean first) {
        SecureRandom generator = new SecureRandom();
        float prob = move.getStatChangeProb() * 100.0f;
        int rand = generator.nextInt(100) + 1;
        Enums.SubStatus moveSubStatus = move.getSubStatus();

        switch (moveSubStatus) {
            case CONFUSED:
                if (prob >= rand && !secondaryEffectsImmune && !target.getSubStatuses().contains(moveSubStatus)) {
                    moveTimeLine.add(applyConfusion(target));
                    moveTimeLine.add(controller.generatePause(1000));
                }
                break;
            case FLINCHED:
                if (prob >= rand && !secondaryEffectsImmune && !target.getSubStatuses().contains(moveSubStatus) && first) {
                    target.getSubStatuses().add(Enums.SubStatus.FLINCHED);
                }
                break;
            case LASER_FOCUS:
                Timeline laserFocusMessage;
                if (!user.getSubStatuses().contains(moveSubStatus)) {
                    user.getSubStatuses().add(Enums.SubStatus.LASER_FOCUS);
                    laserFocusMessage = controller.getBattleTextAnimation(String.format(
                            "%s concentrated intensely!", user.getBattleName()), true);
                    user.setLaserFocusActive(true);
                    System.out.println(user.getBattleName() + " now has Laser Focus status active");
                }
                else
                    laserFocusMessage = controller.getBattleTextAnimation("But it failed!", true);
                moveTimeLine.add(laserFocusMessage);
                moveTimeLine.add(controller.generatePause(1500));
                break;
            case FOCUS_ENERGY:
                Timeline critChangeInfo;
                if(!user.getSubStatuses().contains(moveSubStatus)) {
                    critChangeInfo = controller.getBattleTextAnimation(String.format("%s is getting pumped!", user.getBattleName()),
                            true);
                    user.setCritIncrease(user.getCritIncrease() + 2);
                    user.getSubStatuses().add(moveSubStatus);
                    System.out.println(user.getBattleName() + " crit modifier increased by 2");
                }
                else
                    critChangeInfo = controller.getBattleTextAnimation("But it failed!", true);

                moveTimeLine.add(critChangeInfo);
                moveTimeLine.add(controller.generatePause(1500));
                break;
            case GASTRO_ACID:
                Timeline gastroAcidMessage;
                if (!user.getSubStatuses().contains(moveSubStatus)) {
                    gastroAcidMessage = controller.getBattleTextAnimation(String.format("%s's Ability\nwas supressed!",
                            target.getBattleName()), true);
                    target.getSubStatuses().add(Enums.SubStatus.GASTRO_ACID);
                }
                else
                    gastroAcidMessage = controller.getBattleTextAnimation(String.format("%s's Ability\nis already supressed!",
                            target.getBattleName()), true);

                moveTimeLine.add(gastroAcidMessage);
                moveTimeLine.add(controller.generatePause(1500));
                break;
            default:
                throw new IllegalStateException("Unexpected sub status applied: " + moveSubStatus);
        }
    }

    private MoveDamageInfo processNonStandardDamagingMove(Move move, Pokemon user, Pokemon target) {

        int damage;

        switch (move.getName()) {
            case SUPER_FANG:
                damage = (int)Math.floor(target.getHp() / 2.0);
                if (damage == 0)
                    damage = 1;

                return new MoveDamageInfo(damage, false, 1);

            case ENDEAVOR:
                if (user.getHp() >= target.getHp())
                    return new MoveDamageInfo(0, false, 1);

                damage = target.getHp() - user.getHp();
                return new MoveDamageInfo(damage, false, 1);

            case ELECTRO_BALL:
                double[] speeds;
                double userSpeed, targetSpeed;
                if (user.getOwner().isPlayer()) {
                    speeds = calculateEffectiveSpeeds(user, target);
                    userSpeed = speeds[0];
                    targetSpeed = speeds[1];
                }
                else {
                    speeds = calculateEffectiveSpeeds(target, user);
                    userSpeed = speeds[1];
                    targetSpeed = speeds[0];
                }

                double targetSpeedPercentage = targetSpeed / userSpeed;

                int movePower;
                if (targetSpeedPercentage >= 1)
                    movePower = 40;
                else if (targetSpeedPercentage >= 0.5001)
                    movePower = 60;
                else if (targetSpeedPercentage >= 0.3334)
                    movePower = 80;
                else if (targetSpeedPercentage >= 0.2501)
                    movePower = 120;
                else
                    movePower = 150;

                return calculateMoveDamage(move, user, target, 1, true, movePower);

            default:
                throw new IllegalStateException("Unidentified move matched non standard damaging move criteria: " +
                        move.getName());
        }
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

        System.out.printf("%s drained %d HP from %s (%d -> %d)%n", user.getBattleName(), lifeStolenInteger ,
                target.getBattleNameMiddle(), oldUserHp, user.getHp());
    }

    private void processRecoil(List<Timeline> moveTimeLine, Pokemon user, int damage, Move move) {
        Timeline recoilText = controller.getBattleTextAnimation(String.format(
                "%s took damage%nfrom recoil!",user.getBattleName()),true);

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
        System.out.printf("%s took %d recoil damage (%d -> %d)%n", user.getBattleName(), recoilDamageInt, oldHp,
                user.getHp());
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
        int turns;
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
            System.out.printf("%s is %s!%n", target.getBattleName(), status);
            if (status == Enums.Status.SLEEPING) {
                int sleepTurns = generator.nextInt(3) + 1;
                target.setSleepCounter(sleepTurns);
            }
        }

        return statusChangeInfo;
    }

    private void processStatChange(List<Timeline> moveTimeLine, Move move, Pokemon target, boolean primary) {

        List<Enums.StatType> statTypes;
        if (primary)
            statTypes = move.getStatTypes();
        else
            statTypes = move.getSecondaryStatTypes();

        int change;
        if (primary)
            change = move.getStatChange();
        else
            change = move.getSecondaryStatChange();

        Timeline statChangeSoundPlayback = controller.getStatChangeClipPlayback(change);
        int lastIndex = moveTimeLine.size();
        boolean statChanged = false;

        for (Enums.StatType statType : statTypes) {
            if (statType == Enums.StatType.ACCURACY && target.getAbility() == Ability.KEEN_EYE && change < 0 &&
                    !target.getSubStatuses().contains(Enums.SubStatus.GASTRO_ACID)) {
                Timeline abilityAnimation;
                if (target.getOwner().isPlayer())
                    abilityAnimation = controller.getAllyAbilityInfoAnimation(target);
                else
                    abilityAnimation = controller.getEnemyAbilityInfoAnimation(target);
                moveTimeLine.add(abilityAnimation);

                Timeline abilityInfo = controller.getBattleTextAnimation(String.format(
                        "%s's Keen Eye%nprevents accuracy drops!", target.getBattleName()), true);
                System.out.printf("Accuracy drop prevented by %s's Keen Eye%n", target.getBattleNameMiddle());
                moveTimeLine.add(abilityInfo);
                moveTimeLine.add(controller.generatePause(1500));

                continue;
            }

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

                statChanged = true;

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
            //System.out.println(target.getBattleName() + " stat change to " + statType + ": " + change);
            System.out.printf("%s's stat change to %s: %d (%d -> %d)%n",
                    target.getBattleName(), statType, change, currentStatModifier, statup);
        }

        if (statChangeSoundPlayback != null && statChanged) {
            moveTimeLine.add(lastIndex, statChangeSoundPlayback);
        }
    }

    private MoveDamageInfo calculateMoveDamage(Move move, Pokemon user, Pokemon target, int twoTurnModifier,
                                       boolean canCrit, int overridePower)
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

        if (user.getSubStatuses().contains(Enums.SubStatus.LASER_FOCUS))
            bound = 1;

        int critNum = generator.nextInt(bound);
        boolean isCrit = false;
        float critMod;

        int attackTemp = 0;
        int attackMod = 0;
        int defenseTemp = 0;
        int defenseMod = 0;

        // Check if the executed move is a special or physical move, then grab the corresponding stats
        // Attack for user and defense for target (or Special Attack and Special Defense)
        switch (move.getSubtype()) {
            case PHYSICAL:
                attackTemp = user.getStats(Enums.StatType.ATTACK);
                attackMod = user.getStatModifiers().get(Enums.StatType.ATTACK);
                defenseTemp = target.getStats(Enums.StatType.DEFENSE);
                defenseMod = target.getStatModifiers().get(Enums.StatType.DEFENSE);
                break;
            case SPECIAL:
                attackTemp = user.getStats(Enums.StatType.SPECIAL_ATTACK);
                attackMod = user.getStatModifiers().get(Enums.StatType.SPECIAL_ATTACK);
                defenseTemp = target.getStats(Enums.StatType.SPECIAL_DEFENSE);
                defenseMod = target.getStatModifiers().get(Enums.StatType.SPECIAL_DEFENSE);
                break;
            default:
                throw new IllegalStateException("Move of type " + move.getSubtype() + " is impossible to process");

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

        // Miscellaneous stats calculation (stab, random factor, burn debuff)
        double part1 = ((2.0 * user.getLevel())/5.0) + 2;
        float rand = (generator.nextInt(16) + 85)/100.0f;
        float stab;

        Type firstUserType = user.getType()[0];
        Type secondUserType = user.getType()[1];

        if (move.getType().equals(firstUserType) || move.getType().equals(secondUserType))
            stab = 1.5f;
        else
            stab = 1f;

        float burn = 1;
        if (move.getSubtype() == Enums.Subtypes.PHYSICAL) {
            // Some abilities block burn damage from decreasing physical damage
            boolean userAffectedByBurn = user.getStatus() == Enums.Status.BURNED && user.getAbility() != Ability.GUTS
                    && !user.getSubStatuses().contains(Enums.SubStatus.GASTRO_ACID);
            // Some moves also block burn damage from decreasing physical damage
            boolean ignoresBurn = move.getName() == MoveEnum.CONFUSION_DAMAGE || move.getName() == MoveEnum.FACADE;
            if (userAffectedByBurn && !ignoresBurn)
                burn = 0.5f;
        }

        // Checking how the type of the move is going to affect the damage
        Type firstTargetType = target.getType()[0];
        Type secondTargetType = target.getType()[1];

        // If target is under the effects of Roost, its Flying typing is discarded (or replaced with Normal if monotype)
        if (target.getSubStatuses().contains(Enums.SubStatus.ROOST)) {
            if (firstTargetType.getTypeEnum() == Enums.Types.FLYING)
                firstTargetType = secondTargetType.getTypeEnum() != Enums.Types.NO_TYPE ?
                        Type.getTypeMap(Enums.Types.NO_TYPE) : Type.getTypeMap(Enums.Types.NORMAL);
            else if (secondTargetType.getTypeEnum() == Enums.Types.FLYING)
                secondTargetType = Type.getTypeMap(Enums.Types.NO_TYPE);
        }

        // Calculation of type effect: since a lot of Pokemon are dual type, moves can deal from 0.25x to 4x of its
        // original damage
        float typeEffect = 1;
        for (Enums.Types type : move.getType().getStrongAgainst()){
            if(type.equals(firstTargetType.getTypeEnum()) || type.equals(secondTargetType.getTypeEnum()))
                typeEffect *= 2;
        }
        for (Enums.Types type : move.getType().getWeakAgainst()){
            if(type.equals(firstTargetType.getTypeEnum()) || type.equals(secondTargetType.getTypeEnum()))
                typeEffect *= 0.5f;
        }

        // Final damage calculations
        double modifier = critMod * rand * stab * typeEffect * burn * twoTurnModifier;
        double power;
        if (overridePower == -1)
            power = move.getPower();
        else
            power = overridePower;

        // Check if move effectiveness is affected by current weather conditions
        if (weatherEffect.getKey() != Enums.WeatherEffect.NONE) {
            double weatherMultiplier = calculateWeatherMultiplier(move);
            power *= weatherMultiplier;
        }

        // If under non immobilizing status condition and using Facade, double the power
        if (move.getName() == MoveEnum.FACADE && user.getStatus() != Enums.Status.NONE)
            power *= 2;

        // If under the effect of a multiturn stacking move (multiturn + doesn't cause confusion + hits set to 1)
        // increase power twofold every turn it is used
        boolean nonConfusionMultiturn = move.isMultiturn() && !move.isMultiturnConfusion();
        if (nonConfusionMultiturn && move.getHits() == 1 && user.getStateMove() == move &&
                user.getState() == Enums.States.MULTITURN) {
            int powerMultiplier =  5 - user.getStateCounter();
            power *= powerMultiplier;
        }
        double damageDouble = Math.round(((((part1 * power * ((attack/defense)/50.0)) + 2) * modifier)));
        int damage = (int) damageDouble;

        // Type printed to console
        System.out.println(user.getBattleName() + " used a " + move.getType().toString() + " type move!");

        // Critical hit message
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

    private double calculateWeatherMultiplier(Move move) {
        switch (weatherEffect.getKey()) {
            case RAIN:
                if (move.getType().getTypeEnum() == Enums.Types.WATER)
                    return 1.5;
                if (move.getType().getTypeEnum() == Enums.Types.FIRE || move.getName() == MoveEnum.SOLAR_BEAM)
                    return 0.5;
                return 1;
            default:
                throw new IllegalStateException("Undhandled weather effect: " + weatherEffect.getKey());
        }
    }

    private double processAbilityMultiplier(Move move, Pokemon user) {
        Enums.Types boostedType = Enums.Types.MISSING;
        boolean generalBoost = false;
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
            case GUTS:
                if (user.getStatus() != Enums.Status.NONE)
                    generalBoost = true;
                break;
            default:
                return 1.0;
        }

        double hpThreshold = (double) user.getMaxHP() / 3;
        boolean typeBoost = user.getHp() <= hpThreshold && move.getType().getTypeEnum() == boostedType;

        if (typeBoost || generalBoost)
            return 1.5;
        return 1.0;
    }

    public void switchPokemon(boolean ally, int slot) {

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
        pokemon.setCritIncrease(0);
        pokemon.setStateMove(null);
        pokemon.setStateCounter(0);
        pokemon.setState(Enums.States.NONE);
        pokemon.setConfusionTimer(0);
        pokemon.setTrapped(false);
        pokemon.setTrappedTimer(0);
        pokemon.setLaserFocusActive(false);
    }
}
