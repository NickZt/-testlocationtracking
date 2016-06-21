# Testlocationtracking
Скелет GPS трекера.

Test task Android

Create user location tracking application

Save data in JSON, in such format:

root[{

‘lon’:37.323400,
‘lat’:12.4234234
‘ele’:0.0
},{...}];
ele - unchangable, constant
lon, lat - data from GPS

Request location every 5 min
JSON (route) max 100 elements.

Create .json in local storage.
Location tracking should be done in active app as well as in background.
