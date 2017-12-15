package utility;

public enum Commands {
    COCCLANINFO("/cocclaninfo", "https://api.clashofclans.com/v1/clans?name=%23J0C9CPY", "https://api.clashofclans.com/v1/clans?name="),
    COCCLANDONATIONS("/cocclandonations", "https://api.clashofclans.com/v1/clans/", "https://api.clashofclans.com/v1/clans/%23J0C9CPY/members?limit=50"),
    CRCLANINFO("/crclaninfo","http://api.cr-api.com/clan/", "http://api.cr-api.com/clan/8YLPJY"),
    CRCLANROLES("/crclanroles", "http://api.cr-api.com/clan/", "http://api.cr-api.com/clan/8YLPJY"),
    CRCLANDONATIONS("/crclandonations", "http://api.cr-api.com/clan/", "http://api.cr-api.com/clan/8YLPJY"),
    CRCLANCHEST("/crclanchest", "http://api.cr-api.com/clan/", "http://api.cr-api.com/clan/8YLPJY"),
    TREINTIJDEN("/treintijden"),
    TREINSTORINGEN("/treinstoringen"),
    WEERHUIDIG("/weerhuidig"),
    WEERVOORSPELLING("/weervoorspelling"),
    HELP("/help"),
    HALLO("/hallo");

    private String command;
    private String editableURL;
    private String defaultURL;

    Commands(String command) {
        this.command = command;
        this.editableURL = null;
        this.defaultURL = null;
    }

    Commands(String command, String editableURL) {
        this.command = command;
        this.editableURL = editableURL;
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
