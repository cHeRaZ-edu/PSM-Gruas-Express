package com.psm.edu.psm_gruas_express.networking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.psm.edu.psm_gruas_express.models.Calificacion;
import com.psm.edu.psm_gruas_express.models.Grua;
import com.psm.edu.psm_gruas_express.models.MarkerUser;
import com.psm.edu.psm_gruas_express.models.MessageChat;
import com.psm.edu.psm_gruas_express.models.NetCallback;
import com.psm.edu.psm_gruas_express.models.Position;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Networking extends AsyncTask<Object, Integer, Object> {
    public static final String SERVER_IP  ="http://192.168.1.65/";
    public static final String SERVER_PATH = SERVER_IP + "APIHttp.php";
    public static final int TIMEOUT = 3000;

    //Variables del Post params
    public static final String ACTION = "&action=";
    private static final String USER_JSON = "&userJson=";
    private static final String NICKNAME_VAR = "&nickname=";
    private static final String PASSWORD_VAR ="&password=";
    private static final String NAME_VAR = "&name=";
    private static final String PROVIDER_VAR = "&provider=";
    private static final String ID_USER_VAR = "&idUser=";
    private static final String GRUA_JSON = "&gruaJson=";
    private static final String ID_GRUA_VAR = "&idGrua=";
    private static final String CALIFICACION_JSON = "&calificacionJson=";
    private static final String POSITION_JSON = "&positionJson=";
    private static final String INVISIBLE_VAR = "&invisible=";
    private static final String MODE_VAR = "&mode=";
    private static final String MESSAGE_USER_JSON = "&messageJson=";
    public static final String ID_USER_SEND_VAR  = "&idUserSend=";
    public static final String ID_USER_RECEIVE_VAR  = "&idUserReceive=";
    public static final String ID_LAST_MESSAGE = "&idLastMessage=";

    //Actions
    public static final String SIGNUP = "signup";
    public static final String LOGIN = "login";
    public static final String UPLOAD_IMAGE = "upload_image";
    public static final String LOGIN_WITH = "login_with";
    public static final String SIGNUP_PROVIDER = "signup_provider";
    public static final String UPDATE_USER = "update_user";
    public static final String IMAGE_PERFIL = "image_perfil";
    public static final String IMAGE_BACKGROUND = "image_background";
    public static final String IMAGE_BACKGROUND_GRUA = "image_background_grua";
    public static final String UPDATE_GRUA = "update_grua";
    public static final String GET_ALL_GRUAS = "get_all_gruas";
    public static final String FIND_USER_GRUA = "find_user_grua";
    public static final String CALIFICAR_GRUA = "calificacion_grua";
    public static final String GET_ALL_CALIFICACION = "get_all_calificacion";
    public static final String RESULT_CALIFICACION = "result_calificacion";
    public static final String UPDATE_GEO_USER = "update_geo";
    public static final String UPDATE_MODE_USER = "updateModeUser";
    public static final String GET_MARKERS = "get_all_markes";
    public static final String SEND_MESSAGE_USER = "send_message_user";
    public static  final String MESSAGE_IMAGE  = "message_image";
    public static final String GET_ALL_MESSAGE_USER = "get_all_message_user";
    public static final String GET_LAST_MESSAGE = "get_last_message";
    public static final String GET_USERS_SEND_MESSAGES = "get_users_send_messages";
    public static  final String NOTIFY_MESSAGES = "notify_message";



    Context m_context;
    ProgressDialog m_progressDialog = null;
    private boolean hidden_progressDialog = false;

    public Networking(Context m_context) {
        this.m_context = m_context;
    }

    public  Networking(Context m_context, boolean hidden_progressDialog) {
        this.m_context = m_context;
        this.hidden_progressDialog = hidden_progressDialog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if(hidden_progressDialog)
            return;

        m_progressDialog = new ProgressDialog(m_context, ProgressDialog.THEME_HOLO_DARK);
        m_progressDialog.setTitle("Conectando");
        m_progressDialog.setMessage("Espere...");
        m_progressDialog.setCancelable(false);
        m_progressDialog.show();

    }

    @Override
    protected Object doInBackground(Object... objects) {
        String action = (String) objects[0];
        switch (action) {
            case SIGNUP:
                Signup((User) objects[1],(Bitmap) objects[2],(NetCallback) objects[3]);
                break;
            case LOGIN:
                Login((String)objects[1], (String)objects[2], (NetCallback) objects[3]);
                break;
            case LOGIN_WITH:
                LoginWidth((String) objects[1],(String) objects[2],(NetCallback) objects[3]);
                break;
            case SIGNUP_PROVIDER:
                Signup((User) objects[1], null,(NetCallback) objects[2]);
                break;
            case UPDATE_USER:
                UpdateUser((User) objects[1],(Bitmap) objects[2],(Bitmap) objects[3],(NetCallback) objects[4]);
                break;
            case UPDATE_GRUA:
                UpdateGrua((int) objects[1],(Grua) objects[2],(Bitmap) objects[3],(NetCallback) objects[4]);
                break;
            case GET_ALL_GRUAS:
                GetAllGruas((NetCallback) objects[1]);
                break;
            case FIND_USER_GRUA:
                FindUserGrua((int)objects[1],(NetCallback)objects[2]);
                break;
            case CALIFICAR_GRUA:
                CalificacionGrua((Calificacion)objects[1],(NetCallback)objects[2]);
                break;
            case GET_ALL_CALIFICACION:
                GetAllCalificacionGrua((Grua) objects[1],(NetCallback)objects[2]);
                break;
            case UPDATE_GEO_USER:
                UpdateGeoUser((int)objects[1],(Position)objects[2],(NetCallback)objects[3]);
                break;
            case UPDATE_MODE_USER:
                UpdateModeUser((int)objects[1],(int)objects[2],(int)objects[3],(NetCallback)objects[4]);
                break;
            case GET_MARKERS:
                GetMarkers((NetCallback)objects[1]);
                break;
            case SEND_MESSAGE_USER:
                SendMessageChat((MessageChat)objects[1],(Bitmap)objects[2],(NetCallback)objects[3]);
                break;
            case GET_USERS_SEND_MESSAGES:
                Get_Users_Send_Messages((int)objects[1],(NetCallback)objects[2]);
                break;
            case NOTIFY_MESSAGES:
                notify_message((int)objects[1],(NetCallback)objects[2]);
                break;
        }
        return null;
    }

    void Signup(User user,Bitmap bitmap, NetCallback callback) {
        JSONObject json = SendWebService(ACTION + SIGNUP  + USER_JSON + user.toJSON(),callback);
        if(json==null) {
            callback.onMessageThreadMain("Hubo un problema de Conexion");
            return;
        }

        try {
            user.setId(json.has("Id") ? json.getInt("Id") : -1);
            if(user.getId() == -1) {
                callback.onMessageThreadMain(json.has("message_server")?json.getString("message_server"):"Hubo un problema a iniciar sesion");
                return;
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
        }


        if(bitmap != null)
            user.setImageURL(UploadImage(user,bitmap,IMAGE_PERFIL,callback));
        callback.onWorkFinish(user);
    }
    void Login(String nickname, String password, NetCallback callback) {
        JSONObject json = SendWebService(ACTION + LOGIN  + NICKNAME_VAR + nickname + PASSWORD_VAR + password,callback);
        if(json==null) {
            callback.onMessageThreadMain("Hubo un problema de Conexion");
            callback.onWorkFinish(null,null,false);
            return;
        }
        User user = JsonToUser(json,"json_user");
        Grua grua = JsonToGrua(json,"json_grua");

        if(user == null)
            return;
        try {
            if(user.getId() == -1) {
                callback.onMessageThreadMain(json.has("message_server")?json.getString("message_server"):"Hubo un problema a iniciar sesion");
                return;
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        callback.onWorkFinish(user,grua,true);
    }
    void LoginWidth(String name, String provider, NetCallback callback) {
        JSONObject json = SendWebService(ACTION + LOGIN_WITH  + NAME_VAR + name + PROVIDER_VAR + provider,callback);
        if(json==null) {
            callback.onMessageThreadMain("Hubo un problema de Conexion");
            return;
        }
        User user = JsonToUser(json,"json_user");
        Grua grua = JsonToGrua(json,"json_grua");
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        callback.onWorkFinish(user,grua);
    }
    void UpdateUser(User user, Bitmap bitmapPerfil, Bitmap bitmapBackground, NetCallback callback) {

        JSONObject json = SendWebService(ACTION + UPDATE_USER  + USER_JSON + user.toJSON(),callback);
        if(json==null) {
            callback.onMessageThreadMain("Hubo un problema de Conexion");
            return;
        }

        if(bitmapPerfil!=null) {
            ((Activity)m_context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_progressDialog.setMessage("Actualizando imagen de perfil ...");
                }
            });
            user.setImageURL(UploadImage(user,bitmapPerfil,IMAGE_PERFIL,callback));
        }

        if(bitmapBackground != null) {
            ((Activity)m_context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_progressDialog.setMessage("Actualizando imagen de Fondo ...");
                }
            });
            user.setImageURL(UploadImage(user,bitmapBackground,IMAGE_BACKGROUND,callback));
        }

        callback.onMessageThreadMain("Datos completamente actualizados");
        callback.onWorkFinish(user);
    }
    void UpdateGrua(int idUser, Grua grua, Bitmap bitmap, NetCallback callback) {
        JSONObject json = SendWebService(ACTION + UPDATE_GRUA  + ID_USER_VAR + idUser + GRUA_JSON + grua.toJSON(),callback);
        if(json==null) {
            callback.onMessageThreadMain("Hubo un problema de Conexion");
            return;
        }
        try {
            grua.setId(json.has("Id")?json.getInt("Id") : -1);
            if(grua.getId() == -1) {
                callback.onMessageThreadMain(json.has("message_server") ? json.getString("message_server") : "Hubo un problema a actualizar el servicio");
                return;
            }

        } catch(JSONException ex) {
            ex.printStackTrace();
        }

        if(bitmap!=null) {
            ((Activity)m_context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_progressDialog.setMessage("Actualizando imagen de Fondo ...");
                }
            });
            UploadImage(grua, bitmap, IMAGE_BACKGROUND_GRUA,callback);
        }

        callback.onWorkFinish(grua);
    }
    void GetAllGruas(NetCallback callback) {
        List<Grua> gruas = new ArrayList<>();
        JSONObject json = SendWebService(ACTION + GET_ALL_GRUAS,callback);
        if(json==null) {
            callback.onMessageThreadMain("Hubo un problema de Conexion");
            return;
        }
        try {
            String string_gruas = json.has("json_gruas") ? json.getString("json_gruas") : null;
            if(string_gruas == null)
                return;
            JSONObject json_gruas = new JSONObject(string_gruas);

            Iterator<String> keys = json_gruas.keys();

            while(keys.hasNext()) {
                String key = keys.next();
                if (json_gruas.get(key) instanceof JSONObject) {
                    String grua = json_gruas.getString(key);
                    gruas.add(new Gson().fromJson(grua,Grua.class));
                    Log.i(key,grua);
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        callback.onWorkFinish(gruas);
    }
    void FindUserGrua(int idGrua, NetCallback callback) {
        JSONObject json = SendWebService(ACTION + FIND_USER_GRUA + ID_GRUA_VAR + idGrua,callback);
        if(json==null) {
            callback.onMessageThreadMain("Hubo un problema de Conexion");
            return;
        }
        JSONObject json_c = SendWebService(ACTION + RESULT_CALIFICACION + ID_GRUA_VAR + idGrua,callback);
        if(json_c==null) {
            callback.onMessageThreadMain("Hubo un problema de Conexion");
            return;
        }

        try{
            String json_user = json.has("json_user") ? json.getString("json_user") : null;
            String json_calificacion = json_c.has("json_calificacion") ? json_c.getString("json_calificacion") : null;
            if(json_user == null || json_calificacion == null)
                return;
            User user = new Gson().fromJson(json_user,User.class);
            Calificacion c = new Gson().fromJson(json_calificacion,Calificacion.class);

            callback.onWorkFinish(user,c);
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
    }
    void CalificacionGrua(Calificacion calificacion, NetCallback callback) {

        JSONObject json = SendWebService(ACTION + CALIFICAR_GRUA +  CALIFICACION_JSON + calificacion.toJSON(),callback);
        if(json==null) {
            callback.onMessageThreadMain("Hubo un problema de Conexion");
            return;
        }

        ((Activity)m_context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                m_progressDialog.setMessage("Actualizando votos...");
            }
        });
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        callback.onMessageThreadMain("Votos actualizados");
        callback.onWorkFinish(null);
    }
    void GetAllCalificacionGrua(Grua grua, NetCallback callback) {
        List<Calificacion> calificaciones = new ArrayList<>();
        JSONObject json = SendWebService(ACTION + GET_ALL_CALIFICACION + ID_GRUA_VAR + grua.getId(),callback);
        if(json==null) {
            callback.onMessageThreadMain("Hubo un problema de Conexion");
            return;
        }
        try {
            String string_calificaciones = json.has("json_calificacion") ? json.getString("json_calificacion") : null;
            if(string_calificaciones == null)
                return;
            JSONObject json_calificaciones = new JSONObject(string_calificaciones);
            Iterator<String> keys = json_calificaciones.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                if (json_calificaciones.get(key) instanceof JSONObject) {
                    String c = json_calificaciones.getString(key);
                    calificaciones.add(new Gson().fromJson(c,Calificacion.class));
                    Log.i(key,c);
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        callback.onWorkFinish(calificaciones);
    }
    void UpdateGeoUser(int idUser, Position position, NetCallback callback) {
        JSONObject json = SendWebService(ACTION + UPDATE_GEO_USER + ID_USER_VAR +idUser + POSITION_JSON + position.toJSON(),callback);
        if(json==null) {
            callback.onMessageThreadMain("Hubo un problema de Conexion");
            return;
        }
        try {
            String string_mode = json.has("json_mode") ? json.getString("json_mode") : null;
            if(string_mode == null)
                return;
            JSONObject json_mode = new JSONObject(string_mode);

            int invisible = json_mode.has("invisible") ? json_mode.getInt("invisible") : 0;
            int mode = json_mode.has("mode") ? json_mode.getInt("mode") : 0;

            callback.onWorkFinish(invisible, mode);

        } catch(JSONException ex) {
            ex.printStackTrace();
        }


    }
    void UpdateModeUser(int idUser, int invisible, int mode, NetCallback callback) {
        JSONObject json = SendWebService(ACTION + UPDATE_MODE_USER + ID_USER_VAR + idUser + INVISIBLE_VAR + invisible + MODE_VAR + mode,callback);
        if(json==null) {
            callback.onMessageThreadMain("Hubo un problema de Conexion");
            return;
        }
        callback.onWorkFinish();
    }
    void GetMarkers(NetCallback callback) {
        List<MarkerUser> markers = new ArrayList<>();
        JSONObject json =  SendWebService(ACTION + GET_MARKERS,callback);
        if(json==null) {
            callback.onMessageThreadMain("Hubo un problema de Conexion");
            return;
        }
        try {
            String string_markers = json.has("json_marker") ? json.getString("json_marker") : null;
            if(string_markers == null)
                return;
            JSONObject json_marker = new JSONObject(string_markers);
            Iterator<String> keys = json_marker.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                if (json_marker.get(key) instanceof JSONObject) {
                    String m = json_marker.getString(key);
                    markers.add(new Gson().fromJson(m,MarkerUser.class));
                    Log.i(key,m);
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        callback.onWorkFinish(markers);
    }
    void SendMessageChat(MessageChat message, Bitmap bitmap, NetCallback callback) {
        if(bitmap == null) {
            SendMessageNormal(message,callback);
            return;
        }

        String message_json = UploadImage(message,bitmap,MESSAGE_IMAGE,callback);
        message = new Gson().fromJson(message_json,MessageChat.class);
        callback.onWorkFinish(message);



    }
    void SendMessageNormal(MessageChat message, NetCallback callback) {
        JSONObject json = SendWebService(ACTION + SEND_MESSAGE_USER + MESSAGE_USER_JSON + message.toJSON(),callback);
        if(json==null) {
            callback.onMessageThreadMain("Hubo un problema de Conexion");
            return;
        }
        try {
            int id = json.has("idMessage") ? json.getInt("idMessage") : -1;
            if(id == -1) {
                callback.onMessageThreadMain("No se envio el mensaje");
                return;
            }
            message.setId(id);
            message.setImageURL("");
            callback.onWorkFinish(message);
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
    }
    void Get_Users_Send_Messages(int idUser, NetCallback callback) {
        List<User> users = new ArrayList<>();
        JSONObject json = SendWebService(ACTION + GET_USERS_SEND_MESSAGES + ID_USER_VAR + idUser,callback);
        if(json==null) {
            callback.onMessageThreadMain("Hubo un problema de Conexion");
            return;
        }

        try {
            String string_users = json.has("json_user") ? json.getString("json_user") : null;
            if(string_users == null)
                return;
            JSONObject json_users = new JSONObject(string_users);
            Iterator<String> keys = json_users.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                if (json_users.get(key) instanceof JSONObject) {
                    String u = json_users.getString(key);
                    users.add(new Gson().fromJson(u,User.class));
                    Log.i(key,u);
                }
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        callback.onWorkFinish(users);
    }
    void notify_message(int idUser, NetCallback callback) {
        List<User> users = new ArrayList<>();
        JSONObject json = SendWebService(ACTION + NOTIFY_MESSAGES + ID_USER_VAR + idUser, callback);

        if(json == null)
            return;

        try {
            String string_users = json.has("json_user") ? json.getString("json_user") : null;
            if(string_users == null)
                return;
            JSONObject json_users = new JSONObject(string_users);
            Iterator<String> keys = json_users.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                if (json_users.get(key) instanceof JSONObject) {
                    String u = json_users.getString(key);
                    users.add(new Gson().fromJson(u,User.class));
                    Log.i(key,u);
                }
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        callback.onWorkFinish(users);

    }
    JSONObject SendWebService(String postParams,NetCallback callback) {
        JSONObject json = null;
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
                        json = new JSONObject(responseString);
                        int statusCode = json.has("code_status") ? json.getInt("code_status") : -1;
                        if(statusCode == -1)
                            callback.onMessageThreadMain("No hubo respuesta del Servidor");
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

        return json;
    }
    String UploadImage(Object data, Bitmap bitmap, String option,NetCallback callback) {
        User user = null;
        Grua grua = null;
        MessageChat message = null;
        //Enviar imagen por multipart form data
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date date = Calendar.getInstance().getTime();
        String imageURL = null;
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
            Content-Disposition: form-data; name="option"

            option
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
            request.writeBytes("Content-Disposition: form-data; name=\"option\"" +crlf+crlf);
            request.writeBytes(option);
            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"dataJson\"" +crlf+crlf);
            if(option == IMAGE_PERFIL || option == IMAGE_BACKGROUND) {
                user = (User)data;
                request.writeBytes(user.toJSON());
            }
            else if(option == IMAGE_BACKGROUND_GRUA) {
                grua = (Grua)data;
                request.writeBytes(grua.toJSON());
            } else if(option == MESSAGE_IMAGE) {
                message = (MessageChat) data;
                request.writeBytes(message.toJSON());
            }

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
            switch (responseCode) { // Switch error connection
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

        if(imageURL == null) {
            imageURL = "";
            callback.onMessageThreadMain("Hubo un problema con la conexion");
        }


        return imageURL;
    }
    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if(m_progressDialog!=null)
            m_progressDialog.dismiss();
    }
    User JsonToUser(JSONObject json, String key) {
        User user = null;
        try {
            String json_user = json.has(key) ? json.getString(key) : null;
            if(json_user == null)
                return  user;
            user = new Gson().fromJson(json_user, User.class);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return user;
    }
    Grua JsonToGrua(JSONObject json, String key) {
        Grua grua = null;
        try {
            String json_grua = json.has(key) ? json.getString(key) : null;
            if(json_grua == null)
                return grua;
            grua = new Gson().fromJson(json_grua, Grua.class);
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
        return grua;
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
    // Metodo util para saber si hay conectividad o no.
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable();
    }
}
