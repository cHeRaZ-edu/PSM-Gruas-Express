package com.psm.edu.psm_gruas_express.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.psm.edu.psm_gruas_express.InitActivity;
import com.psm.edu.psm_gruas_express.R;
import com.psm.edu.psm_gruas_express.adapters.MessageAdapter;
import com.psm.edu.psm_gruas_express.models.MessageChat;
import com.psm.edu.psm_gruas_express.models.NetCallback;
import com.psm.edu.psm_gruas_express.models.User;
import com.psm.edu.psm_gruas_express.networking.Networking;
import com.psm.edu.psm_gruas_express.photoUtil.PhotoUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
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

import static android.app.Activity.RESULT_OK;

public class FragmentMessage extends Fragment {
    public static final String TAG = "fragment_message";
    InitActivity activity;
    TextView tvUserName;
    ListView lvMessage;
    EditText editTxtMessage;
    ImageButton btnPhoto;
    FloatingActionButton btnSendMessage;
    Bitmap  bitmap = null;
    boolean isBitmapLoader = false;
    User user;
    MessageAdapter adapter;
    List<MessageChat> messages = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message,container,false);

        activity = (InitActivity) getActivity();
        tvUserName = (TextView) view.findViewById(R.id.tvUserNameMessage);
        lvMessage = (ListView) view.findViewById(R.id.lvMessages);
        editTxtMessage = (EditText) view.findViewById(R.id.editTxtMessage);
        btnPhoto = (ImageButton) view.findViewById(R.id.btnPhotoMessage);
        btnSendMessage = (FloatingActionButton) view.findViewById(R.id.btnSendMessage);
        adapter = new MessageAdapter(activity,messages,activity.user.getId());
        lvMessage.setAdapter(adapter);
        user = activity.user_selected;
        tvUserName.setText(user.getName());


        adapter.notifyDataSetChanged();

        EventsButton();

        /*
        forma correcta de ejecutar multi threads AsyncTask
        https://stackoverflow.com/questions/4068984/running-multiple-asynctasks-at-the-same-time-not-possible
        * @TargetApi(Build.VERSION_CODES.HONEYCOMB) // API 11
        void startMyTask(AsyncTask asyncTask) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
            else
                asyncTask.execute(params);
        }
*/
        new NetworkingMessage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,activity, user, activity.user, new NetCallback() {
            @Override
            public void onWorkFinish(Object... objects) {
                    final List<MessageChat> messageChats = (List<MessageChat>)objects[0];
                    if(messageChats.size() == 0)
                        return;
                    messages.addAll(messageChats);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            lvMessage.setSelection(messages.size() - 1);
                        }
                    });

            }

            @Override
            public void onMessageThreadMain(Object data) {
                final String message = (String) data;

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity,message,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return view;
    }

    void EventsButton() {

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoUtil.ImageSelect(activity);
            }
        });

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isValidate())
                    return;

                DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                Date date = Calendar.getInstance().getTime();

                MessageChat message = new MessageChat(
                        -1,
                        activity.user.getId(),
                        user.getId(),
                        editTxtMessage.getText().toString(),
                        "",
                        "" + dateFormat.format(date),
                        0);

                editTxtMessage.setText("");

                new Networking(activity, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        Networking.SEND_MESSAGE_USER,
                        message,
                        isBitmapLoader ? bitmap : null,
                        new NetCallback() {
                            @Override
                            public void onWorkFinish(Object... objects) {
                                final MessageChat message = (MessageChat)objects[0];
                                messages.add(message);
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        isBitmapLoader = false;
                                        if(bitmap!=null) {
                                            bitmap.recycle();
                                            bitmap = null;
                                        }
                                        adapter.notifyDataSetChanged();
                                        lvMessage.setSelection(messages.size() - 1);
                                    }
                                });

                            }

                            @Override
                            public void onMessageThreadMain(Object data) {
                                final String message = (String) data;

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activity,message,Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
            }
        });

    }

    boolean isValidate() {
        if(editTxtMessage.getText().toString().trim().isEmpty())
            return false;
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && PhotoUtil.PHOTO_SHOT == requestCode) {
            isBitmapLoader = true;
            bitmap = PhotoUtil.ResizeBitmap((Bitmap) data.getExtras().get("data"));

        } else if(resultCode == RESULT_OK && PhotoUtil.STORAGE_IMAGE == requestCode) {
            isBitmapLoader = true;
            Uri uri = data.getData();
            try {
                bitmap = PhotoUtil.ResizeBitmap(MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



    class NetworkingMessage extends AsyncTask<Object, Integer, Object> {
        final int TIMEOUT = 1500;
        User userSend;
        User userRecive;
        InitActivity initActivity;
        @Override
        protected Object doInBackground(Object... objects) {
            initActivity = (InitActivity)objects[0];
            userSend = (User)objects[1];
            userRecive = (User)objects[2];
            NetCallback callback = (NetCallback)objects[3];

            // obtener todos los mensajes
            GetAllMessages(callback);

            try {

                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            while (initActivity.TAG.equals(TAG)) {

                // escuchar si llego otro mensaje

                GetLastMessages(messages.get(messages.size()-1).getId(),callback);

                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }


            return null;
        }

        void GetAllMessages(NetCallback callback) {
            List<MessageChat> messages_chat = new ArrayList<>();
            JSONObject json = SendWebService(
                    Networking.ACTION + Networking.GET_ALL_MESSAGE_USER +
                            Networking.ID_USER_SEND_VAR + userSend.getId() +
                            Networking.ID_USER_RECEIVE_VAR + userRecive.getId(),
                            callback);
            if(json == null)
                return;

            //Obtener todos los mensajes

            try {
                String string_messages = json.has("json_message") ? json.getString("json_message") : null;
                if(string_messages == null)
                    return;
                JSONObject json_messages = new JSONObject(string_messages);
                Iterator<String> keys = json_messages.keys();
                while(keys.hasNext()) {
                    String key = keys.next();
                    if (json_messages.get(key) instanceof JSONObject) {
                        String m = json_messages.getString(key);
                        messages_chat.add(new Gson().fromJson(m,MessageChat.class));
                        Log.i(key,m);
                    }
                }

            } catch (JSONException ex) {
                ex.printStackTrace();
            }

            callback.onWorkFinish(messages_chat);

        }
        void GetLastMessages(int idLastMessage,NetCallback callback) {
            List<MessageChat> messages_chat = new ArrayList<>();
            JSONObject json = SendWebService(
                    Networking.ACTION + Networking.GET_LAST_MESSAGE +
                            Networking.ID_USER_SEND_VAR + userSend.getId() +
                            Networking.ID_USER_RECEIVE_VAR + userRecive.getId() +
                            Networking.ID_LAST_MESSAGE + idLastMessage,
                    callback);
            if(json == null)
                return;

            //Obtener todos los mensajes

            try {
                String string_messages = json.has("json_message") ? json.getString("json_message") : null;
                if(string_messages == null)
                    return;
                JSONObject json_messages = new JSONObject(string_messages);
                Iterator<String> keys = json_messages.keys();
                while(keys.hasNext()) {
                    String key = keys.next();
                    if (json_messages.get(key) instanceof JSONObject) {
                        String m = json_messages.getString(key);
                        messages_chat.add(new Gson().fromJson(m,MessageChat.class));
                        Log.i(key,m);
                    }
                }

            } catch (JSONException ex) {
                ex.printStackTrace();
            }

            callback.onWorkFinish(messages_chat);
        }

        JSONObject SendWebService(String postParams, NetCallback callback) {
            JSONObject json = null;
            URL url = null;
            HttpURLConnection conn = null;
            try {
                url = new URL(Networking.SERVER_PATH); //Endpoint
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

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            initActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(initActivity,"Thread finish" ,Toast.LENGTH_SHORT).show();
                }
            });
        }

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
}
