package edu.montana.csci.csci440.model;

import edu.montana.csci.csci440.util.DB;
import redis.clients.jedis.Client;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Track extends Model {

    private Long trackId;
    private Long albumId;
    private Long mediaTypeId;
    private Long genreId;
    private String name;
    private Long milliseconds;
    private Long bytes;
    private BigDecimal unitPrice;
    private String artistName;
    private String albumTitle;
    private boolean currentOrderBy;

    public static final String REDIS_CACHE_KEY = "cs440-tracks-count-cache";

    public Track() {
        mediaTypeId = 1l;
        genreId = 1l;
        milliseconds  = 0l;
        bytes  = 0l;
        unitPrice = new BigDecimal("0");
    }

    private Track(ResultSet results) throws SQLException {
        name = results.getString("Name");
        milliseconds = results.getLong("Milliseconds");
        bytes = results.getLong("Bytes");
        unitPrice = results.getBigDecimal("UnitPrice");
        trackId = results.getLong("TrackId");
        albumId = results.getLong("AlbumId");
        mediaTypeId = results.getLong("MediaTypeId");
        genreId = results.getLong("GenreId");
        artistName = results.getString("ArtistName");
        albumTitle = results.getString("AlbumTitle");
    }

    @Override
    public boolean create() {
        if (verify()) {
            try (Connection conn = DB.connect();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO tracks (Name, Milliseconds, Bytes, UnitPrice, AlbumId, MediaTypeId, GenreId) VALUES (?,?,?,?,?,?,?)")) {
                stmt.setString(1, this.getName());
                stmt.setLong(2, this.getMilliseconds());
                stmt.setLong(3, this.getBytes());
                stmt.setBigDecimal(4, this.getUnitPrice());
                stmt.setLong(5, this.getAlbumId());
                stmt.setLong(6, this.getMediaTypeId());
                stmt.setLong(7, this.getGenreId());
                stmt.executeUpdate();
                trackId = DB.getLastID(conn);
                Jedis redisClient = new Jedis(); // use this class to access redis and create a cache
                redisClient.del(REDIS_CACHE_KEY);
                return true;
            } catch (SQLException sqlException) {
                throw new RuntimeException(sqlException);
            }
        } else {
            return false;
        }    }

    @Override
    public boolean update() {
        if (verify()) {
            try (Connection conn = DB.connect();
                 PreparedStatement stmt = conn.prepareStatement(
                         "UPDATE tracks SET Name=?, Milliseconds=?, Bytes=?, UnitPrice=?, AlbumId=?, MediaTypeId=?, GenreId=? WHERE TrackId=?")) {
                stmt.setString(1, this.getName());
                stmt.setLong(2, this.getMilliseconds());
                stmt.setLong(3, this.getBytes());
                stmt.setBigDecimal(4, this.getUnitPrice());
                stmt.setLong(5, this.getAlbumId());
                stmt.setLong(6, this.getMediaTypeId());
                stmt.setLong(7, this.getGenreId());
                stmt.setLong(8, this.getTrackId());
                stmt.executeUpdate();
                return true;
            } catch (SQLException sqlException) {
                throw new RuntimeException(sqlException);
            }
        } else {
            return false;
        }    }

    @Override
    public boolean verify() {
        _errors.clear(); // clear any existing errors
        if (name == null || "".equals(name)) {
            addError("Name can't be null or blank!");
        }
        if (milliseconds == null || "".equals(milliseconds)) {
            addError("Milliseconds can't be null or blank!");
        }
        if (bytes == null || "".equals(bytes)) {
            addError("Bytes can't be null or blank!");
        }
        if (unitPrice == null || "".equals(unitPrice)) {
            addError("Unit Price can't be null or blank!");
        }
        if (albumId == null || "".equals(albumId)) {
            addError("Album Id can't be null or blank!");
        }
        if (mediaTypeId == null || "".equals(mediaTypeId)) {
            addError("Media Type Id can't be null or blank!");
        }
        if (genreId == null || "".equals(genreId)) {
            addError("Genre Id can't be null or blank!");
        }
        return !hasErrors();
    }

    @Override
    public void delete() {
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM tracks WHERE TrackID=?")) {
            stmt.setLong(1, this.getTrackId());
            stmt.executeUpdate();
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }    }

    public static Track find(long i) {
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT tracks.TrackId, tracks.Name, tracks.AlbumId, tracks.MediaTypeId, tracks.GenreId, tracks.Milliseconds, tracks.Bytes, tracks.UnitPrice, artists.Name AS ArtistName, albums.Title AS AlbumTitle\n" +
                     "FROM tracks\n" +
                     "JOIN albums ON albums.AlbumId = tracks.AlbumId\n" +
                     "JOIN artists ON albums.ArtistId = artists.ArtistId\n" +
                     "WHERE TrackId=?")) {
            stmt.setLong(1, i);
            ResultSet results = stmt.executeQuery();
            if (results.next()) {
                return new Track(results);
            } else {
                return null;
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public static Long count() {
        Jedis redisClient = new Jedis(); // use this class to access redis and create a cache
        String s = redisClient.get(REDIS_CACHE_KEY);
        long theCount;
        if(s == null) {
            try (Connection conn = DB.connect();
                 PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) as Count FROM tracks")) {
                 ResultSet results = stmt.executeQuery();
                 if (results.next()) {
                    theCount = results.getLong("Count");
                    redisClient.set(REDIS_CACHE_KEY,String.valueOf(theCount));
                 } else {
                    throw new IllegalStateException("Should find a count!");
                 }
            } catch (SQLException sqlException) {
                throw new RuntimeException(sqlException);
            }
        }
        else {
            theCount = Long.parseLong(s);
        }
        return theCount;
    }

    public Album getAlbum() {
        return Album.find(albumId);
    }

    public static List<Track> getForPlaylist(Long playlistId){
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT tracks.TrackId, tracks.Name, tracks.AlbumId, tracks.MediaTypeId, tracks.GenreId, tracks.Milliseconds, tracks.Bytes, tracks.UnitPrice, artists.Name AS ArtistName, albums.Title AS AlbumTitle\n" +
                             "FROM tracks\n" +
                             "JOIN playlist_track ON tracks.TrackId = playlist_track.TrackId\n" +
                             "JOIN albums ON albums.AlbumId = tracks.AlbumId\n" +
                             "JOIN artists ON albums.ArtistId = artists.ArtistId\n" +
                             "WHERE PlaylistId = ? ORDER BY tracks.Name"
             )) {
            stmt.setLong(1, playlistId);
            ResultSet results = stmt.executeQuery();
            List<Track> resultList = new LinkedList<>();
            while (results.next()) {
                resultList.add(new Track(results));
            }
            return resultList;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public MediaType getMediaType() {
        return null;
    }
    public Genre getGenre() {
        return null;
    }
    public List<Playlist> getPlaylists(){
        return Playlist.getForTrack(this.trackId);
    }

    public Long getTrackId() {
        return trackId;
    }

    public void setTrackId(Long trackId) {
        this.trackId = trackId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(Long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public Long getBytes() {
        return bytes;
    }

    public void setBytes(Long bytes) {
        this.bytes = bytes;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public void setAlbum(Album album) {
        albumId = album.getAlbumId();
    }

    public Long getMediaTypeId() {
        return mediaTypeId;
    }

    public void setMediaTypeId(Long mediaTypeId) {
        this.mediaTypeId = mediaTypeId;
    }

    public Long getGenreId() {
        return genreId;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }

    public String getArtistName() {
        // TODO implement more efficiently
        //  hint: cache on this model object

        return this.artistName;
    }

    public String getAlbumTitle() {
        // TODO implement more efficiently
        //  hint: cache on this model object
        return this.albumTitle;
    }

    public static List<Track> advancedSearch(int page, int count,
                                             String search, Integer artistId, Integer albumId,
                                             Integer genreId, Integer mediaTypeId,
                                             Integer maxRuntime, Integer minRuntime) {
        LinkedList<Object> args = new LinkedList<>();

        String query = "SELECT tracks.TrackId, tracks.Name, tracks.AlbumId, tracks.MediaTypeId, tracks.GenreId, tracks.Milliseconds, tracks.Bytes, tracks.UnitPrice, artists.Name AS ArtistName, albums.Title AS AlbumTitle\n" +
                "FROM tracks\n" +
                "JOIN albums ON albums.AlbumId = tracks.AlbumId\n" +
                "JOIN artists ON albums.ArtistId = artists.ArtistId\n" +
                "WHERE tracks.name LIKE ?";
        args.add("%" + search + "%");

        // Conditionally include the query and argument
        if (artistId != null) {
            query += " AND artists.ArtistId=? ";
            args.add(artistId);
        }

        if (albumId != null) {
            query += " AND albums.AlbumId=? ";
            args.add(albumId);
        }

        if (genreId != null) {
            query += " AND GenreId=? ";
            args.add(genreId);
        }

        if (mediaTypeId != null) {
            query += " AND MediaTypeId=? ";
            args.add(mediaTypeId);
        }

        if (maxRuntime != null) {
            query += " AND Milliseconds<=? ";
            args.add(maxRuntime);
        }

        if (minRuntime != null) {
            query += " AND Milliseconds>=? ";
            args.add(minRuntime);
        }

        query += " LIMIT ? OFFSET ?";
        args.add(count);
        args.add(count*(page-1));

        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < args.size(); i++) {
                Object arg = args.get(i);
                stmt.setObject(i + 1, arg);
            }
            ResultSet results = stmt.executeQuery();
            List<Track> resultList = new LinkedList<>();
            while (results.next()) {
                resultList.add(new Track(results));
            }
            return resultList;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public static List<Track> search(int page, int count, String orderBy, String search) {
        String query = "SELECT tracks.TrackId, tracks.Name, tracks.AlbumId, tracks.MediaTypeId, tracks.GenreId, tracks.Milliseconds, tracks.Bytes, tracks.UnitPrice, artists.Name AS ArtistName, albums.Title AS AlbumTitle\n" +
                "FROM tracks\n" +
                "JOIN albums ON albums.AlbumId = tracks.AlbumId\n" +
                "JOIN artists ON albums.ArtistId = artists.ArtistId\n" +
                "WHERE tracks.name LIKE ? ORDER BY " + orderBy + " LIMIT ? OFFSET ?";
        search = "%" + search + "%";
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, search);
            stmt.setInt(2, count);
            stmt.setInt(3, count*(page-1));
            ResultSet results = stmt.executeQuery();
            List<Track> resultList = new LinkedList<>();
            while (results.next()) {
                resultList.add(new Track(results));
            }
            return resultList;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public static List<Track> forAlbum(Long albumId) {
        String query = "SELECT tracks.TrackId, tracks.Name, tracks.AlbumId, tracks.MediaTypeId, tracks.GenreId, tracks.Milliseconds, tracks.Bytes, tracks.UnitPrice, artists.Name AS ArtistName, albums.Title AS AlbumTitle\n" +
                "FROM tracks\n" +
                "JOIN albums ON albums.AlbumId = tracks.AlbumId\n" +
                "JOIN artists ON albums.ArtistId = artists.ArtistId\n" +
                "WHERE tracks.AlbumId=?";
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, albumId);
            ResultSet results = stmt.executeQuery();
            List<Track> resultList = new LinkedList<>();
            while (results.next()) {
                resultList.add(new Track(results));
            }
            return resultList;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    // Sure would be nice if java supported default parameter values
    public static List<Track> all() {
        return all(0, Integer.MAX_VALUE);
    }

    public static List<Track> all(int page, int count) {
        return all(page, count, "TrackId");
    }

    public static List<Track> all(int page, int count, String orderBy) {
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT tracks.TrackId, tracks.Name, tracks.AlbumId, tracks.MediaTypeId, tracks.GenreId, tracks.Milliseconds, tracks.Bytes, tracks.UnitPrice, artists.Name AS ArtistName, albums.Title AS AlbumTitle\n" +
                             "FROM tracks\n" +
                             "JOIN albums ON albums.AlbumId = tracks.AlbumId\n" +
                             "JOIN artists ON albums.ArtistId = artists.ArtistId\n" +
                             "ORDER BY " + orderBy + " LIMIT ? OFFSET ?"
             )) {
            stmt.setInt(1, count);
            stmt.setInt(2,count*(page-1));
            ResultSet results = stmt.executeQuery();
            List<Track> resultList = new LinkedList<>();
            while (results.next()) {
                resultList.add(new Track(results));
            }
            return resultList;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

}
