package roda;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utility.TextFormatting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class R_Boete {
    public static String getBoeteLijst() {
        StringBuilder boeteLijst = new StringBuilder(TextFormatting.toBold("Boeteoverzicht\n\n"));

        ArrayList<R_BoeteSpecificatieContainer> boetes = new ArrayList<>();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/infobotdb", "root", FileUtils.readFileToString(new File("/home/thijs/Infobotfiles/dbpass.txt")));
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM boetespecificatieroda");

            while (rs.next()) {
                boetes.add(new R_BoeteSpecificatieContainer(
                        rs.getInt("Code"),
                        rs.getString("Omschrijving"),
                        rs.getDouble("BoeteBedrag"),
                        rs.getString("Kratje").equals("Ja")
                ));
            }

            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        DecimalFormat format = new DecimalFormat("#.00", new DecimalFormatSymbols(Locale.getDefault()));

        for (R_BoeteSpecificatieContainer boete : boetes) {
            boeteLijst.append(TextFormatting.toCode("Code: " )).append(boete.getCode()).append("\n");
            boeteLijst.append(TextFormatting.toCode("Omschrijving: ")).append(boete.getOmschrijving()).append("\n");
            boeteLijst.append(TextFormatting.toCode("Bedrag: ")).append("€").append(format.format(boete.getBedrag())).append("\n");
            boeteLijst.append(TextFormatting.toCode("Kratje: ")).append(boete.isKratje() ? "Ja" : "Nee").append("\n\n");
        }

        return String.valueOf(boeteLijst);
    }

    public static String voegBoeteToe(String[] boeteArray) {
        StringBuilder boeteBuilder = new StringBuilder();

        ArrayList<R_SpelerContainer> spelersLijst = new ArrayList<>();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/infobotdb", "root", FileUtils.readFileToString(new File("/home/thijs/Infobotfiles/dbpass.txt")));
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Id, Naam FROM boetepotroda");

            while (rs.next()) {
                spelersLijst.add(new R_SpelerContainer(
                        rs.getInt("Id"),
                        rs.getString("Naam")
                ));
            }

            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
            boeteBuilder.append(TextFormatting.toItalic("Ik kan de data niet ophalen uit de database..."));
        }

        R_BoeteContainer boeteContainer;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/infobotdb", "root", FileUtils.readFileToString(new File("/home/thijs/Infobotfiles/dbpass.txt")));
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM boeteoverzichtroda");

            ArrayList<Integer> boeteIds = new ArrayList<>();

            while (rs.next()) {
                boeteIds.add(rs.getInt("BoeteId"));
            }

            boeteIds.sort(Collections.reverseOrder());

            R_SpelerContainer beboeteSpeler = null;
            for (R_SpelerContainer speler : spelersLijst) {
                if (speler.getNaam().equals(boeteArray[0])) {
                    beboeteSpeler = speler;
                }
            }

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date huidigeDatum = new java.util.Date();

            assert beboeteSpeler != null;
            boeteContainer = new R_BoeteContainer(boeteIds.get(0) + 1, beboeteSpeler.getId(), false, boeteArray[1], df.format(huidigeDatum));

            rs.close();

            stmt = con.createStatement();
            stmt.execute("INSERT INTO boeteoverzichtroda (BoeteId, SpelerId, Betaald, BoeteCode, Datum) VALUES ('" +
                    boeteContainer.getBoeteId() + "','" + boeteContainer.getSpelerId() + "','" + (boeteContainer.isBetaald() ? "Ja" : "Nee")
                    + "','" + boeteContainer.getBoeteCode() + "','" + boeteContainer.getDatum() + "')");

            stmt = con.createStatement();
            ResultSet rs2 = stmt.executeQuery("SELECT boeteBedrag FROM boetespecificatieroda WHERE Code = " + boeteContainer.getBoeteCode());
            String boeteBedrag = null;
            DecimalFormat format = new DecimalFormat("#.00", new DecimalFormatSymbols(Locale.getDefault()));

            while (rs2.next()) {
                boeteBedrag = format.format(rs2.getDouble("boeteBedrag"));
            }

            stmt.close();
            con.close();

            updateBoeteLijst();
            boeteBuilder.append(TextFormatting.toItalic(beboeteSpeler.getNaam() + " heeft een boete gekregen van €" + boeteBedrag));

        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
            boeteBuilder.append(TextFormatting.toItalic("Er is iets fout gegaan..."));
        }

        return String.valueOf(boeteBuilder);
    }

    //ToDo: Uitbreiden met datumvak
    public static File getAlleBoetes() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdfTitle = new SimpleDateFormat("yyyy-MM-dd");
        File boeteFile = new File("/home/thijs/Infobotfiles/" + String.valueOf(sdfTitle.format(timestamp)) + "_boetelijst.xlsx");

        ArrayList<R_BoeteContainer> boeteLijst = new ArrayList<>();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/infobotdb", "root", FileUtils.readFileToString(new File("/home/thijs/Infobotfiles/dbpass.txt")));
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT boeteoverzichtroda.BoeteId, boetepotroda.Naam, boeteoverzichtroda.Betaald, boetespecificatieroda.Omschrijving, boetespecificatieroda.BoeteBedrag, boeteoverzichtroda.Datum " +
                    "FROM boeteoverzichtroda " +
                    "INNER JOIN boetepotroda ON boeteoverzichtroda.SpelerId = boetepotroda.Id " +
                    "INNER JOIN boetespecificatieroda ON boeteoverzichtroda.Boetecode = boetespecificatieroda.Code");

            while (rs.next()) {
                boeteLijst.add(new R_BoeteContainer(rs.getInt("BoeteId"),
                        rs.getString("Naam"),
                        rs.getString("Betaald").equals("Ja"),
                        rs.getString("Omschrijving"),
                        rs.getDouble("BoeteBedrag"),
                        rs.getString("Datum")));
            }

            boeteLijst.sort(Comparator.comparing(R_BoeteContainer::getBoeteId));

            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        /* Maak een nieuw workbook en voeg daar een nieuwe sheet aan toe */
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Overzicht boetes");

        /* Stel de voetteksten van de sheet in */
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        sheet.getFooter().setRight(String.valueOf(sdf.format(timestamp)));
        sheet.getFooter().setLeft("Boeteoverzicht");

        DecimalFormat format = new DecimalFormat("#.00", new DecimalFormatSymbols(Locale.getDefault()));

        /* Stijl van de bovenste rij */
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);

        CellStyle doubleStyle = workbook.createCellStyle();
        doubleStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));

        /* Kleur van de bovenste rij */
        Font font = workbook.createFont();
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setBold(true);
        style.setFont(font);

        /* De opmaak voor de bovenste rij */
        int rowNum = 1;
        Row descRow = sheet.createRow(0);

        Cell boeteIdCell = descRow.createCell(0);
        boeteIdCell.setCellValue("Boete ID");
        boeteIdCell.setCellStyle(style);

        Cell datumCell = descRow.createCell(1);
        datumCell.setCellValue("Datum");
        datumCell.setCellStyle(style);

        Cell naamCell = descRow.createCell(2);
        naamCell.setCellValue("Naam");
        naamCell.setCellStyle(style);

        Cell betaaldCell = descRow.createCell(3);
        betaaldCell.setCellValue("Betaald");
        betaaldCell.setCellStyle(style);

        Cell bedragCell = descRow.createCell(4);
        bedragCell.setCellValue("Bedrag");
        bedragCell.setCellStyle(style);

        Cell omschrijvingCell = descRow.createCell(5);
        omschrijvingCell.setCellValue("Omschrijving");
        omschrijvingCell.setCellStyle(style);

        /* Loop door alle spelers in de lijst */
        for (R_BoeteContainer boete : boeteLijst) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;

            /* Ga alle velden langs die een speler heeft en voeg deze toe aan een rij in de File */
            try {
                for (Field field : boete.getClass().getDeclaredFields()) {
                    if (!field.getName().equals("spelerId") && !field.getName().equals("boeteCode")) {
                        field.setAccessible(true);
                        Class type = field.getType();
                        Object obj = field.get(boete);

                        Cell cell = row.createCell(colNum++);

                        if (type == String.class) {
                            cell.setCellValue((String) obj);
                        } else if (type == int.class) {
                            cell.setCellValue((Integer) obj);
                        } else if (type == double.class) {
                            cell.setCellValue(format.format(obj));
                            cell.setCellStyle(doubleStyle);
                        } else if (type == boolean.class) {
                            cell.setCellValue((Boolean) obj ? "Ja" : "Nee");
                        }

                        sheet.autoSizeColumn(colNum);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        /* Exporteer de gemaakte file */
        try {
            FileOutputStream outputStream = new FileOutputStream(boeteFile);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return boeteFile;
    }

    public static String setBoeteBetaald(int boeteId) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/infobotdb", "root", FileUtils.readFileToString(new File("/home/thijs/Infobotfiles/dbpass.txt")));

            PreparedStatement ps = con.prepareStatement(
                    "UPDATE boeteoverzichtroda SET Betaald = ? WHERE BoeteId = ?"
            );

            ps.setString(1, "Ja");
            ps.setInt(2, boeteId);

            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        updateBoeteLijst();
        return TextFormatting.toItalic("Gelukt!");
    }

    public static String setAlleBoetesBetaald(String naam) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/infobotdb", "root", FileUtils.readFileToString(new File("/home/thijs/Infobotfiles/dbpass.txt")));

            PreparedStatement ps = con.prepareStatement(
                    "UPDATE boeteoverzichtroda SET Betaald = ? WHERE BoeteId = ?"
            );

            ps.setString(1, "Ja");
            ps.setString(2, naam);

            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        updateBoeteLijst();
        return TextFormatting.toItalic(naam + " heeft alle boetes betaald!");
    }

    public static String setBoeteOpenstaand(int boeteId) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/infobotdb", "root", FileUtils.readFileToString(new File("/home/thijs/Infobotfiles/dbpass.txt")));

            PreparedStatement ps = con.prepareStatement(
                    "UPDATE boeteoverzichtroda SET Betaald = ? WHERE BoeteId = ?"
            );

            ps.setString(1, "Nee");
            ps.setInt(2, boeteId);

            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        updateBoeteLijst();
        return TextFormatting.toItalic("Gelukt!");
    }

    public static String verwijderBoete(int boeteId) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/infobotdb", "root", FileUtils.readFileToString(new File("/home/thijs/Infobotfiles/dbpass.txt")));
            Statement stmt = con.createStatement();
            stmt.execute("DELETE FROM boeteoverzicht WHERE BoeteId = " + boeteId);

            stmt.close();
            con.close();
        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return TextFormatting.toItalic("Geen boete met dit ID gevonden");
        }

        updateBoeteLijst();
        return TextFormatting.toItalic("Gelukt!");
    }

    public static File getOpenstaandeBoetes() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdfTitle = new SimpleDateFormat("yyyy-MM-dd");
        File boeteFile = new File("/home/thijs/Infobotfiles/" + String.valueOf(sdfTitle.format(timestamp)) + "_boetelijst_openstaand.xlsx");

        ArrayList<R_BoeteContainer> boeteLijst = new ArrayList<>();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/infobotdb", "root", FileUtils.readFileToString(new File("/home/thijs/Infobotfiles/dbpass.txt")));
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT boeteoverzichtroda.BoeteId, boetepotroda.Naam, boeteoverzichtroda.Betaald, boetespecificatieroda.Omschrijving, boetespecificatieroda.BoeteBedrag, boeteoverzichtroda.Datum " +
                    "FROM boeteoverzichtroda " +
                    "INNER JOIN boetepotroda ON boeteoverzichtroda.SpelerId = boetepotroda.Id " +
                    "INNER JOIN boetespecificatieroda ON boeteoverzichtroda.Boetecode = boetespecificatieroda.Code");

            while (rs.next()) {
                if (rs.getString("Betaald").equals("Nee")) {
                    boeteLijst.add(new R_BoeteContainer(rs.getInt("BoeteId"),
                            rs.getString("Naam"),
                            rs.getString("Betaald").equals("Ja"),
                            rs.getString("Omschrijving"),
                            rs.getDouble("BoeteBedrag"),
                            rs.getString("Datum")));
                }
            }

            boeteLijst.sort(Comparator.comparing(R_BoeteContainer::getBoeteId));

            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        /* Maak een nieuw workbook en voeg daar een nieuwe sheet aan toe */
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Overzicht boetes");

        /* Stel de voetteksten van de sheet in */
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        sheet.getFooter().setRight(String.valueOf(sdf.format(timestamp)));
        sheet.getFooter().setLeft("Boeteoverzicht");

        DecimalFormat format = new DecimalFormat("#.00", new DecimalFormatSymbols(Locale.getDefault()));

        /* Stijl van de bovenste rij */
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);

        CellStyle doubleStyle = workbook.createCellStyle();
        doubleStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));

        /* Kleur van de bovenste rij */
        Font font = workbook.createFont();
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setBold(true);
        style.setFont(font);

        /* De opmaak voor de bovenste rij */
        int rowNum = 1;
        Row descRow = sheet.createRow(0);

        Cell boeteIdCell = descRow.createCell(0);
        boeteIdCell.setCellValue("Boete ID");
        boeteIdCell.setCellStyle(style);

        Cell datumCell = descRow.createCell(1);
        datumCell.setCellValue("Datum");
        datumCell.setCellStyle(style);

        Cell naamCell = descRow.createCell(2);
        naamCell.setCellValue("Naam");
        naamCell.setCellStyle(style);

        Cell betaaldCell = descRow.createCell(3);
        betaaldCell.setCellValue("Betaald");
        betaaldCell.setCellStyle(style);

        Cell bedragCell = descRow.createCell(4);
        bedragCell.setCellValue("Bedrag");
        bedragCell.setCellStyle(style);

        Cell omschrijvingCell = descRow.createCell(5);
        omschrijvingCell.setCellValue("Omschrijving");
        omschrijvingCell.setCellStyle(style);

        /* Loop door alle spelers in de lijst */
        for (R_BoeteContainer boete : boeteLijst) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;

            /* Ga alle velden langs die een speler heeft en voeg deze toe aan een rij in de File */
            try {
                for (Field field : boete.getClass().getDeclaredFields()) {
                    if (!field.getName().equals("spelerId") && !field.getName().equals("boeteCode")) {
                        field.setAccessible(true);
                        Class type = field.getType();
                        Object obj = field.get(boete);

                        Cell cell = row.createCell(colNum++);

                        if (type == String.class) {
                            cell.setCellValue((String) obj);
                        } else if (type == int.class) {
                            cell.setCellValue((Integer) obj);
                        } else if (type == double.class) {
                            cell.setCellValue(format.format(obj));
                            cell.setCellStyle(doubleStyle);
                        } else if (type == boolean.class) {
                            cell.setCellValue((Boolean) obj ? "Ja" : "Nee");
                        }

                        sheet.autoSizeColumn(colNum);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        /* Exporteer de gemaakte file */
        try {
            FileOutputStream outputStream = new FileOutputStream(boeteFile);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return boeteFile;
    }

    public static File getBetaaldeBoetes() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdfTitle = new SimpleDateFormat("yyyy-MM-dd");
        File boeteFile = new File("/home/thijs/Infobotfiles/" + String.valueOf(sdfTitle.format(timestamp)) + "_boetelijst_betaald.xlsx");

        ArrayList<R_BoeteContainer> boeteLijst = new ArrayList<>();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/infobotdb", "root", FileUtils.readFileToString(new File("/home/thijs/Infobotfiles/dbpass.txt")));
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT boeteoverzichtroda.BoeteId, boetepotroda.Naam, boeteoverzichtroda.Betaald, boetespecificatieroda.Omschrijving, boetespecificatieroda.BoeteBedrag, boeteoverzichtroda.Datum " +
                    "FROM boeteoverzichtroda " +
                    "INNER JOIN boetepotroda ON boeteoverzichtroda.SpelerId = boetepotroda.Id " +
                    "INNER JOIN boetespecificatieroda ON boeteoverzichtroda.Boetecode = boetespecificatieroda.Code");

            while (rs.next()) {
                if (rs.getString("Betaald").equals("Ja")) {
                    boeteLijst.add(new R_BoeteContainer(rs.getInt("BoeteId"),
                            rs.getString("Naam"),
                            rs.getString("Betaald").equals("Ja"),
                            rs.getString("Omschrijving"),
                            rs.getDouble("BoeteBedrag"),
                            rs.getString("Datum")));
                }
            }

            boeteLijst.sort(Comparator.comparing(R_BoeteContainer::getBoeteId));

            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        /* Maak een nieuw workbook en voeg daar een nieuwe sheet aan toe */
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Overzicht boetes");

        /* Stel de voetteksten van de sheet in */
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        sheet.getFooter().setRight(String.valueOf(sdf.format(timestamp)));
        sheet.getFooter().setLeft("Boeteoverzicht");

        DecimalFormat format = new DecimalFormat("#.00", new DecimalFormatSymbols(Locale.getDefault()));

        /* Stijl van de bovenste rij */
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);

        CellStyle doubleStyle = workbook.createCellStyle();
        doubleStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));

        /* Kleur van de bovenste rij */
        Font font = workbook.createFont();
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setBold(true);
        style.setFont(font);

        /* De opmaak voor de bovenste rij */
        int rowNum = 1;
        Row descRow = sheet.createRow(0);

        Cell boeteIdCell = descRow.createCell(0);
        boeteIdCell.setCellValue("Boete ID");
        boeteIdCell.setCellStyle(style);

        Cell datumCell = descRow.createCell(1);
        datumCell.setCellValue("Datum");
        datumCell.setCellStyle(style);

        Cell naamCell = descRow.createCell(2);
        naamCell.setCellValue("Naam");
        naamCell.setCellStyle(style);

        Cell betaaldCell = descRow.createCell(3);
        betaaldCell.setCellValue("Betaald");
        betaaldCell.setCellStyle(style);

        Cell bedragCell = descRow.createCell(4);
        bedragCell.setCellValue("Bedrag");
        bedragCell.setCellStyle(style);

        Cell omschrijvingCell = descRow.createCell(5);
        omschrijvingCell.setCellValue("Omschrijving");
        omschrijvingCell.setCellStyle(style);

        /* Loop door alle spelers in de lijst */
        for (R_BoeteContainer boete : boeteLijst) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;

            /* Ga alle velden langs die een speler heeft en voeg deze toe aan een rij in de File */
            try {
                for (Field field : boete.getClass().getDeclaredFields()) {
                    if (!field.getName().equals("spelerId") && !field.getName().equals("boeteCode")) {
                        field.setAccessible(true);
                        Class type = field.getType();
                        Object obj = field.get(boete);

                        Cell cell = row.createCell(colNum++);

                        if (type == String.class) {
                            cell.setCellValue((String) obj);
                        } else if (type == int.class) {
                            cell.setCellValue((Integer) obj);
                        } else if (type == double.class) {
                            cell.setCellValue(format.format(obj));
                            cell.setCellStyle(doubleStyle);
                        } else if (type == boolean.class) {
                            cell.setCellValue((Boolean) obj ? "Ja" : "Nee");
                        }

                        sheet.autoSizeColumn(colNum);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        /* Exporteer de gemaakte file */
        try {
            FileOutputStream outputStream = new FileOutputStream(boeteFile);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return boeteFile;
    }

    public static String getTotaalOpenstaand() {
        StringBuilder totaalBuilder = new StringBuilder(TextFormatting.toBold("Openstaande bedragen per persoon\n\n"));
        ArrayList<R_SpelerContainer> spelersLijst = new ArrayList<>();
        DecimalFormat format = new DecimalFormat("#.00", new DecimalFormatSymbols(Locale.getDefault()));

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/infobotdb", "root", FileUtils.readFileToString(new File("/home/thijs/Infobotfiles/dbpass.txt")));
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT boetepotroda.Id, boetepotroda.Naam, boeteoverzichtroda.Betaald, " +
                    "CASE WHEN boeteoverzichtroda.Betaald = 'Ja' THEN SUM(boetespecificatieroda.BoeteBedrag) END AS 'BetaaldBedrag', " +
                    "CASE WHEN boeteoverzichtroda.Betaald = 'Nee' THEN SUM(boetespecificatieroda.BoeteBedrag) END AS 'OpenstaandBedrag' " +
                    "FROM boeteoverzichtroda " +
                    "INNER JOIN boetepotroda ON boeteoverzichtroda.SpelerId = boetepotroda.Id "  +
                    "INNER JOIN boetespecificatieroda ON boeteoverzichtroda.Boetecode = boetespecificatieroda.Code " +
                    "GROUP BY boetepotroda.Id, boetepotroda.Naam, boeteoverzichtroda.Betaald");

            while (rs.next()) {
                spelersLijst.add(new R_SpelerContainer(rs.getInt("Id"),
                        rs.getString("Naam"),
                        rs.getDouble("OpenstaandBedrag"),
                        rs.getDouble("BetaaldBedrag")
                ));
            }

            for (R_SpelerContainer speler : spelersLijst) {
                if (speler.getOpenstaandBedrag() != 0) {
                    totaalBuilder.append(speler.getNaam()).append(" -> €").append(format.format(speler.getOpenstaandBedrag())).append("\n");
                }
            }

            rs.close();
            stmt.close();
            con.close();
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return String.valueOf(totaalBuilder);
    }

    public static String getTotaalBetaald() {
        StringBuilder totaalBuilder = new StringBuilder(TextFormatting.toBold("Betaalde bedragen per persoon\n\n"));
        ArrayList<R_SpelerContainer> spelersLijst = new ArrayList<>();
        DecimalFormat format = new DecimalFormat("#.00", new DecimalFormatSymbols(Locale.getDefault()));

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/infobotdb", "root", FileUtils.readFileToString(new File("/home/thijs/Infobotfiles/dbpass.txt")));
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT boetepotroda.Id, boetepotroda.Naam, boeteoverzichtroda.Betaald, " +
                    "CASE WHEN boeteoverzichtroda.Betaald = 'Ja' THEN SUM(boetespecificatieroda.BoeteBedrag) END AS 'BetaaldBedrag', " +
                    "CASE WHEN boeteoverzichtroda.Betaald = 'Nee' THEN SUM(boetespecificatieroda.BoeteBedrag) END AS 'OpenstaandBedrag' " +
                    "FROM boeteoverzichtroda " +
                    "INNER JOIN boetepotroda ON boeteoverzichtroda.SpelerId = boetepotroda.Id "  +
                    "INNER JOIN boetespecificatieroda ON boeteoverzichtroda.Boetecode = boetespecificatieroda.Code " +
                    "GROUP BY boetepotroda.Id, boetepotroda.Naam, boeteoverzichtroda.Betaald");

            while (rs.next()) {
                spelersLijst.add(new R_SpelerContainer(rs.getInt("Id"),
                        rs.getString("Naam"),
                        rs.getDouble("OpenstaandBedrag"),
                        rs.getDouble("BetaaldBedrag")
                ));
            }

            for (R_SpelerContainer speler : spelersLijst) {
                if (speler.getBetaaldBedrag() != 0) {
                    totaalBuilder.append(speler.getNaam()).append(" -> €").append(format.format(speler.getBetaaldBedrag())).append("\n");
                }
            }

            rs.close();
            stmt.close();
            con.close();
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return String.valueOf(totaalBuilder);
    }

    private static void updateBoeteLijst() {
        ArrayList<R_SpelerContainer> spelersLijst = new ArrayList<>();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/infobotdb", "root", FileUtils.readFileToString(new File("/home/thijs/Infobotfiles/dbpass.txt")));
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT boetepotroda.Id, boetepotroda.Naam, boeteoverzichtroda.Betaald, " +
                    "CASE WHEN boeteoverzichtroda.Betaald = 'Ja' THEN SUM(boetespecificatieroda.BoeteBedrag) END AS 'BetaaldBedrag', " +
                    "CASE WHEN boeteoverzichtroda.Betaald = 'Nee' THEN SUM(boetespecificatieroda.BoeteBedrag) END AS 'OpenstaandBedrag' " +
                    "FROM boeteoverzichtroda " +
                    "INNER JOIN boetepotroda ON boeteoverzichtroda.SpelerId = boetepotroda.Id "  +
                    "INNER JOIN boetespecificatieroda ON boeteoverzichtroda.Boetecode = boetespecificatieroda.Code " +
                    "GROUP BY boetepotroda.Id, boetepotroda.Naam, boeteoverzichtroda.Betaald");

            while (rs.next()) {
                spelersLijst.add(new R_SpelerContainer(rs.getInt("Id"),
                        rs.getString("Naam"),
                        rs.getDouble("OpenstaandBedrag"),
                        rs.getDouble("BetaaldBedrag")
                ));
            }

            for (R_SpelerContainer speler : spelersLijst) {
                PreparedStatement ps = con.prepareStatement(
                        "UPDATE boetepotroda SET OpenstaandBedrag = ?, BetaaldBedrag = ? WHERE Id = ? AND Naam = ?"
                );

                ps.setDouble(1, speler.getOpenstaandBedrag());
                ps.setDouble(2, speler.getBetaaldBedrag());
                ps.setInt(3, speler.getId());
                ps.setString(4, speler.getNaam());

                ps.executeUpdate();
                ps.close();
            }

            rs.close();
            stmt.close();
            con.close();
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
