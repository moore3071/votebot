# Votebot
## An IRC bot for voting.

What does it do? It's counts votes. That is all.

## Commands
### Admin-Only
- `.clear`: Clears all votes
- `.whitelist <nick>`: Allows the user with the given nick to vote
- `.join <channel>`: The bot will join `channel`
- `.part <channel>`: The bot will leave `channel`
- `.die`: The bot dies
- `.vote-as [nick] [vote]`: Vote as another user
- `.rm-vote-as [nick]`: Removes the given user's vote

### Everybody
- `.votes`: Shows the votes
- `.vote <item>`: Adds a vote for `item`
- `.rmvote`: Deletes the user's vote
- `.count:` Says how many votes there are
- `.whodunnit [item]`: Says who voted for that item
- `.whosvoted`: Lists all the people who have voted
- `.whathaveyoudone [nick]`: Says what the user voted for

### "Responses" (`<nick>: <message>`)
- "hello": Replies "Hello, <sender>"
- "beep": Replies "boop"
- "help": Lists summary of commands

## Setup
To get Votebot running, you're going to need to set up a PostgreSQL database
for it.

`varchar` fields should be at least 30 characters long. Any longer than 30
characters will not hurt, but additional spaces will be wasted.

It should:
- Have two tables, `users` and `votes`
  - `users` should have two fields:
    - `id` field (integer)
    - `nick` field (varchar)
  - `votes` should have three fields:
    - `id` (integer)
    - `users_id` (integer)
    - `item` (varchar)
    - `old` (boolean, default to false)

## Configuration
The bot requires a file named `settings.json`. I have included an example file called `settings-example.json`.

- `"bot_nick"`: Bot's nick
- `"bot_pass"`: Bot's password
- `"server"`: IRC server to connect to
- `"port"`: Port to connect to the IRC server on
- `"master"`: The nick of the user to consider "master"
- `"channels"`: An array of channels to join
- `"db-name"`: PostgreSQL database name
- `"db-user"`: User with access to the database
- `"db-pass"`: Password for user to access the database
