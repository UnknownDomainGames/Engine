package nullengine.game;

import configuration.Config;
import configuration.parser.ConfigParsers;
import nullengine.Platform;

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

    public static GameData createFromCurrentEnvironment(Path gameBasePath, String name) {
        GameData gameData = new GameData(gameBasePath, new Config());
        gameData.setName(name);
        Platform.getEngine().getModManager().getLoadedMods().forEach(modContainer ->
                gameData.getDependencies().put(modContainer.getId(), modContainer.getVersion().toString()));
        return gameData;
    }

    public static GameData createFromGame(Path gameBasePath) {
        Config config = ConfigParsers.load(gameBasePath.resolve("game.json"));
        return new GameData(gameBasePath, config);
    }

    private GameData(Path gameBasePath, Config gameData) {
        this.gameBasePath = gameBasePath;
        this.gameData = gameData;
        this.name = gameData.getString("Name");
        this.created = gameData.getBoolean("Created", false);
        gameData.getMap("Dependencies", Map.of()).forEach((key, value) -> dependencies.put(key, (String) value));
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

    public void save() {
        gameData.set("Name", name);
        gameData.set("Created", created);
        gameData.set("Dependencies", dependencies);
        ConfigParsers.save(gameBasePath.resolve("game.json"), gameData);
    }
}
