# UnknownDomain
A 3D sandbox game by Java.


## Some Design Anaysis

### What should we identify in game?

Discuss the things we need to identify in game.

| Name             | What does it referring?                                                                          | How to identify?                                                                                                           | Why?                                                                                                                         |
| ---------------- | ------------------------------------------------------------------------------------------------ | -------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------- |
| Action           | The abstract action, like command, but this should include the action like "player move forward" | By string id (player.move.forward)                                                                                         | We need to bind key or command or other input way to these to control game                                                   |
| Game Object Type | The type of ingame object, like block prototype, item prototype, entity type.                    | By semantic string id (block.stone)                                                                                        | Majorly used for (de)serialization on network or disk                                                                        |
| Game Object      | The important things ingame, mostly the object under the world life cycle (entity, block, item)  | By hierarchy structure string. (block requires location, entity requires id) e.g. /\<dimension id>/\<world id>/\<position> | We need to have a universal way to refer an ingame object which provides a easy way to communicate between client and server |
| Resource         | The game resource in disk (or remote)                                                            | By its string dir                                                                                                          | We need to loadOrder resources obviously                                                                                     |

We have to manage these things differently though.

The `Actions` and `GameObjectType` should be managed by individual registries.

The `Resource` should managed by `ResourceManager`. It should provide the basic level of caching (cache bytes).
The resource will transform to other form (Texture, Model, Sound, Animation) and linked with its location.
After the loading stage of a game, all the resource transformed out. The raw bytes cache should be cleared.

The `GameObject` should managed by its parent. We don't need to do anything special to it.
Just process the dir to get dimension, get world, and get block or entity.

### About the Registry Convenience

The ideal way to register a thing is only providing its id without any redundant information. The Mod should not consider too much about conflicting with other Mod. 

```java
Registry<BlockObject> registry;
registry.register(block.setRegistryName('air')); // infer the query full dir is <current modid>.block.air
Registry<ItemObject> itemRegistry;
itemRegistry.register(item.setRegisterName('stone')); // infer the query full dir is <current modid>.item.stone

RegistryManager manager;
manager.register(block.setRegistryName('stone'));  // infer the query full dir is <current modid>.block.stone

// when we want to get a registered object
manager.get('unknowndomain.block.stone'); // get the stone block
```

### About the Register Name

I suggest we all use the snake case (split word with low_dash) for register name. 

The name should not contain any dot/period (.)

### About Sub Registry Namespace (Suggestion)

There could be sub-named blocks and items. For block, it might be produced by combining properties.


### Booting Progress (Draft)

1. initialize glfw opengl, window and other hook
2. initialize resource manager, pull default resource source
3. initialize mod manager
4. initialize default renderer, which will require loadOrder default textures and objects

### Start Game Loading Progress (Draft)

1. initialize player profile, login information (GUI show to let player login if there is no local cached profile) 
2. pull the resources/mod config from server
    1. check local if they exist
    2. download missing mod and resource
3. initialize action manager
4. initialize keybinding, requiring action manager
5. initialize game context
6. loadOrder all mods by mod manager
    1. mod register all block/item/entity
    2. resource manager loads all required resources by mod
    3. use custom mod resource process pipeline to process resource


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

#### RenderChunk 

A block model **A** has N vertices, then it vertex length is N * 3

[x0 y0 z0 x1 y1 z1 ... xn yn zn]

Bake the chunk as combination of block model:

blockAt (0,0,0) (0,0,1) ....

[x0 y0 z0 x1 y1 z1 ... xn yn zn] [x0 y0 z0 x1 y1 z1 ... xn yn zn] ...

Suppose `len(m)` is the vertices count of model `m`. 

We maintains a summed list `L` where `L[x] = L[x - 1] + len(model at index x)`

When a block is changed in chunk, we update the render chunk data by swap the block vertex:

Suppose the block at position `(x, y, z)` changed,

```java
float[] newVertices; // passed by param
int index = (x & 0xF) < 8 | (y & 0xF) < 4 | (z & 0xF);
int leftSum = L[index - 1];
int rightSum = L[index];
int totalLength = L[(0xF) < 8 | (0xF) < 4 | (0xF)];

int newRightSum = leftSum + newVertices.length;
int oldVerticesCount = rightSum - leftSum;

// these are not real gl commands
// shift the right vertices
glCopy(rightSum, newRightSum, totalLength - rightSum);
// upload the new block vertices to the correct position
glUpload(newVertices, leftSum);
```

### Render Progress Introduction

http://fragmentbuffer.com/gpu-performance-for-game-artists/

[MVP Camera Matrix Transformation](http://www.opengl-tutorial.org/beginners-tutorials/tutorial-3-matrices/)

[Shadow Mapping](https://learnopengl.com/Advanced-Lighting/Shadows/Shadow-Mapping)

### Game Loop

[LWJGL book Fix Step](
https://github.com/lwjglgamedev/lwjglbook/blob/master/chapter02/src/main/java/org/lwjglb/engine/GameEngine.java)

[What is the point of update independent rendering in a game loop?](
https://gamedev.stackexchange.com/questions/132831/what-is-the-point-of-update-independent-rendering-in-a-game-loop)

[Dynamic Tick vs Fix Step Tick](https://gamedev.stackexchange.com/questions/56956/how-do-i-make-a-game-tick-method)
[Introduce Game Loop](http://gameprogrammingpatterns.com/game-loop.html)

Splitting Between Logic and Render

- Logic
    - update independently
    - update world and various world subsystem
        - update world physics between entity
- Renderer
    - update progress depending on the partial progress of logic 
    - maintains block/entity model part transformation
    - maintains batch particles system
    - render update

## Some Humble Design Thought

When we want to create some object. There always some precondition, re-configuration the object may have. Normally, we pass them as the parameter to the object. But, when the object is complex. This way won't really work.

These are three pattern to create a object:

Whent the object is really simple, like data class. We can use constuctor.

```java
class A {
    public A(String paramA, int paramB) {
        this.a = paramA;
        this.b = paramB;
    }
}
```

If there are some transformation or other calculation which **only** related to this object creation, such as load resources from disk. We can have a static create method to manage that.

```java
class A {
    public static A create(String paramA, int paramB) {
        // perform some transformation to paramA
        // perform some transformation to paramB
        // perform complex transformation
        return new A(transformedA, transformedB, transformed C);
    }
    private A(...) {
    }
}
```

If the object lifecycle is really complex, we usually pass a config to constuctor. Then, a initialization function there to trigger the complex initialization. (Maybe multiple init function). 

This is the case that the object will pass through multiple stages changes. 


```java
class A {
    public A(Config config) {
        this.config = config;
    }

    public start(Context otherContext) throws Exception {
        preInit();
        init();
        postInit();
    }
}
```


