#!/usr/bin/env python3
"""Builds the app's offline assets (WebP images, small mp3s, content.json)
from the raw server dump in UploadFolder/AtoZUpload.

Usage: python3 -I tools/build_assets.py
"""
import json, re, shutil, subprocess
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
SRC = ROOT / "UploadFolder" / "AtoZUpload"
OUT = ROOT / "app" / "src" / "main" / "assets"

CATEGORIES = {
    1: ("alphabet", "Alphabet", "A to Z"),
    2: ("numbers", "Numbers", "1 to 10"),
    3: ("birds", "Birds", "Birds"),
    4: ("animals", "Animals", "Animals"),
    5: ("fruits", "Fruits", "Fruits"),
    6: ("colors", "Colors", "Colors"),
    7: ("shapes", "Shapes", "Shapes & Lines"),
    8: ("vehicles", "Vehicles", "Vehicles"),
}
# Item folders to skip (stray uploads that belong to another category).
SKIP = {(1, 31)}
# Folders holding files of two items: (category, item id) -> the item they should be.
OVERRIDE = {(8, 113): "Bus"}
NUMBER_NAMES = ["One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten"]
JUNK = re.compile(r"bforball|^75|Right button|Left button|^alphabet\.|Caption\.", re.I)


def run(*args):
    subprocess.run(args, check=True, stderr=subprocess.DEVNULL)


def files(d):
    return sorted(p for p in d.iterdir() if p.is_file() and not JUNK.search(p.name)) if d.is_dir() else []


def clean_name(stem):
    s = re.sub(r"\s*\(\d+\)$", "", stem)            # "Standing Line_text (1)"
    s = re.sub(r"_(text|img|icon)$", "", s, flags=re.I)
    s = re.sub(r"^\d+\.\s*", "", s)                  # "1.Peacock"
    s = s.split("- ")[0]                             # "Grey- Gray"
    return s.replace("-", " ").strip()


def webp(src, dst, max_side):
    dst.parent.mkdir(parents=True, exist_ok=True)
    run("ffmpeg", "-y", "-i", str(src), "-vf",
        f"scale='min({max_side},iw)':'min({max_side},ih)':force_original_aspect_ratio=decrease",
        "-c:v", "libwebp", "-quality", "82", "-compression_level", "6", str(dst))


def mp3(src, dst, kbps):
    dst.parent.mkdir(parents=True, exist_ok=True)
    run("ffmpeg", "-y", "-i", str(src), "-vn", "-ac", "1", "-c:a", "libmp3lame", "-b:a", f"{kbps}k", str(dst))


def pick(cands, key):
    """Prefer a file whose name satisfies key, else the first candidate."""
    for p in cands:
        if key(p.name):
            return p
    return cands[0] if cands else None


def slug(s):
    return re.sub(r"[^a-z0-9]+", "_", s.lower()).strip("_")


def main():
    if OUT.exists():
        shutil.rmtree(OUT)
    content = {"categories": [], "rhymes": []}
    for cid, (key, title, caption) in CATEGORIES.items():
        icon = next((SRC / "Category" / str(cid)).iterdir())
        webp(icon, OUT / "img" / "category" / f"{key}.webp", 384)
        cat = {"id": key, "title": title, "caption": caption, "icon": f"img/category/{key}.webp", "items": []}
        for item_dir in sorted((SRC / "Item" / str(cid)).iterdir(), key=lambda p: int(p.name)):
            iid = int(item_dir.name)
            if (cid, iid) in SKIP:
                continue
            only = OVERRIDE.get((cid, iid))
            files_ = (lambda d: [p for p in files(d) if p.name.lower().startswith(only.lower())]) if only else files
            img = pick(files_(item_dir / "ItemImage"), lambda n: re.search(r"_(img|icon)|-Icon", n))
            cap = pick(files_(item_dir / "CaptionImage"), lambda n: n.lower().endswith("_text.png") or "_text" in n)
            # Male/female are decided by file name: some uploads put them in the wrong folder.
            audio = files_(item_dir / "AudioMale") + files_(item_dir / "AudioFemale")
            fem = [p for p in audio if "female" in p.name.lower()]
            mal = [p for p in audio if p not in fem]
            name = only or clean_name(cap.stem if cap else img.stem)
            if cid == 2:
                name = NUMBER_NAMES[iid - 27]
            elif cid == 1:
                name = clean_name(cap.stem)
            # Several items carry two sets of audio (old + new); match the item's own name.
            def match(lst):
                want = slug(name.split(" ")[0] if cid != 1 else clean_name(cap.stem))
                return pick(lst, lambda n: slug(n).startswith(want)) if lst else None
            male, female = match(mal), match(fem) or match(mal)
            if not (img and cap and male and female):
                print("INCOMPLETE", cid, iid, name, img, cap, male, female)
                continue
            sid = f"{key}_{slug(name)}"
            webp(img, OUT / "img" / "items" / f"{sid}.webp", 512)
            webp(cap, OUT / "img" / "captions" / f"{sid}.webp", 512)
            mp3(male, OUT / "audio" / "items" / f"{sid}_m.mp3", 48)
            mp3(female, OUT / "audio" / "items" / f"{sid}_f.mp3", 48)
            cat["items"].append({
                "id": sid,
                "name": name if cid != 1 else name,
                "letter": img.stem[0].upper() if cid == 1 else None,
                "image": f"img/items/{sid}.webp",
                "caption": f"img/captions/{sid}.webp",
                "audioMale": f"audio/items/{sid}_m.mp3",
                "audioFemale": f"audio/items/{sid}_f.mp3",
            })
        content["categories"].append(cat)

    # Rhymes: title from the audio file name; prefer the clean lyric card (no UI buttons).
    for rd in sorted((SRC / "Rhymes").iterdir(), key=lambda p: int(p.name)):
        a = next((rd / "Audio").glob("*.mp3"))
        imgs = sorted((rd / "Image").iterdir())
        im = pick(imgs, lambda n: n == "1.jpg") if rd.name == "2" else imgs[0]
        title = re.sub(r"\s*(Hindi )?Rhymes?|\s*-\s*Hindi.*$", "", re.sub(r"^\d+\.\s*", "", a.stem)).strip()
        rid = f"rhyme_{rd.name}"
        webp(im, OUT / "img" / "rhymes" / f"{rid}.webp", 1280)
        mp3(a, OUT / "audio" / "rhymes" / f"{rid}.mp3", 96)
        content["rhymes"].append({"id": rid, "title": title,
                                  "image": f"img/rhymes/{rid}.webp", "audio": f"audio/rhymes/{rid}.mp3"})

    for n in ("Learning", "Quiz", "Rhymes"):
        webp(SRC / "Menu" / f"{n}.png", OUT / "img" / "menu" / f"{n.lower()}.webp", 384)
    (OUT / "content.json").write_text(json.dumps(content, indent=1, ensure_ascii=False))
    print({c["id"]: len(c["items"]) for c in content["categories"]}, len(content["rhymes"]), "rhymes")


if __name__ == "__main__":
    main()
