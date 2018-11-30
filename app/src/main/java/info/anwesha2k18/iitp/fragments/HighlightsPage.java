package info.anwesha2k18.iitp.fragments;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import info.anwesha2k18.iitp.R;
import info.anwesha2k18.iitp.adapters.HighlightsRecylerViewAdapter;
import info.anwesha2k18.iitp.utils.NetworkUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by mayank on 26/5/17.
 */

public class HighlightsPage extends android.support.v4.app.Fragment {

    TextView emptyTextView;
    RecyclerView highlightsRecylerView;
    HighlightsRecylerViewAdapter highlightsRecylerViewAdapter;
    ArrayList<String> header = new ArrayList<>();
    ArrayList<String> details = new ArrayList<>();
    File directory;
    SwipeRefreshLayout swipeRefreshLayout;
    private String LOG_TAG = getClass().toString();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_highlights, container, false);
        directory = new ContextWrapper(getContext()).getDir("highlights", Context.MODE_PRIVATE);

        highlightsRecylerView = (RecyclerView) rootView.findViewById(R.id.highlightsRecylerView);
        emptyTextView = (TextView) rootView.findViewById(R.id.empty_highlight_text_view);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh_highlights);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new UpdateHighlightsAsyncTask().execute(new ContextWrapper(getContext()), getContext());
                swipeRefreshLayout.setRefreshing(false);
            }
        });
//trying to implement swipe to dismiss feature using ItemTouchHelper class.
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                header.remove(position);
                details.remove(position);
                highlightsRecylerViewAdapter.notifyItemRemoved(position);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(highlightsRecylerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        highlightsRecylerView.setLayoutManager(linearLayoutManager);

        highlightsRecylerViewAdapter = new HighlightsRecylerViewAdapter(getContext(), header, details);
        highlightsRecylerView.setAdapter(highlightsRecylerViewAdapter);

        setData();
        return rootView;
    }

    private void checkEmpty() {
        if (header == null || header.size() == 0) {
            emptyTextView.setVisibility(View.VISIBLE);
            highlightsRecylerView.setVisibility(View.GONE);
        } else {
            emptyTextView.setVisibility(View.GONE);
            highlightsRecylerView.setVisibility(View.VISIBLE);
        }
    }

    private void setData() {
        File file = new File(directory, "highlight.txt");
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String x;
            while ((x = bufferedReader.readLine()) != null) {
                System.out.println(x);
                StringTokenizer stringTokenizer = new StringTokenizer(x, ":-");
                header.add(stringTokenizer.nextToken());
                details.add(stringTokenizer.nextToken());
            }
            bufferedReader.close();
            fileReader.close();
            checkEmpty();
            highlightsRecylerViewAdapter.swap(header, details);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearData() {
        header.clear();
        details.clear();
        highlightsRecylerViewAdapter.swap(header, details);
    }

    private class UpdateHighlightsAsyncTask extends AsyncTask<Object, Void, Boolean> {
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                clearData();
                setData();
                Toast.makeText(getContext(), "Highlights updated successfully", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getContext(), "No network connection. Please try again later.", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            return new NetworkUtils().extractHighlights((ContextWrapper) params[0], (Context) params[1]);
        }
    }
}
