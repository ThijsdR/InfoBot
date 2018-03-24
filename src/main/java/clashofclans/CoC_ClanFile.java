package clashofclans;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Deze klasse bevat methode(s) die clangegevens kunnen exporteren naar bestanden
 */
public class CoC_ClanFile {

    /**
     * Deze methode haalt alle informatie op omtrent clanleden van de opgegeven clan.
     * Na het ophalen van deze data wordt de data geplaatst in een Excel bestand.
     *
     * @param urlString             String om de request naar toe te sturen
     * @param isPeriodicGenerated   Boolean of het bestand periodiek gegenereerd is
     * @return                      Een Excel bestand met alle informatie over clanleden
     */
    public static File getClanMembersFileXLSX(String urlString, boolean isPeriodicGenerated, String cocApiKey) {

        /* Bepaal het huidige tijdstip */
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        /* Bepaal het format voor de datum */
        SimpleDateFormat sdfTitle = new SimpleDateFormat("yyyy-MM-dd");

        /* Bepaal de naam van het bestand op basis of het een periodiek gegenereerd bestand is of niet */
        File clanOverviewFile = null;
        if (isPeriodicGenerated) {
            if (org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS) {
                clanOverviewFile = new File("clanoverview_logs/generated_reports/" + String.valueOf(sdfTitle.format(timestamp)) + "_clanlog.xlsx");
            } else if (org.apache.commons.lang3.SystemUtils.IS_OS_LINUX) {
                clanOverviewFile = new File("/home/thijs/Infobotfiles/Logs/Generated/" + String.valueOf(sdfTitle.format(timestamp)) + "_clanlog.xlsx");
            }
        } else {
            if (org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS) {
                clanOverviewFile = new File("clanoverview_logs/logs/" + String.valueOf(sdfTitle.format(timestamp)) + "_clanoverzicht.xlsx");
            } else if (org.apache.commons.lang3.SystemUtils.IS_OS_LINUX) {
                clanOverviewFile = new File("/home/thijs/Infobotfiles/Logs/Reports/" + String.valueOf(sdfTitle.format(timestamp)) + "_clanlog.xlsx");
            }
        }

        /* Maak een nieuw workbook en voeg daar een nieuwe sheet aan toe */
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Overzicht clan");

        /* Stuur aan de hand van de urlString een request naar de server en haal hier alle benodigde data uit */
        String returnJson = CoC_PROC.retrieveDataSupercellAPI(urlString, cocApiKey);
        JSONObject json = new JSONObject(returnJson);
        JSONArray jsonArray = json.getJSONArray("items");

        /* Pas de marges van de sheet aan */
        sheet.setMargin(Sheet.LeftMargin, 0.25);
        sheet.setMargin(Sheet.RightMargin, 0.25);
        sheet.setMargin(Sheet.TopMargin, 0.5);
        sheet.setMargin(Sheet.BottomMargin, 0.5);

        /* Stel de voetteksten van de sheet in */
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        sheet.getFooter().setRight(String.valueOf(sdf.format(timestamp)));
        sheet.getFooter().setLeft("Clanoverzicht");

        /* Lijst om alle spelers in op te slaan */
        List<CoC_PlayerContainer> playersList = new ArrayList<>();

        /* Bepaal het format om de ratio af te ronden op twee decimalen */
        DecimalFormat format = new DecimalFormat(".##", new DecimalFormatSymbols(Locale.US));

        /* Voor ieder jsonobject in de array met items */
        for (int i = 0; i < jsonArray.length(); i++) {

            /* Bepaal de ratio van een speler en rondt deze af op twee decimalen.
               Als een speler niks heeft ontvangen is de ratio gelijk aan wat gedoneerd is. */
            double ratio;
            if (jsonArray.getJSONObject(i).getInt("donationsReceived") == 0) {
                ratio = jsonArray.getJSONObject(i).getInt("donations");
            } else {
                ratio = jsonArray.getJSONObject(i).getDouble("donations") / jsonArray.getJSONObject(i).getDouble("donationsReceived");

                try {
                    ratio = Double.valueOf(format.format(ratio));
                } catch (NumberFormatException nex) {
                    nex.printStackTrace();
                }
            }

            /* Voeg alle gevonden spelers toe aan de lijst */
            playersList.add(new CoC_PlayerContainer(jsonArray.getJSONObject(i).getInt("clanRank"),
                    jsonArray.getJSONObject(i).getString("name"),
                    jsonArray.getJSONObject(i).getString("tag"),
                    jsonArray.getJSONObject(i).getInt("expLevel"),
                    jsonArray.getJSONObject(i).getInt("trophies"),
                    jsonArray.getJSONObject(i).getString("role"),
                    jsonArray.getJSONObject(i).getInt("donations"),
                    jsonArray.getJSONObject(i).getInt("donationsReceived"),
                    ratio)
            );
        }

        /* Stijl van de bovenste rij */
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);

