package utility;

public enum Commands
{
    COCBLACKLISTADD("/blacklistadd"),
    COCBLACKLISTREMOVE("/blacklistremove"),
    COCBLACKLISTVIEW("/blacklistview"),
    COCBLACKLISTCHECK("/blacklistcheck"),
    COCWAROPPONENT("/war", "https://api.clashofclans.com/v1/clans/%23J0C9CPY/currentwar"),
    COCENDTIMEWAR("/time", "https://api.clashofclans.com/v1/clans/%23J0C9CPY/currentwar"),
    COCBASSIEAWARD("/bassie"),
    COCWARSUBSCRIBE("/subscribe"),
    COCWARUNSUBSCRIBE("/unsubscribe"),
    TREINTIJDEN("/treintijden"),
    TREINSTORINGEN("/treinstoringen"),
    CHAT("/chat"),
    HELP("/help"),
    HALLO("/hallo"),
    MODE("/5926"),
    LOG("/logoutput");

    private final String command;
    private final String editableURL;
    private final String defaultURL;

    Commands(String command)
    {
        this.command = command;
        this.editableURL = null;
        this.defaultURL = null;
    }

    Commands(String command, String defaultURL)
    {
        this.command = command;
        this.editableURL = null;
        this.defaultURL = defaultURL;
    }

    Commands(String command, String editableURL, String defaultURL)
    {
        this.command = command;
        this.editableURL = editableURL;
        this.defaultURL = defaultURL;
    }

    public String getCommand()
    {
        return command;
    }

    public String getEditableURL()
    {
        return editableURL;
    }

    public String getDefaultURL()
    {
        return defaultURL;
    }
}
