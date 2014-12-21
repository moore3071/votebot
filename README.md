# Votebot
## An IRC bot for voting.

What does it do? It's counts votes. That is all.

## Commands
### Admin-Only
- `.clear`: Clears all votes
- `.whitelist <nick>`: Allows the user with the given nick to vote
- `.join <channel>`: The bot will join `channel`
- `.leave <channel>`: The bot will leave `channel`
- `.die`: The bot exits

### Everybody
- `.votes`: Shows the votes
- `.vote <item>`: Adds a vote for `item`
- `.rm-vote`: Delete's the user's vote

### "Responses" (`<nick>: <message>`)
- "hello": Bot replies "Hello, <sender>"
- "beep": Bot replies "boop"
- "help": Bot spits out summary of commands

## Configuration
The bot requires a file named `settings.json`. I have included an example file called `settings-example.json`.

- `"bot_nick"`: Bot's nick
- `"bot_pass"`: Bot's password
- `"server"`: IRC server to connect to
- `"port"`: Port to connect to the IRC server on
- `"master"`: The nick of the user to consider "master"
- `"channels"`: An array of channels to join
- `"db-name"`: Postgres database name
- `"db-user"`: User with access to the database
- `"db-pass"`: Password for user to access the database
