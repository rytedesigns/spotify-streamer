package com.rytedesigns.spotifystreamer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rytedesigns.spotifystreamer.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

/**
 * com.rytedesigns.spotifystreamer.adapter
 * <p/>
 * Copyright 2015 Ryan Todd Ellenberger
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This class uses the Spotify Wraper API from https://github.com/kaaes/spotify-web-api-android
 * This class uses the Picaso library from Square.
 */
public class ArtistsAdapter extends ArrayAdapter<Artist> {

    private final String LOG_TAG = ArtistsAdapter.class.getSimpleName();

    public ArtistsAdapter(Context context, int resource) {
        super(context, resource);
    }

    public ArtistsAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public ArtistsAdapter(Context context, int resource, Artist[] objects) {
        super(context, resource, objects);
    }

    public ArtistsAdapter(Context context, int resource, int textViewResourceId, Artist[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public ArtistsAdapter(Context context, int resource, List<Artist> objects) {
        super(context, resource, objects);
    }

    public ArtistsAdapter(Context context, int resource, int textViewResourceId, List<Artist> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView;

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rootView = mInflater.inflate(R.layout.list_item_artists, parent, false);
        } else {
            rootView = convertView;
        }

        Artist artist;

        if (getItem(position) != null) {
            artist = getItem(position);
        } else {
            artist = new Artist();
        }

        if (artist.images.size() > 0) {
            for (Image image : artist.images) {
                Log.d(LOG_TAG, "Artist:" + artist.name + " height:" + image.height + " width:" + image.width + " URL:" + image.url);
                if (image.width > 200) {
                    Picasso.with(getContext())
                            .load(image.url)
                            .resizeDimen(R.dimen.image_width, R.dimen.image_height)
                            .error(R.mipmap.ic_launcher)
                            .placeholder(R.mipmap.ic_launcher)
                            .into((ImageView) rootView.findViewById(R.id.artist_imageview));
                }
            }
        } else {
            Picasso.with(getContext())
                    .load(R.mipmap.ic_launcher)
                    .resizeDimen(R.dimen.image_width, R.dimen.image_height)
                    .into((ImageView) rootView.findViewById(R.id.artist_imageview));
        }

        // Artist Name
        String artistName = getItem(position).name;

        // Find the Artist Name TextView and set it.
        TextView artistNameTextView = (TextView) rootView.findViewById(R.id.artist_textview);
        artistNameTextView.setText(artistName);

        return rootView;
    }
}
