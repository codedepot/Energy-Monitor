    package com.example.ash.energymonitor.dataRetrieval;

    import android.os.AsyncTask;
    import android.util.Log;

    import com.dropbox.client2.DropboxAPI;

    import java.io.BufferedReader;
    import java.io.File;
    import java.io.FileOutputStream;
    import java.io.FileReader;

    /**
     * Created by Mehrdad on 2/11/2015.
     */
    public class DropboxRetrieval extends AsyncTask<String, Void, File>{

        File file;
        FileOutputStream fileOutputStream;
        DropboxAPI mDBApi;
        String filePath;
        public DropboxRetrieval(File file,String filePath, FileOutputStream fileOutputStream, DropboxAPI mDBApi){
            this.file = file;
            this.filePath = filePath;
            this.fileOutputStream = fileOutputStream;
            this.mDBApi = mDBApi;
        }

        @Override
        public File doInBackground(String...Params){
            //android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            String output = "NA";
            try {
                DropboxAPI.DropboxFileInfo info = mDBApi.getFile(filePath, null, fileOutputStream, null);

                //Log.i("Proto", "The file's rev is: " + info.getMetadata().rev);
            }catch(Exception e){
                Log.d("Proto", "this is the exception " +e);
            }


            return file;
        }

        public static String readFromFile(File file){
            String output = "NA";

            try{
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String tempLine = bufferedReader.readLine();
                while(tempLine!= null){
                    //Log.d("Proto",tempLine);
                    output =tempLine;
                    tempLine = bufferedReader.readLine();
                }
            }catch(Exception e){
                Log.d("Proto","It couldnt read it man,..." + e);
            }
            return output;
        }

    }
