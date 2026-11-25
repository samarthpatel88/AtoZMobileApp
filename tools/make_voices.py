#!/usr/bin/env python3
"""Voices every item in content.json with Kokoro (Apache 2.0): a boy voice (am_michael) and a girl
voice (af_heart), so all categories sound the same. Run after build_assets.py.

The model files are not in git (about 350 MB); download them once from
https://github.com/thewh1teagle/kokoro-onnx/releases/tag/model-files-v1.0

Usage: python3 -I tools/make_voices.py kokoro-v1.0.onnx voices-v1.0.bin
"""
import json, subprocess, sys, tempfile
from pathlib import Path

import numpy as np
import soundfile as sf
from kokoro_onnx import Kokoro

ROOT = Path(__file__).resolve().parent.parent
ASSETS = ROOT / "app" / "src" / "main" / "assets"
VOICES = {"audioMale": "am_michael", "audioFemale": "af_heart"}


def text(cat, item):
    return f"{item['letter']} for {item['name']}" if cat["id"] == "alphabet" else item["name"]


def main():
    kokoro = Kokoro(sys.argv[1], sys.argv[2])
    content = json.loads((ASSETS / "content.json").read_text())
    with tempfile.TemporaryDirectory() as tmp:
        for cat in content["categories"]:
            for item in cat["items"]:
                for field, voice in VOICES.items():
                    audio, rate = kokoro.create(text(cat, item) + ".", voice=voice, speed=0.9, lang="en-us")
                    pad = np.zeros(int(rate * 0.15), dtype=audio.dtype)     # a short breath before and after
                    wav = Path(tmp) / "w.wav"
                    sf.write(wav, np.concatenate([pad, audio, pad]), rate)
                    subprocess.run(["ffmpeg", "-y", "-loglevel", "error", "-i", str(wav), "-ac", "1",
                                    "-c:a", "libmp3lame", "-b:a", "48k", str(ASSETS / item[field])], check=True)
            print(cat["id"], len(cat["items"]))


if __name__ == "__main__":
    main()
