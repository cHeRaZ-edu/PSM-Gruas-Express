package com.psm.edu.psm_gruas_express;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.psm.edu.psm_gruas_express.sharedprefences.SharedUtil;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

public class ChangeColor extends AppCompatActivity {
    public static final String KEY_COLOR = "color";
    ColorPickerView colorPickerView;
    int color_selected = -1;
    FloatingActionButton btnSaveColor;
    RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_color);

        colorPickerView = (ColorPickerView) findViewById(R.id.colorPickerView);
        btnSaveColor = (FloatingActionButton)findViewById(R.id.btnSaveChangeColor);
        relativeLayout = (RelativeLayout)findViewById(R.id.relativeLayoutChangeColor);

        colorPickerView.setColorListener(new ColorEnvelopeListener() {
            @Override
            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                relativeLayout.setBackgroundColor(envelope.getColor());
                color_selected = envelope.getColor();
            }
        });
        /*
        colorPickerView.setColorListener(new ColorListener() {
            @Override
            public void onColorSelected(int color) {
                relativeLayout.setBackgroundColor(color);
                color_selected = color;
            }
        });
        */

        btnSaveColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(color_selected == -1) {
                    setResult(RESULT_CANCELED);
                    finish();
                    return;
                }
                String color_string = "#"+Integer.toHexString(color_selected);
                SharedUtil.setBackgroundColorLogin(ChangeColor.this,color_string);
                Intent intent = getIntent();
                intent.putExtra(KEY_COLOR,color_selected);
                setResult(RESULT_OK,intent);
                finish();
            }
        });


    }
}