        /* Kleur van de bovenste rij */
        Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);

        /* De opmaak voor de bovenste rij */
        int rowNum = 1;
        Row descRow = sheet.createRow(0);

        Cell posCell = descRow.createCell(0);
        posCell.setCellValue("Rank");
        posCell.setCellStyle(style);

        Cell nameCell = descRow.createCell(1);
        nameCell.setCellValue("Name");
        nameCell.setCellStyle(style);

        Cell tagCell = descRow.createCell(2);
        tagCell.setCellValue("Player tag");
        tagCell.setCellStyle(style);

        Cell levelCell = descRow.createCell(3);
        levelCell.setCellValue("Exp level");
        levelCell.setCellStyle(style);

        Cell trophyCell = descRow.createCell(4);
        trophyCell.setCellValue("Trophies");
        trophyCell.setCellStyle(style);

        Cell roleCell = descRow.createCell(5);
        roleCell.setCellValue("Role");
        roleCell.setCellStyle(style);

        Cell donateCell = descRow.createCell(6);
        donateCell.setCellValue("Donated");
        donateCell.setCellStyle(style);

        Cell receiveCell = descRow.createCell(7);
        receiveCell.setCellValue("Received");
        receiveCell.setCellStyle(style);

        Cell ratioCell = descRow.createCell(8);
        ratioCell.setCellValue("Ratio");
        ratioCell.setCellStyle(style);

        /* Stijl voor de oneven rijen met een getalwaarde */
        CellStyle cellStyleOdd = workbook.createCellStyle();
        cellStyleOdd.setAlignment(HorizontalAlignment.CENTER);

        /* Stijl voor de even rijen met een getalwaarde */
        CellStyle cellStyleEven = workbook.createCellStyle();
        cellStyleEven.setAlignment(HorizontalAlignment.CENTER);
        cellStyleEven.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        cellStyleEven.setFillPattern(CellStyle.SOLID_FOREGROUND);

        /* Stijl voor de rijen met een stringwaarde */
        CellStyle cellStyleString = workbook.createCellStyle();
        cellStyleString.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        cellStyleString.setFillPattern(CellStyle.SOLID_FOREGROUND);

        /* Loop door alle spelers in de lijst */
        for (CoC_PlayerContainer player : playersList) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;

            /* Ga alle velden langs die een speler heeft en voeg deze toe aan een rij in de File */
            try {
                for (Field field : player.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    Class type = field.getType();
                    Object obj = field.get(player);

                    Cell cell = row.createCell(colNum++);
                    if (type == String.class) {
                        if (cell.getRow().getRowNum() % 2 == 0) {
                            cell.setCellStyle(cellStyleString);
                            cell.setCellValue((String) obj);
                        } else {
                            cell.setCellValue((String) obj);
                        }
                    } else if (type == int.class) {
                        if (cell.getRow().getRowNum() % 2 == 0) {
                            cell.setCellStyle(cellStyleEven);
                            cell.setCellValue((Integer) obj);
                        } else {
                            cell.setCellStyle(cellStyleOdd);
                            cell.setCellValue((Integer) obj);
                        }
                    } else if (type == double.class) {
                        if (cell.getRow().getRowNum() % 2 == 0) {
                            cell.setCellStyle(cellStyleEven);
                            cell.setCellValue((Double) obj);
                        } else {
                            cell.setCellStyle(cellStyleOdd);
                            cell.setCellValue((Double) obj);
                        }
                    }

                    sheet.autoSizeColumn(colNum);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        /* Exporteer de gemaakte file */
        try {
            assert clanOverviewFile != null;
            FileOutputStream outputStream = new FileOutputStream(clanOverviewFile);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return clanOverviewFile;
    }
}
