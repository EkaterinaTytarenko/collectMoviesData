import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException{
        InputStream initialFileStream = new FileInputStream(new File("src/list.json"));
        JSONTokener tokener=new JSONTokener(initialFileStream);
        JSONArray movies=new JSONArray(tokener);

        JSONArray extendedInfo=new JSONArray();

        for (int i=0;i<movies.length();++i) {
            JSONObject movie = movies.getJSONObject(i);
            extendedInfo.put(movieWithExtendedInfo(movie));
        }

        FileWriter file = new FileWriter("src/extendedList.json");
        extendedInfo.write(file);
        file.flush();
        file.close();
    }


    public static JSONObject movieWithExtendedInfo(JSONObject movie) throws IOException, InterruptedException{
        String IMDB_id= movie.getString("id");

        JSONObject extended=new JSONObject();
        extended.put("title",movie.get("title"));
        extended.put("imdb_id",IMDB_id);

        extended.put("picture_url",getImageUlr(IMDB_id));

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://movies-tvshows-data-imdb.p.rapidapi.com/?imdb="+IMDB_id+"&type=get-movie-details")
                .get()
                .addHeader("x-rapidapi-key", "")
                .addHeader("x-rapidapi-host", "movies-tvshows-data-imdb.p.rapidapi.com")
                .build();

        Response response = client.newCall(request).execute();
        JSONObject responseJSON=new JSONObject(response.body().string());

        extended.put("year", responseJSON.get("year"));
        extended.put("genres",responseJSON.get("genres"));

        return extended;
    }

    public static String getImageUlr(String IMDB_id) throws IOException, InterruptedException{
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://movies-tvshows-data-imdb.p.rapidapi.com/?imdb="+IMDB_id+"&type=get-movies-images-by-imdb")
                .get()
                .addHeader("x-rapidapi-key", "")
                .addHeader("x-rapidapi-host", "movies-tvshows-data-imdb.p.rapidapi.com")
                .build();

        Response response = client.newCall(request).execute();
        JSONObject responseJSON=new JSONObject(response.body().string());

        return responseJSON.getString("poster");

    }

}
