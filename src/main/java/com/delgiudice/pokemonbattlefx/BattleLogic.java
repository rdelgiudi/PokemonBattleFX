package com.delgiudice.pokemonbattlefx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static javafx.application.Platform.exit;

public class BattleLogic {

    private final BattleController controller;

    private Player player;
    private NpcTrainer enemy;

    private int currentAllyPokemon;
    private int currentEnemyPokemon;

    boolean inBattle;

    public BattleLogic(BattleController controller) {
        this.controller = controller;
        inBattle = true;
        setup();
        initBattleLoop();
    }

    private void setup() {
        PokemonSpecie.setPokemonList();
        Move.setMoveList();

        // For testing purposes only, delete later
        Pokemon allyPokemon = new Pokemon(PokemonSpecie.getPokemonList().get("Charmander"), 10,
                Move.getMoveList().get("Scratch"), Move.getMoveList().get("Growl"), Move.getMoveList().get("Ember"));
        player = new Player("Red",  allyPokemon);

        player.addPokemon(new Pokemon(PokemonSpecie.getPokemonList().get("Squirtle"), 10, Move.getMoveList().get("Tackle"),
                Move.getMoveList().get("Tail Whip"), Move.getMoveList().get("Water Gun")));

        Pokemon enemyPokemon = new Pokemon(PokemonSpecie.getPokemonList().get("Rattata"), 6, Move.getMoveList().get("Tackle"),
                Move.getMoveList().get("Growl"));

        enemy = new NpcTrainer("Joey", Enums.TrainerTypes.YOUNGSTER ,enemyPokemon);

        enemy.addPokemon(new Pokemon(PokemonSpecie.getPokemonList().get("Charmander"), 8, Move.getMoveList().get("Scratch"),
                Move.getMoveList().get("Growl"), Move.getMoveList().get("Ember")));
    }

    private void initBattleLoop() {
        boolean allyPokemonSelected = checkIfAllyAbleToBattle(true);
        boolean enemyPokemonSelected = checkIfEnemyAbleToBattle();

        if (!allyPokemonSelected) {
            battleLost();
        }

        if (!enemyPokemonSelected) {
            battleWon();
        }

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
                currentEnemyPokemon = index;
                resetStats(pokemon);
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

        battleLostMsg2.setDelay(Duration.seconds(3));
        battleLostMsg3.setDelay(Duration.seconds(3));
        battleLostMsg1.setOnFinished(e -> battleLostMsg2.play());
        battleLostMsg2.setOnFinished(e -> battleLostMsg3.play());
        battleLostMsg3.setOnFinished(e -> exit());
        battleLostMsg1.play();
    }

    private void battleWon() {
        controller.switchToPlayerChoice(false);

        Timeline battleWonMsg1 = controller.getBattleTextAnimation(String.format("%s defeated%n%s %s!", player.getName(),
                enemy.getTrainerType().toString(), enemy.getName()), true);
        Timeline battleWonMsg2 = controller.getBattleTextAnimation("Quitting...", true);

        battleWonMsg1.setDelay(Duration.seconds(2));
        battleWonMsg2.setDelay(Duration.seconds(3));
        battleWonMsg1.setOnFinished(e -> battleWonMsg2.play());
        battleWonMsg2.setOnFinished(e -> exit());

        battleWonMsg1.play();
    }

