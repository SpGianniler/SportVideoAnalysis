# SportVideoAnalysis

A customizable JavaFX video analysis application for sports, originally developed as part of my university thesis. 
It lets users load a video, tag clips by profile-defined categories (like Attack, Defence, Players, Transition), save those clips to a SQLite database, query them by tags or categories, and assemble selected highlights into a new video using `ffmpeg`.

## What it does 
- **Profile-driven tagging**: Load a JSON profile (`profiles/*.json`) that defines label categories (e.g., Attack, Defence, Transition, Players). When active, these categories appear as labeled columns in the video screen.
- **Video playback and recording**: Load a video file, play/pause/seek, set start/end times with record markers, and add clips to the database.
- **Simple database model**: Each profile uses its own SQLite file (`<profile>.db`). The `video` table stores `VideoLoc`, `Start`, `End`, a flat `tags` array, and a `categories` array.
- **Tags and categories query**: The database manager supports toggling between Tags Search and Category Search modes. Queries split keywords by commas and apply `OR` matching against the `tags` or `categories` columns.
- **Highlight generation**: Select saved clips using the "Select Clip" button (stores IDs in memory). The "Save Highlight" button prompts for an output file name, then uses `ffmpeg` to extract the selected time ranges from the original videos and concatenate them into the new highlight file.
- **Profile management**: Create, load, save, and set profiles active. Changing the active profile updates the video screen label lists and associates the database with that profile.

## Languages and frameworks
- Java 21
- JavaFX (UI, media playback, scene controls)
- SQLite (via JDBC)
- Maven
- `ffmpeg` 

## Problem it solves
Coaches and analysts often need a quick, profile-driven way to tag short sports video segments with custom categories, search saved clips by tag or category, and compile highlights—without relying on complex video-editing software or manual file management.

## Setup
```bash
mvn clean compile
```
Run `Main.java` or launch from your IDE.

## Example profile data
`defaultProfile.json` defines categories; `sampleLists.json` provides sample tag values for quick testing.
