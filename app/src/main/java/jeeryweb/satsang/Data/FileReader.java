package jeeryweb.satsang.Data;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import jeeryweb.satsang.R;

/**
 * Created by Debo#Paul on 3/28/2018.
 */

public class FileReader {

    List districtsInState , prayerTimeInState;


    public void read1(Context c){
        InputStream inputStream = c.getResources().openRawResource(R.raw.district);

//        InputStream inputStream2 = c.getResources().openRawResource(R.raw.prayer_timing);

        List<String> resultList = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            List<String> r = new ArrayList();

            while ((csvLine = reader.readLine()) != null) {
//               String[] row = csvLine.split(",");
//                r.add(csvLine.split(","));
                resultList.add(csvLine);
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }

        districtsInState =  resultList;
        Log.e("Data", (String)districtsInState.get(0));
    }

    public void read2(Context c){
        InputStream inputStream = c.getResources().openRawResource(R.raw.prayer_timing);


        List<String> resultList = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            List<String> r = new ArrayList();

            while ((csvLine = reader.readLine()) != null) {
//               String[] row = csvLine.split(",");
//                r.add(csvLine.split(","));
                resultList.add(csvLine);
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }

        prayerTimeInState =  resultList;
        Log.e("Data", (String)prayerTimeInState.get(0));
    }

    public String queryWithDistrict(String d){

        int po=districtsInState.size();



        for(int i=0;i<po;i++){
            String row= String.valueOf(districtsInState.get(i));
            Log.e("one row:",row);

            String row_array[] = row.split(",");
            for(String s:row_array){
//                Log.e("Data",s);

                if(s.contains(d)){
                    return row_array[0];
                }
            }
        }
        return null;
    }

    public String queryWithState(String stateName, int month){

        int numberCols = (month-1)*2+1;
        String output;
        for(int it =0;it<prayerTimeInState.size();it++){

            String row= String.valueOf(prayerTimeInState.get(it));


            if(row.contains(stateName)){
                String row_array[] = row.split(",");

                for(int i=0;i<row_array.length;i++){
                        if(i==numberCols){
                            output =  row_array[i] + "," + row_array[i+1];
                            return output;
                        }
                }
            }

        }
        return null;
    }
}
