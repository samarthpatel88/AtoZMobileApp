package com.example.jean.jcplayer.general.errors;


public class AudioListNullPointerException extends NullPointerException {
    public AudioListNullPointerException() {
        super("The playlist is empty or null");
    }
}
