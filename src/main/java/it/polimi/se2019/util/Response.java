package it.polimi.se2019.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.se2019.server.dataupdate.*;
import it.polimi.se2019.server.games.Game;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable, NetMsg {

    private Game game;
    private boolean success;
    private String message;
    //private static Gson gson;

    private List<StateUpdate> updateData;

    public Response(Game game, boolean success, String message) {
        this.game = game;
        this.success = success;
        this.message = message;
    }

    public Response(List<StateUpdate> updateData) {
        this.success = true;
        this.message =  "Model update";
        this.updateData = updateData;
    }

    public Game getGame() {
        return this.game;
    }

    public boolean getSuccess() {
        return this.success;
    }

    public String getMessage() {
        return this.message;
    }

    public List<StateUpdate> getUpdateData() {
        return updateData;
    }

    @Override
    public String serialize() {
        RuntimeTypeAdapterFactory<StateUpdate> stateUpdateAdapterFactory = RuntimeTypeAdapterFactory.of(StateUpdate.class, "type")
                .registerSubtype(AmmoCrateUpdate.class, "AmmoCrateUpdate")
                .registerSubtype(WeaponCrateUpdate.class, "WeaponCrateUpdate")
                .registerSubtype(CharacterStateUpdate.class, "CharacterStateUpdate")
                .registerSubtype(CurrentPlayerStateUpdate.class, "CurrentPlayerStateUpdate")
                .registerSubtype(CurrentWeaponUnpdate.class, "CurrentWeaponUnpdate")
                .registerSubtype(KillShotTrackUpdate.class, "KillShotTrackUpdate")
                .registerSubtype(WeaponStateUpdate.class, "WeaponStateUpdate");

        Gson gson = new GsonBuilder().registerTypeAdapterFactory(stateUpdateAdapterFactory).create();
        return gson.toJson(this, Response.class);
    }

    @Override
    public NetMsg deserialize(String msg) {
        RuntimeTypeAdapterFactory<StateUpdate> stateUpdateAdapterFactory = RuntimeTypeAdapterFactory.of(StateUpdate.class, "type")
                .registerSubtype(AmmoCrateUpdate.class, "AmmoCrateUpdate")
                .registerSubtype(WeaponCrateUpdate.class, "WeaponCrateUpdate")
                .registerSubtype(CharacterStateUpdate.class, "CharacterStateUpdate")
                .registerSubtype(CurrentPlayerStateUpdate.class, "CurrentPlayerStateUpdate")
                .registerSubtype(CurrentWeaponUnpdate.class, "CurrentWeaponUnpdate")
                .registerSubtype(KillShotTrackUpdate.class, "KillShotTrackUpdate")
                .registerSubtype(WeaponStateUpdate.class, "WeaponStateUpdate");

        Gson gson = new GsonBuilder().registerTypeAdapterFactory(stateUpdateAdapterFactory).create();

        return gson.fromJson(msg, Response.class);
    }
}
