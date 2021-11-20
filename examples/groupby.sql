

-- Select all AlbumID FKs from the tracks table
SELECT AlbumID
FROM tracks
GROUP BY AlbumID;

-- Select all AlbumID FKs from the tracks table
SELECT DISTINCT AlbumID
FROM tracks;

-- Select all AlbumID FKs from the tracks table
SELECT AlbumID, COUNT(*)
FROM tracks
GROUP BY AlbumID;

-- Select all AlbumID FKs from the tracks table
SELECT AlbumID, COUNT(*) as TrackCount
FROM tracks
GROUP BY AlbumID;

-- Select all AlbumID FKs from the tracks table
SELECT tracks.AlbumID, Title, COUNT(*) as TrackCount
FROM tracks
JOIN albums on tracks.AlbumId = albums.AlbumId
GROUP BY tracks.AlbumID;

-- Calculate run time of albums by summing the tracks
SELECT tracks.AlbumID, Title,
       SUM(tracks.Milliseconds) / 1000.0 / 60.0 as Minutes
FROM tracks
JOIN albums on tracks.AlbumId = albums.AlbumId
GROUP BY tracks.AlbumID;

-- Calculate run time of artists by summing the tracks
SELECT artists.Name,
       SUM(tracks.Milliseconds) as Milliseconds
FROM tracks
JOIN albums on tracks.AlbumId = albums.AlbumId
JOIN artists on albums.ArtistId = artists.ArtistId
GROUP BY albums.ArtistId;


-- Calculate run time of artists by summing the tracks
SELECT artists.Name,
       COUNT(tracks.TrackId) as Tracks,
       COUNT(albums.AlbumId) as Albums,
       SUM(tracks.Milliseconds) as Milliseconds
FROM tracks
JOIN albums on tracks.AlbumId = albums.AlbumId
JOIN artists on albums.ArtistId = artists.ArtistId
GROUP BY albums.ArtistId;


-- Simple Join to show the rows
SELECT TrackId, albums.AlbumId, artists.ArtistId
FROM tracks
         JOIN albums on tracks.AlbumId = albums.AlbumId
         JOIN artists on albums.ArtistId = artists.ArtistId;

-- Calculate run time of artists by summing the tracks
SELECT artists.Name,
       COUNT(tracks.TrackId) as Tracks,
       COUNT(DISTINCT albums.AlbumId) as Albums,
       SUM(tracks.Milliseconds) as Milliseconds
FROM tracks
         JOIN albums on tracks.AlbumId = albums.AlbumId
         JOIN artists on albums.ArtistId = artists.ArtistId
GROUP BY albums.ArtistId;

SELECT artists.Name,
       COUNT(tracks.TrackId) as Tracks,
       COUNT(DISTINCT albums.AlbumId) as Albums,
       SUM(tracks.Milliseconds) as Milliseconds
FROM tracks
         JOIN albums on tracks.AlbumId = albums.AlbumId
         JOIN artists on albums.ArtistId = artists.ArtistId
GROUP BY albums.ArtistId
HAVING Tracks >= 10;

SELECT artists.Name,
       COUNT(tracks.TrackId) as Tracks,
       COUNT(DISTINCT albums.AlbumId) as Albums,
       SUM(tracks.Milliseconds) as Milliseconds
FROM tracks
         JOIN albums on tracks.AlbumId = albums.AlbumId
         JOIN artists on albums.ArtistId = artists.ArtistId
GROUP BY albums.ArtistId
HAVING Tracks >= 10 AND tracks.Name LIKE "A%";

SELECT artists.Name,
       COUNT(tracks.TrackId) as Tracks,
       COUNT(DISTINCT albums.AlbumId) as Albums,
       SUM(tracks.Milliseconds) as Milliseconds
FROM tracks
         JOIN albums on tracks.AlbumId = albums.AlbumId
         JOIN artists on albums.ArtistId = artists.ArtistId
WHERE tracks.Name LIKE "A%"
GROUP BY albums.ArtistId
HAVING Tracks > 2;

SELECT employees.FirstName, employees.LastName, employees.Title, managers.FirstName, managers.LastName
FROM employees
JOIN employees as managers ON employees.ReportsTo = managers.EmployeeId
JOIN customers ON customers.SupportRepId = employees.EmployeeId
WHERE managers.FirstName = 'Andrew' AND managers.LastName = 'Adams'
GROUP BY customers.SupportRepId;

SELECT employees.FirstName, employees.LastName, employees.Title, managers.FirstName, managers.LastName, count(*) AS CustomerCount
FROM customers
         JOIN employees as employees ON customers.SupportRepId = employees.EmployeeId
         JOIN employees as managers ON employees.ReportsTo = managers.EmployeeId
WHERE managers.FirstName = 'Nancy' AND managers.LastName = 'Edwards'
GROUP BY customers.SupportRepId;

SELECT tracks.Name, genres.Name as Genre
FROM tracks
    JOIN genres ON tracks.GenreId = genres.GenreId
WHERE genres.Name LIKE 'Ro%';

SELECT employees.FirstName, employees.LastName, employees.Email, count(*) as salesCount, sum(invoices.Total) as salesTotal
FROM employees
JOIN customers ON employees.EmployeeId = customers.SupportRepId
JOIN invoices ON customers.CustomerId = invoices.CustomerId
GROUP BY EmployeeId;


