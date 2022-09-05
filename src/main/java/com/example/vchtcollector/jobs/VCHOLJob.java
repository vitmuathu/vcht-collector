package com.example.vchtcollector.jobs;

import com.example.vchtcollector.constant.ApplicationConstant;
import com.example.vchtcollector.utils.ConnectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

@Service
@Slf4j
public class VCHOLJob {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Scheduled(fixedDelay = 5000)
    public void vcholCrawl() {
        try (
                Connection con = ConnectionUtils.oracleConnection();
                Statement st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_READ_ONLY)

        ) {

            String startTimeStamp = readTimeStampFromFile();
            Date test = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(startTimeStamp);
            String timeStamp2 = minusDateTime(test, 0, 0, 0, 0, -5, 0);

            log.info(test.toString());
            log.info(timeStamp2);
            writeCheckpoint2File(timeStamp2);
            String sqlSelect = String.format("select *\n" +
                    "from VC_HANDOVER_LINE\n" +
                    "where NGAY_NHAP_MAY >= to_timestamp('%s', 'dd-mm-yyyy hh24:mi:ss')\n" +
                    "  and NGAY_NHAP_MAY <= to_timestamp('%s', 'dd-mm-yyyy hh24:mi:ss')", startTimeStamp, timeStamp2);

            long startTime = System.currentTimeMillis();
            ResultSet resultSet = st.executeQuery(sqlSelect);
            long endTime = System.currentTimeMillis();
            int size = 0;
            if (resultSet != null) {
                resultSet.last();    // moves cursor to the last row
                size = resultSet.getRow(); // get row id

            }
            resultSet.first();
            JSONArray jsonArray = convert(resultSet);
            System.out.println(jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                kafkaTemplate.send("VC_HANDOVER_LINE", obj.toString());

            }
            System.out.println("get all record of db " + size + " execution time: " + (endTime - startTime) + "ms FROM " + startTimeStamp + " TO " + timeStamp2);
            kafkaTemplate.send(ApplicationConstant.TOPIC_NAME, sqlSelect);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String minusDateTime(Date date, int year, int month, int mdate, int hours, int minutes, int seconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");

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

    public static void writeCheckpoint2File(String checkpoint) throws IOException {
        FileWriter myWriter = new FileWriter("vc_handover_line.txt");
        myWriter.write(checkpoint);
        myWriter.close();
    }

    public static String readTimeStampFromFile() throws FileNotFoundException {
        File checkpointFile = new File("vc_handover_line.txt");
        Scanner myReader = new Scanner(checkpointFile);
        String timestamp = null;
        while (myReader.hasNextLine()) {
            timestamp = myReader.nextLine();
            break;
        }
        return timestamp;
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

}
