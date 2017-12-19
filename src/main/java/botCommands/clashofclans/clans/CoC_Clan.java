package botCommands.clashofclans.clans;

import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import botCommands.clashofclans.CoC_PROC;
import botCommands.clashofclans.CoC_PlayerContainer;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
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

        StringBuilder botResponse = new StringBuilder("Answer from Inf0_B0t:\n");
        botResponse.append("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=\n");

        for (int i = 0; i < jsonArray.length(); i++) {
            botResponse.append("\n");
            botResponse.append("Clan: ").append(jsonArray.getJSONObject(i).getString("name")).append("\n");
            botResponse.append("Level: ").append(jsonArray.getJSONObject(i).getInt("clanLevel")).append("\n");
            botResponse.append("Clan tag: ").append(jsonArray.getJSONObject(i).getString("tag")).append("\n");
            botResponse.append(EmojiParser.parseToUnicode("Clanpoints: :trophy:")).append(jsonArray.getJSONObject(i).getInt("clanPoints")).append("\n");
            botResponse.append("Clan type: ").append(jsonArray.getJSONObject(i).getString("type")).append("\n");
            botResponse.append(EmojiParser.parseToUnicode("Members: :family:")).append(jsonArray.getJSONObject(i).getInt("members")).append("\n");
            botResponse.append("Country: ").append(jsonArray.getJSONObject(i).getJSONObject("location").getString("name")).append("\n");
        }

        return botResponse.toString();
    }

    /**
     *
     * @param urlString
     * @return
     */
    public static String getClanDonaties(String urlString) {
        JSONObject json = new JSONObject(CoC_PROC.retrieveDataSupercellAPI(urlString));
        JSONArray jsonArray = json.getJSONArray("items");

        StringBuilder botResponse = new StringBuilder("Answer from Inf0_B0t:\n");
        botResponse.append("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=\n");

        for (int i = 0; i < jsonArray.length(); i++) {
            botResponse.append("\n");
            botResponse.append("-").append(jsonArray.getJSONObject(i).getInt("clanRank")).append("-\n");
            botResponse.append("Name: ").append(jsonArray.getJSONObject(i).getString("name")).append("\n");
            botResponse.append("Tag: ").append(jsonArray.getJSONObject(i).getString("tag")).append("\n");
            botResponse.append(EmojiParser.parseToUnicode("Donations: :arrow_forward:")).append(jsonArray.getJSONObject(i).getInt("donations")).append("\n");
            botResponse.append(EmojiParser.parseToUnicode("Received: :arrow_backward:")).append(jsonArray.getJSONObject(i).getInt("donationsReceived")).append("\n");
        }

        return botResponse.toString();
    }

    /**
     *
     * @param urlString
     * @return
     */
    public static String getClanMembers(String urlString) {
        JSONObject json = new JSONObject(CoC_PROC.retrieveDataSupercellAPI(urlString));
        JSONArray jsonArray = json.getJSONArray("items");

        StringBuilder botResponse = new StringBuilder("Answer from Inf0_B0t:\n");
        botResponse.append("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=\n");

        for (int i = 0; i < jsonArray.length(); i++) {
            botResponse.append("\n");
            botResponse.append("-").append(jsonArray.getJSONObject(i).getInt("clanRank")).append("-\n");
            botResponse.append("Name: ").append(jsonArray.getJSONObject(i).getString("name")).append("\n");
            botResponse.append("Tag: ").append(jsonArray.getJSONObject(i).getString("tag")).append("\n");
            botResponse.append("Level: ").append(jsonArray.getJSONObject(i).getInt("expLevel")).append("\n");
            botResponse.append(EmojiParser.parseToUnicode("Trophies: :trophy:")).append(jsonArray.getJSONObject(i).getInt("trophies")).append("\n");
            botResponse.append("Role: ").append(jsonArray.getJSONObject(i).getString("role")).append("\n");
        }

        return botResponse.toString();
    }

    /**
     *
     * @param urlString
     * @return
     */
    public static File getClanMembersFileXLSX(String urlString) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdfTitle = new SimpleDateFormat("yyyy-MM-dd");

        File clanOverviewFile = new File("clanoverview_logs/" + String.valueOf(sdfTitle.format(timestamp)) + "_clanoverzicht.xlsx");

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

        for (int i = 0; i < jsonArray.length(); i++) {
            playersList.add(new CoC_PlayerContainer(jsonArray.getJSONObject(i).getInt("clanRank"),
                        jsonArray.getJSONObject(i).getString("name"),
                        jsonArray.getJSONObject(i).getString("tag"),
                        jsonArray.getJSONObject(i).getInt("expLevel"),
                        jsonArray.getJSONObject(i).getInt("trophies"),
                        jsonArray.getJSONObject(i).getString("role"),
                        jsonArray.getJSONObject(i).getInt("donations"),
                        jsonArray.getJSONObject(i).getInt("donationsReceived"))
            );
        }

        int rowNum = 1;

        Row descRow = sheet.createRow(0);

        Cell posCell = descRow.createCell(0);
        posCell.setCellValue("Position");
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
        trophyCell.setCellValue("Trophycount");
        trophyCell.setCellStyle(style);

        Cell roleCell = descRow.createCell(5);
        roleCell.setCellValue("Role");
        roleCell.setCellStyle(style);

        Cell donateCell = descRow.createCell(6);
        donateCell.setCellValue("Troops donated");
        donateCell.setCellStyle(style);

        Cell receiveCell = descRow.createCell(7);
        receiveCell.setCellValue("Troops received");
        receiveCell.setCellStyle(style);

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
                        cell.setCellValue((String) obj);
                    } else if (type == int.class) {
                        cell.setCellValue((Integer) obj);
                    }
                    sheet.autoSizeColumn(colNum);

                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        for (int i = 1; i < sheet.getLastRowNum(); i += 2) {
            CoC_PROC.makeRowColor(workbook, sheet.getRow(i));
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
