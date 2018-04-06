# GameOfLife
This is an implementation of Conway's Game of Life in Java. It was created as a personal learning project and should not be used as an example of how to code! It is, however, quite fun to play with.

The Game of Life consists of a grid of "cells" and consists of four rules:
 - If a live cell has fewer than two neighbours, it will die of underpopulation.
 - If a live cell has more than three neighbours it will die of overcrowding.
 - If a live cell has exactly two or three neighbours, it survives to the next generation.
 - If a dead cell has exactly three neighbours, it becomes a live cell.
 
 More information can be found [here](https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life).
 
### Controls:
| Key | Effect |
| ------ | ------ |
| ESC | quit |
| spacebar | play/pause |
| c | clear board |
| g  |  show/hide grid |
| mouse buttons | toggle cells (can click + drag) |
| "[" and "]" | increment/decrement random % |
| r | randomise board |
| left/right arrows | step forward/back |
| up/down arrows | increment/decrement fps|


There is also an options file you can use to specify various parameters and change the look of the simulation. You can find it at "options.ini", and it contains its own instructions.