    private void battleLoop() {

        Timeline playerChoiceDialog = controller.getBattleTextAnimation(String.format("What will%n%s do?",
                player.getParty(currentAllyPokemon).getName()), false);
        //playerChoiceDialog.setDelay(Duration.seconds(2));
        controller.switchToPlayerChoice(true);
        playerChoiceDialog.play();

        Button fightButton = controller.getFightButton();
        Button backButton = controller.getBackMoveButton();
        Button pokemonButton = controller.getPokemonButton();
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
                    Timeline outOfPPInfo = controller.getBattleTextAnimation("This move is out of PP!", false);
                    outOfPPInfo.setOnFinished(event -> playerChoiceDialog.play());
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

        int index = 0;

        for (Pokemon pokemon : player.getParty()) {
            Button button = (Button) controller.getPokemonGrid().getChildren().get(index);
            button.setText(String.format("%s%nHP:%d/%d", pokemon.getName(), pokemon.getHp(), pokemon.getMaxHP()));
            button.setDisable(pokemon.getHp() == 0);

            int finalIndex = index;
            index++;

            if (finalIndex == currentAllyPokemon) {
                button.setDisable(true);
                continue;
            }

            button.setOnAction(e -> {
                String currentAllyName = player.getParty(currentAllyPokemon).getName();

                controller.getPokemonGrid().setVisible(false);
                controller.switchToPlayerChoice(false);

                List<Timeline> battleTimeLine = new LinkedList<>();

                Timeline allyPokemonReturnText = controller.getBattleTextAnimation(String.format(
                        "That's enough %s,%ncome back!", currentAllyName), true);
                allyPokemonReturnText.setDelay(Duration.seconds(2));
                battleTimeLine.add(allyPokemonReturnText);

                Timeline allyPokemonReturn = controller.getPokemonFaintedAnimation(true);
                allyPokemonReturn.setDelay(Duration.seconds(1));
                battleTimeLine.add(allyPokemonReturn);

                currentAllyPokemon = finalIndex;
                resetStats(player.getParty(currentAllyPokemon));
                //controller.setAllyInformation(player.getParty(currentAllyPokemon));
                Timeline allyPokemonIntro = controller.getBattleTextAnimation(String.format("Go! %s!",
                        player.getParty(currentAllyPokemon).getName()), true);
                allyPokemonIntro.setDelay(Duration.seconds(1));
                battleTimeLine.add(allyPokemonIntro);

                Timeline allyInfoAnimation = controller.getAllyInfoAnimation(player.getParty(currentAllyPokemon),
                        player.getParty(currentAllyPokemon).getHp());

                battleTimeLine.add(allyInfoAnimation);

                Random generator = new Random();
                int enemyMoveIndex = generator.nextInt(enemy.getParty(currentEnemyPokemon).getMoveList().size());
                Move enemyMove = enemy.getParty(currentEnemyPokemon).getMoveList(enemyMoveIndex);

                List<Timeline> enemyMoveList = useMove(enemyMove, enemy.getParty(currentEnemyPokemon),
                        player.getParty(currentAllyPokemon), true);

                battleTimeLine.addAll(enemyMoveList);

                checkFainted(battleTimeLine);

            });
        }

        if (index < 5) {
            for (int i=index; i <= 6; i++) {
                Button button = (Button)controller.getPokemonGrid().getChildren().get(i);
                button.setText("");
                button.setDisable(true);
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
        Random generator = new Random();

        controller.getMoveGrid().setVisible(false);
        controller.switchToPlayerChoice(false);

        boolean faster = player.getParty(currentAllyPokemon).getStats().get("Speed") >
                enemy.getParty(currentEnemyPokemon).getStats().get("Speed");
        boolean tied = player.getParty(currentAllyPokemon).getStats().get("Speed").equals(
                enemy.getParty(currentEnemyPokemon).getStats().get("Speed"));

        List<Timeline> battleTimeLine;
        int enemyMoveIndex = generator.nextInt(enemy.getParty(currentEnemyPokemon).getMoveList().size());
        Move enemyMove = enemy.getParty(currentEnemyPokemon).getMoveList(enemyMoveIndex);

        if (allyMove.isPriority() && !enemyMove.isPriority()) {
            battleTimeLine = processTurnAllyFaster(allyMove, enemyMove);
        }
        else if (enemyMove.isPriority() && !allyMove.isPriority()) {
            battleTimeLine = processTurnAllySlower(allyMove, enemyMove);
        }
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

        if (enemyFainted) {
            Timeline enemyPokemonFainted = controller.getPokemonFaintedAnimation(false);
            enemyPokemonFainted.setDelay(Duration.seconds(2));

            battleTimeLine.add(enemyPokemonFainted);

            Timeline enemyPokemonFaintedMessage = controller.getBattleTextAnimation(String.format("%s fainted!",
                    enemy.getParty(currentEnemyPokemon).getName()), true);
            enemyPokemonFaintedMessage.setDelay(Duration.seconds(2));

            battleTimeLine.add(enemyPokemonFaintedMessage);

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
        }

        if (allyFainted) {
            Timeline allyPokemonFainted = controller.getPokemonFaintedAnimation(true);
            allyPokemonFainted.setDelay(Duration.seconds(2));

            battleTimeLine.add(allyPokemonFainted);

            Timeline allyPokemonFaintedMessage = controller.getBattleTextAnimation(String.format("%s fainted!",
                    player.getParty(currentAllyPokemon).getName()), true);
            allyPokemonFaintedMessage.setDelay(Duration.seconds(2));

            battleTimeLine.add(allyPokemonFaintedMessage);

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
                    selectNewPokemonMenu(enemyFainted);
                });
            }
            battleTimeLine.get(0).play();
            return;
        }

