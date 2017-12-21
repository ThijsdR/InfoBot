package botCommands.clashofclans.clans;

import botCommands.clashofclans.CoC_PROC;
import botCommands.clashofclans.CoC_PlayerContainer;
import com.vdurmont.emoji.EmojiParser;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CoC_Clan {

    /**
     *
     * @param urlString
     * @return
     */
    public static String getClanInfo(String urlString) {
        JSONObject json = new JSONObject(CoC_PROC.retrieveDataSupercellAPI(urlString));
        JSONArray jsonArray = json.getJSONArray("items");

        StringBuilder botResponse = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            botResponse.append("Clan: ").append(jsonArray.getJSONObject(i).getString("name")).append("\n");
            botResponse.append("Level: ").append(jsonArray.getJSONObject(i).getInt("clanLevel")).append("\n");
            botResponse.append("Clan tag: ").append(jsonArray.getJSONObject(i).getString("tag")).append("\n");
            botResponse.append(EmojiParser.parseToUnicode("Clanpoints: :trophy:")).append(jsonArray.getJSONObject(i).getInt("clanPoints")).append("\n");
            botResponse.append("Clan type: ").append(jsonArray.getJSONObject(i).getString("type")).append("\n");
            botResponse.append(EmojiParser.parseToUnicode("Members: :family:")).append(jsonArray.getJSONObject(i).getInt("members")).append("\n");
            botResponse.append("Country: ").append(jsonArray.getJSONObject(i).getJSONObject("location").getString("name")).append("\n");
            botResponse.append("-~-~-~-~-~-~-~-~\n");
        }

        return String.valueOf(botResponse);
    }

    /**
     *
     * @param urlString
     * @return
     */
    public static String getClanDonaties(String urlString) {
        JSONObject json = new JSONObject(CoC_PROC.retrieveDataSupercellAPI(urlString));
        JSONArray jsonArray = json.getJSONArray("items");

        StringBuilder botResponse = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            botResponse.append("-").append(jsonArray.getJSONObject(i).getInt("clanRank")).append("-\n");
            botResponse.append("Name: ").append(jsonArray.getJSONObject(i).getString("name")).append("\n");
            botResponse.append("Tag: ").append(jsonArray.getJSONObject(i).getString("tag")).append("\n");
            botResponse.append(EmojiParser.parseToUnicode("Donations: :arrow_forward:")).append(jsonArray.getJSONObject(i).getInt("donations")).append("\n");
            botResponse.append(EmojiParser.parseToUnicode("Received: :arrow_backward:")).append(jsonArray.getJSONObject(i).getInt("donationsReceived")).append("\n");
            botResponse.append("-~-~-~-~-~-~-~-~\n");
        }

        return String.valueOf(botResponse);
    }

    /**
     *
     * @param urlString
     * @return
     */
    public static String getClanMembers(String urlString) {
        JSONObject json = new JSONObject(CoC_PROC.retrieveDataSupercellAPI(urlString));
        JSONArray jsonArray = json.getJSONArray("items");

        StringBuilder botResponse = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            botResponse.append("-").append(jsonArray.getJSONObject(i).getInt("clanRank")).append("-\n");
            botResponse.append("Name: ").append(jsonArray.getJSONObject(i).getString("name")).append("\n");
            botResponse.append("Tag: ").append(jsonArray.getJSONObject(i).getString("tag")).append("\n");
            botResponse.append("Level: ").append(jsonArray.getJSONObject(i).getInt("expLevel")).append("\n");
            botResponse.append(EmojiParser.parseToUnicode("Trophies: :trophy:")).append(jsonArray.getJSONObject(i).getInt("trophies")).append("\n");
            botResponse.append("Role: ").append(jsonArray.getJSONObject(i).getString("role")).append("\n");
            botResponse.append("-~-~-~-~-~-~-~-~\n");
        }

        return String.valueOf(botResponse);
    }

    /**
     *
     * @param urlString
     * @return
     */
    public static File getClanMembersFileXLSX(String urlString, boolean isPeriodicGenerated) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdfTitle = new SimpleDateFormat("yyyy-MM-dd");

        File clanOverviewFile;
        if (isPeriodicGenerated) {
            clanOverviewFile = new File("clanoverview_logs/generated_reports/" + String.valueOf(sdfTitle.format(timestamp)) + "_clanlog.xlsx");
        } else {
            clanOverviewFile = new File("clanoverview_logs/logs/" + String.valueOf(sdfTitle.format(timestamp)) + "_clanoverzicht.xlsx");
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Overzicht clan");

        JSONObject json = new JSONObject(CoC_PROC.retrieveDataSupercellAPI(urlString));
        JSONArray jsonArray = json.getJSONArray("items");

        sheet.setMargin(Sheet.LeftMargin, 0.25);
        sheet.setMargin(Sheet.RightMargin, 0.25);
        sheet.setMargin(Sheet.TopMargin, 0.5);
        sheet.setMargin(Sheet.BottomMargin, 0.5);

        sheet.getFooter().setLeft("Clanoverzicht");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        sheet.getFooter().setRight(String.valueOf(sdf.format(timestamp)));

        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);

        Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);

        List<CoC_PlayerContainer> playersList = new ArrayList<>();

        DecimalFormat format = new DecimalFormat(".##");

        for (int i = 0; i < jsonArray.length(); i++) {
            double ratio;
            if (jsonArray.getJSONObject(i).getInt("donationsReceived") == 0) {
                ratio = jsonArray.getJSONObject(i).getInt("donations");
            } else {
                ratio = jsonArray.getJSONObject(i).getDouble("donations") / jsonArray.getJSONObject(i).getDouble("donationsReceived");
                ratio = Double.valueOf(format.format(ratio));
            }

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

        CellStyle cellStyleOdd = workbook.createCellStyle();
        cellStyleOdd.setAlignment(HorizontalAlignment.CENTER);

        CellStyle cellStyleEven = workbook.createCellStyle();
        cellStyleEven.setAlignment(HorizontalAlignment.CENTER);
        cellStyleEven.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        cellStyleEven.setFillPattern(CellStyle.SOLID_FOREGROUND);

        CellStyle cellStyleString = workbook.createCellStyle();
        cellStyleString.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        cellStyleString.setFillPattern(CellStyle.SOLID_FOREGROUND);

        for (CoC_PlayerContainer player : playersList) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;

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

        try {
            FileOutputStream outputStream = new FileOutputStream(clanOverviewFile);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return clanOverviewFile;
    }
}
