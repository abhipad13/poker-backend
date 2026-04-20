package org.example.mypokerspring.service;

import org.example.mypokerspring.model.Game;
import org.example.mypokerspring.ws.GameBroadcaster;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    private final Map<String, Game> games = new ConcurrentHashMap<>();
    private final GameBroadcaster broadcaster;

    public GameService(GameBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    // ✅ Create and store a new Game
    public Game createGame(String gameId, String managerId) {
        Game game = new Game(gameId, broadcaster);
        game.addPlayer(managerId);
        game.setManagerId(managerId);
        games.put(gameId, game);
        return game;
    }

    // ✅ Get a game by ID
    public Game getGame(String gameId) {
        Game game = games.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found: " + gameId);
        }
        return game;
    }

    // ✅ Optional: get all games (for admin/debugging)
    public Map<String, Game> getAllGames() {
        return games;
    }
}