        finalChecks(battleTimeLine, enemyFainted);
    }

    private void finalChecks(List<Timeline> battleTimeLine, boolean enemyFainted) {
        if(enemyFainted) {
            Timeline enemyNewPokemonInfo = controller.getBattleTextAnimation(String.format("%s %s sends out%n%s!",
                            enemy.getTrainerType().toString(), enemy.getName(), enemy.getParty(currentEnemyPokemon).getName()),
                    true);
            enemyNewPokemonInfo.setDelay(Duration.seconds(2));
            battleTimeLine.add(enemyNewPokemonInfo);

            Timeline pokemonIntroAnimation = controller.getEnemyInfoAnimation(enemy.getParty(currentEnemyPokemon),
                    enemy.getParty(currentEnemyPokemon).getHp());
            pokemonIntroAnimation.setDelay(Duration.seconds(1));
            battleTimeLine.add(pokemonIntroAnimation);

        }

        for (int i=1; i<battleTimeLine.size(); i++) {
            final int finalI = i;
            battleTimeLine.get(i-1).setOnFinished(e -> battleTimeLine.get(finalI).play());
        }

        battleTimeLine.get(battleTimeLine.size() - 2).setOnFinished(e -> {
            battleTimeLine.get(battleTimeLine.size() - 1).play();
        });

        battleTimeLine.get(battleTimeLine.size() - 1).setOnFinished(e -> {
            synchronized (battleTimeLine) {
                try {
                    battleTimeLine.wait(2000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
            battleLoop();
        });

        battleTimeLine.get(0).play();
    }

    private void selectNewPokemonMenu(boolean enemyFainted) {
        int index = 0;
        controller.getPokemonGrid().setVisible(true);
        for (Pokemon pokemon : player.getParty()) {
            Button button = (Button)controller.getPokemonGrid().getChildren().get(index);
            button.setText(String.format("%s%nHP:%d/%d", pokemon.getName(), pokemon.getHp(), pokemon.getMaxHP()));
            button.setDisable(pokemon.getHp() == 0);

            int finalIndex = index;
            button.setOnAction(e -> {
                currentAllyPokemon = finalIndex;
                resetStats(player.getParty(currentAllyPokemon));

                controller.getPokemonGrid().setVisible(false);

                List<Timeline> battleTimeLine = new LinkedList<>();

                Timeline allyPokemonIntro = controller.getBattleTextAnimation(String.format("Go! %s!",
                        player.getParty(currentAllyPokemon).getName()), true);
                allyPokemonIntro.setDelay(Duration.seconds(1));

                battleTimeLine.add(allyPokemonIntro);

                //controller.setAllyInformation(player.getParty(currentAllyPokemon));
                Timeline allyInfoAnimation = controller.getAllyInfoAnimation(player.getParty(currentAllyPokemon),
                        player.getParty(currentAllyPokemon).getHp());

                battleTimeLine.add(allyInfoAnimation);

                finalChecks(battleTimeLine, enemyFainted);

            });

            index++;
        }

        if (index < 5) {
            for (int i=index; i <= 6; i++) {
                Button button = (Button)controller.getPokemonGrid().getChildren().get(i);
                button.setText("");
                button.setDisable(true);
            }
        }

        Button button = controller.getPokemonBackButton();
        button.setText("Back");
        button.setDisable(true);
    }

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

    private List<Timeline> processTurnAllySlower(Move allyMove, Move enemyMove) {
        //Random generator = new Random();

        //int enemyMove = generator.nextInt(enemy.getParty(currentEnemyPokemon).getMoveList().size());
        List<Timeline> enemyMoveTimeLine = useMove(enemyMove, enemy.getParty(currentEnemyPokemon),
                player.getParty(currentAllyPokemon), true);

        if (player.getParty(currentAllyPokemon).getHp() == 0) {
            return enemyMoveTimeLine;
        }
        List<Timeline> allyMoveTimeLine = useMove(allyMove, player.getParty(currentAllyPokemon), enemy.getParty(currentEnemyPokemon), false);

        enemyMoveTimeLine.addAll(allyMoveTimeLine);
        return enemyMoveTimeLine;
    }

    private void itemTurn(Item item) {

    }

    private void runTurn() {

    }

    private List<Timeline> useMove(Move move, Pokemon user, Pokemon target, boolean allyTarget) {

        List<Timeline> moveTimeLine = new LinkedList<>();

        System.out.println(user.getName() + " used " + move.getName());
        Timeline moveUsedDialog = controller.getBattleTextAnimation(user.getName() + " used " + move.getName() + "!",
                true);
        //moveUsedDialog.setDelay(Duration.seconds(2));

        moveTimeLine.add(moveUsedDialog);

        move.setPp((byte) (move.getPp() - 1));
        Random generator = new Random();

        int moveAccuracy = move.getAccuracy();

        if (moveAccuracy != 0) {
            int statAccuracy = ((user.getStatModifiers().get("Accuracy") - target.getStatModifiers().get("Evasiveness")) + 3)/3;
            if (statAccuracy > 6)
                statAccuracy = 6;
            else if (statAccuracy < -6)
                statAccuracy = -6;
            int hit = moveAccuracy * statAccuracy;
            int r = generator.nextInt(100) + 1;
            if (r > hit) {
                System.out.println("Move missed!");
                Timeline moveMissedDialogue = controller.getBattleTextAnimation("Move Missed!", true);
                moveMissedDialogue.setDelay(Duration.seconds(2));

                moveTimeLine.add(moveMissedDialogue);

                return moveTimeLine;
            }
        }

        Enums.Subtypes moveType = move.getSubtype();

        MoveDamageInfo damageInfo = new MoveDamageInfo(0, false, -999);

        int i;

        for (i=0; i<move.getHits(); i++) {
            if (target.getHp() == 0)
                break;

            switch (moveType){
                case PHYSICAL:
                    damageInfo = calculateMoveDamage(0, move, user, target);
                    break;
                case SPECIAL:
                    damageInfo = calculateMoveDamage(1, move, user, target);
                    break;
                case STATUS:
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + moveType);
            }

            if (damageInfo.typeEffect() == 0) {
                final Timeline effectInfo = controller.getBattleTextAnimation(String.format("It doesn't affect %s...",
                                target.getName()), true);
                effectInfo.setDelay(Duration.seconds(2));

                moveTimeLine.add(effectInfo);
            }

            else if (damageInfo.damage() > 0) {

                int damage = damageInfo.damage();

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

                System.out.println(move.getName() + " dealt " + damage + " damage!");

                if (damageInfo.critical()) {
                    final Timeline criticalInfo = controller.getBattleTextAnimation("A critical hit!", true);
                    criticalInfo.setDelay(Duration.seconds(2));

                    moveTimeLine.add(criticalInfo);
                }

                if (damageInfo.typeEffect() >= 2) {
                    final Timeline effectInfo = controller.getBattleTextAnimation("It's supper effective!", true);
                    effectInfo.setDelay(Duration.seconds(2));

                    moveTimeLine.add(effectInfo);

                }
                else if (damageInfo.typeEffect() < 1) {
                    final Timeline effectInfo = controller.getBattleTextAnimation("It's not very effective...", true);
                    effectInfo.setDelay(Duration.seconds(2));

                    moveTimeLine.add(effectInfo);

                }
            }

        }

        if (move.getHits() > 1) {
            Timeline hitsInformation = controller.getBattleTextAnimation(String.format("It hit %d time(s)",
                    i), true);
            hitsInformation.setDelay(Duration.seconds(2));

            moveTimeLine.add(hitsInformation);
        }

        final Timeline statusChangeInfo;

        if (move.getStatusType() != null && move.isSelf()) {
            statusChangeInfo = processStatusChange(move, user);
            moveTimeLine.add(statusChangeInfo);

        }
        else if (move.getStatusType() != null) {
            statusChangeInfo = processStatusChange(move, target);
            moveTimeLine.add(statusChangeInfo);
        }

        return moveTimeLine;
    }

    Timeline processStatusChange(Move move, Pokemon target) {

        final Timeline statusChangeInfo;

        int statup = target.getStatModifiers().get(move.getStatusType().toString()) + (int)move.getStatUp();

        if (statup > 6)
            statusChangeInfo = controller.getBattleTextAnimation(String.format("%s's%n%s can't go any higher!",
                    target.getName(), move.getStatusType().toString()), true);
        else if (statup < -6)
            statusChangeInfo = controller.getBattleTextAnimation(String.format("%s's%n%s can't go any lower!",
                    target.getName(), move.getStatusType().toString()), true);
        else {
            target.getStatModifiers().put(move.getStatusType().toString(), statup);
            if (move.getStatUp() > 0)
                statusChangeInfo = controller.getBattleTextAnimation(String.format("%s's%n%s rose!",
                        target.getName(), move.getStatusType().toString()), true);
            else
                statusChangeInfo = controller.getBattleTextAnimation(String.format("%s's%n%s fell!",
                        target.getName(), move.getStatusType().toString()), true);
        }

        statusChangeInfo.setDelay(Duration.seconds(2));

        return statusChangeInfo;
    }

    MoveDamageInfo calculateMoveDamage(int movetype, Move move, Pokemon user, Pokemon target)
    {
        Random generator = new Random();
        int critNum = generator.nextInt(16);
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
                attack = (int)Math.floor((double) attackTemp * (2 + attackMod)/2);
            else
                attack = attackTemp;
            if(defenseMod <= 0) {
                double defense_multiplier = 2.0 / (2 - defenseMod);
                defense = (int) Math.floor(defenseTemp * defense_multiplier);
            }
            else
                defense = defenseTemp;
            critMod = 1.5f;
        }
        else  {
            if(attackMod >= 0) {
                attack = (int) Math.floor((double) attackTemp * (2 + attackMod) / 2);
            }
            else {
                double attack_multiplier = 2.0 / (2 - attackMod);
                attack = (int) Math.floor(attackTemp * attack_multiplier);
            }

            if(defenseMod >= 0) {
                defense = (int) Math.floor((double) defenseTemp * (2 + defenseMod) / 2);
            }
            else {
                double defense_multiplier = 2.0 / (2 - defenseMod);
                defense = (int) Math.floor(defenseTemp * defense_multiplier);
            }
            critMod = 1;
        }
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
        double damageDouble = Math.floor(((((part1 * move.getPower() * ((attack/defense)/50.0)) + 2) * modifier)));
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

    private void resetStats(Pokemon pokemon)
    {
        pokemon.getStatModifiers().clear();
        for(Enums.StatusType type: Enums.StatusType.values()) {
            pokemon.getStatModifiers().put(type.toString(), 0);
        }
    }
}
