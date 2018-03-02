package com.kocur.szymon.smootheye;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.kocur.szymon.smootheye.POJOs.ThreadPOJO;

import java.io.IOException;
import java.net.MalformedURLException;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.google.android.gms.internal.zzid.runOnUiThread;

public class ThreadsActivity extends Fragment {
    LinearLayout list;
    View listInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_more, container, false);

        list = (LinearLayout) rootView.findViewById(R.id.list_thread);
        listInfo = rootView.findViewById(R.id.threads_list_info);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        View decorView = getActivity().getWindow().getDecorView();
        //set background, if your root layout doesn't have one
        Drawable windowBackground = decorView.getBackground();

        BlurView blurView = (BlurView) rootView.findViewById(R.id.blurView);
        ViewGroup root = (ViewGroup) rootView.findViewById(R.id.container);

        final BlurView.ControllerSettings topViewSettings = blurView.setupWith(root)
                .windowBackground(windowBackground)
                .blurAlgorithm(new RenderScriptBlur(getActivity().getApplicationContext()))
                .blurRadius(8);

        if (isNetworkAvailable()) {
            DownloadContent downloadContent = new DownloadContent();
            downloadContent.start();
        }

        final PullRefreshLayout layout = (PullRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        layout.setRefreshStyle(PullRefreshLayout.STYLE_WATER_DROP);

        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetworkAvailable()) {
                    list.removeAllViews();
                    DownloadContent downloadContent = new DownloadContent();
                    downloadContent.start();
                    layout.setRefreshing(false);
                }
            }
        });

        OverScrollDecoratorHelper.setUpOverScroll((ScrollView) rootView.findViewById(R.id.scroll_list_threads));

        return rootView;
    }

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    String runUrl(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private class DownloadContent extends Thread {
        private Gson nGson;

        DownloadContent(){
            nGson = new Gson();
        }

        @Override
        public void run() {
            try {
                ThreadPOJO[] threadPOJOs = nGson.fromJson(runUrl("https://talkpost.pl/api/?action=get&mode=threads&category=smootheye"), ThreadPOJO[].class);

                for (ThreadPOJO item : threadPOJOs) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                    }

                    publishProgress(item);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void publishProgress(final ThreadPOJO mThreadPOJO) {
        runOnUiThread(new Runnable() {
            LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout layout;
            TextView threadName;
            TextView threadCreatedDate;
            TextView threadActiveDate;
            TextView threadCreatedBy;
            CardView cardView;
            ThreadPOJO threadPOJO;

            @Override
            public void run() {
                if (listInfo.getVisibility() == View.VISIBLE) {
                    listInfo.setVisibility(View.GONE);
                }

                layout = (LinearLayout) inflater.inflate(R.layout.item_thread, null);
                cardView = (CardView) layout.findViewById(R.id.card_view);

                threadName = (TextView) layout.findViewById(R.id.thread_name);
                threadCreatedBy = (TextView) layout.findViewById(R.id.thread_author);
                threadCreatedDate = (TextView) layout.findViewById(R.id.thread_created_date);
                threadActiveDate = (TextView) layout.findViewById(R.id.thread_active_date);
                threadPOJO = mThreadPOJO;

                try {
                    threadName.setText(threadPOJO.threadName);
                    threadCreatedDate.setText(threadPOJO.threadCreatedDate);
                    threadActiveDate.setText(threadPOJO.threadActiveDate);
                    threadCreatedBy.setText(threadPOJO.threadCreatedBy);

                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.talkpost.pl/thread-" + threadPOJO.threadID + ".html")));
                            Intent intent = new Intent(getActivity(), ThreadDetailActivity.class);
                            intent.putExtra("threadName", threadPOJO.threadName);
                            intent.putExtra("threadCreatedDate", threadPOJO.threadCreatedDate);
                            intent.putExtra("threadCreatedBy", threadPOJO.threadCreatedBy);
                            intent.putExtra("threadID", threadPOJO.threadID);

                            Pair<View, String> p1 = Pair.create(layout.findViewById(R.id.thread_name), ViewCompat.getTransitionName(getView().findViewById(R.id.thread_name)));
                            Pair<View, String> p2 = Pair.create(layout.findViewById(R.id.thread_author), ViewCompat.getTransitionName(getView().findViewById(R.id.thread_author)));
                            Pair<View, String> p3 = Pair.create(layout.findViewById(R.id.thread_created_date), ViewCompat.getTransitionName(getView().findViewById(R.id.thread_created_date)));

                            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p1, p2, p3);
                            getActivity().startActivity(intent, options.toBundle());
                        }
                    });

                    list.addView(layout);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
