package com.duke.android.ufowatch.common;

public interface SharedConstants {

	// **** NEW CUSTOM SEARCH VALUES ****
	// CURRENTLY NOT IMPLEMENTING CUSTOM SEARCH
	// static final String CUSTOM_SEARCH_URI =
	// "https://www.googleapis.com/customsearch/v1?";
	// public static final String CUSTOM_SEARCH_UFO_WATCH_NEWS_ENGINE_ID =
	// "003700371628419353836:3vip5w__gki";
	// public static final String CUSTOM_SEARCH_KEYWORDS_LATEST_NEWS =
	// "+latest+news";

	// GOOGLE SEARCHES

	// WEB past 2 hrs
	// https://www.google.com/search?
	// q=latest+ufo+sightings
	// &rlz=1C1CHFX_enUS570US571
	// &es_sm=93
	// &source=lnms
	// &sa=X
	// &ei=GAN8U-KcIJKpyASS2oHgCw
	// &ved=0CAcQ_AUoAA
	// &biw=1435&bih=758
	// &dpr=0.9

	// &tbs=qdr:w,sbd:1 (for this week, sorted by date)
	// &tbs=qdr:w (for this week, sorted by relevance)
	// &tbs=qdr:d,sbd:1 (for today, sorted by date)
	// &tbs=qdr:d (for today, sorted by relevance)

	// WEB - https://www.google.com/search?q=latest+ufo+sightings&tbs=qdr:d

	// NEWS -
	// https://www.google.com/search?q=latest+ufo+sightings&tbm=nws&tbs=qdr:w

	// SEARCH ENGINE SEARCH PREFERENCE KEYS
	static final String KEY_PREF_SEARCH_ENGINE = "engine";
	static final String KEY_PREF_SEARCH_SUBJECT = "subject";
	static final String KEY_PREF_SEARCH_RANGE = "range";
	static final String KEY_PREF_SEARCH_SRC = "source";

	// SEARCH ENGINE SEARCH PREFERENCES
	static final String PREF_SEARCH_ENGINE_GOOGLE = "g";
	static final String PREF_SEARCH_ENGINE_BING = "b";
	static final String PREF_SEARCH_ENGINE_YAHOO = "y";

	static final String PREF_SEARCH_SUBJECT_SIGHTINGS = "s";
	static final String PREF_SEARCH_SUBJECT_BEINGS = "e";

	static final String PREF_SEARCH_RANGE_24HRS = "d";
	static final String PREF_SEARCH_RANGE_WEEK = "w";

	static final String PREF_SEARCH_SRC_NEWS = "n";
	static final String PREF_SEARCH_SRC_WEB = "w";
	static final String PREF_SEARCH_SRC_VIDEOS = "v";

	static final String SEARCH_GOOGLE_SUBJECT_SIGHTINGS = "q=ufo+sightings";
	static final String SEARCH_GOOGLE_SUBJECT_BEINGS = "q=extraterrestrial+beings";
	static final String SEARCH_GOOGLE_BASE_URL_WEB = "https://www.google.com/search?";
	static final String SEARCH_GOOGLE_BASE_URL_NEWS = "https://www.google.com/search?";
	static final String SEARCH_GOOGLE_BASE_URL_VIDEOS = "https://www.google.com/search?";
	static final String SEARCH_GOOGLE_QRY_WEB_WEEK = "&tbs=qdr:w&tbas=0";
	static final String SEARCH_GOOGLE_QRY_WEB_24HRS = "&tbs=qdr:d&tbas=0";
	static final String SEARCH_GOOGLE_QRY_NEWS_WEEK = "&tbm=nws&tbs=qdr:w&tbas=0";
	static final String SEARCH_GOOGLE_QRY_NEWS_24HRS = "&tbm=nws&tbs=qdr:d&tbas=0";
	static final String SEARCH_GOOGLE_QRY_VIDEOS_WEEK = "&tbm=vid&tbs=qdr:w&tbas=0";
	static final String SEARCH_GOOGLE_QRY_VIDEOS_24HRS = "&tbm=vid&tbs=qdr:d&tbas=0";

