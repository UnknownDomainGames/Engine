package engine.game;

import configuration.Config;
import configuration.io.ConfigIOUtils;
import engine.Platform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class GameData {

    private final Path gameBasePath;
    private final Config gameData;

    private String name;
    private boolean created;

    private final Map<String, String> dependencies = new HashMap<>();
    private final Map<String, Map<String, Integer>> registries = new HashMap<>();
    private final Map<String, String> worlds = new HashMap<>();

    public static GameData createFromCurrentEnvironment(Path gameBasePath, String name) {
        GameData gameData = new GameData(gameBasePath, new Config());
        gameData.setName(name);
        Platform.getEngine().getModManager().getLoadedMods().forEach(modContainer ->
                gameData.getDependencies().put(modContainer.getId(), modContainer.getVersion().toString()));
        return gameData;
    }

    public static GameData createFromExistingGame(Path gameBasePath, String name) {
        Config config = ConfigIOUtils.load(gameBasePath.resolve(name).resolve("game.json"));
        return new GameData(gameBasePath, config);
    }

    protected GameData(Path gameBasePath, Config gameData) {
        this.gameBasePath = gameBasePath;
        this.gameData = gameData;
        this.name = gameData.getString("Name");
        this.created = gameData.getBoolean("Created", false);
        gameData.getMap("Dependencies", Map.of()).forEach((key, value) -> dependencies.put(key, (String) value));
        gameData.getMap("Worlds", Map.of()).forEach((key, value) -> worlds.put(key, (String) value));
    }

    public Path getGameBasePath() {
        return gameBasePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCreated() {
        return created;
    }

    public void setCreated() {
        this.created = true;
    }

    public Map<String, String> getDependencies() {
        return dependencies;
    }

    public Map<String, Map<String, Integer>> getRegistries() {
        return registries;
    }

    public Map<String, String> getWorlds() {
        return worlds;
    }

    public void save() {
        gameData.set("Name", name);
        gameData.set("Created", created);
        gameData.set("Worlds", worlds);
        gameData.set("Dependencies", dependencies);
        gameData.set("Registries", registries);
        var path = gameBasePath.resolve(name);
        if (Files.notExists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
            }
        }
        gameData.save(path.resolve("game.json"));
    }
}
