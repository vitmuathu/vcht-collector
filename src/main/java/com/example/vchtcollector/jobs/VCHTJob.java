package com.example.vchtcollector.jobs;

import com.example.vchtcollector.models.Record;
import com.example.vchtcollector.repository.RecordRepository;
import com.example.vchtcollector.utils.ConnectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

@Service
@Slf4j
public class VCHTJob {
    @Autowired
    RecordRepository recordRepository;



    public VCHTJob() throws SQLException {
    }

    private final Connection con = ConnectionUtils.openConnection();
    private final Statement st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
            ResultSet.CONCUR_READ_ONLY);
    private Long ID_HANHTRINH = 3449805000L;

    //    @Scheduled(fixedRate = 1000)
//    @Async
//    public void insert2DB() throws SQLException {
//        try {
//            String sqlInsert = "INSERT INTO user(username, password, createdDate) "
//                    + " VALUE('user2', '123', now());";
//            int numberRowsAffected = st.executeUpdate(sqlInsert);
//            System.out.println("Affected rows after inserted: " + numberRowsAffected);
//        } catch (Exception exception) {
//            System.out.println(exception.getMessage());
//        }
//    }
//    @Scheduled(fixedRate = 2000)
//    @Async
//    public void selectDB() throws SQLException {
//        String sqlSelect = "SELECT * FROM user;";
//        long startTime = System.currentTimeMillis();
//        ResultSet resultSet = st.executeQuery(sqlSelect);
//        long endTime = System.currentTimeMillis();
//        int size = 0;
//        if (resultSet != null) {
//            resultSet.last();    // moves cursor to the last row
//            size = resultSet.getRow(); // get row id
//        }
//        System.out.println("get all record of db " + size + " execution time: " + (endTime - startTime));
//        // getting the record of 3rd row
//    }
//    @Scheduled(fixedRate = 2000)
    @Scheduled(fixedRate = 2000000)
    public void evtpCrawl() {
        System.out.println("Start job");
        try (
                Connection con = ConnectionUtils.oracleConnection();
                Statement st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_READ_ONLY)
        ) {

            String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date());
            String timeStamp2 = minusDateTime(0, 0, 0, 4, 0, 0);

            String sqlSelect2 = String.format("select *\n" +
                    "from VC_HANHTRINH\n" +
                    "where NGAY_NHAP_MAY >= to_timestamp('%s', 'dd-mm-yyyy hh24:mi:ss')\n" +
                    "  and NGAY_NHAP_MAY <= to_timestamp('%s', 'dd-mm-yyyy hh24:mi:ss')", timeStamp2, timeStamp);
            System.out.println(sqlSelect2);
            String selectBaseIDHT = String.format("SELECT * from (select * from VC_HANHTRINH where ID_HANHTRINH > %d order by ID_HANHTRINH) where rownum <= %d", ID_HANHTRINH, 100);
            System.out.println(selectBaseIDHT);
            long startTime = System.currentTimeMillis();
            ResultSet resultSet = st.executeQuery(selectBaseIDHT);
            long endTime = System.currentTimeMillis();
            int size = 0;
            if (resultSet != null) {
                resultSet.last();    // moves cursor to the last row
                size = resultSet.getRow(); // get row id

            }
            System.out.println("get all record of db " + size + " execution time: " + (endTime - startTime) + "ms FROM " + timeStamp2 + " TO " + timeStamp);
            long id_next = getNextIDToQuery(resultSet, size, 100L);
            System.out.println("ID for the next query is: " + id_next);
            // Convert ResultSet to JsonArray
            resultSet.first();
            JSONArray jsonArray = convert(resultSet);
            System.out.println(jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Record record = new Record(timeStamp, obj.toString(), id_next);
                recordRepository.save(record);

            }


        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Scheduled(fixedDelay = 500)
    public void crawlVCHANHTRINH() {
        try (
                Connection con = ConnectionUtils.oracleConnection();
                Statement st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_READ_ONLY)
        ) {
            ID_HANHTRINH = readCheckpointFromFile(ID_HANHTRINH);
            String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date());
            String selectBaseIDHT = String.format("SELECT * from (select * from VC_HANHTRINH " +
                    "where ID_HANHTRINH > %d order by ID_HANHTRINH) where rownum <= %d", ID_HANHTRINH, 100);

            log.info(selectBaseIDHT);
            long startTime = System.currentTimeMillis();
            ResultSet resultSet = st.executeQuery(selectBaseIDHT);
            long endTime = System.currentTimeMillis();
            int size = 0;
            if (resultSet != null) {
                resultSet.last();    // moves cursor to the last row
                size = resultSet.getRow(); // get row id

            }
            log.info("get all record of db " + size + " execution time: " + (endTime - startTime) + "ms session  " + timeStamp);
            ID_HANHTRINH = getNextIDToQuery(resultSet, size, ID_HANHTRINH);
            log.info("ID for the next query is: " + ID_HANHTRINH);
            // Convert ResultSet to JsonArray
            resultSet.first();
            JSONArray jsonArray = convert(resultSet);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Record record = new Record(timeStamp, obj.toString(), ID_HANHTRINH);
                recordRepository.save(record);
            }
            writeCheckpoint2File(ID_HANHTRINH);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String minusDateTime(int year, int month, int mdate, int hours, int minutes, int seconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
        Date date = new Date();

        // Convert Date to Calendar
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        // Perform addition/subtraction
        c.add(Calendar.YEAR, -year);
        c.add(Calendar.MONTH, -month);
        c.add(Calendar.DATE, -mdate);
        c.add(Calendar.HOUR, -hours);
        c.add(Calendar.MINUTE, -minutes);
        c.add(Calendar.SECOND, -seconds);

        // Convert calendar back to Date
        Date currentDatePlusOne = c.getTime();

        return dateFormat.format(currentDatePlusOne);
    }

    public static JSONArray convert(ResultSet resultSet) throws Exception {
        JSONArray jsonArray = new JSONArray();
        while (resultSet.next()) {
            int columns = resultSet.getMetaData().getColumnCount();
            JSONObject obj = new JSONObject();
            for (int i = 0; i < columns; i++) {
                obj.put(resultSet.getMetaData().getColumnLabel(i + 1).toLowerCase(), resultSet.getObject(i + 1));
            }
            jsonArray.put(obj);
        }
        return jsonArray;
    }

    public static Long getNextIDToQuery(ResultSet resultSet, Integer size, Long last_id) throws Exception {
        resultSet.first();
        if (size >= 5) {
            resultSet.absolute((int) size / 5);
            long ID_NEXT = resultSet.getLong("ID_HANHTRINH");
            return ID_NEXT;
        } else if (size != 0) {
            resultSet.last();
            long ID_NEXT = resultSet.getLong("ID_HANHTRINH");
            return ID_NEXT;
        } else {
            return last_id;
        }

    }

    public static void writeCheckpoint2File(long checkpoint) throws IOException {
        FileWriter myWriter = new FileWriter("vc_hanhtrinh.txt");
        myWriter.write(Long.toString(checkpoint));
        myWriter.close();
    }

    public static Long readCheckpointFromFile(Long ID_HANHTRINH) {
        try {
            File checkpointFile = new File("vc_hanhtrinh.txt");
            Scanner myReader = new Scanner(checkpointFile);
            while (myReader.hasNextLine()) {
                String checkpoint = myReader.nextLine();
                return Long.parseLong(checkpoint);
            }
        } catch (FileNotFoundException e) {
            log.info("File checkpoint not found!");
            return ID_HANHTRINH;
        }

        return ID_HANHTRINH;
    }
}