	static final String SEARCH_BING_SUBJECT_SIGHTINGS = "q=ufo+sightings";
	static final String SEARCH_BING_SUBJECT_BEINGS = "q=extraterrestrial+beings";
	static final String SEARCH_BING_BASE_URL_WEB = "http://www.bing.com/search?";
	static final String SEARCH_BING_BASE_URL_NEWS = "http://www.bing.com/news/search?";
	static final String SEARCH_BING_BASE_URL_VIDEOS = "http://www.bing.com/videos/search?";
	static final String SEARCH_BING_QRY_WEB_WEEK = "&filters=" + Util.urlEncode("ex1:\"ez2\"");
	static final String SEARCH_BING_QRY_WEB_24HRS = "&filters=" + Util.urlEncode("ex1:\"ez1\"");
	static final String SEARCH_BING_QRY_NEWS_WEEK = "&p1=[" + Util.urlEncode("NewsVertical Interval=\"8\"")
			+ "]&FORM=PTFTNR";
	static final String SEARCH_BING_QRY_NEWS_24HRS = "&p1=[" + Util.urlEncode("NewsVertical Interval=\"7\"")
			+ "]&FORM=PTFTNR";
	static final String SEARCH_BING_QRY_VIDEOS_WEEK = "&qft=" + Util.urlEncode("+filterui:videoage-lt10080");
	static final String SEARCH_BING_QRY_VIDEOS_24HRS = "&qft=" + Util.urlEncode("+filterui:videoage-lt1440");

	// YOUTUBE SEARCH PREFERENCES
	static final String VIDEO_SEARCH_PARAM_NAME_KEYWORDS = "q";
	static final String VIDEO_SEARCH_PARAM_NAME_START_TIME = "starttime";
	static final String VIDEO_SEARCH_PARAM_NAME_END_TIME = "endtime";
	// static final String VIDEO_SEARCH_PARAMS_LOCATION = "location";
	static final String VIDEO_SEARCH_PARAM_NAME_MAXRESULTS = "max-results";
	static final String VIDEO_SEARCH_PARAM_NAME_CHANNELID = "channelid";
	static final String VIDEO_SEARCH_PARAM_NAME_STARTINDEX = "start-index";
	static final String VIDEO_SEARCH_PARAM_NAME_ORDERBY = "orderby";
	// static final String VIDEO_SEARCH_KEYWORDS_USER = "-music+-game+ufo|ovni";
	static final String VIDEO_SEARCH_KEYWORDS = "-music+-game+-band+-gaming+-play+-GTA+ufo|ovni";

	/** Indicates no maximum on number of results returned = -1 */
	static final String NO_RESULT_MAXIMUM = "-1";

	/** Return videos starting from the first position = 1 */
	static final int FIRST_VIDEO_POSITION = 1;

	/** Action for the Home option = "returnhome" */
	public static final String ACTION_RETURN_HOME = "returnhome";

	public static final int NO_HEADER_ITEM_SELECTED = -1;

	/** Key title = "title" */
	public static final String KEY_TITLE = "abtitle";

	/** Key for the url of a webview or RSS feed/blog = "url" */
	public static final String KEY_URL = "url";

	/** Key for a reference to Video in bundle */
	public static final String KEY_VIDEO = "Video";

	public static final long RESULTS_PER_PAGE_PHONE = 6;
	public static final long FEED_ITEM_LIMIT = 100;

	public static final int MAX_TOTAL_VIDEO_RESULTS = 37;

	/** Key for id of the "YouTube" menu item = 1 */
	public static final int ID_MENU_YOUTUBE = 1;
	/** Key for id of the "Share" menu item = 2 */
	public static final int ID_MENU_SHARE = 2;
	/** Key for id of the "Video Full Screen" menu item = 3 */
	public static final int ID_MENU_FULL_SCREEN_VIDEO = 3;
	/** Key for id of the "WebView Full Screen" menu item = 4 */
	public static final int ID_MENU_FULL_SCREEN_WEBVIEW = 4;

