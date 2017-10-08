package info.trongdat.whisperapp.views.libs.materialchips.model;


import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Chip implements ChipInterface {

    private Object id;
    private Uri avatarUri;
    private Drawable avatarDrawable;
    private String label;
    private String info;

    public Chip() {
    }

    public Chip(@NonNull Object id, @Nullable Uri avatarUri, @NonNull String label, @Nullable String info) {
        this.id = id;
        this.avatarUri = avatarUri;
        this.label = label;
        this.info = info;
    }

    public Chip(@NonNull Object id, @Nullable Drawable avatarDrawable, @NonNull String label, @Nullable String info) {
        this.id = id;
        this.avatarDrawable = avatarDrawable;
        this.label = label;
        this.info = info;
    }

    public Chip(@Nullable Uri avatarUri, @NonNull String label, @Nullable String info) {
        this.avatarUri = avatarUri;
        this.label = label;
        this.info = info;
    }

    public Chip(@Nullable Drawable avatarDrawable, @NonNull String label, @Nullable String info) {
        this.avatarDrawable = avatarDrawable;
        this.label = label;
        this.info = info;
    }

    public Chip(@NonNull Object id, @NonNull String label, @Nullable String info) {
        this.id = id;
        this.label = label;
        this.info = info;
    }

    public Chip(@NonNull String label, @Nullable String info) {
        this.label = label;
        this.info = info;
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public Uri getAvatarUri() {
        return avatarUri;
    }

    @Override
    public Drawable getAvatarDrawable() {
        return avatarDrawable;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getInfo() {
        return info;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public void setAvatarUri(Uri avatarUri) {
        this.avatarUri = avatarUri;
    }

    public void setAvatarDrawable(Drawable avatarDrawable) {
        this.avatarDrawable = avatarDrawable;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
