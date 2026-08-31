## ADDED Requirements

### Requirement: Mini Golkonda NPC Rendering
The NPC 39900 SHALL be visually rendered as a smaller version of the Golkonda boss (displayId 25126), scaled down by manipulating its server-side collision values relative to the original model.

#### Scenario: Client scaling
- **WHEN** the Lineage 2 client loads the NPC 39900
- **THEN** it renders the model of Golkonda at a reduced scale based on collision radius 10 and height 22.
