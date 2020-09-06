package com.example.jean.jcplayer.general.errors;

public class AudioAssetsInvalidException extends Exception {
    public AudioAssetsInvalidException(String path) {
        super("The file name is not a valid Assets file: " + path);
    }
}
