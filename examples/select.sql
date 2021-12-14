SELECT 1 + 1;

select 1 + 1;

SELECT TrackId,
       Name,
       Composer,
       UnitPrice
FROM tracks;

SELECT *
FROM tracks;

SELECT name
FROM tracks
WHERE Milliseconds > 3 * 60 * 1000;

CREATE VIEW tracksPlus AS
    SELECT artists.Name AS ArtistName, tracks.Name AS songTitle, tracks.TrackId as TrackId, albums.Title AS AlbumTitle, genres.Name AS GenreName
    FROM artists
    JOIN albums ON artists.ArtistId = albums.ArtistId
    JOIN tracks ON tracks.AlbumId = albums.AlbumId
    JOIN genres ON genres.GenreId = tracks.GenreId;

DROP VIEW tracksPlus;

CREATE TABLE grammy_categories(
    GrammyCategoryId INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    Name TEXT);

DROP TABLE grammy_categories;

INSERT INTO genres (GenreId, Name)
VALUES
       (26, 'Sedimentary Rock'),
       (27, 'Eastern European Turbo Folk'),
       (28, 'Dubstep'),
       (29, 'Jazzercise'),
       (30, 'Kidz Bop');

CREATE TABLE grammy_infos(
    GrammyInfosId INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    ArtistId INTEGER,
    AlbumId INTEGER,
    TrackId INTEGER,
    GrammyCategoryId INTEGER,
    Status TEXT,
    FOREIGN KEY (ArtistId)
        REFERENCES artists (ArtistId),
    FOREIGN KEY (AlbumId)
        REFERENCES albums (AlbumId),
    FOREIGN KEY (TrackId)
        REFERENCES tracks (TrackId),
    FOREIGN KEY (GrammyCategoryId)
        REFERENCES grammy_categories (GrammyCategoryId)
);

DROP TABLE grammy_infos;

SELECT name, AlbumId
FROM tracks
WHERE AlbumId = 1;

SELECT Title
FROM albums
WHERE   AlbumId = 1;

SELECT name, Milliseconds, AlbumId
FROM tracks
WHERE AlbumId = 1 OR Milliseconds > 3 * 60 * 1000;

SELECT name, Milliseconds, AlbumId
FROM tracks
WHERE NOT AlbumId = 1 AND Milliseconds > 3 * 60 * 1000;

select Name from tracks where Composer IS NULL;

SELECT tracks.Name
FROM tracks
    JOIN playlist_track
        ON tracks.TrackId = playlist_track.TrackId
WHERE PlaylistId = 3
ORDER BY tracks.Name;

SELECT *
FROM tracks
ORDER BY Name
LIMIT 1
    OFFSET 0;

SELECT *
FROM tracks
JOIN albums
ON tracks.AlbumId = albums.AlbumId
WHERE tracks.AlbumId = 1;

UPDATE tracks
SET Milliseconds = Milliseconds - ?
WHERE TrackId = ?;

SELECT *
FROM invoice_items;

SELECT tracks.Name
FROM invoice_items
         JOIN tracks
              ON invoice_items.TrackId = tracks.TrackId
GROUP BY invoice_items.TrackId
HAVING COUNT(*) > 1;

SELECT DISTINCT albums.Title
FROM invoice_items
         JOIN tracks
              ON invoice_items.TrackId = tracks.TrackId
         JOIN albums
              ON tracks.AlbumId = albums.AlbumId
GROUP BY invoice_items.TrackId
HAVING COUNT(*) > 1;

SELECT *, COUNT(*)
FROM invoice_items
GROUP BY InvoiceId
;

SELECT customers.Email, employees.FirstName || employees.LastName as employeeName
FROM customers
         JOIN employees ON customers.SupportRepId = employees.EmployeeId
;

SELECT customers.Email
FROM customers
         JOIN employees ON customers.SupportRepId = employees.EmployeeId
WHERE employees.FirstName || employees.LastName = 'JanePeacock';

SELECT customers.Email, tracks.Name, genres.Name
FROM customers
         JOIN invoices ON customers.CustomerId = invoices.CustomerId
         JOIN invoice_items ON invoices.InvoiceId = invoice_items.InvoiceId
         JOIN tracks ON invoice_items.TrackId = tracks.TrackId
         JOIN genres ON tracks.GenreId = genres.GenreId
WHERE genres.Name = 'Rock'
;

SELECT customers.Email
FROM customers
         JOIN employees ON customers.SupportRepId = employees.EmployeeId
WHERE employees.FirstName || employees.LastName = 'JanePeacock'
  AND customers.CustomerId IN(SELECT customers.CustomerId
                                FROM customers
                                         JOIN invoices ON customers.CustomerId = invoices.CustomerId
                                         JOIN invoice_items ON invoices.InvoiceId = invoice_items.InvoiceId
                                         JOIN tracks ON invoice_items.TrackId = tracks.TrackId
                                         JOIN genres ON tracks.GenreId = genres.GenreId
                                WHERE genres.Name = 'Rock');

SELECT tracks.TrackId, tracks.Name, tracks.AlbumId, tracks.MediaTypeId, tracks.GenreId, tracks.Milliseconds, tracks.Bytes, tracks.UnitPrice, artists.Name AS ArtistName, albums.Title AS AlbumTitle
FROM tracks
JOIN albums ON albums.AlbumId = tracks.AlbumId
JOIN artists ON albums.ArtistId = artists.ArtistId
;

SELECT tracks.TrackId, tracks.Name, tracks.AlbumId, tracks.MediaTypeId, tracks.GenreId, tracks.Milliseconds, tracks.Bytes, tracks.UnitPrice, artists.Name AS ArtistName, albums.Title AS AlbumTitle
FROM tracks
JOIN albums ON albums.AlbumId = tracks.AlbumId
JOIN artists ON albums.ArtistId = artists.ArtistId
ORDER BY TrackId LIMIT 10 OFFSET 0