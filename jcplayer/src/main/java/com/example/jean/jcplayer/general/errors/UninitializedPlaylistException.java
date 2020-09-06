package com.example.jean.jcplayer.general.errors;


public class UninitializedPlaylistException extends IllegalStateException {
    public UninitializedPlaylistException() {
        super("The playlist was not initialized. You must to call to initPlaylist method before.");
    }
}
