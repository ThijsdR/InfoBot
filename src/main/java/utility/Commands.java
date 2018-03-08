package utility;

public enum Commands {
    COCCLANINFO("/cocclaninfo","https://api.clashofclans.com/v1/clans?name=","https://api.clashofclans.com/v1/clans?name=%23J0C9CPY"),
    COCCLANDONATIONS("/cocclandonaties", "https://api.clashofclans.com/v1/clans/", "https://api.clashofclans.com/v1/clans/%23J0C9CPY/members"),
    COCCLANMEMBER("/cocclanmembers", "https://api.clashofclans.com/v1/clans/", "https://api.clashofclans.com/v1/clans/%23J0C9CPY/members"),
    COCCLANMEMBERINFO("/cocclanmember","https://api.clashofclans.com/v1/clans/", "https://api.clashofclans.com/v1/clans/%23J0C9CPY/members"),
    COCCLANMEMBERSTOFILE("/cocclanmembersfile","https://api.clashofclans.com/v1/clans/", "https://api.clashofclans.com/v1/clans/%23J0C9CPY/members"),
    COCBLACKLISTADD("/cocblacklistadd"),
    COCBLACKLISTREMOVE("/cocblacklistremove"),
    COCBLACKLISTVIEW("/cocblacklistview"),
    CRCLANINFO("/crclaninfo","http://api.cr-api.com/clan/", "http://api.cr-api.com/clan/8ylpjy"),
    CRCLANMEMBERS("/crclanmembers","http://api.cr-api.com/clan/", "http://api.cr-api.com/clan/8ylpjy"),
    CRCLANROLES("/crclanroles", "http://api.cr-api.com/clan/", "http://api.cr-api.com/clan/8ylpjy"),
    CRCLANDONATIONS("/crclandonations", "http://api.cr-api.com/clan/", "http://api.cr-api.com/clan/8ylpjy"),
    CRCLANCHEST("/crclanchest", "http://api.cr-api.com/clan/", "http://api.cr-api.com/clan/8ylpjy"),
    TREINTIJDEN("/treintijden"),
    TREINSTORINGEN("/treinstoringen"),
    TREINWERKZAAMHEDEN("/treinwerkzaamheden"),
    WEERHUIDIG("/weerhuidig"),
    WEERVOORSPELLING("/weervoorspelling"),
    CHAT("/chat"),
    HELP("/help"),
    JOKE("/joke"),
    HALLO("/hallo"),
    MODE("/5926");

    private final String command;
    private final String editableURL;
    private final String defaultURL;

    Commands(String command) {
        this.command = command;
        this.editableURL = null;
        this.defaultURL = null;
    }

    Commands(String command, String editableURL, String defaultURL) {
        this.command = command;
        this.editableURL = editableURL;
        this.defaultURL = defaultURL;
    }

    public String getCommand() {
        return command;
    }

    public String getEditableURL() {
        return editableURL;
    }

    public String getDefaultURL() {
        return defaultURL;
    }
}
