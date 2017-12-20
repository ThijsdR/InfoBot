package utility;

import botCommands.clashofclans.clans.CoC_Clan;

import java.util.TimerTask;

public class ReportGenerator extends TimerTask {
    @Override
    public void run() {
        CoC_Clan.getClanMembersFileXLSX(Commands.COCCLANMEMBERSTOFILE.getDefaultURL(), true);
    }
}
