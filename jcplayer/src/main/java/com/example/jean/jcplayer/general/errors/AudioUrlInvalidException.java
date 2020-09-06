package com.example.jean.jcplayer.general.errors;

public class AudioUrlInvalidException extends IllegalStateException {
    public AudioUrlInvalidException(String url) {
        super("The url does not appear valid: " + url);
    }
}
