package com.example.ui

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.LibraryMediaEntity
import com.example.data.local.UserPreferences
import com.example.data.model.Episode
import com.example.data.model.MediaDetail
import com.example.data.model.MediaItem
import com.example.data.model.Provider
import com.example.data.model.Season
import com.example.data.model.StreamSource
import com.example.data.api.ApiClient
import com.example.data.repository.StreamRepository
import com.example.services.SourceEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class HomeUiState(
    val featured: MediaItem? = null,
    val top10Today: List<MediaItem> = emptyList(),
    val trendingMovies: List<MediaItem> = emptyList(),
    val trendingSeries: List<MediaItem> = emptyList(),
    val newReleases: List<MediaItem> = emptyList(),
    val popularThisWeek: List<MediaItem> = emptyList(),
    val bestOfTheBest: List<MediaItem> = emptyList(),
    val providers: List<Provider> = emptyList(),
    val isLoading: Boolean = false
)

data class ProviderUiState(
    val selectedProvider: Provider? = null,
    val mediaType: String = "movie", // "movie" or "tv"
    val section: String = "popular", // "new_releases", "top_10", "popular", "best_of_the_best"
    val items: List<MediaItem> = emptyList(),
    val isLoading: Boolean = false
)

data class DetailUiState(
    val detail: MediaDetail? = null,
    val selectedSeasonNumber: Int = 1,
    val episodes: List<Episode> = emptyList(),
    val isFavorite: Boolean = false,
    val isWatchLater: Boolean = false,
    val isLoading: Boolean = false,
    val isResolvingPlayback: Boolean = false
)

data class PlayerUiState(
    val isOpen: Boolean = false,
    val title: String = "",
    val subtitle: String? = null,
    val activeSource: StreamSource? = null,
    val allSources: List<StreamSource> = emptyList(),
    val initialPosMs: Long = 0L,
    val mediaId: Int = 0,
    val mediaType: String = "movie",
    val seasonNumber: Int? = null,
    val episodeNumber: Int? = null,
    val hasNextEpisode: Boolean = false,
    val hasPrevEpisode: Boolean = false
)

data class SearchUiState(
    val query: String = "",
    val results: List<MediaItem> = emptyList(),
    val isSearching: Boolean = false
)

