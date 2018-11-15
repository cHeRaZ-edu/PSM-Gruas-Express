package com.psm.edu.psm_gruas_express.BackgroundUtil;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.gson.Gson;
import com.psm.edu.psm_gruas_express.NotifyUtil.NotifyUtil;
import com.psm.edu.psm_gruas_express.Register;
import com.psm.edu.psm_gruas_express.models.NetCallback;
import com.psm.edu.psm_gruas_express.models.User;
import com.psm.edu.psm_gruas_express.networking.Networking;

import java.util.ArrayList;
import java.util.List;

public class ServiceUtil extends Service{

    //https://stackoverflow.com/questions/50504296/android-oreo-killing-background-services-and-clears-pending-alarms-scheduled-jo
    //IMPORTANTE PARA QUE NO TE DEJEN EN QUINTAS
    //Oreo optimiza las aplicaciones por lo tanto borra servicios y alarmas que esten utilizando
    //como configurar esto, ni idea.

    /*
    Un Service es un componente de una aplicación que puede
    realizar operaciones de larga ejecución en segundo plano y que no proporciona una interfaz de usuario.
    Otro componente de la aplicación puede iniciar un servicio y continuará ejecutándose en segundo plano
    aunque el usuario cambie a otra aplicación.

    Un servicio puede adoptar esencialmente dos formas:

    - Servicio iniciado
        Un servicio está "iniciado" cuando un componente de aplicación (como una actividad)
        lo inicia llamando a startService(). Una vez iniciado, un servicio puede ejecutarse
        en segundo plano de manera indefinida, incluso si se destruye el componente que lo inició.
        Por lo general, un servicio iniciado realiza una sola operación y no devuelve un resultado
        al emisor.

    - Servicio de enlace
        Un servicio es de “de enlace” cuando un componente de la aplicación se vincula a él llamando a bindService().
        Un servicio de enlace ofrece una interfaz cliente-servidor que permite que los componentes interactúen
        con el servicio, envíen solicitudes, obtengan resultados e incluso lo hagan en distintos procesos
        con la comunicación entre procesos (IPC). Un servicio de enlace se ejecuta solamente mientras otro
        componente de aplicación está enlazado con él.

    Advertencia - El servicio sigue en el proceso principal.
    Esto significa que, si tu servicio va a realizar un trabajo que consume más CPU u operaciones de bloqueo
    (como reproducción MP3 o funciones de red), debes crear un subproceso nuevo dentro del servicio
    para realizar ese trabajo. Al utilizar un subproceso independiente, reducirás el riesgo de que se
    produzcan errores de tipo "La aplicación no responde (ANR)", y el subproceso principal de la aplicación
    puede continuar dedicado a la interacción del usuario con tus actividades.

    Este servicio comenzara como iniciado.

    Doc.
    https://developer.android.com/guide/components/services
    */

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private User  user = null;
    private List<User>users = new ArrayList<>();
    private  Intent intent;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            while (true){
                try {
                    Thread.sleep(5000);
                    //...PROCESOS DEL SERVICIO
                    notify_user();
                } catch (InterruptedException e) {
                    // Restore interrupt status.
                    Thread.currentThread().interrupt();
                }
                // Stop the service using the startId, so that we don't stop
                // the service in the middle of handling another job
                //stopSelf(msg.arg1);
            }

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.intent = intent;
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        String json_user =  this.intent.getStringExtra(Register.JSON_USER);
        if(json_user == null) {
            Toast.makeText(this,"No se encontro datos del usuario", Toast.LENGTH_SHORT).show();
            onDestroy();
        }
        user = new Gson().fromJson(json_user, User.class);
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

        private void notify_user() {

        if(!Networking.isNetworkAvailable(ServiceUtil.this))
            return;

        new Networking(ServiceUtil.this,true).execute(Networking.NOTIFY_MESSAGES, user.getId(), new NetCallback() {
            @Override
            public void onWorkFinish(Object... objects) {
                List<User> list_user = (List<User>) objects[0];
                if(list_user.size()> 0) {
                    users.clear();
                    users.addAll(list_user);

                    for(User user : users) {
                        NotifyUtil.showNotification(ServiceUtil.this,"Nuevo mensaje",user.getNickname() + " ha enviado un mensaje.", intent);
                    }

                }
            }

            @Override
            public void onMessageThreadMain(Object data) {
                final String message = (String) data;
                Toast.makeText(ServiceUtil.this,message, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
