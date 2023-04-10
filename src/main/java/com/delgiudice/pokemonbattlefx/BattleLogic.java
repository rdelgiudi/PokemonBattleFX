package com.delgiudice.pokemonbattlefx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.util.Duration;

import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static javafx.application.Platform.exit;

public class BattleLogic {

    private final BattleController controller;

    private Player player;
    private NpcTrainer enemy;

    private int currentAllyPokemon = 0;
    private int currentEnemyPokemon = 0;

    private Move allyTwoTurnMove = null, enemyTwoTurnMove = null;

    private boolean enemySentOut, allySentOut;

    boolean inBattle;

    public BattleLogic(BattleController controller) {
        this.controller = controller;
        inBattle = true;
        setup();
        initBattleLoop();
    }

    private void setup() {
        Pokemon.generatePokemonExamples();

        // For testing purposes only, delete later
        Pokemon allyPokemon = new Pokemon(Pokemon.getPokemonExamples().get("Ivysaur"));
        player = new Player("Red",  allyPokemon);

        player.addPokemon(new Pokemon(PokemonSpecie.getPokemonMap().get("Machop"), 8, Ability.GUTS,
                new Move(MoveTemplate.getMoveMap().get("Tackle")), new Move(MoveTemplate.getMoveMap().get("Growl")),
                new Move(MoveTemplate.getMoveMap().get("Double Kick"))));

        player.addPokemon(new Pokemon(PokemonSpecie.getPokemonMap().get("Squirtle"), 8, Ability.TORRENT,
                new Move(MoveTemplate.getMoveMap().get("Tackle")), new Move(MoveTemplate.getMoveMap().get("Tail Whip")),
                        new Move(MoveTemplate.getMoveMap().get("Water Gun"))));

        Pokemon enemyPokemon = new Pokemon(PokemonSpecie.getPokemonMap().get("Rattata"), 50, Ability.GUTS,
                new Move(MoveTemplate.getMoveMap().get("Scratch")) , new Move(MoveTemplate.getMoveMap().get("Growl")),
                new Move(MoveTemplate.getMoveMap().get("Quick Attack")));

        enemy = new NpcTrainer("Joey", Enums.TrainerTypes.YOUNGSTER ,enemyPokemon);

        enemy.addPokemon(new Pokemon(PokemonSpecie.getPokemonMap().get("Rattata"), 10, Ability.GUTS,
                new Move(MoveTemplate.getMoveMap().get("Scratch")), new Move(MoveTemplate.getMoveMap().get("Growl")),
                new Move(MoveTemplate.getMoveMap().get("Quick Attack"))));

    }

