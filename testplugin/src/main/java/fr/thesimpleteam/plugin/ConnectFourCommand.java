/*
 * MIT License
 *
 * Copyright (c) 2022 minemobs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package fr.thesimpleteam.plugin;

import net.thesimpleteam.pluginapi.command.Command;
import net.thesimpleteam.pluginapi.command.CommandEvent;
import net.thesimpleteam.pluginapi.command.CommandInfo;
import net.thesimpleteam.pluginapi.plugins.Author;
import net.thesimpleteam.pluginapi.plugins.BasePlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CommandInfo(name = "connect", description = "Connect Four command", usage = "connect <userId of the other guy>", aliases = {"cf"})
public class ConnectFourCommand extends Command {

    private static final Map<String, Game> games = new HashMap<>();

    public static class Game {
        private final String player1;
        private final String player2;
        private final String channelID;
        private boolean turn = true;

        /**
         * 2d array of the board with the y-axis being first and the x-axis being second.
         * <br/>
         * 0 = empty
         * x = player 1
         * o = player 2
         */
        private final char[][] board;
        private int winner = -2;

        public Game(String player1, String player2, String channelID) {
            this.board = new char[6][7];
            this.player1 = player1;
            this.player2 = player2;
            this.channelID = channelID;
        }

        public String getPlayer1() {
            return player1;
        }

        public String getPlayer2() {
            return player2;
        }

        public char[][] getBoard() {
            return board;
        }

        public boolean isTheRightChannel(String channelID) {
            return this.channelID.equals(channelID);
        }

        public boolean checkIfLimit(int column) {
            return board[0][column] != 0;
        }

        public void turn(String player, int x) {
            for (int y = board.length - 1; y > 0; y--) {
                if (board[y][x] == 0) {
                    board[y][x] = player.equals(player1) ? 'X' : 'O';
                    break;
                }
            }
            if(checkIfWon()) {
                winner = player.equals(player1) ? 1 : 2;
                games.remove(player1, this);
                games.remove(player2, this);
                return;
            }
            turn = !turn;
        }

        private boolean checkIfWon() {
            char player = turn ? 'X' : 'O';
            //Horizontal check
            for (int x = 0; x < board[0].length - 3; x++) {
                for (char[] chars : board) {
                    if (chars[x] == player && chars[x + 1] == player && chars[x + 2] == player && chars[x + 3] == player) {
                        return true;
                    }
                }
            }
            //Vertical check
            for (int y = 0; y < board.length; y++) {
                for (int x = 0; x < board[y].length; x++) {
                    if (board[y][x] == player && board[y + 1][x] == player && board[y + 2][x] == player && board[y + 3][x] == player) {
                        return true;
                    }
                }
            }
            //Diagonal check
            //Ascending Diagonal Check
            for (int y = 0; y < board.length; y++) {
                for (int x = 0; x < board[y].length; x++) {
                    if (this.board[y][x] == player && this.board[y-1][x+1] == player && this.board[y-2][x+2] == player && this.board[y-3][x+3] == player) {
                        return true;
                    }
                }
            }
            //Descending Diagonal Check
            for (int y = 0; y < board.length; y++) {
                for (int x = 0; x < board[y].length; x++) {
                    if (board[y][x] == player && board[y - 1][x - 1] == player && board[y - 2][x - 2] == player && board[y - 3][x - 3] == player) {
                        return true;
                    }
                }
            }
            return false;
        }

        public String nicelyFormattedBoard() {
            StringBuilder sb = new StringBuilder();
            for (char[] chars : board) {
                for (char c : chars) {
                    sb.append(c == (char) 0 ? " " : ':' + c + ':');
                }
                sb.append("\n");
            }
            return sb.substring(0, sb.length() - 1);
        }

        public String getTurn() {
            return turn ? player1 : player2;
        }

        public String getWinner() {
            return winner == 0 ? player1 : winner == 1 ? player2 : winner == -1 ? "Nobody" : "Not finished";
        }
    }

    public ConnectFourCommand(BasePlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandEvent event) {
        List<String> args = event.getArgList();
        if(games.containsKey(event.getMessage().getAuthor().authorId())) {
            event.reply("You are already in a game!");
            return;
        }
        if(args.isEmpty()) {
            event.reply(event.getCommandInfo().usage());
            return;
        }
        System.out.println("Truc");
        Author user = getPlugin().getLoader().getUser(args.get(0));
        if(user == null) {
            event.reply("User not found!");
            return;
        }
        if(games.containsKey(user.authorId())) {
            event.reply("This user is already in a game!");
            return;
        }
        Game game = new Game(event.getMessage().getAuthor().authorId(), user.authorId(), event.getMessage().getChannelID());
        games.put(event.getMessage().getAuthor().authorId(), game);
        games.put(user.authorId(), game);
        event.reply("Game started between " + event.getMessage().getAuthor().username() + " and " + user.username() + "!");
        System.out.println("Game started between " + event.getMessage().getAuthor().username() + " and " + user.username() + "!");
        event.reply(game.nicelyFormattedBoard());
        System.out.println(game.nicelyFormattedBoard());
    }

    public static Game getGame(String playerID) {
        return games.get(playerID);
    }
}
