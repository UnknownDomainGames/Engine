package engine.game;

import engine.Platform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameDataStorage {

    private Path basePath;
    private Map<String, GameData> games = new HashMap<>();

    public GameDataStorage() {

    }

    public GameDataStorage(Path storageBasePath) {
        basePath = storageBasePath;
        games = gatherGameData(storageBasePath).stream().collect(Collectors.toMap(GameData::getName, gameData -> gameData));
    }

    public Map<String, GameData> getGames() {
        return games;
    }

    public boolean isAcceptableNewGameName(String candidate) {
        return isValidFilename(candidate) && !games.containsKey(candidate);
    }

    public void createGameData(String name) {
        var gameData = GameData.createFromCurrentEnvironment(basePath, name);
        gameData.save();
        games.put(name, gameData);
    }

    public static boolean isValidFilename(String candidate) {
        try {
            Paths.get("/" + candidate + "/");
            return true;
        } catch (InvalidPathException e) {
            return false;
        }
    }

    public static List<GameData> gatherGameData(Path storageBasePath) {
        try {
            return Files.list(storageBasePath)
                    .filter(Files::isDirectory)
                    .filter(path -> Files.exists(path.resolve("game.json")))
                    .map(path -> GameData.createFromExistingGame(storageBasePath, path.getFileName().toString()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            Platform.getLogger().warn("Exception thrown when gathering game data", e);
            return Collections.emptyList();
        }
    }
}
