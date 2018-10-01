package com.kesen.appfire.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.kesen.appfire.utils.IntentUtils;
import com.mygdx.game.R;


/**
 * Created by Devlomi on 25/03/2018.
 */

public class AboutFragment extends PreferenceFragment implements View.OnClickListener {
    private ImageButton emailBtn, websiteBtn, twitterBtn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_fragment, container, false);

        initViews(view);

        emailBtn.setOnClickListener(this);
        websiteBtn.setOnClickListener(this);
        twitterBtn.setOnClickListener(this);
        return view;
    }


    private void initViews(View view) {

        emailBtn = (ImageButton) view.findViewById(R.id.email_btn);
        websiteBtn = (ImageButton) view.findViewById(R.id.website_btn);
        twitterBtn = (ImageButton) view.findViewById(R.id.twitter_btn);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            return false;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.email_btn:
                intent = IntentUtils.getSendEmailIntent(getActivity());
                break;

            case R.id.website_btn:
                intent = IntentUtils.getOpenWebsiteIntent();
                break;

            case R.id.twitter_btn:
                intent = IntentUtils.getOpenTwitterIntent();
                break;
        }
        if (intent != null) {
            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}

