package com.psm.edu.psm_gruas_express.models;

public interface NetCallback {
    void onWorkFinish(Object... objects);
    void onMessageThreadMain(Object data);
}
