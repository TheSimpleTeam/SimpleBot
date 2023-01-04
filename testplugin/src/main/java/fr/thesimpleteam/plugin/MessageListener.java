package fr.thesimpleteam.plugin;

import net.thesimpleteam.pluginapi.event.EventHandler;
import net.thesimpleteam.pluginapi.event.Listener;
import net.thesimpleteam.pluginapi.event.MessageReceiveEvent;

public class MessageListener implements Listener {

    @EventHandler
    public void onMessage(MessageReceiveEvent event) {
        String[] args = event.getMessage().getMessageContent().split("\\s+");
        if(args.length == 0 || !args[0].chars().mapToObj(this::toChar).allMatch(Character::isDigit)) return;
        int number = Integer.parseInt(args[0]);
        if(number < 0 || number > 6) return;
        ConnectFourCommand.Game game = ConnectFourCommand.getGame(event.getMessage().getChannelID());
        if(game == null || !game.isTheRightChannel(event.getMessage().getChannelID())) return;
        if(!game.getTurn().equals(event.getMessage().getAuthor().authorId())) return;
        if(game.checkIfLimit(number)) {
            event.reply("<@" + event.getMessage().getAuthor().authorId() + "> This column is full!");
            return;
        }
        game.turn(event.getMessage().getAuthor().authorId(), number);
        event.reply(game.nicelyFormattedBoard());
    }

    private char toChar(int i) {
        return (char) i;
    }
}