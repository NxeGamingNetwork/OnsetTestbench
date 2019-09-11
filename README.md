# OnsetTestbench
[WIP] An Onset script emulator to kind of test scripts before the release of the game.

## Usage
Just run it (`java -jar OnsetTestbench-1.0.jar`) in your server folder.
Currently most of the functions return a static value so your scripts might crash
because of logical errors even if they are totally fine.
We are working on getting most parts of the api implemented, but we probably won't implement anything.
If you need anything to be implemented just open an issue and we'll see what we can do for you.

## Config
The config can be used to set a custom server directory and enable the web ui emulator.
It has to be called bench.json.
```json
{
  "server_folder": ".",
  "ui_enabled": false,
  "ui_port": 2255,
  "clients": [
    {
      "steam_id": "76561198114435069",
      "name": "Test Client"
    }
  ]
}
```

## Web UI Emulator Mode
When the testbench is started in emulator mode it won't start directly. Instead you have to open a link which will be used to see the web ui's of the client. As soon as you open it the server and one client gets started. As of now the emulator only emulates the web ui's for the first client that joins.

## Implemented functions
Even though every function that is documented in the Onset wiki exists, not all of them are implemented. Most of them just return a static value that might even not be of the correct data type. The following function were fully implemented and can be used safely.
### Server
#### Onset
- GetPackageName
- ServerExit
- AddEvent
- CallEvent
- AddRemoteEvent
- CallRemoteEvent
- GetAllPlayers
- GetPlayerName
- GetPlayerSteamId
#### MariaDB
- mariadb_connect
- mariadb_close
- mariadb_prepare
- mariadb_query
- mariadb_async_query
- mariadb_await_query
- mariadb_set_active_result
- mariadb_delete_result
- mariadb_get_row_count
- mariadb_get_field_count
- mariadb_get_field_name
- mariadb_get_value_index
- mariadb_get_value_index_int
- mariadb_get_value_index_float
- mariadb_get_value_name
- mariadb_get_value_name_int
- mariadb_get_value_name_float
- mariadb_get_insert_id
### Client
- GetPackageName
- AddEvent
- CallEvent
- AddRemoteEvent
- CallRemoteEvent
- GetPlayerId
- CreateWebUI
- DestroyWebUI
- SetWebSize
- SetWebLocation
- LoadWebFile
- ExecuteWebJS