    // function that initiates a battle, adding looping checks here should be avoided
    private void initBattleLoop() {
        boolean allyPokemonSelected = checkIfAllyAbleToBattle(true);
        boolean enemyPokemonSelected = checkIfEnemyAbleToBattle();

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
        enemyPokemonIntro.setDelay(Duration.seconds(1));
        battleTextIntro.setOnFinished(e -> enemyPokemonIntro.play());

        //controller.setEnemyInformation(enemy.getParty(currentEnemyPokemon));
        Timeline enemyInfoAnimation = controller.getEnemyInfoAnimation(enemy.getParty(currentEnemyPokemon),
                enemy.getParty(currentEnemyPokemon).getHp());
        enemyPokemonIntro.setOnFinished(e -> enemyInfoAnimation.play());

        Timeline allyPokemonIntro = controller.getBattleTextAnimation(String.format("Go! %s!",
                player.getParty(currentAllyPokemon).getName()), true);
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
                if (set) {
                    currentAllyPokemon = index;
                    resetStats(pokemon);
                }
                return true;
            }
            index++;
        }
        return false;
    }

    private boolean checkIfEnemyAbleToBattle() {
        int index = 0;

        for (Pokemon pokemon : enemy.getParty()) {
            if (pokemon.getHp() > 0) {
                switchPokemon(false, index);
                return true;
            }
            index++;
        }
        return false;
    }

    private void battleLost() {
        controller.switchToPlayerChoice(false);

        Timeline battleLostMsg1 = controller.getBattleTextAnimation(String.format("%s is out%nof usable Pokemon!",
                player.getName()), true);
        Timeline battleLostMsg2 = controller.getBattleTextAnimation(String.format("%s whited out!", player.getName()),
                true);
        Timeline battleLostMsg3 = controller.getBattleTextAnimation("Quitting...",
                true);
        Timeline paused = controller.generatePause(1000);

        battleLostMsg2.setDelay(Duration.seconds(2));
        battleLostMsg3.setDelay(Duration.seconds(2));
        battleLostMsg1.setOnFinished(e -> battleLostMsg2.play());
        battleLostMsg2.setOnFinished(e -> battleLostMsg3.play());
        battleLostMsg3.setOnFinished(e -> paused.play());
        paused.setOnFinished(e -> exit());
        battleLostMsg1.play();
    }

    private void battleWon() {
        controller.switchToPlayerChoice(false);

        Timeline battleWonMsg1 = controller.getBattleTextAnimation(String.format("%s defeated%n%s %s!", player.getName(),
                enemy.getTrainerType().toString(), enemy.getName()), true);
        Timeline battleWonMsg2 = controller.getBattleTextAnimation("Quitting...", true);
        Timeline paused = controller.generatePause(1000);

        battleWonMsg1.setDelay(Duration.seconds(2));
        battleWonMsg2.setDelay(Duration.seconds(2));
        battleWonMsg1.setOnFinished(e -> battleWonMsg2.play());
        battleWonMsg2.setOnFinished(e -> paused.play());
        paused.setOnFinished(e -> exit());

        battleWonMsg1.play();
    }

    private void battleLoop() {

        if (enemySentOut) {
            enemySentOut = false;
            //TODO: Processing events at the start of the turn
        }
        if (allySentOut) {
            allySentOut = false;
            //TODO: Processing events at the start of the turn
        }

        if (allyTwoTurnMove != null) {
            battleTurn(allyTwoTurnMove);
            return;
        }

        Timeline playerChoiceDialog = controller.getBattleTextAnimation(String.format("What will%n%s do?",
                player.getParty(currentAllyPokemon).getName()), false);
        //playerChoiceDialog.setDelay(Duration.seconds(2));
        controller.switchToPlayerChoice(true);
        playerChoiceDialog.play();

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
                            player.getParty(currentAllyPokemon).getName()), false);
                    playerChoiceDialog2.setDelay(Duration.seconds(1));
                    outOfPPInfo.setOnFinished(event -> playerChoiceDialog2.play());
                    playerChoiceDialog2.setOnFinished(event -> {
                        controller.getMoveGrid().setDisable(false);
                        controller.getMoveGrid().setVisible(false);
                        controller.switchToPlayerChoice(true);
                    });
                    outOfPPInfo.play();
                });
        }

        fightButton.setOnAction(e -> {
            controller.fightButtonPressed(player.getParty(currentAllyPokemon));
        });

        backButton.setOnAction(e -> {
            controller.backButtonPressed();
        });

        pokemonButton.setOnAction( e -> {
            controller.pokemonButtonPressed(player.getParty());
            setPokemonSwapListeners();
        });

    }

    private void setPokemonSwapListeners() {

        int iLimit = Math.min(player.getParty().size(), 2);
        int jLimit = player.getParty().size() > 3 ? 1: 0;
;
        controller.updateSelectPokemonButtons(player.getParty());

        for (int j=0; j <= jLimit; j++) {
            for (int i = 0; i <= iLimit; i++) {
                int partyIndex = i + (3 * j);

                Button button = (Button) controller.getNodeFromGridPane(controller.getPokemonGrid(), i, j);
                Pokemon pokemon = player.getParty(partyIndex);

                if (partyIndex == currentAllyPokemon) {
                    button.setOnAction(e -> {
                        controller.getPokemonGrid().setDisable(true);
                        Timeline alreadyInBattleInfo = controller.getBattleTextAnimation(String.format(
                                "%s is already%nin battle!", pokemon.getName()), false);
                        Timeline resetText = controller.getBattleTextAnimation(String.format("What will%n%s do?",
                                pokemon.getName()), false);
                        resetText.setDelay(Duration.seconds(1));
                        alreadyInBattleInfo.setOnFinished(event -> resetText.play());
                        resetText.setOnFinished(event -> {
                            controller.getPokemonGrid().setDisable(false);
                            controller.getPokemonGrid().setVisible(false);
                            controller.switchToPlayerChoice(true);
                        });
                        alreadyInBattleInfo.play();
                    });
                    continue;
                }

                button.setOnAction(e -> {
                    String currentAllyName = player.getParty(currentAllyPokemon).getName();

                    controller.getPokemonGrid().setVisible(false);
                    controller.switchToPlayerChoice(false);

                    List<Timeline> battleTimeLine = new LinkedList<>();

                    Timeline allyPokemonReturnText = controller.getBattleTextAnimation(String.format(
                            "That's enough %s,%ncome back!", currentAllyName), true);
                    allyPokemonReturnText.setDelay(Duration.seconds(0.1));
                    battleTimeLine.add(allyPokemonReturnText);

                    Timeline allyPokemonReturn = controller.getPokemonFaintedAnimation(true);
                    allyPokemonReturn.setDelay(Duration.seconds(1));
                    battleTimeLine.add(allyPokemonReturn);

                    switchPokemon(true, partyIndex);

                    //controller.setAllyInformation(player.getParty(currentAllyPokemon));
                    Timeline allyPokemonIntro = controller.getBattleTextAnimation(String.format("Go! %s!",
                            player.getParty(currentAllyPokemon).getName()), true);
                    allyPokemonIntro.setDelay(Duration.seconds(1));
                    battleTimeLine.add(allyPokemonIntro);

                    Timeline updateStatus = controller.updateStatus(player.getParty(currentAllyPokemon), true);
                    battleTimeLine.add(updateStatus);

                    Timeline allyInfoAnimation = controller.getAllyInfoAnimation(player.getParty(currentAllyPokemon),
                            player.getParty(currentAllyPokemon).getHp());
                    battleTimeLine.add(allyInfoAnimation);

                    SecureRandom generator = new SecureRandom();
                    int enemyMoveIndex = generator.nextInt(enemy.getParty(currentEnemyPokemon).getMoveList().size());
                    Move enemyMove = enemy.getParty(currentEnemyPokemon).getMoveList(enemyMoveIndex);

                    List<Timeline> enemyMoveList = useMove(enemyMove, enemy.getParty(currentEnemyPokemon),
                            player.getParty(currentAllyPokemon), true);

                    battleTimeLine.addAll(enemyMoveList);

                    checkFainted(battleTimeLine);

                });
            }
        }

        Button button = controller.getPokemonBackButton();
        button.setText("Back");
        button.setDisable(false);

        button.setOnAction(e -> {
            controller.getPokemonGrid().setVisible(false);
            controller.switchToPlayerChoice(true);
        });
    }

    private void battleTurn(Move allyMove) {
        SecureRandom generator = new SecureRandom();

        controller.getMoveGrid().setVisible(false);
        controller.switchToPlayerChoice(false);

        Pokemon allyPokemon = player.getParty(currentAllyPokemon);
        Pokemon enemyPokemon = enemy.getParty(currentEnemyPokemon);

        double playerSpeed = allyPokemon.getStats().get("Speed");
        double enemySpeed = enemyPokemon.getStats().get("Speed");

        playerSpeed = allyPokemon.getStatus() == Enums.Status.PARALYZED ? playerSpeed / 2: playerSpeed;
        enemySpeed = enemyPokemon.getStatus() == Enums.Status.PARALYZED ? enemySpeed / 2: enemySpeed;

        playerSpeed = Math.round(playerSpeed);
        enemySpeed = Math.round(enemySpeed);

        boolean faster = playerSpeed > enemySpeed;
        boolean tied = playerSpeed == enemySpeed;

        List<Timeline> battleTimeLine;
        int enemyMoveIndex = generator.nextInt(enemyPokemon.getMoveList().size());
        Move enemyMove;
        if (enemyTwoTurnMove == null)
            enemyMove = enemyPokemon.getMoveList(enemyMoveIndex);
        else
            enemyMove = enemyTwoTurnMove;

        // Priority move always faster, unless both parties are using them
        if (allyMove.isPriority() && !enemyMove.isPriority()) {
            battleTimeLine = processTurnAllyFaster(allyMove, enemyMove);
        }
        else if (enemyMove.isPriority() && !allyMove.isPriority()) {
            battleTimeLine = processTurnAllySlower(allyMove, enemyMove);
        }
        // If both or neither parties are using priority moves, being faster depends on speed
        else if (faster) {
            battleTimeLine = processTurnAllyFaster(allyMove, enemyMove);
        }
        //On speed tie, the first attacker is randomized
        else if (tied) {
            int flip = generator.nextInt(2);
            if (flip == 1) {
                battleTimeLine = processTurnAllyFaster(allyMove, enemyMove);
            }
            else {
                battleTimeLine = processTurnAllySlower(allyMove, enemyMove);
            }
        }
        //Here is what happens if enemy Pokemon is faster
        else {
            battleTimeLine = processTurnAllySlower(allyMove, enemyMove);
        }

        checkFainted(battleTimeLine);
    }

    private void checkFainted(List<Timeline> battleTimeLine) {
        boolean allyFainted = player.getParty(currentAllyPokemon).getHp() == 0;
        boolean enemyFainted = enemy.getParty(currentEnemyPokemon).getHp() == 0;

        if (allyFainted) {
            processAllyFainted(battleTimeLine);
            //return;
        }
        if (enemyFainted) {
            processEnemyFainted(battleTimeLine);
            //return;
        }

        applyStatusEffect(battleTimeLine);
        //finalChecks(battleTimeLine, enemyFainted);
    }

    // Applies damaging status effect if applicable
    private void applyStatusEffect(List<Timeline> battleTimeLine) {
        boolean allyFainted = player.getParty(currentAllyPokemon).getHp() == 0;
        boolean enemyFainted = enemy.getParty(currentEnemyPokemon).getHp() == 0;
        List<Timeline> allyStatusEffect = null;
        List<Timeline> enemyStatusEffect = null;

        if (!allyFainted)
            allyStatusEffect = processDamageStatusEffects(player.getParty(currentAllyPokemon), true);
        if (!enemyFainted)
            enemyStatusEffect = processDamageStatusEffects(enemy.getParty(currentEnemyPokemon), false);

        allyFainted = player.getParty(currentAllyPokemon).getHp() == 0;
        enemyFainted = enemy.getParty(currentEnemyPokemon).getHp() == 0;

        if (enemyStatusEffect != null) {
            battleTimeLine.addAll(enemyStatusEffect);
            if (enemyFainted) {
                processEnemyFainted(battleTimeLine);
            }
        }

        if (allyStatusEffect != null) {
            battleTimeLine.addAll(allyStatusEffect);
            if (allyFainted) {
                processAllyFainted(battleTimeLine);
            }
        }

        finalChecks(battleTimeLine);
    }

    // Send out new ally Pokemon if available, else game over for the player, even if enemy also out of Pokemon
    // Send out new enemy Pokemon and config timeline list
    private void finalChecks(List<Timeline> battleTimeLine) {

        boolean allyFainted = player.getParty(currentAllyPokemon).getHp() == 0;
        boolean enemyFainted = enemy.getParty(currentEnemyPokemon).getHp() == 0;

        if (allyFainted) {

            for (int i=1; i<battleTimeLine.size(); i++) {
                final int finalI = i;
                battleTimeLine.get(i-1).setOnFinished(e -> battleTimeLine.get(finalI).play());
            }

            if (!checkIfAllyAbleToBattle(false)) {
                battleTimeLine.get(battleTimeLine.size() - 1).setOnFinished(e -> {
                    battleLost();
                });
            }
            else {
                battleTimeLine.get(battleTimeLine.size() - 1).setOnFinished(e -> {
                    selectNewPokemonMenu();
                });
            }
            battleTimeLine.get(0).play();
            return;
        }

        if (enemyFainted) {
            if (!checkIfEnemyAbleToBattle()) {
                for (int i=1; i<battleTimeLine.size(); i++) {
                    final int finalI = i;
                    battleTimeLine.get(i-1).setOnFinished(e -> battleTimeLine.get(finalI).play());
                }

                battleTimeLine.get(battleTimeLine.size() - 1).setOnFinished(e -> {
                    battleWon();
                });

                battleTimeLine.get(0).play();
                return;
            }

            Timeline enemyNewPokemonInfo = controller.getBattleTextAnimation(String.format("%s %s sends out%n%s!",
                            enemy.getTrainerType().toString(), enemy.getName(), enemy.getParty(currentEnemyPokemon).getName()),
                    true);
            enemyNewPokemonInfo.setDelay(Duration.seconds(2));
            battleTimeLine.add(enemyNewPokemonInfo);

            Timeline updateStatus = controller.updateStatus(enemy.getParty(currentEnemyPokemon), false);
            battleTimeLine.add(updateStatus);

            Timeline pokemonIntroAnimation = controller.getEnemyInfoAnimation(enemy.getParty(currentEnemyPokemon),
                    enemy.getParty(currentEnemyPokemon).getHp());
            pokemonIntroAnimation.setDelay(Duration.seconds(1));
            battleTimeLine.add(pokemonIntroAnimation);

        }

        for (int i=1; i<battleTimeLine.size(); i++) {
            final int finalI = i;
            battleTimeLine.get(i-1).setOnFinished(e -> battleTimeLine.get(finalI).play());
        }

        battleTimeLine.get(battleTimeLine.size() - 1).setOnFinished(e -> {
            /*synchronized (battleTimeLine) {
                try {
                    battleTimeLine.wait(2000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }*/
            battleLoop();
        });

        battleTimeLine.get(0).play();
    }

    //Processes damaging status effects
    private List<Timeline> processDamageStatusEffects(Pokemon pokemon, boolean ally) {

        double damageDouble = 0;
        Timeline damageInfoTimeline = null;

        switch (pokemon.getStatus()) {
            case BURNED-> {
                damageDouble = Math.round(pokemon.getMaxHP() / 16.0);
                damageInfoTimeline = controller.getBattleTextAnimation(String.format("%s was hurt by%nburn!",
                        pokemon.getName()), true);
                if (damageDouble == 0)
                    damageDouble = 1;
            }
            case POISONED -> {
                damageDouble = Math.round(pokemon.getMaxHP() / 8.0);
                damageInfoTimeline = controller.getBattleTextAnimation(String.format("%s was hurt by%npoison!",
                        pokemon.getName()), true);
                if (damageDouble == 0)
                    damageDouble = 1;
            }
            case BADLY_POISONED -> {
                damageDouble = Math.round(pokemon.getMaxHP() * pokemon.getPoisonCounter() / 16.0);
                damageInfoTimeline = controller.getBattleTextAnimation(String.format("%s was hurt by%npoison!",
                        pokemon.getName()), true);
                pokemon.setPoisonCounter(pokemon.getPoisonCounter() + 1);
                if (damageDouble == 0)
                    damageDouble = 1;
            }
        }

        if (damageDouble > 0) {
            int damage = (int) damageDouble;
            int oldHp = pokemon.getHp();

            if (damage > pokemon.getHp())
                damage = pokemon.getHp();

            pokemon.setHp(pokemon.getHp() - damage);

            System.out.printf("%s took %d damage from %s%n", pokemon.getName(), damage, pokemon.getStatus());

            Timeline damageTimeline;
            if (ally)
                damageTimeline = controller.getAllyHpAnimation(oldHp, pokemon.getHp(), pokemon.getMaxHP());
            else
                damageTimeline = controller.getEnemyHpAnimation(oldHp, pokemon.getHp(), pokemon.getMaxHP());

            List<Timeline> timelineList = new LinkedList<>();

            if (damageInfoTimeline != null) {
                damageInfoTimeline.setDelay(Duration.seconds(2));
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
        enemyPokemonFainted.setDelay(Duration.seconds(2));

        battleTimeLine.add(enemyPokemonFainted);

        Timeline enemyPokemonFaintedMessage = controller.getBattleTextAnimation(String.format("%s fainted!",
                enemy.getParty(currentEnemyPokemon).getName()), true);
        enemyPokemonFaintedMessage.setDelay(Duration.seconds(2));

        battleTimeLine.add(enemyPokemonFaintedMessage);

        //finalChecks(battleTimeLine, enemyFainted);
    }

    // Processes fainting ally
    private void processAllyFainted(List<Timeline> battleTimeLine) {
        Timeline allyPokemonFainted = controller.getPokemonFaintedAnimation(true);
        allyPokemonFainted.setDelay(Duration.seconds(2));

        battleTimeLine.add(allyPokemonFainted);

        Timeline allyPokemonFaintedMessage = controller.getBattleTextAnimation(String.format("%s fainted!",
                player.getParty(currentAllyPokemon).getName()), true);
        allyPokemonFaintedMessage.setDelay(Duration.seconds(2));

        battleTimeLine.add(allyPokemonFaintedMessage);


        //battleTimeLine.get(0).play();
        //finalChecks(battleTimeLine, enemyFainted);

    }

    // Configures Pokemon select menu
    private void selectNewPokemonMenu() {
        controller.getPokemonGrid().setVisible(true);

        int iLimit = Math.min(player.getParty().size(), 2);
        int jLimit = player.getParty().size() > 3 ? 1: 0;

        controller.updateSelectPokemonButtons(player.getParty());

        for (int j=0; j <= jLimit; j++) {
            for (int i = 0; i <= iLimit; i++) {
                int partyIndex = i + (3 * j);

                Button button = (Button) controller.getNodeFromGridPane(controller.getPokemonGrid(), i, j);

                button.setOnAction(e -> {
                    switchPokemon(true, partyIndex);

                    controller.getPokemonGrid().setVisible(false);

                    List<Timeline> battleTimeLine = new LinkedList<>();

                    Timeline allyPokemonIntro = controller.getBattleTextAnimation(String.format("Go! %s!",
                            player.getParty(currentAllyPokemon).getName()), true);
                    allyPokemonIntro.setDelay(Duration.seconds(1));

                    battleTimeLine.add(allyPokemonIntro);

                    Timeline updateStatus = controller.updateStatus(player.getParty(currentAllyPokemon), true);
                    battleTimeLine.add(updateStatus);

                    //controller.setAllyInformation(player.getParty(currentAllyPokemon));
                    Timeline allyInfoAnimation = controller.getAllyInfoAnimation(player.getParty(currentAllyPokemon),
                            player.getParty(currentAllyPokemon).getHp());

                    battleTimeLine.add(allyInfoAnimation);

                    finalChecks(battleTimeLine);

                });


            }
        }

        Button button = controller.getPokemonBackButton();
        button.setText("Back");
        button.setDisable(true);
    }

    // Events when ally Pokemon is faster
    private List<Timeline> processTurnAllyFaster(Move allyMove, Move enemyMove) {
        //Random generator = new Random();

        List<Timeline> allyMoveTimeLine = useMove(allyMove, player.getParty(currentAllyPokemon),
                enemy.getParty(currentEnemyPokemon), false);
        if (enemy.getParty(currentEnemyPokemon).getHp() == 0) {
            return allyMoveTimeLine;
        }
        //int enemyMove = generator.nextInt(enemy.getParty(currentEnemyPokemon).getMoveList().size());
        List<Timeline> enemyMoveTimeLine = useMove(enemyMove,
                enemy.getParty(currentEnemyPokemon), player.getParty(currentAllyPokemon), true);

        allyMoveTimeLine.addAll(enemyMoveTimeLine);

        return allyMoveTimeLine;
    }

    // Events when ally Pokemon is slower
    private List<Timeline> processTurnAllySlower(Move allyMove, Move enemyMove) {
        //Random generator = new Random();

        //int enemyMove = generator.nextInt(enemy.getParty(currentEnemyPokemon).getMoveList().size());
        List<Timeline> enemyMoveTimeLine = useMove(enemyMove, enemy.getParty(currentEnemyPokemon),
                player.getParty(currentAllyPokemon), true);

        if (player.getParty(currentAllyPokemon).getHp() == 0) {
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

    private List<Timeline> processUserParalyzed(Pokemon user, List<Timeline> moveTimeLine) {
        Timeline paralysisInfo = controller.getBattleTextAnimation(String.format("%s is%nfully paralyzed!",
                user.getName()), true);
        moveTimeLine.add(paralysisInfo);
        moveTimeLine.add(controller.generatePause(2000));
        System.out.println(user.getName() + "is fully paralyzed!");
        return moveTimeLine;
    }

    private List<Timeline> processUserAsleep(Pokemon user, List<Timeline> moveTimeLine) {
        Timeline sleepInfo = controller.getBattleTextAnimation(String.format("%s is%nfast asleep!", user.getName()),
                true);
        user.setSleepCounter(user.getSleepCounter() - 1);
        moveTimeLine.add(sleepInfo);
        moveTimeLine.add(controller.generatePause(2000));
        System.out.printf("%s is asleep!%n", user.getName());
        return moveTimeLine;
    }

    private void processUserWakeUp(Pokemon user, List<Timeline> moveTimeLine, boolean allyTarget) {
        Timeline sleepInfo = controller.getBattleTextAnimation(String.format("%s woke up!", user.getName()),
                true);
        user.setStatus(Enums.Status.NONE);
        Timeline statusChange = controller.updateStatus(user, !allyTarget);
        statusChange.setDelay(Duration.seconds(1));

        moveTimeLine.add(sleepInfo);
        moveTimeLine.add(statusChange);
        moveTimeLine.add(controller.generatePause(1000));
        System.out.printf("%s woke up!%n", user.getName());
    }

    private List<Timeline> processUserFrozen(Pokemon user, List<Timeline> moveTimeLine) {
        Timeline frozenInfo = controller.getBattleTextAnimation(String.format("%s is frozen solid!",
                user.getName()), true);
        moveTimeLine.add(frozenInfo);
        moveTimeLine.add(controller.generatePause(2000));
        return moveTimeLine;
    }

    private void processUserThawOut(Pokemon user, List<Timeline> moveTimeLine, boolean allyTarget) {
        Timeline frozenInfo = controller.getBattleTextAnimation(String.format("%s thawed out!", user.getName()),
                true);
        moveTimeLine.add(frozenInfo);

        user.setStatus(Enums.Status.NONE);
        Timeline updateStatus = controller.updateStatus(user, !allyTarget);
        //updateStatus.setDelay(Duration.seconds(1));
        moveTimeLine.add(updateStatus);
        moveTimeLine.add(controller.generatePause(2000));
    }

    private List<Timeline> processMoveMissed(Pokemon user, List<Timeline> moveTimeLine, boolean allyTarget) {
        System.out.println("Move missed!");
        Timeline moveMissedDialogue = controller.getBattleTextAnimation(String.format("%s's%nattack missed!",
                user.getName()), true);
        moveMissedDialogue.setDelay(Duration.seconds(2));

        moveTimeLine.add(moveMissedDialogue);
        processTwoTurnMoveComplete(moveTimeLine, allyTarget);
        moveTimeLine.add(controller.generatePause(2000));

        return moveTimeLine;
    }

    private void processTwoTurnMoveComplete(List<Timeline> moveTimeLine, boolean allyTarget) {
        if (allyTarget) {
            enemyTwoTurnMove = null;
            Timeline revealEnemy = new Timeline(new KeyFrame(Duration.millis(1),
                    e -> controller.getEnemyPokemonSprite().setVisible(true)));
            moveTimeLine.add(revealEnemy);
        }
        else {
            allyTwoTurnMove = null;
            Timeline revealAlly = new Timeline(new KeyFrame(Duration.millis(1),
                    e -> controller.getAllyPokemonSprite().setVisible(true)));
            moveTimeLine.add(revealAlly);
        }

    }

    private Timeline getTwoTurnMoveInfo(Move move, Pokemon user) {
        Timeline timeline = null;
        switch(move.getName()) {
            case "Dig" -> timeline = controller.getBattleTextAnimation(String.format(
                    "%s dug underground!", user.getName()), true);
            case "Solar Beam" -> timeline = controller.getBattleTextAnimation(String.format(
                    "%s is taking in sunlight!", user.getName()), true);
            default -> throw new IllegalStateException("Unidentified twoturn move: "
            + move.getName());
        }
        return timeline;
    }

    private boolean checkTwoTurnMiss(Move move, boolean allyTarget) {
        Move twoturnmove = allyTarget ? allyTwoTurnMove : enemyTwoTurnMove;
        if (twoturnmove == null || twoturnmove.isCharging())
            return false;

        if (Objects.equals(twoturnmove.getName(), "Dig") &&
                Objects.equals(move.getName(), "Earthquake")) {
            return false;
        }

        return true;
    }

    // Processes using a move, as well as status effects that might prevent from using it
    private List<Timeline> useMove(Move move, Pokemon user, Pokemon target, boolean allyTarget) {

        List<Timeline> moveTimeLine = new LinkedList<>();
        SecureRandom generator = new SecureRandom();

        if (user.getStatus() == Enums.Status.PARALYZED) {
            int rand = generator.nextInt(4);
            if (rand == 0) {
                return processUserParalyzed(user, moveTimeLine);
            }
        }
        if (user.getStatus() == Enums.Status.SLEEPING && user.getSleepCounter() > 0) {
            return processUserAsleep(user, moveTimeLine);
        }
        else if(user.getStatus() == Enums.Status.SLEEPING && user.getSleepCounter() == 0) {
            processUserWakeUp(user, moveTimeLine, allyTarget);
        }

        if (user.getStatus() == Enums.Status.FROZEN) {
            int rand = generator.nextInt(5);
            boolean damagingFireMove = move.getType().getTypeEnum() == Enums.Types.FIRE &&
                    move.getSubtype() != Enums.Subtypes.STATUS;
            if (rand == 0 || damagingFireMove) {
                processUserThawOut(user, moveTimeLine, allyTarget);
            }
            else {
                return processUserFrozen(user, moveTimeLine);
            }
        }

        if (move.isTwoturn() && allyTarget && enemyTwoTurnMove == null) {
            Timeline enemyTwoTurnInfo = getTwoTurnMoveInfo(move, user);
            moveTimeLine.add(enemyTwoTurnInfo);
            enemyTwoTurnMove = move;
            if (!move.isCharging()) {
                Timeline enemyTwoTurnStart = new Timeline(new KeyFrame(Duration.millis(1),
                        e -> controller.getEnemyPokemonSprite().setVisible(false)));
                moveTimeLine.add(enemyTwoTurnStart);
            }
            moveTimeLine.add(controller.generatePause(1000));
            return moveTimeLine;
        }
        if (move.isTwoturn() && !allyTarget && allyTwoTurnMove == null) {
            Timeline allyTwoTurnInfo = getTwoTurnMoveInfo(move, user);
            moveTimeLine.add(allyTwoTurnInfo);
            allyTwoTurnMove = move;
            if (!move.isCharging()) {
                Timeline allyTwoTurnStart = new Timeline(new KeyFrame(Duration.millis(1),
                        e -> controller.getAllyPokemonSprite().setVisible(false)));
                moveTimeLine.add(allyTwoTurnStart);
            }
            moveTimeLine.add(controller.generatePause(1000));
            return moveTimeLine;
        }

        System.out.println(user.getName() + " used " + move.getName());
        Timeline moveUsedDialog = controller.getBattleTextAnimation(user.getName() + " used " + move.getName() + "!",
                true);

        moveTimeLine.add(moveUsedDialog);

        move.setPp(move.getPp() - 1);

        int moveAccuracy = move.getAccuracy();

        if (moveAccuracy != 0) {
            int statAccuracy = user.getStatModifiers().get("Accuracy") - target.getStatModifiers().get("Evasiveness");
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

            float hit = moveAccuracy * accuracyModifier;
            int r = generator.nextInt(100) + 1;
            if (r > hit || checkTwoTurnMiss(move, allyTarget)) {
                return processMoveMissed(user, moveTimeLine, allyTarget);
            }
        }

        Enums.Subtypes moveType = move.getSubtype();
        MoveDamageInfo damageInfo = new MoveDamageInfo(0, false, -999);
        int i;
        int damage = 0;

        for (i=0; i<move.getHits(); i++) {
            if (target.getHp() == 0)
                break;

            switch (moveType) {
                case PHYSICAL -> damageInfo = calculateMoveDamage(0, move, user, target);
                case SPECIAL -> damageInfo = calculateMoveDamage(1, move, user, target);
                case STATUS -> {
                    if (move.getHpRestore() > 0 && user.getHp() != user.getMaxHP()) {
                        double restoredHealth = user.getMaxHP() * move.getHpRestore();
                        double healthChange = restoredHealth + user.getHp();
                        if (healthChange > user.getMaxHP())
                            healthChange = user.getMaxHP();
                        int oldHp = user.getHp();
                        int healthChangeInt = (int) Math.round(healthChange);
                        user.setHp(healthChangeInt);
                        Timeline userHealthChange;
                        if (allyTarget)
                            userHealthChange = controller.getEnemyHpAnimation(oldHp, user.getHp(), user.getMaxHP());

                        else
                            userHealthChange = controller.getAllyHpAnimation(oldHp, user.getHp(), user.getMaxHP());

                        userHealthChange.setDelay(Duration.seconds(1));
                        moveTimeLine.add(userHealthChange);

                        Timeline userHealthInfo = controller.getBattleTextAnimation(String.format(
                                "%s restored health!", user.getName()), true);
                        moveTimeLine.add(userHealthInfo);

                        moveTimeLine.add(controller.generatePause(2000));
                        return moveTimeLine;

                    } else if (move.getHpRestore() > 0 && user.getHp() == user.getMaxHP()) {
                        Timeline userHealthInfo = controller.getBattleTextAnimation(String.format(
                                "%s's health%nis already full!", user.getName()), true);
                        moveTimeLine.add(userHealthInfo);

                        moveTimeLine.add(controller.generatePause(2000));
                        return moveTimeLine;
                    }
                }
                default -> throw new IllegalStateException("Unexpected value: " + moveType);
            }

            if (damageInfo.typeEffect() == 0) {
                final Timeline effectInfo = controller.getBattleTextAnimation(String.format("It doesn't affect %s...",
                                target.getName()), true);
                effectInfo.setDelay(Duration.seconds(2));

                moveTimeLine.add(effectInfo);
                return moveTimeLine;
            }

            else if (damageInfo.damage() > 0) {

                damage = damageInfo.damage();

                //List<Timeline> hitTimeline = new LinkedList<>();

                if (damage > target.getHp())
                    damage = target.getHp();

                int oldHp = target.getHp();
                target.setHp(target.getHp() - damage);

                final Timeline damageDealtAnimation;

                if (allyTarget)
                    damageDealtAnimation = controller.getAllyHpAnimation(oldHp, target.getHp(), target.getMaxHP());
                else
                    damageDealtAnimation = controller.getEnemyHpAnimation(oldHp, target.getHp(), target.getMaxHP());

                damageDealtAnimation.setDelay(Duration.seconds(2));

                moveTimeLine.add(damageDealtAnimation);

                System.out.println(move.getName() + " dealt " + damage + " damage to " + target.getName() + "!");

                if (damageInfo.critical()) {
                    final Timeline criticalInfo = controller.getBattleTextAnimation("A critical hit!", true);
                    criticalInfo.setDelay(Duration.seconds(2));

                    moveTimeLine.add(criticalInfo);
                }

            }

        }

        if (move.isTwoturn()) {
            processTwoTurnMoveComplete(moveTimeLine, allyTarget);
        }

        if (damageInfo.typeEffect() >= 2 && moveType != Enums.Subtypes.STATUS) {
            final Timeline effectInfo = controller.getBattleTextAnimation("It's super effective!", true);
            effectInfo.setDelay(Duration.seconds(2));

            moveTimeLine.add(effectInfo);
        }
        else if (damageInfo.typeEffect() < 1 && damageInfo.typeEffect() > 0 && moveType != Enums.Subtypes.STATUS) {
            final Timeline effectInfo = controller.getBattleTextAnimation("It's not very effective...", true);
            effectInfo.setDelay(Duration.seconds(2));

            moveTimeLine.add(effectInfo);
        }

        if (move.getHits() > 1 && damageInfo.typeEffect() != 0) {
            Timeline hitsInformation = controller.getBattleTextAnimation(String.format("It hit %d time(s)!",
                    i), true);
            hitsInformation.setDelay(Duration.seconds(2));

            moveTimeLine.add(hitsInformation);
        }

        float recoil = move.getTemplate().getRecoil();

        if (recoil > 0 && damage != 0 && move.getHits() == 1) {
            Timeline recoilText = controller.getBattleTextAnimation(String.format(
                    "%s took damage%nfrom recoil!",user.getName()),true);

            recoilText.setDelay(Duration.seconds(1));
            moveTimeLine.add(recoilText);

            double recoilDamage = damage * recoil;
            int recoilDamageInt = (int) Math.round(recoilDamage);
            if (recoilDamageInt > user.getHp())
                recoilDamageInt = user.getHp();

            int oldHp = user.getHp();
            user.setHp(user.getHp() - recoilDamageInt);

            Timeline recoilDealtAnimation;
            if(!allyTarget)
                recoilDealtAnimation = controller.getAllyHpAnimation(oldHp, user.getHp(), user.getMaxHP());
            else
                recoilDealtAnimation = controller.getEnemyHpAnimation(oldHp, user.getHp(), user.getMaxHP());

            moveTimeLine.add(recoilDealtAnimation);
        }

        boolean targetFainted = target.getHp() == 0;
        boolean userFainted = user.getHp() == 0;
        boolean targetProtected = move.getSubtype() != Enums.Subtypes.STATUS &&
                target.getAbility() == Ability.SHIELD_DUST;
        final Timeline statChangeInfo;

        float prob = move.getStatUpProb() * 100;
        int rand = generator.nextInt(100) + 1;

        if (move.getStatType() != null && move.isSelf() && prob >= rand && !userFainted) {
            statChangeInfo = processStatChange(move, user);
            moveTimeLine.add(statChangeInfo);
        }
        else if (move.getStatType() != null && prob >= rand && !targetFainted && !targetProtected) {
            statChangeInfo = processStatChange(move, target);
            moveTimeLine.add(statChangeInfo);
        }

        if (move.getCritIncrease() > 0) {
            Timeline critChangeInfo;
            if(!user.isUnderFocusEnergy()) {
                critChangeInfo = controller.getBattleTextAnimation(String.format("%s is getting pumped!", user.getName()),
                        true);
                user.setCritIncrease(user.getCritIncrease() + 2);
                user.setUnderFocusEnergy(true);
            }
            else
                critChangeInfo = controller.getBattleTextAnimation("But it failed!", true);

            moveTimeLine.add(critChangeInfo);
        }

        prob = move.getStatusProb() * 100;
        rand = generator.nextInt(100) + 1;
        if (move.getStatus() != Enums.Status.NONE && prob >= rand && !targetFainted && !targetProtected) {
            Timeline statusChangeInfo = processStatusChange(move, target);
            statusChangeInfo.setDelay(Duration.seconds(1));
            moveTimeLine.add(statusChangeInfo);

            Timeline updateStatus = controller.updateStatus(target, allyTarget);
            if (move.getStatus() == Enums.Status.SLEEPING || move.getStatus() == Enums.Status.FROZEN)
                processTwoTurnMoveComplete(moveTimeLine, allyTarget);
            statusChangeInfo.setDelay(Duration.seconds(1));
            moveTimeLine.add(updateStatus);
        }

        moveTimeLine.add(controller.generatePause(2000));
        return moveTimeLine;
    }

    Timeline processStatusChange(Move move, Pokemon target) {
        final Timeline statusChangeInfo;
        SecureRandom generator = new SecureRandom();

        if (target.getStatus() != Enums.Status.NONE) {
            statusChangeInfo = controller.getBattleTextAnimation(String.format("%s is already%n%s!",
                    target.getName(), target.getStatus().toString()), true);
            System.out.printf("%s is already %s!%n", target.getName(), target.getStatus().toString());
        }
        else {
            target.setStatus(move.getStatus());
            statusChangeInfo = controller.getBattleTextAnimation(String.format("%s is%n%s!",
                    target.getName(), move.getStatus().toString()), true);
            System.out.printf("%s is %s!%n", target.getName(), move.getStatus().toString());
            if (move.getStatus() == Enums.Status.SLEEPING) {
                int sleepTurns = generator.nextInt(3) + 1;
                target.setSleepCounter(sleepTurns);
            }
        }

        return statusChangeInfo;
    }

    Timeline processStatChange(Move move, Pokemon target) {
        final Timeline statChangeInfo;

        int change = move.getStatUp();
        int currentStatModifier = target.getStatModifiers().get(move.getStatType().toString());
        int statup = currentStatModifier + change;

        if (currentStatModifier == 6 && change > 0) {
            statChangeInfo = controller.getBattleTextAnimation(String.format("%s's%n%s can't go any higher!",
                    target.getName(), move.getStatType().toString()), true);
            change = 0;
        }
        else if (currentStatModifier == -6 && change < 0) {
            statChangeInfo = controller.getBattleTextAnimation(String.format("%s's%n%s can't go any lower!",
                    target.getName(), move.getStatType().toString()), true);
            change = 0;
        }
        else {
            if (statup <= 6 && statup >= -6)
                target.getStatModifiers().put(move.getStatType().toString(), statup);
            else if (statup > 6) {
                statup = 6;
                change = statup - currentStatModifier;
                target.getStatModifiers().put(move.getStatType().toString(), statup);
            }
            else {
                statup = -6;
                change = statup - currentStatModifier;
                target.getStatModifiers().put(move.getStatType().toString(), statup);
            }
            switch (change) {
                case 1 -> statChangeInfo = controller.getBattleTextAnimation(String.format("%s's%n%s rose!",
                        target.getName(), move.getStatType().toString()), true);
                case 2 -> statChangeInfo = controller.getBattleTextAnimation(String.format("%s's%n%s rose sharply!",
                        target.getName(), move.getStatType().toString()), true);
                case 3 -> statChangeInfo = controller.getBattleTextAnimation(String.format("%s's%n%s rose drastically!",
                        target.getName(), move.getStatType().toString()), true);
                case -1 -> statChangeInfo = controller.getBattleTextAnimation(String.format("%s's%n%s fell!",
                        target.getName(), move.getStatType().toString()), true);
                case -2 -> statChangeInfo = controller.getBattleTextAnimation(String.format("%s's%n%s harshly fell!",
                        target.getName(), move.getStatType().toString()), true);
                case -3 -> statChangeInfo = controller.getBattleTextAnimation(String.format("%s's%n%s severely fell!",
                        target.getName(), move.getStatType().toString()), true);
                default -> throw new IllegalStateException(
                        "Wrong change value, should be between -3 and 3 (excluding 0), is: " + change);
            }
        }

        System.out.println(target.getName() + " stat change to " + move.getStatType().toString() + ": " + change);

        statChangeInfo.setDelay(Duration.seconds(2));

        return statChangeInfo;
    }

    MoveDamageInfo calculateMoveDamage(int movetype, Move move, Pokemon user, Pokemon target)
    {
        SecureRandom generator = new SecureRandom();
        int bound;
        int critChanceTempIncrease = move.getTemplate().getCritTemporaryIncrease();
        int critChanceIncrease = user.getCritIncrease();
        int totalCritChanceIncrease = critChanceIncrease + critChanceTempIncrease;

        switch (totalCritChanceIncrease) {
            case 0 -> bound = 16;
            case 1 -> bound = 8;
            case 2 -> bound = 2;
            default -> bound = 1;
        }

        int critNum = generator.nextInt(bound);
        boolean isCrit = false;
        float critMod;

        int attackTemp = 0;
        int attackMod = 0;
        int defenseTemp = 0;
        int defenseMod = 0;

        //First we check if the executed move is a special or physical move, then we grab the corresponding stats
        //Attack for user and defense for target
        switch (movetype) {
            case 0 -> {
                attackTemp = user.getStats("Attack");
                attackMod = user.getStatModifiers().get("Attack");
                defenseTemp = target.getStats("Defense");
                defenseMod = target.getStatModifiers().get("Defense");
            }
            case 1 -> {
                attackTemp = user.getStats("Special Attack");
                attackMod = user.getStatModifiers().get("Special Attack");
                defenseTemp = target.getStats("Special Defense");
                defenseMod = target.getStatModifiers().get("Special Defense");
            }
        }
        //Final effective attack and defense stat
        double attack;
        double defense;
        //We check if the hit is going to be critical (critical hits ignore negative stat changes and only leave positive ones)
        if (critNum == 0)
            isCrit = true;
        if (isCrit) {
            if(attackMod >= 0)
                attack = (int)Math.round((double) attackTemp * (2 + attackMod)/2);
            else
                attack = attackTemp;
            if(defenseMod <= 0) {
                double defense_multiplier = 2.0 / (2 - defenseMod);
                defense = (int) Math.round(defenseTemp * defense_multiplier);
            }
            else
                defense = defenseTemp;
            critMod = 1.5f;
        }
        else  {
            if(attackMod >= 0) {
                attack = (int) Math.round((double) attackTemp * (2 + attackMod) / 2);
            }
            else {
                double attack_multiplier = 2.0 / (2 - attackMod);
                attack = (int) Math.round(attackTemp * attack_multiplier);
            }

            if(defenseMod >= 0) {
                defense = (int) Math.round((double) defenseTemp * (2 + defenseMod) / 2);
            }
            else {
                double defense_multiplier = 2.0 / (2 - defenseMod);
                defense = (int) Math.round(defenseTemp * defense_multiplier);
            }
            critMod = 1;
        }
        double abilityMultiplier = processAbilityMultiplier(move, user);
        attack = attack * abilityMultiplier;
        //Miscellaneous stats calculation (stab, random factor, burn debuff)
        double part1 = ((2.0 * user.getLevel())/5.0) + 2;
        float rand = (generator.nextInt(15) + 85)/100.0f;
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
        double modifier = critMod * rand * stab * typeEffect * burn;
        double damageDouble = Math.round(((((part1 * move.getPower() * ((attack/defense)/50.0)) + 2) * modifier)));
        int damage = (int) damageDouble;

        System.out.println(user.getName() + " used a " + move.getType().toString() + " type move!");

        //Critical hit message
        if(isCrit && typeEffect != 0) {
            System.out.println("A critical hit!");
        }

        //Type effect message
        if(typeEffect == 0)
            System.out.println("It doesn't affect " + target.getName() + "...");
        else if(typeEffect > 1)
            System.out.println("It's super effective!");
        else if(typeEffect < 1)
            System.out.println("It's not very effective...");

        return new MoveDamageInfo(damage, isCrit, typeEffect);
    }

    private double processAbilityMultiplier(Move move, Pokemon user) {
        Enums.Types boostedType;
        switch (user.getAbility()) {
            case OVERGROW -> boostedType = Enums.Types.GRASS;
            case BLAZE -> boostedType = Enums.Types.FIRE;
            case TORRENT -> boostedType = Enums.Types.WATER;
            default -> {
                return 1.0;
            }
        }

        double hpThreshold = (double) user.getMaxHP() / 3;
        if (user.getHp() > hpThreshold)
            return 1.0;

        if (move.getType().getTypeEnum() != boostedType)
            return 1.0;

        return 1.5;
    }

    private void switchPokemon(boolean ally, int slot) {
        if (ally) {
            currentAllyPokemon = slot;
            allySentOut = true;
            resetStats(player.getParty(currentAllyPokemon));
            allyTwoTurnMove = null;
        }
        else {
            currentEnemyPokemon = slot;
            enemySentOut = true;
            resetStats(enemy.getParty(currentEnemyPokemon));
            enemyTwoTurnMove = null;
        }
    }

    private void resetStats(Pokemon pokemon)
    {
        pokemon.getStatModifiers().clear();
        for(Enums.StatType type: Enums.StatType.values()) {
            pokemon.getStatModifiers().put(type.toString(), 0);
        }

        pokemon.setPoisonCounter(1);
        pokemon.getSubStatuses().clear();
        pokemon.setUnderFocusEnergy(false);
        pokemon.setCritIncrease(0);
    }
}