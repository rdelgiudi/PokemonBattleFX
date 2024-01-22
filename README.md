# PokemonBattleFX
Implementation of the battle system from Pokémon games with primitive GUI. Made with Java and JavaFX. Project documentation available [here](https://rdelgiudi.github.io/PokemonBattleFX/).

## Opening project
This project was written with Java 8 in mind. It requires JDK 1.8 and JavaFX.

## Overview
This is a work in progress. The engine aims to imitate mechanics from more recent generations but with graphics more akin to generation 3. So far, the project contains:
- Working battle system with proper turn order (single battles only).
- Basic online multiplayer mode.
- 25+ Pokémon species with 90+ unique moves.
- Fully implemented non-volatile status effects: Poison (and badly Poisoned), Paralysis, Sleep, Burn and Freeze.
- Some secondary status effects, such as confusion and flinching.
- Battlefield effects (such as Tailwind).
- Pokémon abilities.
- Team builder, that allows to customize your team, as well as the opponent's.
- Fully working interactive summary screen accessible during battle.
- Working bag menu, with some items already implemented.
- Weather effects (only rain so far).

Plans for future updates:
- Adding all 151 Kanto Pokémon and their main abilities.
- Adding most (if not all) moves known by Kanto Pokémon.
- Implementing held items and missing usable items.
- Enemy trainer customization, including names and AI difficulty (so far, the AI trainers select moves at random).
- Adding missing weather effects.
- Adding Battlefield types (used for some moves, like Nature Power).
- Adding missing secondary status effects (like Attract).
- Double / Triple Battle Support.

## Screenshots

Team Builder:

![image](https://github.com/rdelgiudi/PokemonBattleFX/assets/83218453/bfd095fd-0a0d-49f9-bb0e-836efb5b5ae9)


Team Builder, adding Pokemon:

![image](https://github.com/rdelgiudi/PokemonBattleFX/assets/83218453/527e5025-7204-4809-a718-0568d98453b8)


In battle:

![ezgif-4-b557510718](https://github.com/rdelgiudi/PokemonBattleFX/assets/83218453/eb050901-909d-4399-a35e-e35445cee006)
![ezgif-4-e374ebefb1](https://github.com/rdelgiudi/PokemonBattleFX/assets/83218453/78d0dd46-9e34-4b04-b017-c815331aba10)




Swap menu:

![image](https://github.com/rdelgiudi/PokemonBattleFX/assets/83218453/dbd5bba3-89ca-44e3-8c9d-f14c94c4df29)



Summary Screen:

![image](https://github.com/rdelgiudi/PokemonBattleFX/assets/83218453/875e4343-954d-4812-8512-66b8c1aff980)


![image](https://github.com/rdelgiudi/PokemonBattleFX/assets/83218453/da289ee7-78ae-42d8-88c0-d76825086301)


Bag menu:

![image](https://github.com/rdelgiudi/PokemonBattleFX/assets/83218453/5002a563-90a8-4fd3-95f5-2527ee4357e6)

Animated Sprites support:

![ezgif-4-8f5d55a6b3](https://github.com/rdelgiudi/PokemonBattleFX/assets/83218453/5eea728d-1734-4c58-8466-01849d0ee0c5)



## License Note
The source code itself is licensed under GNU General Public Licence v3. Pokémon is owned by Nintendo, GameFreak and the Pokémon Company. All artwork that is not mine is not included.
