# Changelog

## [0.3.0] - Third Playtest - 2024-11-01

### Mechanics

- Fixed Flight Core effect being negated when there is 1 stack of Flight
- Changed end of combat event orders such that Self Repair is triggered after ending Duplicate
- Fixed card pools in Duplicate so that random card draw no longer return Echo cards
- Buffed Duplicate to always give 3 energy and 5 card draws
- Adaptive cards no longer draw cards with special rarity
- Fixed orb slots not being reduced properly when returning from the Defect to Echo
- Fixed basic cards being transformed into special cards
- Reduced fullscreen effect is now enabled by default
- Removed long-lasting effects from transformed relics
- Added shader effect to health bar while in Duplicate

### Cards

- Fixed a typo in Peak Power
- Added a keyword for Exhume
- Updated the description of Short Flight
- Fixed a crash when ending combat in Mayhem Form
- Added a missing Exhaust keyword to Nano Boost
- Removed Reevaluation
- Buffed Focusing Beam: cost decreased from 1 to 0
- Buffed Virtualized Offense: cost decreased from 2 to 1
- Buffed Recursive Copy: added Constant
- Buffed Genji Copy: cost decreased from 2 to 1
- Buffed This Ends Now: now damages all enemies
- Buffed Echo High Noon: damage decreased from 10 to 0, scaling increased from 10(12) to 25(30)
- Nerfed Flash Beam: damage decreased from 12(16) to 9(12)
- Reworked Nano Boost: rarity changed from uncommon to rare, duration decreased from 2(1) to 1 round, cost reduced from
  1 to 1(0)
- Nerfed Disengage: cost increased from 1 to 2
- Buffed Self-Learning AI: rarity decreased from rare to uncommon
- Nerfed Carpet Bomb: added exhaust
- Nerfed Power Template: block decreased from 2(4) to 1(3)
- Reworked Rapid Assail: damage increased from 8(13) to 8(14), bonus effect changed from gaining Weak to gaining
  Ultimate Charge, removed Exhaust
- Buffed Sticky Splash: Sticky Bomb to self decreased from 3 to 2
- Nerf Deepfake: cost increased from 2(1) to 3(2)
- Fixed character names on Deepfake
- Buffed Copy: now always give Ultimate Charge before Duplicating
- Added Advanced Template

### Events

- Reworked Precious Data event: now offers each card separately at the cost of max HP

## [0.2.0] - Second Playtest - 2023-05-04

### Mechanics

- Added new mechanic: Swap
- Added new keyword: Foreign
- Buffed Tri-Shot: now guarantees non-zero damage
- Nerfed Flight: damage reduction reduced from 50% to 30%
- Removed cap on Ultimate Charge, with each Charged effect consuming 10 Charges instead of removing all Charges
- Added a limit on the number of Sticky Bombs that explode per round, defaults to 3

- Changed Duplicate to allow 1 orb to stay after duplicating any orb-enabled characters
- Updated descriptions of Flight and Sticky Bomb
- Improved Duplicate card preview so that the preview card is more likely to be in hand when duplicating

- Fixed a bug in Duplicate where post-draw effects are triggered before start-of-turn card draw
- Fixed a bug in Duplicate where the master deck is copied to draw pile
- Fixed NPE crash when exiting game in Duplicate with some powers active
- Fixed Duplicate on other characters so that it properly avoids duplicating the character itself

### Relics

- Added new relics
    - Crystal Ball
    - Metal Fork

- Removed Cloning Module and Overloaded Module
- Added Flight Core and Overloaded Core
- Flight Core is now the starting relic

### Cards

- Added new cards
    - Peak Power
    - Hacked
    - Skill Gap
    - Swift Switch
    - Flex Attack
    - Adaptive Card
    - Power Block
    - Recursive Copy
    - Deepfake
    - Self-Learning AI
    - Portable Battery
    - Carpet Bomb
    - Unpredictability
    - Power Template
    - Virtual Ambush
    - Sticky Splash
    - Initialization
    - Rapid Assail
    - Targeted Defense
    - Disengage
    - Preprocess
    - Blinding Bombs
    - Quick Ignition
    - Virtualized Offense
    - Recursive Copy

