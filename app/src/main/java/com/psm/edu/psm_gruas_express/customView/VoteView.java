package com.psm.edu.psm_gruas_express.customView;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import com.psm.edu.psm_gruas_express.R;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class VoteView {
    private Context context;
    private View view;
    private RatingBar ratingBar;
    private TextView tvPromedio;
    private CardView barRatting_5;
    private CardView barRatting_4;
    private CardView barRatting_3;
    private CardView barRatting_2;
    private CardView barRatting_1;
    private LinearLayout paddingBar_5;
    private LinearLayout paddingBar_4;
    private LinearLayout paddingBar_3;
    private LinearLayout paddingBar_2;
    private LinearLayout paddingBar_1;

    //Votes stars
    private int one;
    private int two;
    private int three;
    private int four;
    private int five;

    public VoteView(Context context, View view) {
        this.context = context;
        this.view = view;
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBarService);
        tvPromedio = (TextView) view.findViewById(R.id.tvPromedio);
        barRatting_5 = (CardView) view.findViewById(R.id.barRatting_5);
        paddingBar_5 = (LinearLayout) view.findViewById(R.id.paddingRatting_5);
        barRatting_4 = (CardView) view.findViewById(R.id.barRatting_4);
        paddingBar_4 = (LinearLayout) view.findViewById(R.id.paddingRatting_4);
        barRatting_3 = (CardView) view.findViewById(R.id.barRatting_3);
        paddingBar_3 = (LinearLayout) view.findViewById(R.id.paddingRatting_3);
        barRatting_2 = (CardView) view.findViewById(R.id.barRatting_2);
        paddingBar_2 = (LinearLayout) view.findViewById(R.id.paddingRatting_2);
        barRatting_1 = (CardView) view.findViewById(R.id.barRatting_1);
        paddingBar_1 = (LinearLayout) view.findViewById(R.id.paddingRatting_1);
    }

    public void setOne(int one) {
        this.one = one;
    }

    public void setTwo(int two) {
        this.two = two;
    }

    public void setThree(int three) {
        this.three = three;
    }

    public void setFour(int four) {
        this.four = four;
    }

    public void setFive(int five) {
        this.five = five;
    }

    private void SetRatingBar(float value) {
        ratingBar.setRating(value);
        tvPromedio.setText(value+"");

        int moda = getVoteModa();
        // moda  ----> 1
        // votos ----> ?
        //votos*1/moda = votos/moda;
        //formato;
        float ratting_5 = (float)five/(float)moda;
        float ratting_4 = (float)four/(float)moda;
        float ratting_3 = (float)three/(float)moda;
        float ratting_2 = (float)two/(float)moda;
        float ratting_1 = (float)one/(float)moda;

        ((LinearLayout.LayoutParams)barRatting_5.getLayoutParams()).weight = ratting_5;
        ((LinearLayout.LayoutParams)paddingBar_5.getLayoutParams()).weight = (1f - ratting_5);
        ((LinearLayout.LayoutParams)barRatting_4.getLayoutParams()).weight = ratting_4;
        ((LinearLayout.LayoutParams)paddingBar_4.getLayoutParams()).weight = (1f - ratting_4);
        ((LinearLayout.LayoutParams)barRatting_3.getLayoutParams()).weight = ratting_3;
        ((LinearLayout.LayoutParams)paddingBar_3.getLayoutParams()).weight = (1f - ratting_3);
        ((LinearLayout.LayoutParams)barRatting_2.getLayoutParams()).weight = ratting_2;
        ((LinearLayout.LayoutParams)paddingBar_2.getLayoutParams()).weight = (1f - ratting_2);
        ((LinearLayout.LayoutParams)barRatting_1.getLayoutParams()).weight = ratting_1;
        ((LinearLayout.LayoutParams)paddingBar_1.getLayoutParams()).weight = (1f - ratting_1);

    }

    public void notifyChanged() {
        float result = ((float)(five*5 + four*4 + three*3 + two*2 + one)) / ((float) (five + four + three + two + one));
        BigDecimal bd = BigDecimal.valueOf(result);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        SetRatingBar(bd.floatValue());

    }

    private int getVoteModa() {

        int moda = one;
        if(moda < two)
            moda = two;
        if(moda < three)
            moda = three;
        if(moda < four)
            moda = four;
        if(moda < five)
            moda = five;

        return moda;
    }


}
