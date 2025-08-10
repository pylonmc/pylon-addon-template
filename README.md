# Construction Wand

Construction Wand is a Minecraft plugin based on Paper that provides players with powerful building and breaking wands, allowing them to place or break multiple blocks at once, greatly improving building and excavation efficiency. This plugin requires PylonCore as a prerequisite.

## Features

### Building Wand
- Place multiple blocks at once (9, 64 or 4096)
- Two modes available:
    - Normal mode: Area can be formed by any blocks
    - Block strict mode: Area can only be formed by one type of block
- Supports permission checks and game mode restrictions

### Breaking Wand
- Break multiple blocks at once (9, 64 or 4096)
- Area can only be formed by one type of block
- Supports permission checks and game mode restrictions

### Projection Display
- Displays a preview of blocks to be placed or broken when players aim at blocks with wands
- Preview uses semi-transparent glass effect for clear visibility

### Axis Control
- Switch wand axis restriction mode by swapping off-hand items
- Supports X, Y, Z axis restrictions and unrestricted mode

## Wand Types

| Wand Name | Type | Default Max Range | Special Requirements |
|-----------|------|-------------------|----------------------|
| Build Staff 1 | Building Wand | 9 blocks | None |
| Build Staff 2 | Building Wand | 64 blocks | None |
| Build Staff 3 | Building Wand | 4096 blocks | OP only |
| Block Strict Build Staff 1 | Building Wand (Block Strict) | 9 blocks | Area must be formed by one type of block |
| Block Strict Build Staff 2 | Building Wand (Block Strict) | 64 blocks | Area must be formed by one type of block |
| Block Strict Build Staff 3 | Building Wand (Block Strict) | 4096 blocks | OP only, area must be formed by one type of block |
| Break Staff 1 | Breaking Wand | 9 blocks | Area must be formed by one type of block |
| Break Staff 2 | Breaking Wand | 64 blocks | Area must be formed by one type of block |
| Break Staff 3 | Breaking Wand | 4096 blocks | OP only, area must be formed by one type of block |

## Configuration

The plugin provides flexible configuration options that can be customized in [config.yml](file://D:\github_clone\construction-wand\src\main\resources\config.yml):

### Enable automatic updates
```yml
auto-update: false
```

### Show projection preview
```yml
display-projection: true
```

### Range settings for various wands
Server administrators can modify `limit-blocks` to limit the number of blocks players can modify when using wands
```yml
limit-blocks:
  building-wand-1: 9
  building-wand-2: 64
  building-wand-3: 4096
  building-wand-block-strict-1: 9
  building-wand-block-strict-2: 64
  building-wand-block-strict-3: 4096
  breaking-wand-1: 9
  breaking-wand-2: 64
  breaking-wand-3: 4096
```

## Usage

1. **Crafting Wands**: Craft various wands according to the recipes
2. **Using Building Wands**:
    - Hold the building wand and right-click on target blocks
    - The wand will automatically calculate the placement area based on the aimed face and adjacent same blocks
    - In survival mode, corresponding blocks will be consumed
3. **Using Breaking Wands**:
    - Hold the breaking wand and right-click on target blocks
    - The wand will automatically calculate the breaking area based on the aimed face and adjacent same blocks
    - In survival mode, corresponding blocks will be obtained
4. **Switching Axis Mode**:
    - When holding a wand, use the F key (swap off-hand items) to switch axis restriction mode
    - Supports X, Y, Z axis restrictions and unrestricted mode

## Permissions and Restrictions

- All wands support permission checks
- OP-only wands (4096 range) are restricted to administrators only
- Using building wands in creative mode does not consume blocks
- Projection display feature can enhance gameplay experience and can be disabled via configuration
- Server administrators can adjust the maximum number of blocks each wand type can modify through configuration

## Multi-language Support

The plugin supports multiple languages, including:
- Czech (cs_CZ)
- German (de_DE)
- English (en)
- Canadian English (en_CA)
- Shakespearan English (enws)
- Canadian French (fr_CA)
- French (fr_FR)
- Italian (it)
- Japanese (ja_JP)
- Korean (ko_KR)
- Russian (ru)
- Simplified Chinese (zh_CN)
- Traditional Chinese (zh_TW)

## Requirements

- Minecraft Paper server (1.21+)
- Java 21 or higher
- [PylonCore](https://github.com/pylonmc/pylon-core) prerequisite plugin

## Developer Information

- Author: balugaq
- Project URL: [https://github.com/balugaq/construction-wand](https://github.com/balugaq/construction-wand)
- Issue Tracker: [https://github.com/balugaq/construction-wand/issues](https://github.com/balugaq/construction-wand/issues)

## Notes

1. Large-range wands (especially 4096 blocks) may impact server performance. Please adjust range limits according to server configuration
2. Ensure you have sufficient permissions when using wands, otherwise operations will be cancelled
3. Ensure you have enough blocks in your inventory when using building wands
4. Server administrators can adjust the number of blocks players can modify when using wands through the `limit-blocks` setting in the configuration file
