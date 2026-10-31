#!/usr/bin/env python3
"""Synthesises the app's original sound effects and background music (no third-party audio).

Usage: python3 -I tools/make_sounds.py
"""
import math, random, struct, subprocess, tempfile, wave
from pathlib import Path

OUT = Path(__file__).resolve().parent.parent / "app" / "src" / "main" / "assets" / "audio" / "sfx"
SR = 22050


def midi(n):
    return 440.0 * 2 ** ((n - 69) / 12)


def tone(buf, start, dur, freq, vol, decay=3.0, bright=0.35):
    """Soft music-box style note: sine + a little 2nd/3rd harmonic, exponential decay."""
    s0, n = int(start * SR), int(dur * SR)
    for i in range(n):
        if s0 + i >= len(buf):
            break
        t = i / SR
        env = min(1.0, i / (0.008 * SR)) * math.exp(-decay * t) * min(1.0, (n - i) / (0.03 * SR))
        w = math.sin(2 * math.pi * freq * t) + bright * math.sin(4 * math.pi * freq * t) \
            + bright * 0.4 * math.sin(6 * math.pi * freq * t)
        buf[s0 + i] += vol * env * w


def save(buf, name, kbps=64):
    peak = max(1e-9, max(abs(x) for x in buf))
    data = b"".join(struct.pack("<h", int(max(-1, min(1, x / peak * 0.9)) * 32767)) for x in buf)
    with tempfile.TemporaryDirectory() as d:
        wav = Path(d) / "a.wav"
        with wave.open(str(wav), "wb") as w:
            w.setnchannels(1); w.setsampwidth(2); w.setframerate(SR); w.writeframes(data)
        OUT.mkdir(parents=True, exist_ok=True)
        subprocess.run(["ffmpeg", "-y", "-v", "error", "-i", str(wav), "-c:a", "libmp3lame", "-b:a", f"{kbps}k",
                        str(OUT / name)], check=True)


def music():
    bpm, bars = 96, 16
    beat = 60 / bpm
    buf = [0.0] * int(SR * beat * 4 * bars)
    chords = [(60, [60, 64, 67]), (57, [57, 60, 64]), (53, [53, 57, 60]), (55, [55, 59, 62])]  # C Am F G
    scale = [72, 74, 76, 79, 81, 84]  # C major pentatonic, upper octave
    rng = random.Random(7)
    for bar in range(bars):
        root, triad = chords[bar % 4]
        t0 = bar * 4 * beat
        tone(buf, t0, beat * 2, midi(root - 12), 0.5, decay=1.6, bright=0.2)          # bass on 1 and 3
        tone(buf, t0 + 2 * beat, beat * 2, midi(root - 12), 0.4, decay=1.6, bright=0.2)
        for k, n in enumerate(triad):                                                  # rolled chord
            tone(buf, t0 + k * beat * 0.5, beat * 3, midi(n), 0.16, decay=1.1)
        # melody: eight eighth-note slots, ~70% filled, chord tones favoured on strong beats
        prev = rng.choice(scale)
        for slot in range(8):
            if rng.random() < 0.3:
                continue
            pool = [m for m in scale if abs(m - prev) <= 4] or scale
            note = rng.choice(pool)
            prev = note
            tone(buf, t0 + slot * beat * 0.5, beat * 1.2, midi(note), 0.32, decay=2.6)
    # Fade the very end so the loop point doesn't click.
    f = int(0.05 * SR)
    for i in range(f):
        buf[-1 - i] *= i / f
    save(buf, "music.mp3", 56)


def cheer():
    buf = [0.0] * int(SR * 1.6)
    for k, n in enumerate([72, 76, 79, 84, 88]):
        tone(buf, k * 0.09, 0.9, midi(n), 0.5, decay=3.5, bright=0.5)
    for k in range(8):                                  # sparkle on top
        tone(buf, 0.5 + k * 0.07, 0.4, midi(96 + (k * 5) % 9), 0.18, decay=7)
    save(buf, "cheer.mp3", 64)


def star():
    buf = [0.0] * int(SR * 0.7)
    tone(buf, 0.0, 0.4, midi(88), 0.5, decay=6, bright=0.6)
    tone(buf, 0.12, 0.5, midi(95), 0.5, decay=6, bright=0.6)
    save(buf, "star.mp3", 64)


if __name__ == "__main__":
    music(); cheer(); star()
    print(sorted(p.name for p in OUT.iterdir()))
