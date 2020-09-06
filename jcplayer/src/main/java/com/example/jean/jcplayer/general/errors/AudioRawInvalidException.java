package com.example.jean.jcplayer.general.errors;


public class AudioRawInvalidException extends Exception {
    public AudioRawInvalidException(String rawId) {
        super("Not a valid raw file id: " + rawId);
    }
}
