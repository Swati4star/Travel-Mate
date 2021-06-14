package objects;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Model class for city object
 */
@Entity (tableName = "city")
public class City implements Serializable {

    @Ignore
    public List<String> mInterests = new ArrayList<>();

    @PrimaryKey
    @NonNull
    public String mId;
    @ColumnInfo (name = "city_latitude")
    public String mLatitude;
    @ColumnInfo (name = "city_longitude")
    public String mLongitude;
    @ColumnInfo (name = "city_funfact")
    public int mFunFactsCount;
    @ColumnInfo (name = "city_background")
    public int mBackgroundColor;
    @ColumnInfo (name = "city_favourite")
    public int mFavouriteCity;

    @ColumnInfo(name = "city_avatar")
    public String mAvatar;
    @ColumnInfo (name = "city_nickname")
    public String mNickname;
    @ColumnInfo (name = "city_description")
    public String mDescription;
    @ColumnInfo (name = "country")
    public String mCountry;

    public City() {
    }

    public City(@NonNull String mId, String mAvatar, String mNickname,
                int funFactsCount, int color, String... interest) {
        this.mAvatar = mAvatar;
        this.mNickname = mNickname;
        this.mId = mId;
        this.mFunFactsCount = funFactsCount;
        this.mBackgroundColor = color;
        mInterests.addAll(Arrays.asList(interest));
    }

    public City(@NonNull String mId, String mAvatar, String mNickname, int funFactsCount,
                int color, List<String> interests) {
        this.mAvatar = mAvatar;
        this.mNickname = mNickname;
        this.mId = mId;
        this.mFunFactsCount = funFactsCount;
        this.mBackgroundColor = color;
        mInterests.addAll(interests);
    }

    @Ignore
    public City(String nickname, @NonNull String id) {
        this.mNickname = nickname;
        this.mId = id;
    }

    @Ignore
    public City(@NonNull String id, String nickname, String avatar) {
        this.mId = id;
        this.mNickname = nickname;
        this.mAvatar = avatar;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public String getNickname() {
        return mNickname;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getId() {
        return mId;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public List<String> getInterests() {
        return mInterests;
    }

    public int getFunFactsCount() {
        return mFunFactsCount;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public void setLatitude(String mLatitude) {
        this.mLatitude = mLatitude;
    }

    public void setLongitude(String mLongitude) {
        this.mLongitude = mLongitude;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setBackgroundColor(int mBackgroundColor) {
        this.mBackgroundColor = mBackgroundColor;
    }

    public int getFavouriteCity() {
        return mFavouriteCity;
    }

    public void setFavouriteCity(int mFavouriteCity) {
        this.mFavouriteCity = mFavouriteCity;
    }

	public String getCountry() {
		return mCountry;
	}

	public void setCountry(String mCountry) {
		this.mCountry = mCountry;
	}
    
}