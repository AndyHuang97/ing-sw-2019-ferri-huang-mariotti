package it.polimi.se2019.server.controller;

/**
 * The messages it needs to send to client
 *
 * @author FF
 *
 */
public enum TurnPhase {
    WAITING_FOR_MOVE,
    WAITING_FOR_SHOOT,
    WAITING_FOR_GRAB,
    WAITING_FOR_RESPAWN,
    WAITING_FOR_EFFECTS,
    WAITING_FOR_POWERUPS,
    WAITING_FOR_RELOAD,
}
