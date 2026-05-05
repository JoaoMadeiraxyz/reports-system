# Reports System

A Minecraft Spigot 1.8.8 plugin for managing player reports.

## Features

- Players report others via `/report <player> <reason>`
- Staff view pending reports in a paginated GUI via `/reports`
- Staff resolve reports by clicking items in the GUI
- All reports saved locally in JSON
- Fully configurable GUI items, materials, names, and messages

## Installation

1. Build the plugin: `mvn clean package`
2. Copy `target/reports-system-1.0-SNAPSHOT.jar` to your server's `plugins/` folder
3. Restart the server

## Usage

### Players
```
/report <player> <reason>
```

### Staff (opped players)
```
/reports
```
Opens the staff panel GUI. Click a report item to resolve it.

## Configuration

All settings are in `plugins/reports-system/config.yml`:

- `gui.staff-panel` — GUI title, rows, items per page, access level
- `gui.staff-panel.items` — Static decorative items
- `gui.staff-panel.report-item` — Template for report items (material, name, lore)
- `gui.staff-panel.no-reports-item` — Item shown when no reports exist
- `messages` — All in-game messages

## Requirements

- Spigot 1.8.8 (or compatible fork)
- Java 8
