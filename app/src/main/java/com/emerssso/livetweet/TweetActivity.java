package com.emerssso.livetweet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetActivity extends AppCompatActivity {

    private static final String TAG = "TweetActivity";
    public static final int MAX_UPDATE_LENGTH = 140;
    public static final String KEY_LAST_UPDATE_ID = "LAST_UPDATE_ID";

    @BindView(R.id.editPrepend) EditText editPrepend;
    @BindView(R.id.editAppend) EditText editAppend;
    @BindView(R.id.editBody) EditText editBody;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private TweetSender tweetSender;
    private int prependLength = 0;
    private int appendLength = 0;
    private int bodyLength = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        tweetSender = new TweetSender(Twitter.getApiClient().getStatusesService());

        if(savedInstanceState != null) {
            tweetSender.setLastId(savedInstanceState.getLong(KEY_LAST_UPDATE_ID));
        }

        editPrepend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                prependLength = charSequence.length();
                setRemainingChars();
            }

            @Override public void afterTextChanged(Editable editable) {

            }
        });

        editAppend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                appendLength = charSequence.length();
                setRemainingChars();
            }

            @Override public void afterTextChanged(Editable editable) {

            }
        });

        editBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                bodyLength = charSequence.length();
                setRemainingChars();
            }

            @Override public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        Long lastId = tweetSender.getLastId();
        if(lastId != null) {
            outState.putLong(KEY_LAST_UPDATE_ID, lastId);
        }

        super.onSaveInstanceState(outState);
    }

    private void setRemainingChars() {
        int length = MAX_UPDATE_LENGTH;

        length -= prependLength + bodyLength + appendLength;
        if(prependLength > 0) {
            length--;
        }

        if(appendLength > 0) {
            length--;
        }

        toolbar.setTitle(getString(R.string.send_next_length, length));
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tweet_activity, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_finish_talk:
                tweetSender = new TweetSender(Twitter.getApiClient().getStatusesService());

                editPrepend.setText("");
                editBody.setText("");

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void sendTweet(View view) {
        String prepend = editPrepend.getText().toString();
        String append = editAppend.getText().toString();
        String body = editBody.getText().toString();

        String message = StringUtils.buildMessage(prepend, body, append);

        if (StringUtils.isNonEmpty(message)&& message.length() <= MAX_UPDATE_LENGTH) {
            tweetSender.queueTweet(message);
            editBody.setText("");
            editBody.requestFocus();
        } else if(message.length() > MAX_UPDATE_LENGTH) {
            Toast.makeText(this, R.string.tweet_too_long, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.update_empty, Toast.LENGTH_LONG).show();
        }
    }
}
