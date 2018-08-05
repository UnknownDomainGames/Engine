# UnknownDomain
A 3D sandbox game by Java.


## Some Design Anaysis

### What should we identify in game?

Discuss the things we need to identify in game.

| Name | What does it referring? | How to identify? | Why? |
| ---  | --- |--------------- | ----------------------- |
| Action | The abstract action, like command, but this should include the action like "player move forward" | By string id (player.move.forward) | We need to bind key or command or other input way to these to control game |
| Game Object Type | The type of ingame object, like block prototype, item prototype, entity type. | By semantic string id (block.stone) | Majorly used for (de)serialization on network or disk | 
| Game Object | The important things ingame, mostly the object under the world life cycle (entity, block, item)| By hierarchy structure string. (block requires location, entity requires id) e.g. /\<dimension id>/\<world id>/\<position> | We need to have a universal way to refer an ingame object which provides a easy way to communicate between client and server |
| Resource | The game resource in disk (or remote) | By its string path | We need to load resources obviously | 

We have to manage these things differently though.

The `Actions` and `GameObjectType` should be managed by individual registries.

The `Resource` should managed by `ResourceManager`. It should provide the basic level of caching (cache bytes).
The resource will transform to other form (Texture, Model, Sound, Animation) and linked with its location.
After the loading stage of a game, all the resource transformed out. The raw bytes cache should be cleared.

The `GameObject` should managed by its parent. We don't need to do anything special to it.
Just process the path to get dimension, get world, and get block or entity.

### Key algorithm/problem

- physics and collision
    - use joml AABB to deal with bonding
    - maybe a general manager under `World` to manage physics?
- raycasting to pick block or entity
    - use joml Ray to deal with picking
- logic/render object state management and update cycle
    - naive idea is that we keep two different form of data in game and renderer thread.
    - the data in logic thread are changed by event (the event could toggle by network/user input)
    - after the logic thread receive event, it should emit the change which only exposed to renderer thread  
    - the data in renderer thread only receives the change provide by logic thread; ideally it won't query logic thread data by it self
- face culling
    - https://0fps.net/2012/06/30/meshing-in-a-minecraft-game/
    - http://www.lighthouse3d.com/tutorials/view-frustum-culling/
    

