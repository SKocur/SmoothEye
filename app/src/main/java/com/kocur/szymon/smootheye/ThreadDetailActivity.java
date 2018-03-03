package com.kocur.szymon.smootheye;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kocur.szymon.smootheye.POJOs.CommentPOJO;
import com.kocur.szymon.smootheye.POJOs.ThreadPOJO;

import java.io.IOException;
import java.net.MalformedURLException;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ThreadDetailActivity extends AppCompatActivity {
    LinearLayout list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_detail);

        TextView textThreadName = (TextView) findViewById(R.id.text_thread_name);
        textThreadName.setText(getIntent().getExtras().getString("threadName", "Title"));

        TextView textThreadAuthor = (TextView) findViewById(R.id.text_thread_author);
        textThreadAuthor.setText(getIntent().getExtras().getString("threadCreatedBy", "Author"));

        TextView textThreadCreatedDate = (TextView) findViewById(R.id.text_thread_created_date);
        textThreadCreatedDate.setText(getIntent().getExtras().getString("threadCreatedDate", "00 >> 00"));

        OverScrollDecoratorHelper.setUpOverScroll((ScrollView) findViewById(R.id.scroll_thread_details));

        if(Tools.isNetworkAvailable(getApplicationContext())) {
            new DownloadContent().start();
            new DownloadContentComments().start();
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection!", Toast.LENGTH_LONG).show();
        }

        list = (LinearLayout) findViewById(R.id.list_comments);
    }

    private class DownloadContent extends Thread {
        private Gson nGson;

        DownloadContent(){
            nGson = new Gson();
        }

        @Override
        public void run() {
            try {
                ThreadPOJO threadPOJO = nGson.fromJson(runUrl("https://talkpost.pl/api/?action=get&mode=preview&threadid=" + getIntent().getExtras().getString("threadID")), ThreadPOJO.class);

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress(threadPOJO);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void publishProgress(final ThreadPOJO mThreadPOJO) {
        ThreadDetailActivity.this.runOnUiThread(new Runnable() {
            TextView threadTextContent;
            ThreadPOJO threadPOJO;

            @Override
            public void run() {
                try {
                    threadTextContent = (TextView) findViewById(R.id.text_thread_content);
                    threadPOJO = mThreadPOJO;

                    String[] data = threadPOJO.threadText.split("<br />");
                    String content = "";

                    for (String line : data) {
                        content += line;
                    }

                    threadTextContent.setText(content);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class DownloadContentComments extends Thread {
        private Gson nGson;

        DownloadContentComments(){
            nGson = new Gson();
        }

        @Override
        public void run() {
            try {
                CommentPOJO[] commentPOJOs = nGson.fromJson(runUrl("https://talkpost.pl/api/?action=get&mode=comments&threadid=" + getIntent().getExtras().getString("threadID")), CommentPOJO[].class);

                for (CommentPOJO item : commentPOJOs) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    publishProgressComments(item);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void publishProgressComments(final CommentPOJO mCommentPOJO) {
        runOnUiThread(new Runnable() {
            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout layout = null;
            TextView commentText = null;
            TextView commentCreatedDate = null;
            TextView commentOwner = null;
            CommentPOJO commentPOJO;

            @Override
            public void run() {
                findViewById(R.id.comment_info).setVisibility(View.GONE);

                layout = (LinearLayout) inflater.inflate(R.layout.item_comment, null);
                commentText = (TextView) layout.findViewById(R.id.comment_text);
                commentOwner = (TextView) layout.findViewById(R.id.comment_author);
                commentCreatedDate = (TextView) layout.findViewById(R.id.comment_created_date);
                commentPOJO = mCommentPOJO;

                try {
                    String commentContent[] = commentPOJO.commentText.split("<br />");
                    String comment = "";
                    for(String text : commentContent)
                        comment += text;

                    commentText.setText(comment);
                    commentCreatedDate.setText(commentPOJO.commentDate.replace("|", "\n"));
                    commentOwner.setText(commentPOJO.commentOwner);

                    list.addView(layout);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    String runUrl(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
