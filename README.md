# PokemonBattleFX
Implementation of the Battle Engine from Pokémon games with primitive GUI. Made with Java and JavaFX.

## Opening project
This project was written with Java 8 in mind. It requires JDK 1.8 or higher and JavaFX.

## Overview
This is a work in progress. The engine aims to imitate mechanics from more recent generations but with graphics more akin to generation 3. So far, the project contains:
- Working (but somewhat incomplete) battle system with proper turn order.
- 15+ Pokémon species with 30+ unique moves.
- Fully implemented non-volatile status effects: Poison (and badly Poisoned), Paralysis, Sleep, Burn and Freeze.
- Some secondary status effects, such as confusion and flinching.
- Battlefield effects (such as Tailwind).
- Pokémon abilities.
- Team builder, that allows to customize your team, as well as the opponent's.
- Fully working interactive summary screen acessible during battle.

Plans for future updates:
- Adding all 151 Kanto Pokémon and their main abilities.
- Adding most (if not all) moves known by Kanto Pokémon.
- Implementing an item system, including held items.
- Trainer customization, including names and AI difficulty (so far, the AI trainers select moves at random).
- Adding weather effects.
- Adding Battlefield types (used for some moves, like Nature Power).
- Adding missing secondary status effects (like Attract).

## Screenshots

Team Builder:
![image](https://user-images.githubusercontent.com/83218453/233850438-7837c5e1-34de-4aeb-8387-269033fd96d2.png)

Team Builder, adding Pokemon:
![Screenshot_20230423_175433](https://user-images.githubusercontent.com/83218453/233850346-f3abe02d-a831-4eb0-8c3f-1a727e5e514a.png)

In battle:
![image](https://github.com/rdelgiudi/PokemonBattleFX/assets/83218453/9ac5eb80-6e79-4c3e-9f93-11efbcdc443a)

![image](https://github.com/rdelgiudi/PokemonBattleFX/assets/83218453/acc3fa39-da8e-4af7-977e-80e43e9ba076)

![image](https://github.com/rdelgiudi/PokemonBattleFX/assets/83218453/eab0a542-cab7-4a02-a5ab-d987490d26a1)


Swap menu:
![image](https://github.com/rdelgiudi/PokemonBattleFX/assets/83218453/ab08cbe2-4341-47f4-8325-686aa4fd0ecf)


Summary Screen:
![image](https://user-images.githubusercontent.com/83218453/233850552-02bffa2b-deba-4803-b287-41e339312474.png)

![image](https://user-images.githubusercontent.com/83218453/233850585-9e924e18-da98-4377-adce-c19eeaf0d2b5.png)

## License Note
The source code itself is licensed under GNU General Public Licence v3. Pokémon is owned by Nintendo, GameFreak and the Pokémon Company. All artwork that is not mine is not included.
