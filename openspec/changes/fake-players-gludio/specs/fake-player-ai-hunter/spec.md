## ADDED Requirements

### Requirement: DNA-Based Combat Logic
The Hunter AI SHALL calculate its combat decisions (attack, provoke, flee) based on its individual DNA weights (aggressiveness, courage).

#### Scenario: Low HP Courage Check
- **WHEN** a Hunter bot's HP drops below a critical threshold
- **THEN** it checks its Courage DNA weight; if it fails the check, it uses a Scroll of Escape or flees towards the nearest town.

### Requirement: Party Logic and Follower Behavior
The Hunter AI SHALL support joining a party and following a designated leader if its Party Tendency DNA is high enough.

#### Scenario: Joining a Party
- **WHEN** a Hunter bot encounters another bot or is prompted by the Party Logic system
- **THEN** if its Party Tendency weight allows, it joins the party and begins assisting the leader in combat or following them.

### Requirement: Role-Based Behavior
The Hunter AI SHALL exhibit behaviors corresponding to its assigned class role (Tanker, DD, Healer).

#### Scenario: Tanker Provoke
- **WHEN** a party member is attacked by a monster
- **THEN** a Hunter with a Tanker role uses Hate/Provoke skills to draw aggro based on its Aggressiveness DNA.