	public static final String UFO_WATCH_API_KEY = "AIzaSyAqoeJg1lA-NUVmUr2Q3abnTpo78IfZeko";
	public static final String CHANNEL_ID_DUKECREATIONS = "UCWH25duFU03tQbw6kDODllA";
	public static final String URL_OAUTH2_APPROVAL = "https://accounts.google.com/o/oauth2/approval";
	public static final String URL_YOUTUBE_RATE_VIDEO = "https://www.googleapis.com/youtube/v3/videos/rate?";
	public static final String URL_FREEBASE_SEARCH = "https://www.googleapis.com/freebase/v1/search?";
	public static final String KEY_ACCESS_TOKEN = "access";
	public static final String KEY_REFRESH_TOKEN = "refresh";
	public static final String KEY_PREV_PAGE_TOKEN = "prev";
	public static final String KEY_NEXT_PAGE_TOKEN = "next";
	public static final String KEY_CURRENT_TOTAL_DISPLAY = "currentCount";
	public static final String KEY_TOTAL_RESULTS = "currentCount";
	public static final String KEY_YOUTUBE_CHOICE = "choice";
	public static final String KEY_USER_CHANNEL_ID = "userChannelId";

	public static final String VIDEO_FAVORITE = "favorite";
	public static final String CHANNEL_SUBSCRIBE = "subscribe";
	public static final String NEW_PLAYLIST_TITLE = "newPlaylistTitle";
	public static final String VIDEO_RATING_LIKE = "like";
	public static final String VIDEO_RATING_DISLIKE = "dislike";
	public static final String VIDEO_RATING_NONE = "none";
	public static final String VIDEO_WATCH_LATER = "watch";
	public static final String VIDEO_ADD_TO = "addTo";
	/** Key for getting features video list */
	public static final String KEY_PLAYLIST = "playlist";

	public static final String LIST_TYPE_FAVORITES = "favorites";
	public static final String LIST_TYPE_LIKES = "likes";
	public static final String LIST_TYPE_WATCH_LATER = "watchLater";

	public static final String AUTHORIZATION_SCOPES_PARAMS = "scope=https://www.googleapis.com/auth/youtube";
	/** Indicates that the user must re-authorize access */
	static final String REAUTHORIZATION_REQUIRED = "REAUTHORIZE";

	public static final String ERROR_REASON_INVALIDCREDENTIALS = "authError";
	public static final String ERROR_REASON_INVALIDCREDENTIALS_MSG = "Invalid Credentials";
	public static final String ERROR_REASON_VIDEOALREADYINPLAYLIST = "videoAlreadyInPlaylist";
	public static final String ERROR_REASON_VIDEOALREADYINPLAYLIST_MSG = "Playlist contains maximum number of items, or video already in playlist.";
	public static final String ERROR_REASON_PLAYLISTNOTFOUND = "playlistNotFound";
	public static final String ERROR_REASON_PLAYLISTNOTFOUND_MSG = "The playlist was not found.";
	public static final String ERROR_REASON_PLAYLISTINVALIDSNIPPET = "invalidPlaylistSnippet";
	public static final String ERROR_REASON_PLAYLISTINVALIDSNIPPET_MSG = "There was a problem creating the playlist.";
	public static final String ERROR_REASON_PLAYLISTTITLEREQUIRED = "playlistTitleRequired";
	public static final String ERROR_REASON_PLAYLISTTITLEREQUIRED_MSG = "Please enter a playlist title.";

	public static final String ERROR_REASON_VIDEONOTFOUND = "videoNotFound";
	public static final String ERROR_REASON_VIDEONOTFOUND_MSG = "The video was not found.";
	public static final String ERROR_REASON_SUBSCRIPTIONDUPLICATE = "subscriptionDuplicate";
	public static final String ERROR_REASON_SUBSCRIPTIONDUPLICATE_MSG = "The subscription that you are trying to create already exists.";
	public static final String ERROR_REASON_SUBSCRIPTIONFORBIDDEN = "subscriptionForbidden";
	public static final String ERROR_REASON_SUBSCRIPTIONFORBIDDEN_MSG = "The request is not properly authenticated or not supported for this channel.";
	public static final String ERROR_REASON_RATINGFORBIDDEN = "forbidden";
	public static final String ERROR_REASON_RATINGFORBIDDEN_MSG = "The video that you are trying to rate cannot be rated.";

	public static final String ERROR_REASON_USER_RATE_LIMIT_EXCEEDED = "userRateLimitExceeded";
	public static final String ERROR_REASON_QUOTA_EXCEEDED = "quotaExceeded";

}
