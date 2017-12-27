package utility;

import clashofclans.CoC_ClanFile;

import java.util.TimerTask;

public class ReportGenerator extends TimerTask {
    @Override
    public void run() {
        CoC_ClanFile.getClanMembersFileXLSX(Commands.COCCLANMEMBERSTOFILE.getDefaultURL(), true);
    }
}