data class OrionUserState(
    val username: String? = null,
    val email: String? = null,
    val accountType: String? = null,
    val isPremium: Boolean = false,
    val dailyLimit: Int = 0,
    val remainingLimit: Int = 0,
    val checking: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val prefs = UserPreferences(application)
    private val db = AppDatabase.getInstance(application)
    private val libraryDao = db.libraryDao()
    private val repository = StreamRepository(prefs)

    // User Library Flows
    val continueWatching = libraryDao.getContinueWatching()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites = libraryDao.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val watchLater = libraryDao.getWatchLater()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val watchHistory = libraryDao.getWatchHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Screen States
    private val _homeState = MutableStateFlow(HomeUiState())
    val homeState: StateFlow<HomeUiState> = _homeState.asStateFlow()

    private val _providerState = MutableStateFlow(ProviderUiState())
    val providerState: StateFlow<ProviderUiState> = _providerState.asStateFlow()

    private val _detailState = MutableStateFlow(DetailUiState())
    val detailState: StateFlow<DetailUiState> = _detailState.asStateFlow()

    private val _playerState = MutableStateFlow(PlayerUiState())
    val playerState: StateFlow<PlayerUiState> = _playerState.asStateFlow()

    private val _searchState = MutableStateFlow(SearchUiState())
    val searchState: StateFlow<SearchUiState> = _searchState.asStateFlow()

    private val _orionUserState = MutableStateFlow(OrionUserState())
    val orionUserState: StateFlow<OrionUserState> = _orionUserState.asStateFlow()

    init {
        loadHome()
    }

    fun loadHome() {
        viewModelScope.launch {
            _homeState.value = _homeState.value.copy(isLoading = true)
            val featured = repository.getFeatured()
            val trendingMovies = repository.getTrendingMovies()
            val trendingSeries = repository.getTrendingSeries()
            val newReleases = repository.getRecentlyReleased()
            val popular = repository.getPopularThisWeek()
            val best = repository.getTopRatedMovies()
            val providers = repository.getProviders()

            val top10 = (trendingMovies + trendingSeries).distinctBy { it.id }.take(10)

            _homeState.value = HomeUiState(
                featured = featured,
                top10Today = top10,
                trendingMovies = trendingMovies,
                trendingSeries = trendingSeries,
                newReleases = newReleases,
                popularThisWeek = popular,
                bestOfTheBest = best,
                providers = providers,
                isLoading = false
            )
        }
    }

    fun selectProvider(provider: Provider) {
        _providerState.value = _providerState.value.copy(
            selectedProvider = provider,
            isLoading = true
        )
        loadProviderContent()
    }

    fun setProviderMediaType(type: String) {
        _providerState.value = _providerState.value.copy(mediaType = type, isLoading = true)
        loadProviderContent()
    }

    fun setProviderSection(section: String) {
        _providerState.value = _providerState.value.copy(section = section, isLoading = true)
        loadProviderContent()
    }

    private fun loadProviderContent() {
        val prov = _providerState.value.selectedProvider ?: return
        val type = _providerState.value.mediaType
        val section = _providerState.value.section

        viewModelScope.launch {
            val items = repository.getProviderContent(prov.providerId, type, section)
            _providerState.value = _providerState.value.copy(
                items = items,
                isLoading = false
            )
        }
    }

    fun loadDetail(mediaId: Int, mediaType: String) {
        viewModelScope.launch {
            _detailState.value = DetailUiState(isLoading = true)
            val detail = repository.getDetails(mediaId, mediaType)

            // Check if favorite/watch later in Room
            val entityId = "${mediaType}_$mediaId"
            val localEntity = libraryDao.getItemById(entityId)

            val firstSeason = detail.seasons.firstOrNull()?.seasonNumber ?: 1
            val episodes = if (mediaType == "tv") {
                repository.getSeasonEpisodes(mediaId, firstSeason)
            } else emptyList()

            _detailState.value = DetailUiState(
                detail = detail,
                selectedSeasonNumber = firstSeason,
                episodes = episodes,
                isFavorite = localEntity?.isFavorite ?: false,
                isWatchLater = localEntity?.isWatchLater ?: false,
                isLoading = false
            )
        }
    }

    fun selectSeason(seasonNumber: Int) {
        val detail = _detailState.value.detail ?: return
        viewModelScope.launch {
            val eps = repository.getSeasonEpisodes(detail.id, seasonNumber)
            _detailState.value = _detailState.value.copy(
                selectedSeasonNumber = seasonNumber,
                episodes = eps
            )
        }
    }

    fun toggleFavorite() {
        val detail = _detailState.value.detail ?: return
        val currentFav = _detailState.value.isFavorite
        val newFav = !currentFav
        _detailState.value = _detailState.value.copy(isFavorite = newFav)

        viewModelScope.launch {
            val entityId = "${detail.mediaType}_${detail.id}"
            val existing = libraryDao.getItemById(entityId)
            if (existing != null) {
                libraryDao.update(existing.copy(isFavorite = newFav))
            } else {
                libraryDao.insert(
                    LibraryMediaEntity(
                        id = entityId,
                        mediaId = detail.id,
                        mediaType = detail.mediaType,
                        title = detail.title,
                        posterPath = detail.posterPath,
                        backdropPath = detail.backdropPath,
                        voteAverage = detail.voteAverage,
                        releaseYear = detail.releaseYear,
                        overview = detail.overview,
                        isFavorite = newFav
                    )
                )
            }
        }
    }

    fun toggleWatchLater() {
        val detail = _detailState.value.detail ?: return
        val current = _detailState.value.isWatchLater
        val nextVal = !current
        _detailState.value = _detailState.value.copy(isWatchLater = nextVal)

        viewModelScope.launch {
            val entityId = "${detail.mediaType}_${detail.id}"
            val existing = libraryDao.getItemById(entityId)
            if (existing != null) {
                libraryDao.update(existing.copy(isWatchLater = nextVal))
            } else {
                libraryDao.insert(
                    LibraryMediaEntity(
                        id = entityId,
                        mediaId = detail.id,
                        mediaType = detail.mediaType,
                        title = detail.title,
                        posterPath = detail.posterPath,
                        backdropPath = detail.backdropPath,
                        voteAverage = detail.voteAverage,
                        releaseYear = detail.releaseYear,
                        overview = detail.overview,
                        isWatchLater = nextVal
                    )
                )
            }
        }
    }

    // Playback Pipeline:
    // Step 1: Gather media parameters
    // Step 2: Query Orion API for 4K, 1080P, 720P, Cached sources
    // Step 3: Rank results: Cached -> 4K -> 1080P -> 720P -> Seeds
    // Step 4: Auto-select best source
    // Step 5: Start Internal Full-Screen Video Player immediately
    fun playMedia(
        mediaId: Int,
        mediaType: String,
        title: String,
        year: String? = null,
        imdbId: String? = null,
        seasonNumber: Int? = null,
        episodeNumber: Int? = null,
        episodeTitle: String? = null,
        posterPath: String? = null,
        backdropPath: String? = null,
        overview: String? = null,
        voteAverage: Double = 0.0
    ) {
        viewModelScope.launch {
            _detailState.value = _detailState.value.copy(isResolvingPlayback = true)

            try {
                val sources = SourceEngine.resolveSources(
                    title = title,
                    year = year,
                    tmdbId = mediaId,
                    imdbId = imdbId,
                    season = seasonNumber,
                    episode = episodeNumber,
                    orionApiKey = prefs.orionApiKey,
                    orionAppKey = prefs.orionAppKey
                )

                val bestSource = SourceEngine.selectBestSource(sources)
                if (bestSource == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            getApplication(),
                            "No streams found on Orion or public indexes.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    return@launch
                }

                // Check saved progress from Room
                val entityId = if (seasonNumber != null && episodeNumber != null) {
                    "${mediaType}_${mediaId}_s${seasonNumber}_e${episodeNumber}"
                } else {
                    "${mediaType}_$mediaId"
                }
                val existingRecord = libraryDao.getItemById(entityId)
                val initialPos = existingRecord?.progressMs ?: 0L

                // Record into watch history
                libraryDao.insert(
                    LibraryMediaEntity(
                        id = entityId,
                        mediaId = mediaId,
                        mediaType = mediaType,
                        title = title,
                        posterPath = posterPath,
                        backdropPath = backdropPath,
                        voteAverage = voteAverage,
                        releaseYear = year.orEmpty(),
                        overview = overview.orEmpty(),
                        progressMs = initialPos,
                        seasonNumber = seasonNumber,
                        episodeNumber = episodeNumber,
                        episodeTitle = episodeTitle,
                        lastWatchedTimestamp = System.currentTimeMillis()
                    )
                )

                val subtitleText = if (seasonNumber != null && episodeNumber != null) {
                    "Season $seasonNumber Episode $episodeNumber${if (!episodeTitle.isNullOrBlank()) " • $episodeTitle" else ""}"
                } else year

                _playerState.value = PlayerUiState(
                    isOpen = true,
                    title = title,
                    subtitle = subtitleText,
                    activeSource = bestSource,
                    allSources = sources,
                    initialPosMs = initialPos,
                    mediaId = mediaId,
                    mediaType = mediaType,
                    seasonNumber = seasonNumber,
                    episodeNumber = episodeNumber,
                    hasNextEpisode = episodeNumber != null && episodeNumber < 12,
                    hasPrevEpisode = episodeNumber != null && episodeNumber > 1
                )
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        getApplication(),
                        "Error resolving playback: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } finally {
                _detailState.value = _detailState.value.copy(isResolvingPlayback = false)
            }
        }
    }

    fun switchPlayerSource(newSource: StreamSource) {
        _playerState.value = _playerState.value.copy(activeSource = newSource)
    }

    fun updatePlaybackProgress(currentMs: Long, durationMs: Long, isFinished: Boolean) {
        val p = _playerState.value
        val entityId = if (p.seasonNumber != null && p.episodeNumber != null) {
            "${p.mediaType}_${p.mediaId}_s${p.seasonNumber}_e${p.episodeNumber}"
        } else {
            "${p.mediaType}_${p.mediaId}"
        }

        viewModelScope.launch {
            val existing = libraryDao.getItemById(entityId)
            if (existing != null) {
                libraryDao.update(
                    existing.copy(
                        progressMs = currentMs,
                        durationMs = durationMs,
                        isFinished = isFinished,
                        lastWatchedTimestamp = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun playNextEpisode() {
        val current = _playerState.value
        val nextEp = (current.episodeNumber ?: 1) + 1
        val season = current.seasonNumber ?: 1
        playMedia(
            mediaId = current.mediaId,
            mediaType = current.mediaType,
            title = current.title,
            seasonNumber = season,
            episodeNumber = nextEp,
            episodeTitle = "Episode $nextEp"
        )
    }

    fun playPrevEpisode() {
        val current = _playerState.value
        val prevEp = ((current.episodeNumber ?: 2) - 1).coerceAtLeast(1)
        val season = current.seasonNumber ?: 1
        playMedia(
            mediaId = current.mediaId,
            mediaType = current.mediaType,
            title = current.title,
            seasonNumber = season,
            episodeNumber = prevEp,
            episodeTitle = "Episode $prevEp"
        )
    }

    fun closePlayer() {
        _playerState.value = _playerState.value.copy(isOpen = false)
    }

    fun search(query: String) {
        _searchState.value = _searchState.value.copy(query = query, isSearching = true)
        viewModelScope.launch {
            val results = repository.multiSearch(query)
            _searchState.value = _searchState.value.copy(results = results, isSearching = false)
        }
    }

    fun checkOrionConnection(userKey: String, appKey: String) {
        viewModelScope.launch {
            _orionUserState.value = OrionUserState(checking = true)
            if (userKey.isBlank()) {
                _orionUserState.value = OrionUserState(errorMessage = "Orion User API Key is empty")
                return@launch
            }
            val finalAppKey = if (appKey.isBlank()) "TESTTESTTESTTESTTESTTESTTESTTEST" else appKey.trim()
            try {
                val res = ApiClient.orionApi.retrieveUser(
                    keyApp = finalAppKey,
                    keyUser = userKey.trim(),
                    mode = "user",
                    action = "retrieve"
                )
                if (res.isSuccessful && res.body()?.data != null) {
                    val d = res.body()!!.data!!
                    _orionUserState.value = OrionUserState(
                        username = d.username,
                        email = d.email,
                        accountType = d.account?.type,
                        isPremium = d.account?.valid == true,
                        dailyLimit = d.limit?.link?.daily ?: 0,
                        remainingLimit = d.limit?.link?.remaining ?: 0,
                        success = true
                    )
                } else {
                    val errorMsg = res.body()?.result?.message ?: res.errorBody()?.string() ?: "Invalid Orion API Key or connection error"
                    _orionUserState.value = OrionUserState(errorMessage = errorMsg)
                }
            } catch (e: Exception) {
                _orionUserState.value = OrionUserState(errorMessage = "Network or connection failed: ${e.localizedMessage}")
            }
        }
    }

    fun clearOrionConnectionStatus() {
        _orionUserState.value = OrionUserState()
    }

    fun saveApiKeys(
        tmdbKey: String,
        simklClientId: String,
        simklSecret: String = "",
        orionKey: String,
        orionAppKey: String,
        region: String
    ) {
        prefs.tmdbApiKey = tmdbKey
        prefs.simklClientId = simklClientId
        if (simklSecret.isNotBlank()) {
            prefs.simklClientSecret = simklSecret
        }
        prefs.orionApiKey = orionKey
        prefs.orionAppKey = orionAppKey
        prefs.watchRegion = region
        loadHome()
    }
}
