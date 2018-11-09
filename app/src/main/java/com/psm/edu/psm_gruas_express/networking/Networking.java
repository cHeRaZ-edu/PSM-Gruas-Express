package com.psm.edu.psm_gruas_express.networking;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.psm.edu.psm_gruas_express.R;
import com.psm.edu.psm_gruas_express.models.NetCallback;
import com.psm.edu.psm_gruas_express.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Networking extends AsyncTask<Object, Integer, Object> {
    static final String SERVER_PATH = "http://192.168.1.68/APIHttp.php";
    static final int TIMEOUT = 3000;

    //Actions
    public static final String SIGNUP = "signup";
    public static final String LOGIN = "login";
    public static final String UPLOAD_IMAGE = "upload_image";

    Context m_context;
    ProgressDialog m_progressDialog;

    public Networking(Context m_context) {
        this.m_context = m_context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        m_progressDialog = new ProgressDialog(m_context, ProgressDialog.THEME_HOLO_DARK);
        m_progressDialog.setTitle("Conectando");
        m_progressDialog.setMessage("Espere...");
        m_progressDialog.setCancelable(false);
        m_progressDialog.show();

    }

    @Override
    protected Object doInBackground(Object... objects) {
        String action = (String) objects[0];
        NetCallback callback = null;
        callback = (NetCallback) objects[3];
        User user = null;

        switch (action) {
            case SIGNUP:
                //Get parameters
                user = (User) objects[1];
                Bitmap bitmap = (Bitmap) objects[2];
                //Register user ...
                int id = Signup(user,callback);
                if(id == -1)
                    return null;
                user.setId(id);
                //Upload image ...
                user.setImageURL(UploadImage(user,bitmap));
                callback.onWorkFinish(user);
                break;
            case LOGIN:

                String nickname = (String)objects[1];
                String password = (String)objects[2];
                user = Login(nickname,password,callback);

                if(user == null)
                    return null;

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                callback.onWorkFinish(user);
                break;
        }

        return null;
    }

    int Signup(User user, NetCallback callback) {

        String postParams = "&action=" + SIGNUP  +"&userJson=" + user.toJSON();

        URL url = null;

        HttpURLConnection conn = null;

        try {

            url = new URL(SERVER_PATH); //Endpoint

            conn = (HttpURLConnection) url.openConnection();//Endpoint connetcion to webservices

            conn.setDoInput(true); //Response
            conn.setDoOutput(true); //Request GET or POST

            conn.setConnectTimeout(TIMEOUT);//Time response the server, max some seg. Why Anystask not be desgin for thread long

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//MIME
            conn.setFixedLengthStreamingMode(postParams.getBytes().length); //send size buffer

            //Send Request  HttpURLConnection and postParams
            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            out.write(postParams.getBytes());
            out.flush();
            out.close();

            //Recive Response
            int responseCode = conn.getResponseCode();

            //Code Connection
            // 1xx Informative answer
            // 2xx Request OK
            // 3xx Redirect
            // 4xx Error Client
            // 5xx Error Server

            //More info https://es.wikipedia.org/wiki/Anexo:C%C3%B3digos_de_estado_HTTP
            switch (responseCode) { //Switch error connection
                case HttpURLConnection.HTTP_OK: //200
                    //Request Correct, get params Response
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    String responseString = inputStreamToString(in); //Response in json
                     try {
                         JSONObject jsonObject = new JSONObject(responseString);
                         int statusCode = jsonObject.has("code_status") ? jsonObject.getInt("code_status") : -1;
                         if(statusCode != -1) {
                             if(statusCode == 200) {
                                 user.setId(jsonObject.has("Id") ? jsonObject.getInt("Id") : -1);
                                 if(user.getId() == -1)
                                     callback.onMessageThreadMain(jsonObject.has("message_server") ? jsonObject.getString("message_server") : "");
                             }
                         } else {
                             callback.onMessageThreadMain("No hubo respuesta del Servidor");
                         }

                     } catch(JSONException e) {
                         e.printStackTrace();
                     }

                    break;
                //Error Client
                case HttpURLConnection.HTTP_BAD_REQUEST: //400
                    callback.onMessageThreadMain("Response Code - La peticion es incorrecta - 400");
                    break;
                case HttpURLConnection.HTTP_CLIENT_TIMEOUT://408
                    callback.onMessageThreadMain("Response Code - Se termino el tiempo de espera del servidor - 408");
                case HttpURLConnection.HTTP_FORBIDDEN: //403
                    callback.onMessageThreadMain("Response Code - No se ha pudo hacer la conexion - 403");
                    //Error Server
                case HttpURLConnection.HTTP_GATEWAY_TIMEOUT: //504
                    callback.onMessageThreadMain("Response Code - Se termino el tiempo de espera de la puerta de enelace - 504");
                default:
                    callback.onMessageThreadMain("Response Code - Error inseperado webservices code");
            }

        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if(conn!=null)
                conn.disconnect();
        }


        return user.getId();
    }

    String UploadImage(User user, Bitmap bitmap) {
        //Enviar imagen por multipart form data
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date date = Calendar.getInstance().getTime();
        String imageURL = "";
        String attachmentName = "file";
        String attachmentFileName =  "img_"+ dateFormat.format(date) +".png";
        //Limites &
        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";

        URL url = null;

        HttpURLConnection conn = null;

        try {
            url = new URL(SERVER_PATH); //Endpoint

            conn = (HttpURLConnection) url.openConnection();//Endpoint connetcion to webservices

            //Setup request
            conn.setDoInput(true); //Response
            conn.setDoOutput(true); //Request GET or POST
            conn.setUseCaches(false);//desable cache
            //conn.setConnectTimeout(TIMEOUT);//Time response the server, max some seg. Why Anystask not be desgin for thread long
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + boundary);

            //Content
            DataOutputStream request = new DataOutputStream(
                    conn.getOutputStream());
            /*
             --*****
            Content-Disposition: form-data; name="action"

            updload
             --*****
            Content-Disposition: form-data; name="jsonUser"

            {json:value}
            --*****
            Content-Disposition: form-data; name="attachmentName";filename=attachmentFileName

            000000011110001010101001010101010101010010101010101
            --*****--

            */
            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"action\"" +crlf+crlf);
            request.writeBytes(UPLOAD_IMAGE);
            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"userJson\"" +crlf+crlf);
            request.writeBytes(user.toJSON());
            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" +
                    attachmentName + "\";filename=\"" +
                    attachmentFileName + "\"" + crlf);
            request.writeBytes(crlf);

            //Bitmap to array bytes
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            request.write(byteArray);//send image

            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary +
                    twoHyphens + crlf);

            request.flush();// send
            request.close();

            //Recive Response
            int responseCode = conn.getResponseCode();

            //Code Connection
            // 1xx Informative answer
            // 2xx Request OK
            // 3xx Redirect
            // 4xx Error Client
            // 5xx Error Server

            //More info https://es.wikipedia.org/wiki/Anexo:C%C3%B3digos_de_estado_HTTP
            switch (responseCode) { //Switch error connection
                case HttpURLConnection.HTTP_OK: //200
                    //Request Correct, get params Response
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    String responseString = inputStreamToString(in); //Response in json
                    try{
                        JSONObject jsonObject = new JSONObject(responseString);
                        int statusCode = jsonObject.has("code_status") ? jsonObject.getInt("code_status") : -1;
                        if(statusCode == -1) {
                            Log.i("ERROR", "No se recivio el formato");
                        } else
                        if(statusCode == 200) {
                            imageURL = jsonObject.has("imageURL") ? jsonObject.getString("imageURL") : "";
                        }

                    } catch(JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                //Error Client
                case HttpURLConnection.HTTP_BAD_REQUEST: //400
                    Log.d("Response Code", "La peticion es incorrecta - 400");
                    break;
                case HttpURLConnection.HTTP_CLIENT_TIMEOUT://408
                    Log.d("Response Code", "Se termino el tiempo de espera del servidor - 408");
                case HttpURLConnection.HTTP_FORBIDDEN: //403
                    Log.d("Response Code", "No se ha pudo hacer la conexion - 403");
                    //Error Server
                case HttpURLConnection.HTTP_GATEWAY_TIMEOUT: //504
                    Log.d("Response Code", "Se termino el tiempo de espera de la puerta de enelace - 504");
                default:
                    Log.d("Response Code", "Error inseperado webservices code");
            }


        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if(conn!=null)
                conn.disconnect();
        }

        return imageURL;
    }

    User Login(String nickname, String password, NetCallback callback) {
        User user = null;
        String postParams = "&action=" + LOGIN  +"&nickname=" + nickname + "&password=" + password;
        URL url = null;
        HttpURLConnection conn = null;

        try {

            url = new URL(SERVER_PATH); //Endpoint

            conn = (HttpURLConnection) url.openConnection();//Endpoint connetcion to webservices

            conn.setDoInput(true); //Response
            conn.setDoOutput(true); //Request GET or POST

            conn.setConnectTimeout(TIMEOUT);//Time response the server, max some seg. Why Anystask not be desgin for thread long

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//MIME
            conn.setFixedLengthStreamingMode(postParams.getBytes().length); //send size buffer

            //Send Request  HttpURLConnection and postParams
            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            out.write(postParams.getBytes());
            out.flush();
            out.close();

            //Recive Response
            int responseCode = conn.getResponseCode();

            //Code Connection
            // 1xx Informative answer
            // 2xx Request OK
            // 3xx Redirect
            // 4xx Error Client
            // 5xx Error Server

            //More info https://es.wikipedia.org/wiki/Anexo:C%C3%B3digos_de_estado_HTTP

            switch (responseCode) { //Switch error connection
                case HttpURLConnection.HTTP_OK: //200
                    //Request Correct, get params Response
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    String responseString = inputStreamToString(in); //Response in json
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        int statusCode = jsonObject.has("code_status") ? jsonObject.getInt("code_status") : -1;
                        if(statusCode != -1) {
                            if(statusCode == 200) {
                                String json_user = jsonObject.has("json_user") ? jsonObject.getString("json_user") : null;
                                if(json_user != null) {
                                    user = new Gson().fromJson(json_user, User.class);
                                    if(user.getId() == -1) {
                                        user = null;
                                        callback.onMessageThreadMain(jsonObject.has("message_server") ? jsonObject.getString("message_server") : "");
                                    }
                                } else {
                                    callback.onMessageThreadMain(jsonObject.has("message_server") ? jsonObject.getString("message_server") : "");
                                }
                            }
                        } else {
                            callback.onMessageThreadMain("No hubo respuesta del Servidor");
                        }

                    } catch(JSONException e) {
                        e.printStackTrace();
                        callback.onMessageThreadMain("Hubo un problema con el servidor");
                    }

                    break;
                //Error Client
                case HttpURLConnection.HTTP_BAD_REQUEST: //400
                    callback.onMessageThreadMain("Response Code - La peticion es incorrecta - 400");
                    break;
                case HttpURLConnection.HTTP_CLIENT_TIMEOUT://408
                    callback.onMessageThreadMain("Response Code - Se termino el tiempo de espera del servidor - 408");
                case HttpURLConnection.HTTP_FORBIDDEN: //403
                    callback.onMessageThreadMain("Response Code - No se ha pudo hacer la conexion - 403");
                    //Error Server
                case HttpURLConnection.HTTP_GATEWAY_TIMEOUT: //504
                    callback.onMessageThreadMain("Response Code - Se termino el tiempo de espera de la puerta de enelace - 504");
                default:
                    callback.onMessageThreadMain("Response Code - Error inseperado webservices code");
            }



        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if(conn!=null)
                conn.disconnect();
        }

        return user;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
       m_progressDialog.dismiss();
    }




    // Metodo que lee un String desde un InputStream (Convertimos el InputStream del servidor en un String)
    private String inputStreamToString(InputStream is) {
        String rLine = "";
        StringBuilder response = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        try {
            while((rLine = rd.readLine()) != null)
            {
                response.append(rLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response.toString();
    }
}
