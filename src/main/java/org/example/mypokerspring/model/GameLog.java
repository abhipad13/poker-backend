package org.example.mypokerspring.model;

import org.example.mypokerspring.ws.GameBroadcaster;
import org.example.mypokerspring.ws.dto.LogPayload;

import java.util.*;

public class GameLog {
    private final Map<Integer, List<LogPayload>> handLogs = new LinkedHashMap<>();
    private int currentHandNumber = 0;

    private final String gameId;
    private final GameBroadcaster broadcaster;

    public String getGameId() {
        return gameId;
    }

    public GameBroadcaster getBroadcaster() {
        return broadcaster;
    }

    public GameLog(String gameId, GameBroadcaster broadcaster) {
        this.gameId = gameId;
        this.broadcaster = broadcaster;
    }

    public void startNewHand() {
        currentHandNumber++;
        handLogs.put(currentHandNumber, new ArrayList<>());
    }

    public void log(LogEventType eventType, String message, boolean broadcast, boolean error) {
        if (!handLogs.containsKey(currentHandNumber)) {
            handLogs.put(currentHandNumber, new ArrayList<>());
        }

        LogPayload payload = new LogPayload(
                gameId,
                eventType.name(),
                message,
                error
        );

        handLogs.get(currentHandNumber).add(payload);
        if (broadcast) {
            broadcast(payload);
        }
    }

    public List<LogPayload> getCurrentHandLogs() {
        return handLogs.getOrDefault(currentHandNumber, new ArrayList<>());
    }

    public Map<Integer, List<LogPayload>> getAllLogs() {
        return handLogs;
    }

    private void broadcast(LogPayload payload) {
        if (broadcaster != null && gameId != null) {
            broadcaster.sendLog(payload);
        }
    }
}