- Changed Copy from starter to common
- Changed Calibration: cost increased from 2(1) to 2, damage increased from 1 to 1(2)
- Changed Sticky Situation: damage increased from 5 to 9(13), Sticky Bombs increased from 1 to 2, cost increased from 1(
  0) to 1
- Changed Nano Boost: type changed from power to skill, exhaust added, cost decreased from 2(1) to 1, duration increased
  from 1 to 1(2) rounds
- Changed Sticky Flare: cost increased from 1(0) to 1, duration increased from 1 to 2(3) rounds
- Changed Quick Assassination: rarity changed from uncommon to rare, cost increased from 1 to 2, damage increased from
  7(10) to 13(17)
- Changed Mayhem Form: now focus on Sticky Bombs only
- Changed Satisfying Stick: rarity changed from common to uncommon, cost increased from 1(0) to 1, Sticky Bombs
  increased from 5 to 5(6)
- Changed Echo Dash: rarity changed from uncommon to rare, cost increased from 0 to 1
- Changed Short Flight: rarity changed from common to starter, Block increased from 3(6) to 4(7), Flight reduced from 2
  to 1(2)
- Changed Swaying Beam: rarity changed from starter to uncommon, Vulnerable increased from 1 to 2, damage increased from
  9(12) to 14(18)
- Buffed Copy: rarity changed from starter to common, cost reduced from 2(1) to 1, added an additional effect to give 4(
  6) Ultimate Change when not Charged

- Buffed Reconfiguration: rarity changed from rare to uncommon, cost reduced from 2 to 1
- Buffed Echoed Aggression: exhaust removed, retain added on upgrade
- Buffed Reevaluation: cost reduced from 2 to 0
- Buffed Virtualized Shield: retain added
- Buffed Deep Learning: cost reduced from 2(1) to 0, innate added on upgrade
- Buffed Generative Strike: damage increased from 4(7) to 6(9), Ultimate Charge increased from 2 to 3
- Buffed Long Range Stick: damage increased from 6(8) to 9(12), Sticky Bombs increased from 2(3) to 3(4)
- Buffed Initiating Stick: Sticky Bombs increased from 3(4) to 4(5)
- Buffed Aerial Agility: Block per Flight increased from 2(3) to 3(4)
- Buffed Melee Combo: upgraded version gives Tri-Shot+
- Buffed Hit And Run: damage increased from 5(8) to 8(11)
- Buffed Flash Beam: draw 1 card when not Focused
- Buffed Efficient Copy: cost reduced from 2(1) to 1, power increased from 1 to 1(2)
- Buffed Echo High Noon: initial damage increased from 0(10) to 10, damage scaling increased from 10 to 10(12)
- Buffed Flexible Copy: cost reduced from 2 to 1
- Buffed Inaccurate Stick: Sticky Bombs increased from 2(3) to 3(4)
- Buffed Fast Generation: damage per Ultimate Charge decreased from 6(4) to 5(4)

- Nerfed This Ends Now: damage reduced from 99999 to 130
- Nerfed BFS: card draw reduced from 5(6) to 4(5)

- Renamed Fatal to Lethal in Echo Blade
- Removed Copy and Swaying Beam from starting deck, replaced by Swift Switch and Short Flight
- Removed Final Blow
- Removed Evasive Copy
- Fixed NPE crash in Echo Dash

### Events

- Added new event: Precious Data

### Effects

- Added SFX and VFX for 3 Charged Copy cards
- Tweaked player animated texture in Duplicate
- Reduced Sticky Bomb SFX volume when the stack is small
- Updated character select button SFX

## [0.1.0] - First Playtest - 2023-04-19

### New content

- Content for the first playtest
    - 52 cards
    - 1 potion
    - 4 relics