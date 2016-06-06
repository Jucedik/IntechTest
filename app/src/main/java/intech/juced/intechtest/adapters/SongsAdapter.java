package intech.juced.intechtest.adapters;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import intech.juced.intechtest.R;
import intech.juced.intechtest.activities.SongDetailsActivity;
import intech.juced.intechtest.activities.SongsActivity;
import intech.juced.intechtest.application.IntechApplication;
import intech.juced.intechtest.application.IntechConstants;
import intech.juced.intechtest.helpers.RealmHelper;
import intech.juced.intechtest.models.SongItem;
import intech.juced.intechtest.models.SongsWrapper;
import intech.juced.intechtest.player.configs.PlayerConfigs;
import intech.juced.intechtest.service.load_playlist.LoadSongsWorker;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by juced on 23.04.2015.
 */
public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public interface ClickListener {
            void clickItem(int id);
            void clickPlay(int id);
        }
        ClickListener clickListener;

        @Override
        public void onClick(View v) {
            String tag = v.getTag() == null ? "" : v.getTag().toString();
            if (tag.equals(IntechApplication.getSingleton().getString(R.string.tag_clickableItem))) {
                clickListener.clickItem(itemId);
            }
            else if (tag.equals(IntechApplication.getSingleton().getString(R.string.tag_play))) {
                clickListener.clickPlay(itemId);
            }
        }

        int itemId;
        CardView card;
        LinearLayout block_clickableContent;
        ImageView img_photo;
        TextView text_songName;
        TextView text_artistName;
        ImageButton btn_play;

        public ViewHolder(View v, int viewType, ClickListener clickListener) {
            super(v);

            if (viewType == SongsWrapper.TYPE_ITEM) {
                this.clickListener = clickListener;
                card = (CardView) v.findViewById(R.id.card);
                block_clickableContent = (LinearLayout) v.findViewById(R.id.block_clickableContent);
                img_photo = (ImageView) v.findViewById(R.id.img_photo);
                text_songName = (TextView) v.findViewById(R.id.text_songName);
                text_artistName = (TextView) v.findViewById(R.id.text_artistName);
                btn_play = (ImageButton) v.findViewById(R.id.btn_play);

                block_clickableContent.setOnClickListener(this);
                btn_play.setOnClickListener(this);
            }
        }
    }

    private ArrayList<SongsWrapper> items;
    private boolean firstTimeLoaded = false;

    private RealmResults<SongItem> realmResults;
    private RealmChangeListener<RealmResults<SongItem>> realmChangeListener;
    private PlayerConfigs.SongsPresentationType presentationType;

    public SongsAdapter(Realm realm, PlayerConfigs.SongsPresentationType presentationType) {
        this.presentationType = presentationType;

        items = new ArrayList<>();

        realmResults = realm.where(SongItem.class).findAllAsync();
        realmChangeListener = new RealmChangeListener<RealmResults<SongItem>>() {
            @Override
            public void onChange(RealmResults<SongItem> elements) {
                // put new items to adapter
                if (!firstTimeLoaded) {
                    firstTimeLoaded = true;
                    if (elements.isEmpty()) {
                        items = SongsWrapper.createInitialList();
                    }
                    else {
                        items = SongsWrapper.prepareItems(elements, true);
                    }
                }

                items = SongsWrapper.prepareItems(elements, true);
                notifyDataSetChanged();
            }
        };
        realmResults.addChangeListener(realmChangeListener);
    }

    public int getItemCount() {
        return items.size();
    }

    public SongsWrapper getItem(int position) {
        return items.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SongsWrapper.TYPE_LOADER) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loader, parent, false), viewType, null);
        }
        else if (viewType == SongsWrapper.TYPE_ITEM) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(presentationType == PlayerConfigs.SongsPresentationType.TABLE ? R.layout.item_song_table : R.layout.item_song_list, parent, false), viewType, new ViewHolder.ClickListener() {
                @Override
                public void clickItem(int id) {
                    Intent intent = new Intent(IntechApplication.getSingleton().getApplicationContext(), SongDetailsActivity.class);
                    intent.putExtra(IntechConstants.KEY_SONG_ID, id);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    IntechApplication.getSingleton().getApplicationContext().startActivity(intent);
                }

                @Override
                public void clickPlay(int id) {
                    IntechApplication.getSingleton().songPicked(id);
                }
            });
        }
        else if (viewType == SongsWrapper.TYPE_SPACE) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_space, parent, false), viewType, null);
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        if (viewType == SongsWrapper.TYPE_LOADER) {
            if (!LoadSongsWorker.isIntentServiceRunning()) {

                if (RealmHelper.isAllSongsLoaded()) {
                    // remove loader item
                    items.remove(getItemCount() - 2);
                    notifyItemRemoved(getItemCount() - 2);
                }
                else {
                    LoadSongsWorker.fireWork(getItemCount() - 2, LoadSongsWorker.LIMIT);
                }
            }
        }
        else if (viewType == SongsWrapper.TYPE_ITEM) {
            SongItem song = getItem(position).getSongItem();
            holder.itemId = song.getId();
            //holder.card.setCardBackgroundColor(ColorGenerator.MATERIAL.getColor(song.getTitle()));
            holder.text_songName.setText(song.getTitle());
            holder.text_artistName.setText(song.getArtist());
            holder.btn_play.setColorFilter(IntechApplication.getSingleton().getResources().getColor(R.color.black_text));

            // set photo
            Glide
                    .with(IntechApplication.getSingleton().getApplicationContext())
                    .load(song.getPicUrl())
                    .centerCrop()
                    .placeholder(R.drawable.img_empty)
                    .error(R.drawable.img_empty)
                    .crossFade()
                    .into(holder.img_photo);

            // TODO: 04.06.2016 set play button state
        }
    }

    public boolean isFullWidth(int position) {
        return getItemViewType(position) == SongsWrapper.TYPE_LOADER;
    }
}
